/* spcReg.js - "SPC 등록" 화면 전용 스크립트.
   기존 quickApiReg.js의 아코디언/좌측트리/검증 패턴을 참고해 이 화면 전용으로 새로 작성했다.
   공용 유틸(alert_message 등)은 apiGlobalScript.js에서 가져다 쓴다. */

/* 브라우저 뒤로가기(bfcache)로 이 페이지에 복귀하면 <select>의 표시 라벨은 남아있는데
   change 이벤트가 다시 안 붙어서, 이전에 남아있던 qr_input_err/qr_show(에러 표시)가
   실제 선택값과 안 맞게 화면에 그대로 남는 경우가 있다. bfcache 복귀 시 에러 표시를
   지우고 폼 상태를 현재 select 값 기준으로 다시 동기화한다. */
$(window).on('pageshow', function (e) {
  if (!e.originalEvent || !e.originalEvent.persisted) { return; }
  $('.qr_input_err').removeClass('qr_input_err');
  $('.qr_err').removeClass('qr_show');
  grpOnSysIdChange();
});

var g_grp_pendingApiList = []; // YAML 등록으로 파싱된, 그룹 저장 직후 함께 등록할 API 목록
var g_grp_isEdit = false; // true면 신규 생성이 아니라 기존 그룹(apiSpcNo) 수정

