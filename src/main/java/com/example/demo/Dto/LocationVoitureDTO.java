package com.example.demo.Dto;

import com.example.demo.Model.EtatDemande;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LocationVoitureDTO {
    private UUID id;
    private UUID voitureId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private BigDecimal montant;
    private String lieuRecuperation;
    private String lieuRetour;
    private Long userId;
    private EtatDemande etatDemande;
    private LocalDateTime dateCreation;
    // Informations client obligatoires
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientNumTel;
}