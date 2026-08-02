<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<t:layout type="default" title="KT Open API - FAQ">
<script type="text/javascript">
$(document).ready(function(){
	//category 데이터 조회
	cateData();
	//top5 목록 조회
	toplist.fnTopList();
	//faq 전체 목록 조회
	searchList.tabFn('1');
});

var selTab = '';

var toplist={
	fnTopList:function(){
		var obj = new Object();
		$.ajax({
			url: '<c:url value="/faq/mvfaqTopListAjax.do"/>', 
			type: 'POST',
			data :obj,
			success: function(data){
				
				var dhtml = "";
				if(data != null  && data.toplist.length != 0){
				$.each(data.toplist, function(index, topItem) {
					
					dhtml+=	' <li> ';
					dhtml+=	' <div id="div_'+index+'"> ';
					if(index == 0){
						dhtml+=	' <a class="active acco_toggle" href="javascript:;" title="'+topItem.faqCtgryCd+'" id="tog_'+index+' "> ';
					}else if(index > 0){
						dhtml+=	' <a class="acco_toggle" href="javascript:;" title="'+topItem.faqCtgryCd+'" id="tog_'+index+' "> ';
					}
					
					dhtml+=	' <span class="faq_tit"> '+topItem.faqCtgryCdNm+' </span> ';
					dhtml+=	' <span class="faq_cont"> '+topItem.title+' </span> ';
					
					dhtml+=	' <span class=""><input id="faq_'+index+'"  name="faqId" type="hidden" value="'+topItem.faqId+'"></span> ';
					
					dhtml+=  ' </a> ';
					dhtml+=  ' </div> ';
					
					dhtml+=  ' <div class="hidden_div"> ';
					dhtml+=  ' <div class="faq_answer"> ';
					dhtml+=  ' <div> '+topItem.sbst+'</div>';
						dhtml+=  ' </div> ';
						dhtml+=  ' </div> ';
						dhtml+=  ' </li> ';
						
					}); //each끝
				}
				$('#fTop').html(dhtml);
				setTogle();
				
			},
			error:function(request,status,error){
				//--@@console.log("code:"+request.status+"\n"+"error:"+error);
			}
		});
		
	}	
		
}

function setTogle(){
	$(".accordion > li a.acco_toggle").on("click", function(e){
		if($(this).parent().has("ul")) {
		  e.preventDefault();
		}
		
		// one open script
		if(!$(this).hasClass("active")) {
			var strId = ''; 
			strId = $(this).attr('id');
			strId = strId.replace('tog_',"");
			var dij ='';
			dij = 'div_'+ strId;
			var hparam = '';
			$('#'+dij).find('input').each(function(idx){
				hparam = $(this).val();
			});	
			upHit(hparam);
			
	      // hide any open menus and remove all other classes
	      $(this).parents('.accordion').find('.hidden_div').slideUp(350);
	      $(this).parents('.accordion').find('a').removeClass("active");
	      
	      // open our new menu and add the open class
	      $(this).parent().next().slideDown(350);
	      $(this).addClass("active");
	    }

	    else if($(this).hasClass("active")) {
	      $(this).removeClass("active");
	      $(this).parent().next().slideUp(350);
	    }
	});
}

function totalTogle(){
	$(".accordion > dd a.acco_toggle").on("click", function(e){
		if($(this).parent().has("ul")) {
		  e.preventDefault();
		}
		
		// one open script
		if(!$(this).hasClass("active")) {
			var strId = ''; 
			strId = $(this).attr('id');
			strId = strId.replace('totog_',"");
			var dij ='';
			dij = 'divto_'+ strId;
			var hparam = '';
			$('#'+dij).find('input').each(function(idx){
				hparam = $(this).val();
			});	
			upHit(hparam);
			
	      // hide any open menus and remove all other classes
	      $(this).parents('.accordion').find('.hidden_div').slideUp(350);
	      $(this).parents('.accordion').find('a').removeClass("active");
	      
	      // open our new menu and add the open class
	      $(this).parent().next().slideDown(350);
	      $(this).addClass("active");
	    }

	    else if($(this).hasClass("active")) {
	      $(this).removeClass("active");
	      $(this).parent().next().slideUp(350);
	    }
	});
}

