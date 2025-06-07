# 🚀 EC2 배포 가이드

## 📋 사전 준비

1. **EC2 인스턴스 생성** (Ubuntu 20.04 LTS 권장)
2. **도메인 설정**: `yonsei-sketch.kro.kr` → EC2 IP 주소
3. **보안 그룹 설정**:
   - HTTP (80): 0.0.0.0/0
   - HTTPS (443): 0.0.0.0/0
   - SSH (22): 본인 IP만

## 🔧 EC2 초기 설정

```bash
# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 재로그인 또는 재부팅
sudo reboot
```

## 📦 프로젝트 배포

```bash
# 프로젝트 클론
git clone [your-repository-url]
cd backend

# SSL 설정 스크립트 실행 권한 부여
chmod +x ssl-setup.sh

# 이메일 주소 수정 (ssl-setup.sh 파일에서)
nano ssl-setup.sh
# your-email@example.com → 실제 이메일 주소로 변경

# SSL 인증서 발급 및 서비스 시작
./ssl-setup.sh
```

## 🌐 접속 확인

- **HTTP**: http://yonsei-sketch.kro.kr (자동으로 HTTPS로 리다이렉트)
- **HTTPS**: https://yonsei-sketch.kro.kr

## 🔍 트러블슈팅

### SSL 인증서 발급 실패 시
```bash
# 도메인 DNS 확인
nslookup yonsei-sketch.kro.kr

# nginx 로그 확인
docker logs nginx-proxy

# certbot 로그 확인
docker logs certbot
```

### 서비스 재시작
```bash
# 전체 서비스 재시작
docker-compose down
docker-compose up -d

# 특정 서비스만 재시작
docker-compose restart nginx
docker-compose restart app
```

### 로그 확인
```bash
# Spring Boot 로그
docker logs spring-app

# Nginx 로그
docker logs nginx-proxy

# MySQL 로그
docker logs mysql-db
```

## 📁 파일 구조

```
backend/
├── docker-compose.yml    # 전체 서비스 정의
├── Dockerfile           # Spring Boot 이미지 빌드
├── nginx/
│   ├── nginx.conf       # Nginx 메인 설정
│   └── conf.d/          # 추가 설정 파일들
├── certbot/
│   ├── conf/            # SSL 인증서 저장
│   └── www/             # Let's Encrypt 인증용
├── ssl-setup.sh         # SSL 초기 설정 스크립트
└── src/                 # Spring Boot 소스 코드
```

## ⚙️ 환경 변수

- `ALLOWED_ORIGINS`: CORS 허용 도메인
- `SPRING_PROFILES_ACTIVE`: 활성 프로필 (prod)
- `SPRING_DATASOURCE_*`: 데이터베이스 연결 정보

## 🔄 SSL 인증서 자동 갱신

certbot 컨테이너가 12시간마다 자동으로 인증서 갱신을 시도합니다.
nginx는 6시간마다 설정을 다시 로드하여 새 인증서를 적용합니다. 