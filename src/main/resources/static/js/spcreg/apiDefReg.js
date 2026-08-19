/* apiDefReg.js - "API 등록"(기존 SPC에 API 추가) 화면 전용 스크립트.
   SPC는 앞 화면(spcReg)에서 이미 확정되어 apiSpcNo/spcBasPath가 hidden 값으로 넘어오므로,
   이 화면엔 그룹 선택 UI/로직이 없다(퍼블 v14.0 확인 사항). 권한그룹(autId)도 서버가 그 그룹의
   sysId로 이미 필터링해 내려준다. 공용 유틸(alert_message 등)은 apiGlobalScript.js, 파라미터
   트리+YAML 편집은 paramTreeEditor.js(공용, 팝업 id는 qr* 고정)를 쓴다. */

var g_def_paramScope = 'in'; // 파라미터 미리보기에서 현재 활성 탭(설계 버튼이 어느 스코프를 열지 결정)
var g_def_origHeadSub = ''; // "OOO 그룹에 API를 추가합니다" 원래 문구 - 수정 모드에서 나갈 때 복원용
var g_def_loadedApiVer = ''; // defFillFormFromDetail로 불러온 API의 현재 버전(예: v1.0, 없으면 '')
var g_def_loadedApiVerNo = ''; // 그 API의 버전 패밀리 키 - 버전업 시 새 API가 이 값을 그대로 물려받는다
var g_def_bstTarget = 'tb'; // BEAST 시스템 선택 팝업이 지금 채우려는 필드('tb' 또는 'prd')

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

  /* "추가 설정" 인라인 토글(퍼블_v19.0) - qr_hide(display:none)로만 감춘다. 입력 요소는 DOM에
     남아 있어야 저장 시 값을 읽을 수 있다. */
  $('#defExtraToggle').on('click', function () {
    defToggleExtra($('#defExtraBox').hasClass('qr_hide'));
  });

  /* 접혀 있을 때 보이는 요약 문구는 값이 바뀔 때마다 다시 만든다. */
  $('#autId, #useYn, #guideGubun, #sandboxYn').on('change', defRefreshExtraSum);
  $('#apiNm, #apiPath').on('input', defRefreshBasicSum);
  $('#methodCd').on('change', defRefreshBasicSum);
  defRefreshExtraSum();
  defRefreshBasicSum();

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

  /* ---------- API그룹 추가 ----------
     구 등록 마법사의 "API그룹 추가"(cateInfoRegForm)에 해당한다. 별도 화면 대신 등록 흐름
     안에서 만들고 바로 선택되게 한다 - 그러려고 화면을 빠져나가면 입력하던 값이 날아간다. */
  $('#defBtnAddCtgry').on('click', function () {
    $('#defCtgryNm').val('').removeClass('qr_input_err');
    $('#defCtgryDesc').val('');
    $('#defCtgryNmErr').removeClass('qr_show');
    $('#defCtgryModal').removeClass('qr_hide');
    $('#defCtgryNm').focus();
  });
  $('#defCtgryModalClose, #defCtgryCancel').on('click', function () {
    $('#defCtgryModal').addClass('qr_hide');
  });
  $('#defCtgrySave').on('click', defSaveCtgry);
  $('#defCtgryNm').on('keydown', function (e) {
    if (e.keyCode === 13) { e.preventDefault(); defSaveCtgry(); }
  });

  /* 템플릿 선택(퍼블_v16.0) - API(DEF) 단위 기능이라 이 화면 헤더에 둔다. */
  $('#qrBtnOpenTmpltModal').on('click', function () { $('#qrTmpltModal').removeClass('qr_hide'); });
  $('#qrTmpltModalClose').on('click', function () { $('#qrTmpltModal').addClass('qr_hide'); });

  /* Provider(단위서비스코드) 선택 - Private API 전용, 기존 등록 마법사의 "Provider 선택" 팝업을
     이 화면 방식(클릭 한 번으로 즉시 반영)으로 재구현. */
  $('#defBtnOpenProviderModal').on('click', defOpenProviderModal);
  $('#qrProviderModalClose').on('click', function () { $('#qrProviderModal').addClass('qr_hide'); });

  /* API ID - 중복검사/신규 발급. 입력값이 바뀌면 이전에 보여준 중복 안내 문구는 지운다(다시
     확인해야 하므로). 최종 저장 시점에도 한 번 더 검사한다(defOnSaveClick, 버튼을 안 눌렀을 수 있어서). */
  $('#apiId').on('input', function () {
    $(this).removeClass('qr_input_err');
    $('#apiIdErr').removeClass('qr_show');
    $('#defApiIdHint').css('color', '').text('예시: OIF_00001 (한글/공백 불가, 시스템 전체에서 유일해야 함)');
  });
  $('#defBtnApiIdChk').on('click', function () { defCheckApiId(true); });
  $('#defBtnNextApiId').on('click', defFillNextApiId);

  /* API 버전업 - Path에 v1.0 같은 세그먼트가 있는 기존 API를 불러왔을 때만 버튼이 보인다
     (defFillFormFromDetail). 팝업에서 새 버전 문자열만 받아 defApplyVerUp()이 처리한다. */
  $('#defBtnVerUp').on('click', defOpenVerUpModal);
  $('#defVerUpCancel').on('click', function () { $('#defVerUpModal').addClass('qr_hide'); });
  $('#defVerUpOk').on('click', defApplyVerUp);

  /* BEAST G/W 시스템 선택 (그룹이 BEAST를 쓸 때만 이 필드/버튼 자체가 렌더링됨 - th:if) */
  $('#defBstSysModalClose').on('click', function () { $('#defBstSysModal').addClass('qr_hide'); });
  $('#defBstSearchBtn').on('click', defSearchBstSys);
  $('#defBstSearchSysId, #defBstSearchSysNm').on('keypress', function (e) {
    if (e.which === 13) { defSearchBstSys(); }
  });

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
  $('#defPendingApiVerNo').val(''); // 기존 API를 그냥 불러온 것뿐 - 버전업 진행 중 상태는 아니다

  g_def_loadedApiVer = def.apiVer || '';
  g_def_loadedApiVerNo = def.apiVerNo || '';
  $('#defBtnVerUp').toggleClass('qr_hide', !defIsVersionInPath(def.apiPath));

  $('#apiId').val(def.apiId || '');
  $('#apiId, #apiIdErr').removeClass('qr_input_err qr_show');
  $('#apiNm').val(def.apiNm || '');
  $('#apiDesc').val(def.apiDesc || '');
  $('#apiPath').val(def.apiPath || '');
  $('#methodCd').val(def.methodCd || '');
  $('#apiCtgryNo').val(def.apiCtgryNo || '');
  $('input[name="apiClass"][value="' + def.apiClass + '"]').prop('checked', true);
  $('#apiHandlerCd').val(def.apiHandlerCd || '');
  $('#providerSeq').val(def.providerSeq || '');
  $('#useYn').val(def.useYn || 'Y');
  $('#guideGubun').val(def.guideGubun || '');
  $('#sandboxYn').val(def.sandboxYn || 'N');
  $('#endpntMethodCd').val(def.endpntMethodCd || '');
  $('#providerNmDisp').val(def.providerSeq ? defProviderNmBySeq(def.providerSeq) : '');
  // Handler 파라미터는 입력칸이 동적으로 만들어지므로, defOnApiClassChange()가 폼을 그리기 전에
  // 값부터 DEF_HP_VALUES에 실어둔다(그려질 때 여기서 값을 되찾아 간다).
  defLoadHandlerParamValues(def);
  defOnApiClassChange();
  defSyncFullPath();

  $('#endpntTbUrl').val(def.endpntTbUrl || '');
  $('#endpntPrdUrl').val(def.endpntPrdUrl || '');
  $('#endpntTimeout').val(def.endpntTimeout || '');
  // 응답매핑/HDP/클라이언트 IP는 Handler 설정 영역에서 동적으로 그려진다(위 defLoadHandlerParamValues 참고).

  // BEAST 필드는 그룹이 BEAST를 안 쓰면 DOM에 아예 없다(th:if) - 있을 때만 채운다. 시스템명은
  // DB에 저장 안 하므로(ID만 저장) 힌트는 비워둔다(다시 선택해야 이름이 다시 보임).
  $('#bstgwTbSysId').val(def.bstgwTbSysId || '');
  $('#bstgwPrdSysId').val(def.bstgwPrdSysId || '');
  $('#bstgwTbSysNmHint, #bstgwPrdSysNmHint').text('');

  // 파라미터: 스코프별로 필터링해서 PT_STORE를 다시 구성(ptBuildTree는 flat row -> 중첩 트리)
  var rows = def.paramList || [];
  PT_STORE['in'] = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1010' && r.paramLoc !== 'query'; }));
  PT_STORE.query = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1010' && r.paramLoc === 'query'; }));
  PT_STORE.out = ptBuildTree(rows.filter(function (r) { return r.paramTypeCd === 'PRMTYP1020'; }));
  defInitDefaultParamTree(); // 아직 파라미터가 하나도 없던 API라면 request/response 기본 골격을 깔아준다
  ptRenderPreview('def', defRefreshSummary);

  defRefreshExtraSum();
  defRefreshBasicSum();
  defSetEditMode(true, def.apiNm);
}