var cateStrs = [ '' , 'FAQCAT1010', 'FAQCAT1020', 'FAQCAT1030', 'FAQCAT1040', 'FAQCAT1050','FAQCAT1060'];
var searchList={
	fnFaqSearList:function(pageIndex){ // 목록 조회
		var params = new Object();
		
		params.searchKeyword=$('#searchKeyword').val();
		params.pageIndex = pageIndex;
		//var commVal = '';
		params.faqCtgryCd = cateStrs[selTab-1];
		//console.log("params.faqCtgryCd:"+params.faqCtgryCd);
	    
		$.ajax({
	        type: "POST",
	        url: '<c:url value="/faq/mvfaqListAjax.do"/>',
	        data: params,
	        success: function (data) {
	        	searchList.fnFaqDrawList(data);	
	        },
			error:function(request,status,error){
				//--@@console.log("code:"+request.status+"\n"+"error:"+error);
			}
	  });
	},
	fnFaqDrawList:function(data){ //리스트 보기 화면 그리기
		var faqhtml = "";
	
		if(data != null  && data.faqlist.length != 0){
			
			$.each(data.faqlist, function(index, faqItem) {
             				 			
				if(index == 0){

					faqhtml += ' <dt> ';
					faqhtml += ' <span class="faq_num">NO</span> ';
					faqhtml += ' <span class="faq_category">카테고리</span> ';
					faqhtml += ' <span class="faq_subject">제목</span> ';
					faqhtml += ' </dt> ';
					
				}
				
				faqhtml += ' <dd> ';
				faqhtml += ' <div id="divto_'+index+'"> ';
				faqhtml += ' <a class="acco_toggle" href="javascript:;" title="'+faqItem.faqCtgryCd+'" id="totog_'+index+' "> ';
				faqhtml += ' <span class="faq_num">'+faqItem.rownum+' </span> ';
				faqhtml += ' <span class="faq_category"> '+faqItem.faqCtgryCdNm+'</span>';
				faqhtml += ' <span class="faq_subject"> '+faqItem.title+'</span>';
				
				faqhtml+=	' <span class=""><input id="faqt_'+index+'"  name="faqId" type="hidden" value="'+faqItem.faqId+'"></span> ';
				
				
				faqhtml += ' </a> ';
				faqhtml += ' </div> ';
				faqhtml += ' <div class="hidden_div"> ';
				faqhtml += ' <div class="faq_answer">  ';
				faqhtml += ' <div> '+faqItem.sbst+' </div> ';
				faqhtml += ' </div> ';
				faqhtml += ' </dd> ';
			}); 
			
		}else{
			faqhtml += ' <dt> ';
			faqhtml += ' <span class="faq_num">NO</span> ';
			faqhtml += ' <span class="faq_category">카테고리</span> ';
			faqhtml += ' <span class="faq_subject">제목</span> ';
			faqhtml += ' </dt> ';
        	faqhtml += ' <dd> ';
			faqhtml += ' <div> ';
			faqhtml += ' <a class="acco_toggle" href="javascript:;" title="" style="text-align: center;"> ';
			faqhtml += ' <span class=" "> FAQ 데이터가 존재하지 않습니다. </span>';
			//faqhtml += '        <span class="faq_num"></span> ';
		    //faqhtml += '        <span class="faq_category"></span> ';
		    //faqhtml += '        <span class="faq_subject"></span> ';
		    
		    faqhtml += '    </a> ';
		    faqhtml += ' </div> ';
		    faqhtml += ' <div class="hidden_div"> ';
		    faqhtml += ' <div class="faq_answer"> ';
		    faqhtml += ' <div></div> ';
		    faqhtml += '    </div> ';
		    faqhtml += ' </div> ';
		    faqhtml += ' </dd> ';
			
		}
		
		$('#tab'+selTab+' > .accordion').html(faqhtml);
		totalTogle();
		
    	//페이징 공통 처리
    	drawPaging('faq'+selTab+'Paging' ,data.paginationInfo.currentPageNo ,data.paginationInfo.firstPageNoOnPageList ,data.paginationInfo.totalPageCount,data.paginationInfo.lastPageNoOnPageList  , 'searchList.pageGo');
	},
	pageGo:function(pageIndex){//공연목록 페이징 조회
		searchList.fnFaqSearList(pageIndex);
	},
	tabFn:function(tabCode){//tab active 설정
		
		$(".tab_list > li").each(function(){
			$(this).removeClass('current');
		});
		
		$("[id^='tab']").removeClass('current');
		
		$("#tab"+tabCode).addClass('current');
		$("#tabId"+tabCode).addClass('current');
		
		selTab = tabCode;
		
		searchList.fnFaqSearList(1, selTab);
		
	}
	
}

