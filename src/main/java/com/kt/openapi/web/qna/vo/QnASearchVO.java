package com.kt.openapi.web.qna.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;

import java.io.Serial;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.vo
* 2. 타입명 : QnASearchVO.java
* 3. 작성일 : 2017. 11. 30. 오후 1:59:30
* 4. 작성자 : user
* 5. 설명 : qna 목록조회
* </pre>
*/
public class QnASearchVO extends DefaultVo {

	@Serial
	private static final long serialVersionUID = 4969112282196452913L;

	private String qnaId                       ;
	private String title                       ;
	private String qstn                        ;
	private String ans                         ;
	private String qstnr                       ;
	private String qstnDt                     ;
	private String ansr                        ;
	private String ansDt                      ;
	private String qnaSttusCd                ;
	private String showYn                     ;
	private String regDt                      ;
	private String regr                        ;
	private String amdDt                      ;
	private String amdr                        ;
	
	
	public String getQnaId() {
		return qnaId;
	}
	public void setQnaId(String qnaId) {
		this.qnaId = qnaId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getQstn() {
		return qstn;
	}
	public void setQstn(String qstn) {
		this.qstn = qstn;
	}
	public String getAns() {
		return ans;
	}
	public void setAns(String ans) {
		this.ans = ans;
	}
	public String getQstnr() {
		return qstnr;
	}
	public void setQstnr(String qstnr) {
		this.qstnr = qstnr;
	}
	public String getQstnDt() {
		return qstnDt;
	}
	public void setQstnDt(String qstnDt) {
		this.qstnDt = qstnDt;
	}
	public String getAnsr() {
		return ansr;
	}
	public void setAnsr(String ansr) {
		this.ansr = ansr;
	}
	public String getAnsDt() {
		return ansDt;
	}
	public void setAnsDt(String ansDt) {
		this.ansDt = ansDt;
	}
	public String getQnaSttusCd() {
		return qnaSttusCd;
	}
	public void setQnaSttusCd(String qnaSttusCd) {
		this.qnaSttusCd = qnaSttusCd;
	}
	public String getShowYn() {
		return showYn;
	}
	public void setShowYn(String showYn) {
		this.showYn = showYn;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "QnASearchVO [qnaId=" + qnaId + ", title=" + title + ", qstn=" + qstn + ", ans=" + ans + ", qstnr="
				+ qstnr + ", qstnDt=" + qstnDt + ", ansr=" + ansr + ", ansDt=" + ansDt + ", qnaSttusCd=" + qnaSttusCd
				+ ", showYn=" + showYn + ", regDt=" + regDt + ", regr=" + regr + ", amdDt=" + amdDt + ", amdr=" + amdr
				+ "]";
	}
	
}
