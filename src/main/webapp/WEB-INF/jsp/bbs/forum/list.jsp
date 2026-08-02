<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
//  파일명  : list.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 개발자 포럼 목록 페이지
--%> 
<t:layout type="default">
<script type="text/javascript">
$(function(){
	fnSearch(1);//조회 
	//글쓰기 폼 이동
	$('.btn.btn_black').on('click' , function(){
		if('${ssUserVo}' == null || '${ssUserVo.mbrId}' == null || '${ssUserVo.mbrId}' == ''){
			var btnHtm = "";
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
			btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
			fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		}else{
			location.href = c_url + 'bbs/forum/mvForumReg.do';
		}
	});
})
//페이징 조회
function pageGo(pageIndex){
	fnSearch(pageIndex);
}
//개발자 포럼 검색
function fnSearch(pageIndex) {
	var obj = new Object();
 	obj.searchCondition =$('#searchCondition').val();
	obj.searchKeyword =$('#searchKeyword').val();
	obj.bbsTypeCd = 'BBSTYP1020'; // 개발자 포럼
	obj.pageIndex = pageIndex;
	$.ajax({
		url: '<c:url value="/bbs/forum/selForumListAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			$(".table-list > tbody").children().remove();
			var html = "";
			if(data != null  && data.nlist.length != 0){
				$.each(data.nlist, function(index, item) { 
					html+=	' <tr> ';
					html+=	'     <td><div>'+item.pstingNo+'</div></td> ';
	                html+=	'     <td class="tdTitle"><div><a href="javascript:fnGoViewPage('+item.pstingId+');" title="N-STEP 작업 공지">'+item.title+'</a></div></td> ';
	                html+=	'     <td><div>'+item.regDt+'</div></td> ';
	                html+=	'     <td><div>'+item.reqCnt+'</div></td> ';
	                html+=	'     <td><div>'+item.retvNum+'</div></td> ';
	                html+=	' </tr> ';
				}); //each끝
			}else {
	    		html += '<tr>';
	    		html += '<td colspan="5">';
	    		html += '	개발자 포럼 정보가 존재하지 않습니다.';
	    		html += '</td>';
	    		html += '</tr>';
	    	}
			$(".table-list > tbody").append(html);
			drawPaging('noticePaging' ,data.paginationInfo.currentPageNo ,data.paginationInfo.firstPageNoOnPageList ,data.paginationInfo.totalPageCount,data.paginationInfo.lastPageNoOnPageList  ,'pageGo');
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}

//개발자포럼 상세보기
function fnGoViewPage(pstingId){
	location.href=c_url+"bbs/forum/mvForumView.do?pstingId="+pstingId+"&bbsTypeCd=BBSTYP1020";
	/* $("#detailFrm > input[name='pstingId']").val(pstingId);
	$('#detailFrm').attr({action:c_url+'bbs/forum/mvForumView.do', method:'post'}).submit(); */
}

</script>
<form id="detailFrm" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value="BBSTYP1020"/>
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
                        <!-- searching_wrap start -->
                        <div class="searching_wrap">                            
                            <div class="select_form">
                              <span class="combo_box">
                                    <select title="검색어 필터"  id="searchCondition" name="searchCondition"  >
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
                                        <col style="width:160px;">
                                        <col style="width:140px;">
                                        <col style="width:140px;">
                                    </colgroup>

                                    <thead>
                                        <tr>
                                            <th scope="col"><div>NO</div></th>
                                            <th scope="col"><div>제목</div></th>
                                            <th scope="col"><div>작성일</div></th>
                                            <th scope="col"><div>답변수</div></th>
                                            <th scope="col"><div>조회수</div></th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                    </tbody>
                                </table>

                            </section>
                            <!-- // 개발자 포럼 List End -->
                        </div>
                        <!-- paging -->
                        <div class="paging"  id="noticePaging"></div>
                        <div class="btn_set-right">
                        	<button type="button" title="글쓰기" class="btn btn_black"><span>글쓰기</span></button>
                           <%--  <c:if test="${ssUserVo.mbrId ne null}"><button type="button" title="글쓰기" class="btn btn_black"><span>글쓰기</span></button></c:if> --%>
                        </div>
                    <!-- // comm_wrap -->
                </div>
			</div>
		</div>
	</div>
</div>
</t:layout>
