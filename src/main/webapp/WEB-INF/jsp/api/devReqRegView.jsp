<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="apiInfo">
<!-- 
    OPEN API version 1.0
  
    Copyright ⓒ 2017 kt corp. All rights reserved.
    
    This is a proprietary software of kt corp, and you may not use this file except in 
    compliance with license agreement with kt corp. Any redistribution or use of this 
    software, with or without modification shall be strictly prohibited without prior written 
    approval of kt corp, and the copyright notice above does not evidence any actual or 
 intended publication of such software. 
-->

<!-- 개발 요청 등록 페이지 -->
<script type="text/javascript">
var selTab = "${param.selTab}";

$(function(){
	
	// 취소
	$('.btn_cancel').on('click' , function(){
		mvMainPage();
	});
	
	// 등록 체크
	$('.btn_reqreg').on('click' , function(){
		
		if($('#reviewOpin').val()==''){
			$('#reviewOpin').focus();
			var msg = "검토의견을 등록하세요";
			var title = "검토의견 미입력";
			if(selTab=="4"){
				title = "답변 미입력";
				msg = "답변을 등록하세요";
			}
			$('.alert_txt').html(msg);
			$( ".pop_alert_devReqRegView" ).dialog( "open" );
			return;
		}
		
		openSaveLayerPopup();
		
	});
	
	// 댓글 및 답변 등록
	$('.btn_popup_save').on('click' , function(){
		savDevReq();
	});
	
	
})

function mvReqDetailView(){
		$('#actionForm').attr("action" , "<c:url value='/api/main/mvDevReqView.do' />").submit();
	}
	
// 목록 화면으로 이동
function mvMainPage(){
	$('#actionForm').attr("action" , "<c:url value='/api/main/mvMainList.do' />").submit();
}

//등록 확인 레이어
function openSaveLayerPopup(){
	var msg = "검토의견을 보내시겠습니까?";
	var title = "검토의견 보내기";
	if(selTab=="4"){
		title = "답변 보내기";
		msg = "답변을 보내시겠습니까?";
	}
	$('.alert_txt').html(msg);
	$(".btn_popup_close").hide();
	$(".btn_popup_save").show();
	$(".btn_popup_cancel").show();
	$( ".pop_alert_devReqRegView" ).dialog( "open" );
}