$(document).ready(function () {
  $('.sv_acc_head').on('click', function () {
    $(this).closest('.sv_acc').toggleClass('sv_open');
  });

  /* 전체보기 펼침/접힘 — max-height 트랜지션 (.qr_collapsed <-> 실측 높이) */
  function expandMoreBox() {
    var $box = $('#grpMoreBox');
    if (!$box.hasClass('qr_collapsed')) { return; }
    $('#grpMoreBtn').attr('aria-expanded', 'true').addClass('qr_on').find('.qr_more_lbl').text('접기');
    $box.removeClass('qr_collapsed').css('max-height', $box.find('.qr_more_inner').outerHeight(true) + 'px');
  }
  $('#grpMoreBtn').on('click', function () {
    var $box = $('#grpMoreBox'), open = $box.hasClass('qr_collapsed');
    if (open) { expandMoreBox(); return; }
    $(this).attr('aria-expanded', 'false').removeClass('qr_on').find('.qr_more_lbl').text('전체보기');
    $box.css('max-height', 0).addClass('qr_collapsed');
  });

  /* 배너 미리보기 — 그룹 이름/호스트/기본경로/스키마를 실시간 반영 */
  function syncBanner() {
    var scheme = ($('#schemeCd').val() || 'https').split(',')[0];
    var host = $.trim($('#host').val()) || 'host';
    var base = $.trim($('#basPath').val());
    $('#grpBannerNm').text($.trim($('#spcNm').val()) || '새 그룹');
    $('#grpBannerUrl').text(scheme + '://' + host + base);
  }
  $('#spcNm, #host, #basPath').on('input', syncBanner);
  $('#schemeCd').on('change', syncBanner);
  syncBanner();

  /* 필수값 검증 — 에러 상태는 .qr_input_err + .qr_err.qr_show. 권한그룹(autId)은 hidden 필드로
     서비스 선택 시 자동 채워지므로 별도 입력칸 검증은 없고, 서비스 검증에 묻어간다. */
  var REQUIRED = [
    { id: '#sysId',   err: '#sysIdErr' },
    { id: '#spcNm',   err: '#spcNmErr' },
    { id: '#host',    err: '#hostErr' },
    { id: '#basPath', err: '#basPathErr', test: function (v) { return v.charAt(0) === '/'; } }
  ];
  function validate() {
    var ok = true, first = null;
    for (var i = 0; i < REQUIRED.length; i++) {
      var r = REQUIRED[i], $f = $(r.id), v = $.trim($f.val());
      var bad = !v || (r.test && !r.test(v));
      $f.toggleClass('qr_input_err', bad);
      if (r.err) { $(r.err).toggleClass('qr_show', bad); }
      if (bad) { ok = false; if (!first) first = $f; }
    }
    if (ok && !$.trim($('#autId').val())) {
      $('#sysId').addClass('qr_input_err');
      $('#sysIdErr').text('선택한 서비스에 권한그룹이 없습니다. 마이페이지에서 권한 신청을 먼저 진행하세요.').addClass('qr_show');
      ok = false; first = $('#sysId');
    }
    // 호스트/기본경로는 "전체보기"(접힘) 안에 있으므로, 거기서 에러가 나면 펼쳐서 보여준다.
    if (!ok && first && first.closest('#grpMoreBox').length) { expandMoreBox(); }
    if (first) first.focus();
    return ok;
  }
  $('#sysId, #spcNm, #host, #basPath').on('input change', function () {
    $(this).removeClass('qr_input_err').closest('.qr_field').find('.qr_err').removeClass('qr_show');
  });

  /* apiSpcNo로 진입했다면(apiDefReg.html "그룹 정보 수정" 버튼) 기존 값을 불러와 수정 모드로 채운다.
     아니라면 기존과 동일하게, 새로고침/뒤로가기로 브라우저가 sysId 선택값을 복원해도 change 이벤트는
     안 뜨는 경우가 있어 로드 시 한 번 강제로 동기화한다(quickApiReg.js의 qrOnApiSpcNoChange 초기
     호출과 동일한 이유). */
  if (g_grp_existing) {
    grpFillFormFromExisting(g_grp_existing);
  } else {
    grpOnSysIdChange();
  }

  /* 저장 확인 팝업(퍼블_v15.0) — 검증을 통과해도 바로 저장하지 않고 팝업을 먼저 띄운다.
     [취소]는 팝업만 닫고, [저장]을 눌러야 실제 grpDoSave()가 실행된다. */
  $('#grpSave').on('click', function () {
    if (!validate()) { return; }

    // validate()를 통과했다는 건 모든 필드가 유효하다는 뜻이므로, 이전 시도에서 남아있을 수 있는
    // 에러 표시(빨간 테두리/문구)를 여기서 한 번 더 확실히 지운다 - 개별 필드의 input/change
    // 리스너가 어떤 이유로든 못 지운 잔여 에러가 저장 성공 후에도 화면에 남는 것을 방지.
    $('#grpRegForm .qr_input_err').removeClass('qr_input_err');
    $('#grpRegForm .qr_err').removeClass('qr_show');

    $('#qrConfirmModal').removeClass('qr_hide');
  });
  $('#qrConfirmCancel').on('click', function () { $('#qrConfirmModal').addClass('qr_hide'); });
  $('#qrConfirmOk').on('click', function () {
    $('#qrConfirmModal').addClass('qr_hide');
    grpDoSave();
  });
  $('#qrConfirmModal').on('click', function (e) { if (e.target === this) { $(this).addClass('qr_hide'); } });

  /* YAML 등록 팝업(퍼블_v16.0) — CodeMirror 에디터 + 파일 불러오기. [적용]은 저장을 바로 하지 않고
     그룹 폼(이름/호스트/기본경로/버전)만 채운 뒤, 파싱된 API 목록을 g_grp_pendingApiList에 보관해뒀다가
     "저장하고 API 추가하러 가기" → grpDoSave()에서 그룹 저장 성공 직후 한꺼번에 등록한다. */
  var grpCM = null;
  $('#grpBtnOpenYamlModal').on('click', function () {
    $('#grpYamlModal').removeClass('qr_hide');
    if (!grpCM && window.CodeMirror) {
      grpCM = CodeMirror.fromTextArea(document.getElementById('grpYamlEditor'), {
        mode: 'text/x-yaml', lineNumbers: true, lineWrapping: true,
        indentUnit: 2, tabSize: 2, foldGutter: true,
        gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter']
      });
    }
    if (grpCM) { setTimeout(function () { grpCM.refresh(); grpCM.focus(); }, 30); }
  });
  $('#grpYamlClose, #grpYamlCancel').on('click', function () { $('#grpYamlModal').addClass('qr_hide'); });
  $('#grpYamlModal').on('click', function (e) { if (e.target === this) { $(this).addClass('qr_hide'); } });
  $('#grpYamlFileBtn').on('click', function () { $('#grpYamlFile').trigger('click'); });
  $('#grpYamlFile').on('change', function () {
    var f = this.files && this.files[0];
    if (!f) { return; }
    var reader = new FileReader();
    reader.onload = function (ev) {
      if (grpCM) { grpCM.setValue(ev.target.result); } else { $('#grpYamlEditor').val(ev.target.result); }
      $('#grpYamlFileNm').text(f.name);
    };
    reader.readAsText(f, 'utf-8');
    this.value = '';
  });
  $('#grpYamlApply').on('click', function () {
    var yamlText = grpCM ? grpCM.getValue() : $('#grpYamlEditor').val();
    grpApplyYaml(yamlText);
  });
});

