package com.example.demo.Service.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;
import com.example.demo.Model.Role;
import com.example.demo.Model.UtilisateurInscrit;
import com.example.demo.Repository.UtilisateurInscritRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UsersService {

    private final UtilisateurInscritRepository repo;

    public UserServiceImpl(UtilisateurInscritRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtilisateurInscritDTO> findAll(Pageable pageable) {
        return repo.findAllByRole(Role.UTILISATEUR, pageable)
                .map(UtilisateurInscrit::getDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurInscritDTO findById(long id) {
        UtilisateurInscrit user = repo.findByIdAndRole(id, Role.UTILISATEUR)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getDTO();
    }

    @Override
    public UtilisateurInscritDTO create(UtilisateurInscritDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        UtilisateurInscrit u = new UtilisateurInscrit();
        apply(dto, u);
        u.setRole(Role.UTILISATEUR); // enforce role
        if (u.getDateInscription() == null) {
            u.setDateInscription(new Date());
        }
        UtilisateurInscrit saved = repo.save(u);
        return saved.getDTO();
    }

    @Override
    public UtilisateurInscritDTO update(long id, UtilisateurInscritDTO dto) {
        UtilisateurInscrit u = repo.findByIdAndRole(id, Role.UTILISATEUR)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (dto.getEmail() != null && repo.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Email already in use");
        }
        apply(dto, u);
        UtilisateurInscrit saved = repo.save(u);
        return saved.getDTO();
    }

    @Override
    public void delete(long id) {
        UtilisateurInscrit u = repo.findByIdAndRole(id, Role.UTILISATEUR)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        repo.delete(u);
    }

    private void apply(UtilisateurInscritDTO dto, UtilisateurInscrit u) {
        if (dto.getEmail() != null) u.setEmail(dto.getEmail());
        if (dto.getMotDePasse() != null) u.setMotDePasse(dto.getMotDePasse());
        if (dto.getNom() != null) u.setNom(dto.getNom());
        if (dto.getPrenom() != null) u.setPrenom(dto.getPrenom());
        if (dto.getTelephone() != null) u.setTelephone(dto.getTelephone());
        if (dto.getAdresse() != null) u.setAdresse(dto.getAdresse());
        if (dto.getVille() != null) u.setVille(dto.getVille());
        if (dto.getCodePostal() != null) u.setCodePostal(dto.getCodePostal());
        if (dto.getPays() != null) u.setPays(dto.getPays());
        if (dto.getDateNaissance() != null) u.setDateNaissance(dto.getDateNaissance());
        if (dto.getImagePath() != null) u.setImagePath(dto.getImagePath());
        if (dto.getDateInscription() != null) u.setDateInscription(dto.getDateInscription());
        if(dto.getRole() != null) u.setRole(dto.getRole());
    }
}