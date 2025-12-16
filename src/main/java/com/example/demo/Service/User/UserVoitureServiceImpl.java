package com.example.demo.Service.User;

import com.example.demo.Dto.VoitureDTO;
import com.example.demo.Repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserVoitureServiceImpl implements UserVoitureService {

    @Autowired
    private VoitureRepository repo;

    @Override
    public List<VoitureDTO> getVoitures() {
        return repo.findAll()
                .stream()
                .map(e -> {
                    VoitureDTO d = VoitureDTO.fromEntity(e);
                    d.setImages(mapImages(e.getImages()));
                    return d;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VoitureDTO getVoitureById(UUID id) {
        return repo.findById(id)
                .map(e -> {
                    VoitureDTO d = VoitureDTO.fromEntity(e);
                    d.setImages(mapImages(e.getImages()));
                    return d;
                })
                .orElse(null);
    }

    @Override
    public List<VoitureDTO> searchVoitures(String q) {
        if (q == null || q.trim().isEmpty()) {
            return getVoitures();
        }
        String term = q.trim();
        return repo.searchByKeyword(term)
                .stream()
                .map(e -> {
                    VoitureDTO d = VoitureDTO.fromEntity(e);
                    d.setImages(mapImages(e.getImages()));
                    return d;
                })
                .collect(Collectors.toList());
    }

    private List<String> mapImages(List<String> imgs) {
        if (imgs == null) return null;
        return imgs.stream()
                .map(name -> {
                    if (name == null) return null;
                    String n = name.trim();
                    if (n.startsWith("http") || n.startsWith("/")) return n;
                    // prefix with your public path where files are served
                    return "/uploads/images/" + n;
                })
                .collect(Collectors.toList());
    }
}
