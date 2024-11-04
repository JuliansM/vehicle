FROM --platform=$BUILDPLATFORM gradle:7.6.1-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

COPY src ./src

RUN gradle build -x test --no-daemon

FROM --platform=$TARGETPLATFORM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV AZURE_SERVICE_BUS_REQUEST_TOPIC_NAME="request-topic"
ENV AZURE_SERVICE_BUS_SUBSCRIPTION_NAME="vehicle-subscription"
ENV AZURE_SERVICE_BUS_ENDPOINT="manejar-Endpoint-service-bus-en-secretos"
ENV AZURE_SERVICE_BUS_RESPONSE_TOPIC_NAME="response-topic"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]