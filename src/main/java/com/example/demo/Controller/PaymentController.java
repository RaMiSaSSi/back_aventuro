package com.example.demo.Controller;

import com.example.demo.Model.Payment;
import com.example.demo.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            Double amount = Double.valueOf(paymentData.get("amount").toString());
            Long userId = Long.valueOf(paymentData.get("userId").toString());

            UUID reservationId = null;
            if (paymentData.get("reservationId") != null) {
                String resId = paymentData.get("reservationId").toString();
                if (!resId.startsWith("res_")) {
                    resId = "res_" + resId;
                }
                reservationId = UUID.nameUUIDFromBytes(resId.getBytes());
            }

            UUID locationVoitureId = null;
            if (paymentData.get("locationVoitureId") != null) {
                locationVoitureId = UUID.fromString(paymentData.get("locationVoitureId").toString());
            }

            Payment payment = paymentService.createPayment(amount, userId, reservationId, locationVoitureId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentId", payment.getFlouciPaymentId());
            response.put("redirectUrl", "http://localhost:4200/payment/test?payment_id=" + payment.getFlouciPaymentId());
            response.put("amount", payment.getAmount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Payment creation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/verify/{paymentId}")
    public ResponseEntity<Map<String, Object>> verifyPayment(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.verifyPayment(paymentId);

            Map<String, Object> response = new HashMap<>();
            if (payment != null) {
                response.put("success", true);
                response.put("status", payment.getStatus());
                response.put("amount", payment.getAmount());
                response.put("paymentId", payment.getFlouciPaymentId());
            } else {
                response.put("success", false);
                response.put("message", "Payment not found");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Payment verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);

            Map<String, Object> response = new HashMap<>();
            if (payment != null) {
                response.put("success", true);
                response.put("status", payment.getStatus());
                response.put("amount", payment.getAmount());
                response.put("createdAt", payment.getCreatedAt());
                response.put("updatedAt", payment.getUpdatedAt());
            } else {
                response.put("success", false);
                response.put("message", "Payment not found");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving payment status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}