# SQL Server → PostgreSQL 마이그레이션 분석 보고서

> **생성일**: 2026-06-05
> **프로젝트**: openapi-mng-dev (APILinkManager)

---

## ⚠️ 작업 규칙

**한 단계 진행 후 무조건 대기합니다. 사용자의 승인을 받은 후 다음 단계를 진행합니다.**

---

## 1. 프로젝트 현황

| 항목 | 내용 |
|------|------|
| **현재 DB** | SQL Server (`mssql-jdbc` driver) |
| **ORM** | MyBatis (XML mapper 기반, Java 코드 내 raw SQL 없음) |
| **SQL Mapper 파일** | `src/main/resources/kt/sqlmap/api/` 하위 **21개 XML** |
| **Java 버전** | 17 (Spring Boot 3.5.14) |
| **페이징** | `OFFSET/FETCH` 기반 (이미 현대화 완료) |
| **환경별 설정** | `config/{local,tb,prod}/application-*.yml` |

---

## 2. 마이그레이션 대상 패턴 요약

### 🟢 LOW (regex 자동화 가능)

| # | 패턴 | 개수 | PostgreSQL 대응 | 상태 |
|------|------|------|----------------|------|
| 1 | [`ISNULL()`](#isnull) | ~235 | `COALESCE()` | ✅ 완료 (2026-06-05) |
| 2 | [`GETDATE()` / `getDate()`](#getdate) | ~108 | `NOW()` | ✅ 완료 (2026-06-05) |
| 3 | [`LEN()`](#len) | ~13 | `LENGTH()` | ✅ 완료 (2026-06-05) |
| 4 | [`TRY_CONVERT()`](#try_convert) | 10 | `CAST(... AS ...)` / `TO_NUMBER()` | ✅ 완료 (2026-06-05) |
| 5 | [`REPLICATE()`](#replicate) | 1 | `RPAD()` / `REPEAT()` | ✅ 완료 (2026-06-05) |
| 6 | [`RIGHT(str, n)`](#right) | 3 | `RIGHT()` (PG 14+) 또는 `SUBSTRING()` | ✅ 완료 (2026-06-05) |
| 7 | [`CAST(... AS MONEY)`](#cast_money) | 1 | `CAST(... AS NUMERIC)` | ✅ 완료 (2026-06-05) |
| 8 | [`+` 문자열 연결](#string_concat) | ~19 | `\|\|` 또는 `CONCAT()` | ✅ 완료 (2026-06-05) |
| 9 | [`dbo.`](#dbo) (테이블 접두사) | 4 | 제거 | ✅ 완료 (2026-06-05) |

### 🟡 MEDIUM (반자동화 + 수동 검증)

| # | 패턴 | 개수 | PostgreSQL 대응 | 상태 |
|------|------|------|----------------|------|
| 10 | [`CONVERT(varchar, ..., style)`](#convert_date) | ~95 | `TO_CHAR(col, 'format')` | ✅ 완료 (2026-06-05) |
| 11 | [`CONVERT(int, ...)`](#convert_date) (타입 캐스팅) | ~20 | `CAST(... AS INTEGER)` | ✅ 완료 (2026-06-05) |
| 12 | [`DATEDIFF()`](#datediff) | 1 | `EXTRACT(DAY FROM ...)` | ✅ 완료 (2026-06-05) |
| 13 | [`TOP n`](#top_n) | ~14 | `LIMIT n` | ✅ 완료 (2026-06-05) |
| 14 | [`STUFF()` + `FOR XML PATH('')`](#stuff_for_xml) | 1 | `STRING_AGG()` | ✅ 완료 (2026-06-05) |

### 🟠 MEDIUM (IDENTITY 관련)

| # | 패턴 | 개수 | PostgreSQL 대응 | 상태 |
|------|------|------|----------------|------|
| 15 | [`SCOPE_IDENTITY()`](#scope_identity) | ~14 | `INSERT ... RETURNING id` | ❌ 미변환 |
| 16 | [`IDENT_CURRENT()`](#ident_current) | ~7 | 시퀀스 조회 또는 제거 | ❌ 미변환 |

### 🔴 HIGH (PostgreSQL 구조 변경 필수)

| # | 패턴 | 개수 | PostgreSQL 대응 | 상태 |
|------|------|------|----------------|------|
| 17 | [`WITH(NOLOCK)` / `WITH (NOLOCK)`](#with_nolock) | ~559 | 제거 (PostgreSQL은 MVCC 기본) | ✅ 완료 (2026-06-05) |

### ✅ 변경 불필요 패턴

| 패턴 | 개수 | 비고 |
|------|------|------|
| `ROW_NUMBER() OVER()` | ~35 | 표준 SQL (PostgreSQL 호환). `rownum`은 단순 별칭 |
| `OFFSET/FETCH` 페이징 | 전체 | PostgreSQL `LIMIT/OFFSET`으로 변환 필요하지만 구조는 유사 |
| `CONCAT()` | 일부 | 표준 SQL (PostgreSQL 호환) |
| `SUBSTRING(str, start, len)` | 전체 | PostgreSQL에서도 동일 동작 |
| `LEFT() / RIGHT()` | 일부 | PostgreSQL 14+에서 `RIGHT()` 지원. 이전 버전은 `SUBSTRING()` 대체 |
| `MyBatis 동적 SQL` | 전체 | `<if>`, `<choose>`, `<foreach>` 등 프레임워크 기능 |

**총 패턴 수: ~1,100개**
**영향 파일: 21개 중 ~20개** (Common_SQL.xml 제외하면 거의 전 파일 영향)

---

## 3. 패턴별 상세 분석

---

### 🟢 LOW

---

<a id="isnull"></a>
### 3.1 `ISNULL()` — SQL Server NULL 처리 (~235개, 17개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `ISNULL(col, default)` | `COALESCE(col, default)` |

**변경 계획**:
1. **전체 치환** — `ISNULL(` → `COALESCE(` 로 100% regex 자동화 가능
2. **중첩 ISNULL 처리** — `ISNULL(col1, ISNULL(col2, ''))` 패턴도 `COALESCE(col1, COALESCE(col2, ''))` 로 자동 변환됨

```sql
-- SQL Server (기존)
SELECT ISNULL(user_name, 'UNKNOWN') FROM user_info
SELECT ISNULL(email, ISNULL(phone, 'NO CONTACT')) FROM member

-- PostgreSQL (변경 후)
SELECT COALESCE(user_name, 'UNKNOWN') FROM user_info
SELECT COALESCE(email, COALESCE(phone, 'NO CONTACT')) FROM member
```

**Regex 변환 규칙**:
```
ISNULL\(  →  COALESCE(
IsNULL\(  →  COALESCE(
```

**⚠️ 주의사항**: `ISNULL`과 `COALESCE`는 SQL 표준에서 behavior가 약간 다름. `ISNULL`은 첫 번째 인자의 타입을 따르지만, `COALESCE`는 모든 인자의 타입이 일치해야 함. PostgreSQL은 타입 엄격하므로 명시적 캐스팅이 필요할 수 있음.

**영향 파일**: `Adptran_SQL.xml`, `Apistatus_SQL.xml`, `ApiDeploy_SQL.xml`, `Guide_SQL.xml`, `Devsupport_SQL.xml`, `UserJoin_SQL.xml`, `Faq_SQL.xml`, `ApiInfo_SQL.xml`, `ApiSearch_SQL.xml`, `SensitiveInfo_SQL.xml`, `ApiReg_SQL.xml`, `Mypage_SQL.xml`, `ApiMain_SQL.xml`, `Beast_SQL.xml`, `BBS_CMN_SQL.xml`, `QnA_SQL.xml`, `Sample_SQL.xml`

---

<a id="getdate"></a>
### 3.2 `GETDATE()` / `getDate()` — SQL Server 현재 시각 (~108개, 14개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `GETDATE()` | `NOW()` 또는 `CURRENT_TIMESTAMP` |

**변경 계획**:
1. **전체 치환** — `GETDATE()` → `NOW()`, `getDate()` → `NOW()` 로 100% regex 자동화 가능
2. **일관성 유지** — 전체 프로젝트에서 `NOW()` 로 통일

```sql
-- SQL Server (기존)
INSERT INTO user_info (reg_date) VALUES (GETDATE())
SET LAST_LOGIN_DT = getDate()

-- PostgreSQL (변경 후)
INSERT INTO user_info (reg_date) VALUES (NOW())
SET LAST_LOGIN_DT = NOW()
```

**Regex 변환 규칙**:
```
GETDATE\(\)  →  NOW()
getDate\(\)  →  NOW()
```

**영향 파일**: `RefCommon_SQL.xml`, `ApiDeploy_SQL.xml`, `Apistatus_SQL.xml`, `Adptran_SQL.xml`, `UserJoin_SQL.xml`, `Devsupport_SQL.xml`, `Beast_SQL.xml`, `ApiReg_SQL.xml`, `ApiMain_SQL.xml`, `Mypage_SQL.xml`, `BBS_CMN_SQL.xml`, `Login_SQL.xml`, `Rest_SQL.xml`, `QnA_SQL.xml`

---

<a id="len"></a>
### 3.3 `LEN()` — SQL Server 문자열 길이 (~13개, 3개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `LEN(col)` | `LENGTH(col)` |

**변경 계획**:
1. **`LEN()` → `LENGTH()`** — regex로 100% 자동화 가능

```sql
-- SQL Server (기존)
SUBSTRING(API_ID, (LEN(#{prefix}) + 1), #{rangeLen})
SUBSTRING(B.REGR, 0, LEN(B.REGR)-2)

-- PostgreSQL (변경 후)
SUBSTRING(API_ID, (LENGTH(#{prefix}) + 1), #{rangeLen})
SUBSTRING(B.REGR, 0, LENGTH(B.REGR)-2)
```

**Regex 변환 규칙**:
```
\bLEN\(  →  LENGTH(
```

**⚠️ 주의사항**: `LEN()` 이 `CONVERT()` 나 `SUBSTRING()` 내부에서 사용되는 경우 함께 변환해야 함.

**영향 파일**: `Adptran_SQL.xml`, `ApiReg_SQL.xml`, `Devsupport_SQL.xml`

---

<a id="try_convert"></a>
### 3.4 `TRY_CONVERT()` — SQL Server 2012+ 안전 캐스팅 (10개, 2개 파일)

**설명**: SQL Server 2012+ 전용 함수로, 캐스팅 실패 시 NULL 을 반환합니다. PostgreSQL 에는 직접 대응 함수가 없습니다.

**발견 위치**:
- `Adptran_SQL.xml`: 5개 (라인 419, 420, 430, 434, 441)
- `ApiReg_SQL.xml`: 5개 (라인 1525, 1526, 1536, 1540, 1547)

**변환 예시**:
```sql
-- SQL Server (기존)
TRY_CONVERT(int, MIN_ID)

-- PostgreSQL (변경 후: FILTER + CAST 조합)
CASE WHEN MIN_ID ~ '^\d+$' THEN MIN_ID::INTEGER END
-- 또는
NULLIF(REGEXP_REPLACE(MIN_ID, '[^0-9]', '', 'g'), '')::INTEGER
```

**⚠️ 주의사항**: PostgreSQL 14+ 에서는 `MIN_ID::INTEGER` 가 실패 시 예외를 던지므로, `CASE WHEN` 으로 사전 검증이 필수입니다.

---

<a id="replicate"></a>
### 3.5 `REPLICATE()` — SQL Server 문자열 반복 (1개, 1개 파일)

**설명**: SQL Server 전용 문자열 반복 함수. PostgreSQL 에서는 `REPEAT()` 이 동일한 역할을 합니다.

**발견 위치**: `Devsupport_SQL.xml` 97 번 라인
```sql
SUBSTRING(B.REGR, 0, LEN(B.REGR)-2) + REPLICATE('*', 3) AS REGR
```

**변환 예시**:
```sql
-- SQL Server (기존)
SUBSTRING(B.REGR, 0, LEN(B.REGR)-2) + REPLICATE('*', 3)

-- PostgreSQL (변경 후)
SUBSTRING(B.REGR, 0, LENGTH(B.REGR)-2) || REPEAT('*', 3)
```

---

<a id="right"></a>
### 3.6 `RIGHT(str, n)` — SQL Server 오른쪽 문자 추출 (3개, 3개 파일)

**설명**: SQL Server 전용 함수. PostgreSQL 14+ 에서 `RIGHT()` 를 지원하지만, 이전 버전에서는 `SUBSTRING()` 으로 대체해야 합니다.

**발견 위치**:
- `Adptran_SQL.xml` 405 번 라인: `RIGHT('0000' + CONVERT(varchar, ...), 4)`
- `ApiReg_SQL.xml` 1511 번 라인: 동일 패턴
- `Sample_SQL.xml` 17 번 라인: `RIGHT('00000' + CAST(...), 5)`

**변환 예시**:
```sql
-- SQL Server (기존)
RIGHT('0000' + CONVERT(varchar, num), 4)

-- PostgreSQL (변경 후)
RIGHT('0000' || num::VARCHAR, 4)
-- 또는 PG 14 미만:
SUBSTRING('0000' || num::VARCHAR FROM LENGTH('0000' || num::VARCHAR) - 3)
```

---

<a id="cast_money"></a>
### 3.7 `CAST(... AS MONEY)` — SQL Server MONEY 타입 (1개, 1개 파일)

**설명**: SQL Server 전용 `MONEY` 타입. PostgreSQL 에서는 `NUMERIC` 으로 대체합니다.

**발견 위치**: `BBS_CMN_SQL.xml` 197 번 라인
```sql
REPLACE(CONVERT(VARCHAR(50), CAST(A.RETV_NUM AS MONEY), 1), '.00', '') as RETV_NUM
```

**변환 예시**:
```sql
-- SQL Server (기존)
REPLACE(CONVERT(VARCHAR(50), CAST(A.RETV_NUM AS MONEY), 1), '.00', '')

-- PostgreSQL (변경 후: TO_CHAR 로 숫자 포맷팅)
TO_CHAR(A.RETV_NUM, 'FM999G999G999G999G990')
-- 또는 단순 변환:
CAST(A.RETV_NUM AS VARCHAR)
```

**⚠️ 주의사항**: SQL Server 의 `CONVERT(..., MONEY, 1)` 은 천 단위 쉼표 포맷팅을 합니다. PostgreSQL 의 `TO_CHAR()` 로 동일한 포맷을 재현해야 합니다.

---

<a id="string_concat"></a>
### 3.8 `+` 문자열 연결 — SQL Server 문자열 결합 (~19개, 3개 파일)

**설명**: SQL Server 는 `+` 연산자로 문자열을 연결하지만, PostgreSQL 은 `||` 연산자를 사용합니다.

| SQL Server | PostgreSQL |
|------------|------------|
| `'%' + #{param} + '%'` | `'%' \|\| #{param} \|\| '%'` |

**발견 위치**:

| 파일 | 라인 | 패턴 |
|------|------|------|
| `ApiSearch_SQL.xml` | 59-62, 97, 179, 217-220, 255 | `LIKE '%' + #{schText} + '%'` 등 |
| `SensitiveInfo_SQL.xml` | 247-248, 280-281 | `LIKE '%' + #{searchTerm} + '%'` |
| `Sample_SQL.xml` | 67, 70, 80, 83 | `LIKE '%' + #{searchKeyword} + '%'` |

**변환 예시**:
```sql
-- SQL Server (기존)
AND (B.API_DESC LIKE '%' + #{schText} + '%')

-- PostgreSQL (변경 후)
AND (B.API_DESC LIKE '%' || #{schText} || '%')
```

**⚠️ 주의사항**: `+` 가 숫자 덧셈으로 사용되는 경우 (예: `SORT_ODRG + 1`) 는 변경하지 말아야 합니다. 문자열 리터럴과 함께 사용되는 경우만 변환 대상입니다.

---

<a id="dbo"></a>
### 3.9 `dbo.` — SQL Server 스키마 접두사 (4개, 1개 파일)

**설명**: SQL Server 의 기본 스키마 접두사. PostgreSQL 에서는 스키마가 다르면 유지하지만, 기본 스키마 (`public`) 인 경우 제거합니다.

**발견 위치**: `ApiReg_SQL.xml` (라인 1336, 1337, 1398, 1407)

**변환**: `dbo.` 제거

```sql
-- SQL Server (기존)
FROM dbo.KOA_TB_API_DEF A WITH(NOLOCK)

-- PostgreSQL (변경 후)
FROM KOA_TB_API_DEF A
```

---

### 🟡 MEDIUM

---

<a id="convert_date"></a>
### 3.10 `CONVERT(varchar, ..., style)` — SQL Server 날짜 변환 (~95개, 15개 파일)

**설명**: SQL Server 전용 날짜/타입 변환 함수. style 코드별로 PostgreSQL `TO_CHAR()` 포맷으로 매핑 필요.

| Style 코드 | 의미 | PostgreSQL 대응 |
|------------|------|----------------|
| `102` | `yyyy.mm.dd` | `TO_CHAR(dt, 'YYYY.MM.DD')` |
| `11` | `mon dd yyyy` | `TO_CHAR(dt, 'Mon DD YYYY')` |
| `23` | `yyyy-mm-dd` | `TO_CHAR(dt, 'YYYY-MM-DD')` |
| `120` | `yyyy-mm-dd hh:mi:ss` | `TO_CHAR(dt, 'YYYY-MM-DD HH24:MI:SS')` |
| `121` | `yyyy-mm-dd hh:mi:ss.mmm` | `TO_CHAR(dt, 'YYYY-MM-DD HH24:MI:SS.MS')` |
| `21` | `yyyy-mm-dd hh:mi:ss.mmm` (UTC) | `TO_CHAR(dt, 'YYYY-MM-DD HH24:MI:SS.MS')` |

**변환 예시**:
```sql
-- SQL Server (기존)
CONVERT(varchar(19), A.REG_DT, 120)
CONVERT(CHAR(10), B.REG_DT, 23)
CONVERT(VARCHAR(10), REG_DT, 102)

-- PostgreSQL (변경 후)
TO_CHAR(A.REG_DT, 'YYYY-MM-DD HH24:MI:SS')
TO_CHAR(B.REG_DT, 'YYYY-MM-DD')
TO_CHAR(REG_DT, 'YYYY.MM.DD')
```

**⚠️ 주의사항**:
- `CONVERT` 가 날짜가 아닌 타입 변환에 사용된 경우 (예: `CONVERT(int, col)`, `CONVERT(varchar, col)`) 는 `CAST(col AS INTEGER)` / `CAST(col AS VARCHAR)` 로 별도 처리 필요
- `CONVERT(VARCHAR(50), col)` (style 없음) → `CAST(col AS VARCHAR(50))` 또는 `col::VARCHAR(50)`

**영향 파일**: `RefCommon_SQL.xml`, `Adptran_SQL.xml`, `ApiDeploy_SQL.xml`, `Devsupport_SQL.xml`, `ApiInfo_SQL.xml`, `ApiSearch_SQL.xml`, `ApiReg_SQL.xml`, `BBS_CMN_SQL.xml`, `ApiMain_SQL.xml`, `SensitiveInfo_SQL.xml`, `Apistatus_SQL.xml`, `Beast_SQL.xml`, `Main_SQL.xml`, `QnA_SQL.xml`, `Mypage_SQL.xml`

---

<a id="datediff"></a>
### 3.11 `DATEDIFF()` — SQL Server 날짜 차이 (1개, 1개 파일)

**발견 위치**: `Apistatus_SQL.xml` 293 번 라인
```sql
DATEDIFF(day, as_chst.STATUS_CHECK_DT, GETDATE()) <= #{statusCheckDtDays}
```

**변환 예시**:
```sql
-- SQL Server (기존)
DATEDIFF(day, as_chst.STATUS_CHECK_DT, GETDATE())

-- PostgreSQL (변경 후)
EXTRACT(DAY FROM NOW() - as_chst.STATUS_CHECK_DT)::INTEGER
-- 또는
(NOW()::DATE - as_chst.STATUS_CHECK_DT::DATE)
```

---

<a id="top_n"></a>
### 3.12 `TOP n` — SQL Server 행 제한 (~14개, 6개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `SELECT TOP 1 ...` | `SELECT ... LIMIT 1` |
| `SELECT TOP 300 ...` | `SELECT ... LIMIT 300` |
| `SELECT TOP (CAST(#{top} AS INT))` | `LIMIT #{top}` |

**변경 계획**:
1. **`TOP n` → `LIMIT n`** — `SELECT TOP n` 에서 `TOP n` 제거 후 쿼리 끝에 `LIMIT n` 추가
2. **반자동화** — `TOP n` 제거는 regex 로 자동화 가능하지만, `LIMIT n` 은 쿼리 끝에 추가해야 하므로 위치 파악 필요

```sql
-- SQL Server (기존)
SELECT TOP 1 SUCCESS_YN FROM KOA_TB_API_VERI_CONDITION WHERE ... ORDER BY SEQ DESC
SELECT TOP (CAST(#{top} AS INT)) * FROM ...

-- PostgreSQL (변경 후)
SELECT SUCCESS_YN FROM KOA_TB_API_VERI_CONDITION WHERE ... ORDER BY SEQ DESC LIMIT 1
SELECT * FROM ... LIMIT #{top}
```

**⚠️ 주의사항**:
- `TOP` 이 서브쿼리 내에 있는 경우 해당 서브쿼리 끝에 `LIMIT` 추가
- `SELECT TOP (CAST(#{top} AS INT))` 패턴은 `LIMIT #{top}` 으로 단순화 가능

**영향 파일**: `ApiDeploy_SQL.xml`, `Apistatus_SQL.xml`, `Devsupport_SQL.xml`, `ApiReg_SQL.xml`, `Beast_SQL.xml`, `SensitiveInfo_SQL.xml`

---

<a id="stuff_for_xml"></a>
### 3.13 `STUFF()` + `FOR XML PATH('')` — SQL Server 문자열 연결 (1개, 1개 파일)

**발견 위치**: `Apistatus_SQL.xml` 229-236 번 라인

**변환 예시**:
```sql
-- SQL Server (기존)
STUFF((
    SELECT ';' + CONVERT(varchar(19), as_cdh.STATUS_CHECK_DT, 120) + '::' + as_cdh.STATUS_CODE + '::' + CAST(as_cdh.STATUS_RES_MSEC AS varchar)
    FROM KOA_TB_API_STATUS_CHECK_DAILY_HST as_cdh
    WHERE as_cdh.API_NO = a_def.API_NO
    FOR XML PATH('')
), 1, 1, '')

-- PostgreSQL (변경 후)
STRING_AGG(
    ';' || TO_CHAR(as_cdh.STATUS_CHECK_DT, 'YYYY-MM-DD HH24:MI:SS') || '::' || as_cdh.STATUS_CODE || '::' || as_cdh.STATUS_RES_MSEC::VARCHAR,
    ''
    ORDER BY as_cdh.STATUS_CHECK_DT
)
```

**⚠️ 주의사항**: `STUFF(..., 1, 1, '')` 는 첫 번째 쉼표 제거용이므로, `STRING_AGG()` 로 변환 시 `SEPARATOR` 를 적절히 설정하면 `STUFF` 가 불필요해집니다.

---

### 🟠 MEDIUM (IDENTITY 관련)

---

<a id="scope_identity"></a>
### 3.14 `SCOPE_IDENTITY()` — SQL Server 삽입 후 ID 반환 (~14개, 5개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `SELECT SCOPE_IDENTITY()` | `INSERT ... RETURNING id` |

**발견 위치**:

| 파일 | 라인 |
|------|------|
| `RefCommon_SQL.xml` | 62 |
| `ApiMain_SQL.xml` | 393, 623, 702, 733, 797, 946 |
| `Adptran_SQL.xml` | 271 |
| `ApiReg_SQL.xml` | 10, 308, 580, 975, 1426 |
| `BBS_CMN_SQL.xml` | 282, 363 |

**변환 예시**:
```xml
<!-- SQL Server (기존) -->
<selectKey keyProperty="seq" resultType="int">
    INSERT INTO KOA_TB_API_DEF (...) VALUES (...)
    SELECT SCOPE_IDENTITY()
</selectKey>

<!-- PostgreSQL (변경 후) -->
<selectKey keyProperty="seq" resultType="int">
    INSERT INTO KOA_TB_API_DEF (...) VALUES (...)
    RETURNING SEQ
</selectKey>
```

**⚠️ 주의사항**: `<selectKey>` 패턴을 사용하는 경우 MyBatis 가 반환된 1 행 1 컬럼을 `keyProperty` 에 자동 매핑하므로 Java 코드 변경 불필요.

---

<a id="ident_current"></a>
### 3.15 `IDENT_CURRENT()` — SQL Server 테이블 최대 ID 조회 (~7개, 2개 파일)

| SQL Server | PostgreSQL |
|------------|------------|
| `IDENT_CURRENT('table')` | 시퀀스 조회 또는 Java 측 ID 생성 |

**발견 위치**:

| 파일 | 라인 | 패턴 |
|------|------|------|
| `ApiMain_SQL.xml` | 658, 790, 980 | `IDENT_CURRENT('KOA_TB_API_SPC')`, `IDENT_CURRENT('KOA_TB_API_DEF')` |
| `ApiReg_SQL.xml` | 45, 58, 1212 | `IDENT_CURRENT('KOA_TB_API_SPC')`, `IDENT_CURRENT('KOA_TB_API_DEF') + 1` |

**변환 전략**:
1. **시퀀스 생성** — 대상 테이블에 대해 PostgreSQL 시퀀스 생성
2. **`nextval('seq_name')`** 으로 대체 — 새 ID 생성 시 사용
3. **Java 측 ID 생성 패턴** 인 경우 (예: `IDENT_CURRENT + 1`) — Java 로 이동 또는 시퀀스 사용

```sql
-- SQL Server (기존)
SELECT CAST(IDENT_CURRENT('KOA_TB_API_DEF') + 1 AS VARCHAR)

-- PostgreSQL (변경 후)
SELECT nextval('koa_tb_api_def_api_no_seq')::VARCHAR
```

---

### 🔴 HIGH

---

<a id="with_nolock"></a>
### 3.16 `WITH(NOLOCK)` / `WITH (NOLOCK)` — SQL Server Table Hint (~559개, 21개 파일)

**설명**: SQL Server 전용 힌트로 `READ UNCOMMITTED` 격리 수준과 동일함. PostgreSQL 은 기본적으로 MVCC 를 사용하므로 제거하면 됨.

**변경 계획**:
1. **PostgreSQL 은 MVCC 기본 동작** — SELECT 는 UPDATE/DELETE 를 블로킹하지 않으므로 `WITH(NOLOCK)` 의 존재 이유가 없음
2. **단순 제거** — 테이블명 뒤의 `WITH(NOLOCK)` / `WITH (NOLOCK)` 만 삭제
3. **자동화 가능** — regex 배치 변환으로 100% 자동화 가능

```sql
-- SQL Server (기존)
SELECT * FROM user_info A WITH(NOLOCK)
SELECT * FROM KOA_TB_API_SYSTEM A WITH (NOLOCK)

-- PostgreSQL (변경 후)
SELECT * FROM user_info A
SELECT * FROM KOA_TB_API_SYSTEM A
```

**Regex 변환 규칙**:
```
WITH\(NOLOCK\)    →  (제거)
WITH \(NOLOCK\)   →  (제거)
```

**⚠️ Behavior 차이 주의**: `WITH(NOLOCK)` 은 dirty read 를 허용하지만, PostgreSQL 의 기본 SELECT 는 커밋된 데이터만 읽음. dirty read 를 의도적으로 활용하는 로직이 있다면 재설계 필요 (일반적으로 없음).

**영향 파일별 개수**:

| 파일 | WITH(NOLOCK) 수 |
|------|----------------|
| ApiDeploy_SQL.xml | ~100 |
| Adptran_SQL.xml | ~25 |
| RefCommon_SQL.xml | ~5 |
| Devsupport_SQL.xml | ~15 |
| ApiInfo_SQL.xml | ~25 |
| ApiReg_SQL.xml | ~90 |
| ApiSearch_SQL.xml | ~45 |
| ApiMain_SQL.xml | ~90 |
| Beast_SQL.xml | ~100 |
| BBS_CMN_SQL.xml | ~15 |
| Common_SQL.xml | 1 |
| Apistatus_SQL.xml | ~25 |
| Mypage_SQL.xml | ~20 |
| QnA_SQL.xml | ~8 |
| Faq_SQL.xml | ~6 |
| Login_SQL.xml | ~5 |
| Guide_SQL.xml | ~6 |
| UserJoin_SQL.xml | 1 |
| Main_SQL.xml | 2 |
| Rest_SQL.xml | ~4 |
| Sample_SQL.xml | 4 |

---

## 4. 변경 불필요 패턴

| 패턴 | 개수 | 비고 |
|------|------|------|
| `ROW_NUMBER() OVER()` | ~35 | 표준 SQL (PostgreSQL 호환). `rownum` 은 단순 별칭 |
| `OFFSET/FETCH` 페이징 | 전체 | PostgreSQL `LIMIT/OFFSET` 으로 변환 필요 |
| `CONCAT()` | 일부 | 표준 SQL (PostgreSQL 호환) |
| `SUBSTRING(str, start, len)` | 전체 | PostgreSQL 에서도 동일 동작 |
| `NULLIF()` | 일부 | 표준 SQL (PostgreSQL 호환) |
| `MyBatis 동적 SQL` | 전체 | `<if>`, `<choose>`, `<foreach>` 등 프레임워크 기능 |
| `CAST(... AS BIGINT/INT/VARCHAR)` | 전체 | 표준 SQL (PostgreSQL 호환) |
| `REPLACE()` | 전체 | 표준 SQL (PostgreSQL 호환) |
| `ROW_NUMBER() / RANK() / DENSE_RANK()` | ~35 | 표준 SQL window functions |
| `LAG / LEAD` | 일부 | 표준 SQL window functions |

---

## 5. 마이그레이션 단계 및 소요 시간

| 단계 | 작업 내용 | 예상 소요 |
|------|-----------|-----------|
| **Phase 1** | `WITH(NOLOCK)` 제거 (~559) + `ISNULL→COALESCE` (~235) + `GETDATE→NOW` (~108) + `LEN→LENGTH` (~13) | **1~2일** |
| **Phase 2** | `CONVERT→TO_CHAR` (~95, style 6 종 매핑) + `CONVERT(int)→CAST` (~20) + `DATEDIFF→EXTRACT` (1) + `TOP→LIMIT` (~14) | **2~3일** |
| **Phase 3** | `SCOPE_IDENTITY→RETURNING` (~14) + `IDENT_CURRENT→시퀀스` (~7) + `STUFF+FOR XML→STRING_AGG` (1) | **1~2일** |
| **Phase 4** | `+`→`\|\|` (~19) + `TRY_CONVERT→CASE/CAST` (10) + `REPLICATE→REPEAT` (1) + `RIGHT` (3) + `CAST(MONEY)` (1) + `dbo.` 제거 (4) | **0.5~1일** |
| **Phase 5** | `OFFSET/FETCH` → `LIMIT/OFFSET` (Common_SQL.xml + 14 개 파일) | **0.5~1일** |
| **Phase 6** | DDL 변경 (IDENTITY→SEQUENCE, 타입 매핑) + 전체 테스트 | **3~5일** |
| **Phase 7** | 성능 튜닝 + edge case 검증 | **2~3일** |

### **총 예상: 10~17일 (1인 기준)**

---

## 6. DDL 마이그레이션 (별도 작업)

SQL Mapper 외에도 테이블 스키마 마이그레이션이 필요합니다.

| SQL Server | PostgreSQL |
|------------|------------|
| `INT IDENTITY(1,1)` | `SERIAL` 또는 `GENERATED ALWAYS AS IDENTITY` |
| `BIGINT IDENTITY(1,1)` | `BIGSERIAL` 또는 `GENERATED ALWAYS AS IDENTITY` |
| `NVARCHAR(n)` | `VARCHAR(n)` |
| `NTEXT` | `TEXT` |
| `BIT` | `BOOLEAN` |
| `DATETIME` | `TIMESTAMP` |
| `DATETIME2` | `TIMESTAMP` |
| `MONEY` | `NUMERIC(19,4)` |
| `UNIQUEIDENTIFIER` | `UUID` |
| `BIT DEFAULT 0` | `BOOLEAN DEFAULT FALSE` |

---

## 7. 주요 리스크

1. **`WITH(NOLOCK)` 559 개** — 프로젝트 전체 SQL 의 대부분에 존재. regex 로 자동화 가능하지만, 변환 후 ALL 쿼리가 정상 동작하는지 검증이 필수. **가장 많은 작업량**
2. **`ISNULL()` 235 개** — regex 로 자동화 가능하지만, 타입 엄격성 문제로 `COALESCE` 로 변경 후 타입 캐스팅이 필요할 수 있음
3. **`CONVERT()` 128 개** — 날짜 변환 (style 코드) 과 타입 캐스팅 (`CONVERT(int, ...)`) 이 혼재되어 있음. style 코드별 정확한 매핑 필수
4. **`TOP n` 14 개** — 서브쿼리 내 `TOP` 이 있는 경우 `LIMIT` 위치를 정확히 파악해야 함. 특히 `SELECT TOP (CAST(#{top} AS INT))` 패턴은 동적 파라미터 사용
5. **`SCOPE_IDENTITY()` 14 개** — `<selectKey>` 내 `RETURNING` 컬럼을 정확히 지정해야 함. 테이블별 PK 컬럼 확인 필수
6. **`IDENT_CURRENT()` 7 개** — Java 측에서 ID 를 미리 생성하여 전달하는 패턴인지, DB 에서 조회하는 패턴인지 구분 필요. 시퀀스 생성 병행
7. **`TRY_CONVERT()` 10 개** — PostgreSQL 에 직접 대응 함수 없음. `CASE WHEN` 으로 사전 검증하는 패턴으로 변환 필요
8. **`+` 문자열 연결 19 개** — `+` 가 숫자 덧셈으로 사용되는 경우와 혼재됨. 문자열 리터럴과 함께 사용되는 경우만 변환
9. **`OFFSET/FETCH` → `LIMIT/OFFSET`** — `Common_SQL.xml` 의 `pagePrefix`/`pageSuffix` 와 14 개 파일의 `ROW_NUMBER()` 기반 페이징 구조 변경 필요
10. **타입 매핑** — `NVARCHAR`→`VARCHAR`, `BIT`→`BOOLEAN`, `DATETIME`→`TIMESTAMP`, `MONEY`→`NUMERIC` 등 DDL 전체 변경 필요
11. **경계값 테스트** — 날짜 포맷, 문자열 인코딩, NULL 처리 등 edge case 검증 필요

---

## 8. 긍정적 요소

- **Java 코드 내 raw SQL 없음** — 모든 SQL 이 XML mapper 에 집중되어 있어 패턴 기반 자동 변환 가능
- **MyBatis 는 DB 중립적** — 프레임워크 자체 변경 불필요, SQL 내용만 교체
- **표준 SQL 비중 높음** — `ROW_NUMBER()`, `COALESCE`, `NULLIF`, `SUBSTRING`, `CAST` 등은 변경 불필요
- **커스텀 UDF 없음** — `dbo.FN_*` 패턴이 전혀 없음 (참고 프로젝트 대비 리스크 대폭 감소)
- **MERGE INTO 없음** — SQL Server 전용 UPSERT 구문이 없음 (참고 프로젝트 대비 가장 큰 리스크 제거)
- **DECLARE @table 없음** — T-SQL 변수 선언이 없음
- **페이징 이미 현대화** — `OFFSET/FETCH` 기반으로 Oracle `rownum BETWEEN` 문제 없음
- **DATALENGTH/COPY_T/CHARINDEX 없음** — 참고 프로젝트의 2 차 감사 누락 패턴이本项目에 없음

---

## 9. 권장 자동화 전략

Phase 1~4 는 regex 기반 배치 변환으로 자동화 가능:

```
# Phase 1 자동화 대상 (100% 자동화)
WITH\(NOLOCK\)          →  (제거)
WITH \(NOLOCK\)         →  (제거)
ISNULL\(                →  COALESCE(
IsNULL\(                →  COALESCE(
GETDATE\(\)             →  NOW()
getDate\(\)             →  NOW()
\bLEN\(                 →  LENGTH(

# Phase 2 자동화 대상 (80% 자동화, 수동 검증 필수)
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*120\)  →  TO_CHAR($1, 'YYYY-MM-DD HH24:MI:SS')
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*121\)  →  TO_CHAR($1, 'YYYY-MM-DD HH24:MI:SS.MS')
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*112\)  →  TO_CHAR($1, 'YYYYMMDD')
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*102\)  →  TO_CHAR($1, 'YYYY.MM.DD')
CONVERT\(CHAR\(\d+\),\s*(\w+),\s*23\)      →  TO_CHAR($1, 'YYYY-MM-DD')
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*21\)   →  TO_CHAR($1, 'YYYY-MM-DD HH24:MI:SS.MS')
CONVERT\(VARCHAR\(\d+\),\s*(\w+),\s*11\)   →  TO_CHAR($1, 'Mon DD YYYY')
CONVERT\(int,\s*(\w+)\)                     →  CAST($1 AS INTEGER)
CONVERT\(VARCHAR\(\d+\),\s*(\w+)\)          →  CAST($1 AS VARCHAR($n))
DATEDIFF\(day,\s*(\w+),\s*(\w+)\)          →  EXTRACT(DAY FROM $2 - $1)::INTEGER
SELECT TOP (\d+)                            →  SELECT   (TOP 제거 후 쿼리 끝에 LIMIT $1 추가)

# Phase 3 자동화 대상 (반자동화)
SELECT SCOPE_IDENTITY()                     →  RETURNING <PK 컬럼> (수동: 테이블별 PK 확인)
REPLICATE\((\w+),\s*(\d+)\)                →  REPEAT($1, $2)

# Phase 4 자동화 대상 (100% 자동화)
'%' \+ #{(\w+)} \+ '%'                     →  '%' || #{$1} || '%'
TRY_CONVERT\((\w+),\s*(\w+)\)              →  CASE WHEN $2 ~ '^\d+$' THEN $2::$1 END (정규식 타입별 조정)
dbo\.                                      →  (제거)
```

**자동화 불가 패턴** (수동 변환 필수):
- `TOP n` → `LIMIT n` — `LIMIT` 위치 파악 필요 (서브쿼리 내 TOP 포함)
- `SCOPE_IDENTITY()` → `RETURNING` — 테이블별 PK 컬럼 확인 필요
- `IDENT_CURRENT()` → 시퀀스 — Java 측 ID 생성 패턴 분석 필요
- `TRY_CONVERT()` → `CASE WHEN` — 타입별 검증 로직이 다름
- `STUFF(...) + FOR XML PATH('')` → `STRING_AGG()` — 서브쿼리 구조 재작성 필요
- `CAST(... AS MONEY)` → `TO_CHAR()` — 포맷팅 로직 재현 필요
- `CONVERT(varchar, ..., style)` — style 코드 6 종 매핑 + 대소문자 구분 없는 매칭 필요

---

## 10. PostgreSQL 전환을 위한 설정 파일 분석

SQL 문법 변환 외에도, PostgreSQL 로 데이터베이스를 전환하기 위해 변경해야 할 설정 파일이 있습니다.

### 11.1 변경 대상 파일 (6 개)

| # | 파일 | 변경 내용 | 난이도 |
|---|------|----------|--------|
| 1 | `pom.xml` | `mssql-jdbc` → `postgresql` 드라이버 | 🟢 LOW |
| 2 | `config/local/application-local.yml` | driver + URL | 🟢 LOW |
| 3 | `config/tb/application-tb.yml` | driver + URL | 🟢 LOW |
| 4 | `config/prod/application-prod.yml` | driver + URL | 🟢 LOW |
| 5 | `context.xml` (Tomcat JNDI) | driver + URL | 🟢 LOW |
| 6 | `MyBatisConfig.java` | 변경 불필요 | ✅ |

### 11.2 현재 MSSQL DataSource 설정 (3 환경)

**local** (`config/local/application-local.yml`):
```yaml
spring:
  datasource:
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    url: jdbc:sqlserver://midb-az01-dev-link-01.5da5dea9bed3.database.windows.net:1433;databaseName=sdpsupport;encrypt=true;trustServerCertificate=true;
    username: "shubsupport_user5"
    password: "shub_1234!"
    hikari:
      maximum-pool-size: 100
      minimum-idle: 30
      connection-timeout: 10000
      idle-timeout: 300000
      max-lifetime: 600000
      pool-name: jdbc/OPENAPI
```

**tb** (`config/tb/application-tb.yml`):
```yaml
spring:
  datasource:
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    url: jdbc:sqlserver://dev-db-server:1433;databaseName=openapi_dev;encrypt=true;trustServerCertificate=true;
    username: "dev_user"
    password: "dev_password"
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      pool-name: HikariTB
```

**prod** (`config/prod/application-prod.yml`):
```yaml
spring:
  datasource:
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    url: jdbc:sqlserver://prd-db-server:1433;databaseName=openapi_prd;encrypt=true;trustServerCertificate=true;
    username: "prd_user"
    password: "prd_password"
    hikari:
      maximum-pool-size: 100
      minimum-idle: 30
      pool-name: HikariProd
```

### 11.3 PostgreSQL 대응 설정 (local 환경 실제 적용)

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://pgdb-az01-dev-shub-01.postgres.database.azure.com:5432/portal?currentSchema=public&stringtype=unspecified
    username: "shubsupport_user5"
    password: "shub_1234!"
    hikari:
      maximum-pool-size: 100
      minimum-idle: 30
      connection-timeout: 10000
      idle-timeout: 300000
      max-lifetime: 600000
      pool-name: jdbc/OPENAPI
```

**⚠️ URL 옵션 설명**:
- `currentSchema=public` — PostgreSQL 의 기본 스키마 명시 (SQL Server 의 `dbo` 에 대응)
- `stringtype=unspecified` — `VARCHAR`/`CHAR`/`TEXT` 구분을 JDBC 타입에 맡김 (MyBatis 호환성 향상)

### 11.4 `context.xml` — Tomcat JNDI DataSource

Docker 배포 시 Tomcat 의 JNDI DataSource 로 MSSQL 연결 중입니다.

**현재** (`context.xml`):
```xml
<Resource removeAbandonedTimeout="60" removeAbandoned="true"
    factory="org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory"
    url="jdbc:sqlserver://#{DB_URL};databasename=sdpsupport;encrypt=true;trustServerCertificate=true;"
    driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
    password="#{DB_PASSWORD}" username="#{DB_USERNAME}"
    maxWaitMillis="10000" maxIdle="30" maxTotal="100"
    type="javax.sql.DataSource" auth="Container" name="jdbc/OPENAPI"/>
```

**PostgreSQL 로 변경 시**:
```xml
<Resource removeAbandonedTimeout="60" removeAbandoned="true"
    factory="org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory"
    url="jdbc:postgresql://#{DB_URL}:5432/sdpsupport"
    driverClassName="org.postgresql.Driver"
    password="#{DB_PASSWORD}" username="#{DB_USERNAME}"
    maxWaitMillis="10000" maxIdle="30" maxTotal="100"
    type="javax.sql.DataSource" auth="Container" name="jdbc/OPENAPI"/>
```

**⚠️ 주의사항**:
- `#{DB_URL}`, `#{DB_PASSWORD}`, `#{DB_USERNAME}` 은 환경 변수 치환용 플레이스홀더로 유지
- `encrypt=true;trustServerCertificate=true;` 옵션은 MSSQL 전용이므로 제거
- PostgreSQL SSL 이 필요한 경우 `ssl=true&sslmode=require` 로 대체

### 11.5 `pom.xml` — JDBC 드라이버 교체

**현재**:
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
</dependency>
```

**변경**:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

**⚠️ 주의사항**:
- Spring Boot 3.5.x 의 BOM 이 PostgreSQL 드라이버 버전을 관리하므로 `<version>` 생략 가능
- `mssql-jdbc` 는 완전히 제거해도 무방함 (다른 의존성이 참조하지 않음)

### 11.6 `MyBatisConfig.java` — 변경 불필요

Spring Boot 가 `application-*.yml` 의 `driver-class-name` 과 `url` 을 자동 감지하여 DataSource 빈을 생성합니다. `MyBatisConfig.java` 는 주입된 DataSource 를 그대로 사용하므로 변경 불필요.

```java
// 변경 불필요 — Spring Boot 가 환경별 YAML 을 자동 로드
@Bean
public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);  // ← 자동 주입됨
    ...
}
```

### 11.7 `RefCommonService.java` — JNDI lookup 존재

`RefCommonService.java` 126 번 라인에 JNDI lookup 이 있습니다:
```java
dataSource = (DataSource) context.lookup("java:comp/env/jdbc/OPENAPI");
```

이 부분은 `context.xml` 의 JNDI DataSource 를 참조하므로, `context.xml` 이 PostgreSQL 로 변경되면 자동으로 연동됩니다. **별도 변경 불필요**.

### 11.8 Dockerfile — PostgreSQL JDBC JAR 포함

현재 WAR 패키징 시 `mssql-jdbc` 가 Spring Boot Maven plugin 의 `includeSystemScope=true` 로 포함됩니다. `pom.xml` 에서 `postgresql` 로 교체하면 동일하게 WAR 에 포함됩니다. **별도 작업 불필요**.

### 11.9 설정 변경 체크리스트

| # | 파일 | 변경 항목 | 상태 |
|---|------|----------|------|
| 1 | `pom.xml` | `mssql-jdbc` → `postgresql` | ✅ 완료 (2026-06-05) |
| 2 | `config/local/application-local.yml` | driver + URL | ✅ 완료 (2026-06-05) |
| 3 | `config/tb/application-tb.yml` | driver + URL | ✅ 완료 (플레이스홀더, 실제 계정 필요) |
| 4 | `config/prod/application-prod.yml` | driver + URL | ✅ 완료 (플레이스홀더, 실제 계정 필요) |
| 5 | `context.xml` | driver + URL | ✅ 완료 (2026-06-05) |
| 6 | `MyBatisConfig.java` | 변경 불필요 | ✅ |
| 7 | `RefCommonService.java` | 변경 불필요 (JNDI 자동 연동) | ✅ |

모두 단순 치환 작업입니다. DB 서버가 준비되면 즉시 적용 가능.
