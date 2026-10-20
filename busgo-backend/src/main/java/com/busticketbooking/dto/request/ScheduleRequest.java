package com.busticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull(message = "Bus ID is required")
        Long busId,

        @NotNull(message = "Route ID is required")
        Long routeId,

        @NotNull(message = "Travel date is required")
        LocalDate travelDate,

        @NotNull(message = "Departure time is required")
        LocalTime departureTime,

        @NotNull(message = "Arrival time is required")
        LocalTime arrivalTime,

        @NotNull(message = "Fare per seat is required")
        @Positive(message = "Fare must be positive")
        Double farePerSeat,

        String status
) {}
