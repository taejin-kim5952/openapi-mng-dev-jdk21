package com.kt.openapi.web.adptran.api;

public enum AdptranApiResultCode {
	//-- define {
	INIT(0, "INIT", "GENERAL")

	,BIZ_EXCEPTION(900, "BIZ_EXCEPTION", "GENERAL")
	,DB_EXCEPTION(800, "DB_EXCEPTION", "GENERAL")
	,ACCESS_DENIED(700, "ACCESS_DENIED", "GENERAL")
	,RC_200_SUCESS(200, "SUCCESS", "GENERAL")
	,RC_500_SERVER_INTERNAL_ERROR(500, "SERVER_INTERNAL_ERROR", "GENERAL")
	,RC_404_NOT_FOUND(404, "RC_404_NOT_FOUND", "GENERAL")
	
	//-- for set_ApiInfo_To_ApiEntity
	,RC_SET_APIENTITY_SUCC(1, "SUCCESS", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_QUERY_API_DEF(-101, "API규격 정보가 없습니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_QUERY_API_PARAM(-102, "API파라미터 정보가 없습니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_URL_FORMAT(-201, "잘못된 Endpoint Url 형식 입니다", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_HANDLER_TYPE(-202, "잘못된 Handler Type 설정 입니다", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_METHOD(-203, "잘못된 API Method 설정 입니다", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_ENDPNT_METHOD(-204, "잘못된 Endpoint Method 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	//-- [tag:SR-20210222][add] {
	,RC_SET_APIENTITY_ERR_REQ_CONFIG_TO_BODY(-205, "잘못된 Request ConfigToBody 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_REQ_HEADER_TO_BODY(-206, "잘못된 Request HeaderToBody 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_REQ_MAPPING_TO_BODY(-207, "잘못된 Request MappingToBody 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_RES_MAPPING_TO_BODY(-208, "잘못된 Response MappingToBody 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	,RC_SET_APIENTITY_ERR_RES_PROVIDE_PARAM(-209, "잘못된 Response ProvideParam 설정 입니다.", "set_ApiInfo_To_ApiEntity")
	//-- [tag:SR-20210222][add] }

	//-- for apigw deploy, deployAsync - ApiActionType.CREATE/UPDATE
	,RC_APIGW_FN_DEPLOY_SUCC(1, "배포처리 실행 성공 입니다.", "APIGW_DEPLOY")
	//--##,RC_APIGW_FN_DEPLOY_FAIL(-1, "배포처리 실행 실패 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_EXCEPTION(-2, "배포처리 실행 예외 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_DEPLOYEXCEPTION(-3, "배포처리 실행 배포예외 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_SET_APIENTITY_ERR(-101, "배포정보 설정시 오류 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_CHECK_STATUS_STANDBY(-201, "배포처리 대기중 입니다.", "APIGW_DEPLOY")		//-- code fixed
	,RC_APIGW_FN_DEPLOY_CHECK_STATUS_INIT(-202, "배포처리 초기화 상태입니다.", "APIGW_DEPLOY")		//-- code fixed
	,RC_APIGW_FN_DEPLOY_CHECK_STATUS_DEPLOYING(-203, "배포처리 진행중 입니다.", "APIGW_DEPLOY")	//-- code fixed
	,RC_APIGW_FN_DEPLOY_CHECK_STATUS_ROLLING_BACK(-204, "배포처리 롤백중 입니다.", "APIGW_DEPLOY")	//-- code fixed

	//-- for apigw deploy, deployAsync - ApiActionType.DELETE
	,RC_APIGW_FN_DEPLOY_DELETE_SUCC(1, "배포삭제처리 실행 성공 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_DELETE_FAIL(-1, "배포삭제처리 실행 실패 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_DELETE_EXCEPTION(-2, "배포삭제처리 실행 예외 입니다.", "APIGW_DEPLOY")
	,RC_APIGW_FN_DEPLOY_DELETE_DEPLOYEXCEPTION(-3, "배포삭제처리 실행 배포예외 입니다.", "APIGW_DEPLOY")

	//-- for apigw deployStatus
	,RC_APIGW_FN_DEPLOYSTATUS_SUCC(1, "배포상태조회 실행 성공 입니다.", "APIGW_DEPLOYSTATUS")
	//--##,RC_APIGW_FN_DEPLOYSTATUS_FAIL(-1, "배포상태조회 실행 실패 입니다.", "APIGW_DEPLOYSTATUS")
	,RC_APIGW_FN_DEPLOYSTATUS_EXCEPTION(-2, "배포상태조회 실행 예외 입니다.", "APIGW_DEPLOYSTATUS")
	,RC_APIGW_FN_DEPLOYSTATUS_DEPLOYEXCEPTION(-3, "배포상태조회 실행 배포예외 입니다.", "APIGW_DEPLOYSTATUS")
	,RC_APIGW_FN_DEPLOYSTATUS_DEPLOY_JOB_IS_NOT_EXIST(-101, "배포상태조회 요청 Job이 없습니다.", "APIGW_DEPLOYSTATUS")

	//-- for set_Request_To_CpApiRequest
	,RC_SET_CPAPIREQUEST_SUCC(1, "SUCCESS", "set_Request_To_CpApiRequest")
	,RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS(-101, "headers parsing exception", "set_Request_To_CpApiRequest")
	,RC_SET_CPAPIREQUEST_ERR_READVALUE_REQUEST(-102, "request parsing exception", "set_Request_To_CpApiRequest")
	,RC_SET_CPAPIREQUEST_ERR_REMOVE_REQUEST_BODY_WRAP(-103, "remove request body wrap", "set_Request_To_CpApiRequest")
	
	//-- for apigw_cpApiGet
	,RC_APIGW_FN_CPAPIGET_SUCC(1, "CpApi호출 실행 성공 입니다.", "APIGW_CPAPIGET")		//-- code fixed
	,RC_APIGW_FN_CPAPIGET_FAIL(-1, "CpApi호출 실행 실패 입니다.", "APIGW_CPAPIGET")
	,RC_APIGW_FN_CPAPIGET_EXCEPTION(-2, "CpApi호출 실행 예외 입니다.", "APIGW_CPAPIGET")
	,RC_APIGW_FN_CPAPIGET_SET_CPAPIREQUEST_ERR(-101, "호출정보 설정시 오류 입니다.", "APIGW_CPAPIGET")
	,RC_APIGW_FN_CPAPIGET_NO_RESPONSE(-102, "CpApi호출 응답정보가 없습니다.", "APIGW_CPAPIGET")

	//-- for apigw_LampLog
	,RC_APIGW_FN_LAMPLOG_SUCC(1, "LampLog 실행 성공 입니다.", "APIGW_LAMPLOG")
	,RC_APIGW_FN_LAMPLOG_FAIL(-1, "LampLog 실행 실패 입니다.", "APIGW_LAMPLOG")
	;
	//-- define }

	private Integer code;
	private String message;
	private String sender;
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	private AdptranApiResultCode(Integer code, String message, String sender) {
		this.code = code;
		this.message = message;
		this.sender = sender;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}

	public static String getMsgFromCode(Integer code, String sender) {
		for (AdptranApiResultCode rc : AdptranApiResultCode.values()) {
			if (rc.getCode().equals(code) && rc.getSender().equals(sender)) {
				return rc.getMessage();
			}
		}
		return "";
	}
}
