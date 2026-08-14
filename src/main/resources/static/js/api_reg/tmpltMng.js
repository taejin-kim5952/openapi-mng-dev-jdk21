/* tmpltMng.js - "빠른 API 등록" 템플릿 관리(목록/등록/수정) 전용 스크립트.
   YAML 파싱은 quickRegShared.js(qrParseOasYaml)를 그대로 재사용 - 실제 등록 화면의
   "YAML 등록"과 동일한 로직으로 미리보기한다. */

var g_tm_cm = null;
var g_tm_lastParsed = null;

// dfltParamJson에서 요청/응답 파라미터 배열을 뽑아낸다. apiDefReg.js의 defSelectTemplate과 같은 규칙:
//  - 예전(quickApiReg) 템플릿: 평탄 배열 [{name,type,required,desc}, ...] = 입력 파라미터만(응답 없음)
//  - 최신 템플릿: {in:[...], out:[...]} = 요청/응답 파라미터를 분리해서 담음
function tmExtractParams(dfltParamJson) {
  try {
    var parsed = JSON.parse(dfltParamJson || '[]');
    if (Array.isArray(parsed)) { return { inParams: parsed, outParams: [] }; }
    return {
      inParams: Array.isArray(parsed.in) ? parsed.in : [],
      outParams: Array.isArray(parsed.out) ? parsed.out : []
    };
  } catch (e) {
    return { inParams: [], outParams: [] };
  }
}

// tmpltYaml 없이 만들어진(예: 초기 시드) 템플릿을 수정하러 들어온 경우, 기존 필드
// (dfltParamJson/mthTypeCd/pathPattern 등)로 YAML을 재구성해 보여준다.
function tmRebuildDefaultYaml(existing) {
  var builtYaml = '';
  try {
    var methodNm = qrCodeNm(g_qr_mthTypeList, existing.mthTypeCd);
    var params = tmExtractParams(existing.dfltParamJson);
    var fieldDefaults = {};
    try { fieldDefaults = JSON.parse(existing.dfltFieldJson || '{}') || {}; } catch (e) { fieldDefaults = {}; }
    builtYaml = qrBuildOasYaml(existing.tmpltNm, existing.tmpltDesc, existing.pathPattern, methodNm, existing.cntTypeCd,
      { params: params.inParams, outParams: params.outParams, fieldDefaults: fieldDefaults });
  } catch (e) {
    // eslint-disable-next-line no-console
    console.error('템플릿 기본 YAML 재구성 실패 - dfltParamJson/mthTypeCd 데이터를 확인하세요.', e, existing);
  }
  if (!builtYaml || !builtYaml.trim()) {
    // 재구성도 실패한 경우 최소한의 뼈대라도 보여줘서 완전한 빈 화면은 피한다.
    builtYaml = 'openapi: 3.0.3\ninfo:\n  title: ' + (existing.tmpltNm || '') + '\n  version: "1.0"\npaths: {}\n';
  }
  return builtYaml;
}

$(document).ready(function () {
  // 목록 화면
  if ($('#tmMngRows').length > 0) {
    tmInitIcons();
  }

  // 폼 화면
  var yamlArea = document.getElementById('tmYamlArea');
  if (yamlArea) {
    g_tm_cm = qrInitYamlEditor(yamlArea);

    if (g_tm_existing && (!g_tm_existing.tmpltYaml || !g_tm_existing.tmpltYaml.trim())) {
      g_tm_cm.setValue(tmRebuildDefaultYaml(g_tm_existing));
      g_tm_cm.refresh();
    }
  }
});

/* ---------------- 목록 화면 ---------------- */

function tmInitIcons() {
  $('.qr_tmplt_icon_sm').each(function () {
    var mthCd = $(this).attr('data-mth-cd') || '';
    $(this).html(qrIconForMethodNm(qrCodeNm(g_qr_mthTypeList, mthCd)));
  });
}

function tmFilterList(keyword) {
  keyword = (keyword || '').trim().toLowerCase();
  var visibleCount = 0;
  $('#tmMngRows .qr_tm_row').each(function () {
    var nm = ($(this).attr('data-nm') || '').toLowerCase();
    var desc = ($(this).attr('data-desc') || '').toLowerCase();
    var match = !keyword || nm.indexOf(keyword) > -1 || desc.indexOf(keyword) > -1;
    $(this).toggleClass('qr_hide', !match);
    if (match) { visibleCount++; }
  });
  $('#tmMngEmptyMsg').toggleClass('qr_hide', visibleCount > 0);
}

