FROM openjdk:20
COPY ./target/kafka-config-service-0.0.1-SNAPSHOT.jar /usr/src/
WORKDIR /usr/src/
CMD ["java", "-jar", "kafka-config-service-0.0.1-SNAPSHOT.jar"]