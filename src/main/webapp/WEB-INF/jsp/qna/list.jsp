<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - Q&A">
<script type="text/javascript">
$(function(){
	fnSearch(1);//조회 
	//글쓰기 폼 이동
	$('.btn.btn_black').on('click' , function(){
		var btnHtm = "";
		if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
			btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
			fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
			return;
		}else{
			location.href = c_url + 'qna/mvQnaReg.do';
		}
	});
})
//페이징 조회
function pageGo(pageIndex){
	fnSearch(pageIndex);
}
//qna 검색
function fnSearch(pageIndex) {
	var obj = new Object();
	obj.searchCondition =$('#searchCondition').val();
	obj.searchKeyword =$('#searchKeyword').val();
	obj.pageIndex = pageIndex;
	$.ajax({
		url: '<c:url value="/qna/selQnaListAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			$(".table-list > tbody").children().remove();
			var html = "";
			if(data != null  && data.nlist.length != 0){
				$.each(data.nlist, function(index, item) { 
					html+=	' <tr> ';
					html+=	'     <td><div>'+item.qnaId+'</div></td> ';
	                html+=	'     <td class="tdTitle"><div><a href="javascript:fnGoViewPage('+item.qnaId+');" >'+item.title+'</a></div></td> ';
	                html+=	'     <td><div>'+item.qstnDt+'</div></td> ';
	                html+=	'     <td><div>'+item.amdrNm+'</div></td> ';
	                html+=	'     <td><div>'+item.qnaSttusNm+'</div></td> ';
	                html+=	' </tr> ';
				}); //each끝
			}else {
	    		html += '<tr>';
	    		html += '<td colspan="5">';
	    		html += '	QNA 정보가 존재하지 않습니다.';
	    		html += '</td>';
	    		html += '</tr>';
	    	}
			$(".table-list > tbody").append(html);
			drawPaging('paging' ,data.paginationInfo.currentPageNo ,data.paginationInfo.firstPageNoOnPageList ,data.paginationInfo.totalPageCount,data.paginationInfo.lastPageNoOnPageList  ,'pageGo');
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}

//QNA 상세보기
function fnGoViewPage(qnaId){
	location.href=c_url+"qna/mvQnaView.do?qnaId="+qnaId;
// 	$("#detailFrm > input[name='qnaId']").val(qnaId);
// 	$('#detailFrm').attr({action:c_url+'qna/mvQnaView.do', method:'post'}).submit();
}
</script>
<form id="detailFrm" >
	<input type="hidden" id="qnaId" name="qnaId" value=""/>
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
                       <!-- searching_wrap start -->
                       <div class="searching_wrap">                            
                           <div class="select_form">
                               <span class="combo_box">
                               <select title="검색어 필터" id="searchCondition" name="searchCondition" >
                                       <option value="">전체</option>
                                       <option value="title">제목</option>
                                       <option value="content">내용</option>
                               </select>
                               </span>
 								<span class="input_txt"><input type="text"  id="searchKeyword" name="searchKeyword"  title="검색어 입력"  onKeyPress="if (event.keyCode==13){fnSearch(1)};"></span>
                               <button type="button" class="btn-lg btn_searching" onclick="fnSearch(1)"><span>검색</span></button>
                           </div>   
                       </div>
                       <!-- searching_wrap end -->

                       <div class="pkg_board">
                           <!-- 개발자 포럼 List start -->
                           <section>
                               <table class="table-list">
                                   <caption>개발자 포럼 List Table</caption>
                                   <colgroup>
                                       <col style="width:110px;">
                                       <col >
                                       <col style="width:140px;">
                                       <col style="width:140px;">
                                       <col style="width:140px;">
                                   </colgroup>

                                   <thead>
                                       <tr>
                                           <th scope="col"><div>NO</div></th>
                                           <th scope="col"><div>제목</div></th>
                                           <th scope="col"><div>작성일</div></th>
                                           <th scope="col"><div>작성자</div></th>
                                           <th scope="col"><div>답변여부</div></th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                   </tbody>
                               </table>
                           </section>
                           <!-- // 개발자 포럼 List End -->
                       </div>
                       
                       <!-- paging -->
                       <div class="paging"  id="paging"></div>

                       <div class="btn_set-right">
                           <button type="button" title="글쓰기" class="btn btn_black"><span>글쓰기</span></button>
                       </div>

                   </div>
                   <!-- // comm_wrap -->
               </div>
		</div>
	</div>
</div>
</t:layout>