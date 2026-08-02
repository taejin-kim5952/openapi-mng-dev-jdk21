<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - 마이페이지">
<script type="text/javascript">
$(document).ready(function(){
	if('${msg}' != null  && '${msg}' != '' ){
		var btnHtm = "";
		btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" >확인</button> ';
		fnOpenLayer(btnHtm, '권한확인', '${msg}');
		return;
	}
});

//작성중 api 
var wTabForm = function(){
	$('#selTab').val('1');
	$('#stabForm').attr("action" , "<c:url value='/api/main/mvMainList.do' />").submit();   	
}
// api 검토요청
var rTabForm = function(){
	$('#selTab').val('3');
	$('#stabForm').attr("action" , "<c:url value='/api/main/mvMainList.do' />").submit();   	
}
// api 개발 요청
var dTabForm = function(){
	$('#selTab').val('4');
    $('#stabForm').attr("action" , "<c:url value='/api/main/mvMainList.do' />").submit();
}
//개발자 포럼 상세 이동
var devFrumView = function(){
	$("#devFrum > input[name='pstingId']").val('${dMap.pstingId}');
    $('#devFrum').attr("action" , "<c:url value='/bbs/forum/mvForumView.do' />").submit();
}
//QNA 상세 이동
var qnaView = function(){
	$("#qnaFrm > input[name='qnaId']").val('${qMap.qnaId}');
    $('#qnaFrm').attr("action" , "<c:url value='/qna/mvQnaView.do' />").submit();
}
//반려 팝업
var backStatus = function(paramAut){
	var obj = new Object();
	obj.autId = paramAut;
	$.ajax({
		url: '<c:url value="/mypage/selBackViewAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			var phtml = "";
			if(data != null){
				if (data.backMap.autApvRcessSbst == null) {
					phtml += '<p>반려사유가 작성되지 않았습니다.<p>';
				}
				else {
					phtml += '<p>' + data.backMap.autApvRcessSbst + '<p>';
				}
				$('.content_wrap').html(phtml);
				backAjaxRes();
			}
		},
		error:function(request,status,error){
			//--@@console.log("code:"+request.status+"\n"+"error:"+error);
		}
	});
}

//권한 등록 
var autInsert = function(){

	if($("select[name=sysChoice]").val() == ""){
		var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '시스템 미선택','<spring:message code="mypage.aut.sys" />' );
		$("select[name=sysChoice]").focus();
		return;
	}
	
    if($("select[name=autChoice]").val() == ""){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '권한 미선택','<spring:message code="mypage.aut.autg" />' );
		$("select[name=autChoice]").focus();
    	return;
	}
	
    if($("#picker_before").val() == ""){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '권한 시작일 미입력','<spring:message code="mypage.aut.startDate" />' );
		return;
    }
    
    if($("#picker_after").val() == ""){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '권한 종료일 미입력','<spring:message code="mypage.aut.endDate" />' );
		return;
    }
	
    var datePattern = /[0-9]{4}.[0-9]{2}.[0-9]{2}/;
    var dateFirChk = '';
    var dateEndChk = '';
    
    dateChk = $("#picker_before").val();
    dateEndChk = $("#picker_after").val();
    
    if(dateChk.length > 10){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '날짜 형식 오류','<spring:message code="mypage.aut.formError" />' );
		$("#picker_before").val('');
    	return;
    }
    
    if(dateEndChk.length > 10){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '날짜 형식 오류','<spring:message code="mypage.aut.formError" />' );
		$("#picker_after").val('');
    	return;
    }
    
    if(!datePattern.test(dateChk)){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '날짜 형식 오류','<spring:message code="mypage.aut.formError" />' );
    	$("#picker_before").val('');
    	return;
    }

    if(!datePattern.test(dateEndChk)){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '날짜 형식 오류','<spring:message code="mypage.aut.formError" />' );
    	$("#picker_after").val('');
    	return;
    }
    
    var fCompare = '';
    var lCompare = '';
    
    fCompare = $("#picker_before").val();
    lCompare = $("#picker_after").val();
    
    var pattern = /[^(가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9)]/gi;   // 특수문자 제거
    fCompare = fCompare.replace(pattern, "");
    lCompare = lCompare.replace(pattern, "");

    if(fCompare > lCompare){
    	var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
		fnOpenLayer(btnHtm, '날짜 선택 오류','<spring:message code="mypage.date.less" />' );
    	$("#picker_before").val('');
    	$("#picker_after").val('');
    	return;
    }
    
    var btnHtm = "";
	btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="mInsAlert()"  id="cBtton">확인</button> ';
	btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
	fnOpenLayer(btnHtm, '권한 요청','<spring:message code="mypage.aut.add" />' );
}

