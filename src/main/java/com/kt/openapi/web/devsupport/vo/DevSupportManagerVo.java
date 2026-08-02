package com.kt.openapi.web.devsupport.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevSupportManagerVo extends DefaultVo {
	
	private int userSeq;
	private String userIdC;
	private String userNmC;
	private String telNo;
	private String email;
	private String depNm;
}
