package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST API 동기화 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstSyncAdmApiDplyVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private String dplyDt;
    private String dplyType;
    private String apiId;
    private String sysId;
    private String ifNo;
    private String ver;
    private String meth;
    private String uriIn;
    private String uriOut;
    private String reqHndlr;
    private String resHndlr;
    private String errHndlr;
    private String timeOut;
    private String prnts;
    private String prntsApiId;
    private String hndlrOptn;
    private String mask;
    private String atribInFmt;
    private String atribOutFmt;
    private String atribInComnParam;
    private String atribOutComnParam;
    private String srcTag;
    private String srcKey;
    private String defApiNo;
    private String udate;
    private String rdate;
}
