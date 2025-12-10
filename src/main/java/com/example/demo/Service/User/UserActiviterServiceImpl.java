// java
                            // File: src/main/java/com/example/demo/Service/User/UserActiviterServiceImpl.java
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

                            import java.math.BigDecimal;
                            import java.util.ArrayList;
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
                                    dto.setImages(mapImages(orEmpty(a.getImages())));
                                    dto.setVideo(a.getVideo());
                                    dto.setEstActive(a.getEstActive());
                                    dto.setHeureFin(a.getHeureFin());
                                    dto.setHeureDebut(a.getHeureDebut());
                                    dto.setPromoActive(a.getPromoActive());
                                    dto.setPromoPercent(a.getPromoPercent());
                                    dto.setPromoStartDate(a.getPromoStartDate());
                                    dto.setPromoEndDate(a.getPromoEndDate());

                                    // New fields - ensure non-null collections and defaults
                                    dto.setInformationsSupplementaires(orEmpty(a.getInformationsSupplementaires()));
                                    dto.setInclus(orEmpty(a.getInclus()));
                                    dto.setNonInclus(orEmpty(a.getNonInclus()));
                                    dto.setConditionsAnnulation(a.getConditionsAnnulation() != null ? a.getConditionsAnnulation() : "");
                                    dto.setFraisService(a.getFraisService() != null ? a.getFraisService() : BigDecimal.ZERO);

                                    if (!"fr".equals(locale)) {
                                        dto.setTitre(safeTranslate(a.getTitre(), locale));
                                        dto.setDescription(safeTranslate(a.getDescription(), locale));
                                        dto.setLieu(safeTranslate(a.getLieu(), locale));
                                    } else {
                                        dto.setTitre(a.getTitre());
                                        dto.setDescription(a.getDescription());
                                        dto.setLieu(a.getLieu());
                                    }
                                    return dto;
                                }

                                private List<String> orEmpty(List<String> list) {
                                    return list == null ? new ArrayList<>() : list;
                                }

                                private String safeTranslate(String original, String locale) {
                                    if (original == null || original.trim().isEmpty()) return original;
                                    try {
                                        String translated = translationService.translateText(original, locale);
                                        if (translated == null || translated.trim().isEmpty()) return original;
                                        return translated;
                                    } catch (Exception ex) {
                                        return original;
                                    }
                                }

                                private List<String> mapImages(List<String> imgs) {
                                    List<String> safe = orEmpty(imgs);
                                    return safe.stream()
                                            .map(name -> {
                                                if (name == null) return null;
                                                String n = name.trim();
                                                if (n.startsWith("http") || n.startsWith("/")) return n;
                                                return "/activites/images/" + n;
                                            })
                                            .collect(Collectors.toList());
                                }
                                @Override
                                public List<ActiviteDTO> filterActivites(String categorie,
                                                                         BigDecimal prixMin,
                                                                         BigDecimal prixMax,
                                                                         Integer dureeMin,
                                                                         Integer dureeMax,
                                                                         String lieu,
                                                                         Boolean promoActive) {
                                    String locale = LocaleContextHolder.getLocale().getLanguage();
                                    return repo.findAll().stream()
                                            .map(a -> toDto(a, locale))
                                            .filter(dto -> {
                                                if (categorie != null && !categorie.trim().isEmpty()) {
                                                    if (dto.getCategorie() == null || !dto.getCategorie().name().equalsIgnoreCase(categorie)) return false;
                                                }
                                                if (prixMin != null) {
                                                    if (dto.getPrix() == null || dto.getPrix().compareTo(prixMin) < 0) return false;
                                                }
                                                if (prixMax != null) {
                                                    if (dto.getPrix() == null || dto.getPrix().compareTo(prixMax) > 0) return false;
                                                }
                                                if (dureeMin != null) {
                                                    if (dto.getDuree() == null || dto.getDuree() < dureeMin) return false;
                                                }
                                                if (dureeMax != null) {
                                                    if (dto.getDuree() == null || dto.getDuree() > dureeMax) return false;
                                                }
                                                if (lieu != null && !lieu.trim().isEmpty()) {
                                                    if (dto.getLieu() == null || !dto.getLieu().toLowerCase().contains(lieu.toLowerCase())) return false;
                                                }
                                                if (promoActive != null) {
                                                    if (dto.getPromoActive() == null || !dto.getPromoActive().equals(promoActive)) return false;
                                                }
                                                return true;
                                            })
                                            .collect(Collectors.toList());
                                }
                            }