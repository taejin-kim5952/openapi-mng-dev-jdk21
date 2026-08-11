/* quickRegShared.js - "빠른 API 등록"/"템플릿 관리" 화면 공용 로직.
   두 화면 모두 같은 YAML 파싱·아이콘 규칙을 쓰므로 여기 한 곳에서만 관리한다.
   기존 4단계 마법사 쪽 JS와는 무관 (그쪽은 건드리지 않음). */

// Method 기준 아이콘 (템플릿 이름은 무엇이든 나올 수 있어 확장성이 없으므로, 종류가 고정된
// Method로 아이콘을 결정한다)
var QR_METHOD_ICONS = {
  GET:     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.5" y2="16.5"/></svg>',
  POST:    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>',
  PUT:     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M17 3l4 4L7 21H3v-4L17 3z"/></svg>',
  PATCH:   '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M17 3l4 4L7 21H3v-4L17 3z"/></svg>',
  DELETE:  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>',
  DEFAULT: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>'
};

function qrIconForMethodNm(methodNm) {
  var key = (methodNm || '').toUpperCase();
  return QR_METHOD_ICONS[key] || QR_METHOD_ICONS.DEFAULT;
}

// OAS schema.type -> DATTYP1000 comnCd (그리고 역방향)
var QR_TYPE_TO_DATTYP = {
  string: 'DATTYP1010', number: 'DATTYP1020', integer: 'DATTYP1030',
  boolean: 'DATTYP1040', object: 'DATTYP1050', array: 'DATTYP1060'
};
var QR_DATTYP_TO_TYPE = {
  DATTYP1010: 'string', DATTYP1020: 'number', DATTYP1030: 'integer',
  DATTYP1040: 'boolean', DATTYP1050: 'object', DATTYP1060: 'array'
};

// CodeMirror YAML lint helper: YAML.parse()가 던지는 js-yaml YAMLException의 e.mark(line/column)를
// 그대로 활용해 실시간으로 에러 위치를 표시한다.
if (typeof CodeMirror !== 'undefined' && CodeMirror.registerHelper) {
  CodeMirror.registerHelper('lint', 'yaml', function (text) {
    var found = [];
    if (!text || !text.trim()) { return found; }
    try {
      YAML.parse(text);
    } catch (e) {
      var line = (e && e.mark && typeof e.mark.line === 'number') ? e.mark.line : 0;
      var ch = (e && e.mark && typeof e.mark.column === 'number') ? e.mark.column : 0;
      found.push({
        from: CodeMirror.Pos(line, ch),
        to: CodeMirror.Pos(line, ch + 1),
        message: (e && e.message) ? e.message : String(e),
        severity: 'error'
      });
    }
    return found;
  });
}

/**
 * YAML 에디터(CodeMirror) 공용 초기화 - "빠른 API 등록"의 YAML 등록 팝업과
 * "템플릿 관리"의 YAML 입력창이 같은 설정(문법 하이라이팅/실시간 오류표시/접기/자동완성)을 쓴다.
 */
function qrInitYamlEditor(textareaEl) {
  return CodeMirror.fromTextArea(textareaEl, {
    mode: 'yaml',
    lineNumbers: true,
    tabSize: 2,
    indentUnit: 2,
    lineWrapping: true,
    foldGutter: true,
    foldOptions: { rangeFinder: CodeMirror.fold.indent },
    lint: true,
    gutters: ['CodeMirror-lint-markers', 'CodeMirror-foldgutter'],
    extraKeys: {
      'Ctrl-Space': 'autocomplete',
      'Ctrl-Q': function (cm) { cm.foldCode(cm.getCursor()); }
    },
    hintOptions: { hint: CodeMirror.hint.anyword }
  });
}

