package com.kt.openapi.web.rest.auth.vo;

import com.kt.openapi.web.rest.common.vo.StatHeaderVO;

public class StatRootVO {
	private StatHeaderVO header;
	private StatBodyVO body;	
	
	public StatHeaderVO getHeader() {
		return header;
	}
	public void setHeader(StatHeaderVO header) {
		this.header = header;
	}
	public StatBodyVO getBody() {
		return body;
	}
	public void setBody(StatBodyVO body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "StatRootDomain [header=" + header + ", body=" + body + "]";
	}
}
