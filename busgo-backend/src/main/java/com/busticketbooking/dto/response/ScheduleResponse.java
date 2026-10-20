package com.busticketbooking.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleResponse(
        Long id,
        BusResponse bus,
        RouteResponse route,
        LocalDate travelDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Double farePerSeat,
        Integer availableSeats,
        String status
) {}
