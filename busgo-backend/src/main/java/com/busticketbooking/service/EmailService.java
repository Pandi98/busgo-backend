package com.busticketbooking.service;

import com.busticketbooking.entity.Booking;

public interface EmailService {

    void sendBookingConfirmation(Booking booking);
}
