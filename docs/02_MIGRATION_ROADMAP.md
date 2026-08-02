# 프로젝트 마이그레이션 실행 로드맵 (Migration Roadmap)

이 문서는 `docs/MIGRATION_FINAL_REPORT.md`의 내용을 바탕으로, 현재 레거시 상태인 프로젝트를 목표 아키텍처(Spring Boot 3, Jakarta EE 10, MyBatis)로 전환하기 위한 단계별 수행 계획을 정의합니다.

---

## 0. 현재 상태 진단 (Current State)
- **프레임워크**: Spring 5.3.39 / egovFrame 4.2.0 (Legacy)
- **EE 스펙**: Java EE (javax.* 패키지 사용)
- **데이터 액세스**: iBatis 2.x 및 MyBatis 3.5 혼용
- **설정 방식**: XML 기반 (`context-*.xml`, `web.xml`)
- **레이아웃**: Apache Tiles 3
- **환경**: JDK 17, Maven 기반

---

## 1. 마이그레이션 단계별 프로세스

### Phase 1: 기반 환경 구축 (Infrastructure)
1. **`pom.xml` 전면 개편**:
   - Spring Boot Starter (3.5.13) 도입.
   - Jakarta EE 10 의존성 설정.
   - 레거시 egovFrame 및 Tiles 의존성 제거 준비.
2. **Main Application 생성**:
   - `com.kt.openapi.OpenApiMngApplication` 클래스 생성.
   - `@SpringBootApplication` 어노테이션 및 실행 로직 작성.
3. **설정 파일 전환**:
   - `src/main/resources/application.yml` 생성 및 기존 `.properties` 통합.

### Phase 2: 프레임워크 현대화 (Modernization)
1. **Jakarta EE 전환**:
   - 프로젝트 전체 소스의 `javax.*` (servlet, annotation, validation 등)을 `jakarta.*`로 일괄 치환.
2. **Java Config 전환 (XML to Java)**:
   - `WebMvcConfig`: 인터셉터, 리소스 핸들러, 뷰 리졸버 설정.
   - `MyBatisConfig`: `SqlSessionFactory`, `MapperScanner` 설정.
   - `CommonConfig`: 공통 빈(Bean) 등록.
3. **예외 처리 통합**:
   - `GlobalExceptionHandler` 생성 (`@ControllerAdvice`).

### Phase 3: 데이터 액세스 계층 전환 (iBatis to MyBatis)
1. **iBatis 제거 및 MyBatis 통합**:
   - `sql-map-config.xml` 설정을 MyBatis 방식으로 이식.
   - iBatis `SqlMapClient` 기반 코드를 MyBatis `Mapper` 인터페이스 및 XML로 전환.
2. **VO(Value Object) 강화**:
   - `EgovMap` (Map 기반) 사용을 지양하고, 업무별 전용 VO 클래스 생성 및 적용.

### Phase 4: 레이아웃 및 UI 시스템 전환 (Tiles to Tag)
1. **Tiles 제거**:
   - `tiles-defs.xml` 및 관련 라이브러리 삭제.
2. **커스텀 레이아웃 태그 생성**:
   - `/WEB-INF/tags/layout.tag` 파일 생성.
3. **JSP 마이그레이션**:
   - 모든 JSP 상단의 Tiles 관련 태그 제거 및 `<t:layout>` 적용.

### Phase 5: 유틸리티 및 정리 (Cleanup)
1. **레거시 유틸리티 교체**:
   - `EgovStringUtil`, `EgovDateUtil` 등을 표준 라이브러리(Apache Commons 등)로 대체.
2. **불필요 라이브러리 제거**:
   - BotDetect(CAPTCHA) 등 비호환 라이브러리 및 설정 삭제.
3. **레거시 XML 삭제**:
   - 전환 완료된 `web.xml` 및 `context-*.xml` 파일 제거.

---

## 2. 수행 우선순위 및 일정 (Proposed Schedule)

| 순서 | 작업명 | 중요도 | 비고 |
| :-- | :--- | :--- | :--- |
| 1 | `pom.xml` 및 부트 실행 환경 구축 | 필수 | 가장 먼저 선행되어야 함 |
| 2 | Jakarta EE 패키지 치환 | 필수 | 컴파일 에러 해결의 핵심 |
| 3 | Java Config 전환 | 필수 | XML 의존성 제거 |
| 4 | 레이아웃 태그 도입 및 JSP 수정 | 높음 | 화면 UI 정상 동작 확인용 |
| 5 | iBatis to MyBatis 및 VO 전환 | 높음 | 점진적 진행 가능 |
| 6 | 레거시 코드 정리 및 최적화 | 보통 | 최종 안정화 단계 |

---

## 3. 주의 사항
- **타입 안정성**: `Map` 대신 VO를 사용하여 런타임 에러를 방지합니다.
- **의존성 충돌**: Spring Boot 3와 호환되지 않는 레거시 JAR 파일은 반드시 제거하거나 대체해야 합니다.
- **검증**: 단계별 작업 후 단위 테스트 및 화면 테스트를 병행하여 회귀 버그를 최소화합니다.
