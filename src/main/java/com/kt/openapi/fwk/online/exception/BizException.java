package com.kt.openapi.fwk.online.exception;

import java.io.Serial;

/**
 * 프로젝트 전역 비즈니스 예외 클래스
 * [마이그레이션] org.egovframe.rte.fdl.cmmn.exception.EgovBizException 대체
 */
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private String messageKey;
    private Object[] messageParameters;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(String message, String messageKey) {
        super(message);
        this.messageKey = messageKey;
    }

    public BizException(String message, String messageKey, Object[] messageParameters) {
        super(message);
        this.messageKey = messageKey;
        this.messageParameters = messageParameters;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageParameters() {
        return messageParameters;
    }
}
