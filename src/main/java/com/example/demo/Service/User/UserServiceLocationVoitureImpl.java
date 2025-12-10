package com.example.demo.Service.User;

import com.example.demo.Dto.*;
import com.example.demo.Model.EtatDemande;
import com.example.demo.Model.LocationVoiture;
import com.example.demo.Model.UtilisateurInscrit;
import com.example.demo.Model.Voiture;
import com.example.demo.Repository.LocationVoitureRepository;
import com.example.demo.Repository.UtilisateurInscritRepository;
import com.example.demo.Repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceLocationVoitureImpl implements UserServiceLocationVoiture {

    @Autowired
    private LocationVoitureRepository locationRepository;

    @Autowired
    private UtilisateurInscritRepository userRepository;

    @Autowired
    private VoitureRepository voitureRepository;

    @Override
    @Transactional
    public LocationVoiture creerLocation(LocationVoitureDTO dto) {
        Voiture voiture = voitureRepository.findById(dto.getVoitureId())
                .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));

        if (!isVoitureDisponible(dto.getVoitureId(), dto.getDateDebut(), dto.getDateFin())) {
            throw new RuntimeException("La voiture n'est pas disponible pour ces dates");
        }

        LocationVoiture location = new LocationVoiture();
        location.setVoiture(voiture);
        location.setDateDebut(dto.getDateDebut());
        location.setDateFin(dto.getDateFin());
        location.setMontant(dto.getMontant());
        location.setLieuRecuperation(dto.getLieuRecuperation());
        location.setLieuRetour(dto.getLieuRetour());
        location.setEtatDemande(EtatDemande.EN_ATTENTE);
        location.setDateCreation(LocalDateTime.now());

        // Si userId est fourni, récupérer les infos de l'utilisateur
        if (dto.getUserId() != null) {
            UtilisateurInscrit user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            location.setUser(user);
            location.setClientNom(user.getNom());
            location.setClientPrenom(user.getPrenom());
            location.setClientEmail(user.getEmail());
            location.setClientNumTel(user.getTelephone());
        } else {
            // Sinon, utiliser les informations fournies manuellement
            if (dto.getClientNom() == null || dto.getClientPrenom() == null ||
                    dto.getClientEmail() == null || dto.getClientNumTel() == null) {
                throw new RuntimeException("Les informations client sont obligatoires si aucun utilisateur n'est spécifié");
            }

            location.setUser(null);
            location.setClientNom(dto.getClientNom());
            location.setClientPrenom(dto.getClientPrenom());
            location.setClientEmail(dto.getClientEmail());
            location.setClientNumTel(dto.getClientNumTel());
        }

        return locationRepository.save(location);
    }


    @Override
    @Transactional
    public void annulerLocation(UUID locationId, Long userId) {
        LocationVoiture location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        if (location.getUser().getId() != userId) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette location");
        }

        if (location.getEtatDemande() == EtatDemande.EN_COURS) {
            throw new RuntimeException("Impossible d'annuler une location validée ou en cours");
        }

        location.setEtatDemande(EtatDemande.ANNULEE);
        locationRepository.save(location);
    }

    @Override
    public boolean isVoitureDisponible(UUID voitureId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<LocationVoiture> locations = locationRepository.findByVoitureIdAndOverlappingDates(
                voitureId, dateDebut, dateFin);
        return locations.isEmpty();
    }

    @Override
    public List<LocationVoiture> getMesLocations(Long userId) {
        return locationRepository.findByUserIdOrderByDateCreationDesc(userId);
    }
    @Override
    public List<DateIndisponibleDTO> getUnavailableDates(UUID voitureId) {
        List<LocationVoiture> locations = locationRepository.findActiveLocationsByVoitureId(voitureId);

        return locations.stream()
                .map(location -> new DateIndisponibleDTO(
                        location.getDateDebut(),
                        location.getDateFin()
                ))
                .collect(Collectors.toList());
    }

}