package com.kt.openapi.web.beast.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kt.openapi.web.adptran.vo.*;
import com.kt.openapi.web.beast.apigw.entity.BstgwApiLinkDataEntity;
import com.kt.openapi.web.beast.apigw.entity.apidply.BstgwApiDplyEntity;
import com.kt.openapi.web.beast.apigw.entity.svcdply.BstgwSvcDplyEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

//-- [tag:PRJ-20220901]
public interface BeastService {

    //-- BEAST API deploy
    // {returnCd:, returnMsg:, result:{mapSyncDb:, mapParamApiReq:, httpEntity:, responseEntity:, bstIfExeHist:, common_code:, common_message:} }
    Map<String, Object> bstgwApiDeploy(String target, int apiNo, String dplyType);

    //-- BEAST get API item by apiId
    BstgwApiDplyEntity getApiDplyById(String target, String apiId);

    //-- BEAST SVC deploy
    // {returnCd:, returnMsg:, result:{mapSyncDb:, mapParamSvcReq:, httpEntity:, responseEntity:, bstIfExeHist:, common_code:, common_message:} }
    Map<String, Object> bstgwSvcDeploy(String target, int devapplySeq, String dplyType);

    //-- BEAST get SVC item by svcId
    BstgwSvcDplyEntity getSvcDplyById(String target, String svcId);

    BstgwSvcDplyEntity deleteSvcDply(String target, String svcId);

    //-- [tag:SR-20230706]
    //-- BEAST ApiLinkData CUD
    // {returnCd:, returnMsg:, result:{mapSyncDb:, mapParamApilikDataReq:, httpEntity:, responseEntity:, bstIfExeHist:, common_code:, common_message:} }
    Map<String, Object> bstgwApiLinkDataCUD(String target, String procType, BstgwApiLinkDataEntity bstgwApiLinkDataEntity);

    //-- BEAST get ApiLinkData item by type + key
    BstgwApiLinkDataEntity getApiLinkDataByTypeKey(String target, String type, String key);

    //-- BEAST I/F API Request Call
    // {returnCd:, returnMsg:, result:{httpEntity:, responseEntity:, bstIfExeHist:} }
    Map<String, Object> procBeastApiRequest(Map<String, Object> map_in);

    //-- get BstgwApiDplyEntity from KOA_TB_API_DEF.API_NO
    // {returnCd:, returnMsg:, result:{bstgwApiDplyEntity:} }
    Map<String, Object> getBstgwApiDplyEntity(String target, int apiNo, String dplyType);

    //-- get BstgwApiDplyEntity String from KOA_TB_API_DEF.API_NO
    String getBstgwApiDplyEntityString(String target, int apiNo, String dplyType, String direct);

    //-- get BstgwSvcDplyEntity from T_DEV_APPLY_API_INFO.DEVAPPLY_SEQ
    // {returnCd:, returnMsg:, result:{bstgwSvcDplyEntity:} }
    Map<String, Object> getBstgwSvcDplyEntity(String target, int devapplySeq, String dplyType);

    //-- get BstgwSvcDplyEntity String from T_DEV_APPLY_API_INFO.DEVAPPLY_SEQ
    String getBstgwSvcDplyEntityString(String target, int devapplySeq, String dplyType, String direct);

    //-- [i][DATA CRUD] {
    //-- /beast/api/{pathVal}/ajax_query.do
    ModelMap beastApiAjaxQuery(HttpServletRequest request, String pathVal);

    //-- /beast/api/{pathVal}/ajax_proc.do
    ModelMap beastApiAjaxProc(HttpServletRequest request, String pathVal, String requestBody) throws JsonMappingException, JsonProcessingException;

    //-- BEAST-시스템-R-목록
    List<BstSyncAdmSysDplyVO> selectBstSyncAdmSysDplyList(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-시스템-R-목록count
    int selectBstSyncAdmSysDplyListCnt(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-시스템-CUD [mode: insert, update, delete]
    int tranBstSyncAdmSysDply(String target, String mode, Map<String, Object> map_in) throws Exception;

    //-- BEAST-시스템R-SEARCH
    List<BstSyncAdmSysDplyVO> selBstSyncAdmSysDplyList(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API-R-목록
    List<BstSyncAdmApiDplyVO> selectBstSyncAdmApiDplyList(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API-R-목록-count
    int selectBstSyncAdmApiDplyListCnt(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API-CUD [mode: insert, update, delete]
    int tranBstSyncAdmApiDply(String target, String mode, Map<String, Object> map_in) throws Exception;

    //-- BEAST-SVC-R-목록
    List<BstSyncAdmSvcDplyVO> selectBstSyncAdmSvcDplyList(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-SVC-R-목록count
    int selectBstSyncAdmSvcDplyListCnt(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-SVC-CUD [mode: insert, update, delete]
    int tranBstSyncAdmSvcDply(String target, String mode, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API_LINK_DATA-R-목록
    List<BstSyncAdmApiLinkDataVO> selectBstSyncAdmApiLinkDataList(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API_LINK_DATA-R-목록count
    int selectBstSyncAdmApiLinkDataListCnt(String target, Map<String, Object> map_in) throws Exception;

    //-- BEAST-API_LINK_DATA-CUD [mode: insert, update, delete]
    int tranBstSyncAdmApiLinkData(String target, String mode, Map<String, Object> map_in) throws Exception;

    //-- BEAST-I/F Execute 이력-C
    int insertBstIfExecHist(Map<String, Object> map_in) throws Exception;

    //-- PORTAL SVC 삭제(TB)
    int deletePortalSvcTb(Map<String, Object> map_in) throws Exception;

    //-- PORTAL SVC 삭제(상용)
    int deletePortalSvcSb(Map<String, Object> map_in) throws Exception;

    //-- BEAST-I/F LOG-R
    public List<BstIfExecHistVO> selBstIfExecHist(int seq) throws Exception;

    //-- APILink서비스신청-기본정보-R
    TDevApplyInfoVO selTDevApplyInfo(int devapplySeq) throws Exception;

    //-- APILink서비스신청-API정보-R-목록
    List<BstDevApplyApiVO> selTDevApplyApiList(int devapplySeq) throws Exception;

    //-- APILink서비스신청-IP정보-R-목록
    List<BstIpListVO> selGetIpList(String target, int devapplySeq) throws Exception;

    //-- [i][DATA CRUD] {
//-- 배포API정보-R
    BstApiDeployVO selDeployView(int apiNo) throws Exception;
//-- [i][DATA CRUD] }

    TDevApplyInfoVO selSbCehck(String svcId) throws Exception;

    TDevApplyInfoVO selTBAppinstid(String svcId) throws Exception;
}