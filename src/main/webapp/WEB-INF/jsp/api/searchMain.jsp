<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

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

	var editorTomcatUse  	 = '<spring:eval expression="@environment.getProperty('editor.tomcat.use')" />' 			// 에디터 톰캣 사용여부
	var editorTomcatHostUse  = '<spring:eval expression="@environment.getProperty('editor.tomcathost.url')" />' 		// 에디터 톰캣 사용여부
	var editorHostUrl    	 = '<spring:eval expression="@environment.getProperty('editor.host.url')" />'      			// 에디터 호스트 url
	var editorDefultHost 	 = '<spring:eval expression="@environment.getProperty('editor.defult.host')" />'			// 에디터 기본정보 디폴트 호스트

	var sysId 		= '${param.sysId}' != '' ? '${param.sysId}' : '';

	var pageIndex 	= 1;
	
	$(function(){
		
		selList('');	//조회
		
		// 검색
		$('.btn_search').on('click' , function(){
			pageIndex 	= 1
			selList('');
		});
		
		// 새로고침
		$('.btn_refresh').on('click' , function(){
			pageIndex 	= 1
			$('.myValue').html("카테고리 검색");
			$('#ctgryNm').val('');
			$('#schText').val('');
			$('.innertab_list > li').removeClass('innercurrent');
			$('.innertab_list > li').eq(0).addClass('innercurrent');
			$('.innertabcontent').removeClass('innercurrent');
			$('#inner_tab0').addClass('innercurrent');
			
			selList('');
		});
		
        $("#schText").keydown(function(key) {
        	if (key.keyCode == 13) {
        		pageIndex 	= 1
    			selList('');
        	}
     	});

	});
	
	function moreList(){
		pageIndex++;
		selMainList(sysId);
	}
	
	
	// 카테고리 조회
	function selCateList( sysId , ctgryNm ){
		// console.log('selCateList sysId=',sysId);
		
		$('#ctgryNm').val(ctgryNm);
		pageIndex 	= 1;
		selMainList(sysId);
	}
	
	//페이징 조회
	function pageGo(pageNo){
		pageIndex = pageNo;
		selMainList(sysId);
	}
	
	function selList(selSysId){
		sysId		= selSysId;
		pageIndex 	= 1;
		$('#ctgryNm').val('');
		
		selMainList(sysId);
	}
	
	// 작성 상태 탭 클릭시에 리스트 조회
	function selMainList(selSysId){
		
		$(".tab_c7 > li").each(function(){
			$(this).removeClass('current');
		});
		
		sysId = selSysId;
		
		if(selSysId==""){
			$("#sysIdtotal").addClass('current');
		}
		
		if(selSysId==""){
			$("#sysIdtotal").addClass('current');
		}else{
			$("#"+selSysId).addClass('current');
		}
		
		// param setting
        var param 		= new Object();
        param.schText	= $('#schText').val();
        param.sysId   	= sysId;
        param.ctgryNm	= $('#ctgryNm').val();
        param.pageIndex = pageIndex;
        
		$.ajax({
			url    : '<c:url value="/api/search/selMainListAjax.do"/>', 
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
	};
	
	function setTabData(data){
		
		$('.search_result > span').html(data.totalCnt+'건');
		
		var html = ''; // '<h6>전체 Tab</h6>'
		
		if( data.info != null){
			if( data.info.length > 0 ){
				for(var i=0 ; i < data.info.length ; i++){
					var mainInfo = data.info[i];
					html += '<dl class="searching_list">                                                                                                  ';
					html += '	<dt>                                                                                                                      ';
					html += '		<h4><span>'+mainInfo.sysIdNm+'</span></h4>                                                                                            ';
					html += '	</dt>                                                                                                                     ';
					html += '	<dd>                                                                                                                      ';
					html += '	<div>                                                                                                                     ';
					html += '		<p class="lst_title">                                                                                                 ';
					html += '		<a href="javascript:;" title="" onClick="mvApiInfo(\''+mainInfo.apiNo+'\' , \''+mainInfo.apiSpcNo+'\');">                                                                                      ';
					//html += '		<a href="javascript:;" title="" onClick="yamlEditorOpen(\''+mainInfo.apiSpcNo+'\');">                                                                                      ';
					html += '		<!-- api-get / api-post / api-put / api-delete / api-head / api-patch / api-options -->                               ';
					var methodCd = mainInfo.methodCd;
					var methodCdClass = 'api-post';
					if(methodCd == "MTHTYP1010"){
						methodCdClass = 'api-get';
					}else if(methodCd == "MTHTYP1020"){
						methodCdClass = 'api-put';
					}else if(methodCd == "MTHTYP1030"){
						methodCdClass = 'api-delete';
					}else if(methodCd == "MTHTYP1040"){
						methodCdClass = 'api-patch';
					}else if(methodCd == "MTHTYP1050"){
						methodCdClass = 'api-head';
					}else if(methodCd == "MTHTYP1060"){
						methodCdClass = 'api-options';
					}
					html += '		<span class="'+methodCdClass+'">'+mainInfo.methodNm+'</span>' + mainInfo.apiNm+ '</a>';
					html += '		<span class="lst_writer"><em>' + mainInfo.amdDtStr + '</em></span>                                                             ';
					html += '		<span class="lst_version">버전 '+ mainInfo.ver + '</span>                                                                            ';
					html += '		</p>                                                                                                                 ';
					html += '		<p class="lst_cont">';
					html += mainInfo.apiDefDesc;
					html += '		</p>                                                                                                                  ';
					html += '	</div>                                                                                                                    ';
					html += '	</dd>                                                                                                                     ';
					html += '</dl>                                                                                                                        ';
				}
				
				if(data.paginationInfo.totalRecordCount > data.paginationInfo.currentPageNo * data.paginationInfo.pageSize ){
					html += '<button type="button" class="btn_moreList" onClick="moreList();"><span>더보기</span></button>';
				}
				
				if(pageIndex==1){
					$('#tab1').html(html);
				}else{
					$('.btn_moreList').remove();
					$('#tab1').append(html);
				}
				
			}else{
				
				html += '<dl class="searching_list nodata">';
				html += '	입력하신 검색어에 해당하는 결과가 없습니다.                                                                               ';
				html += '</dl>';
				
				$('#tab1').html(html);
				
			}
				
		}	// end : if( data.mainList != null){
			
		$('#tab1').addClass("current");
		
	}
	
	
	// yaml 에디터 관련 이벤트
	var winref
	function openOnceTomcat(url, target, options){
		if(winref) 
			winref.close();
		// open a blank "target" window
		// or get the reference to the existing "target" window
		winref = window.open('', target, options, false);
		winref.location.href = url;
		
		return winref;
	}
	function openOnce(url, target, options){
	   if(winref)
		   winref.close();
	   // open a blank "target" window
	   // or get the reference to the existing "target" window
	   winref = window.open('', target, options, false);
	   winref.location.href = url;
	   // winref.location.reload(true);
	   // winref.document.getElementById("onEditorBtn").click()
	   // if the "target" window was just opened, change its url
	   if(winref.location.href === 'about:blank'){
		   winref.location.href = url;
	   }
	   return winref;
	}
	
	var userId 		= '${ssUserVo.enCmbrId}' == '' ? 'nouser' : '${ssUserVo.enCmbrId}';
	var sessionKey 	= 'sessionkey';
	var interval;
	
	// yaml 테스트 버튼 (클릭시 팝업창으로 연결) 
	/**
	 * userId : 유저 ID
	 * import : yaml 저장 경로
	 * */
	function yamlEditorOpen(apiSpcNo,apiPath){
		
		if(userId=='nouser'){
			userwidth 	= 1000;
		}else{
			userwidth 	= (screen.width - 15);
		}
		
		userheight 	= (screen.height - 130);
		
		if(editorTomcatUse == 'true'){
			window.sessionStorage.setItem('sessionkey', sessionKey);
			window.sessionStorage.setItem('mbrid', userId);
			window.sessionStorage.setItem('apino', apiSpcNo);
			
			window.sessionStorage.setItem('new', false);
			window.sessionStorage.setItem('no-proxy', true);
			
			openOnceTomcat(editorTomcatHostUse, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,men ubar=no, status=no, toolbar=no');
		} else {
			openOnce(editorHostUrl+'mbrid='+userId+'&apino='+apiSpcNo+'&new=true&no-proxy=false&sessionkey='+sessionKey, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
		}
		
		//setTimeout(setEditorFocus(apiPath), 5000);
	}
	
	
	function setEditorFocus(apiPath){
		// console.log('apiPath===>',apiPath);
		try{
			winref.document.getElementById("goToApiPath").value=apiPath;
			winref.document.getElementById("goToApi").click();
		}catch(e){
			//--@@console.log(e);
			setTimeout(setEditorFocus(apiPath), 5000);
		}
	}
	
	// api 규격서 페이지로 이동
	function mvApiInfo(apiNo, apiSpcNo){
		$("#pApiNo").val(apiNo);
		$("#pApiSpcNo").val(apiSpcNo);
		$("#apiInfoForm").attr("action", "<c:url value='/api/info/mvInfoView.do'/>");
		$("#apiInfoForm").submit();
	}
	
</script>
<input type="text" id="ctgryNm" name="ctgryNm" value="" />
<form method="POST" action="" id="apiInfoForm">
<input type="hidden" name="apiNo"    id="pApiNo"   />
<input type="hidden" name="apiSpcNo" id="pApiSpcNo"/>
</form>
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
									    	<li data-tab2="inner_tab0" class="innercurrent"><a href="javascript:;" title="전체"><span>전체</span></a></li>
									    	<li data-tab2="inner_tab1" class=""><a href="javascript:;" title="SHUB"><span>SHUB</span></a></li>
									    	<li data-tab2="inner_tab2" class=""><a href="javascript:;" title="IoTMakers"><span>IoTMakers</span></a></li>
									    	<li data-tab2="inner_tab3" class=""><a href="javascript:;" title="ucloud biz"><span>ucloud biz</span></a></li>
									    	<li data-tab2="inner_tab4" class=""><a href="javascript:;" title="GiGA Genie"><span>GiGA Genie</span></a></li>
									    	<li data-tab2="inner_tab5" class=""><a href="javascript:;" title="olleh map biz"><span>olleh map biz</span></a></li>
									    	<li data-tab2="inner_tab6" class=""><a href="javascript:;" title="기타"><span>기타</span></a></li>
									    </ul>
									    <div id="inner_tab0" class="innertabcontent innercurrent">
										    <ul class="aList">
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
				                            			<li><a href="javascript:;" onClick="selCateList( '' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
									      		</c:forEach>
										    </ul>
									    </div>
									    <div id="inner_tab1" class="innertabcontent">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
				                            		<c:if test="${'SHUB' eq ctItem.sysId}" >
				                            			<c:set var="listCheck" value="Y" />
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    <div id="inner_tab2" class="innertabcontent ">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
				                            		<c:if test="${'IOTMAKERS' eq ctItem.sysId}" >
				                            			<c:set var="listCheck" value="Y" />
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    <div id="inner_tab3" class="innertabcontent ">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
				                            		<c:if test="${'UCLOUDBIZ' eq ctItem.sysId}" >
				                            			<c:set var="listCheck" value="Y" />
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    <div id="inner_tab4" class="innertabcontent ">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
										    		<c:set var="listCheck" value="Y" />
				                            		<c:if test="${'GIGAGENIE' eq ctItem.sysId}" >
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    <div id="inner_tab5" class="innertabcontent ">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
										    		<c:set var="listCheck" value="Y" />
				                            		<c:if test="${'OLLEHMAP' eq ctItem.sysId}" >
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    
									    <div id="inner_tab6" class="innertabcontent ">
										    <ul class="aList">
										    	<c:set var="listCheck" value="N" />
										    	<c:forEach var="ctItem" items="${cateList}" varStatus="status2">
										    		<c:set var="listCheck" value="Y" />
				                            		<c:if test="${'ETC' eq ctItem.sysId}" >
				                            			<li><a href="javascript:;" onClick="selCateList( '${ctItem.sysId}' , '${ctItem.ctgryNm}' );" title="카테고리명">${ctItem.ctgryNm}</a></li>
					                                </c:if>
									      		</c:forEach>
									      		<c:if test="${listCheck eq 'N'}" >
			                            			<h4>등록된 카테고리가 없습니다.</h4>
				                                </c:if>
										    </ul>
									    </div>
									    
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