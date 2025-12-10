package com.example.demo.Service.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;

import java.util.List;

public interface UsersService {
    List<UtilisateurInscritDTO> findAll();
    UtilisateurInscritDTO findById(long id);
    UtilisateurInscritDTO create(UtilisateurInscritDTO dto);
    UtilisateurInscritDTO update(long id, UtilisateurInscritDTO dto);
    void delete(long id);
}