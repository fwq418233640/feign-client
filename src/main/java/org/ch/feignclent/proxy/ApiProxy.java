package org.ch.feignclent.proxy;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.spring.SpringContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 所有 api 代理类
 * 被  {@link org.ch.feignclent.annotation.FeignClient} 标记的接口 都会被此类代理
 *
 * @author ch
 */
@Slf4j
public class ApiProxy implements InvocationHandler {

    private final Environment environment;

    public ApiProxy(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (Object.class.equals(declaringClass)) {
            return method.invoke(this, args);
        } else {
            String serverAddress = environment.getProperty("feign.server-address");
            String port = environment.getProperty("feign.port");
            String contextPath = environment.getProperty("feign.context-path");
            String uri = "http://" + serverAddress + ":" + port;
            if (contextPath != null) {
                uri += contextPath;
            }
            Object target = Feign.builder()
                    .contract(new SpringContract())
                    .encoder(new GsonEncoder())
                    .decoder(new GsonDecoder())
                    .target(declaringClass, uri);
            return method.invoke(target, args);
        }
    }
}
