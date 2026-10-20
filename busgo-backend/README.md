# 🚌 BusGo — Spring Boot Backend

**Stack:** Spring Boot 3.5.0 · Java 25 · Spring Security 6 · jjwt 0.12.6 · MySQL 8 · Lombok

---

## 🗂️ Package Structure

```
com.busticketbooking/
├── BusTicketBookingApplication.java
├── config/
│   ├── CorsConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java               ← jjwt 0.12.x API
│   └── SecurityConfig.java           ← Spring Security 6 lambda DSL
├── controller/
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── BookingController.java
│   ├── BusController.java
│   ├── PaymentController.java
│   ├── RouteController.java
│   ├── ScheduleController.java
│   └── UserController.java
├── dto/
│   ├── request/                      ← Java Records (Java 25)
│   │   ├── AuthRequest.java          ← nested Login + Register records
│   │   ├── BookingRequest.java
│   │   ├── BusRequest.java
│   │   ├── PassengerRequest.java
│   │   ├── PaymentRequest.java
│   │   ├── RouteRequest.java
│   │   └── ScheduleRequest.java
│   └── response/                     ← Java Records
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── BookingResponse.java
│       ├── BusResponse.java
│       ├── DashboardResponse.java
│       ├── PassengerResponse.java
│       ├── PaymentResponse.java
│       ├── RouteResponse.java
│       └── ScheduleResponse.java
├── entity/
│   ├── Booking.java
│   ├── Bus.java
│   ├── Passenger.java
│   ├── Payment.java
│   ├── Role.java
│   ├── Route.java
│   ├── Schedule.java
│   ├── Seat.java
│   └── User.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── BookingRepository.java
│   ├── BusRepository.java
│   ├── PaymentRepository.java
│   ├── RouteRepository.java
│   ├── ScheduleRepository.java
│   ├── SeatRepository.java
│   └── UserRepository.java
├── service/
│   ├── AuthService.java
│   ├── BookingService.java
│   ├── BusService.java
│   ├── PaymentService.java
│   ├── RouteService.java
│   └── ScheduleService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── BookingServiceImpl.java
│       ├── BusServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── RouteServiceImpl.java
│       └── ScheduleServiceImpl.java
└── util/
    └── DataSeeder.java               ← Seeds 2 users, 5 buses, 8 routes, 15 schedules
```

---

## ⚙️ Setup

### 1. Prerequisites
- JDK 25.0.1
- IntelliJ IDEA 2025.3
- MySQL 8.x

### 2. IntelliJ IDEA Setup
```
File → Project Structure → Project SDK → Java 25
File → Settings → Build → Compiler → Java Compiler → Target bytecode version → 25
```

Enable preview features in IntelliJ:
```
Settings → Build → Compiler → Java Compiler
→ Additional command line parameters: --enable-preview
```

### 3. Database
MySQL database is auto-created on first boot via:
```
createDatabaseIfNotExist=true
```
Update credentials in `application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 4. Run
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```
Or run `BusTicketBookingApplication.java` directly in IntelliJ.

Server starts at **http://localhost:8080**

---

## 🔐 Demo Credentials (auto-seeded)

| Role  | Email           | Password  |
|-------|-----------------|-----------|
| ADMIN | admin@busgo.com | admin123  |
| USER  | user@busgo.com  | user123   |

---

## 📡 API Reference

### Auth (Public)
| Method | URL                   | Body                          |
|--------|-----------------------|-------------------------------|
| POST   | /api/auth/register    | `{name,email,password,phone}` |
| POST   | /api/auth/login       | `{email,password}`            |

### Buses
| Method | URL                            | Auth  |
|--------|--------------------------------|-------|
| GET    | /api/buses                     | Any   |
| GET    | /api/buses/active              | Any   |
| GET    | /api/buses/{id}                | Any   |
| POST   | /api/buses                     | ADMIN |
| PUT    | /api/buses/{id}                | ADMIN |
| DELETE | /api/buses/{id}                | ADMIN |
| PATCH  | /api/buses/{id}/toggle-status  | ADMIN |

### Routes
| Method | URL                                         | Auth   |
|--------|---------------------------------------------|--------|
| GET    | /api/routes                                 | Any    |
| GET    | /api/routes/{id}                            | Any    |
| GET    | /api/routes/search?source=&destination=     | Public |
| POST   | /api/routes                                 | ADMIN  |
| PUT    | /api/routes/{id}                            | ADMIN  |
| DELETE | /api/routes/{id}                            | ADMIN  |

### Schedules
| Method | URL                                                         | Auth   |
|--------|-------------------------------------------------------------|--------|
| GET    | /api/schedules/search?source=&destination=&travelDate=      | Public |
| GET    | /api/schedules                                              | Any    |
| GET    | /api/schedules/{id}                                         | Any    |
| POST   | /api/schedules                                              | ADMIN  |
| PUT    | /api/schedules/{id}                                         | ADMIN  |
| DELETE | /api/schedules/{id}                                         | ADMIN  |
| PATCH  | /api/schedules/{id}/cancel                                  | ADMIN  |

### Bookings
| Method | URL                              | Auth |
|--------|----------------------------------|------|
| POST   | /api/bookings                    | USER |
| GET    | /api/bookings/my-bookings        | USER |
| GET    | /api/bookings/{id}               | Any  |
| GET    | /api/bookings/reference/{ref}    | Any  |
| PATCH  | /api/bookings/{id}/cancel        | USER |

### Payments
| Method | URL                              | Auth |
|--------|----------------------------------|------|
| POST   | /api/payments/process            | Any  |
| GET    | /api/payments/booking/{id}       | Any  |
| POST   | /api/payments/refund/{bookingId} | Any  |

### Admin
| Method | URL                   | Auth  |
|--------|-----------------------|-------|
| GET    | /api/admin/dashboard  | ADMIN |
| GET    | /api/admin/bookings   | ADMIN |

---

## 🆕 What's New vs Old Version

| Feature              | Old (3.2 / Java 17)        | New (3.5 / Java 25)              |
|----------------------|----------------------------|----------------------------------|
| Java version         | 17                         | 25 (with --enable-preview)       |
| Spring Boot          | 3.2.0                      | 3.5.0                            |
| jjwt                 | 0.11.5 (deprecated API)    | 0.12.6 (new fluent API)          |
| DTOs                 | Lombok @Data classes       | Java Records                     |
| JWT builder          | `.setClaims()` etc.        | `.claims()` fluent API           |
| JWT parser           | `parserBuilder()`          | `parser().verifyWith()`          |
| Security DSL         | chained methods            | Lambda DSL only                  |
| Text blocks in JPQL  | String concatenation       | `"""..."""` text blocks          |
| Switch expression    | if-else                    | `switch` expression (arrow)      |
| Null checks          | Optional boilerplate       | Pattern matching + records       |
| Logging              | Basic                      | Structured with @Slf4j           |
