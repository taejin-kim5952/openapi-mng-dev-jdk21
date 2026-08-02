package com.kt.openapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * OpenApiMng Spring Boot 메인 애플리케이션
 * - WAR 배포를 위해 SpringBootServletInitializer 상속
 * - @EnableRetry, @EnableScheduling 적용 (mngOnm 표준 준수)
 */
@EnableRetry
@EnableScheduling
@SpringBootApplication
public class StoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StoApplication.class, args);
    }
}
