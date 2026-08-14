/* paramTreeEditor.js - "파라미터 설계" 트리+YAML 팝업 공용 모듈.
   quickApiReg.js/simpleView.js 둘 다 이 모듈을 불러다 쓴다(둘 다 같은 마크업/ID로 팝업을 갖고 있어야 함:
   #qrParamModal, #qrPdPane, #qrPdYaml 등 - 퍼블리셔 산출물의 ID를 그대로 재사용).
   원본 데이터는 KOA_TB_API_PARAM의 flat row(PRNTS_PARAM_NO로 자기참조)이고, 화면에서는 이걸
   중첩 트리(kids 배열)로 재구성해서 보여준다. YAML 파싱/생성은 이미 로드된 swagger-parser.min.js
   (YAML.parse/YAML.stringify - quickRegShared.js의 qrParseOasYaml과 동일 라이브러리)를 재사용한다. */

/* g_pt_dataTypeList(DATTYP1000)/g_pt_piiList(PIICLS1000)는 이 파일이 로드되기 전에
   각 페이지의 인라인 스크립트가 이미 선언·할당해 둔다(g_qr_dataTypeList 등과 동일한 관례).
   여기서 var로 다시 선언하면 스크립트 로드 순서상 페이지가 채운 값을 빈 배열로 덮어써 버리므로
   절대 여기서 초기화하지 않는다. */
var g_pt_paramSeq = 0;

var PT_SCOPES = {
  'in': { label: '입력 파라미터', kind: 'schema', paramTypeCd: 'PRMTYP1010', paramLoc: 'body', root: 'requestBody.content.application/json.schema' },
  'query': { label: 'Query 파라미터', kind: 'params', paramTypeCd: 'PRMTYP1010', paramLoc: 'query', root: 'parameters (in: query)' },
  'out': { label: '출력 파라미터', kind: 'schema', paramTypeCd: 'PRMTYP1020', paramLoc: 'body', root: 'responses.200.content.application/json.schema' }
};

var PT_STORE = { 'in': [], 'query': [], 'out': [] }; // 스코프별 현재 트리(루트 노드 배열)
var PT_MTH_CLASS = { GET: 'sv_mth_get', POST: 'sv_mth_post', PUT: 'sv_mth_put', DELETE: 'sv_mth_delete', PATCH: 'sv_mth_patch' };

/* extra: 확장 속성 9종(KOA_TB_API_PARAM) + 응답 상태코드 2종을 담는 선택적 객체. 기존 호출부는
   이 인자 없이 그대로 호출되고(전부 빈 값으로 초기화), DB에서 불러올 때만(ptBuildTree) 채워서 넘긴다. */
function ptNode(name, dataTypeCd, req, desc, ex, kids, ofDataTypeCd, personalData, tempId, extra) {
  extra = extra || {};
  return {
    tempId: tempId || ('t' + (g_pt_paramSeq++)),
    parentTempId: '',
    paramNm: name || '',
    dataTypeCd: dataTypeCd || 'DATTYP1010',
    required: !!req,
    paramDesc: desc || '',
    exam: ex || '',
    of: ofDataTypeCd || 'DATTYP1010',
    personalData: personalData || '',
    kids: kids || [],
    col: false,
    open: false,
    // 확장 속성 9종 - 전부 선택값. Y/N 플래그는 문자열('Y'/'')로 관리(체크박스 on/off와 매핑).
    doNotSend: extra.doNotSend || '',
    fixedValue: extra.fixedValue || '',
    hidden: extra.hidden || '',
    mappingKey: extra.mappingKey || '',
    bigo: extra.bigo || '',
    paramSandboxYn: extra.paramSandboxYn || '',
    hdpUrlDecode: extra.hdpUrlDecode || '',
    hdpUrlEncode: extra.hdpUrlEncode || '',
    hdpUploadTarget: extra.hdpUploadTarget || '',
    // 응답 상태코드(출력 파라미터에서만 의미 있음) - KOA_TB_API_PARAM.RES_CD/RES_DESC
    resCd: extra.resCd || '',
    resDesc: extra.resDesc || ''
  };
}

