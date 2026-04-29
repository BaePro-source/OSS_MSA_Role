# 중고마켓 — MSA 기반 중고거래 플랫폼

Spring Boot + FastAPI + Vue 3으로 구성된 마이크로서비스 아키텍처 학습 프로젝트입니다.  
JWT 기반 인증, 실시간 채팅, AI 기반 상품 추천·신뢰도 평가·리뷰 보조·정책 검증 기능을 포함합니다.

---

## 아키텍처

```
브라우저
  │
  ├── :3000  user-web  (Vue 3 + nginx)
  └── :3001  admin-web (Vue 3 + nginx)
        │
        ▼ /api/**, /ws/**
  :8080  gateway-service  (Spring Cloud Gateway + JWT 검증)
        │
        ├── /api/auth/**       → :8081  auth-service
        ├── /api/products/**   → :8082  product-service
        ├── /api/chat/**       → :8083  chat-service
        ├── /ws/**             → :8083  chat-service  (WebSocket)
        ├── /api/reviews/**    → :8084  review-service
        ├── /api/trades/**     → :8085  trade-history-service
        ├── /api/admin/**      → :8086  admin-service
        │
        ├── /api/ai/recommend/** → :8090  ai-recommendation
        ├── /api/ai/trust/**     → :8091  ai-trust
        ├── /api/ai/review-writer/** → :8092  ai-review-writer
        └── /api/ai/policy/**    → :8093  ai-policy
```

> 게이트웨이가 JWT를 검증하고 `X-User-Id`, `X-User-Role` 헤더를 하위 서비스로 전달합니다.  
> 하위 서비스는 별도의 JWT 파싱 없이 해당 헤더를 신뢰합니다.

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| 인증 | JWT (jjwt 0.12) · Redis (Refresh Token 저장) |
| DB | SQLite (서비스별 독립 파일 볼륨) |
| AI 서비스 | Python FastAPI + OpenAI GPT-4o |
| Frontend | Vue 3, Vite, Pinia, Vue Router |
| 실시간 채팅 | WebSocket (STOMP over SockJS) |
| 인프라 | Docker Compose, nginx |

---

## 서비스 목록

| 서비스 | 포트 | 설명 |
|--------|------|------|
| gateway-service | 8080 | JWT 인증 게이트웨이 · 라우팅 |
| auth-service | 8081 | 회원가입 · 로그인 · 토큰 갱신 |
| product-service | 8082 | 상품 등록 · 조회 · 위치 기반 검색 |
| chat-service | 8083 | 채팅방 생성 · 실시간 메시지 (STOMP) |
| review-service | 8084 | 리뷰 작성 · 평점 조회 · 저신뢰 사용자 탐지 |
| trade-history-service | 8085 | 거래 이력 기록 · 조회 |
| admin-service | 8086 | 관리자 기능 (게시글 삭제 · 저신뢰 사용자 조회) |
| ai-recommendation | 8090 | GPT 기반 채팅/거래이력 상품 추천 |
| ai-trust | 8091 | 리뷰 기반 판매자 신뢰도 평가 |
| ai-review-writer | 8092 | 키워드 → 리뷰 텍스트 생성 |
| ai-policy | 8093 | 게시글·채팅·리뷰 정책 위반 검증 |
| user-web | 3000 | 사용자용 Vue 3 프론트엔드 |
| admin-web | 3001 | 관리자용 Vue 3 프론트엔드 |

---

## 실행 방법

### 1. 환경 변수 설정

```bash
cp .env.example .env
```

`.env` 파일에 아래 값을 채워주세요.

```env
JWT_SECRET=<256비트 이상의 랜덤 문자열>
OPENAI_API_KEY=sk-...
```

### 2. 전체 실행

```bash
docker compose up --build
```

| URL | 설명 |
|-----|------|
| http://localhost:3000 | 사용자 웹 |
| http://localhost:3001 | 관리자 웹 |
| http://localhost:8080 | API Gateway |

### 3. 테스트 계정

| 구분 | 이메일 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin@gmail.com | admin123 |
| 일반 사용자 | 회원가입 후 사용 | - |

> 관리자 계정으로 `http://localhost:3001` (관리자 웹)에 로그인하면 게시글 삭제, 사용자 관리 기능을 사용할 수 있습니다.

### 4. Swagger UI

서비스별 API 문서는 각 서비스 포트의 `/swagger-ui/index.html`에서 확인할 수 있습니다.

| 서비스 | Swagger UI |
|--------|-----------|
| auth-service | http://localhost:8081/swagger-ui/index.html |
| product-service | http://localhost:8082/swagger-ui/index.html |
| chat-service | http://localhost:8083/swagger-ui/index.html |
| review-service | http://localhost:8084/swagger-ui/index.html |
| trade-history-service | http://localhost:8085/swagger-ui/index.html |
| admin-service | http://localhost:8086/swagger-ui/index.html |

### 5. 개별 서비스 재빌드

```bash
docker compose build <서비스명>
docker compose up -d <서비스명>
```

---

## API

> 모든 REST 요청은 `http://localhost:8080` 기준입니다.  
> 인증이 필요한 엔드포인트는 `Authorization: Bearer <accessToken>` 헤더가 필요합니다.

