package com.example.demo.Service.Admin;

import com.example.demo.Dto.ActiviteDTO;

import java.util.List;
import java.util.UUID;

public interface ActiviteService {
    List<ActiviteDTO> findAll();
    ActiviteDTO findById(UUID id);
    ActiviteDTO create(ActiviteDTO dto);
    ActiviteDTO update(UUID id, ActiviteDTO dto);
    void delete(UUID id);
}

