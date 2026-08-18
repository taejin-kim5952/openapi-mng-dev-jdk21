package com.kt.openapi.web.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.chat.controller
 * 2. 타입명   : ChatProxyController.java
 * 5. 설명     : AI 챗봇(openapi-chat-serve, FastAPI)을 이 앱과 같은 도메인으로 노출하기 위한 프록시.
 *
 *              챗봇은 별도 프로세스(기본 127.0.0.1:18100)로 뜨는데, 브라우저에서 다른 오리진으로
 *              직접 부르면 CORS/쿠키 문제가 생기고 챗봇 포트를 외부에 열어야 한다. 그래서 이 앱이
 *              /chat/** 요청을 받아 챗봇으로 중계하고, 챗봇 프로세스는 루프백에만 바인딩해 둔다.
 *
 *              경로에 /chat 접두어를 두는 이유: 챗봇의 실제 경로가 /api/ask 처럼 /api로 시작해서
 *              이 앱의 /api/reg, /api/spcreg 같은 기존 매핑과 그대로는 충돌하기 때문이다.
 *              접두어를 떼고 넘긴다.  예) /apidev/chat/api/ask -> http://127.0.0.1:18100/api/ask
 *
 *              접근 제어
 *               - 로그인 필요: SessionCheckInterceptor의 urlList(인증 제외 목록)에 넣지 않아
 *                 기본 동작(미로그인 시 차단)을 그대로 받는다.
 *               - 관리자/스튜디오 경로(/api/admin/**, /api/studio/**)는 ALLOW_PREFIXES에 없어
 *                 404로 막는다. 챗봇 관리 기능은 기존대로 챗봇 포트에 직접 접속해서 쓴다.
 * </pre>
 */
@Controller
@RequestMapping("/chat")
public class ChatProxyController {

    private static final Logger LOG = LoggerFactory.getLogger(ChatProxyController.class);

    /** 역슬래시(U+005C). 경로 우회 검사에 쓴다 - 문자 리터럴은 이스케이프를 놓치기 쉬워 코드값으로 둔다. */
    private static final int BACKSLASH = 92;

    /** 이 앱에서 챗봇으로 중계할 경로. 사용자 챗봇 화면이 실제로 부르는 것만 열어 둔다.
     *  (openapi-chat-serve의 app/static/chat.js API 객체 + 위젯 정적자원) */
    private static final List<String> ALLOW_PREFIXES = List.of(
        "/api/ask",
        "/api/support",
        "/api/categories",
        "/api/feedback",
        "/api/docs/",
        // 답변/문서 렌더러. 관리자 검수 미리보기와 **같은 함수**를 써야 해서 이 앱에 복사하지
        // 않고 챗봇 쪽 파일 한 벌을 그대로 불러온다. 정적자원 중 이것만 연다.
        "/static/markdown.js"
    );

    /** 중계하면 안 되는 요청 헤더. 홉 단위 헤더와 길이/호스트 정보는 RestClient가 다시 만든다.
     *
     *  cookie/authorization도 뺀다. 같은 도메인이라 브라우저가 이 앱의 세션 쿠키(JSESSIONID)를
     *  같이 보내는데, 그대로 넘기면 챗봇 프로세스에 이 앱의 세션 값이 흘러간다. 사용자 챗봇
     *  API는 인증을 쓰지 않으므로(관리자 경로는 애초에 차단) 빼도 동작에 지장이 없다. */
    private static final Set<String> SKIP_REQUEST_HEADERS = Set.of(
        "host", "content-length", "connection", "keep-alive", "transfer-encoding",
        "upgrade", "proxy-authorization", "proxy-authenticate", "te", "trailer",
        "cookie", "authorization"
    );

    /** 중계하면 안 되는 응답 헤더. 챗봇의 세션 쿠키를 이 도메인에 심지 않기 위해 set-cookie도 뺀다. */
    private static final Set<String> SKIP_RESPONSE_HEADERS = Set.of(
        "content-length", "connection", "keep-alive", "transfer-encoding",
        "upgrade", "set-cookie", "server"
    );

    private final RestClient chatClient;

