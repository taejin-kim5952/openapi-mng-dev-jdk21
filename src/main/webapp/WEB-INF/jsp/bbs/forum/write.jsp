<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
//  파일명  :wirte.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 개발자 포럼 글등록  페이지
--%> 
<t:layout type="default">
<%-- <validator:javascript formName="bbsSaveVo" staticJavascript="false" xhtml="true" cdata="false"/> --%>

<script type="text/javascript">
$(function(){
	if('${pstingId}'   != ''  && '${msg} ' != ''){ // 등록 혹은 수정 이벤트 발생 시
		var btnHtm = "";
		btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnForwardView()">확인</button> ';
		fnOpenLayer(btnHtm, '작성완료', '${msg}');
		return;
	}else if('${bbsSaveVo.pstingId}'   != ''  && '${vmap.pstingId}' != null   ) { //수정 일경우 
		$('#bbsFrm').find('#pstingId').val('${bbsSaveVo.pstingId}');
	}else{
		var strMbrid = '${ssUserVo.mbrId}';
		strMbrid = strMbrid.substring(0,strMbrid.length-3);
		strMbrid = strMbrid +"***";
		$('#bbsFrm').find('#regr').text(strMbrid); // 추후 세션에서 읽어온 마스킹 유저 정보로 대체 예정
		var now = new Date();
		var year= now.getFullYear();
		var mon = (now.getMonth()+1)>9 ? ''+(now.getMonth()+1) : '0'+(now.getMonth()+1);
		var day = now.getDate()>9 ? ''+now.getDate() : '0'+now.getDate();
		$('#bbsFrm').find('#regDt').text(year+'-'+mon+'-'+day); // 금일 날짜
	}
})
//저장이나 수정 완료 후 상세보기 페이지로 이동 처리
function fnForwardView(){
	$('#frm > #bbsTypeCd').val('BBSTYP1020');
	$('#frm > #pstingId').val('${pstingId}');
	$('#frm').attr('action', c_url+'bbs/forum/mvForumView.do' ).submit(); // 상세 보기로 이동
}
//저장 레이어 오픈 
function fnLayer(){
	var btnHtm = "";
	btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnConfirm()"  id="cBtton">확인</button> ';
	btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
	fnOpenLayer(btnHtm, '글등록','<spring:message code="save.req.msg" />' );
}
//포럼글 입력 기능 구현
function fnConfirm(){
	$('#bbsFrm').find('#bbsTypeCd').val('BBSTYP1020');
	$('#bbsFrm').find('#imptYn').val('N');
	$('#bbsFrm').attr('action', c_url+'bbs/forum/saveForum.do' ).submit();
}

//목록 조회 페이지로 이동
function fnGoListPage(){
	$('#frm').attr({action:c_url+'bbs/forum/mvForumList.do', method:'post'}).submit();
}
</script>
<!-- 수정 페이지 이동 폼 -->
<form name="frm" id="frm" method="post">
	<input type="hidden" id="bbsNo" name="bbsNo" value="">
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value="">
	<input type="hidden" id="pstingId" name="pstingId" value="">
</form>
<!-- 등록 및 수정 폼 -->
<form:form modelAttribute="bbsSaveVo"  id="bbsFrm">
<form:hidden path="bbsTypeCd"  id="bbsTypeCd"  />
<form:hidden path="imptYn"  id="imptYn"  />
<form:hidden path="pstingId"  id="pstingId"  />
<div id="container">
	<div class="sVisual sv_community">
		<div>
			<h2>개발자 포럼</h2>
			<p>API에 대한 궁금한 점을  묻고 답할 수 있는 열린 공간입니다</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a>Go home</a> <span>></span> 커뮤니티 <span>></span> 개발자포럼</div>

			<div id="content">
                   <!-- comm_wrap -->
                   <div class="comm_wrap">
                       <div class="pkg_board">
                           <!-- 개발자 포럼 Write start -->
                           <section>
                               <table class="table-vw">
                                   <caption>개발자 포럼 Write Table</caption>
                                   <colgroup>
                                       <col style="width:12%;">
                                       <col style="width:37%;">
                                       <col style="width:12%;">
                                       <col style="width:37%;">
                                   </colgroup>

                                   <tbody>
                                       <tr>
                                           <th scope="row"><div>글쓴이</div></th>
                                           <td><div id="regr">${vmap.regrMasking}</div></td>
                                           <th scope="row"><div>작성일</div></th>
                                           <td><div id="regDt">${vmap.amdDt}</div></td>
                                       </tr>
                                       
                                       <tr>
                                           <th scope="row"><div>제목</div></th>
                                           <td colspan="3"><div>
                                           		<form:input path="title" size="20"  id="title"  onchange="CheckStrLength(200,'title')" onkeyup="CheckStrLength(200,'title')"/><form:errors path="title" />
<!--                                                <input type="text" name="subject_name" title="제목 입력"> -->
                                           </div></td>
                                       </tr>
                                       <tr>
                                           <th scope="row"><div>내용</div></th>
                                           <td colspan="3">
                                           		<form:textarea path="sbst" rows="16"  id="sbst" onchange="CheckStrLength(3000,'sbst')" onkeyup="CheckStrLength(3000,'sbst')"/><form:errors path="sbst" />
<!--                                                <div class="txtarea_wrap"><textarea title="내용 입력"></textarea></div> -->
                                           </td>
                                       </tr>
                                       
                                   </tbody>
                               </table>

                           </section>
                           <!-- // 개발자 포럼 Write End -->
                       </div>
                       
                       <div class="btn_set">
                           <button type="button" title="저장" class="btn btn_black" onclick="fnLayer()"><span>저장</span></button>
                           <button type="button" title="목록" class="btn" onclick="fnGoListPage()"><span>목록</span></button>
                       </div>

                   </div>
                   <!-- // comm_wrap -->
               </div>
		</div>
	</div>
</div>
</form:form>
</t:layout>
