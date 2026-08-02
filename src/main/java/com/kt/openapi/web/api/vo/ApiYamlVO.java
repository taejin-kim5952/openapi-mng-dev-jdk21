package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API YAML 파일 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiYamlVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String yamlFilePath;
    private String yamlFileNm;
    private String yamlSbst;
}
