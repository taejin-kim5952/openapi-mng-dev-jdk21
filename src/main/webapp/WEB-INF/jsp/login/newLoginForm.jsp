<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>

<%-- //-- [tag:SR-20230619] --%>
<%-- ### LoginController login_ide모드사용 --%>
<c:set var="attr_config_use_login_ide"><spring:eval expression="@environment.getProperty('config.use.login.ide')"/></c:set>

<%-- [2025-05-13] JSP 3.0 (Tomcat 10.x) 스크립틀릿 금지 대응: Java scriptlet 변수를 JSTL boolean 변수로 대체 --%>
<c:set var="useLoginIde" value="${not empty attr_config_use_login_ide and fn:toLowerCase(attr_config_use_login_ide) == 'y'}"/>

<%-- 로컬 개발 편의: local 프로파일에서만 openapi-mock PSSO 목 계정을 기본값으로 채움 --%>
<c:set var="attr_active_profile"><spring:eval expression="@environment.getProperty('spring.profiles.active')"/></c:set>
<c:set var="isLocalProfile" value="${attr_active_profile == 'local'}"/>

<t:layout type="default" title="KT Open API - Login">
    <link href="<c:url value='/resources/css/membership.css' />" rel="stylesheet" media="screen">
    <script src="<c:url value='/resources/js/login/rsa.js' />"></script>
    <script src="<c:url value='/resources/js/login/jsbn.js' />"></script>
    <script src="<c:url value='/resources/js/login/prng4.js' />"></script>
    <script src="<c:url value='/resources/js/login/rng.js' />"></script>

    <%-- [2025-05-13] JSP 3.0 스크립틀릿 금지 대응: <% if %> → <c:if>로 대체 --%>
    <c:if test="${useLoginIde}">
        <script>
            $(document).ready(function () {
                $('form#loginForm').find(':text[name="certifyCode"]').on('keyup', function (p_evt) {
                    if ((p_evt.keyCode == 13) && p_evt.ctrlKey && p_evt.shiftKey) {
                        if ('master' == ($(this).val()).toLowerCase()) {
                            p_evt.preventDefault();
                            var jq_loginType = $('form#loginForm').find(':hidden[name="loginType"]');
                            if (jq_loginType.length == 0) {
                                jq_loginType = $('<input type="hidden" name="loginType">');
                                jq_loginType.val('ide');
                                $('form#loginForm').append(jq_loginType);
                            }
                            smsSendCheck = true;
                            var jq_captchaCode = $('form#loginForm').find(':text[name="captchaCode"]')
                            jq_captchaCode.val($is_empty(jq_captchaCode.val()) ? 'dummy' : jq_captchaCode.val());
                            login.valid();
                        }
                    }
                });
            });
        </script>
    </c:if>

    <script type="text/javascript">
        var commonBtn = '<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button>';

        //인증번호 발송 여부
        var smsSendCheck = false;

        $(function () {
            $('form#loginForm :text[name=userId]').focus();
            //validation
            cmmnValidate.obj.rules = {
                userId: {required: true},
                pssoPw: {required: true},
                certifyCode: {required: true}
            };
            cmmnValidate.obj.messages = {
                userId: {required: '사용자 ID를 입력하세요.'},
                pssoPw: {required: '비밀번호를 입력하세요.'},
                certifyCode: {required: '인증번호를 입력하세요.'}
            };
            cmmnValidate.obj.submitFunction = function (form) {
                login.save();
            };
            cmmnValidate.validate('form#loginForm');
        });

        //인증번호 발송
        function otpSend() {
            smsSendCheck = false;
            if ($("#userId").val().trim() == "") {
                fnOpenLayer(commonBtn, 'Alert', "아이디를 입력해주세요.");
                return;
            }
            if ($("#pssoPw").val().trim() == "") {
                fnOpenLayer(commonBtn, 'Alert', "패스워드를 입력해주세요.");
                return;
            }
            var encUserId;
            var encPasswd;
            var rsa = new RSAKey();
            rsa.setPublic($('input#pkm').val(), $('input#pke').val());
            encUserId = rsa.encrypt($(':text[name=userId]').val());
            encPasswd = rsa.encrypt($(':password[name=pssoPw]').val());

            $.ajax({
                type: 'POST',
                url: '<c:url value="/login/otpSend.do"/>',
                data: {userId: encUserId, pssoPw: encPasswd},
                async: false,
                success: function (data) {
                    if (data != null) {
                        if (data.returnCode == '1') {

                            if (data.type == 'phone') {
                                fnOpenLayer(commonBtn, 'Alert', '핸드폰으로 인증번호가 발송되었습니다.');
                            } else if (data.type == 'email') {
                                fnOpenLayer(commonBtn, 'Alert', '이메일로 인증번호가 발송되었습니다.');
                            } else {
                                fnOpenLayer(commonBtn, 'Alert', '등록된 핸드폰 또는 이메일 정보가 없습니다.');
                                return;
                            }

                            smsSendCheck = true;
                        } else {
                            fnOpenLayer(commonBtn, 'Alert', "<b>" + data.failMsg + "</b>");
                            $("#certifyCode").val("");
                        }
                    } else {
                        fnOpenLayer(commonBtn, 'Alert', 'system error!!');
                    }
                }
            });
        }

        var login = {
            valid: function () {
                $('form#loginForm').submit();
            }
            , save: function (type) {
                if (!smsSendCheck) {
                    fnOpenLayer(commonBtn, 'Alert', "인증번호를 발송해주세요.");
                    return;
                }
                var encUserId;
                var encPasswd;
                var rsa = new RSAKey();
                rsa.setPublic($('input#pkm').val(), $('input#pke').val());
                encUserId = rsa.encrypt($('form#loginForm').find(':text[name=userId]').val());
                encPasswd = rsa.encrypt($('form#loginForm').find(':password[name=pssoPw]').val());

                var certifyCode = $("#certifyCode").val();

                var BDC_BackWorkaround_springFormCaptcha = $('form#loginForm').find('#BDC_BackWorkaround_springFormCaptcha').val();
                var BDC_VCID_springFormCaptcha = $('form#loginForm').find('#BDC_VCID_springFormCaptcha').val();
                var param = {
                    userId: encUserId,
                    pssoPw: encPasswd,
                    certifyCode: certifyCode,
                    BDC_BackWorkaround_springFormCaptcha: BDC_BackWorkaround_springFormCaptcha,
                    BDC_VCID_springFormCaptcha: BDC_VCID_springFormCaptcha
                }
                <%-- [2025-05-13] JSP 3.0 스크립틀릿 금지 대응: <% if %> → <c:if>로 대체 --%>
                <c:if test="${useLoginIde}">
                var jq_loginType = $('form#loginForm').find(':hidden[name="loginType"]');
                if (jq_loginType.length != 0) {
                    param['loginType'] = jq_loginType.val();
                    jq_loginType.remove();
                }
                </c:if>

                if (type == 'reLogin') {
                    param.dupChkYn = 'Y';
                }
                $.ajax({
                    type: 'POST',
                    url: '<c:url value="/login/newLoginCheck.do"/>',
                    data: param,
                    async: false,
                    success: function (data) {
                        //-- [2023:codeeyes][depth_4단계 issue]
                        if (data == null) {
                            fnOpenLayer(commonBtn, 'Alert', "유효하지 않은 응답 입니다.");
                            return;
                        }
                        if (data.returnCode == '1') {
                            //로그인 성공
                            location.href = '<c:url value="/mypage/mypageInfo.do"/>';
                        } else {
                            //로그인 실패
                            //로그인 실패 사유가 중복로그인인 경우
                            if (data.errorType == 'dupLogin') {
                                //확인 시 기존 사용자 로그아웃 시킨 후 로그인
                                if (confirm("기존 로그인을 해제하시겠습니까?")) {
                                    login.save('reLogin');
                                } else {
                                    //캡챠 및 인증번호 초기화
                                    $("#certifyCode").val("");
                                    smsSendCheck = false;
                                    return;
                                }
                                //로그인 실패 사유가 psso 회원이지만 apimanager 회원은 아닌경우
                            } else if (data.errorType == 'newMember') { //psso 회원이지만 apimanager db에 회원 정보가 없는경우
                                location.href = '<c:url value="/userJoin/userJoinForm.do"/>';
                            } //그 외 사유로 로그인 실패한 경우
                            else {
                                fnOpenLayer(commonBtn, 'Alert', "<b>" + data.errorDescription + "</b>");
                            }
                        }
                    } //-- success
                });
            } //-- save:
        };  //-- login
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
                            <form id="loginForm">
                                <!-- RSA 인증  -->
                                <input type="hidden" name="pkm" id="pkm" value="${publickeymodulus}"/>
                                <input type="hidden" name="pke" id="pke" value="${publickeyexponent}"/>
                                <!-- RSA 인증  -->
                                <div class="login_wrap_2">
                                    <div>
                                        <dl>
                                            <dt>
                                                <label for="userId">아이디</label>
                                            </dt>
                                            <dd>
                                                <input class="cid_act_enter" type="text" id="userId" name="userId" value="${isLocalProfile ? 'pssouser01' : loginVo_userId }">
                                            </dd>
                                        </dl>
                                        <dl>
                                            <dt>
                                                <label for="pssoPw">비밀번호</label>
                                            </dt>
                                            <dd>
                                                <input class="cid_act_enter" type="password" id="pssoPw" name="pssoPw" value="${isLocalProfile ? 'pssoPass01!' : ''}" autocomplete="off">
                                            </dd>
                                        </dl>

                                        <dl>
                                            <dt>
                                                <label for="certifyCode">인증번호</label>
                                            </dt>
                                            <dd>
                                                <input type="text" placeholder="발송된 인증번호를 입력해주세요." title="SMS 인증번호 입력" id="certifyCode" name="certifyCode" style="width:255px;">
                                                <button type="button" title="" class="btn btn-lg btn_black ml10" onclick="otpSend()">
                                                    <span>인증번호 발송</span></button>
                                            </dd>
                                        </dl>
                                        <div class="mem_btn">
                                            <a href="javascript:login.valid();" class="btn_mem-login cid_act_btn_login" title="로그인"><span>로그인</span></a>
                                            <!-- <a href="javascript:go.createForm();" class="btn_mint " title="신규회원가입">신규회원가입</a> -->
                                        </div>
                                    </div>
                                </div>
                            </form>
                            <div class="mem_btn">
                                <p>
                                    <a href="<c:out value="${userTermsFormLink}"/>" class="btn_join" target="_new" title="회원가입">
                                        <span>회원가입</span>
                                    </a>
                                </p>
                                <p>
                                    <a href="<c:out value="${findUserIdLink}"/>" class="btn_findid" target="_new" title="아이디 찾기">
                                        <span>아이디 찾기</span>
                                    </a>
                                </p>
                                <p>
                                    <a href="<c:out value="${findUserPwLink}"/>" class="btn_findpw" target="_new" title="비밀번호 찾기">
                                        <span>비밀번호 찾기</span>
                                    </a>
                                </p>
                            </div>
                            <br><br><br>
                            <b style="color: red;">*아이디 분실은 apilink@kt.com 메일로 문의 부탁드립니다.</b>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