### 인증 (`/api/auth`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| POST | /api/auth/register | | 회원가입 → accessToken + refreshToken |
| POST | /api/auth/login | | 로그인 → accessToken + refreshToken |
| POST | /api/auth/refresh | | refreshToken으로 토큰 갱신 |
| POST | /api/auth/logout | ✓ | 로그아웃 (Redis Refresh Token 삭제) |
| GET | /api/auth/me | ✓ | 내 정보 조회 |

### 상품 (`/api/products`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| GET | /api/products | | 전체 상품 목록 |
| POST | /api/products | ✓ | 상품 등록 |
| GET | /api/products/{id} | | 상품 상세 |
| PUT | /api/products/{id} | ✓ | 상품 수정 (본인만) |
| DELETE | /api/products/{id} | ✓ | 상품 삭제 (본인만) |
| PATCH | /api/products/{id}/status | ✓ | 상태 변경 (SALE/RESERVED/SOLD) |
| GET | /api/products/nearby?lat=&lng=&radius= | | 위치 기반 상품 조회 |
| GET | /api/products/search?keyword= | | 키워드 검색 |
| GET | /api/products/seller/{sellerId} | | 판매자별 상품 조회 |

### 채팅 (`/api/chat`, `/ws`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| POST | /api/chat/rooms | ✓ | 채팅방 생성 또는 기존 방 조회 |
| GET | /api/chat/rooms | ✓ | 내 채팅방 목록 |
| GET | /api/chat/rooms/{roomId}/messages | ✓ | 채팅 내역 조회 |
| WS | /ws/chat | ✓ | WebSocket 연결 (SockJS) |
| STOMP | /app/chat.send | ✓ | 메시지 전송 |
| STOMP | /topic/room/{roomId} | ✓ | 채팅방 구독 |

### 리뷰 (`/api/reviews`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| POST | /api/reviews | ✓ | 리뷰 작성 |
| GET | /api/reviews/user/{targetId} | | 사용자별 리뷰 목록 |
| GET | /api/reviews/user/{targetId}/summary | | 평균 평점 · 총 리뷰 수 |
| GET | /api/reviews/low-rated?threshold= | ✓ | 저평점 사용자 목록 (관리자용) |

### 거래 이력 (`/api/trades`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| POST | /api/trades | ✓ | 거래 이력 기록 |
| GET | /api/trades/sold | ✓ | 내 판매 이력 |
| GET | /api/trades/bought | ✓ | 내 구매 이력 |
| GET | /api/trades/buyer/{buyerId} | | 구매 이력 조회 (AI 서비스용) |
| GET | /api/trades/{id} | | 거래 상세 |

### 관리자 (`/api/admin`)

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|:----:|------|
| DELETE | /api/admin/products/{productId} | ✓ ADMIN | 게시글 강제 삭제 |
| GET | /api/admin/users/low-rated?threshold= | ✓ ADMIN | 저신뢰 사용자 조회 |
| GET | /api/admin/health | | 서비스 상태 확인 |

### AI (`/api/ai`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | /api/ai/recommend/chat | 채팅 메시지 기반 상품 추천 |
| POST | /api/ai/recommend/history | 거래 이력 기반 상품 추천 |
| POST | /api/ai/trust/evaluate | 리뷰 데이터 기반 판매자 신뢰도 평가 |
| POST | /api/ai/review-writer/complete | 키워드 + 상품명 → 리뷰 텍스트 생성 |
| POST | /api/ai/policy/validate | 게시글·채팅·리뷰 정책 위반 여부 검증 |

---

## 프론트엔드 화면

| 경로 | 설명 | 인증 |
|------|------|:----:|
| / | 홈 (최근 상품 목록) | |
| /login | 로그인 | |
| /register | 회원가입 | |
| /products | 상품 목록 | |
| /products/:id | 상품 상세 · 채팅 연결 | |
| /products/new | 상품 등록 | ✓ |
| /chat | 내 채팅방 목록 · 실시간 채팅 | ✓ |
| /reviews/new | 리뷰 작성 | ✓ |
| /profile | 내 프로필 · 등록 상품 목록 | ✓ |

---

## 프로젝트 구조

```
OSS_MSA_ROLE/
├── docker-compose.yml
├── .env.example
├── backend/
│   ├── gateway-service/      # Spring Cloud Gateway
│   ├── auth-service/         # JWT 인증
│   ├── product-service/      # 상품 관리
│   ├── chat-service/         # 채팅 (STOMP WebSocket)
│   ├── review-service/       # 리뷰 · 평점
│   ├── trade-history-service/# 거래 이력
│   └── admin-service/        # 관리자 기능
├── ai-service/
│   ├── recommendation/       # FastAPI · 상품 추천
│   ├── trust-evaluation/     # FastAPI · 신뢰도 평가
│   ├── review-writer/        # FastAPI · 리뷰 생성
│   └── policy-validation/    # FastAPI · 정책 검증
└── frontend/
    ├── user-web/             # Vue 3 · 사용자 UI
    └── admin-web/            # Vue 3 · 관리자 UI
```

---

## Author

**BaePro**
