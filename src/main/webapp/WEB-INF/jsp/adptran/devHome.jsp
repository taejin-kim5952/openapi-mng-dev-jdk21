<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="com.kt.openapi.web.adptran.util.KsmUtil"%>
<%@ page import="com.kt.openapi.web.adptran.util.AdptranUtil"%>
<%@ page import="com.kt.openapi.web.userJoin.vo.UserJoinVO"%>
<%@ page import="java.util.Enumeration"%>

<%-- ### 실행모드식별자 --%>
<c:set var="attr_config_runmode"><spring:eval expression="@environment.getProperty('config.runmode')" /></c:set>
<%-- ### js cache-update용--%>
<c:set var="attr_config_js_version"><spring:eval expression="@environment.getProperty('config.js.version')" /></c:set>

<%-- ### API url root --%>
<c:set var="attr_config_adptran_api_url"><spring:eval expression="@environment.getProperty('config.adptran.api.url')" /></c:set>
<%-- ### API url root (개발용 ) --%>
<c:set var="attr_dev_adptran_api_url"><spring:eval expression="@environment.getProperty('dev.adptran.api.url')" /></c:set>

<%-- ### javascript console.log() 사용 --%>
<c:set var="attr_dev_use_js_console_log"><spring:eval expression="@environment.getProperty('dev.use.js.console.log')" /></c:set>

<%-- ### webpack-dev-server 사용 --%>
<c:set var="attr_dev_use_webpack_dev_server"><spring:eval expression="@environment.getProperty('dev.use.webpack.dev.server')" /></c:set>
<c:set var="attr_dev_webpack_dev_server"><spring:eval expression="@environment.getProperty('dev.webpack.dev.server')" /></c:set>

<c:set var="bIsKeycheckMode" value="${attr_keycheckmode eq 'y'}" />

<c:set var="dp_userId" value="<%= com.kt.openapi.web.adptran.util.AdptranUtil.getServiceLoginInfo(request.getSession(), \"userid\") %>" />
<c:set var="dp_userName" value="<%= com.kt.openapi.web.adptran.util.AdptranUtil.getServiceLoginInfo(request.getSession(), \"username\") %>" />

<%-- //-- dev session설정처리 { --%>
<c:set var="reqSsKey" value="${param.sskey}" />
<c:set var="reqSsVal" value="${param.ssval}" />
<c:set var="reqOverwrite" value="${param.overwrite}" />

<c:if test="${not empty reqSsKey}">
    <c:set var="bIsPermitKey" value="${reqSsKey eq 'dev.use.devhome.key' or reqSsKey eq 'dev.use.js.console.log' or reqSsKey eq 'dev.use.webpack.dev.server' or reqSsKey eq 'config.runmode' or reqSsKey eq 'dev.master.id' or reqSsKey eq 'kos.apipath.prefix'}" />
    
    <c:choose>
        <c:when test="${bIsPermitKey}">
            <c:choose>
                <c:when test="${empty reqSsVal}">
                    <c:remove var="${reqSsKey}" scope="session" />
                </c:when>
                <c:otherwise>
                    <c:set var="${reqSsKey}" value="${reqSsVal}" scope="session" />
                    <c:if test="${reqSsKey eq 'config.runmode'}">
                        <% com.kt.openapi.web.adptran.util.AdptranUtil.getInstance().setConfigRunmode((String)pageContext.getAttribute("reqSsVal")); %>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${reqSsKey eq 'dev.runmode.tag'}">
            <c:choose>
                <c:when test="${empty reqSsVal}">
                    <c:remove var="${reqSsKey}" scope="session" />
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${reqOverwrite eq 'y'}">
                            <c:set var="${reqSsKey}" value="${reqSsVal}" scope="session" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="sAttr" value="${sessionScope[reqSsKey]}" />
                            <c:if test="${not fn:contains(';'.concat(sAttr).concat(';'), ';'.concat(reqSsVal).concat(';'))}">
                                <c:set var="newVal" value="${not empty sAttr ? sAttr.concat(';').concat(reqSsVal) : reqSsVal}" />
                                <c:set var="${reqSsKey}" value="${newVal}" scope="session" />
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </c:when>
    </c:choose>
</c:if>
<%-- //-- dev session설정처리 } --%>

<c:set var="dp_httpSession">
    <c:forEach items="${sessionScope}" var="item">
        <c:choose>
            <c:when test="${item.key eq 'ssUserVo' and item.value['class'].name eq 'com.kt.openapi.web.userJoin.vo.UserJoinVO'}">
                <%-- //-- [2023:codeeyes][empty_block issue] --%>
            </c:when>
            <c:otherwise>
                <span class="txt_key">${item.key}</span>: 
                <span class="txt_value">
                    <c:choose>
                        <c:when test="${item.key eq 'dev.use.devhome.key'}">
                            <c:forEach begin="1" end="${fn:length(item.value)}">*</c:forEach>
                        </c:when>
                        <c:otherwise>${item.value}</c:otherwise>
                    </c:choose>
                </span><br>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:set>

