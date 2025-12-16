package com.example.demo.Service.User;

import com.example.demo.Dto.VoitureDTO;

import java.util.List;
import java.util.UUID;

public interface UserVoitureService {
    List<VoitureDTO> getVoitures();
    VoitureDTO getVoitureById(UUID id);
    List<VoitureDTO> searchVoitures(String q);
}