    public ChatProxyController(@Value("${chat.serve.base-url:http://127.0.0.1:18100}") String baseUrl) {
        this.chatClient = RestClient.builder().baseUrl(baseUrl).build();
        LOG.info("### ChatProxyController 초기화 - chat-serve base-url: {}", baseUrl);
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) byte[] body) {

        String path = resolveTargetPath(request);

        if (!isAllowed(path)) {
            LOG.warn("### ChatProxy 차단된 경로 요청: {}", path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        URI uri = UriComponentsBuilder.fromUriString(path)
                .query(request.getQueryString())
                .build(true)
                .toUri();

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        try {
            RestClient.RequestBodySpec spec = chatClient.method(method).uri(uri);
            copyRequestHeaders(request, spec);
            if (body != null && body.length > 0) {
                spec.body(body);
            }

            // retrieve()는 4xx/5xx에서 예외를 던진다. 프록시는 챗봇이 준 상태코드를 그대로
            // 돌려줘야 하므로 exchange()로 응답을 직접 조립한다.
            return spec.exchange((req, res) -> toResponseEntity(res), false);

        } catch (Exception e) {
            // 챗봇 프로세스가 안 떠 있는 경우가 대부분이다. 이 앱 전체가 죽으면 안 되므로 502로만 알린다.
            LOG.error("### ChatProxy 중계 실패 [{} {}]: {}", method, path, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"chat service unavailable\"}".getBytes(StandardCharsets.UTF_8));
        }
    }

    /** 요청 URI에서 컨텍스트패스와 /chat 접두어를 떼어 챗봇 쪽 경로를 만든다. */
    private String resolveTargetPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        String path = uri.substring("/chat".length());
        return path.isEmpty() ? "/" : path;
    }

    /**
     * 중계해도 되는 경로인지 본다.
     *
     * 허용 목록을 접두어로만 비교하면 "/api/docs/../admin/qa" 같은 요청이 통과한 뒤
     * 뒷단(Starlette)에서 정규화되어 차단 대상 경로로 라우팅될 수 있다. 컨테이너의
     * 정규화 동작에 기대지 않고, 상위경로로 읽힐 수 있는 문자열이 있으면 그냥 막는다.
     */
    private boolean isAllowed(String path) {
        String lower = path.toLowerCase();
        if (lower.contains("..") || lower.contains("%2e") || lower.contains("%2f")
                || lower.contains("%5c") || lower.indexOf(BACKSLASH) >= 0) {
            return false;
        }
        for (char c : path.toCharArray()) {
            if (c < 0x20 || c == 0x7f) {   // 개행/제어문자로 헤더를 갈라치는 시도
                return false;
            }
        }
        for (String prefix : ALLOW_PREFIXES) {
            if (path.equals(prefix) || path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void copyRequestHeaders(HttpServletRequest request, RestClient.RequestBodySpec spec) {
        Enumeration<String> names = request.getHeaderNames();
        if (names == null) {
            return;
        }
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (SKIP_REQUEST_HEADERS.contains(name.toLowerCase())) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                spec.header(name, values.nextElement());
            }
        }
    }

    private ResponseEntity<byte[]> toResponseEntity(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse res)
            throws IOException {

        byte[] payload = res.getBody().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        res.getHeaders().forEach((name, values) -> {
            if (!SKIP_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                headers.addAll(name, values);
            }
        });
        applyDefaultCharset(headers);
        applySecurityHeaders(headers);

        return ResponseEntity.status(res.getStatusCode()).headers(headers).body(payload);
    }

    /**
     * 중계 응답에 보호 헤더를 붙인다.
     *
     * 특히 SVG가 문제다. 같은 도메인으로 내려주므로, 주소창으로 SVG를 직접 열면 그 안의
     * script가 이 앱의 오리진에서 실행된다(&lt;img&gt;로 그릴 때는 실행되지 않는다).
     * 문서 이미지는 웹 업로드가 아니라 서버에 직접 넣는 경로라 위험이 낮지만, 한 장만
     * 잘못 들어와도 세션이 뚫리는 자리라 CSP로 막아 둔다.
     */
    private void applySecurityHeaders(HttpHeaders headers) {
        headers.set("X-Content-Type-Options", "nosniff");

        MediaType contentType = headers.getContentType();
        if (contentType != null && "image".equals(contentType.getType())) {
            // 스크립트/외부 리소스/폼을 모두 막고 샌드박스로 격리한다. <img> 렌더링에는 영향이 없다.
            headers.set("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; sandbox");
        }
    }

    /**
     * 텍스트 응답에 charset이 없으면 UTF-8을 붙인다.
     *
     * chat-serve는 정적 파일을 "application/javascript"처럼 charset 없이 내려준다. 그러면
     * 브라우저가 인코딩을 추측하는데, 한국어 윈도우에서는 cp949로 읽어 한글 주석/문자열이
     * 전부 깨진다. 챗봇 소스는 전부 UTF-8이므로 여기서 명시해 준다.
     */
    private void applyDefaultCharset(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType == null || contentType.getCharset() != null) {
            return;
        }
        String type = contentType.toString().toLowerCase();
        boolean isText = type.startsWith("text/")
                || type.contains("javascript")
                || type.contains("json")
                || type.contains("xml");
        if (isText) {
            headers.setContentType(new MediaType(contentType, StandardCharsets.UTF_8));
        }
    }
}
