FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle gradle/
RUN chmod +x gradlew

COPY build.gradle.kts settings.gradle.kts ./
COPY file-storing-service/build.gradle.kts file-storing-service/
RUN ./gradlew :file-storing-service:dependencies --no-daemon

COPY file-storing-service/src file-storing-service/src/
RUN ./gradlew :file-storing-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/file-storing-service/build/libs/file-storing-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
