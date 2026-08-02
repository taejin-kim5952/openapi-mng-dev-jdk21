package com.kt.openapi.web.userJoin.vo;

import org.springframework.beans.factory.annotation.Autowired;

import com.kt.openapi.web.util.CommonFunc;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.userJoin.vo
* 2. 타입명 : UserHistVO.java
* 3. 작성일 : 2017. 11. 30. 오후 2:57:35
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 회원 가입 이력
* </pre>
 */
public class UserHistVO {
	
	@Autowired
	private static CommonFunc commonFunc;
	
	private String hstNo;
	private String autId;
	private String mbrId;
	private String mgtSttusCd;
	private String memo;
	private String regDt;
	private String regr;
	
	public String getHstNo() {
		return hstNo;
	}
	public void setHstNo(String hstNo) {
		this.hstNo = hstNo;
	}
	public String getAutId() {
		return autId;
	}
	public void setAutId(String autId) {
		this.autId = commonFunc.urlDecodeStr(autId);
	}
	public String getMbrId() {
		return mbrId;
	}
	public void setMbrId(String mbrId) {
		this.mbrId = commonFunc.urlDecodeStr(mbrId);
	}
	public String getMgtSttusCd() {
		return mgtSttusCd;
	}
	public void setMgtSttusCd(String mgtSttusCd) {
		this.mgtSttusCd = mgtSttusCd;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getRegr() {
		return regr;
	}
	public void setRegr(String regr) {
		this.regr = commonFunc.urlDecodeStr(regr);
	}
	
}
