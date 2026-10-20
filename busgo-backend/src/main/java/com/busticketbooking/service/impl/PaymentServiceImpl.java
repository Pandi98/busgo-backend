package com.busticketbooking.service.impl;


import com.busticketbooking.dto.request.PaymentRequest;
import com.busticketbooking.dto.response.PaymentResponse;
import com.busticketbooking.entity.Booking;
import com.busticketbooking.entity.Payment;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.BookingRepository;
import com.busticketbooking.repository.PaymentRepository;
import com.busticketbooking.repository.ScheduleRepository;
import com.busticketbooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.busticketbooking.service.EmailService;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public PaymentResponse process(PaymentRequest req) {
        var booking = bookingRepository.findByIdWithDetails(req.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", req.bookingId()));


        if (booking.getPayment() != null && "SUCCESS".equals(booking.getPayment().getStatus())) {
            throw new IllegalArgumentException("Payment already completed for this booking.");
        }

        if (!booking.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Payment is allowed for only PENDING bookings");
        }

        // Simulate gateway — always succeeds in demo
        String transactionId = "TXN" + UUID.randomUUID()
                                            .toString()
                                            .replace("-", "")
                                            .substring(0, 12)
                                            .toUpperCase();

        var payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod(req.paymentMethod())
                .status("SUCCESS")
                .transactionId(transactionId)
                .build();

        Payment savedPayment =paymentRepository.save(payment);

        // Confirm the booking
        booking.setPayment(savedPayment);
        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);

        // Send confirmation email
        emailService.sendBookingConfirmation(booking);

        log.info("Payment SUCCESS | booking={} | txn={}", booking.getBookingReference(), transactionId);

        return toResponse(payment);
    }

    @Override
    public PaymentResponse getByBookingId(Long bookingId) {
        var payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refund(Long bookingId) {
        var payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));

        if (!payment.getStatus().equals("SUCCESS")) {
            throw new IllegalArgumentException("Only successful payments can be refunded");
        }
        // Update Payment
        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);

        // Cancel Booking
        Booking booking = payment.getBooking();
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);


        // Restore available seats
        var schedule = booking.getSchedule();

        int seatsToRestore = booking.getSelectedSeats().split(",").length;

        int newAvailableSeats = schedule.getAvailableSeats() + seatsToRestore;
        schedule.setAvailableSeats(
                Math.min(newAvailableSeats, schedule.getBus().getTotalSeats())
        );

        scheduleRepository.save(schedule);

        log.info("Payment REFUNDED | bookingId={}", bookingId);
        return toResponse(payment);



    }

    // ── Mapper ─────────────────────────────────────────────

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getAmount(), p.getPaymentMethod(),
                p.getStatus(), p.getTransactionId(),
                p.getPaymentDate(), p.getFailureReason()
        );
    }
}
