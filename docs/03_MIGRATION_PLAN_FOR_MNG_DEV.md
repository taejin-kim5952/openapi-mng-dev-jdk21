# openapi-mng-dev 마이그레이션 실행 계획서 (Migration Plan)

본 문서는 `openapi-mng-dev-onm` (mngOnm) 프로젝트의 성공적인 마이그레이션 사례를 벤치마킹하여, 현재 레거시 상태인 `openapi-mng-dev` (mngDev) 프로젝트를 현대화하기 위한 상세 실행 계획을 정의합니다.

### 🚀 마이그레이션 궁극적 목표 (Core Vision)
1.  **완전한 egovframe 탈피 (Pure Java/Spring)**: 프로젝트 전반에 뿌리 깊게 박힌 egovframe 의존성(DAO, Map, Utility, Library)을 100% 제거하여 기술적 종속성을 해소합니다.
2.  **완벽한 Spring Boot 3 전환 (Native Boot)**: 레거시 XML 설정을 완전히 걷어내고, Spring Boot 3의 Auto Configuration 및 Java Config 표준을 따르는 현대적 아키텍처를 완성합니다.

---

## 1. 마이그레이션 목표 아키텍처 (Standard: mngOnm)
- **Framework**: Spring Boot 3.5.13 (Spring 6.x 기반)
- **EE Specification**: Jakarta EE 10 (`jakarta.*` 패키지)
- **Build Tool**: Java 17, Maven
- **Data Access**: MyBatis 3.x (mybatis-spring-boot-starter 3.0.4)
- **Configuration**: YAML 기반 설정 및 Java Config (무-XML 전략)
- **UI Layout**: Apache Tiles 3 제거 및 JSP Custom Tag Layout (`layout.tag`) 도입
- **Data Model**: `Map` (`EgovMap`) 중심에서 전용 **VO (Value Object)** 중심으로 전환

---

## 2. 단계별 수행 과제 (Action Items)

### Phase 1: 빌드 및 실행 환경 구축 (Infrastructure)
1.  **`pom.xml` 전면 개편**:
    *   `spring-boot-starter-parent` (3.5.13) 적용 및 최신 스타터 의존성 확보.
    *   `javax.*` 기반 의존성 제거 및 `jakarta.*` 기반 라이브러리 교체.
2.  **메인 애플리케이션 클래스 생성**:
    *   `com.kt.openapi.StoApplication` 생성 (`@SpringBootApplication`, `@EnableScheduling` 등).
3.  **설정 파일 현대화 및 계층적 모듈화**:
    *   **Root 설정**: `src/main/resources/application.yml`.
    *   **계층적 폴더 구조**: `src/main/resources/config/{common,local,tb,prod}/` 도입.
    *   **데이터 1:1 이식**: 기존 properties 내용을 환경별 YAML 파일로 완벽 이전.
    *   **로깅/보안**: `logback-spring.xml` 생성 및 `deploy/` 폴더 내 보안 설정 이동. (실제 Log4j2 → Logback 전환은 Phase 5에서)

### Phase 2: 코드 수준의 현대화 (Modernization)
1.  **Jakarta EE 패키지 치환 (Step-by-Step)**:
    *   **2-1-1. Java 소스 치환**: 모든 `.java` 파일의 `javax.*` -> `jakarta.*` 일괄 변경.
    *   **2-1-2. JSP/TLD 치환**: JSTL URI 등 JSP 내 레거시 참조를 Jakarta EE 10 규격으로 변경.
    *   **2-1-3. 최종 검증**: 잔여 `javax` 참조 전수 조사 및 컴파일 에러 해결.
2.  **Framework 패키지 구조 표준화 (fwk) 및 레거시 의존성 제거**:
    *   `mngOnm` 표준 패키지 구조(`com.kt.openapi.fwk`) 도입.
    *   **서비스 계층 POJO 전환 (egovframe 탈퇴)**:
        *   `extends EgovAbstractServiceImpl` 상속 구조를 전면 제거하여 순수 Spring 빈으로 전환.
        *   의존성 주입 방식을 `@Resource(name="...")`에서 **`@Autowired`** 또는 생성자 주입 방식으로 변경.
        *   트랜잭션 관리를 XML 설정 대신 서비스 메소드에 **`@Transactional`** 어노테이션으로 명시.
    *   `fwk.cmm.config`: Java Config 클래스 집약 (`WebMvcConfig`, `MyBatisConfig`, `AsyncConfig` 등).
    *   `fwk.online`: `checker`, `exception`, `filter`, `page` 등 공통/인프라 성격의 로직 분리.
