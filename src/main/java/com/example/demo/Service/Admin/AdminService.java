package com.example.demo.Service.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;

import java.util.List;

public interface AdminService {
    List<UtilisateurInscritDTO> findAllAdmins();
    UtilisateurInscritDTO findAdminById(long id);
    UtilisateurInscritDTO createAdmin(UtilisateurInscritDTO dto);
    UtilisateurInscritDTO updateAdmin(long id, UtilisateurInscritDTO dto);
    void deleteAdmin(long id);
}