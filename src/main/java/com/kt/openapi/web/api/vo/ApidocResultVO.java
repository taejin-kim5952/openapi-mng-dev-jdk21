package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApidocResultVO.java
 * 3. 작성일   : 2026. 05. 12.
 * 4. 작성자   : Migration
 * 5. 설명     : API doc 파일 업로드 결과 VO (EgovMap 제거)
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApidocResultVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String successYn;
	private String jsonStr;
}