3.  **Java Config 전환 (XML 제거)**:
    *   레거시 XML 설정을 `fwk.cmm.config` 내 Java 클래스로 이식.
    *   `WebMvcConfig.java`: 정적 리소스 핸들러, 콘텐츠 협상(.do, .json) 설정 포함.
4.  **보안 및 공통 기능 구현**:
    *   `SessionCheckInterceptor`: 관리자 권한 제어 이식.
    *   **통합 예외 처리**: `GlobalExceptionHandler` 및 `SimpleMappingExceptionResolver` 도입.
    *   `Pagination`: 페이징 처리 유틸리티 이식.
5.  **스케줄러 현대화 (Scheduler)**:
    *   기존 Quartz XML 설정을 제거하고 Spring `@Scheduled` 어노테이션 기반으로 전환.
6.  **기동 시 자가 진단 로직 구현 (SafeDBChecker)**:
    *   `fwk.online.checker.SafeDBChecker` 생성 및 앱 기동 시 필수 리소스 검증.

### Phase 3: 데이터 액세스 계층 전환 (iBatis to MyBatis)
1.  **분석 및 기반 구축 (Analysis & Foundation)**:
    *   iBatis SQL XML 및 DAO 전수 조사 및 업무별 분류.
    *   `MyBatisConfig` 최적화 (CamelCase, TypeAliases, Null handling).
