#!/bin/bash

echo "🚀 전체 배포 프로세스를 시작합니다..."

# 1. 시스템 초기 설정
echo "📦 시스템 패키지 업데이트 중..."
sudo apt update && sudo apt upgrade -y

echo "☕ Java 17 설치 중..."
sudo apt install -y openjdk-17-jdk

echo "🔧 Maven 설치 중..."
sudo apt install -y maven

echo "🌐 Nginx 설치 중..."
sudo apt install -y nginx

echo "🔐 Certbot 설치 중..."
sudo apt install -y certbot python3-certbot-nginx

echo "📁 Git 설치 중..."
sudo apt install -y git

echo "🔥 방화벽 설정 중..."
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 8080

sudo systemctl start nginx
sudo systemctl enable nginx

# 2. 애플리케이션 빌드
echo "🔨 애플리케이션 빌드 중..."
./mvnw clean package -DskipTests

# 3. Nginx 설정
echo "⚙️ Nginx 설정 중..."
sudo cp nginx/nginx-simple.conf /etc/nginx/sites-available/sketch-app
sudo ln -s /etc/nginx/sites-available/sketch-app /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx

# 4. SSL 인증서 발급
echo "🔐 SSL 인증서 발급 중..."
sudo certbot --nginx -d yonsei-sketch.kro.kr --email gyun6266@gmail.com --agree-tos --non-interactive

# 5. Spring Boot 서비스 등록
echo "🎯 Spring Boot 서비스 등록 중..."
sudo cp sketch-app.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable sketch-app
sudo systemctl start sketch-app

echo "✅ 배포 완료!"
echo ""
echo "🔍 확인 방법:"
echo "  - 서비스 상태: sudo systemctl status sketch-app"
echo "  - 애플리케이션 로그: sudo journalctl -u sketch-app -f"
echo "  - Nginx 상태: sudo systemctl status nginx"
echo "  - 웹사이트: https://yonsei-sketch.kro.kr"
echo "  - H2 콘솔: https://yonsei-sketch.kro.kr/h2-console" 