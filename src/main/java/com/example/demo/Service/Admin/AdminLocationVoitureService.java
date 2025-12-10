package com.example.demo.Service.Admin;

import com.example.demo.Dto.LocationVoitureDTO;
import com.example.demo.Model.EtatDemande;

import java.util.List;
import java.util.UUID;

public interface AdminLocationVoitureService {
    List<LocationVoitureDTO> getAllLocations();
    LocationVoitureDTO getLocationById(UUID locationId);
    List<LocationVoitureDTO> getLocationsByEtat(EtatDemande etat);
    LocationVoitureDTO validerLocation(UUID locationId);
    LocationVoitureDTO refuserLocation(UUID locationId);
    LocationVoitureDTO terminerLocation(UUID locationId);
    void supprimerLocation(UUID locationId);
    List<LocationVoitureDTO> getLocationsByVoiture(UUID voitureId);
    List<LocationVoitureDTO> getLocationsByUser(Long userId);
    LocationVoitureDTO createLocation(LocationVoitureDTO dto);
    LocationVoitureDTO updateLocation(UUID id, LocationVoitureDTO dto);
}