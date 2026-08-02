package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST 시스템 동기화 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstSyncAdmSysDplyVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private String dplyDt;
    private String dplyType;
    private String sysId;
    private String sysNm;
    private String sysCd;
    private String apiLinkCd;
    private String edptProt;
    private String edptAtribUrl;
    private String edptAtribCerti;
    private String edptAtribCertiKey;
    private String edptAtribEcod;
    private String edptAtribAddr;
    private String edptAtribMinPool;
    private String edptAtribMaxPool;
    private String koaSysId;
    private String udate;
    private String rdate;
}
