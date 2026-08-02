package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 상태 그룹 및 요약 정보를 담는 VO
 * [마이그레이션] EgovMap 대체용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiStatusGroupVO extends ComBaseVO {
    private static final long serialVersionUID = 1L;

    // Group info
    private String statusGroupNm;
    private String statusGroupNo;
    private Integer sortOdrg;
    
    // User link fields
    private String userLinkSeq;
    private Integer userLinkSortOdrg;
    
    // Summary info
    private Integer asOkCount;
    private Integer asStatusCount;
    private Integer asDlCount;
    private Integer asNkCount;
}
