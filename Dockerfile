FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY JP-Source/pom.xml JP-Source/pom.xml
COPY JP-Source/src JP-Source/src
WORKDIR /workspace/JP-Source
RUN mvn -B -DskipTests clean package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/JP-Source/target/restaurant-management.jar /app/restaurant-management.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/restaurant-management.jar"]
