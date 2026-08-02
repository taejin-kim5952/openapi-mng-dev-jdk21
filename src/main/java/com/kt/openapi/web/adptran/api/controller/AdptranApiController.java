package com.kt.openapi.web.adptran.api.controller;

import com.kt.openapi.web.adptran.api.AdptranApiConst;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.common.domain.ResultMessageWithData;
import com.kt.openapi.web.adptran.api.common.message.GenericMessage;
import com.kt.openapi.web.adptran.api.common.message.RestMessage;
import com.kt.openapi.web.adptran.api.service.AdptranApiService;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.apigw.entity.deploy.DeployResult;
import com.kt.openapi.web.apigw.entity.lamp.LampResponse;
import com.kt.openapi.web.apigw.services.lamp.LampLogService;
import com.kt.openapi.web.apigw.type.DeployJobStatus;
import com.kt.openapi.web.apigw.type.GwProfile;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RequestMapping(value = AdptranApiConst.ADPTRAN_API_PATH + AdptranApiConst.ADPTRAN_API_VERSION)
@RestController
public class AdptranApiController {

	private static final Logger logger = LoggerFactory.getLogger(AdptranApiController.class);

	//--##@Autowired
	@Autowired
	private AdptranApiService adptranApiService;

	@Autowired
	private LampLogService lampLogService;

