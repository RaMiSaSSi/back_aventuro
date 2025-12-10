// java
    package com.example.demo.Dto;

    import com.example.demo.Model.CategorieActivite;
    import lombok.Getter;
    import lombok.Setter;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.List;
    import java.util.UUID;

    @Getter
    @Setter
    public class ActiviteDTO {
        private UUID id;
        private String titre;
        private String description;
        private CategorieActivite categorie;
        private BigDecimal prix;
        private Float taux;
        private Integer duree;
        private String lieu;
        private List<String> images;
        private String video;
        private Boolean estActive;
        private LocalTime heureDebut;
        private LocalTime heureFin;

        // Promotion attributes
        private Boolean promoActive;
        private Integer promoPercent;
        private LocalDate promoStartDate;
        private LocalDate promoEndDate;
    }