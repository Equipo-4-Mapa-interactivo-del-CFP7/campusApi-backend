# Paso 1: Compilar usando una imagen genérica con JDK 25
FROM maven:3.9.9-openjdk-25 AS build
WORKDIR /app

# Copiamos la subcarpeta donde vive tu código real
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa

# Compilamos el proyecto omitiendo los tests
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen de ejecución liviana con OpenJDK 25
FROM openjdk:25-slim
WORKDIR /app

# Copiamos el archivo .jar generado
COPY --from=build /app/cfp-mapa/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]