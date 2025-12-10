// java
// File: src/main/java/com/example/demo/Dto/ReservationCreateDTO.java
package com.example.demo.Dto;

import com.example.demo.Model.PaymentMehod;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReservationCreateDTO {
    private Long userId;
    private UUID activiteId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer nombreParticipantsAdults;
    private Integer nombreParticipantsChildren;
    private String LieuRencontre;
    private PaymentMehod payment;
    private boolean payé;
    private String devis;

    // Manual client info for non-registered users
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientNumTel;
    private String clientPays;
}