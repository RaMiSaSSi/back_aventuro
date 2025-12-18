package com.example.demo.Service.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    Page<UtilisateurInscritDTO> findAllAdmins(Pageable pageable);
    UtilisateurInscritDTO findAdminById(long id);
    UtilisateurInscritDTO createAdmin(UtilisateurInscritDTO dto);
    UtilisateurInscritDTO updateAdmin(long id, UtilisateurInscritDTO dto);
    void deleteAdmin(long id);
}