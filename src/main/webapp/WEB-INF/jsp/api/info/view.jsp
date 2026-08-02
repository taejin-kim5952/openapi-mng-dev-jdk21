<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/taglib.jsp" %>
<t:layout type="apiInfo">
<jsp:attribute name="head">
<script src="<c:url value="/resources/js/json-view/jquery.json-viewer.js" />"></script>
<link href="<c:url value="/resources/css/json-view/jquery.json-viewer.css" />"  rel="stylesheet" media="screen">
<style>
	pre{
		white-space: pre-wrap;
		}
</style>
<script type="text/javascript">
/*************에디터 호출 관련 이벤트 시작**************************************************************************************/
 	var editorTomcatUse  	 = '<spring:eval expression="@environment.getProperty('editor.tomcat.use')" />' 			// 에디터 톰캣 사용여부
	var editorTomcatHostUse  = '<spring:eval expression="@environment.getProperty('editor.tomcathost.url')" />' 		// 에디터 톰캣 사용여부
	var editorHostUrl    	 = '<spring:eval expression="@environment.getProperty('editor.host.url')" />'      			// 에디터 호스트 url

	/**
	 * userId : 유저 ID
	 * import : yaml 저장 경로
	 * */
	function mvSwaggerEdit(){
		var enCmbrId     = '${ssUserVo.enCmbrId}' == '' ? 'nouser' : '${ssUserVo.enCmbrId}';
		var sessionKey   = 'sessionkey';
		var apiSpcNo     = $("#pApiSpcNo").val();
		var apiPath      = $("#pApiPath").val();
		var apiMethod    = $("#pApiMethod").val();

		if(enCmbrId=='nouser'){
	    	userwidth  = 1000;
	    }else{
	    	userwidth  = (screen.width - 15);
	    }

	    userheight  = (screen.height - 130);

		if(editorTomcatUse == "true"){
			window.sessionStorage.setItem('sessionkey', sessionKey);
			window.sessionStorage.setItem('mbrid', enCmbrId);
			window.sessionStorage.setItem('apino', apiSpcNo);

			window.sessionStorage.setItem('new', false);
			window.sessionStorage.setItem('no-proxy', true);

			window.sessionStorage.setItem('goToApiPath', apiMethod + " " + apiPath);

			openOnceTomcat(editorTomcatHostUse, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,men ubar=no, status=no, toolbar=no');
		} else {
			openOnce(editorHostUrl+'mbrid='+enCmbrId+'&apino='+apiSpcNo+'&new=true&no-proxy=false&sessionkey='+sessionKey, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
		}
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
/*************에디터 호출 관련 이벤트 종료**************************************************************************************/
 	var paramApiNo    = "${param.apiNo}";
	var paramApiSpcNo = "${param.apiSpcNo}";
	$(document).ready(function(){
        console.log("here");
		if(paramApiNo != "" && paramApiSpcNo != ""){
			setApiInfo(paramApiNo, paramApiSpcNo);
		} else {
			$(".acco_depth1").find("li").eq(0).find("div").eq(0).find("a").click();
			$(".acco_depth1").find("li").eq(0).find("div").eq(1).find(".acco_depth2").find("li").eq(0).find("div").eq(0).find("a").click();
			$(".acco_depth1").find("li").eq(0).find("div").eq(1).find(".acco_depth2").find("li").eq(0).find("div").eq(1).find(".last_depth").find("li").eq(0).find("a").click();
		}
	});
	var apiMethod 		= "";	// api 매소드
	var apiPath 		= "";	// api 패스
	var apiId 			= "";	// api 아이디
	var systemId 		= "";	// 시스템 id
	/*0524 추가 시작*/
	var systemNm 		= "";	// 시스템 명
	var ver = "";
	/*0524 추가 끝*/
	var inHtml 	= "";	// 요청파라미터 body 	html
	var schema  = "";   // 기본 스키마
	var apiGubunNm =  "";//Api구분명
	var queryParamStr = "";
	var pathParamBeStr = "";
	var pathParamAfStr = "";
	var reqBodyParamOb = new Object();
	var reqFormdataParamOb = new Object();
	var reqHeadersParamOb = new Object();
	var colsMax = 0; // object 현재 뎁스 수
	var colsAllMax = 0; // object 총 뎁스 수
	function setApiInfo(apiNo, apiSpcNo){

		//검색기능 추가 시작 0426
		$("#apiInfo_search_result").css("display","none");
		$(".pg_location").css("display","block");
		$(".rightConBoxing").css("display","block");
		$(".useGuide_content apiSpecDiv").css("display","block");
		//검색기능 추가 끝 0426


		$(".api_right").scrollTop(0);
		$(".useGuide_content").css("display","none");
		$(".apiSpecDiv").css("display","block");
		/** 저장을 위해 param에 담은뒤 ajax 호출   ==========>   ***/
		var param = {
			apiSpcNo: apiSpcNo,
			apiNo 	: apiNo
		};

		$.ajax({
		 	url    : '<c:url value="/api/info/selApiAjax.do"/>',
		 	type   : 'POST',
		 	data   : param,
		 	cache  : false,
		 	async  : false,
		 	success: function(data){
		 		$('#pApiNm').val(data.defaultApiMap.titleNm);
	 			if(data.defaultApiMap != null){
		 			// 레프트 메뉴 세팅;
			 		if(paramApiNo != "" && paramApiSpcNo != ""){
			 			twoDeptOpen($("#"+data.defaultApiMap.sysId+"_a"), data.defaultApiMap.sysId, '2');
			 			twoDeptOpen($("#spcNo_"+data.defaultApiMap.apiSpcNo), data.defaultApiMap.apiSpcNo, '3');
					}
			 		$(".selApi").removeClass("active");
			 		$("#selApi_"+ data.defaultApiMap.apiNo).addClass("active");
		 			$("#yamlSbst").val(data.defaultApiMap.yamlSbst);
		 			$("#pApiSpcNo").val(data.defaultApiMap.apiSpcNo);
		 			$("#pApiPath").val(data.defaultApiMap.apiPath);
		 			$("#pApiMethod").val(data.defaultApiMap.method);
		 			$("#hApiNo").val(data.defaultApiMap.apiNo);
		 			apiMethod = data.defaultApiMap.method;
		 			apiPath   = data.defaultApiMap.apiPath;
		 			apiId     = data.defaultApiMap.apiId;
		 			systemId  = data.defaultApiMap.sysId;
		 			systemNm  = data.defaultApiMap.sysNm;
		 			apiGubunNm= data.defaultApiMap.apiGubunNm;
		 			ver		  = data.defaultApiMap.ver
		 			setYamlInfo();
	 			}else{
					setDisplay('apiInfo_sub');
					$("#yamlSbst").val('');
		 			$("#pApiSpcNo").val('');
		 			$("#pApiPath").val('');
		 			$("#pApiMethod").val('');
	 			}
		    },
	 		error:function(request,status,error){
				alert("code:"+request.status+"\n"+"error:"+error);
		    }
		});
	};

	function setYamlInfo(){
		var YAML = SwaggerParser.YAML;
		var parser = new SwaggerParser();
		var yamlData = YAML.parse($("#yamlSbst").val());
		var yamlOb   = yamlData;
		inHtml = "";
		// console.log("yamlOb", yamlOb);
		// 선택한 패스의 메소드 변수 세팅
		var pathInfoOb = null;
		if(typeof yamlOb.paths != 'undefined' && typeof yamlOb.paths[apiPath] != 'undefined'){
		var pathInfoOb = yamlOb['paths'][apiPath][apiMethod.toLowerCase()];
		/******************** 기본정보 저장 시작 *****************************/
		// api 이름
		$(".infoTitle").text("");
		$(".infoTitle").text(pathInfoOb.summary);
		$(".apiNmVal").text(pathInfoOb.summary);

		// api 내용
		$(".infoDescription").text("");
		$(".infoDescription").text(pathInfoOb.description);
		// api 메소드
		$(".infoMethod").text("");
		$(".infoMethod").text(apiMethod);

		// api 구분명
		$(".infoApiGubun").text("");
		$(".infoApiGubun").text(apiGubunNm);

		// api path
		$(".infoPath").text("");
		$(".infoPath").text(apiPath);
		// api 아이디
		$(".infoApiId").text("");
		$(".infoApiId").text(pathInfoOb.operationId);
		// api 타입
		$(".infoApiType").text("");
		if(yamlOb['x-apitype'] != undefined){
			$(".infoApiType").text(yamlOb['x-apitype']);
		}
		// api 시스템
		$(".infoSystem").text("");
		if(systemId == "OLLEHMAP"){
			systemId = "GEOMASTER";
		}
		// api 시스템
		$(".infoSystem").text("");
		$(".infoSystem").text(systemNm);
		// 버전
		$(".infoVersion").text("");

		/*0524 DB의 버전으로 변경*/
// 		$(".infoVersion").text(yamlOb.info.version);
		$(".infoVersion").text(ver);

		var txtHost = "";
	    if(systemId == "GEOMASTER"){
	      txtHost = yamlOb.host+"   ※ 테스트를 위한 호스트로 Geo master는 gis.kt.com 으로 상용 서비스됩니다";
	    }else if(systemId == "IOTMAKERS"){
	      txtHost = yamlOb.host+"   ※ 테스트를 위한 호스트로 IoTMakers는 iotmakers.kt.com:443 으로 상용 서비스됩니다";
	    }else if(systemId == "OTHER"){
	      txtHost = yamlOb.host+"   ※ 테스트를 위한 호스트로 GiGA Enegy는 int.api.kt.com 으로 상용 서비스됩니다";
	    }else{
	      txtHost = yamlOb.host;
	    }

		// 호스트
		$(".infoHost").text("");
		$(".infoHost").text(txtHost);
		// 기본경로
		$(".infoBasePath").text("");
		$(".infoBasePath").text(yamlOb.basePath);
		// 스키마
		$(".infoScema").text("");
		$(".infoScema").text(yamlOb.schemes.join(', '));
		// 요청 content type
		if(yamlOb.consumes != undefined){
			$(".infoReqCntType").text("");
			$(".infoReqCntType").text(yamlOb.consumes.join(', '));
		}
		// 응답 content type
		if(yamlOb.produces != undefined){
			$(".infoResCntType").text("");
			$(".infoResCntType").text(yamlOb.produces.join(', '));
		}

		/******************** 기본정보 저장 종료 *****************************/


		/******************** 보안 저장 시작 *****************************/
		$("#apiSecuritySection").find("tbody").html("");
		$("#apiSecuritySection").css("display","none");
		if(yamlOb.securityDefinitions != undefined){
			var securityHtml = "";
			var securityScopeHtml = "";
			// api에 등록된 보안정보 조회
			if(pathInfoOb.security != undefined){
				$("#apiSecuritySection").css("display","block");
				$.each(pathInfoOb.security , function (index, info) {
					$.each(info, function(sIndex, sInfo){
						securityHtml = securityHtml + 	'<tr>'+
													        '<td>'+
												                '<div>' + sIndex + '</div>'+
												            '</td>'+
												            '<td class="al">'+
												                '<div>' + sInfo.join(', ') + '</div>'+
												            '</td>'+
												        '</tr>';
					});
				});
			}
			// api에 등록된 글로벌 보안 정보 조회
			else if(yamlOb.security != undefined){
				$("#apiSecuritySection").css("display","block");
				$.each(yamlOb.security , function (index, info) {
					$.each(info, function(sIndex, sInfo){
						securityHtml = securityHtml + 	'<tr>'+
													        '<td>'+
												                '<div>' + sIndex + '</div>'+
												            '</td>'+
												            '<td class="al">'+
												                '<div>' + sInfo.join(', ') + '</div>'+
												            '</td>'+
												        '</tr>';
					});
				});
			}
			$("#apiSecuritySection").find("tbody").append(securityHtml);
		}
		/******************** 보안 저장 종료 *****************************/

		/******************** 요청 파라미터 시작 *****************************/
		var reqQueryArray = new Array();
		var reqHeadersArray = new Array();
		var reqPathArray = new Array();
		var reqFormDataArray = new Array();
		var reqBodyArray = new Array();

		reqHeadersParamOb = new Object();
		reqBodyParamOb = new Object();

		$("#reqParamiterSection").css("display", "none");
		if(pathInfoOb['parameters'] != undefined){
			$("#reqParamiterSection").css("display", "block");
			if(pathInfoOb.parameters.length > 0){
				for(var i=0; i < pathInfoOb.parameters.length; i++){
					var paramVar = pathInfoOb.parameters[i];
						 if (paramVar.in == "query")   { reqQueryArray.push(paramVar);    }
					else if (paramVar.in == "header")  { reqHeadersArray.push(paramVar);  }
					else if (paramVar.in == "path")    { reqPathArray.push(paramVar);     }
					else if (paramVar.in == "formData"){ reqFormDataArray.push(paramVar); }
					else if (paramVar.in == "body")    { reqBodyArray.push(paramVar);     }
				}
			}
		}
		// query 파라미터 셋팅
		$(".reqQuery").find("tbody").html("");
		$(".reqQuery").css("display", "none");
		if(reqQueryArray.length > 0){
			$(".reqQuery").css("display", "block");
			inHtml = '';
			queryParamStr = "";
			$.each(reqQueryArray, function(index, item){
				// 쿼리 파라미터 예시 세팅
				queryParamStr = queryParamStr + "&"+item.name+"="+item['x-example'];
				// 예제 없을경우
				if(item['x-example'] == undefined){ item['x-example'] = "" };
				// 필수값 변환
				if(item.required == true){
					item.required = "Y";
				} else {
					item.required = "N";
				}

				inHtml =	inHtml +	'<tr>'+
						                    '<td><span>'+item.name+'</span></td>'+
						                    '<td><span>'+item.type+'</span></td>'+
						                    '<td><span>'+item.required+'</span></td>'+
						                    '<td class="al"><span>'+item.description+'</span></td>'+
						                    '<td class="al"><span>'+item['x-example']+'</span></td>'+
						                '</tr>';
			});
			$(".reqQuery").find("tbody").append(inHtml);
		}
		// reqHeaders 파라미터 셋팅
		$(".reqHeaders").find("tbody").html("");
		$(".reqHeaders").css("display", "none");
		if(reqHeadersArray.length > 0){
			$(".reqHeaders").css("display", "block");
			inHtml = '';
			$.each(reqHeadersArray, function(index, item){
				reqHeadersParamOb[item.name] = item['x-example'];
				// 예제 없을경우
				if(item['x-example'] == undefined){ item['x-example'] = "" };
				// 필수값 변환
				if(item.required == true){
					item.required = "Y";
				} else {
					item.required = "N";
				}

				inHtml =	inHtml +	'<tr>'+
								                    '<td><span>'+item.name+'</span></td>'+
								                    '<td><span>'+item.type+'</span></td>'+
								                    '<td><span>'+item.required+'</span></td>'+
								                    '<td class="al"><span>'+item.description+'</span></td>'+
								                    '<td class="al"><span>'+item['x-example']+'</span></td>'+
								                '</tr>';
			});
			$(".reqHeaders").find("tbody").append(inHtml);
		}
		// pathArray 파라미터 셋팅
		$(".reqPath").find("tbody").html("");
		$(".reqPath").css("display", "none");
		if(reqPathArray.length > 0){
			$(".reqPath").css("display", "block");
			inHtml = '';
           	pathParamBeStr = "";
			pathParamAfStr = "";
			$.each(reqPathArray, function(index, item){
				pathParamBeStr = pathParamBeStr + "/{" + item.name + "}";
				pathParamAfStr = pathParamAfStr + "/" + item['x-example'] + "";
				// 예제 없을경우
				if(item['x-example'] == undefined){ item['x-example'] = "" };
				// 필수값 변환
				if(item.required == true){
					item.required = "Y";
				} else {
					item.required = "N";
				}

				inHtml =	inHtml +	'<tr>'+
						                    '<td><span>'+item.name+'</span></td>'+
						                    '<td><span>'+item.type+'</span></td>'+
						                    '<td><span>'+item.required+'</span></td>'+
						                    '<td class="al"><span>'+item.description+'</span></td>'+
						                    '<td class="al"><span>'+item['x-example']+'</span></td>'+
						                '</tr>';
			});
			$(".reqPath").find("tbody").append(inHtml);
		}
		// formData 파라미터 셋팅
		$(".reqFormData").find("tbody").html("");
		$(".reqFormData").css("display", "none");
		if(reqFormDataArray.length > 0){
			$(".reqFormData").css("display", "block");
			inHtml = '';

			$.each(reqFormDataArray, function(index, item){
				// 예제 없을경우
				if(item['x-example'] == undefined){ item['x-example'] = "" };
				// 필수값 변환
				if(item.required == true){
					item.required = "Y";
				} else {
					item.required = "N";
				}

				inHtml 			=	inHtml +	'<tr>'+
								                    '<td><span>'+item.name+'</span></td>'+
								                    '<td><span>'+item.type+'</span></td>'+
								                    '<td><span>'+item.required+'</span></td>'+
								                    '<td class="al"><span>'+item.description+'</span></td>'+
								                    '<td class="al"><span>'+item['x-example']+'</span></td>'+
								                '</tr>';
			});
			$(".reqFormData").find("tbody").append(inHtml);
		}
		// body 파라미터 셋팅
		$(".reqBody").find("tbody").html("");
		$(".reqBody").css("display", "none");
		if(reqBodyArray.length > 0){
			// body의 content type 그리는 부분
			if(pathInfoOb['consumes'].length > 0){
				$(".reqBody").children(".dl_stl").find("dd").html(pathInfoOb['consumes'].join(", "));
			};

			// body의 테이블 그리는 부분
			$(".reqBody").css("display", "block");
			// table 폼 초기화
			inHtml = '';
			colsAllMax = 0;
			$.each(reqBodyArray, function(index, item){
				var requiredYn = "N";
				if(item['required'] != undefined){
					if(item['required'] == true){
						requiredYn = "Y";
					}
				}
				if(item.schema['type'] != undefined){
					reqBodyParamOb = item['x-example'];
					if(item['x-example'] == undefined){
						item['x-example'] = "예제";
					}
					if(item.schema.type == "object"){
						item['x-example'] = "";
					}


					inHtml 				= inHtml + 	'<tr class="reqDept" data-cols="1">'+
										                '<td><div>'+item.name+'</div></td>'+
										                '<td><div>'+item.schema.type+'</div></td>'+
										                '<td><div>'+requiredYn+'</div></td>'+
										                '<td class="al"><div>'+item.schema.description+'</div></td>'+
										                '<td class="al"><div>'+item['x-example']+'</div></td>'+
										            '</tr>';
					if(item.schema.type == "array"){
						colsMax = 0;
						resDataInfoArrayDivSet(item.schema.items, "reqDept" ,2);
					} else if (item.schema.type == "object"){
						colsMax = 0;
						resDataInfoObjectDivSet(item.schema.properties, item.schema, 2, "reqDept");
					}

				} else {
					// datatype 이 있을경우
					inHtml 				= inHtml + 	'<tr>'+
										                '<td><div>'+item.name+'</div></td>'+
										                '<td><div>'+item.schema['$ref'].replace("#/definitions/","")+'</div></td>'+
										                '<td><div>'+requiredYn+'</div></td>'+
										                '<td class="al"><div>'+item.description+'</div></td>'+
										                '<td class="al"><div>'+item['x-example']+'</div></td>'+
										            '</tr>';
				}
				$(".reqBody").find("tbody").append(inHtml);

				var nextCnt = 0;
				if(item.schema.type == "object"){
					$(".reqDept").each(function(index, item){
						var dataCols = parseInt(colsAllMax+1) - parseInt($(item).attr("data-cols"));
						$(item).find("td").eq(0).attr("colspan", dataCols);

						if( parseInt($(item).attr("data-cols") ) != 1){
							if(  parseInt(nextCnt) > 0 &&
									parseInt($(".reqDept").eq(index-1).attr("data-cols") ) < parseInt($(item).attr("data-cols"))   ){
								$(item).find("td").eq(0).before('<td class="move_bar" rowspan="'+nextCnt+'">+</td>');
							}
						}

						nextCnt = nextCntVal(index, item, $(item).attr("data-cols"), ".reqDept");
					});
					$(".reqBody").find("thead").find("th").eq(0).attr("colspan", colsAllMax);
				} else {
					$(".reqBody").find("thead").find("th").eq(0).attr("colspan", "0");
				}
			});
		}
		/******************** 요청 파라미터 종료 *****************************/

		/******************** 요청 세팅 시작 *****************************/
		$.each(yamlOb.schemes, function(index, item){
			if(item == "http"){
				schema = "http";
				return false;
			} else {
				schema = "https";
			}
		});
		// 요청예시 부분 추가
		if(pathParamBeStr != ""){
			apiPath = apiPath.replace(pathParamBeStr, pathParamAfStr);
		}
		$("#reqExampleDiv").children("pre").html("");

		var replaceBasePath = yamlOb.basePath;


		if(replaceBasePath == "/"){
			replaceBasePath = "";
		}

		$("#reqExampleDiv").find("pre").append(apiMethod + " <span style='color:#4b86ce;'>" + schema + "://" + yamlOb.host + replaceBasePath + apiPath + "?" + queryParamStr.substring(1) + " " + "</span> HTTP/1.1 <br />");

		// Host
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Host: </span>"   + yamlOb.host);
		if(pathInfoOb['consumes'] != undefined){
			// Accept
			$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Accept: </span>" + pathInfoOb.consumes);
			// Accept-Encoding
			$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Accept-Encoding: </span>gzip,deflate,sdch");
			// Accept-Language
			$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Accept-Language: </span>en-US;en;q=0.8;fa;q=0.6;sv;q=0.4");
		}
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Cache-Control: </span>no-cache");
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Connection: </span>keep-alive");
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Origin: </span>http://openapis.kt.com:8081");
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Referer: </span>http://openapis.kt.com:8081/apieditor/");
		$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>User-Agent: </span>Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
		var securityArray = new Array();
		if(pathInfoOb.security != undefined){
			$.each(pathInfoOb.security , function (index, info) {
				$.each(info, function(sIndex, sInfo){
					securityArray.push(sIndex);
				});
			});
			$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Authorization: </span>" + securityArray);
		}
		// api에 등록된 글로벌 보안 정보 조회
		else if(yamlOb.security != undefined){
			$.each(yamlOb.security , function (index, info) {
				$.each(info, function(sIndex, sInfo){
					securityArray.push(sIndex);
				});
			});
			$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>Authorization: </span>" + securityArray.join(", "));
		}
		// headers 셋팅
		if(!jQuery.isEmptyObject(reqHeadersParamOb)){
			$.each(reqHeadersParamOb , function (index, info) {
				$("#reqExampleDiv").find("pre").append("<br /><span  style='font-weight: bold;'>" + index+": </span>" + info);
			});
		};
		// formData 셋팅
		if(reqFormDataArray.length > 0){
			$.each(reqFormDataArray, function(index, item){
				reqFormdataParamOb = new Object();
				$("#reqExampleDiv").find("pre").append('<br /><pre id="json-reqFormData'+index+'"></pre>');
				reqFormdataParamOb[item.name] = item['x-example'];
				$('#json-reqFormData'+index).jsonViewer(reqFormdataParamOb, {withQuotes:true});
			});
		}
		// body 셋팅
		if(!jQuery.isEmptyObject(reqBodyParamOb)){
			$("#reqExampleDiv").find("pre").append('\n <pre id="json-reqBody"></pre>');
			$('#json-reqBody').jsonViewer(reqBodyParamOb, {withQuotes:true});
		};
		/******************** 요청 세팅 종료 *****************************/








		/******************** 응답 파라미터 시작 *****************************/

		// 응답 파라미터 셋팅
		$("#resParamiterSection").css("display", "none");

		$("#resExampleDiv").find(".bodyData").html("");
		$("#resExampleDiv").find(".headersData").html("");

		$("#resParamiterSection").find(".resHeaders").find("tbody").html("");
		$("#resParamiterSection").find(".resBody").find("tbody").html("");
		$("#resParamiterSection").find(".resHeaders").css("display", "none");
		$("#resParamiterSection").find(".resBody").css("display", "none");
		$.each(pathInfoOb.responses, function(index, item){
			if(index == "200"){
				$("#resParamiterSection").css("display", "block");
				// Headers 셋팅
				if(pathInfoOb.responses[index].headers != undefined){
					inHtml = '';
					$("#resParamiterSection").find(".resHeaders").css("display", "block");
					$.each(pathInfoOb.responses[index].headers, function(headIndex, headItem){
						$("#resExampleDiv").find(".headersData").append('<span style="font-weight: bold;">'+headIndex+': </span>' + headItem['x-example'] + '<br />');
						inHtml 			= inHtml + '<tr>'+
										                '<td><div>'+headIndex+'</div></td>'+
										                '<td><div>'+headItem.type+'</div></td>'+
										                '<td class="al"><div>'+headItem.description+'</div></td>'+
										                '<td class="al"><div>'+headItem['x-example']+'</div></td>'+
										            '</tr>';

						if(headItem.type == "array"){
							resDataInfoArrayDivSet(headItem.items, "resDept" , 1);
						}
				 	});
					$("#resParamiterSection").find(".resHeaders").find("tbody").append(inHtml);
				}
				// produces 셋팅
				if(pathInfoOb.produces.length > 0){
					$("#resParamiterSection").find(".resBody").css("display", "block");
					$("#resParamiterSection").find(".dl_stl").find("dd").html(pathInfoOb.produces.join(", "));
				}

				// body 셋팅
				if(!jQuery.isEmptyObject(pathInfoOb.responses[index].schema)){
					inHtml = 	'';
					var bodyVar = pathInfoOb.responses[index].schema;
					var requiredYn = "N";
					if(bodyVar['required'] != undefined){
						if(jQuery.inArray(bodyVar['x-name'], bodyVar['required']) != -1){
							requiredYn = "Y";
						}
					}
					// 데이터 타입 이용 안할시에
					if(bodyVar['type'] != undefined){
						$("#resExampleDiv").find(".bodyData").append('<pre id="json-resBody'+index+'"></pre>');
						$('#json-resBody'+index).jsonViewer(bodyVar.example, {withQuotes:true});

						if(bodyVar.type == "object"){
							bodyVar.example  = "";
						}

						inHtml 			= "";
						// table body 세팅
						inHtml 				= inHtml + 	'<tr class="resDept" data-cols="1">'+
										                '<td><div>'+bodyVar['x-name']+'</div></td>'+
										                '<td><div>'+bodyVar.type+'</div></td>'+
										                '<td><div>'+requiredYn+'</div></td>'+
										                '<td class="al"><div>'+bodyVar.description+'</div></td>'+
										                '<td class="al"><div>'+bodyVar.example+'</div></td>'+
										            '</tr>';

						if(bodyVar.type == "array"){
							colsMax = 0;
							resDataInfoArrayDivSet(bodyVar.items, "resDept" , 2);
						} else if (bodyVar.type == "object"){
							colsMax = 0;
							resDataInfoObjectDivSet(bodyVar.properties[bodyVar['x-name']].properties, bodyVar.properties[bodyVar['x-name']], 2, "resDept");
						}

					}
					// 데이터 타입 이용 시에
					else {
						inHtml 			= inHtml + 	'<tr>'+
										                '<td><div>'+bodyVar['x-name']+'</div></td>'+
										                '<td><div>'+bodyVar['$ref'].replace("#/definitions/","")+'</div></td>'+
										                '<td><div>'+requiredYn+'</div></td>'+
										                '<td class="al"><div>'+bodyVar.description+'</div></td>'+
										                '<td class="al"><div>'+bodyVar['x-example']+'</div></td>'+
										            '</tr>';
					}

					$("#resParamiterSection").find(".resBody").find("tbody").html(inHtml);

					var nextCnt = 0;
					if(item.schema.type == "object"){
						$(".resDept").each(function(index, item){
							var dataCols = parseInt(colsAllMax+1) - parseInt($(item).attr("data-cols"));
							$(item).find("td").eq(0).attr("colspan", dataCols);

							if( parseInt( $(item).attr("data-cols") ) != 1){
								if( parseInt(nextCnt) > 0 && parseInt( $(".resDept").eq(index-1).attr("data-cols") ) < parseInt( $(item).attr("data-cols")) ){
									$(item).find("td").eq(0).before('<td class="move_bar" rowspan="'+nextCnt+'">+</td>');
								}
							}

							nextCnt = nextCntVal(index, item, $(item).attr("data-cols"), ".resDept");
						});
						$(".resBody").find("thead").find("th").eq(0).attr("colspan", colsAllMax);
					} else {
						$(".resBody").find("thead").find("th").eq(0).attr("colspan", "0");
					}
				}

			}
		});

		/******************** 응답 파라미터 종료 *****************************/
		}else{
			setDisplay('apiInfo_sub');
		}
	}
	function nextCntVal(index, item, colsVal, deptNm){
		var nextCnt = 0;
		$(deptNm).each(function(iIndex, iItem){
			if(iIndex > index){
				if(  ( parseInt($(iItem).attr("data-cols") )== parseInt(colsVal )  ) || (   parseInt($(iItem).attr("data-cols") ) < parseInt( colsVal) )){
					return false;
				} else {
					nextCnt = parseInt(nextCnt) + 1;
				}
// 				if($(iItem).attr("data-cols") == colsVal){
// 					return false;
// 				} else {
// 					nextCnt = nextCnt + 1;
// 				}
			}
		});
		return nextCnt;
	}
	// 파라미터 타입이 array일때 세팅
	function resDataInfoArrayDivSet(data, deptNm , curDept){
		var itemType = "";
		var itemExample = "";
		var itemItems = new Array();
		var itemPpt   = new Object();

		$.each(data, function(index, item) {
			if(index == "type"){
				itemType = item;
			} else if(index == "x-example"){
				itemExample = item;
			} else if(index == "items"){
				itemItems = item;
			} else if(index == "properties"){
				itemPpt = item;
			}
		});

// 		inHtml 				= inHtml + 	'<tr>'+
// 									        '<td><div></div></td>'+
// 									        '<td><div>'+itemType+'</div></td>'+
// 									        '<td><div></div></td>'+
// 									        '<td class="al"><div>'+itemExample+'</div></td>'+
// 									        '<td class="al"><div>'+itemExample+'</div></td>'+
// 									    '</tr>';
		if(itemType == "array"){
			resDataInfoArrayDivSet(itemItems , deptNm , curDept);
		} else if (itemType == "object"){
			resDataInfoObjectDivSet(itemPpt, data, curDept, deptNm);
		}
	}

	// 파라미터 타입이 object일때 세팅
	function resDataInfoObjectDivSet(data, parentData, curDept, deptNm){
		var eachIndex = 0;
		$.each(data, function(index, item) {
			eachIndex = eachIndex + 1 ;
			var requiredYn = "N";
			var addTrHtml  = "";
			if(item['example'] == undefined){
				item['example'] = item['x-example'];
			}
			if(parentData['required'] != undefined){
				if(jQuery.inArray(index, parentData['required']) != -1){
					requiredYn = "Y";
				}
			}


			colsMax = colsMax + curDept;
			if(item.type == "object"){
				item['example'] = "";
			}
			inHtml 				= inHtml + 	'<tr class="'+deptNm+'" data-cols='+curDept+'">'+
										        '<td><div>'+index+'</div></td>'+
										        '<td><div>'+item.type+'</div></td>'+
										        '<td><div>'+requiredYn+'</div></td>'+
										        '<td class="al"><div>'+item.description+'</div></td>'+
										        '<td class="al"><div>'+item['example']+'</div></td>'+
										    '</tr>';

			if(item.type == "object"){
				resDataInfoObjectDivSet(item.properties, item, curDept + 1, deptNm);
			} else if(item.type == "array"){
				resDataInfoArrayDivSet(item, deptNm , curDept + 1);
				if(colsAllMax < colsMax){
					colsAllMax = colsMax;
				}
			} else {
				if(colsAllMax < colsMax){
					colsAllMax = colsMax;
				}
			}

		});
	}
	function twoDeptOpen(myData, id, dept){
		var leftHtml = "";
		var param = {};
		if(dept == 2){
			param = {
				sysId		: id,
				leftDept	: dept
			};
		} else if(dept == 3){
			param = {
				apiSpcNo	: id,
				leftDept	: dept
			};
		}

		if($(myData).parent().parent().children("div").length == 1){
			$.ajax({
			 	url    : '<c:url value="/api/info/savApiMenuListAjax.do"/>',
			 	type   : 'GET',
			 	data   : param,
			 	async  : false,
			 	cache  : false,
			 	//dataType: "json",
			 	//contentType: "application/json",
			 	success: function(data){
			 		$(".hidden_div").css("display", "none");
			 		$(".acco_toggle").removeClass("active");
			 		$(".acco_depth2").removeClass("active");
			 		$(myData).addClass("active");

		 			if(dept == 2){
			 			for(var i=0;i < data.menuList.length;i++){
			 				if($(myData).parent().parent().children("div").length == 1){
			 					var divHtml = "";
			 					divHtml = 	'<div class="hidden_div" style="display: block;">'+
					                            '<div>'+
					                                '<ul class="accordion_inner acco_depth2 active">'+
					                                 '</ul>'+
					                             '</div>'+
					                         '</div>';
			                	$(myData).parent().parent().append(divHtml);
			 				}

			 				leftHtml = leftHtml + 	'<li>'+
								                        '<div>'+
								                            '<a class="acco_toggle" href="javascript:;" title="' + data.menuList[i].apiNm + '" onclick="twoDeptOpen(this, \'' + data.menuList[i].apiSpcNo + '\', 3)" id="spcNo_'+data.menuList[i].apiSpcNo+'">'+
								                            	'<span class="api-2dp_tit">' + data.menuList[i].apiNm + '('+data.menuList[i].ver+')</span><em></em>'+
								                            '</a>'+
								                        '</div>'+
								                    '</li>';
			 			}
			 			$(myData).parent().parent().find(".acco_depth2").append(leftHtml);
		 			} else if(dept == 3){
		 				$(myData).parent().parent().parent().parent().parent().css("display", "block");
		 				$(myData).parent().parent().parent().parent().parent().parent().find("div").eq(0).find(".acco_toggle").addClass("active");
			 			for(var i=0;i < data.menuList.length;i++){
			 				if($(myData).parent().parent().children("div").length == 1){
			 					var divHtml = "";
			 					divHtml = 	'<div class="hidden_div" style="display: block;">'+
				                            	'<div>'+
				                                	'<ul class="last_depth">'+
				                               		'</ul>'+
				                            	'</div>'+
				                        	'</div>';
			 					$(myData).parent().parent().append(divHtml);
			 				};

			 				leftHtml = leftHtml + 	'<li>'+
									                    '<div id="selApi_'+data.menuList[i].apiNo+'" class="selApi">'+
									                        '<a href="javascript:;" class="api-3dp_tit" title="' + data.menuList[i].cApiNm + '"  onclick="setApiInfo(\'' + data.menuList[i].apiNo + '\', \'' + id + '\')" >' + data.menuList[i].cApiNm + '</a>'+
									                    '</div>'+
									                '</li>';
			 			}
			 			$(myData).parent().parent().find(".last_depth").append(leftHtml);
		 			}
		 			leftTabOn();
			    },
		 		error:function(request,status,error){
		  			alert("code:"+request.status+"\n"+"error:"+error);
		    	}
			});
		}
	}

	function leftTabOn(){
		// accordion - Toggle
		$(".accordion2 > li > div > a.acco_toggle").off("click");
		$(".accordion2 > li > div > a.acco_toggle").on("click", function(e){
			if($(this).parent().has("ul")) {
			  e.preventDefault();
			}
			if(!$(this).hasClass("active")) {
			  // hide any open menus and remove all other classes
			  $(".accordion2 li .hidden_div").slideUp(350);
			  $(".accordion2 li div a").removeClass("active");

			  // open our new menu and add the open class
			  $(this).parent().next().slideDown(350);
			  $(this).addClass("active");
			}

			else if($(this).hasClass("active")) {
			  $(this).removeClass("active");
			  $(this).parent().next().slideUp(350);
			}
		});

		// accordion - Toggle
		$(".accordion_inner > li > div > a.acco_toggle").off("click");
		$(".accordion_inner > li > div > a.acco_toggle").on("click", function(e){
			if($(this).parent().has("ul")) {
			  e.preventDefault();
			}
			if(!$(this).hasClass("active")) {
			  // hide any open menus and remove all other classes
			  $(".accordion_inner li .hidden_div").slideUp(350);
			  $(".accordion_inner li div a").removeClass("active");

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
	function mvMenu(data, param){
		$(".api_right").scrollTop(0);
		$(".selApi").removeClass("active");
		$(data).parent().addClass("active");
		$(".useGuide_content").css("display","none");
		if(param == 'vm'){
			$(".devVmDiv").css("display","block");
		} else if(param == 'data') {
			$(".testDataDiv").css("display","block");
		} else if(param == 'sdk'){
			$(".sdkGuideDiv").css("display","block");
		}
	}

	/*검색 기능*/
	function fnApiSearch(pageIdx,target){
		var apiSearchKeyword = '';
		//단순 페이지 이동
		if(isEmpty(target)){
			apiSearchKeyword = $('#searchKeyword').val();
		}
		//검색버튼 이벤트
		else{
			// 엔터클릭
			if($(target).prop('tagName') == 'input'){
				apiSearchKeyword = $(target).val();
			}
			// 버튼클릭
			else{
				apiSearchKeyword = $(target).parent().find('input[type="text"]').val();
			}
		}

		if(!isEmpty($.trim(apiSearchKeyword))){

			var param = {
				searchKeyword : apiSearchKeyword,
				pageIndex: pageIdx,
				searchYn : 'true'
			}

			$.ajax({
			 	url    : '<c:url value="/api/info/selApiSearchList.do"/>',
			 	type   : 'POST',
			 	data   : param,
			 	dataType : 'JSON',
				async  : true,
			 	success: function(data){
			 		var html 		= "";
			 		var szClassName = "";
			 		var nIndex 		= 0;

			 		if(data.searchApiList.length > 0){
			 			html += '<h2 class="ta-c" style="padding:30px 0 15px;margin-bottom:30px;border-bottom:1px solid #d6dbdf;font-size:29px;color:#333333;font-weight:700;"><span class="font-point01">'+apiSearchKeyword+'</span>에 대해 <span class="font-point01">'+data.paginationInfo.totalRecordCount+'</span>개의 API가 검색되었습니다.</h2>'
				 		$.each(data.searchApiList,function(){
				 			var szApiId	= "";
				 			var apiNm = this.fApiNm;
				 			apiNm = fnSetFocusSearchKeyword(apiNm,apiSearchKeyword);
				 			var apiDesc = lenMaxStrSearch(this.apiDesc,200,apiSearchKeyword);
				 			apiDesc = fnSetFocusSearchKeyword(apiDesc,apiSearchKeyword);

				 			if(nIndex % 2 == 0) {
				 				szClassName = '';
				 			} else {
				 				szClassName = ' mb30_bg';
				 			}
				 			// API_ID로 검색되었을 경우
				 			//if(apiId != this.apiId) {
				 			//	szApiId = "[" + apiId + "] ";
				 			//}

				 			html += '<div class="mb30'  + szClassName + '">'
				 				 +  '<dl class="txt_common02 mb10">'
				 				 +	'<dd>'+this.sysIdNm+' <strong>'+this.apiSpcNm+'</strong></dd>'
				 				 +	'</dl>'
				 				 +	'<a href="/apidev/api/info/mvInfoView.do?apiSpcNo='+this.apiSpcNo+'&apiNo='+this.apiNo+'"><strong class="font-point01">'+apiNm+'</strong></a>'
				 				 +	'<p class="txt_common mt15">'+apiDesc+'</p>'
				 				 +	'</div>';

				 			nIndex++;
				 		});


			 		}
			 		else {
			 			html += '<h2 class="ta-c"><span class="font-point01">'+apiSearchKeyword+'</span>에 대해 <span class="font-point01">0</span>개의 API가 검색되었습니다.</h2>'
			 		}

			 		$('#searchKeyword').val(data.apiMainVo.searchKeyword);
			 		$('#apiInfo_search_result_list').html(html);
			 		drawPaging('paging' ,data.paginationInfo.currentPageNo, data.paginationInfo.firstPageNoOnPageList, data.paginationInfo.totalPageCount, data.paginationInfo.lastPageNoOnPageList  ,'fnApiSearch');
			 		setDisplay('apiInfo_search_result');
			    }
			});
		}
		else{
			alert("검색어를 입력해주세요.");
		}

	}

	function setDisplay(target){
		$(".api_right").scrollTop(0);

		var $contents = $('.api_right > div');

		$.each($contents,function(){
			if(target == this.id){
				$(this).css('display','block');

				if(this.id == 'apiInfo_sub'){
					$('#notExistApi').text($('#pApiNm').val()+'에 대한 가이드 정보가 존재하지 않습니다.');
				}
			}
			else{
				$(this).css('display','none');
			}
		});
	}

	/* 검색키워드 포커싱 처리
	 * CYD - 2020.06.08
	 */
	function fnSetFocusSearchKeyword(html, keyword){
		var strNewHtml = html;
		var strRepalce = new RegExp(keyword,"gi");
		var result 	   = strNewHtml.match(strRepalce);
		//console.log(result);

		if(result != null) {
			$.each(result,function(){
				strNewHtml = strNewHtml.replace(new RegExp(this,"g"), "<span style='background-color:#fff599;color:red;'>" + this + "</span>");
				//console.log(this);
			});
		}
		//strNewHtml = html.replace(strRepalce, "<span style='background-color:#fff599;color:red;'>" + keyword + "</span>");

		return strNewHtml;
	}
</script>
</jsp:attribute>
<jsp:body>
<textarea id="yamlSbst" name="yamlSbst" class="tempTextarea"></textarea>

<input type="hidden" id="hApiNo"   name="hApiNo"   />
<input type="hidden" id="pApiNm"   name="pApiNm"   />
<input type="hidden" id="pApiSpcNo"   name="pApiSpcNo"   />
<input type="hidden" id="pApiPath"    name="pApiPath"    />
<input type="hidden" id="pApiMethod"  name="pApiMethod"  />
<input type="hidden" id="searchKeyword">
<div id="container">
	<div class="contents">
		<div class="conBox">
			<div id="content" class="api_spec">
                   <!-- regist_wrap -->
                   <div class="regist_wrap">
                       <!-- regist_layout -->
                       <div class="spec_layout">
                           <!-- api_left -->
                           <div class="api_left">                                                                
                               <div class="spec_search">
                                   <div class="search_bar">
                                       <div><input type="text" placeholder="검색어를 입력해주세요."></div>
                                       <button type="button" class="btn_search" onClick="fnApiSearch(1,this);"><span>검색</span></button>
                                   </div>
                               </div>

                               <div class="dragToggle">
                                   <ul class="accordion2 acco_depth1">
                                   
                                 		<c:forEach var="info" items="${leftMenuList}" varStatus="status">
	                                        <li>
	                                        	<div>
	                                                <a class="acco_toggle " href="javascript:;" title="${info.sysNm}"  onclick="twoDeptOpen(this,'${info.sysId}', '2')" id="${info.sysId}_a">
	                                                	<span class="api-1dp_tit">${info.sysNm}</span><em></em>
	                                                </a>
	                                            </div>
	                                        </li>
                                       </c:forEach>
                                       <!-- 공통SDK -->
                                       <li>
                                       	   <div>
                                               <a class="acco_toggle " href="javascript:;" title="공통 SDK" >
                                               		<span class="api-1dp_tit">공통 SDK</span><em></em>
                                               </a>
                                           </div>
                                           <!-- 2depth Content -->
                                           <div class="hidden_div">
                                               <div>
                                                   <ul class="last_depth">
                                                       <!-- 3depth -->
                                                       <li>
                                                           <div class="selApi">
                                                               <a href="javascript:;" class="api-3dp_tit" title="개발VM 신청 가이드"     onclick="mvMenu(this, 'vm')">개발VM 신청 가이드</a>
                                                           </div>
                                                       </li>
                                                       <li>
                                                           <div class="selApi">
                                                               <a href="javascript:;" class="api-3dp_tit" title="Test Data 등록 요청"    onclick="mvMenu(this, 'data')">Test Data 등록 요청</a>
                                                           </div>
                                                       </li>
                                                       <li>
                                                           <div class="selApi">
                                                               <a href="javascript:;" class="api-3dp_tit" title="SDK 가이드 및 다운로드" onclick="mvMenu(this, 'sdk')">SDK 가이드 및 다운로드</a>
                                                           </div>
                                                       </li>
                                                   </ul>
                                               </div>
                                           </div>
                                       </li>
                                   </ul>
                               </div>
                           </div>
                           <!-- // api_left -->

                           <!-- api_right -->
                           <div class="api_right">
                               <div class="pg_location"><a href="javascript:;">Go home</a> <span>&gt;</span> API 규격서</div>
                               
                               <div class="rightConBoxing">
                                   <!-- content 시작 -->
                                   <div class="useGuide_content apiSpecDiv">
                                       <!--  List start -->
                                       <section>
                                          <h5>API 이름</h5>
                                          <button type="button" title="테스트 하기" class="btn btn_sml3 btn_black floatR" onclick="mvSwaggerEdit();" style="float: right;"><span>테스트 하기</span></button>
                                          <div>
                                               <span class="infoTitle"></span>
                                               <p class="infoDescription"></p>
                                          </div>
                                       </section>
									<!-- API 기본정보 -->
                                       <section>
                                          <h5>API 기본정보</h5>
                                          <div>
                                               <div class="pkg_board">
                                                   <!-- table start -->
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:20%;">
                                                           <col style="width:80%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>구분</div></th>
                                                               <th scope="col"><div>기본정보</div></th>
                                                           </tr>
                                                       </thead>

                                                       <tbody>
                                                           <tr>
                                                               <td>API 아이디</td>
                                                               <td class="al"><div class="infoApiId"></div></td>
                                                           </tr>
                                                           <tr>
																								              <td>API 구분</td>
																								              <td class="al"><div class="infoApiGubun"></div></td>
																								          </tr>
                                                           <tr>
                                                               <td>API 타입</td>
                                                               <td class="al"><div class="infoApiType"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>시스템 </td>
                                                               <td class="al"><div class="infoSystem"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>버전</td>
                                                               <td class="al"><div class="infoVersion"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>테스트 호스트</td>
                                                               <td class="al"><div class="infoHost"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>기본경로</td>
                                                               <td class="al"><div class="infoBasePath"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>스키마</td>
                                                               <td class="al"><div class="infoScema"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>Path</td>
                                                               <td class="al"><div class="infoPath"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>Method </div></td>
                                                               <td class="al"><div class="infoMethod"></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>요청 Content Type</td>
                                                               <td class="al"><div class=infoReqCntType></div></td>
                                                           </tr>
                                                           <tr>
                                                               <td>응답 Content Type</td>
                                                               <td class="al"><div class="infoResCntType"></div></td>
                                                           </tr>
                                                               
                                                       </tbody>
                                                   </table>
                                                   <!-- // table End -->
                                               </div>
                                          </div>
                                       </section>
									<!-- 보안 정보 -->
                                       <section id="apiSecuritySection">
                                          <h5>API 보안</h5>
                                          <div>
                                               <div class="pkg_board">
                                                   <!-- table start -->
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:30%;">
                                                           <col style="width:70%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>범위이름</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                                   <!-- // table End -->
                                               </div>
                                          </div>
                                       </section>
                                       <!-- 요청 파라미터 -->
                                       <section id="reqParamiterSection">
                                          <h5>요청 파라미터</h5>
                                          <!-- Query 파라미터 -->
                                          <div class="reqQuery">
                                               <span>Query</span>
                                               <div class="pkg_board">
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:25%;">
                                                           <col style="width:20%;">
                                                           <col style="width:10%;">
                                                           <col style="width:20%;">
                                                           <col style="width:35%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>이름</div></th>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>예제</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                               </div>
                                          </div>
                                          <!-- Headers 파라미터 -->
                                          <div class="reqHeaders">
                                               <span>Headers</span>
                                               <div class="pkg_board">
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:25%;">
                                                           <col style="width:20%;">
                                                           <col style="width:10%;">
                                                           <col style="width:20%;">
                                                           <col style="width:35%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>이름</div></th>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>예제</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                               </div>
                                          </div>
                                          <!-- Path 파라미터 -->
                                          <div class="reqPath">
                                               <span>Path</span>
                                               <div class="pkg_board">
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:25%;">
                                                           <col style="width:20%;">
                                                           <col style="width:10%;">
                                                           <col style="width:20%;">
                                                           <col style="width:35%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>이름</div></th>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>예제</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                               </div>
                                          </div>
                                          <!-- FormData 파라미터 -->
                                          <div class="reqFormData">
                                               <span>FormData</span>
                                               <div class="pkg_board">
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:25%;">
                                                           <col style="width:20%;">
                                                           <col style="width:10%;">
                                                           <col style="width:20%;">
                                                           <col style="width:35%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>이름</div></th>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>예제</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                               </div>
                                          </div>
                                          <!-- Body 파라미터 -->
                                          <div class="reqBody">
                                               <span>Body</span>
                                               <dl class="dl_stl">
                                                   <dt>Content Type :</dt>
                                                   <dd></dd>
                                               </dl>
                                               <div class="pkg_board">
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>이름</div></th>
                                                               <th scope="col"><div>타입</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>예제</div></th>
                                                           </tr>
                                                       </thead>
													<tbody>
                                                       </tbody>
                                                   </table>
                                               </div>
                                         	</div>
                                       	</section>
                                       	<!-- 요청 파라미터 -->
                                       	<section id="resParamiterSection">
                                       		<h5>응답파라미터</h5>
                                       		<div>
                                                <span>200-OK</span>
                                                <p>조회(인증) 성공</p>
												<div class="resHeaders">
                                                	<span>Headers</span>
	                                                <div class="pkg_board">
	                                                    <!-- table start -->
	                                                    <table class="table-vw view_style">
	                                                    	<caption>table Table</caption>
	                                                        <colgroup>
	                                                            <col style="width:25%;">
	                                                            <col style="width:25%;">
	                                                            <col style="width:25%;">
	                                                            <col style="width:25%;">
	                                                        </colgroup>
	
	                                                        <thead>
	                                                            <tr>
	                                                                <th scope="col"><div>이름</div></th>
	                                                                <th scope="col"><div>타입</div></th>
	                                                                <th scope="col"><div>설명</div></th>
	                                                                <th scope="col"><div>예제</div></th>
	                                                            </tr>
	                                                        </thead>
	                                                        <tbody>
	                                                        </tbody>
	                                                    </table>
	
	                                                    <!-- // table End -->
	                                                </div>
												</div>
                                                <div class="resBody">
	                                                <span>Body</span>
	                                                <dl class="dl_stl">
	                                                    <dt>Content Type :</dt>
	                                                    <dd></dd>
	                                                </dl>
	                                                <div class="pkg_board move_bar move_bar2">
	                                                    <!-- table start -->
	                                                    <table class="table-vw view_style">
	                                                        <caption>table Table</caption>	
	                                                        <thead>
	                                                            <tr>
	                                                                <th scope="col"><div>이름</div></th>
	                                                                <th scope="col"><div>타입</div></th>
	                                                                <th scope="col"><div>필수여부</div></th>
	                                                                <th scope="col"><div>설명</div></th>
	                                                                <th scope="col"><div>예제</div></th>
	                                                            </tr>
	                                                        </thead>
	
	                                                        <tbody>
	                                                        </tbody>
	                                                    </table>
	                                                    <!-- // table End -->
	                                                </div>
	                                        	</div>
                                            </div>
                                       	</section>
                                       	<section>
	                                       	<h5>요청예시</h5>
	                                        <div>
	                                            <div class="round_codebox" id="reqExampleDiv">
	                                            	<pre></pre>
	                                                <!-- 파란색 GET 색상 코드 : color:#4b86ce -->
	                                            </div>
	                                        </div>
	                                    </section>
										<section>
	                                        <h5>응답예시</h5>
	                                        <div>
	                                            <div class="round_codebox" id="resExampleDiv">
		                                            <div class="headersData"></div>
		                                            <br />	
		                                            <div class="bodyData"></div>
	                                                <!-- 녹색 색상 코드 : color:#5ca924 -->
	                                                <!-- // 에디터 들어가는 영역 -->
	                                            </div>
	                                        </div>
										</section>
										<section>
	                                        <h5><span class="apiNmVal"></span> 시작하기</h5>
	                                        <div>
	                                        	<p>1. 선택한 API의 정보를 확인한 후 [모의서버 테스트하기]를 클릭하여 테스트를 진행 합니다.</p>
	                                            <p><img src="<c:url value="/resources/images/guideimg/spec_guide01.png" />" alt="별도 첨부"></p>
	                                            <div class="hideText">
	                                                <ul>
	                                                    <li>3) 선택한 API의 정보를 확인 합니다.</li>
	                                                    <li>4) API의 정보를 확인한 후 [모의서버 테스트하기]를 클릭하여 테스트를 진행 합니다. </li>
	                                                </ul>
	                                            </div>
	                                            
	                                            <p><span class="apiNmVal"></span> 서비스를 처음 시작할 때 필요한 내용을 설명합니다.</p>
	                                            <p>2. 테스트를 하기 위해서는 서버SDK, 클라이언트 SDK를 다운로드하여 개발에 활용할 수 있습니다.</p>
	                                            <p><img src="<c:url value="/resources/images/guideimg/spec_guide02.png" />" alt="별도 첨부"></p>
	                                            <div class="hideText">
	                                                <ul>
	                                                    <li>1) 서버 SDK, 클라이언트 SDK를 다운로드하여 개발에 활용할 수 있습니다.</li>
	                                                    <li>2) 선택한 API의 보안 스키마를 확인하고 인증 합니다.</li>
	                                                </ul>
	                                            </div>
	                                        </div>
	                                	</section>
	                                	
                                        <div class="btn_set">
                                            <button type="button" title="테스트 하기" class="btn-lg2 btn_black" onclick="mvSwaggerEdit();"><span>테스트 하기</span></button>
                                        </div>
                                   </div>
                                   <!-- content 끝 -->
                                   <!-- content 시작 -->
                                   <!-- 페이지만 출력 할때 이 부분만 붙이시면 됩니다. -->
                                   <div class="useGuide_content devVmDiv">
                                       <h4><span>개발VM 신청 및 기술지원</span></h4>
                                       <!--  List start -->
                                       <section>
                                          <h5>개발 플랫폼(VM)개요</h5>
                                          <div>
                                              <p>서비스의 신속한 개발 착수와 PoC를 위한 플랫폼 개발 환경의 인프라가 필요한 서비스를 위하여 Develop VM을 제공하며 IPC Portal를 통하여 신청함</p>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>프로세스 구성도</h5>
                                          <div>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0101.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <p>사내 클라우드 통합관리</p>
                                                   <dl>
                                                       <dt>VM 신청 & 구축</dt>
                                                       <dd>클라우드 신청 (사업부서)</dd>
                                                       <dd>클라우드 설계 (플랫폼 설계담당)</dd>
                                                       <dd>클라우드 구축 (구축 담당자)</dd>
                                                       <dd>접근제어 & 물리방화벽& 소프트웨어설치 (사업부서,ITSM)</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>VM 반납 신청 & 삭제</dt>
                                                       <dd>클라우드 변경 (사업부서)</dd>
                                                       <dd>클라우드 설계 (플랫폼 설계담당)</dd>
                                                       <dd>클라우드 반납 (ITO 미위탁구축 담당자)</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>단위서비스 리소스 이관</dt>
                                                       <dd>신규 서비스 생성 (사업부서)</dd>
                                                       <dd>리소스 이관 신청 (사업부서)</dd>
                                                       <dd>검토 및 처리 (플랫폼 설계담당)</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>단위서비스 리소스 이관</dt>
                                                       <dd>리소스 사용현황 (서버/디스크)</dd>
                                                       <dd>서버 이관 이력</dd>
                                                       <dd>설계자 권한 관리 (추가/삭제)</dd>
                                                       <dd>리소스 사용 이력 관리 (신청자/설계자)</dd>
                                                       <dd>통계자료추출</dd>
                                                       <dd>프로세스 LeadTime 통계</dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>신청 화면</h5>
                                          <div>
                                              <p>서비스의 신속한 개발 착수와 PoC를 위한 플랫폼 개발 환경의 인프라가 필요한 서비스를 위하여 Develop VM을 제공하며 IPC Portal를 통하여 신청함</p>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0102.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <dl>
                                                       <dt>1. 기본정보 입력</dt>
                                                       <dd>서비스 관련 기본정보 입력</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>2. 인프라구성</dt>
                                                       <dd>서비스에서 필요한 개발 플랫폼 Spec 입력</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>3. Loadbalancer 구성</dt>
                                                       <dd>Loadbalancer의 구성 정보 입력</dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>상세 신청 가이드</h5>

                                          <div>
                                              <span>대상</span>
                                              <p>Develop 환경의 인프라가 필요한 IPC포탈 사용자</p>
                                              <span>리소스 현황</span>
                                              <p>구축이 완료되어 사용중인 인프라의 현황 조회</p>
                                              <span>리소스 신청</span>
                                              <p>
                                                   <em>기존의 ‘클라우드 신청’과 동일하게 사용자가 인프라를 구성하여 설계를 신청한다.</em>
                                                   <em>사용자는 본인이 작성한 신청서의 목록을 확인할 수 있다.</em>
                                              </p>
                                              
                                              <span>리소스 신청서 목록</span>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0103.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <dl>
                                                       <dt>- 신청중 : </dt>
                                                       <dd>신청을 진행중인 단계</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>- 설계요청완료 : </dt>
                                                       <dd>신청을 완료하였지만, 설계자가 확인하지 않아 신청을 취소/재신청할 수 있는 단계</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>- 설계중 :</dt>
                                                       <dd>설계자가 신청내용을 확인중인 단계</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>- 설계반려 : </dt>
                                                       <dd>설계 반려처리 되어 사유 확인 후 재신청 가능</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>- 구축중 : </dt>
                                                       <dd>설계 승인처리 되어 클라우드 구축으로 넘어간 단계</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>- 구축완료 : </dt>
                                                       <dd>‘리소스 현황’에서 확인 가능하며 변경신청 가능</dd>
                                                   </dl>
                                               </div>

                                               <span>신청기본정보</span>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0104.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <dl>
                                                       <dd>- 신청자의 default는 본인이지만, 변경버튼을 이용하여 타인을 지정할 수 있다.</dd>
                                                       <dd>- 개발플랫폼명은 기존의 ‘서비스인프라명’기능을 한다.</dd>
                                                       <dd>- 서비스오픈예정일과 구축희망일은 현재일로부터 7일 이후여야 한다.</dd>
                                                   </dl>
                                               </div>

                                               <span>인프라구성</span>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0105.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <dl>
                                                       <dd>- NetworkSArea, OsType, Spec, ServerType, 용도를 입력한 후 추가를 클릭한다.</dd>
                                                       <dd>- 서버를 추가한 후, 데이터볼륨설정 기능을 이용하여 Single Storage를 생성할 수 있다.</dd>
                                                       <dd>- 동일한 구성의 서버를 1대 추가할 때는 ‘복제’, 여러대의 서버나 데이터 볼륨을 포함하여 추가할 때는 ‘고급복제’기능을 이용한다.</dd>
                                                   </dl>
                                               </div>

                                               <span>LoadBalancer 구성</span>
                                              <p><img src="<c:url value="/resources/images/guideimg/devguide0106.png" />" alt="별도 첨부"></p>
                                              <div class="hideText">
                                                   <dl>
                                                       <dd>- NetworkSArea, Algorithm, Port 입력 후 저장을 클릭한다.</dd>
                                                       <dd>- RedirectionMode는 DSR, Protocol은 ANY로 고정되어 있다.</dd>
                                                       <dd>- 추가된 LB에는 Dummy VM이 자동으로 생성 및 연결되며, 삭제시 함께 삭제된다.</dd>
                                                       <dd>- LB추가 후 밸런싱 대상 설정 기능을 이용할 수 있다.</dd>
                                                       <dd>- 신청사항을 입력 후 ‘신청하기’ 버튼으로 설계를 요청한다.</dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>
                                   </div>
                                   <!-- content 시작 -->
                                   <!-- 페이지만 출력 할때 이 부분만 붙이시면 됩니다. -->
                                   <div class="useGuide_content testDataDiv">
                                       <h4><span>Test Data 등록 요청</span></h4>
                                       <!--  List start -->
                                       <section>
                                          <h5>KT SHUB TEST DATA등록</h5>
                                          <div>
                                              <span>개요</span>
                                              <p>KT Infra에서 보유한 다양한 유무선 기능 및 자원 활용을 위하여 제공되는 SHUB API를 테스트하기 위한 가상의 DATA를 의미합니다. 서비스 개발 시 API의 
   사용에 대한 테스트가 필요한 경우 임의의 데이터를 등록하여 API호출에 대한 응답 값을 확인할 수 있도록 지원 합니다.</p>
                                          </div>
                                       </section>

                                       <section>
                                          <div>
                                              <span>등록 시 유의사항</span>
                                              <p>1) 작성규칙
       해당 Enabler System에서 제공하는 연동 규격서를 참조하여 API에서 사용되는 업무 절차 및 파라미터명을 확인한 후 양식에 개인정보가 없는 가상의
       데이터로 작성하도록 하며, 최소한의 정보를 등록 해야 합니다.</p>
                                              <p>2) 테스트 의도
       테스트 데이터 등록 요청 시 목적을 “기술지원 메뉴”의 입력 폼에 입력 합니다.</p>
                                               <p>3) 적용 시간
       등록 요청 테스트 데이터에 대한 적용은 접수일로부터 해당 Enabler System에 등록되기까지 약 3일(working day기준)정도 소요 됩니다.</p>
                                               <p>4) 기타
       OIF_750, OIF_754와 같은 N-STEP 연동 API는 테스트 데이터 등록 요청 시, 가상 정보가 아닌 실제 폰번호 정보로 DATA를 등록해야 하며, DATA유지기간은
       1일입니다. 등록된 테스트 데이터의 일부는 사용 기간이 정해져 있으며 연장이 필요한 경우 추가 요청 해야 합니다.</p>
                                          </div>
                                       </section>

                                       <section>
                                          <div>
                                              <span>TEST DATA등록 Enabler</span>
                                              <p>- SHUB : 사용자 계약 정보 API<br>
                                               - CAPRI : 인증 API<br>
                                               - N–STEP : 부가서비스 API<br>
                                               - LBS : 측위 API
                                               </p>
                                          </div>
                                       </section>
                                   </div>
                                   <!-- content 끝 -->
                                   <!-- content 시작 -->
                                   <div class="useGuide_content sdkGuideDiv">
                                       <h4><span>SDK 가이드 및 다운로드</span></h4>
                                       <!--  List start -->
                                       <section>
                                          <h5>KT Open API SDK 에 대하여</h5>
                                          <div>
                                               <span>KT Open API 란</span>
                                               <p>KT Infra에서 보유한 다양한 유무선 기능 및 자원 제공을 위한 Interface를 의미 합니다.<br>
                                               사용자는 KT Open API를 이용하여 새로운 수익 창출이 가능한 비즈니스 개발이 가능합니다.  <br>
                                               현재 SHUB, IotMakers, Olleh MAP, gigaGenie, ucloud 등의 서비스에서 Open API를 제공하고 있으며, 향후 지속적으로 증가할 것으로 예상됩니다.</p>
                                               <p><img src="<c:url value="/resources/images/guideimg/devguide0301.png" />" alt="별도 첨부"></p>
                                               <div class="hideText">
                                                   <dl>
                                                       <dt>OpenAPI를 제공하는 KT 시스템</dt>
                                                       <dd>SHUB</dd>
                                                       <dd>IoTMakers</dd>
                                                       <dd>ucloud biz</dd>
                                                       <dd>GIGA Genie</dd>
                                                   </dl>
                                               </div>

                                               <span>KT Open API SDK 란</span>
                                               <p>KT에서 제공하는 Open API 연동 개발을 위한 통합된 개발 KIT를 의미 합니다. 아래의 목표를 지향 합니다. </p>
                                               <p><img src="<c:url value="/resources/images/guideimg/devguide0302.png" />" alt="별도 첨부"></p>
                                               <div class="hideText">
                                                   <dl>
                                                       <dt>연동 플랫폼의 독립적</dt>
                                                       <dd>KT에서 제공하는 API중 Restful API와
                                                       SOAP API 에 대하여
                                                       플랫폼에 상관없이 하나의 SDK를 
                                                       이용하여 범용적으로 사용할 수 있습니다.</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>규격의 유연성</dt>
                                                       <dd>인프라에서 제공하는 규격에 대하여
                                                       SDK의 변환 기능을 이용하여 서비스에서
                                                       원하는 형식(JSON, XML, MAP, STRING)으로
                                                       출력할 수 있으며, 입력값에 대해서도
                                                       자유로운 규격(JSON, XML, STRING)으로
                                                       입력이 가능합니다. </dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>개발의 편리성</dt>
                                                       <dd>SDK에서 제공하는 기능들을
                                                       하나의 Library로 제공하여
                                                       사용이 효율적인 개발이 가능하도록 
                                                       지원 합니다.</dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>KT Open API SDK 구성 및 지원 환경 </h5>
                                          <div>
                                               <span>SDK 구성 요소</span>
                                               <p>KT의 SDK는 아래의 구성으로 이루어져 있으며 통합 패키지 형태 및 항목별 다운로드가 가능합니다.</p>
                                               <p><img src="<c:url value="/resources/images/guideimg/devguide0303.png" />" alt="별도 첨부"></p>
                                               <div class="hideText">
                                                   <dl>
                                                       <dd>API 연동 Library </dd>
                                                       <dd>API 연동 Sample Source</dd>
                                                       <dd>API 연동 Document</dd>
                                                       <dd>SDK Guide Video </dd>
                                                       <dd>API Test Console</dd>
                                                       <dd>SHUB API 입력 Sample 입력 전문</dd>
                                                       <dd>지원환경 : JDK 1.6 이상</dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>KT Open API SDK 이용가이드(SDK)</h5>
                                          <div>
                                               <span>Library 추가</span>
                                               <p>KT의 SDK는 아래의 구성으로 이루어져 있으며 통합 패키지 형태 및 항목별 다운로드가 가능합니다.</p>
                                               <p><img src="<c:url value="/resources/images/guideimg/devguide0304.png" />" alt="별도 첨부"></p>
                                               <div class="hideText">
                                                   <dl>
                                                       <dt>1) Build Path창 오픈</dt>
                                                       <dd>[프로젝트] – [Build Path] – [Configure Build Path] 를 클릭하여 Properties 창을 오픈 합니다. </dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>2) User Library 창 오픈</dt>
                                                       <dd>[Add Library] 클릭 후 - [User Library] 클릭</dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>3) Jar파일 추가</dt>
                                                       <dd>SDK jar파일을 추가한 후 완료 합니다. </dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>4) 정상 등록 여부 확인</dt>
                                                       <dd>SDK jar파일을 추가한 후 완료 합니다. </dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>KT Open API SDK 이용가이드(TEST Console)</h5>
                                          <div>
                                               <span>TEST Console이용 가이드</span>
                                               <p><img src="<c:url value="/resources/images/guideimg/devguide0305.png" />" alt="별도 첨부"></p>
                                               <div class="hideText">
                                                   <dl>
                                                       <dt>1) 정상 등록 여부 확인</dt>
                                                       <dd>TEST Console을 다운 받은 후 사용이 가능하도록 exe파일로 변경합니다.  </dd>
                                                   </dl>
                                                   <dl>
                                                       <dt>2) 화면 설명</dt>
                                                       <dd>
                                                           <ol>
                                                               <li><strong>1. 연동 시스템명</strong>
                                                                   <span>1. Common  : 일반적인 SOAP, Restful에 대하여 범용적으로 사용.
                                                                   (플랫폼별 특이 케이스에 대한 안전성을 보장하지 않음)</span>
                                                                   <span>2. Shub : Shub의 특성을 반영하여 shub연동에 대한 안전성을 확보</span>
                                                                   <span>3. Iot : IotMakers의 특성을 반영하여 shub연동에 대한 안전성을 확보</span>
                                                               </li>
                                                               <li><strong>2. 연동 방식</strong>
                                                                   <span>SOAP과 Restful을 지원함 </span>
                                                               </li>
                                                               <li><strong>3. 연동 URL</strong>
                                                                   <span>각각의 플랫폼에서 제공한 EndPoint URL입력</span>
                                                               </li>
                                                               <li><strong>4. 입력 파라미터</strong>
                                                                   <span>API별 Request에 인자로 전달되는 값 입력</span>
                                                               </li>
                                                               <li><strong>5. Hearder값 입력</strong>
                                                                   <span>플랫폼에서 정의한 Hearder값 입력</span>
                                                               </li>
                                                               <li><strong>6. Variable 입력</strong>
                                                                   <span>API명, 인증 정보(SOAP)등의 기본정보 입력</span>
                                                               </li>
                                                               <li><strong>7. Output </strong>
                                                                   <span>연동 완료된 내용 확인이 가능함 </span>
                                                               </li>
                                                           </ol>
                                                       </dd>
                                                   </dl>
                                               </div>
                                          </div>
                                       </section>

                                       <section>
                                          <h5>KT Open API SDK 에 대하여</h5>
                                          <div>
                                               <p>SDK를 이용하여 연동을 진행할 경우 연동 DATA format은 XML, JSON, key-value 중 개발 환경과 맞는 형태로 입력 가능하며 API를 제공하는  플랫폼에 맞는
                                               format으로 변환되어 전송 됩니다. 아래 가이드는 실제 연동 이 이루어지는 과정을 shub API를 예를 들어 가이드 하여 사용자의 이해를 돕고 효율 적인 개발이 
                                               가능하도록 합니다. </p>
                                               <span>Requeset DATA Format</span>
                                               <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 입력 가능한 format에 대하여 설명 합니다.</p>
                                               <p>1) Request Base 규격</p>
                                               <div class="pkg_board">
                                                   <!-- table start -->
                                                   <table class="table-vw view_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col style="width:35%;">
                                                           <col style="width:30%;">
                                                           <col style="width:15%;">
                                                           <col style="width:30%;">
                                                       </colgroup>

                                                       <thead>
                                                           <tr>
                                                               <th scope="col"><div>파라미터명</div></th>
                                                               <th scope="col"><div>설명</div></th>
                                                               <th scope="col"><div>필수여부</div></th>
                                                               <th scope="col"><div>비고</div></th>
                                                           </tr>
                                                       </thead>

                                                       <tbody>
                                                           <tr>
                                                               <td><div>TRANSACTIONID</div></td>
                                                               <td><div>시스템 발급 일련번호</div></td>
                                                               <td><div>N</div></td>
                                                               <td><div>VOC 응대용도</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>SEQUENCENO</div></td>
                                                               <td><div>시스템 내부 구간순서</div></td>
                                                               <td><div>N</div></td>
                                                               <td><div>해당없음</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>USERID</div></td>
                                                               <td><div>특정CP가 사용하는 ID</div></td>
                                                               <td><div>N</div></td>
                                                               <td><div>해당없음</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>SCREENID</div></td>
                                                               <td><div>특정 CP가 사용되는 ID</div></td>
                                                               <td><div>N</div></td>
                                                               <td><div>해당없음</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>Credt_Id</div></td>
                                                               <td><div>Credential ID</div></td>
                                                               <td><div>Y</div></td>
                                                               <td><div>해당없음</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>User_Name</div></td>
                                                               <td><div>로그인 ID,</div></td>
                                                               <td><div>Y</div></td>
                                                               <td><div>해당없음</div></td>
                                                           </tr>
                                                           <tr>
                                                               <td><div>Subscpn_Type_Cd</div></td>
                                                               <td><div>계약 유형 코드</div></td>
                                                               <td><div>N</div></td>
                                                               <td><div>99일 경우 고객정보만 반환</div></td>
                                                           </tr>
                                                       </tbody>
                                                   </table>

                                                   <!-- // table End -->
                                               </div>

                                               <p>2) SDK이용시 입력 format</p>
                                               <div class="pkg_board">
                                                   <!-- table start -->
                                                   <table class="table-vw code_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col >
                                                       </colgroup>

                                                       <tbody>
                                                           <tr>
                                                               <th><div>JSON 포멧</div></th>                               
                                                           </tr>
                                                           <tr>
                                                               <td><pre>{"Credt_Id":"272833896","User_Name":"z!50674007900","Subscpn_Type_Cd":"2"}
   ☞ 호출 method => shubClient.setApiParamsJson(인자값)</pre></td>
                                                           </tr>
                                                           <tr>
                                                               <th><div>XML포맷</div></th>                               
                                                           </tr>
                                                           <tr>
                                                               <td><pre>&lt;Credt_Id&gt;272833896&lt;/Credt_Id&gt;
   &lt;User_Name&gt;z!50674007900&lt;/User_Name&gt;
   &lt;Subscpn_Type_Cd&gt;2&lt;/Subscpn_Type_Cd&gt;

   ☞ 호출 method = &gt; shubClient.setApiParamsXml(인자값)
   </pre></td>
                                                           </tr>
                                                           <tr>
                                                               <th><div>Key – Value</div></th>                               
                                                           </tr>
                                                           <tr>
                                                               <td><pre>setApiParam(“Credt_Id”) = “272833896”
   setApiParam(“User_Name”) = “z!50674007900”
   setApiParam(“Subscpn_Type_Cd”) = “2”

   ☞ 호출 method => setApiParam(“변수명”) = “인자값”
   </pre></td>
                                                           </tr>
                                                       </tbody>
                                                   </table>

                                                   <!-- // table End -->
                                               </div>

                                               <p>3) SDK 내에서 변환되어 SHUB로 전송 되는 포맷</p>
                                               <div class="pkg_board">
                                                   <!-- table start -->
                                                   <table class="table-vw code_style">
                                                       <caption>table Table</caption>
                                                       <colgroup>
                                                           <col >
                                                       </colgroup>

                                                       <tbody>
                                                           <tr>
                                                               <th><div>JSON 포멧</div></th>                               
                                                           </tr>
                                                           <tr>
                                                               <td><pre>&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
   &lt;soapenv:Envelope
       xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
       xmlns:sdp="http://kt.com/sdp"&gt;
       &lt;soapenv:Header&gt;
           &lt;wsse:Security soapenv:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"&gt;
               &lt;wsse:UsernameToken wsu:Id="UsernameToken-4"                
                                xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"&gt;
       &lt;wsse:Username&gt;AII5920037222QFXBRT&lt;/wsse:Username&gt;
                   &lt;wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText"&gt;
                   TBK5920037222QDYTUT&lt;/wsse:Password&gt;
               &lt;/wsse:UsernameToken&gt;
           &lt;/wsse:Security&gt;
       &lt;/soapenv:Header&gt;
       &lt;soapenv:Body&gt;
           &lt;sdp:getPartyAndSubInfoBySubTypeCDRequest&gt;
               &lt;sdp:Credt_Id&gt;272833896&lt;/sdp:Credt_Id&gt;
               &lt;sdp:SEQUENCENO&gt;&lt;/sdp:SEQUENCENO&gt;
               &lt;sdp:USERID&gt;&lt;/sdp:USERID&gt;
               &lt;sdp:SCREENID&gt;&lt;/sdp:SCREENID&gt;
               &lt;sdp:User_Name&gt;z!50674007900&lt;/sdp:User_Name&gt;
               &lt;sdp:Subscpn_Type_Cd&gt;2&lt;/sdp:Subscpn_Type_Cd&gt;
           &lt;/sdp:getPartyAndSubInfoBySubTypeCDRequest&gt;
       &lt;/soapenv:Body&gt;
   &lt;/soapenv:Envelope&gt;
   </pre></td>
                                                           </tr>
                                                       </tbody>
                                                   </table>
                                           </div>

                                           <span>Requeset DATA Format</span>
                                           <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 리턴되는 출력값에 대하여 설명합니다. </p>
                                           <div class="pkg_board">
                                               <!-- table start -->
                                               <table class="table-vw code_style">
                                                   <caption>table Table</caption>
                                                   <colgroup>
                                                       <col >
                                                   </colgroup>

                                                   <tbody>
                                                       <tr>
                                                           <th><div>JSON 포멧</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td><pre>-. Duration time: 1481 ms
   -. Response code: 200 (HTTP_OK)
   -. Header: 
      -> null : [HTTP/1.1 200 OK]
      -> Connection : [close]
      -> Content-Length : [1489]
      -> Date : [Mon, 18 Dec 2017 13:47:20 GMT]
   -. Response charset: UTF-8

   ☞ 호출 method => .getApiBody()
   </pre></td>
                                                       </tr>
                                                       <tr>
                                                           <th><div>Base Response Format</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td><pre>&lt;?xml version="1.0" encoding="utf-8"?&gt;
   &lt;env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:oas="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-
   secext-1.0.xsd"&gt;
   &lt;env:Header/&gt;
   &lt;env:Body&gt;
   &lt;sdp:getPartyAndSubInfoBySubTypeCDResponse xmlns:n1="http://kt.com/sdp_myolleh2" xmlns:sdp="http://kt.com/sdp"&gt;
   &lt;sdp:TRANSACTIONID&gt;1cc29a82-e288-44f7-XXXXXXXXXXXXXX&lt;/sdp:TRANSACTIONID&gt;
   &lt;sdp:SEQUENCENO&gt;9999&lt;/sdp:SEQUENCENO&gt;
   &lt;sdp:returnCode&gt;1&lt;/sdp:returnCode&gt;
   &lt;sdp:returnDesc&gt;Success&lt;/sdp:returnDesc&gt;
   &lt;sdp:ListofParty&gt;
     &lt;n1:arrayofparty&gt;
       &lt;n1:Party_Detail_Type_Cd&gt;01&lt;/n1:Party_Detail_Type_Cd&gt;
       &lt;n1:Birth_Date&gt;11111212&lt;/n1:Birth_Date&gt;
       &lt;n1:Party_Idtf_Number_Cd&gt;01&lt;/n1:Party_Idtf_Number_Cd&gt;
       &lt;n1:Party_Idtf_Number/&gt;&lt;n1:Customer_Class_Cd/&gt;
       &lt;n1:Ipin_CI&gt;PNxxxxxxxxxxxx&lt;/n1:Ipin_CI&gt;
       &lt;n1:ListofPartyMap&gt;
         &lt;n1:listofpartymap&gt;
           &lt;n1:Source_System_Cd&gt;01&lt;/n1:Source_System_Cd&gt;
           &lt;n1:Source_System_Bind_Id&gt;001111111C&lt;/n1:Source_System_Bind_Id&gt;
         &lt;/n1:listofpartymap&gt;
       &lt;n1:listofpartymap&gt;
         &lt;n1:Source_System_Cd&gt;04&lt;/n1:Source_System_Cd&gt;
         &lt;n1:Source_System_Bind_Id&gt;1111111111&lt;/n1:Source_System_Bind_Id&gt;
       &lt;/n1:listofpartymap&gt;
       &lt;n1:listofpartymap&gt;
         &lt;n1:Source_System_Cd&gt;20&lt;/n1:Source_System_Cd&gt;
         &lt;n1:Source_System_Bind_Id&gt;AAAAAAAA&lt;/n1:Source_System_Bind_Id&gt;
       &lt;/n1:listofpartymap&gt;
       &lt;/n1:ListofPartyMap&gt;
         &lt;n1:Subscpn_Over_Yn&gt;N&lt;/n1:Subscpn_Over_Yn&gt;
     &lt;/n1:arrayofparty&gt;
     &lt;/sdp:ListofParty&gt;
   &lt;/sdp:getPartyAndSubInfoBySubTypeCDResponse&gt;
   &lt;/env:Body&gt;
   &lt;/env:Envelope&gt;

   ☞ 호출 method => .getHttpR☞ 호출 method => .getApiBody()

   </pre></td>
                                                       </tr>
                                                       <tr>
                                                           <th><div>JSON Format</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td><pre>{
     "sdp:ListofParty": {"n1:arrayofparty": {
       "n1:Party_Idtf_Number_Cd": "01",
       "n1:Subscpn_Over_Yn": "N",
       "n1:Party_Detail_Type_Cd": "01",
       "n1:Party_Idtf_Number": "",
       "n1:Customer_Class_Cd": "",
       "n1:Ipin_CI": "PNxxxxxxxxxxxx",
       "n1:ListofPartyMap": {"n1:listofpartymap": [
         {
           "n1:Source_System_Cd": "01",
           "n1:Source_System_Bind_Id": "001111111C"
         },
         {
           "n1:Source_System_Cd": "04",
           "n1:Source_System_Bind_Id": "1111111111"
         },
         {
           "n1:Source_System_Cd": "20",
          "n1:Source_System_Bind_Id": "AAAAAAAA"
         }
       ]},
       "n1:Birth_Date": "19890126"
     }},
     "sdp:TRANSACTIONID": "1cc29a82-e288-44f7-XXXXXXXXXXXXXX",
     "sdp:SEQUENCENO": "9999",
     "sdp:returnDesc": "Success",
     "sdp:returnCode": "1"
   </pre></td>
                                                       </tr>
                                                       <tr>
                                                           <th><div>JSON Format</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td><pre>{sdp:ListofParty={n1:arrayofparty={n1:Party_Idtf_Number_Cd=01, n1:Subscpn_Over_Yn=N, 
   n1:Party_Detail_Type_Cd=01, n1:Party_Idtf_Number=, n1:Customer_Class_Cd=, 
   n1:Ipin_CI=fPN4SYWrn2tDceDKkOLa/RNX4aTOWmqNJFQGuvniGA4Xs+PUWlf0ypGWVsGkL4tW7YqeIX8BSP/rPrY7d
   mQ1LA==, n1:ListofPartyMap={n1:listofpartymap=[{n1:Source_System_Cd=01, 
   n1:Source_System_Bind_Id=0018742658C}, {n1:Source_System_Cd=04, n1:Source_System_Bind_Id=376811595}, 
   {n1:Source_System_Cd=20, n1:Source_System_Bind_Id=AF3KZX4ISO8}]}, n1:Birth_Date=19890126}}, 
   sdp:TRANSACTIONID=1cc29a82-e288-44f7-9574-365623dc01d3, sdp:SEQUENCENO=9999, 
   sdp:returnDesc=Success, sdp:returnCode=1}
   </pre></td>
                                                       </tr>
                                                   </tbody>
                                               </table>
                                           </div>

                                           <span>호출 함수 설명</span>
                                           <p>SHUB API 중 OIF_509(getBasicUserInfo) API를 SDK이용 연동시 리턴되는 출력값에 대하여 설명합니다. </p>
                                           <div class="pkg_board">
                                               <!-- table start -->
                                               <table class="table-vw code_style">
                                                   <caption>table Table</caption>
                                                   <colgroup>
                                                       <col style="width:20%" >
                                                       <col style="width:80%" >
                                                   </colgroup>

                                                   <tbody>
                                                       <tr>
                                                           <th colspan="2"><div>Request Method</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td>setApiUrl(String str)</td>
                                                           <td>API를 연동하는 EndPoint URL 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiMethod(String str)</td>
                                                           <td>Rest API연동시 입력하는 Method (get, put, delete, post) 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiName(String str)</td>
                                                           <td>Soap API연동시 API명 설정 method </td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiParamsJson(String json)</td>
                                                           <td>Json형태의 입력 파라미터 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiParamsXml(String xml)</td>
                                                           <td>xml형태의 입력 파라미터 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiParam(String str)</td>
                                                           <td>Key – value방식의 입력 파리미터 설정 method </td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiUserName(String str)</td>
                                                           <td>Id/pass형식의 SOAP API연동시 발급 받은 인증정보의 ID 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>setApiPassword(String str)</td>
                                                           <td>Id/pass형식의 SOAP API연동시 발급 받은 인증정보의 PASSWORD 설정 method</td>
                                                       </tr>
                                                       <tr>
                                                           <th colspan="2"><div>Request Method</div></th>                               
                                                       </tr>
                                                       <tr>
                                                           <td>sendSoapRequest()</td>
                                                           <td>SOAP전송 method</td>
                                                       </tr>
                                                       <tr>
                                                           <td>sendRestRequest()</td>
                                                           <td>REST전송 method</td>
                                                       </tr>
                                                   </tbody>
                                               </table>
                                           </div>
                                       </section>

                                   </div>
                                   <!-- content 끝 -->
                               </div>
							    <div id="apiInfo_sub" style="display: none;">
									<h3 class="mt50" id="notExistApi" style="position: relative; margin-bottom: 20px; font-size: 28px; color: #333333; font-weight: 700;"></h3>
								</div>
								<div id="apiInfo_search_result" style="display:none;">
									<input type="hidden" id="searchKeyword">
									<div id="apiInfo_search_result_list">
									
									</div>
                       				<div class="paging"  id="paging"></div>
								</div>
                           </div>
                           <!-- // api_right -->
                       </div>
                       <!-- // regist_layout -->
                   </div>
                   <!-- // regist_wrap -->
               </div>
		</div>
	</div>
</div>
</jsp:body>
</t:layout>
