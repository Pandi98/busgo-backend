package com.busticketbooking.controller;

import com.busticketbooking.dto.request.PaymentRequest;
import com.busticketbooking.dto.response.ApiResponse;
import com.busticketbooking.dto.response.PaymentResponse;
import com.busticketbooking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> process(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Payment processed", paymentService.process(request)));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Payment fetched", paymentService.getByBookingId(bookingId)));
    }

    @PostMapping("/refund/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable Long bookingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Refund processed", paymentService.refund(bookingId)));
    }
}
