package com.busticketbooking.service.impl;

import com.busticketbooking.dto.request.BusRequest;
import com.busticketbooking.dto.response.BusResponse;
import com.busticketbooking.entity.Bus;
import com.busticketbooking.entity.Seat;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.BusRepository;
import com.busticketbooking.repository.SeatRepository;
import com.busticketbooking.service.BusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusServiceImpl implements BusService {

    private final BusRepository  busRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public BusResponse create(BusRequest req) {
        if (busRepository.existsByBusNumber(req.busNumber())) {
            throw new IllegalArgumentException("Bus number already exists: " + req.busNumber());
        }

        var bus = Bus.builder()
                .busNumber(req.busNumber())
                .busName(req.busName())
                .busType(req.busType())
                .totalSeats(req.totalSeats())
                .amenities(req.amenities())
                .isActive(req.isActive() != null ? req.isActive() : true)
                .build();

        Bus saved = busRepository.save(bus);
        generateSeats(saved);
        log.info("Bus created: {}", saved.getBusNumber());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BusResponse update(Long id, BusRequest req) {
        var bus = findById(id);
        bus.setBusName(req.busName());
        bus.setBusType(req.busType());
        bus.setTotalSeats(req.totalSeats());
        bus.setAmenities(req.amenities());
        if (req.isActive() != null) bus.setIsActive(req.isActive());
        return toResponse(busRepository.save(bus));
    }

    @Override
    public BusResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public List<BusResponse> getAll() {
        return busRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<BusResponse> getAllActive() {
        return busRepository.findByIsActive(true).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bus", "id", id);
        }
        busRepository.deleteById(id);
        log.info("Bus deleted: id={}", id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        var bus = findById(id);
        bus.setIsActive(!bus.getIsActive());
        busRepository.save(bus);
        log.info("Bus {} status toggled to {}", bus.getBusNumber(), bus.getIsActive());
    }

    // ── Package-level mapper (used by ScheduleServiceImpl) ─

    public BusResponse toResponse(Bus bus) {
        return new BusResponse(
                bus.getId(),
                bus.getBusNumber(),
                bus.getBusName(),
                bus.getBusType(),
                bus.getTotalSeats(),
                bus.getAmenities(),
                bus.getIsActive()
        );
    }

    // ── Private ────────────────────────────────────────────

    private Bus findById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
    }

    private void generateSeats(Bus bus) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            seats.add(Seat.builder()
                    .bus(bus)
                    .seatNumber("S" + i)
                    .seatType(i % 3 == 0 ? "AISLE" : i % 3 == 1 ? "WINDOW" : "MIDDLE")
                    .deckType(i <= bus.getTotalSeats() / 2 ? "LOWER" : "UPPER")
                    .isAvailable(true)
                    .build());
        }
        seatRepository.saveAll(seats);
    }
}
