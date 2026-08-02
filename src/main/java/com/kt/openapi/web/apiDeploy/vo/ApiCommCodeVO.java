package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 관련 공통 코드 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiCommCodeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String comnCd;
    private String cdNm;
    private String groupCd;
    private String prntsCd;
    private String useYn;
    private Integer sortOdrg;
}
