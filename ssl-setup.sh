#!/bin/bash

# SSL 인증서 초기 설정 스크립트
# EC2에서 한 번만 실행하면 됩니다

echo "🔐 SSL 인증서 설정을 시작합니다..."

# 도메인 확인
DOMAIN="yonsei-sketch.kro.kr"
echo "🌐 도메인: $DOMAIN"

# Docker 컨테이너가 실행 중인지 확인하고 임시 중지
echo "📦 기존 Docker 컨테이너 중지 중..."
docker-compose down

# Certbot을 사용하여 SSL 인증서 발급
echo "📜 SSL 인증서 발급 중..."
sudo certbot certonly --standalone \
  --preferred-challenges http \
  --agree-tos \
  --no-eff-email \
  --email your-email@example.com \
  -d $DOMAIN

# SSL 인증서를 Docker 볼륨으로 복사
echo "📂 SSL 인증서를 Docker 볼륨으로 복사 중..."
sudo mkdir -p ./ssl
sudo cp -r /etc/letsencrypt/* ./ssl/
sudo chmod -R 755 ./ssl

# Nginx HTML 디렉토리 생성 (Let's Encrypt 갱신용)
sudo mkdir -p ./nginx/html

# Docker Compose로 서비스 재시작
echo "🚀 Docker 컨테이너 재시작 중..."
docker-compose up -d --build

# 인증서 자동 갱신 크론잡 설정
echo "⏰ 인증서 자동 갱신 설정 중..."
(crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet && sudo cp -r /etc/letsencrypt/* $(pwd)/ssl/ && docker-compose restart nginx") | crontab -

echo "✅ SSL 설정 완료!"
echo ""
echo "🔍 테스트:"
echo "  - HTTP: curl http://$DOMAIN"
echo "  - HTTPS: curl https://$DOMAIN"
echo "  - H2 콘솔: https://$DOMAIN/h2-console"
echo ""
echo "📋 SSL 인증서 정보:"
echo "  - 위치: ./ssl/live/$DOMAIN/"
echo "  - 유효기간: 90일"
echo "  - 자동 갱신: 매일 12:00 크론잡으로 설정됨"
echo ""
echo "⚠️  주의사항:"
echo "  - email 주소를 실제 이메일로 변경하세요 (line 14)"
echo "  - 도메인이 현재 서버 IP를 가리키고 있는지 확인하세요" 