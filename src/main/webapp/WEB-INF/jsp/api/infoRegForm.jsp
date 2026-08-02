<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<t:layout type="apiReg">
<%@ page import="com.kt.openapi.web.util.CommonFunc"%>
<%-- //-- [tag:PRJ-20220901] --%>
<c:set var="bIsBstgwMode" value="<%= com.kt.openapi.web.util.CommonFunc.isRunmodeTag(\"bstgw_mode\") %>" />

<!-- 
   OPEN API version 1.0
  
    Copyright ⓒ 2017 kt corp. All rights reserved.
    
    This is a proprietary software of kt corp, and you may not use this file except in 
    compliance with license agreement with kt corp. Any redistribution or use of this 
    software, with or without modification shall be strictly prohibited without prior written 
    approval of kt corp, and the copyright notice above does not evidence any actual or 
  intended publication of such software. 
-->
<%-- //-- [tag:job-20200420][chg][for share head] --%>
<%@ include file="/WEB-INF/jsp/api/regFormShareHead.jsp" %>

<%-- //-- 아스날 내보내기 팝업 --%>
<%@ include file="/WEB-INF/jsp/api/arsenal/popExportToArsenal.jsp" %>


<!-- apiInfo 등록 관련 스크립트 -->
<script src="<c:url value="/resources/js/api_reg/apiInfoReg.js" />"></script>

<!-- apiInfo yaml파일 파싱 관련 스크립트 -->
<script src="<c:url value="/resources/js/api_reg/apiInfoYamlPs.js" />"></script>

