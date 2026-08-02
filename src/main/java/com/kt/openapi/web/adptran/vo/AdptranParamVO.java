package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Adptran API 파라미터 정보를 담는 VO
 * KOA_TB_API_PARAM 정보를 수용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranParamVO extends ComBaseVO {

    private static final long serialVersionUID = 1L;

    // CTE or Hierarchy fields
    private Integer level;
    private String paramNmFull;

    // KOA_TB_API_PARAM fields
    private String paramNo;
    private String apiNo;
    private String paramTypeCd;
    private String paramTypeCdNm;
    private String sortOdrg;
    private String paramNm;
    private String dataTypeCd;
    private String dataTypeCdNm;
    private String paramDesc;
    private String exam;
    private String prntsParamNo;
    private String resCd;
    private String resDesc;
    private String paramLoc;
    private String objNo;
    private String objOdrg;
    private String required;
    private String personalData;
    private String doNotSend;
    private String fixedValue;
    private String hidden;
    private String mappingKey;
    private String hdpUrlDecode;
    private String hdpUrlEncode;
    private String hdpUploadTarget;
    private String bigo;
    private String paramSandboxYn;
}
