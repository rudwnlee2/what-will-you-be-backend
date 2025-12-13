# What Will You Be - API 문서

진로 추천 시스템 백엔드 API 명세서입니다.

## 📋 기본 정보

- **Base URL**: `http://localhost:8080`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`

## 🔐 인증

JWT 토큰이 필요한 API는 🔒 표시되어 있습니다.

### 토큰 사용법
```
Authorization: Bearer <your-jwt-token>
```

---

## 👤 회원 관리 API

### 1. 회원가입
```http
POST /api/members/signup
```

**Request Body:**
```json
{
  "loginId": "user123",
  "password": "password123!",
  "name": "홍길동",
  "email": "user@example.com",
  "birth": "2000-01-01",
  "gender": "MALE",
  "phone": "010-1234-5678",
  "school": "서울대학교"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "loginId": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "birth": "2000-01-01",
  "gender": "MALE",
  "phone": "010-1234-5678",
  "school": "서울대학교",
  "createdDate": "2024-12-20T10:30:00"
}
```

### 2. 로그인
```http
POST /api/members/login
```

**Request Body:**
```json
{
  "loginId": "user123",
  "password": "password123!"
}
```

**Response (200 OK):**
- **Headers**: `Authorization: Bearer <jwt-token>`
- **Body**: 빈 응답

### 3. 아이디 중복 확인
```http
GET /api/members/check-loginid/{loginId}
```

**Response (200 OK):**
```json
{
  "exists": false
}
```

### 4. 내 정보 조회 🔒
```http
GET /api/members/me
```

**Response (200 OK):**
```json
{
  "id": 1,
  "loginId": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "birth": "2000-01-01",
  "gender": "MALE",
  "phone": "010-1234-5678",
  "school": "서울대학교",
  "createdDate": "2024-12-20T10:30:00"
}
```

### 5. 내 정보 수정 🔒
```http
PATCH /api/members/me
```

**Request Body:**
```json
{
  "loginId": "user123",
  "password": "newpassword123!",
  "name": "홍길동",
  "email": "newemail@example.com",
  "birth": "2000-01-01",
  "gender": "MALE",
  "phone": "010-9876-5432",
  "school": "연세대학교"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "loginId": "user123",
  "name": "홍길동",
  "email": "newemail@example.com",
  "birth": "2000-01-01",
  "gender": "MALE",
  "phone": "010-9876-5432",
  "school": "연세대학교",
  "createdDate": "2024-12-20T10:30:00"
}
```

### 6. 회원 탈퇴 🔒
```http
DELETE /api/members/me
```

**Response (204 No Content):**
- 빈 응답

---

## 📝 추천 정보 관리 API

### 1. 추천 정보 조회 🔒
```http
GET /api/recommendation-info
```

**Response (200 OK):**
```json
{
  "dream": "소프트웨어 개발자",
  "dreamReason": "기술로 세상을 바꾸고 싶어서",
  "interest": "프로그래밍, 인공지능",
  "jobValue": "CREATIVITY",
  "mbti": "INTJ",
  "hobby": "코딩, 독서",
  "favoriteSubject": "수학, 과학",
  "holland": "INVESTIGATIVE"
}
```

### 2. 추천 정보 등록/수정 🔒
```http
PUT /api/recommendation-info
```

**Request Body:**
```json
{
  "dream": "소프트웨어 개발자",
  "dreamReason": "기술로 세상을 바꾸고 싶어서",
  "interest": "프로그래밍, 인공지능",
  "jobValue": "CREATIVITY",
  "mbti": "INTJ",
  "hobby": "코딩, 독서",
  "favoriteSubject": "수학, 과학",
  "holland": "INVESTIGATIVE"
}
```

**Response (200 OK):**
```json
{
  "dream": "소프트웨어 개발자",
  "dreamReason": "기술로 세상을 바꾸고 싶어서",
  "interest": "프로그래밍, 인공지능",
  "jobValue": "CREATIVITY",
  "mbti": "INTJ",
  "hobby": "코딩, 독서",
  "favoriteSubject": "수학, 과학",
  "holland": "INVESTIGATIVE"
}
```

---

## 🎯 직업 추천 API

### 1. 직업 추천 생성 🔒
```http
POST /api/job-recommendations
```

**Request Body:** 없음 (사용자의 추천 정보를 자동으로 사용)

**Response (200 OK):**
```json
[
  {
    "recommendationId": 1,
    "jobName": "백엔드 개발자",
    "recommendedAt": "2024-12-20T14:30:00",
    "jobSummary": "서버 시스템과 데이터베이스를 설계하고 개발하는 직업",
    "reason": "INTJ 성향과 프로그래밍 관심사가 잘 맞음",
    "relatedMajors": "컴퓨터공학, 소프트웨어공학",
    "relatedCertificates": "정보처리기사, AWS 자격증",
    "salary": "4000-8000만원",
    "prospect": "매우 좋음",
    "requiredKnowledge": "Java, Spring, 데이터베이스",
    "careerPath": "대학에서 컴퓨터공학 전공 후 실무 경험 쌓기",
    "environment": "사무실, 재택근무 가능",
    "jobValues": "창조성, 안정성"
  }
]
```

### 2. 내 직업 추천 목록 조회 🔒
```http
GET /api/job-recommendations?page=0&size=12
```

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 12)

**Response (200 OK):**
```json
{
  "content": [
    {
      "recommendationId": 1,
      "jobName": "백엔드 개발자",
      "recommendedAt": "2024-12-20T14:30:00"
    },
    {
      "recommendationId": 2,
      "jobName": "데이터 사이언티스트",
      "recommendedAt": "2024-12-20T15:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 12
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### 3. 직업 추천 상세 조회 🔒
```http
GET /api/job-recommendations/{recommendationId}
```

**Response (200 OK):**
```json
{
  "recommendationId": 1,
  "jobName": "백엔드 개발자",
  "recommendedAt": "2024-12-20T14:30:00",
  "jobSummary": "서버 시스템과 데이터베이스를 설계하고 개발하는 직업",
  "reason": "INTJ 성향과 프로그래밍 관심사가 잘 맞음",
  "relatedMajors": "컴퓨터공학, 소프트웨어공학",
  "relatedCertificates": "정보처리기사, AWS 자격증",
  "salary": "4000-8000만원",
  "prospect": "매우 좋음",
  "requiredKnowledge": "Java, Spring, 데이터베이스",
  "careerPath": "대학에서 컴퓨터공학 전공 후 실무 경험 쌓기",
  "environment": "사무실, 재택근무 가능",
  "jobValues": "창조성, 안정성"
}
```

### 4. 직업 추천 삭제 🔒
```http
DELETE /api/job-recommendations/{recommendationId}
```

**Response (200 OK):**
- 빈 응답

---

## 🐍 Python AI API 연동

직업 추천 생성 시 내부적으로 Python AI 서버와 통신합니다.

### Python API 엔드포인트
```http
POST http://127.0.0.1:8000/api/recommend/
```

### 요청 데이터 (Java → Python)
```json
{
  "memberId": 1,
  "dream": "소프트웨어 개발자",
  "interest": "프로그래밍, 인공지능",
  "jobValue": "CREATIVITY",
  "mbti": "INTJ",
  "hobby": "코딩, 독서",
  "favoriteSubject": "수학, 과학",
  "holland": "INVESTIGATIVE"
}
```

### 응답 데이터 (Python → Java)
```json
{
  "memberId": 1,
  "recommendedJobs": [
    {
      "jobName": "백엔드 개발자",
      "jobSum": "서버 시스템과 데이터베이스를 설계하고 개발하는 직업",
      "way": "대학에서 컴퓨터공학 전공 후 실무 경험 쌓기",
      "major": "컴퓨터공학, 소프트웨어공학",
      "certificate": "정보처리기사, AWS 자격증",
      "pay": "4000-8000만원",
      "jobProspect": "매우 좋음",
      "knowledge": "Java, Spring, 데이터베이스",
      "jobEnvironment": "사무실, 재택근무 가능",
      "jobValues": "창조성, 안정성",
      "reason": "INTJ 성향과 프로그래밍 관심사가 잘 맞음"
    },
    {
      "jobName": "데이터 사이언티스트",
      "jobSum": "데이터를 분석하여 비즈니스 인사이트를 도출하는 직업",
      "way": "통계학이나 컴퓨터공학 전공 후 데이터 분석 경험 쌓기",
      "major": "통계학, 컴퓨터공학, 수학",
      "certificate": "ADsP, 빅데이터분석기사",
      "pay": "5000-9000만원",
      "jobProspect": "매우 좋음",
      "knowledge": "Python, R, 머신러닝, 통계",
      "jobEnvironment": "사무실, 연구소",
      "jobValues": "창조성, 성취감",
      "reason": "탐구형 성향과 수학 관심사가 데이터 분석에 적합"
    }
  ]
}
```

### 처리 흐름
1. 사용자가 `POST /api/job-recommendations` 호출
2. Java 서버가 사용자의 RecommendationInfo 조회
3. RecommendationInfo를 PythonApiRequestDto로 변환
4. Python AI 서버에 HTTP 요청 전송
5. Python 서버에서 AI 추천 결과 반환 (PythonApiResponseDto)
6. 추천 결과를 JobRecommendations 엔티티로 변환하여 DB 저장
7. 클라이언트에 JobRecommendationsResponseDto 형태로 응답

### 연동 요구사항
- **Python 서버 실행**: `http://127.0.0.1:8000`에서 실행 중이어야 함
- **사전 조건**: 사용자의 추천 정보가 미리 등록되어 있어야 함
- **타임아웃**: 30초 (RestClient 기본값)
- **에러 처리**: Python 서버 응답 실패 시 적절한 에러 메시지 반환

---

## ⚙️ 옵션 조회 API

### 1. 성별 옵션 조회
```http
GET /api/options/genders
```

**Response (200 OK):**
```json
[
  {
    "code": "MALE",
    "displayName": "남성"
  },
  {
    "code": "FEMALE",
    "displayName": "여성"
  }
]
```

### 2. 추천 관련 옵션 조회
```http
GET /api/options/recommendations
```

**Response (200 OK):**
```json
{
  "hollandTypes": [
    {
      "code": "REALISTIC",
      "displayName": "현실형",
      "description": "손으로 만지고 조작할 수 있는 구체적이고 물리적인 활동을 선호"
    },
    {
      "code": "INVESTIGATIVE",
      "displayName": "탐구형",
      "description": "관찰하고 분석하며 문제를 해결하는 활동을 선호"
    }
  ],
  "jobValues": [
    {
      "code": "STABILITY",
      "displayName": "안정성",
      "description": "안정적인 직장과 수입을 중시"
    },
    {
      "code": "CREATIVITY",
      "displayName": "창조성",
      "description": "새로운 아이디어와 창의적 활동을 중시"
    }
  ],
  "mbtiTypes": [
    {
      "code": "INTJ",
      "description": "건축가형 - 상상력이 풍부하고 전략적 사고를 하는 성격"
    },
    {
      "code": "ENFP",
      "description": "활동가형 - 열정적이고 창의적인 성격"
    }
  ]
}
```

---

## 📊 데이터 타입 정의

### Gender (성별)
- `MALE`: 남성
- `FEMALE`: 여성

### MBTI 유형
- `INTJ`, `INTP`, `ENTJ`, `ENTP`
- `INFJ`, `INFP`, `ENFJ`, `ENFP`
- `ISTJ`, `ISFJ`, `ESTJ`, `ESFJ`
- `ISTP`, `ISFP`, `ESTP`, `ESFP`

### Holland 유형
- `REALISTIC`: 현실형
- `INVESTIGATIVE`: 탐구형
- `ARTISTIC`: 예술형
- `SOCIAL`: 사회형
- `ENTERPRISING`: 진취형
- `CONVENTIONAL`: 관습형

### JobValue (직업 가치관)
- `STABILITY`: 안정성
- `CREATIVITY`: 창조성
- `ACHIEVEMENT`: 성취감
- `AUTONOMY`: 자율성
- `SOCIAL_CONTRIBUTION`: 사회기여
- `HIGH_INCOME`: 고수입
- `WORK_LIFE_BALANCE`: 워라밸

---

## ❌ 에러 응답

### 400 Bad Request
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "아이디를 입력해주세요.",
  "path": "/api/members/signup"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT 토큰이 필요합니다.",
  "path": "/api/members/me"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "해당 추천 기록을 찾을 수 없습니다.",
  "path": "/api/job-recommendations/999"
}
```

### 409 Conflict
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "이미 존재하는 아이디입니다.",
  "path": "/api/members/signup"
}
```

---