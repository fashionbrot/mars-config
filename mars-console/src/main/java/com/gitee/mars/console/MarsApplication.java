package com.gitee.mars.console;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Date;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.gitee.mars"})
@MapperScan(basePackages = {"com.gitee.mars.dao.dao"})
public class MarsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MarsApplication.class,args);
        log.info("Start to finish:{}", new Date().getTime());
    }

}
