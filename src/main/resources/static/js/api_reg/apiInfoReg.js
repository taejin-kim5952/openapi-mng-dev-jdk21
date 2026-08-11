/***** yaml 기본정보 저장관련 script *******/
// 탭관련 이벤트가 동작하지 않아 매소드로 재정의
var tabNum = 0;
function onTab(num){
    $('.tab_list2 div').removeClass('current');
    $('.tab-content').removeClass('current');
    $('#tab'     + num).addClass('current');
    $('#tabForm' + num).addClass('current');
    
    // 탭 클릭시 스키마 타입에 따라서 필드 활성화
    schemaTypeCng($('#tabForm' + num).find("select[name='type']")[0]);
}

var YAML = SwaggerParser.YAML;
var yamlSaveOb = new Object;
var consumesArray = new Array;
var producesArray = new Array;
var pathArray = new Array;  //--[i]not using
var schemeArray = new Array; 

yamlSaveOb.swagger = '2.0';
yamlSaveOb.info = {};

// host, basePath정보 저장
function baseInfoSave(data) {
  yamlSaveOb[data.name] = data.value;
}
// info정보 저장
function infoSave(myData) {
  if(myData.name == "title"){
    
    apiRegCheckStrLength(500,'infoTitle');
    
    $(".default_info").children("p").text(myData.value);
    $(".default_info").children("p").attr("title",myData.value);
    
    // 이름 중복 검사
    var param = {
        apiSpcNo:   $("#pApiSpcNo").val(),
          apiNm:    myData.value      
      };
    $.ajax({
      url    : selApiInfoNmDupleUrl, 
      type   : 'POST',
      data   : param,
      async  : false,
      cache  : false,
      success: function(data){
        if(data.duplYn == "Y"){
          if($(myData).parent().find(".def_txt").length == 0){
            $(myData).parent().append('<p class="def_txt">* 중복된API 이름이 존재합니다.</p>');
          }
        } else {
          $(myData).parent().find(".def_txt").remove();
        }
        },
      error:function(request,status,error){
        err_message(status, error);
        }
    });
  }
  yamlSaveOb.info[myData.name] = myData.value;
}
// info정보 저장-설명 부분
function infoTextAreaSave(data) {
  apiRegCheckStrLength(4000,'description');
  yamlSaveOb.info[data.name] = data.value;
}
// scheme정보 저장
function schemeSave(data) {
  if(schemeArray.contains(data.value)){
    schemeArray.splice(schemeArray.indexOf(data.value),1);
  } else {
    schemeArray.push(data.value);
  }
  yamlSaveOb.schemes = schemeArray;
}
// consumes정보 저장
function consumesSave(data){
  var num = jQuery.inArray( data.value, consumesArray, 0 );
  // 체크박스 클릭시 해당데이터가 없으면 추가 있으면 삭제
  if(num > -1){
    consumesArray.splice(num, num+1);
  } else {
    consumesArray.push(data.value);
  }
  // 체크박스 클릭시 해당데이터가 없으면 추가 있으면 삭제
  yamlSaveOb[data.name] = consumesArray;
}
// produces정보 저장
function producesSave(data){
  var num = jQuery.inArray( data.value, producesArray, 0 );
  // 체크박스 클릭시 해당데이터가 없으면 추가 있으면 삭제
  if(num > -1){
    producesArray.splice(num, num+1);
  } else {
    producesArray.push(data.value);
  }
  yamlSaveOb[data.name] = producesArray;
}

