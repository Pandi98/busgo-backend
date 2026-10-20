package com.busticketbooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusRequest(
        @NotBlank(message = "Bus number is required")
        String busNumber,

        @NotBlank(message = "Bus name is required")
        String busName,

        @NotBlank(message = "Bus type is required")
        String busType,

        @NotNull(message = "Total seats is required")
        @Min(value = 10, message = "Minimum 10 seats")
        @Max(value = 60, message = "Maximum 60 seats")
        Integer totalSeats,

        String amenities,

        Boolean isActive
) {}
