package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString(callSuper = true)
public class ApiRegVO extends ApiMainVo {

	@Serial
	private static final long serialVersionUID = 1L;

	private String id;			//-- 아이디
	private String name;		//-- 이름
	private String description;	//-- 내용
	private String regUser;		//-- 등록자

	private String yamlStr;		//-- YAML 데이터
	private String yamlSbst;
	
	private String apiSpcNo;
	private String apiSpcId;
	private String ver;
	private String apiNm;
	private String apiDesc;
	private String autId;
	private String sysId;
	private String verDesc;
	private String rfrnWsdlUrl;
	private String rfrnTmpltNo;
	private String rfrnApiSpcNo;
	private String host;
	private String basPath;
	private String yamlFilePath;
	private String yamlFileNm;
	private String regSttusCd;
	private String tmpltYn;
	private String regApvr;
	private String delDt;
	private String delr;
	
	private String apiPath;
	private String methodCd;
	private String methodCdNm;
	private String apiId;
	private String apiNo;
	private String importYn;
	
	private String apiCheckType;	//-- API중복 체크 구분자
	private String checkValue;		//-- API중복 체크 값
	private String importType;		//-- API 불러오기 구분값
	private String ctgryNm;
	private String sortOdrg;
	private String ctgryDesc;
	private String tempApiSpcNo;
	private String saveType ; 		// ins , del , upd
	private String apiSttusCd;
	private String memo;
	private String insertYn;
	private String contTypeCd;
	 
	private String urlPath;			// 외부파일 url
	private String urlType;			// 외부파일 종류
	
	private String paramTypeCd;	// 파라미터타입
	private String dataTypeCd;	// 데이터타입
	private String paramDesc;	// 파라미터설명
	private String Exam;		// 예제

	private String resCd;
	private String resDesc;
	private String paramLoc;
	private String objNo;
	private String objOdrg;
	private String apiGubun;	//-- api 구분 20190201

	//--[tag:adpt][drm][add] {
	//-- KOA_TB_API_SPC
	private String apiClass;

	//-- KOA_TB_API_DEF
	private String apiHandlerCd;		//-- handler구분코드 COMMON, ANYCOMMON, KOS, KOSMOS, SCAP, CAPRI, SB
	//-- [i]handler parameter {
	private String endpntMethodCd;		//-- Endpoint method (GET, POST...)
	private String endpntTbUrl;			//- Endpoint TB Url
	private String endpntPrdUrl;		//- Endpoint PRD Url
	//--@@@[cmt]private String endpntEtcUrl;		//- 기타 Endpoint Url // {Titie: URL}의 개행문자구분
	private String endpntTimeout;		//-- Endpoint Timeout (msec)
	private String endpntClientIp;		//-- Endpoint클라이언트IP 매핑키(for COMMON, ANYCOMMON)
	private String resmapResCdField;	//-- 결과매핑-결과코드필드(for ANYCOMMON)
	private String resmapSuccVal;		//-- 결과매핑-성공판정값(for ANYCOMMON)
	private String resmapErrCdField;	//-- 결과매핑-오류코드필드(for ANYCOMMON)
	private String resmapErrMsgField;	//-- 결과매핑-오류메시지필드(for ANYCOMMON)
	//-- [tag:SR-20210222][add] {
	private String hdpApiOutFormat; 
	private String hdpApiOutCommonParam;
	private String hdpApiEndpointId;
	private String hdpReqApiName;
	private String hdpReqConfigToBody;
	private String hdpReqHeaderToBody;
	private String hdpReqMappingToBody;
	private String hdpReqUrlDecode;
	private String hdpReqUrlEncode;
	private String hdpResMappingToBody;
	private String hdpResProvideParam;
	private String hdpResUrlEncode;
	//-- [tag:SR-20210222][add] }
	//-- [tag:SR-20210515][add]
	private String hdpExtProp;
	//-- [tag:SR-20230113][add]
	private String hdpHndlroptnConfig;
	//-- [i]handler parameter }

	private String apiVer;				//-- API version meta
	private String apiVerNo;			//-- API version group no
	//--[tab:job-20200714][add]
	private String editFlag;			//-- 수정가능여부
	private String deployFlag;			//-- API version group no
	//-- [tag:SR-20201127][add]
	private String guideGubun;
	//--[20201015][!@@!] API별 sandbox 적용여부 
	private String sandboxYn;

	//-- KOA_TB_API_PARAM
	private String required;		//-- 필수여부 [Y|N]
	private String personalData;	//-- 민감정보코드값 (성명, 생년월일, 주소, 유선전화번호, 휴대폰번호, 이메일주소, 주민번호, 운전면허, 외국인등록번호, 여권번호, 계좌번호, 신용카드번호, 멤버십카드번호, 단말정보, 위치정보, 바이오정보, SA_ID)
	private String doNotSend;		//-- Enabler전송안함여부 [Y|N]
	private String fixedValue;		//-- 고정값
	private String hidden;			//-- CP미노출여부 [Y|N]
	private String mappingKey;		//-- 매핑이름
	//-- [tag:SR-20210222][add] {
	private String hdpUrlDecode;	//-- url decode여부 [Y|N]
	private String hdpUrlEncode;	//-- url encode여부 [Y|N]
	private String hdpUploadTarget;	//-- upload target여부 [Y|N]
	//-- [tag:SR-20210222][add] }
	private String bigo;			//-- 비고
	//--[tag:adpt][drm][add] }

	//--[20201023][!@@!] 파라미터 sandbox 적용 여부
	private String paramSandboxYn;

	//-- [tag:SR-20210711]
	private String providerSeq;	// KOA_TB_API_PROVIDER.SEQ
	
	// -- KOA_TB_API_SPC_NS
	private String projectNamespace;	// 프로젝트 네임스페이스
	// -- CYD
	
    //-- [tag:PRJ-20220901] {
	//-- KOA_TB_API_SPC
	private String bstgwYn;
	private String apiVeriBaseurl;
	//-- KOA_TB_API_DEF
	private String bstgwTbSysId;
	private String bstgwPrdSysId;
	private String dplyReqFlag;
	private String tbDplyStatus;
	private String dplyVeriStatus;
	private String prdDplyReqFlag;
	private String prdDplyStatus;
    //-- [tag:PRJ-20220901] }
	
	private String insertImpactYn; //-- 영향도 저장 여부
	private String impact;	//-- 영향도
	private String apiUser;	//-- 대표 사용자
	private String reprocessableYn;	//-- 재처리가능유무
	private String intergrationType;	//-- 실시간/Batch 연동 유무
	private String apiMethod;	//-- API 기능
	private String enablerInfo;	//-- ENABLER 정보
	private String comments; //-- 비고

}
