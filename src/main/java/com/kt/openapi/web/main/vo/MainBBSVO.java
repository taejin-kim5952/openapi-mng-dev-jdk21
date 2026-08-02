package com.kt.openapi.web.main.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.main.vo
 * 2. 타입명   : MainBBSVO.java
 * 3. 작성일   : 2026. 05. 08.
 * 4. 작성자   : AgenticDevTeam
 * 5. 설명     : 메인 페이지 BBS(공지사항/개발자포럼) 최신글 조회용 VO
 * </pre>
 */
@Getter
@Setter
@ToString
public class MainBBSVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String pstingId;
	private String bbsTypeCd;
	private String pstingNo;
	private String title;
	private String sbst;
	private String retvNum;
	private String showYn;
	private String imptYn;
	private String delYn;
	private String regDate;

}
