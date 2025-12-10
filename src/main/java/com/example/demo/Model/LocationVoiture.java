// java
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
    public class LocationVoiture {
        @Id
        @GeneratedValue
        private UUID id;

        // Rendre user optionnel
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private UtilisateurInscrit user;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "voiture_id")
        private Voiture voiture;

        @Enumerated(EnumType.STRING)
        private EtatDemande etatDemande;

        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private BigDecimal montant;
        private LocalDateTime dateCreation;
        private String lieuRecuperation;
        private String lieuRetour;

        // Nouvelles informations client
        private String clientNom;
        private String clientPrenom;
        private String clientEmail;
        private String clientNumTel;
    }