	//-- API정보 select
	@RequestMapping(value = "/api/{apiNo}", method = RequestMethod.GET)
	public RestMessage select_api_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String apiNo) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_DEF_with_API_SPC(apiNo));
		return message;
	}
	@RequestMapping(value = "/api", method = RequestMethod.POST)
	public RestMessage select_api_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_DEF_with_API_SPC(req_api_no));
		return message;
	}

	//-- API parameter정보 select (not used yet) {
	@RequestMapping(value = "/apiParam/{apiNo}", method = RequestMethod.GET)
	public RestMessage select_apiParam_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String apiNo, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		final String con_param_loc_list = "header,body,query,path,formData";

		String req_param_loc_list = KsmUtil.fnSafeStr(param.get("param_loc_list"));

		req_param_loc_list = ((req_param_loc_list.length() == 0) ? con_param_loc_list : req_param_loc_list);
		String a_param_loc_list[] = req_param_loc_list.split(",");
		List<String> param_loc_list = new ArrayList<>();	//-- PARAM_LOC filter
		for (String item : a_param_loc_list) {
			if (("," + con_param_loc_list + ",").indexOf("," + item + ",") != -1) {
				param_loc_list.add(item);
			}
		}

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_PARAM_list(apiNo, param_loc_list));
		return message;
	}
	@RequestMapping(value = "/apiParam", method = RequestMethod.POST)
	public RestMessage select_apiParam_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		final String con_param_loc_list = "header,body,query,path,formData";

		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		String req_param_loc_list = KsmUtil.fnSafeStr(param.get("param_loc_list"));

		req_param_loc_list = ((req_param_loc_list.length() == 0) ? con_param_loc_list : req_param_loc_list);
		String a_param_loc_list[] = req_param_loc_list.split(",");
		List<String> param_loc_list = new ArrayList<>();	//-- PARAM_LOC filter
		for (String item : a_param_loc_list) {
			if (("," + con_param_loc_list + ",").indexOf("," + item + ",") != -1) {
				param_loc_list.add(item);
			}
		}

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_PARAM_list(req_api_no, param_loc_list));
		return message;
	}
	//-- API parameter정보 select (not used yet) }
	
	//-- API def + spc정보 select
	@RequestMapping(value = "/apiDefWithApiSpc/{apiNo}", method = RequestMethod.GET)
	public RestMessage select_apiDefWithApiSpc_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String apiNo) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_DEF_with_API_SPC(apiNo));
		return message;
	}
	@RequestMapping(value = "/apiDefWithApiSpc", method = RequestMethod.POST)
	public RestMessage select_apiDefWithApiSpc_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_DEF_with_API_SPC(req_api_no));
		return message;
	}

	//-- API testcase parameter정보 select
	@RequestMapping(value = "/apiParamTest/{apiNo}", method = RequestMethod.GET)
	public RestMessage select_apiParamTest_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String apiNo
		, @RequestParam(value="param_type_cd_list", required=false, defaultValue="PRMTYP1010,PRMTYP1020") String paramTypeCdList) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		//-- param_type_cd_list setting {		
		final String con_param_type_cd_list = "PRMTYP1010,PRMTYP1020";	//-- request, response
		List<String> param_type_cd_list = new ArrayList<>();	//-- PARAM_TYPE_CD filter
		String a_param_type_cd_list[] = paramTypeCdList.split(",");
		for (String item : a_param_type_cd_list) {
			if (("," + con_param_type_cd_list + ",").indexOf("," + item + ",") != -1) {
				param_type_cd_list.add(item);
			}
		}
		//-- param_type_cd_list setting }		

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_PARAM_TEST_list(apiNo, param_type_cd_list));
		return message;
	}
	@RequestMapping(value = "/apiParamTest", method = RequestMethod.POST)
	public RestMessage select_apiParamTest_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		String req_param_type_cd_list = KsmUtil.fnSafeStr(param.get("param_type_cd_list"));
		//--##req_param_type_cd_list = ((req_param_type_cd_list.length() == 0) ? con_param_type_cd_list : req_param_type_cd_list);

		//-- param_type_cd_list setting {		
		final String con_param_type_cd_list = "PRMTYP1010,PRMTYP1020";	//-- request, response
		List<String> param_type_cd_list = new ArrayList<>();	//-- PARAM_TYPE_CD filter
		String a_param_type_cd_list[] = req_param_type_cd_list.split(",");
		for (String item : a_param_type_cd_list) {
			if (("," + con_param_type_cd_list + ",").indexOf("," + item + ",") != -1) {
				param_type_cd_list.add(item);
			}
		}
		//-- param_type_cd_list setting }		
		
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_PARAM_TEST_list(req_api_no, param_type_cd_list));
		return message;
	}

	//-- API testcase 정보 select
	@RequestMapping(value = "/apiTestCase/{testcaseId}", method = RequestMethod.GET)
	public RestMessage select_apiTestCase_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String testcaseId) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_TESTCASE(testcaseId));
		return message;
	}
	@RequestMapping(value = "/apiTestCase", method = RequestMethod.POST)
	public RestMessage select_apiTestCase_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_testcase_id = KsmUtil.fnSafeStr(param.get("testcase_id"));
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_TESTCASE(req_testcase_id));
		return message;
	}
	
	//-- API testcase 정보 select list
	@RequestMapping(value = "/apiTestCaseList/{apiNo}", method = RequestMethod.GET)
	public RestMessage select_apiTestCaseList_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String apiNo) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_TESTCASE_list(apiNo));
		return message;
	}
	@RequestMapping(value = "/apiTestCaseList", method = RequestMethod.POST)
	public RestMessage select_apiTestCaseList_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_TESTCASE_list(req_api_no));
		return message;
	}

	//-- API testcase 정보 ins/upd/del
	@RequestMapping(value = "/apiTestCaseTrans/{trans}", method = RequestMethod.POST)
	public RestMessage trans_apiTestCaseTrans_by_post(GenericMessage message, HttpServletRequest request, @PathVariable String trans, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//-- single, multi delete 설정
		if ("del".equals(trans) == true) {
			String testcase_id_list = KsmUtil.fnSafeStr(param.get("testcase_id_list"));
			String[] a_testcase_id = testcase_id_list.split(",");
			if (a_testcase_id.length == 1) {
				param.put("testcase_id", a_testcase_id[0]);
			}
			else {
				param.put("paramTestcaseIdList", Arrays.asList(a_testcase_id));
			}
		}
		String ss_mbrid = KsmUtil.fnSafeStr(request.getSession().getAttribute("mbrId"));
		ss_mbrid = CommonFunc.safeDbEncrypt(ss_mbrid);
		param.put("mbrId", ss_mbrid);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.trans_API_TESTCASE(trans, param));
		return message;
	}

	//-- API 검증 정보 select
	@RequestMapping(value = "/apiVerify/{vefify_seq}", method = RequestMethod.GET)
	public RestMessage select_apiVerify_by_get(GenericMessage message, HttpServletRequest request, @PathVariable String vefify_seq) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_VERI_CONDITION(vefify_seq));
		return message;
	}
	@RequestMapping(value = "/apiVerify", method = RequestMethod.POST)
	public RestMessage select_apiVerify_by_post(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_vefify_seq = KsmUtil.fnSafeStr(param.get("vefify_seq"));
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(adptranApiService.select_API_VERI_CONDITION(req_vefify_seq));
		return message;
	}

	//-- API배포실행
	@RequestMapping(value = { "/apigw_deploy", }, method=RequestMethod.POST)
	public RestMessage apigw_deploy(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_proc_seq = KsmUtil.fnSafeStr(param.get("proc_seq"));
		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		String req_api_id = KsmUtil.fnSafeStr(param.get("api_id"));
		String req_gw_profile = KsmUtil.fnSafeStr(param.get("gw_profile"));
		String req_action_type = KsmUtil.fnSafeStr(param.get("action_type"));
		String req_async = KsmUtil.fnSafeStr(param.get("async"));
		String req_check_deploystatus = KsmUtil.fnSafeStr(param.get("checkd_eploystatus"));
		String ss_mbr_id = KsmUtil.fnSafeStr(request.getSession().getAttribute("mbrId"));
		ss_mbr_id = CommonFunc.safeDbEncrypt(ss_mbr_id);

		boolean b_is_deploy_call = true;
		AdptranApiResultCode resultMessage_resultCode = AdptranApiResultCode.INIT;

		ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
		if ("Y".equals(req_check_deploystatus) == true) {
			//-- "_proc_seq_": proccess를 update하지 않는다
			resultMessageWithData = adptranApiService.apigw_deployStatus("_proc_seq_", req_api_no, req_api_id, req_gw_profile, "_deployapply_seq_", ss_mbr_id);
			logger.info("\n\n### {}.{}() [req_proc_seq: {}][req_api_no: {}][ss_mbrid: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), req_proc_seq, req_api_no, ss_mbr_id, resultMessageWithData);

			Integer resultCode = resultMessageWithData.getResultCd();
			//--@@String resultMsg = resultMessageWithData.getResultMsg();
			if (resultCode == AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_SUCC.getCode()) {	//-- 배포상태조회 성공
				HashMap<String, Object> map_data = (HashMap<String, Object>)resultMessageWithData.getData();
				DeployResult deployResult = (DeployResult)map_data.get("deployresult");
				if (deployResult != null) {	//-- 배포상태 job이 있음
					DeployJobStatus deployJobStatus = deployResult.getStatus();	//-- STANDBY, INIT, DEPLOYING, ROLLING_BACK, DONE, FAIL
					if (deployJobStatus ==  DeployJobStatus.STANDBY) { b_is_deploy_call = false; resultMessage_resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_CHECK_STATUS_STANDBY; }
					else if (deployJobStatus ==  DeployJobStatus.INIT) { b_is_deploy_call = false; resultMessage_resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_CHECK_STATUS_INIT; }
					else if (deployJobStatus ==  DeployJobStatus.DEPLOYING) { b_is_deploy_call = false; resultMessage_resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_CHECK_STATUS_DEPLOYING; }
					else if (deployJobStatus ==  DeployJobStatus.ROLLING_BACK) { b_is_deploy_call = false; resultMessage_resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_CHECK_STATUS_ROLLING_BACK; }
				}
			}
			else if (resultCode != AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_DEPLOY_JOB_IS_NOT_EXIST.getCode()) {	//-- 배포상태조회 job없음
				//-- 배포상태조회 실패
				b_is_deploy_call = false;
			}
		}
		if (b_is_deploy_call == true) {
			resultMessageWithData = adptranApiService.apigw_deploy(req_proc_seq, req_api_no, req_gw_profile, req_action_type, req_async);
			logger.info("\n\n### {}.{}() [req_proc_seq: {}][req_api_no: {}][req_gw_profile: {}][req_action_type: {}][req_async: {}][req_check_deploystatus: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), req_proc_seq, req_api_no, req_gw_profile, req_action_type, req_async, req_check_deploystatus, resultMessageWithData);
			message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
			message.setResultMessage("배포처리가 호출 되었습니다.");
			message.setData(resultMessageWithData);
		}
		else {
			message.setResultCode(AdptranApiResultCode.BIZ_EXCEPTION.getCode());
			message.setResultMessage("배포처리가 호출되지 않았습니다.");
			if (resultMessage_resultCode != AdptranApiResultCode.INIT) {
				resultMessageWithData.setResultCd(resultMessage_resultCode.getCode());
				resultMessageWithData.setResultMsg(resultMessage_resultCode.getMessage());
			}
			message.setData(resultMessageWithData);
		}

		return message;
	}
	
	//-- API배포상태조회
	@RequestMapping(value = { "/apigw_deployStatus", }, method=RequestMethod.POST)
	public RestMessage apigw_deployStatus(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_proc_seq = KsmUtil.fnSafeStr(param.get("proc_seq"));
		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		String req_api_id = KsmUtil.fnSafeStr(param.get("api_id"));
		String req_gw_profile = KsmUtil.fnSafeStr(param.get("gw_profile"));
		String req_deployapply_seq = KsmUtil.fnSafeStr(param.get("deployapply_seq"));

		String ss_mbr_id = KsmUtil.fnSafeStr(request.getSession().getAttribute("mbrId"));
		ss_mbr_id = CommonFunc.safeDbEncrypt(ss_mbr_id);

		ResultMessageWithData resultMessageWithData = adptranApiService.apigw_deployStatus(req_proc_seq, req_api_no, req_api_id, req_gw_profile, req_deployapply_seq, ss_mbr_id);
		logger.info("\n\n### {}.{}() [req_proc_seq: {}][req_api_no: {}][req_api_id: {}][ss_mbrid: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), req_proc_seq, req_api_no, req_api_id, ss_mbr_id, resultMessageWithData);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setResultMessage("배포상태조회가 호출 되었습니다.");
		message.setData(resultMessageWithData);

		return message;
	}

	//-- API배포삭제호출
	@RequestMapping(value = { "/apigw_deployDelete", }, method=RequestMethod.POST)
	public RestMessage apigw_deployDelete(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String req_gw_profile = KsmUtil.fnSafeStr(param.get("gw_profile"));
		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));

		ResultMessageWithData resultMessageWithData = adptranApiService.apigw_deployDelete(req_gw_profile, req_api_no);
		logger.info("\n\n### {}.{}() [req_gw_profile: {}][req_api_no: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName() , req_gw_profile, req_api_no, resultMessageWithData);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setResultMessage("배포삭제가 호출 되었습니다.");
		message.setData(resultMessageWithData);

		return message;
	}

	//-- API검증호출
	@RequestMapping(value = { "/apigw_cpApiGet", }, method=RequestMethod.POST)
	public RestMessage apigw_cpApiGet(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//-- [tag:PRJ-20220901]
		//-- [i][BEAST target일시 proc_seq: {0}, gw_profile: {VERI} 설정]
		String req_proc_seq = KsmUtil.fnSafeStr(param.get("proc_seq")); 
		String req_gw_profile = KsmUtil.fnSafeStr(param.get("gw_profile"));	//-- TB, PROD, VERI
		String req_api_url = KsmUtil.fnSafeStr(param.get("api_url"));
		String req_headers = KsmUtil.fnSafeStr(param.get("headers"));
		String req_body = KsmUtil.fnSafeStr(param.get("body"));

		Map<String, Object> map_param = new HashMap<>();
		map_param.put("api_no", KsmUtil.fnSafeStr(param.get("api_no")));
		map_param.put("api_nm", KsmUtil.fnSafeStr(param.get("api_nm")));
		//-- [tag:PRJ-20220901]
		map_param.put("api_veri_baseurl", KsmUtil.fnSafeStr(param.get("api_veri_baseurl")));
		map_param.put("testcase_id", KsmUtil.fnSafeStr(param.get("testcase_id")));
		map_param.put("testcase_seq", KsmUtil.fnSafeStr(param.get("testcase_seq")));
		map_param.put("testcase_nm", KsmUtil.fnSafeStr(param.get("testcase_nm")));
		map_param.put("param_gub", KsmUtil.fnSafeStr(param.get("param_gub")));
		map_param.put("param_header", KsmUtil.fnSafeStr(param.get("param_header")));
		map_param.put("param_body", KsmUtil.fnSafeStr(param.get("param_body")));
		map_param.put("param_query", KsmUtil.fnSafeStr(param.get("param_query")));
		map_param.put("param_header_json", KsmUtil.fnSafeStr(param.get("param_header_json")));
		map_param.put("param_body_json", KsmUtil.fnSafeStr(param.get("param_body_json")));
		map_param.put("assert_case", KsmUtil.fnSafeStr(param.get("assert_case")));
		map_param.put("assert_field", KsmUtil.fnSafeStr(param.get("assert_field")));
		map_param.put("assert_operator", KsmUtil.fnSafeStr(param.get("assert_operator")));
		map_param.put("assert_value", KsmUtil.fnSafeStr(param.get("assert_value")));
		
		String ss_mbr_id = KsmUtil.fnSafeStr(request.getSession().getAttribute("mbrId"));
		ss_mbr_id = CommonFunc.safeDbEncrypt(ss_mbr_id);

		//-- [dep][todo][session의 procseq와 비교처리]		
		//--##int procSeq = CommonFunc.getSession("procSeq", request.getSession());

		ResultMessageWithData resultMessageWithData = adptranApiService.apigw_cpApiGet(req_proc_seq, req_gw_profile, req_api_url, req_headers, req_body, map_param, ss_mbr_id);
		logger.info("\n\n### {}.{}() [req_proc_seq: {}][req_gw_profile: {}][req_api_url: {}][req_headers: {}][req_body: {}][map_param: {}][ss_mbrid: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
			req_proc_seq, req_gw_profile, req_api_url, req_headers, req_body, map_param, ss_mbr_id, resultMessageWithData);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setResultMessage("CpApi가 호출 되었습니다.");
		message.setData(resultMessageWithData);

		return message;
	}

	//-- LampLog조회
	@RequestMapping(value = { "/apigw_LampLog", }, method=RequestMethod.POST)
	public RestMessage apigw_LampLog(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		String req_gw_profile = KsmUtil.fnSafeStr(param.get("gw_profile"));
		String req_search_date = KsmUtil.fnSafeStr(param.get("search_date"));	//-- yyyymmdd
		String req_transaction_id = KsmUtil.fnSafeStr(param.get("transaction_id"));
		String req_api_id = KsmUtil.fnSafeStr(param.get("api_id"));

		ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
		
		GwProfile gwProfile = (("PROD".equals(req_gw_profile) == true) ? GwProfile.PROD : GwProfile.TB);
		LampResponse lampResponse = lampLogService.getByTransaction(gwProfile, req_search_date, req_transaction_id, req_api_id);

		/*-- lampResponse getStatus(), getMessage()처리 안되어있음
		LampResult lampResult = lampResponse.getStatus();
		int resultCd = ((lampResult == lampResult.SUCCESS) ? ResultCode.RC_APIGW_FN_LAMPLOG_SUCC.getCode() : ResultCode.RC_APIGW_FN_LAMPLOG_FAIL.getCode());
		*/
		AdptranApiResultCode resultCode = ((lampResponse != null) ? AdptranApiResultCode.RC_APIGW_FN_LAMPLOG_SUCC : AdptranApiResultCode.RC_APIGW_FN_LAMPLOG_FAIL);
		resultMessageWithData.setResultCd(resultCode.getCode());
		resultMessageWithData.setResultMsg(resultCode.getMessage());
		if (lampResponse != null) {
			resultMessageWithData.setData(lampResponse.getData());	//-- List<LampResponseBody>
		}

		logger.info("\n\n### {}.{}() [req_gw_profile: {}][req_search_date: {}][req_transaction_id: {}][req_api_id: {}][ResultMessageWithData: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
			req_gw_profile, req_search_date, req_transaction_id, req_api_id, resultMessageWithData);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setResultMessage("LampLog가 호출 되었습니다.");
		message.setData(resultMessageWithData);

		return message;
	}

	/*--#####--*/

	//-- test working {
	//-- [ref] json body 형식
	@RequestMapping(value = { "/apigw_xxx_body", }, method=RequestMethod.POST)
	public ModelAndView apigw_xxx_body(HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ResultMessageWithData resultMessageWithData = new ResultMessageWithData();	//-- DO something
		GenericMessage message = new GenericMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "xxx가 수행 되었습니다.");
		message.setData(resultMessageWithData);

		Map<String, Object> map_out = new HashMap<>();
		Integer totalCount = message.getTotalCount();
		if (totalCount != null) { map_out.put("totalCount", totalCount); }
		map_out.put("data", message.getData());
		map_out.put("resultCode", message.getResultCode());
		map_out.put("resultMessage", message.getResultMessage());

		return new ModelAndView("jsonView", map_out);
	}

	//-- [ref] form 형식
	@RequestMapping(value = { "/apigw_xxx_form", }, method=RequestMethod.POST)
	public ModelAndView apigw_xxx_form(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ResultMessageWithData resultMessageWithData = new ResultMessageWithData();	//-- DO something
		GenericMessage message = new GenericMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "xxx가 수행 되었습니다.");
		message.setData(resultMessageWithData);

		Map<String, Object> map_out = new HashMap<>();
		Integer totalCount = message.getTotalCount();
		if (totalCount != null) { map_out.put("totalCount", totalCount); }
		map_out.put("data", message.getData());
		map_out.put("resultCode", message.getResultCode());
		map_out.put("resultMessage", message.getResultMessage());

		return new ModelAndView("jsonView", map_out);
	}
	//-- test working }
}
