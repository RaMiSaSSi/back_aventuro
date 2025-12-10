package com.example.demo.Service.User;

import com.example.demo.Dto.AvisDTO;

import java.util.List;
import java.util.UUID;

public interface AvisService {
    AvisDTO createAvis(AvisDTO dto, Long utilisateurId);
    List<AvisDTO> getAvisByActivite(UUID activiteId);
    List<AvisDTO> getAvisByUtilisateur(Long utilisateurId);
    AvisDTO getAvisById(UUID id);
    List<AvisDTO> getAllAvis();
    double getAverageNote();
}
