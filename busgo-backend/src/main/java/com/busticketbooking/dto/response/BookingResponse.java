package com.busticketbooking.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        String bookingReference,
        Long userId,
        String userName,
        ScheduleResponse schedule,
        LocalDateTime bookingDate,
        Double totalAmount,
        String status,
        String selectedSeats,
        List<PassengerResponse> passengers,
        PaymentResponse payment
) {}
