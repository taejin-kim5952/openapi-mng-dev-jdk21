<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div id="popup_apiclone" title="API복사" style="display:none;">
  <div class="popup_content">
    <div class="pkg_board">
      <table class="table-vw">
        <caption>API복사 Table</caption>
        <colgroup>
          <col style="width: 30%;">
          <col>
        </colgroup>
        <tbody>
          <tr>
            <th scope="row"><div>작업구분</div></th>
            <td>
              <div>
                <div class="radio_set">
                  <em><input type="radio" id="id_apiclone_radio_01" name="apiclone_radio_gub" title="API복사/작성" value="copy" checked><label for="id_apiclone_radio_01"><span></span>API복사/작성</label></em>
                  <em><input type="radio" id="id_apiclone_radio_02" name="apiclone_radio_gub" title="API버전업" value="verup"><label for="id_apiclone_radio_02"><span></span>API버전업</label></em>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <th scope="row"><div>API그룹</div></th>
            <td>
              <select id="id_apiclone_sel_apigrp" class="sel_apigrp" title="API그룹">
                <option value="">선택</option>
              </select>
            </td>
          </tr>
          <tr class="apiver_editline cid_apiclone_apiver_editline">
            <th scope="row"><div>버전정보</div></th>
            <td>
              <input class="txt_apiver_before" type="text" name="apiclone_txt_apiVer_before" readonly>
              =>
              <input class="txt_apiver" type="text" name="apiclone_txt_apiVer" title="API버전">
            </td>
          </tr>
        </tbody>
      </table>
      <input type="hidden" id="id_apiclone_method">
      <input type="hidden" id="id_apiclone_path">
      <input type="hidden" id="id_apiclone_apino_src">
    </div><!-- .pkg_board -->
    <div class="lPop_bottom brd_tp">
      <button type="button" title="확인" onclick="javascript:fn_proc_apiclone()" class="btn btn_black btn_sml3">확인</button>
      <button type="button" title="취소" onclick="javascript:fn_popup_apiclone_close()" class="btn btn_sml3">취소</button>
    </div><!-- .lPop_bottom -->
  </div><!-- .popup_content -->
