package com.kt.openapi.web.adptran.api.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultMessage {

	private Integer resultCd = null;
	//--@@private Boolean resultYn = null;
	private String resultMsg = null;
	
	public ResultMessage() {
		super();
	}
	
	public ResultMessage(String resultMsg) {
		super();
		this.resultMsg = resultMsg;
	}

	public ResultMessage(Integer resultCd, String resultMsg) {
		super();
		this.resultCd = resultCd;
		this.resultMsg = resultMsg;
	}

	/*--@@
	public ResultMessage(Boolean resultYn, String resultMsg) {
		super();
		this.resultYn = resultYn;
		this.resultMsg = resultMsg;
	}

	public ResultMessage(Integer resultCd, Boolean resultYn) {
		super();
		this.resultCd = resultCd;
		this.resultYn = resultYn;
	}
	--*/

	public ResultMessage(Integer resultCd, Boolean resultYn, String resultMsg) {
		super();
		this.resultCd = resultCd;
		//--@@this.resultYn = resultYn;
		this.resultMsg = resultMsg;
	}

	public Integer getResultCd() {
		return resultCd;
	}
	public void setResultCd(Integer resultCd) {
		this.resultCd = resultCd;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	/*--@@
	public Boolean isResultYn() {
		return resultYn;
	}

	public void setResultYn(Boolean resultYn) {
		this.resultYn = resultYn;
	}
	--*/

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);

	}
}
