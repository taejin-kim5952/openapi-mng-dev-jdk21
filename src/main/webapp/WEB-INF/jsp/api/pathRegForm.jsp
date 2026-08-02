<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<c:set var="b_is_master" value="${fn:toLowerCase(sessionScope['dev.master.id']) eq 'master'}" scope="page" />

<t:layout type="apiReg">
<jsp:attribute name="head">
<%-- //-- [tag:job-20200420][chg][for share head] --%>
<%@ include file="/WEB-INF/jsp/api/regFormShareHead.jsp" %>
</jsp:attribute>

<!-- 
    OPEN API version 1.0
  
    Copyright ⓒ 2017 kt corp. All rights reserved.
    
    This is a proprietary software of kt corp, and you may not use this file except in 
    compliance with license agreement with kt corp. Any redistribution or use of this 
    software, with or without modification shall be strictly prohibited without prior written 
    approval of kt corp, and the copyright notice above does not evidence any actual or 
  intended publication of such software. 
-->

<!--// yaml parser 관련 js파일 -->
<script type="text/javascript">
  var tabNum = 0;  // 탭 번호
  var requiredNum = 0;  // required 번호
  var itemSuccess;
  var exampleOb = new Object();
  var exampleArrayStr;
  var Ayinnum = 0;
  <%--
  //--[tag:adpt][cmt][using input #pApiCopyYn]
  //--##var copyYn = "${param.apiCopyYn}";
  --%>
  <%-- //-- //-- [tag:adpt][add][for versionup] --%>
  var g_apiVer = '${fn:escapeXml(param.apiVer)}';  //-- api version input at popApiClone.jsp
  var g_apiVerNo = '${fn:escapeXml(param.apiVerNo)}';  //-- api version group no from popApiClone.jsp

  // 탭 활성화 이벤트
  function onTab(num) {
    //-- [tag:adpt][add][for bug][num 1이 없을수도 있음]
    var jq_tab_btn = $('#tab' + num);
    if (jq_tab_btn.length == 0) {  //-- num이 없을시 첫번째  버튼선택
      num = $('.tab-content').first().attr('data-tabnum');
    }

    $('.tab_list2 div').removeClass('current');
    $('.tab-content').removeClass('current');
    $('#tab'     + num).addClass('current');
    $('#tabForm' + num).addClass('current');

    // 해당 body, header 폼 활성화
    $(".responseForm").css("display","none");
    $("#headerForm"+num).css("display","");
    $("#bodyForm"+num).css("display","");
  }

  $(document).ready(function(){

    $('#apiId').off('blur keyup');
    $('#apiId').on('blur keyup', function() {
      var regexp =/[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g;
      if($("#apiId").val().match(regexp)){
        $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupConfirm").find('#alertTxt').html('API 아이디에는 한글이 들어갈 수 없습니다.');
        $("#popupConfirm").dialog("open");

        $(this).val($(this).val().replace(/[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g, ''));
        return false;
      } else if($("#apiId").val().search(/\s/) != -1) {
        $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupConfirm").find('#alertTxt').html('API 아이디에는 공백이 들어갈 수 없습니다.');
        $("#popupConfirm").dialog("open");
        $("#apiId").val($("#apiId").val().replace(/ /g, ''));
        return false;
      }
    });

    //-- @ apiGlobalScript.js
    dataTypeSet(); //datatype 세팅
    securityReadySet();

    // 좌측상단 타이틀 세팅
    $(".default_info").children("p").text(yamlOb.info.title);
    $(".default_info").children("p").attr("title",yamlOb.info.title);

    var param_apiPath = '${fn:escapeXml(param.apiPath)}';
    var param_apiMethod = '${fn:escapeXml(param.apiMethod)}';
    var param_apiCopyYn = "${fn:escapeXml(param.apiCopyYn)}"; // 'Y':copy, 'A':edit other method, 'V': version upgrade

    //--[tag:adpt][add]
    //--@@if ($('#pApiPath').val() != '') {
    $('#apiPath').val(param_apiPath);  // path 세팅
    //--@@}
    $('#methodBox').val(fn_get_method_comn_cd(param_apiMethod));  // method 세팅

    var b_is_load = false;
    var b_is_update = false;
    var b_is_meta_udate = false;

    var s_apiedit_mode = '신규';
    //--##if (($('#pApiPath').val() != '') && ($('#pApiMethod').val() != '')) {
    if ((param_apiPath.length > 0) && (param_apiMethod.length > 0)) {
      if ($('#pApiNo').val() != '') {
        apiPathInfoSet(); // 정보세팅
        b_is_load = true;
        b_is_update = true;
        s_apiedit_mode = '수정';
        if ('APIREG1030' == sttusCd) {  //-- 등록완료
          b_is_meta_udate = true;
          s_apiedit_mode = 'meta수정';
        }
      }
      else if ('Y' == param_apiCopyYn) { //-- copy
        apiPathInfoSet(); // 정보세팅
        b_is_load = true;
        $('#pApiNo').val('');
        s_apiedit_mode = '복사';
      }
      else if ('V' == param_apiCopyYn) { //-- verup
        apiPathInfoSet(); // 정보세팅
        b_is_load = true;
        $('#pApiNo').val('');
        s_apiedit_mode = '버전업';
        fn_ui_set_versionup(param_apiPath, g_apiVer);  //-- set ui versionup@popApiClone.jsp
      }
      else if (param_apiCopyYn == 'A') { //-- other method
        s_apiedit_mode = '신규method';
      }
    }
    $('.cid_apiedit_mode').attr('title', s_apiedit_mode);
    
    if (b_is_meta_udate == true) {
      //-- disable input except meta data
      var jq_root = $('.rightConBoxing').find('ul.acco_opened').eq(0);
      var jq_ret = jq_root.find(':input').not('.cid_enable_meta_upd').prop('disabled', true);
    }

    if (b_is_update == true) {
      <%-- //-- [tag:adpt][191206][add][apiNo불일치 bug파악을 위해 추가] --%>
      var param_apiNo = '${fn:escapeXml(param.apiNo)}';
      var apidef_apiNo = '${fn:escapeXml(apiDef.apiNo)}';
      var s_msg = '';
      if (apidef_apiNo.length == 0) {
        s_msg = '해당 API정보가 없습니다. - [apiNo: ' + param_apiNo + ']';
      }
      if (s_msg.length > 0) {
        alert_message(s_msg, 'API');
      }
    }
  });

  // 보안 시큐리티 세팅 Fn
  function securityReadySet() {
    // 보안 type 세팅
    var securityHtml =  '<div>'+
                    '<a href="javascript:void(0)">'+
                      '<input type="checkbox" id="public_schema0" name="noGlobalSchema" title="No authentication" value="no" onclick="noGlobalSchema(this)">'+
                      '<label for="public_schema0"><span></span>No authentication</label>'+
                  '</a>'+
              '</div>';
    var scopesNum = 1;
    if(yamlOb.securityDefinitions != null){
      $.each(yamlOb.securityDefinitions, function(index, value) {
        var scopesOpHtml = '';
        var activeClass  = "";
        var activeScopesHtml = '';
        if(yamlOb.security != null){
          $("#public_schema0").removeClass("inheritScuty");
          $.each(yamlOb.security, function(secuKey, secuVal) {
            if(secuVal != null){
              $.each(secuVal, function(securityKey, securityVal) {
                if(securityKey == index){
                  activeClass = "inheritScuty";
                  $.each(securityVal, function(scopesKey, scopesVal) {
                    activeScopesHtml = activeScopesHtml + '<li><span>'+scopesVal+'</span></li>';
                  });
                  return true;
                }
              });
            }
          });
        }

        if(value.type == 'oauth2') {
          if(value.scopes != null){
            $.each(value.scopes, function(scopeIndex, scopeVal) {
              scopesOpHtml = scopesOpHtml + '<option value="'+scopeIndex+'">'+scopeIndex+'</option>';
            });
          }
          securityHtml = securityHtml +   '<div>'+
                                   '<a href="javascript:void(0)">'+
                                       '<input type="checkbox" value="'+index+'" id="public_schema'+scopesNum+'" name="securityType" title="'+index+'" disabled class="'+activeClass+'" onclick="oauthClik(this)">'+
                                       '<label for="public_schema'+scopesNum+'"><span></span>'+index+'</label>'+
                                   '</a>'+
                                   '<dl class="range_wrap">'+
                                       '<dt>'+
                                           '<label>범위</label>'+
                                           '<select class="wx140 '+activeClass+'" name="scopesBox'+scopesNum+'" onclick="scopesSelect('+scopesNum+')" >'+
                                              '<option value="">선택하여 주세요.</option>'+
                                              scopesOpHtml +
                                           '</select>'+
                                       '</dt>'+
                                       '<dd>'+
                                           '<ol class="scopes'+scopesNum+' oauthScope">'+
                                           activeScopesHtml +
                                           '</ol>'+
                                       '</dd>'+
                                   '</dl>'+
                               '</div>';
        } else {
          securityHtml =  securityHtml +  '<div>'+
                                '<a href="javascript:void(0)">'+
                                  '<input type="checkbox" value="'+index+'" id="public_schema'+scopesNum+'" name="securityType" title="'+index+'" disabled class="'+activeClass+'" onclick="onGlobalSchema(this);" >'+
                                  '<label for="public_schema'+scopesNum+'"><span></span>'+index+'</label>'+
                              '</a>'+
                          '</div>';
        }
        scopesNum = scopesNum + 1;
      });
      $("#securityType").html(securityHtml);
      if(yamlOb.security == null){
        $("#public_schema0").addClass("inheritScuty");
      } else {
        $("#public_schema0").removeClass("inheritScuty");
      }
      $("#securityType").find("input[type='checkbox']").prop("disabled", true);
      $("#securityType").find("select").prop("disabled", true);
      $(".inheritScuty").prop("checked", true);
    } else {
      $("#securityTr").css("display", "none");
    }
  }

  // 보안 상속 여부 설정
  function securitySet(data){
    $("#securityType").find("input[type='checkbox']").prop("checked", false);

    if(data == 'inherit'){
      securityReadySet();
      jQuery("#inherit").prop('checked', true);
    } else {
      $(".oauthScope").find("li").remove();
      $("#securityType").find("input[type='checkbox']").prop("disabled", false);

      jQuery("#custom").prop('checked', true);
    }
  }

  // oauth 타입 클릭시에 scopes 선택박스 활성화
  function oauthClik(data){
    if($(data).is(":checked") == true) {
      $("#public_schema0").prop("checked", false);
      $(data).parent().parent().find("select").prop("disabled", false);
    } else {
      $(data).parent().parent().find("select").prop("disabled", true);
    }
  }
  // 보안 타입 oauth2 를 선택 후 selecbox에 scopes를 선택 했을 경우
  function scopesSelect(num){
    var valCheck = true;
    var scopesCkHtml = '';
    if($("select[name='scopesBox"+num+"']").val() != ""){
      if($(".scopes"+num).find("li").length > 0){
        for(var i=0;i < $(".scopes"+num).find("li").length;i++){
          if($($(".scopes"+num).find("li")[i]).find("span")[0].innerText == $("select[name='scopesBox"+num+"']").val()){
            valCheck = false;
          }
        }
        if(valCheck != false){
          scopesCkHtml = '<li><span>'+$("select[name='scopesBox"+num+"']").val()+'</span><button type="button" title="삭제" class="btn btn_garbage" onclick="scopesRemove(this);"><span>삭제</span></button></li>';
          $(".scopes"+num).append(scopesCkHtml);
        }
      } else {
        scopesCkHtml = '<li><span>'+$("select[name='scopesBox"+num+"']").val()+'</span><button type="button" title="삭제" class="btn btn_garbage" onclick="scopesRemove(this);"><span>삭제</span></button></li>';
        $(".scopes"+num).append(scopesCkHtml);
      }
    }
  }
  // 선택된 scopes 값 삭제
  function scopesRemove(data){
    $(data).parent().remove();
  }
  // 타입 선택 body가 아닐 경우
  function typeClick(data){
    if(data.value == "Object"){
      // 기존 object가 아니였을때 div 삭제
      if($(data).parent().parent().parent().next().length > 0){
        $(data).parent().parent().parent().next().remove();
      }

      var sectionHtml = '';
      if($(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").length == 0){
        sectionHtml = '<div class="div_draging ui-sortable">'+
                        '<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElAdd(this);"><span>파라미터 추가</span></button>'+
                        '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElAdd(this);"><span>속성 추가</span></button>'+
                      '</div>';
        $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().append(sectionHtml);
      }
    } else {
      if($(data).parent().parent().parent().next().length == 0){
        if(data.value == "Array"){
          var arrayHtml = '';
          arrayHtml =   '<tr>'+
                    '<th scope="row">'+
                      '<div class="essential">of</div>'+
                    '</th>'+
                    '<td><div>'+
                      '<select class="w100" onchange="typeClick(this);" name="type">'+
                                      '<option value="">타입을 선택하여 주세요</option>'+
                                        <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                          <c:if test="${list.cdNm ne 'Object'}">
                                            '<option value="${list.cdNm}">${list.cdNm}</option>'+
                                          </c:if>
                                        </c:forEach>
                                    '</select>'+
                    '</div></td>'+
                  '</tr>';
          $(data).parent().parent().parent().parent().append(arrayHtml);
        }
      } else {
        if(data.value != "Array"){
          for(var i = $(data).parent().parent().parent().parent().find("tr").length-1;i > 0;i--){
            if(i > $(data).parent().parent().parent().index()){
              $(data).parent().parent().parent().parent().find("tr").eq(i).remove();
            }
          }

        }
      }
    }

    dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
  }
  // 타입 선택 (body 일 경우)
  function typeBodyClick(data){
    if(data.value == "Object"){
      // 기존 object가 아니였을때 div 삭제
      if($(data).parent().parent().parent().next().length > 0){
        $(data).parent().parent().parent().next().remove();
      }

      var sectionHtml = '';
      if($(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").length == 0){
        sectionHtml = '<div class="div_draging ui-sortable">'+
                        '<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElAdd(this);"><span>파라미터 추가</span></button>'+
                        '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElAdd(this);"><span>속성 추가</span></button>'+
                      '</div>';
        $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().append(sectionHtml);
      }
    } else {
      $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").remove();

      if($(data).parent().parent().parent().next().length == 0){
        if(data.value == "Array"){
          var arrayHtml = '';
          arrayHtml =   '<tr>'+
                    '<th scope="row">'+
                      '<div class="essential">of</div>'+
                    '</th>'+
                    '<td><div>'+
                      '<select class="w100" onchange="typeBodyClick(this);" name="type">'+
                                      '<option value="">타입을 선택하여 주세요</option>'+
                                        <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                          '<option value="${list.cdNm}">${list.cdNm}</option>'+
                                        </c:forEach>
                                    '</select>'+
                    '</div></td>'+
                  '</tr>';
          $(data).parent().parent().parent().parent().append(arrayHtml);
        }
      } else {
        if(data.value != "Array"){
          for(var i = $(data).parent().parent().parent().parent().find("tr").length-1;i > 0;i--){
            if(i > $(data).parent().parent().parent().index()){
              $(data).parent().parent().parent().parent().find("tr").eq(i).remove();
            }
          }

        }
      }
    }
    dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
  }
  // 타입 선택 (body 일 경우)(응답 파라미터일 경우)
  function typeBodyExampleClick(data){
    if(data.value == "Object"){
      // 오브젝트 일경우 예제 삭제
      if($(data).parent().parent().parent().parent().find(".example").length > 0){
        $(data).parent().parent().parent().find("th").eq(1).remove();
        $(data).parent().parent().parent().find("td").eq(1).remove();
      }
      // 기존 object가 아니였을때 div 삭제
      if($(data).parent().parent().parent().next().length > 0){
        for(var i = $(data).parent().parent().parent().parent().find("tr").length-1;i > 0;i--){
          if(i > $(data).parent().parent().parent().index()){
            $(data).parent().parent().parent().parent().find("tr").eq(i).remove();
          }
        }
      }


      var sectionHtml = '';
      if($(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").length == 0){
        sectionHtml = '<div class="div_draging ui-sortable">'+
                        '<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElExampleAdd(this);"><span>파라미터 추가</span></button>'+
                        '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElExampleAdd(this);"><span>속성 추가</span></button>'+
                      '</div>';
        $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().append(sectionHtml);
      }
    } else {
      // 오브젝트 일경우 예제 추가
      if($(data).parent().parent().parent().find(".example").length == 0 && $(data).parent().parent().parent().find(".ofClass").length == 0){
        $(data).parent().parent().parent().append('<th scope="row"><div class="essential">예제</div></th>');
        $(data).parent().parent().parent().append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
      }
      $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").remove();

      if($(data).parent().parent().parent().next().length == 0){
        if(data.value == "Array"){
          var arrayHtml = '';
          arrayHtml =   '<tr>'+
                    '<th scope="row">'+
                      '<div class="essential ofClass">of</div>'+
                    '</th>'+
                    '<td><div>'+
                      '<select class="w100" onchange="typeBodyExampleClick(this);" name="type">'+
                                      '<option value="">타입을 선택하여 주세요</option>'+
                                        <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                          '<option value="${list.cdNm}">${list.cdNm}</option>'+
                                        </c:forEach>
                                    '</select>'+
                    '</div></td>'+
                  '</tr>';
          $(data).parent().parent().parent().parent().append(arrayHtml);
        }
      } else {
        if(data.value != "Array"){
          for(var i = $(data).parent().parent().parent().parent().find("tr").length-1;i > 0;i--){
            if(i > $(data).parent().parent().parent().index()){
              $(data).parent().parent().parent().parent().find("tr").eq(i).remove();
            }
          }
        }
      }
    }
    dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
  }
  // 요청 파라미터 파라미터 추가
  function paramAdd(data){
    var paramBtnHtml = '';
    requiredNum = requiredNum + 1;
    if($(data).parent().parent().parent().find("span")[0].innerText == "path"){
      $("#paramForm").find("input[name='required']").prop("checked", true);
      $("#paramForm").find("input[name='required']").prop("disabled", true);
    } else {
      $("#paramForm").find("input[name='required']").prop("checked", false);
      $("#paramForm").find("input[name='required']").prop("disabled", false);
    }
    // body파라미터와 formdata파라미터가 같이 등록될 경우 swagger 구문 에러가 발생
    if($(data).parent().parent().parent().find("span")[0].innerText == "formData"){
      if($("input[name='reqContentType']").is(":checked") == true){
        $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupConfirm").find('#alertTxt').html('body파라미터와 formData파라미터는<br /> 같이 등록할 수 없습니다.');
        $("#popupConfirm").dialog("open");
        return false;
      }
    }
    // 응답 파라미터의 헤더는 필수 값이 없다.
    if($(data).parent().parent().parent().parent().find(".responseForm").length > 0){
      $("#paramForm").find("a").css("display", "none");
    } else {
      $("#paramForm").find("a").css("display", "");
    }

    $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if($(data).parent().parent().parent().find(".div_draging").length > 0){
      $(data).parent().parent().find(".div_draging").find("button").last().before($("#paramForm").html());
    } else {
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                  '<div class="div_draging">'+
                      '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                        $("#paramForm").html() +
                  '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                  '</div>'+
              '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }

    dragDrop();
  }
  function bodyCheckboxCk(data){
    if($(".reqFormData").find("section").length > 0){
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
      $("#popupConfirm").find('#alertTxt').html('body파라미터와 formData파라미터는<br /> 같이 등록할 수 없습니다.');
      $("#popupConfirm").dialog("open");
      $(data).prop("checked", false);
    }
  }
  // 요청 파라미터 삭제
  function paramDel(data){
    // 파라미터가 1개일경우에는 div_draging 전체 삭제 1개 초과일 경우 section만 삭제
    var parentPath = $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent();
    if(parentPath.parent().children("section").length == 1){
      parentPath.parent().parent().remove();
    } else {
      parentPath.remove();
    }
  }
  // 요청 파라미터 삭제 (body 일 경우)
  function paramBodyDel(data){
    // 파라미터가 1개일경우에는 div_draging 전체 삭제 1개 초과일 경우 section만 삭제
    var parentPath = $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent();
    if($(parentPath).parent().parent().attr("class") == "paraDiv_drag"){
      $(parentPath).parent().remove();
    } else {
      parentPath.remove();
    }

  }
  // 요청 파라미터 파라미터 추가 (body 부분)
  function paramBodyAdd(data){
    requiredNum = requiredNum + 1;
    $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if($(data).parent().parent().parent().find("section").length == 1){
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                  '<div class="div_draging">'+
                      $("#paramBodyForm").html() +
                  '</div>'+
              '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }
  }
  // 요청 파라미터 파라미터 추가 (body 부분)
  function paramReqBodyAddBtn(data){
    requiredNum = requiredNum + 1;
    $("#paramReqBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramReqBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if($(data).parent().parent().parent().find("section").length == 1){
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                  '<div class="div_draging">'+
                      $("#paramReqBodyDataTypeForm").html() +
                  '</div>'+
              '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }
  }
  // 응답 파라미터 파라미터 추가 (body 부분)
  function paramResBodyAddBtn(data){
    var aHtml = "";
    if($(data).parent().parent().parent().attr("class").indexOf("responseForm") > -1){
      aHtml = $("#paramResBodyDataTypeForm").find(".fr").html();
      $("#paramResBodyDataTypeForm").find(".fr").find("a").remove();
    }
    requiredNum = requiredNum + 1;

    if($("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0){
      $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
      $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
    }

    $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if($(data).parent().parent().parent().find("section").length == 1){
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                  '<div class="div_draging">'+
                      $("#paramResBodyDataTypeForm").html() +
                  '</div>'+
              '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    } else {
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
      $("#popupConfirm").find('#alertTxt').html('body파라미터는 1개만 추가 할수 있습니다.');
      $("#popupConfirm").dialog("open");
    }
    if($(data).parent().parent().parent().attr("class").indexOf("responseForm") > -1){
      aHtml = $("#paramResBodyDataTypeForm").find(".fr").html(aHtml);
    }
  }

  // object 일 경우 속성 추가 버튼
  function objectElAdd(data){
    requiredNum = requiredNum + 1;
    $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    $(data).parent().find("button").last().before($("#paramBodyForm").html());
  }
  // object 일 경우 속성 추가 버튼응답 파라미터일 경우
  function objectElExampleAdd(data){
    requiredNum = requiredNum + 1;
    $("#paramBodyExampleForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramBodyExampleForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    $(data).parent().find("button").last().before($("#paramBodyExampleForm").html());
  }

  // response tab 추가 이벤트
  function responseTabAdd(data){
    tabNum = tabNum + 1;
    var formHtml = '';
    var tabHtml  = '';
    // 보안 스키마 탭 append 시작
    tabHtml =   '<div id="tab'+tabNum+'">'+
              '<a href="javascript:void(0)" title="basic"><span onclick="onTab('+tabNum+');">200</span></a><button type="button" title="삭제" class="btn btn_garbage" onclick="responseTabDel('+tabNum+');"><span>삭제</span></button>'+
              '</div>';
      $("#responseTab").append(tabHtml);
    // 보안 스키마 탭 append 끝

    // 보안 스키마 탭 form append 시작
    formHtml =  '<div id="tabForm'+tabNum+'" class="tab-content" data-tabnum="'+tabNum+'">'+
            $("#responseTabForm").html()+
          '</div>';


    $(".tab_wraping").append(formHtml);
    // 보안 스키마 탭 form append 끝

    // header 추가
    var jq_new_form = $('#responseHeaderForm').clone().removeAttr('id').show();
    jq_new_form.find('.schema_wrap').attr('id', 'headerForm' + tabNum);
    $('#responseDiv').append(jq_new_form);

    // body 추가
    jq_new_form = $('#responseBodyForm').clone().removeAttr('id').show();
    jq_new_form.find('.schema_wrap').attr('id', 'bodyForm' + tabNum);
    jq_new_form.find('textarea').attr('id', 'resAccount' + tabNum);
    jq_new_form.find('input[name="resContentType"]').each(function(index, item) {
      $(item).attr('id', $(item).attr('id') + tabNum).next().attr('for', $(item).attr('id'));
    });
    $('#responseDiv').append(jq_new_form);

    // 탭관련 class current 수정
    onTab(tabNum);
  }
  // responseCode 변경시에 탭이름 변경
  function resposeCdCng(data) {
    //-- [tag:adpt][chg] var tabId = "#tab" + $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().attr("data-tabnum");
    var tabId = "#tab" + $(data).closest('.tab-content').attr("data-tabnum");
    $(tabId).children("a").children("span").text($(data).val());
  };
  // response 탭 삭제 매소드
  function responseTabDel(num) {
    $('#tab' + num).remove();
    $('#tabForm' + num).remove();
    $('#headerForm' + num).remove();
    $('#bodyForm' + num).remove();
    $('#bodyForm' + num).find('input[name="resContentType"]').each(function(index, item){
      $(item).prop("checked", false);
    });

    //--[tag:adpt][cmt][for bug]tabNum = num-1;
    onTab(1);
  };

  //-- [tag:adpt][renew]
  //-- org_pathRegFormPrivate.js pathSave()로 저장후 renewal
  // path 저장
  function pathSave() {
    //-- API{저장}
    if (fn_check_regform_action('pathRegForm:pathSave') == false) {
      return false;
    }
/*
	if (!hasXSSAndMove(['apiNm','apiDesc','impact','apiPath','apiId'])) {
	    return false;
	}
*/
    // body데이터 형식 체크박스 가 체크되어 있을 경우 파라미터 존재유무 체크
    if ($("input[name='reqContentType']").is(":checked") == true) {
      if ($(".reqBody").find(".inner").length == 0) {
        alert_message('요청 body파라미터를 추가하세요.', 'API');
        return false;
      }
    }

    //-- 데이터 검사 {
    dataValidation();

    // 에러 건수가 1개라도 있으면 저장 되지 않음
    if (errorNum > 0) {
      err_on();
      var offset = $("#container").offset();
      $('html, body').animate({scrollTop : offset.top}, 500);
      return false;
    }
    else {
      //--[tag:adpt][chg]
      //-- $('.err_tooltip').css("display", "none"); $('.err_count').css("display", "none");
      $('.err_tooltip, .err_count').hide();
    }
    //-- 데이터 검사 }

    //-- fv_xxx 선언 {
    var fv_pApiPath_val = $("#pApiPath").val();
    var fv_pApiMethod_val = $("#pApiMethod").val();
    var fv_pApiCtgryNm_val = $("#pApiCtgryNm").val();
    var fv_pApiCopyYn_val = $('#pApiCopyYn').val();
    var fv_input_path_val; //--  $("input[name='path']").val()
    var fv_securityType_checked; //-- $("#securityType input[type='checkbox']:checked")
    //-- fv_xxx 선언 }

    var jq_ref;
    var jq_req_inner, jq_req_inner_idx;     //-- $(".reqQuery").find(".inner"), $(".reqHeaders").find(".inner"), $(".reqPath").find(".inner"), $(".reqFormData").find(".inner")
    var jq_reqBody_draging; //-- $(".reqBody").children().children(".paraDiv_drag").children(".div_draging")

    var jq_res_inner, jq_res_inner_idx;     //-- $("#headerForm"+ (n_ii + 1)).find(".inner"), $("#bodyForm"+ (n_ii + 1)).find(".inner")
    var jq_resBody_draging; //-- $("#bodyForm"+ (n_ii + 1)).children().children(".paraDiv_drag").children(".div_draging");

    //yaml 값 초기화 후 저장 시작
    yamlOb = YAML.parse($("#yamlSbst").val());

    /** PATH URI 저장 시작   ==========>   ***/
    // pathParam이 있을경우에는 path 뒤에 pathParam 붙여서 저장
    var yamlObPaths = "";
    var dataOb = new Object();
    if (typeof(yamlOb['paths']) == 'undefined') {
      yamlOb['paths'] = new Object();
    }
    if ((fv_pApiPath_val != '') && (fv_pApiMethod_val != '') && (fv_pApiCopyYn_val != 'Y') && (fv_pApiCopyYn_val != 'A') && (fv_pApiCopyYn_val != 'V')) {
      if (yamlOb['paths'][fv_pApiPath_val]) {
        delete yamlOb['paths'][fv_pApiPath_val][fv_pApiMethod_val.toLowerCase()]; // 기존 경로의 매소드 삭제
        if (Object.keys(yamlOb['paths'][fv_pApiPath_val]).length == 0) {
          delete yamlOb['paths'][fv_pApiPath_val];
        }
      }
    }

    fv_input_path_val = $("input[name='path']").val();
    // path param이 있을 경우에는 yaml path에 변수로 추가 해주어야 한다
    jq_req_inner = $(".reqPath").find(".inner");
    if (0 < jq_req_inner.length) {
      var pathAdd = "";
      for (var n_ii = 0; n_ii < jq_req_inner.length; n_ii++) {
        var pathParamStr = "/{" + $(jq_req_inner[n_ii]).find("input[name='name']").val() + "}";
        if (fv_input_path_val.indexOf(pathParamStr) == -1) {
          pathAdd = pathAdd + pathParamStr;
        }
      }
      $("input[name='path']").val(fv_input_path_val + pathAdd);
      fv_input_path_val = $("input[name='path']").val();

      if (jQuery.isEmptyObject(yamlOb.paths[fv_input_path_val])) {
        yamlOb.paths[fv_input_path_val] = {};
      }
      yamlObPaths = yamlOb.paths[fv_input_path_val];
    }
    else {
      if (jQuery.isEmptyObject(yamlOb.paths[fv_input_path_val])) {
        yamlOb.paths[fv_input_path_val] = {};
      }
      yamlObPaths = yamlOb.paths[fv_input_path_val];
    }

    
    var methodVar = $("select[name='method'] option:checked").text().toLowerCase(); //메소드 변수명으로 담아준다. (대문자로 들어온 값 소문자로 변환)

    yamlObPaths[methodVar] = {};  // 패스 초기화
    yamlObPaths[methodVar].summary     = $("input[name='summary']").val();  // 패스에 대한 이름
    yamlObPaths[methodVar].operationId = $("input[name='apiId']").val();  // 패스에 대한API ID
    yamlObPaths[methodVar].description = $("textarea[name='account']").val(); // 패스에 대한 설명
    <%--
    //-- [tag:SR-20210222][cmt]
    /*--
    //-- [tag:SR-20201127][add]
    yamlObPaths[methodVar]['x-guideGubun'] = $("#guideGubun").val();
    --*/
    --%>
    <%-- //-- [tag:SR-20210222][add] --%>
    delete yamlObPaths[methodVar]['x-guideGubun'];

    //-- apiNo 처리 {
    if ($('#pApiNo').val() == '') { // 신규 api 번호 조회
      $.ajax({
        url: '<c:url value="/api/reg/selApiPathApiNoAjax.do"/>', type: 'POST', cache: false, async: false,
        success: function(data) {
          if ($has_own(data, 'apiNo') == false) { alert_message('정보검색 작업 수행 중 오류가 발생했습니다. - [err: apino]', '알림'); return false; }
          $('#pApiNo').val(data['apiNo']);
          yamlObPaths[methodVar]['x-apiNo'] = data.apiNo;
          g_apiVerNo = data.apiNo;
          yamlObPaths[methodVar]['x-apiVerNo'] = g_apiVerNo;
        },
        error: function(request, status, error) { err_message(status, error); }
      });
    }
    else {
      yamlObPaths[methodVar]['x-apiNo'] =  $('#pApiNo').val();
      yamlObPaths[methodVar]['x-apiVerNo'] = g_apiVerNo;
    }
    //-- apiNo 처리 }

    // API그룹 명 저장
    //--##[?]yamlObPaths[methodVar]['x-category'] =  {};
    yamlObPaths[methodVar]['x-category'] = fv_pApiCtgryNm_val;
    <%-- //-- [tag:job-20200420][add] --%>
    yamlObPaths[methodVar]['tags'] = [fv_pApiCtgryNm_val];
    
    var XCateOb = new Object();
    XCateOb['apiNm'] = $("input[name='summary']").val();
    XCateOb['apiNo'] = yamlObPaths[methodVar]['x-apiNo'];

    if ((fv_pApiPath_val != '') && (fv_pApiMethod_val != '') && (fv_pApiCopyYn_val != 'Y') && (fv_pApiCopyYn_val != 'A') && (fv_pApiCopyYn_val != 'V')) {
      if (Object.keys(yamlOb['x-category'][fv_pApiCtgryNm_val][fv_pApiPath_val]).length == 1) {
        delete yamlOb['x-category'][fv_pApiCtgryNm_val][fv_pApiPath_val]; // 기존 경로의 매소드 삭제
      } else {
        delete yamlOb['x-category'][fv_pApiCtgryNm_val][fv_pApiPath_val][(fv_pApiMethod_val.toLowerCase())]; // 기존 경로의 매소드 삭제
      }
      if (typeof(yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val]) == 'undefined') {
        yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val] = new Object();
      }
      yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val][methodVar] = XCateOb;
    }
    else {
      if (typeof(yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val]) == 'undefined') {
        yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val] = new Object();
      }
      yamlOb['x-category'][fv_pApiCtgryNm_val][fv_input_path_val][methodVar] = XCateOb;
    }
    //-- [tag:SR-20210222][cmt][i][x-visiblity, x-display deprecated]
    /*--
    //-- 20190308 apiGubun(visiblity),use_yn 추가
    // apiGubun 저장
    //--##[?]yamlObPaths[methodVar]['x-visiblity'] = {};
    yamlObPaths[methodVar]['x-visiblity'] = $('#apiGubun').val();
    // display 저장
    //--##[?]yamlObPaths[methodVar]['x-display'] = {};
    yamlObPaths[methodVar]['x-display'] = $('#apiUseYn').val();
    --*/
    <%-- //-- [tag:SR-20210222][add] --%>
    delete yamlObPaths[methodVar]['x-visiblity']
    delete yamlObPaths[methodVar]['x-display']
    /** PATH URI 저장 끝   ==========>   ***/

    /** 보안 스키마 시작   ==========>   ***/
    fv_securityType_checked = $("#securityType input[type='checkbox']:checked");
    if ((fv_securityType_checked.length > 0) && ($("input[name='setyrityType']:checked").val() == "custom")) {
      var securityArray = new Array();
      var security = new Object();
      var arryList = {};
      // 보안 No authentication 선택시에 저장 안함
      if ($(fv_securityType_checked[0]).val() != "no") {
        for (var n_ii = 0; n_ii < fv_securityType_checked.length; n_ii++) {
          var array = new Array(); //초기화
          jq_ref = $(fv_securityType_checked[n_ii]).parent().parent().find("li");
          for (var n_jj = 0; n_jj < jq_ref.length; n_jj++) {
            array.push($(jq_ref[n_jj]).find("span")[0].innerText);
          }
          arryList = {};
          arryList[$(fv_securityType_checked[n_ii]).val()] = array;
          securityArray.push(arryList);
        }
        yamlObPaths[methodVar].security = securityArray;
      }
    }
    /** 보안 스키마 끝   ==========>   ***/

    /** request 파라미터 세팅 시작   ==========>   ***/
    var paramArray = new Array();
    var paramOb = {};

    <%-- //--##[tag:adpt][renew][이전의 코드를 4번 반복하던것을 renewal] --%>
    //-- query, header, path, form 파라미터 처리 공통화 {
    var a_mode_in = ['query', 'header', 'path', 'formData'];
    var a_jq_sel = ['.reqQuery', '.reqHeaders', '.reqPath', '.reqFormData'];
    for (var n_jj = 0; n_jj < a_mode_in.length; n_jj++) {
      jq_req_inner = $(a_jq_sel[n_jj]).find(".inner");

      for (var n_ii = 0; n_ii < jq_req_inner.length; n_ii++) {
        jq_req_inner_idx = $(jq_req_inner[n_ii]);

        paramOb = {};
        paramOb['in']           = a_mode_in[n_jj];
        paramOb['name']         = jq_req_inner_idx.find("input[name='name']").val();
        paramOb['description']  = jq_req_inner_idx.find("input[name='account']").val();
        paramOb['required']   = jq_req_inner_idx.find("input[name='required']").is(":checked");
        if (a_mode_in[n_jj] == 'path') {
          paramOb['required']   = true; // 패스 일 경우 required는 항상 트루 이다 아닐시에 swagger 에러
        }
        paramOb['x-example']    = jq_req_inner_idx.find("input[name='example']").val();
        paramOb['x-dataTypeCd'] = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

        var ref_select_val = jq_req_inner_idx.find("select").val();
        if (ref_select_val == 'Array') {
          var emptyOb = new Object();
          dataOb[0] = jq_req_inner[n_ii];
          typeArrayFn($(dataOb[0]), emptyOb);

          var ref_key_name_val = jq_req_inner_idx.find("input[name='name']").val();
          paramOb['type']  = emptyOb[ref_key_name_val]['type'];
          paramOb['items'] = emptyOb[ref_key_name_val]['items'];
        }
        else {
          paramOb['type'] = ref_select_val.toLowerCase();
        }
        // Query 파라미터 배열로 저장
        paramArray.push(paramOb);
      }
    }
    //-- query, header, path, form 파라미터 처리 공통화 }

    /*********************** request body 파라미터 세팅 시작   ==========>     ***/
    jq_ref = $("input[name='reqContentType']:checked");
    if (0 < jq_ref.length) { //--@[Content-Type선택이 있으면]
      var consumesArray = new Array();
      exampleOb = new Object();

      //consumes 저장
      jq_ref = $("input[name='reqContentType']:checked");
      for (var n_ii = 0; n_ii < jq_ref.length; n_ii++) {
        consumesArray.push($(jq_ref[n_ii]).val());
      }
      yamlObPaths[methodVar].consumes = consumesArray;  //--@[Content-Type저장]

      paramOb = {};
      paramOb['in']           = 'body';
      paramOb['name']         = 'body';  //--@[초기값설정][overwrite됨?]
      paramOb['description']  = $(".reqBody").find("textarea[name='reqBodyAccount']").val();
      paramOb['schema']       = {};
      paramOb['x-dataTypeCd'] = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

      jq_reqBody_draging = $(".reqBody").children().children(".paraDiv_drag").children(".div_draging");

      var ref_select_val = jq_reqBody_draging.find("select").val();
      var ref_key_name_val = jq_reqBody_draging.find("input[name='name']").val();
      if (ref_select_val == 'Array') {
        paramOb['name']           = ref_key_name_val;
        paramOb['required']       = jq_reqBody_draging.find("input[name='required']").is(":checked");
        paramOb['schema']['type'] = ref_select_val.toLowerCase();
        paramOb['x-example']      = jq_reqBody_draging.find("input[name='example']").val();

        var emptyOb = new Object();
        dataOb[0] = jq_reqBody_draging.find(".inner")[0];
        typeArrayFn($(dataOb[0]), emptyOb);

        paramOb['schema']['items']  = emptyOb[ref_key_name_val]['items'];
        paramOb['schema']['description']  = jq_reqBody_draging.find("input[name='account']").val();
      }
      else if (ref_select_val == 'Object') {
        dataOb[0] = $(jq_reqBody_draging);
        <%-- //--[tag:adpt][cmt][??][baybe dataOb[0]] --%>
        if (!jQuery.isEmptyObject(dataOb)) {
          var emptyOb = new Object();
          typeObject(dataOb[0], emptyOb);
          paramOb['schema']['properties'] = emptyOb['properties'][ref_key_name_val]['properties'];
          paramOb['schema']['required']   = emptyOb['properties'][ref_key_name_val]['required'];
        }
        paramOb['name']                  = ref_key_name_val;
        paramOb['required']              = jq_reqBody_draging.find("input[name='required']").is(":checked");
        paramOb['schema']['type']        = ref_select_val.toLowerCase();
        paramOb['schema']['description'] = jq_reqBody_draging.find("input[name='account']").val();
        paramOb['x-example']             = "Ob_Small_Com_Del" + JSON.stringify(exampleOb) + "Ob_Small_Com_Del";
      }
      else {
        paramOb['name']      = ref_key_name_val;
        paramOb['required']  = jq_reqBody_draging.find("input[name='required']").is(":checked");
        paramOb['x-example'] = jq_reqBody_draging.find("input[name='example']").val();

        if ($(jq_reqBody_draging.find("select option:selected")).text().indexOf("(data type)") > -1) {
          paramOb['schema']['$ref'] = "#/definitions/"+  ref_select_val;
          paramOb['x-dataTypeCd']   = "PRMTYP1040";
        }
        else {
          paramOb['schema']['type']        = ref_select_val.toLowerCase();
          paramOb['schema']['description'] = jq_reqBody_draging.find("input[name='account']").val();
        }
      }
      // body 파라미터 배열로 저장
      paramArray.push(paramOb);
    }
    /*********************** request body 파라미터 세팅 끝   ==========>     ***/
    yamlObPaths[methodVar].parameters = paramArray;
    /** request 파라미터 세팅 끝     ==========>   ***/

    /** response 파라미터 세팅 시작   ==========>   ***/
    <%-- //--##[tag:adpt][cmt]if (0 < tabNum) { --%>
    if ($("#responseTab").children("div").length > 0) {
      yamlObPaths[methodVar].responses = {};
    }
    for (var n_ii = 0; n_ii < $("#responseTab").children("div").length; n_ii++) {
      var resTabId = ("#tabForm"+ (n_ii + 1));
      var statusCdVar = $(resTabId).find("select[name='resStatus']").val(); // 상태 코드

      yamlObPaths[methodVar].responses[statusCdVar] = {};
      yamlObPaths[methodVar].responses[statusCdVar].description = $(resTabId).find("input[name='resAccont']").val();

      /*********************** response header 파라미터 세팅 시작   ==========>     ***/
      var resHeaderId = ("#headerForm"+ (n_ii + 1));

      jq_res_inner = $(resHeaderId).find(".inner");
      if (0 < jq_res_inner.length) {
        yamlObPaths[methodVar].responses[statusCdVar].headers = {};
      }
      for (var n_jj = 0; n_jj < jq_res_inner.length; n_jj++) {
        jq_res_inner_idx = $(jq_res_inner[n_jj]);

        var resStatusHeaderVar = jq_res_inner_idx.find("input[name='name']").val();
        yamlObPaths[methodVar].responses[statusCdVar].headers[resStatusHeaderVar] = {};

        paramOb = {};
        paramOb['description']  = jq_res_inner_idx.find("input[name='account']").val();
        paramOb['x-example']    = jq_res_inner_idx.find("input[name='example']").val();
        paramOb['x-dataTypeCd'] = "PRMTYP1020"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
        /* paramOb['required']  = $($(resHeaderId).find(".inner")[n_jj]).find("input[name='required']").is(":checked");  // 응답 파라미터는 필수 값이 없음*/

        var ref_select_val = jq_res_inner_idx.find("select").val();
        if (ref_select_val == 'Array') {
          var emptyOb = new Object();
          dataOb[0] = jq_res_inner[n_jj];
          typeArrayFn($(dataOb[0]), emptyOb);

          paramOb['type']  = emptyOb[resStatusHeaderVar]['type'];
          paramOb['items'] = emptyOb[resStatusHeaderVar]['items'];
        }
        else {
          paramOb['type'] = ref_select_val.toLowerCase();
        }
        // response headers 저장
        yamlObPaths[methodVar].responses[statusCdVar].headers[resStatusHeaderVar] = paramOb;
      }
      /*********************** response header 파라미터 세팅 끝     ==========>     ***/

      /*********************** response Body 파라미터 세팅 시작   ==========>         ***/
      var resBodyId = ("#bodyForm"+ (n_ii + 1));
      var resProducesArray = new Array();
      yamlObPaths[methodVar].responses[statusCdVar].schema = {};
      // Produces 저장
      jq_ref = $("input[name='resContentType']:checked");
      for (var n_jj = 0; n_jj < jq_ref.length; n_jj++){
        var producesCk = resProducesArray.contains($(jq_ref[n_jj]).val());
        if (producesCk == false) {
          resProducesArray.push($(jq_ref[n_jj]).val());
        }
      }
      yamlObPaths[methodVar].produces = resProducesArray;

      jq_res_inner = $(resBodyId).find(".inner");
      if (0 < jq_res_inner.length) {
        paramOb = {};
        for (var n_jj = 0; n_jj < jq_res_inner.length; n_jj++) {
          // 예제 배열 초기화
          exampleOb = new Object();
  
          paramOb = {};
          paramOb['x-description'] = $(resBodyId).find("textarea").val();
          paramOb['x-dataTypeCd']  = "PRMTYP1020"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
  
          jq_resBody_draging = $(resBodyId).children().children(".paraDiv_drag").children(".div_draging");
  
          var ref_select_val = jq_resBody_draging.find("select").val();
          var ref_key_name_val = jq_resBody_draging.find("input[name='name']").val();
          if (ref_select_val == 'Array') {
            var emptyOb = new Object();
            dataOb[0] = jq_resBody_draging.find(".inner")[0];
            typeArrayFn($(dataOb[0]), emptyOb);
  
            paramOb['type']        = ref_select_val.toLowerCase();
            paramOb['items']       = emptyOb[ref_key_name_val]['items'];
            paramOb['description'] = jq_resBody_draging.find("input[name='account']").val();
            paramOb['x-name']      = ref_key_name_val;
            <%-- //-- [tag:adpt][chg] paramOb['example'] = "Ob_Small_Com_Del" + jq_resBody_draging.find("input[name='example']").val() + "Ob_Small_Com_Del"; --%>
            paramOb['example']     = jq_resBody_draging.find("input[name='example']").val();
          }
          else if (ref_select_val == 'Object') {
            dataOb[0] = $(jq_resBody_draging);
            <%-- //--[tag:adpt][cmt][??][baybe dataOb[0]] --%>
            if (!jQuery.isEmptyObject(dataOb)) {
              var emptyOb = new Object();
              typeObject(dataOb[0], emptyOb);
              paramOb = emptyOb;
            }
            paramOb['type']        = ref_select_val.toLowerCase();
            paramOb['description'] = jq_resBody_draging.find("input[name='account']").val();
            paramOb['x-name']      = ref_key_name_val;
            paramOb['example']     = "Ob_Small_Com_Del" + JSON.stringify(exampleOb) + "Ob_Small_Com_Del"; // example 를 넣을때 yaml에 문자열변환하는 순간 '를 추가 하여서 문자열로 변환 후 '를 삭제하기 위해 임의의 문자 추가
          }
          else {
            paramOb['example']     = jq_resBody_draging.find("input[name='example']").val();
            paramOb['description'] = jq_resBody_draging.find("input[name='account']").val();
            paramOb['x-name']      = ref_key_name_val;
            /* paramOb['required'] = jq_resBody_draging.find("input[name='required']").is(":checked"); */
  
            if (jq_resBody_draging.find("select").children("option:selected").text().indexOf("(data type)") > -1) {
              paramOb['$ref']         = "#/definitions/" + ref_select_val;
              paramOb['x-dataTypeCd'] = "PRMTYP1040";
            }
            else {
              paramOb['type'] = ref_select_val.toLowerCase();
            }
          }
        }
        yamlObPaths[methodVar].responses[statusCdVar].schema = paramOb;
      }
      /*********************** response Body 파라미터 세팅 끝   ==========>       ***/
    } //-- for (var n_ii = 0; n_ii < $("#responseTab").children("div").length; n_ii++) {
    <%-- //--##[tag:adpt][cmt]} //-- if (0 < tabNum) { --%>
    /** response 파라미터 세팅 끝     ==========>   ***/

    // 패스가 2개 이상일 경우에 처음 등록한 / 경로 삭제
    if (Object.keys(yamlOb['paths']).length > 1) {
      delete yamlOb['paths']['/']; // json / 경로 삭제
    }

    var yamlStr = YAML.stringify(yamlOb);
    // 임의 문자열 및 ' 삭제
    yamlStr = yamlStr.replace(/\'Ob_Small_Com_Del/gi, "");
    yamlStr = yamlStr.replace(/example: >-/g, "example:");
    yamlStr = yamlStr.replace(/Ob_Small_Com_Del\'/gi, "");
    yamlStr = yamlStr.replace(/Ob_Small_Com_Del/gi, "");
    yamlStr = yamlStr.replace(/required: \[\]/gi, "");
    // console.log(yamlStr);
    // 필수값이 없는것들 삭제
	// console.log('yamlStr : ', yamlStr);
    <%-- //-- [tag:adpt][add] // apiVerNo --%>
    <%-- //-- [tag:SR-20201127][add] // guideGubun --%>
    var param = {
      apiSpcNo: $("#pApiSpcNo").val(),  // 무조건 존재
      apiNo: $("#pApiNo").val(),  // 존재(수정) , 부재(등록)
      apiVerNo: g_apiVerNo, 
      yamlStr: yamlStr,  // yaml 데이터 : 필수,

      apiNm: $("#apiNm").val(),
      apiDesc: $("#apiDesc").val(),
      apiId: $("#apiId").val(),
      apiPath: $("#apiPath").val(),
      apiCtgryNo: $("#pApiCtgryNo").val(),
      apiCtgryNm: $("#pApiCtgryNm").val(),
      methodCd: $("select[name='method']").val(),
      methodCdNm: $("#methodBox option:checked").text(),
      insertYn: $("#insertYn").val(),
      apiGubun: $("select[name='apiGubun']").val(),
      useYn: $("#apiUseYn").val(),
      guideGubun: $("#guideGubun").val(),
      insertImpactYn: $("#insertImpactYn").val(),
      impact: $("#impact").val()
    };

    // console.log('JSON param : ', JSON.stringify(param));
    
    $.ajax({
      url: '<c:url value="/api/reg/savApiRegPathAjax.do"/>', type: 'POST', cache: false, async: false, data: param,
      success: function(data) {
        if ($has_own(data, 'apiRegVO') == false) { alert_message('정보처리 작업 수행 중 오류가 발생했습니다. - [err: apisave]', '알림'); return false; }
        var alert_option = {
          ok_button_onclick : (function() { //레이어 메세지 적용
            if (getCookie('apiPopDel') != 'Y') { $('.pop_testRequest').dialog('open'); }  //-- 테스트/등록요청 안내
          }),
        };
		
		if("1" == data.returnCode){
			alert_message('<spring:message code="api.req.save.msg" />', 'API', alert_option);

			        // 값 세팅
			        $('#pApiSpcNo').val(data['apiRegVO']['apiSpcNo']);
			        $('#pApiNo').val(data['apiRegVO']['apiNo']);
			        g_apiVerNo = data['apiRegVO']['apiVerNo'];
			        $('#pApiPath').val(data['apiRegVO']['apiPath']);
			        $('#pApiMethod').val(data['apiRegVO']['methodCdNm'].toLowerCase());
			        $('#pApiCtgryNo').val(data['apiRegVO']['apiCtgryNo']);
			        $('#insertYn').val('N');
			        $('#insertImpactYn').val('N');
			        
			        // yaml값 셋팅
			        $('#yamlSbst').val(yamlStr);
			        <%-- //--[tag:adpt][add] --%>
			        $('#pApiCopyYn').val(''); // 'Y':copy, 'A':edit other method, 'V': version upgrade

			        XLeftMenuSet(yamlOb['x-category']); //LEFT 메뉴 다시 셋팅
			        isChange = false; // 페이지 이동 체크 여부
			        // dataInfoOb = JSON.stringify(data['dataTypeInfo']);  // DATY TYPE 다시 세팅
		}else{
			alert_message('입력 할 수 없는 문자가 검출되었습니다.', 'API', alert_option);
		}
		

      },
      error: function(request, status, error) { err_message(status, error); }
    });
  } //-- function pathSave() {

  function typeObject(data, object) {
    object['properties'] = {};
    object['required'] = new Array();
    exampleOb = new Object();
    for (var n_ii = 0; n_ii < $(data[0]).children("section").length; n_ii++) {
      typeObjectTwo($(data[0]).children("section")[n_ii],
        object['properties'],
        exampleOb,
        object['required']);
    }
  }

  function typeObjectTwo(data, object, exOb, requriedArray) {
    var s_name = $(data).find("input[name='name']").val();

    if ($(data).find("select[name='type']").val() == 'Object') {
      object[s_name] = {};
      object[s_name]['type'] = $(data).find("select[name='type']").val().toLowerCase();
      object[s_name]['description'] = $(data).find("input[name='account']").val();
      object[s_name]['properties'] = {};
      if ($(data).find("input[name='example']").val() != undefined) {
        object[s_name]['x-example'] = $(data).find("input[name='example']").val();
      }
      if ($(data).find("input[name='required']").is(":checked") == true) {
        requriedArray.push(s_name);
      }
      exOb[s_name] = {};

      object[s_name]['required'] = new Array();
      for (var n_ii=0; n_ii < $(data).children(".div_draging").children("section").length; n_ii++) {
        typeObjectTwo($(data).children(".div_draging").children("section")[n_ii],
        object[s_name]['properties'],
        exOb[s_name],
        object[s_name]['required']);
      }
    }
    else if ($(data).find("select[name='type']").val() == 'Array') {
      exampleArrayStr = "";
      typeArrayFn($(data), object);
      object[s_name]['description'] = $(data).find("input[name='account']").val();
      if ($(data).find("input[name='example']").val() != undefined) {
        object[s_name]['x-example'] = $(data).find("input[name='example']").val();
      }
      if ($(data).find("input[name='required']").is(":checked") == true) {
        requriedArray.push(s_name);
      }
      exOb[s_name] = exampleArrayStr;
    }
    else {
      object[s_name] = {};
      exOb[s_name] = $(data).find("input[name='example']").val();

      if ($(data).find("select[name='type'] option:selected").text().indexOf("(data type)") > -1) {
        <%-- //--[tag:adpt][chg][toLowerCase()제거] object[s_name]['$ref'] = "#/definitions/"+  $(data).find("select[name='type']").val().toLowerCase(); --%>
        object[s_name]['$ref'] = "#/definitions/"+  $(data).find("select[name='type']").val();
        object[s_name]['x-dataTypeCd']    = "PRMTYP1040";
      }
      else {
        if ($(data).find("input[name='required']").is(":checked") == true) {
          requriedArray.push(s_name);
        }
        object[s_name]['type']      = $(data).find("select[name='type']").val().toLowerCase();
        object[s_name]['description']   = $(data).find("input[name='account']").val();
        if ($(data).find("input[name='example']").val() != undefined) {
          object[s_name]['x-example']   = $(data).find("input[name='example']").val();
        }
      }
    }
  }

  function typeArrayFn(data, object) {
    var s_name = $(data).find("input[name='name']").val();
    
    if ($(data).find("input[name='example']").val() != "") {
      exampleArrayStr = $(data).find("input[name='example']").val();
    }
    if (typeof(s_name) == "undefined") {
      object['items'] = {};
      typeArray = object['items'];
    }
    else {
      object[s_name] = {};
      typeArray = object[s_name];
    }

    for (var n_ii = 0; n_ii < $(data).find("select").length; n_ii++) {
      if ($($(data).find("select")[n_ii]).val() == 'Array') {
        type = $($(data).find("select")[n_ii]).val();
        example = $($($(data).find("select")[n_ii]).parent().parent().parent().find("input")).val();
        typeArray = typeArrayMake(type, example, typeArray);
      }
      else if ($($(data).find("select")[n_ii]).val() == 'Object') {
        typeArray['type'] = $($(data).find("select")[n_ii]).val().toLowerCase();
        if ($($($(data).find("select")[n_ii]).parent().parent().parent().find("input")).val() != undefined) {
          typeArray['x-example'] = $($($(data).find("select")[n_ii]).parent().parent().parent().find("input")).val();
        }
        typeArray['properties'] = {};
        typeArray['required'] = new Array();

        var div_draging = $($(data).find("select")[n_ii]).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().children(".div_draging");
        for (var n_jj = 0; n_jj < div_draging.children("section").length; n_jj++) {
          typeObjectTwo(div_draging.children("section")[n_jj], typeArray['properties'], exampleOb, typeArray['required']);
        }
        break;
      }
      else {
        typeArray['type'] = $($(data).find("select")[n_ii]).val().toLowerCase();
        break;
      }
    }
  }

  function typeArrayMake(type, example, typeArray) {
    typeArray['type'] = type.toLowerCase();
    typeArray['items'] = {};

    return typeArray['items'];
  }

  function dataValidation() {
    var jq_elem;

    <%-- //--[tag:adpt][add] --%>
        errCountReset();

    // 에러 내용 삭제
    $(".err_tooltip").find("dd").remove();
    $(".err_count").find("em").text(errorNum);
    //--@@$(".red_txt").css("display", "none");

    errCountCk($("input[name='summary']"), "pathSummary", true);  // 이름 검사
    errCountCk($("textarea[name='account']"), "pathApiDesc", true);  // 설명 검사
    errCountCk($("textarea[name='impact']"), "pathImpact", true);  // 영향도 검사
    errCountCk($("select[name='apiGubun']"), "pathApiGubun", true);  // API종류 검사

    <%-- //-- [tag:job-20200420][add] --%>
    errCountCk($("select[name='method']"), "methodIsEmpty", false); // method 검사

    if (errCountCk($("input[name='path']"), "pathPathNull", false) == false) {  // Path 검사
      pathErrCountCk($("input[name='path']"), "pathPath", false); 
    }

    errCountCk($("input[name='apiId']"), "pathApiId", false);  // API 아이디 검사

    jq_elem = $("#requestDiv").find($("input[name='name']"));
    for(var i=0;i < jq_elem.length; i++){  // 이름 검사 (요청 파라미터)
      <%--
      //--[tag:SR-20201126][chg]
      //--##errCountCk(jq_elem.eq(i), "reqName"+i , false);
      --%>
      errCountCk(jq_elem.eq(i), ('paramNameFmt' + i), false);
    }
    <%-- //--[tag:SR-20201126][add] --%>
    jq_elem = $("#requestDiv").find($("input[name='account']"));
    for(var i=0;i < jq_elem.length; i++){  // 설명 검사 (요청 파라미터)
      errCountCk(jq_elem.eq(i), ('paramAccountFmt' + i), false);
    }
    jq_elem = $("#requestDiv").find($("select"));
    for(var i=0;i < jq_elem.length; i++){  // selectbox 검사 (요청 파라미터)
      errCountCk(jq_elem.eq(i), "reqSelect"+i , false);
    }
    jq_elem = $("#requestDiv").find($("input[name='example']"));
    for(var i=0;i < jq_elem.length; i++){ // 예제 검사 (요청 파라미터)
      errCountCk(jq_elem.eq(i), "reqExample"+i , false);
    }
    // 응답 파라미터가 있는지 여부 체크
    if(tabNum == 0){
      errorNum = errorNum + 1;
      $(".err_count").find("em").text(errorNum);
      errorText.push("resParamYn");
    }
    // 응답 코드 중복 검사
    var resStatusArray = [];
    var resStatusDupleYn = 'N';
    $("#responseDiv").find("select[name='resStatus']").each(function(index, item){
      if (resStatusArray.indexOf($(item).val()) != -1) {
        resStatusDupleYn = 'Y';
        return false;
      }
      resStatusArray.push($(item).val());
    });

    var tabErrCheck = errorText.indexOf('resStatusDuple');
    if ('Y' == resStatusDupleYn) {
      errorNum = errorNum + 1; $(".err_count").find("em").text(errorNum);
      errorText.push("resStatusDuple");
    }

    for(var i=1;i < tabNum+1; i++){  // 응답 파라미터 내용 검사
      errCountCk($("#tabForm"+i).find("input[name='resAccont']"), "resAccont"+i , false);
    }

    jq_elem = $("#responseDiv").find($("input[name='name']"));
    for(var i=0;i < jq_elem.length; i++){  // 이름 검사 (응답 파라미터)
      <%--
      //--[tag:SR-20201126][chg]
      //--##errCountCk(jq_elem.eq(i), "resName"+i , false);
      --%>
      errCountCk(jq_elem.eq(i), ('paramNameFmt' + i), false);
    }
    <%-- //--[tag:SR-20201126][add] --%>
    jq_elem = $("#responseDiv").find($("input[name='account']"));
    for(var i=0;i < jq_elem.length; i++){  // 설명 검사 (응답 파라미터)
      errCountCk(jq_elem.eq(i), ('paramAccountFmt' + i), false);
    }
    jq_elem = $("#responseDiv").find($("select"));
    for(var i=0;i < jq_elem.length; i++){  // selectbox 검사 (응답 파라미터)
      errCountCk(jq_elem.eq(i), "resSelect"+i , false);
    }
    // 예제 검사 (응답 파라미터)
    jq_elem = $("#responseDiv").find($("input[name='example']"));
    for(var i=0;i < jq_elem.length; i++){
      errCountCk(jq_elem.eq(i), "resExample"+i , false);
    }

    // 매소드 검사
    overlapCk();
    // api id 검사
    apiIdcheck();
    // 에러 내용 추가
    errTextAppend();
  }

  // 불러온 api정보 세팅
  function apiPathInfoSet() {
    var paramApiMethod = "${fn:escapeXml(param.apiMethod)}";
    var paramApiPath   = "${fn:escapeXml(param.apiPath)}";

    //--[tag:adpt][add] {
    //-- check object exist
    if (typeof(yamlOb['paths']) == 'undefined') {
      alert_message('[o-o] yaml.paths object is not exist'); return;
    }
    if (typeof(yamlOb['paths'][paramApiPath]) == 'undefined') {
      alert_message('[o-o] yaml.paths.' + paramApiPath + ' object is not exist'); return;
    }
    if (typeof(yamlOb['paths'][paramApiPath][paramApiMethod.toLowerCase()]) == 'undefined') {
      alert_message('[o-o] yaml.paths.' + paramApiPath + '.' + paramApiMethod.toLowerCase() + ' object is not exist'); return;
    }
    //--[tag:adpt][add] }

    var pathInfoOb = yamlOb['paths'][paramApiPath][paramApiMethod.toLowerCase()];

    //-- 추가항목이어서 x-apiVerNo항목이 있을경우만 설정
    if ($has_own(pathInfoOb, 'x-apiVerNo') == true) {
      g_apiVerNo = pathInfoOb['x-apiVerNo'];  //-- api version group no
    }

    $("#apiNm").val(pathInfoOb['summary']); // 이름 세팅
    <% //--[tag:adpt][add] %>
    $("#methodBox").val(fn_get_method_comn_cd(paramApiMethod)); // method 세팅
    $("#apiPath").val(paramApiPath);  // path 세팅
    $("#apiId").val(pathInfoOb.operationId);  // api id 세팅
    $("#apiDesc").val(pathInfoOb.description);  // 설명 세팅
    
    <%--
    //-- [tag:SR-20210222][cmt]
    /*--
    //-- [tag:SR-20201127][add]
    $('#guideGubun').val(pathInfoOb['x-guideGubun']);
    --*/
    --%>
    
    /********** 보안 탭 세팅 시작 ********************************/
    if(pathInfoOb.security != undefined){
      var noSelectHtml = '' +
        '<div>'+
          '<a href="javascript:void(0)">'+
            '<input type="checkbox" id="public_schema0" name="noGlobalSchema" title="No authentication" value="no" onclick="noGlobalSchema(this)">'+
            '<label for="public_schema0"><span></span>No authentication</label>'+
          '</a>'+
        '</div>';
      $("#securityType").html("");
      $("#securityType").append(noSelectHtml);

      var securityHmlt = '';
      var securityNum = 0;
      var securityChecked = "";
      $("input[name='setyrityType'][id='inherit']").prop('checked', true);
      $.each(yamlOb.securityDefinitions , function (index, info) {
        securityNum = securityNum + 1;
        securityHmlt = "";
        securityChecked = "";
        securityScopeList = "";
        securityDisabled = "disabled";
        securityScopeArray = new Array();
        if(pathInfoOb.security != undefined){
          $.each(pathInfoOb.security , function (num, item) {
            if(item != undefined){
              $.each(item , function (num2, item2) {
                if(index == num2){
                  securityChecked = "checked";
                  securityDisabled        = "";
                  if(item2 != undefined){
                    $.each(item2 , function (num3, item3) {
                      securityScopeArray.push(item3);
                    });
                  }
                }
              });
            }
          });
        }
        else {
          $("input[name='noGlobalSchema']").prop("checked", true);
        }
        if (info.type == "oauth2") {
          var scopesOptionHtml = '';
          if (info.scopes != undefined) {
            $.each(info.scopes , function (scopeNum, scopeItem) {
              scopesOptionHtml = scopesOptionHtml + '<option value="'+scopeNum+'">'+scopeNum+'</option>';
            });
          }
          if (securityScopeArray != undefined) {
            $.each(securityScopeArray , function (scopeNum, scopeItem) {
              securityScopeList = securityScopeList + '<li><span>'+scopeItem+'</span>';
              if (pathInfoOb.security != undefined) {
                securityScopeList = securityScopeList + '<button type="button" title="삭제" class="btn btn_garbage" onclick="scopesRemove(this);"><span>삭제</span></button>';
              }
              securityScopeList = securityScopeList + '</li>';
            });
          }
          securityHmlt = '' +
            '<div>'+
              '<a href="javascript:void(0)">'+
                '<input type="checkbox" id="public_schema'+securityNum+'" name="securityType" title="'+index+'"  onclick="oauthClik(this)" value="'+index+'" ' + securityChecked + '>'+
                '<label for="public_schema'+securityNum+'"><span></span>'+index+'</label>'+
              '</a>'+
              '<dl class="range_wrap">'+
                '<dt>'+
                  '<label>범위</label>'+
                  '<select class="wx140" name="scopesBox'+securityNum+'" onclick="scopesSelect('+securityNum+')"   '+securityDisabled+'   >'+
                    '<option value="">범위를 선택하여 주세요</option>'+ scopesOptionHtml +
                  '</select>'+
                '</dt>'+
                '<dd>'+
                  '<ol class="scopes' + securityNum + ' oauthScope">'+ securityScopeList + '</ol>'+
                '</dd>'+
              '</dl>'+
            '</div>';
        }
        else {
          securityHmlt = '' +
            '<div>'+
              '<a href="javascript:void(0)">'+
                '<input type="checkbox" id="public_schema' + securityNum + '" name="securityType" title="' + index + '" value="' + index + '" ' + securityChecked + ' onclick="onGlobalSchema(this);" >'+
                '<label for="public_schema' + securityNum + '"><span></span>' + index + '</label>'+
               '</a>'+
            '</div>';
        }
        $("#securityType").append(securityHmlt);
        if (pathInfoOb.security != undefined) {
          $("input:radio[name='setyrityType'][value='custom']").prop("checked", true);
        }
      });
    };
    /********** 보안 탭 세팅 끝 ********************************/
    /********** 파라미터 타입 탭 세팅 시작 ********************************/
    var reqQueryArray = new Array();
    var reqHeadersArray = new Array();
    var reqPathArray = new Array();
    var reqFormDataArray = new Array();
    var reqBodyArray = new Array();
    // 타입이 Object 또는 배열일 경우 div 세팅

    //--##[tag:adpt][chg]if(pathInfoOb.parameters.length > 0){
    if (pathInfoOb.parameters != undefined) {
      for(var i=0; i < pathInfoOb.parameters.length; i++){
        var paramVar = pathInfoOb.parameters[i];
        if (paramVar['in'] == "query")   { reqQueryArray.push(paramVar);    }
        else if (paramVar['in'] == "header")  { reqHeadersArray.push(paramVar);  }
        else if (paramVar['in'] == "path")    { reqPathArray.push(paramVar);     }
        else if (paramVar['in'] == "formData"){ reqFormDataArray.push(paramVar); }
        else if (paramVar['in'] == "body")    { reqBodyArray.push(paramVar);     }
      }
    }

    //-- [tag:adpt][renew][이전의 코드를 4번 반복하던것을 renewal]
    var a_mode_array = [ reqQueryArray, reqHeadersArray, reqPathArray, reqFormDataArray];
    var a_jq_sel = ['.reqQuery', '.reqHeaders', '.reqPath', '.reqFormData'];
    var jq_param_sec, jq_new_form;
    var jq_sec_root, jq_param_root;
    for (var n_jj = 0; n_jj < a_mode_array.length; n_jj++) {
      if (a_mode_array[n_jj].length > 0) {
        $.each(a_mode_array[n_jj], function(index, item) {
          requiredNum = requiredNum + 1;
          jq_new_form = $('#paramForm').clone(); //-- clone template
          //-- [tag:adpt][chg] jq_new_form.find('a').css('display', '');
          jq_new_form.find("input[name='required']").attr('id', 'required' + requiredNum);
          jq_new_form.find("input[name='required']").next().attr('for','required' + requiredNum);
          if (a_jq_sel[n_jj] == '.reqPath') {
            jq_new_form.find('input[name="required"]').prop('disabled', true);
          }

          jq_param_sec = $(a_jq_sel[n_jj]);
          jq_sec_root = jq_param_sec.children('.parameter_add').children('.paraDiv_drag');
          if (jq_sec_root.length == 0) {
            var paramHtml = '<div class="paraDiv_drag  cid_template_first_param_root">'+
                              '<div class="div_draging">'+
                                '<button type="button" class="btn btn_addParabox" onclick="paramAdd(this)" title="파라미터 추가"><span>파라미터 추가</span></button>'+
                                 jq_new_form.html() +
                                '<button type="button" class="btn btn_sml btn_gray" onclick="paramAdd(this)" title="파라미터 추가"><span>파라미터 추가</span></button>'+
                              '</div>'+
                            '</div>';
            jq_param_sec.children('.parameter_add').append(paramHtml);
          }
          else {
            jq_sec_root.children('.div_draging').find('button').last().before(jq_new_form.html());
          }

          jq_sec_root = jq_param_sec.children('.parameter_add').children('.paraDiv_drag');
          jq_param_root = jq_sec_root.children('.div_draging').children('section').eq(index);

          jq_param_root.find("input[name='name']").val(item.name);
          jq_param_root.find("input[name='required']").prop("checked", item.required);
          jq_param_root.find("input[name='account']").val(item.description);
          jq_param_root.find("select[name='type']").val(lowString(item.type));
          jq_param_root.find("input[name='example']").val(item['x-example']);
          if (item.type == 'array') {
            dataInfoArrayDivSet(item.items, jq_param_root, 3);
          }
        });
      }
    }
    
    // body데이터
    if(reqBodyArray.length > 0){
      if(pathInfoOb.consumes != undefined){
        for(var i=0;i < pathInfoOb.consumes.length; i++){
          $("input[name='reqContentType']").each(function(index, value){
            if(pathInfoOb.consumes[i] == value.value){
              this.checked = true;
              $(value).prop("checked", true);
            }
          });
        }
      }

      $.each(reqBodyArray, function(index, item){
        requiredNum = requiredNum + 1;
        if(item.schema['type'] != undefined){

          if(item.schema.type == "object"  && $("#paramResBodyDataTypeForm").find("tbody").find(".example").length > 0){
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
          } else if($("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0){
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
          }

          $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
          $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

          if($(".reqBody").children(".parameter_add").children(".paraDiv_drag").length == 0){
            var bodyHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            $("#paramResBodyDataTypeForm").html() +
                        '</div>'+
                    '</div>';
            $(".reqBody").children(".parameter_add").append(bodyHtml);
          } else {
            $("#paramForm").find("a").css("display", "");
            $(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
          }

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
          //--##[tag:adpt][chg][maybe bug]$("textarea[name='reqBodyAccount']").val(item.description);
          $(".reqBody").find("textarea[name='reqBodyAccount']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.schema.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.schema.type));

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
          if(item.schema.type == "array"){
            resDataInfoArrayDivSet(item.schema.items, $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
          } else if (item.schema.type == "object"){
            resDataInfoObjectDivSet(item.schema.properties, $(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section"), item.schema);
          }
        } else {
          // datatype 이 있을경우
          $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
          $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

          var bodyHtml =  '<div class="paraDiv_drag">'+
                      '<div class="div_draging">'+
                          $("#paramResBodyDataTypeForm").html() +
                      '</div>'+
                  '</div>';
          $(".reqBody").children(".parameter_add").append(bodyHtml);

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
          //--##[tag:adpt][chg][maybe bug]$("textarea[name='reqBodyAccount']").val(item.description);
          $(".reqBody").find("textarea[name='reqBodyAccount']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(item.schema['$ref'].replace("#/definitions/",""));
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        }
      });
    }

    /********** 파라미터 타입 탭 세팅 끝 ********************************/

    /**********응답 파라미터 타입 탭 세팅 시작 ********************************/
    if(pathInfoOb.responses != undefined){
      $.each(pathInfoOb.responses, function(index, item){
        tabNum = tabNum + 1;
        var formHtml = '';
        var tabHtml  = '';
        // 보안 스키마 탭 append 시작
        tabHtml =   '<div id="tab'+tabNum+'" onclick="onTab('+tabNum+');">'+
                  '<a href="javascript:void(0)" title="basic"><span>'+index+'</span></a><button type="button" title="삭제" class="btn btn_garbage" onclick="responseTabDel('+tabNum+');"><span>삭제</span></button>'+
                  '</div>';
          $("#responseTab").append(tabHtml);
        // 보안 스키마 탭 append 끝

        // 보안 스키마 탭 form append 시작
        formHtml =  '<div id="tabForm'+tabNum+'" class="tab-content" data-tabnum="'+tabNum+'">'+
                $("#responseTabForm").html()+
              '</div>';
        $(".tab_wraping").append(formHtml);
        // 보안 스키마 탭 form append 끝
        $("#tabForm"+tabNum).find("select").val(index);
        $("#tabForm"+tabNum).find("input[name='resAccont']").val(item.description);

        // header 추가
        var jq_new_form = $('#responseHeaderForm').clone().removeAttr('id').show();
        jq_new_form.find('.schema_wrap').attr('id', 'headerForm' + tabNum);
        $('#responseDiv').append(jq_new_form);

        if(pathInfoOb.responses[index].headers != undefined){
          var paramDept = 0;
          $.each(pathInfoOb.responses[index].headers, function(headIndex, headItem){
            requiredNum = requiredNum + 1;
            $("#paramForm").find("a").css("display", "none");
            $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            if($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").length == 0){
              var responseHeaders =   '<div class="paraDiv_drag">'+
                            '<div class="div_draging">'+
                                '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                                  $("#paramForm").html() +
                            '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                            '</div>'+
                        '</div>';
              $("#headerForm"+tabNum).children(".parameter_add").append(responseHeaders);
            } else {
              $("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
            }
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='name']").val(headIndex);
            /* $("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section").find("input[name='required']").prop("checked", item.required); */
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='account']").val(headItem.description);
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='example']").val(headItem['x-example']);
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("select[name='type']").val(lowString(headItem.type));
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='required']").prop("checked", headItem.required);

            if(headItem.type == "array"){
              dataInfoArrayDivSet(headItem.items, $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]), 2);
            }
            paramDept = paramDept + 1;
          });
        }

        // body 추가
        jq_new_form = $('#responseBodyForm').clone().removeAttr('id').show();
        jq_new_form.find('.schema_wrap').attr('id', 'bodyForm' + tabNum);
        jq_new_form.find('textarea').attr('id', 'resAccount' + tabNum);
        // context-type
        jq_new_form.find('input[name="resContentType"]').each(function(index, item) {
          $(item).attr('id', $(item).attr('id') + tabNum).next().attr('for', $(item).attr('id'));
          $(item).prop('checked', ((pathInfoOb.produces||[]).indexOf($(item).val()) != -1));
        });
        $('#responseDiv').append(jq_new_form);

        // 스키마가 없을경우 바디 파라미터 세팅 안함
        if(!jQuery.isEmptyObject(pathInfoOb.responses[index].schema)){
          var bodyVar = pathInfoOb.responses[index].schema;
          requiredNum = requiredNum + 1;
          $("#bodyForm"+tabNum).find("textarea").val(bodyVar['x-description']);
          // 데이터 타입 이용 안할시에
          var aHtml = "";
          aHtml = $("#paramResBodyDataTypeForm").find(".fr").html();
          $("#paramResBodyDataTypeForm").find(".fr").find("a").remove();
          if(bodyVar['type'] != undefined){
            if(bodyVar.type == "object"){
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
            } else if($("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0){
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
            }

            $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            if($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").length == 0){
              var bodyHtml =  '<div class="paraDiv_drag">'+
                          '<div class="div_draging">'+
                              $("#paramResBodyDataTypeForm").html() +
                          '</div>'+
                      '</div>';
              $("#bodyForm"+tabNum).children(".parameter_add").append(bodyHtml);
            } else {
              $("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramBodyExampleForm").html());
            }
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='name']").val(bodyVar['x-name']);
            /* $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required); */
            //--##[tag:adpt][cmt]$("textarea[name='reqBodyAccount']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='account']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("select[name='type']").val(lowString(bodyVar.type));

            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='example']").val(bodyVar.example);

            if(bodyVar['required'] != undefined){
              if(bodyVar['required'].indexOf(bodyVar['x-name']) != -1){
                $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", true);
              } else {
                $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", false);
              }
            }

            if(bodyVar.type == "array"){
              // 예제 세팅
              resDataInfoArrayDivSet(bodyVar.items, $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")), 3);
            } else if (bodyVar.type == "object"){
              resDataInfoObjectDivSet(bodyVar.properties[bodyVar['x-name']].properties, $("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section"), bodyVar.properties[bodyVar['x-name']]);
            }
          }
          // 데이터 타입 이용 시에
          else {
            $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            var bodyHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            $("#paramResBodyDataTypeForm").html() +
                        '</div>'+
                    '</div>';
            $("#bodyForm"+tabNum).children(".parameter_add").append(bodyHtml);
            $("textarea[name='reqBodyAccount']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", bodyVar.required);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='example']").val(bodyVar.example);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='name']").val(bodyVar['x-name']);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='account']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("select[name='type']").val(bodyVar['$ref'].replace("#/definitions/",""));
          }
          $("#paramResBodyDataTypeForm").find(".fr").html(aHtml);
        }
      });
      onTab(1);
    }
    /**********응답 파라미터 타입 탭 세팅 끝 ********************************/

    dragDrop();
  }

  // 이름 중복 검사
  function summryDupCk(myData){
    apiRegCheckStrLength(500,'apiNm');
    <%-- //-- [tag:adpt][add][apiVerNo for versionup] --%>
    var param = {
      apiSpcNo: $('#pApiSpcNo').val(),
      apiNo: $('#pApiNo').val(),
      apiNm: $('#apiNm').val(),
      apiVerNo: g_apiVerNo,
    };
    $.ajax({
      url: '<c:url value="/api/reg/selApiNmCheckAjax.do"/>', type: 'POST', async: false, cache: false, data: param,
      success: function(data) {
        if ($has_own(data, 'duplYn') == false) { alert_message('정보검색 작업 수행 중 오류가 발생했습니다. - [err: apiname]', '알림'); return false; }
        if (data['duplYn'] == 'Y') {
          if ($(myData).parent().find('.def_txt').length == 0) {
            $(myData).parent().append('<p class="def_txt">* 중복된API 이름이 존재합니다.</p>');
          }
        }
        else {
          $(myData).parent().find('.def_txt').remove();
        }
      },
      error: function(request, status, error) { err_message(status, error); }
    });
  }

  // 매소드 중복 검사
  function overlapCk() {
    var param = {
      apiSpcNo: $('#pApiSpcNo').val(),  // api spc번호(무조건 존재)
      apiPath: $('#apiPath').val(),  // api path(무조건 존재)
      methodCd: $('select[name="method"]').val(),  // api method(무조건 존재)
      apiCtgryNo: $('#pApiCtgryNo').val(),  // API그룹 번호(무조건 존재)
      apiNo: $('#pApiNo').val(),  // api번호(무조건 존재)
    };
    $.ajax({
      url: '<c:url value="/api/reg/salApijDupPathCheckAjax.do"/>', type: 'POST', async: false, cache: false, data: param,
      success: function(data) {
        if ($has_own(data, 'duplYn') == false) { alert_message('정보검색 작업 수행 중 오류가 발생했습니다. - [err: apipath]', '알림'); return false; }
        var errCheck = errorText.indexOf('pathMtthod');
        if (data['duplYn'] == 'Y') {
          if (errCheck == -1) {
            errorNum = errorNum + 1; $('.err_count').find('em').text(errorNum);
            errorText.push('pathMtthod');
          }
          $('select[name=method]').next().css('display', 'block'); // 경고문이 있을경우 활성화
        } 
        else {
          if (errCheck != -1) {
            errorText.splice(errorText.indexOf('pathMtthod'), 1);
          }
          $('select[name=method]').next().css('display', 'none'); // 경고문이 있을경우 비 활성화
        }
      },
      error: function(request, status, error) { err_message(status, error); }
    });
  }

  // api id 중복 체크
  function apiIdcheck(){
    var param = {
      apiSpcNo: $('#pApiSpcNo').val(),  // 무조건 존재
      apiNo: $('#pApiNo').val(),
      apiId: $('#apiId').val(),
      apiVerNo: g_apiVerNo,
    };
    $.ajax({
      url: '<c:url value="/api/reg/salApiIdCheckAjax.do"/>', type: 'POST', async: false, cache: false, data: param,
      success: function(data) {
        if ($has_own(data, 'checkCnt') == false) { alert_message('정보검색 작업 수행 중 오류가 발생했습니다. - [err: apiid]', '알림'); return false; }
        var errorNm = 'pathApiIdOverlap';
        var errCheck = errorText.indexOf(errorNm);
        if (data['checkCnt'] > 0) {
          if (errCheck == -1){
            errorNum = errorNum + 1; $('.err_count').find('em').text(errorNum);
            errorText.push(errorNm);
          }
          $('#apiId').next().text('* API 아이디가 중복입니다.'); // 경고문이 있을경우 활성화
          $('#apiId').next().removeClass('def_txt').addClass('red_txt').css('display', 'inline-block');
        }
        else {
          if (errCheck != -1) {
            errorText.splice(errorText.indexOf(errorNm), 1);
          }
          $('#apiId').next().text('* 예시: OTP_01'); // 경고문이 있을경우 비 활성화
          $('#apiId').next().removeClass('red_txt').addClass('def_txt').css('display', 'none');
        }
      },
      error: function(request, status, error) { err_message(status, error); }
    });
  }

  // dataType Object 시에 div 세팅
  function dataInfoObjectDivSet(data, appendTag){
    if(appendTag.children(".bodyForm").length == 0){
      var html =  '<div class="div_draging paramBodyDataDiv bodyForm ui-sortable">'+
              '<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElAdd(this)"><span>파라미터 추가</span></button>'+
              '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElAdd(this)"><span>속성 추가</span></button>'+
            '</div>';
      appendTag.append(html);
    }
    $.each(data, function(index, item) {
      requiredNum = requiredNum + 1;
      $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
      $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);
      $("#paramBodyForm").find("section").attr("id", "section_"+requiredNum)
      appendTag.children(".paramBodyDataDiv").find("button").last().before($("#paramBodyForm").html());
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='name']").val(index)
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("select").val(lowString(item.type));
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='account']").val(item.description);

      if(lowString(item.type) == "Object"){
        dataInfoObjectDivSet(item.properties, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum));
      } else if(lowString(item.type) == "Array"){
        dataInfoArrayDivSet(item, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum), 1);
      }
    });
  }
  // dataType Object 시에 div 세팅 (응답 파라미터 시에)
  function resDataInfoObjectDivSet(data, appendTag, parentData){
    if(appendTag.children(".bodyForm").length == 0){
      var html =  '<div class="div_draging paramBodyDataDiv bodyForm ui-sortable">'+
              '<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElExampleAdd(this)"><span>파라미터 추가</span></button>'+
              '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElExampleAdd(this)"><span>속성 추가</span></button>'+
            '</div>';
      appendTag.append(html);
    }
    $.each(data, function(index, item) {
      requiredNum = requiredNum + 1;
      if(lowString(item.type) == "Object" && $("#paramBodyExampleForm").find("tbody").find(".example").length > 0){
        $("#paramBodyExampleForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
        $("#paramBodyExampleForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
      } else if($("#paramBodyExampleForm").find("tbody").find(".example").length == 0){
        $("#paramBodyExampleForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
        $("#paramBodyExampleForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
      }

      $("#paramBodyExampleForm").find("input[name='required']").attr("id","required"+requiredNum);
      $("#paramBodyExampleForm").find("input[name='required']").next().attr("for","required"+requiredNum);
      $("#paramBodyExampleForm").find("section").attr("id", "section_"+requiredNum)
      appendTag.children(".paramBodyDataDiv").find("button").last().before($("#paramBodyExampleForm").html());
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='name']").val(index)
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("select").val(lowString(item.type));
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='account']").val(item.description);
      if(item['example'] != undefined){
        appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='example']").val(item['example']);
      }else {
        appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='example']").val(item['x-example']);
      }
      // 필수 값 체크

      if(parentData['required'] != undefined){
        if(parentData['required'].indexOf(index) != -1){
          appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='required']").prop("checked", true);
        } else {
          appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='required']").prop("checked", false);
        }
      }

      if(lowString(item.type) == "Object"){
        resDataInfoObjectDivSet(item.properties, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum), item);
      } else if(lowString(item.type) == "Array"){
        resDataInfoArrayDivSet(item, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum), 1);
      }
    });
  }

  // dataType array 시에 div 세팅
  function dataInfoArrayDivSet(data, appendTag, num){
    var itemType = "";
    var itemItems = new Array();
    var itemPpt   = new Object();

    Ayinnum = num;
    if(Ayinnum == 0){
      appendTag.append($("#dataForm").html());
      appendTag = appendTag.children(".paramBodyDataDiv").find("section");
    }

    $.each(data, function(index, item) {
      if(index == "type"){
        itemType = item;
      } else if(index == "items"){
        itemItems = item;
      } else if(index == "properties"){
        itemPpt = item;
      }
    });
    appendTag.find("tr").eq(Ayinnum).find("select").val(lowString(itemType));

    Ayinnum = Ayinnum + 1;
    if (itemType == "array") {
      dataInfoArrayDivSet(itemItems, appendTag, Ayinnum);
    }
    else if (itemType == "object") {
      dataInfoObjectDivSet(itemPpt, appendTag);
    }
  }

  // dataType array 시에 div 세팅 (응답 파라미터 시에)
  function resDataInfoArrayDivSet(data, appendTag, num){
    var itemType = "";
    var itemItems = new Array();
    var itemPpt   = new Object();

    Ayinnum = num;
    if(Ayinnum == 0){
      appendTag.append($("#dataForm").html());
      appendTag = appendTag.children(".paramBodyDataDiv").find("section");
    }

    $.each(data, function(index, item) {
      if(index == "type"){
        itemType = item;
      } else if(index == "items"){
        itemItems = item;
      } else if(index == "properties"){
        itemPpt = item;
      }
    });
    appendTag.find("tr").eq(Ayinnum).find("select").val(lowString(itemType));

    Ayinnum = Ayinnum + 1;
    if (itemType == "array") {
      resDataInfoArrayDivSet(itemItems, appendTag, Ayinnum);
    }
    else if (itemType == "object") {
      resDataInfoObjectDivSet(itemPpt, appendTag, data);
    }
  }

  // 소문자 변환 Fn
  function lowString(data){
    if(data == "Object" || data == "Array"){
      return data;
    } else {
      return data.toLowerCase();
    }
  }

  // api editor open
  function fn_popApiYamlTool(api_spc_no) {
    if ('function' == typeof(gfn_popApiYamlTool)) {
      gfn_popApiYamlTool(api_spc_no);
    }
  }

  //-- [tag:job-20200420][add] {
  function fn_export_ARSENAL(apiSpcNo) {
    var szArsenalHost = $("#idGitlabArsenalHost").val();
    var szArsenalPath = $("#idGitlabArsenalBasePath").val();
    var szProjectName = "";
    var szNamespace   = "";
    
    fnGetYamlInfoFromDB(apiSpcNo);
    g_apiSpcNo    = typeof apiSpcNo == "undefined" ? "0" : apiSpcNo;
    //g_projectName = typeof projectName == "undefined" ? "" : projectName;
    szProjectName = "<b><font color='blur'>" + g_projectName + "</font></b>";
    szNamespace   = g_szNamespace;
 
    // 작업을 위한 임시 테스트용
    if(confirm("내보기내기를 진행하시겠습니까?")) {
      // 검증이력 dialog
      g_yamlPath = szArsenalHost + szNamespace + "/" + szProjectName + szArsenalPath + szProjectName + ".yaml";
      //$(".popArsenal").dialog("close");
      $(".popArsenal").dialog({title:"EXPORT TO ARSENAL", minWidth:1000, resizable: false, modal:false});
      $("#idSpanReturn").text("");  // 내보내기 결과 셋업
        $("#idSpanErrorCode").text(""); // 에러코드 셋업
        $("#idSpanErrorMsg").text("");  // 에러메세지 셋업
        $(".data_link").parent().removeClass("process_failure_next");
        $(".data_link").parent().removeClass("process_success_next");
        $(".data_link").parent().removeClass("process_failure_prev");
        $(".data_link").parent().removeClass("process_success_prev");

        //event.preventDefault();
        // 프로젝트 ID 전역변수에 담음
      //g_szProjectId = fnGetProjectIdFromGitlab(szNamespace);
        //console.log("Project ID:" + g_szProjectId);

        fnGetFileInfoFromGitlab(szNamespace);
        if(g_bIsExsist) {
        g_bIsExsist = true;
        $(".data_link").parent().addClass("process_success_next");
        $("#idSpanReturn").text("성공");  // 내보내기 결과 셋업
    }
        
      window.setTimeout((function () { $(".popArsenal").dialog("open"); }), 50);
      $("#idSpanYamlPath").html(g_yamlPath);
      
    }
  }

  function fnGetFileInfoFromGitlab(projectNamespace) {

    var szNamespace  = typeof projectNamespace == "undefined" ? "" : projectNamespace;
    var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F";
    var szFilePath   = g_projectName + "%2Eyaml?ref=master";
    var szReturn   = true;
    var szAjaxUr   = "/apidev/api/arsenal/getFileFromGitlabAjax";//szGitlabHost + szFilePath;
    var param        = new Object();
    
    param.projectName = g_projectName;
    param.namespace   = szNamespace;
    
    $.ajax({
        url    : szAjaxUr,
        type   : 'POST',
        data   : JSON.stringify(param),
        async  : false,
        cache  : false,
        crossDomain: true,
        contentType: 'application/json',
        dataType:'json',
        success: function(data){
          var projectData = JSON.parse(data.info.jsonResponse);
          
          if(data.projectId != "") {
            
            if(projectData.file_name != g_projectName + ".yaml") {
              g_bIsExsist = false;
            } else {
              g_bIsExsist = true;
            }
            
            g_szProjectId = data.projectId;
            
          } else {
            if(projectData.errorCode == "404") {
              g_bIsExsist   = false;
                g_szProjectId = "E404";
            } else {
              g_bIsExsist   = false;
              g_szProjectId = "E500";
            }
          }
          
          console.log("Gitlab: " + g_szProjectId);
          //alert(data.errorcode + ": " + data.errordescription);
          //fn_export_GITLAB_(YAML.stringify(data.Content));
          //console.log("ProjectName: " + YAML.stringify(data.Content));
        },
        error:function(request,status,error){
          g_bIsExsist   = false;
          g_szProjectId = "";
          console.log("Gitlab Error: " + error);
        }
      });
  }

  function fnGetYamlInfoFromDB(apiSpcNo) {
    var szReturn = true;
    
    var param = {
        'apiSpcNo': apiSpcNo
      };
      
    $.ajax({
      url    : '<c:url value="/api/main/getYmalAjax.do"/>',
      type   : 'POST',
      data   : param,
      dataType:'json',
      async  : false,
        cache  : false,
      success: function(data){
        if(data == null || data.file_name != g_projectName + ".yaml") {
          szReturn = false;
        }
        g_yamlOb = YAML.parse(data.info.yamlSbst);
        g_projectName = data.info.apiNm;
        g_szNamespace = data.info.projectNamespace;
        console.log("YMAL:" + g_yamlOb);
        console.log("Project Name:" + g_projectName);
      },
      error:function(request,status,error){
        szReturn = false;
        console.log("code:" + request.status + "\n" + "error:" + error);
      }
    });
      
    return szReturn;
  }
  //-- [tag:job-20200420][add] }

  //-- [tag:adpt][add] {
  function fn_ui_set_versionup(p_path, p_ver) {
    if (p_path.length > 0) {
      $('#apiPath').val(p_path).prop('disabled', true);
    }
    if (p_ver.length > 0) {
      $('#apiVer').val(p_ver).prop('disabled', true);
    }
  }
  //-- [tag:adpt][add] }
