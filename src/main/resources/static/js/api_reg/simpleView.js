/* simpleView.js - API "간단 상세" 화면 전용 스크립트 (디자인 리뉴얼판).
   기존 편집폼 JS(apiInfoReg.js 등)와는 무관하게 이 화면만을 위해 새로 작성했다.
   공용 유틸(alert_message 등)만 apiGlobalScript.js, 아이콘/코드조회는 quickRegShared.js에서 가져다 쓴다. */

var MTH_CLASS = { GET: 'sv_mth_get', POST: 'sv_mth_post', PUT: 'sv_mth_put', DELETE: 'sv_mth_delete', PATCH: 'sv_mth_patch' };

function qrMethodNm(methodCd) {
  return qrCodeNm(g_qr_mthTypeList, methodCd);
}

$(document).ready(function () {
  svRenderTree();
  if (g_sv_selectedApiNo) {
    svRenderParamSummary(g_sv_selectedDef, g_sv_selectedParamList);
  }

  $('#svBlocks').on('click', '.sv_acc_head', function () {
    $(this).closest('.sv_acc').toggleClass('sv_open');
  });

  $('#svDefModalClose, #svDefCancel').on('click', function () { $('#svDefModal').addClass('qr_hide'); });
  $('.qr_modal_backdrop').on('click', function (e) {
    if (e.target === this) { $(this).addClass('qr_hide'); }
  });
  $('.qr_modal').on('click', function (e) { e.stopPropagation(); });

  $('.sv_tab').on('click', function () {
    var t = $(this).data('tab');
    $('.sv_tab').removeClass('sv_on'); $(this).addClass('sv_on');
    $('.sv_panel').removeClass('sv_on');
    $('.sv_panel[data-panel="' + t + '"]').addClass('sv_on');
  });

  ptBindDesigner();
});

/* ---------------- 좌측 트리 (실제 네비게이션 - 클릭 시 우측 파라미터 아코디언 갱신) ---------------- */

function svRenderTree() {
  var $tree = $('#svLeftTree');
  $tree.empty();

  if (!g_sv_defList || g_sv_defList.length === 0) {
    $tree.append('<p class="qr_left_hint">등록된 Path/Method가 없습니다.</p>');
    return;
  }

  var spcNm = $('.sv_hero_nm').text() || '';
  var ctgries = {};
  var ctgryOrder = [];
  for (var i = 0; i < g_sv_defList.length; i++) {
    var row = g_sv_defList[i];
    var key = row.ctgryNm || '기본';
    if (!ctgries[key]) { ctgries[key] = []; ctgryOrder.push(key); }
    ctgries[key].push(row);
  }

  var html = '<div class="qr_lt_spc"><div class="qr_lt_spc_nm">' + qrEsc(spcNm) + '</div>';
  for (var c = 0; c < ctgryOrder.length; c++) {
    var ctgryNm = ctgryOrder[c];
    html += '<div class="qr_lt_ctgry"><div class="qr_lt_ctgry_nm">' + qrEsc(ctgryNm) + '</div>';
    var apis = ctgries[ctgryNm];
    for (var a = 0; a < apis.length; a++) {
      var d = apis[a];
      var mNm = qrMethodNm(d.methodCd);
      var selected = (String(d.apiNo) === String(g_sv_selectedApiNo)) ? ' qr_lt_api_selected' : '';
      html += '<div class="qr_lt_api qr_lt_api_clickable' + selected + '" data-api-no="' + qrEsc(d.apiNo) + '">' +
        '<span class="qr_lt_method ' + (MTH_CLASS[mNm] || 'sv_mth_patch') + '">' + qrEsc(mNm) + '</span>' +
        '<span class="qr_lt_path">' + qrEsc(d.apiPath || '') + '</span>' +
        '</div>';
    }
    html += '</div>';
  }
  html += '</div>';
  $tree.html(html);

  $tree.off('click', '.qr_lt_api_clickable').on('click', '.qr_lt_api_clickable', function () {
    svSelectApi($(this).attr('data-api-no'));
  });
}

