<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="apiInfo">
<c:set var="dp_prop_api_no" value="${requestScope.apiNo}" />
<c:set var="dp_prop_proc_seq" value="${requestScope.procSeq}" />
<c:set var="dp_prop_gw_profile" value="${not empty param.gwProfile and (param.gwProfile eq 'TB' or param.gwProfile eq 'PROD') ? param.gwProfile : 'TB'}" />
<c:set var="dp_prop_fn_emit" value="fn_emit_verifyExecute" />
<c:set var="dp_param_vue_custom_properties" value="prop_api_no=\"${dp_prop_api_no}\" prop_proc_seq=\"${dp_prop_proc_seq}\" prop_gw_profile=\"${dp_prop_gw_profile}\" prop_fn_emit=\"${dp_prop_fn_emit}\"" />

<div id="container">
	<div class="sVisual sv_regiapi">
		<div>
			<h2>API 검증</h2>
			<p>여러분이 생각하는 모든 생각들을 API로 만들고 KT 플랫폼을 이용하여 서비스 해보세요</p>
		</div>
	</div>
	<div class="contents ">
		<div class="conBox">
			<!-- api_left -->
			<!-- // api_left -->
			<!-- api_right -->
			<div class="dum-api_right">
				<div class="pg_location">
					<a href="javascript:void(0)">Go home</a> <span>></span> API 등록
				</div>
				<div id="content">
					<!-- content 영역에 붙여야 할 부분  2019.05.20 -->
					<div class="menu_top">
						<h5 class="rTitleOneDep">API 검증</h5>
						<a class="btn btn3 btn_black"
							href="/apidev/api/deploy/mvDeployView.do">API 상세 화면</a>
					</div>

					<!--// <vue-verify-execute prop_api_no="${apiNo}" prop_proc_seq="${procSeq}"></vue-verify-execute> //-->
					<!-- embeded vue component -->
					<div>
						<jsp:include page="/WEB-INF/jsp/adptran/vue_part_mount.jsp" flush="false">
							<jsp:param name="param_vue_part" value="verifyExecute" />
							<jsp:param name="param_vue_part_only" value="N" />
							<jsp:param name="param_vue_custom_properties" value="${dp_param_vue_custom_properties}" />
						</jsp:include>
					</div>
					
					<div class="verify_exe_cont">
						<p class="title">[자동 배포를 위한  필수 검증 항목]</p>
						<ul class="lvl0_ul">
						  <li>필수 테스트 항목(CP에서 필수 테스트 항목을 수행해야 TB 검증 완료 처리)</li>
						  <li>
                <ul class="lvl1_ul">
                  <li>성공로그</li>
                  <li>
                  실패로그
		                <ul class="lvl2_ul circle">
		                  <li>비즈니스 오류</li>
		                  <li>필수파라미터 누락 점검</li>
		                </ul>
                  </li>
                  <li>
                    Lamp 응답 로그 확인
                    <ul class="lvl2_ul pd0">
                      <li>각 케이스별 연동 시스템 로그에 응답 코드 정상적으로 표시하는지 확인</li>
                    </ul>
                  </li>
                  <li>
                  마스킹 확인
                    <ul class="lvl2_ul pd0">
                      <li>마스킹된 파라미터가 있는 경우 연동 시스템 로그에서 마스킹 정상 여부 확인</li>
                    </ul>
                  </li>
                  <li>
                    endUserIp 확인
                    <ul class="lvl2_ul pd0">
                      <li>개인정보가 포함된 API 대한 endUserIp 파라미터 확인</li>
                    </ul>
                  </li>
                  <li>
                  응답시간 타이머 확인
                    <ul class="lvl2_ul pd0">
                      <li>timeout(30,000ms), threadhold-time(25,000ms), 필요시 변경.</li>
                    </ul>
                  </li>
                </ul>
						  </li>
						</ul>
					</div>
					
				</div>
				<!-- #content -->
			</div>
			<!-- .api_right -->
			<!-- // api_right -->
		</div>
		<!-- .conBox -->
	</div>
	<!-- .contents  -->
</div>
<!-- #container -->

<!-- 검증 로그   -->
<%@ include file="/WEB-INF/jsp/api/deploy/popLampLog.jsp"%>
<!-- 검증 로그 -->
<script type="text/javascript">
<%-- //--[tag:sr-20201001][add] --%>
  var ${dp_prop_fn_emit} = (function (p_cmd, p_param) {
    if ('emit_total_verify_finished' == p_cmd) {  //-- call at finish total verify finished
      p_param = (p_param||{});
      //-- { 'succ_count': succ_count, 'fail_count': fail_count, 'etc_count': etc_count, 'verifi_proc_succ_count': verifi_proc_succ_count, 'verifi_proc_fail_count': verifi_proc_fail_count, }
      var n_verifi_proc_succ_count = (p_param['verifi_proc_succ_count']||0);
      var n_verifi_proc_fail_count = (p_param['verifi_proc_fail_count']||0);
      var b_is_verifi_proc_run = ((n_verifi_proc_succ_count + n_verifi_proc_fail_count) > 0); 
      var b_is_verifi_proc_succ = (n_verifi_proc_succ_count > 0);
      var s_msg = '';
      if (true == b_is_verifi_proc_run) {
	      if (true == b_is_verifi_proc_succ) {
	        s_msg = '<b>API Studio에 등록된 API의 검증이 완료 되었습니다.\nAPI 보안 검증이 가능 하며 API 이용가이드 노출 완료 후 \nAPI 보안 검증을 진행하시면 됩니다.\n\nAPI에 대한 보안 검증은 신청하신 그룹 단위로 진행되며\nAPI를 설계한 서비스에서는 보안검증이 진행될 API 리스트와\n검증데이터를 아래 담당자에게 전달 해주셔야 합니다.\n\n보안 검증 담당자: 시큐리티아키텍쳐팀 손혜정 과장, 김재윤 대리</b>';
	      }
	      else {
          s_msg = '검증 완료처리시 오류가 발생 하였습니다.';
	      }
      }
      else {
        //--##s_msg = '검증 완료처리가 수행되지 않았습니다';
      }
      if (s_msg.length > 0) {
        alert_message(s_msg);
      }
    }
  });

  //-- LAMP로그 확인 {
  function fnLampHst() {
    var obj = new Object();
    $.ajax({
      url : '<c:url value="/api/deploy/lampLogAjax.do"/>', type : 'POST',
      data : obj, success : function(data) {
        $('.text_result').text(data.status);
        $('.text_returnMsg').text(data.response);
        var jsonDataVal = data.totRtn;
        var jsonData = JSON.parse(jsonDataVal);
        alert(JSON.stringify(jsonData, null, 4));
        $('.response').val(JSON.stringify(jsonData, null, 4));
      }, error : function(request, status, error) {
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
    $(".popLamplog").dialog("open");
  }

  $(".popLamplog").dialog({
    autoOpen: false, width: 850, modal: true, resizable: false 
  });
  //--LAMP로그 확인  }
</script>

</t:layout>
