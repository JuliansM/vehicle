package com.sofka.vehicle.model;

import lombok.Builder;

@Builder
public record StandardResponse(String message, Object data) {
}
