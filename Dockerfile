FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/mongodbConnection-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT["JAVA","-jar","/app.jar"]
