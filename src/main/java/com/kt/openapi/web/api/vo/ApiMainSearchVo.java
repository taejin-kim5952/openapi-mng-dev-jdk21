package com.kt.openapi.web.api.vo;

import com.kt.openapi.web.sample.vo.SampleDefaultVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.vo
* 2. 타입명 : ApiMainSearchVo.java
* 3. 작성일 : 2017. 12. 8. 오후 1:20:28
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : OPEN API 검색 VO
* </pre>
*/
@Getter
@Setter
@ToString(callSuper = true)
public class ApiMainSearchVo extends SampleDefaultVO {

	@Serial
	private static final long serialVersionUID = 1L;

	private String ctgryNm;
	private String sysId;

	//화면 구분
	private String searchYn = "";

	/** 검색어필드 */
	private String schText = "";

	//--[tag:20200708][add]
	//-- 사용자권한 ['all':모든권한]
	private String regSttusCd;
}
