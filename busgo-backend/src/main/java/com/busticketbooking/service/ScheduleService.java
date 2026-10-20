package com.busticketbooking.service;

import com.busticketbooking.dto.request.ScheduleRequest;
import com.busticketbooking.dto.response.ScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ScheduleResponse     create(ScheduleRequest request);
    ScheduleResponse     update(Long id, ScheduleRequest request);
    ScheduleResponse     getById(Long id);
    List<ScheduleResponse> getAll();
    List<ScheduleResponse> search(String source, String destination, LocalDate travelDate);
    void                 delete(Long id);
    void                 cancel(Long id);
}
