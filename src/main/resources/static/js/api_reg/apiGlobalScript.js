var YAML = SwaggerParser.YAML;
var yamlOb = YAML.parse($('#yamlSbst').val());
var errorText = []; //-- 오류 텍스트
var errorNum = 0; // 오류 갯수
// 페이지 이동시 수정 사항 체크 시작
var isChange = false;
//-- left api목록
var g_x_category = [];

//-- [for youtube] {
//--[drm][chg] {
//--[i]for delayed script insert
var g_is_youtube_script_inserted = false;
var g_fn_showApiMV = void(0);
if ('function' == typeof(showApiMV)) {  //-- showApiMV() @ui.js
  g_fn_showApiMV = showApiMV;
  showApiMV = (function(self, obj) {
    fn_insert_youtube_script();
    g_fn_showApiMV(self, obj);
  });
}
function fn_insert_youtube_script() {
  if (true == g_is_youtube_script_inserted) return;
  g_is_youtube_script_inserted = true;
  // youtube 영상 
  var tag = document.createElement('script');
  //--##[tag:adpt][chg] tag.src = 'https://www.youtube.com/iframe_api';
  tag.src = '//www.youtube.com/iframe_api';
  var firstScriptTag = document.getElementsByTagName('script')[0];
  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}
//--[drm][chg] }

var player;
var done = false;

//3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
function onYouTubeIframeAPIReady() {
  player = new YT.Player('mv_boxing', {
    height: '590',
    width: '1160',
    videoId: 'c9Dyapqkcnc',
    playerVars: { autoplay: 0, },
    events: {
      // 'onReady': onPlayerReady,
      // 'onStateChange': onPlayerStateChange
    }
  });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
  event.target.playVideo();
}

// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
function onPlayerStateChange(event) {
  if (event.data == YT.PlayerState.PLAYING && !done) {
    setTimeout(stopVideo, 6000);
    done = true;
  }
}
function stopVideo() {
  player.stopVideo();
}
//-- [for youtube] }

//Arsenal 내보내기 전역변수///////
var g_yamlPath    = "";             // YMAL파일 경로 (Gitlab서버에 올려진 YAML파일 Full Path)
var g_projectName = "";             // 프로젝트명 (POD명)
var g_bIsExsist   = false;          // YMAL 파일 동기화 플래그(Gitlab에 파일이 존재한다면 true)
var g_szProjectId = "";             // 프로젝트 ID
/////////////////////////////////

$(document).ready(function(){
  pageUpCheck();

  $('.mv_close, .dim_layer').click(function(){
    player.stopVideo();
  });

  if (yamlOb != undefined) {
    if (yamlOb['x-category'] == undefined && yamlOb['paths'] != undefined) {
      //-- [tag:20200913][chg]
      //-- [i][fixed] x-category rebuild
      yamlOb['x-category'] = fn_rebuild_x_category(yamlOb);
    }

    if (yamlOb['x-category'] != undefined) {
      //-- [tag:job-20200420][add]
      fn_rebuild_tags(yamlOb);

      // 레프트 메뉴 세팅
      XLeftMenuSet(yamlOb['x-category']);

      //-- [tag:adpt][drm][renew]
      var b_is_proc_check_path = false;
      var a_proc_url = '/apidev/api/reg/mvApiPathReg.do'.split(';');
      var b_is_proc_check_path = (a_proc_url.indexOf(location.pathname) != -1);
      var b_is_auto_delete = true;
      if (true == b_is_proc_check_path) {
        // path check with g_x_category[]
        var n_orphan_path_del_cnt = 0;
        var n_empty_path_del_cnt = 0;
        $.each(yamlOb['paths'], function(PIndex, PItem) {
          var b_is_delete = b_is_auto_delete;
          if (Object.keys(PItem).length == 0) { //-- method없는 path
            var path = PIndex;
            if (b_is_auto_delete == false) {
              var s_msg = '[path empty정보]\n\n[path:'+ path + ']';
              s_msg += '\n\n삭제 처리 하시겠습니까?';
              b_is_delete = confirm(s_msg);
            }
            if (b_is_delete == true) {
              delete yamlOb['paths'][path];
              n_empty_path_del_cnt++;
              $('#yamlSbst').val(YAML.stringify(yamlOb)); // yaml값 셋팅
            }
          }
          else {
            $.each(PItem, function(MIndex, MItem) {
              var path = PIndex;
              var method = MIndex;
              var apiNo = (MItem['x-apiNo']||'');
              var n_find_idx = g_x_category.findIndex(function(elem) {
                return ((path == elem['path']) && (method == elem['method']));
              });
              if (n_find_idx == -1) { //-- x-category에 없는 path + method
                if (b_is_auto_delete == false) {
                  var s_msg = '[path 목록 정보오류]\n\n[path:'+ path + ']\n[method:'+ method + ']\n[apiNo:'+ apiNo + ']';
                  s_msg += '\n\n삭제 처리 하시겠습니까?';
                  b_is_delete = confirm(s_msg);
                }
                if (b_is_delete == true) {
                  delete yamlOb['paths'][path][method];
                  if (Object.keys(yamlOb['paths'][path]).length == 0) {
                    delete yamlOb['paths'][path];
                  }
                  n_orphan_path_del_cnt++;
                  $('#yamlSbst').val(YAML.stringify(yamlOb)); // yaml값 셋팅
                }
              }
            });
          }
        });
        if ((n_orphan_path_del_cnt + n_empty_path_del_cnt) > 0) {
          //-- [drm][ee]
          var jq_elem = $('.api_left').find('.dataType_wrap');
          if (n_orphan_path_del_cnt > 0) {
            jq_elem.after('<div style="color:white;">' + '[path 목록 정보오류 삭제: ' + n_orphan_path_del_cnt + '건]' + '</div>');
          }
          if (n_empty_path_del_cnt > 0) {
            jq_elem.after('<div style="color:white;">' + '[path empty정보 삭제: ' + n_empty_path_del_cnt + '건]' + '</div>');
          }
        }
      }
    } //-- if(yamlOb['x-category'] != undefined){

    $('#yamlSbst').val(YAML.stringify(yamlOb));

  } //-- if (yamlOb != undefined) {

  //-- [tag:job-20200420][add]
  fn_ui_set_share_layout(); //-- 상황별공통영역 ui설정
});

//-- [tag:job-20200420][add]
//-- [i][dependent: regFormShareHead.jsp]
//-- 상황별공통영역 ui설정
function fn_ui_set_share_layout(ext_param) {
  ext_param = (ext_param||{});
  var sysId = (ext_param['systemId']||'');  //-- systemId가 주어질경우
  sysId = ((sysId.length > 0) ? sysId : g_sysId);

  var b_is_spc_loaded = ((g_sysId||'').length > 0);
  var b_is_ARSENAL = (sysId == con_SYSTEMID_ARSENAL); //-- con_SYSTEMID_ARSENAL @regFormShareHead.jsp
  $('.cid_arsenal_disabled').prop('disabled', b_is_ARSENAL);

  $('.cid_arsenal_hidden').removeClass('disp_none');  //--.show();
  $('.cid_arsenal_show').addClass('disp_none') //--.hide();
  if (true == b_is_ARSENAL) {
    $('.cid_arsenal_hidden').addClass('disp_none') //--.hide();
    $('.cid_arsenal_show').removeClass('disp_none');  //--.show();
  }

  $('.cid_spc_loaded_show').addClass('disp_none');  //--.hide();
  if (true == b_is_spc_loaded) {
    $('.cid_spc_loaded_show').removeClass('disp_none'); //--.show();
  }
}

//페이지 이동이 있을경우
$(window).on('beforeunload', function() {
  //데이터 변경이 있을경우
  if (isChange) {
    return '이 페이지를 벗어나면 작성된 내용은 저장되지 않습니다.';
  } 
});

function pageUpCheck(){
  //input, select에 change event가 일어날 경우
  $('input, select').change(function(){
    isChange = true;
  });
  $('textarea').change(function(){
    isChange = true;
  });
}

//페이지 이동시 수정 사항 체크 끝
// API그룹 삭제하기
function categoryDel(cateNm){
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }
  
  //-- API그룹(삭제)
  if (fn_check_regform_action() == false) {
    return false;
  }

  var category = yamlOb['x-category'][cateNm];
  if (Object.keys(category||{}).length > 0) {
    alert_message('API가 등록된 API그룹은 삭제할 수 없습니다.', 'API 등록');
    return false;
  }

  var btnHtm = '';
  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="categoryDelStart(\''+cateNm+'\')">확인</button> ';
  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
  fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');
}

