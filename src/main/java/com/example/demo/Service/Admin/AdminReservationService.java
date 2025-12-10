// java
package com.example.demo.Service.Admin;

import com.example.demo.Dto.ReservationDTO;
import com.example.demo.Model.StatutReservation;

import java.util.List;
import java.util.UUID;

public interface AdminReservationService {
    List<ReservationDTO> findAll();
    ReservationDTO findById(UUID id);
    ReservationDTO updateStatus(UUID id, StatutReservation statut);
    void delete(UUID id);
    List<ReservationDTO> findByStatut(StatutReservation statut);
    List<ReservationDTO> findByActiviteId(UUID activiteId);

    ReservationDTO create(ReservationDTO dto);
}