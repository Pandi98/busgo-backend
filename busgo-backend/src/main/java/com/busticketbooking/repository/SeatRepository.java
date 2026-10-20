package com.busticketbooking.repository;

import com.busticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdAndIsAvailable(Long busId, Boolean isAvailable);

    Optional<Seat> findByBusIdAndSeatNumber(Long busId, String seatNumber);
}
