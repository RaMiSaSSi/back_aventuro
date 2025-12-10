package com.example.demo.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaymentInitResponse {
    private String paymentId;
    private String redirectUrl;
    private boolean success;


}