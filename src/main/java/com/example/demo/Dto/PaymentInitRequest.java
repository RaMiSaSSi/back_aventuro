package com.example.demo.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class PaymentInitRequest {
    private Double amount;
    private UUID reservationId;
    private UUID locationVoitureId;


}