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
   
<!-- 개발 요청 / 검토요청 등록 페이지 -->
<script type="text/javascript">

$(function(){
	
	// 취소
	$('.btn_cancel').on('click' , function(){
		mvMainPage();
	});
	
	// 등록 진행
	$('.btn_black').on('click' , function(){
		if(!setValideCheck()){
			return false;
		}else{
			openSaveLayerPopup();
		}
	});
	
	// 등록
	$('.btn_popup_save').on('click' , function(){
		savDevReq();
	});
	
	
	<c:if test="${param.selTab eq '4'}" >
	function setValideCheck(){
		if( $("#revSysId option:selected").val() == ''){
			setLayerSetting('revSysId' , "시스템을 선택하세요.");
			return false;
		}
		
		if( $("#reviewRqtTitle").val() == ''){
			setLayerSetting('reviewRqtTitle' , "제목을 입력 하세요.");
			return false;
		}
		
		if( $("#reviewRqtSbst").val() == ''){
			setLayerSetting('reviewRqtTitle' , "개발요청 내용을 입력 하세요.");
			return false;
		}
		
		return true;
	}
	</c:if>
	<c:if test="${param.selTab eq '3'}" >
	function setValideCheck(){
		if( $("#reviewRqtSbst").val() == ''){
			setLayerSetting('reviewRqtTitle' , "검토요청 내용을 입력 하세요.");
			return false;
		}
		return true;
	}
	</c:if>

});

// 레이어 셋팅
function setLayerSetting(targetId, targetMessge){
	if(''!=targetId){
		$("#"+targetId).focus();
	}
	$('.alert_txt').html(targetMessge);
	$(".btn_popup_close").show();
	$(".btn_popup_save").hide();
	$(".btn_popup_cancel").hide();
	
	$( ".pop_alert_devReqRegView" ).dialog( "open" );
}

// 메인페이지로 이동
function mvMainPage(){
	// console.log('mvMainPage');
	$('#actionForm').attr("action" , "<c:url value='/api/main/mvMainList.do' />").submit();
}

// 등록 확인 레이어
function openSaveLayerPopup(){
	$('.alert_txt').html("검토요청을 보내시겠습니까?");
	if('${fn:escapeXml(param.selTab)}' == '4'){
		$('.alert_txt').html("개발요청을 보내시겠습니까?");
	}
	$(".btn_popup_close").hide();
	$(".btn_popup_save").show();
	$(".btn_popup_cancel").show();
	$( ".pop_alert_devReqRegView" ).dialog( "open" );
}

