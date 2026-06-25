# Paso 1: Compilar usando una imagen de Maven compatible con JDK 25
FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos la subcarpeta donde vive tu código real
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa

# Compilamos el proyecto omitiendo los tests para acelerar el despliegue
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen de ejecución liviana con Java 25
FROM eclipse-temurin:25-jdk-caracal
WORKDIR /app

# Copiamos el archivo .jar generado en el paso de compilación
COPY --from=build /app/cfp-mapa/target/*.jar app.jar

# Exponemos el puerto estándar
EXPOSE 8080

# Comando para ejecutar la aplicación de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]