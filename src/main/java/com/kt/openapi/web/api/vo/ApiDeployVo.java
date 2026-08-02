package com.kt.openapi.web.api.vo;

public class ApiDeployVo {
	
	
	/**  seq 자동증가  */
	private int seq;
	
	/**  api번호 KOA_TB_API_DEF 의 api_no  */
	private int apiNo;
	
	/** DEPLOY_CD  공통 코드 DEPLOY1000 참조  */
	private String deployCd;
	
	/** REGR 등록자 id를 암호화 하여 저장 */
	private String regr;
	
	/** 배포를 진행하는 운영자 아이디 */
	private String  deployAdm;

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getApiNo() {
		return apiNo;
	}

	public void setApiNo(int apiNo) {
		this.apiNo = apiNo;
	}

	public String getDeployCd() {
		return deployCd;
	}

	public void setDeployCd(String deployCd) {
		this.deployCd = deployCd;
	}

	public String getRegr() {
		return regr;
	}

	public void setRegr(String regr) {
		this.regr = regr;
	}

	public String getDeployAdm() {
		return deployAdm;
	}

	public void setDeployAdm(String deployAdm) {
		this.deployAdm = deployAdm;
	}

}
