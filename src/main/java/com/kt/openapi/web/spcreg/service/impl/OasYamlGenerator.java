package com.kt.openapi.web.spcreg.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service.impl
 * 2. 타입명   : OasYamlGenerator.java
 * 5. 설명     : KOA_TB_API_SPC/CTGRY/DEF/PARAM 조회 결과(Map 리스트)를 받아 OAS 3.0 문서 트리를
 *              만들고, 그 트리를 Swagger 2.0으로 다운컨버트한다. DB/트랜잭션을 모르는 순수 변환기라
 *              단위 테스트가 쉽고, 조회는 호출부(ApiSpcYamlSyncService)가 담당한다.
 *
 *              2.0을 독립 생성하지 않고 3.0 트리에서 변환하는 이유는 두 문서의 내용이 어긋날 여지를
 *              없애기 위함이다. 매핑 규칙 전문은 docs/04_OAS_GENERATOR_MAPPING.md.
 *
 *              [주의] 타입 매핑과 스키마 분기 규칙은 화면측 paramTreeEditor.js(ptSchemaOf/ptObjSchema/
 *              ptParamsSchema)와 같은 로직이다. 한쪽만 고치면 화면 미리보기와 저장된 YAML이 달라진다.
 * </pre>
 */
@Component
public class OasYamlGenerator {

    /** DATTYP1000 공통코드 -> OAS type. paramTreeEditor.js PT_TYPE_JS와 동일해야 한다. */
    private static final Map<String, String> DATA_TYPE_TO_OAS = Map.of(
            "DATTYP1010", "string",
            "DATTYP1020", "number",
            "DATTYP1030", "integer",
            "DATTYP1040", "boolean",
            "DATTYP1050", "object",
            "DATTYP1060", "array"
    );

    private static final String TYPE_OBJECT = "DATTYP1050";
    private static final String TYPE_ARRAY  = "DATTYP1060";

    /** 입력/Query 파라미터 */
    private static final String PRM_IN  = "PRMTYP1010";
    /** 출력 파라미터 */
    private static final String PRM_OUT = "PRMTYP1020";

    private static final String LOC_QUERY = "query";

    /** API_PATH의 {id} 같은 path 변수 토큰 */
    private static final Pattern PATH_VAR = Pattern.compile("\\{([^}/]+)}");

    private final ObjectMapper yamlMapper;

