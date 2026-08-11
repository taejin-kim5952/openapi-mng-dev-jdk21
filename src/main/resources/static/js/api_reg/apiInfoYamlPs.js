function comment(a){
	// console.log("**********************************************" + a + "**********************************************");
}
//yaml 데이터 파서 - 기본정보 셋팅
function yamlParser(sData){
	var yamlData;
	var parser = new SwaggerParser();
	var sDataOb = YAML.parse(sData);
	var api = sDataOb;
	//-- [tag:job-20200420][cmt]
  //-- XLeftMenuSet(sDataOb['x-category']);
	yamlSaveOb = sDataOb;
	yamlData = api;
	// 기본정보 세팅
	// swagger 필수(openapi값 - string)
	$("input[name='swagger']").val(yamlData.swagger); 
	
	/**********************************************************************************************************/
	/*************************************** info 세팅 START **************************************************/
	// info 필수(기본정보 - object)
	// info 하위 값 - title, description, termsOfService, contact, license, version
	if(yamlData.info != undefined){
		$.each(yamlData.info, function (info, infoVal) {
			if(info == 'title'){
				$("input[name='title']").val(infoVal);
				$(".default_info").children("p").text(infoVal);
				$(".default_info").children("p").attr("title",infoVal);
				/*$("input[name='title']").trigger('change');*/
			}else if(info == 'description'){
				$("textarea[name='description']").val(infoVal);
				/*$("textarea[name='description']").trigger('change');*/
			}else if(info == 'termsOfService'){
				$("input[name='termsOfService']").val(infoVal); 
			}else if(info == 'contact'){ // contact (object) 하위 값 name, url, email
				$.each(infoVal, function (contact, contactVal) {
					if(contact == 'name'){$("input[name='contact_name']").val(contactVal);}
					else if(contact == 'url'){$("input[name='contact_url']").val(contactVal); }
					else if(contact == 'email'){$("input[name='contact_email']").val(contactVal);}
				});
			}else if(info == 'license'){ // license (object) 하위 값 name, url
				$.each(infoVal, function (license, licenseVal) {
					if(license == 'name'){$("input[name='license_name']").val(licenseVal);}
					else if(license == 'url'){$("input[name='license_url']").val(licenseVal);}
				});
			}else if(info == 'version'){
				$("input[name='version']").val(infoVal);
				/*$("input[name='version']").trigger('change');*/
			}
		});	
	}
	/***************************************  info 세팅 END  **************************************************/
    /**********************************************************************************************************/	
	
	// host (string)
	if(yamlData.host != undefined){
		$("input[name='host']").val(yamlData.host);
		/*$("input[name='host']").trigger('change');*/
	}
	// basePath (string)
	if(yamlData.basePath != undefined){
		$("input[name='basePath']").val(yamlData.basePath);
		/*$("input[name='basePath']").trigger('change');*/
	}
	// schemes (string[])
	if(yamlData.schemes != undefined){
		for(var i=0;i < yamlData.schemes.length; i++){
			$("input[name='schema']").each(function(index, value){
				if(yamlData.schemes[i] == value.value){
					this.checked = true;
					/*$("input[name='schema']").eq(i).trigger('change');*/
					//-- [tag:adpt][drm][add][variable at apiInfoReg.js]
					schemeArray.push(value.value);
				}
			});
		}
	}

	// api type
	if(yamlSaveOb['x-apitype'] != undefined){
		// console.log(yamlSaveOb['x-apitype']);
		$("#"+yamlSaveOb['x-apitype']).prop("checked", true)
	}
	
	// consumes (string[])
	if(yamlData.consumes != undefined){
		for(var i=0;i < yamlData.consumes.length; i++){
			$("input[name='consumes']").each(function(index, value){
				if(yamlData.consumes[i] == value.value){
					this.checked = true;
					/*$("input[name='consumes']").eq(i).trigger('change');*/
					//-- [tag:adpt][drm][add][variable at apiInfoReg.js]
					consumesArray.push(value.value);
				}
			});
		}
	}
	// produces (string[])
	if(yamlData.produces != undefined){
		for(var i=0;i < yamlData.produces.length; i++){
			$("input[name='produces']").each(function(index, value){
				if(yamlData.produces[i] == value.value){
					this.checked = true;
					/*$("input[name='produces']").eq(i).trigger('change');*/
					//-- [tag:adpt][drm][add][variable at apiInfoReg.js]
					producesArray.push(value.value);
				}
			});
		}
	}
	/**********************************************************************************************************/
	/******************************* securityDefinitions 세팅 START *******************************************/
	comment("securityDefinitions 정보 세팅 시작");
	if(yamlData.securityDefinitions != undefined){
		tabNum 	 = 0;
		var tabValSetNum  = 0;
		var securityTabHtml = '';
		var securityFormHtml = '';
		var scopeHtml = '';
		var currentClass = "";
		$.each(yamlData.securityDefinitions , function (index, info) {
			tabNum = tabNum + 1;
			if(tabNum == 1){
				currentClass = "current";
			} else {
				currentClass = "";
			}
			$("#securityTabForm").find("textarea").parent().html('<textarea class="w90" style="min-height:95px;" id="account'+tabNum+'" name="account" onchange="apiRegCheckStrLength(4000,\'account'+tabNum+'\')"   onkeyup="apiRegCheckStrLength(4000,\'account'+tabNum+'\')" ></textarea>');
			securityTabHtml = securityTabHtml + '<div id="tab'+tabNum+'" data-tab="tab'+tabNum+'" class="'+currentClass+'">'+
													'<a href="javascript:;" title="'+index+'" onclick="onTab('+tabNum+');"><span>'+index+'</span></a>'+
													'<button type="button" title="삭제" class="btn btn_garbage" onclick="securityTabDel('+tabNum+');">'+
													'<span>'+index+'</span>'+
													'</button>'+
												'</div>';
			securityFormHtml = securityFormHtml + 	'<div id="tabForm'+tabNum+'" class="tab-content '+currentClass+'" data-tabNum="'+tabNum+'">'+
									 					$("#securityTabForm").html() +
									 				'</div>';
		});
		// 탭 추가
		$("#securityTab").append(securityTabHtml);
		// 탭 폼 추가
		$("#securityTab").parent().find(".tab_wraping").append(securityFormHtml);
		
		//값 셋팅
		$.each(yamlData.securityDefinitions , function (index, info) {
			
			tabValSetNum = tabValSetNum + 1;
			
			$("#tabForm"+tabValSetNum).find("input[name='name']").val(index);
			$("#tabForm"+tabValSetNum).find("textarea[name='account']").val(info.description);
			$("#tabForm"+tabValSetNum).find("select[name='type']").val(info.type);
			
			if(info.type == "oauth2"){
				$("#tabForm"+tabValSetNum).find("select[name='authCd']").val(info.flow);
				
				if(info.flow == "implicit"){
					$("#tabForm"+tabValSetNum).find("input[name='authUri']").val(info.authorizationUrl);
				} else if(info.flow == "accessCode"){
					$("#tabForm"+tabValSetNum).find("input[name='authUri']").val(info.authorizationUrl);
					$("#tabForm"+tabValSetNum).find("input[name='accessUri']").val(info.tokenUrl);
				} else {
					$("#tabForm"+tabValSetNum).find("input[name='accessUri']").val(info.tokenUrl);
				}
				if(info.scopes != undefined){
					$.each(info.scopes , function (index, info) {
						scopeHtml =	'<tr class="outhType scopeTr" style="">'+
						                '<th scope="row" class="at">'+
						                    '<div>범위</div>'+
						                '</th>'+
						                '<td class="at" colspan="3"><div>'+
						                    '<input type="text" name="scopeName" title="범위 이름 입력" class="w25" placeholder="이름" onchange="scopeAdd(this)" value="'+index+'" style="margin-right: 5px;">'+
						                    '<input type="text" name="scopeAccount" title="범위 설명 입력" class="w50" placeholder="설명" value="'+info+'" style="margin-right: 5px;">'+
						                    '<button type="button" title="삭제" class="btn btn_garbage" onclick="scopeRecodDel(this);"><span>삭제</span></button>'+
						                '</div></td>'+
						            '</tr>';
						$("#tabForm"+tabValSetNum).find(".scopeTr").last().after(scopeHtml);
					});
					scopeHtml =	'<tr class="outhType scopeTr" style="">'+
					                '<th scope="row" class="at">'+
					                    '<div>범위</div>'+
					                '</th>'+
					                '<td class="at" colspan="3"><div>'+
					                    '<input type="text" name="scopeName" title="범위 이름 입력" class="w25" placeholder="이름" onchange="scopeAdd(this)" value="" style="margin-right: 5px;">'+
					                    '<input type="text" name="scopeAccount" title="범위 설명 입력" class="w50" placeholder="설명" value="" style="margin-right: 5px;">'+
					                '</div></td>'+
					            '</tr>';
					$("#tabForm"+tabValSetNum).find(".scopeTr").last().after(scopeHtml);
				}
				if($("#tabForm"+tabValSetNum).find(".scopeTr").length > 1 ){
					$("#tabForm"+tabValSetNum).find(".scopeTr").first().remove();
				}
			} else if(info.type == "apiKey"){
				$("#tabForm"+tabValSetNum).find("select[name='keyIn']").val(info.in);
			}
		});
		
		
	};
	comment("securityDefinitions 정보 세팅 끝");
	/******************************* securityDefinitions 세팅 END *********************************************/
	/**********************************************************************************************************/
	
	
	/**********************************************************************************************************/
	/************************************* security 세팅 START ************************************************/
	comment("security 정보 세팅 시작");
	if(yamlData.securityDefinitions != undefined){
		var securityHmlt = '';
		var securityNum = 0;
		var securityChecked = "";
		var securityDisabled = "";
		$.each(yamlData.securityDefinitions , function (index, info) {
			securityNum = securityNum + 1;
			securityHmlt = "";
			securityChecked = "";
			securityDisabled = "";
			securityScopeList = "";
			securityScopeArray = new Array();
			if(yamlData.security != undefined){
				$.each(yamlData.security , function (num, item) {
					if(item != undefined){
						$.each(item , function (num2, item2) {
							if(index == num2){
								securityChecked = "checked";
								securityDisabled = "";
								if(item2 != undefined){
									$.each(item2 , function (num3, item3) {
										securityScopeArray.push(item3);
									});
								}
							}
						});
					}
				});
			} else {
				$("input[name='noGlobalSchema']").prop("checked", true);
			}
			if(securityChecked != "checked"){
				securityDisabled = "disabled";
			}
			if(info.type == "oauth2"){
				var scopesOptionHtml = '';
				if(info.scopes != undefined){
                    $.each(info.scopes , function (scopeNum, scopeItem) {
                    	scopesOptionHtml = scopesOptionHtml + '<option value="'+scopeNum+'">'+scopeNum+'</option>';
                    });
				}
                if(securityScopeArray != undefined){
                    $.each(securityScopeArray , function (scopeNum, scopeItem) {
                    	securityScopeList = securityScopeList + '<li><span>'+scopeItem+'</span><button type="button" title="삭제" class="btn btn_garbage" onclick="globalScopeTextDel(this)"><span>삭제</span></button></li>';
                    });
                }
				securityHmlt = 		'<div>'+
						                '<a href="javascript:;">'+
								            '<input type="checkbox" id="public_schema'+securityNum+'" name="golbalsecurity" title="'+index+'" onclick="scopeCng(this)" data-tabNum="'+securityNum+'" value="'+index+'" '+securityChecked+'>'+
								            '<label for="public_schema'+securityNum+'" title="'+index+'" ><span></span>'+index+'</label>'+
								        '</a>'+
								        '<dl class="range_wrap">'+
								            '<dt>'+
								                '<label>범위</label>'+
								                '<select class="wx140" '+securityDisabled+' onclick="scopeTextAdd(this)">'+
								                    '<option value="">범위를 선택하여 주세요</option>'+
								                    scopesOptionHtml + 
								                '</select>'+
								            '</dt>'+
								            '<dd>'+
								                '<ol>'+
								                securityScopeList +
								                '</ol>'+
								            '</dd>'+
								        '</dl>'+
								    '</div>';
			} else {
				securityHmlt = 		'<div>'+
										'<a href="javascript:;">'+
											'<input type="checkbox" id="public_schema'+securityNum+'" name="golbalsecurity" title="'+index+'" value="'+index+'" '+securityChecked+' onclick="onGlobalSchema(this);">'+
											'<label for="public_schema'+securityNum+'" title="'+index+'" ><span></span>'+index+'</label>'+
										'</a>'+
									'</div>';
				
			}
			$("#globalSecurity").append(securityHmlt);
		});
	}
	comment("security 정보 세팅 끝");
	/************************************** security 세팅 END *************************************************/
	/**********************************************************************************************************//**********************************************************************************************************/
	/************************************* 데이터 타입 세팅 START ************************************************/
	comment("데이터 타입 정보 세팅 시작");
	if(yamlData.definitions != undefined){
		var dataTypeHtml = '';
		$(".dataType_wrap").find("ul").html("");
		$.each(yamlData.definitions, function( index, value ) {
			dataTypeHtml = dataTypeHtml + 	'<li>'+
								                '<a href="javascript:;" title="'+index+'" onclick="mvDataTypePage(\''+index+'\');" >'+index+'</a>'+
								                '<div class="option_set">'+
								                    '<button type="button" title="복사" class="btn btn_duplicate" onclick="mvDataTypePage(\''+index+'\');" ><span>복사</span></button>'+
								                    '<button type="button" title="버리기" class="btn btn_garbage" onclick="delDataType(\''+index+'\');" ><span>버리기</span></button>'+
								                '</div>'+
								            '</li>';
		});
		$(".dataType_wrap").find("ul").append(dataTypeHtml);
	}
	comment("데이터 타입 정보 세팅 끝");
	/************************************** 데이터 타입 세팅 END *************************************************/
	/**********************************************************************************************************/
	//onchang 순차적 실행
}