package com.nazyshine.payload.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingRequest {
    @NotEmpty(message = "Service IDs cannot be empty")
    private Set<Long> serviceIds; // ID layanan yang dipilih untuk booking

    @NotNull(message = "Booking time cannot be null")
    @Future(message = "Booking time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Format ISO 8601, e.g., "2025-07-01T10:30:00"
    private LocalDateTime bookingTime; // Tanggal dan waktu booking
}