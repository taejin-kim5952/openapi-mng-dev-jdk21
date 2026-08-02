package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST API 링크 동기화 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstSyncAdmApiLinkDataVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private String dplyDt;
    private String aldtType;
    private String aldtKey;
    private String aldtValue;
    private String srcTag;
    private String srcKey;
    private String defApiNo;
    private String udate;
    private String rdate;
}
