/* apiDefReg.js - "API 등록"(기존 SPC에 API 추가) 화면 전용 스크립트.
   SPC는 앞 화면(spcReg)에서 이미 확정되어 apiSpcNo/spcBasPath가 hidden 값으로 넘어오므로,
   이 화면엔 그룹 선택 UI/로직이 없다(퍼블 v14.0 확인 사항). 권한그룹(autId)도 서버가 그 그룹의
   sysId로 이미 필터링해 내려준다. 공용 유틸(alert_message 등)은 apiGlobalScript.js, 파라미터
   트리+YAML 편집은 paramTreeEditor.js(공용, 팝업 id는 qr* 고정)를 쓴다. */

var g_def_paramScope = 'in'; // 파라미터 미리보기에서 현재 활성 탭(설계 버튼이 어느 스코프를 열지 결정)
var g_def_origHeadSub = ''; // "OOO 그룹에 API를 추가합니다" 원래 문구 - 수정 모드에서 나갈 때 복원용

$(document).ready(function () {
  g_def_origHeadSub = $('#defHeadSub').html();

  defOnMethodChange();
  defOnApiClassChange();

  // 모든 API의 입력 파라미터는 request 오브젝트로, 출력 파라미터는 response 오브젝트로 감싸는 게
  // 규칙이라, 새 API를 만들 때 처음부터 그 최상위 오브젝트를 기본으로 깔아둔다(Query는 OAS
  // parameters 목록이라 오브젝트로 감싸는 대상이 아니므로 그대로 빈 채로 둔다).
  defInitDefaultParamTree();

  ptRenderPreview('def', defRefreshSummary);
  ptBindPreviewToggle('def');
  ptBindDesigner();

  $('.qr_pv_tab').on('click', function () {
    var scope = $(this).attr('data-scope');
    g_def_paramScope = scope;
    $('.qr_pv_tab').removeClass('qr_on');
    $(this).addClass('qr_on');
    $('.qr_pv_pane').addClass('qr_hide');
    $('#defPv_' + scope).removeClass('qr_hide');
    $('#defBtnOpenParamModal').text(PT_SCOPES[scope].label + ' 설계');
  });
  $('#defBtnOpenParamModal').on('click', function () {
    var mNm = $('#methodCd option:selected').text() || '';
    ptOpenDesigner(g_def_paramScope, mNm, $('#apiPath').val() || '', function () {
      // ptBindDesigner()의 저장 버튼은 PT_STORE만 갱신하고 배지 콜백만 불러준다 - 실제 미리보기
      // 목록(#defPv_in 등)/탭 건수를 다시 그리려면 ptRenderPreview를 직접 호출해야 한다.
      ptRenderPreview('def', defRefreshSummary);
    });
  });

  $('.qr_modal_backdrop').on('click', function (e) {
    if (e.target === this) { $(this).addClass('qr_hide'); }
  });
  $('.qr_modal').on('click', function (e) { e.stopPropagation(); });

  $('#defRegForm').on('click', '.sv_acc_head', function () {
    $(this).closest('.sv_acc').toggleClass('sv_open');
  });

  $('#apiPath').on('input', defSyncFullPath);
  defSyncFullPath();
  defLoadGroupApiList();

  /* 좌측 트리에서 기존 API 행 클릭 -> 그 API를 폼에 불러와 수정 모드로 전환 (위임 바인딩 - 트리는
     매번 새로 그려지므로). "새 API 추가"를 누르면 defResetToCreate()로 다시 빈 폼으로 돌아간다. */
  $('#defLeftTree').on('click', '.qr_lt_api', function () {
    var apiNo = $(this).attr('data-api-no');
    if (apiNo) { defLoadApiForEdit(apiNo); }
  });
  $('#defNewBtn').on('click', defResetToCreate);

  /* 템플릿 선택(퍼블_v16.0) - API(DEF) 단위 기능이라 이 화면 헤더에 둔다. */
  $('#qrBtnOpenTmpltModal').on('click', function () { $('#qrTmpltModal').removeClass('qr_hide'); });
  $('#qrTmpltModalClose').on('click', function () { $('#qrTmpltModal').addClass('qr_hide'); });

  /* Provider(단위서비스코드) 선택 - Private API 전용, 기존 등록 마법사의 "Provider 선택" 팝업을
     이 화면 방식(클릭 한 번으로 즉시 반영)으로 재구현. */
  $('#defBtnOpenProviderModal').on('click', defOpenProviderModal);
  $('#qrProviderModalClose').on('click', function () { $('#qrProviderModal').addClass('qr_hide'); });

  /* 저장 확인 팝업(퍼블_v15.0) — 검증을 통과해도 바로 저장하지 않고 팝업을 먼저 띄운다.
     [취소]는 팝업만 닫고, [등록]을 눌러야 실제 defDoSave()가 실행된다. */
  $('#defSaveBtn').on('click', defOnSaveClick);
  $('#qrConfirmCancel').on('click', function () { $('#qrConfirmModal').addClass('qr_hide'); });
  $('#qrConfirmOk').on('click', function () {
    $('#qrConfirmModal').addClass('qr_hide');
    defDoSave();
  });
});

