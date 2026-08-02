package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 서비스 신청 기본 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class TDevApplyInfoVO extends ComBaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String devapplySeq;
    private String sbAppinstid;
    private String sbSdpPw;
    private String tbAppinstid;
    private String tbSdpPw;
    private String devTitle;
    private String tbkVStt;
    private String tbkVEdt;
    private String bstgwAtribCpId;
    private String bstgwAtribServiceId;
    
    // For SB_CHECK
}
