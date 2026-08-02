<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.kt.openapi.web.userJoin.vo.UserJoinVO"%>
<%@ page import="com.kt.openapi.web.auth.vo.AuthVO"%>

<t:layout type="default" title="API 검색">
<%-- //-- 예외처리group --%>
<c:set var="b_is_except_group_A" value="false" />
<c:if test="${not empty ssUserVo}">
	<c:forEach var="authVo" items="${ssUserVo.authList}">
		<c:if test="${not b_is_except_group_A}">
			<c:if test="${authVo.autNm eq 'A/G 운영자 그룹'}">
				<c:set var="b_is_except_group_A" value="true" />
			</c:if>
		</c:if>
	</c:forEach>
</c:if>

<script src="<c:url value="/resources/js/pub/swiper.min.js" />"></script>    
<script type="text/javascript">
$(document).ready(function() {
	fnSearch(1);//조회
});

function fnSearch(pageIdx){
	var sysId = "";
	$(".swiper-wrapper > .swiper-slide").each(function(idx){
   		if($(this).hasClass("on") == true  ){
   			sysId = $(this).attr("svscd");
   		}
	});

	var param = {
			searchKeyword: $("#searchKeyword").val(),	
			searchCondition: $("#searchCondition").val(),
			pageIndex: pageIdx,
			sysId:sysId,
			searchYn : 'true'
	};
<c:if test="${b_is_except_group_A}">
		<%-- //-- regSttusCd: [all, APIREG1010, APIREG1020, APIREG1030, APIREG1040] --%>
		param['regSttusCd'] = 'all';
</c:if>
	$.ajax({
	 	url    : '<c:url value="/api/search/apiSearchListAjax.do"/>', 
	 	type   : 'POST',
	 	data   : param,
	 	dataType: "json",
	 	success: function(data){
	 		$('.tb_search').children().remove();
	 		var htm = "";
	 		if(data.nlist != null && data.nlist.length != 0){
	 			$('#serachResult').children().remove();
	 			var resultMsg = 'API <span>'+comma(data.paginationInfo.totalRecordCount)+'건</span>을 찾았습니다.';
	 			$('#serachResult').html(resultMsg);
	 			$.each(data.nlist, function(i, item) {
	 				htm +='<li>';
		 			htm +='	<div class="tit_api">';
		 			htm +='		<span>'+item.sysIdNm+'</span> <strong>'+item.apiSpcNm+'</strong>';
		 			htm +='	</div>';
		 			htm +='	<div class="con_api">';
		 			htm +='		<a href="javascript:goApiInfo(\''+item.apiSpcNo+'\', \''+item.apiNo+'\');">';
		 			if(item.showStatusCd == 'SHOWST1010'){//대내
		 				htm += '<span class="ico_keyR">';
		 			}else if(item.showStatusCd == 'SHOWST1020'){//대외
		 				htm += '<span class="ico_keyB">';
		 			}
		 			htm += item.fApiNm+'</span>';
		 			htm +='			<p>'+item.fApiDesc+'</p>';
		 			htm +='		</a>';
		 			htm +='	</div>';
		 			htm +='</li>';
				});
				drawPaging('paging' ,data.paginationInfo.currentPageNo ,data.paginationInfo.firstPageNoOnPageList ,data.paginationInfo.totalPageCount,data.paginationInfo.lastPageNoOnPageList  ,'pageGo');
	 		}else{
	 			$('#serachResult').children().remove();
	 			var resultMsg = 'API <span>0건</span>을 찾았습니다.';
	 			$('#serachResult').html(resultMsg);
	 			htm +='<li>';
	 			htm +='	<div class="ac">데이터가 없습니다.</div>';
	 			htm +='</li>';
	 		}
	 		$('.tb_search').append(htm);
	    }
	});
}

var goApiInfo = function(apiSpcNo, apiNo){
	window.open("about:blank").location.href = c_url+'api/info/mvInfoView.do?apiSpcNo='+apiSpcNo+'&apiNo='+apiNo;
}

//서비스 메뉴 클릭
function fnService(obj){
	if($(obj).attr('svscd') == ''){//서비스 전체 클릭 시 검색 항목 초기화
		$('#searchCondition option').eq(0).prop('selected', true);
		$('#searchKeyword').val('');
	}
	$(".swiper-wrapper > .swiper-slide").removeClass("on");
	$(obj).parent().addClass("on");
	fnSearch(1);
}	

//페이징 조회
function pageGo(pageIndex){
	fnSearch(pageIndex);
}

//키워드 검색
function fnKeywordSearch(){
	if($("#searchCondition option:selected").val() != "" && $('#searchKeyword').val().trim() == ""){
		alert('검색어를 입력해 주세요.');
		return;
	}
	fnSearch(1);
}


</script>
<style>
	header{
	    position: fixed;
	    width: 100%;
	    height: 107px;
	    background: rgba(24,24,24,.85);
	    z-index: 3;
	    top: 0;
	}
</style>
<div id="container">
	<div class="tit_contents tit_Api" style="margin-top: 107px;">
		<div style="padding-top:180px important;">
			<h2>API 검색</h2>
			<span style="line-height:50px !important;">무엇이 궁금하신가요?</span>
			</div>
	</div>
<!-- 	<div class="search_only" style="position:relative;z-index:1;"> -->
	<div>
		<div class="conBox">
			<div class="pg_location" style="top:2% !important;"><a>Go home</a> <span>></span> OPEN API 검색</div>
			<div id="content">
				<div class="box_searchred">
					<select id="searchCondition">
						<option value="">API이름 + API설명</option>
						<c:forEach var="searchList" items="${searchList}"  varStatus="idx">
							<option value="${searchList.comnCd}">${searchList.cdNm}</option>
						</c:forEach>
					</select> <span><input type="text" placeholder="검색어를 입력하세요" id="searchKeyword" name="searchKeyword" onkeyup="CheckStrLength(100,'searchKeyword')" onKeyPress="if (event.keyCode==13){fnKeywordSearch()};"/>
					<button onclick="fnKeywordSearch()">검색</button></span>
				</div>
				<div class="tab_board02">
					<div  class="btn_l swiper-button-prev">◀</div>
					<div class="list_navi swiper-container">
						<div class="swiper-wrapper">
							<c:choose>
								<c:when test="${empty sysId }">
									<div class="swiper-slide on" ><a href="<c:url value="/api/search/apiSearch.do"/>">전체</a></div>
								</c:when>
								<c:otherwise>
									<div class="swiper-slide" ><a href="<c:url value="/api/search/apiSearch.do"/>">전체</a></div>
								</c:otherwise>				
							</c:choose>
							<c:if test="${not empty serviceList }">
								<c:forEach var="serviceList" items="${serviceList}"  varStatus="idx">
									<div class="swiper-slide" svscd="${serviceList.sysId }" class="<c:if test="${sysId == serviceList.sysId}">on</c:if>"><a href="javascript:;" onclick="fnService(this)">${serviceList.sysNm}</a></div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					<div class="btn_R swiper-button-next">▶</div>
				</div>

				<div class="txt_apiSearch" style="line-height: initial;">
					<strong>검색결과</strong>
					<div id="serachResult">
					</div>
				</div>
			
				<ul class="tb_search"></ul>
			
				<div class="wrap_paging" id="paging"></div>
		    </div>
		</div>
	</div>
</div>
</t:layout>