<script>
  var g_data = Object.assign((g_data||{}), { 'default_ctgryNm': 'v1.0' });  //-- 신규시기본 API그룹명

  $(document).ready(function(){
    $("input[name='host']").val(editorDefultHost);
    // div_draging 사용시에 드래그앤 드랍 사용 가능
    $(".div_draging").sortable({
        handle:".handler_bar",
        update: function(event,ui){
        }
    }).disableSelection();
    // yaml 데이터 셋팅
    if(yamlOb != undefined){
      yamlParser($("#yamlSbst").val());
    }
    <%-- //-- [tag:job-20200420][add] --%>
    fn_ui_systemId(g_sysId);  //-- systemId별 ui설정
    
 
    // 권한그룹 선택 이베튼
    $("#authId").change(function() {
    	var selectText = $("#authId option:selected").text();
    	
    	selectText = selectText.replace(" 개발자 그룹","");
    	
    	$("#projectNamespace").val(selectText);
    	//alert(selectText);
    });
  });

  // 공통 보안 스키마 사용 안함클릭시 전체 선택해제
  function noGlobalSchema(data){
    if($(data).prop("checked")){
      $("input[name='golbalsecurity']").prop("checked", false);
    }
  }
  //공통 보안 스키마 사용하는것 클릭시에 no checkbox 선택해제
  function onGlobalSchema(data){
    if($(data).prop("checked")){
      $("input[name='noGlobalSchema']").prop("checked", false);
    }
  }
  
  
  //-- API기본정보
  function apiInfoSave(){
    //-- 기본정보{저장}
    if (fn_check_regform_action('infoRegForm:apiInfoSave') == false) {
      return false;
    }
	
	
	if (!hasXSSAndMove(['infoTitle','description','version','host','basePath','apiVeriBaseurl'])) {
	    return false;
	}
	

    // 호스트 파일 다시 담아준다 
    $("input[name='host']").trigger('change');
  
    /**** 데이터 검사 시작******///
    
    //--[tag:adpt][add]
    errCountReset();

    // 에러 내용 삭제
    $(".err_tooltip").find("dd").remove();
    $('.err_count').find('em').text(errorNum);

    dataValidation();
    
    // 에러 내용 추가
    errTextAppend();
  
  
    if(errorNum > 0){
      err_on();
      var offset = $("#container").offset();
          $('html, body').animate({scrollTop : offset.top}, 400);
      return false;
    } else {
      $('.err_tooltip').css("display", "none");
      $('.err_count').css("display", "none");
    }
    /**** 데이터 검사 끝 ******///
    
    /** api Type 저장 시작 ==========>   ***/
    yamlSaveOb['x-apitype'] = $("input[type=radio][name=apiType]:checked").val();
    /** api Type 저장 종료 ==========>   ***/
    /** 보안 스키마 저장 시작 ==========>   ***/
    // 보안 스키마 탭이 있을경우 for문 돌면서 저장
    if(tabNum > 0){
      yamlSaveOb.securityDefinitions = {};
      for (var i = 1; i < tabNum+1; i++) {
        var tabForm = $("#tabForm" + i)[0]; // 보안 스키마 탭
        var schemaName = $(tabForm).find("input[name='name']").val(); //해당 보안 스키마 이름
        yamlSaveOb.securityDefinitions[schemaName]        = {};
              
        // 보안 타입이 basic 일 경우
        if($(tabForm).find("select[name='type']").val() == "basic"){
          yamlSaveOb.securityDefinitions[schemaName].type      = $(tabForm).find("select[name='type']").val();
          yamlSaveOb.securityDefinitions[schemaName].description = $(tabForm).find("textarea[name='account']").val();
        }
        // 보안 타입이 oauth2 일 경우
        else if($(tabForm).find("select[name='type']").val() == "oauth2") {
          var authCd = $(tabForm).find("select[name='authCd']").val(); //해당 auth 코드 
          yamlSaveOb.securityDefinitions[schemaName].type     = $(tabForm).find("select[name='type']").val();
          yamlSaveOb.securityDefinitions[schemaName].description  = $(tabForm).find("textarea[name='account']").val();
          yamlSaveOb.securityDefinitions[schemaName].flow     = authCd;
          
          // authCd 타입에 따른 authorizationUri, access tokenUri 정의
          if(authCd == 'implicit'){
            yamlSaveOb.securityDefinitions[schemaName].authorizationUrl = $(tabForm).find("input[name='authUri']").val();
          } else if(authCd == 'accessCode'){
            yamlSaveOb.securityDefinitions[schemaName].authorizationUrl = $(tabForm).find("input[name='authUri']").val();
            yamlSaveOb.securityDefinitions[schemaName].tokenUrl     = $(tabForm).find("input[name='accessUri']").val();
          } else {
            yamlSaveOb.securityDefinitions[schemaName].tokenUrl     = $(tabForm).find("input[name='accessUri']").val();
          }
          // 범위 저장 
          if($($(tabForm).find(".scopeTr")[0]).find("input[name='scopeName']").val() != ""){
            yamlSaveOb.securityDefinitions[schemaName].scopes = {};
            var scopesOb = new Object();
            for(var k=0;k+1 < $(tabForm).find(".scopeTr").length;k++){
              if($($(tabForm).find(".scopeTr")[k]).find("input[name='scopeName']").val() != ""){
                scopesOb[$($(tabForm).find(".scopeTr")[k]).find("input[name='scopeName']").val()] = $($(tabForm).find(".scopeTr")[k]).find("input[name='scopeAccount']").val();             
              }
            }
            yamlSaveOb.securityDefinitions[schemaName].scopes = scopesOb;
          }
        } 
        // 보안 타입이 apiKey 일 경우
        else if($(tabForm).find("select[name='type']").val() == "apiKey") {
          yamlSaveOb.securityDefinitions[schemaName].type     = $(tabForm).find("select[name='type']").val();
          yamlSaveOb.securityDefinitions[schemaName].name     = schemaName;
          yamlSaveOb.securityDefinitions[schemaName].description  = $(tabForm).find("textarea[name='account']").val();
          yamlSaveOb.securityDefinitions[schemaName]['in']      = $(tabForm).find("select[name='keyIn']").val();
        }
      }
    } else {
      if(!jQuery.isEmptyObject(yamlSaveOb.securityDefinitions)){
        delete yamlSaveOb.securityDefinitions;
      }
    }
    /** 보안 스키마 저장 끝 ==========>   ***/
    /** 글로벌 보안 스키마 저장 시작     ==========>   ***/
    if($("#globalSecurity input[type='checkbox']:checked").length > 0){
      var securityUse = false;
      var securityArray = new Array;
      var security = new Object;
      var arryList = {};
      
      for(var i=0;i < $("#globalSecurity input[type='checkbox']:checked").length;i++){
        var checkVal = $("#globalSecurity input[type='checkbox']:checked")[i]; //선택 된 값
        var array = new Array; //초기화
        
        if($($("#globalSecurity input[type='checkbox']:checked")[0]).val() != "no"){
          securityUse = true;
          if($(checkVal).parent().parent().find("li").length > 0){
            for(var k=0;k < $(checkVal).parent().parent().find("li").length; k++){
              array.push($($(checkVal).parent().parent().find("li")[k]).children("span").text());           
            }
          }
          arryList = {};
          arryList[$(checkVal).val()] = array;
          securityArray.push(arryList);
        }
      }
      if(securityUse == true){
        yamlSaveOb.security = securityArray;
      } else {
        delete yamlSaveOb['security']; // json 메소드 삭제 
      }
    }
    /** 글로벌 보안 스키마 저장 끝       ==========>   ***/
    /** 기본 API그룹 저장 시작*/
    if(yamlSaveOb['x-category'] == undefined){
      yamlSaveOb['x-category']     = new Object();
      yamlSaveOb['x-category'][g_data['default_ctgryNm']] = new Object();
      
      yamlSaveOb['tags'] = [{'name': g_data['default_ctgryNm']}];
    }
    /** 기본 API그룹 저장 끝*/
    /** 오류 제거를 위한 임시 paths 저장 시작    ==========>   ***/
    /*
    if(yamlSaveOb.paths == undefined){
      yamlSaveOb.paths = {};
      yamlSaveOb.paths['/'] = {};
      yamlSaveOb.paths['/']['get'] = {};
      yamlSaveOb.paths['/']['get']['responses'] = {};
      yamlSaveOb.paths['/']['get']['responses']['200'] = {};
      yamlSaveOb.paths['/']['get']['responses']['200']['description'] = {};
      yamlSaveOb.paths['/']['get']['responses']['200']['description'] = 'TEST OK';
    } 
    */
    /** 오류 제거를 위한 임시 paths 저장 끝    ==========>   ***/
    
    //--[tag:adpt][add]
    //-- paths 정리 {
    if (typeof(yamlSaveOb['paths']) == 'undefined') {
      yamlSaveOb['paths'] = {};
    }
    else {
      for (key in yamlSaveOb['paths']) {
        if (Object.keys(yamlSaveOb['paths'][key]).length == 0) {  //-- 정보가 없는 path는 삭제
          delete yamlSaveOb['paths'][key];
        }
      }
    }
    //-- paths 정리 }
  
    // console.log("object", yamlSaveOb);
    
    // Object를 yaml형식의 문자열로 파싱
    var yamlStr = YAML.stringify(yamlSaveOb);
    
    // console.log("string", yamlStr);
    
    // Namespace DB 저장으로 변경됨에 따라 파라메터 추가, 아스날일 경우에만 전달 -- CYD
    var projectNamespace = ($("#systemId").val() == con_SYSTEMID_ARSENAL) ? $("#projectNamespace").val() : "";
    
    if(($("#systemId").val() == con_SYSTEMID_ARSENAL) && !fnGetNSInfoFromGitlab(projectNamespace)) return;
	    
    /** 저장을 위해 param에 담은뒤 ajax 호출   ==========>   ***/
    var apiSpcNo = $('#pApiSpcNo').val();
    var param = {
      apiSpcNo: apiSpcNo,        // '' 이면 등록, 있으면 수정
      yamlStr: yamlStr,              // yaml 데이터 : 필수
      rfrnWsdlUrl: $('#rfrnWsdlUrl').val(),    // 참고WSDL_URL
      rfrnTmpltNo: $('#rfrnTmpltNo').val(),    // 참고템플릿 번호
      rfrnApiSpcNo: $('#rfrnApiSpcNo').val(),   // 참고API명세번호
      autId: $('select[name="authId"]').val(),   // 권한 ID : 필수
      sysId: $('select[name="systemId"]').val(),  // 시스템 ID : 필수
      //-- [tag:adpt][add] {
      apiClass : $('select[name="apiClass"]').val(), // API구분 : 필수
      //-- [tag:adpt][add] }
      //-- [tag:PRJ-20220901]
      bstgwYn: $('input[name="bstgwYn"]:checked').val(),  // BEAST G/W 사용여부
      apiVeriBaseurl: $('input[name="apiVeriBaseurl"]').val(),  // API검증용 BASEURL
      projectNamespace: projectNamespace,  // 네임스페이스명
    };
    if ($is_empty(apiSpcNo)) {  //-- case-신규
      param['ctgryNm'] = g_data['default_ctgryNm'];
    }
    //--console.log(param);
    $.ajax({
      url    : '<c:url value="/api/reg/savApiRegBasicAjax.do"/>', 
      type   : 'POST',
      data   : param,
      cache  : false,
      async  : false,
      success: function(data){
		var alert_option = {};
		if("1" == data.returnCode){
			if (data.info.successStr == 'ins') {
			          //-- [ref] data.info = {apiSpcNo, yamlStr, successStr, filePath, apiCtgryNo, ctgryNm}
			          //레이어 메세지 적용
			          alert_message('<spring:message code="api.req.save.msg" />', '기본정보');
			          $('#pApiSpcNo').val(data.info.apiSpcNo);
			          <%-- //-- [tag:job-20200420][add][info에 대한 전역변수 재설정][regFormShareHead.jsp] --%>
			          <%-- //-- [cmt][not_returned]
			            /*--
			            sttusCd = data.info.regSttusCd; sttusCdNm = data.info.regSttusCdNm;
			            g_sysId = data.info.sysId;
			            fn_ui_systemId(g_sysId);  //-- systemId별 ui설정
			            */
			          --%>
			          g_isAuthYn = data.isAuthYn;
			          //--console.log('AuthYn:' + data.isAuthYn);
			          yamlStr = data.info.yamlStr;
			        }
			        else {
			          //레이어 메세지 적용
			          alert_message('<spring:message code="api.req.update.msg" />', '기본정보');
			        }
			        // yaml값 세팅
			        $('#yamlSbst').val(yamlStr);
			        
			        XLeftMenuSet(yamlSaveOb['x-category']);
			        isChange = false; // 페이지 이동 체크 여부 
		}else{
			alert_message('<, >, ", $ 등 사용할 수 없는 특수문자 <br>또는 스크립트 패턴이 포함되어 있습니다.', 'API', alert_option);
		}
				
        
      },
      error:function(request,status,error){
        err_message(status, error);
      }
    });
  }
  function sysIdCk(data){
    var authHtml = "";
    var authId   = "";
    <c:forEach var="list" items="${ssUserVo.authList}" varStatus="status">
      authId = "${list.sysId}"
      if(authId == data.value){
        authHtml = authHtml + '<option value="${list.autId}" >${list.autNm}</option>';
      }
    </c:forEach>
    if(authHtml == ""){
      authHtml = authHtml + '<option value="" >선택</option>';
    }
    $("#authId").html(authHtml);
    
    <%-- //-- [tag:job-20200420][add] --%>
    fn_ui_systemId(data.value); //-- systemId별 ui설정
  }
  
  <%-- //-- [tag:job-20200420][add] --%>
  function fn_ui_systemId(systemId) {
    var b_is_spc_loaded = (($('#pApiSpcNo').val()||'').length > 0);
    var b_is_ARSENAL = (systemId == con_SYSTEMID_ARSENAL);
    
    var jq_sel = $('#systemId');
    jq_sel.val(systemId);

    //--신규가 아니고 arsenal이면 disable
    jq_sel.prop('disabled', (b_is_ARSENAL && b_is_spc_loaded));

    if (true == b_is_ARSENAL) {
      // 공통보안스키마/보안스키마 초기화
      init_securityDefinitions();

      //-- ARSENAL default value {
      //-- Public: APIGUB1010, Private: APIGUB1020, Internal: APIGUB1030
      $('#apiClass').val('APIGUB1030');
      $('#host').val(editorDefultHost).trigger('change');
      $('#basePath').val('/').trigger('change');
      $('#apiTypeREST').prop("checked", true);
      //-- ARSENAL default value }
    }

    fn_ui_set_share_layout({'systemId': systemId}); //-- 상황별공통영역 ui설정
  }
  
  /* 프로젝트 네임스페이스 정보 가져오기 함수
   * Gitlab서버에서 인수 네임스페이가 존재유무를 확인함
   * Program By CYD - 2020.09.02
   */
  function fnGetNSInfoFromGitlab(a_szNamespace) {
    // 작업을 위한 임시 테스트용
    var szGitlabHost = '<c:url value="/api/reg/existsNSAtGitlabAjax.do"/>';
    var szReturn 	 = true;
    
    var param = {
    	projectNamespace: a_szNamespace
      };
      
    $.ajax({
      url    : szGitlabHost,
      type   : 'POST',
      data   : param,
      async  : false,
      cache  : false,
      crossDomain: true,
      dataType:'json',
      success: function(data){
        if(data != null) {
          //szProjectId = data.id;
          if(data.returncode == "0") {
        	  alert(data.errordescription);
        	  $("#projectNamespace").focus();
        	  szReturn = false;
          }
	        //--@@console.log(data);
        }
      },
      error:function(request,status,error){
	      //--@@console.log(status + ":" + error);
        alert("등록 실패:" + error);
        szReturn = false;
      }
    });

    return szReturn;
  }
