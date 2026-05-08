# ── Etapa 1: Build ────────────────────────────────────────────────────────────
# Usamos la imagen oficial de Maven con JDK 17 para compilar el proyecto.
# Esta etapa solo existe durante la construcción; no forma parte de la imagen final.
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar la caché de capas de Docker.
# Si el pom.xml no cambia, Docker reutiliza la capa de dependencias descargadas.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# ── Etapa 2: Runtime ──────────────────────────────────────────────────────────
# Imagen mínima con solo el JRE (sin Maven ni JDK), reduce el tamaño final.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Variables de entorno con valores por defecto; se sobreescriben en docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/entregaya \
    SPRING_DATASOURCE_USERNAME=entregaya_user \
    SPRING_DATASOURCE_PASSWORD=tu_password_real \
    TELEGRAM_BOT_TOKEN=SIN_CONFIGURAR \
    SERVER_PORT=8081

# Copiamos únicamente el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto en que escucha la aplicación Spring Boot
EXPOSE 8081

# Healthcheck: verifica que la app responde antes de marcarla como "healthy"
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8081/login || exit 1

# Punto de entrada: ejecuta el .jar con opciones de memoria adecuadas para contenedor
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]