package com.kt.openapi.web.apiDeploy.vo;

import java.util.Date;

public class DeployApplyVo {


    
    private int deployProcSeq;
	private String applyRegr;
	private Date regDt;
	private String deployCd;
	private String bigo;
	private int deployApplySeq;
	private int seq;
	private String sysNm;
	private String serviceNm;
	private String deployDesc;
	private String deployAdm;
    private String comment;
    private String cbProSuccessYn;
    private int apiNo;

	
	
	
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDeployAdm() {
		return deployAdm;
	}
	public void setDeployAdm(String deployAdm) {
		this.deployAdm = deployAdm;
	}
	public String getDeployDesc() {
		return deployDesc;
	}
	public void setDeployDesc(String deployDesc) {
		this.deployDesc = deployDesc;
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
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	public int getDeployProcSeq() {
		return deployProcSeq;
	}
	public void setDeployProcSeq(int deployProcSeq) {
		this.deployProcSeq = deployProcSeq;
	}
	public String getApplyRegr() {
		return applyRegr;
	}
	public void setApplyRegr(String applyRegr) {
		this.applyRegr = applyRegr;
	}
	public Date getRegDt() {
		return regDt;
	}
	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}
	public String getDeployCd() {
		return deployCd;
	}
	public void setDeployCd(String deployCd) {
		this.deployCd = deployCd;
	}
	public String getBigo() {
		return bigo;
	}
	public void setBigo(String bigo) {
		this.bigo = bigo;
	}
	public int getDeployApplySeq() {
		return deployApplySeq;
	}
	public void setDeployApplySeq(int deployApplySeq) {
		this.deployApplySeq = deployApplySeq;
	}
	public String getCbProSuccessYn() {
		return cbProSuccessYn;
	}
	public void setCbProSuccessYn(String cbProSuccessYn) {
		this.cbProSuccessYn = cbProSuccessYn;
	}
	public int getApiNo() {
		return apiNo;
	}
	public void setApiNo(int apiNo) {
		this.apiNo = apiNo;
	}
	
	@Override
	public String toString() {
		return "DeployApplyVo [deployProcSeq=" + deployProcSeq + ", applyRegr=" + applyRegr + ", regDt=" + regDt
				+ ", deployCd=" + deployCd + ", bigo=" + bigo + ", deployApplySeq=" + deployApplySeq + ", seq=" + seq
				+ ", sysNm=" + sysNm + ", serviceNm=" + serviceNm + ", deployDesc=" + deployDesc + ", deployAdm="
				+ deployAdm + ", comment=" + comment + ", cbProSuccessYn=" + cbProSuccessYn + ", apiNo=" + apiNo + "]";
	}
	
}
