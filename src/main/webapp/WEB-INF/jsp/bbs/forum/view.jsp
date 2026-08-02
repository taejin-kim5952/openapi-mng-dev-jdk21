<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
//  파일명  :view.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 개발자 포럼 상세보기 페이지
--%> 
<t:layout type="default">
<script type="text/javascript">
//답글 입력 기능 구현
function fnRegComment(obj, pstingId , replNo , regr){
	var btnHtm = "";
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}
	var sbst = "";
	if(replNo == ''){//답글 신규 등록
		if($('#sbst').val().trim() == ''){
			btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '내용확인','<spring:message code="warning.perf.eval.comment"/>');
			return;
		}
		sbst = $('#sbst').val();
	}else{//답글 수정
		if('${ssUserVo.mbrId}' != regr){
			btnHtm =' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '권한확인','수정 권한이 존재하지 않습니다.');
			return;
		}
		if($(obj).prev().children().val().trim() == ''){
			btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '내용확인','<spring:message code="warning.perf.eval.comment"/>');
			return;
		}
		sbst = $(obj).prev().children().val();
	}
	var obj = new Object();
	obj.replNo = replNo;
	obj.pstingId  = pstingId;
	obj.sbst = sbst;
	$.ajax({
		url: '<c:url value="/bbs/forum/saveForumCommentAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			var btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnGoViewPage(\''+ '${vmap.pstingId}'+'\' , \'' +'${vmap.bbsTypeCd}'+'\')">확인</button> ';
			fnOpenLayer(btnHtm, '작성완료',data.msg);
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}

//입력된 댓글 삭제 기능 구현
function fnDelComment(obj, pstingId , replNo , regr){
	var btnHtm ="";
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}else{
		if('${ssUserVo.mbrId}' != regr){
			btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" >확인</button> ';
			fnOpenLayer(btnHtm, '권한확인','삭제 권한이 존재하지 않습니다.');
			return;
		}
	}
	btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnCmmtDel(\''+pstingId+'\' ,  \''+replNo+'\')">확인</button> ';
	btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
	fnOpenLayer(btnHtm, '삭제확인','<spring:message code="question.program.del" />');
}

//댓글 삭제
function fnCmmtDel(pstingId, replNo){
	var obj = new Object();
	obj.pstingId =pstingId;
	obj.replNo =replNo;
	$.ajax({
		url: '<c:url value="/bbs/forum/delForumCommentAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			var btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnGoViewPage(\''+ '${vmap.pstingId}'+'\' , \'' +'${vmap.bbsTypeCd}'+'\')">확인</button> ';
			fnOpenLayer(btnHtm, '작성완료',data.msg);
			return;
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}
//포럼 글 수정 페이지 이동
/* function fnGoUpdPage(pstingId){
	$("#updFrm > input[name='pstingId']").val(pstingId);
	$('#updFrm').attr({action:c_url+'bbs/forum/mvUpdForum.do', method:'post'}).submit();
} */
//포럼 글 삭제 기능 구현
function fnDelForum(){
	var btnHtm = "";
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}else{
		if('${ssUserVo.mbrId}' != '${vmap.regr}'){
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '권한확인','삭제 권한이 존재하지 않습니다.');
			return;
		}
	}
	btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnDelete()">확인</button> ';
	btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
	fnOpenLayer(btnHtm, '삭제확인','<spring:message code="question.program.del"/>');
}
//포럼 글 삭제
function fnDelete(){
	var obj = new Object();
	obj.pstingId ='${vmap.pstingId}';
	obj.bbsTypeCd = '${vmap.bbsTypeCd}';
	$.ajax({
		url: '<c:url value="/bbs/forum/delForumAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			var btnHtm =' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnGoListPage()">확인</button> ';
			fnOpenLayer(btnHtm, '삭제완료',data.msg);
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}
//목록 조회 페이지로 이동
function fnGoListPage(){
	$('#detailFrm').attr({action:c_url+'bbs/forum/mvForumList.do', method:'post'}).submit();
}
//개발자 포럼  이전 다음 
function fnGoViewPage(pstingId, bbsTypeCd){
	location.href=c_url+"bbs/forum/mvForumView.do?pstingId="+pstingId+"&bbsTypeCd="+bbsTypeCd;
/* 	$("#detailFrm > input[name='pstingId']").val(pstingId);
	$("#detailFrm > input[name='bbsTypeCd']").val(bbsTypeCd);
	$('#detailFrm').attr({action:c_url+'bbs/forum/mvForumView.do', method:'post'}).submit(); */
}

//수정 페이지 이동
function fnUdpForum(){
	var btnHtm = "";
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}else{
		if('${ssUserVo.mbrId}' != '${vmap.regr}'){
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '권한확인','수정 권한이 존재하지 않습니다.');
			return;
		}
	}
	$("#updFrm > input[name='pstingId']").val('${vmap.pstingId}');
	$("#updFrm > input[name='bbsTypeCd']").val('${vmap.bbsTypeCd}');
	$('#updFrm').attr('action', c_url+'bbs/forum/mvForumReg.do' ).submit();
}
</script>
<form id="detailFrm" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value=""/>
</form>
<form id="updFrm" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value=""/>
</form>

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
                           <!-- 개발자 포럼 View start -->
                           <section>
                               <table class="table-view">
                                   <caption>개발자 포럼 View Table</caption>
                                   <colgroup>
                                       <col style="width:120px;">
                                       <col style="width:auto;">
                                       <col style="width:160px;">
                                    </colgroup>

                                    <thead>
                                        <tr>
                                            <th scope="row" class="thTitle pl30" colspan="2"><div><c:out value="${vmap.title}" escapeXml="false"></c:out> </div></th>
                                            <th scope="row"><div>${vmap.regDt}</div></th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        <tr>
                                            <td colspan="3" class="view_con"><div><c:out value="${vmap.sbst}" escapeXml="false"></c:out></div></td>
                                        </tr>
                                        <tr>
                                            <td colspan="3">
                                                <div class="bx-comment">
                                                    <h3 class="reply_count">댓글 <span><fmt:formatNumber value="${fn:length(commentList)}" pattern="#,###" /></span></h3>
                                                    
                                                    <!-- Add wrt-fix_cmmt -->
                                                    <div class="wrt-fix_cmmt">
                                                        <div><textarea name="sbst" id="sbst" cols="" rows=""  onchange="CheckStrLength(100,'sbst')" onkeyup="CheckStrLength(100,'sbst')"></textarea></div>
                                                        <button type="button" id="" class="cmmt_add" title="댓글입력" onclick="fnRegComment( this, '${vmap.pstingId}', '' ,'')">댓글입력</button>
                                                    </div>
                                                    <!-- //Add wrt-fix_cmmt -->
                                                    
                                                    <ul>
                                                    <c:if test="${fn:length(commentList) != 0}">
                                                    	<c:forEach var="clist"  items="${commentList }" varStatus="idx">
	                                                    <li>
                                                            <div class="comt1-1">${clist.regrMasking }</div>
                                                            <div class="comt1-2"><span>${clist.regDt }</span></div>
                                                            <div class="comt1-3">
                                                                <button type="button" class="btn_edit" title="edit" ><span>댓글 수정하기</span></button>
                                                                <button type="button" class="btn_del" title="delete"  onclick="fnDelComment(this,  '${clist.pstingId}' ,'${clist.replNo}' ,'${clist.regr}')"><span>댓글 삭제하기</span></button>
                                                            </div>
                                                            <div class="comt1-4">
                                                                <span><c:out value="${clist.sbst }" escapeXml="false"></c:out></span>
                                                                <div class="wrt_cmmt" style="display:none;">
                                                                    <div><textarea name="" id="" cols="" rows=""  onchange="CheckStrLength(100,'sbst')" onkeyup="CheckStrLength(100,'sbst')"></textarea></div>
                                                                    <button type="button" id="" class="cmmt_save" title="저장"  onclick="fnRegComment( this,  '${clist.pstingId}' ,'${clist.replNo}' ,'${clist.regr}')" >저장</button>
                                                                </div>
                                                            </div>              
	                                             		</li>
	                                                	</c:forEach>
                                                    </c:if>
                                                        <script type="text/javascript">
                                                        //Comment Script
                                                        $(function(){
                                                        	  $(".comt1-3 .btn_edit").on("click", function () {
                                                        		  if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
                                                        				var btnHtm = "";
                                                        				btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
                                                        				btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
                                                        				fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
                                                        				return;
                                                        			}
		                                                           	if($(this).parent().parent().find('.comt1-4 span').css('display') != 'none'){
		                                                           		$(this).parent().parent().find('.comt1-4 .wrt_cmmt').find('textarea').val($(this).parent().parent().find('.comt1-4 span').text());
		                                                            	$(this).parent().parent().find('.comt1-4 span').css('display','none');
		                                                                $(this).parent().parent().find('.comt1-4 .wrt_cmmt').css('display','block');
		                                                           	}else{
			                                                           	//--@@console.log('data2 ::' + $(this).parent().parent().find('.comt1-4 span').text());
		                                                           		$(this).parent().parent().find('.comt1-4 .wrt_cmmt').css('display','none');
		                                                           		$(this).parent().parent().find('.comt1-4 span').css('display','block');
		                                                           	}
                                                              });
                                                        });
                                                        </script>  
                                                    </ul>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                    
                                    <tfoot>
                                        <tr>
                                            <th><div><span class="prev_txt">이전글</span></div></th>
                                            <c:choose>
                                                 <c:when test="${vmap.prevPstingId == 0  }">
	                                            	 <td colspan="2"><div>이전 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.prevPstingId}' ,'${ vmap.bbsTypeCd}');" title="${vmap.prevTitle }">${vmap.prevTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                        </tr>
                                        <tr>
                                            <th><div><span class="next_txt">다음글</span></div></th>
                                                <c:choose>
                                                 <c:when test="${vmap.nextPstingId == 0  }">
	                                            	 <td colspan="2"><div>다음 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.nextPstingId}','${ vmap.bbsTypeCd}');" title="${vmap.nextTitle }">${vmap.nextTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                        </tr>
                                    </tfoot>
                                </table>

                            </section>
                            <!-- // 개발자 포럼 View End -->
                       </div>
                       
                       <div class="btn_set">
                           <c:if test="${ssUserVo.mbrId ne null}">
                            <c:if test="${ssUserVo.mbrId  eq  vmap.regr }">
	                       		<button type="button" title="수정" class="btn" onclick="fnUdpForum()"><span>수정</span></button>
	                           	<button type="button" title="삭제" class="btn" onclick="fnDelForum()"><span>삭제</span></button>
	                       	 </c:if>
                           </c:if>
                           <button type="button" title="목록" class="btn btn_black"  onclick="fnGoListPage()"><span>목록</span></button>
                       </div>
                   </div>
                   <!-- // comm_wrap -->
               </div>
		</div>
	</div>
</div>
</t:layout>
