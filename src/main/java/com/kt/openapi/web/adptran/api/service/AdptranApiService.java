package com.kt.openapi.web.adptran.api.service;

import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.common.CommonUtil;
import com.kt.openapi.web.adptran.api.common.domain.ResultMessage;
import com.kt.openapi.web.adptran.api.common.domain.ResultMessageWithData;
import com.kt.openapi.web.adptran.dao.AdptranDAO;
import com.kt.openapi.web.adptran.util.AdptranUtil;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.adptran.vo.AdptranApiVO;
import com.kt.openapi.web.adptran.vo.AdptranParamVO;
import com.kt.openapi.web.adptran.vo.AdptranTestcaseVO;
import com.kt.openapi.web.adptran.vo.AdptranVeriConditionVO;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.apiDeploy.service.ApiDeployService;
import com.kt.openapi.web.apiDeploy.util.ApiDeployResultCode;
import com.kt.openapi.web.apiDeploy.vo.*;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiRequest;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiResponse;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.deploy.DeployResult;
import com.kt.openapi.web.apigw.exception.DeploymentException;
import com.kt.openapi.web.apigw.services.api.GwApiService;
import com.kt.openapi.web.apigw.services.api.cp.CpApiService;
import com.kt.openapi.web.apigw.type.ApiActionType;
import com.kt.openapi.web.apigw.type.DeployJobStatus;
import com.kt.openapi.web.apigw.type.GwProfile;
import com.kt.openapi.web.apigw.type.HandlerType;
import com.kt.openapi.web.cmmn.ApiException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdptranApiService {
    private static final Logger logger = LoggerFactory.getLogger(AdptranApiService.class);

    @Autowired
    private GwApiService gwApiService;

    @Autowired
    private CpApiService cpApiService;

    @Autowired
    private ApiDeployService apiDeployService;

    @Autowired
    private ApiRegDAO apiRegDAO;

    @Autowired
    private AdptranDAO adptranDAO;

    /**
     * API배포실행
     *
     * @return ResultMessageWithData.data: DeployResult { status, message }
     */
    public ResultMessageWithData apigw_deploy(String req_proc_seq, String req_api_no, String req_gw_profile, String req_action_type, String req_async) throws ApiException {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
        DeployResult deployResult = new DeployResult();

        HashMap<String, Object> map_data = new HashMap<>();
        AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

        //-- API규격 검색
        AdptranApiVO mapOut = this.select_API_DEF_with_API_SPC(req_api_no);

        //-- API파라메터 검색
        List<String> param_loc_list = new ArrayList<>();    //-- PARAM_LOC filter
        param_loc_list.add("header");
        param_loc_list.add("body");
        List<AdptranParamVO> listOut = this.select_API_PARAM_list(req_api_no, param_loc_list);

        //-- set GwProfile
        GwProfile gwProfile = GwProfile.TB;
        if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
            gwProfile = GwProfile.PROD;
        }
        //-- set ApiActionType
        ApiActionType apiActionType = ApiActionType.CREATE;
        if ("UPDATE".equals(req_action_type) == true) {
            apiActionType = ApiActionType.UPDATE;
        }
        //-- set ApiEntity
        //--:@apigw deploy정보구성
        ApiEntity apiEntity = new ApiEntity();
        resultCode = AdptranUtil.set_ApiInfo_To_ApiEntity(req_gw_profile, mapOut, listOut, apiEntity);
        //--[20210315][add] {
        map_data.put("set_apientity_resultCd", resultCode.getCode());
        map_data.put("set_apientity_resultMsg", resultCode.getMessage());
        //--[20210315][add] }

        Integer resultCd;
        String resultMsg;
        if (resultCode == AdptranApiResultCode.RC_SET_APIENTITY_SUCC) {
            try {
                if ("Y".equalsIgnoreCase(req_async) == true) {
                    gwApiService.deployAsync(gwProfile, apiActionType, apiEntity);
                    logger.info("\n\n### {}.{}() [gwProfile: {}][apiActionType: {}][apiEntity.getId(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), gwProfile, apiActionType, apiEntity.getId());
                } else {
                    deployResult = gwApiService.deploy(gwProfile, apiActionType, apiEntity);
                    logger.info("\n\n### {}.{}() [gwProfile: {}][apiActionType: {}][apiEntity.getId(): {}][deployResult.getStatus(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), gwProfile, apiActionType, apiEntity.getId(), deployResult.getStatus());
                }
                //-- API배포실행 호출성공
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_SUCC;
            } catch (DeploymentException e) {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_DEPLOYEXCEPTION;
                logger.error("\n\n### {}.{}() [Message: {}][DeploymentException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            } catch (Exception e) {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_EXCEPTION;
                logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            }
            map_data.put("deployresult", deployResult);

            //-- [drm][ing]
            String apiNm = KsmUtil.fnSafeStr(mapOut.getApiNm());
            String spcApiNm = KsmUtil.fnSafeStr(mapOut.getSpcApiNm());

            logger.info("\n\n### {}.{}() [API배포호출][proc_seq: {}][api_no: {}][gwprofile: {}][action_type: {}][async: {}][apiNm: {}][spcApiNm: {}][deployResult.getStatus(): {}][deployResult.getMessage(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    req_proc_seq, req_api_no, req_gw_profile, req_action_type, req_async, apiNm, spcApiNm, ((deployResult == null) ? "null" : deployResult.getStatus()), ((deployResult == null) ? "null" : deployResult.getMessage()));

            map_data.put("apigw_debug", "API배포호출[apiNm: " + apiNm + "][spcApiNm: " + spcApiNm + "][deployResult.Status: " + ((deployResult == null) ? "null" : deployResult.getStatus()) + "][deployResult.Message: " + ((deployResult == null) ? "null" : deployResult.getMessage()) + "]");
        } else {
            resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_SET_APIENTITY_ERR;    //-- ApiEntity설정오류
        }
        resultCd = resultCode.getCode();
        resultMsg = resultCode.getMessage();

        resultMessageWithData.setResultCd(resultCd);
        resultMessageWithData.setResultMsg(resultMsg);
        resultMessageWithData.setData(map_data);

        return resultMessageWithData;
    }

    /**
     * API배포삭제
     *
     * @return ResultMessageWithData.data: DeployResult { status, message }
     */
    public ResultMessageWithData apigw_deployDelete(String req_gw_profile, String req_api_no) throws ApiException {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
        DeployResult deployResult = new DeployResult();

        HashMap<String, Object> map_data = new HashMap<>();
        AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

        //-- API규격 검색
        AdptranApiVO mapOut = this.select_API_DEF_with_API_SPC(req_api_no);

        //-- DB value {
        String apiNm = KsmUtil.fnSafeStr(mapOut.getApiNm());
        String apiPath = KsmUtil.fnSafeStr(mapOut.getApiPath());
        String apiId = KsmUtil.fnSafeStr(mapOut.getApiId());
        //--##String system = KsmUtil.fnSafeStr(mapOut.get("SYS_ID_NM"));
        String apiHandlerCdNm = KsmUtil.fnSafeStr(mapOut.getApiHandlerCdNm());
        String methodCdNm = KsmUtil.fnSafeStr(mapOut.getMethodCdNm());
        //--##String endpntMethodCdNm = KsmUtil.fnSafeStr(mapOut.get("ENDPNT_METHOD_CD_NM"));
        //--@@String endpntTimeout = KsmUtil.fnSafeStr(mapOut.get("ENDPNT_TIMEOUT"));
        //--@@String endpntClientIp = KsmUtil.fnSafeStr(mapOut.get("ENDPNT_CLIENT_IP"));
        //--##String endpntTbUrl = KsmUtil.fnSafeStr(mapOut.get("ENDPNT_TB_URL"));
        //--##String endpntPrdUrl = KsmUtil.fnSafeStr(mapOut.get("ENDPNT_PRD_URL"));
        //--@@String resmapResCdField = KsmUtil.fnSafeStr(mapOut.get("RESMAP_RES_CD_FIELD"));
        //--@@String resmapSuccVal = KsmUtil.fnSafeStr(mapOut.get("RESMAP_SUCC_VAL"));
        //--@@String[] a_resmapSuccVal = resmapSuccVal.split(",");
        //--@@String resmapErrCdField = KsmUtil.fnSafeStr(mapOut.get("RESMAP_ERR_CD_FIELD"));
        //--@@String resmapErrMsgField = KsmUtil.fnSafeStr(mapOut.get("RESMAP_ERR_MSG_FIELD"));
        String version = KsmUtil.fmt_data(apiPath, "fmt_version_in_path");
        //--@@version = KsmUtil.fmt_data(apiPath, "API_VER");
        //-- DB value }

        //-- set ApiEntity
        ApiEntity apiEntity = new ApiEntity();

        apiEntity.setId(apiNm);    //-- notempty	apiId -> apiNm변경
        apiEntity.setVersion(version);
        apiEntity.setApiNo(apiId);        //-- apiNo -> apiId변경

        HandlerType handlerType = AdptranUtil.getApigwHandlerType(apiHandlerCdNm);
        HttpMethod method = AdptranUtil.getApigwHttpMethod(methodCdNm);
        apiEntity.setUrl(apiPath);        //-- notempty
        apiEntity.setHandler(handlerType);    //-- notempty
        apiEntity.setMethod(method);    //-- notempty

        //--[20210311][chk][?]endpoint정보가쓰이지 않는것으로 보임
		/*--[cmt]		
		EndpointConfig endpoint = new EndpointConfig();
		String endpntUrl = ((GwProfile.PROD.getKey().equals(req_gw_profile) == true) ? endpntPrdUrl : endpntTbUrl);
		HttpMethod endpntMethod = AdptranUtil.getApigwHttpMethod(endpntMethodCdNm);
		URLScheme protocol = ((endpntUrl.toLowerCase().indexOf("https") == 0) ? URLScheme.HTTPS : URLScheme.HTTP);
		
		endpoint.setUrl(endpntUrl);
		endpoint.setSystem(system);
		endpoint.setMethod(endpntMethod);
		endpoint.setProtocol(protocol);
		apiEntity.setEndpoint(endpoint);
		--*/

        //-- set GwProfile
        GwProfile gwProfile = GwProfile.TB;
        if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
            gwProfile = GwProfile.PROD;
        }

        try {
            deployResult = gwApiService.deploy(gwProfile, ApiActionType.DELETE, apiEntity);
            logger.info("\n\n### {}.{}() [gwProfile: {}][apiEntity.getId(): {}][apiEntity.getVersion(): {}][deployResult.getStatus(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), gwProfile, apiEntity.getId(), apiEntity.getVersion(), deployResult.getStatus());
            if (deployResult.getStatus() == DeployJobStatus.DONE) {
                //-- API배포삭제 호출성공
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_DELETE_SUCC;
            } else {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_DELETE_FAIL;
            }
        } catch (DeploymentException e) {
            resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_DELETE_DEPLOYEXCEPTION;
            logger.error("\n\n### {}.{}() [Message: {}][DeploymentException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        } catch (Exception e) {
            resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOY_DELETE_EXCEPTION;
            logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
        map_data.put("deployresult", deployResult);

        logger.info("\n\n### {}.{}() [API배포삭제호출][gwprofile: {}][api_no: {}][apiNm: {}][apiVer: {}][deployResult.getStatus(): {}][deployResult.getMessage(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                req_gw_profile, req_api_no, apiNm, version, ((deployResult == null) ? "null" : deployResult.getStatus()), ((deployResult == null) ? "null" : deployResult.getMessage()));

        map_data.put("apigw_debug", "API배포삭제호출[gw_profile: " + req_gw_profile + "][api_no: " + req_api_no + "][apiNm: " + apiNm + "][version: " + version + "][deployResult.Status: " + ((deployResult == null) ? "null" : deployResult.getStatus()) + "][deployResult.Message: " + ((deployResult == null) ? "null" : deployResult.getMessage()) + "]");

        Integer resultCd = resultCode.getCode();
        String resultMsg = resultCode.getMessage();

        resultMessageWithData.setResultCd(resultCd);
        resultMessageWithData.setResultMsg(resultMsg);
        resultMessageWithData.setData(map_data);

        return resultMessageWithData;
    }

    /**
     * API배포상태조회
     *
     * @return ResultMessageWithData.data: DeployResult { status, message }
     */
    public ResultMessageWithData apigw_deployStatus(String req_proc_seq, String req_api_no, String req_api_id, String req_gw_profile, String req_deployapply_seq, String ss_mbrid) throws ApiException {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
        DeployResult deployResult = null;

        HashMap<String, Object> map_data = new HashMap<>();
        AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

        try {
            deployResult = gwApiService.deployStatus(req_api_id);
            logger.info("\n\n### {}.{}() [req_proc_seq: {}][req_api_no: {}][req_api_id: {}][req_gw_profile: {}][req_deployapply_seq: {}][deployResult.getStatus(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    req_proc_seq, req_api_no, req_api_id, req_gw_profile, req_deployapply_seq, ((deployResult == null) ? "null" : deployResult.getStatus()));
            if (deployResult == null) {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_DEPLOY_JOB_IS_NOT_EXIST;
            } else {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_SUCC;    //-- API배포상태조회 호출성공
            }
        } catch (DeploymentException e) {
            resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_EXCEPTION;
            logger.error("\n\n### {}.{}() [Message: {}][DeploymentException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        } catch (Exception e) {
            resultCode = AdptranApiResultCode.RC_APIGW_FN_DEPLOYSTATUS_DEPLOYEXCEPTION;
            logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
        map_data.put("deployresult", deployResult);

        logger.info("\n\n### {}.{}() [API배포상태조회][proc_seq: {}][api_no: {}][req_api_id: {}][deployResult.getStatus(): {}][deployResult.getMessage(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                req_proc_seq, req_api_no, req_api_id, ((deployResult == null) ? "null" : deployResult.getStatus()), ((deployResult == null) ? "null" : deployResult.getMessage()));

        map_data.put("apigw_debug", "API배포상태조회[deployResult.Status: " + ((deployResult == null) ? "null" : deployResult.getStatus()) + "][deployResult.Message: " + ((deployResult == null) ? "null" : deployResult.getMessage()) + "]");

        //-- process update {
        boolean b_is_proccess_update = true;

        DeployJobStatus deployJobStatus = ((deployResult != null) ? deployResult.getStatus() : null);

        int n_proc_seq = KsmUtil.parseInt(req_proc_seq, 0);
        b_is_proccess_update = (b_is_proccess_update && (n_proc_seq > 0));
        b_is_proccess_update = (b_is_proccess_update && (deployResult != null));
        b_is_proccess_update = (b_is_proccess_update && ((deployJobStatus == DeployJobStatus.DONE) || (deployJobStatus == DeployJobStatus.FAIL)));

        if (b_is_proccess_update == true) {
            String resultCd = ((deployJobStatus == DeployJobStatus.DONE) ? "DONE" : "FAIL");
            String resultMsg = deployResult.getMessage();
            String sucessYn = ((deployJobStatus == DeployJobStatus.DONE) ? "Y" : "N");
            //-- DEPLOY1070-배포완료, DEPLOY1063-배포실패
            String deployCd = ((deployJobStatus == DeployJobStatus.DONE) ? ApiDeployResultCode.CD_1070_DEPLOY_APPLY_CODE.getCode() : ApiDeployResultCode.CD_1063_DEPLOY_APPLY_CODE.getCode());

            if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
                ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
                apiDeploySearchVo.setSeq(n_proc_seq);
                apiDeploySearchVo.setDeployCd(deployCd); // 상용배포결과 // DEPLOY1070-배포완료, DEPLOY1063-배포실패
                apiDeploySearchVo.setRegr(ss_mbrid);

                DeployApplyVo deployApplyVo = new DeployApplyVo();
                deployApplyVo.setCbProSuccessYn(sucessYn);
                deployApplyVo.setSeq(KsmUtil.parseInt(req_deployapply_seq, 0));

                DeployHstVo deployHstVo = new DeployHstVo();
                deployHstVo.setResultCd(resultCd);
                deployHstVo.setResultMsg(resultMsg);
                //--@@deployHstVo.setDeployProcSeq(n_proc_seq);
                //--@@deployHstVo.setDeployAdm(ss_mbrid);

                String returnCd = apiDeployService.callPrivateCbDeploySuccess(deployApplyVo, apiDeploySearchVo, deployHstVo);
                //-- ApiDeployResultCode.CD_RETURN_SUCCESS
                //-- ApiDeployResultCode.CD_RETURN_FAIL
                map_data.put("deploy_proc_result", returnCd);
                logger.info("\n\n### {}.{}() [Process Update: PROC ][deployApplyVo: {}][apiDeploySearchVo: {}][deployHstVo: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        apiDeploySearchVo, apiDeploySearchVo, deployHstVo);
            } else if (GwProfile.TB.getKey().equalsIgnoreCase(req_gw_profile) == true) {
                ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
                apiDeployInsertVo.setSeq(n_proc_seq);
                apiDeployInsertVo.setRegr(ss_mbrid);
                apiDeployInsertVo.setDeployAdm(ss_mbrid);
                apiDeployInsertVo.setTbProSuccessYn(sucessYn);

                DeployHstVo deployHstVo = new DeployHstVo();
                deployHstVo.setResultCd(resultCd);
                deployHstVo.setResultMsg(resultMsg);
                deployHstVo.setDeployProcSeq(n_proc_seq);
                deployHstVo.setDeployAdm(ss_mbrid);

                String returnCd = apiDeployService.callPrivateTbDeploy(apiDeployInsertVo, deployHstVo);
                //-- ApiDeployResultCode.CD_RETURN_SUCCESS
                //-- ApiDeployResultCode.CD_RETURN_FAIL
                map_data.put("deploy_proc_result", returnCd);
                logger.info("\n\n### {}.{}() [Process Update: TB ][apiDeployInsertVo: {}][deployHstVo: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                        apiDeployInsertVo, deployHstVo);
            }
        }
        //-- process정보 update }

        resultMessageWithData.setResultCd(resultCode.getCode());
        resultMessageWithData.setResultMsg(resultCode.getMessage());
        resultMessageWithData.setData(map_data);

        return resultMessageWithData;
    }

    /**
     * CpApi 호출
     */
    public ResultMessageWithData apigw_cpApiGet(String req_proc_seq, String req_gw_profile, String req_api_url, String req_headers, String req_body, Map<String, Object> map_param, String ss_mbrid) /*throws ApiException*/ {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        //-- [tag:PRJ-20220901]
        //-- [i][SPC.API_VERI_BASEURL사용시 req_proc_seq: {0}, req_gw_profile: {VERI} 설정]
        boolean bIsUseApiVeriBaseurl = ("VERI".equals(req_gw_profile));

        //-- map_param {
        int req_api_no = KsmUtil.parseInt(map_param.get("api_no"), 0);
        String req_api_nm = KsmUtil.fnSafeStr(map_param.get("api_nm"));
        //-- [tag:PRJ-20220901]
        String req_api_veri_baseurl = KsmUtil.fnSafeStr(map_param.get("api_veri_baseurl"));
        String req_testcase_id = KsmUtil.fnSafeStr(map_param.get("testcase_id"));
        String req_testcase_seq = KsmUtil.fnSafeStr(map_param.get("testcase_seq"));
        String req_testcase_nm = KsmUtil.fnSafeStr(map_param.get("testcase_nm"));
        String req_param_gub = KsmUtil.fnSafeStr(map_param.get("param_gub"));
        String req_param_header = KsmUtil.fnSafeStr(map_param.get("param_header"));
        String req_param_body = KsmUtil.fnSafeStr(map_param.get("param_body"));
        String req_param_query = KsmUtil.fnSafeStr(map_param.get("param_query"));
        String req_param_header_json = KsmUtil.fnSafeStr(map_param.get("param_header_json"));
        String req_param_body_json = KsmUtil.fnSafeStr(map_param.get("param_body_json"));
        String req_assert_case = KsmUtil.fnSafeStr(map_param.get("assert_case"));
        String req_assert_field = KsmUtil.fnSafeStr(map_param.get("assert_field"));
        String req_assert_operator = KsmUtil.fnSafeStr(map_param.get("assert_operator"));
        String req_assert_value = KsmUtil.fnSafeStr(map_param.get("assert_value"));
        //-- map_param }

        ResultMessageWithData resultMessageWithData = new ResultMessageWithData();
        CpApiResponse cpApiResponse = null;

        HashMap<String, Object> map_data = new HashMap<>();
        AdptranApiResultCode resultCode = AdptranApiResultCode.INIT;

        //-- set CpApiRequest
        //--:@apigw cpapiget정보구성
        CpApiRequest cpApiRequest = new CpApiRequest();
        resultCode = AdptranUtil.set_Request_To_CpApiRequest(req_api_url, req_headers, req_body, cpApiRequest);

        Date dt_stTime = new Date();
        Integer resultCd;
        String resultMsg;
        String resultMsg_sub = "";
        if (resultCode == AdptranApiResultCode.RC_SET_CPAPIREQUEST_SUCC) {
            try {
                if (true == bIsUseApiVeriBaseurl) {
                    //-- [tag:PRJ-20220901]
                    cpApiResponse = cpApiService.get(req_api_veri_baseurl, cpApiRequest);
                } else {
                    //-- set GwProfile
                    GwProfile gwprofile = GwProfile.TB;
                    if (GwProfile.PROD.getKey().equalsIgnoreCase(req_gw_profile) == true) {
                        gwprofile = GwProfile.PROD;
                    }
                    cpApiResponse = cpApiService.get(gwprofile, cpApiRequest);
                }
                if (cpApiResponse != null) {
                    //-- getReturnCode() : "0":Fail, "1":Success
                    resultCode = (("1".equals(cpApiResponse.getReturnCode()) == true) ? AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_SUCC : AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_FAIL);
                } else {
                    resultCode = AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_NO_RESPONSE;
                }
            } catch (NumberFormatException e) {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_EXCEPTION;
                logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            } catch (Exception e) {
                resultCode = AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_EXCEPTION;
                logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            }
            logger.info("\n\n### {}.{}() [CpApi호출][proc_seq: {}][gwprofile: {}][api_url: {}][headers: {}][body: {}][api_veri_baseurl: {}][cpApiResponse.getReturnCode(): {}][cpApiResponse.getResponse(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    req_proc_seq, req_gw_profile, req_api_url, req_headers, req_body, req_api_veri_baseurl, ((cpApiResponse == null) ? "null" : cpApiResponse.getReturnCode()), ((cpApiResponse == null) ? "null" : cpApiResponse.getResponse()));

            map_data.put("apigw_debug", "CpApi호출 [cpApiResponse.getReturnCode: " + ((cpApiResponse == null) ? "null" : cpApiResponse.getReturnCode()) + "][cpApiResponse.getResponse: " + ((cpApiResponse == null) ? "null" : cpApiResponse.getResponse()) + "]");
        } else {
            resultMsg_sub = " - [code: %d][message: %s]".formatted(resultCode.getCode(), resultCode.getMessage());
            resultCode = AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_SET_CPAPIREQUEST_ERR;    //-- CpApiRequest설정오류
        }

        resultCd = resultCode.getCode();
        resultMsg = resultCode.getMessage() + resultMsg_sub;

        Date dt_endTime = new Date();

        //-- 호출결과저장 {
        int n_proc_seq = KsmUtil.parseInt(req_proc_seq, 0);

        VerifiResultVo verifiResultVo = new VerifiResultVo();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String StTime = simpleDateFormat.format(dt_stTime);
        String EndTime = simpleDateFormat.format(dt_endTime);

        //-- result basic
		/*--[ref]
		private int seq;
		private int deployProcSeq;
		private String testCaseSeq;
		private String verificationDt;  
		private String resultCd;
		private String resultMsg;
		private String verifiUsr;
		private String successYn;
		private String stTime;
		private String endTime;
		--*/
        verifiResultVo.setSeq(0);    //-- set at apiDeployService.callPrivateVerifiHst();
        verifiResultVo.setDeployProcSeq(n_proc_seq);
        verifiResultVo.setTestCaseSeq(req_testcase_seq);
        verifiResultVo.setVerificationDt("");
        if (resultCode == AdptranApiResultCode.RC_APIGW_FN_CPAPIGET_SUCC) {
            verifiResultVo.setResultCd("000");
            verifiResultVo.setResultMsg("SUCCESS");
            verifiResultVo.setSuccessYn("Y");
        } else {
            verifiResultVo.setResultCd("999");
            verifiResultVo.setResultMsg("FAIL");
            verifiResultVo.setSuccessYn("N");
        }
        verifiResultVo.setVerifiUsr(ss_mbrid);
        verifiResultVo.setStTime(StTime);
        verifiResultVo.setEndTime(EndTime);

        //-- proc result
		/*--[ref]
		//-- Proc result
		private String procResultCd;
		private String procResultMsg;
		--*/
        verifiResultVo.setProcResultCd(String.valueOf(resultCd));
        verifiResultVo.setProcResultMsg(resultMsg);

        //-- CpApiRequest
		/*--[ref]
		private String reqGwProfile;
		private String reqApiVeriBaseurl;
		private String reqApiUrl;
		private String reqHeaders;
		private String reqBody;
		private String reqTransactionId;
		private String reqSequenceNo;
		--*/
        verifiResultVo.setReqGwProfile(req_gw_profile);
        //-- [tag:PRJ-20220901]
        verifiResultVo.setReqApiVeriBaseurl(req_api_veri_baseurl);
        verifiResultVo.setReqApiUrl(cpApiRequest.getApiUrl());
        verifiResultVo.setReqHeaders(req_headers);
        verifiResultVo.setReqBody(req_body);
        verifiResultVo.setReqTransactionId(cpApiRequest.getTransactionId());
        verifiResultVo.setReqSequenceNo(cpApiRequest.getSequenceNo());

        //-- CpApiResponse
		/*--[ref]
		private String resTransactionId;
		private String resSequenceNo;
		private String resReturnCode;
		private String resReturnDescription;
		private String resErrorCode;
		private String resErrorDescription;
		private String resResponse;
		--*/
        JSONObject jso_response = null;
        if (cpApiResponse != null) {
            verifiResultVo.setResTransactionId(cpApiResponse.getTransactionId());
            verifiResultVo.setResSequenceNo(cpApiResponse.getSequenceno());
            verifiResultVo.setResReturnCode(cpApiResponse.getReturnCode());
            verifiResultVo.setResReturnDescription(cpApiResponse.getReturnDescription());
            verifiResultVo.setResErrorCode(cpApiResponse.getErrorCode());
            verifiResultVo.setResErrorDescription(cpApiResponse.getErrorDescription());

            Map<String, Object> response = cpApiResponse.getResponse();
            try {
                jso_response = JSONObject.fromObject(response);
                verifiResultVo.setResResponse(jso_response.toString());
            } catch (JSONException e) {
                logger.error("\n\n### {}.{}() [Message: {}][JSONException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            }
        } else {
            //--[i]mssql의 text의 경우 null일경우 오류발생 (피연산자 유형 충돌: varbinary은(는) text과(와) 호환되지 않습니다.)
            verifiResultVo.setResResponse("");
        }

        //-- api_def / api_testcase
		/*--[ref]
		private int apiNo;
		private String apiNm;
		private String testcaseNm;
		private String paramGub;
		private String paramHeader;
		private String paramBody;
		private String paramQuery;
		private String paramHeaderJson;
		private String paramBodyJson;
		private String assertCase;
		private String assertField;
		private String assertOperator;
		private String assertValue;
		private String assertResult;
		--*/

        //--[dep]Map<String, Object> mapOut = this.select_API_TESTCASE(req_testcase_id);
        verifiResultVo.setApiNo(req_api_no);
        verifiResultVo.setApiNm(req_api_nm);
        verifiResultVo.setTestcaseNm(req_testcase_nm);
        verifiResultVo.setParamGub(req_param_gub);
        verifiResultVo.setParamHeader(req_param_header);
        verifiResultVo.setParamBody(req_param_body);
        verifiResultVo.setParamHeaderJson(req_param_header_json);
        verifiResultVo.setParamBodyJson(req_param_body_json);
        verifiResultVo.setParamQuery(req_param_query);
        verifiResultVo.setAssertCase(req_assert_case);
        verifiResultVo.setAssertField(req_assert_field);
        verifiResultVo.setAssertOperator(req_assert_operator);
        verifiResultVo.setAssertValue(req_assert_value);

        //-- assert 처리 {
        String assertRerult = "";
        if (jso_response != null) {
            //-- assert check
            String assertfield_value = null;
            if ((req_assert_field.length() > 0) && (req_assert_operator.length() > 0) && (req_assert_value.length() > 0)) {
                assertfield_value = AdptranUtil.get_AssertField_Value(req_assert_field, jso_response.toString());
            }
            if (assertfield_value != null) {
                //-- 0:not avail, 1:ok, -1:not ok
                int n_assert_result = AdptranUtil.computeAssert(assertfield_value, req_assert_operator, req_assert_value);
                assertRerult = ((n_assert_result == 1) ? "OK" : ((n_assert_result == -1) ? "NK" : "NA"));
            }
        }
        verifiResultVo.setAssertResult(assertRerult);
        //-- assert 처리 }

        String returnCd = ApiDeployResultCode.CD_RETURN_FAIL.getCode();
        try {
            returnCd = apiDeployService.callPrivateVerifiHst(verifiResultVo);
        } catch (ApiException e) {
            logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            resultMessageWithData.setResultCd(AdptranApiResultCode.DB_EXCEPTION.getCode());
            resultMessageWithData.setResultMsg(AdptranApiResultCode.DB_EXCEPTION.getMessage());
            resultMessageWithData.setData("DB처리 예외가 발생 하였습니다.");
            return resultMessageWithData;
        }
        //-- ApiDeployResultCode.CD_RETURN_SUCCESS
        //-- ApiDeployResultCode.CD_RETURN_FAIL

        String s_succ = (returnCd.equals(ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()) ? "y" : "n");
        map_data.put("verifi_result_vo", verifiResultVo);
        map_data.put("verifi_hst_succ", s_succ);
        //-- 호출결과저장 }

        //-- 초기화
        s_succ = "";
        if (true == bIsUseApiVeriBaseurl) {
            //-- [i][API검증상태설정]['': 초기, NK: 수행실패, OK: 수행성공]]
            String req_dply_veri_status = (("Y".equals(verifiResultVo.getSuccessYn())) ? "OK" : "NK");
            //-- [i]KOA_TB_API_DEF.DPLY_VERI_STATUS 수정
            ApiRegVO apiRegVO = new ApiRegVO();
            apiRegVO.setApiNo("%d".formatted(req_api_no));
            apiRegVO.setDplyVeriStatus(req_dply_veri_status);
            try {
                int nRet = apiRegDAO.updApiDefDplyVeriStatus(apiRegVO);
            } catch (Exception e) {
                logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
                resultMessageWithData.setResultCd(AdptranApiResultCode.DB_EXCEPTION.getCode());
                resultMessageWithData.setResultMsg(AdptranApiResultCode.DB_EXCEPTION.getMessage());
                resultMessageWithData.setData("DB처리 예외가 발생 하였습니다.");
                return resultMessageWithData;
            }
            //-- [i][todo][callPrivateVerifiProc()내의 메일발송등의 처리추가]
            s_succ = "y";
        } else {
            //-- 프로세스상태 설정 {
            //-- DEPLOY1030-검증시작 일시 DEPLOY1040-검증완료, VERIFI1030-검증완료 로 설정
            ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

            //-- Deploy Proc테이블의 정보들을 뽑아온다
            apiDeploySearchVo.setSeq(n_proc_seq);
            String fv_deployCd = "";
            try {
                //-- 배포정보 조회
                ApiDeployVO deployView = apiDeployService.selDeployView(apiDeploySearchVo);
                fv_deployCd = KsmUtil.fnSafeStr(deployView.getDeployCd());

            } catch (ApiException e) {
                logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
                resultMessageWithData.setResultCd(AdptranApiResultCode.DB_EXCEPTION.getCode());
                resultMessageWithData.setResultMsg(AdptranApiResultCode.DB_EXCEPTION.getMessage());
                resultMessageWithData.setData("DB처리 예외가 발생 하였습니다.");
                return resultMessageWithData;
            }    //-- apiDeploy.selDeployView

            if (fv_deployCd.equals(ApiDeployResultCode.CD_1030_DEPLOY_APPLY_CODE.getCode()) == true) {    //-- DEPLOY1030-검증시작
                apiDeploySearchVo.setVerifiCd("VERIFI1030");
                //apiDeploySearchVo.setSeq(23);
                apiDeploySearchVo.setSeq(n_proc_seq);
                apiDeploySearchVo.setRegr(ss_mbrid);
                apiDeploySearchVo.setDeployCd(ApiDeployResultCode.CD_1040_DEPLOY_APPLY_CODE.getCode());    //-- DEPLOY1040-검증완료

                try {
                    returnCd = apiDeployService.callPrivateVerifiProc(apiDeploySearchVo);
                } catch (Exception e) {
                    logger.error("\n\n### {}.{}() [Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
                    resultMessageWithData.setResultCd(AdptranApiResultCode.DB_EXCEPTION.getCode());
                    resultMessageWithData.setResultMsg(AdptranApiResultCode.DB_EXCEPTION.getMessage());
                    resultMessageWithData.setData("DB처리 예외가 발생 하였습니다.");
                    return resultMessageWithData;
                }
                //-- ApiDeployResultCode.CD_RETURN_SUCCESS
                //-- ApiDeployResultCode.CD_RETURN_FAIL

                s_succ = (returnCd.equals(ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()) ? "y" : "n");
            }
            //-- 프로세스상태 설정 }
        }
        //--[tag:sr-20201001][add]
        map_data.put("verifi_proc_succ", s_succ);

        resultMessageWithData.setResultCd(resultCd);
        resultMessageWithData.setResultMsg(resultMsg);
        resultMessageWithData.setData(map_data);

        return resultMessageWithData;
    }

    /**
     * API정보 select :: apiNo로 KOA_TB_API_DEF + KOA_TB_API_SPC 정보를 select
     */
    public AdptranApiVO select_API_DEF_with_API_SPC(String req_api_no) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        param.put("apiNo", req_api_no);

        return adptranDAO.select_API_DEF_with_API_SPC(param);
    }

    /**
     * API parameter정보 select :: apiNo로 KOA_TB_API_PARAM 정보를 select
     */
    public List<AdptranParamVO> select_API_PARAM_list(String req_api_no, List<String> param_loc_list) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        param.put("apiNo", req_api_no);
        param.put("paramLocList", param_loc_list);

        return adptranDAO.select_API_PARAM_list(param);
    }

    /**
     * API testcase parameter정보 select :: apiNo로 testcase KOA_TB_API_PARAM 정보를 select
     */
    public List<AdptranParamVO> select_API_PARAM_TEST_list(String req_api_no, List<String> param_type_cd_list) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        param.put("apiNo", req_api_no);
        param.put("paramTypeCdList", param_type_cd_list);

        return adptranDAO.select_API_PARAM_TEST_list(param);
    }

    /**
     * API testcase 정보 select :: testcaseId로 KOA_TB_API_TESTCASE 정보를 select
     */
    public AdptranTestcaseVO select_API_TESTCASE(String req_testcase_id) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        param.put("testcaseId", req_testcase_id);

        List<AdptranTestcaseVO> list = adptranDAO.select_API_TESTCASE(param);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    /**
     * API testcase 정보 select :: KOA_TB_API_TESTCASE 정보를 select list
     */
    public List<AdptranTestcaseVO> select_API_TESTCASE_list(String req_api_no) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        //-- 선택적query
        param.put("apiNo", req_api_no);

        return adptranDAO.select_API_TESTCASE(param);
    }

    /**
     * API testcase 정보 trans(ins/upd/del :: testcaseId로 KOA_TB_API_TESTCASE 정보처리
     */
    public ResultMessage trans_API_TESTCASE(String req_trans, Map<String, Object> param_in) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        if ("ins".equals(req_trans) == true) {
            try {
                int ret = adptranDAO.insert_API_TESTCASE(param_in);
                if (ret <= 0) {
                    return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 등록이 실패하였습니다.");
                }
                return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "TestCase정보가 등록 되었습니다.");
            } catch (Exception e) {
                CommonUtil.exLogging("insert_API_TESTCASE", e, logger);
                return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 등록 예외.");
            }
        } else if ("upd".equals(req_trans) == true) {
            try {
                int ret = adptranDAO.update_API_TESTCASE(param_in);
                if (ret != 1) {
                    return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 수정이 실패하였습니다.");
                }
                return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "TestCase정보가 정상적으로 수정 되었습니다.");
            } catch (Exception e) {
                CommonUtil.exLogging("update_API_TESTCASE", e, logger);
                return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 수정 예외.");
            }
        } else if ("del".equals(req_trans) == true) {
            try {
                String testcase_id = KsmUtil.fnSafeStr(param_in.get("testcase_id"));
                List<String> paramTestcaseIdList = (List<String>) param_in.get("paramTestcaseIdList");
                int del_count = 0;

                int ret = 0;
                if (testcase_id.length() > 0) {
                    del_count = 1;
                    ret = adptranDAO.delete_API_TESTCASE(param_in);
                } else {
                    del_count = (paramTestcaseIdList != null) ? paramTestcaseIdList.size() : 0;
                    ret = adptranDAO.delete_API_TESTCASE_list(param_in);
                }

                if (ret != del_count) {
                    return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 삭제가 실패하였습니다.");
                }
                return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "TestCase정보가 정상적으로 삭제 되었습니다.");
            } catch (Exception e) {
                CommonUtil.exLogging("delete_API_TESTCASE", e, logger);
                return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "TestCase정보 삭제 예외.");
            }
        }
        return null;
    }

    /**
     * API 검증 정보 select :: KOA_TB_API_VERI_CONDITION 정보를 select
     */
    public AdptranVeriConditionVO select_API_VERI_CONDITION(String req_seq) {
        logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

        Map<String, Object> param = new HashMap<>();
        param.put("seq", req_seq);

        return adptranDAO.select_API_VERI_CONDITION(param);
    }

}