// 보안스키마 탭 추가 매소드
function securityTabAdd(data){
  tabNum = tabNum + 1;
  var formHtml = '';
  var tabHtml  = '';
  
  // 보안 스키마 탭 append 시작
  tabHtml =   '<div id="tab'+tabNum+'" data-tab="tab'+tabNum+'" >'+
            '<a href="javascript:;" title="basic" onclick="onTab('+tabNum+');"><span>basic</span></a><button type="button" title="삭제" class="btn btn_garbage" onclick="securityTabDel('+tabNum+');"><span>삭제</span></button>'+
            '</div>';
    $("#securityTab").append(tabHtml);
  // 보안 스키마 탭 append 끝
  
    $("#securityTabForm").find("textarea").parent().html('<textarea class="w90" style="min-height:95px;" id="account'+tabNum+'" name="account" onchange="apiRegCheckStrLength(4000,\'account'+tabNum+'\')"   onkeyup="apiRegCheckStrLength(4000,\'account'+tabNum+'\')" ></textarea>');
  // 보안 스키마 탭 form append 시작
  formHtml =  '<div id="tabForm'+tabNum+'" class="tab-content" data-tabNum="'+tabNum+'">'+
          $("#securityTabForm").html() +
        '</div>';
  $(".tab_wraping").append(formHtml);
  // 보안 스키마 탭 form append 끝
  
  
    // 탭관련 class current 수정
    onTab(tabNum);
}
// 보안스키마 탭 삭제 매소드
function securityTabDel(num){
  var nextSt = false;
  $('#tab'     + num).remove();
    $('#tabForm' + num).remove();
  $("#public_schema" + num).parent().parent().remove(); //공통 보안 스키마 삭제
    
    tabNum = num-1;

  $.each($("#securityTab").find("div"), function(index, item){
    if("tab"+tabNum == $(item).attr("id")){
      nextSt = true;
      return false;
    }
  });
    if(tabNum > 0 && nextSt == true){
      onTab(tabNum);
    }
}

// oauth2.0 일때 flow 타입 변경에 따른 uri 표출 변경
function flowChange(data){
  // Authorization grants이 속한 tbody 밑에 있는 Authorization URI, Access token URI의 css를 수정
  var parentPath = $(data).parent().parent().parent().parent(); //  해당 Authorization grants의 tbody 경로
  if(data.value == 'implicit'){
    parentPath.find("input[name='authUri']").prop("disabled",false);
    parentPath.find("input[name='accessUri']").prop("disabled",true);
  } else if(data.value == 'accessCode'){
    parentPath.find("input[name='authUri']").prop("disabled",false);
    parentPath.find("input[name='accessUri']").prop("disabled",false);
  } else {
    parentPath.find("input[name='authUri']").prop("disabled",true);
    parentPath.find("input[name='accessUri']").prop("disabled",false);
  }
}

// 보안 스키마 타입변경
function schemaTypeCng(data){
  if(data != undefined){
    // 보안 스키마 타입에 따라 필드 삭제
    if(data.value == 'basic'){
      $(".outhType").css("display", "none");
      $(".apiKeyType").css("display", "none");
    }
    else if(data.value == 'oauth2'){
      $(".outhType").css("display", "");
      $(".apiKeyType").css("display", "none");
    }
    else if(data.value == 'apiKey'){
      $(".outhType").css("display", "none");
      $(".apiKeyType").css("display", "");
    }
    /*
    if($(data).parent().parent().parent().parent().find("input[name='name']").val() == ""){
      $(data).parent().parent().parent().parent().find("input[name='name']").val(data.value);       
    }
    */
    globalSecurityCng($(data).parent().parent().parent().parent().find("input[name='name']")[0]);
  } else{
    onTab(tabNum);
  }
}

