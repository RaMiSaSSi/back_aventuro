package com.example.demo.Service.Admin;

import com.example.demo.Dto.AvisDTO;
import com.example.demo.Model.Avis;
import com.example.demo.Repository.AvisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminAvisServiceImpl implements AdminAvisService {

    private final AvisRepository avisRepository;

    public AdminAvisServiceImpl(AvisRepository avisRepository) {
        this.avisRepository = avisRepository;
    }

    @Override
    public List<AvisDTO> getAllAvis() {
        return avisRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteAvis(UUID id) {
        avisRepository.deleteById(id);
    }

    private AvisDTO toDto(Avis a) {
        AvisDTO dto = new AvisDTO();
        dto.setId(a.getId());
        if (a.getUtilisateurInscrit() != null) {
            dto.setUtilisateurId(a.getUtilisateurInscrit().getId());
            dto.setUtilisateurPrenom(a.getUtilisateurInscrit().getPrenom());
            dto.setUtilisateurNom(a.getUtilisateurInscrit().getNom());
            dto.setUtilisateurImageUrl(a.getUtilisateurInscrit().getImagePath());
        }
        if (a.getActivite() != null) dto.setActiviteId(a.getActivite().getId());
        dto.setNote(a.getNote());
        dto.setCommentaire(a.getCommentaire());
        dto.setDateCreation(a.getDateCreation());
        return dto;
    }
}