function mInsAlert(){
	
	var obj = new Object();
	obj.usePerdStDt = $("#picker_before").val();
    obj.userPerdFndDt = $("#picker_after").val(); 
    obj.autId = $("select[name=autChoice]").val();
    
	$.ajax({
		url: '<c:url value="/mypage/autInsertAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			if(data.msgCd == 'success'){
				var btnHtm = "";
	    		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
	    		fnOpenLayer(btnHtm, '저장완료', data.message);
	    		location.reload();
	    		//$(select).empty().data('options');
	    		$("select[name=sysChoice]").val('');
	            $("select[name=autChoice]").val('');
	            $("#picker_before").val('');
	            $("#picker_after").val('');
	    	}else if(data.msgCd == 'fail'){
	    		var btnHtm = "";
	    		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
	    		fnOpenLayer(btnHtm, '저장실패', data.message);
	    		//$(select).empty().data('options');
	    		$("select[name=sysChoice]").val('');
	            $("select[name=autChoice]").val('');
	            $("#picker_before").val('');
	            $("#picker_after").val('');
	    	}
		},
		error:function(request,status,error){
			//--@@console.log("code:"+request.status+"\n"+"error:"+error);
      //var btnHtm = "";
  		//btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
  		//fnOpenLayer(btnHtm, '저장실패','<spring:message code="insert.fail.msg" />' );
  		//return;
    }
	});
}

var backAjaxRes = function(){
	$( ".pop_back" ).dialog( "open" );
}

