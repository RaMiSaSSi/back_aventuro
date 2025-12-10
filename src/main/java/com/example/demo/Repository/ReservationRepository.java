package com.example.demo.Repository;

import com.example.demo.Model.Reservation;
import com.example.demo.Model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUser_Id(Long userId);

    // find by statut
    List<Reservation> findByStatut(StatutReservation statut);

    // find by activite id
    List<Reservation> findByActivite_Id(UUID activiteId);
}