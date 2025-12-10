package com.example.demo.Service.User;

            import com.example.demo.Dto.ActiviteDTO;
            import com.example.demo.Model.Activite;
            import com.example.demo.Model.CategorieActivite;
            import com.example.demo.Repository.ActiviteRepository;
            import com.example.demo.Service.TranslationService;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.context.i18n.LocaleContextHolder;
            import org.springframework.stereotype.Service;
            import org.springframework.transaction.annotation.Transactional;

            import java.util.Arrays;
            import java.util.List;
            import java.util.UUID;
            import java.util.stream.Collectors;

            @Service
            @Transactional
            public class UserActiviterServiceImpl implements UserActiviteService {

                @Autowired
                private ActiviteRepository repo;
                @Autowired
                private TranslationService translationService;

                @Override
                public List<ActiviteDTO> getActivites() {
                    String locale = LocaleContextHolder.getLocale().getLanguage();
                    return repo.findAll().stream()
                            .map(a -> toDto(a, locale))
                            .collect(Collectors.toList());
                }

                @Override
                public ActiviteDTO getActiviteById(UUID id) {
                    Activite a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Activité non trouvée"));
                    String locale = LocaleContextHolder.getLocale().getLanguage();
                    return toDto(a, locale);
                }

                @Override
                public List<CategorieActivite> getCategories() {
                    return Arrays.asList(CategorieActivite.values());
                }

            @Override
            public List<ActiviteDTO> getActivitesByCategorie(CategorieActivite categorie) {
                String locale = LocaleContextHolder.getLocale().getLanguage();
                return repo.findByCategorie(categorie).stream()
                        .map(a -> toDto(a, locale))
                        .collect(Collectors.toList());
            }

            @Override
            public List<ActiviteDTO> searchActivites(String keyword) {
                if (keyword == null || keyword.trim().isEmpty()) {
                    return getActivites();
                }
                String locale = LocaleContextHolder.getLocale().getLanguage();
                return repo.searchActivites(keyword).stream()
                        .map(a -> toDto(a, locale))
                        .collect(Collectors.toList());
            }

            private ActiviteDTO toDto(Activite a, String locale) {
                    ActiviteDTO dto = new ActiviteDTO();
                    dto.setId(a.getId());
                    dto.setCategorie(a.getCategorie());
                    dto.setPrix(a.getPrix());
                    dto.setTaux(a.getTaux());
                    dto.setDuree(a.getDuree());
                    dto.setImages(mapImages(a.getImages()));
                    dto.setVideo(a.getVideo());
                    dto.setEstActive(a.getEstActive());
                    dto.setHeureFin(a.getHeureFin());
                    dto.setHeureDebut(a.getHeureDebut());
                    dto.setPromoActive(a.getPromoActive());
                    dto.setPromoPercent(a.getPromoPercent());
                    dto.setPromoStartDate(a.getPromoStartDate());
                    dto.setPromoEndDate(a.getPromoEndDate());

                    if (!"fr".equals(locale)) {
                        dto.setTitre(translationService.translateText(a.getTitre(), locale));
                        dto.setDescription(translationService.translateText(a.getDescription(), locale));
                        dto.setLieu(translationService.translateText(a.getLieu(), locale));
                    } else {
                        dto.setTitre(a.getTitre());
                        dto.setDescription(a.getDescription());
                        dto.setLieu(a.getLieu());
                    }
                    return dto;
                }

                private List<String> mapImages(List<String> imgs) {
                    if (imgs == null) return null;
                    return imgs.stream()
                            .map(name -> {
                                if (name == null) return null;
                                String n = name.trim();
                                if (n.startsWith("http") || n.startsWith("/")) return n;
                                return "/activites/images/" + n;
                            })
                            .collect(Collectors.toList());
                }
            }