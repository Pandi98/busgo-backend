package com.busticketbooking.service.impl;


import com.busticketbooking.entity.Booking;
import com.busticketbooking.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;



    @Override

    public void sendBookingConfirmation(Booking booking) {


        log.info("Sending booking confirmation email to {}", booking.getUser().getEmail());

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("pandibusgo@gmail.com");
        message.setTo(booking.getUser().getEmail());
        message.setSubject("🚌 BusGo - Booking Confirmation");

        String passengers = booking.getPassengers().stream()
                .map(p -> p.getName())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("N/A");

        message.setText(
                "Hello " + booking.getUser().getName() + ",\n\n"
                        + "Your booking has been confirmed.\n\n"
                        + "Booking Reference:\n"
                        + booking.getBookingReference() + "\n\n"
                        + "Bus:\n"
                        + booking.getSchedule().getBus().getBusName() + "\n\n"
                        + "Route:\n"
                        + booking.getSchedule().getRoute().getSource()
                        + " → "
                        + booking.getSchedule().getRoute().getDestination() + "\n\n"
                        + "Travel Date:\n"
                        + booking.getSchedule().getTravelDate() + "\n\n"
                        + "Departure:\n"
                        + booking.getSchedule().getDepartureTime() + "\n\n"
                        + "Seats:\n"
                        + booking.getSelectedSeats() + "\n\n"
                        + "Passengers:\n"
                        + passengers + "\n\n"
                        + "Amount:\n"
                        + "₹" + booking.getTotalAmount() + "\n\n"
                        + "Payment:\n"
                        + booking.getPayment().getStatus() + "\n\n"
                        + "Thank you for choosing BusGo.\n\n"
                        + "Happy Journey! 🚌"
        );

        mailSender.send(message);

        log.info("Booking confirmation email sent successfully to {}", booking.getUser().getEmail());
    }}
