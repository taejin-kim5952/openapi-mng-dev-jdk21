package com.kt.openapi.fwk.cmm.config;

import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * 공통 Bean 설정 클래스
 * [마이그레이션] context-common.xml 내용을 Java Config로 전환
 * [보안 보완] HTTP Method 제한 및 WAS 수준의 보안 설정 추가
 */
@Configuration
public class CommonConfig {

    /**
     * AntPathMatcher 설정
     * 경로 패턴 매칭 시 활용
     */
    @Bean
    public AntPathMatcher antPathMater() {
        return new AntPathMatcher();
    }

    /**
     * HTTP 메소드 제한 설정 (WAS 보안 강화)
     * [mngOnm 표준] DELETE, OPTIONS, TRACE 메소드 차단
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("NONE");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            collection.setName("Restricted Methods");
            collection.addMethod("DELETE");
            collection.addMethod("OPTIONS");
            collection.addMethod("TRACE");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
        });
    }
}