function defRefreshSummary() {
  var count = ptCountAll(PT_STORE['in']) + ptCountAll(PT_STORE.query) + ptCountAll(PT_STORE.out);
  $('#defParamBadge').text(count);
}

function defSyncFullPath() {
  var base = $('#spcBasPath').val() || '';
  var p = $.trim($('#apiPath').val());
  $('#defFullPath').text((base + p) || '/');
}

/* ---------------- 좌측 API 트리 (이 그룹에 이미 등록된 API - 클릭하면 수정 모드로 불러옴) ---------------- */

function defLoadGroupApiList() {
  var apiSpcNo = $('#apiSpcNo').val();
  var $tree = $('#defLeftTree');
  $('#defLeftHint').text('불러오는 중...');
  $.ajax({
    url: c_url + 'api/spcreg/def/selDefListByApiSpcNoAjax.do',
    type: 'GET',
    data: { apiSpcNo: apiSpcNo },
    dataType: 'json',
    success: function (res) { defRenderGroupApiList(res.list || []); },
    error: function () {
      $tree.empty();
      $('#defLeftHint').text('목록을 불러오지 못했습니다.');
    }
  });
}

function defRenderGroupApiList(list) {
  var $tree = $('#defLeftTree');
  $tree.empty();

  if (!list || list.length === 0) {
    $('#defLeftHint').text('이 그룹에 등록된 API가 아직 없습니다. 새로 만드는 첫 API입니다.');
    return;
  }
  $('#defLeftHint').text('이 그룹에 이미 등록된 API입니다. 클릭하면 불러와서 수정할 수 있습니다.');

  // ctgryNm -> [api...] 로 그룹핑 (이 화면은 이제 그룹(apiSpcNo) 하나만 다루므로 spc 단위 그룹핑은 불필요)
  var ctgries = {};
  var ctgryOrder = [];
  for (var i = 0; i < list.length; i++) {
    var row = list[i];
    var ctgryKey = row.ctgryNm || '기본';
    if (!ctgries[ctgryKey]) {
      ctgries[ctgryKey] = [];
      ctgryOrder.push(ctgryKey);
    }
    ctgries[ctgryKey].push(row);
  }

  var html = '';
  for (var c = 0; c < ctgryOrder.length; c++) {
    var ctgryNm = ctgryOrder[c];
    html += '<div class="qr_lt_ctgry"><div class="qr_lt_ctgry_nm">' + defEsc(ctgryNm) + '</div>';
    var apis = ctgries[ctgryNm];
    for (var a = 0; a < apis.length; a++) {
      html += '<div class="qr_lt_api" data-api-no="' + defEsc(apis[a].apiNo) + '"><span class="qr_lt_method">'
        + defEsc(defMethodNm(apis[a].methodCd)) + '</span><span class="qr_lt_path">' + defEsc(apis[a].apiPath || '') + '</span></div>';
    }
    html += '</div>';
  }
  $tree.html(html);
}

