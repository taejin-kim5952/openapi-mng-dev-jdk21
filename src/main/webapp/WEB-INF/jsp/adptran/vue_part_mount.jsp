<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="com.kt.openapi.web.adptran.util.KsmUtil"%>
<%@ page import="com.kt.openapi.web.adptran.util.AdptranUtil"%>

<c:set var="dp_bundleScriptSrc" value="<%= com.kt.openapi.web.adptran.util.AdptranUtil.getBundleScriptSrc(request) %>" />
<c:set var="param_vue_part" value="${param.param_vue_part}" />
<c:set var="dp_id_vue_part" value="vid_${param_vue_part}" />
<c:set var="dp_custom_properties" value="${fn:replace(param.param_vue_custom_properties, '&quot;', '\"')}" />
<c:set var="b_use_jquery_custom_scrollbar" value="${fn:contains(param_vue_part, 'adptranService')}" />

<!--// vue_part_mount.jsp -->
<c:if test="${not empty param_vue_part}">

<c:if test="${b_use_jquery_custom_scrollbar}">
  <link href="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.css" />" rel="stylesheet" />
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mousewheel.min.js" />"></script>
  <script src="<c:url value="/resources/adptran/js/cdn/mcustomscrollbar/jquery.mCustomScrollbar.min.js" />"></script>
</c:if>
<c:if test="${param.param_vue_part_only ne 'Y'}">
<jsp:include page="/WEB-INF/jsp/adptran/head_function.jsp" flush="false" />
<link href="<c:url value="/resources/adptran/css/adptran-common.css" />" rel="stylesheet" />
</c:if>

<script defer src="<c:url value="${dp_bundleScriptSrc}" />"></script>
<div id="${dp_id_vue_part}" ${dp_custom_properties} v-cloak></div>

</c:if>
<!-- vue_part_mount.jsp //-->