function defResetToCreate() {
  $('#editApiNo').val('');
  $('#defPendingApiVerNo').val('');
  $('#defBtnVerUp').addClass('qr_hide');
  g_def_loadedApiVer = ''; g_def_loadedApiVerNo = '';
  $('#defRegForm')[0].reset();
  $('#apiHandlerCd, #providerSeq, #providerNmDisp').val('');
  $('#apiId, #apiIdErr').removeClass('qr_input_err qr_show');
  $('#bstgwTbSysNmHint, #bstgwPrdSysNmHint').text('');
  defClearHandlerParamValues();
  // form.reset()이 selected 속성 기준으로 되돌리므로 권한그룹/노출여부/가이드구분/sandbox는
  // 마크업의 기본값(개발자그룹 · 비노출 · REST · 미적용)으로 자동 복원된다.
  defToggleExtra(false);
  defRefreshBasicSum();
  defOnMethodChange();
  defOnApiClassChange();
  defSyncFullPath();

  PT_STORE['in'] = []; PT_STORE.query = []; PT_STORE.out = [];
  defInitDefaultParamTree();
  ptRenderPreview('def', defRefreshSummary);

  $('#defLeftTree .qr_lt_api').removeClass('qr_on');
  defSetEditMode(false);
}

/* ---------------- API 버전업 ---------------- */

/* 기존 등록 마법사 KsmUtil.fmt_data(path, "fmt_version_in_path")/fn_is_version_in_path와 동일한
   패턴 - Path의 두 번째 세그먼트가 v1.0 같은 형태일 때만 버전업 대상으로 본다. */
function defIsVersionInPath(path) {
  return /^(\/[\w\-.]+)\/(v\d+\.\d+)(\/[\w\-./]+)$/.test($.trim(path || ''));
}
function defReplaceVersionInPath(path, newVer) {
  return $.trim(path).replace(/^(\/[\w\-.]+)\/(v\d+\.\d+)(\/[\w\-./]+)$/, '$1/' + newVer + '$3');
}

function defOpenVerUpModal() {
  $('#defVerUpCur').val(g_def_loadedApiVer || '(알 수 없음)');
  $('#defVerUpNew').val('');
  $('#defVerUpNew, #defVerUpErr').removeClass('qr_input_err qr_show');
  $('#defVerUpModal').removeClass('qr_hide');
}

/* [새 버전 만들기] 클릭 시: 새 버전 문자열을 검증하고, Path의 버전 세그먼트만 바꾼 뒤,
   폼을 "새 API 추가" 상태로 전환한다(editApiNo 비움) - 나머지 필드는 지금 값을 그대로 이어받는다.
   defPendingApiVerNo에 원본의 버전 패밀리 키를 담아두면, 저장 시 새 API가 그 패밀리에 합류한다. */