function categoryDelStart(cateNm) {
  var cateNo = selCateNo(cateNm);
  delete yamlOb['x-category'][cateNm]; // API그룹 정보 삭제
  //-- [tag:job-20200420][add]
  fn_delete_tags(yamlOb, cateNm);

  $('#yamlSbst').val(YAML.stringify(yamlOb));
  var param = new Object();
  param.apiSpcNo = $('#pApiSpcNo').val();
  param.apiCtgryNo = cateNo; // API그룹 번호 필수
  param.yamlStr = $('#yamlSbst').val();
  $.ajax({
    url    : delCategoryUrl , 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      // console.log('API그룹 삭제 리턴값', data);
      alert_message('삭제 되었습니다.');
      if(cateNo == $('#pApiCtgryNo').val()){
        mvPage('info');
      } else {
        // leftMenuSet(data.cateInfo.catePathList, data.apiRegVO); // 레프프 메뉴 셋팅
        // 레프트 메뉴 세팅
        XLeftMenuSet(yamlOb['x-category']);
      }
    },
    error:function(request,status,error){
      err_message(status, error);
    }
  });
}
//레프트 메뉴 셋팅
/*
function leftMenuSet(data, regVo){
  var leftOneHtml = '';
  var leftTwoDeptHtml = '';
  var leftThreDeptHtml = '';
  if(data != null){
    $.each(data, function(index, item){
      if(item.sortOrder == 1){
        var leftCtgryNo = item.apiCtgryNo;
        var leftCtgryNm = item.ctgryNm;
        var cateActive  = '';
        var cateActiveStyle = '';
        var pathActive  = '';
        var pathActiveStyle = ''; 
        
        if(item.apiCtgryNo == $('#pApiCtgryNo').val()){
          cateActive    = 'active act';
          cateActiveStyle = 'style="display: block;"';
        } else {
          cateActive    = '';
          cateActiveStyle = '';
        }
        leftTwoDeptHtml = ''; //초기화
        $.each(data, function(index2, item2){
                  if(item2.sortOrder == 2 && item2.apiCtgryNo == leftCtgryNo){
            var leftBaseUri = item2.apiPath;
        
            leftThreDeptHtml = ''; //초기화
            $.each(data, function(index3, item3){
                        if(item3.sortOrder == 3 && item3.apiCtgryNo == leftCtgryNo && item3.apiPath == leftBaseUri){
                          leftThreDeptHtml = leftThreDeptHtml +   '<li>'+
                                                          '<div>'+
                                                              '<span class="tag_'+item3.methodNm.toLowerCase()+'" onclick="mvMethodInfo(\''+item3.methodNm+'\', \''+leftCtgryNo+'\', \''+item3.apiPath+'\', \''+leftCtgryNm+'\', \''+item3.apiNo+'\')" >'+item3.methodNm+'</span>'+
                                                              '<em onclick="mvMethodInfo(\''+item3.methodNm+'\', \''+leftCtgryNo+'\', \''+item3.apiPath+'\', \''+leftCtgryNm+'\', \''+item3.apiNo+'\')" title="'+item3.apiNm+'" >'+item3.apiNm+'</em>'+
                                                              '<div class="option_set">'+
                                                                  '<button type="button" title="API 복사" class="btn btn_duplicate" onclick="mvMethodInfoCopy(\''+item3.methodNm+'\', \''+leftCtgryNo+'\', \''+item3.apiPath+'\', \''+leftCtgryNm+'\', \'Y\')" ><span>복사</span></button>'+
                                                                  '<button type="button" title="API 버리기" class="btn btn_garbage" onclick="delApiMethod(\''+item3.apiPath+'\', \''+item3.methodNm+'\', \''+item3.apiNo+'\')" ><span>버리기</span></button>'+
                                                              '</div>'+
                                                          '</div>'+
                                                      '</li>';
                        };
                      });
            pathActive    = '';
            pathActiveStyle = '';
            if(item2.apiPath == $('#pApiPath').val()){
              pathActive = 'active';
              pathActiveStyle = 'style="display: block;"';
            }
            leftTwoDeptHtml = leftTwoDeptHtml + '<li>'+
                                                '<div class="">'+
                                                    '<a class="acco_act '+pathActive+' " href="javascript:;"></a>'+
                                                    '<span>'+item2.apiPath+'</span>'+
                                                    '<div class="option_set">'+
                                                    '<button type="button" title="API 버리기" class="btn btn_garbage" onclick="delPathAll(\''+item2.apiNo+'\')"><span>API 버리기</span></button>'+
                                                        '<button type="button" title="API 생성" class="btn btn_create2" onclick="addMethodOpen(this, \''+item2.apiCtgryNo+'\', \''+item2.apiPath+'\', \''+leftCtgryNm+'\');"><span>생성</span></button>'+
                                                    '</div>'+
                                                '</div>'+
                                                '<div class="hidden_div depth3" '+pathActiveStyle+'> <!-- style="display:block;" -->'+
                                                    '<ul class="handler_bar">'+
                                                        '<!-- API 정보 -->'   +
                                                        leftThreDeptHtml      + 
                                                    '</ul>'+
                                                '</div>'+ 
                                          '</li>';
                  };
                });
        leftOneHtml = leftOneHtml + '<li data-cateNum="'+item.apiCtgryNo+'">'+
                              '<div class="opener '+cateActive+'">'+
                                  '<a class="acco_act '+cateActive+'" href="javascript:;"></a>'+
                                  '<span onclick="cateInfo(\''+item.apiCtgryNo+'\', \''+item.ctgryNm+'\')">'+item.ctgryNm+'</span>'+
                                  '<div class="option_set">'+
                                      '<button type="button" title="API그룹 버리기" class="btn btn_garbage"><span>버리기</span></button>'+
                                      '<button type="button" title="API그룹 생성" class="btn btn_create" onclick="addApi(\''+item.apiCtgryNo+'\', \''+leftCtgryNm+'\')"><span>생성</span></button>'+
                                  '</div>'+
                              '</div>'+
                              '<div class="hidden_div" '+cateActiveStyle+'> <!-- style="display:block;" -->'+
                                  '<ul class="acco_opened div_draging">'+
                                      '<!-- API 정보 -->'+
                                      leftTwoDeptHtml    +
                                '</ul>'+
                              '</div>'+ 
                          '</li>';
      }
    });
    $('#leftList').html(leftOneHtml);
    leftDrop();
  };
}
*/
//레프트 메뉴 셋팅(최상단 x-category 기준)
function XLeftMenuSet(data){
  var leftOneHtml = '';
  var leftTwoDeptHtml = '';
  var leftThreDeptHtml = '';
  var cateActive  = '';
  var catePointActive  = '';
  var cateActiveStyle = '';
  var pathActive  = '';
  var pathActiveStyle = ''; 

  g_x_category = [];

  if (data != null) {
    $.each(data, function(index, item){
      leftTwoDeptHtml = '';
      if(index == $('#pApiCtgryNm').val()){
        cateActive = 'active act';
        catePointActive = 'active';
        cateActiveStyle = 'style="display: block;"';
      }
      else {
        cateActive = '';
        catePointActive = '';
        cateActiveStyle = '';
      }

      $.each(item, function(PIndex, PItem){
        leftThreDeptHtml = '';
        $.each(PItem, function(MIndex, MItem){
          g_x_category.push({
            'api_group': index,
            'api_nm': MItem.apiNm,
            'api_no': MItem.apiNo,
            'path': PIndex,
            'method': MIndex,
          });
          leftThreDeptHtml = leftThreDeptHtml +
            '<li>' +
            '  <div>' +
            '    <span class="tag_' + MIndex+'" onclick="mvMethodInfo(\'' + MIndex+'\', \'' + PIndex+'\', \'' + index+'\', \'' + MItem.apiNo+'\')">' + MIndex + '</span>' +
            '    <em onclick="mvMethodInfo(\'' + MIndex + '\', \'' + PIndex + '\', \'' + index + '\', \'' + MItem.apiNo + '\')" title="' + MItem.apiNm + '" >' + MItem.apiNm + '</em>' +
            '    <div class="option_set">' +
            '      <button type="button" title="API 복사" class="btn btn_duplicate" onclick="mvMethodInfoCopy(\'' + MIndex + '\', \'' + PIndex + '\', \'' + index + '\', \'Y\', \'' + MItem.apiNo + '\')" ><span>복사</span></button>' +
            '      <button type="button" title="API Method 버리기" class="btn btn_garbage" onclick="delApiMethod(\'' + index + '\', \'' + PIndex + '\', \'' + MIndex + '\', \'' + MItem.apiNo + '\')" ><span>버리기</span></button>' +
            '    </div>'+
            '  </div>'+
            '</li>';
        });
        pathActive = '';
        pathActiveStyle = '';
        if(PIndex == $('#pApiPath').val()){
          pathActive = 'active';
          pathActiveStyle = 'style="display: block;"';
        }
        else {
          pathActive = '';
          pathActiveStyle = '';
        }
        leftTwoDeptHtml = leftTwoDeptHtml +
          '<li>' +
          '  <div class="">' +
          '    <a class="acco_act ' + pathActive + '" href="javascript:;"></a>' +
          '    <span title="'  +  PIndex  +  '">' + PIndex + '</span>' +
          '    <div class="option_set">' +
          '      <button type="button" title="API 버리기" class="btn btn_garbage" onclick="delPathAll(\'' + index + '\', \'' + PIndex + '\')"><span>API 버리기</span></button>' +
          '      <button type="button" title="API 생성" class="btn btn_create2" onclick="addMethodOpen(this, \'' + PIndex + '\', \'' + index + '\');"><span>생성</span></button>' +
          '    </div>' +
          '  </div>' +
          '  <div class="hidden_div depth3" ' + pathActiveStyle  +  '>' +
          '    <ul class="handler_bar">' +  leftThreDeptHtml  +  '</ul>' +
          '  </div>'+ 
          '</li>';
      });
      var delHtml = '';
      //-- [tag:20200617]
      //--##if (!jQuery.isEmptyObject(item)) {
      if ($.isEmptyObject(item)) {
        delHtml = '<button type="button" title="API그룹 버리기" class="btn btn_garbage"  onclick="categoryDel(\'' + index + '\')" ><span>버리기</span></button>';
      }
      leftOneHtml = leftOneHtml +
        '<li>' +
        '  <div class="opener ' + cateActive + ' ">' +
        '    <a class="acco_act ' + catePointActive + '" href="javascript:;"></a>' +
        '    <span onclick="cateInfo(\'' + index + '\')">' + index + '</span>' +
        '    <div class="option_set">' +  delHtml  +
        '      <button type="button" title="API 추가" class="btn btn_create" onclick="addApi(\'' + index + '\')"><span>API 추가</span></button>' +
        '    </div>' +
        '  </div>' +
        '  <div class="hidden_div"  ' + cateActiveStyle + '>' +
        '    <ul class="acco_opened div_draging">' +  leftTwoDeptHtml  +  '</ul>' +
        '  </div>' + 
        '</li>';
    });
    var b_api_search = ('function' == typeof(fn_on_click_searchApi));
    if (true == b_api_search) {
      var jq_base = $('#leftList').parent();
      var jq_api_search = jq_base.siblings('cid_api_search');
      if (jq_api_search.length == 0) {
        var apiSearchHtml = '<div class="cid_api_search api_search">' +
          '<div class="btn_wrap"><button type="button" title="API검색" onclick="fn_on_click_searchApi()" class="btn btn_api_search"><span>API검색</span></button></div>' +
          '</div>';
        $(apiSearchHtml).insertBefore(jq_base);
      }
    }
    $('#leftList').html(leftOneHtml);
    leftDrop();
  };
}


// 레프트 메뉴 데이터 타입 세팅
function dataTypeSet() {
  if(yamlOb.definitions != undefined){
    var dataTypeHtml = '';
    $('.dataType_wrap').find('ul').html('');
    $.each(yamlOb.definitions, function( index, value ) {
      dataTypeHtml = dataTypeHtml +   '<li>'+
                                '<a href="javascript:void(0)" title="'+index+'" onclick="mvDataTypePage(\''+index+'\');">'+index+'</a>'+
                                '<div class="option_set">'+
                                    '<button type="button" title="datatype 복사" class="btn btn_duplicate" onclick="mvDataTypePage(\''+index+'\');" ><span>datatype 복사</span></button>'+
                                    '<button type="button" title="datatype 버리기" class="btn btn_garbage" onclick="delDataType(\''+index+'\');" ><span>datatype 버리기</span></button>'+
                                '</div>'+
                            '</li>';
    });
    $('.dataType_wrap').find('ul').append(dataTypeHtml);
  }
}
// 데이터 타입 삭제
function delDataType(datatypeNm){
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }

  //-- DATA TYPE(삭제)
  if (fn_check_regform_action() == false) {
    return false;
  }

  if(dataInfoOb.indexOf('resDesc=' + datatypeNm + ',') > -1 || dataInfoOb.indexOf('"resDesc":"' + datatypeNm + '",') > -1){
    alert_message('파라미터로 등록된 DATA TYPE은 삭제할 수 없습니다.', 'API 등록');
    return false;
  }

  var btnHtm = '';
  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="datatypeDelStart(\''+datatypeNm+'\')">확인</button> ';
  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
  fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');
}

function datatypeDelStart(datatypeNm) {
  delete yamlOb.definitions[datatypeNm]; // json 에서 API그룹 삭제
  $('#yamlSbst').val(YAML.stringify(yamlOb));
  var param = new Object();
  param.apiSpcNo = $('#pApiSpcNo').val();
  param.yamlStr = $('#yamlSbst').val();
  param.saveType = 'del'; // del 삭제 플래그
  $.ajax({
    url    : delDatatypeUrl, 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      alert_message('삭제 되었습니다.');
      if(datatypeNm == $('#pApiDataTypeNm').val()){
        mvPage('info');
      }
      else {
        dataTypeSet(); // 데이터 타입 셋팅
      }
    },
    error:function(request,status,error){
      err_message(status, error);
      }
  });
}
// 레프트 메뉴 내려가능 기능 다시 한번 호출
function leftDrop(){
  $('.acco_opened > li a.acco_act, .acco_opened > li > div > span').off('click');
  // acco_opened - no Toggle
  $('.acco_opened > li a.acco_act, .acco_opened > li > div > span').on('click', function(e) {
    if($(this).parent().has('ul')) {
      e.preventDefault();
    } 

    // multi open script
    if(!$(this).hasClass('active')) {
      // open our new menu and add the open class
      $(this).parent().next().slideDown(350);
      $(this).parent('.opener').addClass('active');
      $(this).addClass('active');    
    }
    else if($(this).hasClass('active')) {
      $(this).parent('.opener').removeClass('active');
      $(this).removeClass('active');
      $(this).parent().next().slideUp(350);
    }
  });
}

// 드래그앤 드랍
function dragDrop() {
    // div_draging 사용시에 드래그앤 드랍 사용 가능
    $('.div_draging').sortable({
      handle: '.handler_bar',
      update: function(event,ui){ }
    }).disableSelection();
}
// 페이지 이동
function mvPage(type) {
  var apiSpcNo = $('#pApiSpcNo').val();

  if(apiSpcNo == undefined || apiSpcNo == ''){
    alert_message('기본 정보 등록 후 이동 가능합니다.');
    return false;
  }
  //-- API그룹/API등록/DATATYPE등록
  if ((type == 'cate') || (type == 'api') || (type == 'dataType')) {
    if (fn_check_regform_action() == false) {
      return false;
    }
  }

  $('#pApiNo, #pApiMethod, #pApiPath, #pApiDataTypeNm').val('');
  // 기본정보등록 페이지로 이동
  if (type == 'info') {
    $('#pApiCtgryNo, #pApiCtgryNm').val('');
    $('#apiInfoForm').attr('action', mvApiInfoUrl);
  }
  // API그룹 페이지로 이동
  else if (type == 'cate') {
    $('#pApiCtgryNo, #pApiCtgryNm').val('');
    $('#apiInfoForm').attr('action', mvCategoryUrl);
  }
  // DATATYPE등록 페이지로 이동
  else if (type == 'dataType') {
    $('#pApiCtgryNo, #pApiCtgryNm').val('');
    $('#apiInfoForm').attr('action', mvDatatypeUrl);
  }
  // API등록 페이지로 이동
  else if (type == 'api') {
    if($('#pApiCtgryNm').val() == '' || $('#pApiCtgryNo').val() == ''){
      $('#pApiCtgryNo').val($('#leftList').find('li').first().attr('data-catenum'));
      $('#pApiCtgryNm').val($('#leftList').find('li').first().find('span').first().text());
    }
    $('#apiInfoForm').attr('action', mvApiPathUrl);
  }
  $('#apiInfoForm').submit();
}

// 선택한 데이터 타입 수정하기로 이동
function mvDataTypePage(apiDataTypeNm){
  // apino 여부 체크
  if(!mvWarning('mv')){
    return false;
  }
  
  $('#pApiDataTypeNm').val(apiDataTypeNm);
  $('#apiInfoForm').attr('action', mvDatatypeUrl);
  $('#apiInfoForm').submit();
}