</script>

<form method="POST" action="" name="apiInfoForm" id="apiInfoForm" class="tempForm">
  <input type="hidden" id="pApiSpcNo" name="apiSpcNo" value="${fn:escapeXml(param.apiSpcNo)}" />
  <input type="hidden" id="pApiNo" name="apiNo" value="${fn:escapeXml(param.apiNo)}" />
  <input type="hidden" id="pApiCtgryNo" name="apiCtgryNo" value="${fn:escapeXml(param.apiCtgryNo)}" />
  <input type="hidden" id="pApiCtgryNm" name="apiCtgryNm" value="${fn:escapeXml(param.apiCtgryNm)}" />
  <input type="hidden" id="pApiDataTypeNm" name="apiDataTypeNm" value="${fn:escapeXml(param.apiDataTypeNm)}" />
  <input type="hidden" id="pApiPath" name="apiPath" value="${fn:escapeXml(param.apiPath)}" />
  <input type="hidden" id="pApiMethod" name="apiMethod" value="${fn:escapeXml(param.apiMethod)}" />
  <input type="hidden" id="pApiCopyYn" name="apiCopyYn" value="" />

  <input type="hidden" id="rfrnWsdlUrl" name="rfrnWsdlUrl" value="${fn:escapeXml(param.rfrnWsdlUrl)}" />
  <input type="hidden" id="rfrnTmpltNo" name="rfrnTmpltNo" value="${fn:escapeXml(param.rfrnTmpltNo)}" />
  <input type="hidden" id="rfrnApiSpcNo" name="rfrnApiSpcNo" value="${fn:escapeXml(param.rfrnApiSpcNo)}" />
