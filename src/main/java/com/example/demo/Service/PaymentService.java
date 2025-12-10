package com.example.demo.Service;

        import com.example.demo.Dto.FlouciPaymentResponse;
        import com.example.demo.Dto.FlouciVerifyResponse;
        import com.example.demo.Model.Payment;
        import com.example.demo.Repository.PaymentRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
        import java.time.LocalDateTime;
        import java.util.UUID;

        @Service
        public class PaymentService {

            @Autowired
            private PaymentRepository paymentRepository;

            @Autowired
            private FlouciPaymentService flouciService;

            @Value("${app.client.url}")
            private String clientUrl;

            @Value("${flouci.test.mode:false}")
            private boolean testMode;

            public Payment createPayment(Double amount, Long userId, UUID reservationId, UUID locationVoitureId) {
                String description = reservationId != null ? "Activity Reservation Payment" : "Car Rental Payment";

                // Use test URLs when in test mode
                String successUrl = testMode ?
                    clientUrl + "/payment/success" :
                    clientUrl + "/payment/success";
                String failUrl = testMode ?
                    clientUrl + "/payment/fail" :
                    clientUrl + "/payment/fail";

                FlouciPaymentResponse flouciResponse = flouciService.createPayment(amount, description, successUrl, failUrl);

                Payment payment = new Payment();
                payment.setFlouciPaymentId(flouciResponse.getPaymentId());
                payment.setAmount(amount);
                payment.setUserId(userId);
                payment.setReservationId(reservationId);
                payment.setLocationVoitureId(locationVoitureId);
                payment.setStatus("PENDING");
                payment.setCreatedAt(LocalDateTime.now());

                return paymentRepository.save(payment);
            }

            public Payment verifyPayment(String flouciPaymentId) {
                Payment payment = paymentRepository.findByFlouciPaymentId(flouciPaymentId);
                if (payment == null) return null;

                FlouciVerifyResponse verifyResponse = flouciService.verifyPayment(flouciPaymentId);

                if (verifyResponse.isSuccess() && "SUCCESS".equals(verifyResponse.getStatus())) {
                    payment.setStatus("COMPLETED");
                } else {
                    payment.setStatus("FAILED");
                }

                payment.setUpdatedAt(LocalDateTime.now());
                return paymentRepository.save(payment);
            }

            // Add method to get payment by ID
            public Payment getPaymentById(String flouciPaymentId) {
                return paymentRepository.findByFlouciPaymentId(flouciPaymentId);
            }
        }