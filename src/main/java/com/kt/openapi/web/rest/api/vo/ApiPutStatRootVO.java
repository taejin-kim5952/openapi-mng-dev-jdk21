package com.kt.openapi.web.rest.api.vo;

import com.kt.openapi.web.rest.common.vo.StatHeaderVO;

public class ApiPutStatRootVO {
	private StatHeaderVO header;
	private ApiPutStatBodyVO body;	
	
	public StatHeaderVO getHeader() {
		return header;
	}
	public void setHeader(StatHeaderVO header) {
		this.header = header;
	}
	
	public ApiPutStatBodyVO getBody() {
		return body;
	}
	public void setBody(ApiPutStatBodyVO body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "ApiPutStatRootVO [header=" + header + ", body=" + body + "]";
	}
}
