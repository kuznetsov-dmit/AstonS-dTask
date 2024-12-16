# Этап сборки
FROM maven:3.9.6-eclipse-temurin-21 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап развертывания
FROM tomcat:10.1-jdk21
ENV CATALINA_OPTS="-Dfile.encoding=UTF-8"
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war