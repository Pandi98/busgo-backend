package com.busticketbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RouteRequest(
        @NotBlank(message = "Source city is required")
        String source,

        @NotBlank(message = "Destination city is required")
        String destination,

        @NotNull(message = "Distance is required")
        @Positive(message = "Distance must be positive")
        Double distanceKm,

        @NotNull(message = "Estimated duration is required")
        @Min(value = 30, message = "Duration must be at least 30 minutes")
        Integer estimatedDurationMinutes
) {}
