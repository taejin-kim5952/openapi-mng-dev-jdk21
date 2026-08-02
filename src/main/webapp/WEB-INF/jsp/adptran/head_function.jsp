<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="com.kt.openapi.web.adptran.util.KsmUtil"%>
<%@ page import="com.kt.openapi.web.adptran.util.AdptranUtil"%>

<c:set var="attr_dev_use_js_console_log"><spring:eval expression="@environment.getProperty('dev.use.js.console.log')" /></c:set>
<c:set var="attr_config_runmode"><spring:eval expression="@environment.getProperty('config.runmode')" /></c:set>
<c:set var="attr_config_adptran_api_url"><spring:eval expression="@environment.getProperty('config.adptran.api.url')" /></c:set>
<c:set var="attr_dev_adptran_api_url"><spring:eval expression="@environment.getProperty('dev.adptran.api.url')" /></c:set>

<c:set var="dp_devUseJsConsoleLog" value="${not empty sessionScope['dev.use.js.console.log'] ? sessionScope['dev.use.js.console.log'] : attr_dev_use_js_console_log}" />
<c:set var="dp_configRunmode" value="${not empty sessionScope['config.runmode'] ? sessionScope['config.runmode'] : attr_config_runmode}" />
<c:set var="dp_requestUri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:set var="dp_adptranApiUrl" value="${dp_configRunmode eq 'dev' ? attr_dev_adptran_api_url : attr_config_adptran_api_url}" />
<%-- //--##[ref] String userId = KsmAdptranUtil.getServiceLoginInfo(request.getSession(), "userid"); --%>
<%-- //--##[ref] pageContext.setAttribute("dp_userId", userId); --%>


<!--// head_function.jsp -->
<script src="<c:url value="/resources/adptran/js/ksmutil.js" />"></script>
<script>
  var console = ((typeof(window.console) == 'object') ? window.console : {log:(new Function()), debug:(new Function()), info:(new Function()), warn:(new Function()), error:(new Function()), assert:(new Function())});
  if ('${dp_devUseJsConsoleLog}' != 'y') {
    console.log = new Function();
  }
  (function() {
    var adpt_pageinfo = (JSON.parse(localStorage.getItem('adpt_pageinfo')) || {});
    adpt_pageinfo.config_runmode = '${dp_configRunmode}';
    adpt_pageinfo.request_uri = '${dp_requestUri}';
    adpt_pageinfo.adptran_api_url = '${dp_adptranApiUrl}';
    localStorage.setItem('adpt_pageinfo', JSON.stringify(adpt_pageinfo));
  })();
</script>
<!-- head_function.jsp //-->
