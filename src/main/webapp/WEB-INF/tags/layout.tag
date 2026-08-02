<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ tag import="com.kt.openapi.web.util.CommonFunc" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- 레이어 타입 정의: default, main, apiReg, apiInfo, adptran, beast, empty --%>
<%@ attribute name="type" type="java.lang.String" %>
<%@ attribute name="title" type="java.lang.String" %>
<%@ attribute name="bodyClass" type="java.lang.String" %>

<%-- 추가적인 head 및 script 조각 --%>
<%@ attribute name="head" fragment="true" %>
<%@ attribute name="script" fragment="true" %>

<%
    //-- [tag:SR-20220729]
    boolean bIs2022PrjMode = CommonFunc.isRunmodeTag("2022_prj_mode");
    String dpBodyClass = (bIs2022PrjMode ? "renew" : "");
    if (bodyClass != null && !bodyClass.isEmpty()) {
        dpBodyClass += " " + bodyClass;
    }
    jspContext.setAttribute("dp_bodyClass", dpBodyClass);
    jspContext.setAttribute("bIs2022PrjMode", bIs2022PrjMode);

    // Default type handling
    if (type == null || type.isEmpty()) {
        type = "default";
    }
    jspContext.setAttribute("layoutType", type);
%>

<c:choose>
    <c:when test="${layoutType eq 'empty'}">
        <c:if test="${head != null}"><jsp:invoke fragment="head" /></c:if>
        <jsp:doBody />
    </c:when>
    <c:otherwise>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>${not empty title ? title : 'KT Open API'}</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <meta name="description" content="Visual API Designer for OpenAPI (swagger) specification. Design beautiful, functional APIs with zero coding, using OpenAPI" />
    <meta property="og:description" content="Visual API Designer for OpenAPI (swagger) specification. Design beautiful, functional APIs with zero coding, using OpenAPI" />

    <link rel="icon" type="image/png" href="<c:url value='/resources/images/common/icon/icon_32.png' />" >
    
    <%-- [고도화] 각 레이아웃 타입별 전용 CSS 분기 (Legacy Tiles 기반) --%>
    <c:choose>
        <c:when test="${layoutType eq 'apiReg'}">
            <link href="<c:url value='/resources/css/registration.css' />" rel="stylesheet" media="screen">
        </c:when>
        <c:otherwise>
            <link href="<c:url value='/resources/css/style.css' />" rel="stylesheet" media="screen">
        </c:otherwise>
    </c:choose>

    <c:if test="${bIs2022PrjMode}">
        <link href="<c:url value='/resources/css/jquery.mCustomScrollbar.css' />" rel="stylesheet" media="screen">
        <c:if test="${layoutType eq 'apiReg'}">
            <link href="<c:url value='/resources/css/swiper.min.css' />" rel="stylesheet" media="screen">
        </c:if>
    </c:if>

    <!-- Include the plug-in -->
    <script src="<c:url value='/resources/js/pub/jquery-2.1.4.min.js' />"></script>
    <script src="<c:url value='/resources/js/pub/jquery.fullPage.js' />"></script>
    <script src="<c:url value='/resources/js/pub/jquery.bxslider.js' />"></script>
    <script src="<c:url value='/resources/js/pub/jquery.als-1.1.min.js' />"></script>
    <script src="<c:url value='/resources/js/pub/jquery-ui.js' />"></script>
    
    <c:if test="${bIs2022PrjMode}">
        <script src="<c:url value='/resources/js/pub/jquery.mCustomScrollbar.js' />"></script>
        <c:if test="${layoutType eq 'apiReg'}">
            <script src="<c:url value='/resources/js/pub/swiper.min.js' />"></script>
        </c:if>
    </c:if>

    <script src="<c:url value='/resources/js/pub/ui.js' />"></script>
    <script src="<c:url value='/resources/js/pub/jquery.easing.1.3.js' />"></script>

    <%-- 특정 모듈에서 불필요한 스크립트 제외 (Legacy 대응) --%>
    <c:if test="${layoutType ne 'adptran' and layoutType ne 'beast'}">
        <script src="<c:url value='/resources/js/jquery.number.min.js' />"></script>
        <script src="<c:url value='/resources/js/jquery.form.min.js' />"></script>
    </c:if>

    <script src="<c:url value='/resources/js/common/common.js' />"></script>
    <script src="<c:url value='/resources/js/common/paging.js' />"></script>
    
    <c:if test="${layoutType ne 'adptran' and layoutType ne 'beast'}">
        <script src="<c:url value='/resources/js/jquery-validator/jquery.validate.js' />"></script>
        <c:if test="${layoutType ne 'apiReg'}">
            <script src="<c:url value='/resources/js/jquery-validator/validate.fn.js' />"></script>
        </c:if>
    </c:if>
    
    <script src="<c:url value='/resources/js/jquery.blockUI.js' />"></script>

    <%-- API 관리 전용 라이브러리 (apiReg, apiInfo) --%>
    <c:if test="${layoutType eq 'apiReg' or layoutType eq 'apiInfo'}">
        <script src="<c:url value='/resources/js/api_reg/swagger-parser.min.js' />"></script>
        <script src="<c:url value='/resources/js/api_reg/apiInfoMenuReg.js' />"></script>
    </c:if>

    <script src="<c:url value='/resources/adptran/js/ksmutil.js' />"></script>

    <c:if test="${bIs2022PrjMode}">
        <!--[if lt IE 9]>
        <script src="<c:url value='/resources/js/pub/html5.js' />"></script>
        <script src="<c:url value='/resources/js/pub/respond.min.js' />"></script>
        <![endif]-->
        <!--[if lt IE 9]>
        <script src="<c:url value='/resources/js/pub/css3-mediaqueries.js' />"></script>
        <![endif]-->
        <!--[if IE]>
        <script type="text/javascript">
          var console = { log: function() {} };
        </script>
        <![endif]-->
    </c:if>

    <script type="text/javascript">
        var c_url = '<c:out value="${pageContext.request.contextPath}" />';
        c_url = (c_url.length < 2)? '/':c_url+'/';
        
        function fnOpenLayer(btnHtm, titleMsg , msg) {
            $('#popupConfirm').find('.lPop_bottom.brd_tp').html(btnHtm);
            $('#popupConfirm').parent().find('div').eq(0).children('span').text(titleMsg);
            $('#popupConfirm').find('#alertTxt').html(msg);
            $('#popupConfirm .btn_confirm, #popupConfirm .btn_popup_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); });
            $('#popupConfirm .cid_btn_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); });
            $('#popupConfirm').dialog('open');
        }
    
        function fnLogin() {
            location.href="<c:url value='/login/loginForm.do'/>";
        }
        function mypageGo() {
            location.href="<c:url value='/mypage/mypageInfo.do'/>";
        }

        <c:if test="${layoutType eq 'apiInfo'}">
        $(document).ready(function(){
            $('.search_bar > div > input[type="text"]').keypress(function (e) {
                if (e.which == 13){
                    fnApiSearch(1,this);
                }
            });
        });
        </c:if>
    </script>
    <c:if test="${head != null}"><jsp:invoke fragment="head" /></c:if>
</head>
<body class="${dp_bodyClass}" tag="${layoutType}Layout">
    <c:if test="${layoutType eq 'apiReg'}">
        <div class="lod_icon" style="display:none;"><img src="<c:url value='/resources/images/common/icon/spinningwheel.gif' />"></div>
        <div class="dim_layer"></div>
    </c:if>

    <div class="wrap"> 
        <div id="accessibility">
            <ul><li><a accesskey="1" href="#content">Go content</a></li></ul>
        </div>
        
        <%-- Header (top.jsp) — error 타입은 헤더 제외 (마이그레이션 이전과 동일) --%>
        <c:if test="${layoutType ne 'error'}">
            <jsp:include page="/WEB-INF/jsp/include/top.jsp" />
        </c:if>
        
        <%-- Body Content --%>
        <jsp:doBody />
        
        <%-- Footer 노출 제어 (Legacy Tiles 기반 복원) --%>
        <%-- main, apiInfo, error 타입은 Footer를 포함하지 않음 --%>
        <c:choose>
            <c:when test="${layoutType eq 'main' or layoutType eq 'apiInfo' or layoutType eq 'error'}">
                <%-- Footer 미노출 --%>
            </c:when>
            <c:otherwise>
                <jsp:include page="/WEB-INF/jsp/include/footer.jsp" />
            </c:otherwise>
        </c:choose>
    </div>

    <!-- #popupConfirm { -->
    <div id="popupConfirm" class="pop_alert_top" title="${layoutType eq 'apiReg' ? 'API 등록' : '알림'}" style="display:none;">
        <div class="popup_content">
            <div class="alert_txt mb15" style="padding:5px; max-height:450px;overflow-y:auto;"><div id="alertTxt"></div></div>
            <div class="lPop_bottom brd_tp">
                <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm cid_btn_close">확인</button>
                <button type="button" title="취소" class="btn btn_sml3 btn_popup_close cid_btn_close">취소</button>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        $(document).ready(function() {
            $('#popupConfirm').dialog({ autoOpen: false, dialogClass: 'pop_alert_wrap', modal: true, resizable: false });
            $('#popupConfirm .cid_btn_close').on('click', function() {
                $('#popupConfirm').dialog('close');
            });
        });
    </script>
    <c:if test="${script != null}"><jsp:invoke fragment="script" /></c:if>
</body>
</html>
    </c:otherwise>
</c:choose>
