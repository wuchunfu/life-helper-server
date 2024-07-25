package com.weutil.account.service;

import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.weutil.account.entity.LoginSmsTrace;
import com.weutil.account.entity.PhoneAccount;
import com.weutil.account.entity.PhoneCodeLoginLog;
import com.weutil.account.event.LoginByPhoneCodeEvent;
import com.weutil.account.exception.*;
import com.weutil.account.mapper.LoginSmsTraceMapper;
import com.weutil.account.mapper.PhoneCodeLoginLogMapper;
import com.weutil.common.model.IdentityCertificate;
import com.weutil.common.service.IdentityCertificateService;
import com.weutil.common.util.RandomStringUtil;
import com.weutil.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.weutil.account.entity.table.LoginSmsTraceTableDef.LOGIN_SMS_TRACE;

/**
 * 手机验证码登录服务
 *
 * <h2>主要用途
 * <p>处理使用“短信验证码”进行登录的各个环节。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2024/7/23
 * @since 3.0.0
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneCodeLoginService {
    private final LoginSmsTraceMapper loginSmsTraceMapper;
    private final PhoneAccountService phoneAccountService;
    private final SmsService smsService;
    private final IdentityCertificateService identityCertificateService;
    private final PhoneCodeLoginLogMapper phoneCodeLoginLogMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送短信验证码
     *
     * <h3>说明
     * <p>记录客户端 IP 地址的用途是：要求2个环节的 IP 地址一致，防止伪造请求攻击。
     *
     * @param phone 手机号
     * @param ip    客户端 IP 地址
     *
     * @date 2024/6/13
     * @since 2.3.0
     */
    public LoginSmsTrace sendSms(String phone, String ip) {
        // 检查手机号格式是否正确
        checkPhoneFormat(phone);
        // 检查是否达到发送限制
        checkSendingLimit(phone, ip);

        // 检验“验证码”是否正确时，使用 [checkTicket(32位随机字符串) + code] 的方式替代 [phone + code]，能够大大降低被碰撞的概率。
        String checkTicket = RandomStringUtil.generate(32);
        // 短信验证码（6位纯数字）
        String code = RandomStringUtil.generateNumericString(6);
        // 发送前的时间
        LocalDateTime preSendTime = LocalDateTime.now();

        // 正式发送短信
        SendSmsResponseBody result = smsService.sendLoginCode(phone, code);

        // 发送后记录
        LoginSmsTrace inserted = LoginSmsTrace.builder()
            .phone(phone)
            .code(code)
            .checkTicket(checkTicket)
            .ip(ip)
            .preSendTime(preSendTime)
            .resCode(result.getCode())
            .resMessage(result.getMessage())
            .resBizId(result.getBizId())
            .requestId(result.getRequestId())
            .postSendTime(LocalDateTime.now())
            .build();

        if (result.getCode().equals("OK")) {
            // 处理短信发送成功情况
            loginSmsTraceMapper.insertSelective(inserted);
            log.info("[SMS] 短信验证码发送成功, phone={}, code={}", phone, code);
            return loginSmsTraceMapper.selectOneById(inserted.getId());
        } else {
            // 处理短信发送失败情况
            loginSmsTraceMapper.insertSelective(inserted);
            log.error("[SMS] 短信验证码发送失败, phone={}, code={}, response={}", phone, code, result);
            // 短信未发送，抛出异常
            throw new SmsSentFailureException();
        }
    }

    /**
     * 检查手机号格式是否正确
     *
     * @param phone 手机号
     *
     * @date 2024/6/24
     * @since 2.3.0
     */
    private void checkPhoneFormat(String phone) {
        String regex = "^1(3[0-9]|4[5-9]|5[0-3,5-9]|6[5-7]|7[0-8]|8[0-9]|9[0-3,5-9])\\d{8}$";
        if (!phone.matches(regex)) {
            throw new InvalidPhoneNumberException();
        }
    }

    /**
     * （在短信发送前）检查是否达到发送限制
     *
     * <h3>说明
     * <p>目前限制为：
     * <p>1. 每分钟：1条
     * <p>1. 每小时：5条
     *
     * @param phone 手机号
     * @param ip    客户端 IP 地址
     *
     * @date 2024/6/23
     * @since 2.3.0
     */
    private void checkSendingLimit(String phone, String ip) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(LOGIN_SMS_TRACE.ALL_COLUMNS)
            .from(LOGIN_SMS_TRACE)
            .orderBy(LOGIN_SMS_TRACE.POST_SEND_TIME.desc())
            .where(LOGIN_SMS_TRACE.PHONE.eq(phone).or(LOGIN_SMS_TRACE.IP.eq(ip)));

        // 限制每分钟 1 条
        QueryWrapper query1 = queryWrapper.clone().where(LOGIN_SMS_TRACE.POST_SEND_TIME.gt(LocalDateTime.now().minusMinutes(1L)));
        List<LoginSmsTrace> list1 = loginSmsTraceMapper.selectListByQuery(query1);
        if (!list1.isEmpty()) {
            Duration between = Duration.between(list1.get(0).getPostSendTime(), LocalDateTime.now());
            throw new SmsRateLimitExceededException(Duration.ofMinutes(1L).toSeconds() - between.toSeconds());
        }

        // 限制每小时 5 条
        QueryWrapper query2 = queryWrapper.clone().where(LOGIN_SMS_TRACE.POST_SEND_TIME.gt(LocalDateTime.now().minusHours(1L)));
        List<LoginSmsTrace> list2 = loginSmsTraceMapper.selectListByQuery(query2);
        if (list1.size() > 5) {
            Duration between = Duration.between(list2.get(0).getPostSendTime(), LocalDateTime.now());
            throw new SmsRateLimitExceededException(Duration.ofHours(1L).toSeconds() - between.toSeconds());
        }
    }

    /**
     * 通过短信验证码登录
     *
     * @param checkTicket 校验码
     * @param code        短信验证码
     *
     * @date 2024/06/23
     * @since 2.3.0
     */
    public IdentityCertificate loginBySmsCode(String checkTicket, String code, String ip) {
        LoginSmsTrace loginSmsTrack = loginSmsTraceMapper.selectOneByCondition(LOGIN_SMS_TRACE.CHECK_TICKET.eq(checkTicket));

        // 短信验证码不存在
        if (loginSmsTrack == null) {
            throw new SmsCheckTicketNotExistsException();
        }

        // 短信验证码已过期
        if (loginSmsTrack.getPostSendTime().isAfter(LocalDateTime.now().plusMinutes(5L))) {
            throw new PhoneCodeExpiredException();
        }

        LocalDateTime now = LocalDateTime.now();
        LoginSmsTrace updated = UpdateEntity.of(LoginSmsTrace.class, loginSmsTrack.getId());
        if (loginSmsTrack.getFirstAttemptTime() == null) {
            updated.setFirstAttemptTime(now);
        }
        updated.setLastAttemptTime(now);
        UpdateWrapper<LoginSmsTrace> wrapper = UpdateWrapper.of(updated);
        wrapper.set(LOGIN_SMS_TRACE.ATTEMPT_COUNTER, LOGIN_SMS_TRACE.ATTEMPT_COUNTER.add(1));

        if (!Objects.equals(loginSmsTrack.getCode(), code)) {
            loginSmsTraceMapper.update(updated);
            throw new PhoneCodeNotMatchException();
        }

        // 虽然验证码匹配上了，再增加一重保障，要求“获取验证码”操作和“输入验证码”操作的设备是同一个，目前仅验证 IP 地址相同
        if (!Objects.equals(loginSmsTrack.getIp(), ip)) {
            loginSmsTraceMapper.update(updated);
            throw new NotSameIpException();
        }

        // 全部校验通过，登录成功情况
        PhoneAccount userAccountPhone = phoneAccountService.getOrCreatePhoneAccount(loginSmsTrack.getPhone());

        // 生成鉴权凭据
        IdentityCertificate identityCertificate = identityCertificateService.create(userAccountPhone.getUserId());

        // 记录到日志
        PhoneCodeLoginLog insertedLog = PhoneCodeLoginLog.builder()
            .phone(loginSmsTrack.getPhone())
            .phoneAccountId(userAccountPhone.getId())
            .userId(userAccountPhone.getUserId())
            .token(identityCertificate.getToken())
            .ip(loginSmsTrack.getIp())
            .loginTime(LocalDateTime.now())
            .code(loginSmsTrack.getCode())
            .build();
        phoneCodeLoginLogMapper.insertSelective(insertedLog);

        // 剩余信息记录到追踪表
        updated.setSucceedTime(now);
        updated.setPhoneCodeLoginLogId(insertedLog.getId());
        updated.setPhoneAccountId(userAccountPhone.getId());
        loginSmsTraceMapper.update(updated);

        // 发布手机号使用短信验证码登录事件
        applicationEventPublisher.publishEvent(new LoginByPhoneCodeEvent(insertedLog));

        return identityCertificate;
    }
}
