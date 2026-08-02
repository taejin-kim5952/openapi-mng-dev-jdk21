package com.kt.openapi.web.rest.api.vo;

import com.kt.openapi.web.rest.common.vo.StatHeaderVO;

public class ApiGetStatRootVO {
	private StatHeaderVO header;
	private ApiGetStatBodyVO body;	
	
	public StatHeaderVO getHeader() {
		return header;
	}
	public void setHeader(StatHeaderVO header) {
		this.header = header;
	}
	
	public ApiGetStatBodyVO getBody() {
		return body;
	}
	public void setBody(ApiGetStatBodyVO body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "ApiGetStatRootVO [header=" + header + ", body=" + body + "]";
	}
}
