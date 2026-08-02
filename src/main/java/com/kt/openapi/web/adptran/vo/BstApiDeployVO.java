package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST API 배포 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstApiDeployVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiNo;
    private String apiNm;
    private String apiVer;
    private String apiPath;
    private String apiId;
    private String endpntTbUrl;
    private String dplyReqFlag;
    private String dplyReqFlagDate;
    private String tbDplyStatus;
    private String tbDplyStatusDate;
    private String dplyVeriStatus;
    private String dplyVeriStatusDate;
    private String prdDplyReqFlag;
    private String prdDplyReqFlagDate;
    private String prdDplyStatus;
    private String prdDplyStatusDate;
    private String sysSysNm;
    private String spcApiNm;
    private String spcApiVeriBaseurl;
    private String apiHandlerCdNm;
    private Integer fmtRequiredCnt;
}