// 등록 진행
var progressFlag = false;
function savDevReq(){
	if(progressFlag){
		return;
	}
	// param setting
    var param 		= new Object();
	if('${fn:escapeXml(param.selTab)}' == '4'){

	    $('#reviewRqtTypeCd').val('APIRQT1020');
	    $('#reviewSysId').val($("#revSysId option:selected").val());
	    
	    param.reviewRqtTypeCd	= 'APIRQT1020';
	    param.reviewSysId		= $("#revSysId option:selected").val();
	    
	}else{
		
		$('#reviewRqtTypeCd').val('APIRQT1010');
		$('#reviewSysId').val('${fn:escapeXml(param.sysId)}');
		
		param.apiSpcNo			= '${fn:escapeXml(param.apiSpcNo)}';
		param.reviewRqtTypeCd	= 'APIRQT1010';
		param.reviewSysId		= '${info.sysId}';
	}
	
    param.reviewRqtTitle	= $('#reviewRqtTitle').val();
    param.reviewRqtSbst		= $('#reviewRqtSbst').val();
    
    progressFlag = true;
    
	$.ajax({
		url    : '<c:url value="/api/main/savReqRegAjax.do"/>', 
		type   : 'POST',
		data   : param,
		success: function(data){
			
			if ( rfcnt > 0 ) {
				raddFile.rfileUpload(data.apiReviewRqtNo);
		 	}else{
		 		setLayerSetting('', "저장되었습니다.");
		 	}
		},
		error:function(request,status,error){
			alert('등록 실패');
			alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
	
	/*
	$("#fileFrm").ajaxForm({
		url : '<c:url value="/api/main/savReqRegAjax.do"/>',
		type : 'POST',
		dataType : 'json',
		async : false,
//			enctype : "multipart/form-data", 
		success : function(result) {
			console.log("result.fileSeq : ", result.fileSeq);
//				$('#fileSeq').val(result.fileSeq);
			var fileSeqSize = $('input[name="fileSeq"]').length;
			$('input[name="fileSeq"]').eq(fileSeqSize-1).val(result.fileSeq);
			alert("파일첨부 등록이 완료 되었습니다.");
		},
		error:function(request,status,error){
    		alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
    	}
	});
	*/
	
};



var rfcnt=0;
var fcnt=0;

//파일 첨부
var raddFile = {
		rfnAddFile:function(obj ,targetObj){
			$('#fileFrm').children().remove();
			var html ='<input type="file" style="display: none;" id="hdnFile" name="uploadFile"  onchange="raddFile.rfnChangeFile(this ,\''+targetObj+' \' );" />';
			$('#fileFrm').append(html);
			$('#hdnFile').click();
		},
	rfnChangeFile : function(obj, targetObj) {
		
		rfcnt++;
		var fileId= targetObj+rfcnt;
		var file = $(obj)[0].files[0];
		var maxSize = 26214400; // 25MB
		var ext = file.name.split(".").pop().toLowerCase();
		var fileSize = file.size;
		
		if($.inArray(ext, ['zip']) == -1) {
			$("#fileUploadView").focus();
			$('.alert_txt').html("ZIP 파일만 업로드 가능합니다.");
			$( ".pop_alert_devReqRegView" ).dialog( "open" );
			$('#hdnFile').remove();
			return false;
		}  
		
		if(fileSize > maxSize) {
			$("#fileUploadView").focus();
			$('.alert_txt').html("파일은 최대 25MB를 초과할 수 없습니다.");
			$( ".pop_alert_devReqRegView" ).dialog( "open" );
			$('#hdnFile').remove();
			return false;
		}
		
		if($("#fileUploadView").val() == '선택된 파일이 없습니다.'){
			$("#fileUploadView").val(file.name)
		}
		
		//raddFile.rfileUpload(1);
		
	},
	rfnDelFile : function(obj ,frmObj,fcnt, fileSeq) {
		var object = new Object();
		// console.log("fileSeq", fileSeq);
		object.fileNo = fileSeq;

		$.ajax({
			type: "POST",
			url: '<c:url value="/artist/artistFileUpdate.do"/>',
			data: object,
			success: function (data) {
				$('.alert_txt').html("첨부파일이 삭제 되었습니다.");
				$( ".pop_alert_devReqRegView" ).dialog( "open" );
				
				$(obj).parent().remove();
				$('#'+ frmObj).remove();
				$("#inputFileSeq"+fcnt).remove();		   			
            }
         });
		
	},
	rfileUpload : function(apiReviewRqtNo) {
		var html ='<input type="hidden" style="display: none;" id="apiReviewRqtNo" name="apiReviewRqtNo"  value="'+apiReviewRqtNo+'" />';
		$('#fileFrm').append(html);
		$("#fileFrm").ajaxForm({
			url : '<c:url value="mutiUploadFile.do"/>',
			type : 'POST',
			dataType : 'json',
			async : false,
//				enctype : "multipart/form-data", 
			success : function(result) {
//					$('#fileSeq').val(result.fileSeq);
/* 				var fileSeqSize = $('input[name="fileSeq"]').length;
				$('input[name="fileSeq"]').eq(fileSeqSize-1).val(result.fileSeq); */
				setLayerSetting('', "저장되었습니다.");
			},
    		error:function(request,status,error){
	    		alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
	    	}
		});
		$("#fileFrm").submit();
	}
}

</script>

<form id="joinFrm" name="joinFrm" method="post" >

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
                            <section>
                                <table class="table-vw">
                                    <caption>API 검토요청 작성 Table</caption>
                                    <colgroup>
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                    </colgroup>

                                    <tbody>
                                        <tr>
                                            <th scope="row"><div>요청자</div></th>
                                            <td><div>${ssUserVo.mbrNm}</div></td>
                                            <th scope="row"><div>부서</div></th>
                                            <td><div>${ssUserVo.cmpnNm}</div></td>
                                        </tr>
                                        <tr>
                                            <th scope="row"><div>API 시스템</div></th>
                                            <td colspan="3">
                                                <div>
                                                    <c:if test="${param.selTab eq '4'}" >
														<select title="API 시스템" class="w37" id="revSysId" name="revSysId">
                                                    	<option value="">선택</option>
                                                    	<c:forEach var="item" items="${selSysList}" varStatus="status">
                                                    		<option value="${item.sysId}">${item.sysNm}</option>
														</c:forEach>
                                                    </select>
													</c:if>
													<c:if test="${param.selTab eq '3'}" >
														${info.sysId}
													</c:if>
													<input type="hidden" name="reviewSysId" id="reviewSysId" title="">
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th scope="row"><div><c:if test="${param.selTab eq '3'}" >API 이름</c:if><c:if test="${param.selTab eq '4'}" >제목</c:if></div></th>
                                            <td colspan="3"><div>
                                             	<c:if test="${param.selTab eq '3'}" >
													${info.apiNm}
												</c:if>
                                            	<c:if test="${param.selTab eq '4'}" >
													<input type="text" name="reviewRqtTitle" id="reviewRqtTitle" title="API 이름 입력" onchange="CheckStrLength(300,'reviewRqtTitle')"   onkeyup="CheckStrLength(300,'reviewRqtTitle')" >
												</c:if>
                                               
                                            </div></td>
                                        </tr>
                                        <tr>
                                            <th scope="row"><div><c:if test="${param.selTab eq '4'}" >
													개발 요청 내용
												</c:if>
                                                <c:if test="${param.selTab eq '3'}" >
													검토 요청 내용
												</c:if></div></th>
                                            <td colspan="3">
                                                <div class="txtarea_wrap"><textarea id="reviewRqtSbst" name="reviewRqtSbst" title="검토 요청 내용 입력" onchange="CheckStrLength(2000,'reviewRqtSbst')"   onkeyup="CheckStrLength(2000,'reviewRqtSbst')"></textarea></div>
                                            </td>
                                        </tr>
                                        <c:if test="${param.selTab eq '4'}" >
                                        <tr>
                                            <th scope="row"><div>첨부파일</div></th>
                                            <td colspan="3">
                                                <div>
                                                    <div class="file_upload"> 
                                                        <span class="inpDiv"><input type="text" name="fileUploadView" id="fileUploadView" class="file_address" title="file_address" readonly="readonly" value="선택된 파일이 없습니다."></span>
                                                        <button type="button" class="btn btn_import" title="파일선택" onclick="raddFile.rfnAddFile(this,'fileUploadView');" ><span>파일선택</span></button>
                                                    </div>
                                                    <p class="impt_cmmt">* ZIP 파일만 업로드 가능합니다. 파일은 최대 25MB를 초과할 수 없습니다.</p>
                                                </div>
                                            </td>
                                        </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </section>
                        </div>
                        <!-- // pakage board write -->

                        <div class="btn_set">
                            <button type="button" title="개발요청" class="btn btn_black"><span><c:if test="${param.selTab eq '4'}" >개발요청</c:if><c:if test="${param.selTab eq '3'}" >검토요청</c:if></span></button>
                            <button type="button" title="목록" class="btn btn_cancel"><span>목록</span></button>
                        </div>
                    </div>
                    <!-- // regiApi_wrap -->
                </div>
			</div>
		</div>
	</div>
</form>

<form id="actionForm" name="actionForm" method="post" >
	<input type="hidden" id="selTab"  	name="selTab" 	value="${fn:escapeXml(param.selTab)}" />
	<input type="hidden" id="apiSpcNo"  name="apiSpcNo" value="${fn:escapeXml(param.apiSpcNo)}" />
	<input type="hidden" id="schText"  	name="schText" 	value="${fn:escapeXml(param.schText)}" />
	<input type="hidden" id="apiNm"  	name="apiNm" 	value="${info.apiNm}" />
	<input type="hidden" id="sysIdNm"  	name="sysIdNm" 	value="${info.sysId}" />
</form>

<!-- file upload -->
<input type="hidden" id="fileSeq" value="">
<form method="post" enctype="multipart/form-data"  id="fileFrm" name="fileFrm">
</form>

<!--// popup content - 알림 -->
  <div class="pop_alert_devReqRegView" title="알림">
      <!--  popup content Start  -->
      <div class="popup_content">
          <div class="alert_txt">
              
          </div>

          <div class="lPop_bottom brd_tp">
              <!-- <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button> -->
              <button type="button" title="취소" class="btn btn_sml3 btn_black btn_popup_close">확인</button>
              <button type="button" title="확인" class="btn btn_sml3 btn_black btn_popup_save" style="display:none;" >확인</button>
              <button type="button" title="취소" class="btn btn_sml3 btn_popup_cancel" >취소</button>
          </div>
      </div>
  </div>
<!-- popup content - 알림 //-->

	<!-- laypop script -->
    <script>
    $(".pop_alert_devReqRegView" ).dialog({
        autoOpen: false, dialogClass: 'pop_alert_wrap', modal: true, resizable: false
    });

    $(function(){
	    $( ".btn_popup_cancel" ).click(function( event ) {
	        $( ".pop_alert_devReqRegView" ).dialog( "close" );
	        event.preventDefault();
	    });
	    
	    
	    $( ".btn_popup_close" ).click(function( event ) {
	    	$( ".pop_alert_devReqRegView" ).dialog( "close" );
	        event.preventDefault();
	        if(progressFlag){
	        	mvMainPage();
	        }
	    });
    });
    </script>
        
        
</t:layout>