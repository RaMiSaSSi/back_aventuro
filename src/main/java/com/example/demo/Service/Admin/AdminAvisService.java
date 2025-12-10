package com.example.demo.Service.Admin;

import com.example.demo.Dto.AvisDTO;

import java.util.List;
import java.util.UUID;

public interface AdminAvisService {
    List<AvisDTO> getAllAvis();
    void deleteAvis(UUID id);
}
