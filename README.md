# App Backend

Spring Boot 기반 백엔드 서버입니다. 현재 **기획 확정 전 개발 환경 세팅 단계**이며,
서버·DB·보안·문서화·배포의 기본 골격을 잡아둔 상태입니다.

> 최종 정리: 2026-06-09

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.5.14 |
| 빌드 도구 | Gradle |
| 데이터베이스 | MySQL 8.4 |
| ORM | Spring Data JPA (Hibernate) |
| 보안 | Spring Security (Stateless) + JWT(jjwt 0.12.5) |
| API 문서화 | Swagger (springdoc-openapi 2.8.6) |
| 푸시 알림 | FCM (firebase-admin 9.3.0) |
| 클라우드 | AWS (RDS, 배포 예정) |
| 로컬 DB 실행 | Docker (docker-compose) |

---

## 환경 구성 한눈에 보기

```
[ 로컬 개발 PC ]                         [ AWS (배포) ]
  IntelliJ ▶ 앱 실행                       EC2/컴퓨팅 (예정)
       │  (local 프로파일)                      │  (dev 프로파일)
       ▼                                        ▼
  Docker MySQL 8.4                          AWS RDS MySQL
  (docker compose)                          (record-db, us-east-1)
```

- **로컬 개발**: 앱은 IntelliJ로 실행, DB는 **Docker 컨테이너**(MySQL 8.4)로 통일
- **배포**: `dev` 프로파일이 환경변수로 **AWS RDS** 에 연결 (서버 배포는 예정)
- 코드는 **GitHub** 으로 관리 (DB와 무관)

---

## 로컬 개발 환경 세팅

### 1. 사전 준비물

| 도구 | 설치 |
|------|------|
| JDK 21 | (예: `brew install --cask temurin@21`) |
| Docker | `brew install --cask orbstack` 또는 Docker Desktop |
| IntelliJ IDEA | JetBrains |

> Docker는 설치 후 앱(OrbStack/Docker Desktop)을 한 번 실행해 데몬을 켜야 합니다.

### 2. 레포지토리 클론

```bash
git clone <레포 주소>
cd app_backend
```

### 3. Docker로 MySQL 실행 (로컬 DB)

로컬 DB는 **Docker로 통일**합니다. MySQL을 직접 설치할 필요 없이 한 줄이면 됩니다.

```bash
docker compose up -d      # MySQL 8.4 컨테이너 시작 (백그라운드)
docker compose ps         # STATUS가 healthy 면 정상
```

`docker-compose.yml`이 `application-local.yml`과 동일하게 맞춰져 있어
**DB `app_db` / 계정 `root` / 비번 `1234` / 포트 `3306`** 으로 바로 뜨고,
`schema.sql`도 자동 실행됩니다. 데이터는 볼륨에 보존됩니다.

자주 쓰는 명령:

```bash
docker compose logs -f db   # 로그 보기
docker compose down         # 종료 (데이터 유지)
docker compose down -v      # 데이터까지 완전 초기화
```

> ⚠️ 로컬에 별도 MySQL이 깔려 3306을 쓰고 있으면 Docker가 무시됩니다.
> 그 경우 기존 MySQL을 끄거나(시스템 설정 → MySQL → Stop),
> compose 포트를 `"3307:3306"`으로 바꾸고 `application-local.yml`도 3307로 맞추세요.

### 4. 서버 실행

**IntelliJ:** `AppBackendApplication.java` → ▶ 실행
(실행 구성의 **Active profiles 는 `local`** 이어야 합니다. `dev`면 RDS로 붙으려다 실패)

**터미널:**
```bash
./gradlew bootRun
```

### 5. 실행 확인

- 헬스체크: http://localhost:8080/api/health → `{"status":"UP", ...}`
- Swagger UI: http://localhost:8080/swagger-ui/index.html

정상 로그 예시:
```
The following 1 profile is active: "local"
HikariPool-1 - Added connection ...      ← Docker DB 연결 성공
Tomcat started on port 8080
Started AppBackendApplication
```

