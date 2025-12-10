// src/main/java/com/example/demo/dto/UtilisateurInscritDTO.java
package com.example.demo.Dto;


import com.example.demo.Model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class UtilisateurInscritDTO {
    private long id;
    private String email;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String telephone;
    private Date dateInscription;
    private Role role;
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    private LocalDate dateNaissance;
    private String imagePath;
    private boolean autoriseMailing;

}