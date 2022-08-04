package com.inlym.lifehelper.login.wechatcode;

import com.inlym.lifehelper.common.auth.core.AuthenticationCredential;
import com.inlym.lifehelper.common.auth.jwt.JwtService;
import com.inlym.lifehelper.external.wechat.WeChatService;
import com.inlym.lifehelper.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信小程序登录服务
 *
 * <h2>说明
 * <p>使用在小程序中调用 `wx.login` 方法获取 `code` 进行登录。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/8/4
 * @since 1.3.0
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class WeChatCodeLoginService {
    private final UserService userService;

    private final WeChatService weChatService;

    private final JwtService jwtService;

    /**
     * 通过微信获取的 code 登录
     *
     * @param code 微信小程序中获取的 code
     *
     * @since 1.3.0
     */
    public AuthenticationCredential loginByCode(String code) {
        String openid = weChatService.getOpenidByCode(code);
        int userId = userService.getUserIdByOpenid(openid);
        AuthenticationCredential ac = jwtService.createAuthenticationCredential(userId);

        log.info("[小程序用户登录] code={}, openid={}, userId={}", code, openid, userId);

        return ac;
    }
}