function ptIsBranch(n) {
  return n.dataTypeCd === 'DATTYP1050' || (n.dataTypeCd === 'DATTYP1060' && n.of === 'DATTYP1050');
}
function ptTypeNm(cd) { return qrCodeNm(g_pt_dataTypeList, cd) || cd; }
function ptEsc(s) { return $('<div>').text(s == null ? '' : s).html(); }
function ptClone(v) { return JSON.parse(JSON.stringify(v)); }

/* ---------------- flat DB rows -> 중첩 트리 ---------------- */
/* rows: selParamList()가 반환하는 형태(paramNo/prntsParamNo/objOdrg/paramTypeCd/paramLoc 포함).
   scope별로 이미 필터링된 rows를 넘겨받는다(paramTypeCd+paramLoc 기준 필터링은 호출부에서). */
function ptBuildTree(rows) {
  var byRealNo = {};
  var roots = [];
  var i;
  for (i = 0; i < rows.length; i++) {
    var r = rows[i];
    var n = ptNode(r.paramNm, r.dataTypeCd, r.required !== 'N', r.paramDesc, r.exam, [], null, r.personalData, 'r' + r.paramNo, {
      doNotSend: r.doNotSend, fixedValue: r.fixedValue, hidden: r.hidden, mappingKey: r.mappingKey, bigo: r.bigo,
      paramSandboxYn: r.paramSandboxYn, hdpUrlDecode: r.hdpUrlDecode, hdpUrlEncode: r.hdpUrlEncode, hdpUploadTarget: r.hdpUploadTarget,
      resCd: r.resCd, resDesc: r.resDesc
    });
    n.realParamNo = String(r.paramNo);
    n.prntsRealNo = r.prntsParamNo ? String(r.prntsParamNo) : '';
    n.objOdrg = parseInt(r.objOdrg || '0', 10) || 0;
    byRealNo[n.realParamNo] = n;
  }
  var all = [];
  for (var k in byRealNo) { if (byRealNo.hasOwnProperty(k)) { all.push(byRealNo[k]); } }
  all.sort(function (a, b) { return a.objOdrg - b.objOdrg; });
  for (i = 0; i < all.length; i++) {
    var node = all[i];
    if (node.prntsRealNo && byRealNo[node.prntsRealNo]) {
      var parent = byRealNo[node.prntsRealNo];
      node.parentTempId = parent.tempId;
      parent.kids.push(node);
      // 부모가 array이고 자식이 있으면 "array of object"로 취급(별도 저장 컬럼이 없어 이렇게 추론)
      if (parent.dataTypeCd === 'DATTYP1060') { parent.of = 'DATTYP1050'; }
    } else {
      roots.push(node);
    }
  }
  return roots;
}

/* ---------------- 중첩 트리 -> 저장용 flat 행 ---------------- */
function ptFlattenTree(nodes, scope) {
  var sc = PT_SCOPES[scope];
  var out = [];
  (function walk(list, parentTempId) {
    for (var i = 0; i < list.length; i++) {
      var n = list[i];
      if (!n.paramNm || !n.paramNm.trim()) { continue; }
      out.push({
        tempId: n.tempId,
        parentTempId: parentTempId,
        paramNm: n.paramNm,
        dataTypeCd: n.dataTypeCd,
        required: n.required ? 'Y' : 'N',
        paramDesc: n.paramDesc,
        exam: n.exam,
        personalData: n.personalData,
        paramTypeCd: sc.paramTypeCd,
        paramLoc: sc.paramLoc,
        doNotSend: n.doNotSend, fixedValue: n.fixedValue, hidden: n.hidden, mappingKey: n.mappingKey, bigo: n.bigo,
        paramSandboxYn: n.paramSandboxYn, hdpUrlDecode: n.hdpUrlDecode, hdpUrlEncode: n.hdpUrlEncode, hdpUploadTarget: n.hdpUploadTarget,
        resCd: n.resCd, resDesc: n.resDesc
      });
      if (ptIsBranch(n) && n.kids.length) { walk(n.kids, n.tempId); }
    }
  })(nodes, '');
  return out;
}

