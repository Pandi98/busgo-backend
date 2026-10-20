package com.busticketbooking.util;

import com.busticketbooking.entity.*;
import com.busticketbooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository     userRepository;
    private final BusRepository      busRepository;
    private final RouteRepository    routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository     seatRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding demo data...");

        // ── Users ──────────────────────────────────────────
        var admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@busgo.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9000000001")
                .role(Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .name("Pandidurai")
                .email("pandidurai.k1998@busgo.com")
                .password(passwordEncoder.encode("user123"))
                .phone("9000000002")
                .role(Role.USER)
                .build());

        // ── Buses ──────────────────────────────────────────
        var b1 = saveBus("TN01AB1234", "KPN Travels", "AC", 40, "WiFi, Charging, Blanket");
        var b2 = saveBus("TN02CD5678", "SRS Travels", "SLEEPER", 36, "WiFi, Water Bottle, Pillow");
        var b3 = saveBus("TN03EF9012", "VRL Travels", "SEMI_SLEEPER", 42, "Charging Point");
        var b4 = saveBus("TN04GH3456", "Orange Tours", "NON_AC", 50, "");
        var b5 = saveBus("TN05IJ7890", "Parveen Travels", "AC", 45, "WiFi, USB Charging, Water");

        // ── Routes ─────────────────────────────────────────
        var r1 = saveRoute("Chennai", "Bangalore", 346.0, 360);
        var r2 = saveRoute("Chennai", "Coimbatore", 500.0, 480);
        var r3 = saveRoute("Mumbai", "Pune", 150.0, 180);
        var r4 = saveRoute("Delhi", "Agra", 200.0, 210);
        var r5 = saveRoute("Hyderabad", "Chennai", 630.0, 600);
        var r6 = saveRoute("Kolkata", "Bhubaneswar", 440.0, 420);
        var r7 = saveRoute("Bangalore", "Mysore", 145.0, 165);
        var r8 = saveRoute("Coimbatore", "Chennai", 500.0, 480);

        // ── Schedules (Next 30 Days) ─────────────────────────────
        if (scheduleRepository.count() == 0) {
            List<Schedule> schedules = new ArrayList<>();

            for (int i = 0; i < 30; i++) {

                LocalDate date = LocalDate.now().plusDays(i);

                schedules.add(mkSchedule(b1, r1, date, "08:00", "14:00", 650.0));
                schedules.add(mkSchedule(b2, r1, date, "20:00", "06:00", 850.0));

                schedules.add(mkSchedule(b3, r2, date, "07:30", "15:30", 700.0));
                schedules.add(mkSchedule(b4, r2, date, "21:00", "05:30", 450.0));

                schedules.add(mkSchedule(b2, r3, date, "09:00", "12:00", 350.0));
                schedules.add(mkSchedule(b3, r3, date, "15:00", "18:00", 300.0));

                schedules.add(mkSchedule(b4, r4, date, "06:00", "09:30", 420.0));

                schedules.add(mkSchedule(b5, r5, date, "18:00", "08:00", 900.0));
                schedules.add(mkSchedule(b1, r5, date, "19:00", "09:00", 950.0));

                schedules.add(mkSchedule(b3, r6, date, "07:00", "14:00", 600.0));

                schedules.add(mkSchedule(b2, r7, date, "10:00", "12:45", 280.0));
                schedules.add(mkSchedule(b4, r7, date, "16:00", "18:45", 250.0));

                schedules.add(mkSchedule(b5, r8, date, "06:30", "14:30", 680.0));
            }

            scheduleRepository.saveAll(schedules);

            log.info("✅ Seeded: 2 users | 5 buses | 8 routes | {} schedules",
                    schedules.size());

        }
    }

    // ── Helpers ────────────────────────────────────────────

    private Bus saveBus(String num, String name, String type, int seats, String amenities) {
        var bus = busRepository.save(Bus.builder()
                .busNumber(num).busName(name).busType(type)
                .totalSeats(seats).amenities(amenities).isActive(true)
                .build());
        generateSeats(bus);
        return bus;
    }

    private void generateSeats(Bus bus) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            seats.add(Seat.builder()
                    .bus(bus)
                    .seatNumber("S" + i)
                    .seatType(switch (i % 3) {
                        case 0  -> "AISLE";
                        case 1  -> "WINDOW";
                        default -> "MIDDLE";
                    })
                    .deckType(i <= bus.getTotalSeats() / 2 ? "LOWER" : "UPPER")
                    .isAvailable(true)
                    .build());
        }
        seatRepository.saveAll(seats);
    }

    private Route saveRoute(String src, String dst, double dist, int mins) {
        return routeRepository.save(Route.builder()
                .source(src).destination(dst)
                .distanceKm(dist).estimatedDurationMinutes(mins)
                .build());
    }

    private Schedule mkSchedule(Bus bus, Route route, LocalDate date,
                                 String dep, String arr, double fare) {
        return Schedule.builder()
                .bus(bus).route(route).travelDate(date)
                .departureTime(LocalTime.parse(dep))
                .arrivalTime(LocalTime.parse(arr))
                .farePerSeat(fare)
                .availableSeats(bus.getTotalSeats())
                .status("SCHEDULED")
                .build();
    }
}
