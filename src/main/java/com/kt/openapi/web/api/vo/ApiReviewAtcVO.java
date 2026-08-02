package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 검토요청 첨부파일 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiReviewAtcVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String atcFileNo;
    private String apiReviewRqtNo;
    private String saveFileNm;
    private String originFileNm;
    private String fileTypeCd;
    private String filePath;
    private String fileSize;
    private String useYn;
    private Integer downlCnt;
    private String delYn;
    private Date regDt;
    private String regr;
    private Date amdDt;
    private String amdDtStr;
    private String amdr;
}
