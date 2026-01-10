# 1. Base image Java
FROM eclipse-temurin:17-jdk-alpine

# 2. Set working directory
WORKDIR /app

# 3. Copy pom.xml & download dependencies
COPY pom.xml .
RUN apk add --no-cache maven \
    && mvn dependency:go-offline

# 4. Copy source code
COPY src ./src

# 5. Build jar
RUN mvn clean package -DskipTests

# 6. Expose port
EXPOSE 8083

# 7. Run application
CMD ["java", "-jar", "target/distribution-service-1.0.0.jar"]