</script>

<input type="hidden" id="insertYn" name="insertYn" value="${fn:escapeXml(info.insertYn)}" />
<input type="hidden" id="insertImpactYn" name="insertImpactYn" value="${fn:escapeXml(info.insertImpactYn)}" />
<input type="hidden" id="pApiSpcNo" name="pApiSpcNo" value="${fn:escapeXml(param.apiSpcNo)}" />
<input type="hidden" id="pApiNo" name="pApiNo" value="${fn:escapeXml(param.apiNo)}" />
<input type="hidden" id="pApiPath" name="pApiPath" value="${fn:escapeXml(param.apiPath)}" />
<input type="hidden" id="pApiMethod" name="pApiMethod" value="${fn:escapeXml(param.apiMethod)}" />
<input type="hidden" id="pApiCtgryNo" name="pApiCtgryNo" value="${fn:escapeXml(cate.apiCtgryNo)}" />
<input type="hidden" id="pApiCtgryNm" name="pApiCtgryNm" value="${fn:escapeXml(cate.apiNm)}" />
<input type="hidden" id="pApiCopyYn" name="pApiCopyYn" value="${fn:escapeXml(param.apiCopyYn)}" />

<div id="container">
    <div class="sVisual sv_regiapi">
      <div>
        <h2>API 등록</h2>
        <p>여러분이 생각하는 모든 생각들을 API로 만들고 KT 플랫폼을 이용하여 서비스 해보세요</p>
      </div>
    </div>
    <div class="contents">
      <div class="conBox">
        <div class="pg_location"><a>Go home</a> <span>></span> API 등록</div>

        <div id="content">
                    <!-- regiApi_wrap -->
                    <div class="regiApi_wrap">
                        <div class="regi_status_bar">
                            <div class="regi_user_info">
                                <p>${ssUserVo.maskingMbrId}님 환영합니다!</p>
                                <c:set var="authList"   value="${ssUserVo.authList}" />
                                <c:set var="authValue"  value="" />
                                <c:set var="authSize"   value="0" />
                                <c:forEach var="item" items="${authList}" varStatus="status">
                                  <c:if test="${status.index == 0}" >
                                    <c:set var="sysValue" value="${item.sysId}" />
                                    <c:set var="autNm" value="${item.autNm}" />
                                  </c:if>
                                  <c:set var="authSize" value="${status.count}" />
                                </c:forEach>
                                <div><span>${sysValue} / <strong>${autNm}</strong></span><a href="javascript:void(0)" onclick="goMyPage();" class="btn_detailView" title="DetailView">DetailView</a></div>
                            </div>
                            <div class="regi_dashBoard">
                                <div class="call_dev"><p>개발요청 : <a href="javascript:void(0)" title="10">${reqCnt}</a>건</p></div>
                                <div class="btns">
                                    <a href="javascript:void(0)" onclick="mvMainPage();" class="btn-lg btn_gray" title="목록으로 가기"><span>목록으로 가기</span></a>
                                </div>
                            </div>
                        </div>

                        <div class="regi_step">
                            <ul>
                                <li class="step01 current">
                                    <div class="step_info">
                                        <p>STEP 01</p>
                                        <strong>기본정보</strong>
                                    </div>
                                </li>
                                <li class="step02 current">
                                    <div class="step_info">
                                        <p>STEP 02</p>
                                        <strong>API 그룹</strong>
                                    </div>
                                </li>
                                <li class="step03 current">
                                    <div class="step_info">
                                        <p>STEP 03</p>
                                        <strong>API 등록</strong>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <!-- // regiApi_wrap -->

                    <div class="regist_wrap">
                        <!-- regist_layout -->
                        <div class="regist_layout">
                            <!-- api_left -->
                            <div class="api_left">
                                <div class="default_info">
                                    <p title=""></p>
                                    <span>${cate.apiNm}</span>
                                </div>

                                <div class="dragToggle">
                                    <ul class="accordion acco_depth1">
                                        <li class="acco_opened">
                                            <div>
                                                <a class="acco_toggle active" href="javascript:;" title="API 리스트"><span>API 리스트</span><em></em></a>
                                            </div>
                                            <!-- 2depth Content -->
                                            <div class="hidden_div" style="display: block;">
                                                <div>
                                                    <ul class="last_depth" id="XLeftMenu">
                                                        
                                                    </ul>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <!-- // api_left -->

                            <!-- api_right -->
                            <div class="api_right">
                                <div class="rightConBoxing">
                                    <ul class="accordion acco_depth1">
                                        <li class="acco_opened">
                                            <div>
                                                <a class="acco_toggle active" href="javascript:;" title="API 정보"><span>API 정보</span><em></em><strong class="cid_apiedit_mode" title="수정"></strong></a>
                                            </div>
                                            <!-- 2depth Content -->
                                            <div class="hidden_div" style="display: block;">
                                                <div class="pkg_board">
                                                    <table class="table-vw">
                                                        <caption>API 정보 Table</caption>
                                                        <colgroup>
                                                            <col style="width:15%;">
                                                            <col style="width:35%;">
                                                            <col style="width:15%;">
                                                            <col style="width:35%;">
                                                        </colgroup>

                                                        <tbody>
                                                            <tr>
                                                                <th scope="row"><div class="essential">API 이름</div></th>
                                                                <td colspan="3">
                                                                  <div><input type="text" id="apiNm" name="summary" title="API 이름 입력" onkeyup="summryDupCk(this);"></div>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">Method / Path</div></th>
                                                              <td colspan="3">
                                                                <div class="method_path">
                                                                  <select class="w100" id="methodBox" name="method" title="Method 선택" onchange="overlapCk()">
                                                                    <option value="">Method</option>
                                                                    <c:forEach var="item" items="${methodList}" varStatus="status">
                                                                      <option value="${item.comnCd}">${item.cdNm}</option>
                                                                    </c:forEach>
                                                                  </select>
                                                                  <p class="red_txt" style="display: none;">* 중복된 Method/Path가 존재합니다.</p>
                                                                  <span class="path_inp"><input type="text" id="apiPath" name="path" title="Path 입력" onchange="overlapCk()"></span>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">API 아이디</div></th>
                                                              <td colspan="3">
                                                                <div class="api_id"><input type="text" id="apiId" name="apiId" title="API 아이디 입력" onblur="apiIdcheck();">
                                                                <p class="def_txt">* 예시: OTP_01</p></div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">버전</div></th>
                                                              <td colspan="3">
                                                                <div class="api_id"><input type="text" id="apiVer" name="apiVer" title="버전 입력" value="1.0"></div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">API 설명</div></th>
                                                              <td colspan="3">
                                                                <div class="txtarea_wrap"><textarea id="apiDesc" name="account" title="API 설명 입력" onchange="apiRegCheckStrLength(2000,'apiDesc')"   onkeyup="apiRegCheckStrLength(2000,'apiDesc')" ></textarea></div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">영향도</div></th>
                                                              <td colspan="3">
                                                                <div class="txtarea_wrap"><textarea id="impact" name="impact" title="영향도 입력" onchange="apiRegCheckStrLength(2000,'impact')"   onkeyup="apiRegCheckStrLength(2000,'impact')" ></textarea></div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div class="essential">API 종류</div></th>
                                                              <td>
                                                                <select class="w100" name="apiGubun" title="API 종류 선택">
                                                                  <option value="">선택</option>
                                                                  <c:forEach var="item" items="${apiGubunList}" varStatus="status">
                                                                    <option value="${item.comnCd}">${item.cdNm}</option>
                                                                  </c:forEach>
                                                                </select>
                                                              </td>
                                                              <th scope="row"><div class="essential">API 노출여부</div></th>
                                                              <td>
                                                                <select class="w100" id="apiUseYn" name="apiUseYn" title="API 노출여부 선택">
                                                                  <option value="Y">노출</option>
                                                                  <option value="N">비노출</option>
                                                                </select>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <th scope="row"><div>가이드 구분</div></th>
                                                              <td colspan="3">
                                                                <select class="w140" id="guideGubun" name="guideGubun" title="가이드 구분 선택">
                                                                  <option value="">선택안함</option>
                                                                  <c:forEach var="item" items="${guideGubunList}" varStatus="status">
                                                                    <option value="${item.comnCd}">${item.cdNm}</option>
                                                                  </c:forEach>
                                                                </select>
                                                              </td>
                                                            </tr>
                                                            <tr id="securityTr">
                                                              <th scope="row"><div>보안 스키마</div></th>
                                                              <td colspan="3">
                                                                <div class="security_type">
                                                                  <div class="radio_form">
                                                                    <input type="radio" id="inherit" name="setyrityType" value="inherit" checked onclick="securitySet('inherit')"><label for="inherit"><span></span>상속</label>
                                                                    <input type="radio" id="custom"  name="setyrityType" value="custom" onclick="securitySet('custom')"><label for="custom"><span></span>사용자 정의</label>
                                                                  </div>

                                                                  <div id="securityType">
                                                                    
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </li>
                                        <li class="acco_opened">
                                          <div>
                                              <a class="acco_toggle active" href="javascript:;" title="요청 파라미터"><span>요청 파라미터</span><em></em></a>
                                          </div>
                                          <!-- 2depth Content -->
                                          <div class="hidden_div" id="requestDiv" style="display: block;">
                                            <!-- Query -->
                                            <div class="reqQuery">
                                              <div class="parameter_add">
                                                <span>query</span>
                                                <div class="paraDiv_drag  cid_template_first_param_root">
                                                  <div class="div_draging">
                                                      <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                      <button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                            <!-- Headers -->
                                            <div class="reqHeaders">
                                              <div class="parameter_add">
                                                <span>header</span>
                                                <div class="paraDiv_drag  cid_template_first_param_root">
                                                  <div class="div_draging">
                                                      <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                      <button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                            <!-- Path -->
                                            <div class="reqPath">
                                              <div class="parameter_add">
                                                <span>path</span>
                                                <div class="paraDiv_drag  cid_template_first_param_root">
                                                  <div class="div_draging">
                                                      <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                      <button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                            <!-- FormData -->
                                            <div class="reqFormData">
                                              <div class="parameter_add">
                                                <span>formData</span>
                                                <div class="paraDiv_drag  cid_template_first_param_root">
                                                  <div class="div_draging">
                                                      <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                      <button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                            <!-- Body -->
                                            <div class="reqBody">
                                              <div class="parameter_add">
                                                <div class="body_title">
                                                  <span>body</span>
                                                  <div class="check_form ml20">
                                                    <c:forEach var="item" items="${consumesList}" varStatus="status">
                                                      <input type="checkbox" id="reqContentType${status.count}" name="reqContentType" value="${item.cdNm}" onclick="bodyCheckboxCk(this)"><label for="reqContentType${status.count}"><span></span>${item.cdNm}</label>
                                                    </c:forEach>
                                                  </div>
                                                </div>
                                                
                                                <div class="pkg_board mb10">
                                                  <table class="table-vw">
                                                      <caption>API 정보 Table</caption>
                                                      <colgroup>
                                                          <col style="width:15%;">
                                                          <col style="width:85%;">
                                                      </colgroup>

                                                      <tbody>
                                                          <tr>
                                                              <th scope="row"><div>Body 설명</div></th>
                                                              <td>
                                                                <div class="txtarea_wrap"><textarea name="reqBodyAccount" title="Body 설명 입력"></textarea></div>
                                                              </td>
                                                          </tr>
                                                      </tbody>
                                                  </table>
                                                </div>
                                                <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramResBodyAddBtn(this)"><span>파라미터 추가</span></button>
                                              </div>
                                            </div>
                                          </div>
                                        </li>
                                        <li class="acco_opened">
                                          <div>
                                              <a class="acco_toggle active" href="javascript:;" title="응답 파라미터"><span>응답 파라미터</span><em></em></a>
                                          </div>
                                          <!-- 2depth Content -->
                                          <div class="hidden_div" id="responseDiv" style="display: block;">
                                            <div class="tab_list2" id="responseTab">
                                              
                                              <button type="button" title="추가" class="btn btn_add" onclick="responseTabAdd(this);"><span>추가</span></button>
                                            </div>

                                            <div class="tab_wraping">
                                              
                                            </div>
                                          </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <!-- // api_right -->
                        </div>
                        <!-- // regist_layout -->

                        <div class="btn_set">
                            <button type="button" title="저장" class="btn btn_black" onclick="pathSave();"><span>저장</span></button>
                            <!-- <button type="button" title="취소" class="btn btn_cancel"><span>취소</span></button> -->
                        </div>

                        <div class="err_tooltip">
                          <p class="err_count">ERROR <em>0</em></p>
                          <dl>
                            <dt>상세내용</dt>
                          </dl>

                          <button type="button" title="ERROR 메세지 끄기" class="layer_close"><span>ERROR 메세지 끄기</span></button>
                        </div>
                    </div>
                </div>
      </div>
    </div>
  </div>

  <div class="dim_layer"></div>

  <!--// popup content - 테스트/등록요청 안내 -->
  <%@ include file="/WEB-INF/jsp/api/regFormSharePopup.jsp" %>
  <!-- popup content - 테스트/등록요청 안내 //-->

  <!--// popup content - 알림 -->
  <div class="pop_alert_wrap" id="popupConfirm" title="알림">
      <!--  popup content Start  -->
      <div class="popup_content">
          <div class="alert_txt" id="alertTxt">
              
          </div>

          <div class="lPop_bottom brd_tp">
              <button type="button" title="확인" class="btn btn_sml3 btn_black" onclick="jQuery('#popupConfirm').dialog('close');">확인</button>
          </div>
      </div>
  </div>
  <!-- popup content - 알림 //-->

  <!-- 공통으로 쓰이는 파라미터 폼 (request용) -->
  <div id="paramForm" style="display: none;">
    <section class="paraSection_drag ui-sortable-handle">
      <div class="inner">
        <div class="pkg_board">
            <table class="table-vw">
                <caption>API 정보 Table</caption>
                <colgroup>
                    <col style="width:15%;">
                    <col style="width:35%;">
                    <col style="width:15%;">
                    <col style="width:35%;">
                </colgroup>

                <tbody>
                    <tr>
                        <th scope="row"><div class="essential">이름</div></th>
                        <td><div><input type="text" name="name" title="이름 입력"></div></td>
                        <th scope="row"><div>필수</div></th>
                        <td>
                          <div class="check_form">
                            <input type="checkbox" id="required" name="required" value="true"><label for="required"><span></span>필수여부</label>
                            <div class="fr" style="float: right;">
                              <a href="javascript:void(0)" onclick="paramDel(this)" class="btn_garbage" title="삭제">삭제</a>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">타입</div></th>
                        <td>
                          <div>
                            <select class="w100" onchange="typeClick(this);" name="type">
                                <option value="">타입을 선택하여 주세요</option>
                                <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                  <option value="${list.cdNm}">${list.cdNm}</option>
                                </c:forEach>
                            </select>
                          </div>
                        </td>
                        <th scope="row"><div class="essential">설명</div></th>
                        <td><div><input type="text" name="account" title="설명 입력"></div></td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">예제</div></th>
                        <td colspan="3"><div><input type="text" name="example" title="예제 입력"></div></td>
                    </tr>
                </tbody>
            </table>
        </div>
      </div>
    </section>
  </div>

  <!-- 공통으로 쓰이는 파라미터 폼 (body content용) -->
  <div id="paramBodyForm" style="display: none;">
    <section class="paraSection_drag ui-sortable-handle">
      <div class="inner">
        <div class="pkg_board">
            <table class="table-vw">
                <caption>API 정보 Table</caption>
                <colgroup>
                    <col style="width:15%;">
                    <col style="width:35%;">
                    <col style="width:15%;">
                    <col style="width:35%;">
                </colgroup>

                <tbody>
                    <tr>
                        <th scope="row"><div class="essential">이름</div></th>
                        <td><div><input type="text" name="name" title="이름 입력"></div></td>
                        <th scope="row"><div>필수</div></th>
                        <td>
                          <div class="check_form">
                            <input type="checkbox" id="required" name="required" value="true"><label for="required"><span></span>필수여부</label>
                            <div class="fr" style="float: right;">
                              <a href="javascript:void(0)" onclick="paramBodyDel(this)" class="btn_garbage" title="삭제">삭제</a>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">타입</div></th>
                        <td>
                          <div>
                            <select class="w100" onchange="typeBodyClick(this);" name="type">
                                <option value="">타입을 선택하여 주세요</option>
                                <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                  <option value="${list.cdNm}">${list.cdNm}</option>
                                </c:forEach>
                            </select>
                          </div>
                        </td>
                        <th scope="row"><div class="essential">설명</div></th>
                        <td><div><input type="text" name="account" title="설명 입력"></div></td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">예제</div></th>
                        <td colspan="3"><div><input type="text" name="example" title="예제 입력"></div></td>
                    </tr>
                </tbody>
            </table>
        </div>
      </div>
    </section>
  </div>

  <!-- 공통으로 쓰이는 파라미터 폼 (body content용) (응답 파라미터일 경우)-->
  <div id="paramBodyExampleForm" style="display: none;">
    <section class="paraSection_drag ui-sortable-handle">
      <div class="inner">
        <div class="pkg_board">
            <table class="table-vw">
                <caption>API 정보 Table</caption>
                <colgroup>
                    <col style="width:15%;">
                    <col style="width:35%;">
                    <col style="width:15%;">
                    <col style="width:35%;">
                </colgroup>

                <tbody>
                    <tr>
                        <th scope="row"><div class="essential">이름</div></th>
                        <td><div><input type="text" name="name" title="이름 입력"></div></td>
                        <th scope="row"><div>필수</div></th>
                        <td>
                          <div class="check_form">
                            <input type="checkbox" id="required" name="required" value="true"><label for="required"><span></span>필수여부</label>
                            <div class="fr" style="float: right;">
                              <a href="javascript:void(0)" onclick="paramBodyDel(this)" class="btn_garbage" title="삭제">삭제</a>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">타입</div></th>
                        <td>
                          <div>
                            <select class="w100" onchange="typeBodyExampleClick(this);" name="type">
                                <option value="">타입을 선택하여 주세요</option>
                                <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                  <option value="${list.cdNm}">${list.cdNm}</option>
                                </c:forEach>
                            </select>
                          </div>
                        </td>
                        <th scope="row"><div class="essential">설명</div></th>
                        <td><div><input type="text" name="account" title="설명 입력"></div></td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">예제</div></th>
                        <td colspan="3"><div><input type="text" name="example" title="예제 입력"></div></td>
                    </tr>
                </tbody>
            </table>
        </div>
      </div>
    </section>
  </div>

  <!-- 공통으로 쓰이는 파라미터 폼 (body content용) (응답 파라미터일 경우)(data type 이용시)-->
  <div id="paramResBodyDataTypeForm" style="display: none;">
    <section class="paraSection_drag ui-sortable-handle">
      <div class="inner">
        <div class="pkg_board">
            <table class="table-vw">
                <caption>API 정보 Table</caption>
                <colgroup>
                    <col style="width:15%;">
                    <col style="width:35%;">
                    <col style="width:15%;">
                    <col style="width:35%;">
                </colgroup>

                <tbody>
                    <tr>
                        <th scope="row"><div class="essential">이름</div></th>
                        <td><div><input type="text" name="name" title="이름 입력"></div></td>
                        <th scope="row"><div>필수</div></th>
                        <td>
                          <div class="check_form">
                            <input type="checkbox" id="required" name="required" value="true"><label for="required"><span></span>필수여부</label>
                            <div class="fr" style="float: right;">
                              <a href="javascript:void(0)" onclick="paramBodyDel(this)" class="btn_garbage" title="삭제">삭제</a>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">타입</div></th>
                        <td>
                          <div>
                            <select class="w100" onchange="typeBodyExampleClick(this);" name="type">
                                <option value="">타입을 선택하여 주세요</option>
                                <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                  <option value="${list.cdNm}">${list.cdNm}</option>
                                </c:forEach>
                            </select>
                          </div>
                        </td>
                        <th scope="row"><div class="essential">설명</div></th>
                        <td><div><input type="text" name="account" title="설명 입력"></div></td>
                    </tr>
                </tbody>
            </table>
        </div>
      </div>
    </section>
  </div>

  <!-- 공통으로 쓰이는 파라미터 폼 (요청 본문 타입용) (data type 이용시)-->
  <div id="paramReqBodyDataTypeForm" style="display: none;">
    <section class="paraSection_drag ui-sortable-handle">
      <div class="inner">
        <div class="pkg_board">
            <table class="table-vw">
                <caption>API 정보 Table</caption>
                <colgroup>
                    <col style="width:15%;">
                    <col style="width:35%;">
                    <col style="width:15%;">
                    <col style="width:35%;">
                </colgroup>

                <tbody>
                    <tr>
                        <th scope="row"><div class="essential">이름</div></th>
                        <td><div><input type="text" name="name" title="이름 입력"></div></td>
                        <th scope="row"><div>필수</div></th>
                        <td>
                          <div class="check_form">
                            <input type="checkbox" id="required" name="required" value="true"><label for="required"><span></span>필수여부</label>
                            <div class="fr" style="float: right;">
                              <a href="javascript:void(0)" onclick="paramBodyDel(this)" class="btn_garbage" title="삭제">삭제</a>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">타입</div></th>
                        <td>
                          <div>
                            <select class="w100" onchange="typeBodyClick(this);" name="type">
                                <option value="">타입을 선택하여 주세요</option>
                                <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                  <option value="${list.cdNm}">${list.cdNm}</option>
                                </c:forEach>
                            </select>
                          </div>
                        </td>
                        <th scope="row"><div class="essential">설명</div></th>
                        <td><div><input type="text" name="account" title="설명 입력"></div></td>
                    </tr>
                    <tr>
                        <th scope="row"><div class="essential">예제</div></th>
                        <td colspan="3"><div><input type="text" name="example" title="예제 입력"></div></td>
                    </tr>
                </tbody>
            </table>
        </div>
      </div>
    </section>
  </div>

  <!-- 응답 파라미터 탭 폼 -->
  <div id="responseTabForm" style="display: none;">
    <div class="pkg_board mb10">
      <table class="table-vw">
          <caption>API 정보 Table</caption>
          <colgroup>
              <col style="width:15%;">
              <col style="width:85%;">
          </colgroup>

          <tbody>
              <tr>
                  <th scope="row"><div>상태코드</div></th>
                  <td>
                    <div>
                      <select class="w100" name="resStatus" onchange="resposeCdCng(this);">
                          <c:forEach var="item" items="${resStatusList}" varStatus="status">
                            <option value="${item.cdNm}">${item.cdNm}</option>
                          </c:forEach>
                      </select>
                    </div>
                  </td>
              </tr>
              <tr>
                  <th scope="row"><div>상태코드 설명</div></th>
                  <td><div><input type="text" name="resAccont" title="상태코드 설명 입력"></div></td>
              </tr>
          </tbody>
      </table>
    </div>
  </div>

  <!-- response Header 파라미터 폼 -->
  <div id="responseHeaderForm" style="display: none;">
    <div class="schema_wrap responseForm" style="display: none;">
      <div class="parameter_add">
        <span>header</span>
        <div class="paraDiv_drag  cid_template_first_param_root">
          <div class="div_draging">
              <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
              <button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- response Body 파라미터 폼 -->
  <div id="responseBodyForm" style="display: none;">
    <div class="schema_wrap responseForm" style="display: none;">
      <div class="parameter_add">
        <div class="body_title">
          <span>body</span>
          <div class="check_form ml20">
            <c:forEach var="item" items="${producesList}" varStatus="status">
              <input type="checkbox" id="resContentType${status.count}" name="resContentType" value="${item.cdNm}"><label for="resContentType${status.count}"><span></span>${item.cdNm}</label>
            </c:forEach>
          </div>
        </div>
        
        <div class="pkg_board mb10">
          <table class="table-vw">
              <caption>API 정보 Table</caption>
              <colgroup>
                  <col style="width:15%;">
                  <col style="width:85%;">
              </colgroup>

              <tbody>
                  <tr>
                      <th scope="row"><div>Body 설명</div></th>
                      <td>
                        <div class="txtarea_wrap"><textarea title="Body 설명 입력"></textarea></div>
                      </td>
                  </tr>
              </tbody>
          </table>
        </div>
        <button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="paramResBodyAddBtn(this)"><span>파라미터 추가</span></button>
      </div>
    </div>
  </div>

  <script src="<c:url value="/resources/js/pub/jquery-ui.min.js" />"></script>
  <script src="<c:url value="/resources/js/pub/common.js" />"></script>
  <script src="<c:url value="/resources/js/api_reg/apiGlobalScript.js" />"></script>
  <script src="<c:url value="/resources/js/pub/yaml.min.js" />"></script>

  <script>
    // 드래그 앤 드롭 이벤트
    function dragDrop() {
      $(".div_draging").sortable({
        connectWith: ".div_draging",
        handle: ".paraSection_drag",
        cancel: ".btn",
        placeholder: "drag_placeholder"
      });
      $(".div_draging").disableSelection();
    }
  </script>
</div>
</t:layout>
