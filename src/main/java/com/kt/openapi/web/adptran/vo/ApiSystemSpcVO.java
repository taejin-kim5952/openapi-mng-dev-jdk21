package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API 시스템-서비스 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiSystemSpcVO extends ComBaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sysId;
    private String sysNm;
    private String apiSpcNo;
    private String apiNm;
    private String apiSpcId;
    private String ver;
}