<c:set var="dp_config_runmode" value="${not empty sessionScope['config.runmode'] ? sessionScope['config.runmode'] : attr_config_runmode}" />
<c:set var="bIsRunmodeDev" value="${dp_config_runmode eq 'dev'}" />

<c:set var="dp_devUseJsConsoleLog" value="${not empty sessionScope['dev.use.js.console.log'] ? sessionScope['dev.use.js.console.log'] : attr_dev_use_js_console_log}" />
<c:set var="dp_useConsoleObject" value="${dp_devUseJsConsoleLog eq 'y' ? 'use console log' : 'not use console log'}" />

<c:set var="dp_devUseWebpackDevServer" value="${not empty sessionScope['dev.use.webpack.dev.server'] ? sessionScope['dev.use.webpack.dev.server'] : attr_dev_use_webpack_dev_server}" />
<c:set var="dp_useWebpackDevServer" value="${dp_devUseWebpackDevServer eq 'y' ? 'use webpack-dev-server' : 'not use webpack-dev-server'}" />

<c:set var="dp_httpSessionId" value="${pageContext.session.id}" />
<c:set var="dp_dev_runmode_tag" value="${sessionScope['dev.runmode.tag']}" />
<c:set var="dp_adptran_api_url" value="${bIsRunmodeDev ? attr_dev_adptran_api_url : attr_config_adptran_api_url}" />
<c:set var="dp_dev_master_id" value="${sessionScope['dev.master.id']}" />
<c:set var="dp_kos_apipath_prefix" value="${sessionScope['kos.apipath.prefix']}" />

<t:layout type="adptran">
<jsp:attribute name="head">
<style>
  body { font-size: 12px; }
  .txt_key { color: blue; }
  .txt_value { color: red; }
  .pointer { cursor: pointer; }
  
  #id_devhome_wrap { position:absolute; top:180px;}
</style>
</jsp:attribute>

<script>
  function fn_proc_cmd(p_cmd) {
    var s_url = '';
    var homekey = '';
    if (p_cmd == 'go_devHome') {
      homekey = (prompt('input homekey', '')||'');
    }
    var jq_form = $('<form />', { id : 'id_form' });
    if (homekey.length > 0) {
      jq_form.append($('<input />', { name : 'homekey', type : 'hidden', value : homekey }));
      $('body').append(jq_form);
      $('#id_form').attr('method', 'POST').attr('action', s_url).submit();
    }
  }
  function fn_set_session(ss_key, ss_val, def_val) {
    var overwrite_yn = 'n';
    if (ss_val == '?') {
      ss_val = prompt('input ' + ss_key, (def_val||''));
      if ((ss_val||'').length == 0) { return; }
      overwrite_yn = 'y';
    }
    var jq_form = $('<form />', { id : 'id_form' });
    jq_form.append($('<input />', { name : 'sskey', type : 'hidden', value : ss_key }));
    jq_form.append($('<input />', { name : 'ssval', type : 'hidden', value : ss_val }));
    jq_form.append($('<input />', { name : 'overwrite', type : 'hidden', value : overwrite_yn }));
    $('body').append(jq_form);
    $('#id_form').attr('method', 'POST').attr('action', '').submit();
  }
</script>

<c:if test="${bIsKeycheckMode}">
  <script>
    $(document).ready(function() {
      fn_proc_cmd('go_devHome');
    });
  </script>
</c:if>

<c:if test="${not bIsKeycheckMode}">
<!--// home.jsp -->

