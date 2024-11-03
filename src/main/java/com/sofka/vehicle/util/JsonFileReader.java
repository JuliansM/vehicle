package com.sofka.vehicle.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.vehicle.model.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonFileReader {

    private final ObjectMapper objectMapper;

    public Mono<List<Vehicle>> readVehiclesFile() {
        return Mono.fromCallable(() -> {
            try (InputStream inputStream = new ClassPathResource("datasource-vehicles-mock.json").getInputStream()) {
                return objectMapper.readValue(inputStream, new TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading vehicles.json file", e);
            }
        });
    }
}
