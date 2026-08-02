function convertJsonArray(tmpStr){
	//배열로변환하기위해 필요없는문자열 삭제
	tmpStr =  tmpStr.replace(/ /g, '');
	tmpStr =  tmpStr.replace(/{/g, '');
	tmpStr = tmpStr.substring(1, tmpStr.length-2);
	tmpStr = tmpStr.split('},');
	var tmpCode,tmpName;
	var codeList = new Array();
	for(var i=0; i<tmpStr.length; i++){
		tmpCode = tmpStr[i].split(',')[0].split('=')[1];
		tmpName = tmpStr[i].split(',')[1].split('=')[1];
		codeList.push({code : tmpCode, name : tmpName})
	}
	return codeList;
};

/**
 * type이 click 이면 obj 에 들어오는 문자열을 jquery 로 인식하여 클릭 이벤트 실행시킴
 * 필요시 type을 추가하여 이벤트등을 추가해 주세요
 * */
function chkEnter( obj, type ){
	
	if( window.event.keyCode == 13 ){
		if( type == 'click' ){
			$(obj).trigger('click');
		}
	}
};

/**
 *   글자수 제한 체크
 */
function CheckStrLength(msglen , obj) {
	var temp;
	var f = $('#'+obj).val().length;  
	var tmpstr = "";
	var enter = 0;
	var strlen;
	var writeCnt =msglen;

	if (f != 0){
		for (k = 0; k < f; k++) {
			temp = $('#'+obj).val().charAt(k);
			if (escape(temp).length > 4)
				msglen -= 2;
			else
				msglen--;

			if (msglen < 0) {
//				alert("총 영문 "+writeCnt+"자 한글 "+writeCnt/2+ "자 까지 쓰실 수 있습니다.");
//				$("#popupConfirm").parent().find("div").eq(0).children("span").text("카테고리");
//				$("#popupConfirm").find('#alertTxt').html("총 영문 "+writeCnt+"자 한글 "+writeCnt/2+ "자 까지 쓰실 수 있습니다.");
//				$("#popupConfirm").dialog( "open" );
				$('#'+obj).val(tmpstr);
				var btnHtm = "";
				btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
				fnOpenLayer(btnHtm, '입력확인', "총 영문 "+writeCnt+"자 한글 "+writeCnt/2+ "자 까지 쓰실 수 있습니다.");
				break;
			} 
			else {
				tmpstr += temp;
			}
		}
	}
//	window.event.returnValue = false;
}

/**
* 천의 자리에 ',' 표시
* @param		str		데이터 (필수)
* */
function comma(str) {
	if (Number(str)) {
		return String(str).replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
	} else {
		return str;
	}
}

//-- [tag:login][drm][chg][181027][from apilink]
/**
 * 숫자만 허용
 */
$(document).on("keypress", "input:text[numberOnly]", function() {
  if((event.keyCode<48)||(event.keyCode>57)) {
    event.returnValue=false;
  }
});
/**
 * 숫자 와 콤마만 허용
 */
/*--@@@
$(document).on("keyup", "input:text[numberOnly]", function() {
  $(this).number(true);
});
--*/

//숫자와 소수점만 허용
function onlyNumDecimalInput() {
	var code = window.event.keyCode;
	if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105)
			|| code == 110 || code == 190 || code == 8 || code == 9
			|| code == 13 || code == 46) {
		window.event.returnValue = true;
		return;
	}
	window.event.returnValue = false;
}

