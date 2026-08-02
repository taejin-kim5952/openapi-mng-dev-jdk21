<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%-- 
    /*-- [ref]
    model.addAttribute("userJoinVO", userJoinVO);
    //-- {나의 보유 권한} // autId, autNm, sysId, sysNm, sttusCd, autApvDt, autApvr
    model.addAttribute("autList", autList);
    //-- {권한 요청 상태} // autId, autNm, sysId, sysNm, sttusCd, autApvDt, autApvr
    model.addAttribute("aReqList", aReqList);
    //-- 시스템 목록
    model.addAttribute("sysSelBox", sysSelectBox);
    //-- [i]beastApiCountInfo {'nlist':, 'tb_deploy_ok_cnt':, 'verify_ing_cnt':, 'prd_deploy_req_cnt':, 'prd_deploy_ok_cnt': }
    model.addAttribute("beastApiCountInfo", map_ret);
    --*/ 
--%>
<t:layout type="default" title="KT Open API - 마이페이지">
<script>
  $(document).ready(function() {
    fn_init_handler();
    fn_init_dialog();
    fn_init_page();
  });
  function fn_init_handler() {
    $('[name="sysChoice"]').change(function() {
      fn_onchange_sysChoice();
    });
    $('#picker_before').on('change', function() {
      $('#picker_after').datepicker('option', 'minDate', this.value);
    });
    $('#picker_after').on('change', function() {
      $('#picker_before').datepicker('option', 'maxDate', this.value);
    });
  }
  function fn_init_dialog() {
    $('.pop_return').dialog({
      autoOpen: false, width: 680, modal: true, resizable: false
    });
    $('.pop_return .btn.btn_popup_cancel').click(function(p_evt) {
      p_evt.preventDefault();
      $('.pop_return').dialog('close');
    });    
  }
  function fn_init_page() {
    $('#picker_before').datepicker({
      defaultDate: '+1w', changeMonth: false, numberOfMonths: 1 // changeYear: true,
    });
    $('#picker_after').datepicker({
      defaultDate: '+1w', changeMonth: false, numberOfMonths: 1 // changeYear: true,
    });
    $.datepicker.setDefaults({
      dateFormat: 'yy.mm.dd',
      prevText: '이전 달',
      nextText: '다음 달',
      monthNames: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
      monthNamesShort: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
      // dayNames: ['일', '월', '화', '수', '목', '금', '토'],
      // dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
      dayNamesMin: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      showMonthAfterYear: true,
      yearSuffix: '.',
      showOtherMonths: true
      // selectOtherMonths: true
    });
    
    //-- [i][set custom scroll]
    $('.scrollCustom').mCustomScrollbar({
      theme: 'minimal-dark',
      axis: 'y',
      scrollInertia: 500,
      autoHideScrollbar: false,
      alwaysShowScrollbar: 2,
    });

    var s_msg = '${msg}';
    if (s_msg.length > 0){
      alert_message(s_msg, '권한확인');
    }
  }
</script>

