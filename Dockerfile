# 멀티스테이지 빌드: 빌드 스테이지
FROM eclipse-temurin:17-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Maven Wrapper와 pom.xml 복사 (캐시 최적화)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Maven Wrapper 실행 권한 부여
RUN chmod +x ./mvnw

# 의존성 다운로드 (이 레이어는 pom.xml이 변경되지 않으면 캐시됨)
RUN ./mvnw dependency:go-offline -B

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (테스트 스킵하여 빌드 시간 단축)
RUN ./mvnw clean package -DskipTests

# 실행 스테이지: 더 가벼운 JRE 이미지 사용
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=builder /app/target/*.jar app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# JVM 옵션 설정 (메모리 최적화)
CMD ["--spring.profiles.active=prod"] 