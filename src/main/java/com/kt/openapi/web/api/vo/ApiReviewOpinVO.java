package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 검토의견(답변) 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiReviewOpinVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiReviewAnsNo;
    private String apiReviewRqtNo;
    private String reviewOpin;
    private String delYn;
    private Date regDt;
    private String regDtStr;
    private String regr;
    private String regrNm;
    private Date amdDt;
    private String amdDtStr;
    private String amdr;
    private String amdrNm;
}
