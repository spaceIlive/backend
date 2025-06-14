# SKETCH APP - Backend API TEST Plan

## Backend Unit Test Cases
SKETCH APP backend API의 검증은 postman을 활용한 unit test를 사용하고, postman의 command line Collection Runner인 newman을 사용하여 자동 검증을 실행한다.

### 계정 관리 API 테스트

API   | Test Name |  TEST Case ID  | Description | Test Data
---     |   --- |   --- | ---   | ---
POST /api/users    | 회원가입 성공 | TC01-1   |  <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 타입 검증: id, username, email 필드가 올바른 타입이면 pass</li><li>루트 폴더 자동 생성 확인</li> | {"email": "test@example.com", "password": "password123", "name": "테스트사용자"}
POST /api/users   | 회원가입 실패-중복 이메일 | TC01-2   |  <li>Status code is 400: API response status가 400이면 pass</li><li>에러 메시지 검증: "이미 등록된 이메일입니다" 메시지 확인</li> | {"email": "test@example.com", "password": "password456", "name": "테스트사용자2"}
POST /api/users/login | 로그인 성공 | TC02-1  | <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 검증: id, username, email 필드 확인</li> | {"email": "test@example.com", "password": "password123"}
POST /api/users/login | 로그인 실패-잘못된 인증정보 | TC02-2  | <li>Status code is 401: API response status가 401이면 pass</li> | {"email": "test@example.com", "password": "wrongpassword"}
PATCH /api/users/{id}/password | 비밀번호 변경 성공 | TC03-1 | <li>Status code is 200: API response status가 200이면 pass</li> | {"currentPassword": "password123", "newPassword": "newpassword456"}
PATCH /api/users/{id}/password | 비밀번호 변경 실패-현재 비밀번호 불일치 | TC03-2 | <li>Status code is 400: API response status가 400이면 pass</li><li>에러 메시지 검증: "현재 비밀번호가 일치하지 않습니다" 확인</li> | {"currentPassword": "wrongpassword", "newPassword": "newpassword456"}
DELETE /api/users/{id} | 사용자 삭제 성공 | TC04-1 | <li>Status code is 204: API response status가 204이면 pass</li> | 사용자 ID

### 폴더 관리 API 테스트

API   | Test Name |  TEST Case ID  | Description | Test Data
---     |   --- |   --- | ---   | ---
GET /api/folders/structure/{userId} | 폴더 구조 조회 성공 | TC05-1 | <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 구조 검증: 계층적 폴더 구조 확인</li><li>루트 폴더 "문서" 존재 확인</li> | 사용자 ID
POST /api/folders | 폴더 생성 성공 | TC06-1 | <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 검증: folder_id, name, parent_id, user_id 필드 확인</li> | {"userId": 1, "name": "새폴더", "parentId": 1}
POST /api/folders | 루트 폴더 하위 폴더 생성 | TC06-2 | <li>Status code is 200: API response status가 200이면 pass</li><li>parentId가 null일 때 루트 폴더 하위 생성 확인</li> | {"userId": 1, "name": "프로젝트폴더", "parentId": null}
DELETE /api/folders/{folderId} | 폴더 삭제 성공 | TC07-1 | <li>Status code is 204: API response status가 204이면 pass</li><li>하위 폴더 및 스케치 함께 삭제 확인</li> | 폴더 ID
DELETE /api/folders/{folderId} | 루트 폴더 삭제 실패 | TC07-2 | <li>Status code is 400: API response status가 400이면 pass</li><li>루트 폴더("문서") 삭제 불가 에러 메시지 확인</li> | 루트 폴더 ID

### 그리기 기능 API 테스트

API   | Test Name |  TEST Case ID  | Description | Test Data
---     |   --- |   --- | ---   | ---
POST /api/drawings | 스케치 생성 성공 | TC08-1 | <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 검증: drawing_id, title, content, folder_id, timestamps 확인</li> | {"title": "새 스케치", "content": "스케치 데이터", "folderId": 1}
PUT /api/drawings/{drawing_id} | 스케치 수정 성공 | TC09-1 | <li>Status code is 200: API response status가 200이면 pass</li><li>수정된 데이터 반영 확인</li><li>updatedAt 필드 갱신 확인</li> | {"title": "수정된 스케치", "content": "수정된 스케치 데이터"}
GET /api/drawings/folder/{folderId} | 폴더 내 스케치 조회 성공 | TC10-1 | <li>Status code is 200: API response status가 200이면 pass</li><li>Response 데이터 타입 검증: 배열 형태 확인</li><li>각 스케치 객체의 필드 구조 검증</li> | 폴더 ID
DELETE /api/drawings/{drawing_id} | 스케치 삭제 성공 | TC11-1 | <li>Status code is 204: API response status가 204이면 pass</li> | 스케치 ID


