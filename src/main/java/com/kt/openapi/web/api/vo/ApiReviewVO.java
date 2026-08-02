package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 검토요청 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rownum;
    private String apiReviewRqtNo;
    private String apiSpcNo;
    private String reviewRqtTitle;
    private String reviewRqtTypeCd;
    private String reviewRqtTypeCdNm;
    private String reviewSysId;
    private String reviewRqtSbst;
    private Date amdDt;
    private String amdDtStr;
    private String amdr;
    private String amdrNm;
    private String regr;
    private String regrNm;
    private Integer replyCnt;
    private String apiSpcId;
    private String apiNm;
    private String ver;
    private String sysIdNm;
    
    // For single view
    private String delYn;
    private Date regDt;
    private String regDtStr;
    private String cmpnNm;
}