</form><!-- #apiInfoForm -->

<form method="POST" action="<c:url value='/api/reg/mvApiInfoReg.do' />" name="apiImportForm" id="apiImportForm" class="tempForm">
  <textarea id="importYamlSbst" name="yamlSbst" class="tempTextarea"></textarea>
  <input type="hidden" id="importYn" name="importYn" value="" />
</form><!-- #apiImportForm -->

<div id="container">
  <div class="contents">
    <div class="conBox">
      <div id="content" class="api_content">
        <!-- regist_wrap -->
        <div class="regist_wrap">
          <div class="regi_bar">
            <%-- //-- [tag:job-20200420][chg][for share regi_bar layout] --%>
            <%@ include file="/WEB-INF/jsp/api/regFormShareRegiBar.jsp" %>
          </div><!-- .regi_bar -->

          <!-- regist_layout -->
          <div class="regist_layout">
            <div class="api_left">
              <%-- //-- [tag:job-20200420][chg][for share left layout] --%>
              <%@ include file="/WEB-INF/jsp/api/regFormShareLeft.jsp" %>
            </div><!-- .api_left -->
            <!--// 생성 버튼 클릭시 나오는 퀵메뉴 //-->
            <ol class="quickmenu"></ol><!-- .quickmenu -->
            <!-- api_right -->
            <div class="api_right">
              <h5 class="rTitleOneDep">기본정보 <a href="javascript:void(0)" title="API 등록하는 방법보기" class="rtit_btn" onclick="showApiMV(this, '.mv-wrap');return false;">API 등록하는 방법보기</a></h5>
              <div class="btn_RT">
                <button type="button" title="취소" class="btn btn_sml" onClick="history.back()"><span>취소</span></button>
                <button type="button" title="저장" class="btn btn_sml btn_black" onclick="apiInfoSave();"><span>저장</span></button>
              </div>
              <div class="rightConBoxing">
                <!-- accordian active type -->
                <ul class="acco_opened">
                  <!-- 기본정보 -->
                  <li>
                    <article class="tooltip"></article>
                    <dl class="tooltiptext">
                      <dt>기본정보</dt>
                      <dd>YAML에 포함된 API의 공통 기본정보를 입력하세요</dd>
                    </dl>
                    <!--// active bar //-->
                    <div><a class="active acco_act" href="javascript:void(0)" title="기본정보"><span>기본정보</span></a></div>
                    <!-- slide Content -->
                    <div class="hidden_div" style="display: block;">
                      <div class="pkg_board">
                        <!-- table start -->
                        <section>
                          <table class="table-vw">
                            <caption>table Table</caption>
                            <colgroup>
                              <col style="width: 20%;">
                              <col style="width: 80%;">
                            </colgroup>
                            <tbody>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article><!--시스템 -->서비스
                                    <dl class="tooltiptext">
                                      <dt><!-- 시스템 -->서비스</dt>
                                      <dd><!-- API의 시스템을 선택하세요. API는 선택한 시스템에 종속됩니다 -->
                                        API의 서비스를 선택하세요. API는 선택한 서비스에 종속됩니다.
                                      </dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <c:set var="scopeSysId" value="" />
                                    <select title="서비스를 선택" class="wx200" name="systemId" id="systemId" onchange="sysIdCk(this);">
                                      <option value="">선택</option>
                                      <c:forEach var="list" items="${authList}" varStatus="status">
                                        <c:if test="${fn:indexOf(scopeSysId, list.sysId) == -1}">
                                          <c:set var="scopeSysId" value="${scopeSysId}'${list.sysId}'" />
                                          <c:set var="selected" value="" />
                                          <c:if test="${list.sysId eq info.sysId}">
                                            <c:set var="selected" value="selected" />
                                          </c:if>
                                      <option value="${list.sysId}" ${selected}>${list.sysNm}</option>
                                        </c:if>
                                      </c:forEach>
                                    </select> <span class="red_txt">서비스를 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>

                              <!-- Namespace 추가 Start -->
                              <tr class="disp_none cid_arsenal_show">
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article><!-- 네임스페이스명 -->네임스페이스명
                                    <dl class="tooltiptext">
                                      <dt><!-- API 제목 -->네임스페이스명</dt>
                                      <dd>네임스페이스를 입력하세요.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input type="text" name="projectNamespace" title="네임스페이스 입력" value="${info.projectNamespace}" id="projectNamespace">
                                    <p class="red_txt">네임스페이스를 입력하세요.</p>
                                  </div>
                                </td>
                              </tr>
                              <!-- Namespace 추가 End -->

                              <tr>
                                <th scope="row">
                                  <!--// [for arsenal] API그룹/프로젝트명 선택적사용 //-->
                                  <div class="essential disp_none cid_arsenal_show">
                                    <article class="tooltip"></article>프로젝트명
                                    <dl class="tooltiptext">
                                      <dt>프로젝트명</dt>
                                      <dd>프로젝트 제목을 입력하세요</dd>
                                    </dl>
                                  </div>
                                  <div class="essential disp_none cid_arsenal_hidden">
                                    <article class="tooltip"></article><!-- 카테고리를 -->카테고리
                                    <dl class="tooltiptext">
                                      <dt><!-- 카테고리를 -->카테고리</dt>
                                      <dd>YAML에 포함된 API의 공통 제목을 입력하세요</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input type="text" name="title" dum-title="카테고리를 입력" id="infoTitle" onchange="infoSave(this)">
                                    <!--// [for arsenal] API그룹/프로젝트명 선택적사용 //-->
                                    <p class="red_txt">
                                      <span class="disp_none cid_arsenal_hidden">카테고리를 입력하세요.</span>
                                      <span class="disp_none cid_arsenal_show">프로젝트명을 입력하세요.</span>
                                    </p>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div>
                                    <article class="tooltip"></article>설명
                                    <dl class="tooltiptext">
                                      <dt>설명</dt>
                                      <dd>YAML에 포함된 API의 공통 요약정보를 입력하세요.<br>설명을 보고 API에 대한 특징을 확인 합니다.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="txtarea_wrap">
                                    <textarea title="설명 입력" name="description" id="description" onchange="infoTextAreaSave(this)" onkeyup="apiRegCheckStrLength(4000,'description')"></textarea>
                                    <span class="red_txt">설명을 입력하세요.</span>
                                  </div>
                                </td>
                              </tr>

                              <!--  [tag:adpt][add] { -->
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>API 구분
                                    <dl class="tooltiptext">
                                      <dt>API 구분</dt>
                                      <dd>Private : </dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <select title="Api구분 선택" class="wx200 cid_arsenal_disabled" name="apiClass" id="apiClass">
                                      <c:forEach var="list" items="${apiCatList}" varStatus="status">
                                        <c:set var="selected" value="" />
                                        <c:if test="${list.comnCd eq 'APIGUB1020'}">
                                          <c:set var="selected" value="selected" />
										  <option value="${list.comnCd}" ${selected}>${list.cdNm}</option>
                                        </c:if>
                                      </c:forEach>
                                    </select>
                                    <span class="red_txt">API 구분을 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <!-- [tag:adpt][add] } -->

                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>권한그룹
                                    <dl class="tooltiptext" style="display: none;">
                                      <dt>권한그룹</dt>
                                      <dd>API의 권한 그룹을 선택하세요. 권한 그룹은 본인 소속된 그룹만 노출됩니다.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <c:set var="scopeAutId" value="" />
                                    <select title="권한그룹 선택" class="wx200" name="authId" id="authId">
                                      <option value="">선택</option>
                                      <c:forEach var="list" items="${ssUserVo.authList}" varStatus="status">
                                        <c:set var="selected" value="" />
                                        <c:if test="${list.autId eq info.autId}">
                                          <c:set var="selected" value="selected" />
                                        </c:if>
                                        <c:if test="${list.sysId eq info.sysId}">
                                      <option value="${list.autId}" ${selected}>${list.autNm}</option>
                                        </c:if>
                                      </c:forEach>
                                    </select>
                                    <span class="red_txt">권한그룹을 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>버전
                                    <dl class="tooltiptext">
                                      <dt>버전</dt>
                                      <dd>API의 버전을 입력하세요. 숫자와 점만 사용하세요.<br>(예: 1.01)</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input type="text" name="version" id="version" title="버전 입력" class="wx200" onchange="infoSave(this)"> <span class="red_txt">버전을 입력하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>호스트
                                    <dl class="tooltiptext">
                                      <dt>호스트</dt>
                                      <dd>
                                        API를 제공하는 호스트 (이름 또는 IP)입니다. <br>
                                                                      호스트는 scheme이나 sub-paths는 포함하지 않습니다. <br>그리고 포트를 포함 할 수 있습니다.
                                                                      호스트가 포함되지 않은 경우 문서를 제공하는 호스트를 사용해야 합니다 (포트 포함).
                                      </dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input class="cid_arsenal_disabled" type="text" title="호스트 입력" name="host" id="host" onchange="baseInfoSave(this)"> 
                                    <span class="red_txt">호스트를 입력하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>기본 경로
                                    <dl class="tooltiptext">
                                      <dt>기본경로</dt>
                                      <dd>호스트와 관련된 API가 제공되는 기본 경로입니다. 기본경로는 슬래시 (/)로 시작해야 합니다.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input class="cid_arsenal_disabled" type="text" title="호스트 입력" name="basePath" id="basePath" onchange="baseInfoSave(this)">
                                    <p class="red_txt">기본경로는 슬래시 (/)로 시작해야 합니다.</p>
                                    <!-- <p class="red_txt">기본경로 URL은 http : // 또는 https : //로 시작해야 합니다. 예 : http://KT.com </p> -->
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>스키마
                                    <dl class="tooltiptext last-tooltip">
                                      <dt>스키마</dt>
                                      <dd>API의 전송 프로토콜. 값은 목록의 "http", "https" 이어야 합니다.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="chk_agree">
                                    <a href="javascript:void(0)">
                                      <input type="checkbox" id="schema1" name="schema" title="http" onchange="schemeSave(this)" value="http">
                                      <label for="schema1"><span></span>http</label>
                                    </a>
                                    <a href="javascript:void(0)">
                                      <input type="checkbox" id="schema2" name="schema" title="https" onchange="schemeSave(this)" value="https">
                                      <label for="schema2"><span></span>https</label>
                                    </a>
                                    <span class="red_txt">스키마를 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <%-- //-- [tag:PRJ-20220901] --%>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>테스트 BASEURL
                                    <dl class="tooltiptext">
                                      <dt>API 검증 BASEURL</dt>
                                      <dd>
                                        API 검증 호출을 위한 scheme을 포함한 도메인명을 입력 합니다.(포트정보 선택적 포함)<br>기본경로 (/)를 포함한 모든 경로 정보는 포함하지 않습니다. 
                                      </dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input class="cid_arsenal_disabled" type="text" title="API검증 BASEURL 입력" name="apiVeriBaseurl" id="apiVeriBaseurl" value="${info.apiVeriBaseurl}"> 
                                    <span class="red_txt">API검증 BASEURL 항목을 입력하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>API 타입
                                    <dl class="tooltiptext last-tooltip">
                                      <dt>API 타입</dt>
                                      <dd>API 전문 형식을 선택하세요</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="chk_agree">
                                    <a href="javascript:void(0)"><input class="cid_arsenal_disabled" type="radio" id="apiTypeSOAP" name="apiType" title="SOAP" value="SOAP"> <label for="apiTypeSOAP"><span></span>SOAP</label></a>
                                    <a href="javascript:void(0)"><input class="cid_arsenal_disabled" type="radio" id="apiTypeREST" name="apiType" title="REST" value="REST" checked><label for="apiTypeREST"><span></span>REST</label></a>
                                    <span class="red_txt">API 타입을 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>

