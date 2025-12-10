// java
                            // File: src/main/java/com/example/demo/Model/Activite.java
                            package com.example.demo.Model;

                            import com.fasterxml.jackson.annotation.JsonFormat;
                            import com.fasterxml.jackson.annotation.JsonIgnore;
                            import jakarta.persistence.*;
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
                            @Entity
                            public class Activite {
                                @Id
                                @GeneratedValue
                                private UUID id;

                                private String titre;

                                @Column(length = 2000)
                                private String description;

                                @Enumerated(EnumType.STRING)
                                private CategorieActivite categorie;

                                private BigDecimal prix;
                                private Float taux;

                                private Integer duree;

                                private String lieu;

                                @ElementCollection(fetch = FetchType.EAGER)
                                private List<String> images = new ArrayList<>();

                                // video path or URL associated with the activity
                                private String video;

                                private Boolean estActive = true;
                                @JsonFormat(pattern = "HH:mm")
                                private LocalTime heureDebut;

                                @JsonFormat(pattern = "HH:mm")
                                private LocalTime heureFin;

                                @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
                                @JsonIgnore
                                private List<Reservation> reservations = new ArrayList<>();

                                @Column(name = "promo_active")
                                private Boolean promoActive = false;

                                @Column(name = "promo_percent")
                                private Integer promoPercent; // 0-100

                                @Column(name = "promo_start_date")
                                @JsonFormat(pattern = "yyyy-MM-dd")
                                private LocalDate promoStartDate;

                                @Column(name = "promo_end_date")
                                @JsonFormat(pattern = "yyyy-MM-dd")
                                private LocalDate promoEndDate;

                                // --- New fields ---
                                @ElementCollection(fetch = FetchType.EAGER)
                                @Column(name = "informations_supplementaires")
                                private List<String> informationsSupplementaires = new ArrayList<>();

                                @ElementCollection(fetch = FetchType.EAGER)
                                @Column(name = "inclus")
                                private List<String> inclus = new ArrayList<>();

                                @ElementCollection(fetch = FetchType.EAGER)
                                @Column(name = "non_inclus")
                                private List<String> nonInclus = new ArrayList<>();

                                @Column(name = "conditions_annulation", length = 2000)
                                private String conditionsAnnulation;

                                @Column(name = "frais_service", precision = 10, scale = 2)
                                private BigDecimal fraisService = BigDecimal.ZERO;
                                // --- End new fields ---

                                public boolean isPromotionActive() {
                                    if (promoActive == null || !promoActive) return false;
                                    java.time.LocalDate today = java.time.LocalDate.now();
                                    if (promoStartDate != null && today.isBefore(promoStartDate)) return false;
                                    if (promoEndDate != null && today.isAfter(promoEndDate)) return false;
                                    return (promoPercent != null && promoPercent > 0);
                                }

                                public BigDecimal getEffectivePrice(int nombreParticipants) {
                                    if (prix == null) return BigDecimal.ZERO;
                                    BigDecimal base = prix.multiply(BigDecimal.valueOf(nombreParticipants));
                                    BigDecimal result = base;
                                    if (isPromotionActive()) {
                                        if (promoPercent != null && promoPercent > 0) {
                                            BigDecimal multiplier = BigDecimal.valueOf(promoPercent).divide(BigDecimal.valueOf(100));
                                            result = base.multiply(multiplier);
                                        }
                                    }
                                    // Add service fee (fixed per reservation)
                                    if (fraisService != null) {
                                        result = result.add(fraisService);
                                    }
                                    return result;
                                }

                                public BigDecimal calculerPrix(int nombreParticipants) {
                                    return getEffectivePrice(nombreParticipants);
                                }
                            }