package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST 신청 API 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstDevApplyApiVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long devapplySeq;
    private Long apiSeq;
    private String apiNm;
    private String apiCode;
}