// 선택한 메소드로 이동
function mvMethodInfo(method, path, cateNm, apiNo){
  // apino 여부 체크
  if (!mvWarning('call')) {
    return false;
  }

  /* API그룹 번호 조회 시작 */
  var cateNo = selCateNo(cateNm);
  /* API그룹 번호 조회 끝*/
  if(apiNo == 0){
    mvMethodInfoCopy(method, path, cateNm, 'Y', apiNo);
  }
  else {
    $('#pApiNo').val(apiNo);
    $('#pApiCtgryNo').val(cateNo);
    $('#pApiCtgryNm').val(cateNm);
    $('#pApiPath').val(path);
    $('#pApiMethod').val(method);
    $('#pApiCopyYn').val('N');
    $('#apiInfoForm').attr('action', mvApiPathUrl).submit();
  }
}

//선택한 메소드 카피 이동
function mvMethodInfoCopy(method, path, cateNm, copyYn, apiNo) {
  // apino 여부 체크
  if (!mvWarning('call')) { return false; }
  
  //-- API(복사)
  if (fn_check_regform_action() == false) {
    return false;
  }

  if (copyYn != 'A') {  //-- 신규method mode가 아니면
    if ('function' == typeof(fn_popup_apiclone_open)) { //-- function @popApiClone.jsp 
      var is_version_in_path = fn_is_version_in_path(path);
      var mode = ((is_version_in_path == false) ? 'copy_only' : 'copy_verup');
      fn_popup_apiclone_open(mode, method, path, cateNm, apiNo);
      return;
    }
  }
  
  var cateNo = selCateNo(cateNm);
  $('#pApiNo').val('');
  $('#pApiCtgryNo').val(cateNo);
  $('#pApiCtgryNm').val(cateNm);
  $('#pApiPath').val(path);
  $('#pApiMethod').val(method);
  $('#pApiCopyYn').val(copyYn);
  $('#apiInfoForm').attr('action', mvApiPathUrl).submit();
}

// 앞자리만 대문자로 변경 이벤트
function lowString(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// 패스 생성클릭시에 등록가능한 메소드 오픈하는 기능
function addMethodOpen(data, path, ctgryNm){
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }

  //-- API(생성)
  if (fn_check_regform_action() == false) {
    return false;
  }

  var methodText = '';
  var optionHtml = '';
  var methodArray = ['get','post','put','delete','head','options','patch'];
  var cateNo = selCateNo(ctgryNm);
  
  // console.log(yamlOb['x-category'][ctgryNm][path]);
  for (var key in yamlOb['x-category'][ctgryNm][path]) { //오브젝트 obj
    // console.log(key);
    methodArray.splice(methodArray.indexOf(key),1); 
  }
  $.each(methodArray, function(index, item){
    optionHtml = optionHtml + '<li><button type="button" title="'+item+'" onclick="mvMethodInfoCopy(\''+item+'\', \''+path+'\', \''+ctgryNm+'\', \'A\');"><span>'+item.toUpperCase()+'</span></button></li>';
  });
  
  $('.quickmenu').html(optionHtml);
  
  var cre_posTop = ($(data).offset().top) - 10 ;
    var cre_posLeft = ($(data).offset().left) + 30 ;
     
    $('.quickmenu').fadeIn(150);
    $('.quickmenu').offset({top: cre_posTop, left: cre_posLeft});
    
}

// api 생성버튼
function addApi(cateNm){
  //--@@console.log("addApi() 실행");
   
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }
  
  //-- API(추가)
  if (fn_check_regform_action() == false) {
    return false;
  }

  var cateNo = selCateNo(cateNm);
  $('#apiInfoForm').attr('action', mvApiPathUrl);
  $('#pApiCtgryNo').val(cateNo);
  $('#pApiCtgryNm').val(cateNm);
  //pApiCopyYn값을 빈값으로 셋팅 추가 (재식)
  //[기존 소스]
  //$('#pApiNo, #pApiMethod, #pApiPath, #pApiDataTypeNm').val('');
  $('#pApiNo, #pApiMethod, #pApiPath, #pApiDataTypeNm, #pApiCopyYn').val('');
  $('#apiInfoForm').submit();
}

// API그룹 클릭시에 API그룹 페이지로 이동
function cateInfo(cateNm){
  if(!mvWarning('mv')){
    return false;
  }
  
  var cateNo = selCateNo(cateNm);
  $('#pApiNo, #pApiMethod, #pApiPath, #pApiDataTypeNm').val('');
  $('#pApiCtgryNo').val(cateNo);
  $('#pApiCtgryNm').val(cateNm);
  $('#apiInfoForm').attr('action', mvCategoryUrl);
  $('#apiInfoForm').submit();
}

// 기본정보로 이동 버튼
function nextInfoPage() {
  $('#pApiNo, #pApiMethod, #pApiPath, #pApiDataTypeNm').val('');
  var apiSpcNo = $('#pApiSpcNo').val();
  $('#apiInfoForm').attr('action', mvApiInfoUrl);
  $('#apiInfoForm').submit();
}
// yaml 테스트 버튼 (클릭시 팝업창으로 연결) 
/**
 * userId : 유저 ID
 * import : yaml 저장 경로
 * */
function yamlEditorOpen(userId, apiSpcNo, sessionKey, ext_param) {
  // API 권한 체크 CYD - 2020.7.15
  if(!fnApiAuthCheck()) return;

  var ext_param = ((typeof(ext_param) == 'object') ? ext_param : {});
  
  var sysId = (ext_param['systemId']||'');  //-- systemId가 주어질경우
  sysId = ((sysId.length > 0) ? sysId : g_sysId);
  var b_is_ARSENAL = (sysId == con_SYSTEMID_ARSENAL); //-- con_SYSTEMID_ARSENAL @regFormShareHead.jsp

  var b_is_private_api = ('APIGUB1020' == ext_param['apiClass']); //-- Public: APIGUB1010, Private: APIGUB1020, Internal: APIGUB1030
  if (b_is_private_api) {
    alert_message('등록요청은 운영자만 가능합니다.');
    return false;
  }

  if(apiSpcNo == ''){
    if($('#pApiSpcNo').val() == ''){
      alert_message('API를 등록후에 요청하시기 바랍니다.');
      return false;
    }
    else {
      apiSpcNo = $('#pApiSpcNo').val();
	  //git Code scanning 조치
	  // Validate that apiSpcNo is alphanumeric (and optional dashes/underscores)
       var validApiSpcNoPattern = /^[a-zA-Z0-9_\-]+$/;
       if (!validApiSpcNoPattern.test(apiSpcNo)) {
         alert_message('API 번호 형식이 올바르지 않습니다.'); // "Invalid API number format."
         return false;
       }
    }
  }
  //--@@console.log('editorTomcatUse >>>>>>>> ' + editorTomcatUse);
  userwidth   = (screen.width - 15);
  userheight  = (screen.height - 130);
  
  if (editorTomcatUse == 'true') {
    //--@@console.log('editorTomcatUse >>>>>>>>' + userId);
    window.sessionStorage.setItem('sessionkey', sessionKey);
    //--[drm][chg][ing]
    //--[i]raw userid를 전달 -> 전체적정리요망
    var tmpId = encodeURIComponent(userId);
    if(true == b_is_ARSENAL) {
      tmpId = userId;
    }
    
    window.sessionStorage.setItem('mbrid', tmpId);
    //--##window.sessionStorage.setItem('mbrid', encodeURIComponent(userId));
    window.sessionStorage.setItem('apino', apiSpcNo);

    window.sessionStorage.setItem('new', true);
    window.sessionStorage.setItem('no-proxy', true);
    //--@@console.log("editorDefultHost=>" + editorDefultHost);
    //openOnceTomcat(editorTomcatHostUse, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
    //--[tag:20200807][chg]
    //--##openOnceTomcat("https://" + editorDefultHost + editorTomcatHostUse, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
    openOnceTomcat(editorTomcatHostUse, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no,status=no,toolbar=no');
  } else {
    //openOnce(editorHostUrl+'mbrid='+encodeURIComponent(userId)+'&apino='+apiSpcNo+'&new=true&no-proxy=false&sessionkey='+sessionKey, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
	openOnce(editorHostUrl+'mbrid='+encodeURIComponent(userId)+'&apino='+encodeURIComponent(apiSpcNo)+'&new=true&no-proxy=false&sessionkey='+sessionKey, 'MyWindowName', 'left=0,top=0,width='+userwidth+',height='+userheight+',resizable=yes,menubar=no, status=no, toolbar=no');
  }
}

// yaml 에디터 관련 이벤트
var winref
function openOnceTomcat(url, target, options){
  //  url = 'http://10.214.188.53:8081/apieditor/';

  if (winref) { 
    winref.close();
  }
  // open a blank 'target' window or get the reference to the existing 'target' window
  winref = window.open('', target, options, false);
  winref.location.href = url;

  return winref;
}
function openOnce(url, target, options){
   if (winref) {
     winref.close();
   }
   // open a blank 'target' window
   // or get the reference to the existing 'target' window
   winref = window.open('', target, options, false);
   winref.location.href = url;
   // git Code scanning 조치
   // winref.location.reload(true);
   // winref.document.getElementById('onEditorBtn').click()
   // if the 'target' window was just opened, change its url
   if(winref.location.href === 'about:blank'){
     winref.location.href = url;
   }
   return winref;
}

//--[tag:adpt][add]
function errCountReset() {
  errorNum = 0; // 오류 갯수 초기화
  errorText = []; // 오류 텍스트 초기화
}

//--[tag:SR-20201126][chg] {
//-- 실제호출함수
function errCountCk(data_elem, error_id, textUse) { return _fn_errCountCk_ext(data_elem, error_id, textUse); }
function cboxErrCountCk(data_elem, error_id, textUse) { return _fn_errCountCk_ext(data_elem, error_id, textUse, 'cbox_errcountck'); }
function pathErrCountCk(data_elem, error_id, textUse) { return _fn_errCountCk_ext(data_elem, error_id, textUse, 'path_errcountck'); }
function urlErrCountCk(data_elem, error_id, textUse) { return _fn_errCountCk_ext(data_elem, error_id, textUse, 'url_errcountck'); }

//--[tag:SR-20201126][add]
//--[i]이전 errCountCk()함수group을 확장성있게 통합
//--[i]직접호출하지않음
function _fn_errCountCk_ext(data_elem, error_id, textUse, tagId) {
  var jq_data_elem = $(data_elem);
  if (jq_data_elem.length == 0) { return false; } //-- elem없을시 return

  var fn_is_valid_value = (function() { return false; });  //-- 입력validation function
  var jq_red_txt = $(); //-- 경고문dom
  var fn_after = (new Function()); //-- validation()결과후처리 function

  if ('cbox_errcountck' == tagId) {  //-- CboxErrCountCk()
    fn_is_valid_value = (function(p_val, p_data_elem) { return $(p_data_elem).is(':checked'); });
    jq_red_txt = jq_data_elem.parent().parent().find('.red_txt'); //-- checked
  }
  else if ('path_errcountck' == tagId) {  //-- pathErrCountCk()
    fn_is_valid_value = (function(p_val) { return /^\/[A-Za-z0-9\-\/_.+]*$/.test(p_val); });  //-- '/' 문자로시작, alpha + numeric + spcchar
    jq_red_txt = jq_data_elem.next();
  }
  else if ('url_errcountck' == tagId) {  //-- urlErrCountCk()
    fn_is_valid_value = (function(p_val) {
      var regex = new RegExp(/^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/gi); //-- https://user:pass@www.abc.com:1234/dir/file#123 check
      return regex.test(p_val);
    });
    jq_red_txt = jq_data_elem.parent().parent().find('.red_txt');
  }
  else {  //-- errCountCk()
    fn_is_valid_value = (function(p_val) { return (p_val != ''); });
    //-- except {
      //--[tag:SR-20210324][cmt]
      //--##if ('pathSummaryFmt' == error_id) { fn_is_valid_value = (function(p_val) { return /^[^\-]+$/.test(p_val); }); } //-- 이름'-'사용불가
      if (error_id.indexOf('paramNameFmt') == 0) { //-- parameter명
        fn_is_valid_value = (function(p_val) { return /^[\w\-]+$/.test(p_val); });
      }
      else if ((error_id.indexOf('paramAccountFmt') == 0) || (error_id.indexOf('paramBigoFmt') == 0)) { //-- parameter설명, parameter비고
        fn_is_valid_value = (function(p_val) { return /^[^\[\]{}]*$/.test(p_val); }); //-- '[]{}'사용불가
      }
      else if (error_id.indexOf('HNDLROPTN_CONFIG') == 0) { //-- handler option CONFIG
        //-- [tag:SR-20230113][add]
        fn_is_valid_value = (function(p_val) {
          return ($is_empty(p_val) || (!$is_empty(p_val) && $is_json_str(p_val)));
        }); //-- JSON string
      }
    //-- except }
    jq_red_txt = jq_data_elem.next();
  }
  fn_after = (function(p_b_err, p_error_id, p_data_elem) {
    var cls_err = ('input_error cid_focus_' + p_error_id);
    (p_b_err ? $(p_data_elem).addClass(cls_err) : $(p_data_elem).removeClass(cls_err)); //-- <input> class선택처리
  });
  
  jq_red_txt.hide();  // 경고문 hide
  var b_err = (false == fn_is_valid_value((jq_data_elem.val()||''), data_elem));  // validation결과
  if (true == b_err) {
    errorText.push(error_id);
    errorNum = errorNum + 1;
    $('.err_count').find('em').text(errorNum);
    if ((textUse == true) && (jq_red_txt.length > 0)) {
      jq_red_txt.show();  // 경고문 show //-- [drm][ref] jq_data_elem.next().css('display', 'block');  // jq_data_elem.next().css('display', 'none');
    }
  }
  fn_after(b_err, error_id, data_elem); //-- 결과후처리
  return b_err;
}
//--[tag:SR-20201126][chg] }

//--[tag:SR-20201126][dep] {
//--[tag:adpt][chg]
//오류 건수 체크(checkbox용 값 검사)
function _dep_CboxErrCountCk(data, errorNm, textUse){
  var b_err = false;
  var errCheck = errorText.indexOf(errorNm);
  if (($(data).is(':checked') == false) && (errCheck == -1)) {
    errorNum = errorNum + 1;  $('.err_count').find('em').text(errorNum);  errorText.push(errorNm);  b_err = true;
    if (textUse == true) { $(data).parent().parent().find('.red_txt').show(); } // 경고문이 있을경우 활성화    //-- [drm][ref] $(data).parent().parent().find('.red_txt').css('display', 'block');
  }
  else if ($(data).is(':checked') == true) {
    //--@@errorNum = errorNum - 1; $('.err_count').find('em').text(errorNum); errorText.splice(errorText.indexOf(errorNm), 1);
    if (textUse == true) { $(data).parent().parent().find('.red_txt').hide(); } // 경고문이 있을경우 비 활성화  //-- [drm][ref] $(data).parent().parent().find('.red_txt').css('display', 'none');
  }
  return b_err;
}

//--[tag:adpt][chg]
//오류 건수 체크(앞에 / 일 경우)
function _dep_pathErrCountCk(data, errorNm, textUse){
  var b_err = false;
  var errCheck = errorText.indexOf(errorNm);
  var data_val = ($(data).val()||'');  
  if ((data_val.substring(0, 1) != '/') && (errCheck == -1)) {
    errorNum = errorNum + 1;  $('.err_count').find('em').text(errorNum);  errorText.push(errorNm);  b_err = true;
    if (textUse == true) { $(data).next().show(); } // 경고문이 있을경우 활성화  //-- [drm][ref] $(data).next().css('display', 'block');
  }
  else if (data_val.substring(0, 1) == '/') {
    //--@@errorNum = errorNum - 1; $('.err_count').find('em').text(errorNum); errorText.splice(errorText.indexOf(errorNm), 1);
    if (textUse == true) { $(data).next().hide(); } // 경고문이 있을경우 비 활성화  //-- [drm][ref] $(data).next().css('display', 'none');
  }
  return b_err;
}

// 20250805 CodeScanning
////--[tag:adpt][add]
////오류 건수 체크(url validation)
//function _dep_urlErrCountCk(data, errorNm, textUse) {
//  var b_err = false;
//  var errCheck = errorText.indexOf(errorNm);
//  var regex = new RegExp(/^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/gi); //-- https://user:pass@www.abc.com:1234/dir/file#123 check
//  var data_val = ($(data).val()||'');  
//  if ((data_val.match(regex) == null) && (errCheck == -1)) {
//    errorNum = errorNum + 1;  $('.err_count').find('em').text(errorNum);  errorText.push(errorNm);  b_err = true;
//    if (textUse == true) { $(data).parent().parent().find('.red_txt').show(); }
//  }
//  else if (data_val.match(regex) != null) {
//    //--@@errorNum = errorNum - 1; $('.err_count').find('em').text(errorNum); errorText.splice(errorText.indexOf(errorNm), 1);
//    if (textUse == true) { $(data).parent().parent().find('.red_txt').hide(); }
//  }
//  return b_err;
//}
////--[tag:SR-20201126][dep] }

// array 값 확인
Array.prototype.contains = function(obj) {
  var i = this.length;
  while (i--) { if (this[i] == obj) { return true; } }
  return false;
}

// api 메소드 삭제
function delApiMethod(cateNm, apiPath, apiMethod, apiNo){
  //-- API메소드(삭제)
  if (fn_check_regform_action() == false) {
    return false;
  }

  var btnHtm = '';
  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="methodDelStart(\''+cateNm+'\',\''+apiPath+'\',\''+apiMethod+'\',\''+apiNo+'\')">확인</button> ';
  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
  fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');
}

function methodDelStart(cateNm, apiPath, apiMethod, apiNo) {
  delete yamlOb['paths'][apiPath][apiMethod.toLowerCase()]; // json 메소드 삭제
  delete yamlOb['x-category'][cateNm][apiPath][apiMethod.toLowerCase()]; // API그룹내 API정보 삭제

  var yamlStr = YAML.stringify(yamlOb);
  // 패스 저장시에 초기값 불러오기떄문에 초기값 부분 다시 셋팅
  $('#yamlSbst').val(yamlStr);

  var param = new Object();
  param.apiSpcNo = $('#pApiSpcNo').val();
  param.apiNo = apiNo;
  param.apiCtgryNo = $('#pApiCtgryNo').val();
  param.yamlStr = $('#yamlSbst').val();
  $.ajax({
    url    : delApiMethodUrl , 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      alert_message('삭제 되었습니다.');
      if(apiNo == $('#pApiNo').val()){
        mvPage('info');
      }
      else {
        // leftMenuSet(data.cateInfo.catePathList, data.apiRegVO); // 레프프 메뉴 셋팅
        // 레프트 메뉴 세팅
        XLeftMenuSet(yamlOb['x-category']);
      }
    },
    error:function(request,status,error){
      err_message(status, error);
      }
  });
}
//api 패스 삭제 //[i] not used?
function delApiPath(apiPath, apiNo){
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }
  
  //-- API패스(삭제)
  if (fn_check_regform_action() == false) {
    return false;
  }

  var btnHtm = '';
  btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="pathDelStart(\''+apiPath+'\',\''+apiNo+'\')">확인</button> ';
  btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
  fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');
}

