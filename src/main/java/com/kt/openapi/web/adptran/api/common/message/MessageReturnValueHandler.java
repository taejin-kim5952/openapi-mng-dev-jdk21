package com.kt.openapi.web.adptran.api.common.message;
//--## [tag:adpt][drm][ing]

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MessageReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageReturnValueHandler.class);

	@Override
	public boolean supportsReturnType(MethodParameter parameter) {
		return RestMessage.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, 
								  MethodParameter returnType,
								  ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest) throws Exception {
		// TODO Auto-generated method stub
		GenericMessage messages = (GenericMessage)returnValue;

        logger.debug("MessageReturnValueHandler.handleReturnValue() messages= {}", messages);
        
        mavContainer.setRequestHandled(true);
    	/*--[drm][ing]
        webRequest
            .getNativeResponse(HttpServletResponse.class)
            .getWriter()
            .write(messages.getMessageMap().toString());
        */
        webRequest
        .getNativeResponse(HttpServletResponse.class)
        .getWriter()
        .write(messages.toString());
	}

}