/* ---------------- 기존 API 불러오기 / 새 API로 되돌리기 ---------------- */

function defLoadApiForEdit(apiNo) {
  $.ajax({
    url: c_url + 'api/spcreg/def/selApiDefDetailAjax.do',
    type: 'GET',
    data: { apiNo: apiNo },
    dataType: 'json',
    success: function (res) {
      if (res.returnCode !== '1') {
        alert_message(res.message || '불러오지 못했습니다.');
        return;
      }
      defFillFormFromDetail(res.def);
      $('#defLeftTree .qr_lt_api').removeClass('qr_on');
      $('#defLeftTree .qr_lt_api[data-api-no="' + apiNo + '"]').addClass('qr_on');
    },
    error: function () { alert_message('불러오는 중 오류가 발생했습니다.'); }
  });
}

function defFillFormFromDetail(def) {
  $('#editApiNo').val(def.apiNo);

  $('#apiNm').val(def.apiNm || '');
  $('#apiDesc').val(def.apiDesc || '');
  $('#apiPath').val(def.apiPath || '');
  $('#methodCd').val(def.methodCd || '');
  $('input[name="apiClass"][value="' + def.apiClass + '"]').prop('checked', true);
  $('#apiHandlerCd').val(def.apiHandlerCd || '');
  $('#providerSeq').val(def.providerSeq || '');
  $('#providerNmDisp').val(def.providerSeq ? defProviderNmBySeq(def.providerSeq) : '');
  defOnApiClassChange();
  defSyncFullPath();

  $('#endpntTbUrl').val(def.endpntTbUrl || '');
  $('#endpntPrdUrl').val(def.endpntPrdUrl || '');
  $('#endpntClientIp').val(def.endpntClientIp || '');
  $('#endpntTimeout').val(def.endpntTimeout || '');
  $('#resmapResCdField').val(def.resmapResCdField || '');
  $('#resmapSuccVal').val(def.resmapSuccVal || '');
  $('#resmapErrCdField').val(def.resmapErrCdField || '');
  $('#resmapErrMsgField').val(def.resmapErrMsgField || '');
  $('#hdpApiEndpointId').val(def.hdpApiEndpointId || '');
  $('#hdpReqApiName').val(def.hdpReqApiName || '');
  $('#hdpApiOutFormat').val(def.hdpApiOutFormat || '');
  $('#hdpApiOutCommonParam').val(def.hdpApiOutCommonParam || '');
  $('#hdpReqMappingToBody').val(def.hdpReqMappingToBody || '');
  $('#hdpResMappingToBody').val(def.hdpResMappingToBody || '');

  // 파라미터: 스코프별로 필터링해서 PT_STORE를 다시 구성(ptBuildTree는 flat row -> 중첩 트리)
  var rows = def.paramList || [];
  PT_STORE['in'] = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1010' && r.paramLoc !== 'query'; }));
  PT_STORE.query = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1010' && r.paramLoc === 'query'; }));
  PT_STORE.out = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1020'; }));
  defInitDefaultParamTree(); // 아직 파라미터가 하나도 없던 API라면 request/response 기본 골격을 깔아준다
  ptRenderPreview('def', defRefreshSummary);

  defSetEditMode(true, def.apiNm);
}

function defResetToCreate() {
  $('#editApiNo').val('');
  $('#defRegForm')[0].reset();
  $('#apiHandlerCd, #providerSeq, #providerNmDisp').val('');
  defOnMethodChange();
  defOnApiClassChange();
  defSyncFullPath();

  PT_STORE['in'] = []; PT_STORE.query = []; PT_STORE.out = [];
  defInitDefaultParamTree();
  ptRenderPreview('def', defRefreshSummary);

  $('#defLeftTree .qr_lt_api').removeClass('qr_on');
  defSetEditMode(false);
}

