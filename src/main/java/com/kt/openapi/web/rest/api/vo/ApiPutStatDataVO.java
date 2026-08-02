package com.kt.openapi.web.rest.api.vo;

import lombok.Data;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.rest.api.vo
* 2. 타입명 : ApiPutStatDataVO.java
* 3. 작성일 : 2017. 12. 5. 오후 7:57:48
* 4. 작성자 : JungHwan Hwang
* 5. 설명 :
* </pre>
*/
@Data
public class ApiPutStatDataVO {
	
	private String apiNo;
	private String mbrId;
	private String yaml;

    private String apiSpcNo;
    private String ver;
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
