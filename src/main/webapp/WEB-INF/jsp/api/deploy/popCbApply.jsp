 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout type="empty">
<!-- popCbApply { -->
<div class="popCbApply" title="상용 배포" >
  <!--  popup content Start  -->
  <div class="popup_content distribution_cont ">
    <div class="pkg_board">
      <!-- writeform -->
      <table class="table-vw ">
        <caption>상용 배포 Table</caption>
        <colgroup>
          <col style="width:15%;">
          <col style="width:auto;">
          <col style="width:15%;">
          <col style="width:auto;">
        </colgroup>
        <tbody>
          <tr>
            <th>시스템</th>
            <td>${deployMap.sysNm}</td>
            <th>카테고리</th>
            <td>${deployMap.ctgryNm}</td>
          </tr>
          <tr>
            <th>API 명</th>
            <td>${deployMap.apiNm}</td>
            <th></th>
            <td></td>
          </tr>
          <tr>
            <th>메세지</th>
            <td class="message_box" colspan="3">
              <%-- [chk][dep][form:form] --%>
              <%-- <form:textarea path="applyMsg" rows="16"  id="applyMsg" onchange="CheckStrLength(3000,'applyMsg')" onkeyup="CheckStrLength(3000,'applyMsg')"/><form:errors path="applyMsg" /> --%>
              <textarea rows="16"  id="applyMsg" onchange="CheckStrLength(3000,'applyMsg')" onkeyup="CheckStrLength(3000,'applyMsg')"></textarea>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="brd_tp process_btn">
        <button type="button" title="닫기" class="btn btn_sml" onclick="$('.popCbApply').dialog('close')">닫기</button>
        <button type="button" id="btnDeployApply" title="배포 요청" class="btn btn_black btn_sml">배포 요청</button>
    </div>
  </div>
</div>
<!-- popCbApply } -->

</t:layout>