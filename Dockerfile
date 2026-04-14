# build (compilar el proyecto con Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Compilar el proyecto
RUN mvn clean package -DskipTests

# runtime (ejecutar la app)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el .jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8081

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
