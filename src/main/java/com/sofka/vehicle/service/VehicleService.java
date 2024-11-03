package com.sofka.vehicle.service;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.vehicle.model.Score;
import com.sofka.vehicle.model.Vehicle;
import com.sofka.vehicle.util.JsonFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class VehicleService {

    private final ServiceBusSenderAsyncClient senderClient;
    private final ServiceBusReceiverAsyncClient receiverClient;
    private final JsonFileReader jsonFileReader;
    private final ObjectMapper objectMapper;

    public VehicleService(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.topics.send.name}") String requestTopicName,
            @Value("${azure.servicebus.topics.receive.name}") String responseTopicName,
            @Value("${azure.servicebus.topics.receive.subscription-name}") String vehicleSubscriptionName,
            JsonFileReader jsonFileReader,
            ObjectMapper objectMapper) {

        this.jsonFileReader = jsonFileReader;
        this.objectMapper = objectMapper;

        // Crear cliente para enviar mensajes
        this.senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(requestTopicName)
                .buildAsyncClient();

        // Crear cliente para recibir mensajes
        this.receiverClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .topicName(responseTopicName)
                .subscriptionName(vehicleSubscriptionName)
                .buildAsyncClient();
    }

    public Mono<String> sendMessage(List<Score> scores) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(scores))
                .flatMap(message -> senderClient.sendMessage(new ServiceBusMessage(message))
                        .doOnError(error -> System.err.println("Error al enviar mensaje: " + error.getMessage()))
                        .thenReturn("Mensaje enviado con éxito: " + message));
    }

    public Mono<String> receiveMessage() {
        return receiverClient.receiveMessages()
                .timeout(Duration.ofSeconds(10))
                .next()
                .map(ServiceBusReceivedMessage::getBody)
                .map(BinaryData::toString)
                .doOnNext(message -> log.info("Mensaje a procesar: {}", message))
                .doOnError(error -> log.error("Error al recibir mensaje: {} - {}", error.getClass().getSimpleName(), error.getMessage()));
    }

    public Mono<String> getVehicleByPlate(List<Vehicle> vehicles, String plate) {
        return Mono.justOrEmpty(vehicles.stream()
                        .filter(vehicle -> plate.equalsIgnoreCase(vehicle.getLicense_plate()))
                        .findFirst()
                        .orElse(null))
                .flatMap(vehicle -> sendMessage(vehicle.getScores()))
                .flatMap(Mono::just)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontro el plate: " + plate)));
    }

    public Mono<List<Vehicle>> getAllVehicles() {
        return jsonFileReader.readVehiclesFile();
    }

}