<c:if test="${bIsBstgwMode}">
                              <%--//-- [tag:PRJ-20220901] { --%>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>BEAST G/W
                                    <dl class="tooltiptext last-tooltip">
                                      <dt>BEAST G/W</dt>
                                      <dd>BEAST G/W 사용여부를 선택하세요.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="chk_agree">
                                    <c:set var="checked_Y" value="" />
                                    <c:set var="checked_N" value="checked" />
                                    <c:if test="${info.bstgwYn == 'Y'}">
                                      <c:set var="checked_Y" value="checked" />
                                      <c:set var="checked_N" value="" />
                                    </c:if>
                                    <a href="javascript:void(0)"><input class="cid_arsenal_disabled" type="radio" id="bstgwYnY" name="bstgwYn" title="사용" value="Y"  ${checked_Y}> <label for="bstgwYnY"><span></span>사용</label></a>
                                    <a href="javascript:void(0)"><input class="cid_arsenal_disabled" type="radio" id="bstgwYnN" name="bstgwYn" title="미사용" value="N" ${checked_N}><label for="bstgwYnN"><span></span>미사용</label></a>
                                    <span class="red_txt">BEAST G/W 사용여부를 선택하세요.</span>
                                  </div>
                                </td>
                              </tr>
                              <%--//-- [tag:PRJ-20220901] } --%>
