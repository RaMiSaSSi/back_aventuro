
            package com.example.demo.Service.Admin;

            import com.example.demo.Dto.VoitureDTO;
            import com.example.demo.Model.Voiture;
            import com.example.demo.Repository.VoitureRepository;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.stereotype.Service;
            import org.springframework.transaction.annotation.Transactional;

            import java.util.List;
            import java.util.UUID;
            import java.util.stream.Collectors;

            @Service
            @Transactional
            public class AdminVoitureServiceImpl implements AdminVoitureService {

                @Autowired
                private VoitureRepository repo;

                @Override
                public List<VoitureDTO> getVoitures() {
                    return repo.findAll().stream().map(e -> {
                        VoitureDTO d = VoitureDTO.fromEntity(e);
                        d.setImages(mapImages(e.getImages()));
                        return d;
                    }).collect(Collectors.toList());
                }

                @Override
                public VoitureDTO getVoitureById(UUID id) {
                    return repo.findById(id).map(e -> {
                        VoitureDTO d = VoitureDTO.fromEntity(e);
                        d.setImages(mapImages(e.getImages()));
                        return d;
                    }).orElse(null);
                }

                @Override
                public VoitureDTO createVoiture(VoitureDTO dto) {
                    Voiture e = dto.toEntity();
                    e.setId(null); // ensure new
                    Voiture saved = repo.save(e);
                    VoitureDTO out = VoitureDTO.fromEntity(saved);
                    out.setImages(mapImages(saved.getImages()));
                    return out;
                }

                @Override
                public VoitureDTO updateVoiture(UUID id, VoitureDTO dto) {
                    return repo.findById(id).map(existing -> {
                        // update fields
                        existing.setMarque(dto.getMarque());
                        existing.setModele(dto.getModele());
                        existing.setAnnee(dto.getAnnee());
                        existing.setPrixParJour(dto.getPrixParJour());
                        existing.setBoite(dto.getBoite());
                        existing.setCarburant(dto.getCarburant());
                        existing.setKilometrage(dto.getKilometrage());
                        existing.setOptions(dto.getOptions());
                        existing.setClimatisation(dto.getClimatisation());
                        existing.setNombrePlaces(dto.getNombrePlaces());
                        existing.setNombrePortes(dto.getNombrePortes());
                        existing.setImages(dto.getImages());
                        existing.setPolitiqueCarburant(dto.getPolitiqueCarburant());
                        existing.setDepotGarantie(dto.getDepotGarantie());
                        existing.setEstActive(dto.getEstActive());
                        Voiture saved = repo.save(existing);
                        VoitureDTO out = VoitureDTO.fromEntity(saved);
                        out.setImages(mapImages(saved.getImages()));
                        return out;
                    }).orElse(null);
                }

                @Override
                public void deleteVoiture(UUID id) {
                    repo.deleteById(id);
                }

                private List<String> mapImages(List<String> imgs) {
                    if (imgs == null) return null;
                    return imgs.stream()
                            .map(name -> {
                                if (name == null) return null;
                                String n = name.trim();
                                if (n.startsWith("http") || n.startsWith("/")) return n;
                                return "/uploads/images/" + n;
                            })
                            .collect(Collectors.toList());
                }
            }