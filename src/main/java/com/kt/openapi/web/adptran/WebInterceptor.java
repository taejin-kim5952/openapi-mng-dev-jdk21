package com.kt.openapi.web.adptran;
//--## [tag:adpt][drm][ing]

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class WebInterceptor implements HandlerInterceptor {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
								Exception exception) throws Exception {
		logger.debug("WebInterceptor.afterCompletion()");

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
						   ModelAndView exception) throws Exception {
//		logger.debug("================================================");
//		Enumeration<String> headerNames = request.getHeaderNames();
//		while(headerNames.hasMoreElements()) {
//			String name = (String)headerNames.nextElement();
//			String value = request.getHeader(name);
//			logger.debug("request = " + name + " : " + value);
//		}
//
//		Collection<String> headerNames1 = response.getHeaderNames();
//		headerNames1.forEach((headerName) -> {
//			String value = response.getHeader(headerName);
//			logger.debug("response = " + headerName + " : " + value);
//		});

		logger.debug("WebInterceptor.postHandle()");
//		logger.debug("================================================");

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object exception)
			throws Exception {

		// Context Path
		//String contextPath = request.getContextPath();

		// Context Path를 제외한 URI
		//String uri = request.getRequestURI().replaceAll(contextPath, "");

		// QueryString
		//String queryString = request.getQueryString();

		//logger.debug("WebInterceptor.preHandle() contextPath = {} " , contextPath);
		//logger.debug("WebInterceptor.preHandle() uri = {} " , uri);
		//logger.debug("WebInterceptor.preHandle() queryString = {} " , queryString);

		//if(CommonUtil.getUserIdFromHeaderToken(request) == null) {
            //TODO : exception 혹은 return false 처리
        //}

		return true;
	}

}
