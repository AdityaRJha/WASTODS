FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]