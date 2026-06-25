# Paso 1: Compilar la aplicación usando Java 25
FROM openjdk:25-rc-jdk AS extractor
WORKDIR /app

# Instalar Maven manualmente dentro de la imagen de Java 25
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copiamos la subcarpeta con tu código
COPY cfp-mapa /app/cfp-mapa
WORKDIR /app/cfp-mapa

# Compilamos omitiendo tests
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar usando la imagen limpia de Java 25
FROM openjdk:25-rc-jdk
WORKDIR /app

# Copiamos el archivo .jar generado apuntando a la etapa 'extractor'
COPY --from=extractor /app/cfp-mapa/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]