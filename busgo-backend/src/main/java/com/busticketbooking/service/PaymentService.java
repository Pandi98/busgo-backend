package com.busticketbooking.service;

import com.busticketbooking.dto.request.PaymentRequest;
import com.busticketbooking.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse process(PaymentRequest request);
    PaymentResponse getByBookingId(Long bookingId);
    PaymentResponse refund(Long bookingId);
}
