<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="commonFunc" uri="/WEB-INF/tld/CommonFunc.tld" %>
<%@ page import="com.kt.openapi.web.util.CommonFunc"%>

<c:set var="bIsBstgwMode" value="${commonFunc:isRunmodeTag('bstgw_mode')}" />
<c:set var="bIsBstgwManager" value="${commonFunc:isSpecificUser('bstgw.manager')}" />
<c:set var="bIsBstgwViewer" value="${commonFunc:isSpecificUser('bstgw.viewer')}" />

<script type="text/javascript">
  var fnIsLoggedin = function() {
    return ('${not empty ssUserVo ? ssUserVo.maskingMbrId : ''}'.length > 0);
  }
  //API 권한 신청
  var sessionChk = function() {
    if (!fnIsLoggedin()) {
      location.href = '<c:url value="/login/loginForm.do"/>';
      return;
    }
    location.href = '<c:url value="/mypage/mypageInfo.do"/>';
  }

  //회원가입
  var mlink = function() {
   var mvar = "https://apilink.kt.co.kr/login/user/termsForm.do";
   // mvar = '<spring:eval expression="@environment.getProperty('psso.join.url')" />';
    window.open(mvar, '_blank');
  }

  //API 등록
  var apiReg = function() {
    if (!fnIsLoggedin()) {
      var btnHtm = '';
      btnHtm += ' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm cid_btn_close" onclick="fn_golink_with_login(\'apireg\')">확인</button> ';
      btnHtm += ' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close cid_btn_close">취소</button> ';
      fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req" />');
      return;
    }

    var list = new Array(); 
    <c:if test="${not empty ssUserVo}">
    <c:forEach items="${ssUserVo.authList}" var="item">
      list.push('${item.autId}');
    </c:forEach>
    </c:if>
    
    if (!list[0]) {
      var btnHtm = '';
      btnHtm += ' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm cid_btn_close" onclick="fn_golink_with_login(\'mypage\')">확인</button> ';
      btnHtm += ' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close cid_btn_close">취소</button> ';
      fnOpenLayer(btnHtm, 'API 권한 신청','<spring:message code="top.aut.req" />');
      return;  
    }
    else {
      location.href = fn_get_direct_url('apireg');
    }
  }
  
  //-- { API 검증 /배포 /승인-- by ktj
  var apiDeploy = function() {
    location.href = '<c:url value="/api/deploy/mvDeployList.do"/>';
  }
  
  var apiApproval = function() {
    location.href = '<c:url value="/api/deploy/mvApprovalList.do"/>';
  }
  //-- }

  var beastDeployList = function() {
    location.href = '<c:url value="/beast/deploy/mvDeployList.do"/>';
  }

  //-- [i][direct별 url]
  function fn_get_direct_url(direct) {
    var go_url = '';
    if ('console' == direct) {
      go_url = '<c:url value="/mypage/console.do"/>';
    }
    else if ('mypage' == direct) {
      go_url = '<c:url value="/mypage/mypageInfo.do"/>';
    }
    else if ('apireg' == direct) {
      go_url = '<c:url value="/api/main/mvMainList.do"/>';
    }
    return go_url; 
  }
  //-- [i][login후 사용 페이지 confirm]
  function fn_golink_with_login(direct) {
    var go_url = fn_get_direct_url(direct);
    if (go_url.length == 0) { return; }

    if (fnIsLoggedin()) {
      location.href = go_url;
      return;
    }
    
    var btnHtm = '';
    btnHtm += ' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm cid_btn_close" onclick="fn_gologin_with_returl(\'' + direct + '\')">확인</button> ';
    btnHtm += ' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close cid_btn_close">취소</button> ';
    fnOpenLayer(btnHtm, '로그인','<spring:message code="top.login.req" />');
    return;
  }
  function fn_gologin_with_returl(direct) {
    var go_url = fn_get_direct_url(direct);
    if (go_url.length == 0) { return; }
    location.href = '<c:url value="/login/loginForm.do?returnUrl=' + encodeURIComponent(go_url) + '"/>';
  }
</script>

<%-- 상단 메뉴를 dynamic하게 구성 --%>
<script src="<c:url value="/resources/js/pub/main.js" />"></script>

