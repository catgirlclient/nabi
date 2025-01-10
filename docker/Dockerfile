FROM adoptopenjdk:17
FROM rust:1.83.0

WORKDIR /opt/discord1
COPY bot/build/libs/Nabi-*.jar Nabi.jar
ENTRYPOINT ["java", "-jar", "Nabi.jar"]