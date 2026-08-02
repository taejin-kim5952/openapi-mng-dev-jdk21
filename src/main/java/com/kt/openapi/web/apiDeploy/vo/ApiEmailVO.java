package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 관련 이메일 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiEmailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mbrId;
    private String mbrNm;
    private String email;
    private String regEmail;
    private String editEmail;
}