</div><!-- #popup_apiclone -->
<script>
  $(document).ready(function() {
    $('#popup_apiclone').dialog({
      autoOpen: false, width: 600, modal: true, resizable: false
    });
    
    $(':input:radio[name=apiclone_radio_gub]').on('click', function(p_evt) {
      var apiclone_gub = $(':input:radio[name=apiclone_radio_gub]:checked').val();
      (('verup' == apiclone_gub) ? $('.cid_apiclone_apiver_editline').show() : $('.cid_apiclone_apiver_editline').hide()); 
    });
  });
  function fn_proc_apiclone() {
    var jq_form = $('#apiInfoForm');
    if (jq_form.length == 0) {
      alert_message('요청작업을 수행할 수  없습니다. - [err: missing form]', 'API 등록');
      return;
    }

    var apiclone_gub = $(':input:radio[name=apiclone_radio_gub]:checked').val();
    var cateNm = $sf_str($('#id_apiclone_sel_apigrp option:selected').val());
    
    var method = $sf_str($('#id_apiclone_method').val());
    var path = $sf_str($('#id_apiclone_path').val());
    var apiNoSrc = $sf_str($('#id_apiclone_apino_src').val());
    var copyYn = '';
    var apiVer = $sf_str($(':input[name=apiclone_txt_apiVer]').val());

    if ('verup' == apiclone_gub) {
      copyYn = 'V';
    }
    else if ('copy' == apiclone_gub) {
      copyYn = 'Y';
    }
    else {
      alert_message('작업구분을 선택해 주세요.', 'API 등록'); return;
    }

    if (cateNm.length == 0) { alert_message('API그룹을 선택해 주세요.', 'API 등록'); return; }

    var cateNo = selCateNo(cateNm);
    if ((cateNo > 0) == false) { alert_message('API정보 오류 입니다. - [err: cateno]', 'API 등록'); return; }

    if (path.length == 0) { alert_message('API정보 오류 입니다. - [err: path]', 'API 등록'); return; }
    if (method.length == 0) { alert_message('API정보 오류 입니다. - [err: method]', 'API 등록'); return; }
    if (apiNoSrc.length == 0) { alert_message('API정보 오류 입니다. - [err: source apino]', 'API 등록'); return; }
    
    var apiVerNo = selApiVerNo(apiNoSrc);
    if ((apiVerNo > 0) == false) { alert_message('API정보 오류 입니다. - [err: apiverno]', 'API 등록'); return; }

    if ('V' == copyYn) {
      if (apiVer.length == 0) { alert_message('API버전을 입력해 주세요.', 'API 등록'); return; }
      if (fn_is_valid_api_version(apiVer) == false) { alert_message('잘못된 API버전 형식 입니다.\n\ne.g. v1.2', 'API 등록'); return; }
      var apiVer_before = $sf_str($(':input[name=apiclone_txt_apiVer_before]').val());
      if (apiVer == apiVer_before) { alert_message('변경될 API버전을 입력해 주세요.', 'API 등록'); return; }
      //-- apiVer element추가
      var jq_apiVer = jq_form.find(':input[name=apiVer]');
      if (jq_apiVer.length == 0) {
        jq_apiVer = $('<input type="hidden" name="apiVer" />');
        jq_form.append(jq_apiVer);
      }
      jq_apiVer.val(apiVer);
      //-- apiVerNo element추가
      var jq_apiVerNo = jq_form.find(':input[name=apiVerNo]');
      if (jq_apiVerNo.length == 0) {
        jq_apiVerNo = $('<input type="hidden" name="apiVerNo" />');
        jq_form.append(jq_apiVerNo);
      }
      jq_apiVerNo.val(apiVerNo);
    }

    //-- disable beforeunload handler
    $(window).off('beforeunload');

    jq_form.find('#pApiNo').val('');
    jq_form.find('#pApiCtgryNo').val(cateNo);
    jq_form.find('#pApiCtgryNm').val(cateNm);
    jq_form.find('#pApiPath').val(path);
    jq_form.find('#pApiMethod').val(method);
    jq_form.find('#pApiCopyYn').val(copyYn);
    jq_form.attr('action', mvApiPathUrl).submit();

    $('#popup_apiclone').dialog('close');
  }

  function fn_popup_apiclone_open(mode, method, path, cateNm, apiNoSrc) {
    $('#id_apiclone_method').val(method);
    $('#id_apiclone_path').val(path);
    $('#id_apiclone_apino_src').val(apiNoSrc);

    //-- api그룹 select설정
    var o_category = $sf_obj_val(yamlOb, 'x-category', {});
    var jq_sel = $('#id_apiclone_sel_apigrp');
    jq_sel.find('option').remove();
    $.each(o_category, function(key, item) {
      var option_value = key;
      var option_text = key;
      var selected = ((key == cateNm) ? ' selected ' : '');
      jq_sel.append('<option value="' + option_value + '" ' + selected + '>' + option_text + '</option>');
    });
    
    var is_disable_copy = (mode == 'verup_only');
    var is_disable_verup = (mode == 'copy_only');
    
    $(':input:radio[name=apiclone_radio_gub][value=copy]').prop('disabled', is_disable_copy); //-- copy선택 안됨
    $(':input:radio[name=apiclone_radio_gub][value=verup]').prop('disabled', is_disable_verup); //-- verup선택 안됨

    var apiclone_gub = (is_disable_verup ? 'copy' : (is_disable_copy ? 'verup' : ''));
    if (apiclone_gub.length > 0) {
      $(':input:radio[name=apiclone_radio_gub][value=' + apiclone_gub + ']').prop('checked', true);
    }

    $(':input[name=apiclone_txt_apiVer_before]').val(fn_get_version_in_path(path)).prop('readonly', true);
    $(':input[name=apiclone_txt_apiVer]').val('v');
    $(':input:radio[name=apiclone_radio_gub]:checked').trigger('click');

    $('#popup_apiclone').dialog('open');
  }
  function fn_popup_apiclone_close(s_sel) {
    $('#popup_apiclone').dialog('close');
  }

  function fn_ui_set_versionup(path, apiVer) {
    $(':input[name=summary]').prop('readonly', true);
    $(':input[name=path]').prop('readonly', true);
    $(':input[name=apiId]').prop('readonly', true);
    $('.cid_opt_not_verup_case').hide();
    var newVerApiPath = fn_replace_version_in_path(path, apiVer);
    fn_set_input_value('path', newVerApiPath); // path 세팅
  }
  //-- api version group no 조회
  function selApiVerNo(apiNo) {
    var apiVerNo = 0;
    var param = { 'apiNo': apiNo, }
    $.ajax({
      url    : '<c:url value="/api/reg/selApiVerNoAjax.do"/>', type   : 'POST', data   : param, async  : false, cache  : false,
      success: function(data){
        apiVerNo = data.apiVerNo;
      },
      error:function(request, status, error) {
        err_message(status, error);
      }
    });
    return apiVerNo;
  }
</script>