/* quickApiReg.js - "빠른 API 등록" 화면 전용 스크립트 (디자인 리뉴얼판).
   기존 등록 마법사의 apiInfoReg.js 등은 참조하지 않고 이 화면만을 위해 작성했다.
   공용 유틸(alert_message 등)은 apiGlobalScript.js, YAML 파싱/아이콘 규칙은
   quickRegShared.js(템플릿 관리 화면과 공유)에서 가져다 쓴다. */

var g_qr_cm = null; // CodeMirror instance (lazy init)
var g_qr_detailSnapshot = null;
var g_qr_selectedTmpltNm = null;

var g_qr_paramScope = 'in'; // 파라미터 미리보기에서 현재 활성 탭(설계 버튼이 어느 스코프를 열지 결정)

$(document).ready(function () {
  qrInitTmpltIcons();
  qrOnMethodChange();
  qrOnApiClassChange();

  ptRenderPreview('qr', qrRefreshSummary);
  ptBindPreviewToggle('qr');
  ptBindDesigner();

  $('.qr_pv_tab').on('click', function () {
    var scope = $(this).attr('data-scope');
    g_qr_paramScope = scope;
    $('.qr_pv_tab').removeClass('qr_on');
    $(this).addClass('qr_on');
    $('.qr_pv_pane').addClass('qr_hide');
    $('#qrPv_' + scope).removeClass('qr_hide');
    $('#qrBtnOpenParamModal').text(PT_SCOPES[scope].label + ' 설계');
  });
  $('#qrBtnOpenParamModal').on('click', function () {
    var mNm = $('#methodCd option:selected').text() || '';
    ptOpenDesigner(g_qr_paramScope, mNm, $('#apiPath').val() || '', function () {
      qrRefreshSummary();
    });
  });

  $('#qrBtnOpenTmpltModal').on('click', function () {
    $('#qrTmpltSearch').val('');
    qrFilterTmpltList('');
    qrOpenModal('#qrTmpltModal');
  });
  $('#qrTmpltModalClose').on('click', function () { qrCloseModal('#qrTmpltModal'); });

  $('#qrBtnOpenYamlModal').on('click', function () { qrOpenYamlModal(); });
  $('#qrYamlModalClose, #qrYamlCancel').on('click', function () { qrCloseModal('#qrYamlModal'); });
  $('#qrYamlApply').on('click', qrApplyYaml);
  $('#qrYamlFile').on('change', qrOnYamlFileSelected);

  $('#qrBtnOpenDetailModal').on('click', qrOpenDetailModal);
  $('#qrDetailModalClose, #qrDetailCancel').on('click', qrCancelDetailModal);
  $('#qrDetailApply').on('click', qrApplyDetailModal);

  $('.qr_modal_backdrop').on('click', function (e) {
    if (e.target === this) { $(this).addClass('qr_hide'); }
  });
  $('.qr_modal').on('click', function (e) { e.stopPropagation(); });

  $('#quickRegForm').on('click', '.sv_acc_head', function () {
    $(this).closest('.sv_acc').toggleClass('sv_open');
  });
});

function qrOpenModal(sel) { $(sel).removeClass('qr_hide'); }
function qrCloseModal(sel) { $(sel).addClass('qr_hide'); }

/* ---------------- 템플릿 선택 ---------------- */

function qrInitTmpltIcons() {
  $('#qrTmpltGrid .qr_tmplt_row').each(function () {
    var mthCd = $(this).attr('data-mth-cd') || '';
    $(this).find('.qr_tmplt_icon').html(qrIconForMethodNm(qrMethodNm(mthCd)));

    var paramsJson = $(this).attr('data-params');
    var cnt = 0;
    if (paramsJson) {
      try { cnt = (JSON.parse(paramsJson) || []).length; } catch (e) { cnt = 0; }
    }
    $(this).find('.qr_tmplt_cnt').text('파라미터 ' + cnt);
  });
}