//보안스키마 이름 입력시에 공통보안 스키마 수정및 탭 이름 변경
function globalSecurityCng(data){
  // 보안 탭 이름 중복 검사
  var currentId = "tab" + $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().attr("data-tabnum");
  $.each($("#securityTab").find("div"), function(index, item){
    if(data.value == $(item).find("span").eq(0).text() && data.value != "" && currentId != $(item).attr("id")){
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("기본정보");
      $("#popupConfirm").find('#alertTxt').text("보안 스키마의 이름이 중복되었습니다.");
      $("#popupConfirm").dialog( "open" );
      var LastCnt = data.value.length - 1; 
      data.value = "";
      return false;
    }
  });
  
  var parentPath  = $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().parent(); //  해당 tab의 최상위 div 경로
  var data_tabNum = parentPath.attr("data-tabNum");
  // Validate that data_tabNum is digits only to prevent DOM XSS
   if (typeof data_tabNum !== 'string' || !/^\d+$/.test(data_tabNum)) {
     // Optionally, show an error to the user
     alert("탭 식별자가 올바르지 않습니다.");
     return; // abort the function if invalid
   }
  var type_name   = parentPath.find("select[name='type']").val();
  var schemaHtml  = '';
  //해당 이름으로 탭 이름 변경
  $("#tab"+data_tabNum).find("span").text(data.value);
  $("#tab"+data_tabNum).find("span").attr("title", data.value);

  $("#public_schema" + data_tabNum).parent().parent().remove(); //공통 데이터 추가 하기전 기존 데이터 삭제
  if(type_name == 'oauth2'){
	var escapedValue = escapeHtml(data.value);
	schemaHtml =  '<div>'+
                      '<a href="javascript:;">'+
                      //'<input type="checkbox" id="public_schema'+data_tabNum+'" name="golbalsecurity" title="'+data.value+'" onclick="scopeCng(this)" data-tabNum="'+data_tabNum+'" value="'+data.value+'">'+
					  '<input type="checkbox" id="public_schema'+data_tabNum+'" name="golbalsecurity" title="'+escapedValue+'" onclick="scopeCng(this)" data-tabNum="'+data_tabNum+'" value="'+escapedValue+'">'+
                      //'<label for="public_schema'+data_tabNum+'" title="'+data.value+'" ><span></span>'+data.value+'</label>'+
					  '<label for="public_schema'+data_tabNum+'" title="'+escapedValue+'" ><span></span>'+escapedValue+'</label>'+
                  '</a>'+
                  '<dl class="range_wrap">'+
                      '<dt>'+
                          '<label>범위</label>'+
                          '<select class="wx140" disabled onclick="scopeTextAdd(this)">'+
                              '<option value="">범위를 선택하여 주세요</option>'+
                          '</select>'+
                      '</dt>'+
                      '<dd>'+
                          '<ol>'+
                          '</ol>'+
                      '</dd>'+
                  '</dl>'+
              '</div>';
  } else {
	var escapedValue = escapeHtml(data.value);
    schemaHtml =  '<div>'+
                      '<a href="javascript:;">'+
                      //'<input type="checkbox" id="public_schema'+data_tabNum+'" name="golbalsecurity" title="'+data.value+'" value="'+data.value+'" onclick="onGlobalSchema(this);" >'+
					  '<input type="checkbox" id="public_schema'+data_tabNum+'" name="golbalsecurity" title="'+escapedValue+'" value="'+escapedValue+'" onclick="onGlobalSchema(this);" >'+
                      //'<label for="public_schema'+data_tabNum+'" title="'+data.value+'" ><span></span>'+data.value+'</label>'+
					  '<label for="public_schema'+data_tabNum+'" title="'+escapedValue+'" ><span></span>'+escapedValue+'</label>'+
                  '</a>'+
              '</div>';
  }
  $("#globalSecurity").append(schemaHtml);
}
// 공통 보안 스키마에서 oauth2.0타입 선택시에 범위데이터 조회
function scopeCng(data){
  var data_tabNum = $(data).attr("data-tabNum");
  var scopeOption = '<option value="">범위를 선택하여 주세요</option>';
  if($("#"+data.id).is(":checked")){
    $("input[name='noGlobalSchema']").prop("checked", false);
    $(data).parent().parent().find("select").prop("disabled", false);
    for(var i=0;i < $("#tabForm" + data_tabNum).find(".scopeTr").length;i++){
      var scoptName = $($("#tabForm" + data_tabNum).find(".scopeTr")[i]).find("input[name=scopeName]").val(); // sope 이름
      if(scoptName != ""){
        scopeOption = scopeOption + '<option value="' + scoptName + '">' + scoptName + '</option>';
      }
    }
    $(data).parent().parent().find("select").html(scopeOption);
  } else {
    $(data).parent().parent().find("select").prop("disabled", true);
  }
  
}
// 보안 스키마 - outh2.0일 경우 범위 입력시 하단에 범위 input 증가
function scopeAdd(data){
  var dupleYn = "N";
  $.each($(data).parent().parent().parent().parent().find("input[name='scopeName']"), function(index, item){
    if(data.value == $(item).val() && data != item){
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("기본정보");
      $("#popupConfirm").find('#alertTxt').text("범위의 이름이 중복되었습니다.");
      $("#popupConfirm").dialog( "open" );
      var LastCnt = data.value.length - 1; 
      data.value = "";
      $(data).focus();  
      return false;
    }
  });
  
  
  
  var scopeHtml = '<tr class="outhType scopeTr">'+
              '<th scope="row" class="at">'+
                  '<div>범위</div>'+
              '</th>'+
              '<td class="at" colspan="3"><div>'+
                  '<input type="text" name="scopeName"    title="범위 이름 입력" class="w25" placeholder="이름" onchange="scopeAdd(this)" style="margin-right: 5px;">'+
                  '<input type="text" name="scopeAccount" title="범위 설명 입력" class="w50" placeholder="설명" style="margin-right: 5px;">'+
              '</div></td>'+
          '</tr>';
  // 텍스트 입력시 다음 tr이 존재하면 append시키지 않는다.
  if($(data).parent().parent().parent().next().length == 0){
    if($(data).parent().find("button").length == 0){
      $(data).parent().append('<button type="button" title="삭제" class="btn btn_garbage" onclick="scopeRecodDel(this);"><span>삭제</span></button>');
    }
    $(data).parent().parent().parent().parent().append(scopeHtml);
  }
}
// 공통 보안 스키마 oauth 범위 선택
function scopeTextAdd(data){
  var scopeText = false;
  var scopeTextHtml = '';
  if(data.value != ""){
    if($(data).parent().parent().children().find("li").length == 0){
      scopeText = true;
    }
    for(var i=0;i < $(data).parent().parent().children().find("li").length;i++){
      if(data.value == $($(data).parent().parent().children().find("li")[i]).children("span").text()){
        scopeText = false;
        break;
      } else {
        scopeText = true;
      }
    }
    if(scopeText){
      scopeTextHtml = '<li><span>'+data.value+'</span><button type="button" title="삭제" class="btn btn_garbage" onclick="globalScopeTextDel(this)"><span>삭제</span></button></li>'
      $(data).parent().parent().find("ol").append(scopeTextHtml);
    }
  }
}
// 공통 보안 스키마 선택한 범위 삭제
function globalScopeTextDel(data){
  $(data).parent().remove();
}
// 보안 스키마 필드 삭제
function scopeRecodDel(data){
  $(data).parent().parent().parent().remove();
}
// 보안 스키마 내용 삭제
function scopeTextDel(data){
  $(data).parent().find("input[name='scopeName']").val("");
  $(data).parent().find("input[name='scopeAccount']").val("");
}

