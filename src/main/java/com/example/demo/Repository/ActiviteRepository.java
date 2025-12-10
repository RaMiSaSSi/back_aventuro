package com.example.demo.Repository;

import com.example.demo.Model.Activite;
import com.example.demo.Model.CategorieActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActiviteRepository extends JpaRepository<Activite, UUID> {
    List<Activite> findByCategorie(CategorieActivite categorie);

    @Query("SELECT a FROM Activite a WHERE " +
           "LOWER(a.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.lieu) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Activite> searchActivites(@Param("keyword") String keyword);

}

