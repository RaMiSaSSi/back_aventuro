package com.example.demo.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlouciPaymentResponse {
    private String paymentId;
    private String redirectUrl;
    private boolean success;

    // Getters and setters
}