/* ---------------- 부모 화면: 읽기전용 미리보기 트리 ---------------- */
function ptCountAll(list) {
  var n = 0;
  for (var i = 0; i < list.length; i++) { n++; if (ptIsBranch(list[i])) { n += ptCountAll(list[i].kids); } }
  return n;
}
function ptPvRowHtml(n, d, path) {
  var kids = ptIsBranch(n) && n.kids.length;
  var caret = '<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9l6 6 6-6"/></svg>';
  return '<div class="qr_pv_row' + (n.col ? ' qr_pv_col' : '') + '" data-depth="' + d + '" data-path="' + path + '">'
    + (kids ? '<button type="button" class="qr_pv_toggle" aria-label="하위 접기·펼치기">' + caret + '</button>'
            : '<span class="qr_pv_toggle qr_pv_leaf"></span>')
    + '<span class="qr_pv_key">' + ptEsc(n.paramNm) + '</span>'
    + '<span class="qr_pv_type' + (ptIsBranch(n) ? ' qr_pv_type_br' : '') + '">' + ptTypeNm(n.dataTypeCd)
    + (n.dataTypeCd === 'DATTYP1060' ? '<span class="qr_pv_of">of ' + ptTypeNm(n.of) + '</span>' : '') + '</span>'
    + (n.required ? '<span class="qr_pv_req">필수</span>' : '<span class="qr_pv_req qr_pv_opt">선택</span>')
    + '<span class="qr_pv_desc">' + (n.personalData ? '<span class="qr_pv_pii">' + ptEsc(qrCodeNm(g_pt_piiList, n.personalData)) + '</span>' : '') + ptEsc(n.paramDesc) + '</span></div>';
}
function ptNodeAt(list, path) {
  var seg = path.split('.'), node = null;
  for (var i = 0; i < seg.length; i++) { node = list[parseInt(seg[i], 10)]; list = node.kids; }
  return node;
}

/**
 * 부모 화면에 스코프별 읽기전용 미리보기를 그린다.
 * @param prefix 화면별 DOM id 접두어(quickApiReg는 'qr', simpleView는 'sv' 등) - 요소 id 규칙:
 *   [prefix]Pv_[scope] (트리 컨테이너), [prefix]ParamBadge/[prefix]ParamSum(총계, quickApiReg 전용 단일 아코디언 구조에서만 사용)
 */
function ptRenderPreview(prefix, onAfterRender) {
  for (var s = 0; s < 3; s++) {
    var scope = ['in', 'query', 'out'][s];
    var $el = $('#' + prefix + 'Pv_' + scope);
    if ($el.length === 0) { continue; }
    var html = '';
    (function walk(list, d, base) {
      for (var i = 0; i < list.length; i++) {
        var x = list[i], p = base === '' ? String(i) : base + '.' + i;
        html += ptPvRowHtml(x, d, p);
        if (ptIsBranch(x) && !x.col) { walk(x.kids, d + 1, p); }
      }
    })(PT_STORE[scope], 0, '');
    $el.html(html || '<p class="qr_pv_empty">정의된 파라미터가 없습니다. [' + PT_SCOPES[scope].label + ' 설계]에서 추가하세요.</p>');
    var cnt = ptCountAll(PT_STORE[scope]);
    $('.qr_pv_tab[data-scope="' + scope + '"] .qr_pv_cnt').text(cnt);
  }
  if (typeof onAfterRender === 'function') { onAfterRender(); }
}

/* 트리 컨테이너 클릭 위임 등록(접기/펼치기). prefix별로 한 번만 등록하면 됨. */
function ptBindPreviewToggle(prefix) {
  $('#' + prefix + 'ParamBox, .qr_pv_body').off('click.pt').on('click.pt', '.qr_pv_toggle', function () {
    var $row = $(this).closest('.qr_pv_row');
    var scope = $row.closest('[id^="' + prefix + 'Pv_"]').attr('id').replace(prefix + 'Pv_', '');
    var n = ptNodeAt(PT_STORE[scope], $row.attr('data-path'));
    n.col = !n.col;
    ptRenderPreview(prefix);
  });
}

/* ---------------- 팝업: 트리 편집기 ---------------- */
var PT_SCOPE = 'in';
var PT_WORK = [];
var PT_CM = null;
var PT_ON_SAVE = null;

