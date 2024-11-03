package com.sofka.vehicle.model;

import lombok.Data;

import java.util.List;

@Data
public class Vehicle {

    private String owner;
    private String license_plate;
    private List<Score> scores;
}
