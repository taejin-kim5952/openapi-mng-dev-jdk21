<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="empty">
<div class="popCbDeployHst" title="상용배포이력" >
	<!--  popup content Start  -->
	<div class="scroll_box11">
		<div class="popup_content distribution_cont ">
			<div class="pkg_board">
				<!-- writeform -->
				<table class="table-vw table-vw7" id="deployDetailHstList">
					<caption>상용배포이력 Table</caption>
					<colgroup>
						<col style="width:15%;">
						<col style="width:25%;">
						<col style="width:30%;">
						<col style="width:30%;">
		
					</colgroup>
					<thead>
						<tr>
							<th>No.</th>
							<th>일시</th>
							<th>배포 결과</th>
							<th>상세 로그</th>
						
					</thead>
					<tbody>
						
					   
					</tbody>
				</table>
			</div><!-- .pkg_board -->
    </div><!-- .scroll_box11 -->
		<div class="brd_tp process_btn">
			<button type="button" title="닫기" class="btn btn_black btn_sml" onclick="$('.popCbDeployHst').dialog('close')">닫기</button>
		</div>
	</div>
</div>
</t:layout>