package com.kt.openapi.web.guide.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.guide.vo
 * 2. 타입명   : GuideShubVO.java
 * 3. 작성일   : 2026. 05. 08.
 * 4. 작성자   : AgenticDevTeam
 * 5. 설명     : SHUB 가이드 페이지 API 목록 조회용 VO
 * </pre>
 */
@Getter
@Setter
@ToString
public class GuideShubVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String rownum;
	private String apiSpcNo;
	private String apiSpcId;
	private String ver;
	private String apiNm;
	private String apiDesc;
	private String amdr;
	private String amdrNm;
	private String amdDt;
	private String amdDtFmt;
	private String amdDtStr;
	private String regSttusCd;
	private String regSttusCdNm;
	private String sysId;
	private String sysIdNm;
	private String apiDefNm;
	private String apiDefDesc;
	private String methodCd;
	private String apiNo;
	private String methodNm;

}
