package com.kt.openapi.web.adptran;
//--## [tag:adpt][drm][ing]

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//--##@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //--## argumentResolvers.add(new MessageArgumentResolver());
    }

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		//--## returnValueHandlers.add(new MessageReturnValueHandler());
	}

	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		//--## configurer.enable();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(localeChangeInterceptor());
    	//--## registry.addInterceptor(new WebInterceptor());
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//--## converters.add(new MappingJackson2HttpMessageConverter());

		//--##[drm][ing] super.configureMessageConverters(converters);
		//--##converters.add(CustomConveter());
    }

	/*--[not work][ing][try for mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true); ]
    private HttpMessageConverter<?> CustomConveter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        MediaType mediaType_applicationJsonUTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));
        mediaTypes.add(mediaType_applicationJsonUTF8);
        mediaTypes.add(MediaType.TEXT_HTML);
        jacksonConverter.setSupportedMediaTypes(mediaTypes);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        jacksonConverter.setObjectMapper(objectMapper);
    
    	return jacksonConverter;
    }
    --*/
}