<header>
  <div class="top_area">
    <div class="global_navi">
      <ul class="clfix">
        <li class="gbnv_1">
          <c:if test="${empty ssUserVo or ssUserVo.mbrId eq null}" > 
            <a href="<c:url value='/login/loginForm.do' />" title="로그인">로그인</a>
          </c:if>
          <c:if test="${not empty ssUserVo and ssUserVo.mbrId ne null}" >
            <a href="<c:url value='/login/pssoLogout.do' />" title="로그아웃">로그아웃</a>
          </c:if>
        </li>
        <c:if test="${empty ssUserVo or ssUserVo.mbrId eq null}" >
          <li class="gbnv_2"><a href="#" onclick="mlink();" title="회원가입">회원가입</a></li>
        </c:if>
        <c:if test="${not empty ssUserVo and ssUserVo.mbrId ne null}" >
          <li class="gbnv_3"><a href="javascript:void(0);" onclick="sessionChk();" title="API 권한신청">API 권한신청</a></li>
        </c:if>
		
        <!--<li class="gbnv_4"><a href="javascript:void(0);" title="Menu Map" class="allMenu" onclick="showAllmenu(this, '.allMenu-wrap');return false;"></a></li>-->
		
		</ul>
    </div><!-- .global_navi -->
    <h1 class="logo"><a href="<c:url value='/main/index.do'/>" title="KT Open API">KT Open API logo</a></h1>
    <div class="gnb">
      <div class="mainBox">
        <ul class="naviBox" id="gnb">
          <li class="menu1">
            <a href="javascript:void(0);" title="이용가이드">이용가이드</a>
            <ul>
              <li><a href="<c:url value="/guide/mvUseList.do" />" title="이용가이드">이용가이드</a></li>
              <li><a href="<c:url value="/guide/mvShubList.do" />" title="SHUB 이용안내">SHUB 가이드</a></li>
              <!--<li><a href="<c:url value="/devsupport/vmguide/devVmGuide.do" />" title="개발환경 가이드">개발환경 가이드</a></li>-->
            </ul>
          </li><!-- .menu1 -->
          <li class="menu2">
            <a href="javascript:void(0);" onclick="apiReg();" title="API 등록">API 등록</a>
            <ul>
              <li><a href="javascript:void(0);" onclick="apiReg();" title="API 등록">API 등록</a></li>
              <li id="deployMenu"><a href="javascript:void(0);" onclick="apiDeploy();" title="API 배포">API 배포</a></li>
              <li id="deployAuth"><a href="javascript:void(0);" onclick="apiApproval();" title="배포 승인"><div>배포 승인</div></a></li>
<c:if test="${bIsBstgwMode and (bIsBstgwManager or bIsBstgwViewer)}">
              <li><a href="javascript:void(0);" onclick="beastDeployList();" title="BEAST API 배포">BEAST API 배포</a></li>
</c:if>
            </ul>
          </li><!-- .menu2 -->
<c:if test="${bIsBstgwMode}">
          <li class="menu3">
            <a title="BEAST(TB)">BEAST(TB)</a>
            <ul>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSysDplyList/TB_KTC" />" title="시스템관리(KTC)">시스템관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiDplyList/TB_KTC" />" title="API관리(KTC)">API관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSvcDplyList/TB_KTC" />" title="서비스관리(KTC)">서비스관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiLinkDataList/TB_KTC" />" title="부가정보관리(KTC)">부가정보관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSysDplyList/TB_AZURE" />" title="시스템관리(AZURE)">시스템관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiDplyList/TB_AZURE" />" title="API관리(AZURE)">API관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSvcDplyList/TB_AZURE" />" title="서비스관리(AZURE)">서비스관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiLinkDataList/TB_AZURE" />" title="부가정보관리(AZURE)">부가정보관리(AZURE)</a></li>
            </ul>
          </li><!-- .menu3 -->
<c:if test="${bIsBstgwManager or bIsBstgwViewer}">
          <li class="menu4">
            <a title="BEAST(PRD)">BEAST(PRD)</a>
            <ul>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSysDplyList/PRD_KTC" />" title="시스템관리(KTC)">시스템관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiDplyList/PRD_KTC" />" title="API관리(KTC)">API관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSvcDplyList/PRD_KTC" />" title="서비스관리(KTC)">서비스관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiLinkDataList/PRD_KTC" />" title="부가정보관리(KTC)">부가정보관리(KTC)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSysDplyList/PRD_AZURE" />" title="시스템관리(AZURE)">시스템관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiDplyList/PRD_AZURE" />" title="API관리(AZURE)">API관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmSvcDplyList/PRD_AZURE" />" title="서비스관리(AZURE)">서비스관리(AZURE)</a></li>
              <li><a href="<c:url value="/beast/apigwmng/bstAdmApiLinkDataList/PRD_AZURE" />" title="부가정보관리(AZURE)">부가정보관리(AZURE)</a></li>
            </ul>
          </li><!-- .menu4 -->
