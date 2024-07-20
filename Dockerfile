# Build stage
FROM domblack/oracle-jdk8:latest AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM tomcat:8.5.34-jre8

# Copy the WAR file to the webapps directory of Tomcat
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/app.war

# Expose the port that Tomcat will run on
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]