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
@EnableConfigurationProperties({FeignClientConfigBean.class})
public class FeignClientConfigBean {
    /**
     * 扫描 feignClient 标记类的路径
     */
    private String scanPath;
    /**
     * 服务列表 服务=127.0.0.1:3306
     */
    private String[] serverList;
}
