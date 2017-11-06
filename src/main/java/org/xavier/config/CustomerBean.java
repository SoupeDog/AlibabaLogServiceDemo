package org.xavier.config;

import com.aliyun.openservices.log.Client;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述信息：<br/>
 *
 * @author Xavier
 * @version 1.0
 * @date 2017.11.06
 * @since Jdk 1.8
 */
@Configuration
@EnableConfigurationProperties({AliyunProperties.class})
public class CustomerBean {
    @Bean
    public Client client(AliyunProperties aliyunProperties) {
        String endpoint = aliyunProperties.getEndpoint(); // 选择与上面步骤创建 project 所属区域匹配的
        String accessKeyId = aliyunProperties.getAccessKeyId(); // 使用您的阿里云访问密钥 AccessKeyId
        String accessKeySecret = aliyunProperties.getAccessKeySecret(); // 使用您的阿里云访问密钥
        Client client = new Client(endpoint, accessKeyId, accessKeySecret);
        return client;
    }
}
