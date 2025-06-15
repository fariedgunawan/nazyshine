package com.nazyshine.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceRequest {
    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    private String description;
}