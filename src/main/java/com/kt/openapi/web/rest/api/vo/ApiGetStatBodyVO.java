package com.kt.openapi.web.rest.api.vo;

import lombok.Data;

@Data
public class ApiGetStatBodyVO {
	
	private String apiNo;
	private String mbrId;
	private String sessionKey;
	private String apiSpcNo;
	private String ver;
	private String yaml;
	private String apiName;
	private String systemId;
	private String systemName;
	private String apiStatus;
    private String apiDesc;
    private String autId;
    private String verDesc;
    private String host;
    private String basPath;
    private String rfrnWsdlUrl;
    private String rfrnTmpltNo;
    private String rfrnApiSpcNo;
    private String yamlFilePath;
    private String yamlFileNm;
    private String regSttusCd;
    private String tmpltYn;
    private String regr;
    private String amdDt;
    private String amdr;
    private String delYn;
    private String apiClass;
    private String bstgwYn;
    private String apiVeriBaseurl;
    private String minId;
    private String maxId;
    private String projectNamespace;
    private String projectName;
}