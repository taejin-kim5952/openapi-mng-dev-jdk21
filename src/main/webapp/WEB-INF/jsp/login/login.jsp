<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - 로그인">

<%-- [tag:login][drm][add][181027][로그인처리방식] --%>
<c:set var="prop_psso_api_login_method"><spring:eval expression="@environment.getProperty('psso.api.login.method')" /></c:set>

<script type="text/javascript">
<c:if test="${fn:toLowerCase(prop_psso_api_login_method) eq 'iframe'}">
  $(document).ready(function() {
    if ('${flag}' == 'f') {

      var btnHtm = "";
      btnHtm += ' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
      fnOpenLayer(btnHtm, '로그인', '<spring:message code="login.fail.msg" />');
      return;
    }
  })
</c:if>
  // 회원 가입
  var joinMove = function() {
    var joinMoveUrl = '';
    joinMoveUrl = '<spring:eval expression="@environment.getProperty('psso.join.url')" />';
    window.open(joinMoveUrl, '_blank');
  }
  //아이디 비밀번호 찾기
  var idpwdSearCh = function() {
    var idpwdSearChUrl = '';
    idpwdSearChUrl = '<spring:eval expression="@environment.getProperty('psso.idpwd.url')" />';
    window.open(idpwdSearChUrl, '_blank');
  }
</script>
<div id="container">
  <div class="sVisual sv_login">
    <div>
      <h2>로그인</h2>
      <p>로그인 하시면 Open API 등록 및 서비스를 이용하실 수 있습니다.</p>
    </div>
  </div>
  <div class="contents">
    <div class="conBox">
      <div class="pg_location">
        <a>Go home</a>
        <span>></span> 로그인
      </div>
      <div id="content">
        <div class="login_wrap">
          <div>
<c:choose>
<c:when test="${fn:toLowerCase(prop_psso_api_login_method) eq 'iframe'}">

            <p class="tit-login">아이디와 비밀번호를 입력해 주세요.</p>
            <!-- <div class="iframe_wrap"> </div> -->
            <!-- <iframe src="../../layout/layout.html"></iframe> -->
            <iframe marginwidth="0" marginheight="0" src="<spring:eval expression="@environment.getProperty('psso.iframe.url')" />${beforeUrl}<spring:eval expression="@environment.getProperty('psso.iframe.sub.url')" />" frameborder="0" scrolling="no" width="420" height="315"></iframe>
</c:when>
<c:otherwise>
            <jsp:include page="/WEB-INF/jsp/login/loginForm.jsp" />
</c:otherwise>
</c:choose>

            <div class="mem_btn">
              <p>
                <a href="#" onclick="joinMove();" class="btn_join" target="_new" title="회원가입">
                  <span>회원가입</span>
                </a>
              </p>
              <p>
                <a href="#" onclick="idpwdSearCh();" class="btn_findidpw" target="_new" title="아이디/비밀번호 찾기">
                  <span>아이디/비밀번호 찾기</span>
                </a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</t:layout>
