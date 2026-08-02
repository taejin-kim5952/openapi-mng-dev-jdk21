package com.kt.openapi.web.rest.api.vo;

/**
 * <pre>
 * kr.co.squarenet.ecms.ebs.eas.copyright.vo
 * SearchCopyrightVO.java
 * </pre>
 * 
 * @filename: SearchCopyrightVO.java
 * @author  : 오진수
 * @date    : 2015. 3. 16.
 * @version :
 * @comment :
 * @see     :
 */
public class ApiPutSearchVO{
	
	private String mbrId; 	//회원아이디
	private String apiNo; 	//api 번호
	private String yaml; 	// Yaml
	private String sessionKey; 	// sessionKey
	
	
	
	
	public String getMbrId() {
		return mbrId;
	}




	public void setMbrId(String mbrId) {
		this.mbrId = mbrId;
	}




	public String getApiNo() {
		return apiNo;
	}




	public void setApiNo(String apiNo) {
		this.apiNo = apiNo;
	}




	public String getYaml() {
		return yaml;
	}




	public void setYaml(String yaml) {
		this.yaml = yaml;
	}




	public String getSessionKey() {
		return sessionKey;
	}




	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}




	@Override
	public String toString() {
		return "SearchVO [mbr_id=" + mbrId + ", api_no=" + apiNo + ", yaml=" + yaml +" , sessionKey=" + sessionKey + "]";
	}
}
