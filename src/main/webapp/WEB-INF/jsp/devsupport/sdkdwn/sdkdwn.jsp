<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- 
//  파일명  :write.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 통합 SDK다운로드 관리
--%> 

<t:layout type="default">
<script type="text/javascript">

//목록 조회 페이지로 이동
function fnGoDevSupport(){

	$('#frm').attr({action:c_url+'devsupport/devsupport/devSupportList.do', method:'post'}).submit();

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
		
	}
	
	
}


//youtube 영상 
var tag = document.createElement('script');

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
var player;
function onYouTubeIframeAPIReady() {
  player = new YT.Player('mv_boxing', {
    height: '590',
    width: '1160',
    autoplay:'false',
    videoId: 'J2OvpmX0gKw',
    events: {
      // 'onReady': onPlayerReady,
      // 'onStateChange': onPlayerStateChange
      //https://youtu.be/J2OvpmX0gKw 
    }
  });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
  event.target.playVideo();
}

// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
var done = false;
function onPlayerStateChange(event) {
  if (event.data == YT.PlayerState.PLAYING && !done) {
    setTimeout(stopVideo, 6000);
    done = true;
  }
}

function stopVideo() {
  player.stopVideo();
}

function fnSdkDown(dwnUrl){

	location.href = "/apidev" + dwnUrl;
}

$(".mv_close, .dim_layer").click(function(){
  player.stopVideo();
});

</script>
<!-- 수정 페이지 이동 폼 -->

<!-- 등록 및 수정 폼 -->
<form name="frm" id="frm" method="post" >
	
</form>



