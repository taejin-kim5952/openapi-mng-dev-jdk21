package com.kt.openapi.web.cmm.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class CmnCdVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String comnCd;
    private String groupCd;
    private String cdNm;
    private String cdSbst;
    private String sortOdrg;
    private String useYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
}
