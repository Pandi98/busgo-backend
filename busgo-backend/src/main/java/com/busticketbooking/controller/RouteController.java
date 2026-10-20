package com.busticketbooking.controller;

import com.busticketbooking.dto.request.RouteRequest;
import com.busticketbooking.dto.response.ApiResponse;
import com.busticketbooking.dto.response.RouteResponse;
import com.busticketbooking.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Routes fetched", routeService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RouteResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Route fetched", routeService.getById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> search(
            @RequestParam String source,
            @RequestParam String destination) {
        return ResponseEntity.ok(
                ApiResponse.success("Routes found", routeService.search(source, destination)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RouteResponse>> create(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Route created", routeService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RouteResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Route updated", routeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Route deleted", null));
    }
}
