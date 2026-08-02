
<!--// regFormShareHead.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%--
    /*-- [tag:SR-20210222][cmt] --*/
    /*--
    String dp_api_no = "";
    String dp_edit_flag = "";

    String req_apiNo = KsmUtil.fnSafeStr(request.getParameter("apiNo"));
    if (req_apiNo.length() > 0) { //-- api def검색이 있는경우
      dp_edit_flag = "N";
      EgovMap map_apiDef = (EgovMap)request.getAttribute("apiDef");
      dp_api_no = ((map_apiDef != null) ? KsmUtil.fnSafeStr(map_apiDef.get("apiNo")) : "");
      if (true == dp_api_no.equals(req_apiNo)) {
        dp_edit_flag = KsmUtil.fnSafeStr(map_apiDef.get("editFlag"));  //-- 수정가능여부
      }
      dp_edit_flag = ("N".equals(dp_edit_flag) ? dp_edit_flag : "Y");
    }

    pageContext.setAttribute("dp_api_no", dp_api_no);
	  pageContext.setAttribute("dp_edit_flag", dp_edit_flag);
    --*/
--%>
  <style type="text/css">
    <%-- //-- [tag:job-20200420][add] --%>
    <%-- //--[i]display:inline-block을 무효화 하기위해 !important가 반드시 필요/ show()/hide() control이 되지 않음 --%>
    .disp_none { display: none !important; }
    .mv-wrap { display: none; }
  </style>

  <textarea id="yamlSbst" name="yamlSbst" class="tempTextarea"><c:out value='${info.yamlSbst}' escapeXml='true' /></textarea>

  <script src="<c:url value="/resources/adptran/js/ksmutil.js" />"></script>
  <script type="text/javascript">
    var importUrltoYml  = '<c:url value="/api/reg/selUrlToYamlAjax.do"/>';
    var importUrl   = '<c:url value="/api/reg/selImportApiListAjax.do"/>';
    var importUrlSelect = '<c:url value="/api/main/savApiVerAjax.do"/>';
    var importYamlSelect = '<c:url value="/api/reg/savApiYamlAjax.do"/>';
    var regUrl    = "<c:url value='/api/reg/mvApiInfoReg.do' />";
    var delCategoryUrl   = '<c:url value="/api/reg/delApiCateInfoAjax.do"/>';     // API그룹 삭제 url
    var delDatatypeUrl   = '<c:url value="/api/reg/savApiDataTypeRegAjax.do"/>';    // 데이터 타입 삭제 url
    var mvApiInfoUrl     = '<c:url value="/api/reg/mvApiInfoReg.do"/>';       // 기본정보 페이지 url
    var mvCategoryUrl    = '<c:url value="/api/reg/mvApiCateInfoReg.do"/>';     // API그룹 페이지 url
    var mvApiPathUrl     = '<c:url value="/api/reg/mvApiPathReg.do"/>';       // api 패스 등록 페이지 url
    var mvDatatypeUrl    = '<c:url value="/api/reg/mvApiDataTypeReg.do"/>';     // 데이터 타입 등록 url 
    var delApiMethodUrl  = '<c:url value="/api/reg/delApiPathAjax.do"/>';       // api 패스에 대한 매소드 삭제 url
    var delApiPathUrl    = '<c:url value="/api/reg/delApiPathAjax.do"/>';       // api 패스 삭제 삭제 url
    var delApiPathAllUrl   = '<c:url value="/api/reg/delApiAllPathAjax.do"/>';      // api 전체 패스 삭제 삭제 url
    var selApiInfoNmDupleUrl = '<c:url value="/api/reg/selApiInfoNmCheckAjax.do"/>';    // api 기본정보 이름중복검사 url
    var editorTomcatUse    = '<spring:eval expression="@environment.getProperty('editor.tomcat.use')" />'       // 에디터 톰캣 사용여부
    var editorTomcatHostUse  = '<spring:eval expression="@environment.getProperty('editor.tomcathost.url')" />'     // 에디터 톰캣 사용여부
    var editorHostUrl      = '<spring:eval expression="@environment.getProperty('editor.host.url')" />'           // 에디터 호스트 url
    var editorDefultHost   = '<spring:eval expression="@environment.getProperty('editor.defult.host')" />'      // 에디터 기본정보 디폴트 호스트
    var sttusCd              = '${info.regSttusCd}';
    var sttusCdNm            = '${info.regSttusCdNm}';
    var dataInfoOb           = '${dataTypeInfo}';
    var selCategoryNo        =  '<c:url value="/api/reg/selApiCategoryNoAjax.do"/>';      // API그룹 번호 조회

    //-- [tag:job-20200420][add]
    var g_sysId = '${info.sysId}';
    var con_SYSTEMID_ARSENAL = '<spring:eval expression="@environment.getProperty('apisystem.sysid.arsenal')" />';
    var g_is_Aasenal = (g_sysId == con_SYSTEMID_ARSENAL); 
    var g_is_PrivateApi = ('${info.apiClass}' == 'APIGUB1020'); //-- Public: APIGUB1010, Private: APIGUB1020, Internal: APIGUB1030

    
    /*
    ** 권한체크기준
    **  1. 등록한 사용자
    **  2. 수정권한이 있는 사용자(관리자 및 운영자가 부여)
    **  
    ** 위 두가지 조건을 제외한 나머지 사용자들은 수정불가
    ** Y:권한있음, N:권한없음
    **
    ** CYD - 2020.07.14
    *///////////////////////////////////////////////
    var g_isAuthYn = "${sIsAuthYn}";
    ////////////////////////////////////////////////

    <%--
    /*-- [tag:SR-20210222][chg] --*/
    /*-- [i][ajax query로 변경] --*/
    /*--
    //--[tab:job-20200714][add]
    var g_def_apiNo = '${dp_api_no}';
    var g_def_editFlag = '${dp_edit_flag}';
    --*/
    --%>
    var g_map_apiDef = {};
    <%-- //-- [tag:PRJ-20220901] --%>
    var gfn_cb_apiDef;  //-- [i]초기 gfn_query_apiDef() 호출 callback 지정용

    function gfn_get_apiDef(p_key) {
      return $sf_obj_val(g_map_apiDef, p_key);
    }
    
    <%-- //-- [tag:PRJ-20220901] --%>
    function gfn_query_apiDef(api_no, fn_cb) {
      if (false == $is_integer(api_no)) { return; }

      var param = new Object();
      param['cmd'] = 'selApiDef';
      param['apiNo'] = api_no;

      var fn_beforeSend = (function(xhr) { (('function' == typeof($.ajaxSetup()['beforeSend'])) && ($.ajaxSetup()['beforeSend'])(xhr)); });
      var fn_error = (function(request, status, error) { alert('status: ' + request.status + '\n' + 'error: ' + error); });
      var fn_success = (function(data, textStatus, request) {
        g_map_apiDef = $sf_obj_val(data, 'apiDef');
        if ('function' == typeof(fn_cb)) {
          fn_cb(data);
        }
      });
      $.ajax({
        url: '<c:url value="/api/reg/common/ajax_query.do"/>' + '?cmd=' + param['cmd'],
        type : 'POST',
        data: param, cache: false,
        async: false, //-- [i][must async:false]
        beforeSend: fn_beforeSend,
        success: fn_success, 
        error: fn_error,
      });
    }

    function _dep_gfn_query_apiDef() {
      var param_apiNo = '${fn:escapeXml(param.apiNo)}';
      if (param_apiNo.length > 0) {
        $.ajax({
          url: '<c:url value="/api/reg/selApiDefAjax.do"/>', type : 'POST', cache: false, async: false,
          data: { 'apiNo': param_apiNo },
          success: function(data) {
            g_map_apiDef = $sf_obj_val(data, 'apiDef');
          },
          error:function(request,status,error) {
            //-- [2023:codeeyes][empty_block issue]
          }
        });
      }
    }
  </script>

  <%-- //-- [tag:SR-20210222][add] --%>
  <script type="text/javascript">
    $(document).ready(function() {
      regformsh_fn_init_page();
    });
    
    function regformsh_fn_init_page() {
      var api_spc_no = '${info.apiSpcNo}';
      var api_ctgry_no = '${cate.apiCtgryNo}';
      //-- [i][secondary select]
      if (!$is_positive_number(api_spc_no)) {
        api_spc_no = $('#pApiSpcNo').val();
      }
      if (!$is_positive_number(api_ctgry_no)) {
        api_ctgry_no = $('#pApiCtgryNo').val();
      }
      var b_spc_selected = $is_positive_number(api_spc_no);
      var b_spc_ctgry_selected = $is_positive_number(api_ctgry_no);

      var b_is_append = (b_spc_selected && b_spc_ctgry_selected);

      <%-- //-- [tag:PRJ-20220901][i][gfn_simple_api_reg_popup() @popSimpleApiReg.jsp] --%>
      var jq_btn_simple_reg = $('.right_menu .cid_btn_simple_reg');
      jq_btn_simple_reg.hide();
      if ('function' == typeof(gfn_simple_api_reg_popup)) {
        if (b_is_append) {
          jq_btn_simple_reg.find('span').text('간편 API 추가');
          jq_btn_simple_reg.on('click', function(p_evt) { gfn_simple_api_reg_popup(api_spc_no, api_ctgry_no); });
        }
        else {
          jq_btn_simple_reg.find('span').text('간편 API 신규등록');
          jq_btn_simple_reg.on('click', function(p_evt) { gfn_simple_api_reg_popup(); });
        }
        jq_btn_simple_reg.show();
      }

      <%-- //--[ref] var api_no = '${apiDef.apiNo}'; //--%>
      var api_no = $('#pApiNo').val();
      var b_def_loaded = $is_integer(api_no);
      if (b_def_loaded) {
        gfn_query_apiDef(api_no, gfn_cb_apiDef);
      }
    }
    //-- function gfn_simple_api_reg_popup() { alert('coming_soon'); }
  </script>

  <script src="<c:url value="/resources/js/api_reg/apiGlobalScript.js" />"></script>
<!-- regFormShareHead.jsp //-->