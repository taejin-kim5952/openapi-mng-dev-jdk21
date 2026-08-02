<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - Q&A">
<script type="text/javascript">
$(function(){
    if('${qnaId}'   != ''  && '${msg} ' != ''){ // 등록 혹은 수정 이벤트 발생 시
        var btnHtm = "";
        btnHtm=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm"  onclick="fnForwardView()">확인</button> ';
        fnOpenLayer(btnHtm, '작성완료', '${msg}');
        return;
    }else if('${qnASaveVO.qnaId}'   != ''  && '${vmap.qnaId}' != null   ) { //수정 일경우 
        $('#bbsFrm').find('#qnaId').val('${qnASaveVO.qnaId}');
    }else{
        var strMbrid = '${ssUserVo.mbrId}';
        strMbrid = strMbrid.substring(0,strMbrid.length-3);
        strMbrid = strMbrid +"***";
        $('#bbsFrm').find('#regr').text(strMbrid); // 추후 세션에서 읽어온 마스킹 유저 정보로 대체 예정
        var now = new Date();
        var year= now.getFullYear();
        var mon = (now.getMonth()+1)>9 ? ''+(now.getMonth()+1) : '0'+(now.getMonth()+1);
        var day = now.getDate()>9 ? ''+now.getDate() : '0'+now.getDate();
        $('#bbsFrm').find('#regDt').text(year+'-'+mon+'-'+day); // 금일 날짜
        $('#regrDt').text(year+'-'+mon+'-'+day); // 금일 날짜
    }
});

//상세 보기 화면 이동
function fnForwardView(){
    $('#frm > #qnaId').val('${qnaId}');
    $('#frm').attr('action', c_url+'qna/mvQnaView.do' ).submit();
}

//목록 조회 페이지로 이동
function fnGoListPage(){
    $('#frm').attr({action:c_url+'qna/mvQnAList.do', method:'post'}).submit();
}

// QnA 저장 (첨부파일 없음)
function fnSaveQna(){
    $("#bbsFrm").ajaxForm({
        url : '<c:url value="/qna/saveQna.do"/>',
        type : 'POST',
        dataType : 'json',
        success : function(result) {
            alert("등록"+result.msg);
            $('#frm').find('#qnaId').val(result.qnaId);
            $('#frm').attr({action:c_url+'qna/mvQnAList.do', method:'post'}).submit();
        },
        error:function(request,status,error){
            alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
        }
    });
    $("#bbsFrm").submit();
}

//저장 레이어 오픈 
function fnLayer(){
    var btnHtm = "";
    //레이어 버튼 교체
    $("#popupConfirm").find('.lPop_bottom.brd_tp').children().remove();
    btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnSaveQna()"  id="cBtton">확인</button> ';
    btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
    $("#popupConfirm").find('.lPop_bottom.brd_tp').append(btnHtm);
    //레이어 메세지 적용
    $("#ui-id-1").text('작성완료');
    $("#popupConfirm").find('#alertTxt').text('<spring:message code="save.req.msg" />');
    $("#popupConfirm").dialog("open");
}
</script>

<!-- 수정 페이지 이동 폼 -->
<form name="frm" id="frm" method="post">
    <input type="hidden" id="qnaId" name="qnaId" value="">
</form>

<form:form modelAttribute="qnASaveVO" id="bbsFrm" method="post">
    <form:hidden path="qnaId"  id="qnaId"  />
    <div id="container">
        <div class="sVisual sv_community">
            <div>
                <h2>Q&A</h2>
                <p>API에 대해 궁금한 점을 물어보세요</p>
            </div>
        </div>
        <div class="contents">
            <div class="conBox">
                <div class="pg_location"><a>Go home</a> <span>></span> 커뮤니티 <span>></span> Q&A</div>

                <div id="content">
                    <!-- comm_wrap -->
                    <div class="comm_wrap">
                        <div class="pkg_board">
                            <!-- 개발자 포럼 Write start -->
                            <section>
                                <table class="table-vw">
                                    <caption>개발자 포럼 Write Table</caption>
                                    <colgroup>
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                        <col style="width:12%;">
                                        <col style="width:37%;">
                                    </colgroup>

                                    <tbody>
                                        <tr>
                                            <th scope="row"><div>글쓴이</div></th>
                                            <td><div id="regr">${vmap.regrMasking}</div></td>
                                            <th scope="row"><div>작성일</div></th>
                                            <td><div id="regrDt">${vmap.qstnDt}</div></td>
                                        </tr>
                                        
                                        <tr>
                                            <th scope="row"><div>제목</div></th>
                                            <td colspan="3">
                                                <div>
                                                    <form:input path="title" size="20" id="title"
                                                        onchange="CheckStrLength(500,'title')"
                                                        onkeyup="CheckStrLength(500,'title')"/>
                                                    <form:errors path="title" />
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th scope="row"><div>내용</div></th>
                                            <td colspan="3">
                                                <div class="txtarea_wrap">
                                                    <form:textarea path="qstn" id="qstn"
                                                        onchange="CheckStrLength(4000,'qstn')"
                                                        onkeyup="CheckStrLength(4000,'qstn')"/>
                                                    <form:errors path="qstn" />
                                                </div>
                                            </td>
                                        </tr>

                                        <!-- 첨부파일 관련 TR 전체 제거됨 -->
                                    </tbody>
                                </table>

                            </section>
                            <!-- // 개발자 포럼 Write End -->
                        </div>
                        
                        <div class="btn_set">
                            <button type="button" title="저장" class="btn btn_black" onclick="fnLayer()"><span>저장</span></button>
                            <button type="button" title="목록" class="btn" onclick="fnGoListPage()"><span>목록</span></button>
                        </div>

                    </div>
                    <!-- // comm_wrap -->
                </div>
            </div>
        </div>
    </div>
</form:form>
</t:layout>