package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.api.vo.ApiRegVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * Adptran API 상세 정보를 담는 VO
 * KOA_TB_API_DEF와 KOA_TB_API_SPC 조인 결과를 수용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranApiVO extends ApiRegVO {

    @Serial
    private static final long serialVersionUID = 1L;

    // KOA_TB_API_SPC (spc_ prefix fields)
    private String spcApiSpcId;
    private String spcVer;
    private String spcApiNm;
    private String spcApiDesc;
    private String spcAutId;
    private String spcSysId;
    private String spcVerDesc;
    private String spcRfrnWsdlUrl;
    private String spcRfrnTmpltNo;
    private String spcRfrnApiSpcNo;
    private String spcHost;
    private String spcBasPath;
    private String spcYamlFilePath;
    private String spcYamlFileNm;
    private String spcRegSttusCd;
    private String spcRegDt;
    private String spcRegr;
    private String spcAmdDt;
    private String spcYamlSbst;
    private String spcApiClass;
    private String spcBstgwYn;
    private String spcApiVeriBaseurl;
    
    // 추가 코드명
    private String sysIdNm;
    private String methodCdNm;
    private String apiHandlerCdNm;
    private String endpntMethodCdNm;
    private String bstgwTbSysNm;
    private String bstgwPrdSysNm;
}
