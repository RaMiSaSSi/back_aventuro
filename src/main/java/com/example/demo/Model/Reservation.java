package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Reservation {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id",nullable = true)
    private UtilisateurInscrit user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private Integer nombreParticipantsAdults;
    private Integer nombreParticipantsChildren;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    private BigDecimal montantTotal;
    @Enumerated(EnumType.STRING)
    private PaymentMehod payment;
    private boolean payé;
    private String devis;

    private LocalDateTime dateCreation;
    private String LieuRencontre;
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientNumTel;
    private String clientPays;
}