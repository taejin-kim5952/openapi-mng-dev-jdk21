package com.kt.openapi.web.rest.auth.vo;

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
public class SearchVO{
	
	private String mbrId; //회원아이디
	private String apiNo; //api 번호
	private String sessionKey; //
	
	
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
	
	


	public String getSessionKey() {
		return sessionKey;
	}



	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}



	@Override
	public String toString() {
		return "SearchVO [mbr_id=" + mbrId + ", api_no=" + apiNo + ", sessionKey=" + sessionKey + "]";
	}
}