var reqAjaxPop = function(){
	$( ".pop_return" ).dialog( "open" ); 
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
	fnOpenLayer('<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button>', '알림', "<b>apilink@kt.com 으로 문의 부탁드립니다.</b>");
	
}
var writeApiView = function(param){
	$("#apiFrm > input[name='apiSpcNo']").val(param);
	$('#apiFrm').attr("action" , "<c:url value='/api/reg/mvApiInfoReg.do?param' />").submit();
}
</script>
<form id="stabForm" name="stabForm" method="post">
<input type="text" id="selTab"  	name="selTab" 	value="" />
</form>
<form id="devFrum" name="devFrum" method="post">
<input type="hidden" id="pstingId" name="pstingId" value=""/>
</form>
<form id="qnaFrm" name="qnaFrm" method="post">
<input type="hidden" id="qnaId" name="qnaId" value=""/>
</form>
<form id="apiFrm" name="apiFrm" method="post">
<input type="hidden" id="apiSpcNo" name="apiSpcNo" value=""/>
</form>
<div id="container">
		<div class="sVisual sv_login">
			<div>
				<h2>마이페이지</h2>
				<p>나의 권한과 API, 커뮤니티를 확인하실 수 있습니다</p>
			</div>
		</div>
		<div class="contents">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> 마이페이지</div>

				<div id="content">
                    <div class="mypage_wrap">
                        <div>
                            <div class="mypg_tit">MY API</div>
                            <div class="mypg_cont">
                                <div>
                                    <h5>API검토요청</h5>
                                    <p><span class="ico_book">${apiRevCnt}건</span></p>
                                    <a href="javascript:;" onclick="rTabForm();" class="btn_more-mypg">API검토요청 내용 더보기</a>
                                </div>
                                <div>
                                    <h5>API개발요청</h5>
                                    <p><span class="ico_note">${apiDevCnt}건</span></p>
                                    <a href="javascript:;" onclick="dTabForm();"  class="btn_more-mypg">API개발요청 내용 더보기</a>
                                </div>
                                <div>
                                    <h5 class="bot-dot">작성중 API</h5>
                                    <div>
                                    <c:if test="${fn:length(wMap) > 0}">
                                    <p class="api_info">                                            
                                       <span>${wMap.sysId}</span>
                                       <span class="api_info-date">${wMap.amdDtStr}</span>
                                       <span class="api_info-version">버전${wMap.ver}</span>
                                    </p>
                                    <p class="api_tit"><a href="<c:url value='/api/reg/mvApiInfoReg.do?apiSpcNo=${wMap.apiSpcNo}&sysId=${wMap.sysId}' />">${wMap.apiNm}</a>  </p>
                                    <p class="api_cont">${fn:escapeXml(wMap.apiDesc)}</p>
                                    </c:if>
                                    <c:if test="${fn:length(wMap) == 0}">
                                        <div class="noData">등록된 글이 없습니다.</div>
                                    </c:if>
                                    </div>
                                    <a href="javascript:;" onclick="wTabForm();" class="btn_more-mypg">API검토요청 내용 더보기</a>
                                </div>
                            </div>
                        </div>
                        <div>
                            <div class="mypg_tit">MY 회원관리</div>
                            <div class="mypg_cont">
								<div></div>
								<div></div>
                                <div>
                                    <h5 class="no_bd">회원정보</h5>
                                    <ol>
                                        <li class="mem_info"><a href="#" onclick="joinChange();" title="회원정보 변경"><span>회원정보 변경</span></a></li>
                                        <li class="mem_pw"><a href="#" onclick="pwordChange();" title="비밀번호 변경"><span>비밀번호 변경</span></a></li>
                                        <li class="mem_drop"><a href="#" onclick="delReq();" title="회원탈퇴 신청"><span>회원탈퇴 신청</span></a></li>
                                    </ol>
                                </div>
                            </div>
                        </div>
                        <div class="myAuthority">
                            <div class="mypg_tit">MY 권한관리</div>
                            <div class="mypg_cont">
                                <div>
                                    <h5 class="bot-dot">나의 보유권한</h5>
                                    <c:if test="${fn:length(autList) > 0}">
                                      <ul>
                                        <c:forEach var="alist" items="${autList}" varStatus="idx">
                                          <li><a><span><b>(${alist.sysNm})</b> ${alist.autNm}</span></a></li>
                                        </c:forEach>
                                      </ul>
                                    </c:if>
                                    <c:if test="${fn:length(autList) == 0}">
                                    <div class="noData">보유 권한이 없습니다.</div>
                                    </c:if>
                                </div>
                                <div>
                                    <h5 class="bot-dot">권한요청 상태</h5>
                                    <c:if test="${fn:length(aReqList) > 0}">
                                      <ul class="tag_style">
                                        <c:forEach var="aRlist" items="${aReqList}" varStatus="idx">
                                          <c:if test="${aRlist.sysNm eq null}"><li><span>요청한 권한이 없습니다.</span></li></c:if>
                                          <c:if test="${aRlist.autSttusCd eq 'MBRAUT1010'}">
                                            <li>
                                              <a><span><b>(${aRlist.sysNm})</b> ${aRlist.autNm}</span></a>
                                              <em class="ing_tag">진행중</em>
                                            </li>
                                          </c:if>
                                          <c:if test="${aRlist.autSttusCd eq 'MBRAUT1030'}">
                                            <li>
                                              <a href="#" onclick="backStatus('${aRlist.autId}');" title="(SHUB) API 개발팀">
                                                <span><b>(${aRlist.sysNm})</b> ${aRlist.autNm}</span>
                                              </a>
                                              <em class="return_tag">반려</em>
                                            </li>
                                          </c:if>
                                        </c:forEach>
                                      </ul>
                                    </c:if>
                                    <c:if test="${fn:length(aReqList) == 0}">
                                      <div class="noData">요청 중인 권한이 없습니다.</div>
                                    </c:if>
                                </div>
                                <div>
                                    <div class="authority">
                                        <p>관리자에게 새로운 권한을 <br> 요청하실 수 있습니다.</p>
                                        <a href="javascript:;" title="권한요청" onclick="reqAjaxPop();" class="btn btn_black round_sty">권한요청</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>            
                </div>
			</div>
		</div>
</div>
	
<!--// popup content 반려 사유 -->
<div class="pop_back" title="반려 사유">
   <div class="popup_content">
       <div class="content_wrap">
           <p></p>
       </div>
       <div class="lPop_bottom brd_tp">
           <button type="button" title="닫기" class="btn btn_black" onclick="$('.pop_back').dialog('close')">닫기</button>
       </div>
   </div>
</div>
<!-- popup content 반려 사유 //-->

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
                    <p>* 시스템 선택시 원하는 항목이 없을 경우 이메일 <a href="mailto:apilink@kt.com" title="open popup" target="new">( apilink@kt.com )</a>로 
                    <br> 해당 내용 보내주시면 검토하여 필요 양식을 전달드리겠습니다.</p>
                    <div class="btn_set">
                        <a href="<c:url value="/file/fileDownLoad.do?filePath=&downType=docx&orgFileName=authority.docx&saveFileName=authority.docx"/>" title="양식 다운로드" class="btn btn_black">
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
                        <li><button type="button" title="3개월" class="btn btn_white" onclick="dateThree();">3개월</button></li>
                        <li><button type="button" title="6개월" class="btn btn_white" onclick="dateSix();">6개월</button></li>
                        <li><button type="button" title="1년" class="btn btn_white" onclick="dateOneYear();">1년</button></li>
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

