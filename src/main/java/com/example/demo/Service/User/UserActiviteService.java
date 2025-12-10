package com.example.demo.Service.User;

import com.example.demo.Dto.ActiviteDTO;
import com.example.demo.Model.CategorieActivite;

import java.util.List;
import java.util.UUID;

public interface UserActiviteService {
    List<ActiviteDTO> getActivites();
    ActiviteDTO getActiviteById(UUID id);
    List<CategorieActivite> getCategories();
    List<ActiviteDTO> getActivitesByCategorie(CategorieActivite categorie);
    List<ActiviteDTO> searchActivites(String keyword);
}
