FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN apk add --no-cache maven && mvn package -DskipTests -q

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
