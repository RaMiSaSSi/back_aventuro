package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String flouciPaymentId;
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED
    private Double amount;
    private String currency = "TND";

    @Column(name = "reservation_id")
    private UUID reservationId;

    @Column(name = "location_voiture_id")
    private UUID locationVoitureId;

    @Column(name = "user_id")
    private Long userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters
    // ... constructors
}