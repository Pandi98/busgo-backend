package com.busticketbooking.service;

import com.busticketbooking.dto.request.BookingRequest;
import com.busticketbooking.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse     create(BookingRequest request, String userEmail);
    BookingResponse     getById(Long id);
    BookingResponse     getByReference(String reference);
    List<BookingResponse> getByUser(String userEmail);
    List<BookingResponse> getAll();
    void                cancel(Long id, String userEmail);
}