function pathDelStart(apiPath, apiNo) {
  delete yamlOb['paths'][apiPath]; // json 패스 삭제

  var yamlStr = YAML.stringify(yamlOb);
  
  var param     = new Object();
  param.apiSpcNo    = $('#pApiSpcNo').val();
  param.apiCtgryNo  = $('#pApiCtgryNo').val();
  param.yamlStr     = yamlStr;
  $.ajax({
    url    : delApiPathUrl , 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      alert_message('삭제 되었습니다.');
      if(apiNo == $('#pApiNo').val()){
        mvPage('info');
      }
      else {
        // leftMenuSet(data.cateInfo.catePathList, data.apiRegVO); // 레프프 메뉴 셋팅
        // 레프트 메뉴 세팅
        XLeftMenuSet(yamlOb['x-category']);
      }
    },
    error:function(request,status,error){
      err_message(status, error);
    }
  });
}
// 에디터에서 저장시에 부모창 새로고침 
function editorSaveReload(){
  $('#apiInfoForm').attr('action', mvApiInfoUrl);
  $('#apiInfoForm').submit();
};

//체크박스 클릭시 쿠키 정보 저장
function cookieSetInfo(data, cookieNm){
  if($(data).prop('checked') == true){
    setCookie(cookieNm, 'Y');
  } else {
    setCookie(cookieNm, 'N');
  }
}
//쿠키 정보 저장
function setCookie(cookieName, value){
 var exdate = new Date();
 exdate.setDate(exdate.getDate() + 7);
 var cookieValue = escape(value) + '; expires=' + exdate.toGMTString();
 document.cookie = cookieName + '=' + cookieValue;
}
//쿠키 정보 불러오기 
function getCookie(cookieName) {
 cookieName = cookieName + '=';
 var cookieData = document.cookie;
 var start = cookieData.indexOf(cookieName);
 var cookieValue = '';
 if(start != -1){
     start += cookieName.length;
     var end = cookieData.indexOf(';', start);
     if(end == -1)end = cookieData.length;
     cookieValue = cookieData.substring(start, end);
 }
 return unescape(cookieValue);
}
//쿠키 정보 삭제
function deleteCookie(cookieName){
 var expireDate = new Date();
 expireDate.setDate(expireDate.getDate() - 1);
 document.cookie = cookieName + '= ' + '; expires=' + expireDate.toGMTString();
}
// jons 문자열로 변화
function jsonToString(object) {
    var results = [];
    for (var property in object) {
        var value = object[property];
        if (value)
            results.push(property.toString() + ': ' + value);
        }
                
        return '{' + results.join(', ') + '}';
}

// 에러에 대한 내용 추가
function errTextAppend() {
  if (errorNum == 0) {
    $('.err_tooltip').find('dl').empty();
  }
  else {
    //-- 오류식별자: 오류메시지 define {
    var o_error_list = {
      //-- API기본정보
      'infoSystemId': '서비스를 선택하세요.',
      'infoTitle': '카테고리를 입력하세요.',
      'infoApiClass': 'API 구분을 선택하세요.', //-- [tag:adpt][drm][add]
      'infoAuthId': '권한그룹을 선택하세요.',
      'infoVersion': '버전을 입력하세요.',
      'infoHost': '호스트를 입력하세요.',
      //--##'infoBasePath': '기본경로는 슬래시 (/)로 시작해야 합니다.',
      'infoBasePath': '기본경로항목의 형식을 확인하세요.\n(\'/\'문자로 시작)',
      'infoSchema': '스키마를 선택하세요.',
      'infoApiVeriBaseurl': 'API검증 BASEURL 항목을 입력하세요.',
      'inputNameEmpty': '보안 스키마의 이름을 입력하세요.',
      //-- API그룹
      'ctgryNm': 'API그룹 제목을 입력하세요.',
      'cateNmDup': '중복된 API그룹명이 있습니다.',
      //-- DATATYPE등록
      'typeName': '이름을 입력하세요.',
      'mainType': '타입을 선택하세요.',
      'typeExample': 'TEST DATA를 입력하세요.',
      'datatypeInput': '파라미터에 대한 이름을 입력하세요.',         //-- (식별자 + number)
      'datatypeSelect': '파라미터에 대한 타입을 선택하세요.',        //-- (식별자 + number)
      'datatypeExample': '파라미터에 대한 TEST DATA를 입력하세요.',  //-- (식별자 + number)
      'dataNmDuple': 'DATA TYPE명이 중복되었습니다.',
      //-- API등록
      'pathSummary': '이름을 입력하세요.',
      'pathApiDesc': '설명을 입력하세요.',
      'pathImpact': '영향도를 입력하세요.',
      //--##'pathSummaryFmt': '이름 항목의 형식을 확인하세요.\n(\'-\'문자 사용불가)',
      'pathSummaryDup': '중복된 API 이름이 존재합니다.',
      'pathApiGubun': 'API 구분을 선택하세요.',
      //--[20201015][!@@!][add]
      'sandboxYn': 'sandbox 적용여부를 선택하세요.',
      //-- [tag:job-20200420][add]
      'methodIsEmpty': 'Method를 선택하세요',
      'pathPathNull': 'Path를 입력하세요.',
      //--##'pathPath': 'Path 앞 글자는 /로 시작해야 합니다.',
      'pathPath': 'Path 항목의 형식을 확인하세요. (\'/\'문자로 시작)',
      'pathMtthod': '동일한 Method가 존재합니다.',
      'pathApiId': 'API 아이디를 입력하세요.',
      'pathApiIdOverlap': '동일한 API 아이디가 존재합니다.',
      //-- [tag:adpt][drm][add] {
      'pathApiHandlerCd': 'Handler를 선택하세요.',
      'pathEndpntMethodCd': 'Endpoint Method를 선택하세요.',
      'pathEndpntTbUrl': 'Endpoint TB URL을 입력하세요.',
      'pathEndpntTbUrlValid': 'Endpoint TB URL 형식을 확인하세요.',
      'pathEndpntPrdUrl': 'Endpoint 상용 URL을 입력하세요.',
      'pathEndpntPrdUrlValid': 'Endpoint 상용 URL 형식을 확인하세요.',
      'requestContentType': '요청 Body의 Content Type을 선택하세요.',
      'responseContentType': '응답 Body의 Content Type을 선택하세요.',
      //-- [tag:adpt][drm][add] }
      'reqName': '요청 파라미터의 이름을 입력하세요.',         //-- (식별자 + number)
      'reqSelect': '요청 파라미터의 타입을 선택하세요.',       //-- (식별자 + number)
      'reqExample': '요청 파라미터의 TEST DATA를 입력하세요.', //-- (식별자 + number)
      'resParamYn': '응답 파라미터를 1개 이상 등록하세요.',
      'resStatusDuple': '응답 코드가 중복되었습니다.',
      'resAccont': '응답 파라미터의 설명을 입력하세요.',       //-- (식별자 + number)
      'resName': '응답 파라미터의 이름을 입력하세요.',         //-- (식별자 + number)
      'resSelect': '응답 파라미터의 타입을 선택하세요.',       //-- (식별자 + number)
      'resExample': '응답 파라미터의 TEST DATA를 입력하세요.', //-- (식별자 + number)
      //--'scopeNmDuple': '보안 스키마중에 중복된 범위명이 있습니다.',  //-- not found
      'paramNameFmt': '파라미터 이름 항목의 형식을 확인하세요.',     //-- (식별자 + number)
      'paramAccountFmt': '파라미터 설명 항목의 형식을 확인하세요.\n(\'-[]{}\' 문자 사용불가)',   //-- (식별자 + number)
      'paramBigoFmt': '파라미터 비고 항목의 형식을 확인하세요.\n(\'-[]{}\' 문자 사용불가)',      //-- (식별자 + number)
      //-- [tag:SR-20230113][add]
      'HNDLROPTN_CONFIG': 'Handler Option Config항목의 형식을 확인하세요.\n(JSON형식 문자열)',
    }

    /* 아스날시스템 변경처리 // Program By CYD - 2020.05.12 */
    //-- [drm][chg]
    var b_is_ARSENAL = ($("#systemId").val() == con_SYSTEMID_ARSENAL); //-- con_SYSTEMID_ARSENAL @regFormShareHead.jsp
    if (true == b_is_ARSENAL) {
      o_error_list['projectNamespace'] = '네임스페이스명을 입력하세요.'; // 아스날 네임스페이스추가
      o_error_list['infoTitle'] = '프로젝트명을 입력하세요.';  // API그룹 -> 프로젝트명
    }
    //-- 오류식별자: 오류메시지 define }

    var lc_fn_proc_errText = (function(item, error_key, error_text) {
      var jq_dl = $('.err_tooltip').find('dl');
      var jq_dd = jq_dl.find('.' + error_key);
      if (jq_dd.length == 0) {
        var s_html = '';
        var cid_focus = ('.cid_focus_' + item);
        var prefix_a = '', suffix_a = '';
        if ($(cid_focus).length != 0) {
          prefix_a = '<a href="javascript:void(0)" onclick="$(\'' + cid_focus + '\').eq(0).focus()">';
          suffix_a = '</a>';
        }
		//git Code scanning 취약점 조치 
        //jq_dl.append('<dd class="' + item  + '">' + prefix_a + error_text.replace(/\n/, '<br>') + suffix_a + '</dd>');
		jq_dl.append('<dd class="' + item  + '">' + prefix_a + error_text.replace(/\n/g, '<br>') + suffix_a + '</dd>');
      }
      else {
        jq_dd.remove();
      }
    });

    //-- 오류메시지출력 
    $.each(errorText, function(index, item) {
      var error_key = item;

      //-- (식별자 + number) input
      var a_tmpl_error_key_list = [];
      a_tmpl_error_key_list = a_tmpl_error_key_list.concat('datatypeInput,datatypeSelect,datatypeExample'.split(','));
      a_tmpl_error_key_list = a_tmpl_error_key_list.concat('reqName,reqSelect,reqExample'.split(','));
      a_tmpl_error_key_list = a_tmpl_error_key_list.concat('resAccont,resName,resSelect,resExample'.split(','));
      a_tmpl_error_key_list = a_tmpl_error_key_list.concat('paramNameFmt,paramAccountFmt,paramBigoFmt'.split(','));
      var n_find_idx = a_tmpl_error_key_list.findIndex(function(input_name) { return (item.indexOf(input_name) == 0); });
      error_key = ((n_find_idx != -1) ? a_tmpl_error_key_list[n_find_idx] : error_key);

      var error_text = (o_error_list[error_key]||'정의되지 않은 오류 항목 입니다.\n[key: ' + error_key + ']');
      lc_fn_proc_errText(item, error_key, error_text);
    });
  }
}