function qrFilterTmpltList(keyword) {
  keyword = (keyword || '').trim().toLowerCase();
  var visibleCount = 0;
  $('#qrTmpltGrid .qr_tmplt_row').each(function () {
    var nm = ($(this).attr('data-tmplt-nm') || '').toLowerCase();
    var desc = ($(this).find('.qr_tmplt_desc').text() || '').toLowerCase();
    var match = !keyword || nm.indexOf(keyword) > -1 || desc.indexOf(keyword) > -1;
    $(this).toggleClass('qr_hide', !match);
    if (match) { visibleCount++; }
  });
  $('#qrTmpltEmptyMsg').toggleClass('qr_hide', visibleCount > 0);
}

function qrSelectTemplate(el) {
  var $el = $(el);
  $('#qrTmpltGrid .qr_tmplt_row').removeClass('qr_selected');
  $el.addClass('qr_selected');

  var tmpltNm = $el.attr('data-tmplt-nm');
  var apiClass = $el.attr('data-api-class');
  var mthCd = $el.attr('data-mth-cd');
  var cntCd = $el.attr('data-cnt-cd');
  var path = $el.attr('data-path');
  var paramsJson = $el.attr('data-params');

  g_qr_selectedTmpltNm = tmpltNm;

  if (apiClass) {
    $('input[name="apiClass"][value="' + apiClass + '"]').prop('checked', true);
    qrOnApiClassChange();
  }
  if (mthCd) { $('#methodCd').val(mthCd); qrOnMethodChange(); }
  if (cntCd) { $('#cntTypeCd').val(cntCd); }
  if (path && !$('#apiPath').val()) { $('#apiPath').val(path); }

  // 템플릿의 기본 파라미터는 전부 "입력" 스코프로 채운다(템플릿엔 Query/출력 구분이 없음).
  var params = [];
  if (paramsJson) {
    try { params = JSON.parse(paramsJson); } catch (e) { params = []; }
  }
  var nodes = [];
  for (var i = 0; i < params.length; i++) {
    // 템플릿(dfltParamJson)의 type은 이미 DATTYP comn_cd 값이다(qrAddParamRow가 예전에 select 매칭에 쓰던 것과 동일).
    nodes.push(ptNode(params[i].name, params[i].type || 'DATTYP1010', params[i].required !== 'N', params[i].desc));
  }
  PT_STORE['in'] = nodes;
  ptRenderPreview('qr', qrRefreshSummary);
  qrCloseModal('#qrTmpltModal');
}

function qrRefreshSummary() {
  var mNm = $('#methodCd option:selected').text() || '';
  var cNm = $('#cntTypeCd option:selected').text() || '';
  var count = ptCountAll(PT_STORE['in']) + ptCountAll(PT_STORE.query) + ptCountAll(PT_STORE.out);

  $('#qrAppliedBadge').text(g_qr_selectedTmpltNm || '직접 입력');
  $('#qrAppliedMono').text(mNm + (cNm ? (' · ' + cNm) : ''));
  $('#qrAppliedParamCnt').text(count);
  $('#qrAppliedClass').text($('input[name="apiClass"]:checked').next('span').text() || $('input[name="apiClass"]:checked').val() || '');
  $('#qrBannerTmpltNm').text(g_qr_selectedTmpltNm || '기본값');
  $('#qrBannerSummary').text(mNm + (cNm ? (' · ' + cNm) : ''));
  $('#qrBannerParamCount').text(count);
  $('#qrParamBadge').text(count);
}

/* ---------------- 상세 설정 팝업 ---------------- */

function qrOpenDetailModal() {
  g_qr_detailSnapshot = {
    apiClass: $('input[name="apiClass"]:checked').val(),
    apiHandlerCd: $('#apiHandlerCd').val(),
    providerSeq: $('#providerSeq').val(),
    ver: $('#ver').val(),
    basPath: $('#basPath').val(),
    methodCd: $('#methodCd').val(),
    cntTypeCd: $('#cntTypeCd').val()
  };
  qrOpenModal('#qrDetailModal');
}

function qrCancelDetailModal() {
  if (g_qr_detailSnapshot) {
    var s = g_qr_detailSnapshot;
    $('input[name="apiClass"][value="' + s.apiClass + '"]').prop('checked', true);
    qrOnApiClassChange();
    $('#apiHandlerCd').val(s.apiHandlerCd);
    $('#providerSeq').val(s.providerSeq);
    $('#ver').val(s.ver);
    $('#basPath').val(s.basPath);
    $('#methodCd').val(s.methodCd);
    qrOnMethodChange();
    $('#cntTypeCd').val(s.cntTypeCd);
  }
  qrCloseModal('#qrDetailModal');
}