//-- [tag:job-20200420][add]
// 공통보안스키마/보안스키마 초기화
function init_securityDefinitions() {
  //--연동정보초기화
  tabNum = 0;
  consumesArray = new Array;
  producesArray = new Array;

  $("input[name='consumes']").prop("checked", false);
  $("input[name='produces']").prop("checked", false);

  $("input[name='noGlobalSchema']").prop("checked", false);

  //--타입초기화
  $("#globalSecurity").find('div:gt(0)').remove();

  //--공통보안스키마 checkbox초기화
  $('#securityTab').find('div').remove();
  //--보안스키마영역 초기화
  $('.tab_wraping').html('');
}

// 데이터 검사
function dataValidation(){
  var b_is_ARSENAL = ($("#systemId").val() == con_SYSTEMID_ARSENAL); //-- con_SYSTEMID_ARSENAL @regFormShareHead.jsp

  // 시스템 검사
  errCountCk($("select[name='systemId']"), "infoSystemId", true);
  // API 제목 검사
  errCountCk($("input[name='title']"), "infoTitle", true);

  //--[drm][chg]
  // 네임스페이스 변경 infoTermsOfService -> projectNamespace -- CYD
  if (true == b_is_ARSENAL) {
    // Namespace 검사
    errCountCk($("input[name='projectNamespace']"), "projectNamespace", true);
  }

  //-- [tag:adpt][drm][add]
  // API 구분
  errCountCk($("select[name='apiClass']"), "infoApiClass", true);
  // 권한 그룹 검사
  errCountCk($("select[name='authId']"), "infoAuthId", true);
  // 버전 검사
  errCountCk($("input[name='version']"), "infoVersion", true);
  // 버전 검사
  errCountCk($("input[name='host']"), "infoHost", true);
  // 기본경로 검사
  pathErrCountCk($("input[name='basePath']"), "infoBasePath", true);
  // 스키마 검사
  cboxErrCountCk($("input[name='schema']"), "infoSchema", true);
  // API검증 BASEURL 검사
  errCountCk($("input[name='apiVeriBaseurl']"), "infoApiVeriBaseurl", true);

  // 보안 스키마 이름 검사
  var inputNameEmpty = "N";
  $("#securityTab").parent().find("input[name='name']").each(function(index, item){
    if($(item).val() == ""){
      inputNameEmpty = "Y";
      return false;
    };
  });
  var tabErrCheck = jQuery.inArray("inputNameEmpty", errorText);
  if (inputNameEmpty == "Y" && tabErrCheck == -1) {
    errorNum = errorNum + 1;  $(".err_count").find("em").text(errorNum);
    errorText.push("inputNameEmpty");
  }
}