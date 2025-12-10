package com.example.demo.Controller.User;

import com.example.demo.Dto.FavorisDTO;
import com.example.demo.Model.Favoris;
import com.example.demo.Service.User.FavorisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user/favoris")
public class FavorisController {

    private final FavorisService service;

    public FavorisController(FavorisService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FavorisDTO> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }


    @PostMapping("/{userId}/activite/{activiteId}")
    public ResponseEntity<Favoris> addActivite(@PathVariable Long userId, @PathVariable UUID activiteId) {
        return ResponseEntity.ok(service.addActivite(userId, activiteId));
    }

    @DeleteMapping("/{userId}/activite/{activiteId}")
    public ResponseEntity<Favoris> removeActivite(@PathVariable Long userId, @PathVariable UUID activiteId) {
        return ResponseEntity.ok(service.removeActivite(userId, activiteId));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clear(@PathVariable Long userId) {
        service.clearFavoris(userId);
        return ResponseEntity.noContent().build();
    }

}