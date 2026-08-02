package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST 서비스 동기화 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstSyncAdmSvcDplyVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private String dplyDt;
    private String dplyType;
    private String svcId;
    private String svcNm;
    private String userNm;
    private String pw;
    private String slaSec;
    private String slaMin;
    private String slaHr;
    private String slaDay;
    private String slaMon;
    private String svcStDt;
    private String svcEndDt;
    private String apiAut;
    private String ipAcesAutAlwdIp;
    private String ipAcesAutBlckIp;
    private String atribCpId;
    private String atribServiceId;
    private String pwPlain;
    private String srcTag;
    private String udate;
    private String rdate;
}
