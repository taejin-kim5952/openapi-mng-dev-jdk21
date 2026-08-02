package com.kt.openapi.fwk.cmm.config;

import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.servlet.http.HttpSession;
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

    @ModelAttribute("bIs2022PrjMode")
    public boolean bIs2022PrjMode() {
        return CommonFunc.isRunmodeTag("2022_prj_mode");
    }

    @ModelAttribute("ssUserVo")
    public UserJoinVO ssUserVo(HttpSession session) {
        return (UserJoinVO) session.getAttribute("ssUserVo");
    }
}
