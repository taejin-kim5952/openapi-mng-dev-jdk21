package com.kt.openapi.web.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class SimpleCORSFilter  implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Methods", "GET,POST");
        response.setHeader("Access-Control-Max-Age", "3600");
        //--[drm][chg]
        //--##response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        //--[!]OPTIONS이후 main call이 안되는 issue에서 "Access-Control-Allow-Headers"에 "content-type"을 추가하는것이 유효했음.
		//--[!]아래는 org.apache.catalina.filters.CorsFilter사용시 기본 설정을 사용
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
        response.setHeader("Access-Control-Allow-Origin", "*");
        //--[drm][add]
        //--[!]아래는 org.apache.catalina.filters.CorsFilter사용시 기본 설정을 사용
        response.setHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials");
        // frame
        response.setHeader("X-Frame-Options", "ALLOW-FROM http://openapis.kt.com");
        
        String userAgent = request.getHeader("User-Agent");
        String customHeader = request.getHeader("X-Auth-Token");

        // 예시: User-Agent에 스크립트나 특수문자 포함되면 공격으로 간주
        if (userAgent != null && userAgent.matches(".*[<>\"].*")) {
            // 공통 에러 페이지로 리다이렉트
        	response.sendRedirect("/error.do");
            return;
        }
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "OPTIONS not allowed");
			return;
		}
        
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
      //-- [2023:codeeyes][empty_block issue]
    }

    public void destroy() {
      //-- [2023:codeeyes][empty_block issue]
    }
}