<div id="id_devhome_wrap">

  <ul>
    <li>[tag:PRJ-20220901]</li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmSysDplyList', {'target': '_blank'});">beast 시스템 관리</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmApiDplyList', {'target': '_blank'});">beast api 관리</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmSvcDplyList', {'target': '_blank'});">beast 서비스 관리</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmApiLinkDataList', {'target': '_blank'});">beast api link data 관리</span></li>
    <li>&nbsp;</li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmSysDplyList/tb', {'target': '_blank'});">beast 시스템 관리(TB)</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmApiDplyList/tb', {'target': '_blank'});">beast api 관리(TB)</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmSvcDplyList/tb', {'target': '_blank'});">beast 서비스 관리(TB)</span></li>
    <li>## <span class="pointer txt_key" onclick="$fn_go_location('/apidev/beast/apigwmng/bstAdmApiLinkDataList/tb', {'target': '_blank'});">beast api link data 관리(TB)</span></li>
    <li>&nbsp;</li>
  </ul>

  <ul>
    <li><span class="pointer" onclick="$fn_go_location('/apidev/adptran/devQuery');">[goto devQuery]</span></li>
    <li>&nbsp;</li>
    <li><span class="pointer" onclick="fn_set_session('dev.use.devhome.key', '');">[remove dev.use.devhome.key]</span></li>
    <li>&nbsp;</li>
    <li>
      <span class="pointer" onclick="fn_set_session('dev.runmode.tag', '?', '${dp_dev_runmode_tag}');">[set dev.runmode.tag]</span>
      <span class="pointer" onclick="fn_set_session('dev.runmode.tag', '');">[remove dev.runmode.tag]</span>
      <div>
        &nbsp;&nbsp;<span class="pointer" onclick="fn_set_session('dev.runmode.tag', 'bstgw_mode');">[bstgw_mode]</span>
        &nbsp;&nbsp;<span class="pointer" onclick="fn_set_session('dev.runmode.tag', 'e.g.');">[e.g.]</span>
      </div>
    </li>
    <li>&nbsp;</li>
    <li>
      <span class="pointer" onclick="fn_set_session('dev.master.id', '?');">[set dev.master.id]</span>
      <span class="pointer" onclick="fn_set_session('dev.master.id', '');">[remove dev.master.id]</span>
    </li>
    <li>&nbsp;</li>
    <li>
      <span class="pointer" onclick="fn_set_session('kos.apipath.prefix', '?');">[set kos.apipath.prefix]</span>
      <span class="pointer" onclick="fn_set_session('kos.apipath.prefix', '');">[remove kos.apipath.prefix]</span>
    </li>
    <li>&nbsp;</li>
    <li>
      <span class="pointer" onclick="fn_set_session('dev.use.js.console.log', 'y');">[set dev.use.js.console.log=y]</span>
      <span class="pointer" onclick="fn_set_session('dev.use.js.console.log', '#y');">[set dev.use.js.console.log=#y]</span>
      <span class="pointer" onclick="fn_set_session('dev.use.js.console.log', '');">[remove dev.use.js.console.log]</span>
    </li>
    <li>
      <span class="pointer" onclick="fn_set_session('dev.use.webpack.dev.server', 'y');">[set dev.use.webpack.dev.server=y]</span>
      <span class="pointer" onclick="fn_set_session('dev.use.webpack.dev.server', '#y');">[set dev.use.webpack.dev.server=#y]</span>
      <span class="pointer" onclick="fn_set_session('dev.use.webpack.dev.server', '');">[remove dev.use.webpack.dev.server]</span>
    </li>
    <li>
      <span class="pointer" onclick="fn_set_session('config.runmode', 'dev');">[set config.runmode=dev]</span>
      <span class="pointer" onclick="fn_set_session('config.runmode', '#dev');">[set config.runmode=#dev]</span>
      <span class="pointer" onclick="fn_set_session('config.runmode', '');">[remove config.runmode]</span>
    </li>
  
    <li>&nbsp;</li>
    <li>[adptran.properties]</li>
    <li>## <span class="txt_key">dev.use.js.console.log</span>: <span class="txt_value">${attr_dev_use_js_console_log}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">dev.use.webpack.dev.server</span>: <span class="txt_value">${attr_dev_use_webpack_dev_server}</span></li>
    <li>## <span class="txt_key">dev.webpack.dev.server</span>: <span class="txt_value">${attr_dev_webpack_dev_server}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">config.runmode</span>: <span class="txt_value">${attr_config_runmode}</span></li>
    <li>## <span class="txt_key">config.js.version</span>: <span class="txt_value">${attr_config_js_version}</span></li>
  
    <li>&nbsp;</li>
    <li>[adptran using value]</li>
    <li>## <span class="txt_key">use console object</span>: <span class="txt_value">${dp_useConsoleObject}</span></li>
    <li>## <span class="txt_key">use webpack-dev-server</span>: <span class="txt_value">${dp_useWebpackDevServer}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">runmode</span>: <span class="txt_value">${dp_config_runmode}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">devrunmodetag</span>: <span class="txt_value">${dp_dev_runmode_tag}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">adptran_api_url</span>: <span class="txt_value">${dp_adptran_api_url}</span></li>
    <li>&nbsp;</li>
    <li>## <span class="txt_key">devmasterid</span>: <span class="txt_value">${dp_dev_master_id}</span></li>
    <li>## <span class="txt_key">kosapipathprefix</span>: <span class="txt_value">${dp_kos_apipath_prefix}</span></li>
    <li>## <span class="txt_key">userId</span>: <span class="txt_value">${dp_userId}</span></li>
    <li>## <span class="txt_key">userName</span>: <span class="txt_value">${dp_userName}</span></li>
  
    <li>&nbsp;</li>
    <li>[HttpSession]</li>
    <li>sessionid: ${dp_httpSessionId}</li>
    <li>${dp_httpSession}</li>
  
    <li>&nbsp;</li>
  </ul>
  <ul>
    <li>&nbsp;</li>
    <li>[ServerInfo]</li>
    <li>## <span class="txt_key">서버정보(getServerInfo)</span>: <span class="txt_value">${pageContext.servletContext.serverInfo}</span></li>
    <li>## <span class="txt_key">서블릿정보(getMajorVersion+getMinorVersion)</span>: <span class="txt_value">${pageContext.servletContext.majorVersion}.${pageContext.servletContext.minorVersion}</span></li>
    <li>## <span class="txt_key">JSP정보(getSpecificationVersion)</span>: <span class="txt_value"><%= jakarta.servlet.jsp.JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %></span></li>
  </ul>
  <br>
  - 종종 -
</div><!-- #id_devhome_wrap -->
</c:if>
<!-- home.jsp //-->
</t:layout>
