#!/bin/bash

echo "🚀 EC2 서버 설정을 시작합니다..."

# 시스템 업데이트
echo "📦 시스템 패키지 업데이트 중..."
sudo apt update && sudo apt upgrade -y

# Docker 설치
echo "🐳 Docker 설치 중..."
sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Docker Compose 설치
echo "🔧 Docker Compose 설치 중..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Docker 서비스 시작 및 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

# Certbot 설치 (SSL 인증서용)
echo "🔐 Certbot 설치 중..."
sudo apt install -y certbot

# 방화벽 설정 (HTTP/HTTPS 포트만)
echo "🔥 방화벽 설정 중..."
sudo ufw allow 80
sudo ufw allow 443

# Git 설치
sudo apt install -y git

echo "✅ EC2 서버 기본 설정 완료!"
echo ""
echo "📋 다음 단계:"
echo "1. 프로젝트 소스코드를 클론: git clone [repo-url]"
echo "2. 프로젝트 디렉토리로 이동: cd backend"
echo "3. SSL 설정 및 애플리케이션 시작: ./ssl-setup.sh"
echo ""
echo "🔧 H2 데이터베이스 정보:"
echo "  - 타입: 인메모리 데이터베이스"
echo "  - 콘솔 접속: https://yonsei-sketch.kro.kr/h2-console"
echo "  - JDBC URL: jdbc:h2:mem:sketch_app"
echo "  - 사용자: sa"
echo "  - 비밀번호: (공백)"
echo ""
echo "⚠️  주의사항:"
echo "  - 애플리케이션 재시작 시 모든 데이터가 삭제됩니다 (인메모리 특성)"
echo "  - 로그아웃 후 다시 로그인하여 docker 그룹 권한을 적용하세요" 