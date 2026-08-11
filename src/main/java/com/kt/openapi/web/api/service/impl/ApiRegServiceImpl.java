package com.kt.openapi.web.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.adptran.dao.BeastDAO;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.adptran.vo.BstApiDeployVO;
import com.kt.openapi.web.adptran.vo.BstApiStatusCountVO;
import com.kt.openapi.web.adptran.vo.BstApiTrafficVO;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.*;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.beast.apigw.constant.BstgwConstant;
import com.kt.openapi.web.beast.service.BeastService;
import com.kt.openapi.web.cmm.upload.FileUploadInfo;
import com.kt.openapi.web.cmm.upload.RunCmdUtil;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.cmm.upload.WebFileHelper;
import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.JsonToYaml;
import com.kt.openapi.web.util.YamlToJava;
import io.swagger.util.Yaml;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.service.impl
 * 2. 타입명   : ApiRegServiceImpl.java
 * 3. 작성일   : 2017. 12. 23. 오후 8:17:20
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     :
 * </pre>
 */
@Service("apiRegService")
public class ApiRegServiceImpl implements ApiRegService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiRegServiceImpl.class);

	@Autowired
	private ApiRegDAO apiRegDAO;

	@Autowired
	private YamlToJava YamlToJava;

	@Autowired
	private UploadFileUtils uploadFileUtils;

	@Autowired
	private RunCmdUtil runCmdUtil;

	@Value("${yaml.file.path}")
	private String yamlFilePath;

	@Value("${yamlServer.host}")
	private String yamlServerHost;

	@Autowired
	private ApiMainService apiMainService;

	@Autowired
	private UploadFileUtils uploadFileUtiles;

	@Value("${apidocServer.host}")
	private String apidocServerHost;

	@Value("${upload.output.path}")
	private String outFilePath;

	@Value("${wsdlServer.host}")
	private String wsdlServerHost;

	@Value("${wsdlServer.new.host}")
	private String wsdlServerNewHost;

	@Value("${wsdlImport.yaml.fileNm}")
	private String wsdlImportYamlFileNm;

	@Value("${apidocImport.json.fileNm}")
	private String apidocImportJsonFileNm;

	@Value("${apiImport.loop.cnt}")
	private String apiImportLoopCnt;

	@Value("${apiImport.sleep.cnt}")
	private String apiImportSleepCnt;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	//-- [tag:PRJ-20220901] {
	@Autowired
	private BeastDAO beastDAO;
	
	@Autowired
	private BeastService beastService;
	//-- [tag:PRJ-20220901] }

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public HashMap<String, Object> savApiRegBasic(ApiRegVO vo) throws Exception {

		LOGGER.debug("ApiRegServiceImpl savApiRegBasic START =============================");
		HashMap<String, Object> returnHMap = new HashMap<String, Object>();
		String successStr = "ins";

		// ApiRegVO apiRegVO = new apiRegVO();
		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlStr(repYamlStr);

		// 데이터 읽기
		ApiRegVO apiRegVO = (ApiRegVO) YamlToJava.getYamlToInfoData(vo.getYamlStr());

		// 테스트 데이터
		apiRegVO.setYamlSbst(repYamlStr);
		apiRegVO.setRfrnWsdlUrl(KsmUtil.fnSafeStr(vo.getRfrnWsdlUrl())); // 참고WSDL_URL
		apiRegVO.setRfrnTmpltNo(KsmUtil.fnSafeStr(vo.getRfrnTmpltNo())); // 참고템플릿 번호
		apiRegVO.setRfrnApiSpcNo(KsmUtil.fnSafeStr(vo.getRfrnApiSpcNo())); // 참고API명세번호
		apiRegVO.setRegSttusCd("APIREG1010"); // 등록상태
		apiRegVO.setTmpltYn("N"); // 템플릿 여부
		apiRegVO.setAutId(KsmUtil.fnSafeStr(vo.getAutId()));
		apiRegVO.setSysId(KsmUtil.fnSafeStr(vo.getSysId()));
		//-- [tag:adpt][drm][add] {
		apiRegVO.setApiClass(KsmUtil.fnSafeStr(vo.getApiClass()));
		//-- [tag:adpt][drm][add] }

		if (false == "".equals(apiRegVO.getRfrnTmpltNo())) {
			apiRegVO.setTmpltYn("Y");
		}

		apiRegVO.setRegr(vo.getRegr());
		apiRegVO.setAmdr(vo.getRegr());
		apiRegVO.setApiSpcNo(vo.getApiSpcNo());
		
		//-- [tag:PRJ-20220901]
		apiRegVO.setBstgwYn(vo.getBstgwYn());
		apiRegVO.setApiVeriBaseurl(vo.getApiVeriBaseurl());

		// SPC 테이블에 정상등록 되었다면
		/* SYS_ID가 아스날 일때만 등록 추가
		 * CYD - 2020.07.09
		 */
		if(true == "ARSENAL".equalsIgnoreCase(vo.getSysId())) {
			// 네임스페이스 저장
//			EgovMap projectMap = new EgovMap();
//			projectMap.put("apiSpcNo" , apiSpcNo);
//			projectMap.put("projectNS", vo.getProjectNamespace());
//			projectMap.put("projectNM", apiRegVO.getApiNm());
//			projectMap.put("regr"	  , vo.getRegr());
//			LOGGER.debug("Insert Arsenal Namespace = {}", projectMap);
			//apiRegDAO.savArsenalApiNS(projectMap);
			apiRegVO.setProjectNamespace(vo.getProjectNamespace());
			LOGGER.debug("Insert Arsenal Namespace = {}", vo.getProjectNamespace());
		}

		String apiSpcNo = KsmUtil.fnSafeStr(vo.getApiSpcNo());

		//-- [i][using ins]
		String apiCtgryNo = vo.getApiCtgryNo();
		String ctgryNm = KsmUtil.fnSafeStr(vo.getCtgryNm());
		ctgryNm = ((ctgryNm.length() == 0) ? "기본" : ctgryNm); 

		LOGGER.debug("savApiRegInfo apiSpcNo = {}", apiSpcNo);

		String filePath = uploadFileUtils.calcPath(yamlServerHost + yamlFilePath + File.separator).replace(File.separatorChar, '/');
		
		String fileName = apiSpcNo + "";

		LOGGER.debug("savApiRegInfo filePath = {}", filePath);
		LOGGER.debug("savApiRegInfo fileName = {}", fileName);
		LOGGER.debug("SysID = {}", vo.getSysId());
		if ("".equals(apiSpcNo)) {
			apiRegVO.setYamlFilePath(filePath);
			apiRegDAO.savApiRegInfo(apiRegVO);
			apiSpcNo = apiRegVO.getApiSpcNo();
			fileName = apiSpcNo;
			successStr = "ins";
			
			if ((false == "".equals(apiRegVO.getRfrnTmpltNo()))
					|| (false == "".equals(apiRegVO.getRfrnWsdlUrl()))
					|| (false == "".equals(apiRegVO.getRfrnApiSpcNo()))) {
				ApiMainVo apiMainVo = new ApiMainVo();
				if (false == "".equals(apiRegVO.getRfrnTmpltNo())) {
					apiMainVo.setApiSpcNo(apiRegVO.getRfrnTmpltNo());
				} else if (false == "".equals(apiRegVO.getRfrnWsdlUrl())) {
					apiMainVo.setApiSpcNo(apiSpcNo);
				} else if (false == "".equals(apiRegVO.getRfrnApiSpcNo())) {
					apiMainVo.setApiSpcNo(apiRegVO.getRfrnApiSpcNo());
				}
				apiMainVo.setNewApiSpcNo(apiSpcNo);
				apiMainVo.setRegr(vo.getRegr());
				apiMainVo.setAmdr(vo.getRegr());
				apiMainService.insertApiVerUpDep(apiMainVo);
			}
			else {
				apiRegVO.setApiSpcNo(apiSpcNo);
				apiRegVO.setCtgryNm(ctgryNm);
				apiRegVO.setSortOdrg("1");
				apiRegDAO.savApiCateInfo(apiRegVO);
				apiCtgryNo = apiRegVO.getApiCtgryNo();
			}
		}
		else {
			ApiYamlVO fileInfo = apiRegDAO.selApiFileInfo(apiRegVO);

			filePath = fileInfo.getYamlFilePath();
			fileName = fileInfo.getYamlFileNm();

			apiRegDAO.updApiRegInfo(apiRegVO);
			
			// 네임스페이스 테이블의 네임스페이스명 수정
			/* SYS_ID가 아스날 일때만 등록 수정
			 * CYD - 2020.07.09
			 */
//			if(vo.getSysId().equalsIgnoreCase("ARSENAL") == true) {
//				HashMap<String, Object> projectMap = new HashMap<String, Object>();
//				projectMap.put("apiSpcNo" , apiSpcNo);
//				projectMap.put("projectNS", vo.getProjectNamespace());
//				projectMap.put("editr"	  , vo.getRegr());
//				LOGGER.debug("Update Arsenal Namespace = {}", projectMap);
//				apiRegDAO.updArsenalApiNS(projectMap);
//				LOGGER.debug("Update Arsenal Namespace = {}", projectMap);
//			}
			successStr = "upd";
		}

		LOGGER.debug("savApiRegInfo filePath = {}", filePath);
		LOGGER.debug("savApiRegInfo fileName = {}", fileName);

		returnHMap.put("apiSpcNo", apiSpcNo);
		returnHMap.put("yamlStr", vo.getYamlStr());
		returnHMap.put("successStr", successStr);
		returnHMap.put("filePath", filePath);
		
		if (true == "ins".equals(successStr)) {
			//-- [신규일경우]
			returnHMap.put("apiCtgryNo", apiCtgryNo);
			returnHMap.put("ctgryNm", ctgryNm);
		}

		// 이력 저장
		apiRegVO.setApiSttusCd("APIREG1010");
		updApiHisInfo(apiRegVO);

		return returnHMap;
	}

	
	@Override
	public int salApiDupCheck(ApiRegVO vo) throws Exception {
		int returnValue = 0;

		String apiCheckType = vo.getApiCheckType();

		if (apiCheckType.equals("1")) { // API 명
			returnValue = (int) apiRegDAO.salApiNmDupCheck(vo);
		} else if (apiCheckType.equals("2")) { // PATH 명
			returnValue = (int) apiRegDAO.salApiPathDupCheck(vo);
		} else if (apiCheckType.equals("3")) { // PATH의 API명
			returnValue = (int) apiRegDAO.salApiPathNmDupCheck(vo);
		} else if (apiCheckType.equals("4")) {
			returnValue = (int) apiRegDAO.salApiNmDupCheck(vo);
		}

		return returnValue;
	}
	
	@Override
	public int salApiIdCheck(ApiRegVO vo) throws Exception {
		int returnValue = 0;

		returnValue = (int) apiRegDAO.salApiIdCheck(vo);

		return returnValue;
	}
	
	@Override
	public String selApiCateNmDupCheck(ApiRegVO vo) throws Exception {
		String duplYn = "N";

		int checkInt = (int) apiRegDAO.selApiCateNmDupCheck(vo);

		if (checkInt > 0) {
			duplYn = "Y";
		}

		return duplYn;
	}
	
	@Override
	public String selApiNmDupCheck(ApiRegVO vo) throws Exception {
		String duplYn = "N";

		int checkInt = (int) apiRegDAO.selApiNmDupCheck(vo);

		if (checkInt > 0) {
			duplYn = "Y";
		}

		return duplYn;
	}
	
	@Override
	public String selApiInfoNmDupCheck(ApiRegVO vo) throws Exception {
		String duplYn = "N";

		int checkInt = (int) apiRegDAO.selApiInfoNmDupCheck(vo);

		if (checkInt > 0) {
			duplYn = "Y";
		}

		return duplYn;
	}
	
	@Override
	public String salApijDupPathCheck(ApiRegVO vo) throws Exception {
		String duplYn = "N";

		int checkInt = (int) apiRegDAO.salApijDupPathCheck(vo);

		if (checkInt > 0) {
			duplYn = "Y";
		}

		return duplYn;
	}
	
	@Override
	public List<ApiDefVO> selImportApiList(ApiRegVO vo) throws Exception {
		List<ApiImportVO> importList = apiRegDAO.selImportApiList(vo);
		List<ApiDefVO> list = new ArrayList<>();
		
		if (importList != null) {
			for (ApiImportVO importVO : importList) {
				ApiDefVO defVO = new ApiDefVO();
				// ApiImportVO의 필드들을 ApiDefVO로 복사 (필요한 필드 위주로)
				defVO.setApiSpcNo(importVO.getApiSpcNo());
				defVO.setApiNm(importVO.getApiNm());
				
				String amdrNm = "";
				if (null != importVO.getAmdrNm()) {
					amdrNm = CommonFunc.safeDbDecrypt(importVO.getAmdrNm());
				}

				if (amdrNm.length() > 1) {
					amdrNm = amdrNm.substring(0, amdrNm.length() - 1);
					amdrNm = amdrNm + "*";
				}
				defVO.setAmdrNm(amdrNm);
				defVO.setAmdDt(importVO.getAmdDt());
				defVO.setRegSttusCd(importVO.getRegSttusCd());
				defVO.setSysId(importVO.getSysId());
				
				list.add(defVO);
			}
		}
		return list;
	}
	
	@Override
	public int selImportApiTotalList(ApiRegVO vo) throws Exception {
		return (int) apiRegDAO.selImportApiTotalList(vo);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> savApiCateInfo(ApiRegVO vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		String apiCtgryNo = vo.getApiCtgryNo();
		String successStr = "ins";

		if (vo.getApiCtgryNo().equals("")) {
			apiRegDAO.savApiCateInfo(vo);
			apiCtgryNo = vo.getApiCtgryNo();
		} else {
			apiRegDAO.updApiCateInfo(vo);
			successStr = "upd";
		}

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlSbst(repYamlStr);

		int successInt = apiRegDAO.updApiYamlInfo(vo);

		ApiYamlVO fileInfo = apiRegDAO.selApiFileInfo(vo);

		LOGGER.debug("savApiCateInfo apiCtgryNo = {}", apiCtgryNo);

		returnMap.put("apiCtgryNo", apiCtgryNo);
		returnMap.put("successStr", successStr);
		returnMap.put("filePath", fileInfo.getYamlFilePath());
		returnMap.put("yamlStr", fileInfo.getYamlSbst());

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> delApiCateInfo(ApiRegVO vo) throws Exception {

		Map<String, Object> returnMap = new HashMap<>();
		String successStr = "del";

		int returnVal = 0;

		returnVal = apiRegDAO.delApiCateInfo(vo);

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlSbst(repYamlStr);

		int successInt = apiRegDAO.updApiYamlInfo(vo);

		ApiYamlVO fileInfo = apiRegDAO.selApiFileInfo(vo);

		returnMap.put("successStr", successStr);
		returnMap.put("filePath", fileInfo.getYamlFilePath());
		returnMap.put("yamlStr", fileInfo.getYamlSbst());

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public int savApiPathOrderInfo(ApiRegVO vo) throws Exception {
		return apiRegDAO.delApiCateInfo(vo);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public int savApiPathParamInfo(ApiRegVO vo) throws Exception {
		return apiRegDAO.delApiCateInfo(vo);
	}

	@Override
	public ApiDefVO selApiInfo(ApiRegVO vo) throws Exception {
		return apiRegDAO.selApiInfo(vo);
	}

	@Override
	public String selApiPathApiNo(ApiRegVO vo) throws Exception {
		return (String) apiRegDAO.selApiPathApiNo(vo);
	}

	@Override
	public String selApiCategoryNo(ApiRegVO vo) throws Exception {
		return (String) apiRegDAO.selApiCategoryNo(vo);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> savApiDataTypeReg(ApiRegVO vo) throws Exception {
		// 데이터 타입 저장/수정/삭제
		LOGGER.debug("ApiRegServiceImpl savApiDataTypeReg START =============================");
		Map<String, Object> returnMap = new HashMap<>();
		int successInt = 0;

		LOGGER.debug("savApiDataTypeReg vo.getYamlStr() ============================= {} ", vo.getYamlStr());

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlSbst(repYamlStr);

		successInt = apiRegDAO.updApiYamlInfo(vo);

		ApiYamlVO fileInfo = apiRegDAO.selApiFileInfo(vo);

		String filePath = fileInfo.getYamlFilePath();
		String fileName = fileInfo.getYamlFileNm();

		uploadFileUtils.makeYamlFile(filePath, fileName, vo.getYamlSbst());

		returnMap.put("apiSpcNo", vo.getApiSpcNo());
		returnMap.put("yamlStr", vo.getYamlSbst());
		returnMap.put("successInt", successInt);

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnMap;
	}

	@Override
	public ApiCategoryVO selCateInfo(ApiRegVO vo) throws Exception {
		return apiRegDAO.selCateInfo(vo);
	}
	@Override
	public Map<String, Object> selCateList(ApiRegVO vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("cateList", apiRegDAO.selCateList(vo));
		returnMap.put("pathList", apiRegDAO.selPathList(vo));
		returnMap.put("catePathList", apiRegDAO.selCatePathList(vo));

		return returnMap;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> savApiRegPath(ApiRegVO vo) throws Exception {
		LOGGER.debug("savApiRegPath ================================================ START ");

		Map<String, Object> returnMap = new HashMap<>();
		int successInt = 0;
		String apiNo = vo.getApiNo();
		String xApiNo = "";
		String paramNo = "";

		//LOGGER.debug("savApiRegPath vo.getYamlStr() ============================= {} ", vo.getYamlStr());
		LOGGER.debug("savApiRegPath vo.getApiNo() =============================== {} ", vo.getApiNo());
		LOGGER.debug("savApiRegPath vo.getInsertYn() ============================ {} ", vo.getInsertYn());
		LOGGER.debug("savApiRegPath xApiNo        =============================== {} ", xApiNo);

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlSbst(repYamlStr);

		ObjectMapper yamlMapper = Yaml.mapper();
		JsonNode rootNode = yamlMapper.readTree(repYamlStr);

		HashMap<String, Object> jsonResult2 = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		HashMap<String, Object> pathsMap = (HashMap<String, Object>) jsonResult2.get("paths");

		ArrayList<String> targetNameList = YamlToJava.getMapKeyReuturnArrayList(pathsMap, null); // Path 명 조회
		LOGGER.debug("savApiRegPath targetNameList.size ============================= {} ", targetNameList.size());

		for (int i = 0; i < targetNameList.size(); i++) {
			String apiPath = targetNameList.get(i);

			HashMap<String, Object> methObj = (HashMap<String, Object>) pathsMap.get(apiPath);
			LOGGER.debug("paht name ====================================== >1 ", apiPath);
			apiPath = apiPath.replaceAll(" ", "");
			LOGGER.debug("paht name ====================================== >2 ", apiPath);

			//--## [cmt][org is cmt] if(!apiPath.equals("\\\\/")) {
			//LOGGER.debug("i= {}== {} ", methObj);
			LOGGER.debug("i= {} , apiPath= {}", i, apiPath);

			ArrayList<String> methodList = YamlToJava.getMapKeyReuturnArrayList(methObj, null); // GET POST ...등등
			int pathOrder = 1;
			for (String subMethodName : methodList) {
				HashMap<String, Object> methodDetailObj = (HashMap<String, Object>) methObj.get(subMethodName);
				xApiNo = getNullToString((methodDetailObj.get("x-apiNo")));
				LOGGER.debug("savApiRegPath xApiNo === {} , {} ", xApiNo, apiNo);
				// @@@save.
				if (xApiNo.equals(apiNo) == true) {
					// @@@save-KOA_TB_API_SPC
					ApiRegVO pathRegVO = new ApiRegVO();
					pathRegVO.setApiNo(vo.getApiNo()); // API 번호
					pathRegVO.setApiSpcNo(vo.getApiSpcNo()); // API 명세 : apiSpcNo
					pathRegVO.setApiNm(getNullToString((methodDetailObj.get("summary")))); // API명
					pathRegVO.setApiCtgryNo(vo.getApiCtgryNo());	//--[i] apiRegDAO.savApiPathInfo에서는 직접적으로 쓰이지 않은
					pathRegVO.setApiCtgryNm(vo.getApiCtgryNm());	//--[i] apiRegDAO.savApiPathInfo에서 CTGRY_NM을 검색하여 API_CTGRY_NO를 입력하는 방식을 사용
					pathRegVO.setApiPath(apiPath); // path
					pathRegVO.setMethodCd(subMethodName);
					pathRegVO.setApiId(getNullToString((methodDetailObj.get("operationId"))));
					pathRegVO.setApiDesc(getNullToString((methodDetailObj.get("description")))); // API설명
					//-- [v][+]pathRegVO.setRegDt();
					pathRegVO.setRegr(vo.getRegr());
					//-- [v][+]pathRegVO.setAmdDt();
					//-- [v][+]pathRegVO.setAmdr();
					pathRegVO.setSortOdrg(pathOrder + ""); // 순서
					//-- [tag:SR-20210222][chg]
					pathRegVO.setUseYn(vo.getUseYn());
					pathRegVO.setApiGubun(vo.getApiGubun());
					//--##pathRegVO.setUseYn(getNullToString((methodDetailObj.get("x-display")))); // use_yn
					//--##pathRegVO.setApiGubun(getNullToString((methodDetailObj.get("x-visiblity")))); // Api 구분
					//-- [tag:adpt][drm][add] {
					pathRegVO.setApiHandlerCd(vo.getApiHandlerCd()); //-- handler구분코드 COMMON, ANYCOMMON, KOS, KOSMOS, SCAP, CAPRI, SB
					pathRegVO.setEndpntMethodCd(vo.getEndpntMethodCd()); //-- Endpoint method (GET, POST...)
					pathRegVO.setEndpntTbUrl(vo.getEndpntTbUrl()); // - Endpoint TB Url
					pathRegVO.setEndpntPrdUrl(vo.getEndpntPrdUrl()); // - Endpoint PRD Url
					//--@@@[cmt]pathRegVO.setEndpntEtcUrl(vo.getEndpntEtcUrl()); // - 기타 Endpoint Url // {Titie: URL}의 개행문자구분
					pathRegVO.setEndpntTimeout(vo.getEndpntTimeout()); //-- Endpoint Timeout (msec)
					pathRegVO.setEndpntClientIp(vo.getEndpntClientIp()); //-- Endpoint클라이언트IP 매핑키
					//-- [tag:SR-20210222][add] {
					pathRegVO.setResmapResCdField(vo.getResmapResCdField());
					pathRegVO.setResmapSuccVal(vo.getResmapSuccVal());
					pathRegVO.setResmapErrCdField(vo.getResmapErrCdField());
					pathRegVO.setResmapErrMsgField(vo.getResmapErrMsgField());
					pathRegVO.setHdpApiOutFormat(vo.getHdpApiOutFormat());
					pathRegVO.setHdpApiOutCommonParam(vo.getHdpApiOutCommonParam());
					pathRegVO.setHdpApiEndpointId(vo.getHdpApiEndpointId());
					pathRegVO.setHdpReqApiName(vo.getHdpReqApiName());
					pathRegVO.setHdpReqConfigToBody(vo.getHdpReqConfigToBody());
					pathRegVO.setHdpReqHeaderToBody(vo.getHdpReqHeaderToBody());
					pathRegVO.setHdpReqMappingToBody(vo.getHdpReqMappingToBody());
					pathRegVO.setHdpReqUrlDecode(vo.getHdpReqUrlDecode());
					pathRegVO.setHdpReqUrlEncode(vo.getHdpReqUrlEncode());
					pathRegVO.setHdpResMappingToBody(vo.getHdpResMappingToBody());
					pathRegVO.setHdpResProvideParam(vo.getHdpResProvideParam());
					pathRegVO.setHdpResUrlEncode(vo.getHdpResUrlEncode());
					//-- [tag:SR-20210222][add] }
					//-- [tag:SR-20210515][add]
					pathRegVO.setHdpExtProp(vo.getHdpExtProp());
					//-- [tag:SR-20230113][add]
					pathRegVO.setHdpHndlroptnConfig(vo.getHdpHndlroptnConfig());

					String apiVer = KsmUtil.fmt_data(apiPath, "fmt_version_in_path");
					pathRegVO.setApiVer(apiVer);
					pathRegVO.setApiVerNo(vo.getApiVerNo());
					//-- [tag:SR-20201127][add]
					pathRegVO.setGuideGubun(vo.getGuideGubun());
					//--[20201015][!@@!] API별 sandbox 적용여부
					pathRegVO.setSandboxYn(vo.getSandboxYn());
					//-- [tag:SR-20210711]
					pathRegVO.setProviderSeq(vo.getProviderSeq());
					//-- [tag:PRJ-20220901]
					pathRegVO.setBstgwTbSysId(vo.getBstgwTbSysId());
					pathRegVO.setBstgwPrdSysId(vo.getBstgwPrdSysId());
					
					pathRegVO.setImpact(vo.getImpact());
					pathRegVO.setApiUser(vo.getApiUser());
					pathRegVO.setReprocessableYn(vo.getReprocessableYn());
					pathRegVO.setIntergrationType(vo.getIntergrationType());
					pathRegVO.setApiMethod(vo.getApiMethod());
					pathRegVO.setEnablerInfo(vo.getEnablerInfo());
					pathRegVO.setComments(vo.getComments());
					
					//-- [tag:SR-20210222][cmt]
					/*--
					//-- response에서 결과mapping을 처리
					HashMap<String, Object> map_responses = (HashMap<String, Object>) methodDetailObj.get("responses");
					ArrayList<String> alist_response = YamlToJava.getMapKeyReuturnArrayList(map_responses, null);
					for (String resName : alist_response) {
						HashMap<String, Object> map_response = (HashMap<String, Object>) map_responses.get(resName);
						if (map_response.get("schema") != null) { // 응답 : body
							HashMap<String, Object> map_schema = (HashMap<String, Object>) map_response.get("schema");
							if (getNullToString(map_schema.get("x-remapSuccValue")).length() > 0) { //-- 성공기준이 있는 첫번째 정보 설정
								pathRegVO.setResmapResCdField(getNullToString(map_schema.get("x-remapResCd"))); //-- 결과매핑-결과필드(for ANYCOMMON)
								pathRegVO.setResmapSuccVal(getNullToString(map_schema.get("x-remapSuccValue"))); //-- 결과매핑-성공기준(for ANYCOMMON)
								pathRegVO.setResmapErrCdField(getNullToString(map_schema.get("x-remapErrCd"))); //--결과매핑-에러코드필드(for ANYCOMMON)
								pathRegVO.setResmapErrMsgField(getNullToString(map_schema.get("x-remapErrMsg"))); //-- 결과매핑-에러메시지필드(for ANYCOMMON)
								break;
							}
						}
					}
					--*/
					//-- [tag:adpt][drm][add] }

					//-- [v][-]pathRegVO.setApiCtgryNm(getNullToString((methodDetailObj.get("x-category"))));
					// // API카테고리 명
					if ("Y".equals(vo.getInsertYn()) == true) {
						apiRegDAO.savApiPathInfo(pathRegVO);
						apiNo = pathRegVO.getApiNo();
						vo.setApiNo(apiNo);
						
						pathRegVO.setApiNo(apiNo);
						apiRegDAO.savApiImpactInfo(pathRegVO);
					} else {
						apiRegDAO.updApiPathInfo(pathRegVO);
						
						if("Y".equals(vo.getInsertImpactYn())) {
							apiRegDAO.savApiImpactInfo(pathRegVO);
						} else {
							apiRegDAO.updApiImpactInfo(pathRegVO);
						}
						
						apiRegDAO.delApiPathParam(pathRegVO);
					}

					LOGGER.debug(" parameters 시작 ==================> ");
					int paramIndex = 1;
					int objNum = 1;
					// @@@save-req
					//-- request: query, header, path, body or formData
					ArrayList<HashMap<String, Object>> paramsList = (ArrayList<HashMap<String, Object>>) methodDetailObj.get("parameters");
					if (paramsList != null) {
						for (HashMap<String, Object> paramMap : paramsList) {
							// LOGGER.debug(" paramMap : " + paramMap);
							if (paramMap.get("in") != null) {
								LOGGER.debug("    paramIndex : {}", paramIndex);
								LOGGER.debug("    in : {}", paramMap.get("in"));
								LOGGER.debug("    name : {}", paramMap.get("name"));
								LOGGER.debug("    description : {}", paramMap.get("description"));
								LOGGER.debug("    x-dataTypeCd : {}", paramMap.get("x-dataTypeCd"));
								LOGGER.debug("    type : {}", paramMap.get("type"));
								LOGGER.debug("    require : {}", paramMap.get("required"));
								LOGGER.debug("    x-example : {}", paramMap.get("x-example"));
								//-- [tag:adpt][drm][add] {
								LOGGER.debug("    x-require : {}", paramMap.get("x-require"));
								LOGGER.debug("    x-personalData : {}", paramMap.get("x-personalData"));
								LOGGER.debug("    x-doNotSend : {}", paramMap.get("x-doNotSend"));
								LOGGER.debug("    x-fixedValue : {}", paramMap.get("x-fixedValue"));
								LOGGER.debug("    x-hidden : {}", paramMap.get("x-hidden"));
								LOGGER.debug("    x-mappingKey : {}", paramMap.get("x-mappingKey"));
								LOGGER.debug("    x-bigo : {}", paramMap.get("x-bigo"));
								//-- [tag:SR-20210222][add] {
								LOGGER.debug("    x-urlDec : {}", paramMap.get("x-urlDec"));
								LOGGER.debug("    x-urlEnc : {}", paramMap.get("x-urlEnc"));
								LOGGER.debug("    x-uploadTarget : {}", paramMap.get("x-uploadTarget"));
								//-- [tag:SR-20210222][add] }
								//--[20201023][!@@!] 파라미터 sandbox 적용 여부
								LOGGER.debug("    x-paramSandboxYn : {}", paramMap.get("x-paramSandboxYn"));
								//-- [tag:adpt][drm][add] }

								String definName = "";
								String dataTypeCd = getNullToString(paramMap.get("type"));
								LOGGER.debug("    1dataTypeCd : {}", dataTypeCd);
								if ((getNullToString(paramMap.get("in"))).equals("body") == true) {
									// @@@save-reqbody
									HashMap<String, Object> schema = (HashMap<String, Object>) paramMap.get("schema");
									LOGGER.debug("    schema : {}", schema);
									LOGGER.debug("    2dataTypeCd : {}", dataTypeCd);
									dataTypeCd = getNullToString(schema.get("type"));
									if (schema.get("$ref") != null) {
										String ref = getNullToString(schema.get("$ref"));
										LOGGER.debug("    ref : {}", ref);
										ref = ref.replaceAll("#/definitions/", "");
										LOGGER.debug("    ref : {}", ref);
										HashMap<String, Object> definitionsMap = (HashMap<String, Object>) jsonResult2.get("definitions");
										ArrayList<String> definitionsNameList = YamlToJava.getMapKeyReuturnArrayList(definitionsMap, null);
										if (definitionsNameList.contains(ref) == true) {
											definName = ref;
										} else {
											definName = "";
										}
									}
								}
								if (dataTypeCd.equals("") == true) {
									dataTypeCd = "datatype-ref";
									LOGGER.debug("    3dataTypeCd : {}", dataTypeCd);
								}

								// @@@save-case-#1
								// @@@save-KOA_TB_API_PARAM-request
								//-- [i]typecase-all
								ApiRegVO paramRegVO = new ApiRegVO();
								paramRegVO.setApiNo(vo.getApiNo()); // ApiNo
								paramRegVO.setParamTypeCd(getNullToString(paramMap.get("x-dataTypeCd"))); // 파라미터타입코드
								paramRegVO.setSortOdrg(paramIndex + ""); // 순서
								paramRegVO.setParamNm(getNullToString(paramMap.get("name"))); // 파라미터명
								paramRegVO.setDataTypeCd(dataTypeCd); // 데이터타입
								paramRegVO.setParamDesc(getNullToString(paramMap.get("description"))); // 파라미터설명
								paramRegVO.setExam(getNullToString(paramMap.get("x-example"))); // 예제
								paramRegVO.setPrntsParamNo(""); // 부모파라미터번호
								//-- [v][+]paramRegVO.setRegDt(); // 작성시간
								paramRegVO.setRegr(vo.getRegr()); // 작성자
								//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
								//-- [v][+]paramRegVO.setResCd(); // 응답상태
								//-- [v][-]paramRegVO.setResDesc(getNullToString(paramMap.get("description")));
								if (definName.length() > 0) {
									paramRegVO.setResDesc(definName);	// 응답설명
								}
								paramRegVO.setParamLoc(getNullToString(paramMap.get("in"))); // 파라미터위치
								//-- [drm][add]
								if ((dataTypeCd.equals("array") == true) || (dataTypeCd.equals("object") == true)) {
									//-- [v][+]
									paramRegVO.setObjNo(objNum + ""); // 그룹번호
									//-- [v][+]
									paramRegVO.setObjOdrg("1"); // 그룹내순번
								}
								//-- [tag:adpt][drm][add] {
								paramRegVO.setRequired(this.fn_fmt_required(paramMap.get("x-required")));
								paramRegVO.setPersonalData(getNullToString(paramMap.get("x-personalData")));
								paramRegVO.setDoNotSend(getNullToString(paramMap.get("x-doNotSend")));
								paramRegVO.setFixedValue(getNullToString(paramMap.get("x-fixedValue")));
								paramRegVO.setHidden(getNullToString(paramMap.get("x-hidden")));
								paramRegVO.setMappingKey(getNullToString(paramMap.get("x-mappingKey")));
								paramRegVO.setBigo(getNullToString(paramMap.get("x-bigo")));
								//-- [tag:SR-20210222][add] {
								paramRegVO.setHdpUrlDecode(getNullToString(paramMap.get("x-urlDec")));
								paramRegVO.setHdpUrlEncode(getNullToString(paramMap.get("x-urlEnc")));
								paramRegVO.setHdpUploadTarget(getNullToString(paramMap.get("x-uploadTarget")));
								//-- [tag:SR-20210222][add] }
								//--[20201023][!@@!] 파라미터 sandbox 적용 여부
								paramRegVO.setParamSandboxYn(getNullToString(paramMap.get("x-paramSandboxYn")));
								//-- [v][-][?]paramRegVO.setApiCtgryNo(vo.getApiCtgryNo());
								//-- [tag:adpt][drm][add] }

								//-- [#]savApiParamInfo-[request]
								apiRegDAO.savApiParamInfo(paramRegVO);
								paramNo = paramRegVO.getParamNo();
								paramIndex++; //-- [drm][add]++
								paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호

								if ((getNullToString(paramMap.get("in"))).equals("body") == true) {
									//-- content-type저장
									ArrayList<String> consumesList = (ArrayList<String>) methodDetailObj.get("consumes");
									for (String consume : consumesList) {
										paramRegVO.setContTypeCd(consume);
										paramRegVO.setParamNo(paramNo);
										apiRegDAO.insApiParamContType(paramRegVO);
									}
								}
								if (definName.length() > 0) {
									//-- userdefine datatype저장
									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) jsonResult2.get("definitions");
									HashMap<String, Object> definNameMap = (HashMap<String, Object>) definitionsMap.get(definName);
									// HashMap<String,Object> propertiesMap = (HashMap<String,Object>)paramMap.get("properties");
									LOGGER.debug("    definNameMap : {}", definNameMap);
									paramIndex = setSetPathParamArrayObject(definNameMap, paramRegVO, paramIndex, 1, objNum);
									objNum++;
								} else {
									if (dataTypeCd.equals("array") == true) {
										// @@@save-case-#2
										paramIndex = setSetPathParamArrayObject((HashMap<String, Object>) paramMap.get("items"), paramRegVO, paramIndex, 1, objNum);
										objNum++;
									} else if (dataTypeCd.equals("object") == true) {
										HashMap<String, Object> schemaMap = (HashMap<String, Object>) paramMap.get("schema");
										// HashMap<String,Object> propertiesMap = (HashMap<String,Object>)paramMap.get("properties");
										LOGGER.debug("    schemaMap : {}", schemaMap);
										// @@@save-objchild
										paramIndex = setSetPathParamArrayObject(schemaMap, paramRegVO, paramIndex, 1, objNum);
										objNum++;
									}
								}

							}
							//-- [drm][cmt][move to insert] paramIndex++;
						} //-- for (HashMap<String,Object> paramMap : paramsList) {
					} //-- if (paramsList != null) {

					LOGGER.debug("responses 시작 ========= ");
					if (methodDetailObj.get("responses") != null) {
						HashMap<String, Object> responseMap = (HashMap<String, Object>) methodDetailObj.get("responses");
						LOGGER.debug("responseMap ========= {}", responseMap);
						ArrayList<String> responseNameList = YamlToJava.getMapKeyReuturnArrayList(responseMap, null); // 200 400 ..등등
						LOGGER.debug("responseNameList ========= {}", responseNameList);

						for (String resName : responseNameList) {
							HashMap<String, Object> resNameMap = (HashMap<String, Object>) responseMap.get(resName);
							if (resNameMap.get("headers") != null) {
								HashMap<String, Object> headerInfoMap = (HashMap<String, Object>) resNameMap.get("headers");
								ArrayList<String> headerParamNameList = YamlToJava.getMapKeyReuturnArrayList(headerInfoMap, null); // 파라미터
								for (String paramName : headerParamNameList) {
									HashMap<String, Object> paramNameInfo = (HashMap<String, Object>) headerInfoMap.get(paramName);
									LOGGER.debug("    paramIndex : {}", paramIndex);
									LOGGER.debug("    headers param name = {}", paramName);
									LOGGER.debug("    headers param description = {}", paramNameInfo.get("description"));
									LOGGER.debug("    headers param x-dataTypeCd = {}", paramNameInfo.get("x-dataTypeCd"));
									LOGGER.debug("    headers param type = {}", paramNameInfo.get("type"));
									LOGGER.debug("    headers param require = {}", paramNameInfo.get("require"));
									LOGGER.debug("    headers param x-example = {}", paramNameInfo.get("x-example"));
									//-- [tag:adpt][drm][add] {
									LOGGER.debug("    headers param x-required : {}", paramNameInfo.get("x-required"));
									LOGGER.debug("    headers param x-personalData : {}", paramNameInfo.get("x-personalData"));
									LOGGER.debug("    headers param x-doNotSend : {}", paramNameInfo.get("x-doNotSend"));
									LOGGER.debug("    headers param x-fixedValue : {}", paramNameInfo.get("x-fixedValue"));
									LOGGER.debug("    headers param x-hidden : {}", paramNameInfo.get("x-hidden"));
									LOGGER.debug("    headers param x-mappingKey : {}", paramNameInfo.get("x-mappingKey"));
									LOGGER.debug("    headers param x-bigo : {}", paramNameInfo.get("x-bigo"));
									//-- [tag:adpt][drm][add] }
									//-- [tag:SR-20210222][add] {
									LOGGER.debug("    headers param x-urlDec : {}", paramNameInfo.get("x-urlDec"));
									LOGGER.debug("    headers param x-urlEnc : {}", paramNameInfo.get("x-urlEnc"));
									LOGGER.debug("    headers param x-uploadTarget : {}", paramNameInfo.get("x-uploadTarget"));
									//-- [tag:SR-20210222][add] }

									String dataTypeCd = getNullToString(paramNameInfo.get("type"));
									// @@@save-KOA_TB_API_PARAM-response-header
									//-- [i]typecase-all(ex object)
									ApiRegVO paramRegVO = new ApiRegVO();
									paramRegVO.setApiNo(vo.getApiNo()); // ApiNo
									paramRegVO.setParamTypeCd(getNullToString(paramNameInfo.get("x-dataTypeCd"))); // 파라미터타입코드
									paramRegVO.setSortOdrg(paramIndex + ""); // 순서
									paramRegVO.setParamNm(paramName); // 파라미터명
									paramRegVO.setDataTypeCd(dataTypeCd); // 데이터타입
									paramRegVO.setParamDesc(getNullToString(paramNameInfo.get("description"))); // 파라미터설명
									paramRegVO.setExam(getNullToString(paramNameInfo.get("x-example"))); // 예제
									paramRegVO.setPrntsParamNo(""); // 부모파라미터번호
									//-- [v][+]paramRegVO.setRegDt(); // 작성시간
									paramRegVO.setRegr(vo.getRegr()); // 작성자
									//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
									paramRegVO.setResCd(resName); // 응답상태
									//-- [!] header에는 상태-설명을 설정안함
									//-- [v][-]paramRegVO.setResDesc(getNullToString(paramNameInfo.get("description"))); // 응답설명
									paramRegVO.setParamLoc("header"); // 파라미터위치
									//-- [drm][add]
									if (dataTypeCd.equals("array") == true) {	//-- [i]response header는 object가 없음
										//-- [v][+]
										paramRegVO.setObjNo(objNum + ""); // 그룹번호
										//-- [v][+]
										paramRegVO.setObjOdrg("1"); // 그룹내순번
									}
									//-- [tag:adpt][drm][add] {
									paramRegVO.setRequired(this.fn_fmt_required(paramNameInfo.get("x-required")));
									paramRegVO.setPersonalData(getNullToString(paramNameInfo.get("x-personalData")));
									paramRegVO.setDoNotSend(getNullToString(paramNameInfo.get("x-doNotSend")));
									paramRegVO.setFixedValue(getNullToString(paramNameInfo.get("x-fixedValue")));
									paramRegVO.setHidden(getNullToString(paramNameInfo.get("x-hidden")));
									paramRegVO.setMappingKey(getNullToString(paramNameInfo.get("x-mappingKey")));
									paramRegVO.setBigo(getNullToString(paramNameInfo.get("x-bigo")));
									//-- [tag:adpt][drm][add] }
									//-- [tag:SR-20210222][add] {
									paramRegVO.setHdpUrlDecode(getNullToString(paramNameInfo.get("x-urlDec")));
									paramRegVO.setHdpUrlEncode(getNullToString(paramNameInfo.get("x-urlEnc")));
									paramRegVO.setHdpUploadTarget(getNullToString(paramNameInfo.get("x-uploadTarget")));
									//-- [tag:SR-20210222][add] }
									//-- [v][-][?]paramRegVO.setApiCtgryNo(vo.getApiCtgryNo());

									//-- [#]savApiParamInfo-[response header]
									apiRegDAO.savApiParamInfo(paramRegVO);
									paramNo = paramRegVO.getParamNo();
									paramIndex++;
									paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호

									//-- [drm][add][missing array of case]
									if (dataTypeCd.equals("array") == true) {
										paramIndex = setSetPathParamArrayObject((HashMap<String, Object>) paramNameInfo.get("items"), paramRegVO, paramIndex, 1, objNum);
										objNum++;
									}
									//-- [drm][cmt][schema 처리로 이동][bug?]
									/*--
									ArrayList<String> producesList = (ArrayList<String>)methodDetailObj.get("produces");
									for(String produces : producesList) {
										paramRegVO.setContTypeCd(produces);
										paramRegVO.setParamNo(paramNo);
										apiRegDAO.insApiParamContType(paramRegVO);
									}
									--*/
								}
							} //-- if (resNameMap.get("headers") != null) {

							if (resNameMap.get("schema") != null) { // 응답 : body
								HashMap<String, Object> schemaInfoMap = (HashMap<String, Object>) resNameMap.get("schema");
								LOGGER.debug("    paramIndex : {}", schemaInfoMap);
								LOGGER.debug("    schema name = {}", schemaInfoMap.get("x-name"));
								LOGGER.debug("    schema description = {}", schemaInfoMap.get("description"));
								LOGGER.debug("    schema x-dataTypeCd = {}", schemaInfoMap.get("x-dataTypeCd"));
								LOGGER.debug("    schema type = {}", schemaInfoMap.get("type"));
								LOGGER.debug("    schema x-example = {}", schemaInfoMap.get("x-example"));
								//-- [tag:adpt][drm][add] {
								LOGGER.debug("    schema x-required : {}", schemaInfoMap.get("x-required"));
								LOGGER.debug("    schema x-personalData : {}", schemaInfoMap.get("x-personalData"));
								LOGGER.debug("    schema x-doNotSend : {}", schemaInfoMap.get("x-doNotSend"));
								LOGGER.debug("    schema x-fixedValue : {}", schemaInfoMap.get("x-fixedValue"));
								LOGGER.debug("    schema x-hidden : {}", schemaInfoMap.get("x-hidden"));
								LOGGER.debug("    schema x-mappingKey : {}", schemaInfoMap.get("x-mappingKey"));
								LOGGER.debug("    schema x-bigo : {}", schemaInfoMap.get("x-bigo"));
								//-- [tag:adpt][drm][add] }
								//-- [tag:SR-20210222][add] {
								LOGGER.debug("    schema x-urlDec : {}", schemaInfoMap.get("x-urlDec"));
								LOGGER.debug("    schema x-urlEnc : {}", schemaInfoMap.get("x-urlEnc"));
								LOGGER.debug("    schema x-uploadTarget : {}", schemaInfoMap.get("x-uploadTarget"));
								//-- [tag:SR-20210222][add] }
								if (schemaInfoMap.get("type") == null) {
									continue;
								}

								String definName = "";
								//-- [drm][chg] String resDesc = getNullToString(schemaInfoMap.get("description"));
								//-- [i] response body시 resDesc는 상태-설명을 설정
								String resDesc = getNullToString(resNameMap.get("description"));
								String dataTypeCd = getNullToString(schemaInfoMap.get("type"));
								if (schemaInfoMap.get("$ref") != null) {
									String ref = getNullToString(schemaInfoMap.get("$ref"));
									ref = ref.replaceAll("#/definitions/", "");
									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) jsonResult2.get("definitions");
									ArrayList<String> definitionsNameList = YamlToJava.getMapKeyReuturnArrayList(definitionsMap, null);
									if (definitionsNameList.contains(ref) == true) {
										definName = ref;
									} else {
										definName = "";
									}
									resDesc = definName;
								}

								if (dataTypeCd.equals("") == true) {
									dataTypeCd = "datatype-ref";
								}
								//-- [i]typecase-all
								// @@@save-KOA_TB_API_PARAM-response-body
								ApiRegVO paramRegVO = new ApiRegVO();
								paramRegVO.setApiNo(vo.getApiNo()); // ApiNo
								paramRegVO.setParamTypeCd("PRMTYP1020"); // 파라미터타입코드
								paramRegVO.setSortOdrg(paramIndex + ""); // 순서
								paramRegVO.setParamNm(getNullToString(schemaInfoMap.get("x-name"))); // 파라미터명
								paramRegVO.setDataTypeCd(dataTypeCd); // 데이터타입
								//-- [v][+]
								paramRegVO.setParamDesc(getNullToString(schemaInfoMap.get("description"))); // 파라미터설명
								//-- [v][+]
								paramRegVO.setExam(getNullToString(schemaInfoMap.get("x-example"))); // 예제
								//-- [v][+]
								paramRegVO.setPrntsParamNo(""); // 부모파라미터번호
								//-- [v][+]paramRegVO.setRegDt(); // 작성시간
								paramRegVO.setRegr(vo.getRegr()); // 작성자
								//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
								paramRegVO.setResCd(resName); // 응답상태
								paramRegVO.setResDesc(resDesc); // 응답설명
								paramRegVO.setParamLoc("body"); // 파라미터위치
								//-- [drm][add]
								if ((dataTypeCd.equals("array") == true) || (dataTypeCd.equals("object") == true)) {
									//-- [v][+]
									paramRegVO.setObjNo(objNum + ""); // 그룹번호
									//-- [v][+]
									paramRegVO.setObjOdrg("0"); // 그룹내순번
								}
								//-- [tag:adpt][drm][add] {
								paramRegVO.setRequired(this.fn_fmt_required(schemaInfoMap.get("x-required")));
								paramRegVO.setPersonalData(getNullToString(schemaInfoMap.get("x-personalData")));
								paramRegVO.setDoNotSend(getNullToString(schemaInfoMap.get("x-doNotSend")));
								paramRegVO.setFixedValue(getNullToString(schemaInfoMap.get("x-fixedValue")));
								paramRegVO.setHidden(getNullToString(schemaInfoMap.get("x-hidden")));
								paramRegVO.setMappingKey(getNullToString(schemaInfoMap.get("x-mappingKey")));
								paramRegVO.setBigo(getNullToString(schemaInfoMap.get("x-bigo")));
								//-- [tag:adpt][drm][add] }
								//-- [tag:SR-20210222][add] {
								paramRegVO.setHdpUrlDecode(getNullToString(schemaInfoMap.get("x-urlDec")));
								paramRegVO.setHdpUrlEncode(getNullToString(schemaInfoMap.get("x-urlEnc")));
								paramRegVO.setHdpUploadTarget(getNullToString(schemaInfoMap.get("x-uploadTarget")));
								//-- [tag:SR-20210222][add] }
								LOGGER.debug("    schema : {}", schemaInfoMap);

								if (definName.length() > 0) {
									//-- [#]savApiParamInfo-[response body userdefine datatype]
									apiRegDAO.savApiParamInfo(paramRegVO);
									paramNo = paramRegVO.getParamNo();
									paramIndex++;
									paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호

									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) jsonResult2.get("definitions");
									HashMap<String, Object> definNameMap = (HashMap<String, Object>) definitionsMap.get(definName);
									LOGGER.debug("    definNameMap : {}", definNameMap);
									paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호
									paramIndex = setSetPathParamArrayObject(definNameMap, paramRegVO, paramIndex, 1, objNum);
									objNum++;
								} else {
									//-- [drm][chg] paramIndex++ -> paramIndex
									paramIndex = setSetPathParamArrayObject(schemaInfoMap, paramRegVO, paramIndex, 1, objNum);
									objNum++;

									//-- [drm][add][move from header proc]
									//-- paramNo가 없으므로 setSetPathParamArrayObject()로 넘겨서 처리
									//-- content-type저장
									schemaInfoMap.put("produces", methodDetailObj.get("produces"));
									ArrayList<String> producesList = (ArrayList<String>) methodDetailObj.get("produces");
									paramNo = paramRegVO.getParamNo(); //-- setSetPathParamArrayObject()에서 설정하여 복귀
									for (String produces : producesList) {
										paramRegVO.setContTypeCd(produces);
										paramRegVO.setParamNo(paramNo);
										apiRegDAO.insApiParamContType(paramRegVO);
									}
								}
								paramIndex++;
							} //-- if (resNameMap.get("schema") != null) {
						} //-- for (String resName : responseNameList) {
					} //-- if (methodDetailObj.get("responses") != null) {
					LOGGER.debug("responses 끝  ========= ");
					//-- [drm][chg][아래로 이전] pathOrder++;
				} //-- if (xApiNo.equals(apiNo) == true) {
				//-- [drm][add][위에서이전]
				pathOrder++;
			} //-- for (String subMethodName : methodList ) {
			//--## } //--## [cmt][org is cmt] if(!apiPath.equals("\\\\/")) {
		} //-- for (int i = 0 ; i < targetNameList.size(); i++) {

		savYamlToFile(vo);

		returnMap.put("apiSpcNo", vo.getApiSpcNo());
		returnMap.put("yamlSbst", vo.getYamlSbst());
		returnMap.put("successInt", successInt);
		returnMap.put("yamlStr", repYamlStr);

		// apiRegDAO.selCatePathList(vo);

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnMap;
	}

	/**
	 * <pre>
	* 1. 메소드명 : selMethodDupList
	* 2. 작성일 : 2017. 12. 5. 오후 5:12:31
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 공통코드 MTHTYP1000 의 목록 , 패스 등록시 기존에 등록된 패스의 MTHTYP1000 값이 존재하면 제거하고 보여줌.
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	@Override
	public List<Map<String, Object>> selMethodDupList(ApiRegVO vo) throws Exception {
		return apiRegDAO.selMethodDupList(vo);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public void savYamlFile(String filePath, String fileName, String yaml) throws Exception {
		uploadFileUtils.makeYamlFile(filePath, fileName, yaml);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> delApiPath(ApiRegVO vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		String successYn = "N";

		int delParamCnt = (int) apiRegDAO.delApiPathParam(vo);

		LOGGER.debug("delApiPath delParamCnt======= {} ", delParamCnt);

		int delImpactCnt = (int) apiRegDAO.delApiImpact(vo);

		LOGGER.debug("delApiPath delImpactCnt======= {} ", delImpactCnt);
		
		int delPathCnt = (int) apiRegDAO.delApiPath(vo);

		LOGGER.debug("delApiPath delPathCnt======= {} ", delPathCnt);
		
		if (delPathCnt > 0) {
			successYn = "Y";
		}

		savYamlToFile(vo);

		returnMap.put("delPathCnt", delPathCnt);
		returnMap.put("delParamCnt", delParamCnt);
		returnMap.put("successYn", successYn);

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnMap;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public HashMap<String, Object> delApiAllPath(ApiRegVO vo) throws Exception {
		HashMap<String, Object> returnEMap = new HashMap<>();
		String successYn = "N";

		int delParamCnt = (int) apiRegDAO.delApiAllPathParam(vo);
		
		LOGGER.debug("delApiAllPath delParamCnt======= {} ", delParamCnt);
		
		int delImpactCnt = (int) apiRegDAO.delApiAllImpact(vo);

		LOGGER.debug("delApiAllPath delImpactCnt======= {} ", delImpactCnt);
		
		int delCnt = (int) apiRegDAO.delApiAllPath(vo);

		LOGGER.debug("delApiAllPath delCnt======= {} ", delCnt);
		
		if (delCnt > 0) {
			successYn = "Y";
		}

		savYamlToFile(vo);

		returnEMap.put("delPathCnt", delCnt);
		returnEMap.put("delParamCnt", delParamCnt);
		returnEMap.put("successYn", successYn);

		// 이력 저장
		vo.setApiSttusCd("APIREG1010");
		updApiHisInfo(vo);

		return returnEMap;
	}

	/**
	 * <pre>
	* 1. 메소드명 : savYamlToFile
	* 2. 작성일 : 2017. 12. 7. 오전 11:18:13
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : YAML 파일 저장
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	@Override
	@Transactional(rollbackFor={Exception.class})
	public Map<String, Object> savYamlToFile(ApiRegVO vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlSbst(repYamlStr);

		int successInt = apiRegDAO.updApiYamlInfo(vo);

		ApiYamlVO fileInfo = apiRegDAO.selApiFileInfo(vo);

		String filePath = fileInfo.getYamlFilePath();
		String fileName = fileInfo.getYamlFileNm();
		String yaml = vo.getYamlSbst();

		uploadFileUtils.makeYamlFile(filePath, fileName, yaml);

		return returnMap;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> updApiHisInfo(ApiRegVO vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();

		int successInt = apiRegDAO.updApiHisInfo(vo);

		returnMap.put("successInt", successInt);

		return returnMap;
	}

	//--[tag:adpt][191202]
	/*--[i]
	 * yaml파일의 신규 apiNo로의 변경처리가 누락된것으로 보이는 issue에 대해서는 처리하지 않고 원본코드를 그대로 두었음
	 * openapi의 savApiRegRest()는 더이상 사용되지 않고 openapi-factory의 savApiRegRest()로 대체된것으로 보임
	 * 만일 사용시 반드시 코드의 유효성을 검증후 사용하여아 할것으로 보임 
	 *  
	--*/
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> savApiRegRest(ApiRegVO vo) throws Exception {
		//--[tag:adpt][191202][for always exception]
		if (vo != null) {
			throw new Exception();
		}
	
		Map<String, Object> returnMap = new HashMap<>();

		LOGGER.debug("savApiRegRest ====================== START ");

		String repYamlStr = replaceYamlStr(vo.getYamlStr());
		vo.setYamlStr(repYamlStr);

		// 데이터 읽기
		ApiRegVO apiRegBasicVO = (ApiRegVO) YamlToJava.getYamlToInfoData(vo.getYamlStr());

		apiRegBasicVO.setYamlSbst(repYamlStr);
		apiRegBasicVO.setRegSttusCd(vo.getRegSttusCd()); //
		apiRegBasicVO.setRegr(vo.getRegr());
		apiRegBasicVO.setApiSpcNo(vo.getApiSpcNo());

		int basicCnt = apiRegDAO.updApiRegRestBasic(apiRegBasicVO);

		LOGGER.debug("savApiRegRest basicCnt ======= {} ", basicCnt);

		ObjectMapper yamlMapper = Yaml.mapper();
		JsonNode rootNode = yamlMapper.readTree(repYamlStr);

		HashMap<String, Object> jsonResult2 = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		HashMap<String, Object> pathsMap = (HashMap<String, Object>) jsonResult2.get("paths");

		// Path 명 조회
		ArrayList<String> targetNameList = YamlToJava.getMapKeyReuturnArrayList(pathsMap, null);

		LOGGER.debug("savApiRegRest targetNameList.size ============================= {} ", targetNameList.size());

		apiRegDAO.delApiSpcPathParam(vo);
		
		apiRegDAO.delApiSpcImpact(vo);
		
		apiRegDAO.delApiSpcPath(vo);

		// x-category 가 있는지 확인 : 하나도 없으면 하나 강제 생성

		// 패스 조회
		for (int i = 0; i < targetNameList.size(); i++) {

			String apiPath = targetNameList.get(i);

			HashMap<String, Object> methObj = (HashMap<String, Object>) pathsMap.get(apiPath);

			LOGGER.debug("paht name1 ====================================== > ", apiPath);

			apiPath = apiPath.replaceAll(" ", "");

			LOGGER.debug("paht name2 ====================================== > ", apiPath);

			// if(!apiPath.equals("\\\\/")) {

			//LOGGER.debug("i= {}== {} ", methObj);
			LOGGER.debug("i= {} , apiPath= {}", i, apiPath);

			ArrayList<String> methodList = YamlToJava.getMapKeyReuturnArrayList(methObj, null); // GET POST ...등등

			for (String subMethodName : methodList) {

				HashMap<String, Object> methodDetailObj = (HashMap<String, Object>) methObj.get(subMethodName);

				int pathOrder = 1;

				ApiRegVO pathRegVO = new ApiRegVO();
				pathRegVO.setApiSpcNo(vo.getApiSpcNo()); // API 명세 : apiSpcNo
				pathRegVO.setApiNm(getNullToString((methodDetailObj.get("summary")))); // API명
				pathRegVO.setApiCtgryNm(getNullToString((methodDetailObj.get("x-category"))));// API카테고리 명
				pathRegVO.setApiPath(apiPath); // path
				pathRegVO.setSortOdrg(pathOrder + ""); // 순서
				pathRegVO.setApiDesc(getNullToString((methodDetailObj.get("description")))); // API설명
				pathRegVO.setRegr(vo.getRegr());
				pathRegVO.setMethodCd(subMethodName);
				//-- [v]pathRegVO.setApiCtgryNo(vo.getApiCtgryNo());
				pathRegVO.setApiId(getNullToString((methodDetailObj.get("operationId"))));

				apiRegDAO.savApiPathInfo(pathRegVO);
				String apiNo = pathRegVO.getApiNo();
				vo.setApiNo(apiNo);

				LOGGER.debug("           parameters : " + methodDetailObj.get("parameters")); // parameters

				if (methodDetailObj.get("parameters") != null) {

					ArrayList<HashMap<String, Object>> paramsList = (ArrayList<HashMap<String, Object>>) methodDetailObj.get("parameters");

					int paramIndex = 1;

					for (HashMap<String, Object> paramMap : paramsList) {

						if (paramMap.get("in") != null) {

							LOGGER.debug("    paramMap : " + paramMap);
							LOGGER.debug("    name : " + paramMap.get("name"));
							LOGGER.debug("    type : " + paramMap.get("type"));
							LOGGER.debug("    description : " + paramMap.get("description"));
							LOGGER.debug("    x-example : " + paramMap.get("x-example"));
							LOGGER.debug("    x-paramTypeCd : " + paramMap.get("x-paramTypeCd"));

							ApiRegVO paramRegVO = new ApiRegVO();

							String dataTypeCd = getNullToString(paramMap.get("type"));

							if ((getNullToString(paramMap.get("in"))).equals("body")) {
								HashMap<String, Object> schema = (HashMap<String, Object>) paramMap.get("schema");
								dataTypeCd = getNullToString(schema.get("type"));
							}

							if (dataTypeCd.equals("")) {
								dataTypeCd = "datatype-ref";
							}

							paramRegVO.setApiNo(vo.getApiNo());
							paramRegVO.setSortOdrg(paramIndex + ""); // 순서
							paramRegVO.setParamTypeCd(getNullToString(paramMap.get("x-paramTypeCd"))); // 예) PRMTYP1010
							paramRegVO.setDataTypeCd(dataTypeCd); // 데이터타입
							paramRegVO.setParamDesc(getNullToString(paramMap.get("description"))); // 파라미터설명
							paramRegVO.setExam(getNullToString(paramMap.get("x-example"))); // 예제
							paramRegVO.setPrntsParamNo(apiNo); // 부모파라미터번호
							paramRegVO.setParamNm(getNullToString(paramMap.get("name"))); // 부모파라미터번호
							paramRegVO.setRegr(vo.getRegr());
							//-- [v]paramRegVO.setApiCtgryNo(vo.getApiCtgryNo());

							//-- [#]savApiParamInfo-saveApiRegRest()
							apiRegDAO.savApiParamInfo(paramRegVO);
							String paramNo = paramRegVO.getParamNo();

						}
					}

					paramIndex++;
					// 파라미터 셋팅 시작
				}

				pathOrder++;

			} // end : for(String subMethodName : methodList ) {

			// }

		} // end : for(String apiPath : targetNameList ) {

		savYamlToFile(vo);

		LOGGER.debug("savApiRegRest ====================== END ");

		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor={Exception.class})
	public HashMap<String,Object> selYamlDataType(String yamlSbst) throws Exception{
		
		String newYamlSbst = yamlSbst;
		newYamlSbst = replaceYamlStr(newYamlSbst);
		
		ObjectMapper yamlMapper = Yaml.mapper();
		JsonNode rootNode = yamlMapper.readTree(newYamlSbst);
		
		HashMap<String,Object> jsonResult2 = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		HashMap<String,Object> dataTypeMap = (HashMap<String,Object>)jsonResult2.get("definitions");
		
		return dataTypeMap;
	}
	
	@Override
	@Transactional(rollbackFor={Exception.class})
	public int updApiRegRestBasicToWork(ApiRegVO vo) throws Exception{
		return apiRegDAO.updApiRegRestBasicToWork(vo);
	}

	@Override
	public HashMap<String, Object> selUrlToYamlAjax(ApiRegVO vo) throws Exception {
		HashMap<String, Object> info = new HashMap<String, Object>();

		InputStreamReader isr = null;
		String yamlData = "";
		BufferedReader br = null;

		LOGGER.debug("vo.getUrlType() => {} ", vo.getUrlType());
		LOGGER.debug("vo.getUrlPath() => {} ", vo.getUrlPath());

		info.put("successYn", "N");

		if (vo.getUrlType().equals("YAML")) {

			try {

				URL url = new URL(vo.getUrlPath());
				isr = new InputStreamReader(url.openStream());// 입력스트림을 생성합니다.
				br = new BufferedReader(isr);
				String inLine = null;
				String jsonData = "";

				LOGGER.debug("br => {} ", br);

				while ((inLine = br.readLine()) != null) { // 라인단위로 읽어들이기
					jsonData = jsonData + inLine + "\r\n";
				}

				yamlData = jsonData;

				br.close(); // 데이터 읽기가 끝나면 close메소드로 스트림을 닫습니다.

				LOGGER.debug("yamlData => {} ", yamlData);

				info.put("successYn", "Y");
				info.put("yamlData", yamlData);

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					if (isr != null) {
						isr.close();
					}
				} catch (IOException e) {
					throw e;
				}
			}

		} else if (vo.getUrlType().equals("JSON")) {
			InputStream is = new URL(vo.getUrlPath()).openStream();
			try {

				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);

				final JsonToYaml j2y = new JsonToYaml.Default();
				yamlData = j2y.toYaml(jsonText);

				LOGGER.debug("yamlData => {} ", yamlData);

				info.put("successYn", "Y");
				info.put("yamlData", yamlData);
			} catch (Exception e) {
				throw e;
			} finally {
				is.close();
			}

		} else if (vo.getUrlType().equals("WSDL")) {

			UUID uid = UUID.randomUUID();
			String rPath = uid.toString();

			// 임시 파일 생성
			File newF = new File(wsdlServerNewHost + File.separator + rPath);
			LOGGER.debug("파일 생성" + wsdlServerNewHost + File.separator + rPath);
			if (!newF.mkdir()) {
				LOGGER.debug("new file error");
			}
			// 경로
			String swaggerJsonFilePath = wsdlServerNewHost + File.separator + rPath + File.separator
					+ wsdlImportYamlFileNm;

			runCmdUtil.runNodeCmd(vo.getUrlPath(), wsdlServerHost, swaggerJsonFilePath);

			// InputStream is = new URL(vo.getUrlPath()).openStream();
			BufferedReader rd = null;
			String json = "";
			int errCnt = 0;
			boolean loofEnd = false;
			while (true) {
				try {
					File is = new File(swaggerJsonFilePath);
					rd = new BufferedReader(new FileReader(is));
					String jsonText = readAll(rd);

					final JsonToYaml j2y = new JsonToYaml.Default();
					json = j2y.toYaml(jsonText);

					LOGGER.debug("yamlData => {} ", json);

					info.put("successYn", "Y");
					info.put("yamlData", json);
				} catch (Exception e) {

					if (errCnt < Integer.parseInt(apiImportLoopCnt)) {
						LOGGER.debug("errCnt>>>>>>>>>>>>>>>>>>>>>>" + errCnt);
						errCnt = errCnt + 1;
						loofEnd = false;
						// 파일이 없을 경우 3초 대기후 다시 조회
						try {
							Thread.sleep(Integer.parseInt(apiImportSleepCnt));
						} catch (InterruptedException inteerE) {
							LOGGER.debug("inteerE Error", inteerE);
						}
					} else {
						loofEnd = true;
					}
				} finally {
					if (rd != null) {
						try {
							rd.close();

							LOGGER.debug("file delete");
							if (newF.exists() == true) {
								// 생성 되었던 폴더 삭제
								File[] innerFiles = newF.listFiles();
								LOGGER.debug("file length : " + innerFiles.length);
								// 하위 디렉토리 삭제
								for (int i = 0; i < innerFiles.length; i++) {
									//-- [tag:SR-20210915][sparrow][correction]
									if (true == innerFiles[i].exists()) {
										if (!innerFiles[i].delete()) {
											LOGGER.debug("delete sub file error");
										}
									}
								}
								// 폴더 삭제
								if (!newF.delete()) {
									LOGGER.debug("delete file error");
								}
							} else {
								LOGGER.debug("js file not exists @@@@");
							}
							// 정상적으로 파일을 읽은 뒤에 파일 삭제 후에 while문 빠져나감
							break;
						} catch (IOException e) {
							//-- [tag:SR-20210915][sparrow][correction]
							LOGGER.debug("\n\n### ApiRegServiceImpl.selUrlToYamlAjax() [IOException: {}] ###\n", e);
						}
					}
					if (loofEnd) {
						LOGGER.debug("successYn -- > N");
						info.put("successYn", "N");
						info.put("yamlData", "");
						// 파일을 찾지 못하였어도 생성시켜준 파일은 삭제
						if (newF.exists() == true) {
							// 생성 되었던 폴더 삭제
							File[] innerFiles = newF.listFiles();
							LOGGER.debug("file lenth : " + innerFiles.length);
							// 하위 디렉토리 삭제
							for (int i = 0; i < innerFiles.length; i++) {
								innerFiles[i].delete();
							}
							// 폴더 삭제
							if (!newF.delete()) {
								LOGGER.debug("delete file error");
							}
						} else {
							LOGGER.debug("js file not exists @@@@");
						}
						break;
					}
				}

			}
		}
		return info;
	}

	@Override
	public List<Map<String, Object>> selApiDataTypeUseList(ApiRegVO vo) throws Exception {
		return apiRegDAO.selApiDataTypeUseList(vo);
	}

	@Override
	public HashMap<String, Object> regApidocAjax(MultipartFile uploadFile) throws Exception {
		LOGGER.debug(" ###################  regApidocAjax #############################");
		UUID uid = UUID.randomUUID();
		String rPath = uid.toString();
		HashMap<String, Object> map = new HashMap<>();
		if (uploadFile != null && !uploadFile.isEmpty()) {
			CmnFileVo fileVo = addApidocFile(uploadFile);
			LOGGER.debug("input path : {}", fileVo.getFilePath());
			LOGGER.debug("out path : {}", outFilePath);

			// 임시 파일 생성
			File newF = new File(outFilePath + File.separator + rPath);
			LOGGER.debug("파일 생성" + outFilePath + File.separator + rPath);
			if (!newF.mkdir()) {
				LOGGER.debug("file error");
			}
			int fileCnt = 0;
			while (true) {
				if (newF.exists() == true) {
					LOGGER.debug("============ file OK =================== ");
					// cmd 명령어 실행

					runCmdUtil.runCmd(fileVo.getFilePath(), outFilePath + File.separator + rPath + File.separator);
					break;
				} else {
					LOGGER.debug("============ not file=================== ");
					if (fileCnt < Integer.parseInt(apiImportLoopCnt)) {
						LOGGER.debug("fileCnt>>>>>>>>>>>>>>>>>>>>>>" + fileCnt);

						fileCnt = fileCnt + 1;

						try {
							Thread.sleep(Integer.parseInt(apiImportSleepCnt));
						} catch (InterruptedException inteerE) {
							LOGGER.debug("inteerE Error", inteerE);
						}
					}

				}
			}

			// 여기에 생성된 swagger.json 파일을 읽어 스트링으로 뽑는 로직을 추가해 주세요 !!!!
			String swaggerJsonFilePath = outFilePath + File.separator + rPath + File.separator + apidocImportJsonFileNm;

			// InputStream is = new URL(vo.getUrlPath()).openStream();
			BufferedReader rd = null;
			String json = "";
			int errCnt = 0;
			boolean loofEnd = false;
			while (true) {
				try {
					File is = new File(swaggerJsonFilePath);
					rd = new BufferedReader(new FileReader(is));
					String jsonText = readAll(rd);

					final JsonToYaml j2y = new JsonToYaml.Default();
					json = j2y.toYaml(jsonText);

					LOGGER.debug("yamlData => {} ", json);
				} catch (Exception e) {
					if (errCnt < Integer.parseInt(apiImportLoopCnt)) {
						LOGGER.debug("errCnt>>>>>>>>>>>>>>>>>>>>>>" + errCnt);
						errCnt = errCnt + 1;
						loofEnd = false;
						// 파일이 없을 경우 3초 대기후 다시 조회
						try {
							Thread.sleep(Integer.parseInt(apiImportSleepCnt));
						} catch (InterruptedException inteerE) {
							LOGGER.debug("inteerE Error", inteerE);
						}
					} else {
						loofEnd = true;
					}
				} finally {
					if (rd != null) {
						try {
							rd.close();
							// json 파일 삭제 시작
							LOGGER.debug("file delete");
							if (newF.exists() == true) {
								// 생성 되었던 폴더 삭제
								File[] innerFiles = newF.listFiles();
								LOGGER.debug("file length : " + innerFiles.length);
								// 하위 디렉토리 삭제
								for (int i = 0; i < innerFiles.length; i++) {
									//-- [tag:SR-20210915][sparrow][correction]
									if (true == innerFiles[i].exists()) { 
										if (!innerFiles[i].delete()) {
											LOGGER.debug("delete sub file error");
										}
									}
								}
								// 폴더 삭제
								if (!newF.delete()) {
									LOGGER.debug("delete file error");
								}
							} else {
								LOGGER.debug("js file not exists @@@@");
							}
							// json 파일 삭제 종료
							// js 파일 삭제 시작
							File file = new File(apidocServerHost + fileVo.getFilePath() + File.separator + fileVo.getSaveFileName());
							LOGGER.debug("js file full path ; {}", apidocServerHost + fileVo.getFilePath() + File.separator + fileVo.getSaveFileName());
							if (file.exists() == true) {
								LOGGER.debug("js file exists @@@@");
								if (file.delete()) {
									LOGGER.debug("js file delete success @@@@");
								} else {
									LOGGER.debug("js file delete fail @@@@");
								}
							} else {
								LOGGER.debug("js file not exists @@@@");
							}
							// js 파일 삭제 종료
							// 정상적으로 파일을 읽은 뒤에 파일 삭제 후에 while문 빠져나감
							break;
						} catch (IOException e) {
							//-- [tag:SR-20210915][sparrow][correction]
							LOGGER.debug("\n\n### ApiRegServiceImpl.regApidocAjax() [IOException: {}] ###\n", e);
						}
					}
					// 파일을 찾지 못하였어도 생성시켜준 파일은 삭제
					if (loofEnd) {
						map.put("successYn", "N");
						map.put("jsonStr", "");

						// json 파일 삭제 시작
						LOGGER.debug("file delete");
						if (newF.exists() == true) {
							// 생성 되었던 폴더 삭제
							File[] innerFiles = newF.listFiles();
							LOGGER.debug("file lenth : " + innerFiles.length);
							// 하위 디렉토리 삭제
							for (int i = 0; i < innerFiles.length; i++) {
								innerFiles[i].delete();
							}
							// 폴더 삭제

							if (!newF.delete()) {
								LOGGER.debug("delete file error");
							}
						} else {
							LOGGER.debug("js file not exists @@@@");
						}
						// json 파일 삭제 종료
						// js 파일 삭제 시작
						File file = new File(
								apidocServerHost + fileVo.getFilePath() + File.separator + fileVo.getSaveFileName());
						LOGGER.debug("js file full path ; {}",
								apidocServerHost + fileVo.getFilePath() + File.separator + fileVo.getSaveFileName());
						if (file.exists() == true) {
							LOGGER.debug("js file exists @@@@");

							if (file.delete()) {
								LOGGER.debug("js file delete success @@@@");
							} else {
								LOGGER.debug("js file delete fail @@@@");
							}

						} else {
							LOGGER.debug("js file not exists @@@@");
						}
						// js 파일 삭제 종료
						// 정상적으로 파일을 읽은 뒤에 파일 삭제 후에 while문 빠져나감
						break;
					}
				}
			}

			map.put("jsonStr", json);
			map.put("successYn", "Y");

		}
		return map;
	}
	
	//--[tag:adpt][add]
	@Override
	public String selNextApiId(String prefix) throws Exception {
		return apiRegDAO.selNextApiId(prefix);
	}

	//-- [tag:SR-20220328]
	@Override
	//-- [tag:SR-20220328]
	public Map<String, Object> selNextApiIdInfo(Map<String, Object> map_in) throws Exception {
		return apiRegDAO.selNextApiIdInfo(map_in);
	}

	//--[tag:adpt][add]
	@Override
	public List<Map<String, Object>> selDeployProc(Map<String, Object> map_in) throws Exception {
		return apiRegDAO.selDeployProc(map_in);
	}

	//--[tag:adpt][chg][public -> private]
	private String replaceYamlStr(String yamlStr) throws Exception {
		
		String newYamlStr = yamlStr;
		newYamlStr = newYamlStr.replaceAll("&quot;", "\"");
		newYamlStr = newYamlStr.replaceAll("&apos;", "'");
		newYamlStr = newYamlStr.replaceAll("&lt;", "<");
		newYamlStr = newYamlStr.replaceAll("&gt;", ">");
		//-- [tag:adpt][drm][add]
		newYamlStr = newYamlStr.replaceAll("&amp;", "&");

		return newYamlStr;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : setSetPathParamArrayObject
	* 2. 작성일 : 2017. 12. 20. 오후 4:05:44
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :
	* </pre>
	* @param objMap
	* @param vo
	* @param sortOrder
	* @param objNo
	* @param objOdrg
	* @throws Exception
	*/
	//--[tag:adpt][chg][public -> private]
	private int setSetPathParamArrayObject(HashMap<String,Object> objMap, ApiRegVO vo, int sortOrder, int deptInt, int objNo) throws Exception {
		LOGGER.debug("setSetPathParamArrayObject START =================================================================");
		LOGGER.debug("deptInt ={}", deptInt);

		int newSortOrder = sortOrder;
		int newDeptInt = deptInt;
		//-- [drm][add]
		//-- 그룹내순번증가
		int objOdrg = KsmUtil.parseInt(vo.getObjOdrg(), 0) + 1;
		if (objMap != null) {
			String dataType = getNullToString(objMap.get("type"));
			LOGGER.debug("type=> {}", dataType);
			if (dataType.equals("array") == true) {
				objOdrg = 1;
				HashMap<String,Object> itemMap = (HashMap<String,Object>)objMap.get("items");
				LOGGER.debug("	items=> {}", itemMap);
				//-- [i]typecase-상위array->all
				//-- [i]array하위: type, x-추가항목 제외하고 상위의값 설정
				ApiRegVO paramRegVO = new ApiRegVO();
				paramRegVO.setApiNo(vo.getApiNo());	// ApiNo
				paramRegVO.setParamTypeCd(vo.getParamTypeCd());	// 파라미터타입코드
				paramRegVO.setSortOdrg(newSortOrder + "");	// 순서
				paramRegVO.setParamNm(vo.getParamNm());	// 파라미터명
				//-- [v][~][bug]paramRegVO.setDataTypeCd(dataType);	// 데이터타입
				//-- [v][~][bug]paramRegVO.setParamDesc(itemMap.get("type") + "");
				paramRegVO.setDataTypeCd(itemMap.get("type") + "");	// 데이터타입
				paramRegVO.setParamDesc(getNullToString(vo.getParamDesc()));	// 파라미터설명
				//-- [v][~]paramRegVO.setExam("");
				paramRegVO.setExam(getNullToString(vo.getExam()));	// 예제
				paramRegVO.setPrntsParamNo(vo.getPrntsParamNo());	// 부모파라미터번호
				//-- [v][+]paramRegVO.setRegDt();	// 작성시간
				paramRegVO.setRegr(vo.getRegr());	// 작성자
				//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
				paramRegVO.setResCd(vo.getResCd());	// 응답상태
				//-- [v][~]paramRegVO.setResDesc("");
				paramRegVO.setResDesc(vo.getResDesc());	// 응답설명
				paramRegVO.setParamLoc(vo.getParamLoc());	// 파라미터위치
				//-- [v][+]
				paramRegVO.setObjNo(vo.getObjNo());	// 그룹번호
				//-- [v][.][하단에서이동][chg][sortOrder -> objOdrg)
				paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
				//-- [tag:adpt][drm][add] {
				paramRegVO.setRequired(this.fn_fmt_required(objMap.get("x-required")));
				paramRegVO.setPersonalData(getNullToString(objMap.get("x-personalData")));
				paramRegVO.setDoNotSend(getNullToString(objMap.get("x-doNotSend")));
				paramRegVO.setFixedValue(getNullToString(objMap.get("x-fixedValue")));
				paramRegVO.setHidden(getNullToString(objMap.get("x-hidden")));
				paramRegVO.setMappingKey(getNullToString(objMap.get("x-mappingKey")));
				paramRegVO.setBigo(getNullToString(objMap.get("x-bigo")));
				//-- [tag:SR-20210222][add] {
				paramRegVO.setHdpUrlDecode(getNullToString(objMap.get("x-urlDec")));
				paramRegVO.setHdpUrlEncode(getNullToString(objMap.get("x-urlEnc")));
				paramRegVO.setHdpUploadTarget(getNullToString(objMap.get("x-uploadTarget")));
				//-- [tag:SR-20210222][add] }
				//--[20201023][!@@!] 파라미터 sandbox 적용 여부
				paramRegVO.setParamSandboxYn(getNullToString(objMap.get("x-paramSandboxYn"))); 
				//-- [tag:adpt][drm][add] }

				//-- [drm][chg][?]objNo는 호출처에서 복귀이후 ++ 을 처리하므로 변동되면 안되는것아닌지?
				/*--
				if (deptInt != 1) {	// 하위 일 경우
					//-- [drm][chg][?]하위인데 상위번호가 없을수 있는지
					if (getNullToString(vo.getObjNo()).equals("") == false) {	// 상위의 그룹번호가 존재
						paramRegVO.setObjNo(vo.getObjNo());
					}
					else {	// 상위의 그룹번호가 없음
						objNo = objNo + 1;
						paramRegVO.setObjNo((objNo + 1) + "");
					}
					paramRegVO.setObjNo(vo.getObjNo());
				}
				else {	// 처음일 경우
					paramRegVO.setObjNo(objNo + "");	// 그룹번호
				}
				--*/
				//-- [v][-][상단으로이동]paramRegVO.setObjOdrg(sortOrder + "");	// 그룹내 순번

				//-- [#]savApiParamInfo-[array]
				apiRegDAO.savApiParamInfo(paramRegVO);
				String paramNo = paramRegVO.getParamNo();
				newSortOrder++;
				paramRegVO.setPrntsParamNo(paramNo);	// 부모파라미터번호
				//-- [drm][add] for상위복귀후처리
				vo.setParamNo(paramNo);	

				newDeptInt++;
				newSortOrder = setSetPathParamArrayObject(itemMap, paramRegVO, newSortOrder, newDeptInt , objNo);
			}	//-- if (type.equals("array") == true) {
			else if(dataType.equals("object") == true) {
				objOdrg = 1;
				HashMap<String,Object> propertiesMap = (HashMap<String,Object>)objMap.get("properties");
				ArrayList<String> propertiesNameList = YamlToJava.getMapKeyReuturnArrayList(propertiesMap,null);
				for (String propertyName : propertiesNameList) {
					LOGGER.debug("	propertyName_1=> {}", propertyName);
					HashMap<String,Object> proMap = (HashMap<String,Object>)propertiesMap.get(propertyName);
					String propertyDataType = getNullToString(proMap.get("type"));
					if ((propertyDataType.equals("array") == true) || (propertyDataType.equals("object") == true)) {
						//-- [i]typecase-상위object-object/array
						ApiRegVO paramRegVO = new ApiRegVO();
						paramRegVO.setApiNo(vo.getApiNo());	// ApiNo
						paramRegVO.setParamTypeCd(vo.getParamTypeCd());	// 파라미터타입코드
						paramRegVO.setSortOdrg(newSortOrder + "");	// 순서
						paramRegVO.setParamNm(propertyName);	// 파라미터명
						paramRegVO.setDataTypeCd(propertyDataType);	// 데이터타입
						paramRegVO.setParamDesc(getNullToString(proMap.get("description")));	// 파라미터설명
						paramRegVO.setExam(getNullToString(proMap.get("x-example")));	// 예제
						paramRegVO.setPrntsParamNo(vo.getPrntsParamNo());	// 부모파라미터번호
						//-- [v][+]paramRegVO.setRegDt();	// 작성시간
						paramRegVO.setRegr(vo.getRegr());	// 작성자
						//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
						paramRegVO.setResCd(vo.getResCd());	// 응답상태
						//-- [v][~]paramRegVO.setResDesc("");
						paramRegVO.setResDesc(vo.getResDesc());	// 응답설명
						paramRegVO.setParamLoc(vo.getParamLoc());	// 파라미터위치
						//-- [v][+]
						paramRegVO.setObjNo(vo.getObjNo());	// 그룹번호
						//-- [v][+]
						paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
						//-- [tag:adpt][drm][add] {
						paramRegVO.setRequired(this.fn_fmt_required(proMap.get("x-required")));
						//-- [drm][add]
						if (propertyDataType.equals("object") == false) {
							paramRegVO.setPersonalData(getNullToString(proMap.get("x-personalData")));
						}
						paramRegVO.setDoNotSend(getNullToString(proMap.get("x-doNotSend")));
						//-- [drm][add]
						if (propertyDataType.equals("object") == false) {
							paramRegVO.setFixedValue(getNullToString(proMap.get("x-fixedValue")));
						}
						paramRegVO.setHidden(getNullToString(proMap.get("x-hidden")));
						paramRegVO.setMappingKey(getNullToString(proMap.get("x-mappingKey")));
						paramRegVO.setBigo(getNullToString(proMap.get("x-bigo")));
						//-- [tag:SR-20210222][add] {
						paramRegVO.setHdpUrlDecode(getNullToString(proMap.get("x-urlDec")));
						paramRegVO.setHdpUrlEncode(getNullToString(proMap.get("x-urlEnc")));
						paramRegVO.setHdpUploadTarget(getNullToString(proMap.get("x-uploadTarget")));
						//-- [tag:SR-20210222][add] }
						//--[20201023][!@@!] 파라미터 sandbox 적용 여부
						paramRegVO.setParamSandboxYn(getNullToString(proMap.get("x-paramSandboxYn")));
						//-- [tag:adpt][drm][add] }

						//-- [drm][chg][?]objNo는 호출처에서 복귀이후 ++ 을 처리하므로 변동되면 안되는것아닌지?
						/*--
						if (deptInt != 1) {	// 하위 일 경우
							//-- [drm][chg][?]하위인데 상위번호가 없을수 있는지
							if (getNullToString(vo.getObjNo()).equals("") == false) {	// 상위의 그룹번호가 존재
								paramRegVO.setObjNo(vo.getObjNo());
							}
							else {	// 상위의 그룹번호가 없음
								objNo = objNo + 1;
								paramRegVO.setObjNo((objNo + 1) + "");	
							}
						}
						else {	// 처음일 경우
							paramRegVO.setObjNo(objNo + "");	// 그룹번호
						}
						--*/
		
						//-- [#]savApiParamInfo-[object - not primitive]
						apiRegDAO.savApiParamInfo(paramRegVO);
						String paramNo = paramRegVO.getParamNo();
						newSortOrder++;
						paramRegVO.setPrntsParamNo(paramNo);	// 부모파라미터번호
						//-- [drm][add] for상위복귀후처리
						vo.setParamNo(paramNo);	

						newDeptInt++;

						//-- [tag:job-20200728][chg][i][array일경우 param정보가 2중으로 등록되는 현상 // array of object일시는 proMap, 아니면 proMap.get("items")를 전달
						if (propertyDataType.equals("array") == true) {
							HashMap<String,Object> itemMap = (HashMap<String,Object>)proMap.get("items");
							String itemsDataType = getNullToString(itemMap.get("type"));
							if (itemsDataType.equals("object") == true) {
								newSortOrder = setSetPathParamArrayObject(proMap, paramRegVO, newSortOrder, newDeptInt , objNo);
							}
							else {
								newSortOrder = setSetPathParamArrayObject((HashMap<String,Object>)proMap.get("items"), paramRegVO, newSortOrder, newDeptInt , objNo);
							}
						}
						else {
							newSortOrder = setSetPathParamArrayObject(proMap, paramRegVO, newSortOrder, newDeptInt , objNo);
						}
						//--##sortOrder = setSetPathParamArrayObject(proMap, paramRegVO, sortOrder, deptInt , objNo);
					}
					else {
						//-- [i]typecase-object-primitive
						ApiRegVO paramRegVO = new ApiRegVO();
						paramRegVO.setApiNo(vo.getApiNo());	// ApiNo
						paramRegVO.setParamTypeCd(vo.getParamTypeCd());	// 파라미터타입코드
						paramRegVO.setSortOdrg(newSortOrder + "");	// 순서
						paramRegVO.setParamNm(propertyName);	// 파라미터명
						paramRegVO.setDataTypeCd(propertyDataType);			// 데이터타입
						paramRegVO.setParamDesc(getNullToString(proMap.get("description")));	// 파라미터설명
						paramRegVO.setExam(getNullToString(proMap.get("x-example")));			// 예제
						paramRegVO.setPrntsParamNo(vo.getPrntsParamNo());						// 부모파라미터번호
						//-- [v][+]paramRegVO.setRegDt();	// 작성시간
						paramRegVO.setRegr(vo.getRegr());	// 작성자
						//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
						paramRegVO.setResCd(vo.getResCd());	// 응답상태
						//-- [v][~]paramRegVO.setResDesc("");
						paramRegVO.setResDesc(vo.getResDesc());	// 응답설명
						paramRegVO.setParamLoc(vo.getParamLoc());	// 파라미터위치
						paramRegVO.setObjNo(vo.getObjNo());	// 그룹번호
						//-- [v][~]paramRegVO.setObjOdrg(vo.getObjOdrg());
						paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
						//-- [tag:adpt][drm][add] {
						paramRegVO.setRequired(this.fn_fmt_required(proMap.get("x-required")));
						paramRegVO.setPersonalData(getNullToString(proMap.get("x-personalData")));
						paramRegVO.setDoNotSend(getNullToString(proMap.get("x-doNotSend")));
						paramRegVO.setFixedValue(getNullToString(proMap.get("x-fixedValue")));
						paramRegVO.setHidden(getNullToString(proMap.get("x-hidden")));
						paramRegVO.setMappingKey(getNullToString(proMap.get("x-mappingKey")));
						paramRegVO.setBigo(getNullToString(proMap.get("x-bigo")));
						//-- [tag:SR-20210222][add] {
						paramRegVO.setHdpUrlDecode(getNullToString(proMap.get("x-urlDec")));
						paramRegVO.setHdpUrlEncode(getNullToString(proMap.get("x-urlEnc")));
						paramRegVO.setHdpUploadTarget(getNullToString(proMap.get("x-uploadTarget")));
						//-- [tag:SR-20210222][add] }
						//--[20201023][!@@!] 파라미터 sandbox 적용 여부
						paramRegVO.setParamSandboxYn(getNullToString(proMap.get("x-paramSandboxYn")));
						//-- [tag:adpt][drm][add] }

						//-- [#]savApiParamInfo-[object - primitive]
						apiRegDAO.savApiParamInfo(paramRegVO);
						String paramNo = paramRegVO.getParamNo();
						newSortOrder++;
						paramRegVO.setPrntsParamNo(paramNo);	// 부모파라미터번호
						//-- [drm][add] for상위복귀후처리
						vo.setParamNo(paramNo);	
						//-- [drm][add]
						objOdrg++;
					}
				}	//-- for (String propertyName : propertiesNameList) {
			}	//-- else if(type.equals("object") == true) {
			else {
				String s_parent_type = vo.getDataTypeCd();
				LOGGER.debug("    paramIndex : {}", objMap);
				LOGGER.debug("    name = {}",objMap.get("name"));
				LOGGER.debug("    map x-name = {}",objMap.get("x-name"));
				LOGGER.debug("    map description = {}",objMap.get("description"));
				LOGGER.debug("    map x-dataTypeCd = {}",objMap.get("x-dataTypeCd"));
				LOGGER.debug("    map type = {}",objMap.get("type"));
				LOGGER.debug("    map x-example = {}",objMap.get("x-example"));
				//-- [tag:adpt][drm][add] {
				LOGGER.debug("    map x-required : {}", objMap.get("x-required"));
				LOGGER.debug("    map x-personalData : {}", objMap.get("x-personalData"));
				LOGGER.debug("    map x-doNotSend : {}", objMap.get("x-doNotSend"));
				LOGGER.debug("    map x-fixedValue : {}", objMap.get("x-fixedValue"));
				LOGGER.debug("    map x-hidden : {}", objMap.get("x-hidden"));
				LOGGER.debug("    map x-mappingKey : {}", objMap.get("x-mappingKey"));
				LOGGER.debug("    map x-bigo : {}", objMap.get("x-bigo"));
				//-- [tag:SR-20210222][add] {
				LOGGER.debug("    map x-urlDec : {}", objMap.get("x-urlDec"));
				LOGGER.debug("    map x-urlEnc : {}", objMap.get("x-urlEnc"));
				LOGGER.debug("    map x-uploadTarget : {}", objMap.get("x-uploadTarget"));
				//-- [tag:SR-20210222][add] }
				//--[20201023][!@@!] 파라미터 sandbox 적용 여부
				LOGGER.debug("    map x-paramSandboxYn : {}", objMap.get("x-paramSandboxYn"));
				//-- [tag:adpt][drm][add] }

				//-- [i]typecase-array/object->primitive
				ApiRegVO paramRegVO = new ApiRegVO();
				paramRegVO.setApiNo(vo.getApiNo());	// ApiNo
				paramRegVO.setParamTypeCd(vo.getParamTypeCd());	// 파라미터타입코드
				paramRegVO.setSortOdrg(getNullToString(newSortOrder));	// 순서
				String paramNm = vo.getParamNm();
				if (getNullToString(objMap.get("x-name")).length() > 0) {
					paramNm = getNullToString(objMap.get("x-name"));
				}
				paramRegVO.setParamNm(paramNm);	// 파라미터명
				paramRegVO.setDataTypeCd(getNullToString(objMap.get("type")));			// 데이터타입
				//-- [drm][add][i]array of의 primitive일경우 상위 description설정
				if (getNullToString(vo.getDataTypeCd()).equals("array") == true) {
					paramRegVO.setParamDesc(getNullToString(vo.getParamDesc()));			// 파라미터설명
				}
				else {
					paramRegVO.setParamDesc(getNullToString(objMap.get("description")));	// 파라미터설명
				}
				paramRegVO.setExam(getNullToString(objMap.get("x-example")));			// 예제
				paramRegVO.setPrntsParamNo(vo.getPrntsParamNo());						// 부모파라미터번호
				//-- [v][+]paramRegVO.setRegDt();	// 작성시간
				paramRegVO.setRegr(vo.getRegr());	// 작성자
				//-- [v][+]paramRegVO.setAmdDt(); paramRegVO.setAmdr();
				paramRegVO.setResCd(vo.getResCd());	// 응답상태
				//-- [v][~]paramRegVO.setResDesc("");
				paramRegVO.setResDesc(vo.getResDesc());	// 응답설명
				paramRegVO.setParamLoc(vo.getParamLoc());	// 파라미터위치
				//-- [v][+]
				paramRegVO.setObjNo(vo.getObjNo());	// 그룹번호
				//-- [v][.][하단에서이동][chg][sortOrder -> objOdrg)
				paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
				//-- [tag:adpt][drm][add] {
				paramRegVO.setRequired(this.fn_fmt_required(objMap.get("x-required")));
				paramRegVO.setPersonalData(getNullToString(objMap.get("x-personalData")));
				paramRegVO.setDoNotSend(getNullToString(objMap.get("x-doNotSend")));
				paramRegVO.setFixedValue(getNullToString(objMap.get("x-fixedValue")));
				paramRegVO.setHidden(getNullToString(objMap.get("x-hidden")));
				paramRegVO.setMappingKey(getNullToString(objMap.get("x-mappingKey")));
				paramRegVO.setBigo(getNullToString(objMap.get("x-bigo")));
				//-- [tag:adpt][drm][add] }
				//-- [tag:SR-20210222][add] {
				paramRegVO.setHdpUrlDecode(getNullToString(objMap.get("x-urlDec")));
				paramRegVO.setHdpUrlEncode(getNullToString(objMap.get("x-urlEnc")));
				paramRegVO.setHdpUploadTarget(getNullToString(objMap.get("x-uploadTarget")));
				//-- [tag:SR-20210222][add] }
				//--[20201023][!@@!] 파라미터 sandbox 적용 여부
				paramRegVO.setSandboxYn(getNullToString(objMap.get("x-paramSandboxYn")));
				/*--
				//-- [drm][chg][?]하위인데 상위번호가 없을수 있는지
				if (getNullToString(vo.getObjNo()).equals("") == false) {	// 상위의 그룹번호가 존재
					paramRegVO.setObjNo(vo.getObjNo());	// 그룹번호
				}
				else {
					paramRegVO.setObjNo("");	// 그룹번호
				}
				paramRegVO.setObjOdrg(vo.getObjOdrg());	// 그룹내 순번
				--*/

				//-- [#]savApiParamInfo-[array - primitive]
				apiRegDAO.savApiParamInfo(paramRegVO);
				String paramNo = paramRegVO.getParamNo();
				newSortOrder++;
				paramRegVO.setPrntsParamNo(paramNo);	// 부모파라미터번호
				//-- [drm][add] for상위복귀후처리
				vo.setParamNo(paramNo);	
			}
		}
		LOGGER.debug("setSetPathParamArrayObject sortOrder={}", newSortOrder);
		LOGGER.debug("setSetPathParamArrayObject END =================================================================");
		
		return newSortOrder;
	}

	//--[tag:adpt][chg][public static -> private]
	private String getNullToString(Object targetStr) {
		String returnStr = "";
		if (targetStr != null) {
			returnStr = targetStr.toString();
		}
		return returnStr;
	}

	//--[tag:adpt][chg][private static -> private]
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * <pre>
	* 1. 메소드명 : addQnaFile
	* 2. 작성일 : 2017. 12. 1. 오후 4:07:24
	* 3. 작성자 : user
	* 4. 설명 : api doc 첨부파일 처리 
	* </pre>
	* @param uploadFile
	* @return
	* @throws Exception
	*/
	//--[tag:adpt][chg][public -> private]
	private CmnFileVo addApidocFile(MultipartFile uploadFile)  throws Exception {
		File tempFile = WebFileHelper.transferToTempFile(uploadFile);
		FileUploadInfo file = new FileUploadInfo(uploadFile.getOriginalFilename(), tempFile);
		CmnFileVo fileVo = uploadFileUtiles.apidocUpload(file);
		return fileVo;
	}

	//--[tag:adpt][add]
	/*
	 * yaml->map의 required를 DB저장 형태로 변관
	 */
	private String fn_fmt_required(Object required) {
		String s_required = getNullToString(required);
		if ("true".equalsIgnoreCase(s_required) == true) {
			s_required = "Y";
		} else if ("false".equalsIgnoreCase(s_required) == true) {
			s_required = "N";
		}
		return s_required;
	}

	//-- [tag:PRJ-20220901] {
	private ArrayList<String> fn_getAuthSysIdList(UserJoinVO userJoinVO) {
		//-- [i][API접근권한설정]
		boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
		ArrayList<String> authSysIdList= new ArrayList<String>();
		if (false == bIsObserver) {
			Iterator<AuthVO> authitr = userJoinVO.getAuthList().iterator();
			while (authitr.hasNext()) {
				AuthVO authVO = authitr.next();
				String sysId = authVO.getSysId();
				if (false == authSysIdList.contains(sysId)) {
					authSysIdList.add(sysId);
				}
			}
		}
		return authSysIdList;
	}
 
	/**
	* <pre>
	* 1. 메소드명 : selApiSpcAuthCheck
	* 2. 작성일 : 2020. 7. 15. 오후 1:20:37
	* 3. 작성자 : CYD
	* 4. 설명 : API명세 수정 권한 체크
	* </pre>
	* @param vo
	* @return int
	* @throws Exception
	*/
	@Override
	public int selApiSpcAuthCheck(ApiRegVO vo) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selApiSpcAuthCheck(vo);
	}

	/**
	* <pre>
	* 1. 메소드명 : selMbrAuthCheck
	* 2. 작성일 : 2020. 7. 15. 오후 1:20:37
	* 3. 작성자 : CYD
	* 4. 설명 : 관리자 권한 체크
	* </pre>
	* @param vo
	* @return int
	* @throws Exception
	*/
	@Override
	public int selMbrAuthCheck(ApiRegVO vo) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selMbrAuthCheck(vo);
	}

	//--[tab:job-20200714] {
	@Override
	public ApiDefVO selApiDef(ApiRegVO vo) throws Exception {
		return apiRegDAO.selApiDef(vo);
	}

	@Override
	public List<ApiDefVO> selApiDefList(ApiRegVO vo) throws Exception {
		return apiRegDAO.selApiDefList(vo);
	}
	//--[tab:job-20200714] }


	@Override
	public String selGrpAuthCheck(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selGrpAuthCheck(map);
	}


	@Override
	public String saveAutGrp(Map<String, Object> map) throws Exception {
		apiRegDAO.saveAutGrp(map);
		return (String) map.get("authId");
	}


	//-- [tag:SR-20210711]
	//-- ApiProvider목록검색 // KOA_TB_API_PROVIDER 
	@Override
	public ArrayList<HashMap<String, Object>> selApiProviderList() throws Exception {
		// TODO Auto-generated method stub
		
		List<Map<String, Object>> providerList = apiRegDAO.selApiProviderList();
		ArrayList<HashMap<String, Object>> returnList = new ArrayList<>();
		
		for(Map<String, Object> provider : providerList) {
			HashMap<String, Object> hMap = new HashMap<>(provider);
			hMap.put("bigo", CommonFunc.safeDbDecrypt(KsmUtil.fnSafeStr(provider.get("bigo"))));
			returnList.add(hMap);
		}
		
		return returnList;
	}


	@Override
	public ApiDefVO selectApiNmNoCheck(ApiRegVO vo) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map = apiRegDAO.selectApiNmNoCheck(vo);
		ApiDefVO defVO = null;
		if (map != null) {
			defVO = new ApiDefVO();
			defVO.setApiNo(KsmUtil.fnSafeStr(map.get("apiNo")));
			defVO.setApiNm(KsmUtil.fnSafeStr(map.get("apiNm")));
		}
		return defVO;
	}


	@Override
	public int selectApiNoCount(String apiNo) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selectApiNoCount(apiNo);
	}

	//중복된 API ID가 있는지 확인하는 용도 (재식)
	@Override
	public boolean selectApiIdChk(String apiId)throws Exception {
		int result = apiRegDAO.selectApiIdChk(apiId);
		boolean idCheckResult = true;
		if(result > 0) {
			idCheckResult = false;
		}
		return idCheckResult;
	}

	//-- [tag:PRJ-20220901] {
	@Override
	//-- API배포(BEAST)-R-목록
	public List<BstApiDeployVO> selApiDeployBeastList(Map<String, Object> map_in) throws Exception {
		List<BstApiDeployVO> listOut = beastDAO.selApiDeployBeastList(map_in);
		return listOut;
	}

	@Override
	//-- API배포(BEAST)-R-목록count
	public int selApiDeployBeastListCnt(Map<String, Object> map_in) throws Exception {
		return beastDAO.selApiDeployBeastCount(map_in);
	}

	@Override
	//-- API상태별갯수-목록
	public Map<String, Object> selBeastApiCountGroupByStatus(UserJoinVO userJoinVO) throws Exception {
		Map<String, Object> map_in = new HashMap<String, Object>();
		
		boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
		map_in.put("observerYn", (bIsObserver ? "Y" : "N"));
		if (false == bIsObserver) {
			map_in.put("authSysIdList", this.fn_getAuthSysIdList(userJoinVO));
		}

		List<BstApiStatusCountVO> listOut = beastDAO.selBeastApiCountGroupByStatus(map_in);

		int tb_deploy_ok_cnt = 0;
		int verify_ing_cnt = 0;
		int prd_deploy_req_cnt = 0;
		int prd_deploy_ok_cnt = 0;
		for (int n_ii = 0; n_ii < listOut.size();  n_ii++) {
			BstApiStatusCountVO bstApiStatusCountVO = listOut.get(n_ii);
			String dplyReqFlag = KsmUtil.fnSafeStr(bstApiStatusCountVO.getDplyReqFlag());	//-- API배포절차요청상태 ['': 초기(취소), REQ: 요청, NK: 반려, OK: 승인]
			if (false == "OK".equalsIgnoreCase(dplyReqFlag)) {
				continue;
			}
			int count = KsmUtil.parseInt(bstApiStatusCountVO.getCount(), 0);
			String tbDplyStatus = KsmUtil.fnSafeStr(bstApiStatusCountVO.getTbDplyStatus());	//-- API배포상태-TB ['': 초기, NK: 배포실패, OK: 배포성공]
			String dplyVeriStatus = KsmUtil.fnSafeStr(bstApiStatusCountVO.getDplyVeriStatus());	//-- API검증상태 ['': 초기, NK: 수행실패, OK: 수행성공]
			String prdDplyReqFlag = KsmUtil.fnSafeStr(bstApiStatusCountVO.getPrdDplyReqFlag());	//-- API상용배포요청상태 ['': 초기(취소), REQ: 요청, NK: 반려, OK: 승인]
			String prdDplyStatus = KsmUtil.fnSafeStr(bstApiStatusCountVO.getPrdDplyStatus());	//-- API배포상태-상용 ['': 초기, NK: 배포실패, OK: 배포성공]
			
			if ("OK".equalsIgnoreCase(tbDplyStatus)) {
				tb_deploy_ok_cnt++;
				if ((false == "REQ".equalsIgnoreCase(prdDplyReqFlag)) && (false == "OK".equalsIgnoreCase(prdDplyReqFlag))) {
					verify_ing_cnt++;
				}
			}
			if ("REQ".equalsIgnoreCase(prdDplyReqFlag)) {
				prd_deploy_req_cnt++;
			}
			if ("OK".equalsIgnoreCase(prdDplyStatus)) {
				prd_deploy_ok_cnt++;
			}
		}

		Map<String, Object> map_out = new HashMap<String, Object>();
		map_out.put("nlist", listOut);
		map_out.put("tb_deploy_ok_cnt", tb_deploy_ok_cnt);
		map_out.put("verify_ing_cnt", verify_ing_cnt);
		map_out.put("prd_deploy_req_cnt", prd_deploy_req_cnt);
		map_out.put("prd_deploy_ok_cnt", prd_deploy_ok_cnt);

		return map_out;
	}

	@Override
	//-- API배포(BEAST)-R-목록
	public List<BstApiTrafficVO> selectApitrafficSpclist(Map<String, Object> map_in) throws Exception {
		List<BstApiTrafficVO> listOut = beastDAO.select_apitraffic_spclist(map_in);
		return listOut;
	}

	@Override
	//-- API배포(BEAST)-R-목록
	public List<BstApiTrafficVO> selectApitrafficDeflist(Map<String, Object> map_in) throws Exception {
		List<BstApiTrafficVO> listOut = beastDAO.select_apitraffic_deflist(map_in);
		return listOut;
	}

	@Override
	//-- API-traffic정보-[direct // api | topn]-목록
	public List<BstApiTrafficVO> selectApitrafficData(Map<String, Object> map_in) throws Exception {
		String req_direct = KsmUtil.fnSafeStr(map_in.get("direct"));
		List<BstApiTrafficVO> listOut = null;
		if ("api".equals(req_direct)) {
			listOut = beastDAO.select_apitraffic_api(map_in);
		}
		else if ("topn".equals(req_direct)) {
			listOut = beastDAO.select_apitraffic_defapi(map_in);
		}
		else if ("topn_v1".equals(req_direct)) {
			listOut = beastDAO.select_apitraffic_defapi_v1(map_in);
		}
		return listOut;
	}

	@Override
	//-- /api/reg/{pathVal}/ajax_query.do
	public ModelMap ajaxQuery(HttpServletRequest request, String pathVal) {
		UserJoinVO userJoinVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
		userJoinVO = ((null != userJoinVO) ? userJoinVO : (new UserJoinVO()));

		ModelMap model = new ModelMap(); 
	
		int pageUnitVal = pageUnit;	// 페이지당 건수
		int pageSizeVal = pageSize;	// 페이지 리스트에 게시되는 건수

		pageUnit = KsmUtil.parseInt(request.getParameter("pageUnit"), pageUnit);
		pageSize = KsmUtil.parseInt(request.getParameter("pageSize"), pageSize);
		int pageIndex = KsmUtil.parseInt(request.getParameter("pageIndex"), 1);
		Pagination paginationInfo = new Pagination();

		String req_cmd = KsmUtil.fnSafeStr(request.getParameter("cmd"));
		Map<String, Object> map_in = new HashMap<>();

		LOGGER.debug("\n\n### {}.{}() [pathVal: {}][cmd: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), pathVal, req_cmd);

		if (true == "common".equals(pathVal)) {
			if (true == "selApiDef".equalsIgnoreCase(req_cmd)) {
				//--[i][from: regFormShareHead.jsp]
				//--[i] item정보 query
				ApiRegVO vo = new ApiRegVO();
				vo.setApiNo(request.getParameter("apiNo")); 
				try {
					model.addAttribute("apiDef", this.selApiDef(vo));
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
			}
		}
		else if (true == "apitrfgrp".equals(pathVal)) {
			//-- [i]API-traffic조회대상 카테고리-list
			if (true == "cmd_sel_api_category_list".equalsIgnoreCase(req_cmd)) {
				//--[i]API-traffic조회대상 API-list
				//-- [i]set observerYn, authSysIdList
				boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
				map_in.put("observerYn", (bIsObserver ? "Y" : "N"));
				if (false == bIsObserver) {
					map_in.put("authSysIdList", this.fn_getAuthSysIdList(userJoinVO));
				}

//				List<Map<String, Object>> list_out = new ArrayList<Map<String, Object>>();
				List<BstApiTrafficVO> list_out = new ArrayList<BstApiTrafficVO>();
				try {
					list_out = this.selectApitrafficSpclist(map_in);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}

				model.addAttribute("nlist", list_out);//목록 정보
			}
			else if (true == "cmd_sel_api_list".equalsIgnoreCase(req_cmd)) {
				//--[i]API-traffic조회대상 API-list
				map_in.put("apiSpcNo", request.getParameter("apiSpcNo"));
				//-- [i]set observerYn, authSysIdList
				boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
				map_in.put("observerYn", (bIsObserver ? "Y" : "N"));
				if (false == bIsObserver) {
					map_in.put("authSysIdList", this.fn_getAuthSysIdList(userJoinVO));
				}

//				List<Map<String, Object>> list_out = new ArrayList<Map<String, Object>>();
				List<BstApiTrafficVO> list_out = new ArrayList<BstApiTrafficVO>();
				try {
					list_out = this.selectApitrafficDeflist(map_in);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}

				model.addAttribute("nlist", list_out);//목록 정보
			}			
			else if ((true == "cmd_sel_api_traffic".equalsIgnoreCase(req_cmd))
				|| (true == "cmd_api_traffic_topn".equalsIgnoreCase(req_cmd))
				|| (true == "cmd_api_traffic_topn_v1".equalsIgnoreCase(req_cmd))) {
				//--[i]API-traffic조회대상 API-list
				map_in.put("apiSpcNo", request.getParameter("apiSpcNo"));
				//-- [i]set observerYn, authSysIdList
				boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
				map_in.put("observerYn", (bIsObserver ? "Y" : "N"));
				if (false == bIsObserver) {
					map_in.put("authSysIdList", this.fn_getAuthSysIdList(userJoinVO));
				}

				if (true == "cmd_sel_api_traffic".equalsIgnoreCase(req_cmd)) {
					//--[i]api traffic정보 query
					map_in.put("direct", "api"); 
					map_in.put("fdate", request.getParameter("fdate"));
					map_in.put("tdate", request.getParameter("tdate"));
					map_in.put("apiNm", request.getParameter("apiNm"));
				}
				if (true == "cmd_api_traffic_topn".equalsIgnoreCase(req_cmd)) {
					//--[i]topn traffic정보 query
					map_in.put("direct", "topn"); 
					map_in.put("fdate", request.getParameter("fdate"));
					map_in.put("tdate", request.getParameter("tdate"));
					map_in.put("top", request.getParameter("top"));
				}
				else if (true == "cmd_api_traffic_topn_v1".equalsIgnoreCase(req_cmd)) {
					//--[i]topn traffic정보 query
					map_in.put("direct", "topn_v1"); 
					map_in.put("fdate", request.getParameter("fdate"));
					map_in.put("tdate", request.getParameter("tdate"));
					map_in.put("top", request.getParameter("top"));
				}

//				List<Map<String, Object>> list_out = new ArrayList<Map<String, Object>>();
				List<BstApiTrafficVO> list_out = new ArrayList<BstApiTrafficVO>();
				try {
					list_out = this.selectApitrafficData(map_in);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
				model.addAttribute("nlist", list_out);
			}
			else if (true == "cmd_xxx".equalsIgnoreCase(req_cmd)) {
				//--[i]xxx정보 query
				map_in.put("key", request.getParameter("key"));

				List<Map<String, Object>> list_out = new ArrayList<Map<String, Object>>();
	
				try {
					//-- [2023:codeeyes][empty_block issue]
					//--@@list_out = this.select_XXX_List(map_in);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
				model.addAttribute("nlist", list_out);
			}
		}
		else if (true == "simpleapireg".equals(pathVal)) {
			if (true == "selApiSpcCateInfo".equalsIgnoreCase(req_cmd)) {
				//--[i][from: popSimpleApiReg.jsp]
				ApiRegVO vo = new ApiRegVO();
				vo.setApiSpcNo(request.getParameter("apiSpcNo"));
				vo.setApiCtgryNo(request.getParameter("apiCtgryNo"));
				try {
					ApiDefVO map_apiSpc = this.selApiInfo(vo);
					ApiCategoryVO map_cateInfo = this.selCateInfo(vo);
					model.addAttribute("apiSpc", map_apiSpc);
					model.addAttribute("cateInfo", map_cateInfo);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
			}
		}
		else if (true == "beastDeploy".equals(pathVal)) {
			if (true == "selApiDeployBeastList".equalsIgnoreCase(req_cmd)) {
				UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
				
				//--[i][from: /beast/deploy/deployList.jsp]
				//--[i] list정보 query
				map_in.put("dplyReqFlag", request.getParameter("dplyReqFlag"));
				map_in.put("tbDplyStatus", request.getParameter("tbDplyStatus"));
				map_in.put("dplyVeriStatus", request.getParameter("dplyVeriStatus"));
				map_in.put("prdDplyReqFlag", request.getParameter("prdDplyReqFlag"));
				map_in.put("prdDplyStatus", request.getParameter("prdDplyStatus"));
				map_in.put("sysId", request.getParameter("sysId"));
				map_in.put("apiSpcNo", request.getParameter("apiSpcNo"));
				map_in.put("dateFlag", request.getParameter("dateFlag"));
				map_in.put("fromDate", request.getParameter("fromDate"));
				map_in.put("toDate", request.getParameter("toDate"));
				map_in.put("apiNm", request.getParameter("apiNm"));

				boolean bIsObserver = ((userJoinVO != null) && ("Y".equalsIgnoreCase(userJoinVO.getObserverYn())));
				map_in.put("observerYn", (bIsObserver ? "Y" : "N"));
				if (false == bIsObserver) {
					map_in.put("authSysIdList", this.fn_getAuthSysIdList(userJoinVO));
				}

//				List<Map<String, Object>> list_out = new ArrayList<Map<String, Object>>();
				List<BstApiDeployVO> list_out = new ArrayList<BstApiDeployVO>();
				int totCnt = 0;
				try {
					totCnt = this.selApiDeployBeastListCnt(map_in);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
	
				paginationInfo.setPageSize(pageSize);
				paginationInfo.setTotalRecordCount(totCnt);
				paginationInfo.calculate();
				boolean b_use_Pagination = ("y".equalsIgnoreCase(request.getParameter("usePagination")));
				if (true == b_use_Pagination) {
					paginationInfo.setCurrentPageNo(pageIndex); // 현재 페이지 인덱스
					paginationInfo.setRecordCountPerPage(pageUnit);
				}
				else {
					paginationInfo.setCurrentPageNo(1);
					paginationInfo.setRecordCountPerPage((totCnt == 0) ? pageUnit : totCnt);
				}
				map_in.put("firstIndex", paginationInfo.getFirstRecordIndex());
				map_in.put("lastIndex", paginationInfo.getLastRecordIndex());
				map_in.put("recordCountPerPage", paginationInfo.getRecordCountPerPage());
				if (totCnt > 0) {
					try {
						list_out = this.selApiDeployBeastList(map_in);
					}
					catch (Exception e) {
						model.addAttribute("returnCd", "EXCEPT");
						model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
						return model;
					}
				}

				model.addAttribute("nlist", list_out);//목록 정보
				model.addAttribute("paginationInfo", paginationInfo);
			}			
		}
		else if (true == "eg_case".equals(pathVal)) {	//-- eg_case.jsp
		}
		
		return model;
	}

	@Override
	//-- /api/reg/{pathVal}/ajax_proc.do
	public ModelMap ajaxProc(HttpServletRequest request, String pathVal, String requestBody) {
		ModelMap model = new ModelMap(); 

		JSONObject jso_body = JSONObject.fromObject(requestBody);
		String req_cmd = jso_body.optString("cmd", "");

		String returnCd = "";
		String returnMsg = "";
		boolean b_is_err = false;

		Map<String, Object> map_in = new HashMap<>();

		LOGGER.debug("\n\n### {}.{}() [pathVal: {}][cmd: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), pathVal, req_cmd);

		if (true == "common".equals(pathVal)) {
			if (true == "cmd_api_deploy".equalsIgnoreCase(req_cmd)) {
				String req_api_no = jso_body.optString("api_no", "");
				String req_target = jso_body.optString("target", "");
				String req_dplytype = jso_body.optString("dplytype", "");
				
				int apiNo = KsmUtil.parseInt(req_api_no, -1);

				Map<String, Object> map_result = new HashMap<String, Object>();

				//-- [i]beast api deploy
				Map<String, Object> map_ret = beastService.bstgwApiDeploy(req_target, apiNo, req_dplytype);
				map_result.put("map_ret", map_ret);

				try {
					map_result.put("map_out", beastService.selDeployView(apiNo));
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
				model.addAttribute("result", map_result);
			}
			else if (true == "cmd_svc_deploy".equalsIgnoreCase(req_cmd)) {
				String req_devapply_seq = jso_body.optString("devapply_seq", "");
				String req_target = jso_body.optString("target", "");
				String req_dplytype = jso_body.optString("dplytype", "");
				
				int devapplySeq = KsmUtil.parseInt(req_devapply_seq, -1);

				Map<String, Object> map_result = new HashMap<String, Object>();

				//-- [i]beast svc deploy
				Map<String, Object> map_ret = beastService.bstgwSvcDeploy(req_target, devapplySeq, req_dplytype);
				map_result.put("map_ret", map_ret);

				model.addAttribute("result", map_result);
			}
			else if (true == "cmd_upd_dply_req_flag".equalsIgnoreCase(req_cmd)) {
				String req_api_no = jso_body.optString("api_no", "");
				String req_dply_req_flag = jso_body.optString("dply_req_flag", "");
				
				//-- [i]KOA_TB_API_DEF.DPLY_REQ_FLAG 수정
				ApiRegVO apiRegVO = new ApiRegVO();
				apiRegVO.setApiNo(req_api_no);
				apiRegVO.setDplyReqFlag(req_dply_req_flag);
				try {
					int nRet = apiRegDAO.updApiDefDplyReqFlag(apiRegVO);
					model.addAttribute("result", nRet);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
			}
			else if (true == "cmd_upd_prd_dply_req_flag".equalsIgnoreCase(req_cmd)) {
				String req_api_no = jso_body.optString("api_no", "");
				String req_prd_dply_req_flag = jso_body.optString("prd_dply_req_flag", "");
				
				//-- [i]KOA_TB_API_DEF.PRD_DPLY_REQ_FLAG 수정
				ApiRegVO apiRegVO = new ApiRegVO();
				apiRegVO.setApiNo(req_api_no);
				apiRegVO.setPrdDplyReqFlag(req_prd_dply_req_flag);
				try {
					int nRet = apiRegDAO.updApiDefPrdDplyReqFlag(apiRegVO);
					model.addAttribute("result", nRet);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
			}
			else if (true == "cmd_upd_def_deploy_flag".equalsIgnoreCase(req_cmd)) {
				String req_api_no = jso_body.optString("api_no", "");
				String req_target_field = jso_body.optString("target_field", "");
				String req_target_value = jso_body.optString("target_value", "");
				
				//-- [i]KOA_TB_API_DEF.PRD_DPLY_REQ_FLAG 수정
				ApiRegVO apiRegVO = new ApiRegVO();
				apiRegVO.setApiNo(req_api_no);
				int nRet = -1;
				try {
					if ("dply_req_flag".equals(req_target_field)) {
						apiRegVO.setDplyReqFlag(req_target_value);
						nRet = apiRegDAO.updApiDefDplyReqFlag(apiRegVO);
					}
					else if ("tb_dply_status".equals(req_target_field)) {
						apiRegVO.setTbDplyStatus(req_target_value);
						nRet = apiRegDAO.updApiDefTbDplyStatus(apiRegVO);
					}
					else if ("dply_veri_status".equals(req_target_field)) {
						apiRegVO.setDplyVeriStatus(req_target_value);
						nRet = apiRegDAO.updApiDefDplyVeriStatus(apiRegVO);
					}
					else if ("prd_dply_req_flag".equals(req_target_field)) {
						apiRegVO.setPrdDplyReqFlag(req_target_value);
						nRet = apiRegDAO.updApiDefPrdDplyReqFlag(apiRegVO);
					}
					else if ("prd_dply_status".equals(req_target_field)) {
						apiRegVO.setPrdDplyStatus(req_target_value);
						nRet = apiRegDAO.updApiDefPrdDplyStatus(apiRegVO);
					}
					else {
						returnCd = BstgwConstant.RETURN_CD.NK;
						returnMsg = "invalid target_field-[target_field: %s]".formatted(req_target_field);
					}

					Map<String, Object> map_result = new HashMap<String, Object>();
					map_result.put("ret", nRet);
					map_result.put("map_out", beastService.selDeployView(KsmUtil.parseInt(req_api_no, -1)));

					model.addAttribute("result", map_result);
				}
				catch (Exception e) {
					model.addAttribute("returnCd", "EXCEPT");
					model.addAttribute("returnMsg", "[%s][%s][%s]".formatted(pathVal, req_cmd, e.getMessage()));
					return model;
				}
			}
		}
		else if (true == "eg_case".equals(pathVal)) {	//-- eg_case.jsp
			if (true == "cmd_db_tran".equalsIgnoreCase(req_cmd)) {
				//-- [do_something]
			}
		}
		else {
			b_is_err = true; returnCd = "E01"; returnMsg = "pathVal not defined";
		}

		if (false == b_is_err) {
			returnCd = BstgwConstant.RETURN_CD.OK;
		}

		model.addAttribute("returnCd", returnCd);
		model.addAttribute("returnMsg", returnMsg);

		return model;
	}
	//-- [tag:PRJ-20220901] }
}
