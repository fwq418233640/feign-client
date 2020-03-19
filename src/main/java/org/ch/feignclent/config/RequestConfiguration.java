package org.ch.feignclent.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author ch
 * <p>
 * 添加 Content-Type application/json
 * 对于文件上传 可能造成一定影响
 */
public class RequestConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //Content-Type -> {HeaderTemplate@16646} "Content-Type application/json;charset=UTF-8"
        template.header("Content-Type", "application/json");
    }
}
