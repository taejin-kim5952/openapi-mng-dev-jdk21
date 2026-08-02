<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<t:layout type="apiReg">
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

<script type="text/javascript">
  $(document).ready(function () {
    // dataType Set
    dataTypeSet();

    // 좌측상단 타이틀 세팅
    $(".default_info").children("p").text(yamlOb.info.title);
    $(".default_info").children("p").attr("title", yamlOb.info.title);
    
    <%-- //-- [tag:adpt][add][for import xlsx] --%>
    fn_ui_importXlsx();
  });
	
  function cataInfoSave() {
    //-- API그룹{저장}
    if (fn_check_regform_action('cateInfoRegForm:cataInfoSave') == false) {
      return false;
    }
	
	if (!hasXSSAndMove(['ctgryNm','ctgryDesc'])) {
	    return false;
	}
	
    <%-- //--[tag:adpt][add] --%>
    errCountReset();

    // 에러 내용 삭제
    $(".err_tooltip").find("dd").remove();
    $('.err_count').find('em').text(errorNum);

    // 값 검사 시작
    errCountCk($("#ctgryNm"), "ctgryNm", true);

    // 중복된 API그룹명 조회
    var param = {
      apiSpcNo: $("#pApiSpcNo").val(),
      apiCtgryNo: $("#pApiCtgryNo").val(),
      ctgryNm: $("#ctgryNm").val()
    };
    $.ajax({
      url: '<c:url value="/api/reg/selApiCateNmCheckAjax.do"/>',
      type: 'POST',
      data: param,
      cache: false,
      async: false,
      success: function (data) {
        var errCheck = errorText.indexOf("cateNmDup");
        if (data.duplYn == "Y") {
          if (errCheck == -1) {
            errorNum = errorNum + 1; $(".err_count").find("em").text(errorNum);
            errorText.push("cateNmDup");
          }
        }
        else {
          if (errCheck != -1) {
            errorText.splice(errorText.indexOf("cateNmDup"), 1);
          }
        }
      },
      error: function (request, status, error) {
        err_message(status, error);
      }
    });
    // 에러 내용 추가
    errTextAppend();

    if (errorNum > 0) {
      err_on();
      var offset = $("#container").offset();
      $('html, body').animate({ scrollTop: offset.top }, 400);
      return false;
    }
    else {
      $('.err_tooltip').css("display", "none");
      $('.err_count').css("display", "none");
    }
    if ($("#ctgryNm").val() != $("#pApiCtgryNm").val()) {
      if ($("#pApiCtgryNm").val() != "") {
        // 기존 API그룹 수정
        yamlOb['x-category'][$("#ctgryNm").val()] = yamlOb['x-category'][$("#pApiCtgryNm").val()];
        delete yamlOb['x-category'][$("#pApiCtgryNm").val()];
      }
      else {
        // 신규 API그룹 추가
        yamlOb['x-category'][$("#ctgryNm").val()] = new Object();
      }
    }
    // console.log("yamlOb", yamlOb);
    // Object를 yaml형식의 문자열로 파싱
    var yamlStr = YAML.stringify(yamlOb);
	
    // 값 검사 끝
    /** 저장을 위해 param에 담은뒤 ajax 호출   ==========>   ***/
    var param = {
      apiSpcNo: $("#pApiSpcNo").val(), // '' 이면 등록, 있으면 수정
      apiCtgryNo: $("#pApiCtgryNo").val(), // '' 이면 등록, 있으면 수정
      ctgryNm: $("#ctgryNm").val(), // API그룹 명
      ctgryDesc: $("#ctgryDesc").val(), // API그룹 설명
      yamlStr: yamlStr // yaml 문자열
    };
    $.ajax({
      url: '<c:url value="/api/reg/savApiCateInfoAjax.do"/>',
      type: 'POST',
      data: param,
      cache: false,
      async: false,
      success: function (data) {
		
		var alert_option = {};
		
        if("1" == data.returnCode){
			$("#pApiCtgryNo").val(data.info.apiCtgryNo);
			$("#pApiCtgryNm").val($("#ctgryNm").val());
			// yaml값 세팅
			$("#yamlSbst").val(yamlStr);
			// 레프트 메뉴 세팅
			XLeftMenuSet(yamlOb['x-category']);
			if (data.info.successStr == "ins") {
			//var cateLiHtml = "";
			//레이어 메세지 적용
			alert_message('<spring:message code="api.req.save.msg" />', 'API그룹');
			} else {
				//레이어 메세지 적용
				alert_message('<spring:message code="api.req.update.msg" />', 'API그룹');
			}
			isChange = false; // 페이지 이동 체크 여부 
		}else{
			alert_message('<, >, ", $ 등 사용할 수 없는 특수문자 <br>또는 스크립트 패턴이 포함되어 있습니다.', 'API', alert_option);
			delete yamlOb['x-category'][$("#ctgryNm").val()];  //실패 시 해당 내용이 yaml에 쌓이지 않도록 삭제처리
		}
        
      },
      error: function (request, status, error) {
        err_message(status, error);
      }
    });
  }

  <%-- //-- [tag:adpt][add][for import xlsx] --%>
  //-- import xlsx {
  function fn_is_cateInfo_Loaded() {
    var apiSpcNo = ($("#pApiSpcNo").val()||'');
    var apiCtgryNo = ($("#pApiCtgryNo").val()||'');
    var ctgryNm = ($("#ctgryNm").val()||'');
    return ((apiSpcNo.length > 0) && (apiCtgryNo.length > 0) && (ctgryNm.length > 0));
  }
  function fn_ui_importXlsx() {
    //-- 엑셀 등록하기 button show/hide
    var b_is_btn_importXlsx_show = (true == fn_is_cateInfo_Loaded());
    b_is_btn_importXlsx_show &= (true == g_is_PrivateApi); 
    b_is_btn_importXlsx_show &= (false == g_is_Aasenal); 
    (b_is_btn_importXlsx_show ? $('.cid_btn_importXlsx').show() : $('.cid_btn_importXlsx').hide());
  }
  function importXlsx() {
    //-- API그룹{엑셀등록하기}
    if (fn_check_regform_action('cateInfoRegForm:importXlsx') == false) {
      return false;
    }
    if (false == fn_is_cateInfo_Loaded()) {
      alert_message('API그룹 추가중 에서는 사용하실 수 없습니다.', 'API그룹');
      return false;
    }

    var param = {
      'apiSpcNo': $("#pApiSpcNo").val(),
      'apiCtgryNo': $("#pApiCtgryNo").val(),
      'ctgryNm': $("#ctgryNm").val(),
      'yamlOb': yamlOb,
    };
    var fn_cb_importXlsx = (new Function());
    fn_on_click_importXlsx(param, fn_cb_importXlsx);
  }
  //-- import xlsx }
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
              <h5 class="rTitleOneDep">API그룹 <a href="javascript:void(0)" title="API 등록하는 방법보기" class="rtit_btn" onclick="showApiMV(this, '.mv-wrap');return false;">API 등록하는 방법보기</a></h5>
              <div class="btn_RT">
                <%-- //-- [tag:adpt][add][for import xlsx] --%>
                <button type="button" class="btn btn_sml btn_black cid_btn_importXlsx" onclick="importXlsx()" title="엑셀 등록하기"><span>엑셀 등록하기</span></button>
                <button type="button" title="취소" class="btn btn_sml" onClick="history.back()"><span>취소</span></button>
                <button type="button" title="저장" class="btn btn_sml btn_black" onclick="cataInfoSave();"><span>저장</span></button>
              </div>

              <div class="rightConBoxing">
                <!-- accordian active type -->
                <ul class="acco_opened">
                  <!-- API그룹 정보 -->
                  <li>
                    <article class="tooltip"></article>
                    <!-- tooltip -->
                    <dl class="tooltiptext">
                      <dt>API그룹</dt>
                      <dd>공통된 API를 그룹화하여 묶어주는 API그룹의 정보를 입력하세요.</dd>
                    </dl><!-- // tooltip -->
                    <!-- active bar -->
                    <div>
                      <a class="active acco_act" href="javascript:void(0)" title="API그룹 정보"><span>API그룹 정보</span></a>
                    </div><!-- // active bar -->

                    <!-- slide Content -->
                    <div class="hidden_div" style="display:block;">
                      <!-- style="display:block;" -->
                      <div class="pkg_board">
                        <!-- table start -->
                        <section>
                          <table class="table-vw">
                            <caption>table Table</caption>
                            <colgroup><col style="width:20%;"><col style="width:80%;"></colgroup>
                            <tbody>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>API그룹 제목
                                    <!-- tooltip -->
                                    <dl class="tooltiptext">
                                      <dt>API그룹 제목</dt>
                                      <dd>API를 그룹화하여 묶어주는 API그룹의 제목을 입력하세요.</dd>
                                    </dl><!-- // tooltip -->
                                  </div>
                                </th>
                                <td>
                                  <div>
                                    <input type="text" id="ctgryNm" title="API그룹 제목 입력" value="${fn:escapeXml(param.apiCtgryNm)}">
                                    <p class="red_txt">API그룹 제목을 입력하세요.</p>
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div>
                                    <article class="tooltip"></article>설명
                                    <!-- tooltip -->
                                    <dl class="tooltiptext">
                                      <dt>설명</dt>
                                      <dd>API를 그룹화하여 묶어주는 API그룹의 요약정보를 입력하세요.</dd>
                                    </dl><!-- // tooltip -->
                                  </div>
                                </th>
                                <td>
                                  <div class="txtarea_wrap">
                                    <textarea title="설명 입력" id="ctgryDesc" onchange="apiRegCheckStrLength(4000,'ctgryDesc')" onkeyup="apiRegCheckStrLength(4000,'ctgryDesc')"><c:out value='${cate.ctgryDesc}' /></textarea>
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>

                        </section><!-- // table End -->
                      </div>
                    </div><!-- // slide Content -->
                  </li><!-- // API그룹 정보 -->
                </ul>
                <div class="btn_set">
                  <button type="button" title="취소" class="btn btn_sml" onClick="history.back()"><span>취소</span></button>
                  <button type="button" title="저장" class="btn btn_sml btn_black" onclick="cataInfoSave();"><span>저장</span></button>
                </div>
              </div>

            </div><!-- // api_right -->
          </div><!-- // regist_layout -->

        </div><!-- // regist_wrap -->
      </div>
    </div>
  </div>
