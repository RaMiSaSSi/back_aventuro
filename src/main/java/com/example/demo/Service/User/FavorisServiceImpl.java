package com.example.demo.Service.User;

            import com.example.demo.Dto.FavorisDTO;
            import com.example.demo.Model.Activite;
            import com.example.demo.Model.Favoris;
            import com.example.demo.Model.UtilisateurInscrit;
            import com.example.demo.Repository.ActiviteRepository;
            import com.example.demo.Repository.FavorisRepository;
            import com.example.demo.Repository.UtilisateurInscritRepository;
            import org.springframework.stereotype.Service;
            import org.springframework.transaction.annotation.Transactional;

            import java.util.UUID;

            @Service
            @Transactional
            public class FavorisServiceImpl implements FavorisService {

                private final FavorisRepository favorisRepo;
                private final UtilisateurInscritRepository userRepo;
                private final ActiviteRepository activiteRepo;

                public FavorisServiceImpl(FavorisRepository favorisRepo,
                                          UtilisateurInscritRepository userRepo,
                                          ActiviteRepository activiteRepo) {
                    this.favorisRepo = favorisRepo;
                    this.userRepo = userRepo;
                    this.activiteRepo = activiteRepo;
                }

                @Override
                public FavorisDTO getByUser(Long userId) {
                    Favoris favoris = getFavorisEntity(userId);
                    return toDTO(favoris);
                }

                // Méthode privée pour obtenir l'entité Favoris
                private Favoris getFavorisEntity(Long userId) {
                    return favorisRepo.findByUserId(userId)
                            .orElseGet(() -> createForUser(userId));
                }

                private Favoris createForUser(Long userId) {
                    UtilisateurInscrit user = userRepo.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                    Favoris f = new Favoris();
                    f.setUser(user);
                    return favorisRepo.save(f);
                }

                @Override
                public Favoris addActivite(Long userId, UUID activiteId) {
                    Favoris f = getFavorisEntity(userId);
                    Activite a = activiteRepo.findById(activiteId)
                            .orElseThrow(() -> new IllegalArgumentException("Activite not found: " + activiteId));
                    f.getActivites().add(a);
                    return favorisRepo.save(f);
                }

                @Override
                public Favoris removeActivite(Long userId, UUID activiteId) {
                    Favoris f = getFavorisEntity(userId);
                    activiteRepo.findById(activiteId).ifPresent(a -> {
                        f.getActivites().remove(a);
                        favorisRepo.save(f);
                    });
                    return f;
                }

                @Override
                public void clearFavoris(Long userId) {
                    Favoris f = getFavorisEntity(userId);
                    f.getActivites().clear();
                    favorisRepo.save(f);
                }

                private FavorisDTO toDTO(Favoris favoris) {
                    FavorisDTO dto = new FavorisDTO();
                    dto.setUserId(favoris.getUser().getId());
                    dto.setActivites(favoris.getActivites().stream()
                            .map(Activite::getId)
                            .toList());
                    return dto;
                }
            }