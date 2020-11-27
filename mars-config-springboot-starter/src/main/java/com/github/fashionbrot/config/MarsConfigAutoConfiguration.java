package com.github.fashionbrot.config;


import com.github.fashionbrot.spring.config.MarsConfigBeanDefinitionRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(MarsConfigConfigurationProperties.class)
@Import(value = {
        MarsConfigBeanDefinitionRegistrar.class
})
public class MarsConfigAutoConfiguration {



}
