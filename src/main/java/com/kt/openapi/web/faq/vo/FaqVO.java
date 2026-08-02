package com.kt.openapi.web.faq.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.faq.vo
* 2. 타입명 : FaqVO.java
* 3. 작성일 : 2017. 11. 30. 오후 1:47:51
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : FAQ
* </pre>
 */
public class FaqVO  extends DefaultVo{

	private String faqId;
	private String faqCtgryCd;
	private String title;
	private String sbst;
	private String showYn;
	private String top5Yn;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	private String rownum;
	private String faqCtgryCdNm;
	
	public String getFaqId() {
		return faqId;
	}
	public void setFaqId(String faqId) {
		this.faqId = faqId;
	}
	public String getFaqCtgryCd() {
		return faqCtgryCd;
	}
	public void setFaqCtgryCd(String faqCtgryCd) {
		this.faqCtgryCd = faqCtgryCd;
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
	public String getShowYn() {
		return showYn;
	}
	public void setShowYn(String showYn) {
		this.showYn = showYn;
	}
	public String getTop5Yn() {
		return top5Yn;
	}
	public void setTop5Yn(String top5Yn) {
		this.top5Yn = top5Yn;
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
	public String getRownum() {
		return rownum;
	}
	public void setRownum(String rownum) {
		this.rownum = rownum;
	}
	public String getFaqCtgryCdNm() {
		return faqCtgryCdNm;
	}
	public void setFaqCtgryCdNm(String faqCtgryCdNm) {
		this.faqCtgryCdNm = faqCtgryCdNm;
	}
	
}
