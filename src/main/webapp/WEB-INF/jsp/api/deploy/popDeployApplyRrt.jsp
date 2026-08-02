<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="empty">
<div class="pop_ver3" title="배포신청서" >
     <!--  popup content Start  -->
      
        <div class="popup_content distribution_cont ">
            <div class="pkg_board">
                <!-- writeform -->
                <table class="table-vw " >
                    <caption>상용 배포 Table</caption>
                    <colgroup>
                        <col style="width:15%;">
                        <col style="width:auto;">
                        <col style="width:25%;">
                        <col style="width:auto;">
                    </colgroup>
                    <tbody>
                        <tr>
                            <th>시스템2</th>
                            <td><span id="divSystemNm"></span></td>
                            <th>서비스</th>
                            <td><span id="divServiceNm"></span></td>
                        </tr>
                        <tr>
                            <th>API 명</th>
                            <td><span id="divApiNm"></span></td>
                            <th></th>
                            <td></td>
                        </tr>
                        <tr>
                            <th>메세지</th>
                            <td class="message_box" colspan="3">
                              <textarea rows="13" id="bigo"></textarea>
                               
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="brd_tp process_btn">
                <button type="button" title="닫기" class="btn btn_sml">닫기</button>
                <button type="button" title="배포 요청" class="btn btn_black btn_sml">배포 요청</button>
            </div>
        </div>
 </div>   

</t:layout>