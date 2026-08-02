package com.kt.openapi.web.adptran.api.common.message;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.kt.openapi.web.adptran.api.AdptranApiConst;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;

import net.sf.json.JSONObject;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice implements ResponseBodyAdvice<Object> {

	private static final Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body,
								  MethodParameter methodParameter,
								  MediaType mediaType,
								  Class<? extends HttpMessageConverter<?>> messageConverter,
								  ServerHttpRequest request,
								  ServerHttpResponse response) {

		RestMessage message = new GenericMessage();

		if (body instanceof RestMessage restMessage) {
			//RestMessage Wrapping
			message = restMessage;
		}
		else if (body instanceof String) {
			//String bypass
			return body;
		}
		else if (body instanceof JSONObject) {
			//JSONObject bypass
			return body;
		}
		else {
			message = (RestMessage) RequestContextHolder.getRequestAttributes().getAttribute(AdptranApiConst.GENERIC_MESSAGE, RequestAttributes.SCOPE_REQUEST);

			if (message == null) message = new GenericMessage();
			if (body != null) {
				message.setData(body);
			}
		}

		// 리턴 데이터
		//--[drm][cmt]logger.debug("RestControllerMessageAdvice.beforeBodyWrite(): {}", ((GenericMessage) message).toString());

		return message;
	}

	/**
	 * 기본 예외처리 헨들러, 다른 예외처리 헨들러에서 처리되지 않은 예외들을 처리함.
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GenericMessage> handleOtherExceptions(Exception ex, WebRequest request) {
		logger.info("\n\n### {}.{}() [Exception: {}][WebRequest: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), ex, request);
		logger.debug("[Exception][ex.getMessage(): {}][ex.toString(): {}]", ex.getMessage(), ex.toString());

		Object exCauseClass = null;
		if(ex.getCause() != null) {
			exCauseClass = ex.getCause().getClass();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		GenericMessage message = new GenericMessage();
		
		/*
		 * 설명: 소스검증도구(CodeEyes) 진단에 의한 수정
		 * 		"== -> 인스턴스객체.equals(Object)" 객체 연산자로 변경
		 * 개발일:2019. 10. 16
		 * 개발자:CYD
		 */
		if (exCauseClass != null && exCauseClass.equals(SQLException.class)) {
			logger.info("\n\n### {}.{}() [DataBaseException] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
			//--##logger.info("handleOtherExceptions() DataBaseException ");
			setDataBaseException(message, ex);
		} else if (ex instanceof AccessDeniedException) {
			logger.info("\n\n### {}.{}() [AccessDeniedException] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
			//--##logger.info("handleOtherExceptions() AccessDeniedException ");
			setAccessDeniedException(message, ex);
		} else {
			logger.info("\n\n### {}.{}() [runtime error] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
			//--##logger.info("handleOtherExceptions() runtime error. ", ex);
			setDefaultError(message, ex);
		}

		return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void setDataBaseException(RestMessage message, Exception ex) {
		message.setResultCode(AdptranApiResultCode.DB_EXCEPTION.getCode());
		message.setResultMessage(AdptranApiResultCode.DB_EXCEPTION.getMessage());
		//message.setData(ex.toString());
		message.setData("DB처리 예외가 발생 하였습니다.");
	}
	private void setAccessDeniedException(RestMessage message, Exception ex) {
		message.setResultCode(AdptranApiResultCode.ACCESS_DENIED.getCode());
		message.setResultMessage(AdptranApiResultCode.ACCESS_DENIED.getMessage());
		message.setData("접근 권한이 없습니다.");
	}
	private void setDefaultError(RestMessage message, Exception ex) {
		AdptranApiResultCode defaultError = AdptranApiResultCode.RC_500_SERVER_INTERNAL_ERROR;
		message.setResultCode(defaultError.getCode());
		message.setResultMessage(defaultError.getMessage());
		message.setData(ex.toString());
	}
}
