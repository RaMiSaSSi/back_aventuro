package com.example.demo.Dto;

import com.example.demo.Model.PaymentMehod;
import com.example.demo.Model.StatutReservation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReservationDTO {
    private UUID id;
    private Long userId;
    private UUID activiteId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer nombreParticipantsAdults;
    private Integer nombreParticipantsChildren;
    private StatutReservation statut;
    private BigDecimal montantTotal;
    private LocalDateTime dateCreation;
    private PaymentMehod payment;
    private boolean payé;
    private String devis;
    private String LieuRencontre;
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientNumTel;
    private String clientPays;
}