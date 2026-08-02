package com.kt.openapi.web.qna.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.vo
* 2. 타입명 : QnASaveVO.java
* 3. 작성일 : 2017. 11. 30. 오후 5:35:26
* 4. 작성자 : user
* 5. 설명 : qna 등록 vo
* [마이그레이션] Lombok 적용 및 validator.xml 규칙 이식
* </pre>
*/
@Getter
@Setter
@ToString
public class QnASaveVO {

	private String qnaId;

	@NotBlank(message="제목은 필수 입력값입니다.")
	private String title;

	@NotBlank(message="내용은 필수 입력값입니다.")
	private String qstn;
	private String ans;
	private String qstnr;
	private String qstnDt;
	private String ansr;
	private String ansDt;
	private String qnaSttusCd;
	private String showYn;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	
	private String delAtcFileNo;
	
}
