package com.example.demo.Service.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersService {
    Page<UtilisateurInscritDTO> findAll(Pageable pageable);
    UtilisateurInscritDTO findById(long id);
    UtilisateurInscritDTO create(UtilisateurInscritDTO dto);
    UtilisateurInscritDTO update(long id, UtilisateurInscritDTO dto);
    void delete(long id);
}