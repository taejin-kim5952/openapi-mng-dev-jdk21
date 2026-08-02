<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="pop_arsenal_sync_Hst " title="API 동기화 이력" style="display:none;">
	<!--  popup content Start  -->
	<div class="popup_content distribution_cont ">
    <div class="scroll_box11">
  		<div class="pkg_board">
  			<!-- writeform -->
  			<table class="table-vw table-vw7" id="apiSyncHstList">
  				<caption>API 동기화 이력 Table</caption>
  				<colgroup>
  					<col style="width:10%;">
  					<col style="width:15%;">
  					<col style="width:15%;">
  					<col style="width:20%;">
  					<col style="width:40%;">
  				</colgroup>
  				<thead>
  					<tr>
  						<th>No.</th>
  						<th>일시</th>
  						<th>동기화결과</th>
  						<th>동기화방향</th>
  						<th>상세 로그</th>
  						
 					</tr>
  				</thead>
  				<tbody></tbody>
  			</table>
  		</div><!-- .pkg_board -->
    </div><!-- .scroll_box11 -->
		<div class="brd_tp process_btn">
			<a href="javascript:$('.pop_arsenal_sync_Hst').dialog('close')"><button type="button" title="닫기" class="btn btn_black btn_sml">닫기</button></a>
		</div>
	</div>
</div>

<script>
  $(document).ready(function() {
    $('.pop_arsenal_sync_Hst').dialog({
      autoOpen: false, width: 900, modal: true,
      open: function(event, ui) {
        var $objDialog = $(this).dialog("widget");
        $objDialog.find(".ui-dialog-buttonpane").hide();
      }
    });
  });

  //@RequestParam HashMap<String, String> requestMap
  function apiArsenalHstAjax(apiSpcNo) {
  	var szAjaxUr = '<c:url value="/api/arsenal/selApiHistoryAjax.do"/>';
  	var param    = new Object();
  
  	param.apiSpcNo = typeof apiSpcNo == "undefined" ? $('#pApiSpcNo').val() : apiSpcNo;
  	param.kindOf   = "arsenal";
  	//console.log("apiArsenalHstAjax=>" + param.apiSpcNo);
  
    $.ajax({
      url: szAjaxUr,
      type: 'POST',
      data: JSON.stringify(param),
      crossDomain: true,
      contentType: 'application/json',
      dataType:'json',
      success: function (data) {
        var html = "";
        $("#apiSyncHstList > tbody").children().remove();
        if (data != null && data.nlist.length != 0) {
          i = data.nlist.length;
          $.each(data.nlist, function (index, item) {
            html += ' <tr> ';
            html += '     <td><div>' + i + '</div></td> ';
            html += '     <td><div>' + item.regDt + '</div></td> ';

            if (item.cdNm == "성공") {
          	  html += '     <td><div class="blue_txt">'+item.cdNm+'</div></td> ';
            }
            else {
                html += '     <td><div class="red_txt_arsenal">'+item.cdNm+'</div></td> ';
            }

            html += '     <td><div>' + item.direction + '</div></td> ';
            html += '     <td><div>' + item.memo + '</div></td> ';
            html += ' </tr> ';
            --i;
          }); //each끝
        }
        else {
          html += '<tr>';
          html += '<td colspan="9">';
          html += ' 동기화이력 DATA가 존재하지 않습니다.';
          html += '</td>';
          html += '</tr>';
        }
        $("#apiSyncHstList > tbody").append(html);
        
        //--@@console.log(data);
      },
      error: function (request, status, error) {
        alert("code:" + request.status + "\n" + "error:" + error);
      }
    });
    $(".pop_arsenal_sync_Hst").dialog("open");
  }
</script>