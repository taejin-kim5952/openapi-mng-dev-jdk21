package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * BEAST 인터페이스 실행 이력 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BstIfExecHistVO extends ComBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private String logDate;
    private String logTag;
    private String logMsg;
    private String reqUri;
    private String reqMethod;
    private String reqHeader;
    private String reqBody;
    private String resStatusCode;
    private String resHeader;
    private String resBody;
}