function defSetEditMode(isEdit, apiNm) {
  $('#defNewBtn').toggleClass('qr_hide', !isEdit);
  $('#defSaveBtn').text(isEdit ? '수정하기' : '등록하기');
  $('#qrConfirmTitle').text(isEdit ? 'API를 수정할까요?' : 'API를 등록할까요?');
  $('#qrConfirmOk').text(isEdit ? '수정' : '등록');
  if (isEdit) {
    $('#defHeadTitle').text('API 수정');
    $('#defHeadSub').html('<b>' + defEsc(apiNm || '') + '</b> API를 수정합니다.');
  } else {
    $('#defHeadTitle').text('API 등록');
    $('#defHeadSub').html(g_def_origHeadSub);
  }
}

function defMethodNm(methodCd) {
  return qrCodeNm(g_def_mthTypeList, methodCd);
}

function defOnApiClassChange() {
  var apiClass = $('input[name="apiClass"]:checked').val();
  if (apiClass === 'APIGUB1020') { $('#defPrivateRow').removeClass('qr_hide'); }
  else { $('#defPrivateRow').addClass('qr_hide'); }
}

function defOnMethodChange() {
  // Content-Type은 이 화면에서 저장되지 않는 값이라 별도 토글 없음(원본 quickApiReg의 cntTypeCd와 동일하게 미사용).
}

/* 입력 파라미터는 최상위 request 오브젝트, 출력 파라미터는 최상위 response 오브젝트로 시작한다
   (이 회사 API 공통 규칙). request 아래에는 모든 API 공통으로 들어가는 기본 필드 4개도 같이 깔아준다.
   이미 하위 필드가 있는 트리는 덮어쓰지 않는다(기존 API를 불러온 경우 등). */
function defInitDefaultParamTree() {
  if (!PT_STORE['in'] || PT_STORE['in'].length === 0) {
    var reqNode = ptNode('request', 'DATTYP1050', false, '', '', []);
    var defaults = [
      ['TRANSACTIONID', '시스템 발급 일련번호 (VOC 응대용도)'],
      ['SEQUENCENO', '시스템 내부 구간순서'],
      ['USERID', '특정 CP가 사용하는 ID'],
      ['SCREENID', '특정 CP가 사용되는 ID']
    ];
    for (var i = 0; i < defaults.length; i++) {
      var child = ptNode(defaults[i][0], 'DATTYP1010', false, defaults[i][1], '');
      child.parentTempId = reqNode.tempId;
      reqNode.kids.push(child);
    }
    PT_STORE['in'] = [reqNode];
  }
  if (!PT_STORE.out || PT_STORE.out.length === 0) {
    var resNode = ptNode('response', 'DATTYP1050', false, '', '', []);
    var outDefaults = [
      ['returnCode', true, '결과코드 (0 : Fail, 1 : Success)'],
      ['returnDesc', false, '결과설명']
    ];
    for (var j = 0; j < outDefaults.length; j++) {
      var outChild = ptNode(outDefaults[j][0], 'DATTYP1010', outDefaults[j][1], outDefaults[j][2], '');
      outChild.parentTempId = resNode.tempId;
      resNode.kids.push(outChild);
    }
    PT_STORE.out = [resNode];
  }
}

/* ---------------- 템플릿 선택(퍼블_v16.0) ---------------- */

var g_def_selectedTmpltNm = '';

/* quickApiReg.js의 qrSelectTemplate과 동일한 카드 클릭 처리다. 다만 이 화면은 입력 파라미터를
   항상 request 오브젝트로 감싸는 규칙이 있어서, PT_STORE['in']을 통째로 갈아치우지 않고
   request 노드의 kids만 템플릿 값으로 바꾼다(request 래퍼 자체는 유지). */
