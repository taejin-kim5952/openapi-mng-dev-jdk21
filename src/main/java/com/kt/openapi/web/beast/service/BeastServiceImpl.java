package com.kt.openapi.web.beast.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.service.AdptranApiService;
import com.kt.openapi.web.adptran.dao.BeastDAO;
import com.kt.openapi.web.adptran.util.AdptranUtil;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.adptran.vo.*;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.apiDeploy.dao.ApiDeployDAO;
import com.kt.openapi.web.apiDeploy.util.ApiDeployResultCode;
import com.kt.openapi.web.apiDeploy.vo.DeployHstVo;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.type.GwProfile;
import com.kt.openapi.web.beast.apigw.config.BstgwProperties;
import com.kt.openapi.web.beast.apigw.constant.BstgwConstant;
import com.kt.openapi.web.beast.apigw.converter.BstgwApiDplyEntityConverter;
import com.kt.openapi.web.beast.apigw.entity.BstgwApiLinkDataEntity;
import com.kt.openapi.web.beast.apigw.entity.apidply.BstgwApiDplyEntity;
import com.kt.openapi.web.beast.apigw.entity.svcdply.AtribEntity;
import com.kt.openapi.web.beast.apigw.entity.svcdply.BstgwSvcDplyEntity;
import com.kt.openapi.web.beast.apigw.entity.svcdply.IpAcesAutEntity;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
@Service("beastService")
public class BeastServiceImpl implements BeastService {
  private static final Logger LOG = LoggerFactory.getLogger(BeastServiceImpl.class);

  @Value("${pageUnit:10}")
  private int pageUnit;

  @Value("${pageSize:10}")
  private int pageSize;

  @Autowired
    private BstgwProperties bstProperties;

  @Autowired
  private BeastDAO beastDAO;

  @Autowired
  private AdptranApiService adptranApiService;

  @Autowired
  private ApiDeployDAO apiDeployDao;

  @Autowired
  private ApiRegDAO apiRegDAO;

  private final RestTemplate bstRestTemplate;

  @Autowired
  public BeastServiceImpl(RestTemplate bstRestTemplate) {
      this.bstRestTemplate = bstRestTemplate;
      //-- [i][for response body encoding]
  this.bstRestTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
  }

  //-- BEAST API deploy
  // {returnCd:, returnMsg:, result:{mapSyncDb:, mapParamApiReq:, httpEntity:, responseEntity:, bstIfExeHist:, common_code:, common_message:} }
  @Override
  public Map<String, Object> bstgwApiDeploy(String target, int apiNo, String dplyType) {
    LOG.debug("\n\n### {}.{}() [target: {}][apiNo: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), target, apiNo);

    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "/apilink/v1/api/apiDply";
    String paramApiMethod = "POST";
    String paramApiBody = "";

    BstgwApiDplyEntity bstgwApiDplyEntity = null;
    Map<String, Object> mapSyncDb = new HashMap<String, Object>();
    if (false == b_is_err) {
      Map<String, Object> map_ret = this.getBstgwApiDplyEntity(target, apiNo, dplyType);
      String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
      String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
      if (BstgwConstant.RETURN_CD.OK.equals(ret_returnCd)) {
        Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
        bstgwApiDplyEntity = (BstgwApiDplyEntity)ret_map_result.get("bstgwApiDplyEntity");
      }
      if (null == bstgwApiDplyEntity) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[getBstgwApiDplyEntity()][ret_returnCd: %s][ret_returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
      }
    }

    if (false == b_is_err) {
      boolean b_opt_exclude_null = true;
      boolean b_opt_pretty_json = true;

      String direct = ";%s;%s;".formatted((b_opt_exclude_null ? "exclude_null" : ""), (b_opt_pretty_json ? "pretty_json" : ""));
      paramApiBody = bstgwApiDplyEntity.getJson(bstgwApiDplyEntity, direct);
      if (null == paramApiBody) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[bstgwApiDplyEntity.getJson()][%s()]".formatted((b_opt_pretty_json ? "writerWithDefaultPrettyPrinter" : "writeValueAsString"));
      }
    }

    int logSeq = -1;
    if (false == b_is_err) {
      //-- I/F api call {
      Map<String, Object> map_in = new HashMap<>();

      map_in.put("api_target", paramApiTarget);
      map_in.put("api_domain", paramApiDomain);
      map_in.put("api_url", paramApiUrl);
      map_in.put("api_method", paramApiMethod);
      map_in.put("api_body", paramApiBody);

      //-- [i][result]
      map_result.put("mapParamApiReq", map_in);

      Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
      String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
      String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
      Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
      HashMap<String, Object> bstIfExeHist = (HashMap<String, Object>)ret_map_result.get("bstIfExeHist");
      //-- [i][result]
      map_result.put("bstIfExeHist", bstIfExeHist);
      if (null != bstIfExeHist) {
        logSeq = KsmUtil.parseInt(bstIfExeHist.get("logSeq"), 0);
      }

      if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
        HttpEntity<String> httpEntity = (HttpEntity<String>)ret_map_result.get("httpEntity");
        ResponseEntity<String> responseEntity = (ResponseEntity<String>)ret_map_result.get("responseEntity");
        //-- [i][result]
        map_result.put("httpEntity", httpEntity);
        map_result.put("responseEntity", responseEntity);

        //-- [i][responseEntity 200 확인]
        //-- expect: {"common":{"code":200,"message":"정상처리되었습니다."}}
        boolean isResposneOk = false;
        int common_code = -1;
        String common_message = "";
        if (null != responseEntity) {
          if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            JSONObject jso_body = JSONObject.fromObject(responseBody);
            JSONObject jso_body_common = jso_body.getJSONObject("common");
            if (null != jso_body_common) {
              common_code = jso_body_common.getInt("code");
              common_message = KsmUtil.fnSafeStr(jso_body_common.get("message"));
              isResposneOk = (common_code == 200);
            }
            else {
              common_message = "[responseBody not has key 'common'][responseBody: %s]".formatted(jso_body);
            }
          }
          else {
            common_message = "[responseEntity.getStatusCode() is not HttpStatus.OK][statusCode: %s]".formatted(responseEntity.getStatusCode());
          }
        }
        //-- [i][result]
        map_result.put("common_code", common_code);
        map_result.put("common_message", common_message);
        if (false == isResposneOk) {
          b_is_err = true;
          returnCd = BstgwConstant.RETURN_CD.NK;
          returnMsg = "[responseEntity-code is not OK][code: %s][message: %s]".formatted(common_code, common_message);
        }
      }
      else {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[procBeastApiRequest()][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
      }
      //-- I/F api call }
    }

