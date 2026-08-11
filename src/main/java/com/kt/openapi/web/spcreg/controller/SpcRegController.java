package com.kt.openapi.web.spcreg.controller;

import com.kt.openapi.web.spcreg.service.SpcRegService;
import com.kt.openapi.web.spcreg.vo.SpcRegVO;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.controller
 * 2. 타입명   : SpcRegController.java
 * 5. 설명     : "SPC 등록" 전용 컨트롤러. quickApiReg/기존 등록 마법사와는 완전히 독립된 화면/저장 경로다.
 *              저장 대상 DB 테이블은 KOA_TB_API_SPC 1건뿐 — API(Method+Path) 등록은 이 화면 범위 밖.
 * </pre>
 */
@Controller
@RequestMapping(value = "/api/spcreg")
public class SpcRegController {

    private static final Logger LOG = LoggerFactory.getLogger(SpcRegController.class);

    @Autowired
    @Qualifier("spcRegService")
    private SpcRegService spcRegService;

    @Autowired
    @Qualifier("CmnService")
    private CmnService cmnService;

    /** SPC 등록 화면 렌더 */
    @RequestMapping(value = "/mvSpcReg.do")
    public ModelAndView mvSpcReg(HttpSession session, ModelMap model) throws Exception {
        LOG.debug("####################### SpcRegController mvSpcReg START ############################");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("spcreg/spcReg");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");

        // 서비스(sysId) 드롭다운 - sysId 기준 중복제거 (quickApiReg와 동일한 패턴)
        LinkedHashMap<String, AuthVO> authListDistinctBySysId = new LinkedHashMap<>();
        if (userJVo != null && userJVo.getAuthList() != null) {
            for (AuthVO auth : userJVo.getAuthList()) {
                authListDistinctBySysId.putIfAbsent(auth.getSysId(), auth);
            }
        }
        mv.addObject("authList", new ArrayList<>(authListDistinctBySysId.values()));

        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));

        return mv;
    }

    /** 선택한 서비스에 이미 등록된 SPC + API 목록 (좌측 참고 트리) AJAX 조회 */
    @ResponseBody
    @RequestMapping(value = "/selSysSpcTreeAjax.do")
    public ModelAndView selSysSpcTreeAjax(String sysId) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        if (sysId == null || sysId.trim().isEmpty()) {
            mv.addObject("list", java.util.Collections.emptyList());
            return mv;
        }
        mv.addObject("list", spcRegService.selSysSpcTree(sysId));
        return mv;
    }

    /** SPC 등록 저장 */
    @ResponseBody
    @RequestMapping(value = "/savSpcRegAjax.do")
    public ModelAndView savSpcRegAjax(HttpSession session, SpcRegVO vo) throws Exception {
        LOG.debug("####################### SpcRegController savSpcRegAjax START ############################");

        ModelAndView mv = new ModelAndView("jsonView");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }

        vo.setRegr(userJVo.getEnCmbrId());
        vo.setAmdr(userJVo.getEnCmbrId());

        try {
            String apiSpcNo = spcRegService.savSpcReg(vo);
            mv.addObject("returnCode", "1");
            mv.addObject("apiSpcNo", apiSpcNo);
        } catch (Exception e) {
            LOG.error("savSpcRegAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "등록 중 오류가 발생했습니다.");
        }

        return mv;
    }
}
