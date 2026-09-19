package com.api.regie.dto;

public record SoutraInitiatePaymentRequestDTO(
        String reference,
        double amount,
        String note
) {}
