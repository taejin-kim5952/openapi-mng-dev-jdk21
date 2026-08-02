<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="default">
<%-- //-- [tag:PRJ-20220901] --%>
<script type="text/javascript">
  var g_data = Object.assign((g_data||{}), {
    'usePagination': 'y',
    'pageUnit': 10,
    'pageSize': 10,
  });

  $(document).ready(function() {
    fn_init_handler();
    fn_init_dialog();
    fn_init_page();
  });
  function fn_init_handler() {
    $('.cid_btn_search').on('click', function(p_evt) {
      p_evt.preventDefault(); 
      fn_query_list(1, 'manual');
    });

    $('.cid_enter_search').on('keypress', function(p_evt) {
      if (p_evt.keyCode == 13) {
        p_evt.preventDefault(); $('.cid_btn_search').trigger('click');
      }
    });

    $('#id_chk_listall').on('click', function(p_evt) {
      var use_yn = ($(this).prop('checked') ? 'n' : 'y');
      ui_use_pagination(use_yn);
    });
    
    //-- custom
    $('.cid_act_link_reg').on('click', function () {
      location.href = c_url + 'api/main/mvMainList.do';
    });

    $('#id_q_sel_sys_id').on('change', function () {
      fn_ui_select_systemCategoryList($(this).val());
    });
  }

  function fn_init_dialog() {
    $('#id_popup_itemview').dialog({
      autoOpen: false, width: 800, modal: true,
    });
    $('#id_popup_itemview').find('.btn.btn_black').click(function(p_evt) {
      p_evt.preventDefault();
      $('#id_popup_itemview').dialog('close');
    });
  }
  function fn_init_page() {
    if ('y' != g_data['usePagination']) { $('#id_list_paging').hide(); }
    //-- [i]상단버튼 하단복사
    $('.cid_btn_line').eq(1).append($('.cid_btn_line').eq(0).clone());

    fn_query_list();

    //-- 검색조건-system
    fn_ui_select_systemList();
  }

  function ui_use_pagination(use_yn) {
    g_data['usePagination'] = ((use_yn != 'y') ? 'n' : 'y');
    var is_listall = ('y' != g_data['usePagination']);
    if (is_listall) {
      $('#id_list_paging').hide();
    }
    else {
      $('#id_list_paging').show();
    }
    $('#id_chk_listall').prop('checked', is_listall);
  }

  //-- jquer-ui dialog title 설정
  function ui_popup_set_title(s_popup_id, s_title) {
    $('#' + s_popup_id).siblings('.ui-dialog-titlebar').find('span.ui-dialog-title').text(s_title);
  }
  
  //-- 정보 view popup
  function fn_itemview(sel_rec) {
    var jq_dialog = $('#id_popup_itemview');

    var s_title = '정보 조회-[API_NO: ' + sel_rec.apiNo + '][API_NM: ' + sel_rec.apiNm + ']';
    ui_popup_set_title('id_popup_itemview', s_title);

    var jq_tbody = jq_dialog.find('table > tbody');
    jq_tbody.empty();
    $.each(sel_rec, function(key, val) {
      jq_tbody.append('<tr><th scope="row">' + key + '</th><td>' + val + '</td></tr>');
    });
    jq_dialog.dialog('open');
    jq_dialog.find('.scroll_box').scrollTop(0);
  }
