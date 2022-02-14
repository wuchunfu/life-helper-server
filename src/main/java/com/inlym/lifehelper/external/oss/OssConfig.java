package com.inlym.lifehelper.external.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS 配置类（主要用于输出封装好的 OSS 客户端）
 *
 * @author inlym
 * @date 2022-02-12 23:09
 */
@Configuration
public class OssConfig {
    @Bean
    public OSS ossClient(OssProperties ossProperties) {
        return new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
    }
}