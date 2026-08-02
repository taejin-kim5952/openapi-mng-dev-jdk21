<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="apiInfo">
<script type="text/javascript">
$(function(){
	//--@@console.log(4);
	
	$("#btnProc").on('click',function(){
		procExcute("insert")
	});

	$("#btnProcUp").on('click',function(){
		procExcute("update")
	});
	
	$("#btnProcUpSucess").on('click',function(){
		DeployExcute("SUCCESS");
	});
	
	$("#btnProcUpFail").on('click',function(){
		DeployExcuteFail("FAIL");
	});
	
	$("#btnProcUpTbDeploys").on('click',function(){
		tbDeployExcuteSuccess("SUCCESS");
	});
	
	
	$("#btnProcUpTbDeployf").on('click',function(){
		tbDeployExcuteFail("FAIL");
	});
	
	$("#btnProcUpTbVerifis").on('click',function(){ //검증 성공 
		verifiSuccessCase("SUCCESS");
	});
	
	$("#btnProcUpTbVerifif").on('click',function(){ //검증 성공 
		verifiFailCase("FAIL");
	});
	
	$("#btnProcVerifiStart").on('click',function(){ //검증 성공 
		verifiStart();
	});
	
	$("#btnProcVerifiEnd").on('click',function(){ //검증 성공 
		verifiEnd();
	});
	

	procList();
	
	hstList()
	
	//-----------------------------//

	$("#btnProcDrm").on('click',function(){
		procExcuteDrm();
	});
	
			
});

