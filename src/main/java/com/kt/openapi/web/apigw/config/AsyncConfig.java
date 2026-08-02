package com.kt.openapi.web.apigw.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration("apigwAsyncConfig")
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("openapi-async-");
        executor.initialize();
        return executor;
    }

    public static class ApiAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        public static final Logger log = LoggerFactory.getLogger(ApiAsyncExceptionHandler.class);

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            List<String> params = Arrays.stream(objects)
                    .map(Object::toString)
                    .collect(Collectors.toList());

            log.error("Async Exception, message={}, method={}, params=[{}]",
                    throwable.getMessage(), method.getName(), StringUtils.join(params, ","));
        }
    }
}
