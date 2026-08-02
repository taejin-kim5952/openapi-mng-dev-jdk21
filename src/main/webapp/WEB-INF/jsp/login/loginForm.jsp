<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.captcha.botdetect.web.servlet.Captcha"%>
<%@taglib prefix="commonFunc" uri="/WEB-INF/tld/CommonFunc.tld"%>
<%@taglib prefix="botDetect" uri="https://captcha.com/java/jsp"%>

<t:layout type="default" title="Kt Open API - Login">
    <jsp:attribute name="head">
        <link href="<c:url value='/resources/css/membership.css' />" rel="stylesheet" media="screen">
        <script src="<c:url value='/resources/js/login/rsa.js' />"></script>
        <script src="<c:url value='/resources/js/login/jsbn.js' />"></script>
        <script src="<c:url value='/resources/js/login/prng4.js' />"></script>
        <script src="<c:url value='/resources/js/login/rng.js' />"></script>
    </jsp:attribute>

    <script type="text/javascript">
        var g_btnHtm = '<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm">확인</button>';

        $(document).ready(function() {
            fn_init_page();
        });

        function fn_init_page() {
            $(function() {
            
                <c:if test="${not empty returnCode}">
                    var titleMsg = 'Alert';
                    <c:choose>
                        <c:when test="${returnCode eq '11'}">
                            location.href = '<c:url value="/"/>';
                        </c:when>
                        <c:when test="${returnCode eq '12' or returnCode eq '13' }">
                            fnOpenLayer(g_btnHtm, titleMsg, "<spring:message code='login.msg.fail' />");
                        </c:when>
                        <c:when test="${returnCode eq '10'}">
                            fnOpenLayer(g_btnHtm, titleMsg, "보안문자를 확인하세요.");
                        </c:when>
                        <c:otherwise>
                            pssoError('${commonFunc:safeDbEncrypt(returnCode)}');
                        </c:otherwise>
                    </c:choose>
                </c:if>
                
                <c:if test="${returnCode ne '10' && not empty returnCode}">
                    fnOpenLayer(g_btnHtm, titleMsg, "<spring:message code='login.msg.defaultfail' />");
                </c:if>
            
                $('form#loginForm :text[name=userId]').focus();
                
                //validation
                cmmnValidate.obj.rules = {
                    userId : { required : true, maxlength : 50 },
                    pssoPw : { required : true, maxlength : 20 },
                    captchaCode : { required : true }
                }
                cmmnValidate.obj.messages = {
                    userId : { required : '사용자 ID를 입력하세요.' },
                    pssoPw : { required : '비밀번호를 입력하세요.' },
                    captchaCode : { required : '보안문자를 입력하세요.' }
                }
                cmmnValidate.obj.submitFunction = function(form){
                    login.save();
                }
                cmmnValidate.validate('form#loginForm');
                
                $('form#loginForm').find('input.cid_act_enter').on('keypress', function(p_evt) {
                    if (p_evt.keyCode == 13) {
                        p_evt.preventDefault();
                        var jq_elem = $('input.cid_act_enter');
                        var jq_empty = jq_elem.filter(function() { return $is_empty($(this).val()); });
                        if (jq_empty.length == 0) {
                            $('.cid_act_btn_login')[0].click();
                        }
                        else {
                            jq_elem.eq((jq_elem.index(this) + 1) % jq_elem.length).focus();
                        }
                    }
                });
            });
        }

        var login = {
            valid : function(){
                $('form#loginForm').submit();
            }
            ,save : function(){
                var form = $('<form />', { id : 'goLoginForm', name : 'goLoginForm' });
                var rsa = new RSAKey();
                rsa.setPublic($('input#pkm').val(),$('input#pke').val());
                var encUserId = rsa.encrypt($('form#loginForm').find(':text[name="userId"]').val());
                var encPasswd = rsa.encrypt($('form#loginForm').find(':password[name="pssoPw"]').val());
                
                form.append($('<input />', { name : 'userId', type : 'hidden', value : encUserId }));
                form.append($('<input />', { name : 'pssoPw', type : 'hidden', value : encPasswd }));
                form.append($('<input />', { name : 'phoneNo', type : 'hidden', value : $('form#loginForm').find(':hidden[name="phoneNo"]').val() }));
                form.append($('<input />', { name : 'captchaCode', type : 'hidden', value : $('form#loginForm').find('#captchaCode').val() }));
                form.append($('<input />', { name : 'BDC_BackWorkaround_springFormCaptcha', type : 'hidden', value : $('form#loginForm').find('#BDC_BackWorkaround_springFormCaptcha').val() }));
                form.append($('<input />', { name : 'BDC_VCID_springFormCaptcha', type : 'hidden', value : $('form#loginForm').find('#BDC_VCID_springFormCaptcha').val() }));
                
                var returnUrl = $('form#loginForm').find(':hidden[name="returnUrl"]').val();
                if (returnUrl.length > 0) { form.append($('<input />', { name : 'returnUrl', type : 'hidden', value : $('form#loginForm').find(':hidden[name="returnUrl"]').val() })); }
                $('body').append(form);
                $('form#goLoginForm').attr('method', 'POST').attr('action', '<c:url value="/login/loginCheck.do"/>');
                $('form#goLoginForm').submit();
            }
        }

        function pssoError(returnCode){
            var url = '${errorUrl}';
            var pssoForm = $('<form />', { id : 'pssoForm', method : 'post', target : '_blank', action : url });
            pssoForm.append('<input type="hidden" name="ClientKey" value="${clientkey}" />');
            pssoForm.append('<input type="hidden" name="SiteURL" value="${siteUrl}" />');
            pssoForm.append('<input type="hidden" name="EncPSSOID" value="'+encodeURIComponent('${commonFunc:safeDbEncrypt(loginVo_userId)}')+'" />');
            pssoForm.append('<input type="hidden" name="EncPSSOCode" value="'+encodeURIComponent(returnCode)+'" />');
            
            $('body').append(pssoForm);
            $('#pssoForm').submit().remove();
        }
    </script>

    <c:set var="clientkey">
        <spring:eval expression="@environment.getProperty('psso.api.login.clientkey')" />
    </c:set>
    <c:set var="siteUrl">
        <spring:eval expression="@environment.getProperty('psso.api.siteUrl')" />
    </c:set>
    <c:set var="errorUrl">
        <spring:eval expression="@environment.getProperty('psso.api.login.errorUrl')" />
    </c:set>

    <form id="loginForm">
        <input type="hidden" name="returnUrl" value="${returnUrl}" />
        <!-- RSA 인증  -->
        <input type="hidden" name="pkm" id="pkm" value="${publickeymodulus}" />
        <input type="hidden" name="pke" id="pke" value="${publickeyexponent}" />
        <!-- RSA 인증  -->
        <div class="login_wrap_2">
            <div>
                <dl>
                    <dt><label for="inp_id">아이디</label></dt>
                    <dd><input class="cid_act_enter" type="text" id="inp_id" name="userId" value="${loginVo_userId}"></dd>
                </dl>
                <dl>
                    <dt><label for="pssoPw">비밀번호</label></dt>
                    <dd><input class="cid_act_enter" type="password" id="pssoPw" name="pssoPw" value="" autocomplete="off"></dd>
                </dl>
                <dl>
                    <dt><label for="captchaImage" style="line-height:60px;">보안문자</label></dt>
                    <dd id="captchaImage"><botDetect:simpleCaptcha styleName="springFormCaptcha" /></dd>
                </dl>
                <dl>
                    <dt><label for="inputCaptcha">문자입력</label></dt>
                    <dd>
                        <input class="cid_act_enter" id="captchaCode" name="captchaCode" type="text" style="width:50%;" maxlength="6"/><span>&nbsp;&nbsp;3분이내 입력</span>
                    </dd>
                </dl>
                <div class="mem_btn">
                    <a href="javascript:login.valid();" class="btn_mem-login cid_act_btn_login" title="로그인"><span>로그인</span></a>
                </div>
            </div>
        </div>
    </form>
</t:layout>