</div>

<dl class="err_tooltip" style="display: none; top: 60px; left: 2040.97px;">
  <dt>다음과 같은 오류가 발생하였습니다.</dt>
</dl>

<!-- API 추가 안내 -->
<div id="cate_popup_stocked" class="pop_apiAdd" title="API 추가 안내" style="display:none;">
  <!--  popup content Start  -->
  <div class="popup_content">
    <div class="content_wrap" style="max-height:none;">
      <div><img src="<c:url value='/resources/images/common/bg/guidepop02.png'/>" alt="API그룹이 추가 되었습니다. 다음은 API를 추가 하십시오.<좌측 메뉴에서 [API 추가] 버튼을 클릭하여도 추가할 수 있습니다.>"></div>
    </div>
    <p class="etc_text">API를 추가 하시겠습니까?</p>
    <div class="lPop_bottom brd_tp">
      <button type="button" title="API 추가하기" class="btn btn_black btn_confirm" onclick="mvPage('api');">API 추가하기</button>
      <button type="button" title="닫기" class="btn  btn_popup_close">닫기</button>
    </div>
    <div class="chk_agree ar mt20">
      <a href="javascript:void(0)">
        <input type="checkbox" id="noview1" name="noview1" title="7일간 열지 않음" onclick="cookieSetInfo(this, 'catePopDel');">
        <label for="noview1"><span></span>7일간 열지 않음</label>
      </a>
    </div>
  </div>
</div>

<!--// popup script -->
<script>
  $(document).ready(function () {
    $(".pop_apiAdd").dialog({
      autoOpen: false, width: 475, modal: true, resizable: false
    });

    // API 추가 안내
    $(".btn_confirm, .btn_popup_close").click(function (event) {
      $("#cate_popup_stocked").dialog("close");
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
