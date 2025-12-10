package com.example.demo.Service.Auth;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import com.example.demo.Dto.AuthenticationResponse;
import com.example.demo.Dto.UtilisateurInscritDTO;
import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.Role;
import com.example.demo.Model.UtilisateurInscrit;
import com.example.demo.Model.VerificationCode;
import com.example.demo.Repository.PasswordResetTokenRepository;
import com.example.demo.Repository.UtilisateurInscritRepository;
import com.example.demo.Repository.VerificationCodeRepository;
import com.example.demo.Service.Auth.AuthService;
import com.example.demo.Service.Auth.EmailService;
import com.example.demo.Utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UtilisateurInscritRepository utilisateurInscritRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private EmailService emailService;
    @Override
    public AuthenticationResponse login(String email, String motDePasse) {
        Optional<UtilisateurInscrit> utilisateurInscritOptional = utilisateurInscritRepository.findByEmail(email);
        if (utilisateurInscritOptional.isPresent()) {
            UtilisateurInscrit utilisateurInscrit = utilisateurInscritOptional.get();
            if (passwordEncoder.matches(motDePasse, utilisateurInscrit.getMotDePasse())) {
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        utilisateurInscrit.getEmail(),
                        utilisateurInscrit.getMotDePasse(),
                        List.of(new SimpleGrantedAuthority(utilisateurInscrit.getRole().name()))
                );
                String jwt = jwtUtil.generateToken(userDetails);
                return new AuthenticationResponse(jwt);
            }
        }
        throw new IllegalArgumentException("Invalid email or password");
    }
    @Override
    public UtilisateurInscrit findByEmail(String email) {
        return utilisateurInscritRepository.findByEmail(email)
                .orElse(null); // Return null if the user is not found
    }


    @Override
    public boolean verifyUserAndSave(String email, String code, UtilisateurInscritDTO utilisateurInscritDTO) {
        if (utilisateurInscritRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByEmailAndCode(email, code);
        if (verificationCodeOptional.isPresent()) {
            VerificationCode verificationCode = verificationCodeOptional.get();
            if (verificationCode.getExpiryDate().isAfter(LocalDateTime.now())) {
                UtilisateurInscrit utilisateurInscrit = new UtilisateurInscrit();
                utilisateurInscrit.setEmail(utilisateurInscritDTO.getEmail());
                utilisateurInscrit.setMotDePasse(passwordEncoder.encode(utilisateurInscritDTO.getMotDePasse()));
                utilisateurInscrit.setNom(utilisateurInscritDTO.getNom());
                utilisateurInscrit.setPrenom(utilisateurInscritDTO.getPrenom());
                utilisateurInscrit.setTelephone(utilisateurInscritDTO.getTelephone());
                utilisateurInscrit.setRole(Role.UTILISATEUR);
                utilisateurInscrit.setAdresse(utilisateurInscritDTO.getAdresse());
                utilisateurInscrit.setVille(utilisateurInscritDTO.getVille());
                utilisateurInscrit.setCodePostal(utilisateurInscritDTO.getCodePostal());
                utilisateurInscrit.setPays(utilisateurInscritDTO.getPays());
                utilisateurInscrit.setDateNaissance(utilisateurInscritDTO.getDateNaissance());
                utilisateurInscrit.setImagePath(utilisateurInscritDTO.getImagePath());
                utilisateurInscrit.setAutoriseMailing(false);
                String promoToken = utilisateurInscritDTO.getEmail() + "-" + System.currentTimeMillis();

                utilisateurInscritRepository.save(utilisateurInscrit);
                verificationCodeRepository.delete(verificationCode);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean emailExists(String email) {
        return utilisateurInscritRepository.existsByEmail(email);
    }
    @Transactional

    @Override
    public UtilisateurInscritDTO getUserInfo(String jwtToken) {
        String username = jwtUtil.extractUsername(jwtToken);
        UtilisateurInscrit utilisateurInscrit = utilisateurInscritRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToDTO(utilisateurInscrit);
    }


    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    @Override
    public void saveVerificationCode(String email, String code) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setEmail(email);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);
    }
    @Override
    @Transactional
    public UtilisateurInscritDTO updateProfile(String email, UtilisateurInscritDTO dto) {
        UtilisateurInscrit utilisateur = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getNom() != null) utilisateur.setNom(dto.getNom());
        if (dto.getPrenom() != null) utilisateur.setPrenom(dto.getPrenom());
        if (dto.getTelephone() != null) utilisateur.setTelephone(dto.getTelephone());
        if (dto.getAdresse() != null) utilisateur.setAdresse(dto.getAdresse());
        if (dto.getVille() != null) utilisateur.setVille(dto.getVille());
        if (dto.getCodePostal() != null) utilisateur.setCodePostal(dto.getCodePostal());
        if (dto.getPays() != null) utilisateur.setPays(dto.getPays());
        if (dto.getDateNaissance() != null) utilisateur.setDateNaissance(dto.getDateNaissance());

        // If password provided, update it (encode)
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            String provided = dto.getMotDePasse();
            // If the provided value looks like a hash (bcrypt/argon2) we will ignore it to avoid double-encoding
            boolean looksLikeBcrypt = provided.startsWith("$2a$") || provided.startsWith("$2b$") || provided.startsWith("$2y$");
            boolean looksLikeArgon = provided.startsWith("$argon");
            if (!looksLikeBcrypt && !looksLikeArgon) {
                utilisateur.setMotDePasse(passwordEncoder.encode(provided));
            } else {
                // ignore provided hashed value to avoid corrupting the stored password
            }
        }
        if (dto.getImagePath() != null) { // update image path when provided
            utilisateur.setImagePath(dto.getImagePath());
        }

        utilisateurInscritRepository.save(utilisateur);
        return convertToDTO(utilisateur);
    }
    private String generateToken(int byteLength) {
        byte[] bytes = new byte[byteLength];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    @Override
    public void initiatePasswordReset(String email) {
        UtilisateurInscrit user = utilisateurInscritRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // avoid revealing whether email exists; still return silently or log
            return;
        }
        String token = generateToken(32);
        PasswordResetToken prt = new PasswordResetToken();
        prt.setEmail(email);
        prt.setToken(token);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(prt);

        // Send email with token or full link (e.g., https://your-app/reset?token=...&email=...)
        emailService.sendResetPasswordEmail(email, token);
    }
    @Override
    @Transactional
    public boolean resetPassword(String email, String token, String newPassword) {
        Optional<PasswordResetToken> opt = passwordResetTokenRepository.findByEmailAndToken(email, token);
        if (opt.isEmpty()) return false;
        PasswordResetToken prt = opt.get();
        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(prt);
            return false;
        }

        UtilisateurInscrit user = utilisateurInscritRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurInscritRepository.save(user);

        passwordResetTokenRepository.delete(prt);
        return true;
    }
    @Override
    public Long getUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }

        // Normalize header: accept case-insensitive 'Bearer ' and trim
        String token = authHeader.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if (token.isBlank()) {
            return null;
        }

        try {
            // extract username (subject/email) from token using JwtUtils
            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isBlank()) {
                return null;
            }

            // find user by email and return its id (assumes UtilisateurInscrit has getId() -> Long)
            return utilisateurInscritRepository.findByEmail(username)
                    .map(u -> u.getId())
                    .orElse(null);
        } catch (Exception e) {
            // optionally log the error (use your logger or System.err)
            System.err.println("Failed to extract user id from auth header: " + e.getMessage());
            return null;
        }
    }


    private UtilisateurInscritDTO convertToDTO(UtilisateurInscrit utilisateurInscrit) {
        UtilisateurInscritDTO dto = new UtilisateurInscritDTO();
        dto.setId(utilisateurInscrit.getId());
        dto.setEmail(utilisateurInscrit.getEmail());
        // do NOT expose the password hash to the client
        // dto.setMotDePasse(utilisateurInscrit.getMotDePasse());
        dto.setNom(utilisateurInscrit.getNom());
        dto.setPrenom(utilisateurInscrit.getPrenom());
        dto.setTelephone(utilisateurInscrit.getTelephone());
        dto.setRole(utilisateurInscrit.getRole());
        dto.setAdresse(utilisateurInscrit.getAdresse());
        dto.setVille(utilisateurInscrit.getVille());
        dto.setCodePostal(utilisateurInscrit.getCodePostal());
        dto.setPays(utilisateurInscrit.getPays());
        dto.setDateNaissance(utilisateurInscrit.getDateNaissance());
        dto.setImagePath(utilisateurInscrit.getImagePath()); // include image path
        return dto;
    }

    private UtilisateurInscrit convertToEntity(UtilisateurInscritDTO dto) {
        UtilisateurInscrit utilisateurInscrit = new UtilisateurInscrit();
        utilisateurInscrit.setId(dto.getId());
        utilisateurInscrit.setEmail(dto.getEmail());
        utilisateurInscrit.setMotDePasse(dto.getMotDePasse());
        utilisateurInscrit.setNom(dto.getNom());
        utilisateurInscrit.setPrenom(dto.getPrenom());
        utilisateurInscrit.setTelephone(dto.getTelephone());
        utilisateurInscrit.setRole(Role.valueOf(dto.getRole().name()));
        utilisateurInscrit.setAdresse(dto.getAdresse());
        utilisateurInscrit.setVille(dto.getVille());
        utilisateurInscrit.setCodePostal(dto.getCodePostal());
        utilisateurInscrit.setPays(dto.getPays());
        utilisateurInscrit.setDateNaissance(dto.getDateNaissance());
        utilisateurInscrit.setImagePath(dto.getImagePath());
        return utilisateurInscrit;
    }

    @Override
    @Transactional
    public boolean setMailingAuthorization(String email, boolean autoriseMailing) {
        Optional<UtilisateurInscrit> opt = utilisateurInscritRepository.findByEmail(email);
        if (opt.isEmpty()) return false;
        UtilisateurInscrit user = opt.get();
        user.setAutoriseMailing(autoriseMailing);
        utilisateurInscritRepository.save(user);
        return true;
    }



}
