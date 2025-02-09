FROM openjdk:20
COPY ./target/kafka-producer-0.0.1-SNAPSHOT.jar /usr/src/
WORKDIR /usr/src/
CMD ["java", "-jar", "kafka-producer-0.0.1-SNAPSHOT.jar"]