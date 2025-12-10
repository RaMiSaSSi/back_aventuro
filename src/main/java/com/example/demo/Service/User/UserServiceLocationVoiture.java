package com.example.demo.Service.User;

import com.example.demo.Dto.DateIndisponibleDTO;
import com.example.demo.Dto.LocationVoitureDTO;
import com.example.demo.Model.LocationVoiture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserServiceLocationVoiture {
    void annulerLocation(UUID locationId, Long userId);
    List<LocationVoiture> getMesLocations(Long userId);
    LocationVoiture creerLocation(LocationVoitureDTO dto);
    boolean isVoitureDisponible(UUID voitureId, LocalDateTime dateDebut, LocalDateTime dateFin);
    List<DateIndisponibleDTO> getUnavailableDates(UUID voitureId);
}