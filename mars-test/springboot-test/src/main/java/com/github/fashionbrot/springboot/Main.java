package com.github.fashionbrot.springboot;

import com.github.fashionbrot.spring.config.annotation.EnableMarsConfig;
import com.github.fashionbrot.value.config.annotation.EnableMarsValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@EnableMarsConfig
//@EnableMarsValue(envCode = "beta",appId = "app",serverAddress ="localhost:8889",listenLongPollMs ="5000",enableListenLog = "true")
public class Main  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}