function svSelectApi(apiNo) {
  $('#svLeftTree .qr_lt_api').removeClass('qr_lt_api_selected');
  $('#svLeftTree .qr_lt_api[data-api-no="' + apiNo + '"]').addClass('qr_lt_api_selected');

  $.ajax({
    url: c_url + 'api/simpleview/selApiDefDetailAjax.do',
    type: 'GET',
    data: { apiNo: apiNo },
    dataType: 'json',
    success: function (res) {
      svRenderParamSummary(res.def || {}, res.paramList || []);
    },
    error: function () { alert_message('파라미터 정보를 불러오지 못했습니다.'); }
  });
}

// 저장된 param_type_cd(PRMTYP1010=입력/PRMTYP1020=출력)/param_loc(body/query/header) 기준으로
// 3개 스코프로 분류 후 PT_STORE(paramTreeEditor.js 공용 상태)에 중첩 트리로 재구성해 둔다.
var g_sv_headerParamList = []; // 헤더는 편집 UI가 없어 flat하게만 보관(현재 실데이터 0건)
function svSplitParamRows(paramList) {
  var rows = { 'in': [], query: [], out: [] };
  g_sv_headerParamList = [];
  for (var i = 0; i < paramList.length; i++) {
    var p = paramList[i];
    if (p.paramLoc === 'header') { g_sv_headerParamList.push(p); continue; }
    if (p.paramTypeCd === 'PRMTYP1020') { rows.out.push(p); }
    else if (p.paramLoc === 'query') { rows.query.push(p); }
    else { rows['in'].push(p); }
  }
  return rows;
}

// 중첩 트리를 들여쓰기로 평탄화해서 sv_mini_table(읽기전용)에 보여준다.
function svRenderMiniTable(prefix, nodes) {
  var $rows = $('#svParam' + prefix + 'Rows');
  $rows.empty();
  var flatNames = [];
  var reqCnt = 0;
  (function walk(list, depth) {
    for (var i = 0; i < list.length; i++) {
      var n = list[i];
      flatNames.push(n.paramNm);
      if (n.required) { reqCnt++; }
      var reqBadge = n.required ? '<span class="sv_req_y">필수</span>' : '<span class="sv_req_n">선택</span>';
      $rows.append(
        '<tr><td class="sv_mt_nm" style="padding-left:' + (depth * 16) + 'px;">' + qrEsc(n.paramNm) + '</td>' +
        '<td class="sv_mt_type">' + qrEsc(ptTypeNm(n.dataTypeCd)) + '</td>' +
        '<td>' + reqBadge + '</td>' +
        '<td>' + qrEsc(n.paramDesc || '') + '</td></tr>'
      );
      if (ptIsBranch(n) && n.kids.length) { walk(n.kids, depth + 1); }
    }
  })(nodes, 0);

  var total = ptCountAll(nodes);
  $('#svParam' + prefix + 'Badge').text(total);
  $('#svParam' + prefix + 'Sum').text(total > 0 ? (flatNames.join(' · ') + ' — 필수 ' + reqCnt + '건') : '등록된 항목 없음');
  $('#svParam' + prefix + 'Empty').toggleClass('qr_hide', total > 0);
  $('#svParam' + prefix + 'Table').toggleClass('qr_hide', total === 0);
}

function svRenderParamSummary(def, paramList) {
  $('#svApiNo').val(def.apiNo || '');
  g_sv_selectedApiNo = def.apiNo || '';

  var mNm = qrMethodNm(def.methodCd) || $('#svLeftTree .qr_lt_api_selected .qr_lt_method').text() || '';
  $('#svSelMth').text(mNm).attr('class', 'sv_mth ' + (MTH_CLASS[mNm] || 'sv_mth_patch'));
  $('#svSelPath').text(def.apiPath || $('#svLeftTree .qr_lt_api_selected .qr_lt_path').text() || '');

  var rows = svSplitParamRows(paramList || []);
  PT_STORE['in'] = ptBuildTree(rows['in']);
  PT_STORE.query = ptBuildTree(rows.query);
  PT_STORE.out = ptBuildTree(rows.out);

  svRenderMiniTable('In', PT_STORE['in']);
  svRenderMiniTable('Query', PT_STORE.query);
  svRenderMiniTable('Out', PT_STORE.out);
}