</c:if>
                            </tbody>
                          </table>
                        </section>
                        <!-- // table End -->
                      </div>
                    </div> <!-- // slide Content -->
                  </li>
                  <!-- // 기본 정보 -->

                  <!--  Content TYPE -->
                  <li class="disp_none cid_arsenal_hidden">
                    <article class="tooltip"></article> 
                    <dl class="tooltiptext">
                      <dt>Content Type</dt>
                      <dd>API가 사용할 수 있는 MIME 형식의 응답, 요청 값과 보안 스키마를 입력하세요.</dd>
                    </dl>
                    <!--// active bar //-->
                    <div><a class="acco_act" href="javascript:void(0)" title="Content TYPE"><span>Content TYPE</span></a></div>
                    <!-- slide Content -->
                    <div class="hidden_div">
                      <div class="pkg_board">
                        <!-- table start -->
                        <section>
                          <table class="table-vw">
                            <caption>Table</caption>
                            <colgroup>
                              <col style="width: 20%;">
                              <col style="width: 80%;">
                            </colgroup>
                            <tbody>
                              <tr>
                                <th scope="row">
                                  <div>
                                    <article class="tooltip"></article>요청 Content Type
                                    <dl class="tooltiptext">
                                      <dt>요청 Content Type</dt>
                                      <dd>API가 사용할 수 있는 MIME 형식의 요청 파라미터 값을 선택하세요.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="chk_agree aligned">
                                    <c:forEach var="list" items="${cntTypeList}" varStatus="status">
                                    <a href="javascript:void(0)">
                                      <input type="checkbox" id="req_con${status.count}" name="consumes" title="${list.cdNm}" onchange="consumesSave(this)" value="${list.cdNm}">
                                      <label for="req_con${status.count}"><span></span>${list.cdNm}</label>
                                    </a>
                                    </c:forEach>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div>
                                    <article class="tooltip"></article>응답 Content Type
                                    <dl class="tooltiptext last-tooltip">
                                      <dt>응답 Content Type</dt>
                                      <dd>API가 사용할 수 있는 MIME 형식의 응답 파라미터 값을 선택하세요.</dd>
                                    </dl>
                                  </div>
                                </th>
                                <td>
                                  <div class="chk_agree aligned">
                                    <c:forEach var="list" items="${cntTypeList}" varStatus="status">
                                    <a href="javascript:void(0)">
                                      <input type="checkbox" id="res_con${status.count}" name="produces" title="${list.cdNm}" onchange="producesSave(this)" value="${list.cdNm}">
                                      <label for="res_con${status.count}"><span></span>${list.cdNm}</label>
                                    </a>
                                    </c:forEach>
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </section>
                        <!-- // table End -->
                      </div>
                    </div> <!-- slide Content -->
                  </li>
                  <!--  // Content TYPE -->

                  <!--  공통 보안 스키마 -->
                  <li class="disp_none cid_arsenal_hidden">
                    <article class="tooltip"></article> 
                    <dl class="tooltiptext">
                      <dt>공통 보안 스키마</dt>
                      <dd>보안 스키마 정의에서 등록된 스키마를 선택하면 API에 공통으로 적용 됩니다.</dd>
                    </dl>
                    <!--// active bar //-->
                    <div><a class="acco_act" href="javascript:void(0)" title="공통 보안 스키마"><span>공통 보안 스키마</span></a></div>
                    <!-- slide Content -->
                    <div class="hidden_div">
                      <div class="pkg_board">
                        <!-- table start -->
                        <section>
                          <table class="table-vw">
                            <caption>Table</caption>
                            <colgroup>
                              <col style="width: 20%;">
                              <col style="width: 80%;">
                            </colgroup>
                            <tbody>
                              <tr>
                                <th scope="row"><div>타입</div></th>
                                <td>
                                  <div class="chk_agree aligned2" id="globalSecurity">
                                    <div>
                                      <a href="javascript:void(0)">
                                        <input type="checkbox" id="public_schema0" name="noGlobalSchema" title="No authentication" value="no" onclick="noGlobalSchema(this)">
                                        <label for="public_schema0"><span></span>No authentication</label>
                                      </a>
                                    </div>
                                    <!-- // OAuth1  -->
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </section>
                        <!-- // table End -->
                      </div><!-- .pkg_board -->
                    </div><!-- .hidden_div -->
                    <!-- slide Content -->
                  </li>
                  <!--  // 공통 보안 스키마 -->
                  <!--  보안 스키마 -->
                  <li class="disp_none cid_arsenal_hidden">
                    <article class="tooltip"></article> 
                    <dl class="tooltiptext">
                      <dt>보안 스키마 정의</dt>
                      <dd>API에서 사용될 수 있는 보안 스키마를 선언합니다.</dd>
                    </dl>
                    <!--// active bar //-->
                    <div><a class="acco_act" href="javascript:void(0)" title="보안 스키마"> <span>보안 스키마</span></a></div>
                    <!-- slide Content -->
                    <div class="hidden_div">
                      <div class="schema_wrap">
                        <div class="tab_list2" id="securityTab">
                          <span class="add_tab">
                            <button type="button" title="추가" class="btn btn_sml" onclick="securityTabAdd(this)"><span>추가</span></button>
                          </span>
                        </div>
                        <div class="tab_wraping"></div>
                      </div>
                    </div> <!-- slide Content -->
                  </li>
                  <!--  // 보안 스키마 -->
                </ul>
                <div class="btn_set">
                  <button type="button" title="취소" class="btn btn_sml" onClick="history.back()"><span>취소</span></button>
                  <button type="button" title="저장" class="btn btn_sml btn_black" onclick="apiInfoSave();"><span>저장</span></button>
                </div>
              </div>
            </div><!-- .api_right -->
            <!-- // api_right -->
          </div><!-- .regist_layout -->
          <!-- // regist_layout -->
        </div><!-- .regist_wrap -->
        <!-- // regist_wrap -->
      </div><!-- #content -->
    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->

