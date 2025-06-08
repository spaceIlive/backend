# 🚀 EC2 배포 가이드 (H2 인메모리 + Docker)

## 📋 사전 준비

1. **EC2 인스턴스 생성** (Ubuntu 20.04 LTS 권장)
2. **도메인 설정**: `yonsei-sketch.kro.kr` → EC2 IP 주소
3. **보안 그룹 설정**:
   - HTTP (80): 0.0.0.0/0
   - HTTPS (443): 0.0.0.0/0
   - SSH (22): 본인 IP만

## 🔧 자동 설정 스크립트 실행

```bash
# 프로젝트 클론
git clone [your-repository-url]
cd backend

# EC2 초기 설정 스크립트 실행
chmod +x ec2-setup.sh
./ec2-setup.sh

# 로그아웃 후 재로그인 (Docker 그룹 권한 적용)
exit
# SSH 재접속 후...

# SSL 설정 및 애플리케이션 시작
chmod +x ssl-setup.sh
./ssl-setup.sh
```

## 🗄️ 데이터베이스 정보

### H2 인메모리 데이터베이스
- **타입**: 메모리 기반 (서버 재시작 시 데이터 삭제)
- **JDBC URL**: `jdbc:h2:mem:sketch_app`
- **사용자명**: `sa`
- **비밀번호**: (공백)
- **콘솔 접속**: https://yonsei-sketch.kro.kr/h2-console

⚠️ **주의사항**: 애플리케이션 재시작 시 모든 데이터가 삭제됩니다.

## 🌐 접속 확인

- **HTTP**: http://yonsei-sketch.kro.kr (자동으로 HTTPS로 리다이렉트)
- **HTTPS**: https://yonsei-sketch.kro.kr
- **H2 콘솔**: https://yonsei-sketch.kro.kr/h2-console

## 🔍 트러블슈팅

### SSL 인증서 발급 실패 시
```bash
# 도메인 DNS 확인
nslookup yonsei-sketch.kro.kr

# nginx 로그 확인
docker logs sketch_nginx

# 애플리케이션 로그 확인
docker logs sketch_app
```

### 서비스 관리
```bash
# 전체 서비스 시작
docker-compose up -d --build

# 전체 서비스 중지
docker-compose down

# 특정 서비스 재시작
docker-compose restart nginx
docker-compose restart app

# 실시간 로그 확인
docker-compose logs -f app
docker-compose logs -f nginx
```

### 데이터 초기화
```bash
# 애플리케이션 재시작으로 H2 메모리 초기화
docker-compose restart app
```

## 📁 파일 구조

```
backend/
├── docker-compose.yml    # Docker 서비스 정의
├── Dockerfile           # Spring Boot 이미지 빌드
├── nginx/
│   ├── nginx.conf       # Nginx 설정
│   └── html/            # Let's Encrypt 인증용
├── ssl/                 # SSL 인증서 저장 (복사본)
├── ec2-setup.sh         # EC2 초기 설정 스크립트
├── ssl-setup.sh         # SSL 설정 스크립트
└── src/                 # Spring Boot 소스 코드
    └── main/
        └── resources/
            └── application.yml  # H2 설정 포함
```

## ⚙️ 환경 설정

### Docker Compose 서비스
- **app**: Spring Boot 애플리케이션 (포트 8080)
- **nginx**: 리버스 프록시 및 SSL 터미네이션 (포트 80, 443)

### 환경 변수
- `SPRING_PROFILES_ACTIVE=prod`
- `ALLOWED_ORIGINS`: CORS 허용 도메인

## 🔄 SSL 인증서 자동 갱신

크론잡이 매일 12:00에 자동으로 인증서 갱신을 시도합니다:
```bash
# 크론잡 확인
crontab -l

# 수동 갱신
sudo certbot renew --dry-run
```

## 🏗️ 개발 vs 운영 차이점

### 개발 환경
- H2 콘솔 활성화
- 상세 로깅 활성화
- DDL 자동 생성/삭제

### 운영 환경
- HTTPS 강제 리다이렉트
- SSL 보안 강화
- Docker 컨테이너 격리 