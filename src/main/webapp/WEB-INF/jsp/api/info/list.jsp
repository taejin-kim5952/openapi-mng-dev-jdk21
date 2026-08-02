<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<t:layout type="apiInfo">
<!-- 
   	OPEN API version 1.0
  
   	Copyright ⓒ 2017 kt corp. All rights reserved.
   	
   	This is a proprietary software of kt corp, and you may not use this file except in 
   	compliance with license agreement with kt corp. Any redistribution or use of this 
   	software, with or without modification shall be strictly prohibited without prior written 
   	approval of kt corp, and the copyright notice above does not evidence any actual or 
	intended publication of such software. 
-->
<!-- yaml parser 관련 js파일 -->
<!-- 
<script src="/resources/js/convert/swagger-parser.min.js"></script>
<script src="/resources/js/convert/yaml-json.js" ></script> 
-->

<script type="text/javascript">
//api 선택에 따른 데이터 조회	
function fnSelApiSearch(){
	var param 		= new Object();
	param.schText	= $('#schText').val();
	param.ctgryNm	= $('#ctgryNm').val();
	
	$.ajax({
		url    : '<c:url value="/api/info/selApiAjax.do"/>', 
		type   : 'POST',
		data   : param,
		success: function(data){
			
			// console.log("성공", data);
			
			setTabData(data);
			
		},
		error:function(request,status,error){
			alert("code:"+request.status+"\n"+"error:"+error);
	    }
	});
}
</script>
<input type="text" id="ctgryNm" name="ctgryNm" value="" />
<form id="actionForm" name="actionForm" method="post">
</form>
<div id="container">
		<div class="sVisual sv_apisearch">
			<div>
				<h2>OPEN API 검색</h2>
				<p>KT 플랫폼을 통해 개발된 다양한 Open API 서비스를 검색해 보세요</p>
			</div>
		</div>
		<div class="contents style_gray search_only">
			<div class="conBox">
				<div class="pg_location"><a>Go home</a> <span>></span> OPEN API 검색</div>
				
				<div id="content">
                    <!-- regiApi_wrap -->
                    <div class="regiApi_wrap ">
                        <div class="search_wrap">
                            <p>Search</p>
                            <div class="search_bar">
                                <div><input type="text" id="schText" placeholder="검색어를 입력해주세요."></div>
                                <button type="button" class="btn_search"><span>검색</span></button>
                            </div>
                            <button type="button" class="btn_refresh"><span>새로고침</span></button>
                        </div>
                    </div>
                    <!-- // regiApi_wrap -->

                    <div class="regi_list">
                        <p class="search_result">“ Open API <span>${totalCnt}건</span>을 찾았습니다.”</p>

                        <ul class="tab_list tab_c7">
                        	<li id="sysIdtotal" data-tab="tab1" class="current"><a href="javascript:;" title="전체" onClick="javascript:selList('' );" ><span>전체</span></a></li>
                        	<li id="SHUB" data-tab="tab2" class=""><a href="javascript:;" title="" onClick="javascript:selList('SHUB');" ><span>SHUB</span></a></li>
                        	<li id="IOTMAKERS" data-tab="tab3" class=""><a href="javascript:;" title="" onClick="javascript:selList('IOTMAKERS');" ><span>IoTMakers</span></a></li>
                        	<li id="UCLOUDBIZ" data-tab="tab4" class=""><a href="javascript:;" title="" onClick="javascript:selList('UCLOUDBIZ');" ><span>ucloud biz</span></a></li>
                        	<li id="GIGAGENIE" data-tab="tab5" class=""><a href="javascript:;" title="" onClick="javascript:selList('GIGAGENIE');" ><span>GiGA Genie</span></a></li>
                        	<li id="OLLEHMAP" data-tab="tab6" class=""><a href="javascript:;" title="" onClick="javascript:selList('OLLEHMAP');" ><span>olleh map biz</span></a></li>
                        	<li id="ETC" data-tab="tab7" class=""><a href="javascript:;" title="" onClick="javascript:selList('ETC');" ><span>기타</span></a></li>
                        	<%-- <c:forEach var="item" items="${sysList}" varStatus="status">
                            	<li id="${item.sysId}" data-tab="tab${status.count+1}" class=""><a href="javascript:;" title="${item.sysNm}" onClick="javascript:selList('${item.sysId}');" ><span>${item.sysNm}</span></a></li>
					      	</c:forEach> --%>
                        </ul>
                        
                        <div class="category_wrap">
                            <strong>검색결과</strong>

                            <div class="category_select">
                                <div class="select open">
                                    <span class="ctrl"><span class="arrow"></span></span>
                                    <button type="button" class="myValue">카테고리 검색</button>
                                    <div class="category_list">
                                        <p><span>카테고리</span> 검색</p>

                                        <ul class="innertab_list vertical">
                                        	<c:forEach var="item" items="${sysList}" varStatus="status">
				                            	<c:if test="${status.index == 0}" >
				                                	<li data-tab2="inner_tab${status.count}" class="innercurrent"><a href="javascript:;" title="${item.sysNm}"><span>${item.sysNm}</span></a></li>
				                                </c:if>
				                               	<c:if test="${status.index != 0}" >
				                               		<li data-tab2="inner_tab${status.count}" class=""><a href="javascript:;" title="${item.sysNm}"><span>${item.sysNm}</span></a></li>
				                               	</c:if>
									      	</c:forEach>
                                        </ul>
										<c:forEach var="item" items="${sysList}" varStatus="status">
											<c:if test="${status.index == 0}" >
												<c:set var="innercurrent" value="innercurrent" />
											</c:if>
											<div id="inner_tab${status.count}" class="innertabcontent ${innercurrent}">
												<ul class="aList">
												<c:set var="listCheck" value="N" />
												<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
				                            		<c:if test="${item.sysId eq ctItem.sysId}" >
				                            			<li><a href="javascript:;" onClick="selCateList( '${item.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                	<c:set var="listCheck" value="Y" />
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
									      		</ul>
											</div>
											<c:set var="innercurrent" value="" />
								      	</c:forEach>
									      	
                                        <button type="button" title="카테고리검색 끄기" class="layer_close"><span>카테고리검색 끄기</span></button>
                                        
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div id="tab1" class="tabcontent current">
                            
                            
                        </div>
                        
                    </div>
                    
                </div>
                
			</div>
		</div>
	</div>
</t:layout>
