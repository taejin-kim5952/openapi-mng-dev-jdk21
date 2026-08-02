package com.kt.openapi.web.bbs.cmn.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BbsSaveVo {

	private String pstingId;

	@NotBlank(message="제목은 필수 입력값입니다.")
	private String title;

	@NotBlank(message="내용은 필수 입력값입니다.")
	private String sbst;
	private String bbsTypeCd;
	private String imptYn;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	private String showYn;

}
