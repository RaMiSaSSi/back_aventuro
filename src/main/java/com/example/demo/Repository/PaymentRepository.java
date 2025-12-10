package com.example.demo.Repository;

import com.example.demo.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment findByFlouciPaymentId(String flouciPaymentId);
}