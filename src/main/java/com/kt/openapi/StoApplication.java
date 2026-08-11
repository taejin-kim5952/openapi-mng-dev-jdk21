package com.kt.openapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * OpenApiMng Spring Boot 메인 애플리케이션
 * - JAR(내장 톰캣) 단독 실행
 * - @EnableRetry, @EnableScheduling 적용 (mngOnm 표준 준수)
 */
@EnableRetry
@EnableScheduling
@SpringBootApplication
public class StoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoApplication.class, args);
    }
}