</c:if>
</c:if>
<c:if test="${not bIsBstgwMode}">
          <li class="menu3">
            <a href="<c:url value="/api/info/mvInfoView.do" />" title="Open API">Open API</a>
            <ul>
              <li><a href="<c:url value="/api/info/mvInfoView.do" />" title="Open API 규격서">API 검색 및 규격서</a></li>
              <%--
                <li><a href="<c:url value="/guide/mvShubList.do" />" title="SHUB">SHUB</a></li>
                <li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=3" />" title="IoT Makers">IoT Makers</a></li>
                <li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=4" />" title="Geo master">Geo master</a></li>
                <li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=5" />" title="uCloud Biz">uCloud Biz</a></li>
                <li><a href="<c:url value="/guide/mvUseList.do?tabCurrent=6" />" title="GIGA genie">GIGA genie</a></li>
              --%>
            </ul>
          </li><!-- .menu3 -->
          
</c:if>
          <li class="menu5">
            <a href="javascript:void(0);" onclick="fn_golink_with_login('mypage');" title="MY 페이지">MY 페이지</a>
            <ul>
              <li><a href="javascript:void(0);" onclick="fn_golink_with_login('mypage');" title="MY 페이지">MY 페이지</a></li>
<c:if test="${bIsBstgwMode}">
              <li><a href="javascript:void(0);" onclick="fn_golink_with_login('console');" title="Console">Console</a></li>
</c:if>
              <%-- [tag:adpt][drm][add] --%>
              <!--<li><a href="<c:url value="/adptran/apistatus/group" />" title="상황판-그룹">상황판-그룹</a></li>
              <li><a href="<c:url value="/adptran/apistatus/list" />" title="상황판-목록">상황판-목록</a></li>-->
            </ul>
          </li><!-- .menu5 -->
        </ul><!-- .naviBox -->
      </div><!-- .mainBox -->
    </div>
  </div><!-- .top_area -->
</header>

<script type="text/javascript">
  // KOS 개발자 그룹, KOS 운영자 그룹 메뉴 권한 설정 
  $(function() {
    <c:set var="deployMenu_on" value="y" />
    <c:set var="deployAuth_on" value="n" />
    <c:if test="${not empty ssUserVo}">
    <c:forEach items="${ssUserVo.authList}" var="item">
      <c:if test="${item.autNm eq 'KOS 개발자 그룹'}"> 
        <c:set var="deployMenu_on" value="y" />
      </c:if>
      <c:if test="${item.autNm eq 'A/G 운영자 그룹'}"> 
        <c:set var="deployMenu_on" value="y" />
        <c:set var="deployAuth_on" value="y" />
      </c:if>
      <c:if test="${item.autNm eq 'RIS API개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if>
  <c:if test="${item.autNm eq 'Vanilla API 개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if> 
  <c:if test="${item.autNm eq 'POINT HUB 개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if>
  <c:if test="${item.autNm eq 'KT샵개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if> 
  <c:if test="${item.autNm eq 'Loyalty개발자 그룹'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if>
  <c:if test="${item.autNm eq 'BPM 개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if> 
  <c:if test="${item.autNm eq 'MEIN API 개발자'}"> 
       <c:set var="deployMenu_on" value="y" />
      </c:if>
  <c:if test="${item.autNm eq 'GiGAeyes API 개발자'}"> 
    <c:set var="deployMenu_on" value="y" />
  </c:if>  
  <c:if test="${item.autNm eq '기가지니 인공지능 개발자'}">
    <c:set var="deployMenu_on" value="y" />
  </c:if>  
    </c:forEach>
    </c:if>

    $('#deployMenu, #deployAuth').hide();
    <c:if test="${deployMenu_on eq 'y'}">
      $('#deployMenu').show();  //API 배포
    </c:if>
    <c:if test="${deployAuth_on eq 'y'}"> 
      $('#deployAuth').show();  //API 승인
    </c:if>
  });
</script>
