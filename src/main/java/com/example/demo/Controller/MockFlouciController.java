package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock/flouci")
public class MockFlouciController {

    @PostMapping("/payment")
    public ResponseEntity<String> createPayment(@RequestBody String request) {
        System.out.println("Mock Flouci Payment Request: " + request);

        String mockResponse = """
            {
                "result": {
                    "payment_id": "mock_payment_%d",
                    "link": "http://localhost:4200/payment/test?payment_id=mock_payment_%d",
                    "success": true
                }
            }
            """.formatted(System.currentTimeMillis(), System.currentTimeMillis());

        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/verify/{paymentId}")
    public ResponseEntity<String> verifyPayment(@PathVariable String paymentId) {
        System.out.println("Mock Flouci Verify Request for: " + paymentId);

        String mockResponse = """
            {
                "result": {
                    "success": true,
                    "status": "SUCCESS",
                    "amount": 100.0
                }
            }
            """;

        return ResponseEntity.ok(mockResponse);
    }
}