function qrApplyDetailModal() {
  g_qr_selectedTmpltNm = g_qr_selectedTmpltNm || '직접 입력';
  qrRefreshSummary();
  qrCloseModal('#qrDetailModal');
}

function qrOnSysIdChange() {
  var sysId = $('#sysId').val();
  var $autId = $('#autId');
  $autId.empty();

  if (!sysId) {
    $autId.append('<option value="">서비스를 먼저 선택하세요</option>');
    return;
  }

  var matched = [];
  for (var i = 0; i < g_qr_authList.length; i++) {
    if (g_qr_authList[i].sysId === sysId) { matched.push(g_qr_authList[i]); }
  }

  if (matched.length === 0) {
    $autId.append('<option value="">해당 서비스의 권한그룹이 없습니다</option>');
    return;
  }

  $autId.append('<option value="">선택</option>');
  for (var j = 0; j < matched.length; j++) {
    $autId.append('<option value="' + matched[j].autId + '">' + matched[j].autNm + '</option>');
  }

  qrLoadSysApiTree(sysId);
}

/* ---------------- 좌측 API 트리 (선택한 서비스의 기존 등록 현황, 읽기 전용 참고용) ---------------- */

function qrLoadSysApiTree(sysId) {
  var $tree = $('#qrLeftTree');
  if (!sysId) {
    $tree.empty();
    $('#qrLeftHint').text('서비스를 선택하면 이미 등록된 API가 표시됩니다.');
    return;
  }
  $('#qrLeftHint').text('불러오는 중...');
  $.ajax({
    url: c_url + 'api/quickreg/selSysApiTreeAjax.do',
    type: 'GET',
    data: { sysId: sysId },
    dataType: 'json',
    success: function (res) { qrRenderSysApiTree(res.list || []); },
    error: function () {
      $tree.empty();
      $('#qrLeftHint').text('목록을 불러오지 못했습니다.');
    }
  });
}

function qrRenderSysApiTree(list) {
  var $tree = $('#qrLeftTree');
  $tree.empty();

  if (!list || list.length === 0) {
    $('#qrLeftHint').text('이 서비스에 등록된 API가 아직 없습니다. 새로 만드는 첫 API입니다.');
    return;
  }
  $('#qrLeftHint').text('이 서비스에 이미 등록된 API입니다 (참고용, 읽기 전용).');

  // apiSpcNo(버전) -> ctgryNm -> [api...] 로 그룹핑
  var bySpc = {};
  var spcOrder = [];
  for (var i = 0; i < list.length; i++) {
    var row = list[i];
    if (!bySpc[row.apiSpcNo]) {
      bySpc[row.apiSpcNo] = { spcNm: row.spcNm, ver: row.ver, ctgries: {}, ctgryOrder: [] };
      spcOrder.push(row.apiSpcNo);
    }
    var spc = bySpc[row.apiSpcNo];
    var ctgryKey = row.ctgryNm || '기본';
    if (!spc.ctgries[ctgryKey]) {
      spc.ctgries[ctgryKey] = [];
      spc.ctgryOrder.push(ctgryKey);
    }
    spc.ctgries[ctgryKey].push(row);
  }

  for (var s = 0; s < spcOrder.length; s++) {
    var spcNo = spcOrder[s];
    var spcData = bySpc[spcNo];
    var html = '<div class="qr_lt_spc">';
    html += '<div class="qr_lt_spc_nm">' + qrEsc(spcData.spcNm) + ' <span style="font-weight:400;color:#999;">(' + qrEsc(spcData.ver) + ')</span></div>';
    for (var c = 0; c < spcData.ctgryOrder.length; c++) {
      var ctgryNm = spcData.ctgryOrder[c];
      html += '<div class="qr_lt_ctgry"><div class="qr_lt_ctgry_nm">' + qrEsc(ctgryNm) + '</div>';
      var apis = spcData.ctgries[ctgryNm];
      for (var a = 0; a < apis.length; a++) {
        html += '<div class="qr_lt_api"><span class="qr_lt_method">' + qrEsc(qrMethodNm(apis[a].methodCd)) + '</span><span class="qr_lt_path">' + qrEsc(apis[a].apiPath || '') + '</span></div>';
      }
      html += '</div>';
    }
    html += '</div>';
    $tree.append(html);
  }
}

