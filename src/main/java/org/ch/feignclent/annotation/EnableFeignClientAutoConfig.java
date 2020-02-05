package org.ch.feignclent.annotation;

import org.ch.feignclent.config.ApiProxyBeanRegistry;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动注解
 *
 * @author ch
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiProxyBeanRegistry.class)
public @interface EnableFeignClientAutoConfig {
}
