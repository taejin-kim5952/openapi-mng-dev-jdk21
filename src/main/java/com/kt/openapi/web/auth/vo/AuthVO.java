package com.kt.openapi.web.auth.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.auth.vo
 * 2. 타입명   : AuthVO.java
 * 3. 작성일   : 2017. 11. 30. 오후 6:50:05
 * 4. 작성자   : ANEUNTAEK
 * 5. 설명     : 권한 VO
 * </pre>
 */
@Getter
@Setter
@ToString
public class AuthVO {

	private String autId;
	private String sysId;
	private String sysNm;
	private String autNm;
	private String useYn;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	private String mbrId;
	private String autSttusCd;
	private String autApyDt;
	private String autApvDt;
	private String autApvr;
	private String usePerdStDt;
	private String usePerdFnsDt;
	private String userUseYn;

}
