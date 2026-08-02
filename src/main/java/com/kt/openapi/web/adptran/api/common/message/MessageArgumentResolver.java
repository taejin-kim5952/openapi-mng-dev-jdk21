package com.kt.openapi.web.adptran.api.common.message;
//--## [tag:adpt][drm][ing]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kt.openapi.web.adptran.api.AdptranApiConst;

public class MessageArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(MessageArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return RestMessage.class.isAssignableFrom(parameter.getParameterType());
    }

    //--##public MessageArgumentResolver() {}

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

    	RestMessage messages = new GenericMessage();
        // messages.setOK();

        RequestContextHolder.getRequestAttributes().setAttribute(AdptranApiConst.GENERIC_MESSAGE, messages, RequestAttributes.SCOPE_REQUEST);

        logger.debug("MessageArgumentResolver.resolveArgument() messages= {}", messages);

        return messages;
    }

}
