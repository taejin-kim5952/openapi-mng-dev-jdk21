package com.kt.openapi.web.cmmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serial;

public class ApiException extends Exception {

	private static final Logger LOG = LoggerFactory.getLogger(ApiException.class);
	
	private String code;
	private String message;
	private String data;

	@Serial
	private static final long serialVersionUID = 1L;
	private static String m_szExceptionMessage = "";

	// 에러코드 기본값
	private int ERR_CODE = 100;

	public ApiException(ResultCode_dep code, String data) {

		this.code = code.getCode();
		
		this.message = code.getMessage();
		
		this.data = data;
	}
	

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	public String getData() {
		return data;
	}
	
	public ApiException(Throwable e, String msg) {
		super(msg == null ? e.getMessage() : msg);

		m_szExceptionMessage = msg == null ? e.getMessage() : msg;
		LOG.error("MESSAGE : " + m_szExceptionMessage, e);
	}
	
	public ApiException(Throwable e, PlatformTransactionManager txManager, TransactionStatus txStatus, String msg) {
		super(msg == null ? e.getMessage() : msg);
		
		if(TransactionSynchronizationManager.isActualTransactionActive()) {
			if(txManager != null && txStatus != null) {
				txManager.rollback(txStatus);
				//TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				
				String[] errCode = txStatus.toString().split("@");
				ERR_CODE = 102;
				m_szExceptionMessage = "::[@" + errCode[1] + "]Transaction Rollback";
				//logger.warn("MESSAGE : {},  ERRORCODE : {}", "[@" + errCode[1] + "]Transaction Rollback", ERR_CODE);
				//logger.debug("MESSAGE : {},  ERRORCODE : {}", "[@" + errCode[1] + "]Transaction Rollback", ERR_CODE);
	
			}
		}

		m_szExceptionMessage = (msg == null ? e.getMessage() : msg) + m_szExceptionMessage;
		LOG.error("MESSAGE : " + m_szExceptionMessage, e);
	}
	
	public ApiException(String msg, int errCode) {
		super(msg);
		LOG.warn("MESSAGE : {},  ERRORCODE : {}", msg, ERR_CODE);
		ERR_CODE = errCode;
	}
	
	public ApiException(String msg) {
		this(msg, 100); // 기본값 error code 100으로
	}
	
	public int getErrorCode() {
		return ERR_CODE;
	}
}