    if (false == b_is_err) {
      //-- [i][beast query]
      BstgwApiDplyEntity queriedEntity = this.getApiDplyById(target, bstgwApiDplyEntity.getApiId());
      //-- [i][query실패시에는 request를 사용]
      bstgwApiDplyEntity = ((null != queriedEntity) ? queriedEntity : bstgwApiDplyEntity);
      mapSyncDb = BstgwApiDplyEntity.getSyncDbMap(bstgwApiDplyEntity);
      //-- [i][extend data]
      mapSyncDb.put("srcTag", BstgwConstant.SRC_TAG_APILINK);
      mapSyncDb.put("defApiNo", "%d".formatted(apiNo));

      //-- [i][result]
      map_result.put("mapSyncDb", mapSyncDb);

      //-- [i][write sync beast db][BST_SYNC_ADM_API_DPLY, BST_SYNC_TB_ADM_API_DPLY] {
      try {
        String req_mode = "update";
        this.tranBstSyncAdmApiDply(target, req_mode, mapSyncDb);
      }
      catch (Exception e) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[tranBstSyncAdmApiDply()][e: %s]".formatted(e.getMessage());
      }
      //-- [i][write sync beast db][BST_SYNC_ADM_API_DPLY, BST_SYNC_TB_ADM_API_DPLY] }
    }

    //-- [i][set for write history, deploy result]
    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    //-- [i][write deploy history][KOA_TB_DEPLOY_HST] {
    ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpSession session = servletRequestAttribute.getRequest().getSession(true);

    DeployHstVo deployHstVo = new DeployHstVo();
    String deployAdm = "";
    try {
      deployAdm = CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString());
    } catch (Exception e) {
      deployAdm = "no_session";
    }
    deployHstVo.setDeployAdm(deployAdm);
    deployHstVo.setSuccessYn(BstgwConstant.RETURN_CD.OK.equals(returnCd) ? "Y" : "N");
    deployHstVo.setResultCd(returnCd);
    deployHstVo.setResultMsg(returnMsg);
    String deployGb = (BstgwConstant.PROFILE.PRD.equals(target.toUpperCase()) ? ApiDeployResultCode.CD_DEPLOY_CB_GUBUN.getCode() : ApiDeployResultCode.CD_DEPLOY_TB_GUBUN.getCode());
    deployHstVo.setDeployGb(deployGb);
    deployHstVo.setApiNo(apiNo);
    deployHstVo.setLogSeq(logSeq);

    apiDeployDao.insertDeployHst(deployHstVo);
    //-- [i][write deploy history][KOA_TB_DEPLOY_HST] }

    //-- [i][write deploy result][KOA_TB_API_DEF] {
    try {
      String dplyStatus = (BstgwConstant.RETURN_CD.OK.equals(returnCd) ? "OK" : "NK");
      ApiRegVO apiRegVO = new ApiRegVO();
      apiRegVO.setApiNo("%d".formatted(apiNo));
      if (BstgwConstant.PROFILE.PRD.equals(target.toUpperCase())) {
        apiRegVO.setPrdDplyStatus(dplyStatus);
        apiRegDAO.updApiDefPrdDplyStatus(apiRegVO);
      }
      else {
        apiRegVO.setTbDplyStatus(dplyStatus);
        apiRegDAO.updApiDefTbDplyStatus(apiRegVO);
      }
    } catch (Exception e) {
      LOG.error("\n\n### {}.{}() [[Exception: {}]e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
      b_is_err = true;
      returnCd = BstgwConstant.RETURN_CD.EXCEPT;
      returnMsg = "[apiRegDAO.%s()][e: %s]".formatted(((BstgwConstant.PROFILE.PRD.equals(target.toUpperCase())) ? "updApiDefPrdDplyStatus" : "updApiDefTbDplyStatus"), e.getMessage());
    }
    //-- [i][write deploy result][KOA_TB_API_DEF] }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }

  @Override
  //-- BEAST get API item by apiId
  public BstgwApiDplyEntity getApiDplyById(String target, String apiId) {
    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "/apilink/v1/api/getApiDplyById";
    String paramApiMethod = "GET";
    String paramApiBody = "";
    String paramApiQstr = "?apiId=%s".formatted(apiId);
		boolean b_is_if_hist_write = false;

    Map<String, Object> map_in = new HashMap<>();

    map_in.put("api_target", paramApiTarget);
    map_in.put("api_domain", paramApiDomain);
    map_in.put("api_url", paramApiUrl);
    map_in.put("api_method", paramApiMethod);
    map_in.put("api_body", paramApiBody);
    map_in.put("api_qstr", paramApiQstr);
		map_in.put("direct", (b_is_if_hist_write ? "" : "no_write_hist"));

    Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
    String returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
    //---@@String returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
    Map<String, Object> map_result = (Map<String, Object>)map_ret.get("result");

    BstgwApiDplyEntity bstgwApiDplyEntity = null;
    if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(returnCd)) {
      ResponseEntity<String> responseEntity = (ResponseEntity<String>)map_result.get("responseEntity");
      if ((null != responseEntity) && (responseEntity.getStatusCode() == HttpStatus.OK)) {
        String responseBody = responseEntity.getBody();
        JSONObject jso_body = JSONObject.fromObject(responseBody);
        JSONObject jso_body_common = jso_body.getJSONObject("common");
        int common_code = ((null != jso_body_common) ? jso_body_common.getInt("code") : -1);
        //--##String common_message = ((null != jso_body_common) ? KsmUtil.fnSafeStr(jso_body_common.get("message")) : common_code);
        JSONObject jso_body_data = ((common_code == 200) ? jso_body.getJSONObject("data") : null);
        JSONObject jso_body_data_value = ((null != jso_body_data) ? jso_body_data.getJSONObject("value") : null);
        bstgwApiDplyEntity = ((null != jso_body_data_value) ? BstgwApiDplyEntity.setFromJson(jso_body_data_value.toString()): null);
      }
    }

    LOG.info("\n\n### {}.{}() [returnCd: {}][returnMsg: {}][result: {}][bstgwApiDplyEntity: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName()
      , map_ret.get("returnCd"), map_ret.get("returnMsg"), map_ret.get("result"), bstgwApiDplyEntity);

    return bstgwApiDplyEntity;
  }

  //-- BEAST SVC deploy
  // {returnCd:, returnMsg:, result:{mapSyncDb:, mapParamSvcReq:, httpEntity:, responseEntity:, bstIfExeHist:, common_code:, common_message:} }
  /*--
    return Map {
      returnCd : String
        OK : 성공처리
        NK : 실패처리
        ERR : 오류
        EXCEPT : Exception발생
      returnMsg : String
        = returnCd 와 관련된 메시지 (대부분 debug를 위한 비정형 내용)
      result : Map {
        mapSyncDb : Map
          = Beast 정보 Sync를 위한 Table 입력처리값 (BST_SYNC_ADM_SVC_DPLY, BST_SYNC_TB_ADM_SVC_DPLY)
        mapParamSvcReq : Map
          = Beast I/F 호출 input data
        httpEntity : HttpEntity<String>
          = Beast I/F 호출 request
        responseEntity : ResponseEntity<String> responseEntity
          = Beast I/F 호출 response
        bstIfExeHist : Map
          = Beast I/F 호출 History 기록 Table 입력처리값 (BST_IF_EXEC_HIST)
        common_code : int
          = Beast API response 전문의 common.code 항목
            ( {"common":{"code":200,"message":"정상처리되었습니다."}}  에서 200)
        common_message : String
          = Beast API response 전문의 common.message항목
            ( {"common":{"code":200,"message":"정상처리되었습니다."}}  에서 "정상처리되었습니다.")
      }
    }
  --*/
  @Override
  public Map<String, Object> bstgwSvcDeploy(String target, int devapplySeq, String dplyType) {
    LOG.debug("\n\n### {}.{}() [target: {}][devapplySeq: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), target, devapplySeq);

    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "/apilink/v1/svc/svcDplyEnc";
    String paramApiMethod = "POST";
    String paramApiBody = "";

    BstgwSvcDplyEntity bstgwSvcDplyEntity = null;
    Map<String, Object> mapSyncDb = new HashMap<String, Object>();
    if (false == b_is_err) {
      Map<String, Object> map_ret = this.getBstgwSvcDplyEntity(target, devapplySeq, dplyType);
      String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
      String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
      if (BstgwConstant.RETURN_CD.OK.equals(ret_returnCd)) {
        Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
        bstgwSvcDplyEntity = (BstgwSvcDplyEntity)ret_map_result.get("bstgwSvcDplyEntity");
      }
      if (null == bstgwSvcDplyEntity) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[getBstgwSvcDplyEntity()][ret_returnCd: %s][ret_returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
      }
    }

    if (false == b_is_err) {
      boolean b_opt_exclude_null = true;
      boolean b_opt_pretty_json = true;

      String direct = ";%s;%s;".formatted((b_opt_exclude_null ? "exclude_null" : ""), (b_opt_pretty_json ? "pretty_json" : ""));
      paramApiBody = BstgwSvcDplyEntity.getJson(bstgwSvcDplyEntity, direct);
      if (null == paramApiBody) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[bstgwSvcDplyEntity.getJson()][%s()]".formatted((b_opt_pretty_json ? "writerWithDefaultPrettyPrinter" : "writeValueAsString"));
      }
    }

    int logSeq = -1;
    if (false == b_is_err) {
      //-- I/F api call {
      Map<String, Object> map_in = new HashMap<>();

      map_in.put("api_target", paramApiTarget);
      map_in.put("api_domain", paramApiDomain);
      map_in.put("api_url", paramApiUrl);
      map_in.put("api_method", paramApiMethod);
      map_in.put("api_body", paramApiBody);

      //-- [i][result]
      map_result.put("mapParamSvcReq", map_in);

      Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
      String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
      String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
      Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
      HashMap<String, Object> bstIfExeHist = (HashMap<String, Object>)ret_map_result.get("bstIfExeHist");
      //-- [i][result]
      map_result.put("bstIfExeHist", bstIfExeHist);
      if (null != bstIfExeHist) {
        logSeq = KsmUtil.parseInt(bstIfExeHist.get("logSeq"), 0);
      }

      if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
        HttpEntity<String> httpEntity = (HttpEntity<String>)ret_map_result.get("httpEntity");
        ResponseEntity<String> responseEntity = (ResponseEntity<String>)ret_map_result.get("responseEntity");
        //-- [i][result]
        map_result.put("httpEntity", httpEntity);
        map_result.put("responseEntity", responseEntity);

        //-- [i][responseEntity 200 확인]
        //-- expect: {"common":{"code":200,"message":"정상처리되었습니다."}}
        boolean isResposneOk = false;
        int common_code = -1;
        String common_message = "";
        if (null != responseEntity) {
          if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            JSONObject jso_body = JSONObject.fromObject(responseBody);
            JSONObject jso_body_common = jso_body.getJSONObject("common");
            if (null != jso_body_common) {
              common_code = jso_body_common.getInt("code");
              common_message = KsmUtil.fnSafeStr(jso_body_common.get("message"));
              isResposneOk = (common_code == 200);
            }
            else {
              common_message = "[responseBody not has key 'common'][responseBody: %s]".formatted(jso_body);
            }
          }
          else {
            common_message = "[responseEntity.getStatusCode() is not HttpStatus.OK][statusCode: %s]".formatted(responseEntity.getStatusCode());
          }
        }
        //-- [i][result]
        map_result.put("common_code", common_code);
        map_result.put("common_message", common_message);
        if (false == isResposneOk) {
          b_is_err = true;
          returnCd = BstgwConstant.RETURN_CD.NK;
          returnMsg = "[responseEntity-code is not OK][code: %s][message: %s]".formatted(common_code, common_message);
        }
      }
      else {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[procBeastApiRequest()][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
      }
      //-- I/F api call }
    }

    if (false == b_is_err) {
      String pwPlain = bstgwSvcDplyEntity.getPw();  //-- [i][request는 평문pw]
      //-- [i][beast query]
      BstgwSvcDplyEntity queriedEntity = this.getSvcDplyById(target, bstgwSvcDplyEntity.getSvcId());
      //-- [i][query실패시에는 request를 사용]
      bstgwSvcDplyEntity = ((null != queriedEntity) ? queriedEntity : bstgwSvcDplyEntity);
      mapSyncDb = BstgwSvcDplyEntity.getSyncDbMap(bstgwSvcDplyEntity);
      //-- [i][extend data]
      mapSyncDb.put("pwPlain", pwPlain);
      mapSyncDb.put("srcTag", BstgwConstant.SRC_TAG_APILINK);

      //-- [i][result]
      map_result.put("mapSyncDb", mapSyncDb);

      //-- [i][write sync beast db][BST_SYNC_ADM_SVC_DPLY, BST_SYNC_TB_ADM_SVC_DPLY] {
      try {
        String req_mode = "update";
        this.tranBstSyncAdmSvcDply(target, req_mode, mapSyncDb);
      }
      catch (Exception e) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[tranBstSyncAdmSvcDply()][e: %s]".formatted(e.getMessage());
      }
      //-- [i][write sync beast db][BST_SYNC_ADM_SVC_DPLY, BST_SYNC_TB_ADM_SVC_DPLY] }
    }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }

  @Override
  //-- BEAST get SVC item by svcId
  public BstgwSvcDplyEntity getSvcDplyById(String target, String svcId) {
    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "/apilink/v1/svc/getSvcDplyById";
    String paramApiMethod = "GET";
    String paramApiBody = "";
    String paramApiQstr = "?svcId=%s".formatted(svcId);
		boolean b_is_if_hist_write = false;

    Map<String, Object> map_in = new HashMap<>();

    map_in.put("api_target", paramApiTarget);
    map_in.put("api_domain", paramApiDomain);
    map_in.put("api_url", paramApiUrl);
    map_in.put("api_method", paramApiMethod);
    map_in.put("api_body", paramApiBody);
    map_in.put("api_qstr", paramApiQstr);
		map_in.put("direct", (b_is_if_hist_write ? "" : "no_write_hist"));

    Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
    String returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
    //--@@String returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
    Map<String, Object> map_result = (Map<String, Object>)map_ret.get("result");

    BstgwSvcDplyEntity bstgwSvcDplyEntity = null;
    if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(returnCd)) {
      ResponseEntity<String> responseEntity = (ResponseEntity<String>)map_result.get("responseEntity");
      if ((null != responseEntity) && (responseEntity.getStatusCode() == HttpStatus.OK)) {
        String responseBody = responseEntity.getBody();
        JSONObject jso_body = JSONObject.fromObject(responseBody);
        JSONObject jso_body_common = jso_body.getJSONObject("common");
        int common_code = ((null != jso_body_common) ? jso_body_common.getInt("code") : -1);
        //--##String common_message = ((null != jso_body_common) ? KsmUtil.fnSafeStr(jso_body_common.get("message")) : common_code);
        JSONObject jso_body_data = ((common_code == 200) ? jso_body.getJSONObject("data") : null);
        JSONObject jso_body_data_value = ((null != jso_body_data) ? jso_body_data.getJSONObject("value") : null);
        bstgwSvcDplyEntity = ((null != jso_body_data_value) ? BstgwSvcDplyEntity.setFromJson(jso_body_data_value.toString()): null);
      }
    }

    LOG.info("\n\n### {}.{}() [returnCd: {}][returnMsg: {}][result: {}][bstgwSvcDplyEntity: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName()
      , map_ret.get("returnCd"), map_ret.get("returnMsg"), map_ret.get("result"), bstgwSvcDplyEntity);

    return bstgwSvcDplyEntity;
  }
  
	@Override