<!-- security div 폼 -->
<div id="securityTabForm" style="display: none;">
  <!-- tab form -->
  <h6>Oauth 2.0</h6>
  <div>
    <div class="pkg_board">
      <!-- table start -->
      <section>
        <table class="table-oneStyle">
          <caption>table Table</caption>
          <colgroup>
            <col style="width: 13%;">
            <col style="width: 40%;">
            <col style="width: 7%;">
            <col style="width: 40%;">
          </colgroup>
          <tbody>
            <tr>
              <td class="inner-table" colspan="4">
                <table>
                  <colgroup>
                    <col style="width: 10%;">
                    <col style="width: 40%;">
                    <col style="width: 10%;">
                    <col style="width: 40%;">
                  </colgroup>
                  <tr>
                    <th><div class="essential">타입</div></th>
                    <td>
                      <div>
                        <select class="w100" onclick="schemaTypeCng(this)"
                          name="type">
                          <c:forEach var="list" items="${authTypeList}" varStatus="status">
                            <option value="${list.cdNm}">${list.cdNm}</option>
                          </c:forEach>
                        </select>
                      </div>
                    </td>
                    <th rowspan="2"><div>설명</div></th>
                    <td rowspan="2">
                      <div>
                        <textarea class="w90" style="min-height: 95px;" name="account"></textarea>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <th><div class="essential">이름</div></th>
                    <td>
                      <div>
                        <input type="text" name="name" title="이름 입력" class="w100" onchange="globalSecurityCng(this)">
                        <!-- <p class="red_txt">중복된 이름이 있습니다.</p> -->
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr class="apiKeyType" style="display: none">
              <th scope="row"><div class="essential">in</div></th>
              <td colspan="3">
                <div>
                  <select class="w100" name="keyIn">
                    <option value="query">query</option>
                    <option value="header">header</option>
                  </select>
                </div>
              </td>
            </tr>
            <tr class="outhType" style="display: none">
              <th scope="row"><div class="essential">Authorization grants</div></th>
              <td colspan="3">
                <div>
                  <select class="w100" onclick="flowChange(this);" name="authCd">
                  <c:forEach var="list" items="${authGrnList}" varStatus="status">
                    <option value="${list.cdNm}">${list.cdNm}</option>
                  </c:forEach>
                  </select>
                </div>
              </td>
            </tr>
            <tr class="outhType" style="display: none">
              <th scope="row" class="at"><div>Authorization URI</div></th>
              <td class="at authUri">
                <div>
                  <input type="text" name="authUri" title="Authorization URI 입력" class="w100">
                </div>
              </td>
              <th scope="row" class="at"><div>Access token URI</div></th>
              <td class="at accessUri">
                <div>
                  <input type="text" name="accessUri" title="Access token URI 입력" class="w100">
                </div>
              </td>
            </tr>
            <tr class="outhType scopeTr" style="display: none">
              <th scope="row" class="at"><div>범위</div></th>
              <td class="at" colspan="3">
                <div>
                  <input type="text" name="scopeName" title="범위 이름 입력" class="w25" placeholder="이름" onchange="scopeAdd(this)">
                  <input type="text" name="scopeAccount" title="범위 설명 입력" class="w50" placeholder="설명">
                  <!-- <button type="button" title="삭제" class="btn btn_garbage" ><span>삭제</span></button> -->
                  <!-- <p class="red_txt">중복된 이름이 있습니다.</p> -->
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </section>
      <!-- // table End -->
    </div><!-- .pkg_board -->
  </div>
  <!-- // tab form -->
