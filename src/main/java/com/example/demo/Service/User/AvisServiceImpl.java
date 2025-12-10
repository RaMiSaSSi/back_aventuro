// java
package com.example.demo.Service.User;

import com.example.demo.Dto.AvisDTO;
import com.example.demo.Model.Avis;
import com.example.demo.Model.Activite;
import com.example.demo.Model.UtilisateurInscrit;
import com.example.demo.Repository.AvisRepository;
import com.example.demo.Repository.ActiviteRepository;
import com.example.demo.Repository.UtilisateurInscritRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvisServiceImpl implements AvisService {
    @Autowired
    private  AvisRepository avisRepository;
    @Autowired
    private  ActiviteRepository activiteRepository;
    @Autowired
    private  UtilisateurInscritRepository utilisateurRepository;



    @Override
    public AvisDTO createAvis(AvisDTO dto, Long utilisateurId) {
        UtilisateurInscrit user = utilisateurRepository.findById(utilisateurId).orElse(null);
        if (user == null) return null;

        Activite activite = null;
        if (dto.getActiviteId() != null) {
            activite = activiteRepository.findById(dto.getActiviteId()).orElse(null);
            if (activite == null) return null; // invalid activiteId
        }

        Avis a = new Avis();
        a.setUtilisateurInscrit(user);
        a.setActivite(activite); // may be null => platform-wide avis
        a.setNote(dto.getNote());
        a.setCommentaire(dto.getCommentaire());

        Avis saved = avisRepository.save(a);
        return toDto(saved);
    }

    @Override
    public List<AvisDTO> getAvisByActivite(UUID activiteId) {
        return avisRepository.findByActiviteId(activiteId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AvisDTO> getAvisByUtilisateur(Long utilisateurId) {
        UtilisateurInscrit user = utilisateurRepository.findById(utilisateurId).orElse(null);
        if (user == null) return List.of();
        return avisRepository.findByUtilisateurInscrit(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AvisDTO getAvisById(UUID id) {
        return avisRepository.findById(id).map(this::toDto).orElse(null);
    }
    @Override
    public List<AvisDTO> getAllAvis() {
        return avisRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
    @Override
    public double getAverageNote() {
        List<Avis> avis = avisRepository.findAll();
        return avis.stream()
                .mapToDouble(a -> a.getNote() != null ? a.getNote() : 0.0)
                .average()
                .orElse(0.0);
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