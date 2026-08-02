package com.kt.openapi.fwk.online.exception;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WAS 레벨의 에러(404, 500 등)를 처리하는 컨트롤러
 * [마이그레이션] web.xml의 error-page 설정을 대체
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // WAS 레벨의 에러 발생 시 공통 에러 페이지로 포워딩
        return "cmmn/public_error";
    }
}
