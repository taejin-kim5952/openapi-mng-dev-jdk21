package com.kt.openapi.web.api.controller;

import com.kt.openapi.web.api.service.ApiQuickRegService;
import com.kt.openapi.web.api.vo.ApiQuickRegVO;
import com.kt.openapi.web.api.vo.ApiQuickTmpltVO;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.mypage.service.MypageService;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.controller
 * 2. 타입명   : ApiQuickRegController.java
 * 5. 설명     : "빠른 API 등록" 전용 컨트롤러. 기존 ApiRegController의 등록 흐름(YAML 파싱 기반)과는
 *              완전히 독립된 화면/저장 경로를 제공한다. 저장 대상 DB 테이블은 기존과 동일
 *              (KOA_TB_API_SPC/CTGRY/DEF/PARAM)하지만, 그 테이블에 넣는 SQL/서비스 로직은 새로 작성했다.
 * </pre>
 */
@Controller
@RequestMapping(value = "/api/quickreg")
public class ApiQuickRegController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiQuickRegController.class);

    @Autowired
    @Qualifier("apiQuickRegService")
    private ApiQuickRegService apiQuickRegService;

    @Autowired
    @Qualifier("CmnService")
    private CmnService cmnService;

    @Autowired
    @Qualifier("mypageService")
    private MypageService mypageService;

    /** 빠른 API 등록 화면 렌더 */
    @RequestMapping(value = "/mvApiQuickReg.do")
    public ModelAndView mvApiQuickReg(HttpSession session, ModelMap model) throws Exception {
        LOG.debug("####################### ApiQuickRegController mvApiQuickReg START ############################");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("api/quickApiReg");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");

        // 서비스(sysId) 드롭다운 - sysId 기준 중복제거 (기존 infoRegForm과 동일한 패턴)
        LinkedHashMap<String, AuthVO> authListDistinctBySysId = new LinkedHashMap<>();
        if (userJVo != null && userJVo.getAuthList() != null) {
            for (AuthVO auth : userJVo.getAuthList()) {
                authListDistinctBySysId.putIfAbsent(auth.getSysId(), auth);
            }
        }
        mv.addObject("authList", new ArrayList<>(authListDistinctBySysId.values()));

        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));
        mv.addObject("cntTypeList", cmnService.selComnList("CNTTYP1000"));
        mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
        mv.addObject("apiHandlerList", cmnService.selComnList("APIHDR1000"));
        mv.addObject("piiList", cmnService.selComnList("PIICLS1000"));

        List<ApiQuickTmpltVO> tmpltList = apiQuickRegService.selTmpltList();
        mv.addObject("tmpltList", tmpltList);

        return mv;
    }

    /** 선택한 서비스에 이미 등록된 API 목록 (좌측 참고 트리) AJAX 조회 */
    @ResponseBody
    @RequestMapping(value = "/selSysApiTreeAjax.do")
    public ModelAndView selSysApiTreeAjax(String sysId) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        if (sysId == null || sysId.trim().isEmpty()) {
            mv.addObject("list", java.util.Collections.emptyList());
            return mv;
        }
        mv.addObject("list", apiQuickRegService.selSysApiTree(sysId));
        return mv;
    }

    /** 템플릿 상세(기본 파라미터 포함) AJAX 조회 */
    @ResponseBody
    @RequestMapping(value = "/selTmpltDetailAjax.do")
    public ModelAndView selTmpltDetailAjax(String tmpltNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        ApiQuickTmpltVO tmplt = apiQuickRegService.selTmpltDetail(tmpltNo);
        mv.addObject("tmplt", tmplt);
        return mv;
    }

    /** 빠른 API 등록 저장 (기본정보+카테고리+Path/Method+파라미터를 한 번에 저장) */
    @ResponseBody
    @RequestMapping(value = "/savApiQuickRegAjax.do")
    public ModelAndView savApiQuickRegAjax(HttpSession session, ApiQuickRegVO vo) throws Exception {
        LOG.debug("####################### ApiQuickRegController savApiQuickRegAjax START ############################");

        ModelAndView mv = new ModelAndView("jsonView");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
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
        } else {
            vo.setBstgwYn("Y");
        }

        try {
            String apiSpcNo = apiQuickRegService.savApiQuickReg(vo);
            mv.addObject("returnCode", "1");
            mv.addObject("apiSpcNo", apiSpcNo);
        } catch (Exception e) {
            LOG.error("savApiQuickRegAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "등록 중 오류가 발생했습니다.");
        }

        return mv;
    }
}