var pageReload = function (){
	location.reload();
}

var cateData = function (){
	
	var params = new Object();
	var catehtml = "";
	
	$.ajax({
    type: "POST",
    url: '<c:url value="/faq/faqCateAjax.do"/>',
    data: params,
    success: function (data) {
    	if(data != null  && data.cmnCd.length != 0){
    		
    		catehtml += '<li id="tabId1" data-tab="tab1" class="current" value=""><a href="#tab1" onClick="javascript:searchList.tabFn('+1+');"><span>전체</span></a></li>';
    	    
    		$.each(data.cmnCd, function(countNo, cmnItem) {
                     countNo = countNo+2;
     				catehtml += '<li id="tabId'+countNo+'" data-tab="tab'+countNo+'" value=""><a href="#tab'+countNo+'" onClick="javascript:searchList.tabFn('+countNo+');"><span>'+cmnItem.cdNm+'</span></a></li>';
    			
    		});
    	}
    	$('.tab_list').html(catehtml);
    },
    error:function(request,status,error){
      //--@@console.log("code:"+request.status+"\n"+"error:"+error);
    }
  });
}

//조회수 증가
function upHit(hparam){
	var params = new Object();
	params.faqId = hparam;
	$.ajax({
        type: "POST",
        url: '<c:url value="/faq/upRCntAjax.do"/>',
        data: params,
        success: function (data) {
	        //--@@console.log(data.msg);
        },
        error:function(request,status,error){
	        //--@@console.log("code:"+request.status+"\n"+"error:"+error);
        }
    });
}
</script>	
<div id="container">
	<div class="sVisual sv_community">
		<div>
			<h2>FAQ</h2>
			<p>API에 대해 자주하는 질문과 답변입니다</p>
		</div>
	</div>
	<div class="contents">
		<div class="conBox">
			<div class="pg_location">
				<a>Go home</a> <span></span> 커뮤니티 <span></span>
				FAQ
			</div>

			<div id="content">
				<!-- comm_wrap -->
				<div class="comm_wrap">
					<div id="content">
						<!-- faq_wrap -->
						<div class="faq_wrap">
							<!-- FAQ board start -->
							<section>
								<div class="faq_form">
									<h4 class="brd_title">자주하는 질문 TOP 5</h4>
									<ul class="accordion" id="fTop">
									    <li>
                                                <div>
                                                    <a class="active acco_toggle" href="javascript:;" title="인증 API">
                                                        <span class="faq_tit"></span>
                                                        <span class="faq_cont"></span>
                                                    </a>
                                                </div>

                                                <!-- 2depth Content -->
                                                <div class="hidden_div">
                                                    <div class="faq_answer">
                                                        <div></div>
                                                    </div>
                                                </div>
                                            </li>

                                            <li>
                                                <div>
                                                    <a class="acco_toggle" href="javascript:;" title="인증 API">
                                                        <span class="faq_tit"></span>
                                                        <span class="faq_cont"></span>
                                                    </a>
                                                </div>

                                                <!-- 2depth Content -->
                                                <div class="hidden_div">
                                                    <div class="faq_answer">
                                                        <div></div>
                                                    </div>
                                                </div>
                                            </li>

                                            <li>
                                                <div>
                                                    <a class="acco_toggle" href="javascript:;" title="인증 API">
                                                        <span class="faq_tit"></span>
                                                        <span class="faq_cont"></span>
                                                    </a>
                                                </div>

                                                <!-- 2depth Content -->
                                                <div class="hidden_div">
                                                    <div class="faq_answer">
                                                        <div></div>
                                                    </div>
                                                </div>
                                            </li>

                                            <li>
                                                <div>
                                                    <a class="acco_toggle" href="javascript:;" title="인증 API">
                                                        <span class="faq_tit"></span>
                                                        <span class="faq_cont"></span>
                                                    </a>
                                                </div>

                                                <!-- 2depth Content -->
                                                <div class="hidden_div">
                                                    <div class="faq_answer">
                                                        <div></div>
                                                    </div>
                                                </div>
                                            </li>

                                            <li>
                                                <div>
                                                    <a class="acco_toggle" href="javascript:;" title="인증 API">
                                                        <span class="faq_tit"></span>
                                                        <span class="faq_cont"></span>
                                                    </a>
                                                </div>

                                                <!-- 2depth Content -->
                                                <div class="hidden_div">
                                                    <div class="faq_answer">
                                                        <div></div>
                                                    </div>
                                                </div>
                                            </li>
									</ul>
								</div>
							</section> 

							<!-- search_wrap -->
							<section>
								<div class="search_wrap">
									<p>Search</p>
									<div class="search_bar">
										<div>
											<input type="text" id="searchKeyword" name="searchKeyword" placeholder="검색어를 입력해주세요." onKeyPress="if (event.keyCode==13){searchList.fnFaqSearList(1)};">
										</div>
										<button type="button" class="btn_search" onclick="searchList.fnFaqSearList(1);">
											<span>검색</span>
										</button>
									</div>
									<button type="button" onclick="pageReload();" class="btn_refresh">
										<span>새로고침</span>
									</button>
								</div>
							</section>
							<!-- // search_wrap -->

							<!-- tab_list -->
							<section>
								<div class="tab_faq">
									<ul class="tab_list">
									<li id="tabId1" data-tab="tab1" class="current" value=""><a href="#tab1" onClick="javascript:searchList.tabFn('1');"><span></span></a></li>
									<li id="tabId2" data-tab="tab2" value=""><a href="#tab2" onClick="javascript:searchList.tabFn('2');"><span></span></a></li>
									<li id="tabId3" data-tab="tab3" value=""><a href="#tab3" onClick="javascript:searchList.tabFn('3');"><span></span></a></li>
									<li id="tabId4" data-tab="tab4" value=""><a href="#tab4" onClick="javascript:searchList.tabFn('4');"><span></span></a></li>
									<li id="tabId5" data-tab="tab5" value=""><a href="#tab5" onClick="javascript:searchList.tabFn('5');"><span></span></a></li>
									<li id="tabId6" data-tab="tab6" value=""><a href="#tab6" onClick="javascript:searchList.tabFn('6');"><span></span></a></li>
									<li id="tabId7" data-tab="tab7" value=""><a href="#tab7" onClick="javascript:searchList.tabFn('7');"><span></span></a></li>
									</ul>
									 
									<!-- tab1 -->
                                            <div id="tab1" class="tabcontent current">
                                                <h6>전체 Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                    
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq1Paging">
                                                    
                                                </div>
                                            </div>
                                            <!-- // tab1 -->

                                            <!-- tab2 -->
                                            <div id="tab2" class="tabcontent">
                                                <h6>인증 API Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>

                                                    
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq2Paging">
                                                   
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab2 -->

                                            <!-- tab3 -->
                                            <div id="tab3" class="tabcontent">
                                                <h6>메세징 API Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                   
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq3Paging">
                                                  
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab3 -->

                                            <!-- tab4 -->
                                            <div id="tab4" class="tabcontent">
                                                <h6>부가서비스 API Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                 
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq4Paging">
                                                   
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab4 -->

                                            <!-- tab5 -->
                                            <div id="tab5" class="tabcontent">
                                                <h6>과금 API Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                    
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq5Paging">
                                                   
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab5 -->

                                            <!-- tab6 -->
                                            <div id="tab6" class="tabcontent">
                                                <h6>결제 API Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                    
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq6Paging">
                                                   
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab6 -->

                                            <!-- tab7 -->
                                            <div id="tab7" class="tabcontent">
                                                <h6>기타 Tab</h6>
                                                <!-- accordion list start one Set -->
                                                <dl class="accordion">
                                                    <dt>
                                                        <span class="faq_num">NO</span>
                                                        <span class="faq_category">카테고리</span>
                                                        <span class="faq_subject">제목</span>
                                                    </dt>
                                                    
                                                    
                                                </dl>
                                                <!-- // accordion list start one Set -->

                                                <div class="paging" id="faq7Paging">
                                                   
                                                </div>
                                                <!-- // accordion list start one Set -->
                                            </div>
                                            <!-- // tab7 -->
                                            
								</div>
							</section>
							<!-- // tab_list -->
						</div>
						<!-- // FAQ board End -->
					</div>
					<!-- // faq_wrap -->
				</div>

			</div>
			<!-- // comm_wrap -->
		</div>
	</div>
</div>
</t:layout>