package com.kt.openapi.web.rest.util;

//-- [tag:20200930][cmt]
//-- [i] RESPONSE_DATA_ERR_CODE, RESPONSE_DATA_ERR_Msg 를 제외하고는 사용되지 않는것으로 보임
//--##@Service
public class ResultCode {
    public static final String RESPONSE_OK_CODE = "1000";
    public static final String RESPONSE_OK_Msg = "성공";
    
    public static final String RESPONSE_SYS_ERR_CODE = "1011";
    public static final String RESPONSE_SYS_ERR_MSG = "시스템 내부 오류가 발생.(정의되지 않는 Exception,500)";
    
    public static final String RESPONSE_DB_ERR_CODE = "1021";
    public static final String RESPONSE_DB_ERR_Msg = "데이터베이스에서 처리과정중 오류가 발생되었습니다."; 
    
    public static final String RESPONSE_DATA_ERR_CODE = "1031";        
    public static final String RESPONSE_DATA_ERR_Msg = "필수 입력값에 값이 없습니다."; 
    
	public static final String RESPONSE_FAIL_DELETE_CODE 	= "1041";
	public static final String RESPONSE_FAIL_DELETE_Msg 	= "삭제에 실패했습니다. (없는 데이터)"; 
	
	public static final String RESPONSE_DATA_OK_ERR_CODE = "5021";        
    public static final String RESPONSE_DATA_OK_ERR_Msg = "유효한 데이터 타입이 아닙니다.";
}
