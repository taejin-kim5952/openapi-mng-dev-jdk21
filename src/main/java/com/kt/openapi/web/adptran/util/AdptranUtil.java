package com.kt.openapi.web.adptran.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.vo.AdptranApiVO;
import com.kt.openapi.web.adptran.vo.AdptranParamVO;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiRequest;
import com.kt.openapi.web.apigw.entity.api.manager.*;
import com.kt.openapi.web.apigw.entity.endpoint.EndpointConfig;
import com.kt.openapi.web.apigw.type.ApiDataType;
import com.kt.openapi.web.apigw.type.GwProfile;
import com.kt.openapi.web.apigw.type.HandlerType;
import com.kt.openapi.web.apigw.type.URLScheme;
import com.kt.openapi.web.beast.apigw.constant.BstgwConstant;
import com.kt.openapi.web.login.controller.LoginController;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.adptran.util
 * 2. 타입명 : AdptranUtil.java
 * 3. 작성일 : 2019-05-02 00:00:00
 * 4. 작성자 : drm
 * 5. 설명 : adptran관련 업무용 공통함수
 * </pre>
 */
@Component
public class AdptranUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdptranUtil.class);

	private static LoginController loginController;

	@Autowired
	public void setLoginController(LoginController loginController) {
		AdptranUtil.loginController = loginController;
	}

	/*
	 * 싱글톤 패턴 사용
	 */
	private static AdptranUtil adptranUtil = null;

	public static synchronized AdptranUtil getInstance() {
		LOGGER.debug("###[adpt][AdptranUtil.getInstance()]");
		if (AdptranUtil.adptranUtil == null) {
			AdptranUtil.adptranUtil = new AdptranUtil();
			LOGGER.debug("###[adpt][AdptranUtil.getInstance()][new AdptranUtil()]");
		}
		return AdptranUtil.adptranUtil;
	}

	// -- properties 사용 {
	// 실행모드식별자
	private static String configRunmode;
	@Value("${config.runmode}")
	public void setConfigRunmode(String pConfigRunmode) {
		AdptranUtil.configRunmode = pConfigRunmode;
		LOGGER.debug("[start: {}.{}()][config.runmode: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.configRunmode);
	}

	// js cache-update용
	private static String configJsVersion;
	@Value("${config.js.version}")
	public void setConfigJsVersion(String pConfigJsVersion) {
		AdptranUtil.configJsVersion = pConfigJsVersion;
		LOGGER.debug("[start: {}.{}()][config.js.version: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.configJsVersion);
	}

	// -- API url root
	private static String adptranApiUrl;
	@Value("${config.adptran.api.url}")
	public void setAdptranApiUrl(String adptranApiUrl) {
		AdptranUtil.adptranApiUrl = adptranApiUrl;
		LOGGER.debug("[start: {}.{}()][config.adptran.api.url: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.adptranApiUrl);
	}

	//-- API url root (개발용 )
	private static String devAdptranApiUrl;
	@Value("${dev.adptran.api.url}")
	public void setDevAdptranApiUrl(String devAdptranApiUrl) {
		AdptranUtil.devAdptranApiUrl = devAdptranApiUrl;
		LOGGER.debug("[start: {}.{}()][dev.adptran.api.url: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.devAdptranApiUrl);
	}

	// webpack-dev-server 사용
	private static String devUseWebpackDevServer;
	@Value("${dev.use.webpack.dev.server}")
	public void setDevUseWebpackDevServer(String devUseWebpackDevServer) {
		AdptranUtil.devUseWebpackDevServer = devUseWebpackDevServer;
		LOGGER.debug("[start: {}.{}()][dev.use.webpack.dev.server: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.devUseWebpackDevServer);
	}

	// webpack-dev-server domain
	private static String devWebpackDevServer;
	@Value("${dev.webpack.dev.server}")
	public void setDevWebpackDevServer(String devWebpackDevServer) {
		AdptranUtil.devWebpackDevServer = devWebpackDevServer;
		LOGGER.debug("[start: {}.{}()][dev.webpack.dev.server: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AdptranUtil.devWebpackDevServer);
	}
	// -- properties 사용 }

	// -- global const {
	// -- global const }

	// -- login여부
	public static boolean isServiceLogin(HttpSession httpSession) {
		UserJoinVO userJoinVO = (UserJoinVO)httpSession.getAttribute("ssUserVo");
		boolean bRet = loginController.isUsing(userJoinVO.getMbrId());
		//--##boolean bRet = ((null != userJoinVO) && (userJoinVO.getMbrId().length() > 0) && (userJoinVO.getMbrNm().length() > 0));
		LOGGER.debug("###[adpt][AdptranUtil.isServiceLogin()][bRet: {}]", bRet);
		return bRet;
	}

	//-- adptran관련 ajax api url	
	public static String getAdptranApiUrl() {
		String sRet = ("dev".equalsIgnoreCase(AdptranUtil.configRunmode) ? AdptranUtil.devAdptranApiUrl : AdptranUtil.adptranApiUrl);
		LOGGER.debug("###[adpt][AdptranUtil.getAdptranApiUrl()][sRet: {}]", sRet);
		return sRet;
	}

	// -- get service login정보
	public static String getServiceLoginInfo(HttpSession httpSession, String pCmd) {
		String sRet = "";
		UserJoinVO userJoinVO = (UserJoinVO)httpSession.getAttribute("ssUserVo");
		if (null != userJoinVO) {
			if (true == pCmd.equalsIgnoreCase("userid")) {
				sRet = KsmUtil.fnSafeStr(userJoinVO.getMbrId());
			}
			else if (true == pCmd.equalsIgnoreCase("username")) {
				sRet = KsmUtil.fnSafeStr(userJoinVO.getMbrNm());
			}
		}
		LOGGER.debug("###[adpt][AdptranUtil.getServiceLoginInfo()][pCmd: {}][sRet: {}]", pCmd, sRet);
		return sRet;
	}
	
	//-- get bundle script src
	public static String getBundleScriptSrc(HttpServletRequest request) {
		String bundleScriptSrc = "";
		String requestUri = KsmUtil.fnSafeStr(request.getAttribute("jakarta.servlet.forward.request_uri"));
		String requestUriScriptFile = "_unknown_";

		String param_vue_part = KsmUtil.fnSafeStr(request.getParameter("param_vue_part"));
		if (param_vue_part.length() > 0) {	//-- parameter로 bundle명을 지정할 경우
			requestUriScriptFile = param_vue_part;
		}
		else if (requestUri.indexOf("/apidev/adptran/apistatus/") == 0) {
			/*--
				/apidev/adptran/apistatus/group -> apistatus/apistatus_group_bundle.js
				/apidev/adptran/apistatus/list -> apistatus/apistatus_list_bundle.js
			--*/
			requestUriScriptFile = requestUri.replace("/apidev/adptran/apistatus/", "apistatus/apistatus_");
		}
		else {
			requestUriScriptFile = (requestUri.substring(requestUri.lastIndexOf("/") + 1));
		}

		String bundleRoot = "/resources/adptran/bundle";
		String s_suffix = "_bundle.js" + ("?dum=" + AdptranUtil.configJsVersion);

		bundleScriptSrc = bundleRoot + "/" + requestUriScriptFile + s_suffix;

		String ssConfigRunmode = KsmUtil.fnSafeStr(request.getSession().getAttribute("config.runmode"));
		String configRunmode = ((0 < ssConfigRunmode.length()) ? ssConfigRunmode : AdptranUtil.configRunmode);
		boolean bIsRunmodeDev = "dev".equalsIgnoreCase(configRunmode);
		if (true == bIsRunmodeDev) {
			String ssDevUseWebpackDevServer = KsmUtil.fnSafeStr(request.getSession().getAttribute("dev.use.webpack.dev.server"));
			String devUseWebpackDevServer = ((0 < ssDevUseWebpackDevServer.length()) ? ssDevUseWebpackDevServer : AdptranUtil.devUseWebpackDevServer);
			if (true == "y".equalsIgnoreCase(devUseWebpackDevServer)) {
				bundleScriptSrc = AdptranUtil.devWebpackDevServer + bundleScriptSrc;
			}
		}

		return bundleScriptSrc;
	}

	//-- [JSP->Thymeleaf 마이그레이션] 원본 <jsp:include>+<jsp:param name="param_vue_part">는 request
	//-- 파라미터로 실제 전달됐지만, Thymeleaf th:fragment 파라미터(vuePart)는 request와 무관한 템플릿
	//-- 지역변수라 getBundleScriptSrc(request)가 이를 못 읽고 URI 기반 추측(항상 틀림)으로 빠져 번들
	//-- 파일이 존재하지 않는 경로가 되고, 그 결과 서버가 404 대신 HTML 에러페이지를 200으로 반환하는
	//-- 이 프로젝트의 GlobalExceptionHandler 특성상 브라우저가 "Unexpected token '<'" JS 파싱 에러를
	//-- 낸다 - vuePart를 그대로 받아 직접 경로를 구성하도록 오버로드 추가(세션 기반 webpack-dev-server
	//-- 오버라이드는 템플릿에서 #request 사용 불가(Thymeleaf 3.1+)라 재현 불가 - 정적 필드 기본값만 사용)
	public static String getBundleScriptSrc(String vuePart) {
		String s_suffix = "_bundle.js" + ("?dum=" + AdptranUtil.configJsVersion);
		String bundleScriptSrc = "/resources/adptran/bundle/" + vuePart + s_suffix;

		boolean bIsRunmodeDev = "dev".equalsIgnoreCase(AdptranUtil.configRunmode);
		if (true == bIsRunmodeDev && true == "y".equalsIgnoreCase(AdptranUtil.devUseWebpackDevServer)) {
			bundleScriptSrc = AdptranUtil.devWebpackDevServer + bundleScriptSrc;
		}

		return bundleScriptSrc;
	}

	//-- ### for adptran biz ###
	//-- for apigw {
	//-- HandlerCd명칭으로 apigw HandlerType을 구한다
    //-- HandlerType: [ADPJSON|ANYJSON|KOSJSON|KOSSOAP] -> [COMMON|ANYCOMMON|KOSMOS|KOS|SCAP|CAPRI|SB] -> [APIHDR1010 ~ APIHDR1070]
	public static HandlerType getApigwHandlerType(String apiHandlerCdNm) {
		String[] a_HandlerCdNm = { "COMMON", "ANYCOMMON", "KOS", "KOSMOS", "SCAP", "CAPRI", "SB" };
		HandlerType[] a_HandlerType = { HandlerType.ADP_JSON_COMMON, HandlerType.ANY_JSON_COMMON, HandlerType.KOS_SOAP_COMMON, HandlerType.KOS_JSON_COMMON
			//-- [tag:SR-20210222][add]
			, HandlerType.ADP_SCAP_COMMON, HandlerType.ADP_CAPRI_COMMON, HandlerType.ADP_SB_COMMON };
		int findIdx = Arrays.asList(a_HandlerCdNm).indexOf(apiHandlerCdNm);
		return ((findIdx != -1) ? a_HandlerType[findIdx] : null);
	}

	//-- methodCd명칭으로 apigw HttpMethod을 구한다
	public static HttpMethod getApigwHttpMethod(String methodCdNm) {
		String[] a_MethodCdNm = { "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD" };
		HttpMethod[] a_HttpMethod = { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.HEAD };
		int findIdx = Arrays.asList(a_MethodCdNm).indexOf(methodCdNm);
		return ((findIdx != -1) ? a_HttpMethod[findIdx] : null);
	}

	//-- dataTypeCd명칭으로 apigw ApiDataType을 구한다
	public static ApiDataType getApiDataType(String dataTypeCdNm) {
		String[] a_DataTypeCdNm = { "Object", "Array", "String", "Number", "Integer", "Boolean" };
		ApiDataType[] a_ApiDataType = { ApiDataType.OBJECT, ApiDataType.ARRAY, ApiDataType.STRING, ApiDataType.NUMBER, ApiDataType.INTEGER, ApiDataType.BOOLEAN };
		int findIdx = Arrays.asList(a_DataTypeCdNm).indexOf(dataTypeCdNm);
		return ((findIdx != -1) ? a_ApiDataType[findIdx] : null);
	}
	
	/**
	 * header, request정보를 apigw CpApiRequest에 설정한다
	 */
	public static AdptranApiResultCode set_Request_To_CpApiRequest(String req_api_url, String req_headers, String req_body, CpApiRequest cpApiRequest) {
		LOGGER.debug("###[adpt][AdptranUtil.set_Request_To_CpApiRequest()][req_api_url: {}][req_headers: {}][req_body: {}][cpApiRequest: {}]", req_api_url, req_headers, req_body, cpApiRequest);

		AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

		ObjectMapper mapper = new ObjectMapper();

		String transaction_id = UUID.randomUUID().toString();
		String sequence_no = "0";

		req_headers = req_headers.replace("\\p{Cntrl}", "");

		Map<String, String> headers = null;
		try {
			headers = mapper.readValue(req_headers, Map.class);
		} catch (JsonParseException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][headers][resultCode: {}][e.getMessage(): {}][JsonParseException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS;
			return resultCode;
		} catch (JsonMappingException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][headers][resultCode: {}][e.getMessage(): {}][JsonMappingException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS;
			return resultCode;
		} catch (IOException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][headers][resultCode: {}][e.getMessage(): {}][IOException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS;
			return resultCode;
		}
	
		Map<String, Object> body = null;
		try {
			body = mapper.readValue(req_body, Map.class);
		} catch (JsonParseException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][body][resultCode: {}][e.getMessage(): {}][JsonParseException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_REQUEST;
			return resultCode;
		} catch (JsonMappingException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][body][resultCode: {}][e.getMessage(): {}][JsonMappingException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_REQUEST;
			return resultCode;
		} catch (IOException e) {
			LOGGER.error("\n\n### [AdptranUtil.set_Request_To_CpApiRequest()][body][resultCode: {}][e.getMessage(): {}][IOException: {}] ###\n", AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_HEADERS.getMessage(), e.getMessage(), e);
			resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_READVALUE_REQUEST;
			return resultCode;
		}

		if (null != body) {
			LOGGER.debug("###[o-o][adpt][AdptranUtil.set_Request_To_CpApiRequest()][body.getClass(): {}]", body.getClass());
			//-- remove body wrap
			Iterator<Map.Entry<String, Object>> iterator = body.entrySet().iterator();
			if (iterator.hasNext() == true) {
				Map.Entry<String, Object> entry = iterator.next();
				Object value = entry.getValue();
				if (value instanceof HashMap) {
					body = (Map<String, Object>)value;
				}
				else {
					LOGGER.debug("\n\n###[adpt][AdptranUtil.set_Request_To_CpApiRequest()][body.next().getClass(): {}]", value.getClass());
					resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_REMOVE_REQUEST_BODY_WRAP;
					return resultCode;
				}
			}
			else {
				resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_ERR_REMOVE_REQUEST_BODY_WRAP;
				return resultCode;
			}
		}

		//-- header 처리 {
		//-- value없는경우 remove
		//--[20210210][chg]
		headers.values().removeAll(Collections.singleton(""));
		//--##headers.values().remove("");	//-- "" value 1개만 remove됨
		//-- Content-Type지정이 없을시 기본값 설정
		if (false == headers.containsKey("Content-Type")) {
			headers.put("Content-Type", "application/json;charset=utf-8");
		}
		//-- header 처리 }

		cpApiRequest.setApiUrl(req_api_url);
		cpApiRequest.setTransactionId(transaction_id);
		cpApiRequest.setSequenceNo(sequence_no);
		cpApiRequest.setHeaders(headers);
		cpApiRequest.setRequest(body);

		resultCode = AdptranApiResultCode.RC_SET_CPAPIREQUEST_SUCC;

		return resultCode;
	}


	/**
	 * API정보를 apigw ApiEntity에 설정한다 (Map 기반 오버로드 - 하위 호환성 유지)
	 */
	public static AdptranApiResultCode set_ApiInfo_To_ApiEntity(String req_gw_profile, Map<String, Object> mapOut, List<Map<String, Object>> listOut, ApiEntity apiEntity) {
		LOGGER.debug("###[adpt][AdptranUtil.set_ApiInfo_To_ApiEntity()][Map 기반 호출][req_gw_profile: {}]", req_gw_profile);

		AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

		if (mapOut == null || mapOut.size() == 0) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_DEF.getMessage());
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_DEF;
			return resultCode;
		}
		if (listOut == null || listOut.size() == 0) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_PARAM.getMessage());
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_PARAM;
			return resultCode;
		}
		
		//-- DB value {
		String apiNm = KsmUtil.fnSafeStr(mapOut.get("apiNm"));
		String apiPath = KsmUtil.fnSafeStr(mapOut.get("apiPath"));
		String apiId = KsmUtil.fnSafeStr(mapOut.get("apiId"));
		String system = KsmUtil.fnSafeStr(mapOut.get("sysIdNm"));
		String apiHandlerCdNm = KsmUtil.fnSafeStr(mapOut.get("apiHandlerCdNm"));
		String methodCdNm = KsmUtil.fnSafeStr(mapOut.get("methodCdNm"));
		String endpntMethodCdNm = KsmUtil.fnSafeStr(mapOut.get("endpntMethodCdNm"));
		String endpntTimeout = KsmUtil.fnSafeStr(mapOut.get("endpntTimeout"));
		String endpntTbUrl = KsmUtil.fnSafeStr(mapOut.get("endpntTbUrl"));
		String endpntPrdUrl = KsmUtil.fnSafeStr(mapOut.get("endpntPrdUrl"));
		String endpntClientIp = KsmUtil.fnSafeStr(mapOut.get("endpntClientIp"));
		String resmapResCdField = KsmUtil.fnSafeStr(mapOut.get("resmapResCdField"));
		String resmapSuccVal = KsmUtil.fnSafeStr(mapOut.get("resmapSuccVal"));
		String[] a_resmapSuccVal = resmapSuccVal.split(",");
		String resmapErrCdField = KsmUtil.fnSafeStr(mapOut.get("resmapErrCdField"));
		String resmapErrMsgField = KsmUtil.fnSafeStr(mapOut.get("resmapErrMsgField"));
		String version = KsmUtil.fmt_data(apiPath, "fmt_version_in_path");

		String hdpApiOutFormat = KsmUtil.fnSafeStr(mapOut.get("hdpApiOutFormat"));
		String hdpApiOutCommonParam = KsmUtil.fnSafeStr(mapOut.get("hdpApiOutCommonParam"));
		String hdpApiEndpointId = KsmUtil.fnSafeStr(mapOut.get("hdpApiEndpointId"));
		String hdpReqApiName = KsmUtil.fnSafeStr(mapOut.get("hdpReqApiName"));
		String hdpReqConfigToBody = KsmUtil.fnSafeStr(mapOut.get("hdpReqConfigToBody"));
		String hdpReqHeaderToBody = KsmUtil.fnSafeStr(mapOut.get("hdpReqHeaderToBody"));
		String hdpReqMappingToBody = KsmUtil.fnSafeStr(mapOut.get("hdpReqMappingToBody"));
		String hdpReqUrlDecode = KsmUtil.fnSafeStr(mapOut.get("hdpReqUrlDecode"));
		String hdpReqUrlEncode = KsmUtil.fnSafeStr(mapOut.get("hdpReqUrlEncode"));
		String hdpResMappingToBody = KsmUtil.fnSafeStr(mapOut.get("hdpResMappingToBody"));
		String hdpResProvideParam = KsmUtil.fnSafeStr(mapOut.get("hdpResProvideParam"));
		String hdpResUrlEncode = KsmUtil.fnSafeStr(mapOut.get("hdpResUrlEncode"));

		String hdpExtProp = KsmUtil.fnSafeStr(mapOut.get("hdpExtProp"));
		String hdpExtPropIsBiznaru = KsmUtil.fnGetExtProp("is_biznaru", hdpExtProp);

		String hdpHndlroptnConfig = KsmUtil.fnSafeStr(mapOut.get("hdpHndlroptnConfig"));

        String bstGwYn = KsmUtil.fnSafeStr(mapOut.get("spcBstgwYn"));
        String bstgwTbSysId = KsmUtil.fnSafeStr(mapOut.get("bstgwTbSysId"));
        String bstgwPrdSysId = KsmUtil.fnSafeStr(mapOut.get("bstgwPrdSysId"));
		//-- DB value }

		//-- for parameter {
		List<ParamNode> listRequestHeader = new ArrayList<>();
		List<ParamNode> listRequestBody = new ArrayList<>();
		List<ParamNode> listResponseHeader = new ArrayList<>();
		List<ParamNode> listResponseBody = new ArrayList<>();

		List<Integer> listExcludeParam = new ArrayList<>();
		//-- for parameter }

		//-- ApiEntity {
		HandlerType handlerType = AdptranUtil.getApigwHandlerType(apiHandlerCdNm);
		if (handlerType == null) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][apiHandlerCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_HANDLER_TYPE.getMessage(), apiHandlerCdNm);
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_HANDLER_TYPE;
			return resultCode;
		}
		HttpMethod method = AdptranUtil.getApigwHttpMethod(methodCdNm);
		if (method == null) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][methodCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_METHOD.getMessage(), methodCdNm);
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_METHOD;
			return resultCode;
		}
		apiEntity.setId(apiNm);
		apiEntity.setTimeout(KsmUtil.parseInt(endpntTimeout, 30000));
		apiEntity.setUrl(apiPath);
		apiEntity.setHandler(handlerType);
		apiEntity.setMethod(method);

        apiEntity.setBstgwDplyType(BstgwConstant.DPLY_TYPE.DPLY);
        if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
            apiEntity.setBstgwSysId(bstgwPrdSysId);
        }
        else {
            apiEntity.setBstgwSysId(bstgwTbSysId);
        }
        
		if (true == "Y".equalsIgnoreCase(bstGwYn)) {
			hdpHndlroptnConfig = ((hdpHndlroptnConfig.length() == 0) ? "{}" : hdpHndlroptnConfig);
			apiEntity.setHdpHndlroptnConfig(hdpHndlroptnConfig);
		}

		if ((handlerType != HandlerType.ADP_SCAP_COMMON) && (handlerType != HandlerType.ADP_CAPRI_COMMON)) {
			HttpMethod endpntMethod = AdptranUtil.getApigwHttpMethod(endpntMethodCdNm);
			if (endpntMethod == null) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][endpntMethodCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_ENDPNT_METHOD.getMessage(), endpntMethodCdNm);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_ENDPNT_METHOD;
				return resultCode;
			}
		
			EndpointConfig endpoint = new EndpointConfig();
			String endpntUrl = (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) ? endpntPrdUrl : endpntTbUrl);

			if (true == "Y".equalsIgnoreCase(bstGwYn)) {
				endpntUrl = KsmUtil.fmt_parseUrl(endpntUrl, "path");
			}

			URLScheme protocol = null;
			protocol = ((endpntUrl.toLowerCase().indexOf("http") == 0) ? URLScheme.HTTP : protocol);
			protocol = ((endpntUrl.toLowerCase().indexOf("https") == 0) ? URLScheme.HTTPS : protocol);

			system = ((handlerType == HandlerType.ADP_SB_COMMON) ? apiHandlerCdNm : system);
	
			endpoint.setMethod(endpntMethod);
			endpoint.setSystem(system);
			endpoint.setProtocol(protocol);
			endpoint.setUrl(endpntUrl);
			apiEntity.setEndpoint(endpoint);
		}

		if ((handlerType == HandlerType.ADP_JSON_COMMON) || (handlerType == HandlerType.ANY_JSON_COMMON)) {
			apiEntity.setClientIpMappingKey(endpntClientIp);
		}
		if (handlerType == HandlerType.ANY_JSON_COMMON) {
			ApiResultMapping resultMapping = new ApiResultMapping();
			resultMapping.setResultCode(resmapResCdField);
			resultMapping.setSuccessValue(Arrays.asList(a_resmapSuccVal));
			resultMapping.setErrorCode(resmapErrCdField);
			resultMapping.setErrorMessage(resmapErrMsgField);
			apiEntity.setResultMapping(resultMapping);
		}
		if (handlerType == HandlerType.ADP_SCAP_COMMON) {
			apiEntity.setOutCommonParam(hdpApiOutCommonParam);
			apiEntity.setEndpointId(hdpApiEndpointId);
			apiEntity.setReqApiName(hdpReqApiName);
		}
		if (handlerType == HandlerType.ADP_CAPRI_COMMON) {
			apiEntity.setReqUrlEncodeCharset(hdpReqUrlEncode);
		}
		if (handlerType == HandlerType.ADP_SB_COMMON) {
			apiEntity.setOutFormat(hdpApiOutFormat);
			apiEntity.setOutCommonParam(hdpApiOutCommonParam);
			apiEntity.setReqApiName(hdpReqApiName);
		}
		if ((handlerType == HandlerType.ADP_SCAP_COMMON) || (handlerType == HandlerType.ADP_CAPRI_COMMON) || (handlerType == HandlerType.ADP_SB_COMMON)) {
			apiEntity.setReqConfigToBody(hdpReqConfigToBody);
			apiEntity.setReqHeaderToBody(hdpReqHeaderToBody);
			apiEntity.setReqMappingToBody(hdpReqMappingToBody);
			apiEntity.setReqUrlDecodeCharset(hdpReqUrlDecode);
			apiEntity.setResMappingToBody(hdpResMappingToBody);
			apiEntity.setResProvideParam(hdpResProvideParam);
			apiEntity.setResUrlEncodeCharset(hdpResUrlEncode);
		}

		apiEntity.setApiNo(apiId);
		apiEntity.setVersion(version);

		for (int n_ii = 0; n_ii < listOut.size();  n_ii++) {
			Map<String, Object> mapItem = listOut.get(n_ii);

			String paramNm = KsmUtil.fnSafeStr(mapItem.get("paramNm"));
			String paramTypeCd = KsmUtil.fnSafeStr(mapItem.get("paramTypeCd"));
			String paramLoc = KsmUtil.fnSafeStr(mapItem.get("paramLoc"));
			String dataTypeCdNm = KsmUtil.fnSafeStr(mapItem.get("dataTypeCdNm"));
			int prntsParamNo = KsmUtil.parseInt(mapItem.get("prntsParamNo"), 0);
			int paramNo = KsmUtil.parseInt(mapItem.get("paramNo"), 0);

			String paramGub = "";
			paramGub = ((("PRMTYP1010".equals(paramTypeCd) == true) && ("header".equals(paramLoc) == true)) ? "req_header" : paramGub); 
			paramGub = ((("PRMTYP1010".equals(paramTypeCd) == true) && ("body".equals(paramLoc) == true)) ? "req_body" : paramGub); 
			paramGub = ((("PRMTYP1020".equals(paramTypeCd) == true) && ("header".equals(paramLoc) == true)) ? "res_header" : paramGub); 
			paramGub = ((("PRMTYP1020".equals(paramTypeCd) == true) && ("body".equals(paramLoc) == true)) ? "res_body" : paramGub);

			if (("req_body".equals(paramGub) == true) || ("res_body".equals(paramGub) == true)) {
				if (prntsParamNo == 0) {
					listExcludeParam.add(paramNo);
					paramGub = paramGub + "_skip";
				}
				mapItem.put("prntsParamNo", String.valueOf((listExcludeParam.contains(prntsParamNo) == true) ? 0 : prntsParamNo));
				
				if (("Array".equalsIgnoreCase(dataTypeCdNm) == true) && ((n_ii + 1) < listOut.size())) {
					Map<String, Object> mapItem_next = listOut.get(n_ii + 1);
					String paramNm_next = KsmUtil.fnSafeStr(mapItem_next.get("paramNm"));
					int prntsParamNo_next = KsmUtil.parseInt(mapItem_next.get("prntsParamNo"), 0);
					if ((prntsParamNo_next == paramNo) && (paramNm_next.length() > 0) && (paramNm_next.equals(paramNm) == true)) {
						String dataTypeCdNm_next = KsmUtil.fnSafeStr(mapItem_next.get("dataTypeCdNm"));
						if (";Object;Array;".indexOf(";" + dataTypeCdNm_next + ";") != -1) {
							mapItem.put("paramNo", String.valueOf(KsmUtil.parseInt(mapItem_next.get("paramNo"), 0)));
							n_ii++;
						}
						else if (";String;Number;Integer;Boolean;".indexOf(";" + dataTypeCdNm_next + ";") != -1) {
							n_ii++;
						} 
					}
				}
			}
			if ("req_header".equals(paramGub) == true) {
				listRequestHeader.add(AdptranUtil.getParamNode(mapItem));
			}
			else if ("req_body".equals(paramGub) == true) {
				listRequestBody.add(AdptranUtil.getParamNode(mapItem));
			}
			else if ("res_header".equals(paramGub) == true) {
				listResponseHeader.add(AdptranUtil.getParamNode(mapItem));
			}
			else if ("res_body".equals(paramGub) == true) {
				listResponseBody.add(AdptranUtil.getParamNode(mapItem));
			}
		}

		ApiRequestEntity request = new ApiRequestEntity();
		ApiResponseEntity response = new ApiResponseEntity();
		
		AdptranUtil.setApiParameterHierarchy(listRequestHeader, request.getHeaders());
		AdptranUtil.setApiParameterHierarchy(listRequestBody, request.getParameters());
		AdptranUtil.setApiParameterHierarchy(listResponseBody, response.getParameters());
		
		apiEntity.setRequest(request);
		apiEntity.setResponse(response);
		
		resultCode = AdptranApiResultCode.RC_SET_APIENTITY_SUCC;

		return resultCode;
	}

	public static ParamNode getParamNode(Map<String, Object> mapItem) {
		int paramNo = Integer.parseInt(KsmUtil.fnSafeStr(mapItem.get("paramNo")));
		int prntsParamNo = Integer.parseInt(KsmUtil.fnSafeStr(mapItem.get("prntsParamNo")));
		ApiParameter apiParameter = new ApiParameter();

	    ApiDataType type = AdptranUtil.getApiDataType(KsmUtil.fnSafeStr(mapItem.get("dataTypeCdNm")));
	    String name = KsmUtil.fnSafeStr(mapItem.get("paramNm"));
	    String description = KsmUtil.fnSafeStr(mapItem.get("paramDesc"));
	    boolean required = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("required"))));
	    boolean personalData = (KsmUtil.fnSafeStr(mapItem.get("personalData")).length() > 0);
	    boolean hidden = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("hidden"))));
	    boolean doNotSend = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("doNotSend"))));
	    String fixedValue = KsmUtil.fnSafeStr(mapItem.get("fixedValue"));
	    String mappingKey = KsmUtil.fnSafeStr(mapItem.get("mappingKey"));
	    
	    boolean hdpUrlDecode = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("hdpUrlDecode"))));
	    boolean hdpUrlEncode = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("hdpUrlEncode"))));
	    boolean hdpUploadTarget = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(mapItem.get("hdpUploadTarget"))));

		List<ApiParameter> children = new ArrayList<ApiParameter>();
		ApiParameter parent = null;
		int depth = 0;

		apiParameter.setType(type);
		apiParameter.setName(name);
		apiParameter.setDescription(description);
		apiParameter.setRequired(required);
		apiParameter.setPersonalData(personalData);
		apiParameter.setHidden(hidden);
		apiParameter.setDoNotSend(doNotSend);
		apiParameter.setFixedValue(fixedValue);
		apiParameter.setMappingKey(mappingKey);
		apiParameter.setUrlDecode(hdpUrlDecode);
		apiParameter.setUrlEncode(hdpUrlEncode);
		apiParameter.setUploadTarget(hdpUploadTarget);

		apiParameter.setChildren(children);
		apiParameter.setParent(parent);
		apiParameter.setDepth(depth);
		
		ParamNode paramNode = new ParamNode(paramNo, prntsParamNo, apiParameter);
		
		return paramNode;
	}

	/**
	 * API정보를 apigw ApiEntity에 설정한다
	 */
	public static AdptranApiResultCode set_ApiInfo_To_ApiEntity(String req_gw_profile, AdptranApiVO mapOut, List<AdptranParamVO> listOut, ApiEntity apiEntity) {
		LOGGER.debug("###[adpt][AdptranUtil.set_ApiInfo_To_ApiEntity()][req_gw_profile: {}][mapOut: {}][listOut: {}]", req_gw_profile, mapOut, listOut);

		AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

		if (mapOut == null) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_DEF.getMessage());
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_DEF;
			return resultCode;
		}
		if (listOut == null || listOut.size() == 0) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_PARAM.getMessage());
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_QUERY_API_PARAM;
			return resultCode;
		}
		
		//-- DB value {
		String apiNm = KsmUtil.fnSafeStr(mapOut.getApiNm());
		String apiPath = KsmUtil.fnSafeStr(mapOut.getApiPath());
		String apiId = KsmUtil.fnSafeStr(mapOut.getApiId());
		String system = KsmUtil.fnSafeStr(mapOut.getSysIdNm());
		String apiHandlerCdNm = KsmUtil.fnSafeStr(mapOut.getApiHandlerCdNm());
		String methodCdNm = KsmUtil.fnSafeStr(mapOut.getMethodCdNm());
		String endpntMethodCdNm = KsmUtil.fnSafeStr(mapOut.getEndpntMethodCdNm());
		String endpntTimeout = KsmUtil.fnSafeStr(mapOut.getEndpntTimeout());
		String endpntTbUrl = KsmUtil.fnSafeStr(mapOut.getEndpntTbUrl());
		String endpntPrdUrl = KsmUtil.fnSafeStr(mapOut.getEndpntPrdUrl());
		String endpntClientIp = KsmUtil.fnSafeStr(mapOut.getEndpntClientIp());
		String resmapResCdField = KsmUtil.fnSafeStr(mapOut.getResmapResCdField());
		String resmapSuccVal = KsmUtil.fnSafeStr(mapOut.getResmapSuccVal());
		String[] a_resmapSuccVal = resmapSuccVal.split(",");
		String resmapErrCdField = KsmUtil.fnSafeStr(mapOut.getResmapErrCdField());
		String resmapErrMsgField = KsmUtil.fnSafeStr(mapOut.getResmapErrMsgField());
		String version = KsmUtil.fmt_data(apiPath, "fmt_version_in_path");

		String hdpApiOutFormat = KsmUtil.fnSafeStr(mapOut.getHdpApiOutFormat());
		String hdpApiOutCommonParam = KsmUtil.fnSafeStr(mapOut.getHdpApiOutCommonParam());
		String hdpApiEndpointId = KsmUtil.fnSafeStr(mapOut.getHdpApiEndpointId());
		String hdpReqApiName = KsmUtil.fnSafeStr(mapOut.getHdpReqApiName());
		String hdpReqConfigToBody = KsmUtil.fnSafeStr(mapOut.getHdpReqConfigToBody());
		String hdpReqHeaderToBody = KsmUtil.fnSafeStr(mapOut.getHdpReqHeaderToBody());
		String hdpReqMappingToBody = KsmUtil.fnSafeStr(mapOut.getHdpReqMappingToBody());
		String hdpReqUrlDecode = KsmUtil.fnSafeStr(mapOut.getHdpReqUrlDecode());
		String hdpReqUrlEncode = KsmUtil.fnSafeStr(mapOut.getHdpReqUrlEncode());
		String hdpResMappingToBody = KsmUtil.fnSafeStr(mapOut.getHdpResMappingToBody());
		String hdpResProvideParam = KsmUtil.fnSafeStr(mapOut.getHdpResProvideParam());
		String hdpResUrlEncode = KsmUtil.fnSafeStr(mapOut.getHdpResUrlEncode());

		String hdpExtProp = KsmUtil.fnSafeStr(mapOut.getHdpExtProp());
		String hdpExtPropIsBiznaru = KsmUtil.fnGetExtProp("is_biznaru", hdpExtProp);

		String hdpHndlroptnConfig = KsmUtil.fnSafeStr(mapOut.getHdpHndlroptnConfig());

        String bstGwYn = KsmUtil.fnSafeStr(mapOut.getSpcBstgwYn());
        String bstgwTbSysId = KsmUtil.fnSafeStr(mapOut.getBstgwTbSysId());
        String bstgwPrdSysId = KsmUtil.fnSafeStr(mapOut.getBstgwPrdSysId());
		//-- DB value }

		//-- for parameter {
		List<ParamNode> listRequestHeader = new ArrayList<>();
		List<ParamNode> listRequestBody = new ArrayList<>();
		List<ParamNode> listResponseHeader = new ArrayList<>();
		List<ParamNode> listResponseBody = new ArrayList<>();

		List<Integer> listExcludeParam = new ArrayList<>();
		//-- for parameter }

		//-- ApiEntity {
		HandlerType handlerType = AdptranUtil.getApigwHandlerType(apiHandlerCdNm);
		if (handlerType == null) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][apiHandlerCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_HANDLER_TYPE.getMessage(), apiHandlerCdNm);
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_HANDLER_TYPE;
			return resultCode;
		}
		HttpMethod method = AdptranUtil.getApigwHttpMethod(methodCdNm);
		if (method == null) {
			LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][methodCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_METHOD.getMessage(), methodCdNm);
			resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_METHOD;
			return resultCode;
		}
		apiEntity.setId(apiNm);	//-- notempty	apiId -> apiNm변경
		apiEntity.setTimeout(KsmUtil.parseInt(endpntTimeout, 30000));
		apiEntity.setUrl(apiPath);		//-- notempty
		apiEntity.setHandler(handlerType);	//-- notempty
		apiEntity.setMethod(method);	//-- notempty

        //-- [tag:PRJ-20220901] {
        apiEntity.setBstgwDplyType(BstgwConstant.DPLY_TYPE.DPLY);
        if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
            apiEntity.setBstgwSysId(bstgwPrdSysId);
        }
        else {
            apiEntity.setBstgwSysId(bstgwTbSysId);
        }
        //-- [tag:PRJ-20220901] }
        
        //-- [tag:SR-20230113][add]
		if (true == "Y".equalsIgnoreCase(bstGwYn)) {
			//-- [i]empty일시 기본값 {}설정
			hdpHndlroptnConfig = ((hdpHndlroptnConfig.length() == 0) ? "{}" : hdpHndlroptnConfig);
			apiEntity.setHdpHndlroptnConfig(hdpHndlroptnConfig);
		}

		//-- endpoint {
		if ((handlerType != HandlerType.ADP_SCAP_COMMON) && (handlerType != HandlerType.ADP_CAPRI_COMMON)) {
			HttpMethod endpntMethod = AdptranUtil.getApigwHttpMethod(endpntMethodCdNm);
			if (endpntMethod == null) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][endpntMethodCdNm: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_ENDPNT_METHOD.getMessage(), endpntMethodCdNm);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_ENDPNT_METHOD;
				return resultCode;
			}
		
			EndpointConfig endpoint = new EndpointConfig();
			String endpntUrl = "";
			if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
				endpntUrl = endpntPrdUrl;
			}
			else {
				endpntUrl = endpntTbUrl;
			}

			if (true == "Y".equalsIgnoreCase(bstGwYn)) {
				//-- path만 추출
				endpntUrl = KsmUtil.fmt_parseUrl(endpntUrl, "path");
				if (endpntUrl.length() == 0) {
					LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][endpntUrl(path): {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_URL_FORMAT.getMessage(), endpntUrl);
					resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_URL_FORMAT;
					return resultCode;
				}
			}
			else {
				if (handlerType != HandlerType.ADP_SB_COMMON) {
					if (KsmUtil.isValudUrl(endpntUrl) == false) {
						LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][endpntUrl: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_URL_FORMAT.getMessage(), endpntUrl);
						resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_URL_FORMAT;
						return resultCode;
					}
				}
			}

			URLScheme protocol = null;
			protocol = ((endpntUrl.toLowerCase().indexOf("http") == 0) ? URLScheme.HTTP : protocol);
			protocol = ((endpntUrl.toLowerCase().indexOf("https") == 0) ? URLScheme.HTTPS : protocol);

			system = ((handlerType == HandlerType.ADP_SB_COMMON) ? apiHandlerCdNm : system);
	
			endpoint.setMethod(endpntMethod);
			endpoint.setSystem(system);
			endpoint.setProtocol(protocol);
			endpoint.setUrl(endpntUrl);
			apiEntity.setEndpoint(endpoint);
		}
		//-- endpoint }

		//-- optional user-input parameter {
		if ((handlerType == HandlerType.ADP_JSON_COMMON) || (handlerType == HandlerType.ANY_JSON_COMMON)) {
			apiEntity.setClientIpMappingKey(endpntClientIp);
		}
		if (handlerType == HandlerType.ANY_JSON_COMMON) {
			ApiResultMapping resultMapping = new ApiResultMapping();
			resultMapping.setResultCode(resmapResCdField);
			resultMapping.setSuccessValue(Arrays.asList(a_resmapSuccVal));
			resultMapping.setErrorCode(resmapErrCdField);
			resultMapping.setErrorMessage(resmapErrMsgField);
			apiEntity.setResultMapping(resultMapping);
		}
		if (handlerType == HandlerType.ADP_SCAP_COMMON) {
			apiEntity.setOutCommonParam(hdpApiOutCommonParam);
			apiEntity.setEndpointId(hdpApiEndpointId);
			apiEntity.setReqApiName(hdpReqApiName);
		}
		if (handlerType == HandlerType.ADP_CAPRI_COMMON) {
			apiEntity.setReqUrlEncodeCharset(hdpReqUrlEncode);
		}
		if (handlerType == HandlerType.ADP_SB_COMMON) {
			apiEntity.setOutFormat(hdpApiOutFormat);
			apiEntity.setOutCommonParam(hdpApiOutCommonParam);
			apiEntity.setReqApiName(hdpReqApiName);
		}
		if ((handlerType == HandlerType.ADP_SCAP_COMMON) || (handlerType == HandlerType.ADP_CAPRI_COMMON) || (handlerType == HandlerType.ADP_SB_COMMON)) {
			if (false == apiEntity.setReqConfigToBody(hdpReqConfigToBody)) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][ReqConfigToBody: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_CONFIG_TO_BODY.getMessage(), hdpReqConfigToBody);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_CONFIG_TO_BODY;
				return resultCode;
			}
			if (false == apiEntity.setReqHeaderToBody(hdpReqHeaderToBody)) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][ReqHeaderToBody: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_HEADER_TO_BODY.getMessage(), hdpReqHeaderToBody);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_HEADER_TO_BODY;
				return resultCode;
			}
			if (false == apiEntity.setReqMappingToBody(hdpReqMappingToBody)) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][ReqMappingToBody: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_MAPPING_TO_BODY.getMessage(), hdpReqMappingToBody);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_REQ_MAPPING_TO_BODY;
				return resultCode;
			}
			apiEntity.setReqUrlDecodeCharset(hdpReqUrlDecode);
			if (false == apiEntity.setResMappingToBody(hdpResMappingToBody)) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][ResMappingToBody: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_RES_MAPPING_TO_BODY.getMessage(), hdpResMappingToBody);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_RES_MAPPING_TO_BODY;
				return resultCode;
			}
			if (false == apiEntity.setResProvideParam(hdpResProvideParam)) {
				LOGGER.error("\n\n### [AdptranUtil.set_ApiInfo_To_ApiEntity(): {}][ResProvideParam: {}] ###\n", AdptranApiResultCode.RC_SET_APIENTITY_ERR_RES_PROVIDE_PARAM.getMessage(), hdpResProvideParam);
				resultCode = AdptranApiResultCode.RC_SET_APIENTITY_ERR_RES_PROVIDE_PARAM;
				return resultCode;
			}
			apiEntity.setResUrlEncodeCharset(hdpResUrlEncode);
		}
		//-- optional user-input parameter }

		apiEntity.setApiNo(apiId);
		apiEntity.setVersion(version);

		for (int n_ii = 0; n_ii < listOut.size();  n_ii++) {
			AdptranParamVO voItem = listOut.get(n_ii);

			String paramNm = KsmUtil.fnSafeStr(voItem.getParamNm());
			String paramTypeCd = KsmUtil.fnSafeStr(voItem.getParamTypeCd());
			String paramLoc = KsmUtil.fnSafeStr(voItem.getParamLoc());
			String dataTypeCdNm = KsmUtil.fnSafeStr(voItem.getDataTypeCdNm());
			int prntsParamNo = KsmUtil.parseInt(voItem.getPrntsParamNo(), 0);
			int paramNo = KsmUtil.parseInt(voItem.getParamNo(), 0);

			String paramGub = "";
			paramGub = ((("PRMTYP1010".equals(paramTypeCd) == true) && ("header".equals(paramLoc) == true)) ? "req_header" : paramGub); 
			paramGub = ((("PRMTYP1010".equals(paramTypeCd) == true) && ("body".equals(paramLoc) == true)) ? "req_body" : paramGub); 
			paramGub = ((("PRMTYP1020".equals(paramTypeCd) == true) && ("header".equals(paramLoc) == true)) ? "res_header" : paramGub); 
			paramGub = ((("PRMTYP1020".equals(paramTypeCd) == true) && ("body".equals(paramLoc) == true)) ? "res_body" : paramGub);

			if (("req_body".equals(paramGub) == true) || ("res_body".equals(paramGub) == true)) {
				if (prntsParamNo == 0) {
					listExcludeParam.add(paramNo);
					paramGub = paramGub + "_skip";
				}
				voItem.setPrntsParamNo(String.valueOf((listExcludeParam.contains(prntsParamNo) == true) ? 0 : prntsParamNo));
				
				if (("Array".equalsIgnoreCase(dataTypeCdNm) == true) && ((n_ii + 1) < listOut.size())) {
					AdptranParamVO voItem_next = listOut.get(n_ii + 1);
					String paramNm_next = KsmUtil.fnSafeStr(voItem_next.getParamNm());
					int prntsParamNo_next = KsmUtil.parseInt(voItem_next.getPrntsParamNo(), 0);
					if ((prntsParamNo_next == paramNo) && (paramNm_next.length() > 0) && (paramNm_next.equals(paramNm) == true)) {
						String dataTypeCdNm_next = KsmUtil.fnSafeStr(voItem_next.getDataTypeCdNm());
						if (";Object;Array;".indexOf(";" + dataTypeCdNm_next + ";") != -1) {
							voItem.setParamNo(String.valueOf(KsmUtil.parseInt(voItem_next.getParamNo(), 0)));
							n_ii++;
						}
						else if (";String;Number;Integer;Boolean;".indexOf(";" + dataTypeCdNm_next + ";") != -1) {
							n_ii++;
						} 
					}
				}
			}
			if ("req_header".equals(paramGub) == true) {
				listRequestHeader.add(AdptranUtil.getParamNode(voItem));
			}
			else if ("req_body".equals(paramGub) == true) {
				listRequestBody.add(AdptranUtil.getParamNode(voItem));
			}
			else if ("res_header".equals(paramGub) == true) {
				listResponseHeader.add(AdptranUtil.getParamNode(voItem));
			}
			else if ("res_body".equals(paramGub) == true) {
				listResponseBody.add(AdptranUtil.getParamNode(voItem));
			}
			LOGGER.debug("###[adpt][AdptranUtil.set_ApiInfo_To_ApiEntity()][paramGub: {}][voItem: {}]", paramGub, voItem);
		}

		ApiRequestEntity request = new ApiRequestEntity();
		ApiResponseEntity response = new ApiResponseEntity();
		
		AdptranUtil.setApiParameterHierarchy(listRequestHeader, request.getHeaders());
		AdptranUtil.setApiParameterHierarchy(listRequestBody, request.getParameters());
		AdptranUtil.setApiParameterHierarchy(listResponseBody, response.getParameters());
		
		if (handlerType == HandlerType.ADP_JSON_COMMON) {
			if (true == "Y".equalsIgnoreCase(hdpExtPropIsBiznaru)) {
				ApiParameter apiParameter = new ApiParameter();
				apiParameter.setName("Authorization");
				apiParameter.setFixedValue(GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) ? "QUlJNTEyMDA0MTMyNUtXQVFPUzpTVks1MTIwMDQxMzI1VFlGT1JT" : "QUlJMjAwMDAzNjA4OVBQSUhURDpUQksyMDAwMDM2MDg5T05JWldE");
				apiParameter.setHidden(true);
				request.getHeaders().add(apiParameter);
			}
		}
		
		apiEntity.setRequest(request);
		apiEntity.setResponse(response);
		
		resultCode = AdptranApiResultCode.RC_SET_APIENTITY_SUCC;

		return resultCode;
	}
	
	public static ParamNode getParamNode(AdptranParamVO voItem) {
		int paramNo = Integer.parseInt(KsmUtil.fnSafeStr(voItem.getParamNo()));
		int prntsParamNo = Integer.parseInt(KsmUtil.fnSafeStr(voItem.getPrntsParamNo()));
		ApiParameter apiParameter = new ApiParameter();

	    ApiDataType type = AdptranUtil.getApiDataType(KsmUtil.fnSafeStr(voItem.getDataTypeCdNm()));
	    String name = KsmUtil.fnSafeStr(voItem.getParamNm());
	    String description = KsmUtil.fnSafeStr(voItem.getParamDesc());
	    boolean required = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getRequired())));
	    boolean personalData = (KsmUtil.fnSafeStr(voItem.getPersonalData()).length() > 0);
	    boolean hidden = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getHidden())));
	    boolean doNotSend = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getDoNotSend())));
	    String fixedValue = KsmUtil.fnSafeStr(voItem.getFixedValue());
	    String mappingKey = KsmUtil.fnSafeStr(voItem.getMappingKey());
	    
	    boolean hdpUrlDecode = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getHdpUrlDecode())));
	    boolean hdpUrlEncode = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getHdpUrlEncode())));
	    boolean hdpUploadTarget = ("Y".equalsIgnoreCase(KsmUtil.fnSafeStr(voItem.getHdpUploadTarget())));

		List<ApiParameter> children = new ArrayList<ApiParameter>();
		ApiParameter parent = null;
		int depth = 0;

		apiParameter.setType(type);
		apiParameter.setName(name);
		apiParameter.setDescription(description);
		apiParameter.setRequired(required);
		apiParameter.setPersonalData(personalData);
		apiParameter.setHidden(hidden);
		apiParameter.setDoNotSend(doNotSend);
		apiParameter.setFixedValue(fixedValue);
		apiParameter.setMappingKey(mappingKey);
		apiParameter.setUrlDecode(hdpUrlDecode);
		apiParameter.setUrlEncode(hdpUrlEncode);
		apiParameter.setUploadTarget(hdpUploadTarget);

		apiParameter.setChildren(children);
		apiParameter.setParent(parent);
		apiParameter.setDepth(depth);
		
		ParamNode paramNode = new ParamNode(paramNo, prntsParamNo, apiParameter);
		
		return paramNode;
	}

	public static void setApiParameterHierarchy(List<ParamNode> listParamNode, List<ApiParameter> listApiParameter) {
		listApiParameter.clear();	//-- 초기화
	
		//--@@int paramNo, 
		int prntsParamNo;
		ApiParameter apiParameter;

		int depth;
		ParamNode parentNode;

		/*--##
		ListIterator<ParamNode> li = listParamNode.listIterator(listParamNode.size());
		while(li.hasPrevious()) {
			ParamNode paramNode = (ParamNode) li.previous();
		}
		--*/
		for (int n_ii = listParamNode.size() - 1; n_ii >= 0; n_ii--) {
			ParamNode paramNode = listParamNode.get(n_ii);

			//--@@paramNo = paramNode.getParamNo();
			prntsParamNo = paramNode.getPrntsParamNo();
			apiParameter = paramNode.getApiParameter();
			depth = 0;
			if (prntsParamNo == 0) {
				apiParameter.setDepth(depth);	//-- set depth
				listApiParameter.add(apiParameter);	//-- add apiParameter
				listParamNode.remove(paramNode);	//--[?]
				continue;
			}
			depth++;
			parentNode = null;
			for (ParamNode sub_paramNode: listParamNode) {
				int sub_paramNo = sub_paramNode.getParamNo();
				if (sub_paramNo == prntsParamNo) {
					parentNode = sub_paramNode;
					break;
				}
			}
			if (parentNode == null) {
				continue;
			}

			apiParameter.setParent(parentNode.getApiParameter());	//-- set parent

			//--[i]get depth
			int trace_prntsParamNo = parentNode.getPrntsParamNo();
			while (trace_prntsParamNo != 0) {
				depth++;
				ParamNode trace_paramNode = null;
				for (ParamNode sub_paramNode: listParamNode) {
					int sub_paramNo = sub_paramNode.getParamNo();
					if (sub_paramNo == trace_prntsParamNo) {
						trace_paramNode = sub_paramNode;
						break;
					}
				}
				trace_prntsParamNo = 0;
				if (trace_paramNode != null) {
					trace_prntsParamNo = trace_paramNode.getPrntsParamNo();
				}
			}
			apiParameter.setDepth(depth);	//-- set depth
			
			ApiParameter parent_apiParameter = parentNode.getApiParameter();
			List<ApiParameter> children = parent_apiParameter.getChildren();
			/*--[dep][init when new ApiParameter()]
			if (children == null) {
				children = new ArrayList<ApiParameter>();
				parent_apiParameter.setChildren(children);
			}
			--*/
			children.add(apiParameter);

			listParamNode.remove(paramNode);
		}
		
		//--[i][for breakpoint]
		return;
		/*--
		for (ParamNode paramNode: listParamNode) {
			paramNo = paramNode.getParamNo();
			prntsParamNo = paramNode.getPrntsParamNo();
			apiParameter = paramNode.getApiParameter();
			depth = 0;
			if (prntsParamNo != 0) {
				parentNode = null;
				for (ParamNode sub_paramNode: listParamNode) {
					int sub_paramNo = sub_paramNode.getParamNo();
					if (sub_paramNo == prntsParamNo) {
						parentNode = sub_paramNode;
						break;
					}
				}
				if (parentNode != null) {
					apiParameter.setParent(parentNode.getApiParameter());	//-- set parent
					int sub_prntsParamNo = parentNode.getPrntsParamNo();
					while (sub_prntsParamNo != 0) {
						depth++;
						parentNode = null;
						for (ParamNode sub_paramNode: listParamNode) {
							int sub_paramNo = sub_paramNode.getParamNo();
							if (sub_paramNo == sub_prntsParamNo) {
								parentNode = sub_paramNode;
								break;
							}
						}
						sub_prntsParamNo = 0;
						if (parentNode != null) {
							sub_prntsParamNo = parentNode.getPrntsParamNo();
						}
					};
				}
			}
			apiParameter.setDepth(depth);	//-- set depth
			List<ApiParameter> children = new ArrayList<ApiParameter>();
			for (ParamNode sub_paramNode: listParamNode) {
				int sub_prntsParamNo = sub_paramNode.getPrntsParamNo();
				if (sub_prntsParamNo == paramNo) {
					children.add(sub_paramNode.getApiParameter());
				}
			}
			if (children.size() > 0) {
				apiParameter.setChildren(children);	//-- set children
			}
			listApiParameter.add(apiParameter);	//-- add apiParameter
		}
		--*/
	}
	
	//-- parent, children, depth를 구하기 위한 자료구조
	private static class ParamNode {
		private int paramNo;
		private int prntsParamNo;
		private ApiParameter apiParameter;

		public ParamNode(int paramNo, int prntsParamNo, ApiParameter apiParameter) {
			this.paramNo = paramNo;
			this.prntsParamNo = prntsParamNo;
			this.apiParameter = apiParameter;
		}

		public int getParamNo() { return paramNo; }
		public int getPrntsParamNo() { return prntsParamNo; }
		public ApiParameter getApiParameter() { return apiParameter; }
	}
	
	//-- for apigw }

	//-- for adptran util {
	public static String get_AssertField_Value(String assert_field, String json_response) {
		String assertfield_value = null;

		String[] a_assert_field = assert_field.split("\\.");
		String jsonPtrExpr = String.join("/", a_assert_field);
		if (jsonPtrExpr.length() == 0) {
			return assertfield_value;
		}
		jsonPtrExpr = "/" + jsonPtrExpr; 

		ObjectMapper mapper = new ObjectMapper();
		//--@@JsonNode jsonNode = mapper.createObjectNode();
		JsonNode jsonNode = null;
		JsonNode jsonNode_assert_field = null;
		try {
			jsonNode = mapper.readTree(json_response);
		} catch (IOException e) {
			LOGGER.debug("###[adpt][AdptranUtil.get_AssertField_Value()][mapper.readTree()][IOException][json_response: {}]", json_response);
		}
		if (jsonNode != null) {
			try {
				jsonNode_assert_field = jsonNode.at(jsonPtrExpr);
			} catch(Exception e) {
				LOGGER.debug("###[adpt][AdptranUtil.get_AssertField_Value()][jsonNode.at()][Exception][jsonPtrExpr: {}]", jsonPtrExpr);
			}
			if (jsonNode_assert_field != null) {
				if (jsonNode_assert_field.isValueNode() == true) {
					assertfield_value = jsonNode_assert_field.asText();
				}
				else {
					assertfield_value = jsonNode_assert_field.toString();
				}
			}
		}
		return assertfield_value;
	}
	
	public static int computeAssert(String left_value, String operator, String right_value) {
		int n_assert_result = 0;	//-- 0:not avail, 1:ok, -1:not ok
		
		//-- validation operator
		if (";==;!=;<;>;<=;>=;".indexOf(";" + operator + ';') == -1) {
			return n_assert_result;
		}

		//-- try change numeric
		int n_left_value = KsmUtil.parseInt(left_value, Integer.MAX_VALUE);
		int n_right_value = KsmUtil.parseInt(right_value, Integer.MAX_VALUE);
		if ((n_right_value != Integer.MAX_VALUE) && (n_right_value != Integer.MAX_VALUE)) {
			if ("==".equals(operator) == true) {
				n_assert_result = ((n_left_value == n_right_value) ? 1 : -1);
			}
			else if ("!=".equals(operator) == true) {
				n_assert_result = ((n_left_value != n_right_value) ? 1 : -1);
			}
			else if ("<".equals(operator) == true) {
				n_assert_result = ((n_left_value < n_right_value) ? 1 : -1);
			}
			else if (">".equals(operator) == true) {
				n_assert_result = ((n_left_value > n_right_value) ? 1 : -1);
			}
			else if ("<=".equals(operator) == true) {
				n_assert_result = ((n_left_value <= n_right_value) ? 1 : -1);
			}
			else if (">=".equals(operator) == true) {
				n_assert_result = ((n_left_value >= n_right_value) ? 1 : -1);
			}
		}
		else {
			// compareTo 0:==, 1:>, -1:<
			if ("==".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) == 0) ? 1 : -1);
			}
			else if ("!=".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) != 0) ? 1 : -1);
			}
			else if ("<".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) == -1) ? 1 : -1);
			}
			else if (">".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) == 1) ? 1 : -1);
			}
			else if ("<=".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) != 1) ? 1 : -1);
			}
			else if (">=".equals(operator) == true) {
				n_assert_result = ((left_value.compareTo(right_value) != -1) ? 1 : -1);
			}
		}
		return n_assert_result;
	}
	//-- for adptran util }
}
