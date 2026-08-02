package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 카테고리 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sysId;
    private String apiCtgryNo;
    private String apiSpcNo;
    private String ctgryNm;
    private Integer sortOdrg;
    private String ctgryDesc;
    private String delYn;
    private Date regDt;
    private String regr;
    private Date amdDt;
    private String amdr;
}