function procList(){
	
	var obj = new Object();
	
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvDeployListAjax.do"/>', 
		type: 'POST',
		data: obj,
		success: function(data){
			////조회항목 - SPC
			$('#procListLayer').empty(); 
			
			if(data != null  && data.nlist.length != 0){
				
				var spchtml = '';
				
				$.each(data.nlist, function(index, item) { 
					
					spchtml +='<option value="' +  item.seq + '">' + item.seq + '/' +  item.deployCd  + '/' + item.apiNm + '</option>' ;
						
				}); //each끝
				
						
			}else{
				
				spchtml +='<option value="">등록된 프로세스가 없습니다.</option>' ;
			}
			
			$('#procListLayer').append(spchtml); 		
			
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
}


function procExcute(e){

	var obj = new Object();
	
	obj.apiNo    		= $("#apiNo").val();
	obj.deployCd 		= $("#depolyCd").val();
	obj.verifiCd 		= $("#verifiCd").val();
	obj.regr     		= $("#regr").val();
	obj.processGubun    = e;
	
	obj.seq      		= $("#procListLayer option:selected").val();
	
	
	
	//alert(obj.apiNo);
	//alert(obj.deployCd);
	//alert(obj.verifiCd);
	//alert(obj.regr);
	
	
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvTempExcute.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			////조회항목 - SPC
			/* if(e == "insert"){
				$('#spcListLayer').empty(); 
				if(data != null  && data.spcList.length != 0){
					
					var spchtml = '<option value="">전체 카테고리</option>';
					
					$.each(data.spcList, function(index, item) { 
						
						spchtml +='<option value="' + item.apiSpcNo + '">' + item.apiNm + '</option>' ;
							
					}); //each끝
					
							
				}else{
					
					spchtml +='<option value="">카테고리 정보가 없습니다.</option>' ;
				}
				
				$('#spcListLayer').append(spchtml); 		
			} */
			
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
}



function hstList(){

	var obj = new Object();
	
	
	
	//alert(obj.apiNo);
	//alert(obj.deployCd);
	//alert(obj.verifiCd);
	//alert(obj.regr);
	
	
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvDeployApplyListAjax.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			
			$('#deployListLayer').empty(); 
			
			
			
			if(data != null  && data.nlist.length != 0){
				
				var spchtml = '';
				
				$.each(data.nlist, function(index, item) { 
					
					spchtml +='<option value="' +  item.deployApplySeq + '">' +  item.deployApplySeq  + '/' + item.seq + '/'+  item.apiNm + '/'+   item.sysNm + '</option>' ;
						
				}); //each끝
				
						
			}else{
				
				spchtml +='<option value="">등록된 프로세스가 없습니다.</option>' ;
			}
			
			$('#deployListLayer').append(spchtml); 	
			
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
}


function DeployExcute(e){

	var obj = new Object();
	

	
	obj.seq      		= $("#deployListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.sucessYn        = e;	
	obj.resultCd        = "000";
	obj.resultMsg       = "Success";
	
	

	
	$.ajax({
		url: '<c:url value="/api/deploy/mvCbDeployProc.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}


function DeployExcuteFail(e){

	var obj = new Object();
	

	
	obj.seq      		= $("#deployListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.sucessYn        = e;	
	obj.resultCd        = "900";
	obj.resultMsg       = "Fail";
	
	
	alert(obj.seq);
	
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvCbDeployProc.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alert(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}

//TB배포 성공 케이스 
function tbDeployExcuteSuccess(e){
	
	var obj = new Object();
	
	obj.deployProcSeq		=  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.sucessYn     		= e;
	obj.deployGb            = "T";
	obj.resultCd			= "000";
	obj.resultMsg       	= "Success";
	
	alert("TB 배포 성공 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvTbDeployProc.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}

//TB배포 성공 케이스 
function tbDeployExcuteFail(e){
	
	var obj = new Object();
	
	obj.deployProcSeq		=  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.sucessYn     		= e;
	obj.deployGb            = "T";
	obj.resultCd			= "900";
	obj.resultMsg       	= "Fail";
	
	alert("TB 배포 실패 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvTbDeployProc.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}

//검증관련 성공 케이스
function verifiSuccessCase(e){
	
	var obj = new Object();
	
	obj.deployProcSeq		=  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.resultCd			= "000";
	obj.resultMsg       	= "SUCCESS";
	obj.successYn			= e;
   
	
	alert("검증 성공 결과 등록 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvVerifiInsert.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}



//검증관련 실패 케이스
function verifiFailCase(e){
	
	var obj = new Object();
	
	obj.deployProcSeq		=  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.resultCd			= "900";
	obj.resultMsg       	= "FAIL";
	obj.successYn			= e;
   
	
	alert("검증 실패 결과 등록 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvVerifiInsert.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}

//검증 시작 
function verifiStart(){
	var obj = new Object();
	obj.deployProcSeq		=  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.resultCd = "900";
	obj.resultMsg = "FAIL";
	obj.verifiGb = "START";
	
	alert("검증 시작 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvVerifiStart.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}



//검증 시작 
function verifiEnd(){
	var obj = new Object();
	obj.deployProcSeq =  $("#procListLayer option:selected").val();   //DEPLOY_PROC SEQ
	obj.resultCd = "900";
	obj.resultMsg = "FAIL";
	obj.verifiGb = "END";
	
	alert("검증 시작 SEQ >>>" + obj.deployProcSeq)
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvVerifiStart.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alett(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}

//---------------------------//
function procExcuteDrm(){
	
var obj = new Object();
	
	obj.serviceGb = "procInsert";
	
	$.ajax({
		url: '<c:url value="/api/deploy/mvVerifiStart.do"/>', 
		type: 'POST',
		data :obj,
		success: function(data){
			alert(data.returnCode)
		},
		error:function(request,status,error){
	        alert("code:"+request.status+"\n"+"error:"+error);
    	}
	});
	
}
</script>
<br><br><br><br><br><br>
<div id="container">
  <div class="contents ">
    <div id="content">
			<h5 class="rTitleOneDep">배포 프로세스 입력</h5>
			<div class="date_setting">
			    <div class="searching_wrap">
			        <em class="pr10"> APINO  </em>
			        <div class="select_form">
			            <input type="text" size="50"  id="apiNo" style="width:140px;height:40px;">
			            
			        </div>
			        
			    </div>
			    <div class="searching_wrap">
			        <em class="pr10"> DeployCd </em>
			        <div class="select_form">
			            <input type="text" size="50"  id="depolyCd" style="width:140px;height:40px;">
			        </div>
			        <!-- <p>DEPLOY1010(배포전) DEPLOY1020(TB배포완료) DEPLOY1030(검증중) DEPLOY1035(검증반려) DEPLOY1040(검증완료) DEPLOY1050(배포대기중) DEPLOY1060(배포신청) DEPLOY1065(배포반려) DEPLOY1070(배포완료)</p> -->
			        <p>DEPLOY1010(TB배포전) DEPLOY1013(TB배포실패) DEPLOY1020(TB배포완료) DEPLOY1030(검증시작) DEPLOY1040(검증완료) DEPLOY1050(배포신청) DEPLOY1060(배포대기중) DEPLOY1063(배포실패) DEPLOY1065(배포반려) DEPLOY1070(배포완료)</p>
			    </div>
			    <div class="searching_wrap">
			        <em class="pr10"> verifiCd </em>
			        <div class="select_form">
			             <input type="text" size="50" id="verifiCd"  style="width:140px;height:40px;">
			        </div>
			        <!-- <p>VERIFI1010(검증전) VERIFI1020(검증중) VERIFI1030(검증완료)</p> -->
			        <p>VERIFI1010(검증시작코드) VERIFI1020(검증중) VERIFI1030(검증완료)</p>
			    </div>
			    <div class="searching_wrap">
			        <em class="pr10"> regr </em>
			        <div class="select_form">
			             <input type="text" size="50" id="regr"  style="width:140px;height:40px;">
			        </div>
			        <p>0001AopbSP0gd4nq/w24TWxPeg==(netesc)</p>
			    </div>
			    <div class="searching_wrap">
			        <em class="pr10">  </em>
			        <div class="select_form ">
			           
			            <button type="button" class="btn-lg btn_searching" id="btnProc"><span>프로세스등록</span></button>
			            <button type="button" class="btn-lg btn_searching" id="btnProcDrm"><span>프로세스등록(drm)</span></button>
			        </div>
			    </div>
			    <div class="searching_wrap">
			        <em class="pr10"> 프로세스 등록  </em>
			        <div class="select_form ">
			           
			          	APINO
			        </div>
			    </div>
			   
			</div>
			
			<h5 class="rTitleOneDep">배포 프로세스 진행</h5>
			<div class="date_setting">
			    <div class="searching_wrap">
			        <em class="pr10"> apiNo </em>
			        <div class="select_form">
			            <select style="width:500px;height:40px;" title="카테고리" id="procListLayer">>
			                    
			             </select>
			            <button type="button" class="btn-lg btn_searching" id="btnProcUp"><span>프로세스진행</span></button>
			             <button type="button" class="btn-lg btn_searching" id="btnProcUpTbDeploys"><span>TB프로세스(성공)</span></button>
			             <button type="button" class="btn-lg btn_searching" id="btnProcUpTbDeployf"><span>TB프로세스(실패)</span></button>
			             <button type="button" class="btn-lg btn_searching" id="btnProcVerifiStart"><span>검증시작</span></button>
			        		<button type="button" class="btn-lg btn_searching" id="btnProcUpTbVerifis"><span>검증(성공)</span></button>
			        		<button type="button" class="btn-lg btn_searching" id="btnProcUpTbVerifif"><span>검증(실패)</span></button>
			        		<button type="button" class="btn-lg btn_searching" id="btnProcVerifiEnd"><span>검증완료</span></button>
			        		
			        
			        </div>
			        
			    </div>
			   
			</div>
			<h5 class="rTitleOneDep">상용 배포  프로세스</h5>
			<div class="date_setting">
			    <div class="searching_wrap">
			        <em class="pr10"> apiNo </em>
			        <div class="select_form">
			            <select style="width:500px;height:40px;" title="카테고리" id="deployListLayer">>
			                    
			             </select>
			            <button type="button" class="btn-lg btn_searching" id="btnProcUpSucess"><span>성공프로세스</span></button>
			            <button type="button" class="btn-lg btn_searching" id="btnProcUpFail"><span>실패프로세스</span></button>
			        </div>
			        
			    </div>
			   
			</div>
			<h5 class="rTitleOneDep"></h5>
			<div class="date_setting">
			    <div class="searching_wrap">
			        <em class="pr10"> 프로세스 등록  </em>
			        <div class="select_form">
			           apiNo : 15190
			        </div>
			        
			    </div>
			   
			</div>
			
			
    </div>
  </div>
</div>
</t:layout>