<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="com.kt.openapi.web.adptran.util.KsmUtil"%>
<%@ page import="com.kt.openapi.web.adptran.util.AdptranUtil"%>

<c:set var="requestUri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:set var="dp_bundleScriptSrc" value="<%= com.kt.openapi.web.adptran.util.AdptranUtil.getBundleScriptSrc(request) %>" />

<%-- //-- adptranLayout.jsp에서 항상 include --%>
<c:set var="b_use_jquery_ui" value="false" />
<%-- //--##b_use_jquery_ui |= (requestUri.indexOf("/apidev/adptran/apistatus/e.g.url") != -1); --%>

<c:set var="b_use_jquery_custom_scrollbar" value="${fn:contains(requestUri, '/apidev/adptran/apistatus/e.g.url')}" />

<!--// vue_page_mount_apistatus.jsp -->

<c:if test="${b_use_jquery_ui}">
  <script src="<c:url value="/resources/js/pub/jquery-ui.js" />"></script>
</c:if>
<c:if test="${b_use_jquery_custom_scrollbar}">
  <link href="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.css" />" rel="stylesheet" />
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mousewheel.min.js" />"></script>
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.js" />"></script>
</c:if>

<link href="<c:url value="/resources/css/apistatus.css" />" rel="stylesheet" />
<script defer src="<c:url value="${dp_bundleScriptSrc}" />"></script>

<div id="container">
  <div class="sVisual sv_login">
    <div>
      <h2>상황판</h2>
      <p>최근에 사용한 API의 상태를 한눈에 확인하실 수 있습니다</p>
    </div>
  </div>
  <div class="contents">
    <div class="conBox">
      <div class="pg_location"><a>Go home</a> <span>></span> 상황판</div>
      <div id="content">
        <div id="vid_vue_page_mount_apistatus" v-cloak></div>
      </div><!-- #content -->
    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->

<!-- vue_page_mount_apistatus.jsp //-->