function defSelectTemplate(el) {
  var $el = $(el);
  $('#qrTmpltGrid .qr_tmplt_row').removeClass('qr_selected');
  $el.addClass('qr_selected');

  var tmpltNm = $el.attr('data-tmplt-nm');
  var apiClass = $el.attr('data-api-class');
  var mthCd = $el.attr('data-mth-cd');
  var path = $el.attr('data-path');
  var paramsJson = $el.attr('data-params');

  g_def_selectedTmpltNm = tmpltNm;

  if (apiClass) {
    $('input[name="apiClass"][value="' + apiClass + '"]').prop('checked', true);
    defOnApiClassChange();
  }
  if (mthCd) { $('#methodCd').val(mthCd); }
  if (path && !$('#apiPath').val()) { $('#apiPath').val(path); defSyncFullPath(); }

  var parsed = {};
  if (paramsJson) {
    try { parsed = JSON.parse(paramsJson); } catch (e) { parsed = {}; }
  }
  // 예전 템플릿(quickApiReg 시절)은 배열 하나(입력 파라미터만) 형태고, 새 템플릿은
  // {in:[...], out:[...]} 형태로 request/response 양쪽을 같이 담을 수 있다. 둘 다 지원한다.
  var inParams = Array.isArray(parsed) ? parsed : (parsed['in'] || []);
  var outParams = Array.isArray(parsed) ? [] : (parsed.out || []);

  defInitDefaultParamTree(); // request/response 래퍼가 아직 없으면(빈 트리) 먼저 만들어둔다
  defFillWrapperKids(PT_STORE['in'][0], inParams);
  defFillWrapperKids(PT_STORE.out[0], outParams);
  ptRenderPreview('def', defRefreshSummary);

  $('#qrTmpltModal').addClass('qr_hide');
}

/* 템플릿의 파라미터 배열로 request/response 래퍼 노드의 하위 필드를 교체한다.
   템플릿에 그 스코프 값이 없으면(len 0) 기존 기본값(request 4종/response 2종)을 그대로 둔다. */
function defFillWrapperKids(wrapperNode, params) {
  if (!params || params.length === 0) { return; }
  wrapperNode.kids = [];
  for (var i = 0; i < params.length; i++) {
    var p = params[i];
    // 템플릿(dfltParamJson)의 type은 이미 DATTYP comn_cd 값이다.
    var child = ptNode(p.name, p.type || 'DATTYP1010', p.required !== 'N', p.desc, p.exam || '');
    child.parentTempId = wrapperNode.tempId;
    wrapperNode.kids.push(child);
  }
}

