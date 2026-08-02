<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="default" title="Kt Open API - 회원가입">
    <script type="text/javascript">
        $(document).ready(function(){
            $('#popupConfirm').dialog({ autoOpen: false, dialogClass: 'pop_alert_wrap', modal: true, resizable: false });
            $(document).on('click', '#popupConfirm',function(event) {
                event.preventDefault();
                $('#popupConfirm').dialog('close');
            });
            
            $("#container").hide();
            
            var btnHtm = "";
            btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="confirmS()" id="cBtton">확인</button> ';
            btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" onclick="confirmF()" id="cCbtn">취소</button> ';
            fnOpenLayer(btnHtm, '회원가입','<spring:message code="question.userj.nexist" />' );
        });	

        function confirmS(){
            $("#container").show();
        }

        function confirmF(){
            location.href="<c:url value='/login/loginForm.do'/>";
        }

        //다음 단계 함수
        var nextPage = function(){
            if($("input[name=agree1]").is(':checked') == false){
                fnAgr();
                $("#agree1").focus();
            }else if($("input[name=agree2]").is(':checked') == false){
                fnPriv();
                $("#agree2").focus();
            }else{
                $('#userForm').attr({action:c_url+'userJoin/userInfo.do', method:'post'}).submit();
            }
        }

        function fnAgr(){
            var btnHtm = "";
            btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
            fnOpenLayer(btnHtm, '이용약관 동의','<spring:message code="jagree.chk.msg" />' );
        }

        function fnPriv(){
            var btnHtm = "";
            btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton">확인</button> ';
            fnOpenLayer(btnHtm, '개인정보 수집 및 이용 동의','<spring:message code="jpriv.chk.msg" />' );
        }
    </script>

    <form id="userForm" name="userForm" method="post">
        <input type="hidden" id="stpltAgreeYn" name="stpltAgreeYn" value="Y">
        <input type="hidden" id="indvInfoAgreeYn" name="indvInfoAgreeYn" value="Y">
        <input type="hidden" id="mbrNm" name="mbrNm" value="${mbrNm}">
        <input type="hidden" id="mbrId" name="mbrId" value="${mbrId}">
    </form>
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
                                <li class="stp01 onpg">
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
                                <li class="stp03">
                                    <div>
                                        <strong>STEP 03</strong>
                                        <p>가입완료</p>
                                    </div>
                                </li>
                            </ul>

                            <div class="agree_wrap">
                                <h4 class="brd_title-line">Open API 웹사이트 이용약관</h4>
                               <div class="agree_div"><pre><center><strong>KT Open API 웹사이트 이용약관</strong></center>
... (rest of the terms content) ...
                </pre>
                                </div>
                                <div class="chk_agree">
                                    <a href="javascript:;">
                                        <input type="checkbox" id="agree1" name="agree1" title="이용약관에 동의합니다.">
                                        <label for="agree1"><span></span>이용약관에 동의합니다.</label>
                                    </a>
                                </div>
                            </div>

                            <div class="agree_wrap">
                                <h4 class="brd_title-line">Open API 웹사이트 개인정보 수집 및 이용 동의</h4>
                               <div class="agree_div"><pre><center><strong>KT Open API 웹사이트 개인정보 처리방침</strong></center>
... (rest of the policy content) ...
                </pre>
                                </div>
                                <div class="chk_agree">
                                    <a href="javascript:;">
                                        <input type="checkbox" id="agree2" name="agree2" title="개인정보 수집 및 이용에 동의합니다.">
                                        <label for="agree2"><span></span>개인정보 수집 및 이용에 동의합니다.</label>
                                    </a>
                                </div>
                            </div>
                            <div class="btn_set">
                                <button type="button" title="다음단계" class="btn btn_black" onclick="nextPage();"><span>다음단계</span></button>
                            </div>
                        </div>            
                    </form>
                </div>
            </div>
        </div>
    </div>
</t:layout>