function ptOpts(list, sel) {
  var s = '';
  for (var i = 0; i < list.length; i++) {
    s += '<option value="' + list[i].comnCd + '"' + (list[i].comnCd === sel ? ' selected' : '') + '>' + list[i].cdNm + '</option>';
  }
  return s;
}
function ptPiiOpts(sel) {
  var s = '<option value="">민감정보 없음</option>';
  for (var i = 0; i < g_pt_piiList.length; i++) {
    var c = g_pt_piiList[i];
    s += '<option value="' + c.comnCd + '"' + (c.comnCd === (sel || '') ? ' selected' : '') + '>' + c.cdNm + '</option>';
  }
  return s;
}
function ptRowHtml(n, d, path) {
  var kids = ptIsBranch(n) && n.kids.length;
  var rail = '';
  for (var g = 0; g < d; g++) { rail += '<span class="qr_pd_rail"></span>'; }
  var caret = '<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9l6 6 6-6"/></svg>';
  var typeList = [];
  for (var i = 0; i < g_pt_dataTypeList.length; i++) { typeList.push(g_pt_dataTypeList[i]); }
  return '<div class="qr_pd_item' + (ptIsBranch(n) ? ' qr_pd_branch' : '') + (n.col ? ' qr_pd_col' : '') + (n.open ? ' qr_pd_open' : '') + '"'
    + ' data-depth="' + d + '" data-path="' + path + '">'
    + '<div class="qr_pd_row">'
    + '<div class="qr_pd_lead">' + rail
    + '<button type="button" class="qr_pd_caret' + (kids ? '' : ' qr_pd_off') + '" aria-label="하위 접기·펼치기">' + caret + '</button>'
    + '<input type="text" class="qr_pd_name" value="' + ptEsc(n.paramNm) + '" placeholder="파라미터명">'
    + '</div>'
    + '<button type="button" class="qr_pd_info" aria-label="설명·예시 입력">i</button>'
    + '<select class="qr_pd_type">' + ptOpts(typeList, n.dataTypeCd) + '</select>'
    + '<select class="qr_pd_of' + (n.dataTypeCd === 'DATTYP1060' ? '' : ' qr_pd_of_off') + '" aria-label="배열 요소 타입">' + ptOpts(typeList, n.of) + '</select>'
    + '<button type="button" class="qr_pd_req' + (n.required ? ' qr_on' : '') + '" aria-pressed="' + (n.required ? 'true' : 'false') + '" title="필수 여부"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12.5l4.5 4.5L19 7"/></svg></button>'
    + '<button type="button" class="qr_pd_del" aria-label="삭제">&times;</button>'
    + '</div>'
    + '<div class="qr_pd_meta' + (n.open ? '' : ' qr_hide') + '">'
    + '<input type="text" class="qr_pd_desc" value="' + ptEsc(n.paramDesc) + '" placeholder="설명">'
    + '<input type="text" class="qr_pd_ex" value="' + ptEsc(n.exam) + '" placeholder="예시">'
    + '<select class="qr_pd_pii' + (n.personalData ? ' qr_on' : '') + '" aria-label="민감정보 분류">' + ptPiiOpts(n.personalData) + '</select>'
    + ptExtHtml(n)
    + '</div></div>';
}
/* 확장 속성 9종 + 응답 상태코드(출력 스코프에서만) - KOA_TB_API_PARAM 컬럼과 1:1. quickApiReg.css를
   건드리지 않기로 한 원칙 때문에 별도 클래스 스타일 없이 인라인 스타일로만 배치한다. */