</script>
<script type="text/javascript">
  //-- 페이징 조회
  function pageGo(pageIndex) {
    fn_query_list(pageIndex, 'manual');
  }
  //-- list 검색
  function fn_query_list(pageIndex, s_cmd) {
    var param = new Object();
    param['cmd'] = 'selApiDeployBeastList';
    param['usePagination'] = g_data['usePagination'];
    param['pageUnit'] = g_data['pageUnit'];
    param['pageSize'] = g_data['pageSize'];
    param['pageIndex'] = $sf_int(pageIndex, 1);
  
    param['dplyReqFlag'] = $('#id_q_sel_dply_req_flag').val();
    param['tbDplyStatus'] = $('#id_q_sel_tb_dply_status').val();
    param['dplyVeriStatus'] = $('#id_q_sel_dply_veri_status').val();
    param['prdDplyReqFlag'] = $('#id_q_sel_prd_dply_req_flag').val();
    param['prdDplyStatus'] = $('#id_q_sel_prd_dply_status').val();

    param['sysId'] = $('#id_q_sel_sys_id').val();
    param['apiSpcNo'] = $('#id_q_sel_api_spc_no').val();

    param['dateFlag'] = $('#id_q_sel_date_flag').val();
    param['fromDate'] = $('#id_q_txt_from_date').val();
    param['toDate'] = $('#id_q_txt_to_date').val();
    
    param['apiNm'] = $('#id_q_txt_api_nm').val();

    //--##alert_message( JSON.stringify(param) );
    var s_msg = '';
    //--##if (param['apiNm'].length == 0) { s_msg = 'API명 항목을 선택하세요.'; }

    if (s_msg.length > 0) {
      (('manual' == s_cmd) ? alert_message(s_msg) : void(0)) 
      return;
    }

    var fn_beforeSend = (function(xhr) {
      (('function' == typeof($.ajaxSetup()['beforeSend'])) && ($.ajaxSetup()['beforeSend'])(xhr));
      ui_init_list();
      ui_display_message_list('검색중 입니다...');
      $('.cid_tot_rec_cnt').text('-');
    });
    var fn_error = (function(request, status, error) {
      ui_display_message_list('');
      alert_message('status: ' + request.status + '\n' + 'error: ' + error);
    });
    var fn_success = (function(data, textStatus, request) {
      ui_display_message_list('');
      var b_is_valid_data = (($has_own(data, 'nlist') == true) && ($has_own(data, 'paginationInfo') == true));
      var nlist = [];
      var paginationInfo = {};
      if (true == b_is_valid_data) {
        nlist = data['nlist'];
        paginationInfo = data['paginationInfo'];
        b_is_valid_data = ((Array.isArray(nlist) == true) && (typeof(paginationInfo) == 'object'));
      }
      if (true == b_is_valid_data) {
        var s_msg = ((nlist.length == 0) ? '검색 결과가 없습니다.' : ''); 
        ui_display_list(nlist, paginationInfo, s_msg);
        var tot_rec_cnt = $sf_int($sf_obj_val(paginationInfo, 'totalRecordCount'));
        $('.cid_tot_rec_cnt').text(tot_rec_cnt);
      }
      else {
        alert_message('유효하지 않은 검색 결과 입니다.');
      }
    });
    $.ajax({
      url: '<c:url value="/api/reg/beastDeploy/ajax_query.do"/>' + '?cmd=' + param['cmd'],
      type: 'POST',
      data :param,
      beforeSend: fn_beforeSend,
      success: fn_success, 
      error: fn_error,
    });
  }

  function ui_init_list() {
    //-- init list
    $('.cid_item_list').find('tbody').empty();
    $('#id_list_paging').empty();
  }

  function ui_display_message_list(p_msg) {
    var colspan = $('.cid_item_list > thead > tr').children().length;
    var html = '<tr><td colspan="' + colspan + '">' + p_msg + '</td></tr>';
    $('.cid_item_list').find('tbody').empty().append(html);
  }

  function ui_display_list(p_nlist, p_pginfo, p_msg) {
    ui_init_list();
    if (p_msg.length > 0) {
      ui_display_message_list(p_msg);
    }
    else {
      var jq_tbody = $('.cid_item_list').find('tbody');
      var firstRecordIndex = p_pginfo.firstRecordIndex;
      $.each(p_nlist, function (idx, item) {
        idx += firstRecordIndex;
        var jq_tr = $('<tr></tr>').data('rec', item);
        var html = '';
        html += '<td>';
        html += '  <span title="' + $sf_str(item.apiNo) + '">';
        html += '    <a href="javascript:void(0)" onclick="fn_itemview($(this).closest(\'tr\').data(\'rec\'));">' + (idx + 1) + '</a>';
        html += '  </span>';
        html += '</td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.sysSysNm) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.spcApiNm) + '</span></td>';
        html += '<td><span class="txt_elps"><a href="javascript:fn_link_deploy_view(' + $sf_str(item.apiNo) + ');" >' + $sf_str(item.apiNm) + '</a></span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.apiVer) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.dplyReqFlag) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.tbDplyStatus) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.dplyVeriStatus) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.prdDplyReqFlag) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.prdDplyStatus) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item.amdDt).substr(2, 14) + '</span></td>';
        jq_tr.append(html);
        jq_tbody.append(jq_tr);
      });
      jq_tbody.find('input[name="rdo_rec"]').eq(0).prop('checked', true);
      if ('y' == g_data['usePagination']) {
        //-- totalRecordCount, currentPageNo, recordCountPerPage, pageSize, totalPageCount, firstPageNoOnPageList, lastPageNoOnPageList, firstRecordIndex, lastRecordIndex, firstPageNo, lastPageNo 
        drawPaging('id_list_paging', p_pginfo.currentPageNo, p_pginfo.firstPageNoOnPageList, p_pginfo.totalPageCount, p_pginfo.lastPageNoOnPageList, 'pageGo');
      }
    }
  }
</script>