function grpDoSave() {
  var formData = {
    apiSpcNo: $('#editApiSpcNo').val(),
    sysId: $('#sysId').val(),
    autId: $('#autId').val(),
    apiNm: $.trim($('#spcNm').val()),
    apiDesc: $('#spcDesc').val(),
    host: $.trim($('#host').val()),
    basPath: $.trim($('#basPath').val()),
    ver: $('#ver').val(),
    schemeCd: $('#schemeCd').val(),
    apiSchema: $('#schemeCd').val(),
    apiClass: $('input[name="apiClass"]:checked').val(),
    testBaseUrl: $('#testBaseUrl').val(),
    apiVeriBaseurl: $('#testBaseUrl').val(),
    bstgwYn: $('input[name="beastGw"]:checked').val()
  };

  $.ajax({
    url: c_url + 'api/spcreg/savSpcRegAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1') {
        // apiGlobalScript.js의 beforeunload 가드(isChange)는 저장 성공 후에도 리셋되지 않아서,
        // 이 화면 이동을 "저장 안 하고 나가기"로 오인해 브라우저 기본 경고창을 띄운다. 방금
        // 저장이 실제로 성공했으므로 리다이렉트 전에 꺼서 그 경고가 안 뜨게 한다.
        isChange = false;
        var apiSpcNo = res.apiSpcNo;
        if (g_grp_pendingApiList.length > 0) {
          grpSaveApiListThen(apiSpcNo, g_grp_pendingApiList.slice(), function () {
            location.href = c_url + 'api/spcreg/def/mvApiDefReg.do?apiSpcNo=' + apiSpcNo;
          });
        } else {
          location.href = c_url + 'api/spcreg/def/mvApiDefReg.do?apiSpcNo=' + apiSpcNo;
        }
      } else {
        alert_message(res.message || '등록 중 오류가 발생했습니다.');
      }
    },
    error: function () {
      alert_message('등록 중 오류가 발생했습니다.');
    }
  });
}

/* YAML로 파싱된 API 목록을 방금 만든 그룹(apiSpcNo) 밑에 순서대로 하나씩 등록한다. 새로 만든
   apiDefReg 화면의 저장 엔드포인트(savApiDefRegAjax.do)를 그대로 재사용 - 카테고리 재사용/최초생성
   로직도 거기 있는 걸 그대로 타므로 별도 배치 API를 새로 만들 필요가 없다. 순차 처리하는 이유는
   같은 그룹의 "첫 카테고리 재사용" 판단이 이전 건 저장 완료를 전제로 하기 때문(동시에 쏘면 카테고리가
   중복 생성될 수 있음). */
function grpSaveApiListThen(apiSpcNo, apiList, onDone) {
  if (apiList.length === 0) { onDone(); return; }
  var entry = apiList.shift();

  var formData = {
    apiSpcNo: apiSpcNo,
    autId: $('#autId').val(),
    apiNm: entry.apiNm,
    apiDesc: entry.apiDesc,
    apiClass: 'APIGUB1010', // YAML엔 Public/Private 구분이 없어 기본값(Public)으로 등록
    apiPath: entry.apiPath,
    methodCd: entry.methodCd
  };
  for (var i = 0; i < entry.paramList.length; i++) {
    var p = entry.paramList[i];
    formData['paramList[' + i + '].paramNm'] = p.paramNm;
    formData['paramList[' + i + '].dataTypeCd'] = p.dataTypeCd;
    formData['paramList[' + i + '].required'] = p.required;
    formData['paramList[' + i + '].paramDesc'] = p.paramDesc;
    formData['paramList[' + i + '].paramTypeCd'] = p.paramTypeCd;
    formData['paramList[' + i + '].paramLoc'] = p.paramLoc;
    formData['paramList[' + i + '].exam'] = p.exam;
    formData['paramList[' + i + '].personalData'] = p.personalData;
    formData['paramList[' + i + '].tempId'] = p.tempId;
    formData['paramList[' + i + '].parentTempId'] = p.parentTempId;
  }

  $.ajax({
    url: c_url + 'api/spcreg/def/savApiDefRegAjax.do',
    type: 'POST',
    data: formData,
    dataType: 'json',
    success: function () { grpSaveApiListThen(apiSpcNo, apiList, onDone); },
    error: function () { grpSaveApiListThen(apiSpcNo, apiList, onDone); } // 하나 실패해도 나머지는 계속 시도
  });
}

