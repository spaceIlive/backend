# SKETCH APP - REST API 문서

## 1차 목표(기본 기능)

- 다양한 색상 및 굵기로 그림을 그릴 수 있는 그리기 도구
- 지우개 기능 (굵기 설정 가능)
- Redo/Undo 기능
- 그리기 내용을 저장하고 불러오기
- 사용자 계정 관리 (추가, 삭제)
- 폴더 별로 그림 저장

## 🔄 사용자 워크플로우

1. **회원가입** → 자동으로 **"문서"** 루트 폴더 생성
2. **로그인** → 폴더 구조 조회
3. **폴더 생성** → 하위 폴더 추가
4. **스케치 생성** → 폴더 내에서 그림 작업

## 기능 별 API 문서

### 그리기 기능 (/api/drawings)

#### 1. 새로운 스케치 생성
- **엔드포인트**: `POST /api/drawings`
- **요청**:
  - Parameters:
    ```
    title: String (필수)
    content: String (필수)
    folderId: Long (필수)
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "drawing_id": Long,
      "title": String,
      "content": String,
      "folder_id": Long,
      "createdAt": LocalDateTime,
      "updatedAt": LocalDateTime
    }
    ```
  - 실패 (400 Bad Request)

#### 2. 스케치 수정
- **엔드포인트**: `PUT /api/drawings/{drawing_id}`
- **요청**:
  - Body:
    ```json
    {
      "title": String,
      "content": String
    }
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "drawing_id": Long,
      "title": String,
      "content": String,
      "folder_id": Long,
      "createdAt": LocalDateTime,
      "updatedAt": LocalDateTime
    }
    ```
  - 실패 (400 Bad Request)

#### 3. 폴더 내 모든 스케치 조회
- **엔드포인트**: `GET /api/drawings/folder/{folderId}`
- **응답**:
  - 성공 (200 OK):
    ```json
    [
      {
        "drawing_id": Long,
        "title": String,
        "content": String,
        "folder_id": Long,
        "createdAt": LocalDateTime,
        "updatedAt": LocalDateTime
      }
    ]
    ```
  - 실패 (400 Bad Request)

#### 4. 스케치 삭제
- **엔드포인트**: `DELETE /api/drawings/{drawing_id}`
- **응답**:
  - 성공 (204 No Content)
  - 실패 (400 Bad Request)

### 계정 관리 (/api/users)

#### 1. 회원가입
- **엔드포인트**: `POST /api/users`
- **설명**: 회원가입과 동시에 **"문서"** 루트 폴더가 자동 생성됩니다.
- **요청**:
  - Body:
    ```json
    {
      "email": String,
      "password": String,
      "name": String
    }
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "id": Long,
      "username": String,
      "email": String
    }
    ```
  - 실패 (400 Bad Request): "이미 등록된 이메일입니다."

#### 2. 로그인
- **엔드포인트**: `POST /api/users/login`
- **요청**:
  - Body:
    ```json
    {
      "email": String,
      "password": String
    }
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "id": Long,
      "username": String,
      "email": String
    }
    ```
  - 실패 (401 Unauthorized)

#### 3. 비밀번호 변경
- **엔드포인트**: `PATCH /api/users/{id}/password`
- **요청**:
  - Body:
    ```json
    {
      "currentPassword": String,
      "newPassword": String
    }
    ```
- **응답**:
  - 성공 (200 OK)
  - 실패 (400 Bad Request): "현재 비밀번호가 일치하지 않습니다."

#### 4. 사용자 삭제
- **엔드포인트**: `DELETE /api/users/{id}`
- **설명**: 사용자와 연관된 모든 폴더 및 스케치가 함께 삭제됩니다.
- **응답**:
  - 성공 (204 No Content)
  - 실패 (400 Bad Request)

### 스케치 맞추기 게임 (/api/sketch)

#### 1. 스케치 분석 (Claude 3.5 LLM)
- **엔드포인트**: `POST /api/sketch/analyze`
- **설명**: 업로드된 PNG 이미지를 Claude 3.5 LLM으로 분석하여 그림이 무엇인지 추측하는 스케치 맞추기 게임입니다.
- **요청**:
  - Content-Type: `multipart/form-data`
  - Parameters:
    ```
    image: MultipartFile (필수, PNG 파일만 허용, 최대 10MB)
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "success": true,
      "message": "스케치 분석이 완료되었습니다!",
      "guess": "고양이",
      "confidence": 85,
      "otherPossibilities": ["강아지", "토끼", "햄스터"],
      "reason": "둥근 귀와 수염이 보이며, 네 다리로 앉아있는 모습이 고양이와 매우 유사합니다.",
      "drawingQualityScore": 78,
      "creativityScore": 65,
      "overallScore": 75,
      "overallReason": "기본적인 고양이의 특징은 잘 표현되었으나, 세부적인 털의 질감이나 음영 표현이 부족합니다."
    }
    ```
  - 실패 (400 Bad Request):
    ```json
    {
      "success": false,
      "message": "PNG 파일만 업로드 가능합니다.",
      "guess": null,
      "confidence": null,
      "otherPossibilities": null,
      "reason": null,
      "drawingQualityScore": null,
      "creativityScore": null,
      "overallScore": null,
      "overallReason": null
    }
    ```
  - 실패 (500 Internal Server Error):
    ```json
    {
      "success": false,
      "message": "이미지 분석 중 오류가 발생했습니다: [오류 메시지]",
      "guess": null,
      "confidence": null,
      "otherPossibilities": null,
      "reason": null,
      "drawingQualityScore": null,
      "creativityScore": null,
      "overallScore": null,
      "overallReason": null
    }
    ```