/* ---------------- 파라미터 설계 팝업 (스코프별 트리+YAML, quickApiReg와 동일한 컴포넌트 재사용) ---------------- */

function svOpenParamDesigner(scope) {
  var mNm = $('#svSelMth').text() || '';
  var pathTxt = $('#svSelPath').text() || '';
  ptOpenDesigner(scope, mNm, pathTxt, function (savedScope, flatRows) {
    svSaveAllParams();
  });
}

// 어느 스코프를 저장하든 항상 입력/Query/출력 3개 스코프를 전부 합쳐서 보낸다.
// (저장 API가 apiNo 기준으로 파라미터 전체를 재구성하는 방식이라, 일부만 보내면 나머지가 삭제된다)
function svSaveAllParams() {
  var apiNo = $('#svApiNo').val();
  var paramList = ptFlattenTree(PT_STORE['in'], 'in')
    .concat(ptFlattenTree(PT_STORE.query, 'query'))
    .concat(ptFlattenTree(PT_STORE.out, 'out'));

  var paramFormData = { apiNo: apiNo };
  for (var i = 0; i < paramList.length; i++) {
    paramFormData['paramList[' + i + '].paramNm'] = paramList[i].paramNm;
    paramFormData['paramList[' + i + '].dataTypeCd'] = paramList[i].dataTypeCd;
    paramFormData['paramList[' + i + '].required'] = paramList[i].required;
    paramFormData['paramList[' + i + '].paramDesc'] = paramList[i].paramDesc;
    paramFormData['paramList[' + i + '].paramTypeCd'] = paramList[i].paramTypeCd;
    paramFormData['paramList[' + i + '].paramLoc'] = paramList[i].paramLoc;
    paramFormData['paramList[' + i + '].exam'] = paramList[i].exam;
    paramFormData['paramList[' + i + '].personalData'] = paramList[i].personalData;
    paramFormData['paramList[' + i + '].tempId'] = paramList[i].tempId;
    paramFormData['paramList[' + i + '].parentTempId'] = paramList[i].parentTempId;
  }

  $.ajax({
    url: c_url + 'api/simpleview/savApiDefParamsAjax.do',
    type: 'POST',
    data: paramFormData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        svReload();
      } else {
        alert_message(res.message || '파라미터 저장 중 오류가 발생했습니다.');
      }
    },
    error: function () { alert_message('파라미터 저장 중 오류가 발생했습니다.'); }
  });
}

/* ---------------- 필수 정보 ---------------- */

function svToggleSpcExtra() {
  var $extra = $('#svSpcExtra');
  var showing = $extra.hasClass('qr_hide');
  $extra.toggleClass('qr_hide', !showing);
  $('#svSpcExtraToggle').text(showing ? '간략히' : '전체보기');
}

function svToggleSpcEdit(editing) {
  if (editing) {
    $('#svSpcView').addClass('qr_hide');
    $('#svSpcForm').removeClass('qr_hide');
    $('#svSpcHeadBtns').addClass('qr_hide');
  } else {
    $('#svSpcForm').addClass('qr_hide');
    $('#svSpcView').removeClass('qr_hide');
    $('#svSpcHeadBtns').removeClass('qr_hide');
  }
}

function svSaveSpc() {
  var apiNm = $('#svApiNm').val();
  var host = $('#svHost').val();
  var basPath = $('#svBasPath').val();
  if (!apiNm || !host || !basPath) {
    alert_message('API 이름/호스트/기본경로는 필수입니다.');
    return;
  }

  $.ajax({
    url: c_url + 'api/simpleview/savSpcEssentialAjax.do',
    type: 'POST',
    data: {
      apiSpcNo: $('#svApiSpcNo').val(),
      apiNm: apiNm,
      host: host,
      basPath: basPath,
      apiDesc: $('#svApiDesc').val()
    },
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        svReload();
      } else {
        alert_message(res.message || '저장 중 오류가 발생했습니다.');
      }
    },
    error: function () { alert_message('저장 중 오류가 발생했습니다.'); }
  });
}

function svReload() {
  var apiSpcNo = $('#svApiSpcNo').val();
  location.href = c_url + 'api/simpleview/mvApiSimpleView.do?apiSpcNo=' + apiSpcNo + '&apiNo=' + (g_sv_selectedApiNo || '');
}