function qrMethodNm(methodCd) {
  return qrCodeNm(g_qr_mthTypeList, methodCd);
}

function qrOnApiClassChange() {
  var apiClass = $('input[name="apiClass"]:checked').val();
  if (apiClass === 'APIGUB1020') { $('#qrPrivateRow').removeClass('qr_hide'); }
  else { $('#qrPrivateRow').addClass('qr_hide'); }
  $('#qrAppliedClass').text($('input[name="apiClass"]:checked').next('span').text() || apiClass || '');
}

function qrOnMethodChange() {
  var mNm = $('#methodCd option:selected').text();
  if (mNm === 'GET' || mNm === 'DELETE') { $('#cntTypeRow').addClass('qr_hide'); }
  else { $('#cntTypeRow').removeClass('qr_hide'); }
}

/* 파라미터 행 편집 UI(qrAddParamRow/qrCollectParamList)는 paramTreeEditor.js의 트리+YAML
   설계 팝업으로 대체됨. 파라미터 데이터는 이제 PT_STORE(scope별 트리)가 갖고 있다. */

/* ---------------- YAML 등록 ---------------- */

function qrOpenYamlModal() {
  qrOpenModal('#qrYamlModal');
  $('#qrYamlErr').addClass('qr_hide').text('');
  if (!g_qr_cm) {
    g_qr_cm = qrInitYamlEditor(document.getElementById('qrYamlEditorArea'));
  }
  setTimeout(function () { g_qr_cm.refresh(); }, 50);
}

function qrOnYamlModeChange() {
  var mode = $('input[name="qrYamlMode"]:checked').val();
  if (mode === 'upload') { $('#qrYamlUploadRow').removeClass('qr_hide'); }
  else { $('#qrYamlUploadRow').addClass('qr_hide'); }
}

function qrOnYamlFileSelected(e) {
  var file = e.target.files && e.target.files[0];
  if (!file) { return; }
  var reader = new FileReader();
  reader.onload = function (evt) {
    if (g_qr_cm) { g_qr_cm.setValue(evt.target.result || ''); }
  };
  reader.onerror = function () {
    $('#qrYamlErr').removeClass('qr_hide').text('파일을 읽는 중 오류가 발생했습니다.');
  };
  reader.readAsText(file);
}

function qrApplyYaml() {
  var yamlText = g_qr_cm ? g_qr_cm.getValue() : '';
  if (!yamlText || !yamlText.trim()) {
    $('#qrYamlErr').removeClass('qr_hide').text('YAML 내용을 입력하거나 파일을 업로드하세요.');
    return;
  }

  var parsed;
  try {
    parsed = qrParseOasYaml(yamlText);
  } catch (e) {
    $('#qrYamlErr').removeClass('qr_hide').text('YAML을 해석할 수 없습니다: ' + (e && e.message ? e.message : e));
    return;
  }

  if (parsed.apiNm && !$('#apiNm').val()) { $('#apiNm').val(parsed.apiNm); }
  if (parsed.apiDesc && !$('#apiDesc').val()) { $('#apiDesc').val(parsed.apiDesc); }
  if (parsed.host) { $('#host').val(parsed.host); }
  if (parsed.basPath) { $('#basPath').val(parsed.basPath); }
  if (parsed.apiPath) { $('#apiPath').val(parsed.apiPath); }
  if (parsed.methodCd) { $('#methodCd').val(parsed.methodCd); qrOnMethodChange(); }
  if (parsed.cntTypeCd) { $('#cntTypeCd').val(parsed.cntTypeCd); }

  // YAML 파서는 아직 위치(query/body)를 구분해 내려주지 않아, 일단 전부 "입력" 스코프로 채운다.
  var nodes = [];
  if (parsed.params && parsed.params.length > 0) {
    for (var i = 0; i < parsed.params.length; i++) {
      var p = parsed.params[i];
      nodes.push(ptNode(p.name, p.type || 'DATTYP1010', p.required !== 'N', p.desc));
    }
  }
  PT_STORE['in'] = nodes;
  ptRenderPreview('qr', qrRefreshSummary);

  if (parsed.multiplePaths) {
    $('#qrYamlErr').removeClass('qr_hide').text('여러 Path/Method가 감지되었습니다. 첫 번째 항목만 적용되었습니다.');
  }

  g_qr_selectedTmpltNm = 'YAML 등록';
  $('#qrTmpltGrid .qr_tmplt_row').removeClass('qr_selected');
  qrRefreshSummary();
  qrCloseModal('#qrYamlModal');
}