/* ---------------- YAML 등록 ---------------- */

/* [적용] 클릭 시: YAML을 파싱해서 그룹 폼(이름/호스트/기본경로/버전)을 채우고, API 목록은
   grpDoSave()에서 그룹 저장 성공 직후 함께 등록하도록 g_grp_pendingApiList에 담아둔다. */
function grpApplyYaml(yamlText) {
  $('#grpYamlErr').text('');
  if (!yamlText || !yamlText.trim()) {
    $('#grpYamlErr').text('YAML을 입력하거나 파일을 불러와 주세요.');
    return;
  }

  var spec;
  try {
    spec = grpParseYamlSpec(yamlText);
  } catch (e) {
    $('#grpYamlErr').text('YAML을 해석할 수 없습니다: ' + (e && e.message ? e.message : e));
    return;
  }

  if (spec.spcNm) { $('#spcNm').val(spec.spcNm).trigger('input'); }
  if (spec.host) { $('#host').val(spec.host); }
  if (spec.basPath) { $('#basPath').val(spec.basPath); }
  if (spec.ver) { $('#ver').val(spec.ver); }
  $('#spcNm, #host, #basPath').trigger('input');

  g_grp_pendingApiList = spec.apiList;
  if (spec.apiList.length > 0) {
    $('#grpYamlSummary').removeClass('qr_hide').text('YAML에서 API ' + spec.apiList.length + '건을 찾았습니다 — 그룹을 저장하면 함께 등록됩니다.');
  } else {
    $('#grpYamlSummary').addClass('qr_hide').text('');
  }

  $('#grpYamlModal').addClass('qr_hide');
}

/* OAS 3.0 YAML 문서 전체를 파싱해서 그룹(SPC) 기본값 + 여러 Path/Method(API) 목록을 뽑아낸다.
   quickRegShared.js의 qrParseOasYaml은 Path/Method 1개(빠른등록용) 제약이 있어 재사용하지 않고,
   이 화면 전용으로 모든 path/method를 순회하는 파서를 새로 작성했다. */
function grpParseYamlSpec(yamlText) {
  var doc = YAML.parse(yamlText);
  if (!doc || typeof doc !== 'object') { throw new Error('빈 문서이거나 형식이 올바르지 않습니다.'); }

  var spec = { spcNm: '', host: '', basPath: '/', ver: '1.0', apiList: [] };
  if (doc.info) {
    spec.spcNm = doc.info.title || '';
    spec.ver = doc.info.version || '1.0';
  }
  if (doc.servers && doc.servers.length > 0 && doc.servers[0].url) {
    var m = /^https?:\/\/([^/]+)(\/.*)?$/.exec(doc.servers[0].url);
    if (m) { spec.host = m[1]; spec.basPath = m[2] || '/'; }
  }

  var paths = doc.paths || {};
  var methodOrder = ['get', 'post', 'put', 'delete', 'patch'];

  Object.keys(paths).forEach(function (pathKey) {
    var methods = paths[pathKey] || {};
    methodOrder.forEach(function (mth) {
      var op = methods[mth];
      if (!op) { return; }

      var inNodes = [], queryNodes = [], outNodes = [];
      if (op.requestBody && op.requestBody.content && op.requestBody.content['application/json']) {
        inNodes = grpSchemaToWrappedNodes(op.requestBody.content['application/json'].schema || {}, 'request');
      }
      (op.parameters || []).forEach(function (p) {
        if (p['in'] && p['in'] !== 'query') { return; }
        queryNodes.push(ptFromSchema(p.name, p.schema || {}, !!p.required));
      });
      var successResp = op.responses && (op.responses['200'] || op.responses['201'] || op.responses['default']);
      if (successResp && successResp.content && successResp.content['application/json']) {
        outNodes = grpSchemaToWrappedNodes(successResp.content['application/json'].schema || {}, 'response');
      }

      var paramList = ptFlattenTree(inNodes, 'in')
        .concat(ptFlattenTree(queryNodes, 'query'))
        .concat(ptFlattenTree(outNodes, 'out'));

      spec.apiList.push({
        apiNm: op.summary || (mth.toUpperCase() + ' ' + pathKey),
        apiDesc: op.description || '',
        apiPath: pathKey,
        methodCd: 'MTHTYP10' + (methodOrder.indexOf(mth) + 1) + '0',
        paramList: paramList
      });
    });
  });

  return spec;
}

