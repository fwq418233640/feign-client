package org.ch.feignclent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务配置类
 *
 * @author ch
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "feign")
@EnableConfigurationProperties({FeignClientConfig.class})
public class FeignClientConfig {
    /**
     * 服务地址
     */
    private String serverAddress;
    /**
     * 服务端口
     */
    private String port;
    /**
     * 服务路径
     */
    private String contextPath;
    /**
     * 扫描 feignClient 标记类的路径
     */
    private String scanPath;
}