<script>

  //검색조건 리스트
  function fn_ui_select_systemList() {
    var param = {'direct': 'systemlist_only'};
    $.ajax({
      url: '<c:url value="/api/deploy/mvSelectListAjax.do"/>',
      type: 'POST',
      data: param,
      success: function (data) {
        var b_is_valid_data = $has_own(data, 'systemList');
        if (!b_is_valid_data) { alert_message('유효하지 않은 검색 결과 입니다.');  return; }

        var syshtml = '<option value="">시스템</option>';
        $.each(data.systemList, function (index, item) {
          syshtml += '<option value="' + item.sysId + '">' + item.sysNm + '</option>';
        });
        $('#id_q_sel_sys_id').html(syshtml);
        $('#id_q_sel_api_spc_no').empty();
      },
      error: function (request, status, error) {
        alert('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }
  
  function fn_ui_select_systemCategoryList(sysId) {
    var param = { 'sysId': sysId };
    $.ajax({
      url: '<c:url value="/api/deploy/mvSpcListAjax.do"/>',
      type: 'POST',
      data: param,
      success: function (data) {
        var b_is_valid_data = $has_own(data, 'spcList');
        if (!b_is_valid_data) { alert_message('유효하지 않은 검색 결과 입니다.');  return; }

        var spchtml = '<option value="">전체 카테고리</option>';
        if (data.spcList.length == 0) {
          spchtml = '<option value="">카테고리 정보가 없습니다.</option>';
        }
        else {
          $.each(data.spcList, function (index, item) {
            spchtml += '<option value="' + item.apiSpcNo + '">' + item.apiNm + '</option>';
          });
        }
        $('#id_q_sel_api_spc_no').html(spchtml);
      },
      error: function (request, status, error) {
        alert('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }

  $(function () {
    var from = $('#id_q_txt_from_date').datepicker({
      defaultDate: '+1w',
      changeMonth: false,
      // changeYear: true,
      numberOfMonths: 1
    }).on('change', function () {
      to.datepicker('option', 'minDate', getDate(this));
    });
    var to = $('#id_q_txt_to_date').datepicker({
      defaultDate: '+1w',
      changeMonth: false,
      // changeYear: true,
      numberOfMonths: 1
    }).on('change', function () {
      from.datepicker('option', 'maxDate', getDate(this));
    });

    function getDate(element) {
      var dateFormat = 'yy-mm-dd';
      var date = null;
      try {
        date = $.datepicker.parseDate(dateFormat, element.value);
      } catch (e) {
        date = null;
      }
      return date;
    }
  });

  $.datepicker.setDefaults({
    dateFormat: 'yy-mm-dd',
    prevText: '이전 달',
    nextText: '다음 달',
    monthNames: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
    monthNamesShort: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
    // dayNames: ['일', '월', '화', '수', '목', '금', '토'], dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
    dayNamesMin: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    showMonthAfterYear: true,
    yearSuffix: '.',
    showOtherMonths: true
    // selectOtherMonths: true
  });

  // 상세보기
  function fn_link_deploy_view(apiNo) {
    var jq_form = $('<form></form>').attr('method', 'post').attr('action', '<c:url value="/beast/deploy/mvDeployView.do" />');
    jq_form.append($('<input type="hidden" name="apiNo">').val(apiNo));
    $('body').append(jq_form);
    jq_form.submit();
  }
</script>

<div id="container">
  <div class="sVisual sv_regiapi">
    <div>
      <h2>BEAST API 배포</h2>
      <p>여러분이 생각하는 모든 생각들을 API로 만들고 KT 플랫폼을 이용하여 서비스 해보세요</p>
    </div>
  </div>
  <div class="contents ">
    <div class="conBox">
      <div class="pg_location"><a href="javascript:void(0)">Go home</a> <span>></span>BEAST API 배포</div>
      <div id="content">

        <h5 class="rTitleOneDep">BEAST API 배포</h5>
        <div class="date_setting">
          <div class="searching_wrap">
            <em class="pr10"></em>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:200px;height:40px;" title="시스템 명" id="id_q_sel_sys_id"></select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:200px;height:40px;" title="카테고리" id="id_q_sel_api_spc_no">
                  <option value="">카테고리</option>
                </select>
              </span>
            </div>
            <em class="pl20"> API명 </em>
            <div class="select_form">
              <span class="input_txt wx240"><input type="text" class="cid_enter_search" id="id_q_txt_api_nm" title="API명 " placeholder="API명을 입력하세요."></span>
              <button type="button" class="btn-lg btn_searching cid_btn_search"><span>검색</span></button>
            </div>
          </div><!-- .searching_wrap -->
          <div class="searching_wrap">
            <em class="pr10"> 조회항목 </em>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:170px;height:40px;" title="API배포절차요청상태 " id="id_q_sel_dply_req_flag">
                  <option value="">API배포절차요청상태</option>
                  <option value="REQ">요청</option>
                  <option value="NK">반려</option>
                  <option value="OK">승인</option>
                </select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:170px;height:40px;" title="API배포상태 -TB" id="id_q_sel_tb_dply_status">
                  <option value="">API배포상태-TB</option>
                  <option value="NK">배포실패</option>
                  <option value="OK">배포성공</option>
                </select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:170px;height:40px;" title="API검증상태" id="id_q_sel_dply_veri_status">
                  <option value="">API검증상태</option>
                  <option value="NK">검증실패</option>
                  <option value="OK">검증성공</option>
                </select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:170px;height:40px;" title="API상용배포요청상태 " id="id_q_sel_prd_dply_req_flag">
                  <option value="">API상용배포요청상태</option>
                  <option value="REQ">요청</option>
                  <option value="NK">반려</option>
                  <option value="OK">승인</option>
                </select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box">
                <select class="cid_enter_search" style="width:170px;height:40px;" title="API배포상태 -상용" id="id_q_sel_prd_dply_status">
                  <option value="">API배포상태-상용</option>
                  <option value="NK">배포실패</option>
                  <option value="OK">배포성공</option>
                </select>
              </span>
            </div>
          </div><!-- .searching_wrap -->

          <div class="searching_wrap">
            <em class="pr10">상태일자 </em>
            <div class="select_form">
              <span class="combo_box">
                <select style="width:150px;height:40px;" title=상태구분  id="id_q_sel_date_flag">
                  <option value="">상태일자구분</option>
                  <option value="dplyReqFlag">배포절차요청상태</option>
                  <option value="tbDplyStatus">TB배포상태</option>
                  <option value="dplyVeriStatus">검증상태</option>
                  <option value="prdDplyReqFlag">상용배포요청상태</option>
                  <option value="prdDplyStatus">상용배포상태</option>
                </select>
              </span>
            </div>
            <div class="select_form">
              <span class="combo_box mr05"><input class="wx90 cid_enter_search" type="text" id="id_q_txt_from_date" placeholder="시작일자"></span>~
            </div>
            <div class="select_form">
              <span class="combo_box"><input class="wx90 cid_enter_search" type="text" id="id_q_txt_to_date" placeholder="종료일자"></span>
            </div>
          </div><!-- .searching_wrap -->
        </div><!-- .date_setting -->

        <div class="btn_set-right">
          <a href="javascript:void(0);"><input type="checkbox" id="id_chk_listall" name="usePagination" title="모두보기"><label for="id_chk_listall" ><span></span>모두보기</label></a>
        </div>

        <div class="btn_set-right cid_btn_line">
          <p class="list_count">전체: <span class="cid_tot_rec_cnt">-</span> 건</p>

          <a href="javascript:void(0)" title="등록하기" class="btn btn_black"><span class="btn btn_black cid_act_link_reg">등록하기</span></a>
        </div><!-- .btn_set-right.cid_btn_line -->

        <div class="pkg_board">
          <table class="table-list cid_item_list">
            <caption>BEAST API 배포 list</caption>
            <colgroup>
              <col style="width:60px">
              <col style="width:10%">
              <col style="width:10%">
              <col style="width:auto">
              <col style="width:60px">
              <col style="width:70px">
              <col style="width:70px">
              <col style="width:70px">
              <col style="width:70px">
              <col style="width:70px">
              <col style="width:110px">
            </colgroup>
            <thead>
              <tr>
                <th scope="row">#</th>
                <th scope="row">시스템명</th>
                <th scope="row">카테고리</th>
                <th scope="row">API명</th>
                <th scope="row">버전</th>
                <th scope="row">배포절차<br/>요청상태</th>
                <th scope="row">TB배포<br/>상태</th>
                <th scope="row">검증상태</th>
                <th scope="row">상용배포<br/>요청상태</th>
                <th scope="row">상용배포<br/>상태</th>
                <th scope="row">최종수정일</th>
              </tr>
            </thead>
            <tbody>
            </tbody>
          </table><!-- .table-list -->

          <div class="paging" id="id_list_paging"></div>

        </div><!-- .pkg_board -->
      </div><!-- #content -->
    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->

<!-- 정보조회 -->
<div id="id_popup_itemview" class="dp_none">
  <div class="popup_content mb0">
    <div class="pkg_board scroll_box">

      <table class="table-vw2">
        <caption>정보 view Table</caption>
        <colgroup>
          <col style="width:25%" />
          <col style="width:auto" />
        </colgroup>
        <tbody>
          <tr><th scope="row"><!-- title --></th><td><!-- value --></td></tr>
        </tbody>
      </table>

    </div><!-- .pkg_board -->

    <div class="lPop_bottom brd_tp mt10">
      <button type="button" class="btn btn_black">확인</button>
    </div>

  </div><!-- .popup_content mb0 -->
</div><!-- #id_popup_itemview -->
</t:layout>
