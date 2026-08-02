package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API LNB 메뉴 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // Dept 1
    private String sysId;
    private String sysNm;
    
    // Dept 2
    private String apiNm;
    private String apiSpcNo;
    private String ver;
    
    // Dept 3
    private String cApiNm;
    private String cApiPath;
    private String apiNo;
}
