FROM maven:3.9.9-amazoncorretto-25-al2023 AS build
WORKDIR /app
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa
RUN mvn clean package -DskipTests

FROM openjdk:25-rc-jdk
WORKDIR /app
COPY --from=build /app/cfp-mapa/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]