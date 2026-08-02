package com.kt.openapi.fwk.cmm.config;

import com.kt.openapi.web.util.SimpleCORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 서블릿 필터 설정 클래스
 * [마이그레이션] web.xml에 정의된 Filter들을 Java Config로 전환
 */
@Configuration
public class FilterConfig {

    /**
     * CORS 필터 등록
     */
    @Bean
    public FilterRegistrationBean<SimpleCORSFilter> corsFilter() {
        FilterRegistrationBean<SimpleCORSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SimpleCORSFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
