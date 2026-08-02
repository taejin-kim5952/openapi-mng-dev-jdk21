package com.kt.openapi.web.apiDeploy.vo;

import java.util.List;

public class DeployRejectVo {

	private int procseq;
	private int deployapplyseq;
	
	private String rejectMsg;
	
	private List<DeployRejectVo> deployApiList;

	
	
	
	
	public int getProcseq() {
		return procseq;
	}

	public void setProcseq(int procseq) {
		this.procseq = procseq;
	}

	public int getDeployapplyseq() {
		return deployapplyseq;
	}

	public void setDeployapplyseq(int deployapplyseq) {
		this.deployapplyseq = deployapplyseq;
	}

	public List<DeployRejectVo> getDeployApiList() {
		return deployApiList;
	}

	public void setDeployApiList(List<DeployRejectVo> deployApiList) {
		this.deployApiList = deployApiList;
	}

	public String getRejectMsg() {
		return rejectMsg;
	}

	public void setRejectMsg(String rejectMsg) {
		this.rejectMsg = rejectMsg;
	}
}
