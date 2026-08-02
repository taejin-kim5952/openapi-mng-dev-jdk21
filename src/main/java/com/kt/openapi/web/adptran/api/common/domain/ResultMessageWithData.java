package com.kt.openapi.web.adptran.api.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultMessageWithData extends ResultMessage{

	private Object data = null;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ResultMessageWithData() {
		super();
	}

	public ResultMessageWithData(String resultMsg) {
		super(resultMsg);
	}

	/*--@@
	public ResultMessageWithData(Boolean resultYn, String resultMsg) {
		super(resultYn,resultMsg);
	}

	public ResultMessageWithData(Integer resultCd, Boolean resultYn) {
		super(resultCd, resultYn);
	}

	public ResultMessageWithData(Integer resultCd, Boolean resultYn, String resultMsg) {
		super(resultCd, resultYn, resultMsg);
	}
	--*/

	public ResultMessageWithData(Integer resultCd, String resultMsg) {
		super(resultCd, resultMsg);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);

	}
}