<div id="container">
	<div class="sVisual sv_community">
		<div>
			<h2>통합SDK가이드 및 다운로드</h2>
			<p>KT OPEN API 연동을 위한 SDK가이드 및 다운로드</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location"><a href="javascript:;">Go home</a> <span>></span> 개발환경가이드 <span>></span> SDK이용가이드</div>

			<div id="content">
                   <!-- guide_wrap -->
                    <div class="guidedev_wrap">
                        <!-- guide_wrap -->
                        <div class="guide_wrap useguide_wrap">
                            <ul class="tab_list guidetab">
                                <li data-tab="tab3" class="guide01"  onclick="fnVMApply('1')" title="개발VM 신청 및 기술지원"><span>개발VM 신청 및 <br> 기술지원</span></li>
                                <li data-tab="tab3" class="guide02" onclick="fnVMApply('2')" title="Test Data 등록 요청"><span>Test Data <br> 등록 요청</span></li>
                                <li data-tab="tab3" class="guide03 current" onclick="fnVMApply('3')" title="SDK 가이드 및 다운로드"><span>SDK 가이드 및 <br> 다운로드</span></li>
                                <li data-tab="tab3" class="guide04" onclick="fnVMApply('4')"  title="기술 지원 요청"><span>기술 지원 요청</span></li>
                            </ul>

                           

                            <div id="tab3" class="tabcontent current">
                                <h6>SDK 가이드 및 다운로드</h6>

                                <div class="useGuide_content">
                                    <h4><span>SDK 가이드 및 다운로드</span></h4>
                                    <!--  List start -->
                                    <section>
                                       <h5>KT Open API SDK 에 대하여</h5>
                                       <div>
                                            <span>KT Open API 란</span>
                                            <p>KT Infra에서 보유한 다양한 유무선 기능 및 자원 제공을 위한 Interface를 의미 합니다.
                                            사용자는 KT Open API를 이용하여 새로운 수익 창출이 가능한 비즈니스 개발이 가능합니다. 
                                            현재 SHUB, IotMakers, Olleh MAP, gigaGenie, ucloud 등의 서비스에서 Open API를 제공하고 있으며, 향후 지속적으로 증가할 것으로 예상됩니다.</p>
                                            
                                            <p><img src="/apidev/resources/images/guideimg/devguide0301.png" alt="별도 첨부"></p>
                                            <div class="hideText">
                                                <dl>
                                                    <dt>OpenAPI를 제공하는 KT 시스템</dt>
                                                    <dd>SHUB</dd>
                                                    <dd>IoTMakers</dd>
                                                    <dd>ucloud biz</dd>
                                                    <dd>GIGA Genie</dd>
                                                </dl>
                                            </div>

                                            <span>KT Open API SDK 란</span>
                                            <p>KT에서 제공하는 Open API 연동 개발을 위한 통합된 개발 KIT를 의미 합니다. 아래의 목표를 지향 합니다. </p>
                                            <p><img src="/apidev/resources/images/guideimg/devguide0302.png" alt="별도 첨부"></p>
                                            <div class="hideText">
                                                <dl>
                                                    <dt>연동 플랫폼의 독립적</dt>
                                                    <dd>KT에서 제공하는 API중 Restful API와
                                                    SOAP API 에 대하여
                                                    플랫폼에 상관없이 하나의 SDK를 
                                                    이용하여 범용적으로 사용할 수 있습니다.</dd>
                                                </dl>
                                                <dl>
                                                    <dt>규격의 유연성</dt>
                                                    <dd>인프라에서 제공하는 규격에 대하여
                                                    SDK의 변환 기능을 이용하여 서비스에서
                                                    원하는 형식(JSON, XML, MAP, STRING)으로
                                                    출력할 수 있으며, 입력값에 대해서도
                                                    자유로운 규격(JSON, XML, STRING)으로
                                                    입력이 가능합니다. </dd>
                                                </dl>
                                                <dl>
                                                    <dt>개발의 편리성</dt>
                                                    <dd>SDK에서 제공하는 기능들을
                                                    하나의 Library로 제공하여
                                                    사용이 효율적인 개발이 가능하도록 
                                                    지원 합니다.</dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>KT Open API SDK 구성 및 지원 환경 </h5>
                                       <div>
                                            <span>SDK 구성 요소</span>
                                            <p>KT의 SDK는 아래의 구성으로 이루어져 있으며 통합 패키지 형태 및 항목별 다운로드가 가능합니다.</p>
                                            <p><img src="/apidev/resources/images/guideimg/devguide0303.png" alt="별도 첨부"></p>
                                            <div class="hideText">
                                                <dl>
                                                    <dd>API 연동 Library </dd>
                                                    <dd>API 연동 Sample Source</dd>
                                                    <dd>API 연동 Document</dd>
                                                    <dd>SDK Guide Video </dd>
                                                    <dd>API Test Console</dd>
                                                    <dd>SHUB API 입력 Sample 입력 전문</dd>
                                                    <dd>지원환경 : JDK 1.6 이상</dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>KT Open API SDK 이용가이드(SDK) <button type="button" title="신청하기" class="btn btn_black"  onclick="showMV(this, '.mv-wrap');return false;"><span>가이드영상 보기</span></button></h5>
                                       <div>
                                            <span>Library 추가</span>
                                            <p>KT의 SDK는 아래의 구성으로 이루어져 있으며 통합 패키지 형태 및 항목별 다운로드가 가능합니다.</p>
                                            <p><img src="/apidev/resources/images/guideimg/devguide0304.png" alt="별도 첨부"></p>
                                            <div class="hideText">
                                                <dl>
                                                    <dt>1) Build Path창 오픈</dt>
                                                    <dd>[프로젝트] – [Build Path] – [Configure Build Path] 를 클릭하여 Properties 창을 오픈 합니다. </dd>
                                                </dl>
                                                <dl>
                                                    <dt>2) User Library 창 오픈</dt>
                                                    <dd>[Add Library] 클릭 후 - [User Library] 클릭</dd>
                                                </dl>
                                                <dl>
                                                    <dt>3) Jar파일 추가</dt>
                                                    <dd>SDK jar파일을 추가한 후 완료 합니다. </dd>
                                                </dl>
                                                <dl>
                                                    <dt>4) 정상 등록 여부 확인</dt>
                                                    <dd>SDK jar파일을 추가한 후 완료 합니다. </dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>

                                    <section>
                                       <h5>KT Open API SDK 이용가이드(TEST Console)</h5>
                                       <div>
                                            <span>TEST Console이용 가이드</span>
                                            <p><img src="/apidev/resources/images/guideimg/devguide0305.png" alt="별도 첨부"></p>
                                            <div class="hideText">
                                                <dl>
                                                    <dt>1) 정상 등록 여부 확인</dt>
                                                    <dd>TEST Console을 다운 받은 후 사용이 가능하도록 exe파일로 변경합니다.  </dd>
                                                </dl>
                                                <dl>
                                                    <dt>2) 화면 설명</dt>
                                                    <dd>
                                                        <ol>
                                                            <li><strong>1. 연동 시스템명</strong>
                                                                <span>1. Common  : 일반적인 SOAP, Restful에 대하여 범용적으로 사용.
                                                                (플랫폼별 특이 케이스에 대한 안전성을 보장하지 않음)</span>
                                                                <span>2. Shub : Shub의 특성을 반영하여 shub연동에 대한 안전성을 확보</span>
                                                                <span>3. Iot : IotMakers의 특성을 반영하여 shub연동에 대한 안전성을 확보</span>
                                                            </li>
                                                            <li><strong>2. 연동 방식</strong>
                                                                <span>SOAP과 Restful을 지원함 </span>
                                                            </li>
                                                            <li><strong>3. 연동 URL</strong>
                                                                <span>각각의 플랫폼에서 제공한 EndPoint URL입력</span>
                                                            </li>
                                                            <li><strong>4. 입력 파라미터</strong>
                                                                <span>API별 Request에 인자로 전달되는 값 입력</span>
                                                            </li>
                                                            <li><strong>5. Hearder값 입력</strong>
                                                                <span>플랫폼에서 정의한 Hearder값 입력</span>
                                                            </li>
                                                            <li><strong>6. Variable 입력</strong>
                                                                <span>API명, 인증 정보(SOAP)등의 기본정보 입력</span>
                                                            </li>
                                                            <li><strong>7. Output </strong>
                                                                <span>연동 완료된 내용 확인이 가능함 </span>
                                                            </li>
                                                        </ol>
                                                    </dd>
                                                </dl>
                                            </div>
                                       </div>
                                    </section>
                                    
                                    <section>
                                       <h5>KT Open API SDK 상세 spec</h5>
                                       <div>
                                            <p>SDK를 이용하여 연동을 진행할 경우 연동 DATA format은 XML, JSON, key-value 중 개발 환경과 맞는 형태로 입력 가능하며 API를 제공하는  플랫폼에 맞는
                                            format으로 변환되어 전송 됩니다. 아래 가이드는 실제 연동 이 이루어지는 과정을 shub API를 예를 들어 가이드 하여 사용자의 이해를 돕고 효율 적인 개발이 
                                            가능하도록 합니다. </p>
                                            <span>Requeset DATA Format</span>
                                            <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 입력 가능한 format에 대하여 설명 합니다.</p>
                                            <p>1) Request Base 규격</p>
                                            <div class="pkg_board">
                                                <!-- table start -->
                                                <table class="table-vw view_style">
                                                    <caption>table Table</caption>
                                                    <colgroup>
                                                        <col style="width:45%;">
                                                        <col style="width:40%;">
                                                        <col style="width:15%;">
                                                        <col style="width:30%;">
                                                    </colgroup>
