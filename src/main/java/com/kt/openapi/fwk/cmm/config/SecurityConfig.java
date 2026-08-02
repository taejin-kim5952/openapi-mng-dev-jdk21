package com.kt.openapi.fwk.cmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * [마이그레이션] web.xml의 <login-config> (BASIC auth) 대체
 *
 * 원래 web.xml의 상태:
 *   - <auth-method>BASIC</auth-method> 설정은 존재
 *   - 하지만 <security-constraint>가 없음 → 실제 강제 적용 X
 *   - 인증은 SessionCheckInterceptor(세션 기반)가 담당
 *
 * 따라서 Spring Security도 동일한 동작을 재현:
 *   - BASIC auth는 활성화하지만 security-constraint 없음으로 모든 경로 permitAll
 *   - 기존 세션 기반 인증 흐름(SessionCheckInterceptor) 유지
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (API 서버 + 기존 세션 기반 인증 유지)
            .csrf(csrf -> csrf.disable())

            // 원래 web.xml에도 <security-constraint>가 없으므로 모든 요청 허용
            // 실제 인증은 SessionCheckInterceptor가 담당
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Form Login 비활성화 (기존 LoginController가 담당)
            .formLogin(form -> form.disable())

            // BASIC auth 활성화 (web.xml의 <auth-method>BASIC</auth-method> 대응)
            // security-constraint가 없으므로 실제 강제 적용은 없음
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
