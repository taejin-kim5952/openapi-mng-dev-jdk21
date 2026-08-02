<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div>
	<p>
		<a href="javascript:;" class="certify_phone" title="휴대폰 인증">
			<span>휴대폰 인증</span>
		</a>
	</p><p>
		<a href="javascript:;" class="certify_email" title="이메일 인증">
			<span>이메일 인증</span>
		</a>
	</p>
</div>


<script type="text/javascript">
var g_btnHtm = '<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button>';

$(document).on('click', 'a.certify_phone', function(){
	authCheck(function(data){
		if(data != null && data.authCheck != null && eval(data.authCheck)){
			fnOpenLayer(g_btnHtm, 'Alert', "<spring:message code='login.msg.endcert' />");
		}else{
			phone.openLayer();
		}
	});
}).on('click', 'a.certify_email', function(){
	authCheck(function(data){
		if(data != null && data.authCheck != null && eval(data.authCheck)){
			fnOpenLayer(g_btnHtm, 'Alert', "<spring:message code='login.msg.endcert' />");
		}else{
			email.openLayer();
		}
	});
});

function authCheck(fn){
	if($.isFunction(fn)){
		$.ajax({
			type : 'GET',
			url : '<c:url value="/login/auth/authCheck.do"/>',
			async : false,
			success : function(data){
				fn(data);
			}
		});	
	}
}

var phone = {
	openLayer : function(){
		// 휴대폰 인증
		var html = '';
		html += '<div class="mb20">';
		html += 	'<p class="txt_blit">휴대폰 인증을 위하여 입력한 정보는 항상 암호화되어 처리되며 서비스 제공사의 본인 확인용으로만 사용됩니다.</p>';
		html += 	'<p class="txt_blit">고객님의 본인 소유의 휴대폰 번호를 입력해 주시기 바랍니다</p>';
		html += 	'<p class="txt_blit">고객 요청으로 이용 차단된 휴대폰인 경우, 인증이 불가능합니다.</p>';
		html += 	'<p class="txt_blit">SMS(단문 메시지)는 시스템 사정에 따라 다소 지연될 수 있습니다.</p>';
		html += 	'<p class="txt_blit">SMS 인증번호 발송이 지연되는 경우 인증번호 재발송 버튼을 눌러주세요.</p>';
		html += '</div>';
		html += '<div class="emailInpForm">';
		html += 	'<div class="pkg_board pl0 pr0">';
		html += 		'<table class="table-vw">';
		html += 			'<caption>휴대폰 인증 Table</caption>';
		html += 			'<colgroup>';
		html += 				'<col style="width: 15%;">';
		html += 				'<col style="width: auto;">';
		html += 			'</colgroup>';
		html += 			'<tbody>';
		html += 				'<tr>';
		html += 					'<th scope="row"><div class="essential">휴대폰 번호</div></th>';
		html += 					'<td>';
		html += 						'<div class="phoneNum">';
		html += 							'<span class="w20"><select class="phoneNumber" id="phoneNo1"></select></span> ';
		html += 							'<span class="w20">';
		html += 								'<input type="text" id="phoneNo2" title="휴대번호 두번째 입력" maxlength="4" numberOnly  />';
		html += 							'</span>';
		html += 							'<span class="w20">';
		html += 								'<input type="text" id="phoneNo3" title="휴대번호 세번째 입력" maxlength="4" numberOnly />';
		html += 							'</span>';
		html += 							'<input type="hidden" id="phoneNo" value="" />';
		html += 							'<span class="w20 noBlit">';
		html += 								'<button type="button" class="btn_overlap" onclick="phone.sendSms()" title="인증번호 발송">';
		html += 									'<span>인증번호 발송</span>';
		html += 								'</button>';
		html += 							'</span>';
		html += 						'</div>';
		html += 					'</td>';
		html += 				'</tr>';
		html += 				'<tr>';
		html += 					'<th scope="row"><div class="essential">SMS 인증번호</div></th>';
		html += 					'<td>';
		html += 						'<div>';
		html += 							'<input type="text" class="inp_certify" id="certifyCode" title="SMS 인증번호 입력" maxlength="8" />';
		html += 						'</div>';
		html += 					'</td>';
		html += 				'</tr>';
		html += 			'</tbody>';
		html += 		'</table>';
		html += 	'</div>';
		html += '</div>';
		
		var btnHtm = '<button type="button" class="btn02 blue" style="display: none;" onclick="phone.confirm();" title="인증번호 확인">인증번호 확인</button> ';
			btnHtm += '<button type="button" class="btn02 red layer_close" title="취소">취소</button>';	
			
		fnOpenFormLayer(btnHtm, '휴대폰 인증', html, 880);
		addPhoneNum('phoneNo1');
	},
	sendSms : function(){
		$(':hidden[id=phoneNo]').val($('select[id=phoneNo1]').val()+'-'+$(':text[id=phoneNo2]').val()+'-'+$(':text[id=phoneNo3]').val())
		var	phoneNo = $(':hidden[id=phoneNo]').val();
		
		if(/^\d{3}-\d{3,4}-\d{4}$/.test(phoneNo)){
			$.ajax({
				type : 'POST',
				url : '<c:url value="/login/auth/smsSend.do"/>',
				data : { phoneNo : phoneNo },
				dataType : 'jsonp',
				success : function(data){
					if(data != null && data.returnType != '' && eval(data.returnType)){
						fnOpenLayer(g_btnHtm, 'Alert', '인증번호가 발송되었습니다.');
						
						if($('#popupLayer').length > 0){
							$('#popupLayer').find('.btn_wrap.lPop_bottom.brd_tp button:first').show();
						}
					}
				}
			});
		}else{
			fnOpenLayer(g_btnHtm, 'Alert', '올바른 형식이 아닙니다.');
		}
	},
	confirm : function(){
		var certifyCode = $(':text[id=certifyCode]').val().replace(/\s+/g, '');
		if(certifyCode == ''){
			fnOpenLayer(g_btnHtm, 'Alert', 'SMS 인증번호를 입력하셔야 합니다.');
		} else {
			$.ajax({
				type : 'POST',
				url : '<c:url value="/login/auth/phoneConfirm.do"/>',
				data : { certifyCode : certifyCode },
				dataType : 'jsonp',
				success : function(data){
					if(data != null && data.phoneResultType != null){
						if(!eval(data.phoneResultType)){
							fnOpenLayer(g_btnHtm, 'Alert', '인증번호를 확인 해주세요.');
						}else{
							fnOpenLayer(g_btnHtm, 'Alert', '인증이 완료되었습니다.');
							
							var phoneNo = $('form#pageForm :hidden[name=phoneNo]');
							if(phoneNo.length > 0){
								phoneNo.val($(':hidden[id=phoneNo]').val());
							}
							
							if($('#popupLayer').length > 0){
								$("#popupLayer" ).dialog( "close" );
							}
							
							//인증완료후 인증번호 수정못하게 수정
							$(':text[id=certifyCode]').attr('readonly',true);
						}
					}
				}
			});	
		}
	}
}

