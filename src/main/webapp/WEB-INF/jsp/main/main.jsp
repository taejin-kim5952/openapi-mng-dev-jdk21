<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="main">
<form id="detailNotice" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
</form>
<form id="detailDevF" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
</form>    
	<div id="fullpage">
		<div class="section fp-auto-height-responsive" id="section0">
			<div class="box bxslider">
				<div class="bxs1">
					<div class="sec1_txt">
						<span>KT 플랫폼에서는 <br>
						생각하는그대로<br>
						이루어진다
						<br>
						CONNECTED THINKING<br> Open Your KT
						</span>
					</div>
				</div>
				<div class="bxs2">
					<div class="sec1_txt">
						<span>KT 플랫폼에서는 <br>
						생각하는그대로<br>
						이루어진다
						<br>
						CONNECTED THINKING<br> Open Your KT
						</span>
					</div>
				</div>
			</div>
			<div class="guide_scroll">
				
				<p><span></span>SCROLLDOWN</p>
			</div>
		</div>
		<div class="section fp-auto-height-responsive" id="section1">
			<div class="box">
				<div class="sec2_txt">
					<h2>Development Guide<br>API를 개발하기 위한 상세한 설명을 제공합니다.<br>KT에서 보유한 다양한 유무선 기능 및 자원으로 