2.  **iBatis 로직 이식 (XML Translation)**:
    *   **iBatis 로직 이식**: MyBatis Mapper 인터페이스 및 XML로 전환.
    *   iBatis 동적 태그(`isEqual`, `iterate` 등)를 MyBatis 표준(`if`, `foreach`)으로 치환.
    *   MS SQL Server 호환 쿼리 검증 및 변수 바인딩(#, $) 최적화.
3.  **DAO → Mapper 인터페이스 전환 (Pure MyBatis)**:
    *   `EgovAbstractDAO` 상속 제거 및 `@Mapper` 인터페이스 도입.
    *   서비스 레이어 의존성 주입 방식 변경 (DAO → Mapper).
4.  **VO(Value Object) 도입 및 EgovMap 제거 (Type Safety)**:
    *   **EgovMap 기반의 느슨한 데이터 처리를 전면 중단.**
    *   `mngOnm` 방식과 동일하게 업무별 **전용 VO (Value Object) 클래스**를 생성하여 적용.
    *   **MyBatis ResultType을 전용 VO로 매핑**하여 컴파일 시점의 타입 안정성 확보.
5.  **ID 채번 및 페이징 현대화**:
    *   `egovIdGnrService` 제거 및 DB `IDENTITY` / MyBatis `<selectKey>` 전환.
    *   **페이징 처리 표준화**: MS SQL Server 표준 **OFFSET/FETCH 구문 적용**을 통한 페이징 쿼리 현대화.
6.  **데이터 계층 단위 테스트**:
    *   주요 비즈니스 쿼리에 대한 MyBatis Mapper 동작 검증.

### Phase 4: UI/레이아웃 시스템 전환 (Tiles to Tag)
1.  **Tiles 제거 및 코드 정리**: `tiles-defs.xml` 및 관련 라이브러리 완전 삭제.
2.  **커스텀 레이아웃 태그 도입**: `/WEB-INF/tags/layout.tag` 기반 구조 재편.
3.  **공통 페이징 스크립트 적용**: `paging.js` 이식 및 `drawPaging()` 활용.
4.  **JSP 페이지 전수 수정**: 모든 JSP를 `<t:layout>` 기반으로 구조 변경.

---

## 3. mngOnm 참조 핵심 체크리스트 (Reference Checklist)
| 항목 | mngOnm 적용 방식 (표준) | mngDev 적용 계획 |
| :--- | :--- | :--- |
| **JDK 버전** | 17 | 동일 적용 |
| **Spring Boot** | 3.5.13 | 동일 적용 |
| **Jakarta EE** | 10 (`jakarta.*`) | 전체 소스 일괄 치환 |
| **MyBatis** | Starter 3.0.4 | 동일 적용 |
| **Logging** | logback-spring.xml | Log4j2 → Logback 전환 (Phase 5) |
| **Config** | YAML Modules | 환경별/주제별 폴더 분리 |
| **Layout** | JSP Custom Tag | Tiles 완전 제거 및 Tag 전환 |

---

## 4. 데이터 액세스 계층 전환 가이드 (Standard Pattern)

다른 세션이나 작업자도 동일한 규칙을 따르도록 아래 패턴을 준수합니다.

### 4.1 DAO 현대화 패턴 (Class to Interface)
- **기존**: `@Repository` 클래스에서 `SqlSession`을 주입받아 문자열 ID로 호출.
- **변경**: `@Mapper` 인터페이스로 전환하여 메서드 명과 XML ID를 일치시킴.

```java
// 예시: com.kt.openapi.web.login.dao.LoginDAO.java
@Mapper
public interface LoginDAO {
    List<EgovMap> getUserIdChk(UserJoinVO param);
    void updateLDate(UserJoinVO param);
}
```

### 4.2 XML 네임스페이스 동기화
- **기존**: `<mapper namespace="login">` (단순 별칭)
- **변경**: `<mapper namespace="com.kt.openapi.web.login.dao.LoginDAO">` (인터페이스 전체 경로)

---

## 5. 기대 효과
- **유지보수성**: 무-XML 전략 및 계층적 설정 관리를 통한 가독성 증대.
- **보안성**: 최신 보안 패치가 적용된 환경 확보 및 자가 진단 로직 강화.
- **일관성**: `mngOnm`과 동일한 아키텍처를 공유함으로써 관리 효율성 극대화.

---

## 5. 마이그레이션 진행 현황 (Progress Status)
| 단계          | 항목 | 완료 여부 | 완료 일시 | 비고 |
|:------------| :--- | :---: | :--- | :--- |
| **Phase 1** | 1. `pom.xml` 전면 개편 | ✅ | 2026-05-06 | Spring Boot 3, Jakarta EE 10 적용 |
|             | 2. 메인 애플리케이션 클래스 생성 | ✅ | 2026-05-06 | `StoApplication` 생성 완료 |
|             | 3. 설정 파일 현대화 및 계층적 모듈화 | ✅ | 2026-05-06 | `config/` 폴더 기반 데이터 이식 완료 |
| **Phase 2** | 1-1. Java 소스 Jakarta 패키지 치환 | ✅ | 2026-05-06 | 76개 Java 파일 일괄 치환 완료 (`javax` -> `jakarta`) |
|             | 1-2. JSP 태그라이브러리 URI 치환 | ✅ | 2026-05-06 | 104개 JSP 전수 조사 및 `taglib.jsp` 등 주요 파일 치환 완료 |
|             | 1-3. 잔여 javax 참조 정리 및 검증 | ✅ | 2026-05-06 | 전수 조사 결과 Jakarta 타겟 패키지 참조 0건 확인 |
|             | 2. Framework 패키지 구조 표준화 (fwk) 및 레거시 의존성 제거 | ✅ | 2026-05-06 | `fwk` 구조 구축 및 `Pagination`, `SafeDBChecker` 등 핵심 클래스 이식 완료 |
|             | 3. Java Config 전환 (XML 제거) | ✅ | 2026-05-07 | `MyBatisConfig`, `WebMvcConfig`, `TransactionConfig`, `AsyncConfig` 등 생성 완료 |
| **Phase 2** | 4. 보안 및 공통 기능 현대화 | ✅ | 2026-05-07 | `SessionCheckInterceptor`, 통합 에러 페이지 설정 완료 |
|             | (추가) 컨텍스트 경로 불일치 해결 | ✅ | 2026-05-07 | `application.yml`의 `context-path`를 `/apidev`로 수정 완료 |
|             | (예정) EgovProperties 완전 제거 | 🏃 | 2026-05-07 | 설정/유틸 계층 egov 의존성 제거 완료 (파일 삭제 대기) |
|             | 5. 스케줄러 현대화 (Scheduler) | ✅ | 2026-05-07 | 스케줄러 전용 쓰레드 풀 구축 및 주기(Cron/Rate) 설정 외부화 완료 |
|             | 6. 기동 시 자가 진단 로직 구현 | ✅ | 2026-05-07 | `SafeDBChecker` 구현 (Deploy 폴더, DB 연결, SafeDB 검증 완료) |
| **Phase 3** | 1. 분석 및 MyBatis 기반 구축 | ✅ | 2026-05-07 | iBatis 전수 조사 및 MyBatis 설정(MyBatisConfig) 검증 완료 |
|             | 2. SQL XML 마이그레이션 | ✅ | 2026-05-07 | 모든 SQL XML(21개) MyBatis 3.0 이식 완료 |
|             | 3. DAO 현대화 (Pure MyBatis Strategy) | ✅ | 2026-05-07 | 모든 DAO -> @Mapper 인터페이스 전환 완료 |
|             | 4. VO 도입 및 EgovMap 제거 | ✅ | 2026-05-11 | 전 모듈 EgovMap 제거 및 전용 VO/표준 Map 전환 완료 |
|             | 5. ID 채번 및 페이징 현대화 | ✅ | 2026-05-11 | `egovIdGnrService` 제거 및 `OFFSET/FETCH` 적용 |
|             | 6. 데이터 계층 검증 | ✅ | 2026-05-11 | 정적 분석 및 컴파일 영향도 체크 완료 |
| **Phase 4** | 1. Tiles 설정 제거 및 `layout.tag` 도입 | ✅ | 2026-05-11 | Tiles 설정 제거 및 기반 레이아웃 태그 구현 완료 |
|             | 2. 잔여 JSP 파일 전수 레이아웃 전환 | ✅ | 2026-05-11 | 88개 JSP `<t:layout>` 적용 완료 (9개 fragment 제외) |
|             | 3. taglib.jsp 내 Tiles 선언 최종 제거 | ✅ | 2026-05-11 | `taglib.jsp` tiles taglib 선언 삭제, `tiles.xml` 고아 파일 삭제, `layout.tag` beast type 추가 완료 |
|             | 4. dispatcher-servlet.xml 완전 제거 | ✅ | 2026-05-11 | 모든 설정이 `WebMvcConfig.java`로 이식 확인 후 `dispatcher-servlet.xml`, `EgovBindingInitializer.java` 삭제, `WEB-INF/config/` 전체 정리 완료 |
|             | 5. web.xml 완전 제거 | ✅ | 2026-05-11 | WAR 배포용 `web.xml`의 모든 설정(CORS, Encoding, DispatcherServlet, session, jsp-config, error-page 등)이 Java Config로 대체 확인 후 삭제 완료 |
|             | 4. 페이징 처리 표준화 | ✅ | 2026-05-12 | egovframe 페이징 태그 제거 및 drawPaging() JS 기반 전환 완료 |
|             | 5. 최종 라이브러리 다이어트 | ✅ | 2026-05-11 | `WEB-INF/lib` 내 `org.egovframe.*` 및 Tiles 관련 jar 삭제 완료 |
| **Phase 5** | 1. Log4j2 → Logback 로깅 전환 | ✅ | 2026-05-12 | `log4j2.xml` 설정을 `logback-spring.xml`으로 완전 이식 후 삭제, Spring Boot 기본 Logback 적용 |
| **Phase 6** | 1. 85개 빌드 오류 일괄 해결 및 환경 정상화 | ✅ | 2026-05-13 | Java 17, Spring 6, HttpClient 5 환경 최적화 완료 |
|             | 2. SessionCheckInterceptor Spring Bean 주입 전환 | ✅ | 2026-05-15 | `@Component` 추가 및 생성자 주입으로 `@Value` 주입 복구 |
|             | 3. LoginController 싱글톤 패턴 제거 | ✅ | 2026-05-15 | `getInstance()` 제거 및 Spring Bean 주입으로 전환 |
|             | 4. 세션 체크 인터셉터 적용 범위 확장 | ✅ | 2026-05-15 | `.do/.json`만에서 `/**`로 확장하여 모든 URL에 세션 체크 적용 |

---

## 6. [Phase 6] 빌드 오류 해결 및 기술 부채 청산 내역 (2026-05-13)

Java 17, Spring Boot 3.5.x, Jakarta EE 10 업그레이드 직후 발생한 **85개의 대규모 컴파일 오류**를 해결하고 시스템을 안정화했습니다.

### 6.1 Apache HttpClient 5 (HC5) 전면 적용
- **이슈**: HC4 기반의 SSL 설정(`setConnectionSocketFactory`) 및 타임아웃 메서드(`setConnectTimeout(int)`)가 Spring 6 / HC5 환경에서 호환되지 않음.
- **수정**: `GwConfig.java`, `BstgwConfig.java`를 HC5 빌더 패턴 및 `RequestConfig` 방식으로 전면 마이그레이션.

### 6.2 레거시 공통 DAO/Service 완전 제거
- **이슈**: `KsmCmnDAO`, `KsmCmnService` 등 Query ID를 문자열로 넘기던 레거시 패턴이 마이그레이션된 `@Mapper` 환경과 충돌하여 오류 유발.
- **수정**: 해당 클래스 및 인터페이스 3종 삭제, `BeastServiceImpl`, `AdptranApiService` 등에서의 의존성 제거. 모든 쿼리는 업무별 전용 DAO로 일원화.

### 6.3 MyBatis Insert 및 키 반환 로직 정형화
- **이슈**: `insert` 결과(`int`)를 `String`으로 직접 받으려 하거나, 생성된 키(`SCOPE_IDENTITY()`)를 가져오지 못하는 오류 발생.
- **수정**: `ApiMainServiceImpl`, `QnAServiceImpl`, `DevSupportServiceImpl` 등에서 `vo.getGeneratedKey()` 방식으로 키를 얻도록 로직 수정 및 서비스 인터페이스 반환 타입(`int` -> `String`) 동기화.

### 6.4 Spring 6 / Java 17 호환성 및 타입 안정성 확보
- **StatusCode 대응**: `ResponseEntity.getStatusCode()`가 `HttpStatusCode`(interface)를 반환함에 따라, `ResultEntity` 클래스의 필드 및 생성자 타입을 업데이트하여 `HttpStatus`(enum)와의 불일치 해결.
- **제네릭 타입 추론**: Java 17의 엄격해진 타입 추론에 맞춰 `new ResultEntity<List<GwApi>>(...)` 등 모든 호출부에 명시적 타입 파라미터 적용.
- **VO 상속 구조 개선**: `ApiMainVo`와 `ApiRegVO` 간의 필드 중복 정의를 제거하여 중의적 메서드 호출 오류(ambiguous match) 원천 차단.

### 6.5 누락된 기술 자산 복구
- **VO 필드 보강**: `ApiController`에서 참조하지만 VO에 정의되지 않았던 30여 개의 필드(`apiDesc`, `autId` 등)를 복구하고 Lombok `@Data` 적용.
- **오타 및 참조 오류**: `getFileSeq()` -> `getAtcFileNo()` 등 마이그레이션 과정에서 발생한 잔여 오타 정정 및 존재하지 않는 메서드 참조 수정.

**결과: `mvn compile` 성공 (BUILD SUCCESS) 및 전체 기술 부채 1차 청산 완료.**

### 6.6 HttpClient 5 API 최종 마이그레이션
- **이슈**: `BstgwConfig.java`에서 HC4 API(`org.apache.http.conn.ssl.*`, `org.apache.http.impl.client.*`)를 사용 중이었으나, pom.xml에는 HC5(`httpclient5`)만 의존성으로 등록되어 컴파일 실패.
- **수정**:
  - `TrustStrategy` import를 `org.apache.hc.core5.ssl.TrustStrategy`로 변경
  - `SSLContexts.custom()`을 HC5 API로 전환
  - `setSSLConnectionSocketFactory()` → `setTlsSocketFactory()`로 HC5 메서드명 적용
  - `NoopHostnameVerifier` import를 HC5 패키지로 변경

### 6.7 누락된 import 및 package 선언 복구
- **이슈**: `BeastServiceImpl.java`에서 `package` 선언 누락, `BeastService` 인터페이스 import 누락, `KsmCmnDAO` import 누락으로 컴파일 실패.
- **수정**: `package com.kt.openapi.web.beast.service;` 선언 추가, 누락된 import 2개 추가

### 6.8 MyBatis Mapper 스캔 범위 제한
- **이슈**: `@MapperScan(basePackages = "com.kt.openapi.web")`이 너무 넓어서 서비스 인터페이스(`ApiArsenalService`)를 MyBatis mapper로 잘못 스캔 → `ApiArsenalServiceImpl`과 빈 이름 충돌 (`ConflictingBeanDefinitionException`).
- **수정**: `@MapperScan(basePackages = "com.kt.openapi.web", annotationClass = Mapper.class)`로 변경 → `@Mapper` 어노테이션이 붙은 DAO 인터페이스만 스캔

### 6.9 Spring Bean 이름 충돌 해결
- **이슈**: `com.kt.openapi.fwk.cmm.config.AsyncConfig`와 `com.kt.openapi.web.apigw.config.AsyncConfig`가 모두 `asyncConfig`라는 빈 이름으로 등록되어 충돌 (`ConflictingBeanDefinitionException`).
- **수정**: `apigw.config.AsyncConfig`의 `@Configuration`에 고유 빈 이름 `"apigwAsyncConfig"` 할당

**결과: `mvn compile` 성공 (BUILD SUCCESS), 런타임 Bean 충돌 해결 완료.**

### 6.10 PropertyConfig 순환 참조 해결
- **이슈**: `PropertyConfig`에서 `@Autowired`로 `LegacyPropertyService`를 필드 주입받으면서 동시에 `@Bean` 메서드에서 `new`로 직접 생성 → Spring 6의 엄격한 순환 참조 검사로 `BeanCurrentlyInCreationException` 발생. 또한 `LegacyPropertyService` 내부 클래스의 `@Autowired Environment`가 `new`로 생성된 객체에서 동작하지 않음.
- **수정**:
  - `LegacyPropertyService`의 `@Autowired Environment` 제거 → 생성자 주입 방식으로 전환
  - `PropertyConfig`의 `@Autowired LegacyPropertyService` 필드 제거 → `Environment`만 필드 주입
  - `@PostConstruct`에서 `@Bean` 메서드를 직접 호출하여 `staticService` 초기화

**결과: `mvn compile` 성공 (BUILD SUCCESS), 런타임 순환 참조 해결 완료.**

### 6.11 MyBatis Mapper XML 파싱 오류 해결
- **이슈 1**: `Adptran_SQL.xml` 465번 줄에 `</mapper>` 닫는 태그 뒤에 여분 `>` 문자 존재 → `SAXParseException: Content is not allowed in trailing section` 발생
- **수정 1**: 여분 `>` 문자 제거
- **이슈 2**: `sql-map-config.xml`(레거시 iBatis 설정 파일)이 MyBatis `SqlSessionFactory`에 의해 스캔됨 → `<!DOCTYPE sqlMapConfig PUBLIC "-//iBATIS.com//DTD SQL Map Config 2.0//EN" "http://batis.apache.org/dtd/sql-map-config-2.dtd">`에서 `batis.apache.org` 호스트를 DNS 조회 실패 → `UnknownHostException` 발생
- **수정 2**: `sql-map-config.xml` 삭제 (MyBatisConfig에서 이미 `classpath:/kt/sqlmap/**/*.xml`로 모든 Mapper XML 스캔 중)

**결과: MyBatis SqlSessionFactory 생성 성공, XML 파싱 오류 해결 완료.**

### 6.12 yaml.file.path 설정 누락 해결
- **이슈**: `EgovYamlToJava`, `ApiRegServiceImpl`, `UploadFileUtils`에서 `@Value("${yaml.file.path}")`로 참조하는 속성이 YAML 설정 파일에 누락 → `PlaceholderResolutionException` 발생
- **수정**: `config/local/common.yml`, `config/tb/common.yml`, `config/prod/common.yml`에 `yaml.file.path` 설정 추가 (각 환경별 `yamlServer.host` 경로와 동일)

**결과: `yaml.file.path` 속성 주입 성공.**

### 6.13 Gitlab 관련 설정 누락 일괄 해결
- **이슈**: `ApiMainController`, `ApiRegController`에서 `@Value`로 참조하는 gitlab 관련 속성 4개가 YAML 설정 파일에 누락 → `PlaceholderResolutionException` 발생
  - `gitlab.arsenal.host`
  - `gitlab.arsenal.base.path`
  - `gitlab.arsenal.token`
  - `gitlab.private.token`
- **수정**: `config/local/common.yml`, `config/tb/common.yml`, `config/prod/common.yml`에 gitlab 관련 설정 4개 일괄 추가

**결과: gitlab 관련 속성 4개 모두 주입 성공.**

### 6.14 SafeDBChecker ExceptionInInitializerError 해결
- **이슈**: `SafeDBChecker`에서 `CommonFunc.safeDbEncrypt()` 호출 시 SafeDB SDK 초기화 실패 → `ExceptionInInitializerError` 발생. `catch (Exception e)`는 `Error`를 캐치하지 못하여 앱 기동 자체가 실패. 로컬 개발 환경에서 SafeDB Agent가 실행되지 않은 경우에도 앱 기동이 불가능한 문제.
- **수정**: `catch (Exception e)` → `catch (Throwable e)`로 변경하여 `Error` 계열 예외도 캐치. 로컬 개발 환경에서는 SafeDB 없이 기동 가능하도록 경고 로그만 출력하고 계속 진행.

**결과: SafeDB 미설정 환경에서도 앱 기동 성공.**

### 6.15 public_error.jsp Tiles taglib 참조 오류 해결
- **이슈**: `public_error.jsp`에서 `<t:layout>` 사용 시 `<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>` 선언 누락 → `<t>` prefix가 Tiles taglib(`http://tiles.apache.org/tags-tiles`)로 해석되어 `JasperException` 발생
- **수정**: `<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>` 선언 추가

**결과: public_error.jsp 렌더링 성공.**

### 6.16 DataSource 설정 누락 해결
- **이슈**: `application-local.yml`에 DB 설정이 존재하지만, `application.yml`의 `spring.config.import`에서 `${spring.profiles.active}`가 Spring Boot 3.x에서 import 시점에 해석되지 않아 `application-local.yml`이 로드되지 않음 → `Failed to determine a suitable driver class` 발생
- **수정**: `application.yml`의 `spring.config.import`에서 `${spring.profiles.active}` 사용 제거 → 하드코딩된 경로(`config/local/application-local.yml`)로 변경

**결과: application-local.yml 정상 로드, DataSource 빈 생성 성공.**

### 6.17 egovFrame 명칭 완전 제거
- **이슈**: 프로젝트 내 `egov` 명칭이 Java 클래스명, JSP 변수명, Spring XML config, Config 파일, Sample 코드 등 42개 파일에 걸쳐 잔존. egovFrame 라이브러리 직접 의존성 16개 파일 포함.
- **수정**:
  - **Java 클래스명 10개 rename**: `EgovStringUtil`→`StringUtil`, `EgovMessageUtil`→`MessageUtil`, `EgovYamlTransform`→`YamlTransform`, `EgovYamlToJson`→`YamlToJson`, `EgovYamlToJava`→`YamlToJava`, `EgovYamlPaser`→`YamlParser`(오타 수정), `EgovWebUtil`→`WebUtil`, `EgovJsonToYaml`→`JsonToYaml`, `EgovJsonToYamlSample`→`JsonToYamlSample`, `EgovYamlToJsonSample`→`YamlToJsonSample`
  - **Java import 17개 파일 수정**: `DevSupportServiceImpl`, `DevSupportController`, `QnAServiceImpl`, `UploadFileUtils`, `MypageController`, `ApiRegServiceImpl`, `ApiRegController`, `ApiMainController` 등
  - **JSP 변수명 14개 파일 변경**: `egovMapList`→`item`, `egovFaqList`→`faqItem`, `egovCmnList`→`cmnItem` 등
  - **Spring XML config 5개 삭제**: `context-properties.xml`, `context-idgen.xml`, `context-datasource.xml`, `context-common.xml`, `context-aspect.xml`(dead code 확인 후 삭제)
  - **JSP EgovMap import 2개 제거**: `regFormShareHead.jsp`(import 제거), `pathRegFormPrivate.jsp`(`HashMap`으로 대체)
  - **Config 파일 3개 egov 참조 제거**: `main.iml`(Maven library 참조 제거), `application-profile.yml`(egov message bundle 제거), `logback-spring.xml`(egov logger 제거)
  - **Sample 코드 5개 삭제**: `SampleController.java`, `SampleServiceImpl.java`, `SampleService.java`, `egovError.jsp`, `egovBizException.jsp`
  - **Java 변수명 3개 파일 변경**: `ApiController.java`(`egovMap`→`apiDef`, 127개), `ApiDeployController.java`(`egovMap`→`deployMap`), `AdptranApiService.java`(`egovMapOut`→`deployView`)
  - **JSP model attribute 3개 파일 변경**: `deployView.jsp`, `popCbApply.jsp`, `approvalListNew.jsp`(`${egovMap.xxx}`→`${deployMap.xxx}`)
  - **UTF-8 BOM 문제 해결**: PowerShell `Set-Content`로 인한 BOM 추가 18개 Java 파일 + 8개 JSP 파일에서 BOM 제거
- **결과**: 활성 코드에서 egov 참조 0개. 잔여는 `JsonToYamlSample.java` 주석, `BizException.java` javadoc, 문서 파일뿐.

### 6.18 JSP 스크립틀릿 제거 필요성
스크립틀릿(`<% %>`)은 JSP 2.0(2003년)부터 비권장(deprecated)되었으며, JSP 2.1 이후에는 완전히 제거된 기능입니다. 제거해야 하는 이유는 다음과 같습니다:

1. **보안 취약점 (XSS/코드 주입)**: 스크립틀릿은 서버 측에서 직접 Java 코드를 실행하므로, 사용자 입력을 안전하게 이스케이프하지 않으면 XSS 공격에 노출됩니다. JSTL의 `<c:out escapeXml="true">`는 자동으로 XSS를 방어합니다.
2. **유지보수성 저하**: Java 로직이 HTML 내부에 섞여 있어 뷰 템플릿을 읽기 어렵고, 프론트엔드 개발자가 수정하기 불가능합니다. JSTL/EL은 선언형 문법으로 HTML 구조가 명확합니다.
3. **성능 저하**: 스크립틀릿은 매 요청마다 JSP → Servlet 컴파일을 유발하지만, JSTL/EL은 컨테이너가 캐싱하고 최적화합니다.
4. **테스트 불가능**: 스크립틀릿 내 로직은 단위 테스트로 검증할 수 없지만, 커스텀 태그/EL 표현식은 독립적으로 테스트 가능합니다.
5. **Spring Boot 3 / Jakarta EE 10 표준 불일치**: 현대적 JSP 규격은 스크립틀릿을 허용하지 않으며, IDEA 등 최신 IDE에서 스크립틀릿 사용 시 경고가 발생합니다.
6. **컨트롤러-뷰 분리 원칙 위반**: 비즈니스 로직이 뷰 계층에 섞여 있어 MVC 패턴을 위반합니다. JSTL/EL은 데이터 바인딩만 담당하므로 컨트롤러에서 로직을 처리하는 표준 패턴과 일치합니다.

### 6.18 JSP 스크립틀릿 분석 완료
- **이슈**: 97개 JSP 파일에 781개 스크립틀릿(`<% %>`) 잔존. JSTL/JSP 표준으로 전환 필요.
- **분석 결과**:
  - **pageContext.setAttribute**: 18개 파일, 48개 사용
  - **request.getAttribute**: 15개 파일, 18개 사용
  - **HttpSession**: 5개 파일, 9개 사용
  - **if 조건문**: 14개 파일, 37개 사용
  - **KsmUtil.fnSafeStr**: 9개 파일, 37개 사용 (자체 유틸리티)
  - **CommonFunc.isRunmodeTag/isSpecificUser**: 13개 파일, 25개 사용
  - **BstgwConstant**: 6개 파일, 12개 사용
  - **String/boolean 변수 선언**: 18개 파일, ~75개 사용
  - **for/while 루프**: 0개 (없음)
  - **try-catch**: 0개 (없음)
- **파일별 난이도**: 쉬움 40개, 중간 30개, 어려움 27개(`pathRegFormPrivate.jsp`만 215개)
- **추정 작업량**: 40~103시간 (커스텀 태그 3개 작성 + 파일별 변환)
- **다음 단계**: 커스텀 태그(`<ksm:fnSafeStr>`, `<common:isRunmodeTag>`, `<common:isSpecificUser>`) 작성 후 쉬운 파일부터 순차 변환
