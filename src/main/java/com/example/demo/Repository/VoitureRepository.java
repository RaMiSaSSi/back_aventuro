package com.example.demo.Repository;

import com.example.demo.Model.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoitureRepository extends JpaRepository<Voiture, UUID> {
}