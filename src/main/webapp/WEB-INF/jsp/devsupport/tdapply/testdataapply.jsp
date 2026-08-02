<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- 
//  파일명  :write.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : TEST DATA 등록 요청 페이지
--%> 

<t:layout type="default">
<script type="text/javascript">

//목록 조회 페이지로 이동
function fnGoDevSupport(){

	var btnHtm = "";

	if('${userJVo}' == null || '${userJVo.mbrId}' == null  || '${userJVo.mbrId}' == ''  )   {
		
		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
	}else{
		$('#frm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();
	}
		
	
	
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


function fnGoTDPDown(){
	
	
	var btnHtm = "";

	if('${userJVo}' == null || '${userJVo.mbrId}' == null  || '${userJVo.mbrId}' == ''  )   {
		
		if('${userJVo}' == null || '${userJVo.mbrId}' == null  || '${userJVo.mbrId}' == ''  )   {
			$("#tab3").addClass('"tabcontent current"');
			
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
			btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
			btnClickEvent = fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
			
			return;
		}else{
			$('#frm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();
		}		
		
		
	}else{
		location.href = "/apidev/file/fileDownLoad.do?filePath=/upload//2017/12/27&downType=FILTYP1040&orgFileName=TEST_DATA_PACK_1.0.0.zip&saveFileName=437684d5-30bc-4cbe-bf5d-20ffdddbaf27";

	}
}

</script>
<!-- 수정 페이지 이동 폼 -->

<!-- 등록 및 수정 폼 -->
<form name="frm" id="frm" method="post" >
	
</form>
<div id="container">
	<div class="sVisual sv_community">
		<div><h2>TEST DATA 등록 가이드 </h2>
			<p>개발 연동을 위한  TEST DATA 등록 방법에 대하여 가이드 합니다.</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a href="javascript:;">Go home</a> <span>></span> 개발환경가이드 <span>></span> TEST DATA등록 가이드</div>

			<div id="content">
                   <!-- guide_wrap -->
                    <div class="guidedev_wrap">
                        <!-- guide_wrap -->
                        <div class="guide_wrap useguide_wrap">
                            <ul class="tab_list guidetab">
                                <li data-tab="tab2" class="guide02"  onclick="fnVMApply('1')" title="개발VM 신청 및 기술지원"><span>개발VM 신청 및 <br> 기술지원</span></li>
                                <li data-tab="tab2" class="guide02 current" onclick="fnVMApply('2')" title="Test Data 등록 요청"><span>Test Data <br> 등록 요청</span></li>
                                <li data-tab="tab2" class="guide02" onclick="fnVMApply('3')" title="SDK 가이드 및 다운로드"><span>SDK 가이드 및 <br> 다운로드</span></li>
                                <li data-tab="tab2" class="guide02" onclick="fnVMApply('4')"  title="기술 지원 요청"><span>기술 지원 요청</span></li>
                            </ul>

                            <div id="tab2" class="tabcontent current">
                                <h6>Test Data 등록 요청</h6>

                                <div class="useGuide_content">
                                    <h4><span>Test Data 등록 요청</span></h4>
                                    <!--  List start -->
                                    <section>
                                       <h5>KT SHUB TEST DATA등록</h5>
                                       <div>
                                           <span>개요</span>
                                           <p>KT Infra에서 보유한 다양한 유무선 기능 및 자원 활용을 위하여 제공되는 SHUB API를 테스트하기 위한 가상의 DATA를 의미합니다. 서비스 개발 시 API의 
사용에 대한 테스트가 필요한 경우 임의의 데이터를 등록하여 API호출에 대한 응답 값을 확인할 수 있도록 지원 합니다.</p>
                                       </div>
                                    </section>

                                    <section>
                                       <div>
                                           <span>등록 시 유의사항</span>
                                           <p>1) 작성규칙
    해당 Enabler System에서 제공하는 연동 규격서를 참조하여 API에서 사용되는 업무 절차 및 파라미터명을 확인한 후 양식에 개인정보가 없는 가상의
    데이터로 작성하도록 하며, 최소한의 정보를 등록 해야 합니다.</p>
                                           <p>2) 테스트 의도
    테스트 데이터 등록 요청 시 목적을 “기술지원 메뉴”의 입력 폼에 입력 합니다.</p>
                                            <p>3) 적용 시간
    등록 요청 테스트 데이터에 대한 적용은 접수일로부터 해당 Enabler System에 등록되기까지 약 3일(working day기준)정도 소요 됩니다.</p>
                                            <p>4) 기타
    OIF_750, OIF_754와 같은 N-STEP 연동 API는 테스트 데이터 등록 요청 시, 가상 정보가 아닌 실제 폰번호 정보로 DATA를 등록해야 하며, DATA유지기간은
    1일입니다. 등록된 테스트 데이터의 일부는 사용 기간이 정해져 있으며 연장이 필요한 경우 추가 요청 해야 합니다.</p>
                                       </div>
                                    </section>

                                    <section>
                                       <div>
                                           <span>TEST DATA등록 Enabler</span>
                                           <p>- SHUB : 사용자 계약 정보 API<br>
                                            - CAPRI : 인증 API<br>
                                            - N–STEP : 부가서비스 API<br>
                                            - LBS : 측위 API
                                            </p>
                                       </div>
                                    </section>
                                    
                                    <section>
                                       <div>
                                           <span>TEST DATA PACK </span>
                                           <p>제공되는 SDK와 시뮬레이터를 이용하여 바로 연동 테스트가 가능하도록 TEST DATA가 포함된 JSON규격의 TEST DATA PACK를 제공합니다.   <br>
                                            - 제공 플랫폼  : SHUB, IotMakers, 쿠폰 <br>
                                            - 버젼 정보 : TEST DATA PACK Ver 1.0.0<br>
                                            
                                            </p>
                                       </div>
                                    </section>
                                </div>

                                <div class="btn_set">
                                    <button type="button" title="Test Data 등록 신청" class="btn-lg2 btn_black" onclick="fnGoDevSupport()" ><span>Test Data 등록 신청</span></button>
                               		<button type="button" title="Test Data PACK" class="btn-lg2 btn_black" onclick="fnGoTDPDown()" ><span>TEST DATA PACK다운로드</span></button>
                               
                                </div>
                                
                                
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