KT Open API 서비스 개발을 시작해 보세요.
					</h2>
				</div>
				<ul class="forwarding_menu">
					<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=1" />" title="Open API 소개"><span>Open API<br>소개</span></a></li>
					<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=2" />" title="Design UI 가이드"><span>Design UI<br> 가이드</span></a></li>
					<li><a href="<c:url value="/guide/mvShubList.do" />"  			 title="SHUB"><span>SHUB</span></a></li>
					<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=3" />" title="IoT Makers"><span>IoT Makers</span></a></li>
					<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=4" />" title="Geo master"><span>Geo master</span></a></li>
					<!--<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=5" />" title="uCloud Biz"><span>uCloud Biz</span></a></li>-->
					<li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=6" />" title="GIGA genie"><span>GIGA genie</span></a></li>
					<!--<li><a href="<c:url value="/devsupport/vmguide/devVmGuide.do" />" title="개발환경 가이드"><span>개발환경<br>가이드</span></a></li>-->
				</ul>
			</div>
		</div>

		<div class="section fp-auto-height-responsive" id="section2">
			<div class="box">
				<div class="sec3_txt">
					<div>
						<h2>API 등록의 긍정적인 변화</h2>
						<p>API 산출물 걱정은 이제 그만!<br>
						등록 방법을 영상으로 확인하세요.</p>
						<div><button type="button" title="영상 보기" class="btn_mvPlay" onclick="showMV(this, '.mv-wrap');return false;"><span>영상 보기</span></button></div>
					</div>
				</div>
			</div>
		</div>
		<div class="section fp-auto-height-responsive" id="section3">
			<div class="box">
				<div class="sec4_slide">
					<h2>API SERVICE</h2>
					<p>KT의 Open API 서비스를 만나보세요</p>
					<div id="lista2" class="als-container">
						<span class="als-prev"></span>
						<div class="als-viewport">
							<div class="als-wrapper">
								<div class="als-item als-itm01">1. SHUB - KT SHUB는 KT에서 제공하는
																다양한 서비스를 CP/SP 및
																3rd Party에게 단일화된 형태의
																API로 제공합니다
																<a href="<c:url value="/guide/mvShubList.do" />">Link</a></div>
								<div class="als-item als-itm02">2. IoT Makers - 손쉽게 IoT 디바이스를 연결하여
																테스트하고 수집된 데이터를 관리하며,
																제공되는 OPEN API를 통하여
																IoT 서비스를 만들 수 있습니다
																<a href="<c:url value="/guide/mvUseList.do?tabCurrent=3" />">Link</a></div>
								<div class="als-item als-itm03">3. Olleh Map Biz - Web과 Mobile 환경에서
																지도서비스와 위치정보서비스를
																쉽게 이용할 수 있는 API를 제공합니다
																<a href="<c:url value="/guide/mvUseList.do?tabCurrent=4" />">Link</a></div>
								<div class="als-item als-itm04">4. uCloud Biz - 네트워크의 인프라를
																가장 빠르고 정확하게 제공하는
																ucloud biz의 api서비스를 만나보세요
																<a href="<c:url value="/guide/mvUseList.do?tabCurrent=5" />">Link</a></div>
								<div class="als-item als-itm05">5. GIGA genie - 손쉽게 IoT 디바이스를 연결하여
																테스트하고 수집된 데이터를 관리하며,
																제공되는 OPEN API를 통하여
																IoT 서비스를 만들 수 있습니다
																<a href="<c:url value="/guide/mvUseList.do?tabCurrent=6" />">Link</a></div>
								<div class="als-item als-itm06"></div>
							</div>
						</div>
						<span class="als-next"></span>
					</div>
				</div>
			</div>
			
		</div>
		<div class="section fp-auto-height-responsive" id="section4">
			<div class="box">
				<div class="mBoard_wrap">
					<!--
					<div>
                        <h5 class="">News & Notice <button type="button" onclick="noticeUrl();" title="News & Notice 더보기" class="btn btn_mMore"><span>News & Notice 더보기</span></button></h5>
                        <ul>
                            <c:forEach var="nlist"  items="${resNList}" varStatus="idx">
                            <li><a href="javascript:;" title="${nlist.title}" onclick="fnGoViewNotice('${nlist.pstingId}')"><span>${nlist.title}</span></a><em>${nlist.regDate}</em></li>
                            </c:forEach>
                        </ul>
					</div>

					
					<div>
                        <h5 class="">개발자 포럼<button type="button" onclick="forumUrl();" title="개발자 포럼 더보기" class="btn btn_mMore"><span>개발자 포럼 더보기</span></button></h5>
                        <ul>
                            <c:forEach var="dlist"  items="${resDevFList}" varStatus="idx">
                            <li><a href="javascript:;" title="${dlist.title}" onclick="fnGoViewDevF('${dlist.pstingId}')"><span>${dlist.title}</span></a><em>${dlist.regDate}</em></li>
                            </c:forEach>
                        </ul>
					</div>
					-->
				</div>

				<div class="mainBtnSet">
					<!--
					<a href="javascript:;" onclick="faqUrl();" title="자주찾는 질문" class="btn_mQna">자주찾는 질문</a>
					<a href="javascript:;" onclick="qnaUrl();" title="질문과 답변" class="btn_mFaq">질문과 답변</a>
					-->
				</div>
			</div>
		</div>
		<div class="section fp-auto-height-responsive" id="section5">
			<div class="fp-tableCell">
				<footer>
			        <div class="footerDiv clfix">
			            <ul class="ft_menu">
			                <li><a href="javascript:;" onclick="companyInfo();" title="회사소개">회사소개</a></li>
			                <li><a href="<c:url value='/agree/agViewinfo.do'/>" title="이용약관">이용약관</a></li>
			                <li><a href="<c:url value='/priv/pViewinfo.do'/>" title="개인정보취급방침">개인정보취급방침</a></li>
			                <li><a href="javascript:;" onclick="spamInfo();" title="불법스팸대응">불법스팸대응</a></li>
			                <li><a href="javascript:;" onclick="msafer();" title="명의도용방지서비스">명의도용방지서비스</a></li>
			            </ul>
			            <ul>
			                <li>(주)케이티 대표이사 &nbsp;박윤영 
			                사업자등록번호 :&nbsp; 102-81-42945
			                케이티 통신판매업 신고 :&nbsp; 2002-경기성남-0048</li>
			                <li><!-- 13606  -->경기도 성남시 분당구 불정로 90(정자동 206번지)</li>
			                <li>Copyright © 2018 kt Corp. All rights reserved.</li>
			            </ul>
			        </div>  
			    </footer>
			</div>
		</div>
		
	</div>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$('#fullpage').fullpage({
				verticalCentered: false,
				'navigation': true,
				'navigationPosition': 'right',
				'responsive': true,

				// 'navigationTooltips': ['fullPage.js', 'Powerful', 'Amazing', 'Simple'],
			});

			$('.bxslider').bxSlider({
			  mode: 'fade',
			  auto: true,
			  autoControls: true,
			  stopAutoOnClick: true,
			  pager: true
			});
			$("#lista2").als({
				scrolling_items: 1,
				orientation: "vertical",
				circular: "no",
				autoscroll: "no",
				start_from: 0
			});
		});
		
		//회사소개
		var companyInfo = function(){
			  var cvar = '';
			  cvar = 'https://corp.kt.com/html/intro/main.html';

			  window.open(cvar, '_blank'); 
		}

		//불법스팸대응
		var spamInfo = function(){
			  var spvar = '';
			  spvar = 'https://spam.kisa.or.kr';

			  window.open(spvar, '_blank'); 
		}

		//명의도용방지 서비스
		var msafer = function(){
			  var msavar = '';
			  msavar = 'https://www.msafer.or.kr';

			  window.open(msavar, '_blank'); 
		}
		
		//공지사항
		var noticeUrl = function(){
			
			location.href="<c:url value='/bbs/notice/mvNoticeList.do'/>";
		}
		
		//개발자포럼
        var forumUrl = function(){
        	location.href="<c:url value='/bbs/forum/mvForumList.do' />";
		}
		
        //자주찾는 질문
        var faqUrl = function(){
        	location.href="<c:url value='/faq/mvfaqList.do' />";
    	}
		
        //질문과 답변
        var qnaUrl = function(){
        	location.href="<c:url value='/qna/mvQnAList.do' />";
        }
        //공지사항 페이지 이동
        function fnGoViewNotice(pstingId){
        	$("#detailNotice > input[name='pstingId']").val(pstingId);
        	$('#detailNotice').attr({action:c_url+'bbs/notice/mvNoticeView.do', method:'post'}).submit();
        }
        
        //개발자 포럼 페이지 이동
        function fnGoViewDevF(pstingId){
        	$("#detailDevF > input[name='pstingId']").val(pstingId);
        	$('#detailDevF').attr({action:c_url+'bbs/forum/mvForumView.do', method:'post'}).submit();
        }
        
          // youtube 영상 
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
		      //videoId: 'vl05KuTJgDA',
		      videoId: 'c9Dyapqkcnc',
		      events: {
		        // 'onReady': onPlayerReady,
		        // 'onStateChange': onPlayerStateChange
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
		  
		  $(".mv_close, .dim_layer").click(function(){
		    player.stopVideo();
		  });
		  
		  //api 등록
		  var fapiReg = function(){
			  var loginStep = '';
			  loginStep = "${ssUserVo.mbrId}";
			  if(!loginStep){
				  var btnHtm = "";
				  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnDeLogin()"  id="cBtton">확인</button> ';
				  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
				  fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req" />' );
				  return;
			  }
			 
			  var list = new Array(); 
			  <c:forEach items="${ssUserVo.authList}" var="item">
			  list.push("${item.autId}");
			  </c:forEach>
			  
			  if(!list[0]){
				  
				  var btnHtm = "";
				  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="mypageGo()"  id="cBtton">확인</button> ';
				  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
				  fnOpenLayer(btnHtm, 'API 권한 신청','<spring:message code="top.aut.req" />' );
				  	  
			  }else{
		 		  location.href="<c:url value='/api/main/mvMainList.do'/>"; 
			  }
		}
		  
		  //mypage 페이지
		  var floginChkAut = function(){
			  var loginChkUrl = '';
			  loginChkUrl = "${ssUserVo.mbrId}";
			  //console.log('main mypage:'+loginChkUrl);
			  if(!loginChkUrl){
                     
				  var btnHtm = "";
				  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnDeMyPage()"  id="cBtton">확인</button> ';
				  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
				  fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req" />' );
				  
			  }else{
				  location.href="<c:url value='/mypage/mypageInfo.do'/>";
			  }
		}
		  
		  //회원가입
		  var fmlink = function(){
			  var mvar = '';
			  mvar = '<spring:eval expression="@environment.getProperty('psso.join.url')" />';
			  
			  window.open(mvar, '_blank'); 
		}
		 
		  //api 권한신청
		  var fsessionChk = function(){
			  
			  var flocationUrl = '';
			  flocationUrl = "${ssUserVo.mbrId}";
			  
			  if(!flocationUrl){
				  
				  location.href="<c:url value='/login/loginForm.do'/>";

			  }else {
				
				  location.href="<c:url value='/mypage/mypageInfo.do'/>";
				  
			  }
		}
		//openapi 검색 버튼 클릭시
		var apiRegSearch = function(){
			  var apiReglChkUrl = '';
			  apiReglChkUrl = "${ssUserVo.mbrId}";
// 			  location.href="<c:url value='/api/search/mvMainList.do'/>";
			  location.href="<c:url value='/api/search/apiSearch.do'/>";
		}
		
		function fnDeLogin(){
			  var strUrl = c_url + 'api/main/mvMainList.do';
			  location.href='<c:url value="/login/loginForm.do?returnUrl='+ strUrl +'"/>';
			  return;
		}

		function fnDeMyPage(){
			  var strUrl = c_url + 'mypage/mypageInfo.do';
			  location.href='<c:url value="/login/loginForm.do?returnUrl='+ strUrl +'"/>';
			  return;
		}
	</script>
</t:layout>
