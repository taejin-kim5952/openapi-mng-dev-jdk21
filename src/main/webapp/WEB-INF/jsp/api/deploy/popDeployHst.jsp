<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout type="empty">
<!-- popDeployHst { -->
<div class="popDeployHst" title="배포이력" >
	<!--  popup content Start  -->
	<div class="popup_content distribution_cont ">
    <div class="scroll_box11">
  		<div class="pkg_board">
  			<!-- writeform -->
  			<table class="table-vw table-vw7 cid_item_list" id="deployDetailHstList">
  				<caption>배포이력 Table</caption>
  				<colgroup>
  					<col style="width:10%;">
  					<col style="width:24%;">
  					<col style="width:26%;">
  					<col style="width:40%;">
  				</colgroup>
  				<thead>
  					<tr>
  						<th>No.</th>
  						<th>일시</th>
  						<th>배포 결과</th>
  						<th>상세 로그</th>
  				</thead>
  				<tbody></tbody>
  			</table>
  		</div><!-- .pkg_board -->
    </div><!-- .scroll_box11 -->
		<div class="brd_tp process_btn">
			<button type="button" title="닫기" class="btn btn_black btn_sml" onclick="$('.popDeployHst').dialog('close')">닫기</button>
		</div>
	</div>
</div>
<!-- popDeployHst } -->

</t:layout>