package com.kt.openapi.web.adptran.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 상태 일별 이력 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiStatusDailyHstVO extends ApiStatusVO {
    private static final long serialVersionUID = 1L;

    private String dailyList;
}
