package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API 불러오기 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiImportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rownum;
    private String apiSpcNo;
    private String apiSpcId;
    private String sysId;
    private String sysIdNm;
    private String amdr;
    private String amdrNm;
    private String ver;
    private String apiNm;
    private String rfrnTmpltNo;
    private String apiDesc;
    
    // Missing fields (build error fix)
    private java.util.Date amdDt;
    private String regSttusCd;
}
