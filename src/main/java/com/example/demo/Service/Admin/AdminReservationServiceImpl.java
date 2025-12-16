package com.example.demo.Service.Admin;

                import com.example.demo.Dto.ReservationDTO;
                import com.example.demo.Model.Activite;
                import com.example.demo.Model.Reservation;
                import com.example.demo.Model.UtilisateurInscrit;
                import com.example.demo.Model.StatutReservation;
                import com.example.demo.Repository.ActiviteRepository;
                import com.example.demo.Repository.ReservationRepository;
                import com.example.demo.Repository.UtilisateurInscritRepository;
                import org.springframework.stereotype.Service;

                import java.time.Duration;
                import java.time.LocalDateTime;
                import java.util.List;
                import java.util.UUID;
                import java.util.stream.Collectors;

                @Service
                public class AdminReservationServiceImpl implements AdminReservationService {
                    private final ReservationRepository reservationRepository;
                    private final UtilisateurInscritRepository utilisateurRepository;
                    private final ActiviteRepository activiteRepository;

                    public AdminReservationServiceImpl(ReservationRepository reservationRepository,
                                                       UtilisateurInscritRepository utilisateurRepository,
                                                       ActiviteRepository activiteRepository) {
                        this.reservationRepository = reservationRepository;
                        this.utilisateurRepository = utilisateurRepository;
                        this.activiteRepository = activiteRepository;
                    }

                    @Override
                    public List<ReservationDTO> findAll() {
                        return reservationRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
                    }

                    @Override
                    public ReservationDTO findById(UUID id) {
                        return reservationRepository.findById(id).map(this::toDto).orElse(null);
                    }

                    @Override
                    public ReservationDTO updateStatus(UUID id, StatutReservation statut) {
                        return reservationRepository.findById(id).map(r -> {
                            // Prevent admin from cancelling within 24 hours (same rule as client)
                            if (statut == StatutReservation.CANCELLED && !canCancel(r)) return null;
                            r.setStatut(statut);
                            return toDto(reservationRepository.save(r));
                        }).orElse(null);
                    }

                    @Override
                    public void delete(UUID id) {
                        reservationRepository.deleteById(id);
                    }

                    @Override
                    public List<ReservationDTO> findByStatut(StatutReservation statut) {
                        return reservationRepository.findByStatut(statut).stream().map(this::toDto).collect(Collectors.toList());
                    }

                    @Override
                    public List<ReservationDTO> findByActiviteId(UUID activiteId) {
                        return reservationRepository.findByActivite_Id(activiteId).stream().map(this::toDto).collect(Collectors.toList());
                    }

                    @Override
                    public ReservationDTO create(ReservationDTO dto) {
                        Reservation entity = dtoToEntity(dto, null);
                        Reservation saved = reservationRepository.save(entity);
                        return toDto(saved);
                    }

                    private boolean canCancel(Reservation r) {
                        LocalDateTime start = r.getDateDebut();
                        if (start == null) return false;
                        LocalDateTime now = LocalDateTime.now();
                        Duration untilStart = Duration.between(now, start);
                        return !untilStart.isNegative() && untilStart.toHours() >= 24;
                    }

                    // dtoToEntity and toDto unchanged...
                    private Reservation dtoToEntity(ReservationDTO dto, Reservation existing) {
                        Reservation r = existing != null ? existing : new Reservation();

                        if (dto.getActiviteId() != null) {
                            Activite activite = activiteRepository.findById(dto.getActiviteId())
                                    .orElseThrow(() -> new RuntimeException("Activité non trouvée"));
                            r.setActivite(activite);
                        } else {
                            r.setActivite(null);
                        }

                        if (dto.getUserId() != null) {
                            UtilisateurInscrit user = utilisateurRepository.findById(dto.getUserId())
                                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                            r.setUser(user);
                            r.setClientNom(null);
                            r.setClientPrenom(null);
                            r.setClientEmail(null);
                            r.setClientNumTel(null);
                            r.setClientPays(null);
                        } else {
                            r.setUser(null);
                            r.setClientNom(dto.getClientNom());
                            r.setClientPrenom(dto.getClientPrenom());
                            r.setClientEmail(dto.getClientEmail());
                            r.setClientNumTel(dto.getClientNumTel());
                            r.setClientPays(dto.getClientPays());
                        }

                        r.setDateDebut(dto.getDateDebut());
                        r.setDateFin(dto.getDateFin());
                        r.setNombreParticipantsAdults(dto.getNombreParticipantsAdults());
                        r.setNombreParticipantsChildren(dto.getNombreParticipantsChildren());
                        r.setMontantTotal(dto.getMontantTotal());
                        r.setDevis(dto.getDevis());
                        r.setLieuRencontre(dto.getLieuRencontre());

                        if (dto.getStatut() != null) {
                            r.setStatut(dto.getStatut());
                        }

                        if (existing == null) {
                            r.setDateCreation(dto.getDateCreation() != null ? dto.getDateCreation() : LocalDateTime.now());
                        }

                        return r;
                    }

                    private ReservationDTO toDto(Reservation r) {
                        ReservationDTO dto = new ReservationDTO();
                        dto.setId(r.getId());
                        dto.setUserId(r.getUser() != null ? r.getUser().getId() : null);
                        dto.setActiviteId(r.getActivite() != null ? r.getActivite().getId() : null);
                        dto.setDateDebut(r.getDateDebut());
                        dto.setDateFin(r.getDateFin());
                        dto.setNombreParticipantsAdults(r.getNombreParticipantsAdults());
                        dto.setNombreParticipantsChildren(r.getNombreParticipantsChildren());
                        dto.setStatut(r.getStatut());
                        dto.setMontantTotal(r.getMontantTotal());
                        dto.setDevis(r.getDevis());
                        dto.setDateCreation(r.getDateCreation());
                        dto.setLieuRencontre(r.getLieuRencontre());
                        dto.setClientNom(r.getClientNom());
                        dto.setClientPrenom(r.getClientPrenom());
                        dto.setClientEmail(r.getClientEmail());
                        dto.setClientNumTel(r.getClientNumTel());
                        dto.setClientPays(r.getClientPays());
                        return dto;
                    }
                }