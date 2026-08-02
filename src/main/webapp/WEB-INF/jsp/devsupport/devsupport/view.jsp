<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- 
//  파일명  :view.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 개발자 포럼 상세보기 페이지
--%> 
<t:layout type="default">
<script type="text/javascript">


//포럼 글 수정 페이지 이동
function fnGoUpdPage(pstingId){
	$("#updFrm > input[name='pstingId']").val(pstingId);
	$('#updFrm').attr({action:c_url+'devsupport/devsupport/mvUpdForum.do', method:'post'}).submit();
}
//포럼 글 삭제 기능 구현
function fnDelForum(){
	var obj = new Object();
	obj.pstingId ='${vmap.pstingId}';
	obj.bbsTypeCd = '${vmap.bbsTypeCd}';
	 var f = confirm('<spring:message code="question.program.del" />');
     if(!f) return;
	$.ajax({
		url: '<c:url value="/bbs/forum/delForumAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alert(data.msg);
			fnGoListPage();
		},
		error:function(request,status,error){
		        alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}
//목록 조회 페이지로 이동
function fnGoListPage(){
	$('#detailFrm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();
}
//개발자 포럼  이전 다음 
function fnGoViewPage(pstingId, bbsTypeCd){
	$("#detailFrm > input[name='pstingId']").val(pstingId);
	$("#detailFrm > input[name='bbsTypeCd']").val(bbsTypeCd);
	$('#detailFrm').attr({action:c_url+'devsupport/devsupport/mvDevsupportView.do', method:'post'}).submit();
}

//수정 페이지 이동
function fnUdpForum(){
	$("#updFrm > input[name='pstingId']").val('${vmap.pstingId}');
	$("#updFrm > input[name='bbsTypeCd']").val('${vmap.bbsTypeCd}');
	$('#updFrm').attr('action', c_url+'devsupport/devsupport/mvDevsupportReg.do' ).submit();
}

$(document).ready(function() {
	/*
	alert('${vmap.service_BO}');
	alert('${vmap.pstingId}');
	alert('${vmap.devSupportGb}');
	alert('${vmap.title}');
	*/
	// 신청 기본 세팅
	
	if('${vmap.supportgb}' == 'VMAP'){
		$("#devSupportGb").append("VM신청 기술지원"); 
	}else if('${vmap.supportgb}' == 'FIAP'){
		$("#devSupportGb").append("방화벽 신청 기술지원"); 
	}else{
		$("#devSupportGb").append("TEST DATA등록 요청");  
	}
	
	//처리 결과 세팅 
	
	if('${vmap.status}' == '00'){
		$("#status").append("접수"); 
	}else if('${vmap.status}' == '30'){
		$("#status").append("처리중"); 
	}else{
		$("#status").append("처리완료");  
	}
});

function fnUdpDevSupport(){
	$("#updFrm > input[name='pstingId']").val('${vmap.pstingId}');
	$('#updFrm').attr('action', c_url+'devsupport/devsupport/mvDevsupportReg.do' ).submit();
}

//페이지로 이동
function fnVMApply(gb){
	
	var btnHtm = "";
	
	if(gb == "1"){
		//$('#frm').attr({action:c_url+'devsupport/vmguide/devVmGuide.do', method:'post'}).submit();
		 //window.open('http://ipc.kt.com/login','_blank');
	}else if(gb == "2"){ //TEST DATA
		
		$('#frm').attr({action:c_url+'devsupport/tdapply/testdataapply.do', method:'post'}).submit();
	}else if(gb == "3"){
		//$('#frm').attr({action:c_url+'devsupport/sdkdwn/sdkdwn.do', method:'post'}).submit();
	}else if(gb == "4"){
		
		if('${userJVo}' == null || '${userJVo.mbrId}' == null  || '${userJVo.mbrId}' == ''  )   {
			
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
			btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
			fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
		}else{
			$('#frm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();
		}
	
	}
	
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

<form name="frm" id="frm" method="post" >
	
</form>

<div id="container">
		<div class="sVisual sv_community">
			<div>
				<h2>개발 기술 지원</h2>
				<p>개발환경 구축을 위한 기술 지원 요청</p>
			</div>
		</div>
		<div class="contents style_gray2 search_only">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> 개발 기술 지원</div>

				<div id="content">
                    <!-- guide_wrap -->
                    <div class="guidedev_wrap">
                        <!-- guide_wrap -->
                        <div class="guide_wrap">
                             <ul class="tab_list guidetab">
                                <li onclick="fnVMApply('1')" class="guide01" title="개발VM신청 가이드"><span>개발VM 신청 <br>가이드</span></li>
                                <li onclick="fnVMApply('2')" class="guide02" title="Test Data 등록 요청"><span>Test Data <br> 등록 요청</span></li>
                                <li onclick="fnVMApply('3')" class="guide03" title="SDK 가이드 및 다운로드"><span>SDK 가이드 및 <br> 다운로드</span></li>
                                <li onclick="fnVMApply('4')" class="guide04 current" title="기술 지원 요청"><span>기술 지원 요청</span></li>
                            </ul>

                            <div id="tab1" class="tabcontent current">
                                <h6>개발VM 신청 및 기술지원 신청하기</h6>

                                <div class="pkg_board">
                                <!--  write start -->
                                    <h4 class="brd_title">상세보기</h4>
                                    <section>
                                        <h5 class="section_title">기본정보</h5>
                                        <table class="table-vw">
                                            <caption>기본정보 view Table</caption>
                                            <colgroup>
                                                <col style="width:13%;">
                                                <col style="width:37%;">
                                                <col style="width:13%;">
                                                <col style="width:37%;">
                                            </colgroup>

                                            <tbody>
                                                <tr>
                                                    <th scope="row"><div>신청구분</div></th>
                                                    <td colspan="3"><div id="devSupportGb"></div></td>
                                                </tr>
                                                
                                                <tr>
                                                    <th scope="row"><div>제목</div></th>
                                                    <td colspan="3"><div>${vmap.title}</div></td>
                                                </tr>
                                                <tr>
                                                    <th scope="row"><div>아이디</div></th>
                                                    <td><div>${vmap.regr}</div></td>
                                                    <th scope="row"><div>이름</div></th>
                                                    <td><div>${vmap.regrn}</div></td>
                                                </tr>
                                                <tr>
                                                    <th scope="row"><div>서비스 인프라명</div></th>
                                                    <td colspan="3"><div>${vmap.servinfran}</div></td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </section>

                                    <section>
                                        <h5 class="section_title">신청정보</h5>
                                        <table class="table-vw">
                                            <caption>신청정보 view Table</caption>
                                            <colgroup>
                                                <col style="width:13%;">
                                                <col style="width:37%;">
                                                <col style="width:13%;">
                                                <col style="width:37%;">
                                            </colgroup>

                                            <tbody>
                                               
                                                <tr>
                                                    <th scope="row"><div>서비스 담당자</div></th>
                                                    <td><div>${vmap.busern}</div></td>
                                                    <th scope="row"><div>개발 담당자</div></th>
                                                    <td><div>${vmap.dusern}</div></td>
                                                </tr>
                                                <tr>
                                                    <th scope="row"><div>ADC 관리번호1</div></th>
                                                    <td><div>${vmap.adci}</div></td>
                                                    <th scope="row"><div>표준서비스 코드</div></th>
                                                    <td><div>${vmap.servc}</div></td>
                                                </tr>
                                                <tr>
                                                    <th scope="row"><div>신청내용</div></th>
                                                    <td colspan="3">
                                                        <div class="txtarea_wrap">${vmap.sbst}</div>
                                                    </td>
                                                </tr>
                                                
                                                <tr>
                                                    <th scope="row"><div>첨부파일</div></th>
                                                    <td class="tdFileDwn" colspan="3">
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
                                                </tr>
                                            </tbody>
                                        </table>
                                    </section>

                                    <section>
                                        <h5 class="section_title">처리결과</h5>
                                        <table class="table-vw">
                                            <caption>처리결과 view Table</caption>
                                            <colgroup>
                                                <col style="width:13%;">
                                                <col style="width:87%;">
                                            </colgroup>

                                            <tbody>
                                                <tr>
                                                    <th scope="row"><div>처리상태</div></th>
                                                    <td><div id="status"></div></td>
                                                </tr>
                                                <tr>
                                                    <th scope="row"><div>COMMENT</div></th>
                                                    <td><div>${vmap.comment}</div></td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </section>
<!-- 
                                    <section>
                                        <h5 class="section_title">VM 신청현황</h5>
                                        <table class="table-list">
                                            <caption> List Table</caption>
                                            <colgroup>
                                                <col style="width:25%">
                                                <col style="width:25%">
                                                <col style="width:25%">
                                                <col style="width:25%">
                                            </colgroup>

                                            <thead>
                                                <tr>
                                                    <th scope="col"><div>타이틀</div></th>
                                                    <th scope="col"><div>타이틀</div></th>
                                                    <th scope="col"><div>타이틀</div></th>
                                                    <th scope="col"><div>타이틀</div></th>
                                                </tr>
                                            </thead>

                                            <tbody>
                                                <tr>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                </tr>
                                                <tr>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                    <td><div>내용</td>
                                                </tr>
                                                
                                                
                                            </tbody>
                                        </table>
                                    </section>
 -->
                                </div>
                                

                                <div class="btn_set">
                                    <button type="button" title="수정" class="btn btn_black" onclick="fnUdpDevSupport()"><span>수정</span></button>
                                    <button type="button" title="삭제" class="btn"><span>삭제</span></button>
                                     <button type="button" title="목록" class="btn btn_black"  onclick="fnGoListPage()"><span>목록</span></button>
                                </div>
                                <!-- // write End -->
                            </div>

                            <div id="tab2" class="tabcontent">
                                <h6>Test Data 등록 요청</h6>
                                <!-- // List End -->
                            </div>

                            <div id="tab3" class="tabcontent ">
                                <h6>SDK 가이드 및 다운로드</h6>
                                
                                <!-- // List End -->
                            </div>

                            <div id="tab4" class="tabcontent">
                                <h6>기술 지원 요청</h6>
                                
                                <!-- // List End -->
                            </div>
                        </div>
                        <!-- // guide_wrap -->


                    </div>
                    <!-- // guide_wrap -->
                </div>
			</div>
		</div>
	</div>
</t:layout>