</div><!-- #securityTabForm -->

<dl class="err_tooltip" style="display: none; top: 60px; left: 2040.97px;">
  <dt>다음과 같은 오류가 발생하였습니다.</dt>
</dl>

<!-- API그룹 추가 안내 -->
<div id="info_popup_stocked" class="pop_category" title="API그룹 추가 안내" style="display:none;">
  <!--  popup content Start  -->
  <div class="popup_content">
    <div class="content_wrap" style="max-height: none;">
      <div class="">
        <img src="<c:url value='/resources/images/common/bg/guidepop01.png'/>" alt="기본정보가 저장 되었습니다. 다음은 API그룹을 추가 하십시오. <좌측 메뉴에서 [API그룹 추가] 버튼을 클릭하여도 추가할 수 있습니다.>">
      </div>
    </div>
    <p class="etc_text">API그룹을 추가 하시겠습니까?</p>
    <div class="lPop_bottom brd_tp">
      <button type="button" title="API그룹 추가하기" class="btn btn_black btn_category" onclick="mvPage('cate')">API그룹 추가하기</button>
      <button type="button" title="닫기" class="btn  btn_popup_close">닫기</button>
    </div>
    <div class="chk_agree ar mt20">
      <a href="javascript:void(0)">
        <input type="checkbox" id="noview1" name="noview1" title="7일간 열지 않음" onclick="cookieSetInfo(this, 'infoPopDel');">
        <label for="noview1"><span></span>7일간 열지 않음</label>
      </a>
    </div>
  </div>
</div>

<!--// popup script -->
<script>
  $(document).ready(function(){
    $(".pop_category").dialog({
      autoOpen: false, width: 475, modal: true, resizable: false
    });
  
    // API그룹 추가 안내
    $(".btn_category, .btn_popup_close").click(function(event) {
      $("#info_popup_stocked").dialog('close');
      event.preventDefault();
    });
  });
</script>
<!-- popup script //-->

<%-- //-- [tag:job-20200812][chg][for share popup] --%>
<%@ include file="/WEB-INF/jsp/api/regFormSharePopup.jsp" %>
<%-- //-- [tag:adpt][add][for api clone] --%>
<%@ include file="/WEB-INF/jsp/api/popApiClone.jsp" %>
<%-- //-- [tag:adpt][add][for api search] --%>
<jsp:include page="/WEB-INF/jsp/adptran/vue_part_mount_adptranService.jsp" flush="false" />
<%-- //-- [tag:PRJ-20220901][for simple api reg] --%>
<%@ include file="/WEB-INF/jsp/api/popSimpleApiReg.jsp" %>
</t:layout>
