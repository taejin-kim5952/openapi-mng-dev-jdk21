/**
 *  페이징 공통
 *  * divname  : 페이지 그릴 div 이름
 *  * currentPageNo : 현재 페이지 번호
	 * firstPageNoOnPageList : 페이지 리스트의 첫 페이지 번호
	 * totalPageCount : 페이지 개수
	 * lastPageNoOnPageList : 페이지 리스트의 마지막 페이지 번호
 */
function drawPaging(divname, currentPageNo, firstPageNoOnPageList , totalPageCount , lastPageNoOnPageList , func){
	
	
	var currentPageNo = parseInt(currentPageNo) ;
	var beforePageNo = parseInt(firstPageNoOnPageList);
	var afterPageNo 	= parseInt(lastPageNoOnPageList);
	var html='';
	
	//페이징 페이지 수가 1건일경우엔 굳이 화면에 그리지 않는다. 2019-05-28
	if(totalPageCount < 2){
		$('#'+divname).children().remove();
		return;
	}
	
	if(currentPageNo > 10){
		beforePageNo = currentPageNo -10;
	}else{
		beforePageNo = 1;
	}
	
	var bPNo = (currentPageNo !=1)?currentPageNo-1:currentPageNo;
	html='<span class="btn_first"> <a class="btn_first" href="javascript:'+func+'('+beforePageNo+')">go_first</a></span>  <span class="btn_prev"> <a class="arrow" href="javascript:'+func+'('+bPNo+')">go_prev</a></span>';
	html +='<ul>';
	var  i = 1;
	if(currentPageNo > 10){
		i = parseInt(currentPageNo/10) + '1';
		i = parseInt(i);
		
		if(parseInt(currentPageNo % 10) == 0 ){
			i -= 10;
		}
	}
	
	for(i ;  i <= totalPageCount ; i++){
		if(currentPageNo == i){
			html +='<li class="active" title="선택됨"><a href="javascript:'+func+'('+(i)+')">'+(i)+'</a></li>';
		}else{
			html +='<li><a href="javascript:'+func+'('+(i)+')">'+(i)+'</a></li>';
		}
		if(i % 10 == 0) break;
	}
	html +='</ul>';
	
	if(currentPageNo +10 <= totalPageCount){
		afterPageNo = currentPageNo +10;
	}else{
		afterPageNo = totalPageCount;
	}
	var aPNo = (currentPageNo != totalPageCount)?currentPageNo+1:currentPageNo;
	html +='<span class="btn_next"><a class="btn_next" href="javascript:'+func+'('+aPNo+')">go_next</a></span>  <span class="btn_last"><a  href="javascript:'+func+'('+afterPageNo+')">go_last</a></span>';
	
	
	$('#'+divname).html(html);
}
