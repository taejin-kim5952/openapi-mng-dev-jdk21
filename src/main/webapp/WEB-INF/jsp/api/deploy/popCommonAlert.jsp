<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout type="empty">
<!-- popCommonAlert { -->
<div id="popup_commonAlert" class="popup_commonAlert " title="알림" >
  <!--  popup content Start  -->
  <div class="popup_content distribution_cont ">
    <div class="pkg_board"></div>
    <div class="btn_txt">
      <!--
      <p id="msg_1">상용 배포 요청이 반려 되었습니다.</p>
      <p id="msg_2">(주의) 배포를 취소하실 경우, API는 서비스에서 삭제됩니다.</p>
      <p><b>문의메일 apilink@kt.co.kr</b></p>
      -->
    </div>
    <div class="brd_tp process_btn">
      <button type="button" title="확인"  class="btn btn_black btn_sml" onclick="$('#popup_commonAlert').dialog('close');">확인</button>
    </div>
  </div>
</div>
<!-- popCommonAlert } -->

</t:layout>