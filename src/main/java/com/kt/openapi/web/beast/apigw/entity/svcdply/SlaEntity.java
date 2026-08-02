package com.kt.openapi.web.beast.apigw.entity.svcdply;

import java.io.Serial;
import java.io.Serializable;

public class SlaEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -8099970071646129211L;

	//-- [i]SLA
	//-- payload {
    private Integer sec; //-- 초
    private Integer min; //-- 분
    private Integer hr; //-- 시
    private Integer day; //-- 일
    private Integer mon; //-- 월
	//-- payload }
	
	public Integer getSec() { return sec; }
	public void setSec(Integer sec) { this.sec = sec; }
	public Integer getMin() { return min; }
	public void setMin(Integer min) { this.min = min; }
	public Integer getHr() { return hr; }
	public void setHr(Integer hr) { this.hr = hr; }
	public Integer getDay() { return day; }
	public void setDay(Integer day) { this.day = day; }
	public Integer getMon() { return mon; }
	public void setMon(Integer mon) { this.mon = mon; }
}
