<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
//  파일명  : list.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 공지사항 글 목록 페이지
--%> 
<t:layout type="default">
<script type="text/javascript">
$(function(){
	fnSearch(1);//조회
})
//페이징 조회
function pageGo(pageIndex){
	fnSearch(pageIndex);
}
//공지사항 검색
function fnSearch(pageIndex) {
	var obj = new Object();
	
	obj.searchCondition =$('#searchCondition').val();
	obj.searchKeyword =$('#searchKeyword').val();
	obj.bbsTypeCd = 'BBSTYP1010'; // 공지사항
	obj.pageIndex = pageIndex;
	$.ajax({
		url: '<c:url value="/bbs/notice/selNoticeListAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			$(".table-list > tbody").children().remove();
			var html = "";
			if(data != null  && data.nlist.length != 0){
				$.each(data.nlist, function(index, item) {
					html+=	' <tr> ';
					if(parseInt(item.rownum) == 0){
						   html+=	'     <td><div><span class="important_tag">중요</span></div></td> ';
					}else{
						   html+=	'     <td><div><span>'+item.pstingNo+'</span></div></td> ';
					}
	                html+=	'     <td class="tdTitle"><div><a href="javascript:fnGoViewPage('+item.pstingId+', \'' +item.imptYn+ '\');" title="N-STEP 작업 공지">'+item.title+'</a></div></td> ';
	                html+=	'     <td><div>'+item.regDt+'</div></td> ';
	                if(parseInt(item.cnt) > 0){
	                	  html+=	'     <td><div><a class="filedwn_circle" >첨부파일 다운로드</a></div></td> ';
					}else{
						  html+=	'     <td><div></div></td> ';
					}
	                html+=	' </tr> ';
				}); //each끝
			}else {
	    		html += '<tr>';
	    		html += '<td colspan="4">';
	    		html += '	공지사항이  없습니다.';
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

//공지사항 상세보기
function fnGoViewPage(pstingId ,imptYn ){
	location.href=c_url+"bbs/notice/mvNoticeView.do?pstingId="+pstingId+"&bbsTypeCd=BBSTYP1010&imptYn="+imptYn;
	/* $("#detailFrm > input[name='pstingId']").val(pstingId);
	$("#detailFrm > input[name='imptYn']").val(imptYn);
	$('#detailFrm').attr({action:c_url+'bbs/notice/mvNoticeView.do', method:'post'}).submit(); */
}
</script>
<form id="detailFrm" >
	<input type="hidden" id="imptYn" name="imptYn" value=""/>
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value="BBSTYP1010"/>
</form>

<div id="container">
	<div class="sVisual sv_community">
		<div>
			<h2>공지사항</h2>
			<p>Open API의 새로운 소식과 안내사항 입니다</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a>Go home</a> <span>></span> 커뮤니티 <span>></span> 공지사항</div>

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
                           <!-- 공지사항 List start -->
                           <section>
                               <table class="table-list">
                                   <caption>공지사항 List Table</caption>
                                   <colgroup>
                                       <col style="width:110px;">
                                       <col style="width:auto;">
                                       <col style="width:160px;">
                                       <col style="width:160px;">
                                   </colgroup>

                                   <thead>
                                       <tr>
                                           <th scope="col"><div>NO</div></th>
                                           <th scope="col"><div>제목</div></th>
                                           <th scope="col"><div>작성일</div></th>
                                           <th scope="col"><div>첨부파일</div></th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                   </tbody>
                               </table>

                           </section>
                           <!-- // 공지사항 List End -->
                       </div>
                       <!-- paging -->
                       <div class="paging"  id="noticePaging"></div>
                   </div>
                   <!-- // comm_wrap -->
               </div>
		</div>
	</div>
</div>
</t:layout>