//--[dep]
/*--
function _dep_errTextAppend_org(){
  var err_dl = $('.err_tooltip').find('dl');
  if(errorNum == 0){
    err_dl.children().remove();
    //err_dl.append('<dt>다음과 같은 오류가 발생하였습니다.</dt>');
  }
  else {
    //API그룹 제목 에러 
    $.each(errorText, function(index, item) {
      // === API 기본정보 페이지 ===
      if (item == 'infoSystemId' && err_dl.find('.infoSystemId').length == 0){ err_dl.append('<dd class="infoSystemId">서비스를 선택하세요.</dd>'); }
      else if(errorText.indexOf('infoSystemId') == -1 && err_dl.find('.infoSystemId').length > 0){ err_dl.find('.infoSystemId').remove(); } 
      if (item == 'infoTitle' && err_dl.find('.infoTitle').length == 0){ err_dl.append('<dd class="infoTitle">카테고리를 입력하세요.</dd>'); }
      else if(errorText.indexOf('infoTitle') == -1 && err_dl.find('.infoTitle').length > 0){ err_dl.find('.infoTitle').remove(); }
      //-- [tag:adpt][drm][add]
      if (item == 'infoApiClass' && err_dl.find('.infoApiClass').length == 0){ err_dl.append('<dd class="infoApiClass">API 구분을 선택하세요.</dd>'); }
      else if(errorText.indexOf('infoApiClass') == -1 && err_dl.find('.infoApiClass').length > 0){ err_dl.find('.infoApiClass').remove(); }
      if (item == 'infoAuthId' && err_dl.find('.infoAuthId').length == 0){ err_dl.append('<dd class="infoAuthId">권한그룹을 선택하세요.</dd>'); }
      else if(errorText.indexOf('infoAuthId') == -1 && err_dl.find('.infoAuthId').length > 0){ err_dl.find('.infoAuthId').remove(); }
      if (item == 'infoVersion' && err_dl.find('.infoVersion').length == 0){ err_dl.append('<dd class="infoVersion">버전을 입력하세요.</dd>'); }
      else if(errorText.indexOf('infoVersion') == -1 && err_dl.find('.infoVersion').length > 0){ err_dl.find('.infoVersion').remove(); } 
      if (item == 'infoHost' && err_dl.find('.infoHost').length == 0){ err_dl.append('<dd class="infoHost">호스트를 입력하세요.</dd>'); }
      else if(errorText.indexOf('infoHost') == -1 && err_dl.find('.infoHost').length > 0){ err_dl.find('.infoHost').remove(); } 
      if (item == 'infoBasePath' && err_dl.find('.infoBasePath').length == 0){ err_dl.append('<dd class="infoBasePath">기본경로는 슬래시 (/)로 시작해야 합니다.</dd>'); }
      else if(errorText.indexOf('infoBasePath') == -1 && err_dl.find('.infoBasePath').length > 0){ err_dl.find('.infoBasePath').remove(); } 
      if (item == 'infoSchema' && err_dl.find('.infoSchema').length == 0){ err_dl.append('<dd class="infoSchema">스키마를 선택하세요.</dd>'); }
      else if(errorText.indexOf('infoSchema') == -1 && err_dl.find('.infoSchema').length > 0){ err_dl.find('.infoSchema').remove(); }
      if (item == 'inputNameEmpty' && err_dl.find('.inputNameEmpty').length == 0){ err_dl.append('<dd class="inputNameEmpty">보안 스키마의 이름을 입력하세요.</dd>'); }
      else if(errorText.indexOf('inputNameEmpty') == -1 && err_dl.find('.inputNameEmpty').length > 0){ err_dl.find('.inputNameEmpty').remove(); }
      // === API 기본정보 페이지 ===
      // === API그룹 페이지 ===
      // 제목 체크
      if (item == 'ctgryNm' && err_dl.find('.ctgryNm').length == 0){ err_dl.append('<dd class="ctgryNm">API그룹 제목을 입력하세요.</dd>'); }
      else if(errorText.indexOf('ctgryNm') == -1 && err_dl.find('.ctgryNm').length > 0){ err_dl.find('.ctgryNm').remove(); }
      // 중복된 API그룹명
      if (item == 'cateNmDup' && err_dl.find('.cateNmDup').length == 0){ err_dl.append('<dd class="cateNmDup">중복된 API그룹명이 있습니다.</dd>'); }
      else if(errorText.indexOf('cateNmDup') == -1 && err_dl.find('.cateNmDup').length > 0){ err_dl.find('.cateNmDup').remove(); }
      // === API그룹 페이지 ===
      // === DATATYPE 등록 페이지 ===
      // 제목 에러 텍스트
      if (item == 'typeName' && err_dl.find('.typeName').length == 0){ err_dl.append('<dd class="typeName">이름을 입력하세요.</dd>'); }
      else if(errorText.indexOf('typeName') == -1 && err_dl.find('.typeName').length > 0){ err_dl.find('.typeName').remove(); }
      // 메인 타입 에러 텍스트
      if (item == 'mainType' && err_dl.find('.mainType').length == 0){ err_dl.append('<dd class="mainType">타입을 선택하세요.</dd>'); }
      else if(errorText.indexOf('mainType') == -1 && err_dl.find('.mainType').length > 0){ err_dl.find('.mainType').remove(); }
      // 메인 예제 에러 텍스트
      if (item == 'typeExample' && err_dl.find('.typeExample').length == 0){ err_dl.append('<dd class="typeExample">TEST DATA를 입력하세요.</dd>'); }
      else if(errorText.indexOf('typeExample') == -1 && err_dl.find('.typeExample').length > 0){ err_dl.find('.typeExample').remove(); }
      // 파라미터에 대한 네임 에러 텍스트
      if(item.indexOf('datatypeInput') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">파라미터에 대한 이름을 입력하세요.</dd>'); } }
      // 파라미터에 대한 타입 에러 텍스트
      if(item.indexOf('datatypeSelect') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">파라미터에 대한 타입을 선택하세요.</dd>'); } }
      // 파라미터에 대한 예제 에러 텍스트
      if(item.indexOf('datatypeExample') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">파라미터에 대한 TEST DATA를 입력하세요.</dd>'); } }
      // DATA TYPE명이 중복
      if (item == 'dataNmDuple' && err_dl.find('.dataNmDuple').length == 0){ err_dl.append('<dd class="dataNmDuple">DATA TYPE명이 중복되었습니다.</dd>'); }
      else if(errorText.indexOf('dataNmDuple') == -1 && err_dl.find('.dataNmDuple').length > 0){ err_dl.find('.dataNmDuple').remove(); }
      // === DATATYPE 등록 페이지 ===
      // === PATH 등록 페이지 ===
      // 제목 에러 텍스트
      if (item == 'pathSummary' && err_dl.find('.pathSummary').length == 0){ err_dl.append('<dd class="pathSummary">이름을 입력하세요.</dd>'); }
      else if(errorText.indexOf('pathSummary') == -1 && err_dl.find('.pathSummary').length > 0){ err_dl.find('.pathSummary').remove(); }
      if (item == 'pathSummaryDup' && err_dl.find('.pathSummaryDup').length == 0){ err_dl.append('<dd class="pathSummaryDup">중복된 API 이름이 존재합니다.</dd>'); }
      else if(errorText.indexOf('pathSummaryDup') == -1 && err_dl.find('.pathSummaryDup').length > 0){ err_dl.find('.pathSummaryDup').remove(); }
      if (item == 'pathApiGubun' && err_dl.find('.pathApiGubun').length == 0){ err_dl.append('<dd class="pathApiGubun">API 구분을 선택하세요.</dd>'); }
      else if(errorText.indexOf('pathApiGubun') == -1 && err_dl.find('.pathApiGubun').length > 0){ err_dl.find('.pathApiGubun').remove(); }
      // 패스 입력없을시 에러 텍스트
      if (item == 'pathPathNull' && err_dl.find('.pathPathNull').length == 0){ err_dl.append('<dd class="pathPathNull">Path를 입력하세요.</dd>'); }
      else if(errorText.indexOf('pathPathNull') == -1 && err_dl.find('.pathPathNull').length > 0){ err_dl.find('.pathPathNull').remove(); }
      // 패스 에러 텍스트
      if (item == 'pathPath' && err_dl.find('.pathPath').length == 0){ err_dl.append('<dd class="pathPath">Path 앞 글자는 /로 시작해야 합니다.</dd>'); }
      else if(errorText.indexOf('pathPath') == -1 && err_dl.find('.pathPath').length > 0){ err_dl.find('.pathPath').remove(); }
      // 메소드 에러 텍스트
      if (item == 'pathMtthod' && err_dl.find('.pathMtthod').length == 0){ err_dl.append('<dd class="pathMtthod">동일한 Method가 존재합니다.</dd>'); }
      else if(errorText.indexOf('pathMtthod') == -1 && err_dl.find('.pathMtthod').length > 0){ err_dl.find('.pathMtthod').remove(); }
      // api id 에러 텍스트
      if (item == 'pathApiId' && err_dl.find('.pathApiId').length == 0){ err_dl.append('<dd class="pathApiId">API 아이디를 입력하세요.</dd>'); }
      else if(errorText.indexOf('pathApiId') == -1 && err_dl.find('.pathApiId').length > 0){ err_dl.find('.pathApiId').remove(); }
      // api id 중복 에러 텍스트
      if (item == 'pathApiIdOverlap' && err_dl.find('.pathApiIdOverlap').length == 0){ err_dl.append('<dd class="pathApiIdOverlap">동일한 API 아이디가 존재합니다.</dd>'); }
      else if(errorText.indexOf('pathApiIdOverlap') == -1 && err_dl.find('.pathApiIdOverlap').length > 0){ err_dl.find('.pathApiIdOverlap').remove(); }
      //-- [tag:adpt][drm][add] {
      if (item == 'pathApiHandlerCd' && err_dl.find('.pathApiHandlerCd').length == 0){ err_dl.append('<dd class="pathApiHandlerCd">Handler를 선택하세요.</dd>'); }
      else if(errorText.indexOf('pathApiHandlerCd') == -1 && err_dl.find('.pathApiHandlerCd').length > 0){ err_dl.find('.pathApiHandlerCd').remove(); }
      if (item == 'pathEndpntMethodCd' && err_dl.find('.pathEndpntMethodCd').length == 0){ err_dl.append('<dd class="pathEndpntMethodCd">Endpoint Method를 선택하세요.</dd>'); }
      else if(errorText.indexOf('pathEndpntMethodCd') == -1 && err_dl.find('.pathEndpntMethodCd').length > 0){ err_dl.find('.pathEndpntMethodCd').remove(); }
      if (item == 'pathEndpntTbUrl' && err_dl.find('.pathEndpntTbUrl').length == 0){ err_dl.append('<dd class="pathEndpntTbUrl">Endpoint TB URL을 입력하세요.</dd>'); }
      else if(errorText.indexOf('pathEndpntTbUrl') == -1 && err_dl.find('.pathEndpntTbUrl').length > 0){ err_dl.find('.pathEndpntTbUrl').remove(); }
      if (item == 'pathEndpntTbUrlValid' && err_dl.find('.pathEndpntTbUrlValid').length == 0){ err_dl.append('<dd class="pathEndpntTbUrlValid">Endpoint TB URL 형식을 확인하세요.</dd>'); }
      else if(errorText.indexOf('pathEndpntTbUrlValid') == -1 && err_dl.find('.pathEndpntTbUrlValid').length > 0){ err_dl.find('.pathEndpntTbUrlValid').remove(); }
      if (item == 'pathEndpntPrdUrl' && err_dl.find('.pathEndpntPrdUrl').length == 0){ err_dl.append('<dd class="pathEndpntPrdUrl">Endpoint 상용 URL을 입력하세요.</dd>'); }
      else if(errorText.indexOf('pathEndpntPrdUrl') == -1 && err_dl.find('.pathEndpntPrdUrl').length > 0){ err_dl.find('.pathEndpntPrdUrl').remove(); }
      if (item == 'pathEndpntPrdUrlValid' && err_dl.find('.pathEndpntPrdUrlValid').length == 0){ err_dl.append('<dd class="pathEndpntPrdUrlValid">Endpoint 상용 URL 형식을 확인하세요.</dd>'); }
      else if(errorText.indexOf('pathEndpntPrdUrlValid') == -1 && err_dl.find('.pathEndpntPrdUrlValid').length > 0){ err_dl.find('.pathEndpntPrdUrlValid').remove(); }
      if (item == 'requestContentType' && err_dl.find('.requestContentType').length == 0){ err_dl.append('<dd class="requestContentType">요청 Body의 Content Type을 선택하세요.</dd>'); }
      else if(errorText.indexOf('requestContentType') == -1 && err_dl.find('.requestContentType').length > 0){ err_dl.find('.requestContentType').remove(); }
      if (item == 'responseContentType' && err_dl.find('.responseContentType').length == 0){ err_dl.append('<dd class="responseContentType">응답 Body의 Content Type을 선택하세요.</dd>'); }
      else if(errorText.indexOf('responseContentType') == -1 && err_dl.find('.responseContentType').length > 0){ err_dl.find('.responseContentType').remove(); }
      //-- [tag:adpt][drm][add] }     
      // 요청 파라미터 이름 에러 텍스트
      if(item.indexOf('reqName') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">요청 파라미터의 이름을 입력하세요.</dd>'); } }
      // 요청 파라미터 타입 에러 텍스트
      if(item.indexOf('reqSelect') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">요청 파라미터의 타입을 선택하세요.</dd>'); } }
      // 요청 파라미터 예제 에러 텍스트
      if(item.indexOf('reqExample') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">요청 파라미터의 TEST DATA를 입력하세요.</dd>'); } }
      // api id 중복 에러 텍스트
      if (item == 'resParamYn' && err_dl.find('.resParamYn').length == 0){ err_dl.append('<dd class="resParamYn">응답 파라미터를 1개 이상 등록하세요.</dd>'); }
      else if(errorText.indexOf('resParamYn') == -1 && err_dl.find('.resParamYn').length > 0){ err_dl.find('.resParamYn').remove(); }
      // api 응답 코드 중복 에러 텍스트
      if (item == 'resStatusDuple' && err_dl.find('.resStatusDuple').length == 0){ err_dl.append('<dd class="resStatusDuple">응답 코드가 중복되었습니다.</dd>'); }
      else if(errorText.indexOf('resStatusDuple') == -1 && err_dl.find('.resStatusDuple').length > 0){ err_dl.find('.resStatusDuple').remove(); }
      // 응답 파라미터 내용 에러 텍스트
      if(item.indexOf('resAccont') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">응답 파라미터의 설명을 입력하세요.</dd>'); } }
      // 응답 파라미터 이름 에러 텍스트
      if(item.indexOf('resName') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">응답 파라미터의 이름을 입력하세요.</dd>'); } }     
      // 응답 파라미터 타입 에러 텍스트
      if(item.indexOf('resSelect') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">응답 파라미터의 타입을 선택하세요.</dd>'); } }
      // 응답 파라미터 예제 에러 텍스트
      if(item.indexOf('resExample') > -1) { if (err_dl.find('.' + item).length == 0){ err_dl.append('<dd class="' + item + '">응답 파라미터의 TEST DATA를 입력하세요.</dd>'); } }   
      // 보안 스키마중에 중복된 범위명 -> [?]not used
      if (item == 'scopeNmDuple' && err_dl.find('.scopeNmDuple').length == 0){ err_dl.append('<dd class="scopeNmDuple">보안 스키마중에 중복된 범위명이 있습니다.</dd>'); }
      else if(errorText.indexOf('scopeNmDuple') == -1 && err_dl.find('.scopeNmDuple').length > 0){ err_dl.find('.scopeNmDuple').remove(); }
      // === PATH 등록 페이지 ===
    });
  }
}
--*/

