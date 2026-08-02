package com.kt.openapi.fwk.cmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 및 스케줄러 설정 클래스
 * [마이그레이션] GwApiServiceImpl 등의 @Async 및 @Scheduled 어노테이션 활성화를 위한 설정
 * [mngOnm 표준 이식] 효율적인 쓰레드 풀 관리를 통해 시스템 자원 최적화
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * 비동기 실행을 위한 쓰레드 풀 설정 (@Async)
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);        // 기본 쓰레드 수
        executor.setMaxPoolSize(50);         // 최대 쓰레드 수
        executor.setQueueCapacity(100);      // 대기 큐 사이즈
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

    /**
     * 스케줄러 실행을 위한 쓰레드 풀 설정 (@Scheduled)
     * [보안 보완] 여러 개의 스케줄러가 병렬로 작동할 수 있도록 별도의 풀 구성
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);           // 동시 실행 가능한 스케줄러 쓰레드 수
        scheduler.setThreadNamePrefix("ScheduleExecutor-");
        scheduler.initialize();
        return scheduler;
    }
}
