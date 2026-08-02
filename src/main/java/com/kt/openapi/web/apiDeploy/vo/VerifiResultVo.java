package com.kt.openapi.web.apiDeploy.vo;

public class VerifiResultVo {
	
	private int seq;
	private int deployProcSeq;
	private String testCaseSeq;
	private String verificationDt;  
	private String resultCd;
	private String resultMsg;
	private String verifiUsr;
	private String successYn;
	private String stTime;
	private String endTime;

	//-- Proc result
	private String procResultCd;
	private String procResultMsg;
	
	//-- CpApiRequest
	private String reqGwProfile;
	//-- [tag:PRJ-20220901]
	private String reqApiVeriBaseurl;
	private String reqApiUrl;
	private String reqHeaders;
	private String reqBody;
	private String reqTransactionId;
	private String reqSequenceNo;
	
	//-- CpApiResponse
	private String resTransactionId;
	private String resSequenceNo;
	private String resReturnCode;
	private String resReturnDescription;
	private String resErrorCode;
	private String resErrorDescription;
	private String resResponse;
	
	//-- api_def
	private int apiNo;
	private String apiNm;

	//-- api_testcase
	private String testcaseNm;
	private String paramGub;
	private String paramHeader;
	private String paramBody;
	private String paramQuery;
	private String paramHeaderJson;
	private String paramBodyJson;
	private String assertCase;
	private String assertField;
	private String assertOperator;
	private String assertValue;
	private String assertResult;

	//-- getter/setter {
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
	public String getTestCaseSeq() {
		return testCaseSeq;
	}
	public void setTestCaseSeq(String testCaseSeq) {
		this.testCaseSeq = testCaseSeq;
	}
	public String getVerificationDt() {
		return verificationDt;
	}
	public void setVerificationDt(String verificationDt) {
		this.verificationDt = verificationDt;
	}
	public String getResultCd() {
		return resultCd;
	}
	public void setResultCd(String resultCd) {
		this.resultCd = resultCd;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getVerifiUsr() {
		return verifiUsr;
	}
	public void setVerifiUsr(String verifiUsr) {
		this.verifiUsr = verifiUsr;
	}
	public String getSuccessYn() {
		return successYn;
	}
	public void setSuccessYn(String successYn) {
		this.successYn = successYn;
	}
	public String getStTime() {
		return stTime;
	}
	public void setStTime(String stTime) {
		this.stTime = stTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getProcResultCd() {
		return procResultCd;
	}
	public void setProcResultCd(String procResultCd) {
		this.procResultCd = procResultCd;
	}
	public String getProcResultMsg() {
		return procResultMsg;
	}
	public void setProcResultMsg(String procResultMsg) {
		this.procResultMsg = procResultMsg;
	}
	public String getReqGwProfile() {
		return reqGwProfile;
	}
	public void setReqGwProfile(String reqGwProfile) {
		this.reqGwProfile = reqGwProfile;
	}
	public String getReqApiVeriBaseurl() {
		return reqApiVeriBaseurl;
	}
	public void setReqApiVeriBaseurl(String reqApiVeriBaseurl) {
		this.reqApiVeriBaseurl = reqApiVeriBaseurl;
	}
	public String getReqApiUrl() {
		return reqApiUrl;
	}
	public void setReqApiUrl(String reqApiUrl) {
		this.reqApiUrl = reqApiUrl;
	}
	public String getReqHeaders() {
		return reqHeaders;
	}
	public void setReqHeaders(String reqHeaders) {
		this.reqHeaders = reqHeaders;
	}
	public String getReqBody() {
		return reqBody;
	}
	public void setReqBody(String reqBody) {
		this.reqBody = reqBody;
	}
	public String getReqTransactionId() {
		return reqTransactionId;
	}
	public void setReqTransactionId(String reqTransactionId) {
		this.reqTransactionId = reqTransactionId;
	}
	public String getReqSequenceNo() {
		return reqSequenceNo;
	}
	public void setReqSequenceNo(String reqSequenceNo) {
		this.reqSequenceNo = reqSequenceNo;
	}
	public String getResTransactionId() {
		return resTransactionId;
	}
	public void setResTransactionId(String resTransactionId) {
		this.resTransactionId = resTransactionId;
	}
	public String getResSequenceNo() {
		return resSequenceNo;
	}
	public void setResSequenceNo(String resSequenceNo) {
		this.resSequenceNo = resSequenceNo;
	}
	public String getResReturnCode() {
		return resReturnCode;
	}
	public void setResReturnCode(String resReturnCode) {
		this.resReturnCode = resReturnCode;
	}
	public String getResReturnDescription() {
		return resReturnDescription;
	}
	public void setResReturnDescription(String resReturnDescription) {
		this.resReturnDescription = resReturnDescription;
	}
	public String getResErrorCode() {
		return resErrorCode;
	}
	public void setResErrorCode(String resErrorCode) {
		this.resErrorCode = resErrorCode;
	}
	public String getResErrorDescription() {
		return resErrorDescription;
	}
	public void setResErrorDescription(String resErrorDescription) {
		this.resErrorDescription = resErrorDescription;
	}
	public String getResResponse() {
		return resResponse;
	}
	public void setResResponse(String resResponse) {
		this.resResponse = resResponse;
	}
	public int getApiNo() {
		return apiNo;
	}
	public void setApiNo(int apiNo) {
		this.apiNo = apiNo;
	}
	public String getApiNm() {
		return apiNm;
	}
	public void setApiNm(String apiNm) {
		this.apiNm = apiNm;
	}
	public String getTestcaseNm() {
		return testcaseNm;
	}
	public void setTestcaseNm(String testcaseNm) {
		this.testcaseNm = testcaseNm;
	}
	public String getParamGub() {
		return paramGub;
	}
	public void setParamGub(String paramGub) {
		this.paramGub = paramGub;
	}
	public String getParamHeader() {
		return paramHeader;
	}
	public void setParamHeader(String paramHeader) {
		this.paramHeader = paramHeader;
	}
	public String getParamBody() {
		return paramBody;
	}
	public void setParamBody(String paramBody) {
		this.paramBody = paramBody;
	}
	public String getParamQuery() {
		return paramQuery;
	}
	public void setParamQuery(String paramQuery) {
		this.paramQuery = paramQuery;
	}
	public String getParamHeaderJson() {
		return paramHeaderJson;
	}
	public void setParamHeaderJson(String paramHeaderJson) {
		this.paramHeaderJson = paramHeaderJson;
	}
	public String getParamBodyJson() {
		return paramBodyJson;
	}
	public void setParamBodyJson(String paramBodyJson) {
		this.paramBodyJson = paramBodyJson;
	}
	public String getAssertCase() {
		return assertCase;
	}
	public void setAssertCase(String assertCase) {
		this.assertCase = assertCase;
	}
	public String getAssertField() {
		return assertField;
	}
	public void setAssertField(String assertField) {
		this.assertField = assertField;
	}
	public String getAssertOperator() {
		return assertOperator;
	}
	public void setAssertOperator(String assertOperator) {
		this.assertOperator = assertOperator;
	}
	public String getAssertValue() {
		return assertValue;
	}
	public void setAssertValue(String assertValue) {
		this.assertValue = assertValue;
	}
	public String getAssertResult() {
		return assertResult;
	}
	public void setAssertResult(String assertResult) {
		this.assertResult = assertResult;
	}
	//-- getter/setter }
}
