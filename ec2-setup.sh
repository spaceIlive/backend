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
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Docker 서비스 시작 및 사용자 권한 설정
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# MySQL 서버 설치
echo "🗄️ MySQL 서버 설치 중..."
sudo apt install -y mysql-server

# MySQL 서비스 시작
sudo systemctl start mysql
sudo systemctl enable mysql

# MySQL 보안 설정 및 데이터베이스 생성
echo "🔐 MySQL 데이터베이스 설정 중..."
sudo mysql -e "
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '1234';
FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS sketch_app;
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON sketch_app.* TO 'admin'@'localhost';
GRANT ALL PRIVILEGES ON sketch_app.* TO 'admin'@'%';
FLUSH PRIVILEGES;
"

# MySQL 외부 접속 허용 설정
echo "🌐 MySQL 외부 접속 설정 중..."
sudo sed -i 's/bind-address.*= 127.0.0.1/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf
sudo systemctl restart mysql

# 방화벽 설정 (필요한 포트 열기)
echo "🔥 방화벽 설정 중..."
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 3306

# Git과 Java 설치
sudo apt install -y git openjdk-17-jdk

# Maven 설치 (선택사항)
sudo apt install -y maven

echo "✅ EC2 서버 기본 설정 완료!"
echo ""
echo "📋 다음 단계:"
echo "1. 새 터미널 세션을 시작하거나 'newgrp docker' 실행"
echo "2. 프로젝트 소스코드를 클론"
echo "3. SSL 인증서 설정 실행"
echo "4. Docker Compose로 애플리케이션 시작"
echo ""
echo "🔑 MySQL 접속 정보:"
echo "  - 호스트: localhost"
echo "  - 포트: 3306"
echo "  - 데이터베이스: sketch_app"
echo "  - 사용자: admin"
echo "  - 비밀번호: 1234" 