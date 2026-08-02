package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST API 상태별 갯수 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstApiStatusCountVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer count;
    private String dplyReqFlag;
    private String tbDplyStatus;
    private String dplyVeriStatus;
    private String prdDplyReqFlag;
    private String prdDplyStatus;
}
