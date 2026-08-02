package com.kt.openapi.web.bbs.cmn.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BbsCommentVo extends DefaultVo {

	private String pstingId;
	private String replNo;
	private String sbst;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;

}
