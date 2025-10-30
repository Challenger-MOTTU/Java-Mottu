# ---------- build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -DskipTests -ntp package

# ---------- run ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75"
EXPOSE 8080
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar /app/app.jar"]
