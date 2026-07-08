# ==================== Etapa de Build ====================
#FROM maven:3.9.16-amazoncorretto-25-al2023 AS build
# O alternativamente con Maven 4 (RC):
FROM maven:4.0.0-rc-5-amazoncorretto-25-al2023 AS build

WORKDIR /app
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa

# Limpia y compila (recomiendo no saltar tests en producción, pero lo mantengo como tenías)
RUN mvn clean package -DskipTests

# ==================== Etapa de Runtime ====================
FROM eclipse-temurin:25-jdk AS runtime
# Alternativa si prefieres OpenJDK oficial (aunque está deprecated):
# FROM openjdk:25-jdk

WORKDIR /app

# Copia el JAR desde la etapa de build
COPY --from=build /app/cfp-mapa/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]