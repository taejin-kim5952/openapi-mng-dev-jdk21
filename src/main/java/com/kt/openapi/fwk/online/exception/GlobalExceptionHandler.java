package com.kt.openapi.fwk.online.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리기
 * [마이그레이션] 전자정부 ExceptionTransfer를 대체하는 Spring 표준 예외 처리
 * [mngOnm 표준 이식]
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BizException.class)
    public String handleBizException(BizException e, Model model) {
        LOGGER.error("Biz Exception: ", e);
        model.addAttribute("exception", e);
        model.addAttribute("msg", e.getMessage());
        // 필요 시 메시지 키를 통한 다국어 처리 로직 추가 가능
        return "cmmn/public_error";
    }

    /**
     * 모든 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        LOGGER.error("Global Exception: ", e);
        model.addAttribute("exception", e);
        model.addAttribute("msg", e.getMessage());
        return "cmmn/public_error";
    }

    /**
     * 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        LOGGER.error("Runtime Exception: ", e);
        model.addAttribute("exception", e);
        model.addAttribute("msg", e.getMessage());
        return "cmmn/public_error";
    }
}
