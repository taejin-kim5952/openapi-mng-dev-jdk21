package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 명세(SPC) 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiSpcVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rownum;
    private String apiSpcNo;
    private String apiSpcId;
    private String ver;
    private String verDesc;
    private String apiNm;
    private String apiDesc;
    private String amdr;
    private String amdrNm;
    private Date amdDt;
    private String amdDtStr;
    private String regSttusCd;
    private String regSttusCdNm;
    private String sysId;
    private String sysIdNm;
    private String apiClass;
    private Integer completeCnt;
    
    // Additional fields for sub version list
    private String regrNm;
    
    // For YAML info
    private String yamlFilePath;
    private String yamlFileNm;
    
    // For full record
    private String projectNamespace;
    
    // Missing fields (build error fix)
    private String yamlSbst;
    private String apiCtgryNo;
    private String apiNo;
    private String prntsParamNo;
    private String paramNo;
}
