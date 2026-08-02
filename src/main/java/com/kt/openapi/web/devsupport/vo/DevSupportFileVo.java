package com.kt.openapi.web.devsupport.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevSupportFileVo implements Serializable {

	@Serial
	private static final long serialVersionUID = 699436723589855368L;
	
	private String atcFileNo;
	private String pstingId;
	private String saveFileNm;
	private String originFileNm;
	private String fileTypeCd;
	private String filePath;
	private long fileSize;
	private String useYn;
	private String downlCnt;
	private String showOdrg;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
}