function err_on(){
  $('.err_count').css('display', 'inline-block').show();
  var cre_posTop = ($('.err_count').offset().top) + 60 ;
  var cre_posLeft = ($('.err_count').offset().left) - 130 ;
  $('.err_tooltip').show();
  $('.err_tooltip').offset({top: cre_posTop, left: cre_posLeft});
}
function err_message(status, error) {
  //--@@console.log('[err_message()]', 'code: ', status, '[error: ]', +error);
  alert_message('요청한 작업 수행 중 오류가 발생했습니다.<br>잠시 후 다시 시도해 주시기 바랍니다.', '알림');
}

//-- ~Layout.jsp의 layer
function alert_message(message, title, alert_option) {
  title = (title||'알림');
  message = (message||'');
  alert_option = (alert_option||{});

  $('#popupConfirm').parent().find('div').eq(0).children('span').text(title);
  $('#popupConfirm').find('#alertTxt').html(message.replace(/\n/g, '<br>'));
  $('#popupConfirm .btn_popup_close').hide();
  $('#popupConfirm .cid_btn_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); });
  if ('function' == typeof(alert_option.ok_button_onclick)) { 
    $('#popupConfirm .btn_confirm').on('click', function() {
      alert_option.ok_button_onclick(alert_option);
    });
  }
  $('#popupConfirm').dialog({ width: (alert_option.width||'auto'), height: (alert_option.height||'auto') });
  $('#popupConfirm').dialog('open');
  $('#popupConfirm').find('button').last().focus();
}

//-- ~Layout.jsp의 layer
function confirm_message(message, title, alert_option) {
  title = (title||'선택');
  message = (message||'');
  alert_option = (alert_option||{});

  $('#popupConfirm').parent().find('div').eq(0).children('span').text(title);
  $('#popupConfirm').find('#alertTxt').html(message.replace(/\n/g, '<br>'));
  $('#popupConfirm .btn_popup_close').show();
  $('#popupConfirm .cid_btn_close').off('click').on('click', function() { $('#popupConfirm').dialog('close'); });
  if ('function' == typeof(alert_option.ok_button_onclick)) { 
    $('#popupConfirm .btn_confirm').off('click').on('click', function() {
      //-- (false === return) 일경우 not close
      if (false !== alert_option.ok_button_onclick(alert_option)) { $('#popupConfirm').dialog('close'); }
    });
  }
  $('#popupConfirm').dialog({ width: (alert_option.width||'auto'), height: (alert_option.height||'auto') });
  $('#popupConfirm').dialog('open');
  $('#popupConfirm').find('button').last().focus();
}

// api 패스 전체 삭제
function delPathAll(cateNm, apiPath){
  // apino 여부 체크
  if(!mvWarning('call')){
    return false;
  }
  
  //-- API(삭제)
  if (fn_check_regform_action() == false) {
    return false;
  }

  if(yamlOb.paths != undefined){
    var keyNum = 0;
    for( var key in yamlOb.paths) {
      keyNum = keyNum + 1;
    }
/*  
    if(keyNum < 2){
      var btnHtm = '';
      btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_popup_close" id="cCbtn">확인</button> ';
      fnOpenLayer(btnHtm, '삭제','패스가 1개일경우 삭제가 불가능합니다.');  
    } else {
      var btnHtm = '';
      btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="pathAllDelStart(\''+cateNm+'\',\''+apiPath+'\')">확인</button> ';
      btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
      fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');     
    }
    
*/
    var btnHtm = '';
    btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="pathAllDelStart(\''+cateNm+'\',\''+apiPath+'\')">확인</button> ';
    btnHtm+=' <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> ';
    fnOpenLayer(btnHtm, '삭제', '삭제 하시겠습니까?');
  }
}

function pathAllDelStart(cateNm, apiPath) {
  delete yamlOb.paths[apiPath]; // json 에서 패스 삭제
  delete yamlOb['x-category'][cateNm][apiPath]; // API그룹내 API정보 삭제
  
  $('#yamlSbst').val(YAML.stringify(yamlOb));
  var param = {
    apiSpcNo:   $('#pApiSpcNo').val(),
    apiPath:    apiPath,
    yamlStr: $('#yamlSbst').val()
  };
  $.ajax({
    url    : delApiPathAllUrl, 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      // console.log('data', data);
      if(apiPath == $('#pApiPath').val()){
        mvPage('info');
      } else {
        // leftMenuSet(data.cateInfo.catePathList, data.apiRegVO); // 레프프 메뉴 셋팅
        // 레프트 메뉴 세팅
        XLeftMenuSet(yamlOb['x-category']);
      }
    },
    error:function(request,status,error){
      err_message(status, error);
    }
  });
}

/**
 *   글자수 제한 체크
 */
function apiRegCheckStrLength(msglen , obj) {
  var temp;
  var f = $('#'+obj).val().length;  
  var tmpstr = '';
  var enter = 0;
  var strlen;
  var writeCnt =msglen;

  if (f != 0){
    for (k = 0; k < f; k++) {
      temp = $('#'+obj).val().charAt(k);
      if (escape(temp).length > 4) {
        msglen -= 2;
      }
      else {
        msglen--;
      }
      if (msglen < 0) {
        alert_message('총 영문 ' + writeCnt + '자 한글 ' + writeCnt/2 + '자 까지 쓰실 수 있습니다.', 'API 등록');
        $('#'+obj).val(tmpstr);
        break;
      } 
      else {
        tmpstr += temp;
      }
    }
  }
}

//API그룹 번호 조회
function selCateNo(cateNm) {
  var cateNo = 0;
  var param = {
    'apiSpcNo': $('#pApiSpcNo').val(),
    'ctgryNm': cateNm,
  }
  $.ajax({
    url    : selCategoryNo, 
    type   : 'POST',
    data   : param,
    async  : false,
    cache  : false,
    success: function(data){
      cateNo = data.cateNo;
    },
    error:function(request,status,error){
      err_message(status, error);
    }
  });
  return cateNo;
}

// api번호가 없으면 다른 기능 호출 시에 경고 메세지
function mvWarning(type){
  if ($('#pApiSpcNo').val() == '') {
    if (type == 'mv') {
      alert_message('기본 정보 등록 후 이동 가능합니다.', '기본정보');
      return false;
    }
    else if (type == 'call') {
      alert_message('기본 정보 등록 후 요청 가능합니다.', '기본정보');
      return false;
    }
  }
  return true;
}

