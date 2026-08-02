package com.kt.openapi.web.devsupport.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevSupportVo extends DefaultVo {
	
	private Integer rownum;
	private String pstingId;
    private String title;
    private String sbst;
    private String bbsTypeCd;
    private String imptYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    private String showYn;
    private String serviceInfra_NM;
    private int service_BO;
    private int service_DO;
    private String myServiceInfra_NM;
    private String status;
    private String myServiceInfraNm;
    private String devSupportGb;
    private String devSupportGba;
    private String supportgb;
    private String regrn;
    private String servinfran;
    private String busern;
    private String dusern;
    private String adci;
    private String servc;
    private String comment;
    private String adcId;
    private String serviceCode;
    private String SERVBO;
    private String SERVDO;
    private String servbo;
    private String servdo;
    private String aaaa;

	// servbo getter: servbo가 null이면 SERVBO로 fallback
	public String getServbo() {
		return servbo != null ? servbo : (SERVBO != null ? SERVBO : null);
	}

	// servdo getter: servdo가 null이면 SERVDO로 fallback
	public String getServdo() {
		return servdo != null ? servdo : (SERVDO != null ? SERVDO : null);
	}
}
