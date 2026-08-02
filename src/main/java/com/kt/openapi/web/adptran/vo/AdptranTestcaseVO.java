package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Adptran API 테스트케이스 정보를 담는 VO
 * KOA_TB_API_TESTCASE 정보를 수용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranTestcaseVO extends ComBaseVO {

    private static final long serialVersionUID = 1L;

    private String testcaseId;
    private String apiNo;
    private String testcaseNm;
    private String paramGub;
    private String paramHeader;
    private String paramBody;
    private String paramQuery;
    private String paramHeaderJson;
    private String paramBodyJson;
    private String assertCase;
    private String assertField;
    private String assertOperator;
    private String assertValue;
    private String infoviewYn;
    
    // Join fields
    private String defApiNo;
    private String defApiNm;
    private String defApiHandlerCdNm;
}