function ptExtCheckHtml(cls, label, checked) {
  return '<label style="display:inline-flex;align-items:center;gap:4px;font-size:12px;margin:0 12px 0 0;white-space:nowrap;">'
    + '<input type="checkbox" class="' + cls + '"' + (checked === 'Y' ? ' checked' : '') + '>' + ptEsc(label) + '</label>';
}
function ptExtHtml(n) {
  var html = '<div class="qr_pd_ext" style="flex-basis:100%;display:flex;flex-wrap:wrap;align-items:center;gap:8px;margin-top:8px;padding-top:8px;border-top:1px dashed #ddd;">';
  html += ptExtCheckHtml('qr_pd_donotsend', '미전송', n.doNotSend);
  html += ptExtCheckHtml('qr_pd_hidden', '숨김', n.hidden);
  html += ptExtCheckHtml('qr_pd_sandbox', 'sandbox 전용', n.paramSandboxYn);
  html += ptExtCheckHtml('qr_pd_urldecode', 'URL Decode', n.hdpUrlDecode);
  html += ptExtCheckHtml('qr_pd_urlencode', 'URL Encode', n.hdpUrlEncode);
  html += ptExtCheckHtml('qr_pd_uploadtarget', '업로드 대상', n.hdpUploadTarget);
  html += '<input type="text" class="qr_pd_fixedvalue" value="' + ptEsc(n.fixedValue) + '" placeholder="고정값" style="flex:1 1 100px;min-width:80px;">';
  html += '<input type="text" class="qr_pd_mappingkey" value="' + ptEsc(n.mappingKey) + '" placeholder="매핑키" style="flex:1 1 100px;min-width:80px;">';
  html += '<input type="text" class="qr_pd_bigo" value="' + ptEsc(n.bigo) + '" placeholder="비고" style="flex:2 1 160px;min-width:120px;">';
  if (PT_SCOPE === 'out') {
    html += '<input type="text" class="qr_pd_rescd" value="' + ptEsc(n.resCd) + '" placeholder="응답 상태코드(기본 200)" style="flex:1 1 140px;min-width:120px;">';
    html += '<input type="text" class="qr_pd_resdesc" value="' + ptEsc(n.resDesc) + '" placeholder="상태 설명" style="flex:1 1 140px;min-width:120px;">';
  }
  html += '</div>';
  return html;
}
function ptTree() {
  var html = '';
  (function walk(list, d, base) {
    for (var i = 0; i < list.length; i++) {
      var x = list[i], p = base === '' ? String(i) : base + '.' + i;
      html += ptRowHtml(x, d, p);
      if (ptIsBranch(x) && !x.col) {
        walk(x.kids, d + 1, p);
        html += '<button type="button" class="qr_pd_addchild" data-path="' + p + '" style="margin-left:' + ((d + 1) * 16 + 22) + 'px">+ 하위 필드 추가</button>';
      }
    }
  })(PT_WORK, 0, '');
  $('#qrPdPane').html(html || '<p class="qr_pv_empty">아직 정의된 필드가 없습니다.</p>');
}
function ptListAt(list, path) {
  var seg = path.split('.');
  for (var i = 0; i < seg.length - 1; i++) { list = list[parseInt(seg[i], 10)].kids; }
  return list;
}

/* ---------------- 트리 <-> YAML(OAS 3.0) ---------------- */
var PT_TYPE_JS = { DATTYP1010: 'string', DATTYP1020: 'number', DATTYP1030: 'integer', DATTYP1040: 'boolean', DATTYP1050: 'object', DATTYP1060: 'array' };
var PT_JS_TYPE = { string: 'DATTYP1010', number: 'DATTYP1020', integer: 'DATTYP1030', boolean: 'DATTYP1040', object: 'DATTYP1050', array: 'DATTYP1060' };
function ptIsNumType(cd) { return cd === 'DATTYP1020' || cd === 'DATTYP1030'; }

