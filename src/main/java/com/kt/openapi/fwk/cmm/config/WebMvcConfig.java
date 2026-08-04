package com.kt.openapi.fwk.cmm.config;

import com.kt.openapi.fwk.online.filter.SessionCheckInterceptor;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.Arrays;
import java.util.Collections;
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
     * JSP Prelude 설정 (모든 JSP에 taglib.jsp 자동 포함)
     * [마이그레이션] web.xml의 jsp-config 설정을 대체
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> jspConfigCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            JspPropertyGroup group = new JspPropertyGroup();
            group.addUrlPattern("*.jsp");
            group.addIncludePrelude("/WEB-INF/jsp/include/taglib.jsp");
            group.setPageEncoding("UTF-8");

            JspPropertyGroupDescriptorImpl groupDescriptor = new JspPropertyGroupDescriptorImpl(group);
            context.setJspConfigDescriptor(new JspConfigDescriptorImpl(
                Collections.singletonList(groupDescriptor), Collections.emptyList()));
        });
    }

    /**
     * 루트 경로(/) 및 웰컴 페이지 설정
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    /**
     * 정적 리소스 핸들러 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
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
     * 표준 JSP ViewResolver 설정
     * [JSP->Thymeleaf 마이그레이션] Thymeleaf가 먼저(order=1) 템플릿을 찾고,
     * 없으면 null을 반환해 이 리졸버(order=2)로 폴백 - 전환 중인 페이지와 공존.
     */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(2);
        return resolver;
    }

    /**
     * [JSP->Thymeleaf 마이그레이션] Thymeleaf ViewResolver
     * Boot가 자동구성한 SpringTemplateEngine을 재사용하고, JSP보다 우선순위를 높게(order=1) 설정.
     * ThymeleafViewResolver는 뷰 이름 존재 여부와 무관하게 항상 뷰를 만들어내고 실제 렌더링
     * 시점에야 실패하기 때문에(JSP 리졸버로 자연 폴백이 안 됨), setViewNames로 "이미 Thymeleaf로
     * 전환된 뷰 이름 패턴"만 명시적으로 처리하도록 제한한다. 전환이 끝나지 않은 나머지 뷰 이름은
     * 이 리졸버가 애초에 관여하지 않아 JSP 리졸버(order=2)로 넘어간다.
     * 새 화면을 Thymeleaf로 전환할 때마다 이 목록에 패턴을 추가할 것.
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
            "api/main"
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
