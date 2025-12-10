package com.example.demo.Model;

        import com.example.demo.Dto.UtilisateurInscritDTO;
        import com.fasterxml.jackson.annotation.JsonIgnore;
        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import jakarta.persistence.*;
        import lombok.Getter;
        import lombok.Setter;

        import java.time.LocalDate;
        import java.util.Date;
        import java.util.HashSet;
        import java.util.Set;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

        @Getter
        @Setter
        @Entity
        @Inheritance(strategy = InheritanceType.JOINED)
        public class UtilisateurInscrit  {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private long id;
            private String email;
            private String motDePasse;
            private String nom;
            private String prenom;
            private String telephone;
            private Date dateInscription;
            @Enumerated(EnumType.STRING)
            private Role role;
            private String adresse;
            private String ville;
            private String codePostal;
            private String pays;
            private LocalDate dateNaissance;
            private String imagePath;
            private boolean autoriseMailing;

            @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
            @JsonIgnore
            private Set<Reservation> reservations = new HashSet<>();
            public UtilisateurInscritDTO getDTO() {
                UtilisateurInscritDTO dto = new UtilisateurInscritDTO();
                dto.setId(id);
                dto.setEmail(email);
                dto.setMotDePasse(null);
                dto.setNom(nom);
                dto.setPrenom(prenom);
                dto.setTelephone(telephone);
                dto.setDateInscription(dateInscription);
                dto.setRole(role);
                dto.setAdresse(adresse);
                dto.setVille(ville);
                dto.setCodePostal(codePostal);
                dto.setPays(pays);
                dto.setDateNaissance(dateNaissance);
                dto.setImagePath(imagePath);
                return dto;
            }
        }