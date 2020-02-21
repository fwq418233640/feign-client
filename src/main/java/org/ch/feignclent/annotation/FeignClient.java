package org.ch.feignclent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被此注解标记的类会被 代理
 *
 * @author ch
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeignClient {

    /**
     * 服务
     */
    String server();

    /**
     * 端口
     */
    String port() default "";

    /**
     * 服务地址
     */
    String serverIpAddr() default "";

    /**
     * 访问路径
     */
    String contextPath() default "";
}
