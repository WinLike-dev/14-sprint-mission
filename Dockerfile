# Railway는 Nixpacks로 빌드 방식을 추측하지만, 추측에 맡기면 JDK 버전이 바뀔 때
# 빌드가 조용히 깨진다. 이 프로젝트는 Java 17 toolchain을 쓰므로 명시한다.

# 1단계: 빌드
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 의존성 목록만 먼저 복사해 받아둔다.
# 소스만 바뀐 배포에서는 이 레이어가 캐시에 남아 다시 받지 않는다.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행
# JRE만 담아 이미지에서 컴파일러와 빌드 도구를 뺀다.
FROM eclipse-temurin:17-jre
WORKDIR /app

# 파일 저장소 기본 경로. Railway Volume을 여기에 붙이면 배포 사이에 데이터가 남는다.
ENV DISCODEIT_DATA_ROOT=/app/data
RUN mkdir -p /app/data

COPY --from=build /workspace/build/libs/*-SNAPSHOT.jar app.jar

# PORT는 플랫폼이 넘겨준다. application.yml이 그 값을 읽는다.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