function qrEsc(s) {
  return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// 공통코드(comnCd) 목록에서 이름(cdNm)을 찾는다. 못 찾으면 코드값 그대로 반환.
function qrCodeNm(list, code) {
  for (var i = 0; i < (list || []).length; i++) {
    if (list[i].comnCd === code) { return list[i].cdNm; }
  }
  return code || '';
}

/**
 * OAS/Swagger YAML 문자열을 파싱해 필요한 값만 평탄화해서 뽑아낸다.
 * `swagger: "2.0"` 문서와 `openapi: "3.x"` 문서를 모두 감지해서 각자 구조에 맞게 처리한다
 * (OAS2는 host/basePath/schemes + parameters[in=body|query|path|header|formData],
 *  OAS3는 servers[].url + parameters + requestBody.content).
 * 여러 path/method가 있으면 첫 번째만 채택한다(빠른등록/템플릿 모두 Path/Method 1개 제약).
 * body 스키마는 1단계 속성만 평탄화한다(중첩 객체 미지원). OAS2의 $ref는 최상위 definitions만 따라간다.
 */
function qrParseOasYaml(yamlText) {
  var doc = YAML.parse(yamlText);
  if (!doc || typeof doc !== 'object') { throw new Error('빈 문서이거나 형식이 올바르지 않습니다.'); }

  var isOas2 = !!doc.swagger && String(doc.swagger).charAt(0) === '2';
  var result = { params: [], oasVersion: isOas2 ? '2.0' : '3.0' };

  if (doc.info) {
    result.apiNm = doc.info.title;
    result.apiDesc = doc.info.description;
  }

  if (isOas2) {
    if (doc.host) {
      result.host = doc.host;
      result.basPath = doc.basePath || '/';
    }
  } else if (doc.servers && doc.servers.length > 0 && doc.servers[0].url) {
    var m = /^https?:\/\/([^/]+)(\/.*)?$/.exec(doc.servers[0].url);
    if (m) {
      result.host = m[1];
      result.basPath = m[2] || '/';
    }
  }

  var paths = doc.paths || {};
  var pathKeys = Object.keys(paths);
  if (pathKeys.length === 0) { return result; }

  var firstPath = pathKeys[0];
  var methods = paths[firstPath] || {};
  var methodKeys = Object.keys(methods).filter(function (k) {
    return ['get', 'post', 'put', 'delete', 'patch'].indexOf(k.toLowerCase()) > -1;
  });
  if (methodKeys.length === 0) { return result; }

  var firstMethod = methodKeys[0];
  var op = methods[firstMethod] || {};

  result.apiPath = firstPath;
  result.methodCd = 'MTHTYP10' + (['get', 'post', 'put', 'delete', 'patch'].indexOf(firstMethod.toLowerCase()) + 1) + '0';
  result.methodNm = firstMethod.toUpperCase();
  if (!result.apiNm) { result.apiNm = op.summary; }
  if (!result.apiDesc) { result.apiDesc = op.description; }

  result.multiplePaths = (pathKeys.length > 1) || (methodKeys.length > 1);

  if (isOas2) {
    qrExtractSwagger2Params(doc, op, result);
  } else {
    qrExtractOas3Params(op, result);
  }

  return result;
}

function qrExtractOas3Params(op, result) {
  (op.parameters || []).forEach(function (p) {
    var type = (p.schema && p.schema.type) || 'string';
    result.params.push({
      name: p.name,
      type: QR_TYPE_TO_DATTYP[type] || 'DATTYP1010',
      required: p.required ? 'Y' : 'N',
      desc: p.description || ''
    });
  });

  if (op.requestBody && op.requestBody.content) {
    var jsonContent = op.requestBody.content['application/json'];
    if (jsonContent) {
      result.cntTypeCd = 'CNTTYP1010';
      var schema = jsonContent.schema || {};
      var props = schema.properties || {};
      var required = schema.required || [];
      Object.keys(props).forEach(function (key) {
        var prop = props[key] || {};
        result.params.push({
          name: key,
          type: QR_TYPE_TO_DATTYP[prop.type] || 'DATTYP1010',
          required: required.indexOf(key) > -1 ? 'Y' : 'N',
          desc: prop.description || ''
        });
      });
    }
  }
}

function qrExtractSwagger2Params(doc, op, result) {
  (op.parameters || []).forEach(function (p) {
    if (p.in === 'body') {
      result.cntTypeCd = 'CNTTYP1010';
      var schema = qrResolveSwagger2Ref(doc, p.schema || {});
      var props = schema.properties || {};
      var required = schema.required || [];
      Object.keys(props).forEach(function (key) {
        var prop = props[key] || {};
        result.params.push({
          name: key,
          type: QR_TYPE_TO_DATTYP[prop.type] || 'DATTYP1010',
          required: required.indexOf(key) > -1 ? 'Y' : 'N',
          desc: prop.description || ''
        });
      });
      return;
    }
    if (p.in === 'formData' && !result.cntTypeCd) {
      result.cntTypeCd = 'CNTTYP1030'; // application/x-www-form-urlencoded
    }
    result.params.push({
      name: p.name,
      type: QR_TYPE_TO_DATTYP[p.type] || 'DATTYP1010',
      required: p.required ? 'Y' : 'N',
      desc: p.description || ''
    });
  });

  if (!result.cntTypeCd && op.consumes && op.consumes.indexOf('application/json') > -1) {
    result.cntTypeCd = 'CNTTYP1010';
  }
}

// OAS2 문서 내 최상위 $ref(예: '#/definitions/Foo')만 따라간다(중첩/외부 참조 미지원).
function qrResolveSwagger2Ref(doc, schema) {
  if (schema && schema.$ref) {
    var parts = String(schema.$ref).replace(/^#\//, '').split('/');
    var node = doc;
    for (var i = 0; i < parts.length && node; i++) { node = node[parts[i]]; }
    return node || {};
  }
  return schema || {};
}

/**
 * qrParseOasYaml의 역방향: 이미 저장된 필드(이름/설명/Path/Method/Content-Type/파라미터)로부터
 * OAS 3.0 YAML 문서를 재구성한다. tmpltYaml 없이 만들어진 옛날 템플릿(dflt_param_json 기반)을
 * YAML 에디터로 열었을 때 빈 화면 대신 값을 보여주기 위한 용도.
 * params: [{name, type(DATTYP comnCd), required('Y'/'N'), desc}]
 */
function qrBuildOasYaml(apiNm, apiDesc, apiPath, methodNm, cntTypeCd, params) {
  var methodLower = (methodNm || 'get').toLowerCase();
  var hasBody = ['post', 'put', 'patch'].indexOf(methodLower) > -1;

  var op = { summary: apiNm || '', description: apiDesc || '' };

  if (hasBody) {
    var props = {};
    var required = [];
    (params || []).forEach(function (p) {
      props[p.name] = { type: QR_DATTYP_TO_TYPE[p.type] || 'string', description: p.desc || '' };
      if (p.required === 'Y') { required.push(p.name); }
    });
    op.requestBody = { content: { 'application/json': { schema: { type: 'object', properties: props, required: required } } } };
  } else {
    op.parameters = (params || []).map(function (p) {
      return { name: p.name, in: 'query', required: p.required === 'Y', description: p.desc || '', schema: { type: QR_DATTYP_TO_TYPE[p.type] || 'string' } };
    });
  }

  var doc = { openapi: '3.0.3', info: { title: apiNm || '', description: apiDesc || '', version: '1.0' }, paths: {} };
  doc.paths[apiPath || '/'] = {};
  doc.paths[apiPath || '/'][methodLower] = op;

  return YAML.stringify(doc);
}