//특수문자 치환
function ConvertSystemSourcetoHtml(str){
	 str = str.replace(/</g,"&lt;");
	 str = str.replace(/>/g,"&gt;");
	 str = str.replace(/\"/g,"&quot;");
	 str = str.replace(/\'/g,"&#39;");
	 str = str.replace(/\n/g,"<br />");
	 return str;
}

//default ajax option
//공통설정
$.ajaxSetup({
	beforeSend : function(xmlHttpRequest){
		// ajax 호출을  header에 기록
		$.blockUI();
		xmlHttpRequest.setRequestHeader('AJAX', 'true');
	}
  , complete: function(xhr, textStatus) {
		$.unblockUI();
	}
  , error: function(xhr, textStatus, error) {
    if (xhr.status=='500') {
      //--@@console.log('Login Session Expired');
    }
  }
});

//숫자만 입력 허용
$(document).on('keyup', '.numbersOnly' ,function(){
	this.value = this.value.replace(/[^0-9]/g,'');
});

//--[tag:login][drm][add] {
/**
 * 핸드폰번호 select 생성
 * @param id
 * @returns
 */
function addPhoneNum(id){
  var html = '';
  html += '<option value="010">010</option>';
  html += '<option value="011">011</option>';
  html += '<option value="016">016</option>';
  html += '<option value="017">017</option>';
  html += '<option value="018">018</option>';
  html += '<option value="019">019</option>';
  
  $('select#'+id).html(html);
}
function isEmpty(p_str) {
  if (p_str == null || p_str == "" || p_str == "undefined" || typeof p_str == "undefined") {
    return true;
  }
  return false;
}
//--[tag:login] }

//api info @@ 임시 제거 함수
function replaceAt(str){
	return str.replace(/@@/gi, ""); 
}

function lenMaxStr(str, intMax) {
	
	if(str.length > intMax){
		str = str.substring(0,intMax) + ' ...';
	}
	
	return str;
};

/* 검색 전용 문자컷 함수
 * CYD 2020.06.15
 */
function lenMaxStrSearch(str, intMax, keyword) {
	var strRepalce   = new RegExp(keyword,"gi");
	var result 	     = str.match(strRepalce);
	var newString    = str;
	var nIndexOfWord = result != null ? str.indexOf(result[0]) : str.indexOf(result);

	if(str.length > intMax){
		newString = str.substring(0,intMax) + ' ...';
	}
	
	if(nIndexOfWord > intMax) {
		newString += " ..." + str.substring(nIndexOfWord,nIndexOfWord + 10) + ' ...';
	}

	return newString;
}

//date 기본값 세팅 
function fnDateNullCheck(str, gubun) {
	
	if(!str && gubun == "start"){
		str = "1900-01-01";
	}else if(!str && gubun == "end"){
		str = "2099-12-31";
	}
	
	return str;
};

//날짜 정보 변환
function fnConverDate(str){
	if(str === null || str === undefined){
		return "null";
	}else{
		return "20" + str.replace(/\//gi,"-");
	}
}

// 특수문자 치환
function Fn_Word_Out(CheckValue) {
  if (CheckValue != "") {
    CheckValue = String(CheckValue).replace(/&amp;/g, '&');
    CheckValue = String(CheckValue).replace(/&quot;/g, '\'');
    CheckValue = String(CheckValue).replace(/&#124;/g, '|');
    CheckValue = String(CheckValue).replace(/&lt;/g, '<');
    CheckValue = String(CheckValue).replace(/&gt;/g, '>');
    CheckValue = String(CheckValue).replace(/&quot;/g, '\"');
    return CheckValue;
  }
  return Nvl(CheckValue, "");
}

//세션 저장 함수 호출
function setSession(sessionNm , sessionValue, gourl){
	var obj = new Object();
	obj.sessionNm = sessionNm;
	obj.sessionValue = sessionValue;

	$.ajax({
  	url: '/apidev/api/deploy/setSession.do', 
  	type: 'POST',
  	data: obj,
  	success: function(data){
  		
  		if(data.returnCd == "000"){
  			location.href=c_url+ gourl;
  		}else{
  			alert("값이 정상적으로 저장되지 않았습니다. 페이지 새로고침 후 다시 진행해 주시기 바랍니다.");
  		}
  	},
  	error:function(request,status,error){
      alert("code:"+request.status+"\n"+"error:"+error);
    }
	});
	
}

//-- ~Layout.jsp의 layer
function alert_message(message, title, alert_option) {
  title = (title||'알림');
  message = (message||'');
  alert_option = (alert_option||{});

  $('#popupConfirm').parent().find('div').eq(0).children('span').text(title);
  $('#popupConfirm').find('#alertTxt').html(message.replace(/\n/g, '<br>'));
  $('#popupConfirm .btn_popup_close').hide();
  $('#popupConfirm .cid_btn_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); });
  if ('function' == typeof(alert_option.ok_button_onclick)) { 
    $('#popupConfirm .btn_confirm').on('click', function() {
      alert_option.ok_button_onclick(alert_option);
    });
  }
  $('#popupConfirm').dialog({ width: (alert_option.width||'auto'), height: (alert_option.height||'auto') });
  $('#popupConfirm').dialog('open');
  $('#popupConfirm').find('button').last().focus();
}

//-- ~Layout.jsp의 layer
function confirm_message(message, title, alert_option) {
  title = (title||'선택');
  message = (message||'');
  alert_option = (alert_option||{});

  $('#popupConfirm').parent().find('div').eq(0).children('span').text(title);
  $('#popupConfirm').find('#alertTxt').html(message.replace(/\n/g, '<br>'));
  $('#popupConfirm .btn_popup_close').show();
  $('#popupConfirm .cid_btn_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); $('.apiDomainExText').remove();});
  if ('function' == typeof(alert_option.ok_button_onclick)) {
    $('#popupConfirm .btn_confirm').off('click').on('click', function() {
      //-- (false === return) 일경우 not close
      if (false !== alert_option.ok_button_onclick(alert_option)) { $('#popupConfirm').dialog('close'); $('.apiDomainExText').remove();}
    });
  }
  $('#popupConfirm').dialog({ width: (alert_option.width||'auto'), height: (alert_option.height||'auto') });
  $('#popupConfirm').dialog('open');
  $('#popupConfirm').find('button').last().focus();
}

function fn_api_deploy() {
  $( "#popup_confirmAlert" ).dialog( "close" );

  var a_apiList = [];
  //-- textarea -> apiitem array
  $('#deploy_apiList').val().split('\n').forEach(function(item) {
    var obj = {};
    try { obj = JSON.parse(item); } catch(e) {
      //-- [2023:codeeyes][empty_block issue]
    };
    if (obj.hasOwnProperty('api_no') && obj.hasOwnProperty('gw_profile') && obj.hasOwnProperty('api_nm')) {
      a_apiList.push(obj);
    }
  });
  
  var fn_cb_deploy = (function(o_ret) {
    var s_msg = '';
    var b_ret = $sf_obj_val(o_ret, 'return', false);
    if (b_ret == true) {
      var data = $sf_obj_val(o_ret, 'data');
      var o_summary_count = $sf_obj_val(data, 'summary_count');
      s_msg = '배포처리 종료.\n\n[o_summary_count: ' + $obj2str(o_summary_count) + ']';
    }
    else {
      s_msg = '배포처리 종료.\n\n[return: ' + b_ret + ']';
    }
    if (s_msg.length > 0) {
      alert_message(s_msg);
    }
    fnProc(); 
  });

  if (a_apiList.length > 0) {
    (g_vue_comp_adptranService && g_vue_comp_adptranService.proc_deploy(a_apiList, fn_cb_deploy));
  }
  //procExcuteDrmB("DEPLOY1020");
  //procExcuteDrmTc("TESTCASE");
}

function fn_api_deployDelete(gw_profile, api_no) {
  $( "#popup_confirmAlert" ).dialog( "close" );

  var fn_cb_deployDelete = (function(o_ret) {
    var s_msg = '';
    var b_ret = $sf_obj_val(o_ret, 'return', false);
    if (b_ret == true) {
      //-- data: { 'ret': call_ret, 'ret_msg': resultMessage, 'deployresult': deployresult }
      //-- call_ret: ok, nk, failed, catched
      var data = $sf_obj_val(o_ret, 'data');
      var data_ret = $sf_obj_val(data, 'ret');
      var data_ret_msg = $sf_obj_val(data, 'ret_msg');
      var data_deployresult = $sf_obj_val(data, 'deployresult');
      
      s_msg = '배포 삭제처리 종료.';
      //--[drm][test][ing]
      s_msg += '\n\n[ret: ' + data_ret + ']';
      s_msg += '\n[ret_msg: ' + data_ret_msg + ']';
      s_msg += '\n[deployresult: ' + $obj2str(data_deployresult) + ']';
    }
    else {
      s_msg = '배포 삭제처리 종료.\n\n[return: ' + b_ret + ']';
    }
    if (s_msg.length > 0) {
      alert_message(s_msg);
    }
    fnProc(); 
  });
  
  (g_vue_comp_adptranService && g_vue_comp_adptranService.proc_deploy_delete(gw_profile, api_no, fn_cb_deployDelete));
}

function fn_make_deploy_api_object(api_id , api_no , gw_profile, api_nm, proc_seq, deployapply_seq) {
  //return  { 'api_no': api_no, 'gw_profile': gw_profile, 'api_nm': api_nm, 'proc_seq'  : proc_seq , 'deployapply_seq': deployapply_seq };
  return  { 'api_id': api_id, 'api_no': api_no,  'gw_profile': gw_profile, 'api_nm': api_nm, 'proc_seq'  : proc_seq , 'deployapply_seq': deployapply_seq };
}
// --}

//모달 팝업 관련
function modalPop(popNm, popSize, btnNm){
  $(popNm).dialog({
    autoOpen: false, width: popSize, modal: true,
    buttons: [
      { text: "Ok", click: function() { $( this ).dialog( "close" ); } },
      { text: "Cancel", click: function() { $( this ).dialog( "close" ); } },
      { text: btnNm, click: function() { $( this ).dialog( "close" ); } }
    ]
  });
}

//모달 팝업 관련- 창닫기
function modalPopClose(popId){
	$( "#"+ popId ).dialog( "close" );
}

////
var alertMsg = function(pageNm, msgGb){
		var msg = "";
		if(pageNm == "approvalList"){
			if(msgGb == "rejectOk"){
				msg  = '<p>배포요청이 반려되었습니다.</p>';
				msg += '<p class="red_txt">재 배포는 개발자가 재 요청 후 가능합니다.</p>'
				msg += '<p class="red_txt">API 수정이 필요하신 경우  관리자에게 프로세스 진행 요청 하신 후 TB배포 단계 부터 진행 하셔야 합니다</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "choiceCb"){
				msg  = '<p>상용 배포를 진행할 API를 선택해 주세요</p>';
			
			}else if(msgGb == "choiceCbReject"){
				msg  = '<p>상용 배포 반려를 진행할 API를 선택해 주세요</p>';
			}
		}else if(pageNm == "deployView"){
			if(msgGb == "deployApply"){
				msg  = '<p>상용 배포 요청되었습니다.</p>';
				msg += '<p class="red_txt">요청 내용은 운영 담당자에게 메일로 전송 됩니다.</p>'
				msg += '<p class="red_txt">상용 배포는 운영담당자가 검증 내역 확인 후 진행 됩니다.</p>'
				msg += '<p class="red_txt">배포 반려 나 실패시에는 담당자에게 관련 내용이 메일로 전송됩니다.</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "editApi"){
				msg  = '<p>API수정</p>';
				msg += '<p class="red_txt"></p>'
				msg += '<p class="red_txt">수정된 규격은  관리자 승인후  TB배포를 진행하셔야 G/W에 반영됩니다.</p>'
				
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "cbDpApply"){
				msg  = '<p></p>';
				msg += '<p class="red_txt">상용 배포 요청은 검증이 완료된 후 가능합니다.</p>'
				
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "tbDeploy"){
				msg  = '<p></p>';
				msg += '<p class="red_txt">배포 진행시 GateWay서버에 바로 반영 됩니다.</p>'
				msg += '<p class="red_txt">TB배포를 진행 하시겠습니까?</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
      }
      else if(msgGb == "tbDeployDelete"){
        msg  = '<p></p>';
        msg += '<p class="red_txt">배포삭제 진행시 GateWay서버에 바로 반영 됩니다.</p>'
        msg += '<p class="red_txt">TB배포 삭제를 진행 하시겠습니까?</p>'
        msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "tbNoDpApply"){
				msg  = '<p></p>';
				msg += '<p class="red_txt">검증 요청은 TB배포가 완료된 후 가능합니다.</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}else if(msgGb == "cbApplyVerCheck"){
				msg  = '<p></p>';
				msg += '<p class="red_txt">상용 배포 요청은 버전 v1.0 에서만 가능합니다.</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}
		}else if(pageNm == "deploy"){
			if(msgGb == "deployApply"){
				msg  = '<p>상용 배포 요청되었습니다.</p>';
				msg += '<p class="red_txt">요청 내용은 운영 담당자에게 메일로 전송 됩니다.</p>'
				msg += '<p class="red_txt">상용 배포는 운영담당자가 검증 내역 확인 후 진행 됩니다.</p>'
				msg += '<p class="red_txt">배포 반려 나 실패시에는 담당자에게 관련 내용이 메일로 전송됩니다.</p>'
				msg += '<p class="blue_txt">문의메일 : apilink@kt.co.kr</p>'
			}
		}
		
		return msg;
}

//공통 팝업 띄우기 
function popCommonAlert(pageNm, msgGb, title){
  //-- using with /api/deploy/popCommonAlert.jsp :: #popup_commonAlert.popup_commonAlert
  var jq_root = $('#popup_commonAlert.popup_commonAlert');
  if (jq_root.length == 0) { return; }
      
  jq_root.find('.btn_txt').html(alertMsg(pageNm, msgGb));
  jq_root.attr('title', title);
  jq_root.dialog('open');
}

//공통 confirm
function popConfirmAlert(pageNm, msgGb, title, onclick_handler){
  //-- using with /api/deploy/popConfirmAlert.jsp :: #popup_confirmAlert.popup_confirmAlert
  var jq_root = $('#popup_confirmAlert.popup_confirmAlert');
  if (jq_root.length == 0) { return; }

  jq_root.find('.btn_txt').html(alertMsg(pageNm, msgGb));
  jq_root.attr('title', title);
  jq_root.dialog('open');
	
	var jq_okBtn_area = jq_root.find('.brd_tp span').eq(0); //-- id="okBtn"
	if (jq_okBtn_area.length == 1) {
    var html = '<button type="button" title="확인" class="btn btn_black btn_sml">확인</button>'
  	jq_okBtn_area.html(html);
    if (typeof(onclick_handler) == 'function') {
      jq_okBtn_area.find('button').on('click', onclick_handler);
    }
	}
}

function findXSSChars(input) {
    if (!input) {
        return false;
    }

	const xssPatterns = [
	  /eval\s*\(/i,
	  /alert\s*\(/i,
	  /prompt\s*\(/i,
	  /confirm\s*\(/i,
	  /settimeout\s*\(/i,
	  /setinterval\s*\(/i,
	  /src\s*=/i,
	  /href\s*=/i,
	  /onerror\s*=/i,
	  /onclick\s*=/i,
	  /onload\s*=/i,
	  /<\s*script/i,
	  /javascript\s*:/i,
	  /</,        // <
	  />/,        // >
	  /"/,        // "
	  /\$/,       // $
	  /\/'/       // /' 연속패턴
	];

    return xssPatterns.some(pattern => pattern.test(input));
}

function hasXSSAndMove(idList) {

    for (var i = 0; i < idList.length; i++) {

        var $el = $('#' + idList[i]);
        if ($el.length === 0) continue;

        var value = $el.val();

        if (findXSSChars(value)) {

            // 스크롤 이동
            $('html, body').animate({
                scrollTop: $el.offset().top - 100
            }, 400);

            // 포커스
            $el.focus();
			
			alert_message(
			    '<, >, ", $ 등 사용할 수 없는 특수문자 또는 스크립트 패턴이 포함되어 있습니다.',
			    'API그룹'
			);
			
            return false;
        }
    }

    return true;
}