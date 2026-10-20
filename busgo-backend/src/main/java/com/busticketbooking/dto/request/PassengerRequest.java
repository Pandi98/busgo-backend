package com.busticketbooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassengerRequest(
        @NotBlank(message = "Passenger name is required")
        String name,

        @NotNull(message = "Age is required")
        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 120, message = "Age must be realistic")
        Integer age,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotBlank(message = "Seat number is required")
        String seatNumber,

        String idProofType,
        String idProofNumber
) {}
