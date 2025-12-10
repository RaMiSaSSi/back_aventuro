// java
        // File: src/main/java/com/example/demo/Dto/ActiviteDTO.java
        package com.example.demo.Dto;

        import com.example.demo.Model.CategorieActivite;
        import lombok.Getter;
        import lombok.Setter;

        import java.math.BigDecimal;
        import java.time.LocalDate;
        import java.time.LocalTime;
        import java.util.ArrayList;
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
            private List<String> images = new ArrayList<>();
            private String video;
            private Boolean estActive;
            private LocalTime heureDebut;
            private LocalTime heureFin;

            // Promotion attributes
            private Boolean promoActive;
            private Integer promoPercent;
            private LocalDate promoStartDate;
            private LocalDate promoEndDate;

            // New fields matching the Activite model (initialized to non-null defaults)
            private List<String> informationsSupplementaires = new ArrayList<>();
            private List<String> inclus = new ArrayList<>();
            private List<String> nonInclus = new ArrayList<>();
            private String conditionsAnnulation = "";
            private BigDecimal fraisService = BigDecimal.ZERO;
        }