package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Favoris {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private UtilisateurInscrit user;

    @ManyToMany
    @JoinTable(name = "favoris_activites",
            joinColumns = @JoinColumn(name = "favoris_id"),
            inverseJoinColumns = @JoinColumn(name = "activite_id"))
    private Set<Activite> activites = new HashSet<>();
}