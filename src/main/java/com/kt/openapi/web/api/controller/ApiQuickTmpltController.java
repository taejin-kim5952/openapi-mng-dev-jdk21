package com.kt.openapi.web.api.controller;

import com.kt.openapi.web.api.service.ApiQuickRegService;
import com.kt.openapi.web.api.vo.ApiQuickTmpltVO;
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

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.controller
 * 2. 타입명   : ApiQuickTmpltController.java
 * 5. 설명     : "빠른 API 등록" 템플릿 관리(목록/등록/수정/삭제) 전용 컨트롤러.
 *              ApiQuickRegController와 같은 "빠른등록" 기능군이라 서비스/DAO는 공유하되,
 *              URL 네임스페이스만 /api/quickreg/tmplt로 분리했다.
 * </pre>
 */
@Controller
@RequestMapping(value = "/api/quickreg/tmplt")
public class ApiQuickTmpltController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiQuickTmpltController.class);

    @Autowired
    @Qualifier("apiQuickRegService")
    private ApiQuickRegService apiQuickRegService;

    @Autowired
    @Qualifier("CmnService")
    private CmnService cmnService;

    /** 템플릿 목록 화면 */
    @RequestMapping(value = "/mvTmpltMng.do")
    public ModelAndView mvTmpltMng() throws Exception {
        ModelAndView mv = new ModelAndView("api/tmpltMngList");
        mv.addObject("tmpltList", apiQuickRegService.selTmpltMngList());
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));
        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        return mv;
    }

    /** 템플릿 등록/수정 폼 (tmpltNo 없으면 신규) */
    @RequestMapping(value = "/mvTmpltForm.do")
    public ModelAndView mvTmpltForm(String tmpltNo) throws Exception {
        ModelAndView mv = new ModelAndView("api/tmpltMngForm");
        if (tmpltNo != null && !tmpltNo.trim().isEmpty()) {
            mv.addObject("tmplt", apiQuickRegService.selTmpltDetail(tmpltNo));
        }
        mv.addObject("apiGubList", cmnService.selComnList("APIGUB1000"));
        mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
        mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000"));
        return mv;
    }

    /** 템플릿 저장 (신규/수정 겸용) */
    @ResponseBody
    @RequestMapping(value = "/savTmpltAjax.do")
    public ModelAndView savTmpltAjax(HttpSession session, ApiQuickTmpltVO vo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");

        UserJoinVO userJVo = (UserJoinVO) session.getAttribute("ssUserVo");
        if (userJVo == null) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "로그인 세션이 만료되었습니다.");
            return mv;
        }

        if (vo.getTmpltNm() == null || vo.getTmpltNm().trim().isEmpty()) {
            mv.addObject("returnCode", "0");
            mv.addObject("message", "템플릿 이름을 입력해 주세요.");
            return mv;
        }

        vo.setRegr(userJVo.getEnCmbrId());
        vo.setAmdr(userJVo.getEnCmbrId());

        try {
            String tmpltNo = apiQuickRegService.savTmplt(vo);
            mv.addObject("returnCode", "1");
            mv.addObject("tmpltNo", tmpltNo);
        } catch (Exception e) {
            LOG.error("savTmpltAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "저장 중 오류가 발생했습니다.");
        }
        return mv;
    }

    /** 템플릿 삭제 (소프트 삭제) */
    @ResponseBody
    @RequestMapping(value = "/delTmpltAjax.do")
    public ModelAndView delTmpltAjax(String tmpltNo) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");
        try {
            apiQuickRegService.delTmplt(tmpltNo);
            mv.addObject("returnCode", "1");
        } catch (Exception e) {
            LOG.error("delTmpltAjax error", e);
            mv.addObject("returnCode", "0");
            mv.addObject("message", "삭제 중 오류가 발생했습니다.");
        }
        return mv;
    }
}
