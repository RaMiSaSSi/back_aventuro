
package com.example.demo.Service.User;

import com.example.demo.Dto.FavorisDTO;
import com.example.demo.Model.Favoris;

import java.util.UUID;

public interface FavorisService {
    FavorisDTO getByUser(Long userId);
    Favoris addActivite(Long userId, UUID activiteId);
    Favoris removeActivite(Long userId, UUID activiteId);
    void clearFavoris(Long userId);

}