package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 다음 API ID 관련 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranNextApiIdInfoVO extends ComBaseVO {

    private static final long serialVersionUID = 1L;

    private String prefix;
    private Integer curMinId;
    private Integer curMaxId;
    private Integer sysMinId;
    private Integer sysMaxId;
}
