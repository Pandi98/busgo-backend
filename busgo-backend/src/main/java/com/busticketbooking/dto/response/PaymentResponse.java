package com.busticketbooking.dto.response;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Double amount,
        String paymentMethod,
        String status,
        String transactionId,
        LocalDateTime paymentDate,
        String failureReason
) {}
