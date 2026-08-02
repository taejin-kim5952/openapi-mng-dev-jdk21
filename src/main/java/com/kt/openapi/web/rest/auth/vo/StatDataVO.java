package com.kt.openapi.web.rest.auth.vo;

public class StatDataVO {
	
	private String login_yn; // 로그인 여부
	private String auth_yn; 	// 해당 API 대한 권한 그룹 포함 여부
	
	public String getLogin_yn() {
		return login_yn;
	}
	public void setLogin_yn(String login_yn) {
		this.login_yn = login_yn;
	}
	public String getAuth_yn() {
		return auth_yn;
	}
	public void setAuth_yn(String auth_yn) {
		this.auth_yn = auth_yn;
	}
	
}