function defApplyVerUp() {
  var newVer = $.trim($('#defVerUpNew').val());
  var isValidFormat = /^v\d+\.\d+$/.test(newVer);
  $('#defVerUpNew, #defVerUpErr').removeClass('qr_input_err qr_show');

  if (!newVer || !isValidFormat || newVer === g_def_loadedApiVer) {
    $('#defVerUpNew').addClass('qr_input_err');
    $('#defVerUpErr').addClass('qr_show');
    return;
  }

  var origPath = $('#apiPath').val();
  var newPath = defReplaceVersionInPath(origPath, newVer);

  // 버전 패밀리 키: 원본이 이미 어딘가에 속해 있으면(g_def_loadedApiVerNo) 그걸 물려받고,
  // 없으면(0 또는 빈 값) 원본의 apiNo 자체가 그 패밀리의 시작점이므로 그걸 물려받는다.
  var inheritVerNo = g_def_loadedApiVerNo && g_def_loadedApiVerNo !== '0' ? g_def_loadedApiVerNo : $('#editApiNo').val();

  $('#apiPath').val(newPath).trigger('input');
  $('#editApiNo').val('');
  $('#defPendingApiVerNo').val(inheritVerNo);
  $('#defBtnVerUp').addClass('qr_hide');
  $('#defLeftTree .qr_lt_api').removeClass('qr_on');

  defSetEditMode(false);
  $('#defHeadTitle').text('API 버전업');
  $('#defHeadSub').html('<b>' + defEsc(g_def_loadedApiVer || '') + ' → ' + defEsc(newVer) + '</b>로 새 API를 만듭니다. 내용을 확인하고 저장하세요.');

  $('#defVerUpModal').addClass('qr_hide');
}

/* ---------------- BEAST G/W 시스템 선택 ---------------- */

function defOpenBstSysModal(target) {
  g_def_bstTarget = target; // 'tb' 또는 'prd'
  $('#defBstSysModalTitle').text(target === 'prd' ? '상용 G/W 시스템 선택' : 'TB G/W 시스템 선택');
  $('#defBstSearchSysId, #defBstSearchSysNm').val('');
  $('input[name="defBstPlatform"][value="KTC"]').prop('checked', true);
  $('#defBstSysGrid').html('<p id="defBstSysEmptyMsg" class="qr_no_params">검색 조건을 입력하고 [검색]을 눌러주세요.</p>');
  $('#defBstSysModal').removeClass('qr_hide');
}

function defSearchBstSys() {
  var platform = $('input[name="defBstPlatform"]:checked').val() || 'KTC';
  var target = (g_def_bstTarget === 'prd' ? 'PRD' : 'TB') + '_' + platform;
  var sysId = $.trim($('#defBstSearchSysId').val());
  var sysNm = $.trim($('#defBstSearchSysNm').val());

  $('#defBstSysGrid').html('<p class="qr_no_params">검색 중...</p>');
  $.ajax({
    url: c_url + 'api/spcreg/def/selBstSysListAjax.do',
    type: 'GET',
    data: { target: target, sysId: sysId, sysNm: sysNm },
    dataType: 'json',
    success: function (res) { defRenderBstSysList(res.list || []); },
    error: function () { $('#defBstSysGrid').html('<p class="qr_no_params">검색 중 오류가 발생했습니다.</p>'); }
  });
}

function defRenderBstSysList(list) {
  var $grid = $('#defBstSysGrid');
  if (!list || list.length === 0) {
    $grid.html('<p class="qr_no_params">검색 결과가 없습니다.</p>');
    return;
  }
  var html = '';
  for (var i = 0; i < list.length; i++) {
    var row = list[i];
    html += '<button type="button" class="qr_tmplt_row" data-sys-id="' + defEsc(row.sysId) + '" data-sys-nm="' + defEsc(row.sysNm) + '" onclick="defSelectBstSys(this);">'
      + '<span class="qr_tmplt_icon"></span>'
      + '<span class="qr_tmplt_txt"><span class="qr_tmplt_nm">' + defEsc(row.sysId) + '</span><span class="qr_tmplt_desc">' + defEsc(row.sysNm) + '</span></span>'
      + '<span class="qr_tmplt_meta"><span class="qr_tmplt_method">' + defEsc(row.edptAtribUrl || '') + '</span></span>'
      + '</button>';
  }
  $grid.html(html);
}

