package com.kt.openapi.web.mypage.vo;

import java.util.ArrayList;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.mypage.vo
* 2. 타입명 : MypageVO.java
* 3. 작성일 : 2017. 11. 30. 오후 2:41:10
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : MYPAGE
* </pre>
 */
public class MypageVO {
	
	private String autId;
    private String mbrId;
    private String autSttusCd;
    private String autApyDt;
    private String autApvDt;
    private String autApvr;
    private String usePerdStDt;
    private String userPerdFndDt;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    
    private String reviewRqtTypeCd;
    private String regSttusCd;
    private String showYn;
    private String bbsTypeCd;
    private String sysId;
    private String mgtSttusCd;
    private String mbrSttusCd; 
    
    private String pstingId;
    
    /**
	 * 사용자 시스템 목록
	 */
    private ArrayList<String> userSysIdList	= new ArrayList<String>();
    
    private ArrayList<String> userAutIdList	= new ArrayList<String>();
    
	public String getPstingId() {
		return pstingId;
	}
	public void setPstingId(String pstingId) {
		this.pstingId = pstingId;
	}
    
	public String getAutId() {
		return autId;
	}
	public void setAutId(String autId) {
		this.autId = autId;
	}
	public String getMbrId() {
		return mbrId;
	}
	public void setMbrId(String mbrId) {
		this.mbrId = mbrId;
	}
	public String getAutSttusCd() {
		return autSttusCd;
	}
	public void setAutSttusCd(String autSttusCd) {
		this.autSttusCd = autSttusCd;
	}
	public String getAutApyDt() {
		return autApyDt;
	}
	public void setAutApyDt(String autApyDt) {
		this.autApyDt = autApyDt;
	}
	public String getAutApvDt() {
		return autApvDt;
	}
	public void setAutApvDt(String autApvDt) {
		this.autApvDt = autApvDt;
	}
	public String getAutApvr() {
		return autApvr;
	}
	public void setAutApvr(String autApvr) {
		this.autApvr = autApvr;
	}
	public String getUsePerdStDt() {
		return usePerdStDt;
	}
	public void setUsePerdStDt(String usePerdStDt) {
		this.usePerdStDt = usePerdStDt;
	}
	public String getUserPerdFndDt() {
		return userPerdFndDt;
	}
	public void setUserPerdFndDt(String userPerdFndDt) {
		this.userPerdFndDt = userPerdFndDt;
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
		this.regr = regr;
	}
	public String getAmdDt() {
		return amdDt;
	}
	public void setAmdDt(String amdDt) {
		this.amdDt = amdDt;
	}
	public String getAmdr() {
		return amdr;
	}
	public void setAmdr(String amdr) {
		this.amdr = amdr;
	}
	public String getReviewRqtTypeCd() {
		return reviewRqtTypeCd;
	}
	public void setReviewRqtTypeCd(String reviewRqtTypeCd) {
		this.reviewRqtTypeCd = reviewRqtTypeCd;
	}
	public String getRegSttusCd() {
		return regSttusCd;
	}
	public void setRegSttusCd(String regSttusCd) {
		this.regSttusCd = regSttusCd;
	}
	public String getShowYn() {
		return showYn;
	}
	public void setShowYn(String showYn) {
		this.showYn = showYn;
	}
	public String getBbsTypeCd() {
		return bbsTypeCd;
	}
	public void setBbsTypeCd(String bbsTypeCd) {
		this.bbsTypeCd = bbsTypeCd;
	}
	public String getSysId() {
		return sysId;
	}
	public void setSysId(String sysId) {
		this.sysId = sysId;
	}
	public String getMgtSttusCd() {
		return mgtSttusCd;
	}
	public void setMgtSttusCd(String mgtSttusCd) {
		this.mgtSttusCd = mgtSttusCd;
	}
	public String getMbrSttusCd() {
		return mbrSttusCd;
	}
	public void setMbrSttusCd(String mbrSttusCd) {
		this.mbrSttusCd = mbrSttusCd;
	}
	public ArrayList<String> getUserSysIdList() {
		return userSysIdList;
	}
	public void setUserSysIdList(ArrayList<String> userSysIdList) {
		this.userSysIdList = userSysIdList;
	}
	public ArrayList<String> getUserAutIdList() {
		return userAutIdList;
	}
	public void setUserAutIdList(ArrayList<String> userAutIdList) {
		this.userAutIdList = userAutIdList;
	}
}
