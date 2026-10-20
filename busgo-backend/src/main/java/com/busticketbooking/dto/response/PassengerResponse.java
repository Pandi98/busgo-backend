package com.busticketbooking.dto.response;

public record PassengerResponse(
        Long id,
        String name,
        Integer age,
        String gender,
        String seatNumber,
        String idProofType,
        String idProofNumber
) {}
