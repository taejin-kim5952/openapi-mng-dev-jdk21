/** */
var importT = '';
var pageI  = 1;

// 팝업 오픈
function preOpenPopup(iType){
	$("#popupSchText").val("");
	
	if(iType == 1){
		$(".pop_import_api").parent().find("div").eq(0).children("span").text("API 불러오기");
	} else {
		$(".pop_import_api").parent().find("div").eq(0).children("span").text("템플릿 불러오기");
	}
	
	importT = iType;
	openPopup();
}

// 검색어
function goImportSearch(){
	pageI = 1;
	openPopup();
}

//페이징 조회
function pageGo(pageNo){
	pageI = pageNo;
	openPopup();
}

function openPopup(){
	var html = '';
	// param setting
    var param 		= new Object();
    param.schText	= $('#popupSchText').val();
    param.pageIndex = pageI;
    param.importType= importT;
    
    // console.log('openPopup param ' , param);
    
	$.ajax({
		url    : importUrl, 
		type   : 'POST',
		data   : param,
	 	async  : false,
	 	cache  : false,
		success: function(data){
			
			// console.log("성공", data);
			
			setPopupListData(data);
			
		},
		error:function(request,status,error){
			// console.log("code:"+request.status+"\n"+"error:"+error);
			$(".alert_txt").html("요청한 작업 수행중 에러가 발생했습니다.<br>잠시후 다시 시도해 주시기 바랍니다.");
			$(".pop_alert").dialog('open');
	    }
	});
}

function setPopupListData(data){
	
	var html = '';
	for(var i=0 ; i < data.list.length ; i++){
		var info = data.list[i];
		html +='<li>                                                                                              ';
		html +='	<div>                                                                                         ';
		html +='		<div class="api_info">                                                                    ';
		html +='			<span>'+info.sysIdNm+'</span>                                                                     ';
		html +='			<span class="api_info-writer">수정자 : '+info.amdrNm+'</span>                                   ';
		html +='			<span class="api_info-version">버전 '+info.ver+'</span>                                       ';
		html +='		</div>                                                                                    ';
		html +='		<div class="api_tit popup_api_tit" onClick="javascript:yamlEditorOpen(\''+info.apiSpcNo+'\');" style="cursor : pointer ;" title="'+info.apiNm+'">'+info.apiNm+'</div> ';
		html +='		<button type="button" class="btn-lg btn_selected" title="선택" onClick="javascript:setApi(\''+info.apiSpcNo+'\')" ><span>선택</span></button> ';
		html +='	</div>                                                                                        ';
		html +='</li>                                                                                             ';
	}
	
	//console.log(html);
	
	$('#popupUl').html(html);
	
	drawPaging('popupPaging' ,data.paginationInfo.currentPageNo ,data.paginationInfo.firstPageNoOnPageList ,data.paginationInfo.totalPageCount,data.paginationInfo.lastPageNoOnPageList  ,'pageGo');
	
	$('.pop_import_api').dialog('open');
}

function setApi(apiSpcNo){
	$(".alert_txt").html("선택을 누르시면 API가 생성됩니다. </br>진행하시겠습니까?");
	$('.btn_popup_close2').hide();
	$('.btn_popup_save').show();
	$('.btn_popup_cancel').show();
	$(".pop_alert").dialog('open');
	setImportApiSpcNo=apiSpcNo;
	if(importT=='1'){
		$('#rfrnApiSpcNo').val(setImportApiSpcNo);
	}else{
		$('#rfrnTmpltNo').val(setImportApiSpcNo);
	}
}

//버전업 실행
var verProgressFlag = true;
function savApiVerProgress2(){
	// console.log('verProgressFlag 1 verProgressFlag',verProgressFlag);
	if(!verProgressFlag){
		return;
	}
	
	// console.log('verProgressFlag 2 verProgressFlag',verProgressFlag);
	
	var param 		= new Object();
	param.apiSpcNo	= setImportApiSpcNo;
	param.ver		= "";
	param.verDesc	= "";
	
	verProgressFlag = false;
	
	$.ajax({
		// url    : importUrlSelect,
		url    : importYamlSelect,
		type   : 'POST',
		data   : param,
	 	async  : false,
	 	cache  : false,
		success: function(data){
			// console.log("성공", data);
			$("#importYamlSbst").val(data.yamlInfo.yamlSbst);
			$("#importYn").val("Y");
			$("#apiImportForm").submit();
		},
		error:function(request,status,error){
			// console.log("code:"+request.status+"\n"+"error:"+error);
			$(".alert_txt").html("요청한 작업 수행중 에러가 발생했습니다.<br>잠시후 다시 시도해 주시기 바랍니다.");
			$(".pop_alert").dialog('open');
			verProgressFlag = true;
	    }
	});
}

