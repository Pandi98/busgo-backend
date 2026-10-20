package com.busticketbooking.service;

import com.busticketbooking.dto.request.RouteRequest;
import com.busticketbooking.dto.response.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse     create(RouteRequest request);
    RouteResponse     update(Long id, RouteRequest request);
    RouteResponse     getById(Long id);
    List<RouteResponse> getAll();
    List<RouteResponse> search(String source, String destination);
    void              delete(Long id);
}
