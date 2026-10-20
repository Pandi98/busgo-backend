package com.busticketbooking.repository;

import com.busticketbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
            SELECT s FROM Schedule s
            JOIN FETCH s.bus b
            JOIN FETCH s.route r
            WHERE LOWER(r.source)      = LOWER(:source)
              AND LOWER(r.destination) = LOWER(:destination)
              AND s.travelDate         = :travelDate
              AND s.status             = 'SCHEDULED'
            ORDER BY s.departureTime
            """)
    List<Schedule> searchByRouteAndDate(
            @Param("source")      String source,
            @Param("destination") String destination,
            @Param("travelDate")  LocalDate travelDate
    );

    List<Schedule> findByBusId(Long busId);

    List<Schedule> findByStatus(String status);

    long countByStatus(String status);
}
