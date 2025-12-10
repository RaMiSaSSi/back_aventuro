package com.example.demo.Service.Admin;

import com.example.demo.Dto.VoitureDTO;

import java.util.List;
import java.util.UUID;

public interface AdminVoitureService {
    List<VoitureDTO> getVoitures();
    VoitureDTO getVoitureById(UUID id);
    VoitureDTO createVoiture(VoitureDTO dto);
    VoitureDTO updateVoiture(UUID id, VoitureDTO dto);
    void deleteVoiture(UUID id);
}