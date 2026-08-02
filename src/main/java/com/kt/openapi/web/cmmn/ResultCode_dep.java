package com.kt.openapi.web.cmmn;

//-- [tag:20200930][dep][i][사용치 않는것으로 판단][ResultCode -> ResultCode_dep]
public enum ResultCode_dep {
	
	BIZ_EXCEPTION("900", "BIZ_EXCEPTION");
	
	private String code;
	private String message;
	
	private ResultCode_dep(String code, String message) {
		
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		
		return code;
	}
	
	public String getMessage() {
		
		return message;
	}


}
