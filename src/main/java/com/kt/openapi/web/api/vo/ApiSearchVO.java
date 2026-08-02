package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 검색 결과 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiSearchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rownum;
    private String apiSpcNo;
    private String apiSpcId;
    private String ver;
    private String apiNm;
    private String apiSpcDesc;
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
    
    private String titleNm;
    private String apiNo;
    private String apiId;
    private String apiPath;
    private String apiDesc;
    private Integer sortOdrg;
    private String method;

    // selMainList 추가 필드
    private String amdrNm;
    private String amdDtStr;
    private String regSttusCdNm;
    private String sysIdNm;
    private String apiSpcNm;
    private String apiDefDesc;
    private String methodCd;
    private String methodNm;
    private String showStatusCd;
    private String fApiNm;
    private String fApiDesc;
}
