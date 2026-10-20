package com.busticketbooking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull(message = "Schedule ID is required")
        Long scheduleId,

        @NotEmpty(message = "At least one seat must be selected")


        @NotEmpty(message = "At least one passenger is required")
        @Valid
        List<PassengerRequest> passengers
) {}
