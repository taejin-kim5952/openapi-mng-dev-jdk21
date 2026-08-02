package com.kt.openapi.web.apiDeploy.vo;

import java.util.Date;
import java.util.HashMap;

public class ApiDeployInsertVo {

	
   
	           

	private String apiNo;
    private String deployCd;
    private String verifiCd;
    private String regr;
    private int seq;
    
    private String updateUsr;
    private String afterDeployCd;
    
    private String processGubun;
    
    private String apiNm;
    private int deployApplySeq;
    private Date deployStDt;
    private String deployAdm;
    private String useYn;
    private String bigo;
    
    private String tbProSuccessYn;
    
    private HashMap deployApiList;

    
	public String getBigo() {
		return bigo;
	}
	public void setBigo(String bigo) {
		this.bigo = bigo;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	public String getDeployAdm() {
		return deployAdm;
	}
	public void setDeployAdm(String deployAdm) {
		this.deployAdm = deployAdm;
	}
	public Date getDeployStDt() {
		return deployStDt;
	}
	public void setDeployStDt(Date deployStDt) {
		this.deployStDt = deployStDt;
	}
	public int getDeployApplySeq() {
		return deployApplySeq;
	}
	public void setDeployApplySeq(int deployApplySeq) {
		this.deployApplySeq = deployApplySeq;
	}
	public String getApiNm() {
		return apiNm;
	}
	public void setApiNm(String apiNm) {
		this.apiNm = apiNm;
	}
	public String getUpdateUsr() {
		return updateUsr;
	}
	public void setUpdateUsr(String updateUsr) {
		this.updateUsr = updateUsr;
	}
	public String getAfterDeployCd() {
		return afterDeployCd;
	}
	public void setAfterDeployCd(String afterDeployCd) {
		this.afterDeployCd = afterDeployCd;
	}
	public String getProcessGubun() {
		return processGubun;
	}
	public void setProcessGubun(String processGubun) {
		this.processGubun = processGubun;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getApiNo() {
		return apiNo;
	}
	public void setApiNo(String apiNo) {
		this.apiNo = apiNo;
	}
	public String getDeployCd() {
		return deployCd;
	}
	public void setDeployCd(String deployCd) {
		this.deployCd = deployCd;
	}
	public String getVerifiCd() {
		return verifiCd;
	}
	public void setVerifiCd(String verifiCd) {
		this.verifiCd = verifiCd;
	}
	public String getRegr() {
		return regr;
	}
	public void setRegr(String regr) {
		this.regr = regr;
	}
	public String getTbProSuccessYn() {
		return tbProSuccessYn;
	}
	public void setTbProSuccessYn(String tbProSuccessYn) {
		this.tbProSuccessYn = tbProSuccessYn;
	}
    
}
