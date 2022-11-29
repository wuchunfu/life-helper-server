package com.inlym.lifehelper.extern.wechat;

import com.inlym.lifehelper.extern.wechat.pojo.WeChatGetAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 微信服务端接口调用凭据服务
 *
 * <h2>说明
 * <p>围绕着微信服务端接口调用凭据的方法较多，因此将它们剥离出来，单独弄一个类。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022/8/3
 * @since 1.3.0
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatAccessTokenService {
    /** 在 Redis 中存储微信服务端接口调用凭证的键名 */
    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";

    private final WeChatHttpService weChatHttpService;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 刷新存储在 Redis 中的凭证
     *
     * <h2>说明
     * <p>无论之前是否存在或者是否正确，均强制获取并存储一个新的。
     *
     * @since 1.3.0
     */
    public String refresh() {
        WeChatGetAccessTokenResponse data = weChatHttpService.getAccessToken();
        stringRedisTemplate
            .opsForValue()
            .set(ACCESS_TOKEN_KEY, data.getAccessToken(), Duration.ofSeconds(data.getExpiresIn()));

        return data.getAccessToken();
    }

    /**
     * 异步刷新存储在 Redis 中的凭证
     *
     * <h2>说明
     * <p>用于当检测到凭证错误时，在错误捕获器中异步刷新。
     *
     * @since 1.3.0
     */
    public void refreshAsync() {
        refresh();
    }

    /**
     * 获取接口调用凭证
     *
     * <h2>说明
     * <p>用于外部服务调用使用。
     *
     * @since 1.3.0
     */
    public String get() {
        // 从 Redis 中获取
        String token = stringRedisTemplate
            .opsForValue()
            .get(ACCESS_TOKEN_KEY);

        // Redis 中存在则直接返回，否则获取一个新的
        return token == null ? refresh() : token;
    }
}