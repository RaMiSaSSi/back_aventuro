package com.example.demo.Repository;

import com.example.demo.Model.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VoitureRepository extends JpaRepository<Voiture, UUID> {
    @Query("select v from Voiture v where lower(v.marque) like concat('%', lower(:q), '%') or lower(v.modele) like concat('%', lower(:q), '%')")
    List<Voiture> searchByKeyword(@Param("q") String q);
}