<!-- 
                                                    <thead>
                                                        <tr>
                                                            <th scope="col"><div>파라미터명</div></th>
                                                            <th scope="col"><div>설명</div></th>
                                                            <th scope="col"><div>필수여부</div></th>
                                                            <th scope="col"><div>비고</div></th>
                                                        </tr>
                                                    </thead>
-->
                                                    <tbody>
                                                        <tr>
                                                            <td><div>TRANSACTIONID</div></td>
                                                            <td><div>시스템 발급 일련번호</div></td>
                                                            <td><div>N</div></td>
                                                            <td><div>VOC 응대용도</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>SEQUENCENO</div></td>
                                                            <td><div>시스템 내부 구간순서</div></td>
                                                            <td><div>N</div></td>
                                                            <td><div>해당없음</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>USERID</div></td>
                                                            <td><div>특정CP가 사용하는 ID</div></td>
                                                            <td><div>N</div></td>
                                                            <td><div>해당없음</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>SCREENID</div></td>
                                                            <td><div>특정 CP가 사용되는 ID</div></td>
                                                            <td><div>N</div></td>
                                                            <td><div>해당없음</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>Credt_Id</div></td>
                                                            <td><div>Credential ID</div></td>
                                                            <td><div>Y</div></td>
                                                            <td><div>해당없음</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>User_Name</div></td>
                                                            <td><div>로그인 ID,</div></td>
                                                            <td><div>Y</div></td>
                                                            <td><div>해당없음</div></td>
                                                        </tr>
                                                        <tr>
                                                            <td><div>Subscpn_Type_Cd</div></td>
                                                            <td><div>계약 유형 코드</div></td>
                                                            <td><div>N</div></td>
                                                            <td><div>99일 경우 고객정보만 반환</div></td>
                                                        </tr>
                                                    </tbody>
                                                </table>

                                                <!-- // table End -->
                                            </div>

                                            <p>2) SDK이용시 입력 format</p>
                                            <div class="pkg_board">
                                                <!-- table start -->
                                                <table class="table-vw code_style">
                                                    <caption>table Table</caption>
                                                    <colgroup>
                                                        <col >
                                                    </colgroup>

                                                    <tbody>
                                                        <tr>
                                                            <th><div>JSON 포멧</div></th>                               
                                                        </tr>
                                                        <tr>
                                                            <td><pre>{"Credt_Id":"272833896","User_Name":"z!50674007900","Subscpn_Type_Cd":"2"}
