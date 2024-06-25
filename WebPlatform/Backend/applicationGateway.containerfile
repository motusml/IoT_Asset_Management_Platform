FROM docker.io/library/maven:3-eclipse-temurin-21-alpine
WORKDIR /src
COPY pom.xml .
COPY ApplicationGateway/ project
WORKDIR /src/project
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dmaven.test.skip

FROM docker.io/library/eclipse-temurin:21-alpine
COPY --from=0 /src/project/target/*.jar executable.jar
CMD ["java", "-jar", "executable.jar"]