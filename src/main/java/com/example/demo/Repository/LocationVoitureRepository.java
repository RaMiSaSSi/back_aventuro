package com.example.demo.Repository;

import com.example.demo.Model.EtatDemande;
import com.example.demo.Model.LocationVoiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LocationVoitureRepository extends JpaRepository<LocationVoiture, UUID> {



    @Query("SELECT l FROM LocationVoiture l WHERE l.voiture.id = :voitureId " +
           "AND l.etatDemande IN ('EN_ATTENTE', 'VALIDEE', 'EN_COURS') " +
           "AND ((l.dateDebut <= :dateFin AND l.dateFin >= :dateDebut))")
    List<LocationVoiture> findByVoitureIdAndOverlappingDates(
            @Param("voitureId") UUID voitureId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
    List<LocationVoiture> findByUserIdOrderByDateCreationDesc(Long userId);
    @Query("SELECT l FROM LocationVoiture l WHERE l.voiture.id = :voitureId AND l.etatDemande IN ('EN_ATTENTE', 'VALIDEE', 'EN_COURS')")
    List<LocationVoiture> findActiveLocationsByVoitureId(@Param("voitureId") UUID voitureId);
    List<LocationVoiture> findByEtatDemande(EtatDemande etat);
    List<LocationVoiture> findByVoitureId(UUID voitureId);
    List<LocationVoiture> findByUserId(Long userId);

}