☞ 호출 method => shubClient.setApiParamsJson(인자값)</pre></td>
                                                        </tr>
                                                        <tr>
                                                            <th><div>XML포맷</div></th>                               
                                                        </tr>
                                                        <tr>
                                                            <td><pre>&lt;Credt_Id&gt;272833896&lt;/Credt_Id&gt;
&lt;User_Name&gt;z!50674007900&lt;/User_Name&gt;
&lt;Subscpn_Type_Cd&gt;2&lt;/Subscpn_Type_Cd&gt;

☞ 호출 method = &gt; shubClient.setApiParamsXml(인자값)
</pre></td>
                                                        </tr>
                                                        <tr>
                                                            <th><div>Key – Value</div></th>                               
                                                        </tr>
                                                        <tr>
                                                            <td><pre>setApiParam(“Credt_Id”) = “272833896”
setApiParam(“User_Name”) = “z!50674007900”
setApiParam(“Subscpn_Type_Cd”) = “2”

☞ 호출 method => setApiParam(“변수명”) = “인자값”
</pre></td>
                                                        </tr>
                                                    </tbody>
                                                </table>

                                                <!-- // table End -->
                                            </div>

                                            <p>3) SDK 내에서 변환되어 SHUB로 전송 되는 포맷</p>
                                            <div class="pkg_board">
                                                <!-- table start -->
                                                <table class="table-vw code_style">
                                                    <caption>table Table</caption>
                                                    <colgroup>
                                                        <col >
                                                    </colgroup>

                                                    <tbody>
                                                        <tr>
                                                            <th><div>XML 포멧</div></th>                               
                                                        </tr>
                                                        <tr>
                                                            <td><pre>&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:sdp="http://kt.com/sdp"&gt;
    &lt;soapenv:Header&gt;
        &lt;wsse:Security soapenv:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"&gt;
            &lt;wsse:UsernameToken wsu:Id="UsernameToken-4"                
                             xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"&gt;
    &lt;wsse:Username&gt;AII5920037222QFXBRT&lt;/wsse:Username&gt;
                &lt;wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText"&gt;
                TBK5920037222QDYTUT&lt;/wsse:Password&gt;
            &lt;/wsse:UsernameToken&gt;
        &lt;/wsse:Security&gt;
    &lt;/soapenv:Header&gt;
    &lt;soapenv:Body&gt;
        &lt;sdp:getPartyAndSubInfoBySubTypeCDRequest&gt;
            &lt;sdp:Credt_Id&gt;272833896&lt;/sdp:Credt_Id&gt;
            &lt;sdp:SEQUENCENO&gt;&lt;/sdp:SEQUENCENO&gt;
            &lt;sdp:USERID&gt;&lt;/sdp:USERID&gt;
            &lt;sdp:SCREENID&gt;&lt;/sdp:SCREENID&gt;
            &lt;sdp:User_Name&gt;z!50674007900&lt;/sdp:User_Name&gt;
            &lt;sdp:Subscpn_Type_Cd&gt;2&lt;/sdp:Subscpn_Type_Cd&gt;
        &lt;/sdp:getPartyAndSubInfoBySubTypeCDRequest&gt;
    &lt;/soapenv:Body&gt;
&lt;/soapenv:Envelope&gt;
</pre></td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                        </div>

                                        <span>Requeset DATA Format</span>
                                        <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 리턴되는 출력값에 대하여 설명합니다. </p>
                                        <div class="pkg_board">
                                            <!-- table start -->
                                            <table class="table-vw code_style">
                                                <caption>table Table</caption>
                                                <colgroup>
                                                    <col >
                                                </colgroup>

                                                <tbody>
                                                    <tr>
                                                        <th><div>Response status</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td><pre>-. Duration time: 1481 ms
