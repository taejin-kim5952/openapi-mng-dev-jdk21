package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST IP 목록 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstIpListVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String target;
    private Long ipListSeq;
    private String ip;
}