function ptSchemaOf(n) {
  var s;
  if (n.dataTypeCd === 'DATTYP1050') { s = ptObjSchema(n.kids); }
  else if (n.dataTypeCd === 'DATTYP1060') {
    s = { type: 'array', items: n.of === 'DATTYP1050' ? ptObjSchema(n.kids) : { type: PT_TYPE_JS[n.of] || 'string' } };
  } else {
    s = { type: PT_TYPE_JS[n.dataTypeCd] || 'string' };
  }
  if (n.paramDesc) { s.description = n.paramDesc; }
  if (n.exam !== '') { s.example = ptIsNumType(n.dataTypeCd) ? Number(n.exam) : n.exam; }
  if (n.personalData) { s['x-personalData'] = n.personalData; }
  return s;
}
function ptObjSchema(kids) {
  var o = { type: 'object' }, req = [], props = {}, i;
  for (i = 0; i < kids.length; i++) { if (kids[i].required) { req.push(kids[i].paramNm); } }
  if (req.length) { o.required = req; }
  for (i = 0; i < kids.length; i++) { props[kids[i].paramNm] = ptSchemaOf(kids[i]); }
  o.properties = props;
  return o;
}
function ptParamsSchema(nodes) {
  var out = [];
  for (var i = 0; i < nodes.length; i++) {
    var n = nodes[i], p = { name: n.paramNm, 'in': 'query' };
    if (n.paramDesc) { p.description = n.paramDesc; }
    p.required = n.required;
    p.schema = { type: PT_TYPE_JS[n.dataTypeCd] || 'string' };
    if (n.exam !== '') { p.schema.example = ptIsNumType(n.dataTypeCd) ? Number(n.exam) : n.exam; }
    if (n.personalData) { p['x-personalData'] = n.personalData; }
    out.push(p);
  }
  return out;
}
function ptWorkYaml() {
  var sc = PT_SCOPES[PT_SCOPE];
  var root = sc.kind === 'params' ? ptParamsSchema(PT_WORK) : ptObjSchema(PT_WORK);
  try {
    return '# ' + sc.root + '\n' + YAML.stringify(root);
  } catch (e) {
    return '# ' + sc.root + '\n';
  }
}
function ptFromSchema(name, s, req) {
  s = s || {};
  var dataTypeCd = PT_JS_TYPE[s.type] || 'DATTYP1010';
  // Swagger2 파라미터/응답 헤더는 표준 OAS3의 `example` 대신 커스텀 `x-example`을 쓴다
  // (예: 참고자료/API샘플YML/gigaGenie.yml) - 둘 다 지원.
  var exampleVal = s.example != null ? s.example : s['x-example'];
  var n = ptNode(name, dataTypeCd, req, s.description || '', exampleVal == null ? '' : String(exampleVal), null, null, s['x-personalData'] || '');
  if (dataTypeCd === 'DATTYP1050') { n.kids = ptFromObject(s); }
  if (dataTypeCd === 'DATTYP1060') {
    var it = s.items || { type: 'string' };
    n.of = PT_JS_TYPE[it.type] || 'DATTYP1010';
    if (n.of === 'DATTYP1050') { n.kids = ptFromObject(it); }
  }
  return n;
}
function ptFromObject(o) {
  var props = o.properties || {}, req = o.required || [], out = [], k;
  for (k in props) {
    if (props.hasOwnProperty(k)) { out.push(ptFromSchema(k, props[k], $.inArray(k, req) > -1)); }
  }
  return out;
}
function ptFromParams(arr) {
  var out = [];
  for (var i = 0; i < arr.length; i++) {
    var p = arr[i] || {}, s = p.schema || {};
    var dataTypeCd = PT_JS_TYPE[s.type] || 'DATTYP1010';
    out.push(ptNode(p.name || 'unnamed', dataTypeCd, p.required === true || p.required === 'true',
      p.description || '', s.example == null ? '' : String(s.example), null, null, p['x-personalData'] || ''));
  }
  return out;
}
function ptYamlMsg(txt, isErr) {
  $('#qrPdYamlMsg').text(isErr ? txt : '').toggleClass('qr_hide', !isErr);
}
function ptYamlGet() { return PT_CM ? PT_CM.getValue() : $('#qrPdYaml').val(); }
function ptYamlSet(v) {
  if (!PT_CM) { $('#qrPdYaml').val(v); return; }
  var sc = PT_CM.getScrollInfo();
  PT_CM.setValue(v);
  PT_CM.scrollTo(sc.left, sc.top);
}
function ptPushYaml() { ptYamlSet(ptWorkYaml()); ptYamlMsg('', false); }
function ptPullYaml() {
  var text = ptYamlGet();
  var lines = text.split('\n').filter(function (l) { return l.trim().charAt(0) !== '#'; }).join('\n');
  var parsed;
  try { parsed = YAML.parse(lines); } catch (e) { ptYamlMsg('YAML을 읽지 못했습니다: ' + (e && e.message ? e.message : e), true); return; }
  if (!parsed) { ptYamlMsg('YAML이 비어있습니다.', true); return; }
  var sc = PT_SCOPES[PT_SCOPE];
  if (sc.kind === 'params') {
    if (Object.prototype.toString.call(parsed) !== '[object Array]') { ptYamlMsg('Query 스코프는 - name: 으로 시작하는 목록이어야 합니다.', true); return; }
    PT_WORK = ptFromParams(parsed);
  } else {
    if (!parsed.properties) { ptYamlMsg('properties 항목을 찾지 못했습니다.', true); return; }
    PT_WORK = ptFromObject(parsed);
  }
  ptTree();
  ptYamlMsg('', false);
}

