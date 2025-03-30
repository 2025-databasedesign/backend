FROM eclipse-temurin:19-jdk

COPY myapp-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "/app.jar"]