//--[tag:adpt][add]
function fn_get_method_comn_cd(p_nm) {
  var s_nm = (p_nm||'').toLowerCase();
  var o_method = { 'get': 'MTHTYP1010', 'post': 'MTHTYP1020', 'put': 'MTHTYP1030', 'delete': 'MTHTYP1040', 'patch': 'MTHTYP1050', 'head': 'MTHTYP1060', 'options': 'MTHTYP1070', };
  return (o_method[s_nm]||'');
}

//-- path validation
function fn_is_valid_path(path) {
  //--##return /^\/[\w\-.\/]+$/.test(path);
  return true;  
}
//-- KOS, KOSMOS시 path validation
function fn_is_valid_KOS_path(path) {
  return /^\/kos\/v\d+.\d+\/\w+$/.test(path);
}
//-- KOS, KOSMOS시 apiid validation
function fn_is_valid_KOS_apiid(apiid) {
//--## return /^OIF_1[0-9]{4,5}$/.test(apiid);
	return true;
}
//-- private API apiid validation (KOS, KOSMOS제외)
function fn_is_valid_private_apiid(apiid) {
//--## return /^(OIF|CIF)_[0-9]{4,5}$/.test(apiid);
	return true;
}
//-- is valid version information 
function fn_is_valid_api_version(path) {
  return /^(v\d+.\d+)$/.test(path);
}
//-- is path include version information 
function fn_is_version_in_path(path) {
  return /^(\/[\w-.]+)\/(v\d+.\d+)(\/[\w\-.\/]+)$/.test(path);
}
//-- get version in path
function fn_get_version_in_path(path) {
  //-- e.g. /kos/v1.9/abc/123
  //-- group1: /kos, group2: v1.9, group3: /abc/123
  let s_ret = '';
  var regex = /^(\/[\w-.]+)\/(v\d+.\d+)(\/[\w\-.\/]+)$/;
  let a_match = path.match(regex);
  if (a_match != null) {
    s_ret = a_match[2];
  }
  return s_ret;
}
function fn_replace_version_in_path(path, apiver) {
  let s_ret = '';
  var regex = /^(\/[\w-.]+)\/(v\d+.\d+)(\/[\w-./]+)$/;
  let a_match = path.match(regex);
  if (a_match != null) {
    s_ret = path.replace(regex, '$1\/' + apiver + '$3');
  }
  return s_ret;
}

//-- [tag:job-20200420][add] {
//-- tags 항목 재구성 / x-category순정렬
//-- [tag:20200913][renew]
function fn_rebuild_tags(o_yaml) {
  var category = o_yaml['x-category'];
  var arr_tags = [];
  var renew_paths = {};

  if (o_yaml.hasOwnProperty('paths')) {  //-- 'paths'존재시
    $.each(category, function(p_key, p_item) {
      var category_key = p_key;
      var category_item = p_item;
      $.each(category_item, function(p_key, p_item) {
        var api_path_key = p_key;
        var api_path_item = p_item;
        if (o_yaml['paths'].hasOwnProperty(api_path_key)) {  //-- api path 존재시
          $.each(api_path_item, function(p_key, p_item) {
            var api_method_key = p_key;
            var api_method_item = p_item;
            if (o_yaml['paths'][api_path_key].hasOwnProperty(api_method_key)) {  //-- api path + method 존재시
              o_yaml['paths'][api_path_key][api_method_key]['tags'] = [category_key];
              renew_paths[api_path_key] = (renew_paths[api_path_key]||{});
              renew_paths[api_path_key][api_method_key] = o_yaml['paths'][api_path_key][api_method_key];
            }
          });
        }
      });
      arr_tags.push({'name': category_key});
    });
  }
  o_yaml['paths'] = renew_paths;
  o_yaml['tags'] = arr_tags;
}

//-- tags에서 cateNm삭제
function fn_delete_tags(yamlOb, cateNm) {
  var arr_tags = (yamlOb['tags']||[]);
  $.each(arr_tags, function(p_idx, p_item) {
    if (p_item['name'] == cateNm) {
      arr_tags.splice(p_idx, 1);
      return false;
    }
  });
  yamlOb['tags'] = arr_tags;
}
//-- [tag:job-20200420][add] }

//-- [tab:job-20200714][add] {
//-- action 처리가능 점검
function fn_check_regform_action(p_tag) {
  /*
   * p_tag: 호출from
   * 
   * infoRegForm:apiInfoSave
   * cateInfoRegForm:cataInfoSave, cateInfoRegForm:importXlsx
   * dataTypeRegForm:dataTypeSave
   * pathRegForm:pathSave, pathRegFormPrivate:pathSave, pathRegFormArsenal:pathSave
   *  
   */
  // API 권한 체크 CYD - 2020.7.15
  if (fnApiAuthCheck() == false) {
    return false;
  }

  //-- KOA_TB_API_SPC.REG_STTUS_CD 점검
  if (fn_check_reg_sttus_cd(p_tag) == false) {
    return false;
  }
  //-- KOATB_API_DEF.EDIT_FLAG 점검
  if (fn_check_def_edit_flag(p_tag) == false) {
    return false;
  }

  return true;
}

function fn_check_reg_sttus_cd(p_tag) {
  var regSttusCd = sttusCd; //-- sttusCd, sttusCdNm @regFormShareHead.jsp
  var regSttusCdNm = sttusCdNm;

  if ((p_tag == 'pathRegForm:pathSave') || (p_tag == 'pathRegFormPrivate:pathSave') || (p_tag == 'pathRegFormArsenal:pathSave')) {
    var def_apiNo = (('function' == typeof(gfn_get_apiDef)) ? gfn_get_apiDef('apiNo') : '');
    //-- meta update허용
    if (('' != def_apiNo) && ('APIREG1030' == sttusCd)) {
      return true; 
    }
  }

  //-- KOA_TB_API_SPC.REG_STTUS_CD [APIREG1010-작성중, APIREG1020-등록요청, APIREG1030-등록완료, APIREG1040-등록검토]
  if ((regSttusCd == 'APIREG1020') || (regSttusCd == 'APIREG1030')) { 
    var alert_option;
    //--##alert_option = { ok_button_onclick : (function() { window.location.reload(); }), };
    alert_message(regSttusCdNm + ' 상태 에서는 사용하실 수 없는 기능입니다.', '알림', alert_option);
    return false;
  }
  /*--##[dep][ref]
  if (('APIREV1020' == sttusCd) || ('APIREG1020' == sttusCd) || ('APIREG1030' == sttusCd)) { // 작성중, 검토요청, 등록검토일 경우에만 저장
    var alert_option = { ok_button_onclick: (function() { window.location.reload(); }), };
    alert_message(sttusCdNm + '에서는 수정하실 수 없습니다.', 'API', alert_option);
    return false;
  }
  --*/  
  return true; 
}

function fn_check_def_edit_flag(p_tag) {
  var def_editFlag = (('function' == typeof(gfn_get_apiDef)) ? gfn_get_apiDef('editFlag') : 'Y');
  if ('N' == def_editFlag) {
    alert_message('현재 API등록 상태 에서는 사용하실 수 없는 기능입니다.\n\n관리자에게 문의 하시기 바랍니다.', '알림');
    return false;
  }
  return true;
}
//-- [tab:job-20200714][add] }

//-- [tag:20200913][add]
//-- from 'paths' => 'x-category'
function fn_rebuild_x_category(o_yaml) {
  var x_category = {};
  $.each(o_yaml['paths'], function(p_key, p_item) { //-- paths
    var path = p_key;
    var path_item = p_item;
    $.each(path_item, function(p_key, p_item) { //-- path + method
      var method = p_key;
      var method_item = p_item;

      var apiNm = (method_item['summary']||'#noname#');
      var apiNo = (method_item['x-apiNo']||'-1');
      var category = (method_item['x-category']||'기본');

      x_category[category] = (x_category.hasOwnProperty(category) ? x_category[category] : {});
      var x_category_item = x_category[category];
      x_category_item[path] = (x_category_item.hasOwnProperty(path) ? x_category_item[path] : {});
      var path_item = x_category_item[path];
      path_item[method] = { 'apiNm': apiNm, 'apiNo': apiNo };
    });
  });
  return x_category;
}

//-- [CYD][add] {
/* Gitlab 내보내기 모달창 호출 함수
 * 아스날 API Yaml 파일을 Gitlab에 올릴 수 있는 모달창 호출
 * Program By CYD - 2020.05.07
 */
function fn_export_ARSENAL(apiSpcNo) {
  
  // API 권한 체크 CYD - 2020.7.15
  if(!fnApiAuthCheck()) return;
  
  g_projectName = "";
  var szArsenalHost = $("#idGitlabArsenalHost").val();
  var szArsenalPath = $("#idGitlabArsenalBasePath").val();
  var szProjectName = "";
  var szNamespace   = "";

  g_projectName = typeof yamlOb['info']['title'] == "undefined" ? "" : yamlOb['info']['title'];
  szNamespace   = typeof $("#projectNamespace").val() == "undefined" ? "" : $("#projectNamespace").val();
  szProjectName = "<b><font color='blur'>" + g_projectName + "</font></b>";
  
  // 작업을 위한 임시 테스트용
  if(confirm("수정 후 저장하지 않았다면 먼저 저장 후 진행하세요\n내보기내기를 진행하시겠습니까?")) {
    // 검증이력 dialog
    //modalPop(".pop_ver22", 850);
    g_yamlPath = szArsenalHost + szNamespace + "/" + szProjectName + szArsenalPath + szProjectName + ".yaml";
    //$( ".popArsenal" ).dialog( "close" );
    $( ".popArsenal" ).dialog( {title:"EXPORT TO ARSENAL", minWidth:1000, resizable:false} );
    $("#idSpanReturn").text("");  // 내보내기 결과 셋업
    $("#idSpanErrorCode").text(""); // 에러코드 셋업
    $("#idSpanErrorMsg").text("");  // 에러메세지 셋업
    $(".data_link").parent().removeClass("process_failure_next");
    $(".data_link").parent().removeClass("process_success_next");
    $(".data_link").parent().removeClass("process_failure_prev");
    $(".data_link").parent().removeClass("process_success_prev");
      
    // 프로젝트 ID 전역변수에 담음
    //g_szProjectId = fnGetProjectIdFromGitlab(yamlOb['info']['termsOfService']);
    //g_szProjectId = fnGetProjectIdFromGitlab(szNamespace);
    fnGetFileInfoFromGitlab();
    if(g_bIsExsist) {
      //g_bIsExsist = true;
      $(".data_link").parent().addClass("process_success_next");
      $("#idSpanReturn").text("성공");  // 내보내기 결과 셋업
    }
    //alert($(".data_link").parent().html());
    //event.preventDefault();
    window.setTimeout((function () { $(".popArsenal").dialog("open"); }), 50);
	//git Code scanning 조치
    //$("#idSpanYamlPath").html(g_yamlPath);
	$("#idSpanYamlPath").text(g_yamlPath);
    
  }
}

/* Gitlab 내보내기 콜백함수
 * Ymal 파일을 Gitlab에 업로드처리
 * Program By CYD - 2020.05.07
 */
