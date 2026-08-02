package com.kt.openapi.web.apiDeploy.vo;

import java.util.Date;

import com.kt.openapi.web.cmm.vo.DefaultVo;

public class DeployHstVo  extends DefaultVo {
	

	private int seq;
	private String sysNm;
	private String serviceNm;
	private String apiNm;
	private String regDt;
	private String deployDate;
	private String deployAdm;
	private String successYn;
	private String resultMsg;
	private String resultCd;
	private int deployApplySeq;
	private int deployProcSeq;
	private String deployGb;
	//-- [tag:PRJ-20220901]
	private int apiNo;
	private int logSeq;
	private String dep_seq;
	
	private String searchSystem;
	private Date stDate;
	private Date endDate;
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	public String getSysNm() {
		return sysNm;
	}
	public void setSysNm(String sysNm) {
		this.sysNm = sysNm;
	}
	public String getServiceNm() {
		return serviceNm;
	}
	public void setServiceNm(String serviceNm) {
		this.serviceNm = serviceNm;
	}
	public String getApiNm() {
		return apiNm;
	}
	public void setApiNm(String apiNm) {
		this.apiNm = apiNm;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getDeployDate() {
		return deployDate;
	}
	public void setDeployDate(String deployDate) {
		this.deployDate = deployDate;
	}
	public String getSuccessYn() {
		return successYn;
	}
	public void setSuccessYn(String successYn) {
		this.successYn = successYn;
	}
	public String getDeployAdm() {
		return deployAdm;
	}
	public void setDeployAdm(String deployAdm) {
		this.deployAdm = deployAdm;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getResultCd() {
		return resultCd;
	}
	public void setResultCd(String resultCd) {
		this.resultCd = resultCd;
	}
	public int getDeployApplySeq() {
		return deployApplySeq;
	}
	public void setDeployApplySeq(int deployApplySeq) {
		this.deployApplySeq = deployApplySeq;
	}
	public String getDeployGb() {
		return deployGb;
	}
	public void setDeployGb(String deployGb) {
		this.deployGb = deployGb;
	}
	public int getDeployProcSeq() {
		return deployProcSeq;
	}
	public void setDeployProcSeq(int deployProcSeq) {
		this.deployProcSeq = deployProcSeq;
	}
	public String getSearchSystem() {
		return searchSystem;
	}
	public void setSearchSystem(String searchSystem) {
		this.searchSystem = searchSystem;
	}
	public Date getStDate() {
		return stDate;
	}
	public void setStDate(Date stDate) {
		this.stDate = stDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	//-- [tag:PRJ-20220901] {
	public int getApiNo() { return apiNo; }
	public void setApiNo(int apiNo) { this.apiNo = apiNo; }
	public int getLogSeq() { return logSeq; }
	public void setLogSeq(int logSeq) { this.logSeq = logSeq; }
	//-- [tag:PRJ-20220901] }
	public String getDep_seq() {
		return dep_seq;
	}
	public void setDep_seq(String dep_seq) {
		this.dep_seq = dep_seq;
	}
}