/* ---------------- Path/Method 상세 팝업 (엔드포인트/응답매핑/HDP 전용 - 파라미터는 별도 설계 팝업) ---------------- */

function svOpenDefDetail(apiNo) {
  $.ajax({
    url: c_url + 'api/simpleview/selApiDefDetailAjax.do',
    type: 'GET',
    data: { apiNo: apiNo },
    dataType: 'json',
    success: function (res) {
      svFillDefDetail(res.def || {});
      $('.sv_tab[data-tab="ep"]').trigger('click');
      $('#svDefModal').removeClass('qr_hide');
    },
    error: function () { alert_message('상세 정보를 불러오지 못했습니다.'); }
  });
}

function svFillDefDetail(def) {
  $('#svDefApiNo').val(def.apiNo || '');
  var mNm = qrMethodNm(def.methodCd) || '';
  $('#svDefMethod').text(mNm).attr('class', 'sv_mth ' + (MTH_CLASS[mNm] || 'sv_mth_patch'));
  $('#svDefPath').text(def.apiPath || '');

  $('#svEndpntTbUrl').val(def.endpntTbUrl || '');
  $('#svEndpntPrdUrl').val(def.endpntPrdUrl || '');
  $('#svEndpntClientIp').val(def.endpntClientIp || '');
  $('#svEndpntTimeout').val(def.endpntTimeout || '');

  $('#svResmapResCdField').val(def.resmapResCdField || '');
  $('#svResmapSuccVal').val(def.resmapSuccVal || '');
  $('#svResmapErrCdField').val(def.resmapErrCdField || '');
  $('#svResmapErrMsgField').val(def.resmapErrMsgField || '');

  $('#svHdpApiEndpointId').val(def.hdpApiEndpointId || '');
  $('#svHdpReqApiName').val(def.hdpReqApiName || '');
  $('#svHdpApiOutFormat').val(def.hdpApiOutFormat || '');
  $('#svHdpApiOutCommonParam').val(def.hdpApiOutCommonParam || '');
  $('#svHdpReqMappingToBody').val(def.hdpReqMappingToBody || '');
  $('#svHdpResMappingToBody').val(def.hdpResMappingToBody || '');

  $('#svTbDplyStatus').text(def.tbDplyStatus || '-');
  $('#svDplyVeriStatus').text(def.dplyVeriStatus || '-');
  $('#svPrdDplyStatus').text(def.prdDplyStatus || '-');
}

/* ---------------- 저장 ---------------- */

function svSaveDefDetail() {
  var apiNo = $('#svDefApiNo').val();

  var detailData = {
    apiNo: apiNo,
    endpntTbUrl: $('#svEndpntTbUrl').val(),
    endpntPrdUrl: $('#svEndpntPrdUrl').val(),
    endpntClientIp: $('#svEndpntClientIp').val(),
    endpntTimeout: $('#svEndpntTimeout').val(),
    resmapResCdField: $('#svResmapResCdField').val(),
    resmapSuccVal: $('#svResmapSuccVal').val(),
    resmapErrCdField: $('#svResmapErrCdField').val(),
    resmapErrMsgField: $('#svResmapErrMsgField').val(),
    hdpApiEndpointId: $('#svHdpApiEndpointId').val(),
    hdpReqApiName: $('#svHdpReqApiName').val(),
    hdpApiOutFormat: $('#svHdpApiOutFormat').val(),
    hdpApiOutCommonParam: $('#svHdpApiOutCommonParam').val(),
    hdpReqMappingToBody: $('#svHdpReqMappingToBody').val(),
    hdpResMappingToBody: $('#svHdpResMappingToBody').val()
  };

  $.ajax({
    url: c_url + 'api/simpleview/savApiDefDetailAjax.do',
    type: 'POST',
    data: detailData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        g_sv_selectedApiNo = apiNo;
        svReload();
      } else {
        alert_message(res.message || '저장 중 오류가 발생했습니다.');
      }
    },
    error: function () { alert_message('저장 중 오류가 발생했습니다.'); }
  });
}