-. Response code: 200 (HTTP_OK)
-. Header: 
   -> null : [HTTP/1.1 200 OK]
   -> Connection : [close]
   -> Content-Length : [1489]
   -> Date : [Mon, 18 Dec 2017 13:47:20 GMT]
-. Response charset: UTF-8

☞ 호출 method => .getApiBody()
</pre></td>
                                                    </tr>
                                                    <tr>
                                                        <th><div>Base Response Format</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td><pre>&lt;?xml version="1.0" encoding="utf-8"?&gt;
&lt;env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:oas="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-
secext-1.0.xsd"&gt;
&lt;env:Header/&gt;
&lt;env:Body&gt;
&lt;sdp:getPartyAndSubInfoBySubTypeCDResponse xmlns:n1="http://kt.com/sdp_myolleh2" xmlns:sdp="http://kt.com/sdp"&gt;
&lt;sdp:TRANSACTIONID&gt;1cc29a82-e288-44f7-XXXXXXXXXXXXXX&lt;/sdp:TRANSACTIONID&gt;
&lt;sdp:SEQUENCENO&gt;9999&lt;/sdp:SEQUENCENO&gt;
&lt;sdp:returnCode&gt;1&lt;/sdp:returnCode&gt;
&lt;sdp:returnDesc&gt;Success&lt;/sdp:returnDesc&gt;
&lt;sdp:ListofParty&gt;
  &lt;n1:arrayofparty&gt;
    &lt;n1:Party_Detail_Type_Cd&gt;01&lt;/n1:Party_Detail_Type_Cd&gt;
    &lt;n1:Birth_Date&gt;11111212&lt;/n1:Birth_Date&gt;
    &lt;n1:Party_Idtf_Number_Cd&gt;01&lt;/n1:Party_Idtf_Number_Cd&gt;
    &lt;n1:Party_Idtf_Number/&gt;&lt;n1:Customer_Class_Cd/&gt;
    &lt;n1:Ipin_CI&gt;PNxxxxxxxxxxxx&lt;/n1:Ipin_CI&gt;
    &lt;n1:ListofPartyMap&gt;
      &lt;n1:listofpartymap&gt;
        &lt;n1:Source_System_Cd&gt;01&lt;/n1:Source_System_Cd&gt;
        &lt;n1:Source_System_Bind_Id&gt;001111111C&lt;/n1:Source_System_Bind_Id&gt;
      &lt;/n1:listofpartymap&gt;
    &lt;n1:listofpartymap&gt;
      &lt;n1:Source_System_Cd&gt;04&lt;/n1:Source_System_Cd&gt;
      &lt;n1:Source_System_Bind_Id&gt;1111111111&lt;/n1:Source_System_Bind_Id&gt;
    &lt;/n1:listofpartymap&gt;
    &lt;n1:listofpartymap&gt;
      &lt;n1:Source_System_Cd&gt;20&lt;/n1:Source_System_Cd&gt;
      &lt;n1:Source_System_Bind_Id&gt;AAAAAAAA&lt;/n1:Source_System_Bind_Id&gt;
    &lt;/n1:listofpartymap&gt;
    &lt;/n1:ListofPartyMap&gt;
      &lt;n1:Subscpn_Over_Yn&gt;N&lt;/n1:Subscpn_Over_Yn&gt;
  &lt;/n1:arrayofparty&gt;
  &lt;/sdp:ListofParty&gt;
&lt;/sdp:getPartyAndSubInfoBySubTypeCDResponse&gt;
&lt;/env:Body&gt;
&lt;/env:Envelope&gt;

☞ 호출 method => .getHttpR☞ 호출 method => .getApiBody()

