package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 상태 요약 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiStatusSummaryVO extends ComBaseVO {
    private static final long serialVersionUID = 1L;

    private String statusGroupNm;
    private String statusGroupNo;
    
    private Integer asOkCount;
    private Integer asStatusCount;
    
    private Integer asDlCount;
    private Integer asNkCount;
}
