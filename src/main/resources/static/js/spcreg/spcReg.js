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

  /* 새로고침/뒤로가기로 브라우저가 sysId 선택값을 복원해도 change 이벤트는 안 뜨는 경우가 있어,
     로드 시 한 번 강제로 동기화한다(quickApiReg.js의 qrOnApiSpcNoChange 초기 호출과 동일한 이유). */
  grpOnSysIdChange();

  $('#grpSave').on('click', function () {
    if (!validate()) { return; }

    // validate()를 통과했다는 건 모든 필드가 유효하다는 뜻이므로, 이전 시도에서 남아있을 수 있는
    // 에러 표시(빨간 테두리/문구)를 여기서 한 번 더 확실히 지운다 - 개별 필드의 input/change
    // 리스너가 어떤 이유로든 못 지운 잔여 에러가 저장 성공 후에도 화면에 남는 것을 방지.
    $('#grpRegForm .qr_input_err').removeClass('qr_input_err');
    $('#grpRegForm .qr_err').removeClass('qr_show');

    var formData = {
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
          location.href = c_url + 'api/spcreg/def/mvApiDefReg.do?apiSpcNo=' + res.apiSpcNo;
        } else {
          alert_message(res.message || '등록 중 오류가 발생했습니다.');
        }
      },
      error: function () {
        alert_message('등록 중 오류가 발생했습니다.');
      }
    });
  });
});

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
