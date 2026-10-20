package com.busticketbooking.service.impl;


import com.busticketbooking.dto.request.BookingRequest;
import com.busticketbooking.dto.response.BookingResponse;
import com.busticketbooking.dto.response.PassengerResponse;
import com.busticketbooking.dto.response.PaymentResponse;
import com.busticketbooking.entity.Booking;
import com.busticketbooking.entity.Passenger;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.BookingRepository;
import com.busticketbooking.repository.ScheduleRepository;
import com.busticketbooking.repository.UserRepository;
import com.busticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository  bookingRepository;
    private final UserRepository     userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleServiceImpl scheduleMapper;

    @Override
    @Transactional
    public BookingResponse create(BookingRequest req, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        var schedule = scheduleRepository.findById(req.scheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", req.scheduleId()));

        if (!schedule.getStatus().equals("SCHEDULED")) {
            throw new IllegalArgumentException("Schedule is not available for booking");
        }


//        int seatCount = req.passengers().size();

        int seatCount = req.passengers().size();

        long uniqueSeatCount = req.passengers().stream()
                .map(p -> p.seatNumber())
                .distinct()
                .count();

        if (uniqueSeatCount != seatCount) {
            throw new IllegalArgumentException("Duplicate seat numbers are not allowed.");
        }

        List<Booking> existingBookings =
                bookingRepository.findActiveBookingsByScheduleId(schedule.getId());

        List<String> bookedSeats = existingBookings.stream()
                .flatMap(booking -> List.of(booking.getSelectedSeats().split(",")).stream())
                .toList();

        for (var passenger : req.passengers()) {
            if (bookedSeats.contains(passenger.seatNumber())) {
                throw new IllegalArgumentException(
                        "Seat " + passenger.seatNumber() + " is already booked."
                );
            }
        }

        if (schedule.getAvailableSeats() < seatCount) {
            throw new IllegalArgumentException(
                    "Not enough available seats. Requested: "
                            + seatCount + ", Available: " + schedule.getAvailableSeats());
        }


//        if (schedule.getAvailableSeats() < req.selectedSeats().size()) {
//            throw new IllegalArgumentException("Not enough available seats. Requested: "
//                    + req.selectedSeats().size() + ", Available: " + schedule.getAvailableSeats());
//        }

        double totalAmount = schedule.getFarePerSeat() * seatCount;
        String reference   = "BK" + UUID.randomUUID().toString()
                                        .replace("-", "")
                                        .substring(0, 8)
                                        .toUpperCase();



        String selectedSeats = req.passengers().stream()
                .map(p -> p.seatNumber())
                .collect(Collectors.joining(","));

        var booking = Booking.builder()
                .bookingReference(reference)
                .user(user)
                .schedule(schedule)
                .totalAmount(totalAmount)
                .status("PENDING")
                .selectedSeats(selectedSeats)
                .build();

//        var booking = Booking.builder()
//                .bookingReference(reference)
//                .user(user)
//                .schedule(schedule)
//                .totalAmount(totalAmount)
//                .status("PENDING")
//                .selectedSeats(String.join(",", req.selectedSeats()))
//                .build();

        List<Passenger> passengers = req.passengers().stream()
                .map(p -> Passenger.builder()
                        .booking(booking)
                        .name(p.name())
                        .age(p.age())
                        .gender(p.gender())
                        .seatNumber(p.seatNumber())
                        .idProofType(p.idProofType())
                        .idProofNumber(p.idProofNumber())
                        .build())
                .toList();

        booking.setPassengers(passengers);

        // Decrement available seats
        schedule.setAvailableSeats(schedule.getAvailableSeats() - seatCount);
        scheduleRepository.save(schedule);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: {} for user: {}", saved.getBookingReference(), userEmail);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getByReference(String reference) {
        return toResponse(
                bookingRepository.findByBookingReferenceWithDetails(reference)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking", "reference", reference))
        );
    }

    @Override
    @Transactional (readOnly = true)
    public List<BookingResponse> getByUser(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        return bookingRepository.findByUserIdWithDetails(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAll() {
        return bookingRepository.findAllWithDetails().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void cancel(Long id, String userEmail) {
        var booking = findById(id);

        // Only the owner or admin can cancel
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You are not authorized to cancel this booking");
        }
        if (booking.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");

        // Restore available seats
        var schedule      = booking.getSchedule();
        int seatsRestored = booking.getSelectedSeats().split(",").length;

        int newAvailableSeats =
                schedule.getAvailableSeats() + seatsRestored;

        schedule.setAvailableSeats(
                Math.min(newAvailableSeats, schedule.getBus().getTotalSeats())
        );

        bookingRepository.save(booking);
        log.info("Booking cancelled: {}", booking.getBookingReference());
    }

    // ── Mapper ─────────────────────────────────────────────

    public BookingResponse toResponse(Booking b) {
        List<PassengerResponse> passengerResponses = b.getPassengers() == null
                ? List.of()
                : b.getPassengers().stream()
                        .map(p -> new PassengerResponse(
                                p.getId(), p.getName(), p.getAge(),
                                p.getGender(), p.getSeatNumber(),
                                p.getIdProofType(), p.getIdProofNumber()))
                        .toList();

        PaymentResponse paymentResponse = null;
        if (b.getPayment() != null) {
            var pay = b.getPayment();
            paymentResponse = new PaymentResponse(
                    pay.getId(), pay.getAmount(), pay.getPaymentMethod(),
                    pay.getStatus(), pay.getTransactionId(),
                    pay.getPaymentDate(), pay.getFailureReason());
        }

        return new BookingResponse(
                b.getId(),
                b.getBookingReference(),
                b.getUser().getId(),
                b.getUser().getName(),
                scheduleMapper.toResponse(b.getSchedule()),
                b.getBookingDate(),
                b.getTotalAmount(),
                b.getStatus(),
                b.getSelectedSeats(),
                passengerResponses,
                paymentResponse
        );
    }

    private Booking findById(Long id) {
        return bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }
}
