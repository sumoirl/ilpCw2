FROM eclipse-temurin:21-jdk

EXPOSE 8080

WORKDIR /app

COPY ./target/cw2-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]