<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - Q&A">
<script type="text/javascript">
//QnA 글 삭제 기능 구현
function fnDelQna(){
	var btnHtm = "";
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}else{
		if('${ssUserVo.mbrId}' != '${vmap.regr}'){
			btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> ';
			fnOpenLayer(btnHtm, '권한확인','삭제 권한이 존재하지 않습니다.');
			return;
		}
	}
	btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnDel()">확인</button> ';
	btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
	fnOpenLayer(btnHtm, '삭제확인','<spring:message code="question.program.del" />');
}
//삭제 처리
function fnDel(){
	var obj = new Object();
	obj.qnaId ='${vmap.qnaId}';
	obj.encQnaId = '${vmap.encQnaId}';
	obj.amdr = '${ssUserVo.mbrId}';
	$.ajax({
		url: '<c:url value="/qna/delQnaAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			var btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnGoListPage()">확인</button> ';
			fnOpenLayer(btnHtm, '작성완료',data.msg);
			return;
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}
//목록 조회 페이지로 이동
function fnGoListPage(){
	$('#detailFrm').attr({action:c_url+'qna/mvQnAList.do', method:'post'}).submit();
}
//qna  이전 다음 
function fnGoViewPage(qnaId){
	$("#detailFrm > input[name='qnaId']").val(qnaId);
	$('#detailFrm').attr({action:c_url+'qna/mvQnaView.do', method:'post'}).submit();
}
//수정 페이지 이동
function fnUdpQna(){
	if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
		var btnHtm = "";
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		return;
	}else{
		if('${ssUserVo.mbrId}' != '${vmap.regr}'){
			var btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" >확인</button> ';
			fnOpenLayer(btnHtm, '권한확인',"수정 권한이 존재하지 않습니다.");
			return;
		}
	}
	$("#updFrm > input[name='qnaId']").val('${vmap.qnaId}');
	$("#updFrm > input[name='encQnaId']").val('${vmap.encQnaId}');

	$('#updFrm').attr('action', c_url+'qna/mvQnaReg.do' ).submit();
}
</script>    

<form id="detailFrm" >
	<input type="hidden" name="qnaId" value=""/>
</form>
<form id="updFrm" >
	<input type="hidden" name="qnaId" value=""/>
  <input type="hidden" name="encQnaId" value=""/>
</form>


<div id="container">
	<div class="sVisual sv_community">
		<div>
               <h2>Q&A</h2>
               <p>API에 대해 궁금한 점을 물어보세요</p>
           </div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a>Go home</a> <span>></span> 커뮤니티 <span>></span> Q&A</div>

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
                                            <th scope="row" class="thTitle pl30" colspan="2"><div><c:out value="${vmap.title}" escapeXml="true"></c:out> </div></th>
                                            <th scope="row"><div>${vmap.qstnDt}</div></th>
                                        </tr>
                                         <!--<tr>
                                            <td class="tdfield">
                                            	<div>첨부파일</div>
                                            </td>
                                            <td class="tdFileDwn">
                                            	<div>
                                            	 <c:if test="${not empty fList }">
												        <c:forEach items="${fList}" var="refFiles"  varStatus="idx"> 
													    	<a class="filedwn_txt"  href="<c:url value="/file/fileDownLoad.do?filePath=${refFiles.filePath}&downType=${refFiles.fileTypeCd}&orgFileName=${refFiles.originFileNm}&saveFileName=${refFiles.saveFileNm}" />"  download="${refFiles.originFileNm}">
													    		${refFiles.originFileNm}
															</a>
													    </c:forEach>
													  </c:if>
													  <c:if test="${empty fList }">
													  	<li>등록된 첨부파일이 없습니다.</li>
													  </c:if>
                                            	</div>
                                            </td>
                                            <td><div></div></td>
                                        </tr>-->
                                    </thead>

                                    <tbody>
                                        <tr>
                                            <td colspan="3" class="view_con"><div>  <c:out value="${vmap.qstn}" escapeXml="true"></c:out>  </div></td>
                                        </tr>
                                        <tr>
                                            <td colspan="3">
                                          		<c:if test="${ vmap.qnaSttusCd == 'QNASTS1020'}">
			                                        <div class="bx-comment">
	                                                    <h3 class="reply_count">답변 </h3>
	                                                    <ul>
	                                                        <li>
	                                                            <div class="comt1-1">${vmap.amdrNm}</div>
	                                                            <div class="comt1-2"><span>${vmap.ansDt}</span></div>
	                                                           
	                                                            <div class="comt1-4">
	                                                                <span>
			                                                                ${vmap.ans}
	                                                                </span>
	                                                           </div>              
	                                                       </li>
	                                                   </ul>
	                                               </div>
                                       			 </c:if>
                                           </td>
                                       </tr>
                                   </tbody>
                                   
                                   <tfoot>
                                       <tr>
                                           <th><div><span class="prev_txt">이전글</span></div></th>
                                           <c:choose>
                                                 <c:when test="${vmap.prevQnaId == 0  }">
	                                            	 <td colspan="2"><div>이전 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.prevQnaId}' );" title="${vmap.prevTitle }">${vmap.prevTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                       </tr>
                                       <tr>
                                           <th><div><span class="next_txt">다음글</span></div></th>
                                            <c:choose>
                                                 <c:when test="${vmap.nextQnaId == 0  }">
	                                            	 <td colspan="2"><div>다음 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.nextQnaId}');" title="${vmap.nextTitle }">${vmap.nextTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                       </tr>
                                   </tfoot>
                               </table>

                           </section>
                           <!-- // 개발자 포럼 View End -->
                       </div>
                       
                       <div class="btn_set">
                       <c:if test="${ssUserVo ne null }">
                       	 <c:if test="${ssUserVo.mbrId  eq  vmap.regr }">
                       	  	<button type="button" title="수정" class="btn" onclick="fnUdpQna()"><span>수정</span></button>
                           	<button type="button" title="삭제" class="btn" onclick="fnDelQna()"><span>삭제</span></button>
                       	 </c:if>
                       </c:if>
                           <button type="button" title="목록" class="btn btn_black" onclick="fnGoListPage()"><span>목록</span></button>
                       </div>
                   </div>
                   <!-- // comm_wrap -->
               </div>
		</div>
	</div>
</div>
</t:layout>