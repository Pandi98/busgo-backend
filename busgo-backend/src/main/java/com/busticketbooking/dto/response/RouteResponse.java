package com.busticketbooking.dto.response;

public record RouteResponse(
        Long id,
        String source,
        String destination,
        Double distanceKm,
        Integer estimatedDurationMinutes
) {}
