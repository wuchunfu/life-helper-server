package com.inlym.lifehelper.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Spring Redis 配置
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2021-12-16
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class SpringRedisConfig {
    private final RedisConnectionFactory redisConnectionFactory;

    private final ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        // 开启 `Redis Repositories` 需要关闭 Redis 事务
        template.setEnableTransactionSupport(false);

        template.afterPropertiesSet();

        return template;
    }
}
