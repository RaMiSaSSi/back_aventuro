package com.example.demo.Service.Auth;

import com.example.demo.Dto.AuthenticationResponse;
import com.example.demo.Dto.UtilisateurInscritDTO;
import com.example.demo.Model.UtilisateurInscrit;

public interface AuthService {
    AuthenticationResponse login(String email, String motDePasse);
    void saveVerificationCode(String email, String code);
    boolean verifyUserAndSave(String email, String code, UtilisateurInscritDTO utilisateurInscritDTO);
    UtilisateurInscrit findByEmail(String email);
    boolean emailExists(String email);
    UtilisateurInscritDTO getUserInfo(String jwtToken);
    UtilisateurInscritDTO updateProfile(String email, UtilisateurInscritDTO utilisateurInscritDTO);
    void initiatePasswordReset(String email);
    boolean resetPassword(String email, String token, String newPassword);
    Long getUserIdFromAuthHeader(String authHeader);
    boolean setMailingAuthorization(String email, boolean autoriseMailing);

}
