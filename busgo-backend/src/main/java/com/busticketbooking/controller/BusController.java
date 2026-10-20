package com.busticketbooking.controller;

import com.busticketbooking.dto.request.BusRequest;
import com.busticketbooking.dto.response.ApiResponse;
import com.busticketbooking.dto.response.BusResponse;
import com.busticketbooking.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Buses fetched", busService.getAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success("Active buses fetched", busService.getAllActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Bus fetched", busService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusResponse>> create(@Valid @RequestBody BusRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bus created", busService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Bus updated", busService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Bus deleted", null));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        busService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Bus status toggled", null));
    }
}
