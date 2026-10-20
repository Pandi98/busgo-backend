package com.busticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = @UniqueConstraint(name = "uk_payment_transaction", columnNames = "transaction_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_payment_booking"))
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;   // UPI | CREDIT_CARD | DEBIT_CARD | NET_BANKING

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";  // PENDING | SUCCESS | FAILED | REFUNDED

    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @CreationTimestamp
    @Column(name = "payment_date", nullable = false, updatable = false)
    private LocalDateTime paymentDate;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;
}
