package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 배포 처리 상태 정보를 담는 VO
 * KOA_TB_DEPLOY_PROC 정보를 수용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranDeployProcVO extends ComBaseVO {

    private static final long serialVersionUID = 1L;

    private String apiNo;
    private String deployCd;
    private String verifiCd;
    private String deployStDt;
}
