package com.busticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Booking ID is required")
        Long bookingId,

        @NotBlank(message = "Payment method is required")
        String paymentMethod,

        // Card / UPI fields — handled by payment gateway in production
        String upiId,
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        String cvv
) {}
