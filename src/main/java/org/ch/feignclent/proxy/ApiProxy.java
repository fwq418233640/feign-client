package org.ch.feignclent.proxy;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.spring.SpringContract;
import lombok.extern.slf4j.Slf4j;
import org.ch.feignclent.annotation.FeignClient;
import org.ch.feignclent.config.RequestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有 api 代理类
 * 被  {@link org.ch.feignclent.annotation.FeignClient} 标记的接口 都会被此类代理
 *
 * @author ch
 */
@Slf4j
public class ApiProxy implements InvocationHandler {
    /**
     * 接口类型
     */
    private final Class<?> interfaceType;
    /**
     * 配置文件
     */

    private final Map<String, String> map;

    public ApiProxy(Class<?> interfaceType, Environment environment) {
        this.interfaceType = interfaceType;
        this.map = new HashMap<>();
        String serverList = environment.getProperty("feign.server-list");
        if (!StringUtils.isEmpty(serverList)) {
            String[] split = serverList.split(",");
            for (String m : split) {
                if (m.contains("=")) {
                    String[] server = m.split("=");
                    map.put(server[0], server[1]);
                }
            }
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (Object.class.equals(declaringClass)) {
            return method.invoke(this, args);
        } else {
            FeignClient annotation = interfaceType.getDeclaredAnnotation(FeignClient.class);
            if (annotation != null) {
                String server = annotation.server();
                // 若填写 server  则首先从配置文件中获取
                // 配置文件不存在 则从注解本身获取
                String address = this.map.get(server);
                String uri;
                if (StringUtils.isEmpty(address)) {
                    String serverAddress = annotation.serverIpAddr();
                    String port = annotation.port();
                    String contextPath = annotation.contextPath();

                    if (StringUtils.isEmpty(serverAddress) || StringUtils.isEmpty(port)) {
                        throw new RuntimeException("没有填写 " + declaringClass + " Api 服务器地址或者端口");
                    }
                    uri = "http://" + serverAddress + ":" + port + contextPath;
                } else {
                    uri = "http://" + address;
                }


                Object target = Feign.builder()
                        .requestInterceptor(new RequestConfiguration())
                        .contract(new SpringContract())
                        .encoder(new GsonEncoder())
                        .decoder(new GsonDecoder())
                        .target(declaringClass, uri);
                return method.invoke(target, args);
            } else {
                throw new RuntimeException("在" + declaringClass + "上没有找到 @FeignClient 注解");
            }
        }
    }
}
