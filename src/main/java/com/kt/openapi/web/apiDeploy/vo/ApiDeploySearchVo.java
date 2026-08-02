package com.kt.openapi.web.apiDeploy.vo;

import java.util.Date;
import java.util.List;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.vo.DefaultVo;

public class ApiDeploySearchVo  extends DefaultVo {
	private String searchKeyword;
	private int apiNo;
	private String  deployCd;
	
	private String sysNm;
	private String ctgryNm;
	private String apiNm;

	private String deployCdnm;
	private String verifiCdnm;
	
	private String regUserNm;
	private String regr;
	private Date regDt;
	private int seq;
	
	private String apiPath;
	private String vericdNm;
	
	private String systemId;
	private String sysId;

	

	private String searchCondition;
	private String searchVerifi;
	


	private String searchDeploy;
	private String searchSystem;
	private String searchSpc;
	private Date stDate;
	private Date endDate;
	private String bigo;
	
	private String apiSpcNo;
	private String applyMsg;
	
	private int deployApplySeq;
	
	private String verifiCd;
	private String cbProSuccessYn;
	private String searchCategory;
	
	private int tbCnt;
	private int cbCnt;
	
	private int tbSuccessCnt ;
	private int verifiCnt ;
	
	private String handlerNm;
	private String endpntTbUrl;

	//신청 내역 검색시에는 신청 테이블에서 조회
	private String searchApplyCd;
	
	//권한 리스트(sysId)
	private List authSysIdList;
	
	/*
     * API Link(Studio) Gateway Writer 권한 설정
     *   Y: Being Writer
     *   N: Not Writer
     * CYD - 2020.07.08
     */
    private String writerYn;
    
    /*
     * API Link(Studio) Gateway Observer 권한 설정
     *   Y: Being Observer(=Read)
     *   N: Not Observer
     * CYD - 2020.07.08
     */
    private String observerYn;
	
	public String getApplyMsg() {
		return applyMsg;
	}


	public void setApplyMsg(String applyMsg) {
		this.applyMsg = applyMsg;
	}


	public String getApiSpcNo() {
		return apiSpcNo;
	}


	public void setApiSpcNo(String apiSpcNo) {
		this.apiSpcNo = apiSpcNo;
	}


	public String getSysId() {
		return sysId;
	}


	public void setSysId(String sysId) {
		this.sysId = sysId;
	}
	
	
	public String getBigo() {
		return bigo;
	}

	public void setBigo(String bigo) {
		this.bigo = bigo;
	}

	public String getSearchCondition() {
		return searchCondition;
	}


	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}


	public String getSearchVerifi() {
		return searchVerifi;
	}



	public void setSearchVerifi(String searchVerifi) {
		this.searchVerifi = searchVerifi;
	}



	public String getSearchDeploy() {
		return searchDeploy;
	}



	public void setSearchDeploy(String searchDeploy) {
		this.searchDeploy = searchDeploy;
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



	public String getVericdNm() {
		return vericdNm;
	}



	public void setVericdNm(String vericdNm) {
		this.vericdNm = vericdNm;
	}



	public String getSystemId() {
		return systemId;
	}



	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	

	public String getRegr() {
		return regr;
	}



	public void setRegr(String regr) {
		this.regr = regr;
	}



	public Date getRegDt() {
		return regDt;
	}



	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}



	public String getApiPath() {
		return apiPath;
	}



	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}



	public void setRegUserNm(String regUserNm) {
		this.regUserNm = regUserNm;
	}



	public int getSeq() {
		return seq;
	}



	public void setSeq(int seq) {
		this.seq = seq;
	}



	public String getRegUserNm() {
		
		return regUserNm;
	}
	
	
	
	public String getDeployCdnm() {
		return deployCdnm;
	}
	
	public void setDeployCdnm(String deployCdnm) {
		this.deployCdnm = deployCdnm;
	}
	
	public String getVerifiCdnm() {
		return verifiCdnm;
	}
	public void setVerifiCdnm(String verifiCdnm) {
		this.verifiCdnm = verifiCdnm;
	}
	public String getSysNm() {
		return sysNm;
	}
	public void setSysNm(String sysNm) {
		this.sysNm = sysNm;
	}
	public String getCtgryNm() {
		return ctgryNm;
	}
	public void setCtgryNm(String ctgryNm) {
		this.ctgryNm = ctgryNm;
	}
	public String getApiNm() {
		return apiNm;
	}
	public void setApiNm(String apiNm) {
		this.apiNm = apiNm;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
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


	public int getDeployApplySeq() {
		return deployApplySeq;
	}


	public void setDeployApplySeq(int deployApplySeq) {
		this.deployApplySeq = deployApplySeq;
	}


	public String getVerifiCd() {
		return verifiCd;
	}


	public void setVerifiCd(String verifiCd) {
		this.verifiCd = verifiCd;
	}


	public String getCbProSuccessYn() {
		return cbProSuccessYn;
	}


	public void setCbProSuccessYn(String cbProSuccessYn) {
		this.cbProSuccessYn = cbProSuccessYn;
	}


	public String getSearchCategory() {
		return searchCategory;
	}


	public void setSearchCategory(String searchCategory) {
		this.searchCategory = searchCategory;
	}


	


	public int getTbSuccessCnt() {
		return tbSuccessCnt;
	}


	public void setTbSuccessCnt(int tbSuccessCnt) {
		this.tbSuccessCnt = tbSuccessCnt;
	}


	public int getVerifiCnt() {
		return verifiCnt;
	}


	public void setVerifiCnt(int verifiCnt) {
		this.verifiCnt = verifiCnt;
	}


	public void setTbCnt(int tbCnt) {
		this.tbCnt = tbCnt;
	}


	public void setCbCnt(int cbCnt) {
		this.cbCnt = cbCnt;
	}


	public int getTbCnt() {
		return tbCnt;
	}


	public int getCbCnt() {
		return cbCnt;
	}


	public String getHandlerNm() {
		return handlerNm;
	}


	public void setHandlerNm(String handlerNm) {
		this.handlerNm = handlerNm;
	}


	public String getEndpntTbUrl() {
		return endpntTbUrl;
	}


	public void setEndpntTbUrl(String endpntTbUrl) {
		this.endpntTbUrl = endpntTbUrl;
	}


	public String getSearchSpc() {
		return searchSpc;
	}


	public void setSearchSpc(String searchSpc) {
		this.searchSpc = searchSpc;
	}


	public String getSearchApplyCd() {
		return searchApplyCd;
	}


	public void setSearchApplyCd(String searchApplyCd) {
		this.searchApplyCd = searchApplyCd;
	}


	public List<String> getAuthSysIdList() {
		return authSysIdList;
	}


	public void setAuthSysIdList(List authSysIdList) {
		this.authSysIdList = authSysIdList;
	}


	/**
	 * @return the writerYn
	 */
	public String getWriterYn() {
		return writerYn;
	}


	/**
	 * @param writerYn the writerYn to set
	 */
	public void setWriterYn(String writerYn) {
		this.writerYn = writerYn;
	}


	/**
	 * @return the observerYn
	 */
	public String getObserverYn() {
		return observerYn;
	}


	/**
	 * @param observerYn the observerYn to set
	 */
	public void setObserverYn(String observerYn) {
		this.observerYn = observerYn;
	}





	
}

