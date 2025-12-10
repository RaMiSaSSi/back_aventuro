package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Avis {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurInscrit utilisateurInscrit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activite_id")
    private Activite activite;
    private Integer note;
    @Column(length = 2000)
    private String commentaire;
    private LocalDateTime dateCreation = LocalDateTime.now();
}
