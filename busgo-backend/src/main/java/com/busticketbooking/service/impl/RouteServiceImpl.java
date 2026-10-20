package com.busticketbooking.service.impl;

import com.busticketbooking.dto.request.RouteRequest;
import com.busticketbooking.dto.response.RouteResponse;
import com.busticketbooking.entity.Route;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.RouteRepository;
import com.busticketbooking.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Override
    @Transactional
    public RouteResponse create(RouteRequest req) {
        var route = Route.builder()
                .source(req.source())
                .destination(req.destination())
                .distanceKm(req.distanceKm())
                .estimatedDurationMinutes(req.estimatedDurationMinutes())
                .build();
        return toResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public RouteResponse update(Long id, RouteRequest req) {
        var route = findById(id);
        route.setSource(req.source());
        route.setDestination(req.destination());
        route.setDistanceKm(req.distanceKm());
        route.setEstimatedDurationMinutes(req.estimatedDurationMinutes());
        return toResponse(routeRepository.save(route));
    }

    @Override
    public RouteResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public List<RouteResponse> getAll() {
        return routeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<RouteResponse> search(String source, String destination) {
        return routeRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route", "id", id);
        }
        routeRepository.deleteById(id);
    }

    // ── Package-level mapper ───────────────────────────────

    public RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getSource(),
                route.getDestination(),
                route.getDistanceKm(),
                route.getEstimatedDurationMinutes()
        );
    }

    private Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
    }
}
