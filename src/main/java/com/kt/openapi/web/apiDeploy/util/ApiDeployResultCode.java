package com.kt.openapi.web.apiDeploy.util;

public enum ApiDeployResultCode {
	
	RC_200_NO_EXIST_DATA("200","NO EXIST DATA") ,
	
	//배포 코드
	CD_1000_DEPLOY_BASE_CODE("DEPLOY1000", "배포리스트"),
	CD_1010_DEPLOY_APPLY_CODE("DEPLOY1010", "TB 배포전"),
	CD_1013_DEPLOY_APPLY_CODE("DEPLOY1013", "TB 배포 실패"),
	CD_1020_DEPLOY_APPLY_CODE("DEPLOY1020", "TB 배포 완료"),
	CD_1030_DEPLOY_APPLY_CODE("DEPLOY1030", "검증시작"),
	CD_1040_DEPLOY_APPLY_CODE("DEPLOY1040", "검증완료"),
	CD_1045_DEPLOY_APPLY_CODE("DEPLOY1045", "보안검증"),
	CD_1050_DEPLOY_APPLY_CODE("DEPLOY1050", "배포신청"),
	CD_1060_DEPLOY_APPLY_CODE("DEPLOY1060", "상용배포 대기중"),
	CD_1063_DEPLOY_APPLY_CODE("DEPLOY1063", "배포 실패"),
	CD_1065_DEPLOY_APPLY_CODE("DEPLOY1065", "배포 반려"),
	CD_1070_DEPLOY_APPLY_CODE("DEPLOY1070", "배포 완료"),
	
	//검증 코드
	CD_1000_VERIFI_BASE_CODE("VERIFI1000", "검증리스트"),
	CD_1010_VERIFI_BASE_CODE("VERIFI1010", "검증시작코드"),
	CD_1020_VERIFI_BASE_CODE("VERIFI1020", "검증중"),
	CD_1030_VERIFI_BASE_CODE("VERIFI1030", "검증완료"),
	
	//배포 구분
	GB_DEPLOY_UPDATE_GUBUN("update", "배포 업데이트 "),
	
	//에러코드
	CD_DEPLOY_PROCESS_ERROR("PROC900", "PROCESS ERROR"),
	CD_DECRYPT_ERROR("ENC910", "Decrypt Error"),
	
	//배포결과 코드
	CD_RETURN_SUCCESS("000", "SUCCESS"),
	CD_RETURN_FAIL("999", "CB DEPOLY FAIL"),
	
	//배포구분 상용 : C  , TB : T
	CD_DEPLOY_CB_GUBUN("C","상용"),
	CD_DEPLOY_TB_GUBUN("T","상용"),
	
	//CASE CODE 
	CD_1010_TEST_CASE("TECASE1010","정상케이스"),
	CD_1020_TEST_CASE("TECASE1020","예외케이스"),
	CD_1030_TEST_CASE("TECASE1030","실패케이스"),
	
	//메일 수신 코드
	MAIL_MBR_KOS_ADMIN("79","KOS담당자"),
	MAIL_MBR_KOS_DEVELOPER("79","KOS개발자"),
	MAIL_MBR_KOS_SHUBADMIN("79","SHUB담당자"),
	MAIL_MBR_SEC_ADMIN("79","보안담당자"),
	MAIL_TEMPLATE_KOS_CODE("100000014","KOS안내메일템플릿"),
	MAIL_TEMPLATE_SEC_CODE("100000022","보안검증안내메일템플릿"),
	MAIL_TITLE_1050("DEPLOY1050","API배포 요청 안내 메일"),
	MAIL_TITLE_1065("DEPLOY1065","API배포 반려 안내 메일"),
	MAIL_TITLE_1030("DEPLOY1030","API검증 완료에 따른 보안검증 안내 메일 ");
	
	private String code;
	private String message;
	
	private ApiDeployResultCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
}