---

## 환경 프로파일

| 프로파일 | 용도 | DB 연결 | 설정 파일 |
|---------|------|--------|----------|
| `local` | 로컬 개발 (기본값) | Docker MySQL (localhost:3306, root/1234) | `application-local.yml` |
| `dev` | AWS 배포 서버 | AWS RDS (환경변수 `RDS_ENDPOINT` 등) | `application-dev.yml` |

`application.yml`의 기본 프로파일이 `local`이라 **별도 설정 없이 바로 실행**됩니다.

배포(`dev`) 시 필요한 환경변수:

| 환경변수 | 설명 |
|---------|------|
| `RDS_ENDPOINT` / `RDS_USERNAME` / `RDS_PASSWORD` | AWS RDS 접속 정보 |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) |
| `FCM_CREDENTIALS_FILE` | Firebase 서비스 계정 JSON 경로 |

---

## 프로젝트 구조

```
app_backend/
├── build.gradle                 # 의존성/빌드 설정 (Gradle)
├── docker-compose.yml           # 로컬 MySQL 8.4 컨테이너
├── Dockerfile                   # 앱 이미지 빌드 (배포용)
├── README.md
├── docs/                        # 구축 정리 문서
└── src/main/
    ├── java/com/app/backend/
    │   ├── AppBackendApplication.java   # 진입점
    │   ├── config/
    │   │   └── SecurityConfig.java      # 보안 설정 (JWT 필터 추가 예정)
    │   ├── controller/
    │   │   ├── HealthController.java    # GET /api/health
    │   │   └── TestItemController.java  # 샘플(테스트용)
    │   ├── service/      └ TestItemService.java   # 샘플
    │   ├── repository/   └ TestItemRepository.java # 샘플
    │   ├── entity/       └ TestItem.java           # 샘플
    │   └── dto/response/ └ TestItemResponse.java   # 샘플
    └── resources/
        ├── application.yml            # 공통 (기본 프로파일: local)
        ├── application-local.yml      # 로컬 DB (Docker)
        ├── application-dev.yml        # 배포 DB (RDS)
        └── sql/schema.sql             # DB 초기화 SQL
```

> `TestItem*` 는 구조 검증용 샘플 코드입니다. 실제 도메인 확정 후 교체/삭제됩니다.

---

## 구축 현황

### ✅ 완료
- [x] Spring Boot 3.5.14 프로젝트 기본 골격 (Gradle, Java 21)
- [x] MySQL 연동 (JPA) — 로컬은 **Docker**, 배포는 **AWS RDS**
- [x] 환경별 프로파일 분리 (`local` / `dev`)
- [x] Spring Security 기본 설정 (Stateless, CSRF off, 경로 허용)
- [x] JWT 의존성 및 설정값 세팅
- [x] Swagger UI
- [x] FCM 의존성 추가
- [x] 헬스체크 API (`GET /api/health`)
- [x] Docker 기반 로컬 DB 환경 통일 (`docker-compose.yml`)
- [x] 배포용 `Dockerfile`

### 🔜 기획 확정 후 진행 (미완성)
- [ ] JWT 발급/검증 필터 + 회원/인증 도메인 (User, 회원가입/로그인 API)
- [ ] (필요 시) OAuth2 소셜 로그인 (카카오/구글) — 현재 의존성 미추가
- [ ] FCM 초기화 설정 + 푸시 발송 서비스
- [ ] Swagger OpenAPI 설정 빈 (JWT 보안 스킴 등)
- [ ] 글로벌 예외 처리(`@RestControllerAdvice`), CORS 설정
- [ ] 실제 비즈니스 도메인 API (Entity/Service/Repository)
- [ ] AWS 서버 배포 (EC2/ECS 등) + CI/CD

---


## 현재 API 목록

| 메서드 | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api/health` | 불필요 | 서버 상태 확인 |
| GET | `/api/test/items` | 불필요 | 샘플(구조 검증용) |