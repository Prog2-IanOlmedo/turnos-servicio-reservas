# Etapa 1: compilo con Maven. Copio primero el pom para que las dependencias
# queden en caché y no se bajen de nuevo si solo cambia el código.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src/ src/
# Los tests corren en el CI (usan Testcontainers y acá no hay Docker).
RUN ./mvnw -B -q package -DskipTests

# Etapa 2: solo la JRE y el jar, corriendo con un usuario sin privilegios.
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --no-create-home reservas
COPY --from=build /app/target/*.jar app.jar
USER reservas
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
