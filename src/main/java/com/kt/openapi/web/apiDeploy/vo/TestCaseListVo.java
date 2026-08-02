package com.kt.openapi.web.apiDeploy.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;

public class TestCaseListVo extends DefaultVo {

	private int seq;
	private int apiNo;
    private String caseGb; 
    private String delYN;
    private String caseCd;
    private String regr;
	private String caseNm;
	
	
    private String cdNm;
    private int verifiSuccess;
    private int verifiFail;
    private String lastSuccessYn;
    
    //API LIST VO
    
	
	
	public int getApiNo() {
		return apiNo;
	}
	public void setApiNo(int apiNo) {
		this.apiNo = apiNo;
	}
	public String getCaseGb() {
		return caseGb;
	}
	public void setCaseGb(String caseGb) {
		this.caseGb = caseGb;
	}
	public String getDelYN() {
		return delYN;
	}
	public void setDelYN(String delYN) {
		this.delYN = delYN;
	}
	public String getCaseCd() {
		return caseCd;
	}
	public void setCaseCd(String caseCd) {
		this.caseCd = caseCd;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getRegr() {
		return regr;
	}
	public void setRegr(String regr) {
		this.regr = regr;
	}
	public String getCaseNm() {
		return caseNm;
	}
	public void setCaseNm(String caseNm) {
		this.caseNm = caseNm;
	}
	public String getCdNm() {
		return cdNm;
	}
	public void setCdNm(String cdNm) {
		this.cdNm = cdNm;
	}
	public int getVerifiSuccess() {
		return verifiSuccess;
	}
	public void setVerifiSuccess(int verifiSuccess) {
		this.verifiSuccess = verifiSuccess;
	}
	public int getVerifiFail() {
		return verifiFail;
	}
	public void setVerifiFail(int verifiFail) {
		this.verifiFail = verifiFail;
	}
	public String getLastSuccessYn() {
		return lastSuccessYn;
	}
	public void setLastSuccessYn(String lastSuccessYn) {
		this.lastSuccessYn = lastSuccessYn;
	}
}
