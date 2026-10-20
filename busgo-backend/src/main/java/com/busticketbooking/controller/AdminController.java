package com.busticketbooking.controller;

import com.busticketbooking.dto.response.ApiResponse;
import com.busticketbooking.dto.response.BookingResponse;
import com.busticketbooking.dto.response.DashboardResponse;
import com.busticketbooking.repository.*;
import com.busticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository     userRepository;
    private final BusRepository      busRepository;
    private final RouteRepository    routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository  bookingRepository;
    private final BookingService     bookingService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {

        double revenue = bookingRepository.getTotalRevenue() != null
                         ? bookingRepository.getTotalRevenue() : 0.0;

        var dashboard = new DashboardResponse(
                userRepository.count(),
                busRepository.count(),
                routeRepository.count(),
                scheduleRepository.count(),
                bookingRepository.count(),
                bookingRepository.countByStatus("CONFIRMED"),
                bookingRepository.countByStatus("CANCELLED"),
                bookingRepository.countByStatus("PENDING"),
                revenue
        );

        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", dashboard));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(
                ApiResponse.success("All bookings fetched", bookingService.getAll()));
    }

    @GetMapping("/users/count")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        return ResponseEntity.ok(ApiResponse.success("User count", userRepository.count()));
    }
}
