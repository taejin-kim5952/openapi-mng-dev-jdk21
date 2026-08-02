package com.kt.openapi.web.bbs.cmn.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;

public class BbsSearchVo  extends  DefaultVo{
	
	 private String pstingId  ;
     private String title     	   ;
     private String sbst     ;
     private String bbsTypeCd   ;
     private String imptYn      ;
     private String regDt    ;
     private String regr    ;
     private String amdDt   ;
     private String amdr   ;
     private String showYn        ;
     
	public String getPstingId() {
		return pstingId;
	}
	public void setPstingId(String pstingId) {
		this.pstingId = pstingId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSbst() {
		return sbst;
	}
	public void setSbst(String sbst) {
		this.sbst = sbst;
	}
	public String getBbsTypeCd() {
		return bbsTypeCd;
	}
	public void setBbsTypeCd(String bbsTypeCd) {
		this.bbsTypeCd = bbsTypeCd;
	}
	public String getImptYn() {
		return imptYn;
	}
	public void setImptYn(String imptYn) {
		this.imptYn = imptYn;
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
	public String getShowYn() {
		return showYn;
	}
	public void setShowYn(String showYn) {
		this.showYn = showYn;
	}
     
}
