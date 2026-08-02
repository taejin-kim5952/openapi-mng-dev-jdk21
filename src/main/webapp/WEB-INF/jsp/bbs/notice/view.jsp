<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout type="default">
<%-- 
//  파일명  : view.jsp 
//  작성자  :  js
//  작성일자: 2017/11/14 
//  수정일자: 
//  설명 : 공지사항 상세보기 페이지
--%> 
<script type="text/javascript">
//목록
function fnGoListPage(){
	$('#detailFrm').attr({action:c_url+'bbs/notice/mvNoticeList.do', method:'post'}).submit();
}
//공지사항 상세보기
function fnGoViewPage(pstingId,bbsTypeCd, imptYn){
	location.href=c_url+"bbs/notice/mvNoticeView.do?pstingId="+pstingId+"&bbsTypeCd="+bbsTypeCd+"&imptYn="+imptYn;
	/* $("#detailFrm > input[name='pstingId']").val(pstingId);
	$("#detailFrm > input[name='bbsTypeCd']").val(bbsTypeCd);
	$("#detailFrm > input[name='imptYn']").val(imptYn);
	$('#detailFrm').attr({action:c_url+'bbs/notice/mvNoticeView.do', method:'post'}).submit(); */
}
</script>

<form id="detailFrm" >
	<input type="hidden" id="pstingId" name="pstingId" value=""/>
	<input type="hidden" id="imptYn" name="imptYn" value=""/>
	<input type="hidden" id="bbsTypeCd" name="bbsTypeCd" value=""/>
</form>

<div id="container">
		<div class="sVisual sv_community">
			<div>
				<h2>공지사항</h2>
				<p>Open API의 새로운 소식과 안내사항 입니다</p>
			</div>
		</div>
		<div class="contents">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> 커뮤니티 <span>></span> 공지사항</div>

				<div id="content">
                    <!-- comm_wrap -->
                    <div class="comm_wrap">
                        <div class="pkg_board">
                            <!-- 공지사항 View start -->
                            <section>
                                <table class="table-view">
                                    <caption>공지사항 View Table</caption>
                                    <colgroup>
                                        <col style="width:120px;">
                                        <col style="width:auto;">
                                        <col style="width:160px;">
                                    </colgroup>

                                    <thead>
                                        <tr>
                                            <th scope="row"><div><span>제목</span></div></th>
                                            <th scope="row" class="thTitle"><div><c:out value="${vmap.title}" escapeXml="false"></c:out></div></th>
                                            <th scope="row"><div>${vmap.regDt }</div></th>
                                        </tr>
                                        <!--<tr>
                                            <td class="tdfield"><div>첨부파일</div></th>
                                            <td class="tdFileDwn">
                                            	<div>
                                            		 <c:if test="${not empty fList }">
												        <c:forEach items="${fList}" var="refFiles"  varStatus="idx"> 
													    	<a class="filedwn_txt"  href="<c:url value="/file/fileDownLoad.do?filePath=${refFiles.filePath}&downType=${refFiles.fileTypeCd}&orgFileName=${refFiles.originFileNm}&saveFileName=${refFiles.saveFileNm}" />"  download="${refFiles.originFileNm}">${refFiles.originFileNm}
															</a>
													    </c:forEach>
													  </c:if>
													  <c:if test="${empty fList }">
													  	<li>등록된 첨부파일이 없습니다.</li>
													  </c:if>
                                            	</div></td>
                                            <td><div></div></td>
                                        </tr>-->
                                    </thead>

                                    <tbody>
                                        <tr>
                                            <td colspan="3" class="view_con">
                                            	<div><c:out value="${vmap.sbst}" escapeXml="false"></c:out>
                                            		<c:if test="${not empty fList }">
												        <c:forEach items="${fList}" var="refFiles"  varStatus="idx"> 
												        	<c:if test="${refFiles.fileTypeCd == 'FILTYP1010'}">
												        		<img  src="<c:url value="/file/fileDownLoad.do?filePath=${refFiles.filePath}&downType=${refFiles.fileTypeCd}&orgFileName=${refFiles.originFileNm}&saveFileName=${refFiles.saveFileNm}" />" alt="${viewMap.perfName}"/>
												        	</c:if>
													    </c:forEach>
													  </c:if>
												</div>
												</td>
                                        </tr>
                                    </tbody>
                                    
                                    <tfoot>
                                        <tr>
                                            <th><div><span class="prev_txt">이전글</span></div></th>
                                            <c:choose>
                                                 <c:when test="${vmap.prevPstingId == 0  }">
	                                            	 <td colspan="2"><div>이전 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.prevPstingId}','${ vmap.bbsTypeCd}' , '${ vmap.imptYn}' );" title="${vmap.prevTitle }">${vmap.prevTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                        </tr>
                                        <tr>
                                            <th><div><span class="next_txt">다음글</span></div></th>
                                                <c:choose>
                                                 <c:when test="${vmap.nextPstingId == 0  }">
	                                            	 <td colspan="2"><div>다음 글이 없습니다.</div></td>
	                                            </c:when>
	                                            <c:otherwise><td colspan="2"><div><a href="javascript:fnGoViewPage('${vmap.nextPstingId}','${ vmap.bbsTypeCd}' , '${ vmap.imptYn}' );" title="${vmap.nextTitle }">${vmap.nextTitle }</a></div></td></c:otherwise>
                                            </c:choose>
                                        </tr>
                                    </tfoot>
                                </table>

                            </section>
                            <!-- // 공지사항 View End -->
                        </div>
                        
                        <div class="btn_set">
                            <button type="button" title="목록" class="btn btn_black"  onclick="fnGoListPage()"><span>목록</span></button>
                        </div>
                    </div>
                    <!-- // comm_wrap -->
                </div>
			</div>
		</div>
	</div>
</t:layout>