<script>
  $(".pop_back").dialog({
      autoOpen: false, width: 600, modal: true, resizable: false
  });

  $(".pop_return").dialog({
      autoOpen: false, width: 680, modal: true, resizable: false
  });

  $( ".pop_return .btn_popup_cancel" ).click(function( event ) {
      $( ".pop_return" ).dialog( "close" );
      $("select[name=sysChoice]").val('');
      $("select[name=autChoice]").val('');
      $("#picker_before").val('');
      $("#picker_after").val('');
      event.preventDefault();
  });

  //x 버튼 클릭시 확인
  $( ".ui-dialog-titlebar-close" ).click(function( event ) {
      $("select[name=sysChoice]").val('');
      $("select[name=autChoice]").val('');
      $("#picker_before").val('');
      $("#picker_after").val('');
  });

  $( function() {
    var from = $('#picker_before').datepicker({
      defaultDate: '+1w', changeMonth: false, numberOfMonths: 1 // changeYear: true,
    }).on('change', function() {
      to.datepicker('option', 'minDate', this.value);
    });
    var to = $('#picker_after').datepicker({
      defaultDate: '+1w', changeMonth: false, numberOfMonths: 1 // changeYear: true,
    }).on('change', function() {
      from.datepicker('option', 'maxDate', this.value);
    });
    $('[name="sysChoice"]').change(function(){
    	var obj = new Object();
    	obj.sysId = $(".sect_wrap option:selected").val();
    	$.ajax({
			url: '<c:url value="/mypage/selboxAjax.do"/>', 
			type: 'POST',
			data :obj,
			success: function(data){
				var autGhtml = "";
				if(data != null  && data.autGroup.length != 0){
					autGhtml += '<option value="">권한그룹 선택</option>';
					$.each(data.autGroup, function(index, autGList) { 
						autGhtml += '<option value="'+autGList.autId+'">'+autGList.autNm+'</option>';
						/* if(index == 0){
							autGhtml += '<option value="">권한그룹 선택</option>';
						}else{
							autGhtml += '<option value="'+autGList.autId+'">'+autGList.autNm+'</option>';
    					} */
					});
					$('[name="autChoice"]').html(autGhtml);
				}else{
					autGhtml += '<option value="">권한그룹 선택</option>';
					$('[name="autChoice"]').html(autGhtml);
				}
			},
			error:function(request,status,error){
				//--@@console.log("code:"+request.status+"\n"+"error:"+error);    
			}
		});
    });
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

  //3개월 after         
  function dateThree(){
  	$("#picker_before").val(getCalculatedDate(0,0,0,'.'));
  	$("#picker_after").val(getCalculatedDate(0,+3,0,'.'));
  } 
  
  //6개월 after
  function dateSix(){
  	$("#picker_before").val(getCalculatedDate(0,0,0,'.'));
  	$("#picker_after").val(getCalculatedDate(0,+6,0,'.'));
  }
  
  //1년 after
  function dateOneYear(){
  	$("#picker_before").val(getCalculatedDate(0,0,0,'.'));
  	$("#picker_after").val(getCalculatedDate(+1,0,0,'.'));
  }

    // 날짜 계산 함수 
	function getCalculatedDate(iYear, iMonth, iDay, seperator) {
    //현재 날짜 객체를 얻어옴.
    var gdCurDate = new Date();
    //현재 날짜에 날짜 게산.
    gdCurDate.setYear( gdCurDate.getFullYear() + iYear );
    gdCurDate.setMonth( gdCurDate.getMonth() + iMonth );
    gdCurDate.setDate( gdCurDate.getDate() + iDay );
    
    //실제 사용할 연, 월, 일 변수 받기.
    var giYear = gdCurDate.getFullYear();
    var giMonth = gdCurDate.getMonth()+1;
    var giDay = gdCurDate.getDate();
    //월, 일의 자릿수를 2자리로 맞춘다.
    giMonth = "0" + giMonth;
    giMonth = giMonth.substring(giMonth.length-2,giMonth.length);
    giDay   = "0" + giDay;
    giDay   = giDay.substring(giDay.length-2,giDay.length);
    //display 형태 맞추기.
    return giYear + seperator + giMonth + seperator +  giDay;
	}

</script>

</t:layout>
