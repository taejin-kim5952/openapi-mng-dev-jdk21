package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 프로세스 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDeployVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seq;
    private String deployCd;
    private String deployCdnm;
    private String verifiCd;
    private String verifiCdnm;
    private String prntsCd;
    private String sysNm;
    private String ctgryNm;
    private String apiNm;
    private String apiPath;
    private String regr;
    private String regDt;
    private String bigo;
    private String apiVer;
    private String apiNo;
    private String apiId;
    private String deployStDt;
    private String deployAdm;
    private String useYn;
    private String vericdNm;
    private Integer tbCnt;
    private Integer tbSuccessCnt;
    private Integer cbCnt;
    private Integer verifiCnt;
    private String handlerNm;
    private String endpntTbUrl;
    private Integer requiredCnt;
    private String errorMsg;
    private String deployProcCd;
}
