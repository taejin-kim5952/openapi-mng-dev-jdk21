package com.kt.openapi.web.bbs.cmn.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BbsAtcVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String atcFileNo;
    private String pstingId;
    private String fileTypeCd;
    private String filePath;
    private String fileSize;
    private String originFileNm;
    private String saveFileNm;
    private String useYn;
    private String downlCnt;
    private String showOdrg;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
}
