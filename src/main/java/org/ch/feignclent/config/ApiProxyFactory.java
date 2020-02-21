package org.ch.feignclent.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.ch.feignclent.proxy.ApiProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 接口实例工厂，这里主要是用于提供接口的实例对象
 *
 * @param <T>
 * @author lichuang
 */
public class ApiProxyFactory<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private final Environment environment;

    public ApiProxyFactory(Class<T> interfaceType, Environment environment) {
        this.interfaceType = interfaceType;
        this.environment = environment;
    }

    @Override
    public T getObject() throws Exception {
        //  这里主要是创建接口对应的实例，便于注入到spring容器中
        InvocationHandler handler = new ApiProxy(interfaceType,environment);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