function defSelectBstSys(el) {
  var $el = $(el);
  var sysId = $el.attr('data-sys-id');
  var sysNm = $el.attr('data-sys-nm');
  if (g_def_bstTarget === 'prd') {
    $('#bstgwPrdSysId').val(sysId);
    $('#bstgwPrdSysNmHint').text(sysNm ? ('선택됨: ' + sysNm) : '');
  } else {
    $('#bstgwTbSysId').val(sysId);
    $('#bstgwTbSysNmHint').text(sysNm ? ('선택됨: ' + sysNm) : '');
  }
  $('#defBstSysModal').addClass('qr_hide');
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
  var isPrivate = (apiClass === 'APIGUB1020');
  $('#defPrivateRow').toggleClass('qr_hide', !isPrivate);

  // Handler 설정은 Private일 때만 의미가 있다. 열릴 때마다 현재 Handler 기준으로 입력칸을 다시 그린다.
  $('#defHandlerParamBox').toggleClass('qr_hide', !isPrivate);
  if (isPrivate) { defBuildHandlerParamForm($('#apiHandlerCd').val()); }
  else { $('#defHandlerParamGrid').empty(); }
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

/* (폐지된) 빠른 API 등록 화면의 qrSelectTemplate과 동일한 카드 클릭 처리다. 다만 이 화면은 입력 파라미터를
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
  var fieldsJson = $el.attr('data-fields');

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

  defApplyTmpltFieldDefaults(fieldsJson);

  $('#qrTmpltModal').addClass('qr_hide');
}

/* 템플릿의 dfltFieldJson(YAML의 x-def-fields 확장에서 파생)을 API 등록 폼에 반영한다.
   API 이름/권한그룹/엔드포인트/고급설정 등 "기본 정보"+"고급 설정" 영역의 임의 필드를 대상으로 하며,
   템플릿이 정의하지 않은(키가 없거나 빈 값인) 필드는 건드리지 않는다 - 특정 필드를 코드에 하드코딩하지
   않고 폼의 id와 x-def-fields의 key를 그대로 매칭하는 범용 방식이라, 폼에 필드가 추가돼도 템플릿의
   YAML만 고치면 바로 반영된다. */
function defApplyTmpltFieldDefaults(fieldsJson) {
  if (!fieldsJson) { return; }
  var fieldDefaults = {};
  try { fieldDefaults = JSON.parse(fieldsJson) || {}; } catch (e) { return; }

  // Handler 파라미터는 지금 화면에 입력칸이 없을 수도 있다(다른 Handler를 고른 상태). DOM 대신
  // DEF_HP_VALUES에 넣고 마지막에 한 번만 다시 그린다.
  var hpField = {};
  DEF_HANDLER_PARAM_FIELDS.forEach(function (fd) { hpField[fd] = true; });
  defSyncHandlerParamsFromDom();

  // Handler 자체가 바뀌면 입력칸 구성이 달라지므로 다른 키보다 먼저 반영한다. 여기서는 change를
  // 쏘지 않는다 - 지금 쏘면 아직 옛 Handler의 입력칸이 남아 있어 아래에서 넣을 값을 덮어쓴다.
  if (fieldDefaults.apiHandlerCd) {
    $('#apiHandlerCd').val(fieldDefaults.apiHandlerCd);
  }

  Object.keys(fieldDefaults).forEach(function (key) {
    var val = fieldDefaults[key];
    if (val === undefined || val === null || val === '' || key === 'apiHandlerCd') { return; }
    if (hpField[key]) { DEF_HP_VALUES[key] = val; return; }
    var $field = $('#' + key);
    if ($field.length === 0) { return; }
    $field.val(val).trigger('change');
  });

  if (!$('#defHandlerParamBox').hasClass('qr_hide')) {
    defBuildHandlerParamForm($('#apiHandlerCd').val());
  }
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

/* ---------------- API ID 중복검사 / 신규 발급 ---------------- */

/* 한글/공백은 기존 등록 마법사와 동일한 규칙으로 막는다. */
function defApiIdFormatError(apiId) {
  if (/[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(apiId)) { return 'API 아이디에는 한글이 들어갈 수 없습니다.'; }
  if (/\s/.test(apiId)) { return 'API 아이디에는 공백이 들어갈 수 없습니다.'; }
  return '';
}

/* interactive=true면 버튼을 직접 눌러서 확인하는 경우(알림창으로도 결과를 보여줌).
   false면 저장 직전에 조용히 한 번 더 확인하는 경우(defOnSaveClick) - callback(dup)로만 결과 전달. */
function defCheckApiId(interactive, callback) {
  var apiId = $.trim($('#apiId').val());
  if (!apiId) {
    if (interactive) { alert_message('API ID를 먼저 입력해 주세요.'); }
    if (callback) { callback(false); }
    return;
  }
  var fmtErr = defApiIdFormatError(apiId);
  if (fmtErr) {
    $('#apiIdErr').text(fmtErr).addClass('qr_show');
    $('#apiId').addClass('qr_input_err');
    if (interactive) { alert_message(fmtErr); }
    if (callback) { callback(true); }
    return;
  }
  $.ajax({
    url: c_url + 'api/spcreg/def/selApiIdChkAjax.do',
    type: 'GET',
    data: { apiId: apiId, apiNo: $('#editApiNo').val(), apiVerNo: $('#defPendingApiVerNo').val() },
    dataType: 'json',
    success: function (res) {
      var dup = !!res.dup;
      $('#apiId').toggleClass('qr_input_err', dup);
      $('#apiIdErr').text('이미 사용 중인 API ID입니다.').toggleClass('qr_show', dup);
      $('#defApiIdHint').css('color', dup ? 'var(--qr-danger)' : 'var(--qr-teal-dark)')
        .text(dup ? '이미 사용 중인 API ID입니다.' : '사용 가능한 API ID입니다.');
      if (interactive) { alert_message(dup ? '이미 사용 중인 API ID입니다.' : '사용 가능한 API ID입니다.'); }
      if (callback) { callback(dup); }
    },
    error: function () {
      if (interactive) { alert_message('중복 확인 중 오류가 발생했습니다.'); }
      if (callback) { callback(false); }
    }
  });
}

function defFillNextApiId() {
  $.ajax({
    url: c_url + 'api/spcreg/def/selNextApiIdAjax.do',
    type: 'GET',
    dataType: 'json',
    success: function (res) {
      if (res.returnCode === '1' && res.nextApiId) {
        $('#apiId').val(res.nextApiId).trigger('input');
      } else {
        alert_message('다음 API ID를 가져오지 못했습니다.');
      }
    },
    error: function () { alert_message('다음 API ID를 가져오는 중 오류가 발생했습니다.'); }
  });
}

/* ---------------- 저장 ---------------- */

/* "등록하기" 클릭 시: 검증만 하고 통과하면 저장 확인 팝업을 띄운다. API ID는 저장 직전에 한 번 더
   조용히 중복 확인한다("중복검사" 버튼을 안 눌렀을 수도 있어서). 실제 저장은 defDoSave()에서. */
function defOnSaveClick() {
  var autId = $('#autId').val();
  var apiId = $.trim($('#apiId').val());
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();
  var endpntTbUrl = $('#endpntTbUrl').val();
  var endpntPrdUrl = $('#endpntPrdUrl').val();

  var hasErr = false;
  $('#apiNmErr, #apiPathErr, #endpntTbUrlErr, #endpntPrdUrlErr, #apiIdErr').removeClass('qr_show');
  $('#apiNm, #apiPath, #endpntTbUrl, #endpntPrdUrl, #apiId').removeClass('qr_input_err');

  var apiIdFmtErr = apiId ? defApiIdFormatError(apiId) : '';
  if (!apiId) {
    $('#apiIdErr').text('API ID를 입력하세요.').addClass('qr_show'); $('#apiId').addClass('qr_input_err'); hasErr = true;
  } else if (apiIdFmtErr) {
    $('#apiIdErr').text(apiIdFmtErr).addClass('qr_show'); $('#apiId').addClass('qr_input_err'); hasErr = true;
  }
  if (!apiNm) { $('#apiNmErr').addClass('qr_show'); $('#apiNm').addClass('qr_input_err'); hasErr = true; }
  if (!apiPath || apiPath.charAt(0) !== '/') {
    $('#apiPathErr').text('Path를 입력하세요. (/로 시작)').addClass('qr_show');
    $('#apiPath').addClass('qr_input_err'); hasErr = true;
  }
  /* 권한그룹은 "추가 설정" 안에 접혀 있을 수 있다 - 비어 있으면 펼쳐서 어디를 고쳐야 하는지 보여준다. */
  $('#autIdErr').removeClass('qr_show');
  $('#autId').removeClass('qr_input_err');
  if (!autId) {
    defToggleExtra(true);
    $('#autId').addClass('qr_input_err');
    $('#autIdErr').addClass('qr_show');
    hasErr = true;
  }
  /* 게이트웨이가 뒤에서 실제로 호출하는 주소라 필수값이다("제공 시스템 주소" 섹션).
     접힌 상태면 오류 표시가 안 보이므로 섹션을 펼쳐준다. */
  var endpntErr = false;
  if (!$.trim(endpntTbUrl)) { $('#endpntTbUrlErr').addClass('qr_show'); $('#endpntTbUrl').addClass('qr_input_err'); endpntErr = true; }
  if (!$.trim(endpntPrdUrl)) { $('#endpntPrdUrlErr').addClass('qr_show'); $('#endpntPrdUrl').addClass('qr_input_err'); endpntErr = true; }
  if (endpntErr) {
    $('#endpntTbUrl').closest('.sv_acc').addClass('sv_open');
    hasErr = true;
  }
  /* Handler별 필수 항목(예: COMMON/ANYCOMMON의 Request Client IP 매핑키) - 선택한 Handler에
     따라 검사 대상 자체가 달라진다. */
  if (!defValidateHandlerParams()) { hasErr = true; }

  if (hasErr) { return; }

  defCheckApiId(false, function (apiIdDup) {
    if (apiIdDup) { $('#apiId').focus(); return; }
    defCheckPathMethodDup(function (pathDup) {
      if (pathDup) { $('#apiPath').focus(); return; }
      $('#qrConfirmModal').removeClass('qr_hide');
    });
  });
}

/* Method+Path 중복 체크 - 기존 등록 마법사가 쓰던 엔드포인트(/api/reg/salApijDupPathCheckAjax.do,
   ApiRegController.salApijDupPathCheckAjax -> salApijDupPathCheck)를 그대로 재사용한다(그룹 이름
   중복검사와 동일한 이유 - 새 규칙이 아니라 기존과 완전히 동일한 검사라 재구현하지 않기로 확정됨).
   이 검사는 그룹 이름 중복검사와 달리 "경고만"이 아니라 저장을 막는다 - 같은 경로/메서드가
   중복되면 실제 게이트웨이 라우팅이 깨지기 때문. */
function defCheckPathMethodDup(callback) {
  $.ajax({
    url: c_url + 'api/reg/salApijDupPathCheckAjax.do',
    type: 'POST',
    data: {
      apiSpcNo: $('#apiSpcNo').val(),
      apiPath: $.trim($('#apiPath').val()),
      methodCd: $('#methodCd').val(),
      apiNo: $('#editApiNo').val()
    },
    dataType: 'json',
    success: function (res) {
      var dup = res.duplYn === 'Y';
      $('#apiPath').toggleClass('qr_input_err', dup);
      $('#apiPathErr').text(dup ? '이미 등록된 Method/Path입니다.' : 'Path를 입력하세요. (/로 시작)').toggleClass('qr_show', dup);
      callback(dup);
    },
    error: function () { callback(false); } // 확인 실패는 저장까지 막지 않음(구 화면도 별도 오류 안내만)
  });
}

function defDoSave() {
  var apiSpcNo = $('#apiSpcNo').val();
  var autId = $('#autId').val();
  var apiNm = $('#apiNm').val();
  var apiPath = $('#apiPath').val();

  var formData = {
    apiSpcNo: apiSpcNo,
    apiCtgryNo: $('#apiCtgryNo').val(),   // 고른 API그룹. 비면 서버가 v1.0 을 만든다
    apiNo: $('#editApiNo').val(),
    apiId: $.trim($('#apiId').val()),
    apiVerNo: $('#defPendingApiVerNo').val(), // 버전업 진행 중일 때만 값이 있음(defApplyVerUp)
    autId: autId,
    apiNm: apiNm,
    apiDesc: $('#apiDesc').val(),
    apiClass: $('input[name="apiClass"]:checked').val(),
    apiPath: apiPath,
    methodCd: $('#methodCd').val(),
    apiHandlerCd: $('#apiHandlerCd').val(),
    providerSeq: $('#providerSeq').val(),
    useYn: $('#useYn').val(),
    guideGubun: $('#guideGubun').val(),
    sandboxYn: $('#sandboxYn').val(),
    endpntMethodCd: $('#endpntMethodCd').val(),
    endpntTbUrl: $('#endpntTbUrl').val(),
    endpntPrdUrl: $('#endpntPrdUrl').val(),
    endpntTimeout: $('#endpntTimeout').val(),
    // Handler 파라미터(응답매핑/HDP/클라이언트 IP 등)는 아래 defAppendHandlerParams()가 채운다.
    // 그룹이 BEAST를 안 쓰면 이 입력칸 자체가 DOM에 없다(th:if) - 없으면 빈 문자열로 보낸다.
    bstgwTbSysId: $('#bstgwTbSysId').val() || '',
    bstgwPrdSysId: $('#bstgwPrdSysId').val() || ''
  };

  defAppendHandlerParams(formData);

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
    formData['paramList[' + i + '].doNotSend'] = paramList[i].doNotSend;
    formData['paramList[' + i + '].fixedValue'] = paramList[i].fixedValue;
    formData['paramList[' + i + '].hidden'] = paramList[i].hidden;
    formData['paramList[' + i + '].mappingKey'] = paramList[i].mappingKey;
    formData['paramList[' + i + '].bigo'] = paramList[i].bigo;
    formData['paramList[' + i + '].paramSandboxYn'] = paramList[i].paramSandboxYn;
    formData['paramList[' + i + '].hdpUrlDecode'] = paramList[i].hdpUrlDecode;
    formData['paramList[' + i + '].hdpUrlEncode'] = paramList[i].hdpUrlEncode;
    formData['paramList[' + i + '].hdpUploadTarget'] = paramList[i].hdpUploadTarget;
    formData['paramList[' + i + '].resCd'] = paramList[i].resCd;
    formData['paramList[' + i + '].resDesc'] = paramList[i].resDesc;
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

/* ============================================================================
   Handler 설정 - 선택한 Handler(APIHDR10xx)에 따라 입력 항목이 달라진다.

   기존 등록 마법사(regFormPrivateHandlerParam_inc.html)의 g_handler_param_cfg/g_handler_param을
   이 화면 형태로 옮긴 것이다. 원래 이 화면은 응답매핑 4종과 HDP 6종을 Handler와 무관하게 "고급
   설정"에 항상 노출했는데, 실제로는 Handler마다 필요한 항목이 다르고 일부는 필수라서 잘못된
   구현이었다. 항목 구성/필수 여부는 마법사 쪽 정의를 그대로 따른다.

   입력값은 DOM이 아니라 DEF_HP_VALUES에 모아 둔다. Handler를 바꾸면 입력칸을 새로 그리는데,
   DOM만 믿으면 그때 값이 날아가기 때문이다(예: SCAP -> CAPRI로 바꿨다가 되돌린 경우).
   ========================================================================== */

// gub: stxt(한 줄) | mtxt(여러 줄) | chkyn(체크박스=Y/N)
// man: 'Y'면 필수. fd: KOA_TB_API_DEF 컬럼과 1:1인 VO 필드명(= input의 id/name)
var DEF_HANDLER_PARAM_CFG = {
  req_CLIENT_IP_RULE       : { title: 'Request Client IP 매핑키'      , fd: 'endpntClientIp'       , gub: 'stxt' , man: 'Y', max: 200  },
  res_RESMAP_RES_CD_FIELD  : { title: 'Response 결과매핑-결과필드'      , fd: 'resmapResCdField'     , gub: 'stxt' , man: 'N', max: 200  },
  res_RESMAP_SUCC_VAL      : { title: 'Response 결과매핑-성공기준'      , fd: 'resmapSuccVal'        , gub: 'stxt' , man: 'N', max: 100  },
  res_RESMAP_ERR_CD_FIELD  : { title: 'Response 결과매핑-에러코드필드'   , fd: 'resmapErrCdField'     , gub: 'stxt' , man: 'N', max: 200  },
  res_RESMAP_ERR_MSG_FIELD : { title: 'Response 결과매핑-에러메시지필드' , fd: 'resmapErrMsgField'    , gub: 'stxt' , man: 'N', max: 200  },
  api_OUT_FORMAT           : { title: 'out-format'                   , fd: 'hdpApiOutFormat'      , gub: 'stxt' , man: 'Y', max: 50   },
  api_OUT_COMMON_PARAM     : { title: 'out-common-param'             , fd: 'hdpApiOutCommonParam' , gub: 'stxt' , man: 'Y', max: 50   },
  api_ENDPOINT_ID          : { title: 'endpoint-id'                  , fd: 'hdpApiEndpointId'     , gub: 'stxt' , man: 'Y', max: 100  },
  req_API_NAME             : { title: 'Request API_NAME'             , fd: 'hdpReqApiName'        , gub: 'stxt' , man: 'Y', max: 200  },
  req_CONFIG_TO_BODY       : { title: 'Request CONFIG_TO_BODY'       , fd: 'hdpReqConfigToBody'   , gub: 'mtxt' , man: 'N', max: 8000 },
  req_HEADER_TO_BODY       : { title: 'Request HEADER_TO_BODY'       , fd: 'hdpReqHeaderToBody'   , gub: 'mtxt' , man: 'N', max: 8000 },
  req_MAPPING_TO_BODY      : { title: 'Request MAPPING_TO_BODY'      , fd: 'hdpReqMappingToBody'  , gub: 'mtxt' , man: 'N', max: 8000 },
  req_URL_DECODE           : { title: 'Request URL_DECODE'           , fd: 'hdpReqUrlDecode'      , gub: 'stxt' , man: 'N', max: 50   },
  req_URL_ENCODE           : { title: 'Request URL_ENCODE'           , fd: 'hdpReqUrlEncode'      , gub: 'stxt' , man: 'N', max: 50   },
  res_MAPPING_TO_BODY      : { title: 'Response MAPPING_TO_BODY'     , fd: 'hdpResMappingToBody'  , gub: 'mtxt' , man: 'N', max: 8000 },
  res_PROVIDE_PARAM        : { title: 'Response PROVIDE_PARAM'       , fd: 'hdpResProvideParam'   , gub: 'mtxt' , man: 'N', max: 8000 },
  res_URL_ENCODE           : { title: 'Response URL_ENCODE'          , fd: 'hdpResUrlEncode'      , gub: 'stxt' , man: 'N', max: 50   },
  HNDLROPTN_CONFIG         : { title: 'handler option CONFIG'        , fd: 'hdpHndlroptnConfig'   , gub: 'mtxt' , man: 'N', max: 8000 },
  // 체크박스 하나가 컬럼 전체가 아니라 "key=value" 여러 줄 중 is_biznaru 한 줄만 담당한다.
  ext_prop_IS_BIZNARU      : { title: '비즈나루API 여부'               , fd: 'hdpExtProp'           , gub: 'chkyn', man: 'N', max: -1, propKey: 'is_biznaru' }
};

// Handler 코드별로 노출할 항목(순서 그대로 렌더링). 마법사 g_handler_param과 동일.
var DEF_HANDLER_PARAM = {
  APIHDR1010: ['req_CLIENT_IP_RULE', 'HNDLROPTN_CONFIG'],                                                     // COMMON
  APIHDR1020: ['req_CLIENT_IP_RULE', 'res_RESMAP_RES_CD_FIELD', 'res_RESMAP_SUCC_VAL',
               'res_RESMAP_ERR_CD_FIELD', 'res_RESMAP_ERR_MSG_FIELD', 'HNDLROPTN_CONFIG'],                    // ANYCOMMON
  APIHDR1030: ['HNDLROPTN_CONFIG'],                                                                           // KOS
  APIHDR1040: ['HNDLROPTN_CONFIG'],                                                                           // KOSMOS
  APIHDR1050: ['api_OUT_COMMON_PARAM', 'api_ENDPOINT_ID', 'req_API_NAME', 'req_URL_DECODE', 'res_URL_ENCODE',
               'req_CONFIG_TO_BODY', 'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY',
               'res_PROVIDE_PARAM', 'HNDLROPTN_CONFIG'],                                                      // SCAP
  APIHDR1060: ['req_URL_DECODE', 'req_URL_ENCODE', 'res_URL_ENCODE', 'req_CONFIG_TO_BODY',
               'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY', 'res_PROVIDE_PARAM',
               'HNDLROPTN_CONFIG'],                                                                           // CAPRI
  APIHDR1070: ['api_OUT_FORMAT', 'api_OUT_COMMON_PARAM', 'req_API_NAME', 'req_URL_DECODE', 'res_URL_ENCODE',
               'req_CONFIG_TO_BODY', 'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY',
               'res_PROVIDE_PARAM', 'HNDLROPTN_CONFIG']                                                       // SB
};

// Handler에 따라 켜지고 꺼지는 전체 필드 목록(저장 payload 구성/초기화에 사용)
var DEF_HANDLER_PARAM_FIELDS = (function () {
  var seen = {}, out = [];
  for (var key in DEF_HANDLER_PARAM_CFG) {
    var fd = DEF_HANDLER_PARAM_CFG[key].fd;
    if (!seen[fd]) { seen[fd] = true; out.push(fd); }
  }
  return out;
})();

var DEF_HP_VALUES = {}; // { VO필드명: 값 } - 화면에 지금 안 보이는 항목의 값도 여기 남아 있다

/* 비즈나루 서비스일 때만 COMMON에 "비즈나루API 여부"가 붙는다(마법사의 bIsSysIdBiznaru와 동일). */
function defHandlerParamKeys(handlerCd) {
  var keys = (DEF_HANDLER_PARAM[handlerCd] || []).slice();
  if (handlerCd === 'APIHDR1010' && $('#defIsSysIdBiznaru').val() === 'Y') {
    keys.push('ext_prop_IS_BIZNARU');
  }
  return keys;
}

/* ext_prop 컬럼은 "key=value"를 줄바꿈으로 이어붙인 형식이다(기존 ksmutil.js $fn_get/set_ext_prop). */
function defExtPropGet(propKey, raw) {
  var found = '';
  String(raw || '').split('\n').forEach(function (line) {
    var pos = line.indexOf('=');
    if (pos < 0) { return; }
    if ($.trim(line.substring(0, pos)).toLowerCase() === propKey) {
      found = $.trim(line.substring(pos + 1));
    }
  });
  return found;
}

function defExtPropSet(propKey, propVal, raw) {
  var lines = [], done = false;
  String(raw || '').split('\n').forEach(function (line) {
    var pos = line.indexOf('=');
    if (pos < 0) { return; }
    var key = $.trim(line.substring(0, pos)).toLowerCase();
    if (key === propKey) {
      if (propVal !== null) { lines.push(key + '=' + propVal); done = true; }
      return; // propVal이 null이면 그 줄을 지운다
    }
    lines.push(key + '=' + $.trim(line.substring(pos + 1)));
  });
  if (propVal !== null && !done) { lines.push(propKey + '=' + propVal); }
  return lines.join('\n');
}

/* 지금 그려져 있는 입력칸의 값을 DEF_HP_VALUES로 되받는다(입력칸을 다시 그리기 직전에 호출). */
function defSyncHandlerParamsFromDom() {
  $('#defHandlerParamGrid').find('[data-hp-key]').each(function () {
    var cfg = DEF_HANDLER_PARAM_CFG[$(this).attr('data-hp-key')];
    if (!cfg) { return; }
    if (cfg.gub === 'chkyn') {
      DEF_HP_VALUES[cfg.fd] = defExtPropSet(cfg.propKey, $(this).is(':checked') ? 'Y' : null, DEF_HP_VALUES[cfg.fd]);
    } else {
      DEF_HP_VALUES[cfg.fd] = $(this).val();
    }
  });
}

/* 선택한 Handler에 맞는 입력칸을 새로 그린다. 값은 DEF_HP_VALUES에서 복원한다. */
function defBuildHandlerParamForm(handlerCd) {
  var keys = defHandlerParamKeys(handlerCd);
  var $grid = $('#defHandlerParamGrid').empty();

  if (keys.length === 0) {
    $('#defHandlerParamSub').text('이 Handler는 추가로 입력할 항목이 없습니다.');
    return;
  }
  $('#defHandlerParamSub').text('선택한 Handler에 필요한 항목입니다. *는 필수입니다.');

  keys.forEach(function (key) {
    var cfg = DEF_HANDLER_PARAM_CFG[key];
    var val = DEF_HP_VALUES[cfg.fd] || '';
    var attrs = ' id="' + cfg.fd + '" name="' + cfg.fd + '" data-hp-key="' + key + '"';
    if (cfg.max > 0) { attrs += ' maxlength="' + cfg.max + '"'; }

    var inputHtml;
    if (cfg.gub === 'mtxt') {
      inputHtml = '<textarea' + attrs + ' rows="3" style="font-family:monospace;"></textarea>';
    } else if (cfg.gub === 'chkyn') {
      inputHtml = '<label class="qr_chk_wrap"><input type="checkbox"' + attrs + '> <span>사용</span></label>';
    } else {
      inputHtml = '<input type="text"' + attrs + ' style="font-family:monospace;">';
    }

    var $field = $('<div class="qr_field' + (cfg.gub === 'mtxt' ? ' qr_span2' : '') + '">'
      + '<label for="' + cfg.fd + '">' + qrEsc(cfg.title) + (cfg.man === 'Y' ? ' <em>*</em>' : '') + '</label>'
      + inputHtml
      + '<span class="qr_err" id="' + cfg.fd + 'Err">' + qrEsc(cfg.title) + ' 값을 입력하세요.</span>'
      + '</div>');

    // 값은 마크업 문자열이 아니라 .val()로 넣는다(따옴표/줄바꿈이 섞여도 안전).
    if (cfg.gub === 'chkyn') {
      $field.find('input[type="checkbox"]').prop('checked', defExtPropGet(cfg.propKey, val) === 'Y');
    } else {
      $field.find('#' + cfg.fd).val(val);
    }
    $grid.append($field);
  });
}

/* Handler 변경 - 현재 입력값을 보관한 뒤 새 Handler의 입력칸으로 교체한다. */
function defOnHandlerChange() {
  defSyncHandlerParamsFromDom();
  defBuildHandlerParamForm($('#apiHandlerCd').val());
}

/* 필수 Handler 파라미터 검증. 화면에 보이는 항목만 검사한다(안 보이는 값은 저장 시 서버가 비운다). */
function defValidateHandlerParams() {
  if ($('#defHandlerParamBox').hasClass('qr_hide')) { return true; }

  var hasErr = false;
  $('#defHandlerParamGrid').find('.qr_err').removeClass('qr_show');
  $('#defHandlerParamGrid').find('input, textarea').removeClass('qr_input_err');

  defHandlerParamKeys($('#apiHandlerCd').val()).forEach(function (key) {
    var cfg = DEF_HANDLER_PARAM_CFG[key];
    if (cfg.man !== 'Y' || cfg.gub === 'chkyn') { return; }
    if (!$.trim($('#' + cfg.fd).val() || '')) {
      $('#' + cfg.fd).addClass('qr_input_err');
      $('#' + cfg.fd + 'Err').addClass('qr_show');
      hasErr = true;
    }
  });

  if (hasErr) {
    $('#defHandlerParamGrid').find('.qr_input_err').first().focus();
  }
  return !hasErr;
}

/* 저장 payload에 Handler 파라미터를 채운다. 화면에 없는 항목은 빈 값으로 보내고, 서버도
   clearUnusedHandlerParams()로 한 번 더 정리한다. */
function defAppendHandlerParams(formData) {
  defSyncHandlerParamsFromDom();
  var used = {};
  if (!$('#defHandlerParamBox').hasClass('qr_hide')) {
    defHandlerParamKeys($('#apiHandlerCd').val()).forEach(function (key) {
      used[DEF_HANDLER_PARAM_CFG[key].fd] = true;
    });
  }
  DEF_HANDLER_PARAM_FIELDS.forEach(function (fd) {
    formData[fd] = used[fd] ? (DEF_HP_VALUES[fd] || '') : '';
  });
  return formData;
}

/* 수정 모드로 기존 API를 불러올 때 - 입력칸을 그리기 전에 값부터 채워 넣는다. */
function defLoadHandlerParamValues(def) {
  DEF_HP_VALUES = {};
  DEF_HANDLER_PARAM_FIELDS.forEach(function (fd) {
    DEF_HP_VALUES[fd] = def[fd] || '';
  });
}

function defClearHandlerParamValues() {
  DEF_HP_VALUES = {};
}

/* ---------------- 기본 정보 간소화(퍼블_v19.0) ---------------- */

/* "추가 설정" 열고 닫기. qr_hide(display:none)로만 감춘다 - 입력 요소를 DOM에서 빼면
   저장 시 값을 읽을 수 없다. */
function defToggleExtra(open) {
  $('#defExtraBox').toggleClass('qr_hide', !open);
  $('#defExtraToggle').toggleClass('qr_on', open).attr('aria-expanded', open ? 'true' : 'false');
  if (!open) { defRefreshExtraSum(); }
}

/* 접혀 있을 때 보이는 요약 - "개발자그룹 · 비노출 · REST · 미적용" 형태.
   셀렉트의 선택된 텍스트를 그대로 쓰므로 옵션 문구가 바뀌어도 따라간다. */
function defRefreshExtraSum() {
  var parts = [];
  ['#autId', '#useYn', '#guideGubun', '#sandboxYn'].forEach(function (sel) {
    var txt = $.trim($(sel).find('option:selected').text());
    if (txt && txt !== '선택' && txt !== '선택안함') { parts.push(txt); }
  });
  $('#defExtraSum').text(parts.join(' · '));
}

/* "기본 정보" 아코디언을 접었을 때 헤더에 보이는 요약 - 입력 중인 API 이름과 Method/Path.
   아직 아무것도 안 넣었으면 원래 안내 문구를 그대로 둔다. */
function defRefreshBasicSum() {
  var apiNm = $.trim($('#apiNm').val() || '');
  var apiPath = $.trim($('#apiPath').val() || '');
  var $sum = $('#defBasicSum');

  if (!apiNm && !apiPath) {
    $sum.text('반드시 입력해야 하는 항목만 남겼습니다');
    return;
  }

  var $out = $();
  if (apiNm) { $out = $out.add($('<span class="qr_sum_v"></span>').text(apiNm)); }
  if (apiPath) {
    if (apiNm) { $out = $out.add($('<span class="qr_sum_sep">·</span>')); }
    var method = defMethodNm($('#methodCd').val()) || '';
    $out = $out.add($('<span class="qr_sum_v qr_mono"></span>').text($.trim(method + ' ' + apiPath)));
  }
  $sum.empty().append($out);
}

/* API그룹을 만들고 드롭다운에 넣은 뒤 그대로 선택한다. 이름 중복은 서버가 막는다
   - 같은 이름이 둘이면 좌측 트리에서 어느 쪽에 넣었는지 구분할 수 없다. */
function defSaveCtgry() {
  var nm = $.trim($('#defCtgryNm').val());
  if (!nm) {
    $('#defCtgryNm').addClass('qr_input_err');
    $('#defCtgryNmErr').addClass('qr_show');
    return;
  }

  $('#defCtgrySave').prop('disabled', true);
  $.ajax({
    url: c_url + 'api/spcreg/def/savCtgryAjax.do',
    type: 'POST', cache: false, dataType: 'json',
    data: {
      apiSpcNo: $('#apiSpcNo').val(),
      ctgryNm: nm,
      ctgryDesc: $('#defCtgryDesc').val()
    }
  })
    .done(function (res) {
      if (res.returnCode !== '1') {
        alert_message(res.message || 'API그룹을 추가하지 못했습니다.');
        return;
      }
      $('<option></option>').val(res.ctgry.apiCtgryNo).text(res.ctgry.ctgryNm).appendTo('#apiCtgryNo');
      $('#apiCtgryNo').val(res.ctgry.apiCtgryNo);
      $('#defCtgryModal').addClass('qr_hide');
    })
    .fail(function () { alert_message('API그룹을 추가하지 못했습니다.'); })
    .always(function () { $('#defCtgrySave').prop('disabled', false); });
}
