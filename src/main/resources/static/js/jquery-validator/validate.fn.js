var cmmnValidate = {
	obj : {
		ignore : true
		,rules : ''
		,messages : ''
		,submitFunction : ''
	},
	/**
	 * 공통 validate
	 * @param targetForm : form Object
	 * 
	 * cmmnValidate.obj 설정 필요
	 * 
	 * @returns
	 */
	validate : function(targetForm){
		if($(targetForm).find(':text').length == 1){
			$(targetForm).append('<input type="text" style="display: none;" />');
		}
		
		if(cmmnValidate.obj.rules != ''){
			var rules = cmmnValidate.obj.rules;
			
			for(var key in rules){
				if(typeof rules[key].maxlength != 'undefined'){
					cmmnValidate.obj.messages[key].maxlength = $.validator.format('{0}자 내로 입력하세요.');
				}
				if(typeof rules[key].email != 'undefined'){
					cmmnValidate.obj.messages[key].email = '이메일 형식에 맞게 입력하세요.';
				}
				if(typeof rules[key].byteLength != 'undefined'){
					cmmnValidate.obj.messages[key].byteLength = '총 영문 {0}자 한글 {0/2}자 까지 쓰실 수 있습니다.';
				}
			}
		}
		
		$(targetForm).validate({
			ignore: cmmnValidate.obj.ignore ? ':hidden' : 'not:hidden',
			rules : cmmnValidate.obj.rules,
			messages : cmmnValidate.obj.messages,
			errorPlacement: function(error, element) {
			},
			invalidHandler: function(form, validator) {
				//--##fnOpenLayer(' <button type="button" title="확인" class="btn02 black btn-confirm" onclick="'+validator.errorList[0].element.focus()+'">확인</button> ', 'Alert', validator.errorList[0].message);
				validator.errorList[0].element.focus();
				fnOpenLayer('<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button>', 'Alert', validator.errorList[0].message);
			},
			submitHandler : cmmnValidate.obj.submitFunction
		});
	}	
}

/**
 * custom function
 */
// url check
$.validator.addMethod("urlCheck", function(value, element) {
	return /^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/|www\.)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/.test(value);
}, 'URL형식으로 입력하세요.');

// 영어 알파벳과 숫자, 밑줄, 하이픈
$.validator.addMethod("fileNmValid", function(value, element) {
	return value.match(/^[_A-Za-z0-9+]*$/);
}, '영어, 숫자, 밑줄, 하이픈만 입력가능합니다.');

// byte check
$.validator.addMethod("byteLength", function(value, element, param) {
	return stringByteSize(value) <= param;
}, function(param, element) {
	return '총 영문 '+param+'자 한글 '+param/2+ '자 까지 쓰실 수 있습니다.'
});

//linkUrlCheck
$.validator.addMethod("linkUrlCheck", function(value, element) {
	if(value != '') {
		return /^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/|www\.)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/.test(value);
	} else {
		if ($('#chk01').is(':checked')) {
			return /^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/|www\.)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/.test(value);
		} else {
			return true;
		}
	}	
}, 'URL형식으로 입력하세요.');

//videoUrlCheck
$.validator.addMethod("videoUrlCheck", function(value, element) {
	if($(':radio[name="mediaYn"]:checked').val() == 'Y') {
		return /^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/|www\.)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/.test(value);
	} else {
		return true;
	}
}, 'URL형식으로 입력하세요.');

//숫자만
$.validator.addMethod("numberValid", function(value, element) {
	return value.match(/^[0-9]*$/);
}, '숫자만 입력가능합니다.');

// 로그인시 사용
$.validator.addMethod("pwValid", function(value, element) {
	// 최소 8자 이상
	if(value.length < 8){
		return false;
	}
	
	// 영문자, 숫자, 특수문자 중 3종류 이상, 공백안됨 ( 사용가능 특수문자 : ` ~ ! @ $ ^ * { } | / )
//		console.log(/(?=.*\d)(?=.*[A-Za-z])(?=.*[\'\~\!\@\$\^\*\{\}\|\/])/.test(value)); // 문자가 포함되어있는지
//		console.log(/^[a-zA-Z0-9\'\~\!\@\$\^\*\{\}\|\/]+$/.test(value)); // 해당문자로만 썻는지
	return /(?=.*\d)(?=.*[A-Za-z])(?=.*[\'\~\!\@\$\^\*\{\}\|\/])/.test(value) && /^[a-zA-Z0-9\'\~\!\@\$\^\*\{\}\|\/]+$/.test(value);
}, '최소 8자 이상의 영문자, 숫자, 특수문자 중 3종류 이상 조합되어야합니다.<br>( 사용가능 특수문자 : ` ~ ! @ $ ^ * { } | / )');
$.validator.addMethod("pwCheck", function(value, element) {
	return value == $('form#loginForm').find(':password[name=pssoPw]').val();
}, '비밀번호 확인을 입력하세요.');
//-- 로그인시 사용

$.validator.addMethod("telCheck", function(value, element) {
	return /^\d{2,3}-\d{3,4}-\d{4}$/.test(value);
}, '잘못된 전화번호 입니다.');

var tel = {
	/**
	 * 
	 * @param id1 전화번호1
	 * @param id2 전화번호2
	 * @param id3 전화번호3
	 * @param name value setting name
	 * @returns
	 */
	check : function(id1, id2, id3, name){
		$(document).on($('#'+id1).prop('tagName') == 'SELECT' ? 'change' : 'keypress', '#'+id1, function(e){
			setTimeout(function() {
				tel.sum(id1, id2, id3, name);
			});
		});
		$(document).on('keypress', '#'+id2+',#'+id3, function(e){
			setTimeout(function() {
				tel.sum(id1, id2, id3, name);
			});
		});
	}
	,sum : function(id1, id2, id3, name){
		var telNo1 = '', telNo2 = '', telNo3 = '', telNo;
		
		telNo1 = $('#'+id1).val();
		telNo2 = $('#'+id2).val();
		telNo3 = $('#'+id3).val();
		
		if(typeof telNo1 != 'undefined'
			&& typeof telNo2 != 'undefined'
			&& typeof telNo3 != 'undefined'
			){
			telNo = telNo1 + '-' + telNo2 + '-' + telNo3;
			$('form').find(':hidden[name='+name+']').val(telNo);
		}
	}
	/**
	 * 전화번호 분리
	 */
	,get : function(id1, id2, id3, name){
		if(name != '' && typeof name != 'undefined'
			&& id1 != '' && typeof id1 != 'undefined'
			&& id2 != '' && typeof id2 != 'undefined'
			&& id3 != '' && typeof id3 != 'undefined'
			){
			var value = $('form').find(':hidden[name='+name+']').val();
			
			if(value != ''){
				$('#'+id1).val(value.split('-')[0]);
				$('#'+id2).val(value.split('-')[1]);
				$('#'+id3).val(value.split('-')[2]);
			}
		}
	}
}

function stringByteSize(str) {
	if (str == null || str.length == 0) {
		return 0;
	}
	var size = 0;

	for (var i = 0; i < str.length; i++) {
		size += charByteSize(str.charAt(i));
	}
	return size;
}

function charByteSize(ch) {
	if (ch == null || ch.length == 0) {
		return 0;
	}

	var charCode = ch.charCodeAt(0);

	if (charCode <= 0x00007F) {
		return 1;
	} else if (charCode <= 0x0007FF) {
		return 2;
	} else if (charCode <= 0x00FFFF) {
		return 3;
	} else {
		return 4;
	}
}
