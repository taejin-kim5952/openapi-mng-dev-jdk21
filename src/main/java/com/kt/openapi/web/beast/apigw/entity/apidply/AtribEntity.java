package com.kt.openapi.web.beast.apigw.entity.apidply;

import java.io.Serial;
import java.io.Serializable;

public class AtribEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -8099970071646129211L;

	//-- [i][속성]
	//-- payload {
    private String inFmt; //-- 요청 포맷
    private String outFmt; //-- 응답 포맷
    private String inComnParam; //-- 요청 공통 파라미터
    private String outComnParam; //-- 응답 공통 파라미터
	//-- payload }

    public String getInFmt() { return inFmt; }
    public void setInFmt(String inFmt) { this.inFmt = inFmt; }
    public String getOutFmt() { return outFmt; }
    public void setOutFmt(String outFmt) { this.outFmt = outFmt; }
    public String getInComnParam() { return inComnParam; }
    public void setInComnParam(String inComnParam) { this.inComnParam = inComnParam; }
    public String getOutComnParam() { return outComnParam; }
    public void setOutComnParam(String outComnParam) { this.outComnParam = outComnParam; }
}
