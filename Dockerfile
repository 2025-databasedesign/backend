FROM eclipse-temurin:19-jdk

# Redis 설치
RUN apt-get update && \
    apt-get install -y redis-server && \
    apt-get clean

# 애플리케이션 JAR 복사
COPY build/libs/moviebook-0.0.1-SNAPSHOT.jar /app.jar

# Redis 설정 파일 위치 (기본 설정 사용 or 필요시 추가)
# COPY redis.conf /etc/redis/redis.conf

# Redis & Spring Boot 동시 실행
CMD redis-server --daemonize yes && java -Xms128m -Xmx256m -jar /app.jar
