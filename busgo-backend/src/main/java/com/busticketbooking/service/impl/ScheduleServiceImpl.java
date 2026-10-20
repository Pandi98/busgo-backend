package com.busticketbooking.service.impl;

import com.busticketbooking.dto.request.ScheduleRequest;
import com.busticketbooking.dto.response.ScheduleResponse;
import com.busticketbooking.entity.Schedule;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.BusRepository;
import com.busticketbooking.repository.RouteRepository;
import com.busticketbooking.repository.ScheduleRepository;
import com.busticketbooking.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository      busRepository;
    private final RouteRepository    routeRepository;
    private final BusServiceImpl     busMapper;
    private final RouteServiceImpl   routeMapper;

    @Override
    @Transactional
    public ScheduleResponse create(ScheduleRequest req) {
        var bus   = busRepository.findById(req.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", req.busId()));
        var route = routeRepository.findById(req.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", req.routeId()));

        var schedule = Schedule.builder()
                .bus(bus)
                .route(route)
                .travelDate(req.travelDate())
                .departureTime(req.departureTime())
                .arrivalTime(req.arrivalTime())
                .farePerSeat(req.farePerSeat())
                .availableSeats(bus.getTotalSeats())
                .status(req.status() != null ? req.status() : "SCHEDULED")
                .build();

        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public ScheduleResponse update(Long id, ScheduleRequest req) {
        var schedule = findById(id);
        var bus   = busRepository.findById(req.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", req.busId()));
        var route = routeRepository.findById(req.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", req.routeId()));

        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setTravelDate(req.travelDate());
        schedule.setDepartureTime(req.departureTime());
        schedule.setArrivalTime(req.arrivalTime());
        schedule.setFarePerSeat(req.farePerSeat());
        if (req.status() != null) schedule.setStatus(req.status());

        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional (readOnly = true)
    public ScheduleResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional (readOnly = true)
    public List<ScheduleResponse> getAll() {
        return scheduleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<ScheduleResponse> search(String source, String destination, LocalDate travelDate) {
        return scheduleRepository
                .searchByRouteAndDate(source, destination, travelDate)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule", "id", id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        var schedule = findById(id);
        schedule.setStatus("CANCELLED");
        scheduleRepository.save(schedule);
    }

    // ── Package-level mapper ───────────────────────────────

    public ScheduleResponse toResponse(Schedule s) {
        return new ScheduleResponse(
                s.getId(),
                busMapper.toResponse(s.getBus()),
                routeMapper.toResponse(s.getRoute()),
                s.getTravelDate(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                s.getFarePerSeat(),
                s.getAvailableSeats(),
                s.getStatus()
        );
    }

    private Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
    }
}
