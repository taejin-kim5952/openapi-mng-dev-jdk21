package com.kt.openapi.web.adptran.controller;

import com.kt.openapi.web.adptran.util.AdptranUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * [JSP->Thymeleaf 마이그레이션] adptran 뷰 템플릿에서 공통으로 쓰던 request 기반 값을
 * 모델 속성으로 미리 계산해 넣어준다. Thymeleaf 3.1부터 템플릿에서 #request를
 * 직접 쓸 수 없으므로(권장되지 않음), 원래 JSP의 <%= AdptranUtil.getBundleScriptSrc(request) %>
 * 를 대체.
 */
@ControllerAdvice(basePackageClasses = AdptranController.class)
public class AdptranViewModelAdvice {

    @ModelAttribute("dpBundleScriptSrc")
    public String dpBundleScriptSrc(HttpServletRequest request) {
        return AdptranUtil.getBundleScriptSrc(request);
    }
}
