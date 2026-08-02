<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
//  파일명  :write.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 개발 VM이용 가이드 페이지
--%>
<t:layout type="default" title="KT Open API - 개발 VM이용 가이드">

<script type="text/javascript">

//IPC Portal로 이동
function fnIpcGo(){
	$('#frm').attr({action:'http://ipc.kt.com', method:'post'}).submit();
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
			$("#tab3").addClass('"tabcontent current"');
			
			btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnLogin()">확인</button> ';
			btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
			btnClickEvent = fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req"/>');
			
			return;
		}else{
			$('#frm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();
		}		
		
		
		
	}else if(gb == "5"){ // ipc 포털 이동

		btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnIpcGo()">확인</button> ';
		btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		fnOpenLayer(btnHtm, '네트워크 접속 확인','<spring:message code="dev.request.ipc.msg"/>');
	
		
	
	}else if(gb == "6"){ // 기술지원 리스트 로그인 체크
		
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
<!-- 수정 페이지 이동 폼 -->

<!-- 등록 및 수정 폼 -->
<form name="frm" id="frm" method="post" >
	
</form>
<div id="container">
	<div class="sVisual sv_community">
		<div>
			<h2>개발 VM이용 가이드</h2>
			<p>개발환경 구축을 위한 VM신청 가이드</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a>Go home</a> <span>></span> 개발환경가이드 <span>></span> 개발vm신청가이드</div>

			<div id="content">
                   <!-- guide_wrap -->
                    <div class="guidedev_wrap">
                        <!-- guide_wrap -->
                        <div class="guide_wrap useguide_wrap">
                            <ul class="tab_list guidetab">
                                <li data-tab="tab1" class="guide01 current"  onclick="fnVMApply('1')" title="개발VM 신청 및 기술지원"><span>개발VM 신청 및 <br> 기술지원</span></li>
                                <li data-tab="tab1" class="guide02" onclick="fnVMApply('2')" title="Test Data 등록 요청"><span>Test Data <br> 등록 요청</span></li>
                                <li data-tab="tab1" class="guide03" onclick="fnVMApply('3')" title="SDK 가이드 및 다운로드"><span>SDK 가이드 및 <br> 다운로드</span></li>
                                <li data-tab="tab1" class="guide04" onclick="fnVMApply('4')"  title="기술 지원 요청"><span>기술 지원 요청</span></li>
                            </ul>

                            <div id="tab1" class="tabcontent current">
                                <h6>개발VM 신청 및 기술지원</h6>

                                <div class="useGuide_content">
                                    <h4><span>개발VM 신청 가이드</span></h4>
                                    <!--  List start -->
                                    <section>
                                       <h5>개발 플랫폼(VM)개요</h5>
                                       <div>
                                           <p>서비스의 신속한 개발 착수와 PoC를 위한 플랫폼 개발 환경의 인프라가 필요한 서비스를 위하여 Develop VM을 제공하며 IPC Portal를 통하여 신청함</p>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>프로세스 구성도</h5>
                                       <div>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0101.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <p>사내 클라우드 통합관리</p>
                                                <dl>
                                                    <dt>VM 신청 & 구축</dt>
                                                    <dd>클라우드 신청 (사업부서)</dd>
                                                    <dd>클라우드 설계 (플랫폼 설계담당)</dd>
                                                    <dd>클라우드 구축 (구축 담당자)</dd>
                                                    <dd>접근제어 & 물리방화벽& 소프트웨어설치 (사업부서,ITSM)</dd>
                                                </dl>
                                                <dl>
                                                    <dt>VM 반납 신청 & 삭제</dt>
                                                    <dd>클라우드 변경 (사업부서)</dd>
                                                    <dd>클라우드 설계 (플랫폼 설계담당)</dd>
                                                    <dd>클라우드 반납 (ITO 미위탁구축 담당자)</dd>
                                                </dl>
                                                <dl>
                                                    <dt>단위서비스 리소스 이관</dt>
                                                    <dd>신규 서비스 생성 (사업부서)</dd>
                                                    <dd>리소스 이관 신청 (사업부서)</dd>
                                                    <dd>검토 및 처리 (플랫폼 설계담당)</dd>
                                                </dl>
                                                <dl>
                                                    <dt>단위서비스 리소스 이관</dt>
                                                    <dd>리소스 사용현황 (서버/디스크)</dd>
                                                    <dd>서버 이관 이력</dd>
                                                    <dd>설계자 권한 관리 (추가/삭제)</dd>
                                                    <dd>리소스 사용 이력 관리 (신청자/설계자)</dd>
                                                    <dd>통계자료추출</dd>
                                                    <dd>프로세스 LeadTime 통계</dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>신청 화면</h5>
                                       <div>
                                           <p>서비스의 신속한 개발 착수와 PoC를 위한 플랫폼 개발 환경의 인프라가 필요한 서비스를 위하여 Develop VM을 제공하며 IPC Portal를 통하여 신청함</p>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0102.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <dl>
                                                    <dt>1. 기본정보 입력</dt>
                                                    <dd>서비스 관련 기본정보 입력</dd>
                                                </dl>
                                                <dl>
                                                    <dt>2. 인프라구성</dt>
                                                    <dd>서비스에서 필요한 개발 플랫폼 Spec 입력</dd>
                                                </dl>
                                                <dl>
                                                    <dt>3. Loadbalancer 구성</dt>
                                                    <dd>Loadbalancer의 구성 정보 입력</dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>상세 신청 가이드</h5>

                                       <div>
                                           <span>대상</span>
                                           <p>Develop 환경의 인프라가 필요한 IPC포탈 사용자</p>
                                           <span>리소스 현황</span>
                                           <p>구축이 완료되어 사용중인 인프라의 현황 조회</p>
                                           <span>리소스 신청</span>
                                           <p>
                                                <em>기존의 ‘클라우드 신청’과 동일하게 사용자가 인프라를 구성하여 설계를 신청한다.</em>
                                                <em>사용자는 본인이 작성한 신청서의 목록을 확인할 수 있다.</em>
                                           </p>
                                           
                                           <span>리소스 신청서 목록</span>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0103.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <dl>
                                                    <dt>- 신청중 : </dt>
                                                    <dd>신청을 진행중인 단계</dd>
                                                </dl>
                                                <dl>
                                                    <dt>- 설계요청완료 : </dt>
                                                    <dd>신청을 완료하였지만, 설계자가 확인하지 않아 신청을 취소/재신청할 수 있는 단계</dd>
                                                </dl>
                                                <dl>
                                                    <dt>- 설계중 :</dt>
                                                    <dd>설계자가 신청내용을 확인중인 단계</dd>
                                                </dl>
                                                <dl>
                                                    <dt>- 설계반려 : </dt>
                                                    <dd>설계 반려처리 되어 사유 확인 후 재신청 가능</dd>
                                                </dl>
                                                <dl>
                                                    <dt>- 구축중 : </dt>
                                                    <dd>설계 승인처리 되어 클라우드 구축으로 넘어간 단계</dd>
                                                </dl>
                                                <dl>
                                                    <dt>- 구축완료 : </dt>
                                                    <dd>‘리소스 현황’에서 확인 가능하며 변경신청 가능</dd>
                                                </dl>
                                            </div>

                                            <span>신청기본정보</span>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0104.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <dl>
                                                    <dd>- 신청자의 default는 본인이지만, 변경버튼을 이용하여 타인을 지정할 수 있다.</dd>
                                                    <dd>- 개발플랫폼명은 기존의 ‘서비스인프라명’기능을 한다.</dd>
                                                    <dd>- 서비스오픈예정일과 구축희망일은 현재일로부터 7일 이후여야 한다.</dd>
                                                </dl>
                                            </div>

                                            <span>인프라구성</span>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0105.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <dl>
                                                    <dd>- NetworkSArea, OsType, Spec, ServerType, 용도를 입력한 후 추가를 클릭한다.</dd>
                                                    <dd>- 서버를 추가한 후, 데이터볼륨설정 기능을 이용하여 Single Storage를 생성할 수 있다.</dd>
                                                    <dd>- 동일한 구성의 서버를 1대 추가할 때는 ‘복제’, 여러대의 서버나 데이터 볼륨을 포함하여 추가할 때는 ‘고급복제’기능을 이용한다.</dd>
                                                </dl>
                                            </div>

                                            <span>LoadBalancer 구성</span>
                                           <p><img src="/apidev/resources/images/guideimg/devguide0106.png" alt="별도 첨부"></p>
                                           <div class="hideText">
                                                <dl>
                                                    <dd>- NetworkSArea, Algorithm, Port 입력 후 저장을 클릭한다.</dd>
                                                    <dd>- RedirectionMode는 DSR, Protocol은 ANY로 고정되어 있다.</dd>
                                                    <dd>- 추가된 LB에는 Dummy VM이 자동으로 생성 및 연결되며, 삭제시 함께 삭제된다.</dd>
                                                    <dd>- LB추가 후 밸런싱 대상 설정 기능을 이용할 수 있다.</dd>
                                                    <dd>- 신청사항을 입력 후 ‘신청하기’ 버튼으로 설계를 요청한다.</dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>
                                </div>

                                <div class="btn_set">
                                    <button type="button" title="신청하기" class="btn-lg2 btn_black" onclick="fnVMApply('5')"><span>신청하기</span></button>
                                    <button type="button" title="기술지원요청" class="btn-lg2 btn_black" onclick="fnVMApply('6')"><span>기술지원요청</span></button>
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