#### 응답 필드 설명
- `success`: 분석 성공 여부 (boolean)
- `message`: 결과 메시지 (string)
- `guess`: AI가 추측한 답 (string)
- `confidence`: 확신도 1-100% (integer)
- `otherPossibilities`: 다른 가능성들 (string array)
- `reason`: 추측 이유/설명 (string)
- `drawingQualityScore`: 그림 품질 점수 0-100점 (integer) - 선의 정확성, 비례, 완성도, 세밀함 등
- `creativityScore`: 창의성 점수 0-100점 (integer) - 독창적 표현, 아이디어, 색상/구성의 참신함 등
- `overallScore`: 전체 평가 점수 0-100점 (integer) - 품질과 창의성을 종합한 전반적 평가
- `overallReason`: 종합 평가 이유 (string) - 전체 점수에 대한 상세 설명

### 폴더 관리 (/api/folders)

#### 1. 전체 폴더 구조 조회
- **엔드포인트**: `GET /api/folders/structure/{userId}`
- **설명**: 사용자의 모든 폴더와 스케치를 계층적으로 조회합니다.
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "folder_id": Long,
      "name": "문서",
      "parent_id": null,
      "user_id": Long,
      "children": [
        {
          "folder_id": Long,
          "name": String,
          "parent_id": Long,
          "user_id": Long,
          "children": [],
          "drawings": [],
          "createdAt": LocalDateTime,
          "updatedAt": LocalDateTime
        }
      ],
      "drawings": [
        {
          "drawing_id": Long,
          "title": String,
          "content": String,
          "folder_id": Long,
          "createdAt": LocalDateTime,
          "updatedAt": LocalDateTime
        }
      ],
      "createdAt": LocalDateTime,
      "updatedAt": LocalDateTime
    }
    ```
  - 실패 (400 Bad Request)

#### 2. 폴더 생성
- **엔드포인트**: `POST /api/folders`
- **설명**: 새로운 폴더를 생성합니다. parentId가 null이면 루트 폴더 하위에 생성됩니다.
- **요청**:
  - Parameters:
    ```
    userId: Long (필수)
    name: String (필수)
    parentId: Long (선택, null이면 루트 폴더 아래 생성)
    ```
- **응답**:
  - 성공 (200 OK):
    ```json
    {
      "folder_id": Long,
      "name": String,
      "parent_id": Long,
      "user_id": Long,
      "children": [],
      "drawings": [],
      "createdAt": LocalDateTime,
      "updatedAt": LocalDateTime
    }
    ```
  - 실패 (400 Bad Request)

#### 3. 폴더 삭제
- **엔드포인트**: `DELETE /api/folders/{folderId}`
- **설명**: 폴더와 하위의 모든 폴더 및 스케치가 함께 삭제됩니다. 루트 폴더("문서")는 삭제할 수 없습니다.
- **응답**:
  - 성공 (204 No Content)
  - 실패 (400 Bad Request)


## 📋 API 사용 예시

### 새 사용자 등록부터 스케치 생성까지

```http
# 1. 회원가입 (자동으로 "문서" 폴더 생성)
POST /api/users
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}

# 2. 로그인
POST /api/users/login
{
  "email": "user@example.com",
  "password": "password123"
}

# 3. 폴더 구조 확인
GET /api/folders/structure/1

# 4. 프로젝트 폴더 생성
POST /api/folders?userId=1&name=내프로젝트&parentId=1

# 5. 스케치 생성
POST /api/drawings?title=첫번째작품&content=스케치데이터&folderId=2

# 6. 스케치 맞추기 게임 (Claude 3.5 LLM)
POST /api/sketch/analyze
Content-Type: multipart/form-data
image: [PNG 파일]
```

### 스케치 맞추기 게임 사용 예시

```http
# 스케치 분석 요청
POST /api/sketch/analyze
Content-Type: multipart/form-data

# FormData로 PNG 파일 전송
image: [PNG 파일 바이너리]

# 응답 예시
200 OK
{
  "success": true,
  "message": "스케치 분석이 완료되었습니다!",
  "guess": "나무",
  "confidence": 92,
  "otherPossibilities": ["꽃", "식물", "풀"],
  "reason": "직선적인 줄기와 위쪽의 둥근 형태가 나무의 특징과 일치합니다.",
  "drawingQualityScore": 85,
  "creativityScore": 70,
  "overallScore": 80,
  "overallReason": "나무의 기본 구조는 명확하게 표현되었으나, 잎사귀나 나무껍질 등의 세부 표현이 아쉽습니다."
}
```

```

### 공통 에러 응답

- 400 Bad Request: 잘못된 요청 (요청 형식 오류, 유효성 검증 실패 등)
- 401 Unauthorized: 인증 실패
- 403 Forbidden: 권한 없음
- 404 Not Found: 리소스를 찾을 수 없음
- 500 Internal Server Error: 서버 내부 오류 

## 🔧 서버 실행 가이드

### 1. 필수 프로그램 설치 (있다면 패스)
- JDK 17
- MySQL workbench 8.0
- Maven

### 2. MySQL 데이터베이스 설정
```sql
-- MySQL Workbench 명령줄에서(쿼리부분)에서 아래 명령어 실행:

CREATE DATABASE sketch_app;

CREATE USER 'admin'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON sketch_app.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 환경변수 설정 (Claude API)
application.yml에서 직접 설정:
```yaml
claude:
  api:
    key: your-actual-claude-api-key-here
```

### 4. 서버 실행
```
# backend 폴더에서 실행
mvn spring-boot:run
```

### 5. 테스트 진행은 위에 api문서 참고해서 진행
- 서버 실행하면 기본적으로 http://yonsei-sketch:8080 에서 실행중
- 뒤에 api 붙여서 테스트 해보면 됩니다.
- 스케치 맞추기 게임 기능을 사용하려면 Claude API 키가 필요합니다.


