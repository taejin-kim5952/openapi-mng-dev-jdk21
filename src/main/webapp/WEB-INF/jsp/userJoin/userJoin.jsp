<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - 회원가입">
<script type="text/javascript">
function goMain(){
	location.href="<c:url value='/main/index.do'/>";
}

function goApiChk(){
	location.href="<c:url value='/login/loginForm.do'/>";
}
</script>
<div id="container">
		<div class="sVisual sv_login">
			<div>
				<h2>회원가입</h2>
				<p>회원가입 하시면 Open API 웹사이트에서 제공하는 서비스를 이용하실 수 있습니다</p>
			</div>
		</div>
		<div class="contents">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> 회원가입</div>

				<div id="content">
                    <form>
                        <div class="join_wrap">
                            <ul class="step_navigation">
                                <!-- 활성화된 tap에 onpg class 추가 -->
                                <li class="stp01">
                                    <div>
                                        <strong>STEP 01</strong>
                                        <p>이용약관</p>
                                    </div>
                                </li>
                                <li class="stp02">
                                    <div>
                                        <strong>STEP 02</strong>
                                        <p>정보입력</p>
                                    </div>
                                </li>
                                <li class="stp03 onpg">
                                    <div>
                                        <strong>STEP 03</strong>
                                        <p>가입완료</p>
                                    </div>
                                </li>
                            </ul>

                            <div class="join-complite_wrap">
                                <div class="join-complite_msg">
                                    <p><span>회원가입</span>이 완료 되었습니다.</p>
                                    <div>
                                        <dl>
                                            <dt>아이디</dt>
                                            <dd><c:if test="${jmap.userId ne null && jmap.userId != ''}">${fn:substring(jmap.userId,0,fn:length(jmap.userId)-3)}***</c:if></dd>
                                        </dl>
                                        <dl>
                                            <dt>이름</dt>
                                            <dd><c:if test="${jmap.userName ne null && jmap.userName != ''}">${fn:substring(jmap.userName,0,fn:length(jmap.userName)-1)}*</c:if></dd>
                                        </dl>
                                        <dl>
                                            <dt><c:if test="${jmap.authCode eq 'MBRDIV1010'}">부서명</c:if><c:if test="${jmap.authCode eq 'MBRDIV1020'}">업체명</c:if></dt>
                                            <dd>${jmap.company}</dd>
                                        </dl>
                                    </div>
                                </div>

                                <div class="btn_set">
                                    <p class="join_ment">API 개발자는 API 개발 권한 요청이 필요합니다. <br>
                                    로그인 후 API 개발 권한을 요청하십시오.</p>
                                    <button type="button" title="메인으로" onclick="javascript:goMain();" class="btn btn_black"><span>메인으로</span></button>
                                    <button type="button" title="API권한요청" onclick="javascript:goApiChk();"  class="btn"><span>로그인 페이지로</span></button>
		</div>
	</div>
</div>
</t:layout>
                    </form>
                </div>
			</div>
		</div>
	</div>