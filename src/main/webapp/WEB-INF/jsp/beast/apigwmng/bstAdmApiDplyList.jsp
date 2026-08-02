<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="commonFunc" uri="/WEB-INF/tld/CommonFunc.tld" %>
<%@ page import="com.kt.openapi.web.util.CommonFunc"%>
<%@ page import="com.kt.openapi.web.beast.apigw.constant.BstgwConstant"%>

<t:layout type="default">
<%--
  //-- [tag:SR-20230113]
  //-- [i]prntsApiId 항목 규격추가 // List<String>
  //-- PRNTS_API_ID // varchar(1000) // ';'구분 문자열
--%>
<c:set var="bIsBstgwManager" value="${commonFunc:isSpecificUser('bstgw.manager')}" />
<c:set var="bIsBstgwViewer" value="${commonFunc:isSpecificUser('bstgw.viewer')}" />
<%-- <c:set var="bIsTargetTb" value="${attr_target eq 'TB'}" /> --%>
<c:set var="dp_target_text" value="(${attr_target})" />

<%-- //-- [tag:PRJ-20220901] --%>
<script type="text/javascript">
  var g_data = Object.assign((g_data||{}), {
    'usePagination': 'y',
    'pageUnit': 20,
    'pageSize': 10,
    'expItemKey': 'exp_item_key_bstAdmApiDplyList',
    'target': '${attr_target}'.toUpperCase(),
	'azureYn': ''
  });

  $(document).ready(function() {
    fn_init_handler();
    fn_init_dialog();
    fn_init_page();
	localStorage.removeItem(g_data['expItemKey']);
  });
  function fn_init_handler() {
    $('.cid_btn_search').on('click', function(p_evt) {
      p_evt.preventDefault();	
      fn_query_list(1, 'manual');
    });

    $('.cid_enter_search').on('keypress', function(p_evt) {
      if (p_evt.keyCode == 13) {
        p_evt.preventDefault();	$('.cid_btn_search').trigger('click');
      }
    });

    $('#id_chk_listall').on('click', function(p_evt) {
      var use_yn = ($(this).prop('checked') ? 'n' : 'y');
      ui_use_pagination(use_yn);
    });

    $('#id_popup_edit .cid_sel_edpt_prot').on('click change', function(p_evt) {
      var prot = $sf_str($(this).val()).toLowerCase();
      var jq_dialog = $('#id_popup_edit');
      jq_dialog.find('.cid_opt_edpt_prot').hide();
      jq_dialog.find('.cid_opt_edpt_prot_' + prot).show();
    });
    
    $('.cid_btn_bst_api_test_direct').on('mouseover', function(p_evt) {
      if ($(this).is('.cid_api_get_list')) {
        var jq_target = $(this);
        var jq_direct = jq_target.find('.cid_bst_api_test_direct');
        if (jq_direct.length == 0) {
          jq_target.css({'position':'relative'});
          jq_direct = $('<div class="p10 cid_bst_api_test_direct" onmouseleave="$(this).hide();" style="display:flex;position:absolute;top:30px;left:0px;z-index:999;background:lightgrey;border-radius:10px;"></div>');
          jq_direct.append($('<span><button type="button" class="mr10 pl05 pr05 btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_get_list cid_direct"><span>All</span></button></span>'));
          jq_direct.append($('<span><button type="button" class="mr10 pl05 pr05 btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_get_list cid_direct cid_direct_dplytype_dply"><span>DPLY</span></button></span>'));
          jq_direct.append($('<span><button type="button" class="mr10 pl05 pr05 btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_get_list cid_direct cid_direct_dplytype_del"><span>DEL</span></button></span>'));
          jq_target.append(jq_direct);
        }
        else {
          jq_direct.show();
        }
      }
      else if ($(this).is('.cid_api_post')) {
        var jq_target = $(this);
        var jq_direct = jq_target.find('.cid_bst_api_test_direct');
        if (jq_direct.length == 0) {
          jq_target.css({'position':'relative'});
          jq_direct = $('<div class="p10 cid_bst_api_test_direct" onmouseleave="$(this).hide();" style="display:flex;position:absolute;top:30px;left:0px;z-index:999;background:lightgrey;border-radius:10px;"></div>');
          jq_direct.append($('<span><button type="button" class="mr10 pl05 pr05 btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_post cid_direct cid_direct_dplytype_dply"><span>DPLY</span></button></span>'));
          jq_direct.append($('<span><button type="button" class="mr10 pl05 pr05 btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_post cid_direct cid_direct_dplytype_del"><span>DEL</span></button></span>'));
          jq_target.append(jq_direct);
        }
        else {
          jq_direct.show();
        }
      }
    });
    $('.cid_btn_bst_api_test_direct').on('mouseleave', function(p_evt) {
      $('.cid_bst_api_test_direct').hide();
    });

    $(document).on('click', '.cid_btn_bst_api_test', function(p_evt) {
      p_evt.preventDefault();
      var api_dir = '';
      api_dir = ($(this).is('.cid_api_get_list') ? 'api-get-list' : api_dir);
      api_dir = ($(this).is('.cid_api_get_list.cid_direct_dplytype_dply') ? 'api-get-list-dply' : api_dir);
      api_dir = ($(this).is('.cid_api_get_list.cid_direct_dplytype_del') ? 'api-get-list-del' : api_dir);
      api_dir = ($(this).is('.cid_api_get_item') ? 'api-get' : api_dir);
      api_dir = ($(this).is('.cid_api_post.cid_direct_dplytype_dply') ? 'api-dply' : api_dir);
      api_dir = ($(this).is('.cid_api_post.cid_direct_dplytype_del') ? 'api-del' : api_dir);
      
      fn_beast_api_test(api_dir);
    });
}

  function fn_init_dialog() {
    $('#id_popup_edit').dialog({
      autoOpen: false, width: 1080, modal: true,
    });
    $('#id_popup_edit').find('.btn.btn_cancel').click(function(p_evt) {
      p_evt.preventDefault();
      $('#id_popup_edit').dialog('close');
    });
    $('#id_popup_itemview').dialog({
      autoOpen: false, width: 800, modal: true,
    });
    $('#id_popup_itemview').find('.btn.btn_cancel').click(function(p_evt) {
      p_evt.preventDefault();
      $('#id_popup_itemview').dialog('close');
    });
  }
  function fn_init_page() {
    if ('y' != g_data['usePagination']) { $('#id_list_paging').hide(); }
    //-- [i]상단버튼 하단복사
    $('.cid_btn_line').eq(1).append($('.cid_btn_line').eq(0).clone());

    fn_query_list();
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

  //-- #id_popup_edit popup input clear
  function ui_popup_edit_clear() {
    $('#id_popup_edit').find('.cid_input').val('');
  }

  //-- jquer-ui dialog title 설정
  function ui_popup_set_title(s_popup_id, s_title) {
    $('#' + s_popup_id).siblings('.ui-dialog-titlebar').find('span.ui-dialog-title').text(s_title);
  }

  //-- 선택 정보 edit popup
  function fn_create() {
    fn_edit('insert', {});
  }

  function fn_update() {
    var jq_rdo_rec = $('input[name="rdo_rec"]:checked');
    if (jq_rdo_rec.length == 0) { alert_message('선택정보가 없습니다.'); return; }
    var sel_rec = jq_rdo_rec.closest('tr').data('rec');
    fn_edit('update', sel_rec);
  }

  function fn_view() {
    var jq_rdo_rec = $('input[name="rdo_rec"]:checked');
    if (jq_rdo_rec.length == 0) { alert_message('선택정보가 없습니다.'); return; }
    var sel_rec = jq_rdo_rec.closest('tr').data('rec');
    fn_edit('view', sel_rec);
  }

  function fn_edit(s_mode, sel_rec) {
    var jq_dialog = $('#id_popup_edit');
    
    ui_popup_edit_clear();

    //-- mode에 따른 화면설정
    jq_dialog.find('.cid_ui_mode').hide();
    //-- key field dis/enable
    var is_readonly = ('insert' != s_mode);
    jq_dialog.find('.cid_txt_api_id').prop('readonly', is_readonly);

    jq_dialog.find('.cid_txt_dply_type').prop('readonly', true);
    
    if ('insert' == s_mode) {
      ui_popup_set_title('id_popup_edit', 'API 정보' + '${dp_target_text}' + ' 등록');
      jq_dialog.find('.cid_ui_mode_insert').show();

      jq_dialog.find('.cid_txt_dply_dt').val($fn_fmt_date('fmt_ymd', (new Date()), '-') + 'T' + $fn_fmt_date('fmt_hms', (new Date()), ':') + '.000');
      jq_dialog.find('.cid_txt_dply_type').val('DPLY');
			
      jq_dialog.find('.cid_sel_edpt_prot').trigger('click');
    }
    else if (('update' == s_mode) || ('view' == s_mode)) {
      if ('update' == s_mode) {
        ui_popup_set_title('id_popup_edit', 'API 정보' + '${dp_target_text}' + ' 수정');
        jq_dialog.find('.cid_ui_mode_update').show();
      }
      else {
        ui_popup_set_title('id_popup_edit', 'API 정보' + '${dp_target_text}' + ' 조회');
        jq_dialog.find('.cid_ui_mode_view').show();
      }

      jq_dialog.find('.cid_txt_dply_dt'             ).val(sel_rec['dplyDt']);
      jq_dialog.find('.cid_txt_dply_type'           ).val(sel_rec['dplyType']);
      jq_dialog.find('.cid_txt_api_id'              ).val(sel_rec['apiId']);
      jq_dialog.find('.cid_txt_sys_id'              ).val(sel_rec['sysId']);
      jq_dialog.find('.cid_txt_if_no'               ).val(sel_rec['ifNo']);
      jq_dialog.find('.cid_txt_ver'                 ).val(sel_rec['ver']);
      jq_dialog.find('.cid_txt_meth'                ).val(sel_rec['meth']);
      jq_dialog.find('.cid_txt_uri_in'              ).val(sel_rec['uriIn']);
      jq_dialog.find('.cid_txt_uri_out'             ).val(sel_rec['uriOut']);
      jq_dialog.find('.cid_txt_req_hndlr'           ).val(sel_rec['reqHndlr']);
      jq_dialog.find('.cid_txt_res_hndlr'           ).val(sel_rec['resHndlr']);
      jq_dialog.find('.cid_txt_err_hndlr'           ).val(sel_rec['errHndlr']);
      jq_dialog.find('.cid_txt_time_out'            ).val(sel_rec['timeOut']);
      jq_dialog.find('.cid_txt_prnts'               ).val(sel_rec['prnts']);
      jq_dialog.find('.cid_txt_prnts_api_id'        ).val(sel_rec['prntsApiId']);
      jq_dialog.find('.cid_txt_hndlr_optn'          ).val(sel_rec['hndlrOptn']);
      jq_dialog.find('.cid_txt_mask'                ).val(sel_rec['mask']);
      jq_dialog.find('.cid_txt_atrib_in_fmt'        ).val(sel_rec['atribInFmt']);
      jq_dialog.find('.cid_txt_atrib_out_fmt'       ).val(sel_rec['atribOutFmt']);
      jq_dialog.find('.cid_txt_atrib_in_comn_param' ).val(sel_rec['atribInComnParam']);
      jq_dialog.find('.cid_txt_atrib_out_comn_param').val(sel_rec['atribOutComnParam']);
      jq_dialog.find('.cid_txt_src_tag'             ).val(sel_rec['srcTag']);
      jq_dialog.find('.cid_txt_src_key'             ).val(sel_rec['srcKey']);
      jq_dialog.find('.cid_txt_def_api_no'          ).val(sel_rec['defApiNo']);
    }

    jq_dialog.dialog('open');
  }

  function fn_delete() {
    var jq_rdo_rec = $('input[name="rdo_rec"]:checked');
    if (jq_rdo_rec.length == 0) { alert_message('선택정보가 없습니다.'); return; }
    var sel_rec = jq_rdo_rec.closest('tr').data('rec');
    
    if (!confirm('선택된 정보를 삭제 하시겠습니까?')) { return; }
    
    var rec = { 'seq': sel_rec['seq'] };
    if (true == $is_empty(rec['seq'])) { alert_message('[err][seq 설정없음]'); return; };
    if (false == $is_positive_number(rec['seq'])) { alert_message('[err][seq 형식오류]'); return; };
    
    proc_item('delete', rec);
  }
  
  //-- 수정저장
  function fn_edit_proc(s_mode) {
    var jq_dialog = $('#id_popup_edit');
    
    if ('dev_api_send' == s_mode) {
      var param = new Object();
      param['cmd'] = 'cmd_api_send';
      param['api_target'] = '';
      param['api_domain'] = 'http://127.0.0.1:19080/apidev/beast';
      param['api_url'] = '/apilink/v1/api/getApiDplyList';
      param['api_method'] = 'GET';
      param['api_body'] = JSON.stringify(param);
      
      $.ajax({
        type: 'POST',
        url: '<c:url value="/beast/api/common/ajax_proc.do"/>' + '?cmd=' + param['cmd'],
        data: JSON.stringify(param),
        contentType: 'application/json',
        dataType: 'JSON',
        async: false,
        success: function(data) {
          var s_json = $sf_json_stringify(data);
          alert_message(s_json);
          $console_log('o-o', 'data: ', data);
        },
        error: function(request, status, error) {
	        //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
        }
      });
      return;
    }

    if ('dev_test' == s_mode) {
      jq_dialog.find('.cid_txt_dply_dt'             ).val($fn_fmt_date('fmt_ymd', (new Date()), '-') + 'T' + $fn_fmt_date('fmt_hms', (new Date()), ':') + '.000');
      jq_dialog.find('.cid_txt_dply_type'           ).val('DPLY');
      jq_dialog.find('.cid_txt_api_id'              ).val('testApi');
      jq_dialog.find('.cid_txt_sys_id'              ).val('SCAP-ID-001');
      jq_dialog.find('.cid_txt_if_no'               ).val('HRSYSTEM-0100');
      jq_dialog.find('.cid_txt_ver'                 ).val('v1');
      jq_dialog.find('.cid_txt_meth'                ).val('GET;POST');
      jq_dialog.find('.cid_txt_uri_in'              ).val('/v1/in/authHandlerTest');
      jq_dialog.find('.cid_txt_uri_out'             ).val('/v1/out/authHandlerTest');
      jq_dialog.find('.cid_txt_req_hndlr'           ).val('REQ.AUTH;REQ.API-AUT;REQ.SLA;REQ.IP-ACES-AUTH');
      jq_dialog.find('.cid_txt_res_hndlr'           ).val('RES.AUTH;RES.API-AUT;RES.SLA;REQ.IP-ACES-AUTH');
      jq_dialog.find('.cid_txt_err_hndlr'           ).val('ERR.AUTH');
      jq_dialog.find('.cid_txt_time_out'            ).val('10000');
      jq_dialog.find('.cid_txt_prnts'               ).val('true');
      jq_dialog.find('.cid_txt_prnts_api_id'        ).val('getCustInfo;getMapInfo;postInfo');
      jq_dialog.find('.cid_txt_hndlr_optn'          ).val($sf_json_stringify({'option_1':'value_1', 'option_2':'value_2'}));
      jq_dialog.find('.cid_txt_mask'                ).val('fn_01;fn_02');
      jq_dialog.find('.cid_txt_atrib_in_fmt'        ).val('KHUB');
      jq_dialog.find('.cid_txt_atrib_out_fmt'       ).val('SDP');
      jq_dialog.find('.cid_txt_atrib_in_comn_param' ).val('TYPE_B');
      jq_dialog.find('.cid_txt_atrib_out_comn_param').val('TYPE_A');
      jq_dialog.find('.cid_txt_src_tag'             ).val('APILINK');
      jq_dialog.find('.cid_txt_src_key'             ).val('');
      jq_dialog.find('.cid_txt_def_api_no'          ).val('12345');
      
      return;
    }
    
    var rec = {};

    rec['dplyDt'] = $sf_str(jq_dialog.find('.cid_txt_dply_dt').val());
    rec['dplyType'] = $sf_str(jq_dialog.find('.cid_txt_dply_type').val());
    rec['apiId'] = $sf_str(jq_dialog.find('.cid_txt_api_id').val());
    rec['sysId'] = $sf_str(jq_dialog.find('.cid_txt_sys_id').val());
    rec['ifNo'] = $sf_str(jq_dialog.find('.cid_txt_if_no').val());
    rec['ver'] = $sf_str(jq_dialog.find('.cid_txt_ver').val());
    rec['meth'] = $sf_str(jq_dialog.find('.cid_txt_meth').val());
    rec['uriIn'] = $sf_str(jq_dialog.find('.cid_txt_uri_in').val());
    rec['uriOut'] = $sf_str(jq_dialog.find('.cid_txt_uri_out').val());
    rec['reqHndlr'] = $sf_str(jq_dialog.find('.cid_txt_req_hndlr').val());
    rec['resHndlr'] = $sf_str(jq_dialog.find('.cid_txt_res_hndlr').val());
    rec['errHndlr'] = $sf_str(jq_dialog.find('.cid_txt_err_hndlr').val());
    rec['timeOut'] = $sf_str(jq_dialog.find('.cid_txt_time_out').val());
    rec['prnts'] = $sf_str(jq_dialog.find('.cid_txt_prnts').val()).toLowerCase();
    rec['prntsApiId'] = $sf_str(jq_dialog.find('.cid_txt_prnts_api_id').val());
    rec['hndlrOptn'] = $sf_str(jq_dialog.find('.cid_txt_hndlr_optn').val());
    rec['mask'] = $sf_str(jq_dialog.find('.cid_txt_mask').val());
    rec['atribInFmt'] = $sf_str(jq_dialog.find('.cid_txt_atrib_in_fmt').val());
    rec['atribOutFmt'] = $sf_str(jq_dialog.find('.cid_txt_atrib_out_fmt').val());
    rec['atribInComnParam'] = $sf_str(jq_dialog.find('.cid_txt_atrib_in_comn_param').val());
    rec['atribOutComnParam'] = $sf_str(jq_dialog.find('.cid_txt_atrib_out_comn_param').val());
    rec['srcTag'] = $sf_str(jq_dialog.find('.cid_txt_src_tag').val());
    rec['srcKey'] = $sf_str(jq_dialog.find('.cid_txt_src_key').val());
    rec['defApiNo'] = $sf_str(jq_dialog.find('.cid_txt_def_api_no').val());

    if (true == $is_empty(rec['dplyDt'])) { alert_message('[err][dplyDt 입력없음]'); return false; };
    if (true == $is_empty(rec['dplyType'])) { alert_message('[err][dplyType 입력없음]'); return false; };
    if (true == $is_empty(rec['apiId'])) { alert_message('[err][apiId 입력없음]'); return false; };
    if (true == $is_empty(rec['sysId'])) { alert_message('[err][sysId 입력없음]'); return false; };
    if (true == $is_empty(rec['ifNo'])) { alert_message('[err][ifNo 입력없음]'); return false; };
    
    //-- [i][null field type check]
    if (!$is_empty(rec['timeOut']) && !$is_number(rec['timeOut'])) { alert_message('[err][timeOut 형식오류]'); return false; }
    if (!$is_empty(rec['prnts']) && (rec['prnts'] != 'true') && (rec['prnts'] != 'false')) { alert_message('[err][prnts 형식오류]'); return false; }
    if (!$is_empty(rec['defApiNo']) && !$is_number(rec['defApiNo'])) { alert_message('[err][apiId 형식오류]'); return false; }

    //-- [i][hndlrOptn validation] {
    //-- [i]hndlrOptn: {request: 'json_string', response: 'json_string', ...}
    var o_hndlrOptn = fn_fmt_hndlrOptn(rec['hndlrOptn']);
    if (null == o_hndlrOptn) { alert_message('[err][hndlrOptn 형식오류]'); return false; }
    var a_valid_key_list = 'request;response;config;custom'.split(';');
    var s_invalid_key = '';
    for (key in o_hndlrOptn) {
      if (a_valid_key_list.indexOf(key.toLowerCase()) == -1) {
        s_invalid_key = key;
        break;
      }
    }
    if (!$is_empty(s_invalid_key)) { alert_message('[err][hndlrOptn에 정의되지 않은 키 포함][invalid key: ' + s_invalid_key + ']'); return false; }
    //--@@if (!$has_own(o_hndlrOptn, 'request')) { alert_message('[err][hndlrOptn.request정보 없음]'); return false; }
    //--@@if (!$has_own(o_hndlrOptn, 'response')) { alert_message('[err][hndlrOptn.response정보 없음]'); return false; }
    jq_dialog.find('.cid_txt_hndlr_optn').val($sf_json_stringify(o_hndlrOptn));
    //-- [i][hndlrOptn validation] }

    if ('get_item' == s_mode) {
      return rec;
    }

    if ('insert' == s_mode) {
      var o_item = fn_query_item({'apiId': rec['apiId'] });
      var s_msg = '';
      if (null == o_item) {
        s_msg = '중복검색 오류';
      }
      else {
        var fv_apiId = $sf_obj_val(o_item, 'API_ID');
        if (fv_apiId == rec['apiId']) {
          s_msg = '[err][apiId 중복값]';
        }
      }
      if (s_msg.length > 0) { alert_message(s_msg); return; }
    }

    if (('insert' == s_mode) || ('update' == s_mode)) {
      //-- [i][hndlrOptn foramtting]
      //-- [i][must $sf_json_stringify() for convert JSONObject() // JSONObject jso_rec = jso_body.optJSONObject("rec")
      rec['hndlrOptn'] = $sf_json_stringify(fn_fmt_hndlrOptn(rec['hndlrOptn']));
    }
    
    proc_item(s_mode, rec);
    
    jq_dialog.dialog('close');
  }

  //-- 정보처리
  function proc_item(s_mode, rec) {
    var param = new Object();
    param['cmd'] = 'cmd_db_tran';
    param['target'] = g_data['target'];
    param['mode'] = s_mode;
    if ('insert' == s_mode) {
      param['rec'] = rec;
    }
    else if ('update' == s_mode) {
      param['rec'] = rec;
    }
    else if ('delete' == s_mode) {
      param['rec'] = rec;
    }
    else {
      return;
    }
    $.ajax({
      type: 'POST',
      url: '<c:url value="/beast/api/bstAdmApiDply/ajax_proc.do"/>' + '?cmd=' + param['cmd'],
      data: JSON.stringify(param),
      contentType: 'application/json',
      dataType: 'JSON',
      async: false,
      success: function(data) {
        var b_is_valid_data = $has_own(data, 'returnCd');
        b_is_valid_data &= $has_own(data, 'returnMsg');
        if (!b_is_valid_data) { alert_message('유효하지 않은 처리 결과 입니다.');  return; }

        var returnCd = $sf_obj_val(data, 'returnCd');
        var returnMsg = $sf_obj_val(data, 'returnMsg');
        var result = $sf_obj_val(data, 'result', '#N/A#');
        var s_msg = '';
        if ('OK' == returnCd) {
          s_msg = '처리 되었습니다.';
          s_msg += '\n\n[result: ' + result + ']';
          fn_query_list();
        }
        else {
          s_msg = '[err][returnCd: ' + returnCd + ']\n[returnMsg: ' + returnMsg + ']';
        }
        if (s_msg.length > 0) {
          window.setTimeout(function() { alert_message(s_msg); }, 100);
        }
      },
      error: function(request, status, error) {
	      //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }

  //-- 정보 view popup
  function fn_itemview(sel_rec) {
    var jq_dialog = $('#id_popup_itemview');

    var s_title = '정보 조회-[seq: ' + sel_rec['seq'] + '][apiId: ' + sel_rec['apiId'] + ']';
    ui_popup_set_title('id_popup_itemview', s_title);

    var jq_tbody = jq_dialog.find('table > tbody');
    jq_tbody.empty();
    var a_item_key = $sf_arr(g_a_item_key, []);
    $.each(a_item_key, function(sub_idx, sub_item) {
      if ($has_own(sel_rec, sub_item) == true) {
        var s_tit = fn_get_export_item_title(sub_item);
        if (s_tit.length > 0) {
          var s_val = sel_rec[sub_item];
          jq_tbody.append('<tr><th scope="row">' + s_tit + '</th><td>' + s_val + '</td></tr>');
        }
      }
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
    param['cmd'] = 'cmd_db_list';
    param['target'] = g_data['target'];
    param['usePagination'] = g_data['usePagination'];
    param['pageUnit'] = g_data['pageUnit'];
    param['pageSize'] = g_data['pageSize'];
    param['pageIndex'] = $sf_int(pageIndex, 1);
  
    param['apiId'] = $('#id_q_api_id').val();
    param['sysId'] = $('#id_q_sys_id').val();
    param['ifNo'] = $('#id_q_if_no').val();
    param['srcTag'] = $('#id_q_src_tag').val();
    param['dplyType'] = $('#id_q_dply_type').val();

    //--##alert_message( JSON.stringify(param) );
    var s_msg = '';
    //--##if (param['apiId'].length == 0) { s_msg = 'API ID를 입력하세요.'; }

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
      url: '<c:url value="/beast/api/bstAdmApiDply/ajax_query.do"/>' + '?cmd=' + param['cmd'],
      type: 'POST',
      data :param,
      beforeSend: fn_beforeSend,
      success: fn_success, 
      error: fn_error,
    });
  }

  //-- item 검색
  function fn_query_item(o_param) {
    o_param = $sf_obj(o_param);
    var param = new Object();
    param['cmd'] = 'cmd_db_item';
    param['target'] = g_data['target'];
    param = Object.assign(param, $sf_obj(o_param));

    var o_item = null;
    
    var fn_error = (function(request, status, error) {
      ui_display_message_list('');
      alert_message('status: ' + request.status + '\n' + 'error: ' + error);
    });
    var fn_success = (function(data, textStatus, request) {
      ui_display_message_list('');
      var b_is_valid_data = ($has_own(data, 'nlist') == true);
      var nlist = [];
      if (true == b_is_valid_data) {
        nlist = data['nlist'];
        b_is_valid_data = (Array.isArray(nlist) == true);
      }
      if (true == b_is_valid_data) {
        o_item = ((nlist.length > 0) ? nlist[0] : {}); 
      }
    });
    $.ajax({
      url: '<c:url value="/beast/api/bstAdmApiDply/ajax_query.do"/>' + '?cmd=' + param['cmd'],
      type: 'POST',
      data :param,
      async: false,
      success: fn_success, 
      error: fn_error,
    });

    return o_item;
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
        html += '  <span class="chk_agree">';
        html += '    <a href="javascript:void(0)">';
        html += '      <input type="radio" name="rdo_rec" id="id_rdo_' + idx + '" title="정보선택">';
        html += '      <label for="id_rdo_' + idx + '"><span></span></label>';
        html += '    </a>';
        html += '  </span>';
        html += '</td>';
        html += '<td>';
        html += '  <span title="' + $sf_str(item['seq']) + '">';
        html += '    <a href="javascript:void(0)" onclick="fn_itemview($(this).closest(\'tr\').data(\'rec\'));">' + (idx + 1) + '</a>';
        html += '  </span>';
        html += '</td>';
        html += '<td><span class="txt_elps" title="' + $sf_str(item['apiId']) + '">' + $sf_str(item['apiId']) + '</span></td>';
        html += '<td><span class="txt_elps" title="' + $sf_str(item['sysId']) + '">' + $sf_str(item['sysId']) + '</span></td>';
        html += '<td><span class="txt_elps" title="' + $sf_str(item['ifNo']) + '">' + $sf_str(item['ifNo']) + '</span></td>';
        html += '<td><span class="txt_elps" title="' + $sf_str(item['uriIn']) + '">' + $sf_str(item['uriIn']) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item['srcTag']) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item['dplyDt']).substr(2, 14) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item['dplyType']) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item['defApiNo']) + '</span></td>';
        html += '<td><span class="txt_elps">' + $sf_str(item['udate']).substr(2, 14) + '</span></td>';
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
  
  function fn_beast_api_test(api_dir) {
    var api_info = {
      'api-get-list': ['/apilink/v1/api/getApiDplyList', 'GET'],
      'api-get-list-dply': ['/apilink/v1/api/getApiDplyList', 'GET', '?dplyType=DPLY'],
      'api-get-list-del': ['/apilink/v1/api/getApiDplyList', 'GET', '?dplyType=DEL'],
      'api-get': ['/apilink/v1/api/getApiDplyById', 'GET'],
      'api-dply': ['/apilink/v1/api/apiDply', 'POST'],
      'api-del': ['/apilink/v1/api/apiDply', 'POST'],
    };
    
    //-- API_ID -> apiId
    var prvfn_objkey_camelCase = (function(o_rec) {
      o_rec = $sf_obj(o_rec);
      var o_new_rec = {};
      for (key in o_rec) {
        if (o_rec.hasOwnProperty(key)) {
          var new_key = key.toLowerCase().replace(/(_.)/g, function(c) { return c.slice(1).toUpperCase(); } );
          o_new_rec[new_key] = o_rec[key];
        }
      }
      return o_new_rec;
    });
  
    var req_url = $sf_str($sf_arr($sf_obj_val(api_info, api_dir))[0]);
    var req_method = $sf_str($sf_arr($sf_obj_val(api_info, api_dir))[1]);
    var req_qstr = $sf_str($sf_arr($sf_obj_val(api_info, api_dir))[2]);
    var req_body = null;
    var a_direct = [];

    if (req_url.length == 0) { alert_message('설정된 request url 정보가 없습니다.'); return; }
    if (req_method.length == 0) { alert_message('설정된 request method 정보가 없습니다.'); return; }
  
    var jq_rdo_rec = $('input[name="rdo_rec"]:checked');
    
    if ('api-get' == api_dir) {
      var apiId = '';
      if ($('#id_popup_edit').is(':visible')) {
        apiId = $sf_str($('#id_popup_edit .cid_txt_api_id').val());
        if (apiId.length == 0) { alert_message('[err][apiId 입력없음]'); return; }
      }
      else {
        if (jq_rdo_rec.length == 0) { alert_message('선택정보가 없습니다.'); return; }
        var sel_rec = jq_rdo_rec.closest('tr').data('rec');
        apiId = $sf_str(sel_rec['apiId']);
        if (apiId.length == 0) { alert_message('[err][apiId 설정없음]'); return; }
      }
      req_qstr = '?apiId=' + encodeURIComponent(apiId);
      a_direct.push('api-get');
      a_direct.push('import_data');
    }
    else if (('api-dply' == api_dir) || ('api-del' == api_dir)) {
      var sel_rec
      if (('api-dply' == api_dir) && ($('#id_popup_edit').is(':visible'))) {
        sel_rec = fn_edit_proc('get_item');
        if (false === sel_rec) {	//-- [i]validation fail
          return;
        }
      }
      else {
        if (jq_rdo_rec.length == 0) { alert_message('선택정보가 없습니다.'); return; }
        sel_rec = prvfn_objkey_camelCase(jq_rdo_rec.closest('tr').data('rec'));
      }
      //-- [i][set dplyDt, dplyType]
      sel_rec['dplyDt'] = $fn_fmt_date('fmt_ymd', (new Date()), '-') + 'T' + $fn_fmt_date('fmt_hms', (new Date()), ':');
      sel_rec['dplyType'] = (('api-del' == api_dir) ? 'DEL' : 'DPLY');
      req_body = fn_make_payload('apiDply', sel_rec);
    }
    else if (('api-get-list' == api_dir) || ('api-get-list-dply' == api_dir) || ('api-get-list-del' == api_dir)) {
      a_direct.push('api-get-list');
      a_direct.push('import_data');
    }
    
    var test_param = {
      'req_url': req_url,
      'req_method': req_method,
      'req_qstr': req_qstr,
      'req_body': $sf_json_stringify(req_body),
      'direct': a_direct.join(';')
    }
  
    if ('function' == typeof(bstapi_fn_popup)) {
      bstapi_fn_popup(test_param);
    }
  }
  
  function fn_make_payload(direct, sel_rec) {
    var o_payload = null;
    if ('apiDply' == direct) {
      o_payload = {
        'dplyDt': $sf_obj_val(sel_rec, 'dplyDt'),
        'dplyType': $sf_obj_val(sel_rec, 'dplyType'),
        'apiId': $sf_obj_val(sel_rec, 'apiId'),
        'sysId': $sf_obj_val(sel_rec, 'sysId'),
        'ifNo': $sf_obj_val(sel_rec, 'ifNo'),
        'ver': $sf_obj_val(sel_rec, 'ver'),
        'meth': $sf_obj_val(sel_rec, 'meth'),
        'in': $sf_obj_val(sel_rec, 'uriIn'),
        'out': $sf_obj_val(sel_rec, 'uriOut'),
        'reqHndlr': $sf_obj_val(sel_rec, 'reqHndlr'),
        'resHndlr': $sf_obj_val(sel_rec, 'resHndlr'),
        'errHndlr': $sf_obj_val(sel_rec, 'errHndlr'),
        'timeOut': $sf_obj_val(sel_rec, 'timeOut'),
        'prnts': $sf_obj_val(sel_rec, 'prnts'),
        'prntsApiId': $sf_obj_val(sel_rec, 'prntsApiId'),
        'hndlrOptn': $sf_obj_val(sel_rec, 'hndlrOptn'),
        'mask': $sf_obj_val(sel_rec, 'mask'),
        'atrib': {
          'inFmt': $sf_obj_val(sel_rec, 'atribInFmt'),
          'outFmt': $sf_obj_val(sel_rec, 'atribOutFmt'),
          'inComnParam': $sf_obj_val(sel_rec, 'atribInComnParam'),
          'outComnParam': $sf_obj_val(sel_rec, 'atribOutComnParam')
        }
      };
      if (!$is_empty(o_payload['meth'])) { o_payload['meth'] = $sf_str(o_payload['meth']).split(';'); }
      else { o_payload['meth'] = []; }
      if (!$is_empty(o_payload['reqHndlr'])) { o_payload['reqHndlr'] = $sf_str(o_payload['reqHndlr']).split(';'); }
      else { o_payload['reqHndlr'] = []; }
      if (!$is_empty(o_payload['resHndlr'])) { o_payload['resHndlr'] = $sf_str(o_payload['resHndlr']).split(';'); }
      else { o_payload['resHndlr'] = []; }
      if (!$is_empty(o_payload['mask'])) { o_payload['mask'] = $sf_str(o_payload['mask']).split(';'); }
      else { o_payload['mask'] = []; }
      if ($is_integer(o_payload['timeOut'])) { o_payload['timeOut'] = $sf_int(o_payload['timeOut']); }
      else { delete o_payload['timeOut']; }
      if ('true' === $sf_str(o_payload['prnts']).toLowerCase()) { o_payload['prnts'] = true; }
      else if ('false' === $sf_str(o_payload['prnts']).toLowerCase()) { o_payload['prnts'] = false; }
      else { delete o_payload['prnts']; }
      if (!$is_empty(o_payload['prntsApiId'])) { o_payload['prntsApiId'] = $sf_str(o_payload['prntsApiId']).split(';'); }
      else { o_payload['prntsApiId'] = []; }
      var s_hndlrOptn = $sf_str(o_payload['hndlrOptn']);
      if ($is_empty(s_hndlrOptn.length)) {
          o_payload['hndlrOptn'] = null;
      }
      else {
        o_payload['hndlrOptn'] = fn_fmt_hndlrOptn(s_hndlrOptn);
        if (null == o_payload['hndlrOptn']) {
          delete o_payload['hndlrOptn'];
        }
      }
    }
    return o_payload;
  }

  function fn_fmt_hndlrOptn(s_hndlrOptn) {
    var ret = null;
    //-- [i]for json_parse
    var lfn_trim_ctrl = (function(str) {
      return str.replace(/[\t\n\v\f\r\0]/g, '');
    });

    if ($is_json_str(s_hndlrOptn)) {
      //-- [i]hndlrOptn: {request: 'json_string', response: 'json_string', ...}
      var o_hndlrOptn = $sf_obj($sf_json_parse(lfn_trim_ctrl(s_hndlrOptn)));
      for (key in o_hndlrOptn) {
        var val = o_hndlrOptn[key];
        if (typeof(val) != 'string') {
          val = ($is_json_obj(val) ? $sf_json_stringify(val) : val);  //-- [i]must json_string
        }
        o_hndlrOptn[key] = val; 
      }
      ret = o_hndlrOptn;
    }
    return ret;
  }
</script>
<script type="text/javascript">
  var g_a_item_key = [];  //-- export item key
  var g_a_def_item_key = [
    'dplyDt',
    'dplyType',
    'apiId',
    'sysId',
    'ifNo',
    'ver',
    'meth',
    'uriIn',
    'uriOut',
    'reqHndlr',
    'resHndlr',
    'errHndlr',
    'timeOut',
    'prnts',
    'prntsApiId',
    'hndlrOptn',
    'mask',
    'atribInFmt',
    'atribOutFmt',
    'atribInComnParam',
    'atribOutComnParam',
    'srcTag',
    'srcKey',
    'defApiNo',
    'udate',
    'rdate',
  ];

  //-- export대상 항목
  var g_o_exp_item_data = {
    'dplyDt'              :' 배포 일자',
    'dplyType'            :' 배포 유형',
    'apiId'               :' API ID(BEAST)',
    'sysId'               : '시스템 ID(BEAST)',
    'ifNo'                : '인터페이스 번호',
    'ver'                  : '버전',
    'meth'                 : '메소드',
    'uriIn'               : 'URI IN',
    'uriOut'              : 'URI OUT',
    'reqHndlr'            : '요청 핸들러',
    'resHndlr'            : '응답 핸들러',
    'errHndlr'            : '에러 핸들러',
    'timeOut'             : '타임 아웃',
    'prnts'                : '부모 여부',
    'prntsApiId'         : '부모 API ID',
    'hndlrOptn'           : '핸들러 옵션',
    'mask'                 : '마스킹 대상',
    'atribInFmt'         : '속성-요청 포맷',
    'atribOutFmt'        : '속성-응답 포맷',
    'atribInComnParam'  : '속성-요청 공통 파라미터',
    'atribOutComnParam' : '속성-응답 공통 파라미터',
    'srcTag'              : '정보출처',
    'srcKey'              : '정보출처 관련Key',
    'defApiNo'           : 'API NO(DEF)',
    'udate'                :' 최종수정일',
    'rdate'                :' 최초작성일',
  };
  
  //-- 엑셀저장
  function fn_excel() {
    if (g_a_item_key.length == 0) { alert_message('출력항목 설정이 없습니다.'); return; }
    var a_data = xlsexp_fn_get_export_data(g_a_item_key);
    if (a_data.length == 0) { alert_message('대상 정보가 없습니다.'); return; }
    
    if (!confirm('엑셀파일을 저장 하시겠습니까?')) { return; }

    var excel_cfg = {
      filename: 'export_' + $fn_fmt_date('fmt_ymd', (new Date())) + '_' + $fn_fmt_date('fmt_hms', (new Date())),
      format: 'xlsx',   //-- 'xlsx'/'xls'/'csv'
      sheet_name: $fn_fmt_date('fmt_ymdhms_kor', (new Date())),
      sheet_data: a_data,
    };
    gfn_ExcelExport(excel_cfg);
  }
</script>

<div id="container">
  <div class="sVisual sv_regiapi">
    <div>
      <h2>BEAST - API 정보${dp_target_text}</h2>
      <p>BEAST 솔루션의 API 정보를 관리 합니다</p>
    </div>
  </div>
  <div class="contents">
    <div class="conBox">
      <div class="pg_location"><a href="javascript:void(0);">Go home</a> <span>></span> BEAST 관리</div>

      <div id="content">

        <h5 class="rTitleOneDep">API 정보${dp_target_text} 관리</h5>

        <div class="date_setting">
          <div class="searching_wrap">
            <div class="select_form ">
              <span class="pr10">API ID</span>
              <span class="input_txt wx100"><input class="cid_enter_search" type="text" id="id_q_api_id"></span>
              <span class="pr10 pl10">시스템 ID</span>
              <span class="input_txt wx100"><input class="cid_enter_search" type="text" id="id_q_sys_id"></span>
              <span class="pr10 pl10">유형</span>
              <span class="pr10">
                <select class="cid_input cid_enter_search" id="id_q_dply_type">
                  <option value="">전체</option>
                  <option value="DPLY">DPLY</option>
                  <option value="DEL">DEL</option>
                </select>
              </span>
              <button type="button" class="btn-lg btn_searching cid_btn_search"><span>검색</span></button>

              <div class="btn_block_right">
                <span class="cid_btn_bst_api_test_direct cid_api_get_list"><button type="button" class="btn btn_sml btn_lightGray"><span>API-GET-list</span></button></span>
                <span><button type="button" class="btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_get_item"><span>API-GET-item</span></button></span>
<c:if test="${bIsBstgwManager}">
                <span class="cid_btn_bst_api_test_direct cid_api_post"><button type="button" class="btn btn_sml btn_lightGray"><span>API-POST</span></button></span>
</c:if>
              </div>
            </div>
          </div><!-- .searching_wrap -->
          <div class="searching_wrap">
            <div class="select_form">
              <span class="pr10">IF 번호</span>
              <span class="input_txt wx100 pr10"><input class="cid_enter_search" type="text" id="id_q_if_no"></span>
              <span class="pr10">정보출처</span>
              <span class="input_txt wx140 pr10"><input class="cid_enter_search" type="text" id="id_q_src_tag"></span>

<c:if test="${bIsBstgwManager}">
              <!--<div class="btn_block_right">
                <span><button type="button" class="btn btn_sml btn_lightGray wx140 cid_btn_bst_api_deploy_payload"><span>API-DPLY-payload</span></button></span>
              </div>-->
</c:if>
            </div>
          </div><!-- .searching_wrap -->
        </div><!-- .date_setting -->

        <div class="btn_set-right">
          <a href="javascript:void(0);"><input type="checkbox" id="id_chk_listall" name="usePagination" title="모두보기"><label for="id_chk_listall" ><span></span>모두보기</label></a>
        </div>

        <div class="btn_set-right cid_btn_line">
          <p class="list_count">전체: <span class="cid_tot_rec_cnt">-</span> 건</p>

<c:choose>
<c:when test="${bIsBstgwManager}">
          <button type="button" class="btn btn_sml2 btn_black" onclick="fn_create()"><span>등록</span></button>
          <button type="button" class="btn btn_sml2 btn_black" onclick="fn_update()"><span>수정</span></button>
          <button type="button" class="btn btn_sml2 btn_black" onclick="fn_delete()"><span>삭제</span></button>
</c:when>
<c:otherwise>
          <button type="button" class="btn btn_sml2 btn_black" onclick="fn_view()"><span>조회</span></button>
</c:otherwise>
</c:choose>
          <span class="wx53" style="display:inline-block"></span>
          <button type="button" class="btn btn_sml2 btn_black" onclick="fn_excel()"><span>엑셀저장</span></button>
          <button type="button" class="btn btn_sml2 btn_black" onclick="xlsexp_fn_export_item('popup')"><span>항목설정</span></button>
        </div><!-- .btn_set-right.cid_btn_line -->

        <div class="pkg_board">
          <table class="table-list cid_item_list">
            <caption>BEAST API list</caption>
            <colgroup>
              <col style="width:40px" />
              <col style="width:60px" />
              <col style="width:280px" />
              <col style="width:120px" />
              <col style="width:120px" />
              <col style="width:auto">
              <col style="width:70px" />
              <col style="width:110px" />
              <col style="width:60px" />
              <col style="width:60px" />
              <col style="width:110px" />
            </colgroup>
            <thead>
              <tr>
                <th scope="col">선택</th>
                <th scope="col">#</th>
                <th scope="col">API ID</th>
                <th scope="col">시스템 ID</th>
                <th scope="col">IF 번호</th>
                <th scope="col">URI IN</th>
                <th scope="col">정보출처</th>
                <th scope="col">배포 일자</th>
                <th scope="col">배포<br/>유형</th>
                <th scope="col">DEF<br/>API NO</th>
                <th scope="col">수정일</th>
              </tr>
            </thead>
            <tbody class="td_thin"></tbody>
          </table><!-- .table-list -->

          <div class="paging" id="id_list_paging"></div><!-- #paging.paging -->
          
        </div><!-- .pkg_board -->
        
        <div class="btn_set-right cid_btn_line"></div><!-- .btn_set-right.cid_btn_line -->
        

      </div><!-- .content -->

    </div><!-- .conBox -->
  </div><!-- .contents -->
</div><!-- #container -->

<!-- 정보edit -->
<div id="id_popup_edit" class="dp_none">
  <div class="popup_content mb0">
    <div class="pkg_board">

      <table class="table-vw2">
        <caption>정보edit Table</caption>
        <colgroup>
          <col style="width:12%" />
          <col style="width:21%" />
          <col style="width:12%" />
          <col style="width:21%" />
          <col style="width:12%" />
          <col style="width:22%" />
        </colgroup>
        <tbody>
          <tr>
            <th scope="row">API ID</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_api_id" title="API_ID" maxlength="50"></span>
            </td>
            <th scope="row">배포 일자</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_dply_dt" title="DPLY_DT" maxlength="30" placeholder="yyyy-MM-ddTHH:mm:ss"></span>
            </td>
            <th scope="row">
                        배포 유형
              <div class="cid_ui_mode cid_ui_mode_update">
                <a href="javascript:void(0);" onclick="$('#id_popup_edit .cid_txt_dply_type').val('DPLY');">[DPLY]</a><a href="javascript:void(0);" onclick="$('#id_popup_edit .cid_txt_dply_type').val('DEL');">[DEL]</a>
              </div>
           </th>
            <td>
              <span><input type="text" class="cid_input cid_txt_dply_type" title="DPLY_TYPE" maxlength="10"></span>
            </td>
          </tr>
          <tr>
            <th scope="row">시스템 ID</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_sys_id" title="SYS_ID" maxlength="50"></span>
            </td>
            <th scope="row">인터페이스 번호</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_if_no" title="IF_NO" maxlength="50"></span>
            </td>
            <th scope="row">버전</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_ver" title="VER" maxlength="50"></span>
            </td>
          </tr>
          <tr>
            <th scope="row">메소드</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_meth" title="METH" maxlength="100" placeholder="Array(';'구분)"></span>
            </td>
            <th scope="row">URI IN</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_uri_in" title="URI_IN" maxlength="200"></span>
            </td>
            <th scope="row">URI OUT</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_uri_out" title="URI_OUT" maxlength="200"></span>
            </td>
          </tr>
          <tr>
            <th scope="row">요청 핸들러</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_req_hndlr" title="REQ_HNDLR" maxlength="1000" placeholder="Array(';'구분)"></span>
            </td>
            <th scope="row">응답 핸들러</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_res_hndlr" title="RES_HNDLR" maxlength="1000" placeholder="Array(';'구분)"></span>
            </td>
            <th scope="row">에러 핸들러</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_err_hndlr" title="ERR_HNDLR" maxlength="100"></span>
            </td>
          </tr>
          <tr>
            <th scope="row">타임 아웃</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_time_out" title="TIME_OUT" maxlength="50" placeholder="숫자"></span>
            </td>
            <th scope="row">마스킹 대상</th>
            <td colspan="3">
              <span><input type="text" class="cid_input cid_txt_mask" title="MASK" maxlength="3000" placeholder="Array(';'구분)"></span>
            </td>
          </tr>
          <tr>
            <th scope="row">부모 여부</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_prnts" title="PRNTS" maxlength="10" placeholder="true 또는 false"></span>
            </td>
            <th scope="row">부모 API ID</th>
            <td colspan="3">
              <span><input type="text" class="cid_input cid_txt_prnts_api_id" title="PRNTS_API_ID" maxlength="1000" placeholder="Array(';'구분)"></span>
            </td>
          </tr>
          <tr class="cid_opt_edpt_prot cid_opt_edpt_prot_socket">
            <th scope="row">핸들러 옵션</th>
            <td colspan="5">
              <span><textarea class="cid_input cid_txt_hndlr_optn" title="HNDLR_OPTN" maxlength="8000" placeholder="JSON 문자열" style="height:80px"></textarea></span>
            </td>
          </tr>
          <tr>
            <th scope="row">요청 포맷</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_atrib_in_fmt" title="ATRIB_IN_FMT" maxlength="100"></span>
            </td>
            <th scope="row">요청<br/>공통 파라미터</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_atrib_in_comn_param" title="ATRIB_IN_COMN_PARAM" maxlength="100"></span>
            </td>
            <td conspan="2"></td>
          </tr>
          <tr>
            <th scope="row">응답 포맷</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_atrib_out_fmt" title="ATRIB_OUT_FMT" maxlength="100"></span>
            </td>
            <th scope="row">응답<br/>공통 파라미터</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_atrib_out_comn_param" title="ATRIB_OUT_COMN_PARAM" maxlength="100"></span>
            </td>
            <td conspan="2"></td>
          </tr>
          <tr>
            <th scope="row">정보출처</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_src_tag" title="SRC_TAG" maxlength="100"></span>
            </td>
            <th scope="row">정보출처 관련Key</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_src_key" title="SRC_KEY" maxlength="100"></span>
            </td>
            <th scope="row">API NO(DEF)</th>
            <td>
              <span><input type="text" class="cid_input cid_txt_def_api_no" title="DEF_API_NO" maxlength="10" placeholder="숫자"></span>
            </td>
          </tr>
        </tbody>
      </table>
    </div><!-- .pkg_board -->

    <div class="lPop_bottom brd_tp mt10 cid_ui_mode cid_ui_mode_insert">
      <button type="button" title="등록" class="btn btn_sml2 btn_black" onclick="fn_edit_proc('insert')">등록</button>
      <button type="button" title="취소" class="btn btn_sml2 btn_cancel">취소</button>
      <button type="button" class="btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_post cid_direct_dplytype_dply"><span>API-POST-dply</span></button>
      <button type="button" class="btn btn_sml btn_lightGray cid_btn_bst_api_test cid_api_get_item"><span>API-GET-item</span></button>

      <button type="button" title="dev_test" class="btn btn_white" onclick="fn_edit_proc('dev_test')">dev_test</button>
    </div>

    <div class="lPop_bottom brd_tp mt10 cid_ui_mode cid_ui_mode_update">
      <input type="hidden" class="cid_txt_status_group_no">
      <button type="button" title="수정" class="btn btn_sml2 btn_black" onclick="fn_edit_proc('update')">수정</button>
      <button type="button" title="취소" class="btn btn_sml2 btn_cancel">취소</button>

      <!-- <button type="button" title="dev_api_send" class="btn btn_white" onclick="fn_edit_proc('dev_api_send')">dev_api_send</button> -->
    </div><!-- .pkg_board -->

    <div class="lPop_bottom brd_tp mt10 cid_ui_mode cid_ui_mode_view">
      <button type="button" title="확인" class="btn btn_sml2 btn_black btn_cancel">확인</button>
    </div><!-- .pkg_board -->

  </div><!-- .popup_content mb0 -->
</div><!-- #id_popup_edit -->

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
      <button type="button" class="btn btn_black btn_cancel">확인</button>
    </div>

  </div><!-- .popup_content mb0 -->
</div><!-- #id_popup_itemview -->

<!-- beast api deploy payload -->
<script>
  $(document).ready(function() {
    $('#id_popup_bst_api_deploy_payload').dialog({
      autoOpen: false, width: 800, modal: true,
    });
    $('#id_popup_bst_api_deploy_payload').find('.btn.btn_cancel').click(function(p_evt) {
      p_evt.preventDefault();
      $('#id_popup_bst_api_deploy_payload').dialog('close');
    });

    $(document).on('click', '.cid_btn_bst_api_deploy_payload', function(p_evt) {
      p_evt.preventDefault(); 
      fn_popup_bst_api_deploy_payload();
    });
    $('#id_popup_bst_api_deploy_payload .cid_sub_btn_generate').on('click', function(p_evt) {
      p_evt.preventDefault(); 
      fn_get_bst_api_deploy_payload();
    });
    
    $('#id_popup_bst_api_deploy_payload .cid_sub_enter_search').on('keypress', function(p_evt) {
      if (p_evt.keyCode == 13) {
        p_evt.preventDefault(); $('#id_popup_bst_api_deploy_payload .cid_sub_btn_generate').trigger('click');
      }
    });
  });
  
  //-- beast api deploy payload
  function fn_popup_bst_api_deploy_payload() {
    var jq_dialog = $('#id_popup_bst_api_deploy_payload');

    var s_title = 'BEAST API DEPOLY PAYLOAD';
    ui_popup_set_title('id_popup_bst_api_deploy_payload', s_title);
    
    jq_dialog.find('.cid_q_api_no, .cid_ta_payload').val('');
    
    jq_dialog.dialog('open');
    jq_dialog.find('.scroll_box').scrollTop(0);
  }

  function fn_get_bst_api_deploy_payload() {
    var apiNo = $('#id_popup_bst_api_deploy_payload .cid_q_api_no').val();
    if (true == $is_empty(apiNo)) { alert_message('[err][apiNo 입력없음]'); return; };
    if (false == $is_positive_number(apiNo)) { alert_message('[err][apiNo 형식오류]'); return; };
    
    $('#id_popup_bst_api_deploy_payload .cid_ta_payload').val('');

    var param = new Object();
    param['cmd'] = 'cmd_get_bst_api_deploy_payload';
    param['api_no'] = apiNo;
    param['target'] = g_data['target'];
    param['dplytype'] = 'DPLY';
    param['direct'] = '#exclude_null;pretty_json';
    $.ajax({
      type: 'POST',
      url: '<c:url value="/beast/api/common/ajax_proc.do"/>' + '?cmd=' + param['cmd'],
      data: JSON.stringify(param),
      contentType: 'application/json',
      dataType: 'JSON',
      success: function(data) {
        var b_is_valid_data = $has_own(data, 'returnCd');
        b_is_valid_data &= $has_own(data, 'returnMsg');
        if (!b_is_valid_data) { alert_message('유효하지 않은 처리 결과 입니다.');  return; }

        var returnCd = $sf_obj_val(data, 'returnCd');
        var returnMsg = $sf_obj_val(data, 'returnMsg');
        var result = $sf_obj_val(data, 'result', '#N/A#');
        var s_msg = '';
        if ('OK' == returnCd) {
          var s_json = result;
          $('#id_popup_bst_api_deploy_payload .cid_ta_payload').val(s_json);
          //--##$console_log('o-o', 'data: ', data);
        }
        else {
          s_msg = '[err][returnCd: ' + returnCd + ']\n[returnMsg: ' + returnMsg + ']';
        }
        if (s_msg.length > 0) {
          window.setTimeout(function() { alert_message(s_msg); }, 100);
        }
      },
      error: function(request, status, error) {
	      //--@@console.log('code: ' + request.status + '\n' + 'error: ' + error);
      }
    });
  }
</script>
<div id="id_popup_bst_api_deploy_payload" class="dp_none">
  <div class="popup_content mb0">
    <div class="date_setting">
      <div class="searching_wrap">
        <div class="select_form ">
          <span class="pr10">API_NO</span>
          <span class="input_txt"><input class="cid_sub_enter_search cid_q_api_no" type="text"></span>
          <button type="button" class="btn-lg btn_searching cid_sub_btn_generate"><span>작성</span></button>
        </div>
      </div><!-- .searching_wrap -->
    </div><!-- .date_setting -->

    <div class="pkg_board scroll_box">

      <table class="table-vw2">
        <caption>BEAST API DEPLOY PAYLOAD Table</caption>
        <colgroup>
          <col style="width:auto" />
        </colgroup>
        <tbody>
          <tr>
            <td>
              <span>
                <textarea class="cid_ta_payload" style="height:500px"></textarea>
               </span>
            </td>
          </tr>
        </tbody>
      </table>

    </div><!-- .pkg_board -->

    <div class="lPop_bottom brd_tp mt10">
      <button type="button" class="btn btn_black btn_cancel">확인</button>
    </div>

  </div><!-- .popup_content mb0 -->
</div><!-- #id_popup_bst_api_deploy_payload -->

<jsp:include page="/WEB-INF/jsp/beast/inc/beastApiTest_inc.jsp" />

<jsp:include page="/WEB-INF/jsp/beast/inc/excelExport_inc.jsp" />
</t:layout>
