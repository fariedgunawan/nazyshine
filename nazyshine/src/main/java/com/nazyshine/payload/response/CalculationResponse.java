package com.nazyshine.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List; // Penting: Pastikan List diimpor

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    private String message;
    private BigDecimal totalPrice;
    private List<String> selectedServicesNames; // Nama layanan yang dipilih (opsional)
}