<script>
  function fn_ui_set_duration_month(month) {
    // 날짜 계산 함수 
    var lfn_getCalculatedDate = function(iYear, iMonth, iDay, seperator) {
      var gdCurDate = new Date();
      gdCurDate.setYear(gdCurDate.getFullYear() + iYear);
      gdCurDate.setMonth(gdCurDate.getMonth() + iMonth);
      gdCurDate.setDate(gdCurDate.getDate() + iDay);
  
      var giYear = gdCurDate.getFullYear();
      var giMonth = gdCurDate.getMonth()+1;
      var giDay = gdCurDate.getDate();
  
      giMonth = '0' + giMonth;
      giMonth = giMonth.substring(giMonth.length - 2, giMonth.length);
      giDay   = '0' + giDay;
      giDay   = giDay.substring(giDay.length-2, giDay.length);
      return giYear + seperator + giMonth + seperator +  giDay;
    };
    
    var year = parseInt(month / 12);
    month = (month % 12); 
    $('#picker_before').val(lfn_getCalculatedDate(0, 0, 0,'.'));
    $('#picker_after').val(lfn_getCalculatedDate(year, month, 0,'.'));
  }

  //-- 반려 팝업
  var backStatus = function(autId){
    var param = { 'autId': autId };
    $.ajax({
      url: '<c:url value="/mypage/selBackViewAjax.do"/>', 
      type: 'POST',
      data: param,
      success: function(data) {
        var b_is_valid_data = $has_own(data, 'backMap');
        if (!b_is_valid_data) { alert_message('유효하지 않은 처리 결과 입니다.');  return; }

        var backMap = $sf_obj_val(data, 'backMap');
        var autApvRcessSbst = backMap.autApvRcessSbst;
        var html = '';
        if (null == autApvRcessSbst) {
          html += '<p>반려사유가 작성되지 않았습니다.<p>';
        }
        else {
          html += '<p>' + autApvRcessSbst + '<p>';
        }
        if (html.length > 0) {
          var alert_option = { 'width': 600 };
          alert_message(html, '반려 사유', alert_option);
        }
      },
      error: function(request, status, error) {
	      //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }
  
  //-- 권한 등록 
  var autInsert = function(){
    if ($is_empty($('select[name="sysChoice"]').val())) {
      alert_message('<spring:message code="mypage.aut.sys" />', '시스템 미선택');
      $('select[name="sysChoice"]').focus();
      return;
    }
    if ($is_empty($('select[name="autChoice"]').val())) {
      alert_message('<spring:message code="mypage.aut.autg" />', '권한 미선택');
      $('select[name="autChoice"]').focus();
      return;
    }

    var date_before = $('#picker_before').val();
    var date_after = $('#picker_after').val();
    if ($is_empty(date_before)) {
      alert_message('<spring:message code="mypage.aut.startDate" />', '권한 시작일 미입력');
      return;
    }
    if ($is_empty(date_after)) {
      alert_message('<spring:message code="mypage.aut.endDate" />', '권한 종료일 미입력');
      return;
    }
  
    var datePattern = /[0-9]{4}.[0-9]{2}.[0-9]{2}/;
    if (!datePattern.test(date_before)) {
      alert_message('<spring:message code="mypage.aut.formError" />', '날짜 형식 오류');
      $('#picker_before').val('');
      return;
    }
    if (!datePattern.test(date_after)) {
      alert_message('<spring:message code="mypage.aut.formError" />', '날짜 형식 오류');
      $('#picker_after').val('');
      return;
    }
    if (date_before > date_after){
      alert_message('<spring:message code="mypage.date.less" />', '날짜 형식 오류');
      $('#picker_before, #picker_after').val('');
      return;
    }

    var alert_option = { ok_button_onclick : (function() { mInsAlert(); }) };
    confirm_message('<spring:message code="mypage.aut.add" />', '권한 요청', alert_option);
  }

  function mInsAlert(){
    var param = {
      'usePerdStDt': $("#picker_before").val(),
      'userPerdFndDt': $("#picker_after").val(), 
      'autId': $("select[name=autChoice]").val()
    };
    $.ajax({
      url: '<c:url value="/mypage/autInsertAjax.do"/>', 
      type: 'POST',
      data: param,
      success: function(data) {
        var b_is_valid_data = $has_own(data, 'msgCd');
        if (!b_is_valid_data) { alert_message('유효하지 않은 처리 결과 입니다.');  return; }

        var msgCd = $sf_obj_val(data, 'msgCd');
        var message = $sf_obj_val(data, 'message');
        if ('success' == msgCd) {
          if (message.length > 0) {
            var alert_option = { ok_button_onclick : (function() { window.location.reload(); }) };
            alert_message(message, '저장완료', alert_option);
          }
          else {
            location.reload();
          }
        }
        else if ('fail' == msgCd) {
          alert_message(message, '저장실패', alert_option);
        }
      },
      error: function(request, status, error) {
	      //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }
  
  var reqAjaxPop = function(){
    //-- [i][clear input]
    $('.pop_return').find('select[name="sysChoice"], select[name="autChoice"], #picker_before, #picker_after').val('');
    $('.pop_return').dialog('open'); 
  }

  //회원정보 변경
  var joinChange = function(){
    //var joinChUrl = '';
    //joinChUrl = '<spring:eval expression="@environment.getProperty('psso.joinCh.url')" />';
    //window.open(joinChUrl, '_blank');
	
	window.open("https://apilink.kt.co.kr/");
  }

  //비밀번호 변경
  var pwordChange = function(){
	window.open("https://apilink.kt.co.kr/login/findUserPw.do");
  }

  //회원탈퇴 신청
  var delReq = function(){
    //var delReqUrl = '';
    //delReqUrl ='<spring:eval expression="@environment.getProperty('psso.jdel.url')" />';
    //window.open(delReqUrl, '_blank');
	
	fnOpenLayer('<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button>', '알림', "<b>apilink@kt.com 으로 문의 부탁드립니다.</b>");
  }
  
  function fn_onchange_sysChoice() {
    var param = { 'sysId': $('.sect_wrap option:selected').val() };
    $.ajax({
      url: '<c:url value="/mypage/selboxAjax.do"/>', 
      type: 'POST',
      data: param,
      success: function(data){
        var b_is_valid_data = $has_own(data, 'autGroup');
        if (!b_is_valid_data) { alert_message('유효하지 않은 처리 결과 입니다.');  return; }
  
        var autGroup = $sf_obj_val(data, 'autGroup');
        var autGhtml = '<option value="">권한그룹 선택</option>';
        $.each(autGroup, function(idx, item) { 
          autGhtml += '<option value="' + item.autId + '">' + item.autNm + '</option>';
        });
        $('[name="autChoice"]').html(autGhtml);
      },
      error: function(request, status, error) {
	      //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }
</script>

<div id="container">
  <div class="contents">
    <div class="conBox">
      <div id="content" class="type2">
        <div class="dashboard_top">
          <div class="dash_user">
            <span class="name"><strong>${userJoinVO.mbrNm}<!-- 홍길동 --></strong>님,</span>
            <p class="desc">
              <strong>API 등록현황</strong>을<br>확인하세요.
            </p>
          </div><!-- // dash_user -->

          <div class="dash_status">
            <ul class="list">
              <li>
                <a href="javascript:void(0)" class="ibox">
                  <span class="txt">TB 배포완료</span>
                  <em class="num"><strong>${beastApiCountInfo.tb_deploy_ok_cnt}</strong>건</em>
                </a>
              </li>
              <li>
                <a href="javascript:void(0)" class="ibox ty2">
                  <span class="txt">검증중</span>
                  <em class="num"><strong>${beastApiCountInfo.verify_ing_cnt}</strong>건</em>
                </a>
              </li>
              <li>
                <a href="javascript:void(0)" class="ibox ty3">
                  <span class="txt">상용배포 대기</span>
                  <em class="num"><strong>${beastApiCountInfo.prd_deploy_req_cnt}</strong>건</em>
                </a>
              </li>
              <li>
                <a href="javascript:void(0)" class="ibox ty4">
                  <span class="txt">상용배포 완료</span>
                  <em class="num"><strong>${beastApiCountInfo.prd_deploy_ok_cnt}</strong>건</em>
                </a>
              </li>
            </ul>
          </div><!-- // dash_status -->
        </div><!-- .dashboard_top -->

        <%-- //-- [tag:PRJ-20220901][for simple api reg] --%>
        <%@ include file="/WEB-INF/jsp/mypage/apiTrafficGraph_inc.jsp" %>

        <div class="dash_btm">
          <div class="left_area">
            <div class="sub_tit_area">
              <div class="left_area">
                <h2 class="stit">MY 권한관리</h2>
              </div>
            </div><!-- // sub_tit_area -->

            <div class="my_box_area">
              <ul class="my_list_box">
                <li>
                  <div class="my_box">
                    <span class="tit">나의 보유 권한</span>
                    <div class="scrollCustom" style="max-height:210px;">
                      <c:if test="${fn:length(autList) > 0}">
                        <ul class="infobox ty2">
                          <c:forEach var="item" items="${autList}" varStatus="status">
                            <li>
                              <div class="mbox">
                                <div class="left">
                                  <span title="${item.sysNm}">${item.sysNm}</span>
                                  <em title="${item.autNm}">${item.autNm}</em>
                                </div>
                                <div class="right"></div>
                              </div>
                            </li>
                          </c:forEach>
                        </ul>
                      </c:if>
                      <c:if test="${fn:length(autList) == 0}">
                        <div class="no_data">
                          <p>보유 권한이 없습니다.</p>
                        </div>
                      </c:if>
                    </div><!-- .scrollCustom -->
                  </div><!-- .my_box -->
                </li>
                <li>
                  <div class="my_box">
                    <span class="tit">권한 요청 상태</span>
                    <div class="scrollCustom" style="max-height:210px;">
                      <c:if test="${fn:length(aReqList) > 0}">
                        <ul class="infobox">
                          <c:forEach var="item" items="${aReqList}" varStatus="status">
                            <li>
                              <div class="mbox">
                                <div class="left">
                                  <span title="${item.sysNm}">${item.sysNm}</span>
                                  <em title="${item.autNm}">${item.autNm}</em>
                                </div>
                                <div class="right">
                                  <c:if test="${item.autSttusCd eq 'MBRAUT1010'}">
                                    <span class="r_state">진행중</span>
                                  </c:if>
                                  <c:if test="${item.autSttusCd eq 'MBRAUT1030'}">
                                    <a href="javascript:void(0)" onclick="backStatus('${item.autId}');" title="(SHUB) API 개발팀">
                                      <span class="r_state ty2">반려</span>
                                    </a>
                                  </c:if>
                                </div>
                              </div>
                            </li>
                          </c:forEach>
                        </ul>
                      </c:if>
                      <c:if test="${fn:length(aReqList) == 0}">
                        <div class="no_data">
                          <p>요청 중인 권한이 없습니다.</p>
                        </div>
                      </c:if>
                    </div><!-- .scrollCustom -->
                  </div><!-- .my_box -->
                </li>
                <li>
                  <div class="my_box2">
                    <p class="info">
                      관리자에게 <br>
                      새로운 권한을 <br>
                      요청하실 수 있습니다.
                    </p>
                    <a href="javascript:void(0)" class="m_link" onclick="reqAjaxPop()" title="권한요청"><span>권한 요청</span></a>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <div class="right_area">
            <div class="sub_tit_area">
              <div class="left_area">
                <h2 class="stit">회원정보</h2>
              </div>
            </div><!-- // sub_tit_area -->
            <ul class="main_profile_link">
              <li><a href="javascript:void(0)" onclick="joinChange();" title="회원정보 변경"><span class="ico">회원정보 변경</span></a></li>
              <li><a href="javascript:void(0)" onclick="pwordChange();" title="비밀번호 변경"><span class="ico ty2">비밀번호 변경</span></a></li>
              <li><a href="javascript:void(0)" onclick="delReq();" title="회원탈퇴 신청"><span class="ico ty3">회원탈퇴 신청</span></a></li>
            </ul>
          </div>
        </div><!-- .dash_btm -->

      </div><!-- #content -->
    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->
	
<!--// popup content 권한요청 -->
<div class="pop_return" title="권한요청">
  <div class="popup_content">
    <div class="request-auth_wrap">
      <section>
        <h5 class="lowest_tit">1. 권한그룹 및 권한 선택</h5>
        <div class="sect_wrap">
          <select title="시스템 선택" name="sysChoice">
            <option value="">시스템 선택</option>
            <c:forEach var="sysSBox" items="${sysSelBox}" varStatus="idx">
              <option value="${sysSBox.sysId}">${sysSBox.sysNm}</option>
            </c:forEach>
          </select>
          <select title="권한그룹 선택" name="autChoice">
            <option value="">권한그룹 선택</option>
          </select>
        </div>
        <div class="form_download">
          <p>* 시스템 선택시 원하는 항목이 없을 경우 아래 양식을 다운받아서 작성한 후<br> 이메일 <a href="mailto:apilink@kt.com" title="open popup" target="new">( apilink@kt.com )</a>로 보내주시면 검토하여 추가하도록 하겠습니다.</p>
          <div class="btn_set">
            <a href="<c:url value="/file/fileDownLoad.do?filePath=&downType=docx&orgFileName=authority.docx&saveFileName=authority.docx" />"
              title="양식 다운로드" class="btn btn_black" download="authority.docx">
              <span>양식 다운로드</span>
            </a>
          </div>
        </div>
      </section>

      <section>
        <h5 class="lowest_tit">2. 사용기간</h5>
        <div class="sect_wrap cal_type">
          <p><span><input type="text" id="picker_before" name="picker_before"></span><label for="picker_before"></label></p>
          <p><span><input type="text" id="picker_after" name="picker_after"></span><label for="picker_after"></label></p>
          <ol>
            <li><button type="button" title="3개월" class="btn btn_white" onclick="fn_ui_set_duration_month(3);">3개월</button></li>
            <li><button type="button" title="6개월" class="btn btn_white" onclick="fn_ui_set_duration_month(6);">6개월</button></li>
            <li><button type="button" title="1년" class="btn btn_white" onclick="fn_ui_set_duration_month(12);">1년</button></li>
          </ol>

        </div>
      </section>
    </div>

    <div class="lPop_bottom brd_tp">
      <button type="button" title="권한요청" onclick="autInsert();" class="btn btn_sml btn_black">권한요청</button>
      <button type="button" title="취소" class="btn btn_sml btn_popup_cancel">취소</button>
    </div>
  </div>
</div>
<!-- popup content 권한요청 //-->
</t:layout>
