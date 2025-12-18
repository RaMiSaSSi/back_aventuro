// java
                    package com.example.demo.Service.Admin;

                    import com.example.demo.Dto.LocationVoitureDTO;
                    import com.example.demo.Model.EtatDemande;
                    import com.example.demo.Model.LocationVoiture;
                    import com.example.demo.Model.UtilisateurInscrit;
                    import com.example.demo.Model.Voiture;
                    import com.example.demo.Repository.LocationVoitureRepository;
                    import com.example.demo.Repository.UtilisateurInscritRepository;
                    import com.example.demo.Repository.VoitureRepository;
                    import com.example.demo.Service.Auth.EmailService;
                    import jakarta.mail.MessagingException;
                    import org.slf4j.Logger;
                    import org.slf4j.LoggerFactory;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.stereotype.Service;
                    import org.springframework.transaction.annotation.Transactional;

                    import java.time.LocalDateTime;
                    import java.util.List;
                    import java.util.UUID;

                    @Service
                    @Transactional
                    public class AdminLocationVoitureServiceImpl implements AdminLocationVoitureService {
                        @Autowired
                        private EmailService emailService;
                        @Autowired
                        private LocationVoitureRepository locationRepository;

                        @Autowired
                        private VoitureRepository voitureRepository;

                        @Autowired
                        private UtilisateurInscritRepository utilisateurRepository;
                        private static final Logger log = LoggerFactory.getLogger(AdminLocationVoitureServiceImpl.class);

                        @Override
                        public List<LocationVoitureDTO> getAllLocations() {
                            return locationRepository.findAll().stream().map(this::toDto).toList();
                        }

                        @Override
                        public LocationVoitureDTO getLocationById(UUID locationId) {
                            LocationVoiture location = locationRepository.findById(locationId)
                                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));
                            return toDto(location);
                        }

                        @Override
                        public List<LocationVoitureDTO> getLocationsByEtat(EtatDemande etat) {
                            return locationRepository.findByEtatDemande(etat).stream()
                                    .map(this::toDto)
                                    .toList();
                        }

                        @Override
                        public LocationVoitureDTO validerLocation(UUID locationId) {
                            LocationVoiture location = locationRepository.findById(locationId)
                                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));

                            if (location.getEtatDemande() != EtatDemande.EN_ATTENTE) {
                                throw new RuntimeException("Seules les demandes en attente peuvent être validées");
                            }

                            location.setEtatDemande(EtatDemande.EN_COURS);
                            LocationVoiture saved = locationRepository.save(location);
                            try {
                                emailService.sendValidationEmail(saved.getClientEmail(), saved.getClientNom() + " " + saved.getClientPrenom());
                            } catch (MessagingException e) {
                                log.error("Failed to send validation email for location {}", locationId, e);
                            }
                            return toDto(saved);
                        }

                        @Override
                        public LocationVoitureDTO refuserLocation(UUID locationId) {
                            LocationVoiture location = locationRepository.findById(locationId)
                                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));

                            if (location.getEtatDemande() != EtatDemande.EN_ATTENTE) {
                                throw new RuntimeException("Seules les demandes en attente peuvent être refusées");
                            }

                            location.setEtatDemande(EtatDemande.ANNULEE);
                            LocationVoiture saved = locationRepository.save(location);
                            try {
                                emailService.sendRefusalEmail(saved.getClientEmail(), saved.getClientNom() + " " + saved.getClientPrenom());
                            } catch (MessagingException e) {
                                log.error("Failed to send refusal email for location {}", locationId, e);
                            }
                            return toDto(saved);
                        }

                        @Override
                        public LocationVoitureDTO terminerLocation(UUID locationId) {
                            LocationVoiture location = locationRepository.findById(locationId)
                                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));

                            if (location.getEtatDemande() != EtatDemande.EN_COURS) {
                                throw new RuntimeException("Seules les locations en cours peuvent être terminées");
                            }

                            location.setEtatDemande(EtatDemande.TERMINEE);
                            LocationVoiture saved = locationRepository.save(location);
                            return toDto(saved);
                        }

                        @Override
                        public void supprimerLocation(UUID locationId) {
                            if (!locationRepository.existsById(locationId)) {
                                throw new RuntimeException("Location non trouvée");
                            }
                            locationRepository.deleteById(locationId);
                        }

                        @Override
                        public List<LocationVoitureDTO> getLocationsByVoiture(UUID voitureId) {
                            return locationRepository.findByVoitureId(voitureId).stream()
                                    .map(this::toDto)
                                    .toList();
                        }

                        @Override
                        public List<LocationVoitureDTO> getLocationsByUser(Long userId) {
                            return locationRepository.findByUserId(userId).stream()
                                    .map(this::toDto)
                                    .toList();
                        }

                        // New: create location
                        @Override
                        public LocationVoitureDTO createLocation(LocationVoitureDTO dto) {
                            LocationVoiture entity = dtoToEntity(dto, null);
                            LocationVoiture saved = locationRepository.save(entity);
                            return toDto(saved);
                        }

                        // New: update location
                        @Override
                        public LocationVoitureDTO updateLocation(UUID id, LocationVoitureDTO dto) {
                            LocationVoiture existing = locationRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));
                            LocationVoiture updated = dtoToEntity(dto, existing);
                            LocationVoiture saved = locationRepository.save(updated);
                            return toDto(saved);
                        }

                        private LocationVoiture dtoToEntity(LocationVoitureDTO dto, LocationVoiture existing) {
                            LocationVoiture location = existing != null ? existing : new LocationVoiture();

                            // determine creation date (dto or now)
                            LocalDateTime dateCreation = dto.getDateCreation() != null ? dto.getDateCreation() : LocalDateTime.now();
                            if (existing == null) {
                                location.setDateCreation(dateCreation);
                            }

                            // Voiture is required
                            Voiture voiture = voitureRepository.findById(dto.getVoitureId())
                                    .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));
                            location.setVoiture(voiture);

                            // User optional
                            if (dto.getUserId() != null) {
                                UtilisateurInscrit user = utilisateurRepository.findById(dto.getUserId())
                                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                                location.setUser(user);
                            } else {
                                location.setUser(null);
                            }

                            location.setDateDebut(dto.getDateDebut());
                            location.setDateFin(dto.getDateFin());
                            location.setMontant(dto.getMontant());
                            location.setLieuRecuperation(dto.getLieuRecuperation());
                            location.setLieuRetour(dto.getLieuRetour());
                            location.setClientNom(dto.getClientNom());
                            location.setClientPrenom(dto.getClientPrenom());
                            location.setClientEmail(dto.getClientEmail());
                            location.setClientNumTel(dto.getClientNumTel());

                            // EtatDemande logic on create:
                            if (dto.getEtatDemande() != null) {
                                location.setEtatDemande(dto.getEtatDemande());
                            } else if (existing == null) {
                                // default based on creation date vs start date
                                if (dto.getDateDebut() != null) {
                                    LocalDateTime dateDebut = dto.getDateDebut();
                                    if (dateCreation.isBefore(dateDebut)) {
                                        location.setEtatDemande(EtatDemande.VALIDEE);
                                    } else if (dateCreation.isEqual(dateDebut)) {
                                        location.setEtatDemande(EtatDemande.EN_COURS);
                                    } else {
                                        // fallback if creation is after start - keep as EN_ATTENTE
                                        location.setEtatDemande(EtatDemande.EN_ATTENTE);
                                    }
                                } else {
                                    location.setEtatDemande(EtatDemande.EN_ATTENTE);
                                }
                            }

                            return location;
                        }

                        private LocationVoitureDTO toDto(LocationVoiture location) {
                            LocationVoitureDTO dto = new LocationVoitureDTO();
                            dto.setId(location.getId());
                            dto.setVoitureId(location.getVoiture() != null ? location.getVoiture().getId() : null);
                            dto.setDateDebut(location.getDateDebut());
                            dto.setDateFin(location.getDateFin());
                            dto.setMontant(location.getMontant());
                            dto.setLieuRecuperation(location.getLieuRecuperation());
                            dto.setLieuRetour(location.getLieuRetour());
                            dto.setUserId(location.getUser() != null ? location.getUser().getId() : null);
                            dto.setClientNom(location.getClientNom());
                            dto.setClientPrenom(location.getClientPrenom());
                            dto.setClientEmail(location.getClientEmail());
                            dto.setClientNumTel(location.getClientNumTel());
                            dto.setEtatDemande(location.getEtatDemande());
                            dto.setDateCreation(location.getDateCreation());
                            return dto;
                        }

                    }