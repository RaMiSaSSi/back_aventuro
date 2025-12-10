// java
    // File: src/main/java/com/example/demo/Service/Admin/ActiviteServiceImpl.java
    package com.example.demo.Service.Admin;

    import com.example.demo.Dto.ActiviteDTO;
    import com.example.demo.Model.Activite;
    import com.example.demo.Repository.ActiviteRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;
    import java.util.stream.Collectors;

    @Service
    @Transactional
    public class ActiviteServiceImpl implements ActiviteService {

        @Autowired
        ActiviteRepository repo;

        @Override
        public List<ActiviteDTO> findAll() {
            return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
        }

        @Override
        public ActiviteDTO findById(UUID id) {
            Activite a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Activité non trouvée"));
            return toDto(a);
        }

        @Override
        public ActiviteDTO create(ActiviteDTO dto) {
            Activite a = new Activite();
            applyDtoToEntity(dto, a);
            Activite saved = repo.save(a);
            return toDto(saved);
        }

        @Override
        public ActiviteDTO update(UUID id, ActiviteDTO dto) {
            Activite a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Activité non trouvée"));
            applyDtoToEntity(dto, a);
            Activite saved = repo.save(a);
            return toDto(saved);
        }

        @Override
        public void delete(UUID id) {
            if (!repo.existsById(id)) throw new IllegalArgumentException("Activité non trouvée");
            repo.deleteById(id);
        }

        private ActiviteDTO toDto(Activite a) {
            ActiviteDTO dto = new ActiviteDTO();
            dto.setId(a.getId());
            dto.setTitre(a.getTitre());
            dto.setDescription(a.getDescription());
            dto.setCategorie(a.getCategorie());
            dto.setPrix(a.getPrix());
            dto.setTaux(a.getTaux());
            dto.setDuree(a.getDuree());
            dto.setLieu(a.getLieu());
            dto.setImages(mapImages(orEmpty(a.getImages())));
            dto.setVideo(a.getVideo());
            dto.setEstActive(a.getEstActive());
            dto.setHeureDebut(a.getHeureDebut());
            dto.setHeureFin(a.getHeureFin());

            // Map promotion fields
            dto.setPromoActive(a.getPromoActive());
            dto.setPromoPercent(a.getPromoPercent());
            dto.setPromoStartDate(a.getPromoStartDate());
            dto.setPromoEndDate(a.getPromoEndDate());

            // New fields - ensure non-null lists and defaults
            dto.setInformationsSupplementaires(orEmpty(a.getInformationsSupplementaires()));
            dto.setInclus(orEmpty(a.getInclus()));
            dto.setNonInclus(orEmpty(a.getNonInclus()));
            dto.setConditionsAnnulation(a.getConditionsAnnulation() != null ? a.getConditionsAnnulation() : "");
            dto.setFraisService(a.getFraisService() != null ? a.getFraisService() : BigDecimal.ZERO);

            return dto;
        }

        private void applyDtoToEntity(ActiviteDTO dto, Activite a) {
            if (dto.getTitre() != null) a.setTitre(dto.getTitre());
            if (dto.getDescription() != null) a.setDescription(dto.getDescription());
            if (dto.getCategorie() != null) a.setCategorie(dto.getCategorie());
            if (dto.getPrix() != null) a.setPrix(dto.getPrix());
            if (dto.getTaux() != null) a.setTaux(dto.getTaux());
            if (dto.getDuree() != null) a.setDuree(dto.getDuree());
            if (dto.getLieu() != null) a.setLieu(dto.getLieu());
            if (dto.getImages() != null) a.setImages(dto.getImages());
            if (dto.getVideo() != null) a.setVideo(dto.getVideo());
            if (dto.getEstActive() != null) a.setEstActive(dto.getEstActive());
            if (dto.getHeureDebut() != null) a.setHeureDebut(dto.getHeureDebut());
            if (dto.getHeureFin() != null) a.setHeureFin(dto.getHeureFin());

            // Apply promotion fields
            if (dto.getPromoActive() != null) a.setPromoActive(dto.getPromoActive());
            if (dto.getPromoPercent() != null) a.setPromoPercent(dto.getPromoPercent());
            if (dto.getPromoStartDate() != null) a.setPromoStartDate(dto.getPromoStartDate());
            if (dto.getPromoEndDate() != null) a.setPromoEndDate(dto.getPromoEndDate());

            // Apply new fields
            if (dto.getInformationsSupplementaires() != null) a.setInformationsSupplementaires(dto.getInformationsSupplementaires());
            if (dto.getInclus() != null) a.setInclus(dto.getInclus());
            if (dto.getNonInclus() != null) a.setNonInclus(dto.getNonInclus());
            if (dto.getConditionsAnnulation() != null) a.setConditionsAnnulation(dto.getConditionsAnnulation());
            if (dto.getFraisService() != null) a.setFraisService(dto.getFraisService());
        }

        private List<String> orEmpty(List<String> list) {
            return list == null ? new ArrayList<>() : list;
        }

        private List<String> mapImages(List<String> imgs) {
            List<String> safe = orEmpty(imgs);
            return safe.stream()
                    .map(name -> {
                        if (name == null) return null;
                        String n = name.trim();
                        if (n.startsWith("http") || n.startsWith("/")) return n;
                        return "/uploads/images/" + n;
                    })
                    .collect(Collectors.toList());
        }
    }