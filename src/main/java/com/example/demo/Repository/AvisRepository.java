package com.example.demo.Repository;

import com.example.demo.Model.Avis;
import com.example.demo.Model.Activite;
import com.example.demo.Model.UtilisateurInscrit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvisRepository extends JpaRepository<Avis, UUID> {
    List<Avis> findByActiviteId(UUID activiteId);
    List<Avis> findByUtilisateurInscrit(UtilisateurInscrit utilisateur);
}
