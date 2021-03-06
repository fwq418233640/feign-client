package org.ch.feignclent.config;

import org.ch.feignclent.annotation.FeignClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 用于Spring动态注入自定义接口
 *
 * @author lichuang
 */
@Component
public class ApiProxyBeanRegistry implements BeanDefinitionRegistryPostProcessor
        , ResourceLoaderAware, ApplicationContextAware {


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Environment environment = getEnvironment();
        String scanPath = environment.getProperty("feign.scan-path");
        if (StringUtils.isEmpty(scanPath)) {
            scanPath = this.getClass().getClassLoader().getResource("").getPath();
            System.out.println(scanPath);
        }
        Set<Class<?>> beanClazzs = scannerPackages(scanPath);
        for (Class<?> beanClazz : beanClazzs) {
            Annotation annotation = beanClazz.getDeclaredAnnotation(FeignClient.class);
            if (annotation != null) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
                definition.setBeanClass(ApiProxyFactory.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
            }
        }
    }

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private MetadataReaderFactory metadataReaderFactory;

    /**
     * 根据包路径获取包及子包下的所有类
     *
     * @param basePackage basePackage
     * @return Set<Class < ?>> Set<Class<?>>
     */
    private Set<Class<?>> scannerPackages(String basePackage) {
        Set<Class<?>> set = new LinkedHashSet<>();

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

        if (basePackage == null) {
            packageSearchPath += DEFAULT_RESOURCE_PATTERN;
        } else {
            String resolveBasePackage = resolveBasePackage(basePackage);
            packageSearchPath += resolveBasePackage + '/' + DEFAULT_RESOURCE_PATTERN;
        }

        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz;
                    try { // Handler$Initializer
                        System.out.println("   | " + resource);
                        clazz = Class.forName(className);
                        set.add(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return set;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private ResourcePatternResolver resourcePatternResolver;

    private ApplicationContext applicationContext;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }
}