function fn_export_GITLAB() {
  var confirmMsg  = g_bIsExsist == true ? "Gitlab서버에 해당 파일이 존재합니다.\n파일을 덮어쓰기 하시겠습니까?" : "내보기내기를 진행하시겠습니까?"
  var szNamespace = "";
  
  szNamespace = typeof $("#projectNamespace").val() == "undefined" ? "" : $("#projectNamespace").val();

  if(szNamespace == "") {
    $("#idSpanReturn").text("실패"); // 내보내기 결과 셋업
      $("#idSpanErrorCode").text("0"); // 에러코드 셋업
      $("#idSpanErrorMsg").text("네임스페이스명이 Null입니다.");   // 에러메세지 셋업
      return;
  } else {
    if(g_szProjectId == "E404") {
      $("#idSpanReturn").text("실패"); // 내보내기 결과 셋업
        $("#idSpanErrorCode").text(g_szProjectId); // 에러코드 셋업
        $("#idSpanErrorMsg").text("Gitlab서버에 " + g_projectName + "프로젝트가 존재하지 않습니다.");   // 에러메세지 셋업
        return;
    } else if(g_szProjectId == "E500") {
      $("#idSpanReturn").text("실패"); // 내보내기 결과 셋업
        $("#idSpanErrorCode").text(g_szProjectId); // 에러코드 셋업
        $("#idSpanErrorMsg").text("서버 오류가 발생했습니다.");   // 에러메세지 셋업
        return;
    }
  }
  
  if(confirm(confirmMsg)) {
    // Gitlab서버에 해당 프로젝트가 존재하지 않습니다. 프로젝트 생성을 하시고 다시 진행해 주세요. 계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다
    // Gitlab서버 동기화 중 네트워크 오류가 발생하여 내보내기 실패했습니다. 잠시 후 다시 진행해 주세요. 계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다
    //alert(g_yamlPath + "Gitlab서버에 내보내기를 완료하였습니다");
    var mothod    = "2";//POST
    var commitMsg = "create a new file";
    
    if(g_bIsExsist) {
      mothod    = "3";//PUT
      commitMsg = "update file";
      //alert("Gitlab서버에 '" + g_projectName + ".yaml' 파일이 존재하지 않습니다. 잠시 후 다시 진행해 주세요.\n\n계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다");
      //return;
    }
    
    var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F";
    var szFilePath   = g_projectName + "%2Eyaml";
    var szAjaxUr   = "/apidev/api/arsenal/exportApiToGitlab";
    
    //console.log($('#yamlSbst').val());
    var param            = new Object();
    param.branch       = "master";
    param.content        = $('#yamlSbst').val();//YAML.stringify($('#yamlSbst').val());
    param.commit_message = commitMsg;
    param.projectId    = g_szProjectId;
    param.projectName    = g_projectName;
    param.methodType   = mothod;
    param.apiSpcNo     = $('#pApiSpcNo').val();
    //--@@console.log(param);
    $.ajax({
        url    : szAjaxUr,//szGitlabHost + szFilePath,
        type   : "POST",//mothod,
        data   : JSON.stringify(param),
        async  : false,
        cache  : false,
        crossDomain: true,
        dataType:'json',
        contentType: 'application/json;charset=utf-8',
        //headers: {"PRIVATE-TOKEN": "yyuCn4SDn_N5Abyyzbsa"},
        success: function(data){
          var projectData = JSON.parse(data.info.jsonResponse);
          $("#idSpanErrorCode").text(""); // 에러코드 셋업
          $("#idSpanErrorMsg").text("");  // 에러메세지 셋업
          $(".data_link").parent().removeClass("process_failure_next");
          $(".data_link").parent().removeClass("process_success_next");
          $(".data_link").parent().addClass("process_success_next");
          $("#idSpanReturn").text("성공");  // 내보내기 결과 셋업
          alert("Gitlab서버에 내보내기를 완료하였습니다");
          
          /*$.each(data, function() {
            $.each(this, function(k,v){
              console.log("K:" + k + "=>V:" + v);
            });
          });*/
          //--@@console.log("Gitlab:" + projectData.file_path);
        },
        error:function(request,status,error){
          //err_message(status, error);
          $("#idSpanReturn").text("실패");  // 내보내기 결과 셋업
          $("#idSpanErrorCode").text(status); // 에러코드 셋업
          $("#idSpanErrorMsg").text(error);   // 에러메세지 셋업
          $(".data_link").parent().removeClass("process_failure_next");
          $(".data_link").parent().removeClass("process_success_next");
          $(".data_link").parent().addClass("process_failure_next");
          
          alert("Gitlab서버 동기화 중 오류가 발생하여 내보내기 실패\n하였습니다. 잠시 후 다시 진행해 주세요.\n\n계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다");
        }
    });
    
  }
}


function fn_export_GITLAB_(content) {
    var confirmMsg  = g_bIsExsist == true ? "Gitlab서버에 해당 파일이 존재합니다.\n파일을 덮어쓰기 하시겠습니까?" : "내보기내기를 진행하시겠습니까?"
    var szNamespace = "";
    
    szNamespace = typeof $("#projectNamespace").val() == "undefined" ? "" : $("#projectNamespace").val();

    if(szNamespace == "") {
      $("#idSpanReturn").text("실패"); // 내보내기 결과 셋업
        $("#idSpanErrorCode").text("0"); // 에러코드 셋업
        $("#idSpanErrorMsg").text("네임스페이스명이 Null입니다.");   // 에러메세지 셋업
        return;
    } else {
      if(g_szProjectId == "E404") {
        $("#idSpanReturn").text("실패"); // 내보내기 결과 셋업
          $("#idSpanErrorCode").text(g_szProjectId); // 에러코드 셋업
          $("#idSpanErrorMsg").text("Gitlab서버에 " + g_projectName + "프로젝트가 존재하지 않습니다.");   // 에러메세지 셋업
          return;
      }
    }
    
    if(confirm(confirmMsg)) {
      // Gitlab서버에 해당 프로젝트가 존재하지 않습니다. 프로젝트 생성을 하시고 다시 진행해 주세요. 계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다
      // Gitlab서버 동기화 중 네트워크 오류가 발생하여 내보내기 실패했습니다. 잠시 후 다시 진행해 주세요. 계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다
      //alert(g_yamlPath + "Gitlab서버에 내보내기를 완료하였습니다");
      var mothod    = "POST";
      var commitMsg = "create a new file";
      
      if(g_bIsExsist) {
        mothod    = "PUT";
        commitMsg = "update file";
        //alert("Gitlab서버에 '" + g_projectName + ".yaml' 파일이 존재하지 않습니다. 잠시 후 다시 진행해 주세요.\n\n계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다");
        //return;
      }
      
      var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F";
      var szFilePath   = g_projectName + "_%2Eyaml";
      
      //--@@console.log($('#yamlSbst').val());
      var param          = new Object();
      param.branch       = "master";
      param.content      = content;//YAML.stringify(yamlOb);
      param.commit_message = commitMsg;
      
      $.ajax({
          url    : szGitlabHost + szFilePath,
          type   : mothod,
          data   : param,
          async  : false,
          cache  : false,
          crossDomain: true,
          dataType:'json',
          headers: {"PRIVATE-TOKEN": "yyuCn4SDn_N5Abyyzbsa"},
          success: function(data){
            $("#idSpanErrorCode").text(""); // 에러코드 셋업
            $("#idSpanErrorMsg").text("");  // 에러메세지 셋업
            $(".data_link").parent().removeClass("process_failure_next");
            $(".data_link").parent().removeClass("process_success_next");
            $(".data_link").parent().addClass("process_success_next");
            $("#idSpanReturn").text("성공");  // 내보내기 결과 셋업
            alert("Gitlab서버에 내보내기를 완료하였습니다");
            
            /*$.each(data, function() {
              $.each(this, function(k,v){
                console.log("K:" + k + "=>V:" + v);
              });
            });*/
            //--@@console.log("Gitlab:" + data.file_path);
          },
          error:function(request,status,error){
            //err_message(status, error);
            $("#idSpanReturn").text("실패");  // 내보내기 결과 셋업
            $("#idSpanErrorCode").text(status); // 에러코드 셋업
            $("#idSpanErrorMsg").text(error);   // 에러메세지 셋업
            $(".data_link").parent().removeClass("process_failure_next");
            $(".data_link").parent().removeClass("process_success_next");
            $(".data_link").parent().addClass("process_failure_next");
            
            alert("Gitlab서버 동기화 중 오류가 발생하여 내보내기 실패\n하였습니다. 잠시 후 다시 진행해 주세요.\n\n계속 같은 오류가 발생하면 운영팀(adc@kt.com)으로 문의 바랍니다");
          }
      });
      
    }
  }


/* 프로젝트 Yaml 파일 존재유무 확인 함수
 * Gitlab서버에 Yaml파일이 존재하는지 확인하는 함수
 * Program By CYD - 2020.05.07
 */
function fnGetFileInfoFromGitlab() {

  // 작업을 위한 임시 테스트용
  //var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F";
  //var szFilePath   = g_projectName + "%2Eyaml?ref=master";
  var szNamespace  = typeof $("#projectNamespace").val() == "undefined" ? "" : $("#projectNamespace").val();
  var szReturn     = true;
  var szAjaxUr     = "/apidev/api/arsenal/getFileFromGitlabAjax";//szGitlabHost + szFilePath;
  var param        = new Object();
  
  param.projectName = g_projectName;
  //param.userId      = "music6842";
  param.namespace   = szNamespace;

  $.ajax({
      url    : szAjaxUr,
      type   : 'POST',
      data   : JSON.stringify(param),
      async  : false,
      cache  : false,
      crossDomain: true,
      contentType: 'application/json',
      dataType:'json',
      //headers: {"PRIVATE-TOKEN": "yyuCn4SDn_N5Abyyzbsa"},
      success: function(data){
        var projectData = JSON.parse(data.info.jsonResponse);
        
        if(data.projectId != "") {
          
          if(projectData.file_name != g_projectName + ".yaml") {
            g_bIsExsist = false;
          } else {
            g_bIsExsist = true;
          }
          
          g_szProjectId = data.projectId;
          
        } else {
          if(projectData.errorCode == "404") {
            g_bIsExsist   = false;
              g_szProjectId = "E404";
          } else {
            g_bIsExsist   = false;
            g_szProjectId = "E500";
          }
        }
        
        //--@@console.log("Gitlab: " + g_szProjectId);
        //alert(data.errorcode + ": " + data.errordescription);
        //fn_export_GITLAB_(YAML.stringify(data.Content));
        //console.log("ProjectName: " + YAML.stringify(data.Content));
      },
      error:function(request,status,error){
        g_bIsExsist   = false;
        g_szProjectId = "";
        //--@@console.log("Gitlab Error: " + error);
      }
    });
    
  //return szReturn;
}

function fnGetFileInfoFromGitlab1() {

    // 작업을 위한 임시 테스트용
    var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F";
    var szFilePath   = g_projectName + "%2Eyaml?ref=master";
    var szReturn     = true;
    var szAjaxUr     = szGitlabHost + szFilePath;
    var param        = new Object();
    
    param.projectId   = g_szProjectId;
    param.projectName = g_projectName;

    $.ajax({
      url    : szAjaxUr,
      type   : 'GET',
      data   : param,
      async  : false,
      cache  : false,
      crossDomain: true,
      dataType:'json',
      headers: {"PRIVATE-TOKEN": "yyuCn4SDn_N5Abyyzbsa"},
      success: function(data){
        if(data == null || data.file_name != g_projectName + ".yaml") {
          szReturn = false;
        }
        //--@@console.log("Gitlab:" + data.file_name);
        /*$.each(data, function() {
            $.each(this, function(k,v){
              console.log("K:" + k + "=>V:" + v);
            });
          });*/
      },
      error:function(request,status,error){
        szReturn = false;
        //alert("파일 불러오기 실패:" + error);
      }
    });
      
    return szReturn;
}

/* 프로젝트 ID 정보 가져오기 함수
 * Gitlab서버에서 가져온 프로젝트 목록 중에서 프로젝트명에 해당하는 식별값을 읽어옴
 * Program By CYD - 2020.05.15
 */
function fnGetProjectIdFromGitlab(a_szNamespace) {

  // 작업을 위한 임시 테스트용
  var szGitlabHost = $("#idGitlabArsenalHost").val() + "api/v4/projects/";
  var szFilePath   = a_szNamespace + "%2F" + g_projectName;
  var szProjectId  = "0";
  //--@@console.log(a_szNamespace);
  $.ajax({
    url    : szGitlabHost + szFilePath,
    type   : 'GET',
    async  : false,
    cache  : false,
    crossDomain: true,
    dataType:'json',
    headers: {"PRIVATE-TOKEN": "yyuCn4SDn_N5Abyyzbsa"},
    success: function(data){
      if(data != null) {
        szProjectId = data.id;
      }
    },
    error:function(request,status,error){
      szProjectId = "E404";
      //--@@console.log(status + ":" + error);
      //alert("파일 불러오기 실패:" + error);
    }
  });

  return szProjectId;
}

function fn_export_history_ARSENAL() {

  // 동기화이력 함수 호출
  apiArsenalHstAjax();
}

/*
** 권한체크기준
**  1. 등록한 사용자
**  2. 수정권한이 있는 사용자(관리자 및 운영자가 부여)
** 위 두가지 조건을 제외한 나머지 사용자들은 수정불가
**
** CYD - 2020.07.14
*///////////////////////////////////////////////
function fnApiAuthCheck() {
  var b_is_spc_loaded = (($('#pApiSpcNo').val()||'').length > 0);
  if(g_isAuthYn == "N" && b_is_spc_loaded) {
    //alert("수정권한이 없습니다!");
    var btnHtm = "";
    //btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" onclick="fnDeLogin()"  id="cBtton">확인</button> ';
    btnHtm+=' <button type="button" title="확인" class="btn btn_sml3 btn_confirm" id="cCbtn">확인</button> ';
    // 차단 사유가 두 가지다 - 실제 권한 부족과, 신규 등록화면(spcreg)으로 만든 명세라 이 화면에서는
    // 조회만 되는 경우. 후자에 "권한이 없습니다"를 띄우면 사용자가 권한 신청을 하러 가게 되므로 구분한다.
    if (typeof g_readOnlyReason !== 'undefined' && g_readOnlyReason == 'SPCREG') {
      fnOpenLayer(btnHtm, '읽기 전용', '신규 "API 등록" 화면에서 만든 API입니다.<br>이 화면에서는 조회만 가능하며, 수정은 신규 등록 화면에서 해주세요.');
    } else {
      fnOpenLayer(btnHtm, 'API권한경고','권한이 없습니다!<br>권한이 필요하시다면 운영팀(apilink@kt.com)으로 문의 주세요.' );
    }
    return false;
  }
  
  return true;
}
///////////////////////////////////////////////
//-- [CYD][add] }