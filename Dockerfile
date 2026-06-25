# Paso 1: Compilar la aplicación usando Maven y el JDK 25
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos la subcarpeta con tu código
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa

# Compilamos omitiendo tests
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar usando la imagen exacta de Java 25
WORKDIR /app

# Copiamos el archivo .jar generado en el paso anterior
COPY --from=build /app/cfp-mapa/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]