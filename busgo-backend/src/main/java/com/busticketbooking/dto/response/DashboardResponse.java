package com.busticketbooking.dto.response;

public record DashboardResponse(
        long totalUsers,
        long totalBuses,
        long totalRoutes,
        long totalSchedules,
        long totalBookings,
        long confirmedBookings,
        long cancelledBookings,
        long pendingBookings,
        double totalRevenue
) {}
