package com.kt.openapi.web.devsupport.vo;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevSupportSaveVo {
	
	private String pstingId;

	@NotBlank(message="{devSupportSaveVo.title}")
    private String title;

	@NotBlank(message="{devSupportSaveVo.sbst}")
    private String sbst;

    private String bbsTypeCd;
    private String imptYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    private String showYn;
    private String devSupport_GB;

	@NotBlank(message="{devSupportSaveVo.serviceInfra_NM}")
    private String serviceInfra_NM;

    private int service_BO;
    private int service_DO;
    private String myServiceInfraNm;
    private String status;
    private String serviceCode;

	@Digits(integer=10, fraction=0, message="ADC 관리번호는 숫자만 입력 가능합니다.")
    private String adcId;
    private String regrN;
    private String delAtcFileNo;
}