/* qrParseOasYaml/qrEsc/QR_TYPE_TO_DATTYP는 quickRegShared.js로 이동(템플릿 관리 화면과 공유) */

/* ---------------- 저장 ---------------- */

function qrSaveQuickReg() {
  var sysId = $('#sysId').val();
  var autId = $('#autId').val();
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();
  var host = $('#host').val();
  var basPath = $('#basPath').val();

  var hasErr = false;
  $('#apiNmErr, #hostErr, #apiPathErr').removeClass('qr_show');
  $('#apiNm, #host, #apiPath').removeClass('qr_input_err');

  if (!apiNm) { $('#apiNmErr').addClass('qr_show'); $('#apiNm').addClass('qr_input_err'); hasErr = true; }
  if (!host) { $('#hostErr').addClass('qr_show'); $('#host').addClass('qr_input_err'); hasErr = true; }
  if (!apiPath || apiPath.charAt(0) !== '/') { $('#apiPathErr').addClass('qr_show'); $('#apiPath').addClass('qr_input_err'); hasErr = true; }
  if (!sysId || !autId) { alert_message('서비스와 권한그룹을 선택해 주세요.'); hasErr = true; }
  if (!basPath) { alert_message('기본경로를 입력해 주세요.'); hasErr = true; }

  if (hasErr) { return; }

  var formData = {
    sysId: sysId,
    autId: autId,
    apiNm: apiNm,
    apiDesc: $('#apiDesc').val(),
    apiClass: $('input[name="apiClass"]:checked').val(),
    ver: $('#ver').val(),
    host: host,
    basPath: basPath,
    apiPath: apiPath,
    methodCd: $('#methodCd').val(),
    apiHandlerCd: $('#apiHandlerCd').val(),
    providerSeq: $('#providerSeq').val(),
    apiVeriBaseurl: 'https://' + host
  };

  var paramList = ptFlattenTree(PT_STORE['in'], 'in')
    .concat(ptFlattenTree(PT_STORE.query, 'query'))
    .concat(ptFlattenTree(PT_STORE.out, 'out'));
  for (var i = 0; i < paramList.length; i++) {
    formData['paramList[' + i + '].paramNm'] = paramList[i].paramNm;
    formData['paramList[' + i + '].dataTypeCd'] = paramList[i].dataTypeCd;
    formData['paramList[' + i + '].required'] = paramList[i].required;
    formData['paramList[' + i + '].paramDesc'] = paramList[i].paramDesc;
    formData['paramList[' + i + '].paramTypeCd'] = paramList[i].paramTypeCd;
    formData['paramList[' + i + '].paramLoc'] = paramList[i].paramLoc;
    formData['paramList[' + i + '].exam'] = paramList[i].exam;
    formData['paramList[' + i + '].personalData'] = paramList[i].personalData;
    formData['paramList[' + i + '].tempId'] = paramList[i].tempId;
    formData['paramList[' + i + '].parentTempId'] = paramList[i].parentTempId;
  }

  $.ajax({
    url: c_url + 'api/quickreg/savApiQuickRegAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        $('#qrAdvancedLink').attr('href', c_url + 'api/reg/mvApiPathReg.do?apiSpcNo=' + res.apiSpcNo);
        $('#qrResultBox').removeClass('qr_hide');
        $('html, body').animate({ scrollTop: $('#qrResultBox').offset().top - 80 }, 300);
      } else {
        alert_message(res.message || '등록 중 오류가 발생했습니다.');
      }
    },
    error: function () {
      alert_message('등록 중 오류가 발생했습니다.');
    }
  });
}