    public OasYamlGenerator() {
        YAMLFactory f = new YAMLFactory()
                // 문서 선두의 "---" 제거. 기존 YAML_SBST 값들과 형태를 맞춘다.
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                // MINIMIZE_QUOTES만 켜면 swagger: 2.0 / version: 1.0 이 따옴표 없이 나가고, 이는
                // YAML 스칼라 규칙상 float으로 파싱된다. Swagger 2.0 파서는 swagger를 문자열
                // "2.0"으로 기대하므로 문서가 무효해진다. 숫자처럼 생긴 문자열은 항상 인용한다.
                .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);
        this.yamlMapper = new ObjectMapper(f);
    }

    // =========================================================================
    // public
    // =========================================================================

    /**
     * OAS 3.0 문서 트리를 만든다.
     *
     * @param spc      selSpcForOas 결과
     * @param ctgryList selCtgryListForOas 결과
     * @param defList  selDefListForOas 결과 (methodNm은 소문자 HTTP 동사)
     * @param paramsByApiNo apiNo -> 그 API의 파라미터 행 목록
     */
    public Map<String, Object> buildOas3(Map<String, Object> spc,
                                         List<Map<String, Object>> ctgryList,
                                         List<Map<String, Object>> defList,
                                         Map<String, List<Map<String, Object>>> paramsByApiNo) {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("openapi", "3.0.3");
        doc.put("info", buildInfo(spc));

        List<Map<String, Object>> servers = new ArrayList<>();
        Map<String, Object> server = new LinkedHashMap<>();
        // DB에 http/https 구분 컬럼이 없어 https 고정 (docs/04_OAS_GENERATOR_MAPPING.md §6-1)
        server.put("url", "https://" + str(spc.get("host")) + str(spc.get("basPath")));
        servers.add(server);
        doc.put("servers", servers);

        List<Map<String, Object>> tags = buildTags(ctgryList);
        if (!tags.isEmpty()) {
            doc.put("tags", tags);
        }

        doc.put("paths", buildPaths(defList, paramsByApiNo));
        return doc;
    }

    /** OAS 3.0 문서 트리를 Swagger 2.0으로 다운컨버트한다. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> downgradeToSwagger2(Map<String, Object> oas3) {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("swagger", "2.0");
        doc.put("info", oas3.get("info"));

        // servers[0].url -> host + basePath + schemes
        String url = "";
        List<Map<String, Object>> servers = (List<Map<String, Object>>) oas3.get("servers");
        if (servers != null && !servers.isEmpty()) {
            url = str(servers.get(0).get("url"));
        }
        String scheme = "https";
        String rest = url;
        int sep = url.indexOf("://");
        if (sep > 0) {
            scheme = url.substring(0, sep);
            rest = url.substring(sep + 3);
        }
        int slash = rest.indexOf('/');
        doc.put("host", slash >= 0 ? rest.substring(0, slash) : rest);
        doc.put("basePath", slash >= 0 ? rest.substring(slash) : "/");
        doc.put("schemes", List.of(scheme));
        // 3.0은 미디어타입이 content 키에 있지만 2.0은 문서/오퍼레이션 레벨 선언이 필요하다.
        doc.put("consumes", List.of("application/json"));
        doc.put("produces", List.of("application/json"));

        if (oas3.containsKey("tags")) {
            doc.put("tags", oas3.get("tags"));
        }

        Map<String, Object> paths3 = (Map<String, Object>) oas3.get("paths");
        Map<String, Object> paths2 = new LinkedHashMap<>();
        if (paths3 != null) {
            for (Map.Entry<String, Object> pe : paths3.entrySet()) {
                Map<String, Object> ops3 = (Map<String, Object>) pe.getValue();
                Map<String, Object> ops2 = new LinkedHashMap<>();
                for (Map.Entry<String, Object> oe : ops3.entrySet()) {
                    ops2.put(oe.getKey(), downgradeOperation((Map<String, Object>) oe.getValue()));
                }
                paths2.put(pe.getKey(), ops2);
            }
        }
        doc.put("paths", paths2);
        return doc;
    }

    /** 문서 트리를 YAML 문자열로 직렬화한다. */
    public String toYaml(Map<String, Object> doc) {
        try {
            return yamlMapper.writeValueAsString(doc);
        } catch (Exception e) {
            throw new IllegalStateException("OAS YAML 직렬화 실패", e);
        }
    }

    // =========================================================================
    // OAS 3.0 조립
    // =========================================================================

    private Map<String, Object> buildInfo(Map<String, Object> spc) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", str(spc.get("apiNm")));
        String desc = str(spc.get("apiDesc"));
        if (!desc.isEmpty()) {
            info.put("description", desc);
        }
        info.put("version", str(spc.get("ver")));
        // 역추적용 확장 필드 - 이 YAML이 어느 명세에서 생성됐는지 문서만 보고 알 수 있게 한다.
        info.put("x-apiSpcNo", str(spc.get("apiSpcNo")));
        info.put("x-sysId", str(spc.get("sysId")));
        return info;
    }

    private List<Map<String, Object>> buildTags(List<Map<String, Object>> ctgryList) {
        List<Map<String, Object>> tags = new ArrayList<>();
        if (ctgryList == null) {
            return tags;
        }
        // 자동 생성된 API그룹 하나뿐이면 의미 있는 분류가 아니므로 tags를 만들지 않는다.
        // ("기본"은 예전 기본값 - 그때 만든 그룹도 같이 걸러준다)
        String only = ctgryList.size() == 1 ? str(ctgryList.get(0).get("ctgryNm")) : "";
        if ("v1.0".equals(only) || "기본".equals(only)) {
            return tags;
        }
        for (Map<String, Object> c : ctgryList) {
            String nm = str(c.get("ctgryNm"));
            if (nm.isEmpty()) {
                continue;
            }
            Map<String, Object> t = new LinkedHashMap<>();
            t.put("name", nm);
            String d = str(c.get("ctgryDesc"));
            if (!d.isEmpty()) {
                t.put("description", d);
            }
            tags.add(t);
        }
        return tags;
    }

    private Map<String, Object> buildPaths(List<Map<String, Object>> defList,
                                           Map<String, List<Map<String, Object>>> paramsByApiNo) {
        Map<String, Object> paths = new LinkedHashMap<>();
        if (defList == null) {
            return paths;
        }
        for (Map<String, Object> def : defList) {
            String path = str(def.get("apiPath"));
            String method = str(def.get("methodNm"));
            if (path.isEmpty() || method.isEmpty()) {
                // METHOD_CD가 MTHTYP1000에 없는 유령 코드면 조인 결과가 비어 여기 걸린다.
                // 문서 전체를 실패시키는 대신 해당 Operation만 건너뛴다.
                continue;
            }
            String apiNo = str(def.get("apiNo"));
            List<Map<String, Object>> rows = paramsByApiNo.getOrDefault(apiNo, List.of());

            @SuppressWarnings("unchecked")
            Map<String, Object> ops = (Map<String, Object>) paths.computeIfAbsent(path, k -> new LinkedHashMap<String, Object>());
            ops.put(method, buildOperation(def, path, rows));
        }
        return paths;
    }

    private Map<String, Object> buildOperation(Map<String, Object> def, String path, List<Map<String, Object>> rows) {
        Map<String, Object> op = new LinkedHashMap<>();

        String ctgryNm = str(def.get("ctgryNm"));
        if (!ctgryNm.isEmpty()) {
            op.put("tags", List.of(ctgryNm));
        }
        op.put("summary", str(def.get("apiNm")));
        String desc = str(def.get("apiDesc"));
        if (!desc.isEmpty()) {
            op.put("description", desc);
        }
        op.put("operationId", resolveOperationId(def, path));
        if ("N".equalsIgnoreCase(str(def.get("useYn")))) {
            // 비노출 API도 문서에는 남긴다 - 빼버리면 구버전 화면에서 API가 통째로 사라져 보인다.
            op.put("deprecated", true);
        }

        List<Map<String, Object>> parameters = new ArrayList<>();
        parameters.addAll(buildPathParameters(path));
        parameters.addAll(buildQueryParameters(rows));
        if (!parameters.isEmpty()) {
            op.put("parameters", parameters);
        }

        Map<String, Object> requestBody = buildRequestBody(rows);
        if (requestBody != null) {
            op.put("requestBody", requestBody);
        }

        op.put("responses", buildResponses(rows));

        String apiVer = str(def.get("apiVer"));
        if (!apiVer.isEmpty()) {
            op.put("x-apiVer", apiVer);
        }
        op.put("x-apiNo", str(def.get("apiNo")));
        return op;
    }

    private String resolveOperationId(Map<String, Object> def, String path) {
        String apiId = str(def.get("apiId"));
        if (!apiId.isEmpty()) {
            return apiId;
        }
        // API_ID가 비어 있으면 method_path 형태로 합성한다(OAS에서 operationId는 문서 내 유일해야 함).
        String normalized = path.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return str(def.get("methodNm")) + "_" + normalized;
    }

    /**
     * API_PATH의 {id} 토큰에서 path 파라미터를 만든다.
     *
     * 신규 등록 화면에 path 파라미터를 저장하는 스코프가 없어(PT_SCOPES = in/query/out) DB에 값이
     * 없다. 그런데 경로에 {id}가 있는데 대응 파라미터 정의가 없으면 OAS 2.0/3.0 모두 유효하지 않은
     * 문서가 되므로, 경로 토큰에서 역으로 만들어 채운다. 타입 정보가 없으므로 string 고정.
     * (docs/04_OAS_GENERATOR_MAPPING.md §6-3)
     */
    private List<Map<String, Object>> buildPathParameters(String path) {
        List<Map<String, Object>> out = new ArrayList<>();
        Matcher m = PATH_VAR.matcher(path);
        while (m.find()) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("name", m.group(1));
            p.put("in", "path");
            p.put("required", true);
            p.put("schema", Map.of("type", "string"));
            out.add(p);
        }
        return out;
    }

    private List<Map<String, Object>> buildQueryParameters(List<Map<String, Object>> rows) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            if (!PRM_IN.equals(str(r.get("paramTypeCd"))) || !LOC_QUERY.equals(str(r.get("paramLoc")))) {
                continue;
            }
            // Query 파라미터는 평면 구조만 쓴다(자식이 있는 행은 무시).
            if (!str(r.get("prntsParamNo")).isEmpty()) {
                continue;
            }
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("name", str(r.get("paramNm")));
            p.put("in", "query");
            String desc = str(r.get("paramDesc"));
            if (!desc.isEmpty()) {
                p.put("description", desc);
            }
            p.put("required", "Y".equalsIgnoreCase(str(r.get("required"))));

            Map<String, Object> schema = new LinkedHashMap<>();
            schema.put("type", oasType(str(r.get("dataTypeCd"))));
            Object example = exampleValue(r);
            if (example != null) {
                schema.put("example", example);
            }
            p.put("schema", schema);

            String pii = str(r.get("personalData"));
            if (!pii.isEmpty()) {
                p.put("x-personalData", pii);
            }
            out.add(p);
        }
        return out;
    }

    private Map<String, Object> buildRequestBody(List<Map<String, Object>> rows) {
        List<Map<String, Object>> roots = rootsOf(rows, PRM_IN, false);
        if (roots.isEmpty()) {
            return null;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("required", true);
        body.put("content", Map.of("application/json",
                Map.of("schema", objectSchema(roots, rows))));
        return body;
    }

    private Map<String, Object> buildResponses(List<Map<String, Object>> rows) {
        List<Map<String, Object>> roots = rootsOf(rows, PRM_OUT, true);
        Map<String, Object> responses = new LinkedHashMap<>();

        if (roots.isEmpty()) {
            // 출력 파라미터가 하나도 없어도 responses는 필수다.
            responses.put("200", Map.of("description", "OK"));
            return responses;
        }

        // RES_CD 기준으로 응답을 나눈다. 비어 있으면 200으로 본다.
        Map<String, List<Map<String, Object>>> byCode = new LinkedHashMap<>();
        Map<String, String> descByCode = new LinkedHashMap<>();
        for (Map<String, Object> r : roots) {
            String code = str(r.get("resCd"));
            if (code.isEmpty()) {
                code = "200";
            }
            byCode.computeIfAbsent(code, k -> new ArrayList<>()).add(r);
            String d = str(r.get("resDesc"));
            if (!d.isEmpty()) {
                descByCode.putIfAbsent(code, d);
            }
        }

        for (Map.Entry<String, List<Map<String, Object>>> e : byCode.entrySet()) {
            Map<String, Object> res = new LinkedHashMap<>();
            // 2.0에서 description은 필수라 비면 문서가 무효가 된다.
            res.put("description", descByCode.getOrDefault(e.getKey(), "OK"));
            res.put("content", Map.of("application/json",
                    Map.of("schema", objectSchema(e.getValue(), rows))));
            responses.put(e.getKey(), res);
        }
        return responses;
    }

    // =========================================================================
    // 파라미터 트리 -> 스키마
    // =========================================================================

    /**
     * 지정 스코프의 루트 행(부모 없음)을 순서대로 뽑는다.
     * paramTreeEditor.js ptBuildTree()와 동일하게 OBJ_ODRG 우선 정렬은 SQL에서 이미 끝나 있다.
     */
    private List<Map<String, Object>> rootsOf(List<Map<String, Object>> rows, String paramTypeCd, boolean includeQueryLoc) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            if (!paramTypeCd.equals(str(r.get("paramTypeCd")))) {
                continue;
            }
            if (!includeQueryLoc && LOC_QUERY.equals(str(r.get("paramLoc")))) {
                continue;
            }
            if (!str(r.get("prntsParamNo")).isEmpty()) {
                continue;
            }
            out.add(r);
        }
        return out;
    }

    private List<Map<String, Object>> childrenOf(List<Map<String, Object>> rows, String parentParamNo) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            if (parentParamNo.equals(str(r.get("prntsParamNo")))) {
                out.add(r);
            }
        }
        return out;
    }

    /** 노드 목록을 object 스키마(properties/required)로 만든다. */
    private Map<String, Object> objectSchema(List<Map<String, Object>> nodes, List<Map<String, Object>> allRows) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        List<String> required = new ArrayList<>();
        for (Map<String, Object> n : nodes) {
            if ("Y".equalsIgnoreCase(str(n.get("required")))) {
                required.add(str(n.get("paramNm")));
            }
        }
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        Map<String, Object> props = new LinkedHashMap<>();
        for (Map<String, Object> n : nodes) {
            props.put(str(n.get("paramNm")), schemaOf(n, allRows));
        }
        schema.put("properties", props);
        return schema;
    }

    /** 단일 노드 -> 스키마. paramTreeEditor.js ptSchemaOf()와 같은 분기. */
    private Map<String, Object> schemaOf(Map<String, Object> node, List<Map<String, Object>> allRows) {
        String dataTypeCd = str(node.get("dataTypeCd"));
        List<Map<String, Object>> kids = childrenOf(allRows, str(node.get("paramNo")));

        Map<String, Object> schema;
        if (TYPE_OBJECT.equals(dataTypeCd)) {
            schema = objectSchema(kids, allRows);
        } else if (TYPE_ARRAY.equals(dataTypeCd)) {
            schema = new LinkedHashMap<>();
            schema.put("type", "array");
            // 배열 요소 타입을 담는 컬럼이 없다. 자식이 있으면 object, 없으면 string으로 추론한다 -
            // ptBuildTree()의 추론과 동일. array of integer 등은 저장 시점에 이미 소실되어 복원 불가.
            // (docs/04_OAS_GENERATOR_MAPPING.md §6-2)
            schema.put("items", kids.isEmpty() ? new LinkedHashMap<>(Map.of("type", "string"))
                                               : objectSchema(kids, allRows));
        } else {
            schema = new LinkedHashMap<>();
            schema.put("type", oasType(dataTypeCd));
        }

        String desc = str(node.get("paramDesc"));
        if (!desc.isEmpty()) {
            schema.put("description", desc);
        }
        Object example = exampleValue(node);
        if (example != null) {
            schema.put("example", example);
        }
        String pii = str(node.get("personalData"));
        if (!pii.isEmpty()) {
            schema.put("x-personalData", pii);
        }
        return schema;
    }

    // =========================================================================
    // 3.0 -> 2.0 다운컨버트
    // =========================================================================

    @SuppressWarnings("unchecked")
    private Map<String, Object> downgradeOperation(Map<String, Object> op3) {
        Map<String, Object> op2 = new LinkedHashMap<>();
        copyIfPresent(op3, op2, "tags", "summary", "description", "operationId");

        List<Map<String, Object>> params2 = new ArrayList<>();
        List<Map<String, Object>> params3 = (List<Map<String, Object>>) op3.get("parameters");
        if (params3 != null) {
            for (Map<String, Object> p3 : params3) {
                params2.add(downgradeParameter(p3));
            }
        }
        // requestBody -> parameters[in: body]
        Map<String, Object> reqBody = (Map<String, Object>) op3.get("requestBody");
        if (reqBody != null) {
            Map<String, Object> bodyParam = new LinkedHashMap<>();
            bodyParam.put("name", "body");
            bodyParam.put("in", "body");
            bodyParam.put("required", reqBody.getOrDefault("required", true));
            bodyParam.put("schema", jsonSchemaOf(reqBody));
            params2.add(bodyParam);
        }
        if (!params2.isEmpty()) {
            op2.put("parameters", params2);
        }

        Map<String, Object> res3 = (Map<String, Object>) op3.get("responses");
        Map<String, Object> res2 = new LinkedHashMap<>();
        if (res3 != null) {
            for (Map.Entry<String, Object> e : res3.entrySet()) {
                Map<String, Object> r3 = (Map<String, Object>) e.getValue();
                Map<String, Object> r2 = new LinkedHashMap<>();
                r2.put("description", r3.getOrDefault("description", "OK"));
                Object schema = jsonSchemaOf(r3);
                if (schema != null) {
                    r2.put("schema", schema);
                }
                res2.put(e.getKey(), r2);
            }
        }
        op2.put("responses", res2);

        if (Boolean.TRUE.equals(op3.get("deprecated"))) {
            op2.put("deprecated", true);
        }
        copyIfPresent(op3, op2, "x-apiVer", "x-apiNo");
        return op2;
    }

    /** 3.0 파라미터의 schema 래핑을 풀어 2.0 형태로 평탄화한다. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> downgradeParameter(Map<String, Object> p3) {
        Map<String, Object> p2 = new LinkedHashMap<>();
        copyIfPresent(p3, p2, "name", "in", "description", "required");

        Map<String, Object> schema = (Map<String, Object>) p3.get("schema");
        if (schema != null) {
            if (schema.containsKey("type")) {
                p2.put("type", schema.get("type"));
            }
            if (schema.containsKey("example")) {
                // 2.0 파라미터 객체에는 example이 없다 - 확장 필드로 보존한다.
                p2.put("x-example", schema.get("example"));
            }
        }
        copyIfPresent(p3, p2, "x-personalData");
        return p2;
    }

    /** {content: {application/json: {schema: ...}}} 에서 schema만 꺼낸다. */
    @SuppressWarnings("unchecked")
    private Object jsonSchemaOf(Map<String, Object> holder) {
        Map<String, Object> content = (Map<String, Object>) holder.get("content");
        if (content == null) {
            return null;
        }
        Map<String, Object> media = (Map<String, Object>) content.get("application/json");
        return media == null ? null : media.get("schema");
    }

    // =========================================================================
    // helper
    // =========================================================================

    private void copyIfPresent(Map<String, Object> from, Map<String, Object> to, String... keys) {
        for (String k : keys) {
            if (from.containsKey(k)) {
                to.put(k, from.get(k));
            }
        }
    }

    private String oasType(String dataTypeCd) {
        return DATA_TYPE_TO_OAS.getOrDefault(dataTypeCd, "string");
    }

    /** EXAM은 문자열 컬럼이라 숫자 타입일 때만 숫자로 되돌린다(따옴표 붙은 예시 방지). */
    private Object exampleValue(Map<String, Object> row) {
        String exam = str(row.get("exam"));
        if (exam.isEmpty()) {
            return null;
        }
        String dataTypeCd = str(row.get("dataTypeCd"));
        try {
            // [주의] 삼항연산자로 Long/Double을 한 식에 두면 자바 이항 수치 승격이 일어나 정수도
            // double(1 -> 1.0)이 된다. 반드시 분기해서 반환할 것.
            if ("DATTYP1030".equals(dataTypeCd)) {
                return Long.valueOf(exam.trim());
            }
            if ("DATTYP1020".equals(dataTypeCd)) {
                return Double.valueOf(exam.trim());
            }
        } catch (NumberFormatException e) {
            // 숫자 타입인데 예시가 숫자가 아니면 원문 그대로 둔다.
            return exam;
        }
        return exam;
    }

    private String str(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
