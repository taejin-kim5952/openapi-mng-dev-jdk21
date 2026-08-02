<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - 중복 로그인">
<script type="text/javascript">
$(document).ready(function(){
	var btnHtm ='<button type="button" title="확인" class="btn02 blue" onclick="goDup()"  id="cBtton">확인</button> ';
 		btnHtm+=' <button type="button" title="취소" class="btn02 red layer_close" onclick="cancel()">취소</button> ';
 	fnOpenLayer(btnHtm, '중복 로그인','<spring:message code="dup.id.msg" />' );
});

function fnGoDupPage(){
	//--###$('#dupf').attr({action:c_url+'login/loginSuccess.do', method:'post'}).submit();
	$('#dupf').attr({action:c_url+'login/login_success.do', method:'post'}).submit();
}

function goDup(){
	$('#dupf > #dupChkYn').val('Y');
	fnGoDupPage();
}

function cancel(){
	$('#dupf > #dupChkYn').val('N');
	fnGoDupPage();
}

$('div.ui-dialog-titlebar').find('button').click(function(e){
	cancel();
});
</script>

<form name="dupf" id="dupf" method="post">
    <input type="hidden" id="dupChkYn" name="dupChkYn" value="" />
    <input type="hidden" name="userId" value="${userId}" />
    <input type="hidden" name="pssoPw" value="${pssoPw}" />
</form>
</t:layout>

