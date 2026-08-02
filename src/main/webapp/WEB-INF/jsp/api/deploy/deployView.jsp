<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="apiInfo">
<c:set var="b_is_master" value="${fn:toLowerCase(sessionScope['dev.master.id']) eq 'master'}" />

<script type="text/javascript">
  var deployCd;
 
   //KOS운영자 등급 확인

 

  $(function () {
    //프로세스 이미지 
    fnProc();

    //배포 버튼 클릭 이벤트 
    $('#btnDeployApply').on('click', function () {
      deployApply();
    });

    //검증이력  
    $('#btnVeriHst').on('click', function () {
      veriHistOpen();
    });

    //상용 배포 요청 기본 값 세팅 
    $('textarea[id=applyMsg]').val("** ${deployMap.apiNm} 배포 요청 **\nAPI ID : ${deployMap.apiId} \nAPI명 : ${deployMap.apiNm} \n요청날짜 :   ");

    //API리스트를 
    $('#deploy_apiList').val(JSON.stringify({
      'api_no': '${deployMap.apiNo}',
      'gw_profile': 'TB',
      'api_nm': '${deployMap.apiNm}',
      'proc_seq': '${deployMap.seq}',
      'api_id': '${deployMap.apiId}'
    }));

    //배포하기 버튼 클릭 이벤트
    $('#tbDeployA').on('click', function () {
      var onclick_handler = (function() {
        fn_api_deploy();
      });
      popConfirmAlert("deployView", "tbDeploy", "API배포", onclick_handler);
    });

    //배포삭제하기 버튼 클릭 이벤트
    $('#tbDeployA_delete').on('click', function () {
      fn_api_deployDelete('TB', '${deployMap.apiNo}');
      /*--
      var onclick_handler = (function() { fn_api_deployDelete('TB', '${deployMap.apiNo}'); });
      popConfirmAlert("deployView", "tbDeployDelete", "API배포삭제", onclick_handler);
      --*/
    });
  });

  /* 상세 내역 view ajax  */
  function fnProc() {
    var param = new Object();
    param.seq = '${deployMap.seq}'
    $.ajax({
      url: '<c:url value="/api/deploy/mvDeployViewProcAjax.do"/>',
      type: 'POST',
      data: param,
      success: function (data) {
        var lenPrntsCd = data.vmap.prntsCd;
        var verification_on;

        //배포 프로세스 이미지 
        for (i = 0; i < 6; i++) {
          //alert(lenPrntsCd.substring(i,i+1))
          if (lenPrntsCd.substring(i, i + 1) == "1") {
            verification_on = "verification_on";
          } else {
            verification_on = "";
          }
          switch (i) {
            case 0:
              $('#TbDployPre').addClass(verification_on);
              break;
            case 1:
              $('#TbDployEnd').addClass(verification_on);
              break;
            case 2:
              $('#Verifimid').addClass(verification_on);
              break;
            case 3:
              $('#VerifiEnd').addClass(verification_on);
              break;
            case 4:
              $('#DepoloPre').addClass(verification_on);
              break;
            case 5:
              $('#DepoloEnd').addClass(verification_on);
              break;
          }
        }
        deployCd = data.vmap.deployCd;

        var tbDeployA;
        var verifiA;
        var cbDeployA;

        //deploy cd에 맞춰 버튼 활성화 여부 결정
        if (data.vmap.deployCd == "DEPLOY1010") {
          $('#tbDeployA').removeClass();
          $('#tbDeployA').addClass('btn4 btn_gray');
        }
        else if (data.vmap.deployCd == "DEPLOY1020") {
          $('#verifiA').removeClass();
          $('#verifiA').addClass('btn4 btn_gray');
        }
        else if (data.vmap.deployCd == "DEPLOY1040") {
          $('#cbDeployA').removeClass();
          $('#cbDeployA').addClass('btn4 btn_gray');
        }
        else if (data.vmap.deployCd == "DEPLOY1063") { //상용 배포 실패 처리 
          $('#cbDeployA').removeClass();
          $('#tbDeployA').removeClass();
          $('#cbDeployA').addClass('btn4 btn_gray');
          $('#tbDeployA').addClass('btn4 btn_gray');

          $('#cbDeployDiv').text('배포실패');
          $('#cbDeployA').text('재배포요청')
          $('#tbDeployA').text('TB재배포');
        }
        else if (data.vmap.deployCd == "DEPLOY1013") { //TB 배포 실패 처리 
          $('#tbDeployA').removeClass();
          $('#tbDeployA').addClass('btn4 btn_gray');

          $('#tbDeployA').text('TB재배포');
          $('#tbDeployDiv').text('TB배포 실패')
        }
        else if (data.vmap.deployCd == "DEPLOY1065") { //배포 반려
          $('#tbDeployA').removeClass();
          $('#tbDeployA').addClass('btn4 btn_gray');

          $('#tbDeployA').text('TB재배포');
          $('#cbDeployDiv').text('상용배포 반려');

          $('#cbDeployA').text('재배포 요청');
          $('#cbDeployA').removeClass();
          $('#cbDeployA').addClass('btn4 btn_gray');
          // $('#tbDeployDiv').text('TB배포 실패')
        }
        else if (data.vmap.deployCd == "DEPLOY1030") { //배포 반려
          $('#verifiA').removeClass();
          $('#verifiA').text('검증중');
          $('#verifiA').addClass('btn4 btn_gray');
        }
        else {
          $('#tbDeployA').removeClass();
          $('#tbDeployA').addClass('btn4 btn_lightGray');

          $('#verifiA').removeClass();
          $('#verifiA').addClass('btn4 btn_lightGray');

          $('#cbDeployA').removeClass();
          $('#cbDeployA').addClass('btn4 btn_lightGray');
        }
      },
      error: function (request, status, error) {
        alert('프로세스 조회에 실패했습니다.');
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
  }

  /* 배포 요청  */
  function deployApply() {
    var param = new Object();
    param.deployProcSeq = '${deployMap.seq}'
    param.bigo = $("textarea[id='applyMsg']").val();
    //console.log("param.bigo : "+$("textarea[id='applyMsg']").val());
   
    $.ajax({
      url: '<c:url value="/api/deploy/mvDeployApply.do"/>',
      type: 'POST',
      data: param,
      success: function (data) {
        if (data.deployApplySeq > 0) {
          $("#popup_stocked").dialog("close");
          popCommonAlert("deployView", "deployApply");
          fnProc();
        }
        else {
          alert('상용 배포 요청에 실패했습니다. 관리자에게 문의 하시기 바랍니다.');
        }
      },
      error: function (request, status, error) {
        alert('상용 배포 요청에 실패했습니다. 관리자에게 문의 하시기 바랍니다.');
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
    
  }

  /*  리스트 페이지로 이동하기   */
  function fnGoListPage(qnaId) {
    location.href = c_url + "api/deploy/mvDeployList.do";
  }

  /* 검증 클릭시 검증 중으로 변경 */
  function fnVerifyProc() {
    var returnCd;
    var param = new Object();
    param.procSeq = '${deployMap.seq}'

    $.ajax({
      url: '<c:url value="/api/deploy/verifyStartAjax.do"/>',
      type: 'POST',
      async: false,
      data: param,
      success: function (data) {
        //--alert(data.returnCd);
        returnCd = data.returnCd;
      },
      error: function (request, status, error) {
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });

    return returnCd;
  }

  /*  배포 / 검증 버튼 클릭 이벤트    */
  var deploy = {
    tbExcute: function () {
      if (deployCd == "DEPLOY1010") {
        if (confirm("TB배포를 진행하시겠습니까?")) {
          //--- TB배포 로직이 들어감 --//
        }
      }
      else {
        alert("TB배포는 TB배포전 단계에서만 가능합니다.");
      }
    },
    tbCallHst: function () { },
    verifiHstCall: function () {
      veriHistOpen();
      $(".popVerifiHst").dialog("open");
      /* if(0 < "${deployMap.tbCnt}"){  //verifiCnt검증이력이 있는 경우 오픈
          veriHistOpen();
          $(".popVerifiHst").dialog( "open" );
        }else{
          alert("검증 이력이 없습니다.");
        } */
    },
    verifiCall: function (p_evt) {
    <c:choose>
      <c:when test="${b_is_master}">
      if ((0 < "${deployMap.tbSuccessCnt}" && deployCd >= "DEPLOY1020") || (true == p_evt.ctrlKey)) {
      </c:when>
      <c:otherwise>
      if (0 < "${deployMap.tbSuccessCnt}" && deployCd >= "DEPLOY1020") {
      </c:otherwise>
    </c:choose>
        var Proc = "000";
        //검증 이전 단계일경우 검증 중으로 변경
        if (deployCd <= "DEPLOY1030") {
          var Proc = fnVerifyProc();
        }
        if (Proc == "000") {
          setSession("apiNo", "${deployMap.apiNo}", "api/deploy/mvVerifyExecute.do");
          //--@@@location.href=c_url+"api/deploy/mvVerifiExcute.do";
          location.href = c_url + "api/deploy/mvVerifyExecute.do";
        }
      }
      else {
        popCommonAlert("deployView", "tbNoDpApply");
      }
    },
    cbExcute: function () { //상용배포 요청 
        /*
      if("${deployMap.apiVer}" != "v1.0"){
    	popCommonAlert("deployView", "cbApplyVerCheck");
      }else if (deployCd == "DEPLOY1040" || deployCd == "DEPLOY1065") { // 배포 실패시 재배포 요청 후 배포 진행
        $(".popCbApply").dialog("open");
      }
	*/

	  if (deployCd == "DEPLOY1040" || deployCd == "DEPLOY1065") { // 배포 실패시 재배포 요청 후 배포 진행
        $(".popCbApply").dialog("open");
      }
      else {
        //alert("상용 배포 요청은 검증이 완료된 후 가능합니다.");
        popCommonAlert("deployView", "cbDpApply");
      }
    },
    cbCallHst: function () { },
    deployHst: function (e) {
      deployHstAjax(e);
      $(".popDeployHst").dialog("open");
    }
  }

  var fnGoUrl = function (url) {
    location.href = url;
  }
  
  <c:if test="${deployMap.requiredCnt > '0' }">
	alert("bizheader(하위 orderId, cbSvcName, cbFnName 포함)의 경우 SHUB에서 자동 입력되어 전송되기 때문에 사용자는 알지 못하는 값입니다.\n반드시 필수 해제해주시기 바랍니다.");
  </c:if>
</script>

<%-- [chk][dep][form:form] --%>
<%-- <form:form commandName="apiDeploySearchVo"> --%>

  <div id="container">
    <div class="sVisual sv_regiapi">
      <div>
        <h2>API 배포</h2>
        <p>여러분이 생각하는 모든 생각들을 API로 만들고 KT 플랫폼을 이용하여 서비스 해보세요</p>
      </div>
    </div>
    <div class="contents">
      <div class="conBox">
        <div class="pg_location"><a href="javascript:void(0);">Go home</a> <span>></span> API 배포</div>
        
        <div id="content">
          <h5 class="rTitleOneDep">API 배포</h5>
          <div class="pkg_board">
            <table class="table-vw">
              <caption>API 배포</caption>
              <colgroup>
                <col style="width:12%;">
                <col style="width:37%;">
                <col style="width:12%;">
                <col style="width:37%;">
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row"><div>API명</div></th>
                  <td><div>${deployMap.apiNm}</div></td>
                  <th scope="row"><div>작성자</div></th>
                  <td><div>${deployMap.regr}</div></td>
                </tr>
                <tr>
                  <th scope="row"><div>Path</div></th>
                  <td><div>${deployMap.apiPath}</div></td>
                  <th scope="row"><div>작성일</div></th>
                  <td><div>20${deployMap.regDt}</div></td>
                </tr>
                <tr>
                  <th scope="row"><div>Handler</div></th>
                  <td><div>${deployMap.handlerNm}</div></td>
                  <th scope="row"><div>카테고리</div></th>
                  <td><div>${deployMap.ctgryNm}</div></td>
                </tr>
                <tr>
                  <th scope="row"><div>API ID</div></th>
                  <td><div>${deployMap.apiId}</div></td>
                  <th scope="row"><div>bizHeader</br>필수여부</div></th>
                  <td>
                    <div>
                    
                    <c:if test="${deployMap.requiredCnt > '0' }">
                      Y  
                    </c:if>
                    <c:if test="${deployMap.requiredCnt eq '0' }">
                      N 
                    </c:if>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>EndPoint</div></th>
                  <td><div>${deployMap.endpntTbUrl}</div></td>
                  <th scope="row"><div>검증상태</div></th>
                  <td>
                    <div class="verification_btn">
                      <div>${deployMap.vericdNm}</div>
                      <div class="file_btn">
                    <c:if test="${deployMap.deployProcCd > 'DEPLOY1030' }">
                      <button type="button" title="검증 이력" class="btn6 btn_gray" id="btnVeriHst">검증 이력</button>
                    </c:if>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>검증상태</div></th>
                  <td colspan="3">
                    <div class="verification">
                      <div id="TbDployPre" class="verification_cont">
                        <div class="verification_progress "><p class="verification01">1</p></div>
                        <div class="verification_txt" id="tbDeployDiv">TB 배포 전</div>
                      </div>
                      <div id="TbDployEnd" class="verification_cont">
                        <div class="verification_progress"><p class="verification02">1</p></div>
                        <div class="verification_txt">TB 배포완료</div>
                      </div>
                      <div  id="Verifimid"  class="verification_cont">
                        <div class="verification_progress"><p class="verification03">1</p></div>
                        <div class="verification_txt">검증 중</div>
                      </div>
                      <div id="VerifiEnd" class="verification_cont">
                        <div class="verification_progress"><p class="verification04">1</p></div>
                        <div class="verification_txt">검증 완료</div>
                      </div>
                      <div id="DepoloPre" class="verification_cont">
                        <div class="verification_progress"><p class="verification05">1</p></div>
                        <div class="verification_txt" id="cbDeployDiv">상용배포 대기중</div>
                      </div>
                      <!-- verification_on 배포 완료 -->
                      <!-- verification_companion 반려 -->
                      <div  id="DepoloEnd" class="verification_cont">
                        <div class="verification_progress"><p class="verification06">1</p></div>
                        <div class="verification_txt">배포완료</div>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>TB배포</div></th>
                  <td colspan="3">
                    <div>
                      <ul class="file_btn">
                        <li>
                          <a class="btn4 btn_lightGray" href="javascript:void(0);" id="tbDeployA">배포하기</a>
                          <a class="btn4 btn_bd_gray" href="javascript:void(0);" onclick="deploy.deployHst('T');">TB 배포이력</a>
                          <a class="btn4 btn_lightGray" href="javascript:void(0);" id="tbDeployA_delete">배포삭제</a>
                        </li>
                      </ul>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>검증</div></th>
                  <td colspan="3">
                    <div>
                      <ul class="file_btn">
                        <li>
                          <a id="verifiA" class="btn4 btn_lightGray" href="javascript:void(0)" onclick="deploy.verifiCall(event)">검증</a>
                          <a class="btn4 btn_bd_gray" href="javascript:void(0)" onclick="deploy.verifiHstCall()">검증 이력</a>
                        </li>
                        <li class="file_btn_txt"><p></p></li>
                      </ul>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>상용배포</div></th>
                  <td colspan="3">
                    <div>
                      <ul class="file_btn">
                        <li>
                          <a id="cbDeployA" class="btn4 btn_lightGray" href="javascript:void(0);" onclick="deploy.cbExcute();">배포요청</a>
                          <a class="btn4 btn_bd_gray" href="javascript:void(0);" onclick="deploy.deployHst('C');">상용 배포이력</a>
                        </li>
                      </ul>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div class="bottom_btn">
              <a class="btn5 btn_bd_black" href="javascript:void(0);" onclick="fnGoListPage();">목록</a>
              <a class="btn5 btn_black" href="javascript:void(0);" onclick="fnEditApi();">API수정</a>
            </div>
          </div><!-- .pkg_board -->
        </div><!-- #content -->

      </div><!-- .conBox -->
    </div><!-- .contents -->
  </div><!-- #container -->

  <!--  // 검증 이력   -->
  <%@ include file="/WEB-INF/jsp/api/deploy/popVerifiHst.jsp" %>
  <!--  // 검증 이력 끝   -->     
       
  <!--  // 상용 배포 요청   -->
  <%@ include file="/WEB-INF/jsp/api/deploy/popCbApply.jsp" %>
  <!--  // 상용 배포 요청 끝   -->
     
  <!-- 배포 이력  -->
  <%@ include file="/WEB-INF/jsp/api/deploy/popDeployHst.jsp" %>
  <!-- 배포 이력 끝 -->
  
  <!--  // popup  - 공통 alert  -->
  <%@ include file="/WEB-INF/jsp/api/deploy/popCommonAlert.jsp" %>
  <!--  // popup  - 공통 alert  -->
  
  <!--  // popup  - 공통 confirm  -->
  <%@ include file="/WEB-INF/jsp/api/deploy/popConfirmAlert.jsp" %>
  <!--  // popup  - 공통 confirm  -->

<%-- [chk][dep][form:form] --%>
<%-- </form:form> --%>

<!-- laypop script -->
<script>
  function veriHistOpen() {
    var obj = new Object();
    var i = 0
    // obj.deployApplySeq = applySeq;
    obj.deployProcSeq = '${deployMap.seq}'
  
    $.ajax({
      url: '<c:url value="/api/deploy/mvVerifiResltAjax.do"/>',
      type: 'POST',
      data: obj,
      success: function (data) {
        var html = "";
        $("#verifiHst > tbody").children().remove();
        if (data != null && data.nlist.length != 0) {
          i = data.nlist.length
          $.each(data.nlist, function (index, list) {
            html += '<tr>';
            html += '<td>' + i + '</td>';
            html += '<td>' + fnConverDate(list.verificationDt) + '</td>';
            if (list.successYn == "Y") {
              html += '<td class="blue_txt">성공</td>';
            }
            else {
              html += '<td class="red_txt">실패</td>';
            }
            // html +=  '<td class="blue_txt">' + list.resultMsg +'</td>';
            html += '<td><a href="javascript:void(0);" onclick="verifiHst(\'' + list.seq + '\')"><button type="button" title="확인" class="btn btn9 btn_sml">확인</button></a></td>';
            html += '</tr>';
            i--;
          });
          $("#verifiHst > tbody").append(html);
        }
        else {
          html += '<tr>';
          html += '<td colspan="4">';
          html += ' 검색된 DATA가 존재하지 않습니다.';
          html += '</td>';
          html += '</tr>';
          $("#verifiHst > tbody").append(html);
        }
      },
      error: function (request, status, error) {
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
    $(".popVerifiHst").dialog("open");
  }

  // 검증이력 dialog
  modalPop(".popVerifiHst", 550);

  function verifiHst(verify_seq) {
    $(".popVerifiHst").dialog("close");

    var fn_cb_verify_view = (function(o_ret) {
      window.setTimeout((function () { $(".popVerifiHst").dialog("open"); }), 50);
      var s_msg = '';
      var b_ret = $sf_obj_val(o_ret, 'return', false);
      if (b_ret == true) {
        var data = $sf_obj_val(o_ret, 'data');
        var verify_seq = $sf_obj_val(data, 'verify_seq', '');
      }
    });
    (g_vue_comp_adptranService && g_vue_comp_adptranService.proc_verify_view(verify_seq, fn_cb_verify_view));
  }
  
  // 상용배포 dialog
  modalPop(".popCbApply", 750);

  $(".btn_popup_close").click(function (p_evt) {
    p_evt.preventDefault();
    $("#popup_stocked").dialog("close");
  });

  //-- { TB 배포 이력 --
  //@RequestParam HashMap<String, String> requestMap
  function deployHstAjax(e) {
    var obj = new Object();
    obj.deployProcSeq = '${deployMap.seq}';
    obj.deployGb = e;
    //alert(e);
    $.ajax({
      url: '<c:url value="/api/deploy/mvTbDeployHstAjax.do"/>',
      type: 'POST',
      data: obj,
      success: function (data) {
        var html = "";
        $("#deployDetailHstList > tbody").children().remove();
        //배포 요청 리스트
        if (data != null && data.nlist.length != 0) {
          i = data.nlist.length;
          $.each(data.nlist, function (index, item) {
            succesYn = (item.successYn == 'Y') ? '성공' : '실패'
            html += ' <tr> ';
            html += '     <td><div>' + i + '</div></td> ';
            html += '     <td><div>' + fnConverDate(item.deployDate) + '</div></td> ';
            if (item.successYn == "Y") {
              html += '     <td><div class="blue_txt">성공</div></td> ';
            }
            else {
              html += '     <td><div class="red_txt">실패</div></td> ';
            }
            html += '     <td><div>' + item.resultMsg + '</div></td> ';
            html += ' </tr> ';
            --i;
          }); //each끝
        }
        else {
          html += '<tr>';
          html += '<td colspan="9">';
          html += ' 검색된 DATA가 존재하지 않습니다.';
          html += '</td>';
          html += '</tr>';
        }
        $("#deployDetailHstList > tbody").append(html);
      },
      error: function (request, status, error) {
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
    $(".popDeployHst").dialog("open");
  }
  modalPop(".popDeployHst", 750);
  // --TB 배포 이력 끝}

  //-- { 상용 배포 이력 --
  //--##modalPop(".pop_ver_CbDeploy_Hst", 750);
  //--}

  //공통 ALERT 
  modalPop(".popup_commonAlert", 430);
  // -- }

  //공통 ALERT 
  modalPop(".popup_confirmAlert", 430);
  // -- }

  //API수정
  function fnEditApi() {
    var onclick_handler = (function() {
      fnGoUrl('/apidev/api/main/mvMainList.do');
    });
    popConfirmAlert("deployView", "editApi", "API수정", onclick_handler);
  }

  //kos운영자에게는 노출시키지 않음
  <c:forEach items="${ssUserVo.authList}" var="item">
	  if("${item.autNm}" == "KOS 운영자 그룹"){
		  $('#tbDeployA').hide(); 
		  $('#cbDeployA').hide(); 
		  $('#verifiA').hide(); 
	   }
  </c:forEach>
  
</script>

<input type="hidden"  id="deploy_apiList">

<%-- //-- [tag:adpt][drm][add] --%>
<jsp:include page="/WEB-INF/jsp/adptran/vue_part_mount_adptranService.jsp" flush="false" />

</t:layout>
