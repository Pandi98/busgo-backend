package com.busticketbooking.repository;

import com.busticketbooking.entity.Booking;
import com.busticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.user                        
            JOIN FETCH b.schedule s
            JOIN FETCH s.bus
            JOIN FETCH s.route
            LEFT JOIN FETCH b.passengers
            LEFT JOIN FETCH b.payment
            WHERE b.user.id = :userId
            ORDER BY b.bookingDate DESC
            """)
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);

    List<Booking> findByUser(User user);

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByStatus(String status);

    long countByStatus(String status);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.user
        JOIN FETCH b.schedule s
        JOIN FETCH s.bus
        JOIN FETCH s.route
        LEFT JOIN FETCH b.passengers
        LEFT JOIN FETCH b.payment
        WHERE b.id = :id
        """)
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);


    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    Double getTotalRevenue();

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.schedule.id = :scheduleId
          AND b.status <> 'CANCELLED'
    """)
    List<Booking> findActiveBookingsByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("""
    SELECT b FROM Booking b
    JOIN FETCH b.user
    JOIN FETCH b.schedule s
    JOIN FETCH s.bus
    JOIN FETCH s.route
    LEFT JOIN FETCH b.passengers
    LEFT JOIN FETCH b.payment
    WHERE b.bookingReference = :reference
""")
    Optional<Booking> findByBookingReferenceWithDetails(
            @Param("reference") String reference);

    @Query("""
    SELECT DISTINCT b FROM Booking b
    JOIN FETCH b.user
    JOIN FETCH b.schedule s
    JOIN FETCH s.bus
    JOIN FETCH s.route
    LEFT JOIN FETCH b.passengers
    LEFT JOIN FETCH b.payment
    ORDER BY b.bookingDate DESC
""")
    List<Booking> findAllWithDetails();

}