//-- BEAST delete SVC deploy item by svcId
	public BstgwSvcDplyEntity deleteSvcDply(String target, String svcId) {
		String paramApiTarget = target;
		String paramApiDomain = ""; // -- [not_used // use only pre-defined target]
		String paramApiUrl = "/apilink/v1/svc/svcDply";
		String paramApiMethod = "DELETE";
		String paramApiBody = "";
		String paramApiQstr = "?svcId=%s".formatted(svcId);

		boolean b_is_if_hist_write = false;

		Map<String, Object> map_in = new HashMap<>();
		map_in.put("api_target", paramApiTarget);
		map_in.put("api_domain", paramApiDomain);
		map_in.put("api_url", paramApiUrl);
		map_in.put("api_method", paramApiMethod);
		map_in.put("api_body", paramApiBody);
		map_in.put("api_qstr", paramApiQstr);
		map_in.put("direct", (b_is_if_hist_write ? "" : "no_write_hist"));
		map_in.put("svcId", svcId);
		try {
			if(BstgwConstant.PROFILE.TB.equalsIgnoreCase(paramApiTarget) || BstgwConstant.PROFILE.TB_KTC.equalsIgnoreCase(paramApiTarget) || BstgwConstant.PROFILE.TB_AZURE.equalsIgnoreCase(paramApiTarget)) {
				int nRet = this.deletePortalSvcTb(map_in);
			}else if(BstgwConstant.PROFILE.PRD.equalsIgnoreCase(paramApiTarget) || BstgwConstant.PROFILE.PRD_KTC.equalsIgnoreCase(paramApiTarget) || BstgwConstant.PROFILE.PRD_AZURE.equalsIgnoreCase(paramApiTarget)) {
				int nRet = this.deletePortalSvcSb(map_in);
				TDevApplyInfoVO voOut_svc = this.selTBAppinstid(svcId);
				String TB_APPINSTID = (voOut_svc == null || voOut_svc.getTbAppinstid() == null) ? "" : voOut_svc.getTbAppinstid().trim();
				String newParamApiTarget = "TB";
				String newParamApiDomain = ""; // -- [not_used // use only pre-defined target]
				String newParamApiUrl = "/apilink/v1/svc/svcDply";
				String newParamApiMethod = "DELETE";
				String newParamApiBody = "";
				String newParamApiQstr = "?svcId=%s".formatted(TB_APPINSTID);

				boolean new_b_is_if_hist_write = false;

				Map<String, Object> new_map_in = new HashMap<>();
				new_map_in.put("api_target", newParamApiTarget);
				new_map_in.put("api_domain", newParamApiDomain);
				new_map_in.put("api_url", newParamApiUrl);
				new_map_in.put("api_method", newParamApiMethod);
				new_map_in.put("api_body", newParamApiBody);
				new_map_in.put("api_qstr", newParamApiQstr);
				new_map_in.put("direct", (new_b_is_if_hist_write ? "" : "no_write_hist"));
				new_map_in.put("svcId", TB_APPINSTID);
				
				Map<String, Object> map_ret = this.procBeastApiRequest(new_map_in);
				
				String returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));

				Map<String, Object> map_result = (Map<String, Object>) map_ret.get("result");

				BstgwSvcDplyEntity bstgwSvcDplyEntity = null;

				if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(returnCd)) {
					ResponseEntity<String> responseEntity = (ResponseEntity<String>) map_result.get("responseEntity");

					if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
						String responseBody = responseEntity.getBody();
						if (responseBody == null || responseBody.isBlank()) {
							bstgwSvcDplyEntity = null;
						} else {
							JSONObject jso_body = JSONObject.fromObject(responseBody);

							JSONObject jso_body_common = jso_body.optJSONObject("common");
							int common_code = (jso_body_common != null) ? jso_body_common.optInt("code", -1) : -1;

							JSONObject jso_body_data = (common_code == 200) ? jso_body.optJSONObject("data") : null;

							Object v = (jso_body_data != null) ? jso_body_data.opt("value") : null;

							if (v instanceof JSONObject) {
								// value가 객체(JSON)면 기존대로 엔티티 생성
								JSONObject jso_body_data_value = (JSONObject) v;
								bstgwSvcDplyEntity = BstgwSvcDplyEntity.setFromJson(jso_body_data_value.toString());
							} else if (v instanceof String) {
								String valueStr = (String) v;

								bstgwSvcDplyEntity = new BstgwSvcDplyEntity();
							} else {
								// null 또는 기타 타입
								bstgwSvcDplyEntity = null;
							}
						}
					}
					Map<String, Object> new_tb_delete_map_in = new HashMap<>();
					new_tb_delete_map_in.put("svcId", TB_APPINSTID);
					new_tb_delete_map_in.put("target", "TB");
					int n_ret_tb_delete = tranBstSyncAdmSvcDply(newParamApiTarget,"delete",new_tb_delete_map_in);
				}

				LOG.info("\n\n### {}.{}() [returnCd: {}][returnMsg: {}][result: {}][bstgwSvcDplyEntity: {}] ###\n",
						getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
						map_ret.get("returnCd"), map_ret.get("returnMsg"), map_ret.get("result"), bstgwSvcDplyEntity);
			}
	          
        }
        catch (Exception e) {
          //-- [i][ignore write history except]
          LOG.error("\n\n### {}.{}() [[Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
		
		Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
		
		String returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));

		Map<String, Object> map_result = (Map<String, Object>) map_ret.get("result");

		BstgwSvcDplyEntity bstgwSvcDplyEntity = null;

		if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(returnCd)) {
			ResponseEntity<String> responseEntity = (ResponseEntity<String>) map_result.get("responseEntity");

			if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
				String responseBody = responseEntity.getBody();
				if (responseBody == null || responseBody.isBlank()) {
					bstgwSvcDplyEntity = null;
				} else {
					JSONObject jso_body = JSONObject.fromObject(responseBody);

					JSONObject jso_body_common = jso_body.optJSONObject("common");
					int common_code = (jso_body_common != null) ? jso_body_common.optInt("code", -1) : -1;
					
					String common_msg = (jso_body_common != null) ? jso_body_common.optString("message", "") : "";
					
					if(common_code != 200) {
						throw new RuntimeException("BEAST API error, common_code=" + common_code + ", message=" + common_msg + " 잠시 후 다시 시도해주세요.");
					}
					JSONObject jso_body_data = (common_code == 200) ? jso_body.optJSONObject("data") : null;

					Object v = (jso_body_data != null) ? jso_body_data.opt("value") : null;

					if (v instanceof JSONObject) {
						// value가 객체(JSON)면 기존대로 엔티티 생성
						JSONObject jso_body_data_value = (JSONObject) v;
						bstgwSvcDplyEntity = BstgwSvcDplyEntity.setFromJson(jso_body_data_value.toString());
					} else if (v instanceof String) {
						String valueStr = (String) v;

						bstgwSvcDplyEntity = new BstgwSvcDplyEntity();
					} else {
						// null 또는 기타 타입
						bstgwSvcDplyEntity = null;
					}
				}
			}
		}

		LOG.info("\n\n### {}.{}() [returnCd: {}][returnMsg: {}][result: {}][bstgwSvcDplyEntity: {}] ###\n",
				getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				map_ret.get("returnCd"), map_ret.get("returnMsg"), map_ret.get("result"), bstgwSvcDplyEntity);

		return bstgwSvcDplyEntity;
	}

  @Override
  public Map<String, Object> bstgwApiLinkDataCUD(String target, String procType, BstgwApiLinkDataEntity bstgwApiLinkDataEntity) {
    LOG.debug("\n\n### {}.{}() [target: {}][procType: {}][bstgwApiLinkDataEntity: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), target, procType, BstgwApiLinkDataEntity.getJson(bstgwApiLinkDataEntity, ""));

    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "";
    String paramApiMethod = "";
    String paramApiBody = "";
    String paramApiQstr = "";
    
    if (BstgwApiLinkDataEntity.con_PROCTYPE_DPLY.equals(procType)) {
      if (null != bstgwApiLinkDataEntity) {
        String type = KsmUtil.fnSafeStr(bstgwApiLinkDataEntity.getType());
        String key = KsmUtil.fnSafeStr(bstgwApiLinkDataEntity.getKey());
        //-- [i][beast query]
        BstgwApiLinkDataEntity queriedEntity = this.getApiLinkDataByTypeKey(target, type, key);
        boolean bIsApilinkDataExist = ((null != queriedEntity) && type.equals(queriedEntity.getType()) && key.equals(queriedEntity.getKey())); 
        procType = (bIsApilinkDataExist ? BstgwApiLinkDataEntity.con_PROCTYPE_UPDATE : BstgwApiLinkDataEntity.con_PROCTYPE_CREATE);
      }
    }
    

    if (BstgwApiLinkDataEntity.con_PROCTYPE_CREATE.equals(procType)) {
      paramApiUrl = "/apilink/v1/data/createApiLinkData";
      paramApiMethod = "POST";
    }
    else if (BstgwApiLinkDataEntity.con_PROCTYPE_UPDATE.equals(procType)) {
      paramApiUrl = "/apilink/v1/data/apiLinkData";
      paramApiMethod = "PUT";
    }
    else if (BstgwApiLinkDataEntity.con_PROCTYPE_DELETE.equals(procType)) {
      paramApiUrl = "/apilink/v1/data/apiLinkData";
      paramApiMethod = "DELETE";
    }

    if (false == b_is_err) {
      if (null == bstgwApiLinkDataEntity) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[bstgwApiLinkDataCUD()][bstgwApiLinkDataEntity is null]".formatted();
      }
    }

    if (false == b_is_err) {
      if ((BstgwApiLinkDataEntity.con_PROCTYPE_CREATE.equals(procType)) || (BstgwApiLinkDataEntity.con_PROCTYPE_UPDATE.equals(procType))) {
        boolean b_opt_exclude_null = true;
        boolean b_opt_pretty_json = true;
  
        String direct = ";%s;%s;".formatted((b_opt_exclude_null ? "exclude_null" : ""), (b_opt_pretty_json ? "pretty_json" : ""));
        paramApiBody = BstgwApiLinkDataEntity.getJson(bstgwApiLinkDataEntity, direct);
        if (null == paramApiBody) {
          b_is_err = true;
          returnCd = BstgwConstant.RETURN_CD.EXCEPT;
          returnMsg = "[bstgwApiLinkDataEntity.getJson()][%s()]".formatted((b_opt_pretty_json ? "writerWithDefaultPrettyPrinter" : "writeValueAsString"));
        }
      }
      else if (BstgwApiLinkDataEntity.con_PROCTYPE_DELETE.equals(procType)) {
        paramApiQstr = "?type=%s&key=%s".formatted(bstgwApiLinkDataEntity.getType(), bstgwApiLinkDataEntity.getKey());
      }
    }

    int logSeq = -1;
    if (false == b_is_err) {
      //-- I/F api call {
      Map<String, Object> map_in = new HashMap<>();

      map_in.put("api_target", paramApiTarget);
      map_in.put("api_domain", paramApiDomain);
      map_in.put("api_url", paramApiUrl);
      map_in.put("api_method", paramApiMethod);
      map_in.put("api_body", paramApiBody);
      map_in.put("api_qstr", paramApiQstr);

      //-- [i][result]
      map_result.put("mapParamApiReq", map_in);

      Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
      String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
      String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
      Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
      HashMap<String, Object> bstIfExeHist = (HashMap<String, Object>)ret_map_result.get("bstIfExeHist");
      //-- [i][result]
      map_result.put("bstIfExeHist", bstIfExeHist);
      if (null != bstIfExeHist) {
        logSeq = KsmUtil.parseInt(bstIfExeHist.get("logSeq"), 0);
      }

      if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
        HttpEntity<String> httpEntity = (HttpEntity<String>)ret_map_result.get("httpEntity");
        ResponseEntity<String> responseEntity = (ResponseEntity<String>)ret_map_result.get("responseEntity");
        //-- [i][result]
        map_result.put("httpEntity", httpEntity);
        map_result.put("responseEntity", responseEntity);

        //-- [i][responseEntity 200 확인]
        //-- expect: {"common":{"code":200,"message":"정상처리되었습니다."}}
        boolean isResposneOk = false;
        int common_code = -1;
        String common_message = "";
        if (null != responseEntity) {
          if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            JSONObject jso_body = JSONObject.fromObject(responseBody);
            JSONObject jso_body_common = jso_body.getJSONObject("common");
            if (null != jso_body_common) {
              common_code = jso_body_common.getInt("code");
              common_message = KsmUtil.fnSafeStr(jso_body_common.get("message"));
              isResposneOk = (common_code == 200);
            }
            else {
              common_message = "[responseBody not has key 'common'][responseBody: %s]".formatted(jso_body);
            }
          }
          else {
            common_message = "[responseEntity.getStatusCode() is not HttpStatus.OK][statusCode: %s]".formatted(responseEntity.getStatusCode());
          }
        }
        //-- [i][result]
        map_result.put("common_code", common_code);
        map_result.put("common_message", common_message);
        if (false == isResposneOk) {
          b_is_err = true;
          returnCd = BstgwConstant.RETURN_CD.NK;
          returnMsg = "[responseEntity-code is not OK][code: %s][message: %s]".formatted(common_code, common_message);
        }
      }
      else {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[procBeastApiRequest()][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
      }
      //-- I/F api call }
    }

    if (false == b_is_err) {
      Map<String, Object> mapSyncDb = new HashMap<String, Object>();

      if (BstgwApiLinkDataEntity.con_PROCTYPE_CREATE.equals(procType) || BstgwApiLinkDataEntity.con_PROCTYPE_UPDATE.equals(procType)) {
        //-- [i][beast query]
        BstgwApiLinkDataEntity queriedEntity = this.getApiLinkDataByTypeKey(target, bstgwApiLinkDataEntity.getType(), bstgwApiLinkDataEntity.getKey());
        //-- [i][query실패시에는 request를 사용]
        bstgwApiLinkDataEntity = ((null != queriedEntity) ? queriedEntity : bstgwApiLinkDataEntity);
      }
      mapSyncDb = BstgwApiLinkDataEntity.getSyncDbMap(bstgwApiLinkDataEntity);
      //-- [i][extend data]
      mapSyncDb.put("srcTag", BstgwConstant.SRC_TAG_APILINK);

      //-- [i][result]
      map_result.put("mapSyncDb", mapSyncDb);

      //-- [i][write sync beast db][BST_SYNC_ADM_API_LINK_DATA, BST_SYNC_TB_ADM_API_LINK_DATA] {
      try {
        String req_mode = "";
        if (BstgwApiLinkDataEntity.con_PROCTYPE_CREATE.equals(procType)) {
          req_mode = "insert";
        }
        else if (BstgwApiLinkDataEntity.con_PROCTYPE_UPDATE.equals(procType)) {
          req_mode = "update";
        }
        else if (BstgwApiLinkDataEntity.con_PROCTYPE_DELETE.equals(procType)) {
          req_mode = "delete";
        }
        this.tranBstSyncAdmApiLinkData(target, req_mode, mapSyncDb);
      }
      catch (Exception e) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[tranBstSyncAdmApiLinkData()][e: %s]".formatted(e.getMessage());
      }
      //-- [i][write sync beast db][BST_SYNC_ADM_API_LINK_DATA, BST_SYNC_TB_ADM_API_LINK_DATA] }
    }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }

  @Override
  public BstgwApiLinkDataEntity getApiLinkDataByTypeKey(String target, String type, String key) {
    String paramApiTarget = target;
    String paramApiDomain = ""; //-- [not_used // use only pre-defined target]
    String paramApiUrl = "/apilink/v1/data/getApiLinkDataByType";
    String paramApiMethod = "GET";
    String paramApiBody = "";
    String paramApiQstr = "?type=%s&key=%s".formatted(type, key);
		boolean b_is_if_hist_write = false;

    Map<String, Object> map_in = new HashMap<>();

    map_in.put("api_target", paramApiTarget);
    map_in.put("api_domain", paramApiDomain);
    map_in.put("api_url", paramApiUrl);
    map_in.put("api_method", paramApiMethod);
    map_in.put("api_body", paramApiBody);
    map_in.put("api_qstr", paramApiQstr);
		map_in.put("direct", (b_is_if_hist_write ? "" : "no_write_hist"));

    Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
    String returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
    //--@@String returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
    Map<String, Object> map_result = (Map<String, Object>)map_ret.get("result");

    BstgwApiLinkDataEntity bstgwApiLinkDataEntity = null;
    if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(returnCd)) {
      ResponseEntity<String> responseEntity = (ResponseEntity<String>)map_result.get("responseEntity");
      if ((null != responseEntity) && (responseEntity.getStatusCode() == HttpStatus.OK)) {
        String responseBody = responseEntity.getBody();
        JSONObject jso_body = JSONObject.fromObject(responseBody);
        JSONObject jso_body_common = jso_body.getJSONObject("common");
        int common_code = ((null != jso_body_common) ? jso_body_common.getInt("code") : -1);
        //--##String common_message = ((null != jso_body_common) ? KsmUtil.fnSafeStr(jso_body_common.get("message")) : common_code);
        JSONObject jso_body_data = ((common_code == 200) ? jso_body.getJSONObject("data") : null);
        JSONObject jso_body_data_value = ((null != jso_body_data) ? jso_body_data.getJSONObject("value") : null);
        //-- [i][20230805]
        //-- [i][except처리] {
        //-- [i]["value"key에 대한 값이 json문자열인경우 JSON parsing시 object로 deserialize하는 issue]
        JSONObject jsoValue = null;	//-- [i]"value"key의 값을 보관저장
        if ((null != jso_body_data_value) && (true == jso_body_data_value.has("value"))) {
          Object objValue = jso_body_data_value.get("value");
          if (objValue instanceof JSONObject object) {
            jsoValue = object;	//-- [i]"value"key의 값을 보관저장
            //--##//-- [i][option처리]["value"에 문자열을 escaped처리하여 설정하지만 이후 jsoValue.toString()으로 재설정됨]
            //--##jso_body_data_value.put("value", jsoValue.toString().replace("\\", "\\\\").replace("\"", "\\\""));
            jso_body_data_value.put("value", "");	//-- [i]deserialize하는 issue를 위해 빈값설정
          }
        }
        //-- [i][except처리] }

        bstgwApiLinkDataEntity = ((null != jso_body_data_value) ? BstgwApiLinkDataEntity.setFromJson(jso_body_data_value.toString()): null);

        //-- [i][20230805]
        //-- [i][except처리] {
        if ((null != bstgwApiLinkDataEntity) && (null != jsoValue)) {
          bstgwApiLinkDataEntity.setValue(jsoValue.toString());	//-- [i]"value"key의 값을 재설정
        }
        //-- [i][except처리] }
      }
    }

    LOG.info("\n\n### {}.{}() [returnCd: {}][returnMsg: {}][result: {}][bstgwApiLinkDataEntity: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName()
      , map_ret.get("returnCd"), map_ret.get("returnMsg"), map_ret.get("result"), bstgwApiLinkDataEntity);

    return bstgwApiLinkDataEntity;
  }

  @Override
  //-- BEAST I/F API Request Call
  // {returnCd:, returnMsg:, result:{httpEntity:, responseEntity:, bstIfExeHist:} }
  public Map<String, Object> procBeastApiRequest(Map<String, Object> map_in) {
    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String apiTarget = KsmUtil.fnSafeStr(map_in.get("api_target"));
    String apiDomain = KsmUtil.fnSafeStr(map_in.get("api_domain"));
    String apiUrl = KsmUtil.fnSafeStr(map_in.get("api_url"));
    String apiMethod = KsmUtil.fnSafeStr(map_in.get("api_method")).toUpperCase();
    String apiBody = KsmUtil.fnSafeStr(map_in.get("api_body"));
    String apiQstr = KsmUtil.fnSafeStr(map_in.get("api_qstr"));
    String direct = ";%s;".formatted(KsmUtil.fnSafeStr(map_in.get("direct")));
    String sbCheck = KsmUtil.fnSafeStr(map_in.get("sb_check"));
    String svcId = KsmUtil.fnSafeStr(map_in.get("svc_id"));
    String SB_APPINSTID = "";
    String DEVAPPLY_SEQ = "";
    String TB_APPINSTID = "";
    
    Map<String, Object> map_ret = new HashMap<>();
    if(sbCheck.equals("TB")) {
    	try {
                TDevApplyInfoVO voOut_svc = this.selSbCehck(svcId);
                SB_APPINSTID = (voOut_svc == null || voOut_svc.getSbAppinstid() == null) ? "" : voOut_svc.getSbAppinstid().trim();
                DEVAPPLY_SEQ = (voOut_svc == null || voOut_svc.getDevapplySeq() == null) ? "" : voOut_svc.getDevapplySeq().trim();
        }
        catch (Exception e) {
          //-- [i][ignore write history except]
          LOG.error("\n\n### {}.{}() [[Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
    }else if(sbCheck.equals("PRD")) {
    	try {
			TDevApplyInfoVO voOut_svc = this.selTBAppinstid(svcId);
			TB_APPINSTID = (voOut_svc == null || voOut_svc.getTbAppinstid() == null) ? "" : voOut_svc.getTbAppinstid().trim();
			Map<String, Object> new_map_in = new HashMap<>();
			
			String paramApiTarget = "TB";
			String paramApiDomain = ""; // -- [not_used // use only pre-defined target]
			String paramApiUrl = "/apilink/v1/svc/getSvcDplyById";
			String paramApiMethod = "GET";
			String paramApiBody = "";
			String paramApiQstr = "?svcId=%s".formatted(TB_APPINSTID);
			
			boolean b_is_if_hist_write = false;
			
			new_map_in.put("api_target", paramApiTarget);
			new_map_in.put("api_domain", paramApiDomain);
			new_map_in.put("api_url", paramApiUrl);
			new_map_in.put("api_method", paramApiMethod);
			new_map_in.put("api_body", paramApiBody);
			new_map_in.put("api_qstr", paramApiQstr);
			new_map_in.put("direct", (b_is_if_hist_write ? "" : "no_write_hist"));
			new_map_in.put("svcId", svcId);
			Map<String, Object> map_ret_tb_dply_type_check = procBeastApiRequest(new_map_in);
			String ret_returnCd = KsmUtil.fnSafeStr(map_ret_tb_dply_type_check.get("returnCd"));
	        if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
	        	map_ret.put("tbDplyTypeCheck", map_ret_tb_dply_type_check.get("result"));
	          }
			
    	}
	    catch (Exception e) {
	      //-- [i][ignore write history except]
	      LOG.error("\n\n### {}.{}() [[Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
	    }
    }
    
    boolean b_is_no_write_hist = (direct.indexOf(";no_write_hist;") != -1);

    if ("test".equalsIgnoreCase(apiTarget)) {
      apiDomain = bstProperties.bstgwApiTestUrl;
    }
    else if ("local".equalsIgnoreCase(apiTarget)) {
      apiDomain = bstProperties.bstgwApiLocalUrl;
    }
    else if (BstgwConstant.PROFILE.TB.equalsIgnoreCase(apiTarget)) {
      apiDomain = bstProperties.bstgwApiTbUrl;
    }
    else if (BstgwConstant.PROFILE.TB_KTC.equalsIgnoreCase(apiTarget)) {
    	apiDomain = bstProperties.bstgwApiTbUrl;
    }
    else if (BstgwConstant.PROFILE.TB_AZURE.equalsIgnoreCase(apiTarget)) {
    	apiDomain = bstProperties.bstgwApiNewTbUrl;
    }
    else if (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(apiTarget)) {
      apiDomain = bstProperties.bstgwApiPrdUrl;
    }
    else if (BstgwConstant.PROFILE.PRD_KTC.equalsIgnoreCase(apiTarget)) {
    	apiDomain = bstProperties.bstgwApiPrdUrl;
    }
    else if (BstgwConstant.PROFILE.PRD_AZURE.equalsIgnoreCase(apiTarget)) {
    	apiDomain = bstProperties.bstgwApiNewPrdUrl;
    }

    if (false == b_is_err) {
      if (apiDomain.length() == 0) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[invalid apiDomain][apiTarget: %s][apiDomain: %s]".formatted(apiTarget, apiTarget);
      }
    }
    if (false == b_is_err) {
      apiUrl = apiDomain + apiUrl + apiQstr;

      HttpMethod httpMethod = HttpMethod.POST;
      if ("GET".equals(apiMethod)) {
        httpMethod = HttpMethod.GET;
      }
      else if ("PUT".equals(apiMethod)) {
        httpMethod = HttpMethod.PUT;
      }
      else if ("DELETE".equals(apiMethod)) {
        httpMethod = HttpMethod.DELETE;
      }


      RestTemplate restTemplate = this.bstRestTemplate;
      //--@@RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      //-- [i][set header] {
      //-- [i][Content-Type=application/json; charset=UTF-8]
      Charset utf8 = Charset.forName("UTF-8");
      MediaType mediaType = new MediaType("application", "json", utf8);
      String xAgwTxId = UUID.randomUUID().toString();

      headers.add("Authorization", bstProperties.bstgwApiHeaderAuthorization);
      headers.add("BaseUrl", bstProperties.bstgwApiHeaderBaseUrl);
      headers.setContentType(mediaType);
      headers.add("X-AGW-TX-ID", xAgwTxId);
      //-- [i][set header] }

      HttpEntity<String> httpEntity = new HttpEntity<>(apiBody, headers);
      ResponseEntity<String> responseEntity = null;
      try {
        responseEntity = restTemplate.exchange(apiUrl, httpMethod, httpEntity, String.class);
        LOG.info("[httpEntity: {}][responseEntity: {}]", httpEntity, responseEntity);
      }
      catch (Exception e) {
        LOG.error("\n\n### {}.{}() [[Exception: {}]e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[restTemplate.exchange()][e:%s]".formatted(e.getMessage());
      }

      //-- [i][write history] {
      Map<String, Object> map_hist_in = new HashMap<>();
      int logSeq = -1;
      if (true == b_is_no_write_hist) {
        logSeq = 0;
      }
      else {
        String logTag = "[X-AGW-TX-ID:%s]".formatted(xAgwTxId);
        map_hist_in.put("logTag", KsmUtil.fnSafeStr(logTag));
        map_hist_in.put("reqUri", KsmUtil.fnSafeStr(apiUrl));
        map_hist_in.put("reqMethod", KsmUtil.fnSafeStr(httpMethod.toString()));
        map_hist_in.put("reqHeader", KsmUtil.fnSafeStr(httpEntity.getHeaders().toString()));
        map_hist_in.put("reqBody", KsmUtil.fnSafeStr(httpEntity.getBody()));
        //-- [i][for][피연산자 유형 충돌: varbinary은(는) text과(와) 호환되지 않습니다.][Exception: SQLException SQL state [S0002]; error code [206];]
        map_hist_in.put("resStatusCode", "");
        map_hist_in.put("resHeader", "");
        map_hist_in.put("resBody", "");
        map_hist_in.put("logMsg", "");
        if (null != responseEntity) {
          map_hist_in.put("resStatusCode", KsmUtil.fnSafeStr(responseEntity.getStatusCode().toString()));
          map_hist_in.put("resHeader", KsmUtil.fnSafeStr(responseEntity.getHeaders().toString()));
          map_hist_in.put("resBody", KsmUtil.fnSafeStr(responseEntity.getBody()));
        }
        if (BstgwConstant.RETURN_CD.EXCEPT.equals(returnCd)) {
          map_hist_in.put("logMsg", returnMsg);
        }
        try {
          int nRet = this.insertBstIfExecHist(map_hist_in);
          logSeq = nRet;
        }
        catch (Exception e) {
          //-- [i][ignore write history except]
          LOG.error("\n\n### {}.{}() [[Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
      }
      //-- [i][write history] }

      //-- [i][result]
      map_result.put("httpEntity", httpEntity); //-- request
      map_result.put("responseEntity", responseEntity); //-- response
      map_hist_in.put("logSeq", logSeq);
      map_result.put("bstIfExeHist", map_hist_in);  //-- log
    }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);
    map_ret.put("sbCheck", SB_APPINSTID);
    map_ret.put("devApplySeq", DEVAPPLY_SEQ);

    return map_ret;
  }

  //-- get BstgwApiDplyEntity from KOA_TB_API_DEF.API_NO
  // {returnCd:, returnMsg:, result:{bstgwApiDplyEntity:} }
  @Override
  public Map<String, Object> getBstgwApiDplyEntity(String target, int apiNo, String dplyType) {
    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String req_api_no = "%d".formatted(apiNo);
    String req_gw_profile = ((BstgwConstant.PROFILE.TB.equalsIgnoreCase(target) || BstgwConstant.PROFILE.TB_KTC.equalsIgnoreCase(target) || BstgwConstant.PROFILE.TB_AZURE.equalsIgnoreCase(target)) ? GwProfile.TB.getKey() : GwProfile.PROD.getKey());

    BstgwApiDplyEntity bstgwApiDplyEntity = null;

    AdptranApiVO mapOut_api = null;
    if (false == b_is_err) {
      //-- API규격 검색
      mapOut_api = adptranApiService.select_API_DEF_with_API_SPC(req_api_no);
      if (mapOut_api == null) {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[adptranApiService.select_API_DEF_with_API_SPC()][apiNo: %s]".formatted(req_api_no);
      }
    }

    List<String> param_loc_list = new ArrayList<>();    //-- PARAM_LOC filter
    //--:@apigw deploy정보구성
    ApiEntity apiEntity = new ApiEntity();
    if (false == b_is_err) {
      //-- API파라메터 검색
      param_loc_list.add("header");
      param_loc_list.add("body");
      List<AdptranParamVO> listOut_param = adptranApiService.select_API_PARAM_list(req_api_no, param_loc_list);

      //-- set ApiEntity
      AdptranApiResultCode resultCode = AdptranUtil.set_ApiInfo_To_ApiEntity(req_gw_profile, mapOut_api, listOut_param, apiEntity);
      LOG.info("\n\n### {}.{}() [AdptranUtil.set_ApiInfo_To_ApiEntity()][result_Cd: {}][result_Msg: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), resultCode.getCode(), resultCode.getMessage());
      if (resultCode == AdptranApiResultCode.RC_SET_APIENTITY_SUCC) {
        //-- [i][set dplytype for DEL]
        dplyType = (BstgwConstant.DPLY_TYPE.DEL.equals(dplyType) ? BstgwConstant.DPLY_TYPE.DEL : BstgwConstant.DPLY_TYPE.DPLY);
        apiEntity.setBstgwDplyType(dplyType);

        BstgwApiDplyEntityConverter bstgwApiDplyEntityConverter = new BstgwApiDplyEntityConverter();
        bstgwApiDplyEntity = bstgwApiDplyEntityConverter.convert(apiEntity);
      }
      else {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[AdptranUtil.set_ApiInfo_To_ApiEntity()][result_Cd: %d][result_Msg: %s]".formatted(resultCode.getCode(), resultCode.getMessage());
      }
    }

    //-- [i][result]
    map_result.put("bstgwApiDplyEntity", bstgwApiDplyEntity);

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }

  //-- get BstgwApiDplyEntity String from KOA_TB_API_DEF.API_NO
  @Override
  public String getBstgwApiDplyEntityString(String target, int apiNo, String dplyType, String direct) {
    direct = (";" + KsmUtil.fnSafeStr(direct) + ";");

    String entityJson = "";

    Map<String, Object> map_ret = this.getBstgwApiDplyEntity(target, apiNo, dplyType);
    String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
    String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));

    if (BstgwConstant.RETURN_CD.OK.equals(ret_returnCd)) {
      Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
      BstgwApiDplyEntity bstgwApiDplyEntity = (BstgwApiDplyEntity)ret_map_result.get("bstgwApiDplyEntity");
      if (null != bstgwApiDplyEntity) {
        entityJson = bstgwApiDplyEntity.getJson(bstgwApiDplyEntity, direct);
        if (null == entityJson) {
          entityJson = "[ERR][%s]".formatted("[Exception: getJson()]");
        }
      }
      else {
        entityJson = "[ERR][%s]".formatted("[bstgwApiDplyEntity is null]");
      }
    }
    else {
      entityJson = "[ERR][%s]".formatted("[getBstgwApiDplyEntity()][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg));
    }

    return entityJson;
  }

  //-- get BstgwSvcDplyEntity from T_DEV_APPLY_API_INFO.DEVAPPLY_SEQ
  // {returnCd:, returnMsg:, result:{bstgwSvcDplyEntity:} }
  @Override
  public Map<String, Object> getBstgwSvcDplyEntity(String target, int devapplySeq, String dplyType) {
    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String req_gw_profile = ((BstgwConstant.PROFILE.TB.equalsIgnoreCase(target) || BstgwConstant.PROFILE.TB_KTC.equalsIgnoreCase(target) || BstgwConstant.PROFILE.TB_AZURE.equalsIgnoreCase(target)) ? GwProfile.TB.getKey() : GwProfile.PROD.getKey());

    BstgwSvcDplyEntity bstgwSvcDplyEntity = null;

    //-- 서비스 검색 (T_DEV_APPLY_API)
    TDevApplyInfoVO voOut_svc = null;

    List<String> mapApiAut = new ArrayList<String>();
    List<String> mapAlwdIp = new ArrayList<String>();
    try {
      voOut_svc = this.selTDevApplyInfo(devapplySeq);
    }
    catch (Exception e) {
      LOG.error("\n\n### {}.{}() [selTDevApplyInfo()][Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
      b_is_err = true;
      returnCd = BstgwConstant.RETURN_CD.EXCEPT;
      returnMsg = "[selTDevApplyInfo()][e: %s]".formatted(e.getMessage());
    }

    if (false == b_is_err) {
      if (null != voOut_svc) {
        if (false == b_is_err) {
          //-- 신청API 검색 (T_DEV_APPLY_API)
          try {
            List<BstDevApplyApiVO> listOut_api = this.selTDevApplyApiList(devapplySeq);
            if (null != listOut_api) {
              for (BstDevApplyApiVO item : listOut_api) {
                mapApiAut.add(KsmUtil.fnSafeStr(item.getApiNm()));
              }
            }
            else {
              b_is_err = true;
              returnCd = BstgwConstant.RETURN_CD.ERR;
              returnMsg = "[selTDevApplyApiList()][devapplySeq: %d]".formatted(devapplySeq);
            }
          }
          catch (Exception e) {
            LOG.error("\n\n### {}.{}() [selTDevApplyApiList()][Exception: {}][e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            b_is_err = true;
            returnCd = BstgwConstant.RETURN_CD.EXCEPT;
            returnMsg = "[selTDevApplyApiList()][e: %s]".formatted(e.getMessage());
          }
        }
        if (false == b_is_err) {
          //-- 신청IP 검색 (PRD: T_DEV_APPLY_IP_REQ_LIST, T_DEV_APPLY_IP_REQ)(TB: T_DEV_APPLY_IP_LIST)
          try {
            List<BstIpListVO> listOut_ip = this.selGetIpList(target, devapplySeq);
            if (null != listOut_ip) {
              for (BstIpListVO item : listOut_ip) {
                String ip = KsmUtil.fnSafeStr(item.getIp());
                ip = KsmUtil.fmt_data(ip, "fmt_ip4_cc_range_regexp");
                mapAlwdIp.add(ip);
              }
            }
            else {
              b_is_err = true;
              returnCd = BstgwConstant.RETURN_CD.ERR;
              returnMsg = "[selGetIpList()][devapplySeq: %d]".formatted(devapplySeq);
            }
          }
          catch (Exception e) {
            LOG.error("\n\n### {}.{}() [selGetIpList()][Exception: {}][e {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
            b_is_err = true;
            returnCd = BstgwConstant.RETURN_CD.EXCEPT;
            returnMsg = "[selGetIpList()][e: %s]".formatted(e.getMessage());
          }
        }

        bstgwSvcDplyEntity = new BstgwSvcDplyEntity();

        //-- [i][NN] {
        Date dtNow = new Date();
        String dplyDt = "%sT%s".formatted((new SimpleDateFormat("yyyy-MM-dd")).format(dtNow), (new SimpleDateFormat("HH:mm:ss")).format(dtNow));
        bstgwSvcDplyEntity.setDplyDt(dplyDt);
        //-- [i][set dplytype for DEL]
        dplyType = (BstgwConstant.DPLY_TYPE.DEL.equals(dplyType) ? BstgwConstant.DPLY_TYPE.DEL : BstgwConstant.DPLY_TYPE.DPLY);
        bstgwSvcDplyEntity.setDplyType(dplyType);
        String appInstId = "";
        String sdpPw = "";
        if (true == GwProfile.PROD.getKey().equals(req_gw_profile)) {
          appInstId = KsmUtil.fnSafeStr(voOut_svc.getSbAppinstid());
          sdpPw = KsmUtil.fnSafeStr(voOut_svc.getSbSdpPw());
        }
        else if (true == GwProfile.TB.getKey().equals(req_gw_profile)) {
          appInstId = KsmUtil.fnSafeStr(voOut_svc.getTbAppinstid());
          sdpPw = KsmUtil.fnSafeStr(voOut_svc.getTbSdpPw());
        }
        bstgwSvcDplyEntity.setSvcId(appInstId);
        bstgwSvcDplyEntity.setSvcNm(KsmUtil.fnSafeStr(voOut_svc.getDevTitle()));
        bstgwSvcDplyEntity.setUserNm(appInstId);
        bstgwSvcDplyEntity.setPw(sdpPw);
        //-- [i][NN] }

        //-- [i][tag:SR-20230227][PROD의 svcStDt, svcEndDt 설정변경]
        //-- [i][PROD의 start date는 배포일 - 1일][PROD의 end date는 2099-12-31]
        String svcStDt = "";
        String svcEndDt = "";
        if (true == GwProfile.PROD.getKey().equals(req_gw_profile)) {
          Calendar cal = java.util.Calendar.getInstance();
          cal.setTime(dtNow);
          cal.add(Calendar.DATE, -1);
          svcStDt = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());
          svcStDt = "%sT%s".formatted(svcStDt.substring(0, 10), svcStDt.substring(11, 19));
          svcEndDt = "2099-12-31T00:00:00";
        }
        else if (true == GwProfile.TB.getKey().equals(req_gw_profile)) {
          svcStDt = KsmUtil.fnSafeStr(voOut_svc.getTbkVStt()); //-- yyyy-mm-dd hh:mm:ss
          svcEndDt = KsmUtil.fnSafeStr(voOut_svc.getTbkVEdt());
          svcStDt = ((svcStDt.length() == 19) ? "%sT%s".formatted(svcStDt.substring(0, 10), svcStDt.substring(11, 19)) : dplyDt);
          svcEndDt = ((svcEndDt.length() == 19) ? "%sT%s".formatted(svcEndDt.substring(0, 10), svcEndDt.substring(11, 19)) : "2099-12-31T00:00:00");
        }
        bstgwSvcDplyEntity.setSvcStDt(svcStDt);
        bstgwSvcDplyEntity.setSvcEndDt(svcEndDt);
        bstgwSvcDplyEntity.setApiAut(mapApiAut);
        //--##SlaEntity sla = new SlaEntity();
        //--##bstgwSvcDplyEntity.setSla(sla);
        IpAcesAutEntity ipAcesAut = new IpAcesAutEntity();
        ipAcesAut.setAlwdIp(mapAlwdIp);
        ipAcesAut.setBlckIp(new ArrayList<String>());
        bstgwSvcDplyEntity.setIpAcesAut(ipAcesAut);
        AtribEntity atrib = new AtribEntity();
        atrib.setCpId(KsmUtil.fnSafeStr(voOut_svc.getBstgwAtribCpId()));
        atrib.setServiceId(KsmUtil.fnSafeStr(voOut_svc.getBstgwAtribServiceId()));
        bstgwSvcDplyEntity.setAtrib(atrib);
      }
      else {
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.ERR;
        returnMsg = "[selTDevApplyInfo()][devapplySeq: %d]".formatted(devapplySeq);
      }
    }

    //-- [i][result]
    map_result.put("bstgwSvcDplyEntity", bstgwSvcDplyEntity);

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }

  //-- get BstgwSvcDplyEntity String from T_DEV_APPLY_API_INFO.DEVAPPLY_SEQ
  @Override
  public String getBstgwSvcDplyEntityString(String target, int devapplySeq, String dplyType, String direct) {
    direct = (";" + KsmUtil.fnSafeStr(direct) + ";");

    String entityJson = "";

    Map<String, Object> map_ret = this.getBstgwSvcDplyEntity(target, devapplySeq, dplyType);
    String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
    String ret_returnMsg = KsmUtil.fnSafeStr(map_ret.get("returnMsg"));

    if (BstgwConstant.RETURN_CD.OK.equals(ret_returnCd)) {
      Map<String, Object> ret_map_result = (Map<String, Object>)map_ret.get("result");
      BstgwSvcDplyEntity bstgwSvcDplyEntity = (BstgwSvcDplyEntity)ret_map_result.get("bstgwSvcDplyEntity");
      if (null != bstgwSvcDplyEntity) {
        entityJson = bstgwSvcDplyEntity.getJson(bstgwSvcDplyEntity, direct);
        if (null == entityJson) {
          entityJson = "[ERR][%s]".formatted("[Exception: getJson()]");
        }
      }
      else {
        entityJson = "[ERR][%s]".formatted("[bstgwApiDplyEntity is null]");
      }
    }
    else {
      entityJson = "[ERR][%s]".formatted("[getBstgwApiDplyEntity()][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg));
    }

    return entityJson;
  }


  //-- [i][DATA CRUD] {
  @Override
  //-- /beast/api/{pathVal}/ajax_query.do
  public ModelMap beastApiAjaxQuery(HttpServletRequest request, String pathVal) {
    ModelMap model = new ModelMap();

    int pageUnitVal = pageUnit;  // 페이지당 건수
    int pageSizeVal = pageSize;  // 페이지 리스트에 게시되는 건수

    pageUnit = KsmUtil.parseInt(request.getParameter("pageUnit"), pageUnit);
    pageSize = KsmUtil.parseInt(request.getParameter("pageSize"), pageSize);
    int pageIndex = KsmUtil.parseInt(request.getParameter("pageIndex"), 1);
    Pagination paginationInfo = new Pagination();

    String req_cmd = KsmUtil.fnSafeStr(request.getParameter("cmd"));
    //-- [i][target: PRD, TB구분]
    String target = KsmUtil.fnSafeStr(request.getParameter("target"));
//    target = (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB);

    Map<String, Object> map_in = new HashMap<>();

    LOG.debug("\n\n### {}.{}() [pathVal: {}][cmd: {}][target: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), pathVal, req_cmd, target);

    if (true == "common".equals(pathVal)) {
      //-- [2023:codeeyes][empty_block issue]
    }
    else if (true == "bstAdmSysDply".equals(pathVal)) { //-- bstAdmSysDplyList.jsp
      if (true == "cmd_db_list".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmSysDplyList.jsp list정보 query
        map_in.put("sysId", request.getParameter("sysId"));
        map_in.put("sysNm", request.getParameter("sysNm"));
        map_in.put("dplyType", request.getParameter("dplyType"));

        List<BstSyncAdmSysDplyVO> list_out = new ArrayList<>();

        int totCnt = 0;
        try {
          totCnt = this.selectBstSyncAdmSysDplyListCnt(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmSysDplyItemListCnt][" + e.getMessage() + "]");
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
            list_out = this.selectBstSyncAdmSysDplyList(target, map_in);
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[selectBstAdmSysDplyItemList][" + e.getMessage() + "]");
            return model;
          }
        }

        model.addAttribute("nlist", list_out);//목록 정보
        model.addAttribute("paginationInfo", paginationInfo);
      }
      else if (true == "cmd_db_item".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmSysDplyList.jsp item정보 query
        map_in.put("sysId", request.getParameter("sysId"));

        List<BstSyncAdmSysDplyVO> list_out = new ArrayList<>();

        map_in.put("firstIndex", 0);
        map_in.put("lastIndex", 1);
        try {
          list_out = this.selectBstSyncAdmSysDplyList(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmSysDplyItemList][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "bstAdmApiDply".equals(pathVal)) { //-- bstAdmApiDplyList.jsp
      if (true == "cmd_db_list".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmApiDplyList.jsp list정보 query
        map_in.put("apiId", request.getParameter("apiId"));
        map_in.put("sysId", request.getParameter("sysId"));
        map_in.put("ifNo", request.getParameter("ifNo"));
        map_in.put("srcTag", request.getParameter("srcTag"));
        map_in.put("dplyType", request.getParameter("dplyType"));

        List<BstSyncAdmApiDplyVO> list_out = new ArrayList<>();

        int totCnt = 0;
        try {
          totCnt = this.selectBstSyncAdmApiDplyListCnt(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmApiDplyItemListCnt][" + e.getMessage() + "]");
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
            list_out = this.selectBstSyncAdmApiDplyList(target, map_in);
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[selectBstAdmApiDplyItemList][" + e.getMessage() + "]");
            return model;
          }
        }

        model.addAttribute("nlist", list_out);//목록 정보
        model.addAttribute("paginationInfo", paginationInfo);
      }
      else if (true == "cmd_db_item".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmApiDplyList.jsp item정보 query
        map_in.put("apiId", request.getParameter("apiId"));

        List<BstSyncAdmApiDplyVO> list_out = new ArrayList<>();

        map_in.put("firstIndex", 0);
        map_in.put("lastIndex", 1);
        try {
          list_out = this.selectBstSyncAdmApiDplyList(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmApiDplyItemList][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "bstAdmSvcDply".equals(pathVal)) { //-- bstAdmSvcDplyList.jsp
      if (true == "cmd_db_list".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmSvcDplyList.jsp list정보 query
        map_in.put("svcId", request.getParameter("svcId"));
        map_in.put("svcNm", request.getParameter("svcNm"));
        map_in.put("srcTag", request.getParameter("srcTag"));
        map_in.put("dplyType", request.getParameter("dplyType"));

        List<BstSyncAdmSvcDplyVO> list_out = new ArrayList<>();

        int totCnt = 0;
        try {
          totCnt = this.selectBstSyncAdmSvcDplyListCnt(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmSvcDplyItemListCnt][" + e.getMessage() + "]");
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
            list_out = this.selectBstSyncAdmSvcDplyList(target, map_in);
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[selectBstAdmSvcDplyItemList][" + e.getMessage() + "]");
            return model;
          }
        }

        model.addAttribute("nlist", list_out);//목록 정보
        model.addAttribute("paginationInfo", paginationInfo);
      }
      else if (true == "cmd_db_item".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmSvcDplyList.jsp item정보 query
        map_in.put("svcId", request.getParameter("svcId"));

        List<BstSyncAdmSvcDplyVO> list_out = new ArrayList<>();

        map_in.put("firstIndex", 0);
        map_in.put("lastIndex", 1);
        try {
          list_out = this.selectBstSyncAdmSvcDplyList(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmSvcDplyItemList][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "bstAdmApiLinkData".equals(pathVal)) { //-- bstAdmApiLinkDataList.jsp
      if (true == "cmd_db_list".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmApiLinkDataList.jsp list정보 query
        map_in.put("aldtType", request.getParameter("aldtType"));
        map_in.put("aldtKey", request.getParameter("aldtKey"));
        map_in.put("srcTag", request.getParameter("srcTag"));
        map_in.put("dplyType", request.getParameter("dplyType"));

        List<BstSyncAdmApiLinkDataVO> list_out = new ArrayList<>();

        int totCnt = 0;
        try {
          totCnt = this.selectBstSyncAdmApiLinkDataListCnt(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmApiLinkDataItemListCnt][" + e.getMessage() + "]");
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
            list_out = this.selectBstSyncAdmApiLinkDataList(target, map_in);
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[selectBstAdmApiLinkDataItemList][" + e.getMessage() + "]");
            return model;
          }
        }

        model.addAttribute("nlist", list_out);//목록 정보
        model.addAttribute("paginationInfo", paginationInfo);
      }
      else if (true == "cmd_db_item".equalsIgnoreCase(req_cmd)) {
        //--[i] bstAdmApiLinkDataList.jsp item정보 query
        map_in.put("aldtType", request.getParameter("aldtType"));
        map_in.put("aldtKey", request.getParameter("aldtKey"));

        List<BstSyncAdmApiLinkDataVO> list_out = new ArrayList<>();

        map_in.put("firstIndex", 0);
        map_in.put("lastIndex", 1);
        try {
          list_out = this.selectBstSyncAdmApiLinkDataList(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selectBstAdmApiLinkDataItemList][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "popBstSysSelect".equals(pathVal)) { //--[i] popBstSysSelect.jsp
      if (true == "cmd_db_list".equalsIgnoreCase(req_cmd)) {
        //--[i] popBstSysSelect.jsp item정보 query
        map_in.put("koaSysId", request.getParameter("koaSysId"));
        map_in.put("sysId", request.getParameter("sysId"));
        map_in.put("sysNm", request.getParameter("sysNm"));
        map_in.put("edptAtribUrl", request.getParameter("edptAtribUrl"));

        List<BstSyncAdmSysDplyVO> list_out = new ArrayList<>();

        try {
          list_out = this.selBstSyncAdmSysDplyList(target, map_in);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selBstSyncAdmSysDplyList][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "deployView".equals(pathVal)) {  //--[i] deployView.jsp
      if (true == "cmd_sel_bst_if_exec_hist".equalsIgnoreCase(req_cmd)) {
        //--[i] deployView.jsp I/F로그 item정보 query
        int seq = KsmUtil.parseInt(request.getParameter("seq"), -1);

        List<BstIfExecHistVO> list_out = new ArrayList<>();
        try {
          list_out = this.selBstIfExecHist(seq);
        }
        catch (Exception e) {
          model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
          model.addAttribute("returnMsg", "[selBstIfExecHist][" + e.getMessage() + "]");
          return model;
        }
        model.addAttribute("nlist", list_out);  //item 정보
      }
    }
    else if (true == "eg_case".equals(pathVal)) { //-- eg_case.jsp
    }

    return model;
  }

  @Override
  //-- /beast/api/{pathVal}/ajax_proc.do
  public ModelMap beastApiAjaxProc(HttpServletRequest request, String pathVal, String requestBody) throws JsonMappingException, JsonProcessingException {
    ModelMap model = new ModelMap();

    UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
    String ss_userId = ((userVO != null) ? userVO.getEnCmbrId() : "");

    JSONObject jso_body = JSONObject.fromObject(requestBody);
    String req_cmd = jso_body.optString("cmd", "");
    //-- [i][target: PRD, TB구분]
    String target = jso_body.optString("target", "");
//    target = (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB);

    String returnCd = "";
    String returnMsg = "";
    String sbCheck = "";
    String devApplySeq = "";
    boolean b_is_err = false;

    Map<String, Object> map_in = new HashMap<>();

    LOG.debug("\n\n### {}.{}() [pathVal: {}][cmd: {}][target: {}]###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), pathVal, req_cmd, target);

    if (true == "common".equals(pathVal)) {
      if (true == "cmd_api_send".equalsIgnoreCase(req_cmd)) {
        String req_api_target = jso_body.optString("api_target", "");
        String req_api_domain = jso_body.optString("api_domain", "");
        String req_api_url = jso_body.optString("api_url", "");
        String req_api_method = jso_body.optString("api_method", "");
        String req_api_body = jso_body.optString("api_body", "");
        String req_direct = jso_body.optString("direct", "");
        String req_sb_check = jso_body.optString("sb_check", "");
        String req_svc_id = jso_body.optString("svc_id", "");

        if ((false == b_is_err) && (req_api_domain.length() == 0) && (req_api_target.length() == 0)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "api_domain or api_target is not found";
        }
        if ((false == b_is_err) && (req_api_target.length() > 0)) {
          if ((false == b_is_err) && (";test;tb;prd;".indexOf((";" + req_api_target + ";").toLowerCase()) == -1)) {
            b_is_err = true; returnCd = "E02"; returnMsg = "api_target is not valid";
          }
        }
        if ((false == b_is_err) && (req_api_url.length() == 0)) {
          b_is_err = true; returnCd = "E03"; returnMsg = "api_url is not found";
        }
        if ((false == b_is_err) && (req_api_method.length() == 0)) {
          b_is_err = true; returnCd = "E04"; returnMsg = "api_method is not found";
        }
        if (false == b_is_err) {
          map_in.put("api_target", req_api_target);
          map_in.put("api_domain", req_api_domain);
          map_in.put("api_url", req_api_url);
          map_in.put("api_method", req_api_method);
          map_in.put("api_body", req_api_body);
          map_in.put("direct", req_direct);
          map_in.put("sb_check", req_sb_check);
          map_in.put("svc_id", req_svc_id);

          Map<String, Object> map_ret = this.procBeastApiRequest(map_in);
          String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
          String ret_returnMsg= KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
          if(map_ret.get("sbCheck") != null && map_ret.get("sbCheck") != "") {
        	  sbCheck = KsmUtil.fnSafeStr(map_ret.get("sbCheck"));
          }
          if(map_ret.get("devApplySeq") != null && map_ret.get("devApplySeq") != "") {
        	  devApplySeq = KsmUtil.fnSafeStr(map_ret.get("devApplySeq"));
          }
          if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
            model.addAttribute("result", map_ret.get("result"));
            if(map_ret.get("tbDplyTypeCheck") != null && map_ret.get("tbDplyTypeCheck") != "") {
            	model.addAttribute("tbDplyTypeCheck", map_ret.get("tbDplyTypeCheck"));
            }
          }
          else {
            b_is_err = true; returnCd = "E05"; returnMsg = "[procBeastApiRequest][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
          }
        }
      }
      else if (true == "cmd_api_shub_instauth".equalsIgnoreCase(req_cmd)) {
        String req_api_url = jso_body.optString("api_url", "");
        String req_api_method = jso_body.optString("api_method", "");
        String req_api_body = jso_body.optString("api_body", "");
        String req_direct = jso_body.optString("direct", "");

        if ((false == b_is_err) && (req_api_url.length() == 0)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "api_url is not found";
        }
        if ((false == b_is_err) && (req_api_method.length() == 0)) {
          b_is_err = true; returnCd = "E02"; returnMsg = "api_method is not found";
        }
        if (false == b_is_err) {
          map_in.put("api_url", req_api_url);
          map_in.put("api_method", req_api_method);
          map_in.put("api_body", req_api_body);
          map_in.put("direct", req_direct);

          Map<String, Object> map_ret = this.procApiRequestProxy(map_in);
          String ret_returnCd = KsmUtil.fnSafeStr(map_ret.get("returnCd"));
          String ret_returnMsg= KsmUtil.fnSafeStr(map_ret.get("returnMsg"));
          if (BstgwConstant.RETURN_CD.OK.equalsIgnoreCase(ret_returnCd)) {
            model.addAttribute("result", map_ret.get("result"));
          }
          else {
            b_is_err = true; returnCd = "E03"; returnMsg = "[procApiRequestProxy][returnCd: %s][returnMsg: %s]".formatted(ret_returnCd, ret_returnMsg);
          }
        }
      }
      else if (true == "cmd_get_bst_api_deploy_payload".equalsIgnoreCase(req_cmd)) {
        String req_api_no = jso_body.optString("api_no", "");
        String req_target = jso_body.optString("target", "");
        String req_dplytype =jso_body.optString("dplytype", "");
        String req_direct = jso_body.optString("direct", ""); //-- exclude_null;pretty_json
        req_dplytype = (BstgwConstant.DPLY_TYPE.DEL.equals(req_dplytype) ? BstgwConstant.DPLY_TYPE.DEL : BstgwConstant.DPLY_TYPE.DPLY);
        model.addAttribute("result", this.getBstgwApiDplyEntityString(req_target, KsmUtil.parseInt(req_api_no, 0), req_dplytype, req_direct));
      }
      else if (true == "cmd_get_bst_svc_deploy_payload".equalsIgnoreCase(req_cmd)) {
        String req_devapply_seq = jso_body.optString("devapply_seq", "");
        String req_target = jso_body.optString("target", "");
        String req_dplytype =jso_body.optString("dplytype", "");
        String req_direct = jso_body.optString("direct", ""); //-- exclude_null;pretty_json
        req_dplytype = (BstgwConstant.DPLY_TYPE.DEL.equals(req_dplytype) ? BstgwConstant.DPLY_TYPE.DEL : BstgwConstant.DPLY_TYPE.DPLY);
        model.addAttribute("result", this.getBstgwSvcDplyEntityString(req_target, KsmUtil.parseInt(req_devapply_seq, 0), req_dplytype, req_direct));
      }
      else {
        b_is_err = true; returnCd = "E01"; returnMsg = "cmd not defined";
      }
    }
    else if (true == "bstAdmSysDply".equals(pathVal)) { //-- bstAdmSysDplyList.jsp
      if (true == "cmd_db_tran".equalsIgnoreCase(req_cmd)) {
        String req_mode = jso_body.optString("mode", "");
        JSONObject jso_rec = jso_body.optJSONObject("rec");
        //--##map_in.put("amdr", ss_userId);

        if ((false == b_is_err) && (null == jso_rec)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "rec not found";
        }
        if (false == b_is_err) {
          Map<String, Object> map_rec = new HashMap<>();
          Iterator it = jso_rec.keys();
          while (it.hasNext()){
            String key = (String) it.next();
            String val = jso_rec.optString(key);
            map_rec.put(key, val);
          }
          map_in.putAll(map_rec);
        }
        if (true == "update".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_sysId = jso_rec.optString("sysId", "");
            if (req_sysId.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(sysId) is not found";
            }
          }
        }
        else if (true == "delete".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_seq = jso_rec.optString("seq", "");
            if (req_seq.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(seq) is not found";
            }
          }
        }
        else if (true == "insert".equalsIgnoreCase(req_mode)) {
          //-- [2023:codeeyes][empty_block issue]
        }
        else {
          b_is_err = true; returnCd = "E01"; returnMsg = "mode not defined";
        }
        if (false == b_is_err) {
          try {
            model.addAttribute("result", this.tranBstSyncAdmSysDply(target, req_mode, map_in));
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[tranBstSyncAdmSysDply][" + e.getMessage() + "]");
            return model;
          }
        }
      }
      else {
        b_is_err = true; returnCd = "E01"; returnMsg = "cmd not defined";
      }
    }
    else if (true == "bstAdmApiDply".equals(pathVal)) { //-- bstAdmApiDplyList.jsp
      if (true == "cmd_db_tran".equalsIgnoreCase(req_cmd)) {
        String req_mode = jso_body.optString("mode", "");
        JSONObject jso_rec = jso_body.optJSONObject("rec");
        //--##map_in.put("amdr", ss_userId);

        if ((false == b_is_err) && (null == jso_rec)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "rec not found";
        }
        if (false == b_is_err) {
          Map<String, Object> map_rec = new HashMap<>();
          Iterator it = jso_rec.keys();
          while (it.hasNext()){
            String key = (String) it.next();
            String val = jso_rec.optString(key);
            map_rec.put(key, val);
          }
          map_in.putAll(map_rec);
        }
        if (true == "update".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_apiId = jso_rec.optString("apiId", "");
            if (req_apiId.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(apiId) is not found";
            }
          }
        }
        else if (true == "delete".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_seq = jso_rec.optString("seq", "");
            if (req_seq.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(seq) is not found";
            }
          }
        }
        else if (true == "insert".equalsIgnoreCase(req_mode)) {
          //-- [2023:codeeyes][empty_block issue]
        }
        else {
          b_is_err = true; returnCd = "E01"; returnMsg = "mode not defined";
        }
        if (false == b_is_err) {
          try {
            model.addAttribute("result", this.tranBstSyncAdmApiDply(target, req_mode, map_in));
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[tranBstSyncAdmApiDply][" + e.getMessage() + "]");
            return model;
          }
        }
      }
      else {
        b_is_err = true; returnCd = "E01"; returnMsg = "cmd not defined";
      }
    }
    else if (true == "bstAdmSvcDply".equals(pathVal)) { //-- bstAdmSvcDplyList.jsp
      if (true == "cmd_db_tran".equalsIgnoreCase(req_cmd)) {
        String req_mode = jso_body.optString("mode", "");
        JSONObject jso_rec = jso_body.optJSONObject("rec");
        //--##map_in.put("amdr", ss_userId);
        String svcId = jso_body.optString("svc_id", "");
        if ((false == b_is_err) && (null == jso_rec)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "rec not found";
        }
        if (false == b_is_err) {
          Map<String, Object> map_rec = new HashMap<>();
          Iterator it = jso_rec.keys();
          while (it.hasNext()){
            String key = (String) it.next();
            String val = jso_rec.optString(key);
            map_rec.put(key, val);
          }
          map_in.putAll(map_rec);
        }
        if (true == "update".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_svcId = jso_rec.optString("svcId", "");
            if (req_svcId.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(svcId) is not found";
            }
          }
        }
        else if (true == "delete".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_seq = jso_rec.optString("seq", "");
            if (req_seq.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(seq) is not found";
            }
          }
        }
        else if (true == "insert".equalsIgnoreCase(req_mode)) {
          //-- [2023:codeeyes][empty_block issue]
        }
        else {
          b_is_err = true; returnCd = "E01"; returnMsg = "mode not defined";
        }
        if (false == b_is_err) {
          try {
	    	  if (true == "delete".equalsIgnoreCase(req_mode)) {
	          	deleteSvcDply(target, svcId);
	          }
            model.addAttribute("result", this.tranBstSyncAdmSvcDply(target, req_mode, map_in));
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[tranBstSyncAdmSvcDply, deleteSvcDply][" + e.getMessage() + "]");
            return model;
          }
        }
      }
      else {
        b_is_err = true; returnCd = "E01"; returnMsg = "cmd not defined";
      }
    }
    else if (true == "bstAdmApiLinkData".equals(pathVal)) { //-- bstAdmApiLinkDataList.jsp
      if (true == "cmd_db_tran".equalsIgnoreCase(req_cmd)) {
        String req_mode = jso_body.optString("mode", "");
        JSONObject jso_rec = jso_body.optJSONObject("rec");
        //--##map_in.put("amdr", ss_userId);

        if ((false == b_is_err) && (null == jso_rec)) {
          b_is_err = true; returnCd = "E01"; returnMsg = "rec not found";
        }
        if (false == b_is_err) {
          Map<String, Object> map_rec = new HashMap<>();
          Iterator it = jso_rec.keys();
          while (it.hasNext()){
            String key = (String) it.next();
            String val = jso_rec.optString(key);
            map_rec.put(key, val);
          }
          map_in.putAll(map_rec);
        }
        if (true == "update".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_aldt_type = jso_rec.optString("aldtType", "");
            String req_aldt_key = jso_rec.optString("aldtKey", "");
            if (req_aldt_type.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(aldtType) is not found";
            }
            else if (req_aldt_key.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(aldtKey) is not found";
            }
          }
        }
        else if (true == "delete".equalsIgnoreCase(req_mode)) {
          if (false == b_is_err) {
            String req_seq = jso_rec.optString("seq", "");
            if (req_seq.length() <= 0) {
              b_is_err = true; returnCd = "E01"; returnMsg = "key(seq) is not found";
            }
          }
        }
        else if (true == "insert".equalsIgnoreCase(req_mode)) {
          //-- [2023:codeeyes][empty_block issue]
        }
        else {
          b_is_err = true; returnCd = "E01"; returnMsg = "mode not defined";
        }
        if (false == b_is_err) {
          try {
            model.addAttribute("result", this.tranBstSyncAdmApiLinkData(target, req_mode, map_in));
          }
          catch (Exception e) {
            model.addAttribute("returnCd", BstgwConstant.RETURN_CD.EXCEPT);
            model.addAttribute("returnMsg", "[tranBstSyncAdmApiLinkData][" + e.getMessage() + "]");
            return model;
          }
        }
      }
      else {
        b_is_err = true; returnCd = "E01"; returnMsg = "cmd not defined";
      }
    }
    else if (true == "eg_case".equals(pathVal)) { //-- eg_case.jsp
    }
    else {
      b_is_err = true; returnCd = "E01"; returnMsg = "pathVal not defined";
    }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    model.addAttribute("returnCd", returnCd);
    model.addAttribute("returnMsg", returnMsg);
    if(sbCheck != null && sbCheck != "") {
    	model.addAttribute("sbCheck", sbCheck);
    }
    model.addAttribute("devApplySeq", devApplySeq);

    return model;
  }

  @Override
  //-- BEAST-시스템-R-목록
  public List<BstSyncAdmSysDplyVO> selectBstSyncAdmSysDplyList(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_SYS_DPLY_list(map_in);
  }

  @Override
  //-- BEAST-시스템-R-목록count
  public int selectBstSyncAdmSysDplyListCnt(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_SYS_DPLY_count(map_in);
  }

  @Override
  //-- BEAST-시스템-CUD [mode: insert, update, delete]
  public int tranBstSyncAdmSysDply(String target, String mode, Map<String, Object> map_in) throws Exception {
    int n_ret = -1;
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    if (("insert".equalsIgnoreCase(mode)) || ("update".equalsIgnoreCase(mode))) {
      n_ret = beastDAO.merge_BST_SYNC_ADM_SYS_DPLY(map_in);
    }
    else if ("delete".equalsIgnoreCase(mode)) {
      n_ret = beastDAO.delete_BST_SYNC_ADM_SYS_DPLY(map_in);
    }
    return n_ret;
  }

  @Override
  //-- BEAST-시스템R-SEARCH
  public List<BstSyncAdmSysDplyVO> selBstSyncAdmSysDplyList(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_SYS_DPLY_list(map_in);
  }

  @Override
  //-- BEAST-API-R-목록
  public List<BstSyncAdmApiDplyVO> selectBstSyncAdmApiDplyList(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_API_DPLY_list(map_in);
  }

  @Override
  //-- BEAST-API-R-목록-count
  public int selectBstSyncAdmApiDplyListCnt(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_API_DPLY_count(map_in);
  }

  @Override
  //-- BEAST-API-CUD [mode: insert, update, delete]
  public int tranBstSyncAdmApiDply(String target, String mode, Map<String, Object> map_in) throws Exception {
    int n_ret = -1;
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    if (("insert".equalsIgnoreCase(mode)) || ("update".equalsIgnoreCase(mode))) {
      n_ret = beastDAO.merge_BST_SYNC_ADM_API_DPLY(map_in);
    }
    else if ("delete".equalsIgnoreCase(mode)) {
      n_ret = beastDAO.delete_BST_SYNC_ADM_API_DPLY(map_in);
    }
    return n_ret;
  }

  @Override
  //-- BEAST-SVC-R-목록
  public List<BstSyncAdmSvcDplyVO> selectBstSyncAdmSvcDplyList(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_SVC_DPLY_list(map_in);
  }

  @Override
  //-- BEAST-SVC-R-목록count
  public int selectBstSyncAdmSvcDplyListCnt(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_SVC_DPLY_count(map_in);
  }

  @Override
  //-- BEAST-SVC-CUD [mode: insert, update, delete]
  public int tranBstSyncAdmSvcDply(String target, String mode, Map<String, Object> map_in) throws Exception {
    int n_ret = -1;
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    if (("insert".equalsIgnoreCase(mode)) || ("update".equalsIgnoreCase(mode))) {
      n_ret = beastDAO.merge_BST_SYNC_ADM_SVC_DPLY(map_in);
    }
    else if ("delete".equalsIgnoreCase(mode)) {
      n_ret = beastDAO.delete_BST_SYNC_ADM_SVC_DPLY(map_in);
    }
    return n_ret;
  }

  @Override
  //-- BEAST-API_LINK_DATA-R-목록
  public List<BstSyncAdmApiLinkDataVO> selectBstSyncAdmApiLinkDataList(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_API_LINK_DATA_list(map_in);
  }

  @Override
  //-- BEAST-API_LINK_DATA-R-목록count
  public int selectBstSyncAdmApiLinkDataListCnt(String target, Map<String, Object> map_in) throws Exception {
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    return beastDAO.select_BST_SYNC_ADM_API_LINK_DATA_count(map_in);
  }

  @Override
  //-- BEAST-API_LINK_DATA-CUD [mode: insert, update, delete]
  public int tranBstSyncAdmApiLinkData(String target, String mode, Map<String, Object> map_in) throws Exception {
    int n_ret = -1;
//    map_in.put("target", (BstgwConstant.PROFILE.PRD.equalsIgnoreCase(target) ? BstgwConstant.PROFILE.PRD : BstgwConstant.PROFILE.TB));
    map_in.put("target", target.toUpperCase());
    if (("insert".equalsIgnoreCase(mode)) || ("update".equalsIgnoreCase(mode))) {
      n_ret = beastDAO.merge_BST_SYNC_ADM_API_LINK_DATA(map_in);
    }
    else if ("delete".equalsIgnoreCase(mode)) {
      n_ret = beastDAO.delete_BST_SYNC_ADM_API_LINK_DATA(map_in);
    }
    return n_ret;
  }

  @Override
  //-- BEAST-I/F Execute 이력-C
  public int insertBstIfExecHist(Map<String, Object> map_in) throws Exception {
    return beastDAO.insert_BST_IF_EXEC_HIST(map_in);
  }
  
  @Override
  //-- BEAST-I/F Execute 이력-C
  public int deletePortalSvcTb(Map<String, Object> map_in) throws Exception {
    return beastDAO.delete_PORTAL_SVC_TB(map_in);
  }
  
  @Override
  //-- BEAST-I/F Execute 이력-C
  public int deletePortalSvcSb(Map<String, Object> map_in) throws Exception {
    return beastDAO.delete_PORTAL_SVC_SB(map_in);
  }

  @Override
  //-- BEAST-I/F LOG-R
  public List<BstIfExecHistVO> selBstIfExecHist(int seq) throws Exception {
    Map<String, Object> map_in = new HashMap<>();
    map_in.put("seq", seq);
    return beastDAO.sel_BST_IF_EXEC_HIST(map_in);
  }

  @Override
  //-- APILink서비스신청-기본정보-R
  public TDevApplyInfoVO selTDevApplyInfo(int devapplySeq) throws Exception {
    Map<String, Object> map_in = new HashMap<>();
    map_in.put("devapplySeq", devapplySeq);
    return beastDAO.sel_T_DEV_APPLY_INFO(map_in);
  }

  @Override
  //-- APILink서비스신청-API정보-R-목록
  public List<BstDevApplyApiVO> selTDevApplyApiList(int devapplySeq) throws Exception {
    Map<String, Object> map_in = new HashMap<>();
    map_in.put("devapplySeq", devapplySeq);
    return beastDAO.sel_T_DEV_APPLY_API_list(map_in);
  }

  @Override
  //-- APILink서비스신청-IP정보-R-목록
  public List<BstIpListVO> selGetIpList(String target, int devapplySeq) throws Exception {
    Map<String, Object> map_in = new HashMap<>();
    map_in.put("devapplySeq", devapplySeq);
    map_in.put("target", target);
    return beastDAO.sel_getIp_list(map_in);
  }

  @Override
  //-- 배포API정보-R
  public BstApiDeployVO selDeployView(int apiNo) throws Exception {
    Map<String, Object> map_in = new HashMap<>();
    map_in.put("apiNo", apiNo);
    return beastDAO.selDeployView(map_in);
  }

  //-- [i][DATA CRUD] }

  //-- I/F API Request Call
  // {returnCd:, returnMsg:, result:{httpEntity:, responseEntity:} }
  public Map<String, Object> procApiRequestProxy(Map<String, Object> map_in) {
    boolean b_is_err = false;
    String returnCd = BstgwConstant.RETURN_CD.INIT;
    String returnMsg = "";
    Map<String, Object> map_result = new HashMap<>();

    String apiUrl = KsmUtil.fnSafeStr(map_in.get("api_url"));
    String apiMethod = KsmUtil.fnSafeStr(map_in.get("api_method")).toUpperCase();
    String apiBody = KsmUtil.fnSafeStr(map_in.get("api_body"));
    String direct = ";%s;".formatted(KsmUtil.fnSafeStr(map_in.get("direct")));

    if (false == b_is_err) {
      HttpMethod httpMethod = HttpMethod.POST;
      if ("GET".equals(apiMethod)) {
        httpMethod = HttpMethod.GET;
      }
      else if ("PUT".equals(apiMethod)) {
        httpMethod = HttpMethod.PUT;
      }
      else if ("DELETE".equals(apiMethod)) {
        httpMethod = HttpMethod.DELETE;
      }


      RestTemplate restTemplate = this.bstRestTemplate;
      //--@@RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      //-- [i][set header] {
      /*--[ref]
      //-- [i][Content-Type=application/json; charset=UTF-8]
      Charset utf8 = Charset.forName("UTF-8");
      MediaType mediaType = new MediaType("application", "json", utf8);
      String xAgwTxId = UUID.randomUUID().toString();

      headers.add("Authorization", bstProperties.bstgwApiHeaderAuthorization);
      headers.add("BaseUrl", bstProperties.bstgwApiHeaderBaseUrl);
      headers.setContentType(mediaType);
      headers.add("X-AGW-TX-ID", xAgwTxId);
      --*/
      //-- [i][set header] }

      HttpEntity<String> httpEntity = new HttpEntity<>(apiBody, headers);
      ResponseEntity<String> responseEntity = null;
      try {
        responseEntity = restTemplate.exchange(apiUrl, httpMethod, httpEntity, String.class);
        LOG.info("[httpEntity: {}][responseEntity: {}]", httpEntity, responseEntity);
      }
      catch (Exception e) {
        LOG.error("\n\n### {}.{}() [[Exception: {}]e: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        b_is_err = true;
        returnCd = BstgwConstant.RETURN_CD.EXCEPT;
        returnMsg = "[restTemplate.exchange()][e:%s]".formatted(e.getMessage());
      }

      //-- [i][result]
      map_result.put("httpEntity", httpEntity); //-- request
      map_result.put("responseEntity", responseEntity); //-- response
    }

    if (false == b_is_err) {
      returnCd = BstgwConstant.RETURN_CD.OK;
    }

    Map<String, Object> map_ret = new HashMap<>();
    map_ret.put("returnCd", returnCd);
    map_ret.put("returnMsg", returnMsg);
    map_ret.put("result", map_result);

    return map_ret;
  }
  
  @Override
	//-- 상용키 존재 확인
	public TDevApplyInfoVO selSbCehck(String svcId) throws Exception {
	  Map<String, Object> map_in = new HashMap<>();
	  map_in.put("svcId", svcId);
	  return beastDAO.sel_SB_CHECK(map_in);
	}
  
  @Override
	//-- TB키 확인
	public TDevApplyInfoVO selTBAppinstid(String svcId) throws Exception {
	  Map<String, Object> map_in = new HashMap<>();
	  map_in.put("svcId", svcId);
	  return beastDAO.sel_TB_APPINSTID(map_in);
	}
}