## Integration Test Cases

### 사용자 워크플로우 통합 테스트

Test Case ID: TC14-1 - 전체 사용자 워크플로우 테스트

Test Step  | Test Data | Expected Result
--- |   --- |   ---
1. 회원가입 실행 | {"email": "integration@test.com", "password": "test123", "name": "통합테스트"} | 사용자 생성 및 루트 폴더 "문서" 자동 생성
2. 로그인 실행 | {"email": "integration@test.com", "password": "test123"} | 로그인 성공, 사용자 정보 반환
3. 폴더 구조 조회 | userId | 루트 폴더 "문서" 포함 폴더 구조 반환
4. 새 폴더 생성 | {"userId": userId, "name": "테스트프로젝트", "parentId": rootFolderId} | 새 폴더 생성 성공
5. 스케치 생성 | {"title": "첫번째 작품", "content": "테스트 스케치", "folderId": newFolderId} | 스케치 생성 성공
6. 스케치 수정 | {"title": "수정된 작품", "content": "수정된 스케치"} | 스케치 수정 성공
7. 폴더 내 스케치 조회 | folderId | 생성한 스케치 조회 성공
8. 스케치 삭제 | drawingId | 스케치 삭제 성공
9. 폴더 삭제 | folderId | 폴더 삭제 성공
10. 사용자 삭제 | userId | 사용자 및 모든 관련 데이터 삭제 성공

---

## Error Handling Test Cases

### 유효성 검증 테스트

Test Case ID: TC15-1 - 필수 필드 누락 테스트

API Endpoint | Missing Field | Expected Result
--- | --- | ---
POST /api/users | email 누락 | 400 Bad Request, 필드 누락 에러
POST /api/drawings | title 누락 | 400 Bad Request, 필드 누락 에러
POST /api/folders | name 누락 | 400 Bad Request, 필드 누락 에러

### 권한 및 접근 제어 테스트

Test Case ID: TC16-1 - 존재하지 않는 리소스 접근

API Endpoint | Test Data | Expected Result
--- | --- | ---
GET /api/drawings/folder/{folderId} | 존재하지 않는 폴더 ID | 404 Not Found
PUT /api/drawings/{drawing_id} | 존재하지 않는 스케치 ID | 404 Not Found
DELETE /api/folders/{folderId} | 존재하지 않는 폴더 ID | 404 Not Found

---

## Performance Test Cases

### 부하 테스트

Test Case ID: TC17-1 - 동시 사용자 테스트

Test Scenario | Concurrent Users | Duration | Expected Result
--- | --- | --- | ---
스케치 생성 부하 테스트 | 50명 | 5분 | 평균 응답시간 < 2초, 에러율 < 5%
폴더 구조 조회 부하 테스트 | 100명 | 3분 | 평균 응답시간 < 1초, 에러율 < 1%

---



## Test Environment Setup

### Database Setup
```sql
-- 테스트용 데이터베이스 생성
CREATE DATABASE sketch_app_test;
CREATE USER 'test_admin'@'localhost' IDENTIFIED BY 'test1234';
GRANT ALL PRIVILEGES ON sketch_app_test.* TO 'test_admin'@'localhost';
FLUSH PRIVILEGES;
```

### Test Server Configuration
- **Base URL**: `http://localhost:8080`
- **Database**: MySQL 8.0 (테스트 전용 DB)
- **Environment**: Spring Boot Test Profile

### Test Data Management
- 각 테스트 케이스 실행 전 데이터베이스 초기화
- 테스트 완료 후 생성된 데이터 정리
- 독립적인 테스트 환경 보장

---

## Test Execution Strategy

1. **Unit Tests**: 개별 API 엔드포인트 테스트
2. **Integration Tests**: 사용자 워크플로우 기반 통합 테스트
3. **Error Handling Tests**: 예외 상황 및 에러 처리 테스트
4. **Performance Tests**: 부하 및 성능 테스트


