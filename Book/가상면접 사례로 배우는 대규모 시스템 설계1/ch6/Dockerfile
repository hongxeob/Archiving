FROM openjdk:17-jdk-slim

WORKDIR /app

# JAR 파일이 어디에 있는지 확인 후 정확한 경로 사용
# 예: ./build/libs/*.jar 또는 ./out/*.jar
COPY ./target/*.jar app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
