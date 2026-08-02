package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST API 트래픽 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstApiTrafficVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiNo;
    private String apiSpcNo;
    private String apiNm;
    private String defApiNm;
    private String spcApiNm;
    private String sysSysId;
    private String sysSysNm;
    private String statDt;
    private Long sucesCnt;
    private Long failCnt;
    private Long rqtCnt;
}
