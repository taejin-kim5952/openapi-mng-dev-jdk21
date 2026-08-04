package com.kt.openapi.fwk.cmm.config;

import com.kt.openapi.web.adptran.util.AdptranUtil;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * [JSP->Thymeleaf 마이그레이션] layout.tag/top.jsp/footer.jsp 등 거의 모든 화면에서
 * 암묵적으로(스코프 검색으로) 참조하던 값들을 전역 모델 속성으로 명시적으로 주입한다.
 * Thymeleaf 3.1부터는 템플릿에서 #session을 직접 쓸 수 없고, JSP의 EL처럼
 * page/request/session 스코프를 자동으로 뒤져주지도 않기 때문에 필요.
 * 기존 컨트롤러의 session.setAttribute(...) 로직은 전혀 건드리지 않는다 (읽기 전용 투영).
 */
@ControllerAdvice
public class GlobalViewModelAdvice {

    @Autowired
    private Environment environment;

    @ModelAttribute("bIs2022PrjMode")
    public boolean bIs2022PrjMode() {
        return CommonFunc.isRunmodeTag("2022_prj_mode");
    }

    @ModelAttribute("ssUserVo")
    public UserJoinVO ssUserVo(HttpSession session) {
        return (UserJoinVO) session.getAttribute("ssUserVo");
    }

    // [JSP->Thymeleaf] 여러 JSP(api/main.jsp, api/deploy/deployView.jsp 등)에서 각자
    // <c:set var="b_is_master" value="${fn:toLowerCase(sessionScope['dev.master.id']) eq 'master'}" />
    // 형태로 중복 계산하던 것을 하나로 통합
    @ModelAttribute("b_is_master")
    public boolean bIsMaster(HttpSession session) {
        String devMasterId = (String) session.getAttribute("dev.master.id");
        return devMasterId != null && devMasterId.equalsIgnoreCase("master");
    }

    // [JSP->Thymeleaf] adptran/head_function.jsp, adptran/vue_part_mount.jsp가 쓰던
    // 세션값 우선 -> 없으면 환경설정값 폴백 로직을 그대로 재현 (dp_devUseJsConsoleLog, dp_configRunmode)

    @ModelAttribute("dp_devUseJsConsoleLog")
    public String dpDevUseJsConsoleLog(HttpSession session) {
        Object ss = session.getAttribute("dev.use.js.console.log");
        return (ss != null) ? ss.toString() : environment.getProperty("dev.use.js.console.log");
    }

    @ModelAttribute("dp_configRunmode")
    public String dpConfigRunmode(HttpSession session) {
        Object ss = session.getAttribute("config.runmode");
        return (ss != null) ? ss.toString() : environment.getProperty("config.runmode");
    }

    @ModelAttribute("dp_adptranApiUrl")
    public String dpAdptranApiUrl(HttpSession session) {
        String runmode = dpConfigRunmode(session);
        return "dev".equalsIgnoreCase(runmode)
                ? environment.getProperty("dev.adptran.api.url")
                : environment.getProperty("config.adptran.api.url");
    }

    @ModelAttribute("dp_requestUri")
    public String dpRequestUri(HttpServletRequest request) {
        Object uri = request.getAttribute("jakarta.servlet.forward.request_uri");
        return (uri != null) ? uri.toString() : "";
    }

    @ModelAttribute("dp_bundleScriptSrc")
    public String dpBundleScriptSrc(HttpServletRequest request) {
        return AdptranUtil.getBundleScriptSrc(request);
    }
}
