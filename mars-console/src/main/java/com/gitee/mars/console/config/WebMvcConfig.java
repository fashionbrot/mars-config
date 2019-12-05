package com.gitee.mars.console.config;

import com.gitee.mars.console.interceptor.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public Interceptor getInterceptor(){
        return new Interceptor();
    }

    @Bean
    public ReturnValueHandler getReturnValueHandler(){
        return new ReturnValueHandler();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(getReturnValueHandler());
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(getInterceptor())
                .addPathPatterns("/api/**");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "OPTIONS")
                .allowCredentials(true);
    }

}
