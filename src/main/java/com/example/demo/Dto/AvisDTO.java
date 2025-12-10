package com.example.demo.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class AvisDTO {
    private UUID id;
    private Long utilisateurId;
    private String utilisateurImageUrl;
    private String utilisateurPrenom;
    private String utilisateurNom;
    private UUID activiteId;
    private Integer note;

    private String commentaire;

    private LocalDateTime dateCreation;



}
