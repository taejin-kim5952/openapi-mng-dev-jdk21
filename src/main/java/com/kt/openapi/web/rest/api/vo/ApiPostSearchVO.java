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
public class ApiPostSearchVO{
	
	private String mbr_id; //회원아이디
	private String api_no; //api 번호
	
	
	public String getMbr_id() {
		return mbr_id;
	}
	public void setMbr_id(String mbr_id) {
		this.mbr_id = mbr_id;
	}
	public String getApi_no() {
		return api_no;
	}
	public void setApi_no(String api_no) {
		this.api_no = api_no;
	}
	@Override
	public String toString() {
		return "SearchVO [mbr_id=" + mbr_id + ", api_no=" + api_no + "]";
	}
}
