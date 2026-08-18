package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 정의(DEF) 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDefVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiNo;
    private String apiSpcNo;
    private String apiNm;
    private String apiCtgryNo;
    private String apiPath;
    private String methodCd;
    private String apiId;
    private String apiDesc;
    private Date regDt;
    private String regr;
    private Date amdDt;
    private String amdr;
    private Integer sortOdrg;
    private String showStatusCd;
    private String useYn;
    private String apiGubun;
    private String apiHandlerCd;
    private String endpntMethodCd;
    private String endpntTbUrl;
    private String endpntPrdUrl;
    private Integer endpntTimeout;
    private String endpntClientIp;
    private String resmapResCdField;
    private String resmapSuccVal;
    private String resmapErrCdField;
    private String resmapErrMsgField;
    private String hdpApiOutFormat;
    private String hdpApiOutCommonParam;
    private String hdpApiEndpointId;
    private String hdpReqApiName;
    private String hdpReqConfigToBody;
    private String hdpReqHeaderToBody;
    private String hdpReqMappingToBody;
    private String hdpReqUrlDecode;
    private String hdpReqUrlEncode;
    private String hdpResMappingToBody;
    private String hdpResProvideParam;
    private String hdpResUrlEncode;
    private String hdpExtProp;
    private String hdpHndlroptnConfig;
    private String apiVer;
    private String apiVerNo;
    private String sandboxYn;
    private String guideGubun;
    private String apiConnGubun;
    private String guideTwoGroup;
    private String editFlag;
    private String deployFlag;
    private Long providerSeq;
    private String bstgwTbSysId;
    private String bstgwTbSysNm;
    private String bstgwPrdSysId;
    private String bstgwPrdSysNm;
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
    
    // Impact fields
    private String impact;
    private String apiUser;
    private String reprocessableYn;
    private String integrationType;
    private String apiMethod;
    private String enablerInfo;
    private String comments;
    
    // Additional fields for list/view
    private String sysIdNm;
    private String regSttusCdNm;
    private String minId;
    private String maxId;
    private String projectNamespace;
    private String projectName;
    
    // For list union
    private String ctgryNm;
    private String methodNm;
    private Integer sortOrder;
    private Integer pathCnt;
    private Long procSeq;
    private String spcNm;
    private String sysNm;
    
    // Missing fields (build error fix)
    private String yamlSbst;
    private String apiClass;
    private String sysId;
    private String amdrNm;
    
    /**
     * 명세 생성 출처(KOA_TB_API_SPC.SPC_SRC_CD). LEGACY=구버전 등록화면, SPCREG=신규 등록화면,
     * QUICK=빠른 API 등록. 구버전 등록화면에서 SPCREG 명세를 읽기 전용으로 막는 판정 기준.
     */
    private String spcSrcCd;

    // Missing fields from selApiInfo SQL
    private String apiSpcId;
    private String ver;
    private String autId;
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
    private String delYn;
    private String bstgwYn;
    private String apiVeriBaseurl;
}
