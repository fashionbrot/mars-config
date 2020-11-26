package com.github.fashionbrot.value;


import com.github.fashionbrot.value.config.MarsValueBeanDefinitionRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(MarsConfigValueConfigurationProperties.class)
@Import(value = {
        MarsValueBeanDefinitionRegistrar.class
})
public class MarsConfigValueAutoConfiguration {



}
