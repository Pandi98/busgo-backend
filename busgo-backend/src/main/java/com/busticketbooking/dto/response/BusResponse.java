package com.busticketbooking.dto.response;

public record BusResponse(
        Long id,
        String busNumber,
        String busName,
        String busType,
        Integer totalSeats,
        String amenities,
        Boolean isActive
) {}