function tmDeleteTmplt(tmpltNo) {
  if (!confirm('이 템플릿을 삭제하시겠습니까?\n(빠른 API 등록 화면의 템플릿 목록에서 더 이상 보이지 않습니다.)')) {
    return;
  }
  $.ajax({
    url: c_url + 'api/quickreg/tmplt/delTmpltAjax.do',
    type: 'POST',
    data: { tmpltNo: tmpltNo },
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        location.reload();
      } else {
        alert_message(res.message || '삭제 중 오류가 발생했습니다.');
      }
    },
    error: function () { alert_message('삭제 중 오류가 발생했습니다.'); }
  });
}

/* ---------------- 등록/수정 폼 ---------------- */

function tmPreview() {
  var yamlText = g_tm_cm ? g_tm_cm.getValue() : '';
  $('#tmYamlErr').addClass('qr_hide').text('');

  if (!yamlText || !yamlText.trim()) {
    $('#tmYamlErr').removeClass('qr_hide').text('YAML 내용을 입력하세요.');
    return null;
  }

  var parsed;
  try {
    parsed = qrParseOasYaml(yamlText);
  } catch (e) {
    $('#tmYamlErr').removeClass('qr_hide').text('YAML을 해석할 수 없습니다: ' + (e && e.message ? e.message : e));
    return null;
  }

  g_tm_lastParsed = parsed;

  var fieldDefaultCount = Object.keys(parsed.fieldDefaults || {}).length;
  $('#tmPvApiNm').text(parsed.apiNm || '(이름 없음)');
  $('#tmPvSummary').text(
    'OAS ' + (parsed.oasVersion || '3.0') + ' · ' +
    (parsed.methodNm || '-') + (parsed.apiPath ? (' ' + parsed.apiPath) : '') +
    (parsed.cntTypeCd ? (' · ' + parsed.cntTypeCd) : '') +
    ' · 응답 ' + (parsed.outParams || []).length + '건' +
    (fieldDefaultCount > 0 ? (' · 추가 기본값(x-def-fields) ' + fieldDefaultCount + '건') : '')
  );
  $('#tmPvParamCount').text((parsed.params || []).length);
  $('#tmPreviewBox').removeClass('qr_hide');

  var $rows = $('#tmPvParamRows').empty();
  (parsed.params || []).forEach(function (p) {
    $rows.append(
      '<tr><td>' + qrEsc(p.name) + '</td><td>' + qrEsc(qrCodeNm(g_qr_dataTypeList, p.type)) + '</td><td>' +
      (p.required === 'Y' ? '필수' : '선택') + '</td><td>' + qrEsc(p.desc) + '</td></tr>'
    );
  });
  $('#tmPvParamTable').toggleClass('qr_hide', (parsed.params || []).length === 0);

  if (parsed.multiplePaths) {
    $('#tmYamlErr').removeClass('qr_hide').text('여러 Path/Method가 감지되었습니다. 첫 번째 항목만 템플릿에 반영됩니다.');
  }

  return parsed;
}

function tmStripParam(p) {
  var item = { name: p.name, type: p.type, required: p.required, desc: p.desc };
  if (p.exam) { item.exam = p.exam; }
  return item;
}

function tmSaveTmplt() {
  var tmpltNm = $('#tmpltNm').val();
  if (!tmpltNm || !tmpltNm.trim()) {
    alert_message('템플릿 이름을 입력해 주세요.');
    return;
  }

  var parsed = g_tm_lastParsed || tmPreview();
  if (!parsed) {
    return;
  }

  var dfltParamJson = JSON.stringify({
    in: (parsed.params || []).map(tmStripParam),
    out: (parsed.outParams || []).map(tmStripParam)
  });

  var formData = {
    tmpltNo: $('#tmpltNo').val(),
    tmpltNm: tmpltNm,
    tmpltDesc: $('#tmpltDesc').val(),
    apiClass: $('input[name="tmApiClass"]:checked').val(),
    mthTypeCd: parsed.methodCd || '',
    cntTypeCd: parsed.cntTypeCd || '',
    pathPattern: parsed.apiPath || '',
    dfltParamJson: dfltParamJson,
    dfltFieldJson: JSON.stringify(parsed.fieldDefaults || {}),
    tmpltYaml: g_tm_cm ? g_tm_cm.getValue() : ''
  };

  $.ajax({
    url: c_url + 'api/quickreg/tmplt/savTmpltAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        location.href = c_url + 'api/quickreg/tmplt/mvTmpltMng.do';
      } else {
        alert_message(res.message || '저장 중 오류가 발생했습니다.');
      }
    },
    error: function () { alert_message('저장 중 오류가 발생했습니다.'); }
  });
}