/* requestBody/response의 스키마를 트리로 바꾸면서 request/response 오브젝트로 감싼다(apiDefReg.js의
   defInitDefaultParamTree와 동일한 회사 공통 규칙). 문서 작성자가 이미 최상위를 request/response로
   감싸뒀다면 이중으로 감싸지 않고 그 노드를 그대로 쓴다. */
function grpSchemaToWrappedNodes(schema, wrapperName) {
  var kids = ptFromObject(schema || {});
  if (kids.length === 1 && ptIsBranch(kids[0]) && (kids[0].paramNm || '').toLowerCase() === wrapperName) {
    return [kids[0]];
  }
  var wrapNode = ptNode(wrapperName, 'DATTYP1050', false, '', '', kids);
  kids.forEach(function (k) { k.parentTempId = wrapNode.tempId; });
  return [wrapNode];
}

/* apiSpcNo로 진입했을 때(apiDefReg.html "그룹 정보 수정" 버튼) 서버가 내려준 기존 값을 폼에
   채운다. sysId/host/basPath는 .trigger('input'|'change')로 채워서 이미 바인딩된 syncBanner
   리스너가 그대로 반응하게 한다(별도 호출 불필요). */
function grpFillFormFromExisting(spc) {
  $('#editApiSpcNo').val(spc.apiSpcNo);
  $('#sysId').val(spc.sysId);
  grpOnSysIdChange();
  if (spc.autId) { $('#autId').val(spc.autId); }
  $('#spcNm').val(spc.spcNm).trigger('input');
  $('#spcDesc').val(spc.spcDesc);
  $('#host').val(spc.host).trigger('input');
  $('#basPath').val(spc.basPath).trigger('input');
  $('#ver').val(spc.ver);
  $('#schemeCd').val(spc.schemeCd).trigger('change');
  $('input[name="apiClass"][value="' + spc.apiClass + '"]').prop('checked', true);
  $('#testBaseUrl').val(spc.testBaseUrl);
  $('input[name="beastGw"][value="' + (spc.bstgwYn || 'N') + '"]').prop('checked', true);
  grpSetEditMode(true, spc.spcNm);
}

/* 신규 생성 화면과 "그룹 정보 수정" 화면은 폼 구조가 같아 하나의 템플릿을 재사용하되, 헤더/저장
   버튼 문구·확인 팝업·다음 단계 안내 배너·YAML 일괄등록 버튼(신규 생성 전용 흐름)만 토글한다. */
function grpSetEditMode(isEdit, spcNm) {
  g_grp_isEdit = isEdit;
  if (isEdit) {
    $('#grpHeadTitle').text('그룹 정보 수정');
    $('#grpHeadSub').text((spcNm || '') + ' 그룹의 정보를 수정합니다.');
    $('#grpHeadBadge').text('그룹 정보 수정');
    $('#grpSave').text('저장하기');
    $('#grpBannerSection').addClass('qr_hide');
    $('#grpBtnOpenYamlModal').addClass('qr_hide');
    $('#qrConfirmTitle').text('그룹 정보를 수정할까요?');
    $('#qrConfirmModal .qr_confirm_msg').text('수정한 내용이 저장됩니다.');
  } else {
    $('#grpHeadTitle').text('API 그룹(스펙) 등록');
    $('#grpHeadSub').text('API가 속할 그룹을 먼저 만듭니다 — 호스트·기본경로는 그룹 단위로 한 번만 정합니다.');
    $('#grpHeadBadge').text('새 그룹 만들기');
    $('#grpSave').text('저장하고 API 추가하러 가기');
    $('#grpBannerSection').removeClass('qr_hide');
    $('#grpBtnOpenYamlModal').removeClass('qr_hide');
    $('#qrConfirmTitle').text('그룹을 저장할까요?');
    $('#qrConfirmModal .qr_confirm_msg').text('저장하면 이 그룹이 만들어지고, 이어서 API 등록 화면으로 이동합니다. 그룹 정보는 나중에 수정할 수 있습니다.');
  }
}

function grpOnSysIdChange() {
  var sysId = $('#sysId').val();
  var autId = $('#sysId').find('option:selected').attr('data-aut-id') || '';
  $('#autId').val(autId);

  if (!sysId) {
    $('#grpLeftTree').empty();
    $('#grpLeftHint').text('서비스를 선택하면 그 서비스의 기존 API 그룹이 표시됩니다. 같은 호스트·기본경로가 이미 있는지 확인하세요.');
    return;
  }

  grpLoadSysSpcTree(sysId);
}

