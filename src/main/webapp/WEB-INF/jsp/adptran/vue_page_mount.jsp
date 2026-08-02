<%-- not used yet --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="com.kt.openapi.web.adptran.util.KsmUtil"%>
<%@ page import="com.kt.openapi.web.adptran.util.AdptranUtil"%>

<c:set var="requestUri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:set var="dp_bundleScriptSrc" value="<%= com.kt.openapi.web.adptran.util.AdptranUtil.getBundleScriptSrc(request) %>" />
<c:set var="b_use_jquery_ui" value="${fn:contains(requestUri, '/apidev/adptran/e.g.url')}" />
<c:set var="b_use_jquery_custom_scrollbar" value="${fn:contains(requestUri, '/apidev/adptran/devQuery')}" />
<c:set var="b_use_canvas_datagrid" value="${fn:contains(requestUri, '/apidev/adptran/devQuery')}" />
<c:set var="dp_vuePageProperties" value="${requestScope['attr_vue_page_properties']}" />

<!--// vue_page_mount.jsp -->

<c:if test="${b_use_jquery_ui}">
  <script src="<c:url value="/resources/js/pub/jquery-ui.js" />"></script>
</c:if>
<c:if test="${b_use_jquery_custom_scrollbar}">
  <link href="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.css" />" rel="stylesheet" />
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mousewheel.min.js" />"></script>
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.js" />"></script>
</c:if>
<c:if test="${b_use_canvas_datagrid}">
  <script src="<c:url value="/resources/adptran/js/canvas-datagrid.0.22.12.js" />"></script>
</c:if>
<link href="<c:url value="/resources/adptran/css/adptran-common.css" />" rel="stylesheet" />
<script defer src="<c:url value="${dp_bundleScriptSrc}" />"></script>

<div id="container">
  <div id="vid_vue_page_mount" ${dp_vuePageProperties} v-cloak></div>
</div><!-- #container -->

<!-- vue_page_mount.jsp //-->
