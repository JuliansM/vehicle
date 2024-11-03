package com.sofka.vehicle.controller;

import com.sofka.vehicle.model.StandardResponse;
import com.sofka.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{plate}")
    public Mono<ResponseEntity<StandardResponse>> getScore(@PathVariable String plate) {


        return vehicleService.getAllVehicles()
                .flatMap(vehicles -> vehicleService.getVehicleByPlate(vehicles, plate))
                .flatMap(vehicle -> vehicleService.receiveMessage())
                .flatMap(Mono::just)
                .flatMap(s -> Mono.just(ResponseEntity.ok(StandardResponse.builder()
                        .data(s)
                        .message("Score obtenido exitosamente.")
                        .build())))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.just(ResponseEntity.badRequest()
                            .body(StandardResponse.builder()
                                    .message(e.getMessage())
                                    .build()));
                });
    }
}
