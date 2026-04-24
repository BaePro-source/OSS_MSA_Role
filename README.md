# 중고마켓 — MSA 기반 중고거래 플랫폼

Spring Boot + FastAPI + Vue 3으로 구성된 마이크로서비스 아키텍처 학습 프로젝트입니다.  
AI 기반 상품 추천, 판매자 신뢰도 평가, 정책 검증 기능을 포함합니다.

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
        ├── :8081  auth-service          (인증/회원)
        ├── :8082  product-service       (상품)
        ├── :8083  chat-service          (채팅 · WebSocket/STOMP)
        ├── :8084  review-service        (리뷰)
        ├── :8085  trade-history-service (거래 이력)
        ├── :8086  admin-service         (관리자)
        │
        ├── :8090  ai-recommendation     (AI 상품 추천)
        ├── :8091  ai-trust              (AI 판매자 신뢰도)
        ├── :8092  ai-review-writer      (AI 리뷰 작성 보조)
        └── :8093  ai-policy             (AI 게시글 정책 검증)
```

> 게이트웨이가 JWT를 검증하고 `X-User-Id`, `X-User-Role` 헤더를 하위 서비스로 전달합니다.  
> 하위 서비스는 별도의 JWT 파싱 없이 해당 헤더를 신뢰합니다.

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| 인증 | JWT (jjwt 0.12) · Redis (Refresh Token) |
| DB | SQLite (각 서비스 독립 파일) |
| AI 서비스 | Python FastAPI + OpenAI GPT-4o |
| Frontend | Vue 3, Vite, Pinia, Vue Router |
| 실시간 채팅 | WebSocket (STOMP over SockJS) |
| 인프라 | Docker Compose, nginx |

---

## 서비스 목록

| 서비스 | 내부 포트 | 설명 |
|--------|----------|------|
| gateway-service | 8080 | JWT 인증 게이트웨이 · 라우팅 |
| auth-service | 8081 | 회원가입 · 로그인 · 토큰 갱신 |
| product-service | 8082 | 상품 등록 · 조회 · 위치 기반 검색 |
| chat-service | 8083 | 채팅방 · 실시간 메시지 (STOMP) |
| review-service | 8084 | 리뷰 작성 · 평점 조회 |
| trade-history-service | 8085 | 거래 이력 기록 |
| admin-service | 8086 | 관리자 기능 (상품 삭제 · 저신뢰 사용자 조회) |
| ai-recommendation | 8090 | GPT 기반 상품 추천 |
| ai-trust | 8091 | 판매자 리뷰 기반 신뢰도 평가 |
| ai-review-writer | 8092 | 키워드 → 리뷰 텍스트 생성 |
| ai-policy | 8093 | 게시글 정책 위반 검증 |
| user-web | 3000 | 사용자용 Vue 3 프론트엔드 |
| admin-web | 3001 | 관리자용 Vue 3 프론트엔드 |

---

## 실행 방법

### 1. 환경 변수 설정

```bash
cp .env.example .env
```

`.env` 파일을 열어 아래 값을 채워주세요.

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

### 3. 개별 서비스 재빌드

```bash
docker compose build <서비스명>
docker compose up -d <서비스명>
```

---

## 주요 API

> 모든 요청은 `http://localhost:8080`을 기준으로 합니다.  
> 인증이 필요한 엔드포인트는 `Authorization: Bearer <accessToken>` 헤더가 필요합니다.

### 인증

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | /api/auth/register | 회원가입 |
| POST | /api/auth/login | 로그인 (accessToken + refreshToken 반환) |
| POST | /api/auth/refresh | 토큰 갱신 |
| POST | /api/auth/logout | 로그아웃 |

### 상품

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | /api/products/nearby?lat=&lng=&radius= | 위치 기반 상품 조회 |
| GET | /api/products/search?keyword= | 키워드 검색 |
| GET | /api/products/{id} | 상품 상세 |
| POST | /api/products | 상품 등록 (인증 필요) |

### AI

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | /api/ai/recommend/chat | 채팅 기반 상품 추천 |
| POST | /api/ai/trust/evaluate | 판매자 신뢰도 평가 |
| POST | /api/ai/review-writer/complete | 리뷰 텍스트 생성 |
| POST | /api/ai/policy/validate | 게시글 정책 검증 |

---

## 프로젝트 구조

```
OSS_MSA_ROLE/
├── docker-compose.yml
├── .env.example
├── backend/
│   ├── gateway-service/
│   ├── auth-service/
│   ├── product-service/
│   ├── chat-service/
│   ├── review-service/
│   ├── trade-history-service/
│   └── admin-service/
├── ai-service/
│   ├── recommendation/
│   ├── trust-evaluation/
│   ├── review-writer/
│   └── policy-validation/
└── frontend/
    ├── user-web/
    └── admin-web/
```

## Author

**BaePro**