</pre></td>
                                                    </tr>
                                                    <tr>
                                                        <th><div>JSON Format</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td><pre>{
  "sdp:ListofParty": {"n1:arrayofparty": {
    "n1:Party_Idtf_Number_Cd": "01",
    "n1:Subscpn_Over_Yn": "N",
    "n1:Party_Detail_Type_Cd": "01",
    "n1:Party_Idtf_Number": "",
    "n1:Customer_Class_Cd": "",
    "n1:Ipin_CI": "PNxxxxxxxxxxxx",
    "n1:ListofPartyMap": {"n1:listofpartymap": [
      {
        "n1:Source_System_Cd": "01",
        "n1:Source_System_Bind_Id": "001111111C"
      },
      {
        "n1:Source_System_Cd": "04",
        "n1:Source_System_Bind_Id": "1111111111"
      },
      {
        "n1:Source_System_Cd": "20",
       "n1:Source_System_Bind_Id": "AAAAAAAA"
      }
    ]},
    "n1:Birth_Date": "19890126"
  }},
  "sdp:TRANSACTIONID": "1cc29a82-e288-44f7-XXXXXXXXXXXXXX",
  "sdp:SEQUENCENO": "9999",
  "sdp:returnDesc": "Success",
  "sdp:returnCode": "1"
</pre></td>
                                                    </tr>
                                                    <tr>
                                                        <th><div>JSON Format</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td><pre>{sdp:ListofParty={n1:arrayofparty={n1:Party_Idtf_Number_Cd=01, n1:Subscpn_Over_Yn=N, 
n1:Party_Detail_Type_Cd=01, n1:Party_Idtf_Number=, n1:Customer_Class_Cd=, 
n1:Ipin_CI=fPN4SYWrn2tDceDKkOLa/RNX4aTOWmqNJFQGuvniGA4Xs+PUWlf0ypGWVsGkL4tW7YqeIX8BSP/rPrY7d
mQ1LA==, n1:ListofPartyMap={n1:listofpartymap=[{n1:Source_System_Cd=01, 
n1:Source_System_Bind_Id=0018742658C}, {n1:Source_System_Cd=04, n1:Source_System_Bind_Id=376811595}, 
{n1:Source_System_Cd=20, n1:Source_System_Bind_Id=AF3KZX4ISO8}]}, n1:Birth_Date=19890126}}, 
sdp:TRANSACTIONID=1cc29a82-e288-44f7-9574-365623dc01d3, sdp:SEQUENCENO=9999, 
sdp:returnDesc=Success, sdp:returnCode=1}
</pre></td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>

                                        <span>호출 함수 설명</span>
                                        <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 리턴되는 출력값에 대하여 설명합니다. </p>
                                        <div class="pkg_board">
                                            <!-- table start -->
                                            <table class="table-vw code_style">
                                                <caption>table Table</caption>
                                                <colgroup>
                                                    <col style="width:20%" >
                                                    <col style="width:80%" >
                                                </colgroup>

                                                <tbody>
                                                    <tr>
                                                        <th colspan="2"><div>Request Method</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td>setApiUrl(String str)</td>
                                                        <td>API를 연동하는 EndPoint URL 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiMethod(String str)</td>
                                                        <td>Rest API연동시 입력하는 Method (get, put, delete, post) 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiName(String str)</td>
                                                        <td>Soap API연동시 API명 설정 method </td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiParamsJson(String json)</td>
                                                        <td>Json형태의 입력 파라미터 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiParamsXml(String xml)</td>
                                                        <td>xml형태의 입력 파라미터 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiParam(String str)</td>
                                                        <td>Key – value방식의 입력 파리미터 설정 method </td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiUserName(String str)</td>
                                                        <td>Id/pass형식의 SOAP API연동시 발급 받은 인증정보의 ID 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>setApiPassword(String str)</td>
                                                        <td>Id/pass형식의 SOAP API연동시 발급 받은 인증정보의 PASSWORD 설정 method</td>
                                                    </tr>
                                                    <tr>
                                                        <th colspan="2"><div>Request Method</div></th>                               
                                                    </tr>
                                                    <tr>
                                                        <td>sendSoapRequest()</td>
                                                        <td>SOAP전송 method</td>
                                                    </tr>
                                                    <tr>
                                                        <td>sendRestRequest()</td>
                                                        <td>REST전송 method</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </section>

                                </div>


									
		                                            	
                                <div class="btn_set">
                                	<c:if test="${not empty fList }">
                                		<c:forEach items="${fList}" var="refFiles"  varStatus="idx"> 
                                    		<button type="button" title="SDK 다운로드" class="btn-lg2 btn_black" onclick="fnSdkDown('/file/fileDownLoad.do?filePath=${refFiles.filePath}&downType=${refFiles.fileTypeCd}&orgFileName=${refFiles.originFileNm}&saveFileName=${refFiles.saveFileNm}')"><span>SDK 다운로드</span></button>
                               			</c:forEach>
                               		</c:if>
                               		<c:if test="${empty fList }">
								  		<li>등록된 첨부파일이 없습니다.</li>
								  	</c:if>
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
