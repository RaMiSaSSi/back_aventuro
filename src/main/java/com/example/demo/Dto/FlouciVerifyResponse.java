package com.example.demo.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlouciVerifyResponse {
    private boolean success;
    private String status;
    private Double amount;

    // Getters and setters
}