/* ---------------- 팝업 열기/저장 ---------------- */
function ptInitCodeMirror() {
  if (PT_CM || !window.CodeMirror) { return; }
  PT_CM = CodeMirror.fromTextArea(document.getElementById('qrPdYaml'), {
    mode: 'text/x-yaml',
    lineNumbers: true,
    lineWrapping: true,
    indentUnit: 2,
    tabSize: 2,
    foldGutter: true,
    gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
    extraKeys: { 'Ctrl-Q': function (cm) { cm.foldCode(cm.getCursor()); } }
  });
  var timer = null;
  PT_CM.on('change', function (cm, ch) {
    if (ch.origin === 'setValue') { return; }
    clearTimeout(timer);
    timer = setTimeout(ptPullYaml, 350);
  });
}

/**
 * 스코프 하나를 편집하는 팝업을 연다.
 * @param scope 'in'|'query'|'out'
 * @param methodNm 팝업 헤더에 보여줄 Method 표시용 텍스트
 * @param pathTxt 팝업 헤더에 보여줄 Path
 * @param onSave function(scope, flatRows) - 저장 버튼 클릭 시 호출. flatRows는 ptFlattenTree() 결과.
 */
function ptOpenDesigner(scope, methodNm, pathTxt, onSave) {
  PT_SCOPE = scope;
  PT_WORK = ptClone(PT_STORE[scope]);
  PT_ON_SAVE = onSave;

  $('#qrPdTitle').text(PT_SCOPES[scope].label + ' 설계');
  $('#qrPdTreeScope').text(PT_SCOPES[scope].label);
  $('#qrPdScope').text(PT_SCOPES[scope].root);
  $('#qrPdMth').text(methodNm || '').attr('class', 'qr_pd_mth');
  $('#qrPdPath').text(pathTxt || '');

  ptTree();
  ptInitCodeMirror();
  ptPushYaml();
  $('#qrParamModal').removeClass('qr_hide');
  if (PT_CM) { setTimeout(function () { PT_CM.refresh(); }, 30); }
}

