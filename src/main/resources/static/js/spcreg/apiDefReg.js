/* apiDefReg.js - "API 등록"(기존 SPC에 API 추가) 화면 전용 스크립트.
   SPC는 앞 화면(spcReg)에서 이미 확정되어 apiSpcNo/spcBasPath가 hidden 값으로 넘어오므로,
   이 화면엔 그룹 선택 UI/로직이 없다(퍼블 v14.0 확인 사항). 권한그룹(autId)도 서버가 그 그룹의
   sysId로 이미 필터링해 내려준다. 공용 유틸(alert_message 등)은 apiGlobalScript.js, 파라미터
   트리+YAML 편집은 paramTreeEditor.js(공용, 팝업 id는 qr* 고정)를 쓴다. */

var g_def_paramScope = 'in'; // 파라미터 미리보기에서 현재 활성 탭(설계 버튼이 어느 스코프를 열지 결정)

$(document).ready(function () {
  defOnMethodChange();
  defOnApiClassChange();

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
      defRefreshSummary();
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
  if (g_sysId) { defLoadSysApiTree(g_sysId); }
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

/* ---------------- 좌측 API 트리 (이 그룹이 속한 서비스의 기존 등록 현황, 읽기 전용 참고용) ---------------- */

function defLoadSysApiTree(sysId) {
  var $tree = $('#defLeftTree');
  if (!sysId) {
    $tree.empty();
    $('#defLeftHint').text('그룹을 선택하면 이미 등록된 API가 표시됩니다.');
    return;
  }
  $('#defLeftHint').text('불러오는 중...');
  $.ajax({
    url: c_url + 'api/spcreg/def/selSysApiTreeAjax.do',
    type: 'GET',
    data: { sysId: sysId },
    dataType: 'json',
    success: function (res) { defRenderSysApiTree(res.list || []); },
    error: function () {
      $tree.empty();
      $('#defLeftHint').text('목록을 불러오지 못했습니다.');
    }
  });
}

function defRenderSysApiTree(list) {
  var $tree = $('#defLeftTree');
  $tree.empty();

  if (!list || list.length === 0) {
    $('#defLeftHint').text('이 서비스에 등록된 API가 아직 없습니다. 새로 만드는 첫 API입니다.');
    return;
  }
  $('#defLeftHint').text('이 서비스에 이미 등록된 API입니다 (참고용, 읽기 전용).');

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
    html += '<div class="qr_lt_spc_nm">' + defEsc(spcData.spcNm) + ' <span style="font-weight:400;color:#999;">(' + defEsc(spcData.ver) + ')</span></div>';
    for (var c = 0; c < spcData.ctgryOrder.length; c++) {
      var ctgryNm = spcData.ctgryOrder[c];
      html += '<div class="qr_lt_ctgry"><div class="qr_lt_ctgry_nm">' + defEsc(ctgryNm) + '</div>';
      var apis = spcData.ctgries[ctgryNm];
      for (var a = 0; a < apis.length; a++) {
        html += '<div class="qr_lt_api"><span class="qr_lt_method">' + defEsc(defMethodNm(apis[a].methodCd)) + '</span><span class="qr_lt_path">' + defEsc(apis[a].apiPath || '') + '</span></div>';
      }
      html += '</div>';
    }
    html += '</div>';
    $tree.append(html);
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

function defEsc(s) {
  return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}

/* ---------------- 저장 ---------------- */

function defSaveApiDefReg() {
  var apiSpcNo = $('#apiSpcNo').val();
  var autId = $('#autId').val();
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();

  var hasErr = false;
  $('#apiNmErr, #apiPathErr').removeClass('qr_show');
  $('#apiNm, #apiPath').removeClass('qr_input_err');

  if (!apiNm) { $('#apiNmErr').addClass('qr_show'); $('#apiNm').addClass('qr_input_err'); hasErr = true; }
  if (!apiPath || apiPath.charAt(0) !== '/') { $('#apiPathErr').addClass('qr_show'); $('#apiPath').addClass('qr_input_err'); hasErr = true; }
  if (!autId) { alert_message('권한그룹을 선택해 주세요.'); hasErr = true; }

  if (hasErr) { return; }

  var formData = {
    apiSpcNo: apiSpcNo,
    autId: autId,
    apiNm: apiNm,
    apiDesc: $('#apiDesc').val(),
    apiClass: $('input[name="apiClass"]:checked').val(),
    apiPath: apiPath,
    methodCd: $('#methodCd').val(),
    apiHandlerCd: $('#apiHandlerCd').val(),
    providerSeq: $('#providerSeq').val()
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
    url: c_url + 'api/spcreg/def/savApiDefRegAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        $('#defResultBox').removeClass('qr_hide');
        $('html, body').animate({ scrollTop: $('#defResultBox').offset().top - 80 }, 300);
      } else {
        alert_message(res.message || '등록 중 오류가 발생했습니다.');
      }
    },
    error: function () {
      alert_message('등록 중 오류가 발생했습니다.');
    }
  });
}