/* ---------------- 좌측 그룹 트리 (선택한 서비스의 기존 등록 현황, 읽기 전용 참고용) ---------------- */

function grpLoadSysSpcTree(sysId) {
  var $tree = $('#grpLeftTree');
  $('#grpLeftHint').text('불러오는 중...');
  $.ajax({
    url: c_url + 'api/spcreg/selSysSpcTreeAjax.do',
    type: 'GET',
    data: { sysId: sysId },
    dataType: 'json',
    success: function (res) { grpRenderSysSpcTree(res.list || []); },
    error: function () {
      $tree.empty();
      $('#grpLeftHint').text('목록을 불러오지 못했습니다.');
    }
  });
}

function grpRenderSysSpcTree(list) {
  var $tree = $('#grpLeftTree');
  $tree.empty();

  if (!list || list.length === 0) {
    $('#grpLeftHint').text('이 서비스에 등록된 그룹이 아직 없습니다. 새로 만드는 첫 그룹입니다.');
    return;
  }
  $('#grpLeftHint').text('이 서비스에 이미 등록된 그룹입니다 (참고용, 읽기 전용) — 그룹명을 클릭하면 그 그룹의 API만 펼쳐집니다.');

  // apiSpcNo -> [api...] 로 그룹핑, host는 그룹 헤더에만, API 줄은 Method+full path(basPath+apiPath)만.
  // mvMainList.do와 동일하게 "그룹 단위 목록 → 클릭해서 그 그룹의 API만 확인"하는 구조로,
  // 기본적으로는 그룹명 + 건수만 보여주고 API 목록은 접어둔다(전부 펼쳐서 한꺼번에 보여주지 않음).
  var bySpc = {};
  var spcOrder = [];
  for (var i = 0; i < list.length; i++) {
    var row = list[i];
    if (!bySpc[row.apiSpcNo]) {
      bySpc[row.apiSpcNo] = { spcNm: row.spcNm, ver: row.ver, host: row.host, basPath: row.basPath || '', apis: [] };
      spcOrder.push(row.apiSpcNo);
    }
    if (row.apiPath) {
      bySpc[row.apiSpcNo].apis.push(row);
    }
  }

  for (var s = 0; s < spcOrder.length; s++) {
    var spcData = bySpc[spcOrder[s]];
    var html = '<div class="qr_lt_spc">';
    html += '<button type="button" class="qr_lt_spc_nm" aria-expanded="false">'
      + '<span class="qr_lt_spc_nm_txt">' + grpEsc(spcData.spcNm) + ' <span style="font-weight:400;color:#999;">(' + grpEsc(spcData.ver) + ')</span> <span class="qr_lt_host">' + grpEsc(spcData.host) + '</span></span>'
      + '<span class="qr_lt_spc_cnt">' + spcData.apis.length + '건</span>'
      + '</button>';
    html += '<div class="qr_lt_ctgry qr_hide">';
    if (spcData.apis.length === 0) {
      html += '<p class="qr_lt_empty">등록된 API가 없습니다.</p>';
    }
    for (var a = 0; a < spcData.apis.length; a++) {
      var fullPath = (spcData.basPath || '') + (spcData.apis[a].apiPath || '');
      html += '<div class="qr_lt_api"><span class="qr_lt_method">' + grpEsc(grpMethodNm(spcData.apis[a].methodCd)) + '</span><span class="qr_lt_path">' + grpEsc(fullPath) + '</span></div>';
    }
    html += '</div></div>';
    $tree.append(html);
  }
}

/* 그룹명 클릭 시 그 그룹의 API 목록만 펼침/접힘 (위임 바인딩 - 트리는 매번 새로 그려지므로) */
$(document).on('click', '#grpLeftTree .qr_lt_spc_nm', function () {
  var $body = $(this).next('.qr_lt_ctgry');
  var open = $body.hasClass('qr_hide');
  $body.toggleClass('qr_hide', !open);
  $(this).attr('aria-expanded', open ? 'true' : 'false').toggleClass('qr_on', open);
});

function grpMethodNm(methodCd) {
  for (var i = 0; i < g_grp_mthTypeList.length; i++) {
    if (g_grp_mthTypeList[i].comnCd === methodCd) { return g_grp_mthTypeList[i].cdNm; }
  }
  return methodCd;
}

function grpEsc(s) {
  return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}