function ptBindDesigner() {
  $('#qrPdCancel, #qrParamModalClose').off('click.pt').on('click.pt', function () {
    $('#qrParamModal').addClass('qr_hide');
  });
  $('#qrPdSave').off('click.pt').on('click.pt', function () {
    PT_STORE[PT_SCOPE] = ptClone(PT_WORK);
    var flat = ptFlattenTree(PT_STORE[PT_SCOPE], PT_SCOPE);
    if (typeof PT_ON_SAVE === 'function') { PT_ON_SAVE(PT_SCOPE, flat); }
    $('#qrParamModal').addClass('qr_hide');
  });
  $('#qrPdCollapse').off('click.pt').on('click.pt', function () {
    (function walk(list) {
      for (var i = 0; i < list.length; i++) { if (ptIsBranch(list[i])) { list[i].col = true; walk(list[i].kids); } }
    })(PT_WORK);
    ptTree();
  });
  $('#qrPdToggleYaml').off('click.pt').on('click.pt', function () {
    var on = $('.qr_pd_body').toggleClass('qr_pd_solo').hasClass('qr_pd_solo');
    $(this).text(on ? 'YAML 보기' : 'YAML 숨기기').attr('aria-expanded', on ? 'false' : 'true');
    if (!on && PT_CM) { setTimeout(function () { PT_CM.refresh(); }, 30); }
  });
  $('#qrPdGrip').off('click.pt').on('click.pt', function () { $('#qrPdToggleYaml').trigger('click'); });

  var $pane = $('#qrPdPane');
  function nodeOf(el) { return ptNodeAt(PT_WORK, $(el).closest('[data-path]').attr('data-path')); }
  $pane.off('.pt');
  $pane.on('input.pt', '.qr_pd_name', function () { nodeOf(this).paramNm = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_desc', function () { nodeOf(this).paramDesc = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_ex', function () { nodeOf(this).exam = $(this).val(); ptPushYaml(); });
  $pane.on('change.pt', '.qr_pd_type', function () {
    var n = nodeOf(this);
    n.dataTypeCd = $(this).val();
    if (!ptIsBranch(n)) { n.kids = []; }
    ptTree(); ptPushYaml();
  });
  $pane.on('change.pt', '.qr_pd_of', function () {
    var n = nodeOf(this);
    n.of = $(this).val();
    if (!ptIsBranch(n)) { n.kids = []; }
    ptTree(); ptPushYaml();
  });
  $pane.on('change.pt', '.qr_pd_pii', function () {
    var n = nodeOf(this);
    n.personalData = $(this).val();
    $(this).toggleClass('qr_on', !!n.personalData);
    ptPushYaml();
  });

  /* 확장 속성 9종 - 체크박스는 'Y'/'' 문자열로 저장(DB CHAR(1) 컬럼과 그대로 매핑). */
  var extCheckMap = {
    qr_pd_donotsend: 'doNotSend', qr_pd_hidden: 'hidden', qr_pd_sandbox: 'paramSandboxYn',
    qr_pd_urldecode: 'hdpUrlDecode', qr_pd_urlencode: 'hdpUrlEncode', qr_pd_uploadtarget: 'hdpUploadTarget'
  };
  $.each(extCheckMap, function (cls, field) {
    $pane.on('change.pt', '.' + cls, function () {
      nodeOf(this)[field] = $(this).prop('checked') ? 'Y' : '';
      ptPushYaml();
    });
  });
  $pane.on('input.pt', '.qr_pd_fixedvalue', function () { nodeOf(this).fixedValue = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_mappingkey', function () { nodeOf(this).mappingKey = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_bigo', function () { nodeOf(this).bigo = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_rescd', function () { nodeOf(this).resCd = $(this).val(); ptPushYaml(); });
  $pane.on('input.pt', '.qr_pd_resdesc', function () { nodeOf(this).resDesc = $(this).val(); ptPushYaml(); });
  $pane.on('click.pt', '.qr_pd_req', function () {
    var n = nodeOf(this);
    n.required = !n.required;
    $(this).toggleClass('qr_on', n.required).attr('aria-pressed', n.required ? 'true' : 'false');
    ptPushYaml();
  });
  $pane.on('click.pt', '.qr_pd_info', function () {
    var n = nodeOf(this);
    n.open = !n.open;
    $(this).closest('.qr_pd_item').toggleClass('qr_pd_open', n.open).find('.qr_pd_meta').toggleClass('qr_hide', !n.open);
  });
  $pane.on('click.pt', '.qr_pd_caret', function () {
    var n = nodeOf(this);
    if (!ptIsBranch(n) || !n.kids.length) { return; }
    n.col = !n.col;
    ptTree();
  });
  $pane.on('click.pt', '.qr_pd_del', function () {
    var path = $(this).closest('[data-path]').attr('data-path');
    var list = ptListAt(PT_WORK, path), idx = parseInt(path.split('.').pop(), 10);
    list.splice(idx, 1);
    ptTree(); ptPushYaml();
  });
  $pane.on('click.pt', '.qr_pd_addchild', function () {
    var n = ptNodeAt(PT_WORK, $(this).attr('data-path'));
    n.col = false;
    var child = ptNode('', 'DATTYP1010', false, '', '');
    child.parentTempId = n.tempId;
    n.kids.push(child);
    ptTree(); ptPushYaml();
  });
  $('.qr_pd_left').off('click.pt').on('click.pt', '.qr_pd_addroot', function () {
    PT_WORK.push(ptNode('', 'DATTYP1010', false, '', ''));
    ptTree(); ptPushYaml();
    $('#qrPdPane').scrollTop($('#qrPdPane')[0].scrollHeight);
  });
}
