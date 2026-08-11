package com.kt.openapi.web.api.controller;

import com.kt.openapi.web.api.service.ApiSimpleViewService;
import com.kt.openapi.web.api.vo.ApiSimpleDefVO;
import com.kt.openapi.web.api.vo.ApiSimpleParamFormVO;
import com.kt.openapi.web.api.vo.ApiSimpleSpcVO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.controller
 * 2. 타입명   : ApiSimpleViewController.java
 * 5. 설명     : "간단 상세" 전용 컨트롤러. 기존 편집폼(ApiRegController)/죽은 상세뷰(ApiInfoController)와
 *              완전히 독립된 조회/저장 경로를 제공한다. 저장 대상 테이블은 기존과 동일
 *              (KOA_TB_API_SPC/DEF/PARAM)하지만 조회·수정 SQL/서비스는 새로 작성했다.
 * </pre>
 */
@Controller
@RequestMapping(value = "/api/simpleview")
public class ApiSimpleViewController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSimpleViewController.class);

    @Autowired
    @Qualifier("apiSimpleViewService")
    private ApiSimpleViewService apiSimpleViewService;

    @Autowired
    @Qualifier("CmnService")
    private CmnService cmnService;

    /** 간단 상세 화면 렌더. apiNo가 없으면 스펙의 첫 Path/Method를 기본 선택한다. */
    @RequestMapping(value = "/mvApiSimpleView.do")
    public ModelAndView mvApiSimpleView(String apiSpcNo, String apiNo) throws Exception {
        ModelAndView mv = new ModelAndView("api/simpleView");
        mv.addObject("spc", apiSimpleViewService.selSpcEssential(apiSpcNo));

        List<ApiSimpleDefVO> defList = apiSimpleViewService.selDefList(apiSpcNo);
        mv.addObject("defList", defList);

        String selectedApiNo = apiNo;
        if ((selectedApiNo == null || selectedApiNo.isEmpty()) && !defList.isEmpty()) {
            selectedApiNo = defList.get(0).getApiNo();
        }
        if (selectedApiNo != null && !selectedApiNo.isEmpty()) {
            mv.addObject("selectedDef", apiSimpleViewService.selDefDetail(selectedApiNo));
            mv.addObject("selectedParamList", apiSimpleViewService.selParamList(selectedApiNo));
        }

        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));
        mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
        mv.addObject("piiList", cmnService.selComnList("PIICLS1000"));
        return mv;
    }

    /** Path/Method 상세 + 파라미터 목록 AJAX 조회 (팝업용) */
    @ResponseBody
    @RequestMapping(value = "/selApiDefDetailAjax.do")
    public ModelAndView selApiDefDetailAjax(String apiNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("def", apiSimpleViewService.selDefDetail(apiNo));
        mv.addObject("paramList", apiSimpleViewService.selParamList(apiNo));
        return mv;
    }

    /** 스펙 필수정보 저장 */
    @ResponseBody
    @RequestMapping(value = "/savSpcEssentialAjax.do")
    public ModelAndView savSpcEssentialAjax(HttpSession session, ApiSimpleSpcVO vo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }
        vo.setAmdr(userJVo.getEnCmbrId());
        try {
            apiSimpleViewService.savSpcEssential(vo);
            mv.addObject("returnCode", "1");
        } catch (Exception e) {
            LOG.error("savSpcEssentialAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "저장 중 오류가 발생했습니다.");
        }
        return mv;
    }

    /** Path/Method 상세 저장 (엔드포인트/응답매핑/HDP 그룹) */
    @ResponseBody
    @RequestMapping(value = "/savApiDefDetailAjax.do")
    public ModelAndView savApiDefDetailAjax(HttpSession session, ApiSimpleDefVO vo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }
        vo.setAmdr(userJVo.getEnCmbrId());
        try {
            apiSimpleViewService.savDefDetail(vo);
            mv.addObject("returnCode", "1");
        } catch (Exception e) {
            LOG.error("savApiDefDetailAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "저장 중 오류가 발생했습니다.");
        }
        return mv;
    }

    /** 파라미터 목록 저장 (기존 전체 삭제 후 재등록) */
    @ResponseBody
    @RequestMapping(value = "/savApiDefParamsAjax.do")
    public ModelAndView savApiDefParamsAjax(HttpSession session, ApiSimpleParamFormVO form) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }

        List<Map<String, Object>> paramList = new ArrayList<>();
        if (form.getParamList() != null) {
            for (ApiSimpleParamFormVO.ApiSimpleParamVO p : form.getParamList()) {
                Map<String, Object> m = new HashMap<>();
                m.put("paramNm", p.getParamNm());
                m.put("dataTypeCd", p.getDataTypeCd());
                m.put("required", p.getRequired());
                m.put("paramDesc", p.getParamDesc());
                m.put("paramTypeCd", p.getParamTypeCd());
                m.put("paramLoc", p.getParamLoc());
                m.put("exam", p.getExam());
                m.put("personalData", p.getPersonalData());
                m.put("tempId", p.getTempId());
                m.put("parentTempId", p.getParentTempId());
                paramList.add(m);
            }
        }

        try {
            apiSimpleViewService.savDefParams(form.getApiNo(), paramList, userJVo.getEnCmbrId());
            mv.addObject("returnCode", "1");
        } catch (Exception e) {
            LOG.error("savApiDefParamsAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "파라미터 저장 중 오류가 발생했습니다.");
        }
        return mv;
    }
}
