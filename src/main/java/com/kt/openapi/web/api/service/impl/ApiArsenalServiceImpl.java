package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.api.dao.ApiMainDAO;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.service.ApiArsenalService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.Map.Entry;

@Service("apiArsenalService")
public class ApiArsenalServiceImpl implements ApiArsenalService {
	private static final Logger LOG = LoggerFactory.getLogger(ApiMainServiceImpl.class);
	
	@Autowired
	private ApiRegService apiRegService;

	@Autowired
	private ApiMainDAO apiMainDAO;

	@Autowired
	private ApiRegDAO apiRegDAO;
		
	@Autowired
	PlatformTransactionManager txManager;

	@Override
	public void getYamlInfoFromGitlab(String fileName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor=Exception.class, timeout=60)
	public void syncApiDep(HashMap<String,Object> map, ApiRegVO vo) throws Exception {
		// TODO Auto-generated method stub
		LOG.debug("==== syncApiDep ===========================  START ");
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus txStatus = txManager.getTransaction(txDefinition);
		
		String xApiNo	= "";
		String xApiVerNo	= "";
		// 1. API카테고리와  API정의에 존재하는 데이터 조회
		//ArrayList<EgovMap> apiList = (ArrayList<EgovMap>)apiMainDAO.selApiInfoList2(orgVo);
		try {

			// API_NO를 배열 초기화
			ArrayList<String> arrApiNo = new ArrayList<String>();
			HashMap<String,Object> pathsMap = (HashMap<String,Object>)map.get("paths");
			
			for(Entry<String, Object> eMap : pathsMap.entrySet()) {
				String pathsName = eMap.getKey();
				HashMap<String,Object> childPathMap = (HashMap<String,Object>)eMap.getValue();
				pathsName = pathsName.replaceAll(" ", "");
				
				LOG.debug("==== Yaml Path Info1 \n {}={} " ,pathsName, childPathMap);
				
				int pathOrder = 1;
				for(Entry<String, Object> eMap1 : childPathMap.entrySet()) {
					String subMethodName = eMap1.getKey();

					HashMap<String,Object> childPathMap1 = (HashMap<String,Object>)eMap1.getValue();
					LOG.debug("==== Yaml Path Info2 \n {}={} " ,subMethodName, childPathMap1);
					// Yama에서 API 가져옴
					xApiNo = getNullToString(childPathMap1.get("x-apiNo"));
					xApiVerNo = getNullToString(childPathMap1.get("x-apiVerNo"));
					
					// api정보(KOA_TB_API_DEF)
					String fvUseYn = "Y";
					String fvApiGubun = "APIGUB1010";
					if (xApiNo.length() > 0) {
						ApiRegVO apiRegDefVo = new ApiRegVO();
						apiRegDefVo.setApiNo(xApiNo);
						ApiDefVO map_apiDef = apiRegService.selApiDef(apiRegDefVo);  
						if (map_apiDef != null) {
							fvUseYn = map_apiDef.getUseYn() != null ? map_apiDef.getUseYn() : "Y";
							fvApiGubun = map_apiDef.getApiGubun() != null ? map_apiDef.getApiGubun() : fvApiGubun;
						}
					}

					// @@@save-KOA_TB_API_SPC
					ApiRegVO pathRegVO = new ApiRegVO();
					//pathRegVO.setApiNo(xApiNo); 												// API 번호
					pathRegVO.setApiSpcNo(vo.getApiSpcNo()); 									// API 명세 : apiSpcNo
					pathRegVO.setApiNm(getNullToString((childPathMap1.get("summary")))); 		// API명
					pathRegVO.setApiCtgryNo(vo.getApiCtgryNo());								//--[i] apiRegDAO.savApiPathInfo에서는 직접적으로 쓰이지 않은
					pathRegVO.setApiCtgryNm(vo.getApiCtgryNm());								//--[i] apiRegDAO.savApiPathInfo에서 CTGRY_NM을 검색하여 API_CTGRY_NO를 입력하는 방식을 사용
					pathRegVO.setApiPath(pathsName); 											// path
					pathRegVO.setMethodCd(subMethodName);										// Method
					pathRegVO.setApiId(getNullToString((childPathMap1.get("operationId"))));	// API ID
					pathRegVO.setApiDesc(getNullToString((childPathMap1.get("description")))); 	// API설명
					pathRegVO.setRegr(vo.getRegr());											// 수정자
					pathRegVO.setSortOdrg(pathOrder + ""); 										// 순서
					//-- [tag:SR-20210222][chg]
					pathRegVO.setUseYn(fvUseYn);
					pathRegVO.setApiGubun(fvApiGubun);
					//-- [tag:SR-20210222][cmt]
					/*--
					pathRegVO.setUseYn(getNullToString((childPathMap1.get("x-display")))); 		// use_yn
					pathRegVO.setApiGubun(getNullToString((childPathMap1.get("x-visiblity")))); // Api 구분
					pathRegVO.setApiHandlerCd(vo.getApiHandlerCd()); 							//-- handler구분코드 COMMON, ANYCOMMON, KOS, KOSMOS
					pathRegVO.setEndpntMethodCd(vo.getEndpntMethodCd()); 						//-- Endpoint method (GET, POST...)
					pathRegVO.setEndpntTbUrl(vo.getEndpntTbUrl()); 								// - Endpoint TB Url
					pathRegVO.setEndpntPrdUrl(vo.getEndpntPrdUrl()); 							// - Endpoint PRD Url
					pathRegVO.setEndpntClientIp(vo.getEndpntClientIp()); 						//-- Endpoint클라이언트IP 매핑키
					pathRegVO.setEndpntTimeout(vo.getEndpntTimeout()); 							//-- Endpoint Timeout (msec)
					--*/
					
					String apiVer = KsmUtil.fmt_data(pathsName, "fmt_version_in_path");
					pathRegVO.setApiVer(apiVer);
					pathRegVO.setApiVerNo(xApiVerNo);

					//-- [tag:SR-20210222][cmt]
					/*--
					//-- response에서 결과mapping을 처리
					HashMap<String, Object> map_responses = (HashMap<String, Object>) childPathMap1.get("responses");
					ArrayList<String> alist_response = getMapKeyReuturnArrayList(map_responses, null);
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

					// Yaml 파일에 API가 존재를 하면 수정
					if (xApiNo.equalsIgnoreCase("") == false) {
						pathRegVO.setApiNo(xApiNo); 	// API 번호
						
						LOG.debug("==== Yaml Path DbInfo : {}" ,pathRegVO);
						apiRegDAO.updApiPathInfo(pathRegVO);
						apiRegDAO.delApiPathParam(pathRegVO);
					} 
					// 존재하지 않다면 신규추가
					else {
						apiRegDAO.savApiPathInfo(pathRegVO);
						xApiNo = pathRegVO.getApiNo();
						LOG.debug("==== New APIInfo : {}" ,xApiNo);

						childPathMap1.put("x-apiNo", xApiNo);
						childPathMap1.put("x-apiVerNo", xApiNo);
					}
					
					LOG.debug(" parameters 시작 ==================> ");
					int paramIndex = 1;
					int objNum = 1;
					String paramNo = "";
					// @@@save-req
					//-- request: query, header, path, body or formData
					ArrayList<HashMap<String, Object>> paramsList = (ArrayList<HashMap<String, Object>>) childPathMap1.get("parameters");
					if (paramsList != null) {
						for (HashMap<String, Object> paramMap : paramsList) {
							// LOG.debug(" paramMap : " + paramMap);
							if (paramMap.get("in") != null) {
								LOG.debug("    paramIndex : {}", paramIndex);
								LOG.debug("    in : {}", paramMap.get("in"));
								LOG.debug("    name : {}", paramMap.get("name"));
								LOG.debug("    description : {}", paramMap.get("description"));
								LOG.debug("    type : {}", paramMap.get("type"));
								LOG.debug("    require : {}", paramMap.get("required"));
								LOG.debug("    x-dataTypeCd : {}", paramMap.get("x-dataTypeCd"));
								LOG.debug("    x-example : {}", paramMap.get("x-example"));
								//-- [tag:SR-20210222][cmt]
								/*--
								//-- [tag:adpt][add] {
								LOG.debug("    x-require : {}", paramMap.get("x-require"));
								LOG.debug("    x-personalData : {}", paramMap.get("x-personalData"));
								LOG.debug("    x-doNotSend : {}", paramMap.get("x-doNotSend"));
								LOG.debug("    x-fixedValue : {}", paramMap.get("x-fixedValue"));
								LOG.debug("    x-hidden : {}", paramMap.get("x-hidden"));
								LOG.debug("    x-mappingKey : {}", paramMap.get("x-mappingKey"));
								LOG.debug("    x-bigo : {}", paramMap.get("x-bigo"));
								//-- [tag:adpt][add] }
								--*/

								String definName = "";
								String dataTypeCd = getNullToString(paramMap.get("type"));
								LOG.debug("    1dataTypeCd : {}", dataTypeCd);
								if ((getNullToString(paramMap.get("in"))).equals("body") == true) {
									// @@@save-reqbody
									HashMap<String, Object> schema = (HashMap<String, Object>) paramMap.get("schema");
									LOG.debug("    schema : {}", schema);
									LOG.debug("    2dataTypeCd : {}", dataTypeCd);
									dataTypeCd = getNullToString(schema.get("type"));
									if (schema.get("$ref") != null) {
										String ref = getNullToString(schema.get("$ref"));
										LOG.debug("    ref : {}", ref);
										ref = ref.replaceAll("#/definitions/", "");
										LOG.debug("    ref : {}", ref);
										HashMap<String, Object> definitionsMap = (HashMap<String, Object>) map.get("definitions");
										ArrayList<String> definitionsNameList = getMapKeyReuturnArrayList(definitionsMap, null);
										if (definitionsNameList.contains(ref) == true) {
											definName = ref;
										} else {
											definName = "";
										}
									}
								}
								if (dataTypeCd.equals("") == true) {
									dataTypeCd = "datatype-ref";
									LOG.debug("    3dataTypeCd : {}", dataTypeCd);
								}

								// @@@save-case-#1
								// @@@save-KOA_TB_API_PARAM-request
								//-- [i]typecase-all
								ApiRegVO paramRegVO = new ApiRegVO();
								paramRegVO.setApiNo(xApiNo); // ApiNo
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
								//-- [tag:SR-20210222][cmt]
								/*--
								//-- [tag:adpt][add] {
								//paramRegVO.setRequired(this.fn_fmt_required(paramMap.get("x-required")));
								paramRegVO.setPersonalData(getNullToString(paramMap.get("x-personalData")));
								paramRegVO.setDoNotSend(getNullToString(paramMap.get("x-doNotSend")));
								paramRegVO.setFixedValue(getNullToString(paramMap.get("x-fixedValue")));
								paramRegVO.setHidden(getNullToString(paramMap.get("x-hidden")));
								paramRegVO.setMappingKey(getNullToString(paramMap.get("x-mappingKey")));
								paramRegVO.setBigo(getNullToString(paramMap.get("x-bigo")));
								//-- [v][-][?]paramRegVO.setApiCtgryNo(vo.getApiCtgryNo());
								//-- [tag:adpt][add] }
								--*/

								//-- [#]savApiParamInfo-[request]
								apiRegDAO.savApiParamInfo(paramRegVO);
								paramNo = paramRegVO.getParamNo();
								paramIndex++; //-- [drm][add]++
								paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호

								if ((getNullToString(paramMap.get("in"))).equals("body") == true) {
									//-- content-type저장
									ArrayList<String> consumesList = (ArrayList<String>) childPathMap1.get("consumes");
									for (String consume : consumesList) {
										paramRegVO.setContTypeCd(consume);
										paramRegVO.setParamNo(paramNo);
										apiRegDAO.insApiParamContType(paramRegVO);
									}
								}
								if (definName.length() > 0) {
									//-- userdefine datatype저장
									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) map.get("definitions");
									HashMap<String, Object> definNameMap = (HashMap<String, Object>) definitionsMap.get(definName);
									// HashMap<String,Object> propertiesMap = (HashMap<String,Object>)paramMap.get("properties");
									LOG.debug("    definNameMap : {}", definNameMap);
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
										LOG.debug("    schemaMap : {}", schemaMap);
										// @@@save-objchild
										paramIndex = setSetPathParamArrayObject(schemaMap, paramRegVO, paramIndex, 1, objNum);
										objNum++;
									}
								}

							}
							//-- [drm][cmt][move to insert] paramIndex++;
						} //-- for (HashMap<String,Object> paramMap : paramsList) {
					} //-- if (paramsList != null) {
					
					
					LOG.debug("responses 시작 ========= ");
					HashMap<String, Object> responseMap = (HashMap<String, Object>) childPathMap1.get("responses");
					if (responseMap != null) {
						
						LOG.debug("responseMap ========= {}", responseMap);
						ArrayList<String> responseNameList = getMapKeyReuturnArrayList(responseMap, null); // 200 400 ..등등
						LOG.debug("responseNameList ========= {}", responseNameList);

						for (String resName : responseNameList) {
							HashMap<String, Object> resNameMap = (HashMap<String, Object>) responseMap.get(resName);
							if (resNameMap.get("headers") != null) {
								HashMap<String, Object> headerInfoMap = (HashMap<String, Object>) resNameMap.get("headers");
								ArrayList<String> headerParamNameList = getMapKeyReuturnArrayList(headerInfoMap, null); // 파라미터
								for (String paramName : headerParamNameList) {
									HashMap<String, Object> paramNameInfo = (HashMap<String, Object>) headerInfoMap.get(paramName);
									LOG.debug("    paramIndex : {}", paramIndex);
									LOG.debug("    headers param name = {}", paramName);
									LOG.debug("    headers param description = {}", paramNameInfo.get("description"));
									LOG.debug("    headers param x-dataTypeCd = {}", paramNameInfo.get("x-dataTypeCd"));
									LOG.debug("    headers param type = {}", paramNameInfo.get("type"));
									LOG.debug("    headers param require = {}", paramNameInfo.get("require"));
									LOG.debug("    headers param x-example = {}", paramNameInfo.get("x-example"));
									//-- [tag:SR-20210222][cmt]
									/*--
									//-- [tag:adpt][add] {
									LOG.debug("    headers param x-required : {}", paramNameInfo.get("x-required"));
									LOG.debug("    headers param x-personalData : {}", paramNameInfo.get("x-personalData"));
									LOG.debug("    headers param x-doNotSend : {}", paramNameInfo.get("x-doNotSend"));
									LOG.debug("    headers param x-fixedValue : {}", paramNameInfo.get("x-fixedValue"));
									LOG.debug("    headers param x-hidden : {}", paramNameInfo.get("x-hidden"));
									LOG.debug("    headers param x-mappingKey : {}", paramNameInfo.get("x-mappingKey"));
									LOG.debug("    headers param x-bigo : {}", paramNameInfo.get("x-bigo"));
									//-- [tag:adpt][add] }
									--*/

									String dataTypeCd = getNullToString(paramNameInfo.get("type"));
									// @@@save-KOA_TB_API_PARAM-response-header
									//-- [i]typecase-all(ex object)
									ApiRegVO paramRegVO = new ApiRegVO();
									paramRegVO.setApiNo(xApiNo); // ApiNo
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
									//-- [tag:SR-20210222][cmt]
									/*--
									//-- [tag:adpt][add] {
									//paramRegVO.setRequired(this.fn_fmt_required(paramNameInfo.get("x-required")));
									paramRegVO.setPersonalData(getNullToString(paramNameInfo.get("x-personalData")));
									paramRegVO.setDoNotSend(getNullToString(paramNameInfo.get("x-doNotSend")));
									paramRegVO.setFixedValue(getNullToString(paramNameInfo.get("x-fixedValue")));
									paramRegVO.setHidden(getNullToString(paramNameInfo.get("x-hidden")));
									paramRegVO.setMappingKey(getNullToString(paramNameInfo.get("x-mappingKey")));
									paramRegVO.setBigo(getNullToString(paramNameInfo.get("x-bigo")));
									//-- [tag:adpt][add] }
									--*/
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
								LOG.debug("    paramIndex : {}", schemaInfoMap);
								LOG.debug("    schema name = {}", schemaInfoMap.get("x-name"));
								LOG.debug("    schema description = {}", schemaInfoMap.get("description"));
								LOG.debug("    schema x-dataTypeCd = {}", schemaInfoMap.get("x-dataTypeCd"));
								LOG.debug("    schema type = {}", schemaInfoMap.get("type"));
								LOG.debug("    schema x-example = {}", schemaInfoMap.get("x-example"));
								//-- [tag:SR-20210222][cmt]
								/*--
								//-- [tag:adpt][add] {
								LOG.debug("    schema x-required : {}", schemaInfoMap.get("x-required"));
								LOG.debug("    schema x-personalData : {}", schemaInfoMap.get("x-personalData"));
								LOG.debug("    schema x-doNotSend : {}", schemaInfoMap.get("x-doNotSend"));
								LOG.debug("    schema x-fixedValue : {}", schemaInfoMap.get("x-fixedValue"));
								LOG.debug("    schema x-hidden : {}", schemaInfoMap.get("x-hidden"));
								LOG.debug("    schema x-mappingKey : {}", schemaInfoMap.get("x-mappingKey"));
								LOG.debug("    schema x-bigo : {}", schemaInfoMap.get("x-bigo"));
								//-- [tag:adpt][add] }
								--*/
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
									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) map.get("definitions");
									ArrayList<String> definitionsNameList = getMapKeyReuturnArrayList(definitionsMap, null);
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
								paramRegVO.setApiNo(xApiNo); // ApiNo
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
								//-- [tag:SR-20210222][cmt]
								/*--
								//-- [tag:adpt][add] {
								//paramRegVO.setRequired(this.fn_fmt_required(schemaInfoMap.get("x-required")));
								paramRegVO.setPersonalData(getNullToString(schemaInfoMap.get("x-personalData")));
								paramRegVO.setDoNotSend(getNullToString(schemaInfoMap.get("x-doNotSend")));
								paramRegVO.setFixedValue(getNullToString(schemaInfoMap.get("x-fixedValue")));
								paramRegVO.setHidden(getNullToString(schemaInfoMap.get("x-hidden")));
								paramRegVO.setMappingKey(getNullToString(schemaInfoMap.get("x-mappingKey")));
								paramRegVO.setBigo(getNullToString(schemaInfoMap.get("x-bigo")));
								//-- [tag:adpt][add] }
								--*/
								LOG.debug("    schema : {}", schemaInfoMap);

								if (definName.length() > 0) {
									//-- [#]savApiParamInfo-[response body userdefine datatype]
									apiRegDAO.savApiParamInfo(paramRegVO);
									paramNo = paramRegVO.getParamNo();
									paramIndex++;
									paramRegVO.setPrntsParamNo(paramNo); // 부모파라미터번호

									HashMap<String, Object> definitionsMap = (HashMap<String, Object>) map.get("definitions");
									HashMap<String, Object> definNameMap = (HashMap<String, Object>) definitionsMap.get(definName);
									LOG.debug("    definNameMap : {}", definNameMap);
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
									schemaInfoMap.put("produces", childPathMap1.get("produces"));
									ArrayList<String> producesList = (ArrayList<String>) childPathMap1.get("produces");
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
					LOG.debug("responses 끝  ========= ");
					
					// API 배열에 담기
					arrApiNo.add(xApiNo);
					
					pathOrder++;
					
					
					
					
					
					
					
					
					
					for(Entry<String, Object> eMap2 : childPathMap1.entrySet()) {
						String pathsName2 = eMap2.getKey();
						//HashMap<String,Object> childPathMap2 = (HashMap<String,Object>)eMap2.getValue();
						LOG.debug("==== Yaml Path Info3 : {}={} " ,pathsName2, eMap2.getValue());				
					}
				}
			}
			
			/* API DELETE
			 * 등록된 API와 역동기화한 Yaml파일의 API를 비교해서 없다면 삭제처리
			 *////////////////////////////////////////////////////////
			List<ApiDefVO> apiMap = apiRegDAO.selApiDefList(vo);
			
			ApiRegVO apiRegVO = null;
			if(apiMap != null && apiMap.size() > 0) apiRegVO = new ApiRegVO();
			
			if (apiMap != null) {
				for(ApiDefVO eMap : apiMap) {
					String apiNo = eMap.getApiNo();
					//LOG.debug("==== API LIST : {}={} " , "API_NO",  apiNo);
					if(arrApiNo.contains(apiNo) == false) {
						LOG.debug("==== DELETE API LIST : {}={} " , "API_NO",  apiNo);
						
						apiRegVO.setApiNo(apiNo);
						apiRegDAO.delApiPathParam(apiRegVO);
						apiRegDAO.delApiImpact(apiRegVO);
						apiRegDAO.delApiPath(apiRegVO);
					}
				}
			}
			////////////////////////////////////////////////////////
			
			// Yaml data Is Updated to DB
			apiRegDAO.updApiYamlInfo(vo);
			// Update history
			vo.setApiSttusCd("APISYN1010");
			vo.setDelr("A");
			vo.setMemo("-");
			apiRegDAO.updApiHisInfo(vo);
			
			txManager.commit(txStatus);
			LOG.debug("==== syncApiDep ===========================  COMMIT ");
		
		} catch (Exception e) {
			txManager.rollback(txStatus);
			
			// Update history
			vo.setApiSttusCd("APISYN1020");
			vo.setDelr("A");
			vo.setMemo(e.getMessage());
			apiRegDAO.updApiHisInfo(vo);
			LOG.debug("==== syncApiDep ===========================  ROLLBACK ");
			LOG.error("Exception==> Method:syncApiDep, {} ,{}", e.getMessage(), e.getStackTrace());
		}
		
		
		
		
		
		
		
		
		
		
		
		

		/*
		 * LOG.debug("==== apiList -- {}" , apiList.size());
		 * 
		 * for(EgovMap eMap : apiList) {
		 * 
		 * String apiNo = (eMap.get("apiNo")).toString(); String newApiNo = "";
		 * 
		 * if(!selApiNo.equals(apiNo)) {
		 * 
		 * orgVo.setApiNo(apiNo);
		 * 
		 * newApiNo = apiMainDAO.savApiVerUpApiDef(orgVo);
		 * 
		 * LOG.debug("==== ==== newApiNo = {} " , newApiNo );
		 * 
		 * orgVo.setApiNo(apiNo); orgVo.setNewApiNo(newApiNo);
		 * 
		 * //insertApiParam(orgVo);
		 * 
		 * String yamlSbst = orgVo.getYamlSbst(); String replaceStr1 = "apiNo: \'" +
		 * apiNo+"\'"; String replaceStr2 = "apiNo: \'" + newApiNo+"\'"; yamlSbst =
		 * yamlSbst.replaceAll(replaceStr1, replaceStr2 ); replaceStr1 = "x-apiNo: \'" +
		 * apiNo+"\'"; replaceStr2 = "x-apiNo: \'" + newApiNo+"\'"; yamlSbst =
		 * yamlSbst.replaceAll(replaceStr1, replaceStr2 ); ApiRegVO avo = new
		 * ApiRegVO(); avo.setYamlSbst(yamlSbst);
		 * avo.setApiSpcNo(orgVo.getNewApiSpcNo()); avo.setRegr(orgVo.getRegr());
		 * 
		 * int apiCnt = apiRegDAO.updApiYamlInfo(avo); // yaml 내용 중 apiNo도 최신 정보로 변경 한다.
		 * } selApiNo = apiNo; }
		 */
		
		
	}
	
	private String getNullToString(Object targetStr) {
		String returnStr = "";
		if (targetStr != null) {
			returnStr = targetStr.toString();
		}
		return returnStr;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : getMapKeyReuturnArrayList
	* 2. 작성일 : 2017. 11. 13. 오후 2:16:26
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 멥의 키값을 가져와서 ArrayList 로 반환
	* </pre>
	* @param targetMap
	* @param targetStr
	* @return
	*/
	@SuppressWarnings({"unchecked", "rawtypes" })
	public static ArrayList getMapKeyReuturnArrayList(HashMap<String,Object> targetMap, String targetStr) {
		ArrayList alist = new ArrayList();
		Set set = null;
		if(targetStr==null) {
			set = targetMap.keySet();
		}else {
			set = ((HashMap<String,Object>)targetMap.get(targetStr)).keySet();
		}
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){
		  String key = (String)iterator.next();
		  alist.add(key);
		}
		return alist;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : getStringVectorToString
	* 2. 작성일 : 2017. 11. 13. 오후 3:14:26
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : String[] or MAP or ArrayList 의 내용을 ArrayList로 반환. 
	* </pre>
	* @param targetStr
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getStringVectorToString(Object targetStr) {
		ArrayList returnArrayList 	= new ArrayList();
		if(targetStr!=null) {
			if(targetStr instanceof java.util.LinkedHashMap) {
				returnArrayList = getMapKeyReuturnArrayList((HashMap<String,Object>)targetStr, null);
			}
			
			if(targetStr instanceof java.util.ArrayList<?> tStr) {
				for(int i=0; i< tStr.size() ; i++) {
					returnArrayList.add(tStr.get(i));
				}
			}
			if(targetStr instanceof String[] tStr) {
				for(int i=0; i< tStr.length ; i++) {
					returnArrayList.add(tStr[i]);
				}
			}
		}else {
			returnArrayList = null;
		}
		return returnArrayList;
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
		LOG.debug("setSetPathParamArrayObject START =================================================================");
		LOG.debug("deptInt ={}", deptInt);

		//-- [drm][add]
		//-- 그룹내순번증가
		int newSortOrder = sortOrder;
		int newDeptInt = deptInt;
		int objOdrg = KsmUtil.parseInt(vo.getObjOdrg(), 0) + 1;
		if (objMap != null) {
			String dataType = getNullToString(objMap.get("type"));
			LOG.debug("type=> {}", dataType);
			if (dataType.equals("array") == true) {
				objOdrg = 1;
				HashMap<String,Object> itemMap = (HashMap<String,Object>)objMap.get("items");
				LOG.debug("	items=> {}", itemMap);
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
				//-- [v][.][하단에서이동][chg][newSortOrder -> objOdrg)
				paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
				//-- [tag:SR-20210222][cmt]
				/*--
				//-- [tag:adpt][add] {
				//paramRegVO.setRequired(this.fn_fmt_required(objMap.get("x-required")));
				paramRegVO.setPersonalData(getNullToString(objMap.get("x-personalData")));
				paramRegVO.setDoNotSend(getNullToString(objMap.get("x-doNotSend")));
				paramRegVO.setFixedValue(getNullToString(objMap.get("x-fixedValue")));
				paramRegVO.setHidden(getNullToString(objMap.get("x-hidden")));
				paramRegVO.setMappingKey(getNullToString(objMap.get("x-mappingKey")));
				paramRegVO.setBigo(getNullToString(objMap.get("x-bigo")));
				//-- [tag:adpt][add] }
				--*/

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
				//-- [v][-][상단으로이동]paramRegVO.setObjOdrg(newSortOrder + "");	// 그룹내 순번

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
				ArrayList<String> propertiesNameList = getMapKeyReuturnArrayList(propertiesMap,null);
				for (String propertyName : propertiesNameList) {
					LOG.debug("	propertyName=> {}", propertyName);
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
						//-- [drm][add]
						if (propertyDataType.equals("object") == false) {
							paramRegVO.setExam(getNullToString(proMap.get("x-example")));	// 예제
						}
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
						//-- [tag:SR-20210222][cmt]
						/*--
						//-- [tag:adpt][add] {
						//paramRegVO.setRequired(this.fn_fmt_required(proMap.get("x-required")));
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
						//-- [tag:adpt][add] }
						--*/

						//-- [drm][chg][?]objNo는 호출처에서 복귀이후 ++ 을 처리하므로 변동되면 안되는것아닌지?
						/*--
						if (newDeptInt != 1) {	// 하위 일 경우
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
						newSortOrder = setSetPathParamArrayObject(proMap, paramRegVO, newSortOrder, newDeptInt , objNo);
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
						//-- [tag:SR-20210222][cmt]
						/*--
						//-- [tag:adpt][add] {
						//paramRegVO.setRequired(this.fn_fmt_required(proMap.get("x-required")));
						paramRegVO.setPersonalData(getNullToString(proMap.get("x-personalData")));
						paramRegVO.setDoNotSend(getNullToString(proMap.get("x-doNotSend")));
						paramRegVO.setFixedValue(getNullToString(proMap.get("x-fixedValue")));
						paramRegVO.setHidden(getNullToString(proMap.get("x-hidden")));
						paramRegVO.setMappingKey(getNullToString(proMap.get("x-mappingKey")));
						paramRegVO.setBigo(getNullToString(proMap.get("x-bigo")));
						//-- [tag:adpt][add] }
						--*/

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
				LOG.debug("    paramIndex : {}", objMap);
				LOG.debug("    name = {}",objMap.get("name"));
				LOG.debug("    map x-name = {}",objMap.get("x-name"));
				LOG.debug("    map description = {}",objMap.get("description"));
				LOG.debug("    map x-dataTypeCd = {}",objMap.get("x-dataTypeCd"));
				LOG.debug("    map type = {}",objMap.get("type"));
				LOG.debug("    map x-example = {}",objMap.get("x-example"));
				//-- [tag:SR-20210222][cmt]
				/*--
				//-- [tag:adpt][add] {
				LOG.debug("    map x-required : {}", objMap.get("x-required"));
				LOG.debug("    map x-personalData : {}", objMap.get("x-personalData"));
				LOG.debug("    map x-doNotSend : {}", objMap.get("x-doNotSend"));
				LOG.debug("    map x-fixedValue : {}", objMap.get("x-fixedValue"));
				LOG.debug("    map x-hidden : {}", objMap.get("x-hidden"));
				LOG.debug("    map x-mappingKey : {}", objMap.get("x-mappingKey"));
				LOG.debug("    map x-bigo : {}", objMap.get("x-bigo"));
				//-- [tag:adpt][add] }
				--*/

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
				//-- [drm][add
				//-- [i]array of의 primitive일경우 상위 description,exam설정
				if (getNullToString(vo.getDataTypeCd()).equals("array") == true) {
					paramRegVO.setParamDesc(getNullToString(vo.getParamDesc()));	// 파라미터설명
					paramRegVO.setExam(getNullToString(vo.getExam()));			// 예제
				}
				else {
					paramRegVO.setParamDesc(getNullToString(objMap.get("description")));	// 파라미터설명
					paramRegVO.setExam(getNullToString(objMap.get("x-example")));			// 예제
				}
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
				//-- [v][.][하단에서이동][chg][newSortOrder -> objOdrg)
				paramRegVO.setObjOdrg(objOdrg + "");	// 그룹내순번
				//-- [tag:SR-20210222][cmt]
				/*--
				//-- [tag:adpt][add] {
				//paramRegVO.setRequired(this.fn_fmt_required(objMap.get("x-required")));
				paramRegVO.setPersonalData(getNullToString(objMap.get("x-personalData")));
				paramRegVO.setDoNotSend(getNullToString(objMap.get("x-doNotSend")));
				paramRegVO.setFixedValue(getNullToString(objMap.get("x-fixedValue")));
				paramRegVO.setHidden(getNullToString(objMap.get("x-hidden")));
				paramRegVO.setMappingKey(getNullToString(objMap.get("x-mappingKey")));
				paramRegVO.setBigo(getNullToString(objMap.get("x-bigo")));
				//-- [tag:adpt][add] }
				--*/
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
		LOG.debug("setSetPathParamArrayObject newSortOrder={}", newSortOrder);
		LOG.debug("setSetPathParamArrayObject END =================================================================");
		
		return newSortOrder;
	}

	@Override
	public ApiSpcVO selApiSpcInfoByProjectNsWithNm(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selApiSpcInfoByProjectNsWithNm(map);
	}

	@Override
	public List<ApiHistoryVO> selApiSpcHistory(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selApiSpcHistory(map);
	}

	@Override
	public List<ApiNamespaceVO> selApiCountAndInfoByProjectNs(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return apiRegDAO.selApiCountAndInfoByProjectNs(map);
	}

}