function defFilterTmpltList(keyword) {
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

/* ---------------- Provider(단위서비스코드) 선택 ---------------- */

/* 팝업을 열 때마다 현재 선택된 값(#providerSeq)에 qr_selected 표시를 해준다 - 템플릿 선택
   팝업과 달리 선택 후에도 다시 열어 바꿀 수 있어야 하므로 매번 동기화. */
function defOpenProviderModal() {
  var curSeq = $('#providerSeq').val();
  $('#qrProviderGrid .qr_tmplt_row').each(function () {
    $(this).toggleClass('qr_selected', $(this).attr('data-seq') === curSeq);
  });
  $('#qrProviderSearch').val('');
  defFilterProviderList('');
  $('#qrProviderModal').removeClass('qr_hide');
}

function defSelectProvider(el) {
  var $el = $(el);
  $('#providerSeq').val($el.attr('data-seq'));
  $('#providerNmDisp').val($el.attr('data-nm'));
  $('#qrProviderModal').addClass('qr_hide');
}

function defFilterProviderList(keyword) {
  keyword = (keyword || '').trim().toLowerCase();
  var visibleCount = 0;
  $('#qrProviderGrid .qr_tmplt_row').each(function () {
    var nm = ($(this).attr('data-nm') || '').toLowerCase();
    var code = ($(this).attr('data-code') || '').toLowerCase();
    var match = !keyword || nm.indexOf(keyword) > -1 || code.indexOf(keyword) > -1;
    $(this).toggleClass('qr_hide', !match);
    if (match) { visibleCount++; }
  });
  $('#qrProviderEmptyMsg').toggleClass('qr_hide', visibleCount > 0);
}

function defProviderNmBySeq(seq) {
  for (var i = 0; i < g_def_providerList.length; i++) {
    if (String(g_def_providerList[i].seq) === String(seq)) { return g_def_providerList[i].providerNm; }
  }
  return '';
}

function defEsc(s) {
  return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}

/* ---------------- 저장 ---------------- */

/* "등록하기" 클릭 시: 검증만 하고 통과하면 저장 확인 팝업을 띄운다. 실제 저장은 defDoSave()에서. */
function defOnSaveClick() {
  var autId = $('#autId').val();
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();
  var endpntTbUrl = $('#endpntTbUrl').val();
  var endpntPrdUrl = $('#endpntPrdUrl').val();

  var hasErr = false;
  $('#apiNmErr, #apiPathErr, #endpntTbUrlErr, #endpntPrdUrlErr').removeClass('qr_show');
  $('#apiNm, #apiPath, #endpntTbUrl, #endpntPrdUrl').removeClass('qr_input_err');

  if (!apiNm) { $('#apiNmErr').addClass('qr_show'); $('#apiNm').addClass('qr_input_err'); hasErr = true; }
  if (!apiPath || apiPath.charAt(0) !== '/') { $('#apiPathErr').addClass('qr_show'); $('#apiPath').addClass('qr_input_err'); hasErr = true; }
  if (!autId) { alert_message('권한그룹을 선택해 주세요.'); hasErr = true; }
  /* 배포 후 게이트웨이가 실제로 호출하는 주소라 필수값이다 - 고급 설정(선택)이 아니라 여기서 검증. */
  if (!$.trim(endpntTbUrl)) { $('#endpntTbUrlErr').addClass('qr_show'); $('#endpntTbUrl').addClass('qr_input_err'); hasErr = true; }
  if (!$.trim(endpntPrdUrl)) { $('#endpntPrdUrlErr').addClass('qr_show'); $('#endpntPrdUrl').addClass('qr_input_err'); hasErr = true; }

  if (hasErr) { return; }

  $('#qrConfirmModal').removeClass('qr_hide');
}

function defDoSave() {
  var apiSpcNo = $('#apiSpcNo').val();
  var autId = $('#autId').val();
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();

  var formData = {
    apiSpcNo: apiSpcNo,
    apiNo: $('#editApiNo').val(),
    autId: autId,
    apiNm: apiNm,
    apiDesc: $('#apiDesc').val(),
    apiClass: $('input[name="apiClass"]:checked').val(),
    apiPath: apiPath,
    methodCd: $('#methodCd').val(),
    apiHandlerCd: $('#apiHandlerCd').val(),
    providerSeq: $('#providerSeq').val(),
    endpntTbUrl: $('#endpntTbUrl').val(),
    endpntPrdUrl: $('#endpntPrdUrl').val(),
    endpntClientIp: $('#endpntClientIp').val(),
    endpntTimeout: $('#endpntTimeout').val(),
    resmapResCdField: $('#resmapResCdField').val(),
    resmapSuccVal: $('#resmapSuccVal').val(),
    resmapErrCdField: $('#resmapErrCdField').val(),
    resmapErrMsgField: $('#resmapErrMsgField').val(),
    hdpApiEndpointId: $('#hdpApiEndpointId').val(),
    hdpReqApiName: $('#hdpReqApiName').val(),
    hdpApiOutFormat: $('#hdpApiOutFormat').val(),
    hdpApiOutCommonParam: $('#hdpApiOutCommonParam').val(),
    hdpReqMappingToBody: $('#hdpReqMappingToBody').val(),
    hdpResMappingToBody: $('#hdpResMappingToBody').val()
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

  var wasEdit = !!formData.apiNo;

  $.ajax({
    url: c_url + 'api/spcreg/def/savApiDefRegAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        $('#defResultMsg').text(wasEdit ? '✓ 수정이 완료되었습니다.' : '✓ 등록이 완료되었습니다.');
        $('#defResultBox').removeClass('qr_hide');
        $('html, body').animate({ scrollTop: $('#defResultBox').offset().top - 80 }, 300);
        defResetToCreate();
        defLoadGroupApiList();
      } else {
        alert_message(res.message || '등록 중 오류가 발생했습니다.');
      }
    },
    error: function () {
      alert_message('등록 중 오류가 발생했습니다.');
    }
  });
}
