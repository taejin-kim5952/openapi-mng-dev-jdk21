package com.kt.openapi.web.spcreg.controller;

import com.kt.openapi.web.apiDeploy.service.ApiDeployService;
import com.kt.openapi.web.apiDeploy.util.ApiDeployResultCode;
import com.kt.openapi.web.apiDeploy.vo.ApiDeployInsertVo;
import com.kt.openapi.web.spcreg.service.ApiDefRegService;
import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.controller
 * 2. 타입명   : ApiDefRegController.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 전용 컨트롤러. quickApiReg/기존 등록 마법사와는
 *              완전히 독립된 화면/저장 경로다. 이 화면은 SPC를 만들지 않는다 — spcReg가 만든
 *              apiSpcNo를 받아 그 아래 KOA_TB_API_DEF(+PARAM)만 추가한다.
 * </pre>
 */
@Controller
@RequestMapping(value = "/api/spcreg/def")
public class ApiDefRegController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefRegController.class);

    /** 비즈나루 서비스(sysId)일 때만 COMMON Handler에 "비즈나루API 여부" 항목이 추가된다 */
    @Value("${apisystem.sysid.biznaru}")
    private String apisystemSysidBiznaru;

    /**
     * Handler(APIHDR10xx)별로 실제 사용하는 Handler 파라미터 필드명.
     * 화면(apiDefReg.js DEF_HANDLER_PARAM)과 같은 표를 서버에도 두어, 화면을 우회한 요청이 와도
     * 해당 Handler와 무관한 컬럼에는 값이 들어가지 않도록 저장 직전에 비운다.
     */
    private static final Map<String, Set<String>> HANDLER_PARAM_FIELDS = Map.of(
        "APIHDR1010", Set.of("endpntClientIp", "hdpHndlroptnConfig", "hdpExtProp"),
        "APIHDR1020", Set.of("endpntClientIp", "resmapResCdField", "resmapSuccVal", "resmapErrCdField",
                             "resmapErrMsgField", "hdpHndlroptnConfig"),
        "APIHDR1030", Set.of("hdpHndlroptnConfig"),
        "APIHDR1040", Set.of("hdpHndlroptnConfig"),
        "APIHDR1050", Set.of("hdpApiOutCommonParam", "hdpApiEndpointId", "hdpReqApiName", "hdpReqUrlDecode",
                             "hdpResUrlEncode", "hdpReqConfigToBody", "hdpReqHeaderToBody", "hdpReqMappingToBody",
                             "hdpResMappingToBody", "hdpResProvideParam", "hdpHndlroptnConfig"),
        "APIHDR1060", Set.of("hdpReqUrlDecode", "hdpReqUrlEncode", "hdpResUrlEncode", "hdpReqConfigToBody",
                             "hdpReqHeaderToBody", "hdpReqMappingToBody", "hdpResMappingToBody",
                             "hdpResProvideParam", "hdpHndlroptnConfig"),
        "APIHDR1070", Set.of("hdpApiOutFormat", "hdpApiOutCommonParam", "hdpReqApiName", "hdpReqUrlDecode",
                             "hdpResUrlEncode", "hdpReqConfigToBody", "hdpReqHeaderToBody", "hdpReqMappingToBody",
                             "hdpResMappingToBody", "hdpResProvideParam", "hdpHndlroptnConfig")
    );

    /** HANDLER_PARAM_FIELDS에 등장하는 전체 필드(= Handler에 따라 켜지고 꺼지는 값들) */
    private static final List<String> ALL_HANDLER_PARAM_FIELDS = List.of(
        "endpntClientIp", "resmapResCdField", "resmapSuccVal", "resmapErrCdField", "resmapErrMsgField",
        "hdpApiEndpointId", "hdpReqApiName", "hdpApiOutFormat", "hdpApiOutCommonParam",
        "hdpReqMappingToBody", "hdpResMappingToBody", "hdpReqConfigToBody", "hdpReqHeaderToBody",
        "hdpResProvideParam", "hdpReqUrlDecode", "hdpReqUrlEncode", "hdpResUrlEncode",
        "hdpHndlroptnConfig", "hdpExtProp"
    );

    @Autowired
    @Qualifier("apiDefRegService")
    private ApiDefRegService apiDefRegService;

    @Autowired
    @Qualifier("CmnService")
    private CmnService cmnService;

    @Autowired
    private ApiDeployService apiDeployService;

    /**
     * API 등록 화면 렌더. 이 화면은 앞 단계(spcReg)에서 이미 확정된 apiSpcNo를 받아 그 그룹에
     * API(DEF)만 추가한다 - apiSpcNo가 없거나 존재하지 않는 그룹이면 그룹부터 만들도록 spcReg로
     * 돌려보낸다(퍼블 v14.0 확인 사항). 권한그룹(autId)은 그 그룹의 sysId로 서버에서 미리 필터링해
     * 내려준다(클라이언트에서 다시 필터링하지 않음).
     */
    @RequestMapping(value = "/mvApiDefReg.do")
    public ModelAndView mvApiDefReg(HttpSession session, ModelMap model, String apiSpcNo) throws Exception {
        LOG.debug("####################### ApiDefRegController mvApiDefReg START ############################");

        if (apiSpcNo == null || apiSpcNo.trim().isEmpty()) {
            return new ModelAndView("redirect:/api/spcreg/mvSpcReg.do");
        }

        Map<String, Object> spc = apiDefRegService.selSpcByNo(apiSpcNo);
        if (spc == null) {
            return new ModelAndView("redirect:/api/spcreg/mvSpcReg.do");
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("spcreg/apiDefReg");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        String sysId = String.valueOf(spc.get("sysId"));

        // 이 그룹의 서비스(sysId)에 속한 권한그룹만 - 화면엔 select 없이 그대로 렌더링
        List<AuthVO> autList = new ArrayList<>();
        if (userJVo != null && userJVo.getAuthList() != null) {
            for (AuthVO auth : userJVo.getAuthList()) {
                if (sysId.equals(auth.getSysId())) {
                    autList.add(auth);
                }
            }
        }
        mv.addObject("autList", autList);

        mv.addObject("apiSpcNo", apiSpcNo);
        mv.addObject("spcNm", spc.get("spcNm"));
        mv.addObject("spcBasPath", spc.get("basPath"));
        mv.addObject("sysId", sysId);
        // 그룹에서 BEAST G/W 사용을 켰을 때만 API 등록 화면에 BEAST 시스템 선택 UI를 노출한다.
        mv.addObject("bstgwYn", spc.get("bstgwYn"));

        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));
        mv.addObject("cntTypeList", cmnService.selComnList("CNTTYP1000"));
        mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
        mv.addObject("apiHandlerList", cmnService.selComnList("APIHDR1000"));
        // COMMON Handler에 "비즈나루API 여부" 항목을 추가할지 여부(기존 마법사 ApiRegController와 동일 기준)
        mv.addObject("isSysIdBiznaru", (apisystemSysidBiznaru != null && apisystemSysidBiznaru.equals(sysId)) ? "Y" : "");
        mv.addObject("piiList", cmnService.selComnList("PIICLS1000"));
        mv.addObject("tmpltList", apiDefRegService.selTmpltList());
        mv.addObject("apiProviderList", apiDefRegService.selApiProviderList());

        return mv;
    }

    /** 이 그룹(apiSpcNo)에 이미 등록된 API 목록 (좌측 트리) AJAX 조회 - 클릭하면 selApiDefDetailAjax로 불러온다 */
    @ResponseBody
    @RequestMapping(value = "/selDefListByApiSpcNoAjax.do")
    public ModelAndView selDefListByApiSpcNoAjax(String apiSpcNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        if (apiSpcNo == null || apiSpcNo.trim().isEmpty()) {
            mv.addObject("list", java.util.Collections.emptyList());
            return mv;
        }
        mv.addObject("list", apiDefRegService.selDefListByApiSpcNo(apiSpcNo));
        return mv;
    }

    /** API(DEF) 1건의 상세+파라미터 조회 - 좌측 트리에서 기존 API 클릭 시 폼에 그대로 불러오기 위함 */
    @ResponseBody
    @RequestMapping(value = "/selApiDefDetailAjax.do")
    public ModelAndView selApiDefDetailAjax(String apiNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        Map<String, Object> detail = apiDefRegService.selApiDefDetail(apiNo);
        if (detail == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "해당 API를 찾을 수 없습니다.");
            return mv;
        }
        mv.addObject("returnCode", "1");
        mv.addObject("def", detail);
        return mv;
    }

    /** API ID 중복 체크 - 기존 마법사와 동일하게 시스템 전체(그룹 무관)에서 유일해야 한다.
        수정 모드에서는 apiNo를 같이 넘겨 자기 자신은 중복 대상에서 빼고, 버전업 진행 중이면
        apiVerNo(원본의 버전 패밀리 키)도 같이 넘겨 같은 패밀리는 중복 대상에서 뺀다. */
    @ResponseBody
    @RequestMapping(value = "/selApiIdChkAjax.do")
    public ModelAndView selApiIdChkAjax(String apiId, String apiNo, String apiVerNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        boolean dup = apiDefRegService.selApiIdChk(apiId, apiNo, apiVerNo);
        mv.addObject("returnCode", "1");
        mv.addObject("dup", dup);
        return mv;
    }

    /** BEAST G/W 시스템 검색 - "TB G/W 시스템 선택"/"상용 G/W 시스템 선택" 팝업. target은
        tb|prd + platform(KTC|AZURE)을 조합해 "TB_KTC" 형태로 넘어온다. */
    @ResponseBody
    @RequestMapping(value = "/selBstSysListAjax.do")
    public ModelAndView selBstSysListAjax(String target, String sysId, String sysNm) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("list", apiDefRegService.selBstSysList(target, sysId, sysNm));
        return mv;
    }

    /** 다음 API ID 제안값("OIF_" + 5자리 순번) 조회 - "신규 API ID" 버튼 */
    @ResponseBody
    @RequestMapping(value = "/selNextApiIdAjax.do")
    public ModelAndView selNextApiIdAjax() throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("returnCode", "1");
        mv.addObject("nextApiId", apiDefRegService.selNextApiId());
        return mv;
    }

    /** API(DEF) 등록/수정 저장 - apiNo가 넘어오면 수정, 없으면 신규 등록(카테고리 재사용/최초생성 포함) */
    @ResponseBody
    @RequestMapping(value = "/savApiDefRegAjax.do")
    public ModelAndView savApiDefRegAjax(HttpSession session, ApiDefRegVO vo) throws Exception {
        LOG.debug("####################### ApiDefRegController savApiDefRegAjax START ############################");

        ModelAndView mv = new ModelAndView("jsonView");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }

        if (vo.getApiSpcNo() == null || vo.getApiSpcNo().trim().isEmpty()) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "API 그룹을 선택하세요.");
            return mv;
        }

        vo.setRegr(userJVo.getEnCmbrId());
        vo.setAmdr(userJVo.getEnCmbrId());

        if (vo.getCtgryNm() == null || vo.getCtgryNm().trim().isEmpty()) {
            vo.setCtgryNm("기본");
        }

        // Private이 아니면 Handler/Provider는 사용하지 않음
        if (!"APIGUB1020".equals(vo.getApiClass())) {
            vo.setApiHandlerCd(null);
            vo.setProviderSeq(null);
        }

        clearUnusedHandlerParams(vo);

        boolean isEdit = vo.getApiNo() != null && !vo.getApiNo().trim().isEmpty();

        try {
            String apiSpcNo = isEdit ? apiDefRegService.updApiDefReg(vo) : apiDefRegService.savApiDefReg(vo);

            if (!isEdit) {
                initDeployProc(vo.getApiNo(), userJVo.getEnCmbrId());
            }

            mv.addObject("returnCode", "1");
            mv.addObject("apiSpcNo", apiSpcNo);
            mv.addObject("apiNo", vo.getApiNo());
        } catch (Exception e) {
            LOG.error("savApiDefRegAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "등록 중 오류가 발생했습니다.");
        }

        return mv;
    }

    /**
     * 신규 등록된 API의 배포 프로세스(KOA_TB_DEPLOY_PROC) 초기 행을 만든다 - "API 등록/배포현황"
     * (/api/deploy/mvDeployList.do)이 이 테이블을 API_NO로 INNER JOIN해서 보여주므로, 이걸 안
     * 만들면 방금 등록한 API가 배포현황 목록에 영원히 나타나지 않는다. 기존 등록 마법사의 마지막
     * 단계(mvTempExcute.do, processGubun=insert)가 하던 일을 여기서 대신한다 - 초기 상태는 그
     * 흐름과 동일하게 DEPLOY1010(TB 배포전)/VERIFI1010(검증시작코드). API 등록 자체는 이미 성공했으므로
     * 여기서 실패해도 등록 결과는 그대로 성공 처리하되, 배포현황에 안 나타날 수 있다는 걸 로그로 남긴다.
     */
    private void initDeployProc(String apiNo, String regr) {
        try {
            ApiDeployInsertVo deployVo = new ApiDeployInsertVo();
            deployVo.setApiNo(apiNo);
            deployVo.setDeployCd(ApiDeployResultCode.CD_1010_DEPLOY_APPLY_CODE.getCode());
            deployVo.setVerifiCd(ApiDeployResultCode.CD_1010_VERIFI_BASE_CODE.getCode());
            deployVo.setRegr(regr);
            apiDeployService.insertDeployInfo(deployVo);
        } catch (Exception deployEx) {
            LOG.error("API(apiNo={}) 배포 프로세스 초기화 실패 - 배포현황 목록에 나타나지 않을 수 있습니다.", apiNo, deployEx);
        }
    }

    /**
     * 선택한 Handler에서 쓰지 않는 Handler 파라미터를 빈 값으로 만든다.
     * Handler를 바꿔가며 입력하다 저장하면 이전 Handler에서 채운 값이 그대로 남을 수 있는데,
     * 그 값이 KOA_TB_API_DEF에 저장되면 배포 시 G/W 설정이 실제 화면과 달라진다.
     * Private이 아니면(=Handler 자체가 없으면) 전부 비운다.
     */
    private void clearUnusedHandlerParams(ApiDefRegVO vo) {
        String handlerCd = vo.getApiHandlerCd();
        Set<String> used = (handlerCd == null) ? Set.of()
                : HANDLER_PARAM_FIELDS.getOrDefault(handlerCd, Set.of());

        for (String field : ALL_HANDLER_PARAM_FIELDS) {
            if (used.contains(field)) {
                continue;
            }
            try {
                Method setter = ApiDefRegVO.class.getMethod(
                        "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1), String.class);
                setter.invoke(vo, "");
            } catch (Exception e) {
                LOG.warn("clearUnusedHandlerParams: {} 필드를 비우지 못했습니다.", field, e);
            }
        }
    }

}
