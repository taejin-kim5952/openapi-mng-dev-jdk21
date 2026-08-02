package com.kt.openapi.web.util;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 메시지 처리 유틸리티 (MessageUtil 현대화)
 * [마이그레이션] Spring MessageSource를 사용하여 메시지를 조회하도록 리팩토링함.
 */
@Component
public class MessageUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

	@Autowired
	private MessageSource messageSource;

	private static MessageSource staticMessageSource;

	@PostConstruct
	public void init() {
		staticMessageSource = messageSource;
	}

    /**
     * 해당되는 속성키로부터 에러 메시지를 얻는다.
     *
     * @param strCode
     * @return
     */
    public static String getMsg(String strCode) {
		return getMsg(strCode, null);
    }

    /**
     * 해당되는 속성키로부터 에러 메시지(파라미터 변환 포함)를 얻는다.
     *
     * @param strCode
     * @param arrParam
     * @return
     */
    public static String getMsg(String strCode, String[] arrParam) {
		if (strCode == null || strCode.trim().isEmpty()) {
			return "";
		}

		try {
			if (staticMessageSource == null) {
				LOG.warn("MessageSource is not initialized yet. Code: {}", strCode);
				return strCode;
			}
			return staticMessageSource.getMessage(strCode, arrParam, LocaleContextHolder.getLocale());
		} catch (Exception e) {
			LOG.error("Exception in getMsg for code [{}]: {}", strCode, e.getMessage());
			return strCode; // 메시지를 찾지 못하면 키를 그대로 반환
		}
    }
}
