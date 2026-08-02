package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 기본 정보를 담는 VO (SPC + DEF)
 */
@Getter
@Setter
@ToString
public class ApiDefaultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiSpcNo;
    private String apiSpcId;
    private String ver;
    private String apiNm;
    private String apiDesc;
    private String autId;
    private String sysId;
    private String sysNm;
    private String verDesc;
    private String rfrnWsdlUrl;
    private String rfrnTmpltNo;
    private String rfrnApiSpcNo;
    private String host;
    private String basPath;
    private String yamlFilePath;
    private String yamlFileNm;
    private String regSttusCd;
    private String tmpltYn;
    private String regApvr;
    private Date regDt;
    private String regr;
    private Date amdDt;
    private String amdr;
    private String delYn;
    private Date delDt;
    private String delr;
    private String yamlSbst;
    private String showYn;
    private String apiClass;
    
    private String titleNm;
    private String apiNo;
    private String apiId;
    private String apiPath;
    private String method;
    private String apiGubun;
    private String apiGubunNm;
    private String apiHandlerCd;
    private String apiHandlerCdNm;
    private String apiVer;
    private String apiVerNo;
}
