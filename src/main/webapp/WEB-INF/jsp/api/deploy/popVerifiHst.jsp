<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout type="empty">
<!-- popVerifiHst { -->
<div class="popVerifiHst" title="검증 이력" >
  <!--  popup content Start  -->
  <div class="popup_content distribution_cont ">
    <div class="scroll_box13">
      <div class="pkg_board">
        <!-- writeform -->
        <table class="table-vw cid_item_list" id="verifiHst">
          <caption>검증 이력</caption>
          <colgroup>
            <col style="width:15%;">
            <col style="width:35%;">
            <col style="width:25%;">
            <col style="width:auto;">
          </colgroup>
          <thead>
            <tr>
              <th>No.</th>
              <th>일시</th>
              <th>검증 여부</th>
              <th>상세보기</th>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div><!-- .pkg_board -->
    </div><!-- .scroll_box13 -->
    <div class="brd_tp process_btn">
      <button type="button" title="확인" class="btn btn_black btn_sml" onclick="$('.popVerifiHst').dialog('close')">확인</button>
    </div>
  </div>
</div>
<!-- popVerifiHst } -->

</t:layout>