// 답변 등록
function savDevReq(){
	
	// param setting
    var param 		= new Object();
    param.apiReviewRqtNo	= '${param.apiReviewRqtNo}';
    param.reviewOpin		= $('#reviewOpin').val();
    
	$.ajax({
		url    : '<c:url value="/api/main/savDevReplyRegAjax.do"/>', 
		type   : 'POST',
		data   : param,
		success: function(data){
			mvMainPage();
		},
		error:function(request,status,error){
			alert('등록 실패');
			alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
};

</script>

	<div id="container">
		<div class="sVisual sv_regiapi">
			<div>
				<c:if test="${param.selTab eq '4'}" >
					<h2>API 개발요청</h2>
					<p>신규 API에 대하여 개발자에게 개발요청을 하세요</p>
				</c:if>
				<c:if test="${param.selTab eq '3'}" >
					<h2>API 검토의견</h2>
					<p>검토 요청하신 API에 대한 의견을 확인하세요</p>
				</c:if>
			</div>
		</div>
		<div class="contents">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> API 등록</div>

				<div id="content">
                    <!-- regiApi_wrap -->
                    <div class="regiApi_wrap">
                        <!-- pakage board write -->
                        <div class="pkg_board">
                            <!-- 개발 요청 내용 -->
                            <section>
                                <c:if test="${param.selTab eq '4'}" >
									<h4 class="brd_title">개발 요청 내용</h4>
								</c:if>
								<c:if test="${param.selTab eq '3'}" >
									<h4 class="brd_title">검토 의견 내용</h4>
								</c:if>
                                <table class="table-vw">
                                    <caption>개발 요청 내용 Table</caption>
                                    <colgroup>
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                    </colgroup>

                                    <tbody>
                                        <tr>
                                            <th scope="row"><div>요청자</div></th>
                                            <td><div>${info.amdrNm}</div></td>
                                            <th scope="row"><div>부서</div></th>
                                            <td><div>${info.cmpnNm}</div></td>
                                        </tr>
                                        <tr>
                                            <th scope="row"><div>API 시스템</div></th>
                                            <td colspan="3">${info.sysIdNm}</td>
                                        </tr>
                                        <c:if test="${param.selTab eq '4'}" >
											<tr>
	                                            <th scope="row"><div>제목</div></th>
	                                            <td colspan="3"><div>${info.reviewRqtTitle}</div></td>
	                                        </tr>
										</c:if>
										<c:if test="${param.selTab eq '3'}" >
											<tr>
	                                            <th scope="row"><div>API 이름</div></th>
	                                            <td colspan="3"><div>${info.apiNm}</div></td>
	                                        </tr>
										</c:if>
                                        <tr>
                                            <th scope="row"><div>
                                            		<c:if test="${param.selTab eq '4'}" >
														내용
													</c:if>
													<c:if test="${param.selTab eq '3'}" >
														검토 요청 내용
													</c:if>
                                            		</div></th>
                                            <td colspan="3">
                                                <div class="txtarea_wrap">${info.reviewRqtSbst}
                                                </div>
                                            </td>
                                        </tr>
                                        <c:if test="${param.selTab eq '4'}" >
                                        <tr>
                                            <th scope="row"><div>첨부파일</div></th>
                                            <td colspan="3">
                                                <div>
                                                    <ul class="file_list">
                                                    	<c:if test="${not empty fList }">
												        	<c:forEach items="${fList}" var="refFiles"  varStatus="idx"> 
												        	<li>
												        		<a href="#" title="${refFiles.originFileNm}" class="filedwn_txt">${refFiles.originFileNm}</a>
                                                            	<a href="<c:url value="/file/fileDownLoad.do"/>?filePath=${refFiles.filePath}&downType=${refFiles.fileTypeCd}&orgFileName=${refFiles.originFileNm}&saveFileName=${refFiles.saveFileNm}"  title="IoT Makers.zip" class="filedwn_icon">IoT Makers.zip</a>
															</li>
												        	</c:forEach>
												        </c:if>
														<c:if test="${empty fList }">
														  	<li>등록된 첨부파일이 없습니다.</li>
														</c:if>
                                                    </ul>
                                                </div>
                                            </td>
                                        </tr>
										</c:if>
                                    </tbody>
                                </table>
                            </section>
                            <!-- // 개발 요청 내용 -->
                            <!-- 개발 요청 답변 -->
                           	<c:if test="${reply.size() > 0 }" >
                           	<section>
                            	<c:if test="${param.selTab eq '4'}" >
									<h4 class="brd_title">개발 요청 답변</h4>
								</c:if>
								<c:if test="${param.selTab eq '3'}" >
									<h4 class="brd_title">검토 의견</h4>
								</c:if>
                            	<c:forEach var="item" items="${reply}" varStatus="status">
									<table class="table-vw">
		                                <caption>개발 요청 답변 보기 Table</caption>
		                                <colgroup>
		                                    <col style="width:150px;">
		                                    <col >
		                                </colgroup>
		                                <tbody>
		                                    <tr>
		                                        <th scope="row"><div>등록자</div></th>
		                                        <td colspan="3">
		                                            <div>${item.amdrNm} ${item.regDtStr}</div>
		                                        </td>
		                                    </tr>
		                                    <tr>
		                                        <th scope="row"><div>
		                                        <c:if test="${param.selTab eq '4'}" >
													답변내용
												</c:if>
												<c:if test="${param.selTab eq '3'}" >
													검토 의견 내용</div>
												</c:if>
												</th>
		                                        <td colspan="3">
		                                            <div class="txtarea_wrap txtarea-view">${item.reviewOpin}
		                                            </div>
		                                        </td>
		                                    </tr>
		                                </tbody>
		                            </table>
								</c:forEach>
							</section>
							</c:if>
							
							
							<c:if test="${ (info.replyCnt < 1 and param.selTab eq '4') or param.selTab eq '3' }">
							<section>
								<c:if test="${param.selTab eq '4'}" >
									<h4 class="brd_title">개발 요청 답변</h4>
								</c:if>
								<c:if test="${param.selTab eq '3'}" >
									<h4 class="brd_title">검토 의견 답변</h4>
								</c:if>
                                         <table class="table-vw">
	                                    <caption>개발 요청 답변 Table</caption>
	                                    <colgroup>
	                                        <col style="width:12%;">
	                                        <col >
	                                    </colgroup>
	                                    <tbody>
	                                        <tr>
	                                            <th scope="row"><div><c:if test="${param.selTab eq '4'}" >
													답변내용
												</c:if>
												<c:if test="${param.selTab eq '3'}" >
													검토 의견 내용</div>
												</c:if></div></th>
	                                            <td colspan="3">
	                                                <div class="txtarea_wrap"><textarea id="reviewOpin" name="reviewOpin" title="답변내용 입력" onchange="CheckStrLength(4000,'reviewOpin')"   onkeyup="CheckStrLength(4000,'reviewOpin')" ></textarea></div>
	                                            </td>
	                                        </tr>
	                                    </tbody>
	                                </table>
	                        </section>
							</c:if>
                            
                            <!-- // 개발 요청 답변 -->
                        </div>
                        <!-- // pakage board write -->

                        <div class="btn_set">
                        	<c:if test="${param.selTab eq '4' and info.replyCnt < 1}" >
								<button type="button" title="답변 보내기" class="btn btn_black btn_reqreg"><span>답변 보내기</span></button>
							</c:if>
							<c:if test="${param.selTab eq '3'}" >
								<button type="button" title="의견 보내기" class="btn btn_black btn_reqreg"><span>의견 보내기</span></button>
							</c:if>
                            <button type="button" title="목록" class="btn btn_cancel"><span>목록</span></button>
                        </div>
                    </div>
                    <!-- // regiApi_wrap -->
                </div>
			</div>
		</div>
	</div>
	
	<form id="actionForm" name="actionForm" method="post" >
		<input type="hidden" id="selTab"  name="selTab" value="${param.selTab}" />
		<input type="hidden" id="apiReviewRqtNo"  name="apiReviewRqtNo" value="${param.apiReviewRqtNo}" />
	</form>

	<!--// popup content - 알림 -->
    <div class="pop_alert_devReqRegView" title="알림" style="display:none;">
        <!--  popup content Start  -->
        <div class="popup_content">
            <div class="alert_txt">
                
            </div>

            <div class="lPop_bottom brd_tp">
                <button type="button" title="취소" class="btn btn_sml3 btn_popup_close">확인</button>
                <button type="button" title="확인" class="btn btn_sml3 btn_black btn_popup_save" style="display:none;" >확인</button>
                <button type="button" title="취소" class="btn btn_sml3 btn_popup_cancel" style="display:none;">취소</button>
            </div>
        </div>
    </div>
	<!-- popup content - 알림 //-->

	<!-- laypop script -->
    <script>
    $(".pop_alert_devReqRegView" ).dialog({
        autoOpen: false, dialogClass: 'pop_alert_wrap', modal: true, resizable: false
    });

    $( ".btn_popup_close , .btn_popup_cancel" ).click(function( event ) {
      $( ".pop_alert_devReqRegView" ).dialog( "close" );
      $(".btn_popup_close").show();
      $(".btn_popup_save").hide();
      $(".btn_popup_cancel").hide();
      event.preventDefault();
    });

    </script>

</t:layout>