
FROM openjdk:16-jdk-alpine
RUN mkdir -p /workspace
WORKDIR /workspace/
COPY target/cc-0.0.3-SNAPSHOT.jar ./
CMD ["java", "-jar","cc-0.0.3-SNAPSHOT.jar"]
