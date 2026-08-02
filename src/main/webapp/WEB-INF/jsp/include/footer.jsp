<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
//API 권한신청
var fsessionChk = function(){
	  var flocationUrl = '';
	  flocationUrl = "${not empty ssUserVo ? ssUserVo.maskingMbrId : ''}";
	  if(!flocationUrl){
		  location.href="<c:url value='/login/loginForm.do'/>";
	  }else {
		  location.href="<c:url value='/mypage/mypageInfo.do'/>";
	  }
}

//회원가입
var fmlink = function(){
	  var mvar = '';
	  mvar = '<spring:eval expression="@environment.getProperty('psso.join.url')" />';
	  window.open(mvar, '_blank'); 
}

//회사소개
var companyInfo = function(){
	  var cvar = '';
	  cvar = 'https://corp.kt.com/html/intro/main.html';
	  window.open(cvar, '_blank'); 
}

//불법스팸 대응
var spamInfo = function(){
	  var spvar = '';
	  spvar = 'https://spam.kisa.or.kr';
	  window.open(spvar, '_blank'); 
}

//명의 도용 방지
var msafer = function(){
	  var msavar = '';
	  msavar = 'https://www.msafer.or.kr';
	  window.open(msavar, '_blank'); 
}

//API 등록
 var fapiReg = function(){
      var loginStep = '';
	  loginStep = "${ssUserVo.maskingMbrId}";
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
		  return;
	  }else{
 		  location.href="<c:url value='/api/main/mvMainList.do'/>";
	  }
} 

//mypage 페이지
var floginChkAut = function(){
	  var loginChkUrl = '';
	  loginChkUrl = "${ssUserVo.maskingMbrId}";
	  if(!loginChkUrl){
		  var btnHtm = "";
		  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnDeMyPage()"  id="cBtton">확인</button> ';
		  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
		  fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req" />' );
		  return;
	  }else{
		  location.href="<c:url value='/mypage/mypageInfo.do'/>";
	  }
}
//openapi 검색 버튼 클릭시
var apiRegSearch = function(){
	  var apiReglChkUrl = '';
	  apiReglChkUrl = "${ssUserVo.maskingMbrId}";	  
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
<!-- allMenu -->
    <div class="allMenu-wrap">
        <div class="all_menu">
            <dl>
                <dt>이용가이드</dt>
                <dd><a href="<c:url value="/guide/mvUseList.do" />" title="이용가이드">이용가이드</a></dd>
                <dd><a href="<c:url value="/guide/mvShubList.do" />" title="SHUB 가이드">SHUB 가이드</a></dd>
               <!-- <dd><a href="<c:url value="/devsupport/vmguide/devVmGuide.do" />" title="개발환경 가이드">개발환경 가이드</a></dd> -->
            </dl>

            <dl>
                <dt>API 등록</dt>
                <dd><a href="javascript:;" onclick="fapiReg();" title="API 등록">API 등록</a></dd>
            </dl>

            <dl>
                <dt>Open API</dt>
                <dd><a href="javascript:;" onclick="apiRegSearch();"  title="Open API 검색">Open API 검색</a></dd>
                <dd><a href="<c:url value="/guide/mvShubList.do" />" title="SHUB">SHUB</a></dd>
                <dd><a href="<c:url value="/guide/mvUseList.do?tabCurrent=3" />" title="IoT Makers">IoT Makers</a></dd>
                <dd><a href="<c:url value="/guide/mvUseList.do?tabCurrent=4" />" title="Geo master">Geo master</a></dd>
                <dd><a href="<c:url value="/guide/mvUseList.do?tabCurrent=5" />" title="uCloud Biz">uCloud Biz</a></dd>
                <dd><a href="<c:url value="/guide/mvUseList.do?tabCurrent=6" />" title="GIGA genie">GIGA genie</a></dd>
            </dl>
<!--
            <dl>
                <dt>커뮤니티</dt>
                <dd><a href="<c:url value="/bbs/notice/mvNoticeList.do" />" title="공지사항">공지사항</a></dd>
                <dd><a href="<c:url value="/bbs/forum/mvForumList.do" />" title="개발자포럼">개발자포럼</a></dd>
                <dd><a href="<c:url value="/faq/mvfaqList.do" />" title="FAQ">FAQ</a></dd>
                <dd><a href="<c:url value="/qna/mvQnAList.do" />" title="Q&A">Q&A</a></dd>
            </dl>
			-->
            <dl>
                <dt>MY 페이지</dt>
                <dd><a href="javascript:;" onclick="floginChkAut();" title="MY 페이지">MY 페이지</a></dd>
            </dl>
        </div>
        
        <ul class="memship_menu">
            <c:if test="${ssUserVo.mbrId eq null}" > 
	        <li><a href="<c:url value="/login/loginForm.do" />" title="로그인">로그인</a></li>
            </c:if>
            <c:if test="${ssUserVo.mbrId ne null}" >
            <li><a href="<c:url value="/login/pssoLogout.do" />" title="로그아웃">로그아웃</a></li>
            </c:if>
            <li><a href="#" onclick="fmlink();" title="회원가입">회원가입</a></li>
            <c:if test="${ssUserVo.mbrId ne null}" >
	        <li><a href="#" onclick="fsessionChk();" title="API 권한신청">API 권한신청</a></li>
	        </c:if>
        </ul>
        <button type="button" title="전체메뉴 끄기" class="allmenu_close"><span>전체메뉴 끄기</span></button>
    </div>

    <!-- allMenu -->
    <div class="mv-wrap">
        <div>
        	<h3>뛰어난 변화, API 산출물 자동생성</h3>
            <div id="mv_boxing">
            	
            </div>
        </div>
        
        <button type="button" title="전체메뉴 끄기" class="mv_close"><span>전체메뉴 끄기</span></button>
    </div>
    
    <div class="dim_layer"></div>
    <!-- allMenu -->