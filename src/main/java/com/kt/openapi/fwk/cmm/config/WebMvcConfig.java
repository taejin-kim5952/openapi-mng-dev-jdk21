package com.kt.openapi.fwk.cmm.config;

import com.kt.openapi.fwk.online.filter.SessionCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.Arrays;
import java.util.Properties;

/**
 * Spring MVC 설정 클래스
 * [마이그레이션] dispatcher-servlet.xml 및 web.xml 설정을 Java Config로 통합
 * [표준] Tiles를 제거하고 JSP Tag Files 기반 레이아웃 시스템으로 전환을 고려한 설계
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessionCheckInterceptor sessionCheckInterceptor;

    public WebMvcConfig(SessionCheckInterceptor sessionCheckInterceptor) {
        this.sessionCheckInterceptor = sessionCheckInterceptor;
    }

    /**
     * 루트 경로(/) 및 웰컴 페이지 설정
     * [JSP 지원 제거 완료] 이전에는 "index" 뷰 이름이 webapp/index.jsp(<jsp:forward
     * page="main/index.do"/>)로 해석되었으나, JSP 리졸버가 사라져 더 이상 어떤 리졸버도
     * "index"를 처리하지 못한다. 인증 안 된 요청은 SessionCheckInterceptor가 먼저
     * /main/index.do로 리다이렉트하므로 평소엔 드러나지 않았지만, 인증된 사용자가 "/"로
     * 직접 접근하면 뷰를 찾지 못해 에러가 났을 것 - 원본 index.jsp와 동일한 동작(즉시
     * /main/index.do로 이동)을 리다이렉트로 명시적으로 재현.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/main/index.do");
    }

//    /**
//     * 콘텐츠 협상(Content Negotiation) 설정
//     */
//    @Override
//    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//        configurer
//            .favorParameter(false)
//            .ignoreAcceptHeader(false)
//            .defaultContentType(MediaType.TEXT_HTML)
//            .mediaType("json", MediaType.APPLICATION_JSON)
//            .mediaType("xml", MediaType.APPLICATION_XML)
//            .mediaType("text", MediaType.TEXT_PLAIN)
//            .mediaType("htm", MediaType.TEXT_HTML)
//            .mediaType("html", MediaType.TEXT_HTML)
//            .mediaType("do", MediaType.TEXT_HTML);
//    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // [Boot4 마이그레이션] favorPathExtension(boolean)이 Spring Framework에서 제거됨.
        // 확장자 기반 콘텐츠 협상은 이미 프레임워크 기본값이 비활성화(false)라 동작 변화 없음.
        configurer
                .favorParameter(false)              // 파라미터 기반 추론 비활성화
                .ignoreAcceptHeader(false)           // Accept 헤더는 사용
                .defaultContentType(MediaType.APPLICATION_JSON);  // 기본값 JSON
    }

    /**
     * 인터셉터 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Locale 변경 인터셉터
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);

        // 세션 체크 인터셉터 (레거시 urlList 유지)
        sessionCheckInterceptor.setUrlList(Arrays.asList(
            "/apidev/sample/egovSampleList.do",
            "/apidev/login/loginForm.do",
            "/apidev/login/newLoginCheck.do",
            "/apidev/login/otpSend.do",
            "/apidev/login/login_success.do",
            "/apidev/main/index.do",
            "/apidev/login/logout.do",
            "/apidev/userJoin/userJoinForm.do",
            "/apidev/bbs/notice/mvNoticeList.do",
            "/apidev/bbs/forum/mvForumList.do",
            "/apidev/faq/mvfaqList.do",
            "/apidev/qna/mvQnAList.do",
            "/apidev/bbs/notice/mvNoticeView.do",
            "/apidev/bbs/forum/mvForumView.do",
            "/apidev/userJoin/userInfo.do",
            "/apidev/userJoin/sJoinInfo.do",
            "/apidev/api/search/mvMainList.do",
            "/apidev/file/fileDownLoad.do",
            "/apidev/mbr.json",
            "/apidev/delapi.json",
            "/apidev/api.json",
            "/apidev/apipost.json",
            "/apidev/apiput.json",
            "/apidev/guide/mvUseList.do",
            "/apidev/guide/mvShubList.do",
            "/apidev/api/reg/yamlDownload.do",
            "/apidev/bbs/forum/selForumListAjax.do",
            "/apidev/faq/mvfaqTopListAjax.do",
            "/apidev/faq/mvfaqListAjax.do",
            "/apidev/faq/faqCateAjax.do",
            "/apidev/qna/selQnaListAjax.do",
            "/apidev/qna/mvQnaView.do",
            "/apidev/bbs/notice/selNoticeListAjax.do",
            "/apidev/bbs/notice/mvNoticeView.do",
            "/apidev/api/search/selMainListAjax.do",
            "/apidev/userJoin/insertJoin.do",
            "/apidev/file/fileDownType.do",
            "/apidev/agree/agViewinfo.do",
            "/apidev/priv/pViewinfo.do",
            "/apidev/devsupport/vmguide/devVmGuide.do",
            "/apidev/devsupport/tdapply/testdataapply.do",
            "/apidev/devsupport/sdkdwn/sdkdwn.do",
            "/apidev/devsupport/devsupport/devSupportList.do",
            "/apidev/api/info/mvInfoView.do",
            "/apidev/api/info/selApiAjax.do",
            "/apidev/api/info/selApiSearchList.do",
            "/apidev/api/info/savApiMenuListAjax.do",
            "/apidev/login/pssoLogout.do",
            "/apidev/login/auth/smsSend.do",
            "/apidev/login/auth/phoneConfirm.do",
            "/apidev/login/auth/authCheck.do",
            "/apidev/login/loginCheck.do",
            "/apidev/login/dupChkYn.do",
            "/apidev/api/sensitiveInfo/report.do",
            "/apidev/api/sensitiveInfo/register.do",
            "/apidev/adptran/(.*)",
            "/apidev/adptran_api/(.*)",
            "/apidev/apistatus_api/(.*)",
            "/apidev/ref_adptran_api/(.*)",
            "/apidev/api/arsenal/(.*)"
        ));
        registry.addInterceptor(sessionCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/resources/**");
    }

    /**
     * Thymeleaf ViewResolver
     * [JSP 지원 제거 완료] 이전에는 JSP 리졸버(order=2)와의 공존을 위해 setViewNames로 화면
     * 이름을 제한했으나, 이제 JSP가 완전히 제거되어 다른 폴백 리졸버가 없다. setViewNames는
     * "jsonView"/"redirect:*"/"forward:*" 같은 다른 리졸버(BeanNameViewResolver 등)가
     * 처리해야 하는 특수 뷰 이름을 ThymeleafViewResolver가 가로채지 않도록 여전히 필요하므로
     * 그대로 유지한다. 새 화면을 추가할 때마다 이 목록에 뷰 이름을 추가할 것.
     */
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setOrder(1);
        resolver.setViewNames(new String[]{
            "adptran/vue_page_mount_apistatus",
            "adptran/vue_page_mount",
            "adptran/devHome",
            "cmmn/public_error",
            "agree/view",
            "priv/view",
            "main/main",
            "guide/useList",
            "guide/shubList",
            "devsupport/tdapply/testdataapply",
            "devsupport/devsupport/list",
            "devsupport/devsupport/write",
            "devsupport/devsupport/view",
            "login/newLoginForm",
            "userJoin/userForm",
            "userJoin/userInfo",
            "userJoin/userJoin",
            "mypage/console",
            "mypage/mypage",
            "api/searchMain",
            "api/apiSearchList",
            "api/info/list",
            "api/sensitiveInfoReport",
            "api/sensitiveInfoRegister",
            "api/main",
            "api/deploy/deployList",
            "api/deploy/deployView",
            "api/deploy/approvalListNew",
            "api/deploy/verifyExecute",
            "api/deploy/tempForm",
            "api/deploy/tempFormDrm",
            "beast/deploy/verifyExecute",
            "beast/deploy/deployList",
            "beast/deploy/deployView",
            "beast/apigwmng/bstAdmApiLinkDataList",
            "beast/apigwmng/bstAdmSysDplyList",
            "beast/apigwmng/bstAdmApiDplyList",
            "beast/apigwmng/bstAdmSvcDplyList",
            "api/cateInfoRegForm",
            "api/dataTypeRegForm",
            "api/infoRegForm",
            "api/pathRegForm",
            "api/pathRegFormArsenal",
            "api/pathRegFormPrivate",
            "spcreg/spcReg",
            "spcreg/apiDefReg",
            "api/tmpltMngList",
            "api/tmpltMngForm",
            "api/simpleView"
        });
        return resolver;
    }

    /**
     * [JSP->Thymeleaf 마이그레이션] layout.tag 대체용 레이아웃 다이얼렉트
     */
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    /**
     * BeanNameViewResolver
     */
    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(0);
        return resolver;
    }

    /**
     * AJAX 응답을 위한 MappingJackson2JsonView
     */
    @Bean(name = "jsonView")
    public MappingJackson2JsonView jsonView() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setContentType("application/json;charset=UTF-8");
        return jsonView;
    }

    /**
     * 다국어 처리를 위한 Locale Resolver (세션 방식)
     */
    @Bean
    public SessionLocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    /**
     * 예외 처리(Exception Resolver) 설정
     */
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        resolver.setDefaultErrorView("cmmn/public_error");
        
        Properties mappings = new Properties();
        mappings.setProperty("org.springframework.dao.DataAccessException", "cmmn/public_error");
        mappings.setProperty("org.springframework.transaction.TransactionException", "cmmn/public_error");
        mappings.setProperty("org.springframework.security.AccessDeniedException", "cmmn/public_error");        
        resolver.setExceptionMappings(mappings);
        return resolver;
    }

    /**
     * 파일 업로드(Multipart) 설정
     * [마이그레이션] Spring Boot 3 표준인 StandardServletMultipartResolver 사용
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
