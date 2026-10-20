package com.busticketbooking.service;

import com.busticketbooking.dto.request.BusRequest;
import com.busticketbooking.dto.response.BusResponse;

import java.util.List;

public interface BusService {
    BusResponse   create(BusRequest request);
    BusResponse   update(Long id, BusRequest request);
    BusResponse   getById(Long id);
    List<BusResponse> getAll();
    List<BusResponse> getAllActive();
    void          delete(Long id);
    void          toggleStatus(Long id);
}