var email = {
	openLayer : function(){
		// 이메일 인증
		var html = '';
		html += '<div class="content_wrap" style="max-height:600px;">';
	    html += '<div class="import_wrap">';
	    html +=     '<div class="emailInpForm">';
		html +=     	'<ul>';
		html +=              '<li>';
		html +=                  '<div>';
		html +=                  	'<input type="text" id="mailId" title="Mail ID" placeholder="Mail ID" maxlength="20" />';
		html +=                  '</div>';
		html +=                  '<div class="ml10">';
		html +=                  	'<input type="text" id="mailAddr" title="이메일주소" placeholder="이메일주소" maxlength="20" onKeyPress="if (event.keyCode==13){email.sendEmail()};" />';
		html +=                  '</div>';
		html +=              '</li>';
		html +=     	'</ul>';
		html += 	'</div>';
		html += '</div>';
		html += '</div>';
		
		var btnHtm = '<button type="button" class="btn02 blue" onclick="email.sendEmail();" title="발송">발송</button> ';
			btnHtm += '<button type="button" class="btn02 red layer_close" title="취소">취소</button>';
		
		fnOpenFormLayer(btnHtm, '이메일 인증', html, 880);
		$('#mailId').focus();
	},
	sendEmail : function(){
		var mailId = $('#mailId'), mailAddr = $('#mailAddr'), value = '';
		if(mailId.val() == ''){
			fnOpenLayer(g_btnHtm, 'Alert', 'Mail ID를 입력하셔야 합니다.');
		}
		if(mailAddr.val() == ''){
			fnOpenLayer(g_btnHtm, 'Alert', '이메일주소를 입력하셔야 합니다.');
		}
		
		value = mailId.val()+'@'+mailAddr.val();
		
		if(value.match(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/)){
			$.ajax({
				type : 'POST',
				url : '<c:url value="/login/auth/mailSend.do"/>',
				data : { email : value },
				dataType : 'jsonp',
				success : function(data){
					if(data != null && data.mailResultVo.seq != ''){
						$('.layer_close').click();
						
						var target = $('form#pageForm :hidden[name=email]');
						if(target.length > 0){
							target.val(value);
						}
						
						email.confirmMail(data.mailResultVo.seq);
					}
				}
			});
		}else{
			fnOpenLayer(g_btnHtm, 'Alert', '올바른 형식의 주소가 아닙니다.');
		}
	},
	confirmMail : function(seq){
		if(seq != ''){
			var html = '<h5 class="guideTxt">메일에 첨부된 코드를 입력하세요.</h4>';
				html += '<ul class="mt10">';
				html += 	'<li class="certify_code">';
				html +=     	'<div>';
				html +=     		'<input type="text" id="certifyCode" title="인증코드 입력" placeholder="인증코드 입력" maxlength="20" />';
				html +=     	'</div>';
				html += 	'</li>';
				html += '</ul>';
			
			var btnHtm = '<button type="button" class="btn02 blue" onclick="email.confirm(\''+seq+'\');" title="확인">확인</button> ';
				btnHtm += '<button type="button" class="btn02 red layer_close" title="취소">취소</button>';
			
			fnOpenFormLayer(btnHtm, '이메일 인증', html, 880);
			$('div#popupLayer :text[id=certifyCode]').focus();
		}
	},
	confirm : function(seq){
		var certifyCode = $('div#popupLayer :text[id=certifyCode]').val();
		if(certifyCode == ''){
			fnOpenLayer(g_btnHtm, 'Alert', '인증코드를 입력하셔야 합니다.');
		} else {
			$.ajax({
				type : 'POST',
				url : '<c:url value="/login/auth/mailConfirm.do"/>',
				data : { certifyCode : certifyCode, seq : seq },
				dataType : 'jsonp',
				success : function(data){
					if(data != null && data.mailResultType != null){
						if(!eval(data.mailResultType)){
							fnOpenLayer(g_btnHtm, 'Alert', '인증번호를 확인 해주세요.');
						}else{
							fnOpenLayer(g_btnHtm, 'Alert', '인증이 완료되었습니다.');
							$("#popupLayer" ).dialog( "close" );
						}
					}
				}
			});	
		}
	}
}

</script>
