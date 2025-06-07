#!/bin/bash

# SSL 인증서 초기 설정 스크립트
# EC2에서 한 번만 실행하면 됩니다

echo "🔧 SSL 인증서 설정을 시작합니다..."

# 필요한 디렉토리 생성
mkdir -p nginx/conf.d
mkdir -p certbot/conf
mkdir -p certbot/www

# 임시 nginx 설정 (SSL 인증서 발급용)
cat > nginx/conf.d/temp.conf << 'EOF'
server {
    listen 80;
    server_name yonsei-sketch.kro.kr;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 200 'SSL 인증서 발급 중입니다...';
        add_header Content-Type text/plain;
    }
}
EOF

echo "📋 1단계: 임시 nginx 컨테이너 실행"
docker-compose up -d nginx

echo "⏳ nginx가 시작될 때까지 잠시 기다립니다..."
sleep 10

echo "🔐 2단계: SSL 인증서 발급"
docker-compose run --rm certbot certonly \
    --webroot \
    --webroot-path=/var/www/certbot \
    --email gyun6266@gmail.com \
    --agree-tos \
    --no-eff-email \
    -d yonsei-sketch.kro.kr

if [ $? -eq 0 ]; then
    echo "✅ SSL 인증서 발급 완료!"
    
    # 임시 설정 파일 삭제
    rm nginx/conf.d/temp.conf
    
    echo "🔄 3단계: 전체 서비스 재시작"
    docker-compose down
    docker-compose up -d
    
    echo "🎉 설정 완료! https://yonsei-sketch.kro.kr 로 접속하세요"
else
    echo "❌ SSL 인증서 발급 실패"
    echo "도메인이 올바르게 설정되었는지 확인하세요"
    exit 1
fi 