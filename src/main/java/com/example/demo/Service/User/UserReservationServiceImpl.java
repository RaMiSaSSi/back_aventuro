package com.example.demo.Service.User;

                    import com.example.demo.Dto.ReservationCreateDTO;
                    import com.example.demo.Dto.ReservationDTO;
                    import com.example.demo.Model.Activite;
                    import com.example.demo.Model.Reservation;
                    import com.example.demo.Model.StatutReservation;
                    import com.example.demo.Model.UtilisateurInscrit;
                    import com.example.demo.Repository.ActiviteRepository;
                    import com.example.demo.Repository.ReservationRepository;
                    import com.example.demo.Repository.UtilisateurInscritRepository;
                    import org.springframework.stereotype.Service;
                    import java.math.BigDecimal;
                    import java.time.Duration;
                    import java.time.LocalDateTime;
                    import java.util.List;
                    import java.util.UUID;
                    import java.util.stream.Collectors;

                    @Service
                    public class UserReservationServiceImpl implements UserReservationService {
                        private final ReservationRepository reservationRepository;
                        private final UtilisateurInscritRepository utilisateurRepo;
                        private final ActiviteRepository activiteRepo;

                        public UserReservationServiceImpl(ReservationRepository reservationRepository,
                                                          UtilisateurInscritRepository utilisateurRepo,
                                                          ActiviteRepository activiteRepo) {
                            this.reservationRepository = reservationRepository;
                            this.utilisateurRepo = utilisateurRepo;
                            this.activiteRepo = activiteRepo;
                        }

                        @Override
                        public ReservationDTO createReservation(ReservationCreateDTO dto) {
                            Activite activite = activiteRepo.findById(dto.getActiviteId()).orElse(null);
                            if (activite == null) return null;

                            UtilisateurInscrit user = null;
                            if (dto.getUserId() != null) {
                                user = utilisateurRepo.findById(dto.getUserId()).orElse(null);
                                if (user == null) return null;
                            }

                            Reservation r = new Reservation();
                            if (user != null) {
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

                            r.setActivite(activite);
                            r.setDateDebut(dto.getDateDebut());
                            r.setDateFin(dto.getDateFin());
                            r.setNombreParticipantsAdults(dto.getNombreParticipantsAdults());
                            r.setNombreParticipantsChildren(dto.getNombreParticipantsChildren());
                            r.setDateCreation(LocalDateTime.now());
                            r.setStatut(StatutReservation.PENDING);
                            r.setLieuRencontre(dto.getLieuRencontre());
                            r.setPayment(dto.getPayment());
                            r.setPayé(dto.isPayé());
                            r.setDevis(dto.getDevis());

                            BigDecimal total = BigDecimal.ZERO;
                            int adults = r.getNombreParticipantsAdults() == null ? 0 : r.getNombreParticipantsAdults();
                            int children = r.getNombreParticipantsChildren() == null ? 0 : r.getNombreParticipantsChildren();
                            if (activite.getPrix() != null) total = total.add(activite.getPrix().multiply(BigDecimal.valueOf(adults)));
                            r.setMontantTotal(total);

                            Reservation saved = reservationRepository.save(r);
                            return toDto(saved);
                        }

                        @Override
                        public List<ReservationDTO> findByUserId(Long userId) {
                            return reservationRepository.findByUser_Id(userId).stream().map(this::toDto).collect(Collectors.toList());
                        }

                        @Override
                        public ReservationDTO findByIdAndUserId(UUID id, Long userId) {
                            return reservationRepository.findById(id)
                                    .filter(r -> r.getUser() != null && r.getUser().getId() == userId)
                                    .map(this::toDto).orElse(null);
                        }

                        @Override
                        public ReservationDTO cancelReservation(UUID id, Long userId) {
                            return reservationRepository.findById(id)
                                    .filter(r -> r.getUser() != null && r.getUser().getId() == userId)
                                    .map(r -> {
                                        if (!canCancel(r)) return null;
                                        r.setStatut(StatutReservation.CANCELLED);
                                        return toDto(reservationRepository.save(r));
                                    }).orElse(null);
                        }

                        private boolean canCancel(Reservation r) {
                            LocalDateTime start = r.getDateDebut();
                            if (start == null) return false;
                            LocalDateTime now = LocalDateTime.now();
                            Duration untilStart = Duration.between(now, start);
                            // allow cancellation only if at least 24 hours remain
                            return !untilStart.isNegative() && untilStart.toHours() >= 24;
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
                            dto.setDateCreation(r.getDateCreation());
                            dto.setLieuRencontre(r.getLieuRencontre());
                            dto.setPayment(r.getPayment());
                            dto.setPayé(r.isPayé());
                            dto.setClientNom(r.getClientNom());
                            dto.setClientPrenom(r.getClientPrenom());
                            dto.setClientEmail(r.getClientEmail());
                            dto.setClientNumTel(r.getClientNumTel());
                            dto.setClientPays(r.getClientPays());
                            dto.setDevis(r.getDevis());
                            return dto;
                        }
                    }