// 외부파일 오픈
function openPopupEtc(){
	// 값 초기화
	$('input:radio[name="r01"][value="WSDL"]').click();
	// console.log('openPopupEtc');
	$('.pop_import_extfile').dialog('open');
}

// 외부파일 불러오기 체크
function selYamlUrlDataCheck(){
	if($("input[type=radio][name=r01]:checked").val() == 'APIDOC'){
		if($("#fileUploadView").children().length == 0 ) {
			$(".alert_txt").html("첨부파일을 선택하십시오.");
			$(".pop_alert").dialog('open');
			return false;
		}
	} else if($("input[type=radio][name=r01]:checked").val() == 'JSON' || $("input[type=radio][name=r01]:checked").val() == 'YAML'){
		if($("#yamlStrFile").val() == "") {
			$(".alert_txt").html("내용을 입력하십시오.");
			$(".pop_alert").dialog('open');
			return false;
		}
	} else{
		if(!$("input[type=radio][name=r01]:checked").val()){
			$(".alert_txt").html("파일을 선택하십시오.");
			$(".pop_alert").dialog('open');
			return false;
		}
		if($('#urlPath').val()==''){
			$(".alert_txt").html("주소를 입력하십시오.");
			$(".pop_alert").dialog('open');
			return false;
		}
	}
	
	return true;
}

// 외부파일 불러오기
function selYamlUrlData(){

	if(!selYamlUrlDataCheck()){
		return;
	}
	
	var urlType= $("input[type=radio][name=r01]:checked").val();
	if(urlType == "JSON"){
		// json 일때
		try{
			var importJson = JSON.parse($("#yamlStrFile").val());
			SwaggerParser.parse(importJson).then(function(api) {
				$("#importYamlSbst").val($("#yamlStrFile").val());
				$("#importYn").val("Y");
				$("#apiImportForm").submit();
			}).catch(function(err) {
				$(".alert_txt").html("외부파일 불러오기에 실패했습니다.");
				$(".pop_alert").dialog('open');
				return false;
			});
		}catch(err){
			$(".alert_txt").html("외부파일 불러오기에 실패했습니다.");
			$(".pop_alert").dialog('open');
			return false;
		}
		
	} else if(urlType == "YAML"){
		// yaml 일때
		try{
			var importJson = JSON.parse(JSON.stringify(YAML.parse($("#yamlStrFile").val())));
			SwaggerParser.parse(importJson).then(function(api) {
				$("#importYamlSbst").val($("#yamlStrFile").val());
				$("#importYn").val("Y");
				$("#apiImportForm").submit();
			}).catch(function(err) {
				$(".alert_txt").html("외부파일 불러오기에 실패했습니다.");
				$(".pop_alert").dialog('open');
				return false;
			});
		}catch(err){
			$(".alert_txt").html("외부파일 불러오기에 실패했습니다.");
			$(".pop_alert").dialog('open');
			return false;
		}
	} else if(urlType == "WSDL"){
		// wsdl 일때
		if(!selYamlUrlDataCheck()){
			return;
		}
		var param = {};
		param.urlType 	= urlType;
		param.urlPath 	= $('#urlPath').val();
		$.ajax({
			url    : importUrltoYml, 
			type   : 'POST',
			data   : param,
		 	async  : false,
		 	cache  : false,
			success: function(data){
				// console.log("data", data);
				if(data.hashMap.successYn == 'Y'){
					$("#importYamlSbst").val(data.hashMap.yamlData);
					$("#importYn").val("Y");
					$("#apiImportForm").submit();
				}else{
					$(".alert_txt").html("외부파일 불러오기에 실패했습니다.");
					$(".pop_alert").dialog('open');
				}
				
				$('#urlPath').val('');
				$(".pop_import_extfile").dialog('close');
			},
			error:function(request,status,error){
				// console.log("code:"+request.status+"\n"+"error:"+error);
				$(".alert_txt").html("잘못된 주소이거나 파일을 가져올 수 없습니다.<br>다시 확인하시고 호출 하십시오.");
				$(".pop_alert").dialog('open');
		    }
		});
	} else if(urlType == 'APIDOC'){
		addFile.fileUpload();
		return;
	}
}