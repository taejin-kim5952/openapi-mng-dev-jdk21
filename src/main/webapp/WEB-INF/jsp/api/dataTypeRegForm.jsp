<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<t:layout type="apiReg">
<!-- 
   	OPEN API version 1.0
  
   	Copyright ⓒ 2017 kt corp. All rights reserved.
   	
   	This is a proprietary software of kt corp, and you may not use this file except in 
   	compliance with license agreement with kt corp. Any redistribution or use of this 
   	software, with or without modification shall be strictly prohibited without prior written 
   	approval of kt corp, and the copyright notice above does not evidence any actual or 
	intended publication of such software. 
-->
<%-- //-- [tag:job-20200420][chg][for share head] --%>
<%@ include file="/WEB-INF/jsp/api/regFormShareHead.jsp" %>

<script type="text/javascript">
	var exampleOb    = new Object();
	var exampleArrayStr;
	$(document).ready(function(){
		
		// dataType Set
		dataTypeSet();
		
		// 좌측상단 타이틀 세팅
		$(".default_info").children("p").text(yamlOb.info.title);
		$(".default_info").children("p").attr("title",yamlOb.info.title);
		
		// dataInfo Set
		var datatypeNm ="${fn:escapeXml(param.apiDataTypeNm)}";
		if(datatypeNm != ""){
			dataInfoSet("${fn:escapeXml(param.apiDataTypeNm)}");
		}
	});
	var test = new Object();
	var sectionHtml = '';
	var dept = 0;
	var requiredArray = new Array();
	
	// 타입 선택
	function typeClick(data){
		//ojbect 경우에 dataBodyForm 사용
		if(data.value == "Object"){
			// 오브젝트 일경우 예제 삭제
			if($(data).parent().parent().parent().parent().find(".example").length > 0){
				$(data).parent().parent().parent().find("th").eq(1).html("");
				$(data).parent().parent().parent().find("td").eq(1).html("");
			}
			
			if($(".paramBodyInfoDiv").find(".noBodyForm").length > 0){
				$(".paramBodyInfoDiv").find(".paramBodyDataDiv").remove();
				$(".paramBodyInfoDiv").find("button").remove();
			}
			if($(".paramBodyInfoDiv").find(".paramBodyDataDiv").length == 0){
				sectionHtml = 	'<div class="div_draging paramBodyDataDiv bodyForm">'+
                    				'<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                    				'<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                    			'</div>'
				$(".paramBodyInfoDiv").append(sectionHtml);
				dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
			}
		}
		//array 경우에 dataForm 사용
		else if(data.value == "Array"){
			// 오브젝트 일경우 예제 추가
			if($(data).parent().parent().parent().find(".example").length == 0 && $(data).parent().parent().parent().find(".ofClass").length == 0){
				$(data).parent().parent().parent().find("th").eq(1).html('<div class="essential">예제</div>');
				$(data).parent().parent().parent().find("td").eq(1).html('<div class="example"><input type="text" name="typeExample" title="예제 입력"></div>');
			}
			
			if($(".paramBodyInfoDiv").find(".bodyForm").length > 0){
				$(".paramBodyInfoDiv").find(".paramBodyDataDiv").remove();
				$(".paramBodyInfoDiv").find("button").remove();
			}
			if($(".paramBodyInfoDiv").find(".paramBodyDataDiv").length == 0){
				$(".paramBodyInfoDiv").append($("#dataForm").html());
			}
		}else{
			// 오브젝트 일경우 예제 추가
			if($(data).parent().parent().parent().find(".example").length == 0 && $(data).parent().parent().parent().find(".ofClass").length == 0){
				$(data).parent().parent().parent().find("th").eq(1).html('<div class="essential">예제</div>');
				$(data).parent().parent().parent().find("td").eq(1).html('<div class="example"><input type="text" name="typeExample" title="예제 입력"></div>');
			}
			
			$(".paramBodyInfoDiv").find(".paramBodyDataDiv").remove();
			$(".paramBodyInfoDiv").find("button").remove();
		}
		dragDrop();
	}
	// 파라미터에서 타입 선택시에
	function paramTypeClick(data){
		if(data.value == "Object"){
			// 오브젝트 일경우 예제 삭제
			if($(data).parent().parent().parent().parent().find(".example").length > 0){
				$(data).parent().parent().parent().find("th").eq(1).remove();
				$(data).parent().parent().parent().find("td").eq(1).remove();
			}
			
			// 기존 object가 아니였을때 div 삭제					            
			if($(data).parent().parent().parent().next().length > 0){
				$(data).parent().parent().parent().next().remove();
			}
			
			var sectionHtml = '';
			if($(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").length == 0){	
				sectionHtml =	'<div class="div_draging ui-sortable">'+
					            	'<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElAdd(this)"><span>파라미터 추가</span></button>'+
						            '<button type="button" title="속성 추가" class="btn btn_sml btn_gray" onclick="objectElAdd(this)"><span>속성 추가</span></button>'+
				            	'</div>';
				$(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().append(sectionHtml);
				dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
			}
		} else {
			// 오브젝트 일경우 예제 추가
			if($(data).parent().parent().parent().find(".example").length == 0 && $(data).parent().parent().parent().find(".ofClass").length == 0){
				$(data).parent().parent().parent().append('<th scope="row"><div class="essential">예제</div></th>');
				$(data).parent().parent().parent().append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
			}
			
			// object속성추가 버튼 삭제
			$(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").remove();
			
			
			if($(data).parent().parent().parent().next().length == 0){
				if(data.value == "Array"){
					var arrayHtml = '';
					arrayHtml = 	'<tr>'+
										'<th scope="row">'+
											'<div class="essential ofClass">of</div>'+
										'</th>'+
										'<td><div>'+
										 	'<select class="w100" onclick="paramTypeClick(this);" name="name">'+
				                            	'<option value="">타입을 선택하여 주세요</option>'+
			                               		<c:forEach var="list" items="${dataTypeList}" varStatus="status">
			                               		'<option value="${list.cdNm}">${list.cdNm}</option>'+
			                               		</c:forEach>
				                          	'</select>'+
										'</div></td>'+
									'</tr>';
					$(data).parent().parent().parent().parent().append(arrayHtml);
				}
			} else {
				if(data.value != "Array"){
					for(var i = $(data).parent().parent().parent().parent().find("tr").length-1;i > 0;i--){
						if(i > $(data).parent().parent().parent().index()){
							$(data).parent().parent().parent().parent().find("tr").eq(i).remove();	
						}
					}
					
				}
			}
		}
		dragDrop();
	}
	// 오브젝트 선택시에 파마리터 추가 버튼
	function paramAdd(data){
		// 필수 체크박스 id와 label for에 유니크한 값 추가
		$("#dataBodyForm").find("input[name='required']").attr("id","required"+dept);
		$("#dataBodyForm").find("input[name='required']").next().attr("for","required"+dept);
		$(".paramBodyInfoDiv").find("button").last().before($("#dataBodyForm").html());
		dept = dept + 1;
	}
	// 오브젝트 선택시에 속성 추가 버튼
	function objectElAdd(data){
		// 필수 체크박스 id와 label for에 유니크한 값 추가
		$("#dataBodyForm").find("input[name='required']").attr("id","required"+dept);
		$("#dataBodyForm").find("input[name='required']").next().attr("for","required"+dept);
		$(data).parent().find("button").last().before($("#dataBodyForm").html());
		dept = dept + 1;
	}
	// 오브젝트 form에서 삭제 버튼 
	function paramDel(data){
		$(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().remove();
	}
	// 데이터 타입 저장
	
	function dataTypeSave(){
		//-- DATA TYPE{저장}
		if (fn_check_regform_action('dataTypeRegForm:dataTypeSave') == false) {
			return false;
		}
		
		if (!hasXSSAndMove(['typeName','typeExample','typeAccount'])) {
		    return false;
		}
		
		if($("#pApiDataTypeNm").val() != $("input[name='typeName']").val() && $("#pApiDataTypeNm").val() != ""){
			$("#popupConfirm").parent().find("div").eq(0).children("span").text("DATA TYPE");
			$("#popupConfirm").find('#alertTxt').text('DATA TYPE 이름은 수정할 수 없습니다.');
			$("#popupConfirm").dialog( "open" );
			
			return false;
			
		}

    <%-- //--[tag:adpt][add] --%>
    errCountReset();

    $(".err_tooltip").find("dd").remove();
    $(".err_count").find("em").text(errorNum);
    <%-- //--@@$(".red_txt").css("display", "none"); --%>

		if($("#pApiDataTypeNm").val() == ""){
			// 이름 중복 검사
			for (var key in yamlOb['definitions']) { //오브젝트 obj
		        if (yamlOb['definitions'].hasOwnProperty(key)) { 
		          if ($("input[name='typeName']").val() == key) {
		            var errCheck = errorText.indexOf("dataNmDuple");  
		            if (errCheck == -1) {
		              errorNum = errorNum + 1; $(".err_count").find("em").text(errorNum);
		              errorText.push("dataNmDuple");
		            }
		            break;
		          }
		        }
			}
		}

		/**** 데이터 검사 시작******///
		dataValidation();

		if(errorNum > 0){
			err_on();
			var offset = $("#container").offset();
      $('html, body').animate({scrollTop : offset.top}, 400);
			return false;
		} else {
			$('.err_tooltip').css("display", "none");
			$('.err_count').css("display", "none");
		}
		/**** 데이터 검사 끝 ******///
		
		// 타입이 object 일경우 담을 변수
		var dataOb = new Object();
		exampleOb  = new Object();
		var name = $("input[name='typeName']").val(); // 데이터 타입 명
		paramOb = {};
		paramOb[name] = {};
		
		// type이 Array 일 경우 저장
		if($("select[name='type']").val() == 'Array'){
			var emptyOb = new Object();
			
			dataOb[0] = $($(".paramBodyInfoDiv").children(".paramBodyDataDiv")[0]);
			
			typeArrayFn($(dataOb[0]), emptyOb);
			paramOb[name] = emptyOb;

			paramOb[name].example		= $("input[name='typeExample']").val();
		} 
		// type이 Object 일 경우 저장
		else if($("select[name='type']").val() == 'Object'){
			dataOb[0] = $($(".paramBodyInfoDiv").children(".paramBodyDataDiv")[0]);
			
			if(!jQuery.isEmptyObject(dataOb)){
				var emptyOb = new Object();
				typeObject(dataOb[0], emptyOb);
				paramOb[name] = emptyOb;
			} 

			paramOb[name].example		= "Ob_Small_Com_Del{"+$("input[name='typeName']").val()+":"+JSON.stringify(exampleOb)+"}Ob_Small_Com_Del"; // example 를 넣을때 yaml에 문자열변환하는 순간 '를 추가 하여서 문자열로 변환 후 '를 삭제하기 위해 임의의 문자 추가
		} else {
			paramOb[name].example		= $("input[name='typeExample']").val();
		}
		
		// 기본정보 추가
		paramOb[name].description 	= $("textarea[name='typeAccount']").val();
		paramOb[name].type 			= $("select[name='type']").val().toLowerCase();
		
		// definitions이 없을 경우 새로 초기화 후 값 추가 있을경우 기존 값과 합쳐서 추가
		if(yamlOb.hasOwnProperty("definitions") == true) {
			yamlOb['definitions'] = $.extend(yamlOb['definitions'], paramOb);
		} else {
			yamlOb['definitions'] = {};
			yamlOb['definitions'] = $.extend(yamlOb['definitions'], paramOb);	
		}

		var yamlStr = YAML.stringify(yamlOb);
		// 임의 문자열 및 ' 삭제
		yamlStr = yamlStr.replace(/\'Ob_Small_Com_Del/g,"");
		yamlStr = yamlStr.replace(/example: >-/g,"example:");
		yamlStr = yamlStr.replace(/Ob_Small_Com_Del\'/gi,"");
		yamlStr = yamlStr.replace(/Ob_Small_Com_Del/g,"");
		
		var param = {
			apiSpcNo: $("#pApiSpcNo").val(),  		// 무조건 존재
			yamlStr : yamlStr  // yaml 데이터 : 필수
		};

		$.ajax({
		 	url    : '<c:url value="/api/reg/savApiDataTypeRegAjax.do"/>', 
		 	type   : 'POST',
		 	data   : param,
		 	cache  : false,
		 	async  : false,
		 	success: function(data){
				
				var alert_option = {};
				
				if("1" == data.returnCode){
					dataTypeSet();
									 
					//레이어 메세지 적용
					$("#popupConfirm").parent().find("div").eq(0).children("span").text("DATA TYPE");
					$("#popupConfirm").find('#alertTxt').text('<spring:message code="api.req.save.msg" />');
					$("#popupConfirm").dialog( "open" );
					// yaml값 세팅
					$("#yamlSbst").val(yamlStr);
					
					isChange = false; // 페이지 이동 체크 여부 
				}else{
					alert_message('<, >, ", $ 등 사용할 수 없는 특수문자 <br>또는 스크립트 패턴이 포함되어 있습니다.', 'API', alert_option);
					delete yamlOb['definitions'][$("#typeName").val()]; //실패 시 해당 내용이 yaml에 쌓이지 않도록 삭제처리
				}
		    },
	 		error:function(request,status,error){
	 			err_message(status, error);
	    	}
		});
	}
	
    function typeObject(data, object){
    	object['properties'] = {};  
    	object['required']   = new Array();
    	for(var i=0;i < $(data[0]).children("section").length;i++){
			typeObjectTwo($(data[0]).children("section")[i], object['properties'], exampleOb, object['required']);	
		}
	}
	function typeObjectTwo(data, object, exOb, requriedArray){
		if($(data).find("select[name='type']").val() == 'Object'){
			object[$(data).find("input[name='name']").val()] 					= {};
			object[$(data).find("input[name='name']").val()]['type'] 			= $(data).find("select[name='type']").val().toLowerCase();
			object[$(data).find("input[name='name']").val()]['description'] 	= $(data).find("input[name='account']").val();
			object[$(data).find("input[name='name']").val()]['properties'] 		= {};
			
			if($(data).find("input[name='example']").val() != undefined){
				object[$(data).find("input[name='name']").val()]['x-example'] 	= $(data).find("input[name='example']").val();
			}
			if($(data).find("input[name='required']").is(":checked") == true){
				requriedArray.push($(data).find("input[name='name']").val());
			}
			exOb[$(data).find("input[name='name']").val()] = {};
			
			for(var i=0; i < $(data).children(".div_draging").children("section").length; i++){
				typeObjectTwo($(data).children(".div_draging").children("section")[i], object[$(data).find("input[name='name']").val()]['properties'], exOb[$(data).find("input[name='name']").val()], object[$(data).find("input[name='name']").val()]['required']);	
			}
			
		} else if($(data).find("select[name='type']").val() == 'Array'){
			exampleArrayStr = "";
			typeArrayFn($(data), object);
			object[$(data).find("input[name='name']").val()]['description'] 	= $(data).find("input[name='account']").val();
			if($(data).find("input[name='example']").val() != undefined){
				object[$(data).find("input[name='name']").val()]['x-example'] 	= $(data).find("input[name='example']").val();
			}
			if($(data).find("input[name='required']").is(":checked") == true){
				requriedArray.push($(data).find("input[name='name']").val());
			}
			exOb[$(data).find("input[name='name']").val()] 	= exampleArrayStr;	
		} else {
			object[$(data).find("input[name='name']").val()] 				= {};
			exOb[$(data).find("input[name='name']").val()]  				= $(data).find("input[name='example']").val();
			object[$(data).find("input[name='name']").val()]['type'] 			= $(data).find("select[name='type']").val().toLowerCase();
			object[$(data).find("input[name='name']").val()]['description'] 	= $(data).find("input[name='account']").val();
			
			if($(data).find("input[name='example']").val() != undefined){
				object[$(data).find("input[name='name']").val()]['x-example']	 	= $(data).find("input[name='example']").val();	
			}
			if($(data).find("input[name='required']").is(":checked") == true){
				requriedArray.push($(data).find("input[name='name']").val());
			}
		}
	}
	function typeArrayFn(data, object){
		if($(data).find("input[name='example']").val() != ""){
			exampleArrayStr = $(data).find("input[name='example']").val();
		}
		if(typeof $(data).find("input[name='name']").val() == "undefined"){
			object['items'] = {};
			typeArray = object['items'];
		} else {
			object[$(data).find("input[name='name']").val()] = {};	
			typeArray = object[$(data).find("input[name='name']").val()];
		}
		
		
		for(var i= 0;i < $(data).find("select").length; i++){
			if($($(data).find("select")[i]).val() == 'Array') {
				type 			= $($(data).find("select")[i]).val();
				example 		= $($($(data).find("select")[i]).parent().parent().parent().find("input")).val();
				typeArray 		= typeArrayMake(type, example, typeArray);
			} else if($($(data).find("select")[i]).val() == 'Object') {
				typeArray['type'] 			= $($(data).find("select")[i]).val().toLowerCase();
				/* typeArray['example'] 		= $($($(data).find("select")[i]).parent().parent().parent().find("input")).val(); */
				typeArray['properties'] 			= {};
				for(var k=0;k < $($(data).find("select")[i]).parent().parent().parent().parent().parent().parent().parent().parent().parent().children(".div_draging").children("section").length; k++){
					typeObjectTwo($($(data).find("select")[i]).parent().parent().parent().parent().parent().parent().parent().parent().parent().children(".div_draging").children("section")[k], typeArray['properties'], exampleOb);	
				}
				break;
			} else {
				typeArray['type'] 			= $($(data).find("select")[i]).val().toLowerCase();
				/* typeArray['example'] 		= $($($(data).find("select")[i]).parent().parent().parent().find("input")).val(); */
				break;
			}
		}
	}
	function typeArrayMake(type, example, typeArray){
		typeArray['type'] 			= type.toLowerCase();
		/* typeArray['example'] 		= example; */
		typeArray['items'] 		    = {};
		
		return typeArray['items']; 
	}
	
	// 테이터 값 체크
	function dataValidation(){

		$(".err_tooltip").find("dd").remove();
		
		// 기본정보 - 이름 검사
		errCountCk($("input[name='typeName']"), "typeName", true);
		
		// 메인 타입 데이터 검사
		errCountCk($("#mainType"), "mainType", false);
		
		// 기본정보 - 예제 검사
		errCountCk($("input[name='typeExample']"), "typeExample", true);	
		
		// 데이터 타입 검사	
		$.each($(".paramBodyInfoDiv").find("select"), function(index, item){
			errCountCk(item, "datatypeSelect"+index, false);	
		});
		
		// 데이터 이름 검사
		$.each($(".paramBodyInfoDiv").find("input[type='text'][name='name']"), function(index, item) {
			errCountCk(item, "datatypeInput"+index, false);
		});
		 // 데이터 예제 검사
		$.each($(".paramBodyInfoDiv").find("input[type='text'][name='example']"), function(index, item) {
			errCountCk(item, "datatypeExample"+index, false);
		});
		
		// 에러에 대한 내용 추가
		errTextAppend();

		$('.err_tooltip').css("display", "block");
	}


	// datatype 정보 세팅
	function dataInfoSet(datatypeNm){
		var dataInfo = yamlOb.definitions[datatypeNm];
		
		
		if(lowString(dataInfo.type) == "Object"  && $("#datatypeInfo").find("tbody").find(".example").length > 0){
			$("#datatypeInfo").find("tbody").find("tr").eq(0).find("th").eq(1).html('');
			$("#datatypeInfo").find("tbody").find("tr").eq(0).find("td").eq(1).html('');
		} else if( $("#datatypeInfo").find("tbody").find(".example").length == 0){
			$("#datatypeInfo").find("tbody").find("tr").eq(0).find("th").eq(1).html('<div class="essential">예제</div>');
			$("#datatypeInfo").find("tbody").find("tr").eq(0).find("td").eq(1).html('<div class="example"><input type="text" name="typeExample" title="예제 입력"></div>');
		}
		
		
		$("input[name='typeName']").val(datatypeNm);
		$("textarea[name='typeAccount']").val(dataInfo.description);
		$("#datatypeInfo").find("select").val(lowString(dataInfo.type));
		$("#datatypeInfo").find("input[name='typeExample']").val(dataInfo.example);
		
		// 타입이 Object 또는 배열일 경우 div 세팅
		if(lowString(dataInfo.type) == 'Object' && dataInfo.properties != null) { //Object
			
			dataInfoObjectDivSet(dataInfo.properties, $(".paramBodyInfoDiv"), dataInfo);
		
		} else if(lowString(dataInfo.type) == 'Array') { // Array 키값을 알아낸뒤 한번만 돌립니다.
			for(var k in dataInfo) {
				dataInfoArrayDivSet(dataInfo[k], $(".paramBodyInfoDiv"), 0);
				break;
			}
		}
	}
	
	// dataType Object 시에 div 세팅
	function dataInfoObjectDivSet(data, appendTag, parentData){
		if(appendTag.children(".bodyForm").length == 0){
			var html = 	'<div class="div_draging paramBodyDataDiv bodyForm ui-sortable">'+
							'<button type="button" title="파라미터 추가" class="btn btn_addParabox" onclick="objectElAdd(this)"><span>파라미터 추가</span></button>'+
							'<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="objectElAdd(this)"><span>속성 추가</span></button>'+
						'</div>';
			appendTag.append(html);
		}
		$.each(data, function(index, item) {
			dept = dept + 1;
			
			if(lowString(item.type) == "Object"  && $("#dataBodyForm").find("tbody").find(".example").length > 0){
				$("#dataBodyForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
				$("#dataBodyForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
			} else if( $("#dataBodyForm").find("tbody").find(".example").length == 0){
				$("#dataBodyForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
				$("#dataBodyForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
			}
			
			$("#dataBodyForm").find("input[name='required']").attr("id","required"+dept);
			$("#dataBodyForm").find("input[name='required']").next().attr("for","required"+dept);
			$("#dataBodyForm").find("section").attr("id", "section_"+dept)
			appendTag.children(".paramBodyDataDiv").find("button").last().before($("#dataBodyForm").html());
			appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("input[name='name']").val(index)
			appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("select").val(lowString(item.type));
			appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("input[name='account']").val(item.description);
			if(item['x-example'] != undefined){
				appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("input[name='example']").val(item['x-example']);	
			}
			if(parentData['required'] != undefined){
				if(jQuery.inArray(index, parentData['required']) != -1){
					appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("input[name='required']").prop("checked", true);		
				} else {
					appendTag.children(".paramBodyDataDiv").find("#section_"+dept).children(".inner").find("input[name='required']").prop("checked", false);
				}
			}
			if(lowString(item.type) == "Object"){
				
				dataInfoObjectDivSet(item.properties, appendTag.children(".paramBodyDataDiv").find("#section_"+dept), item);
				
			} else if(lowString(item.type) == "Array"){
				dataInfoArrayDivSet(item, appendTag.children(".paramBodyDataDiv").find("#section_"+dept), 2);
				
			}
		});
	}
	
	// dataType Array 시에 div 세팅
	var Ayinnum = 0;
	function dataInfoArrayDivSet(data, appendTag, num){
		var itemType = "";
		var itemExample = "";
		var itemItems = new Array();
		var itemPpt   = new Object();
		Ayinnum = num;
		if(Ayinnum == 0){
			appendTag.append($("#dataForm").html());
			appendTag = appendTag.children(".paramBodyDataDiv").find("section");
		} else {
			var arrayHtml = '';
			if(appendTag.find("tr").eq(Ayinnum).length == 0) {
				arrayHtml = 	'<tr>'+
									'<th scope="row">'+
										'<div class="essential ofClass">of</div>'+
									'</th>'+
									'<td><div>'+
									 	'<select class="w100" onclick="paramTypeClick(this);" name="name">'+
			                            	'<option value="">타입을 선택하여 주세요</option>'+
		                               		<c:forEach var="list" items="${dataTypeList}" varStatus="status">
		                               		'<option value="${list.cdNm}">${list.cdNm}</option>'+
		                               		</c:forEach>
			                          	'</select>'+
									'</div></td>'+
								'</tr>';
				appendTag.find("tbody").append(arrayHtml);
			}
		}
		$.each(data, function(index, item) {
			if(index == "type"){
				itemType = lowString(item);	
			} else if(index == "x-example"){
				itemExample = item;	
			} else if(index == "items"){
				itemItems = item;
			} else if(index == "properties"){
				itemPpt = item;
			}
		});
		appendTag.find("tr").eq(Ayinnum).find("input[name='account']").val(itemExample);	
		appendTag.find("tr").eq(Ayinnum).find("input[name='example']").val(itemExample);	
		appendTag.find("tr").eq(Ayinnum).find("select").val(itemType);
		
		Ayinnum = Ayinnum + 1;
		if(itemType == "Array"){
			dataInfoArrayDivSet(itemItems, appendTag, Ayinnum);
		} else if (itemType == "Object"){
			dataInfoObjectDivSet(itemPpt, appendTag, data);
		}
	}
</script>

<form method="POST" action="" name="apiInfoForm" id="apiInfoForm" class="tempForm">
  <input type="hidden" id="pApiSpcNo" name="apiSpcNo" value="${fn:escapeXml(param.apiSpcNo)}" />
  <input type="hidden" id="pApiNo" name="apiNo" value="${fn:escapeXml(param.apiNo)}" />
  <input type="hidden" id="pApiCtgryNo" name="apiCtgryNo" value="${fn:escapeXml(param.apiCtgryNo)}" />
  <input type="hidden" id="pApiCtgryNm" name="apiCtgryNm" value="${fn:escapeXml(param.apiCtgryNm)}" />
  <input type="hidden" id="pApiDataTypeNm" name="apiDataTypeNm" value="${fn:escapeXml(param.apiDataTypeNm)}" />
  <input type="hidden" id="pApiPath" name="apiPath" value="${fn:escapeXml(param.apiPath)}" />
  <input type="hidden" id="pApiMethod" name="apiMethod" value="${fn:escapeXml(param.apiMethod)}" />
  <input type="hidden" id="pApiCopyYn" name="apiCopyYn" value="" />
</form><!-- #apiInfoForm -->

<form method="POST" action="<c:url value='/api/reg/mvApiInfoReg.do' />" name="apiImportForm" id="apiImportForm" class="tempForm">
  <textarea id="importYamlSbst" name="yamlSbst" class="tempTextarea"></textarea>
  <input type="hidden" id="importYn" name="importYn" value="" />
</form><!-- #apiImportForm -->

<div id="container">
  <div class="contents">
    <div class="conBox">
      <div id="content" class="api_content">
        <!-- regist_wrap -->
        <div class="regist_wrap">
          <div class="regi_bar">
            <%-- //-- [tag:job-20200420][chg][for share regi_bar layout] --%>
            <%@ include file="/WEB-INF/jsp/api/regFormShareRegiBar.jsp" %>
          </div><!-- .regi_bar -->

          <!-- regist_layout -->
          <div class="regist_layout">
            <div class="api_left">
              <%-- //-- [tag:job-20200420][chg][for share left layout] --%>
              <%@ include file="/WEB-INF/jsp/api/regFormShareLeft.jsp" %>
            </div><!-- .api_left -->
            <!--// 생성 버튼 클릭시 나오는 퀵메뉴 //-->
            <ol class="quickmenu"></ol><!-- .quickmenu -->
            <!-- api_right -->
            <div class="api_right">
              <h5 class="rTitleOneDep">DATA TYPE <a href="javascript:;" title="API 등록하는 방법보기" class="rtit_btn" onclick="showApiMV(this, '.mv-wrap');return false;">API 등록하는 방법보기</a></h5>
              <div class="btn_RT">
                <button type="button" title="취소" class="btn btn_sml" onClick="history.back()" ><span>취소</span></button>
                <button type="button" title="저장" class="btn btn_sml btn_black" onclick="dataTypeSave();"><span>저장</span></button>
              </div>

              <div class="rightConBoxing">
                <!-- accordian active type -->
                <ul class="acco_opened">
                  <!-- 기본 정보 -->
                  <li>
                    <article class="tooltip"></article>
                    <!-- tooltip -->
                    <dl class="tooltiptext">
                        <dt>기본 정보</dt>
                        <dd>Data Type의 기본정보를 입력하세요. 등록된 Data Type은 API 타입 선택 항목에 노출되어 선택할 수 있습니다.</dd>
                    </dl>
                    <!-- // tooltip -->
                    <!-- active bar -->
                    <div>
                      <a class="acco_act active" href="javascript:;" title="기본 정보">
                        <span>기본 정보</span>
                      </a>
                    </div>
                    <!-- // active bar -->

                    <!-- slide Content -->
                    <div class="hidden_div" style="display: block;"> <!-- style="display:block;" -->
                      <div class="pkg_board">
                        <!-- table start -->
                        <section>
                          <table class="table-vw">
                            <caption>table Table</caption>
                            <colgroup>
                              <col style="width:20%;">
                              <col style="width:80%;">
                            </colgroup>
                            <tbody>
                              <tr>
                                <th scope="row">
                                  <div class="essential">
                                    <article class="tooltip"></article>이름
                                    <!-- tooltip -->
                                    <dl class="tooltiptext">
                                      <dt>이름</dt>
                                      <dd>Data Type을 대표할 수 있는 이름을 입력하세요.</dd>
                                    </dl>
                                    <!-- // tooltip -->
                                  </div>
                                </th>
                                <td><div><input type="text" name="typeName" id="typeName" title="이름 입력">
                                  <p class="red_txt">이름을 입력하세요.</p>
                                </div></td>
                              </tr>
                              <tr>
                                <th scope="row">
                                  <div>
                                    <article class="tooltip"></article>설명
                                    <!-- tooltip -->
                                    <dl class="tooltiptext">
                                      <dt>설명</dt>
                                      <dd>Data Type의 요약정보를 입력하세요.<br>설명을 보고 Data Type에 대한 특징을 확인 합니다.</dd>
                                    </dl>
                                    <!-- // tooltip -->
                                  </div>
                                </th>
                                <td><div class="txtarea_wrap"><textarea title="설명 입력" name="typeAccount" id="typeAccount" onchange="apiRegCheckStrLength(4000,'typeAccount')"   onkeyup="apiRegCheckStrLength(4000,'typeAccount')"></textarea></div></td>
                              </tr>
                            </tbody>
                          </table>
                        </section>
                        <!-- // table End -->
                      </div><!-- .pkg_board -->
                    </div><!-- .hidden_div -->
                    <!-- // slide Content -->
                  </li>
                  <!-- // 기본 정보 -->

                  <!--  Data Type -->
                  <li>
                    <article class="tooltip"></article>
                    <!-- tooltip -->
                    <dl class="tooltiptext">
                      <dt>DATA TYPE</dt>
                      <dd>Data Type에 대한 정보를 입력하세요. <br>API 타입에서 Data Type을 선택할 경우 정의된 값이 적용됩니다.</dd>
                    </dl>
                    <!-- // tooltip -->
                    <!-- active bar -->
                    <div>
                      <a class="acco_act active" href="javascript:;" title="DATA TYPE">
                        <span>DATA TYPE</span>
                      </a>
                    </div>
                    <!-- // active bar -->

                    <!-- slide Content -->
                    <div class="hidden_div" style="display:block;">
                      <div class="schema_wrap" id="datatypeInfo">
                        <div class="pkg_board">
                          <!-- table start -->
                          <section>
                            <table class="table-oneStyle">
                              <caption>table Table</caption>
                              <colgroup>
                                <col style="width:13%;">
                                <col style="width:40%;">
                                <col style="width:7%;">
                                <col style="width:40%;">
                              </colgroup>
                              <tbody>
                                <tr>
                                  <th><div class="essential">타입</div></th>
                                  <td><div>
                                      <select class="w100" onchange="typeClick(this);" name="type" id="mainType">
                                      		<option value="">타입을 선택하여 주세요</option>
                                      		<c:forEach var="list" items="${dataTypeList}" varStatus="status">
                                      			<option value="${list.cdNm}">${list.cdNm}</option>
                                      		</c:forEach>
                                      </select>
                                  </div></td>
                                  <th><div class="essential">예제</div></th>
                                  <td><div class="example"><input type="text" name="typeExample" id="typeExample" title="예제 입력"></div></td>
                                </tr>
                              </tbody>
                            </table>
                          </section>
                          <!-- // table End -->
                        </div><!-- .pkg_board -->
                      </div><!-- .schema_wrap -->

                      <!-- body -->
                      <div class="schema_wrap">
                        <!-- parameter_add -->
                        <div class="parameter_add">
                          <div class="paraDiv_drag paramBodyInfoDiv" style="display:block;">
                          <!-- div_draging 1set -->
                          <!-- // div_draging 1set -->
                          </div><!-- .paraDiv_drag .paramBodyInfoDiv -->
                        </div><!-- .parameter_add -->
                        <!-- // parameter_add -->
                      </div><!-- .schema_wrap -->
                      <!-- // body -->
                      
                    </div><!-- .hidden_div -->
                    <!-- slide Content -->
                  </li>
                  <!-- // Data Type -->

                </ul><!-- .acco_opened -->
                <div class="btn_set">
                  <button type="button" title="취소" class="btn btn_sml" onClick="history.back()" ><span>취소</span></button>
                  <button type="button" title="저장" class="btn btn_sml btn_black" onclick="dataTypeSave();"><span>저장</span></button>
                </div>

              </div><!-- .rightConBoxing -->

            </div><!-- .api_right -->
            <!-- // api_right -->
          </div><!-- .regist_layout -->
          <!-- // regist_layout -->


        </div><!-- .regist_wrap -->
        <!-- // regist_wrap -->
      </div><!-- .api_content -->
    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->

<dl class="err_tooltip" style="display: none; top: 60px; left: 2040.97px;">
  <dt>다음과 같은 오류가 발생하였습니다.</dt>
</dl>

<div id="dataForm" style="display:none;">
  <div class="div_draging paramBodyDataDiv bodyForm">
    <section>
      <div class="inner paramBodyDataDiv noBodyForm">
        <p class="handler_bar ui-sortable-handle">handler</p>
        <div class="para_content">
          <div class="pkg_board">
            <!-- table start -->
            <table class="table-noBrd">
              <caption>table Table</caption>
              <colgroup>
                <col style="width:10%;">
                <col style="width:40%;">
                <col style="width:10%;">
                <col style="width:40%;">
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row">
                    <div class="essential ofClass">of</div>
                  </th>
                  <td>
                    <div>
                      <select class="w100" onclick="paramTypeClick(this);" name="type">
                        <option value="">타입을 선택하여 주세요</option>
                        <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                          <option value="${list.cdNm}">${list.cdNm}</option>
                        </c:forEach>
                      </select>
                    </div>
                  </td>
                  <th scope="row"></th>
                  <td></td>
                </tr>
              </tbody>
            </table>
          </div><!-- .pkg_board -->
        </div><!-- .para_content -->
      </div><!-- .inner -->
    </section>
  </div><!-- .div_draging -->
</div><!-- #dataForm -->

<div id="dataBodyForm" style="display:none;">
<!--// parameter dep 1-1 -->
<section>
  <div class="inner">
    <p class="handler_bar">handler</p>
    <div class="para_content">
      <div class="pkg_board">
        <table class="table-noBrd">
          <caption>table Table</caption>
          <colgroup>
           <col style="width:10%;">
           <col style="width:40%;">
           <col style="width:10%;">
           <col style="width:40%;">
          </colgroup>
          <tbody>
            <tr>
              <td colspan="4">
                <div>
                  <!-- <span class="red_txt">중복된 이름이 있습니다.</span> -->
                  <span class="fr">
                    <a href="javascript:;">
                      <input type="checkbox" name="required" title="필수">
                      <label><span></span>필수</label>
                    </a>
                    <button type="button" title="삭제" class="btn btn_garbage" onclick="paramDel(this);"><span>삭제</span></button>
                 </span>
                </div>
              </td>
            </tr>
            <tr>
              <th scope="row"><div class="essential">이름</div></th>
              <td><div><input type="text" name="name" title="이름 입력"></div></td>
              <th scope="row">
              <td><div><input type="text" name="account" title="설명 입력"></div></td>
            </tr>
            <tr>
              <th scope="row"><div class="essential">타입</div></th>
              <td>
                <div>
                  <select class="w100" onclick="paramTypeClick(this);" name="type">
                    <option value="">타입을 선택하여 주세요</option>
                    <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                      <option value="${list.cdNm}">${list.cdNm}</option>
                    </c:forEach>
                  </select>
                </div>
              </td>
              <th scope="row"><div class="essential">예제</div></th>
              <td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div><!-- .inner -->
</section>
<!-- parameter dep 1-1 //-->
</div><!-- #dataBodyForm -->

<%-- //-- [tag:job-20200812][chg][for share popup] --%>
<%@ include file="/WEB-INF/jsp/api/regFormSharePopup.jsp" %>
<%-- //-- [tag:adpt][add][for api clone] --%>
<%@ include file="/WEB-INF/jsp/api/popApiClone.jsp" %>
<%-- //-- [tag:adpt][add][for api search] --%>
<jsp:include page="/WEB-INF/jsp/adptran/vue_part_mount_adptranService.jsp" flush="false" />
<%-- //-- [tag:PRJ-20220901][for simple api reg] --%>
<%@ include file="/WEB-INF/jsp/api/popSimpleApiReg.jsp" %>
</t:layout>
