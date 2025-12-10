package com.example.demo.Controller.User;

import com.example.demo.Dto.ActiviteDTO;
import com.example.demo.Model.CategorieActivite;
import com.example.demo.Service.User.UserActiviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/activites")
public class UserActiviteController {

    @Autowired
    private UserActiviteService service;

    @GetMapping
    public ResponseEntity<List<ActiviteDTO>> list() {
        return ResponseEntity.ok(service.getActivites());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiviteDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getActiviteById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategorieActivite>> categories() {
        return ResponseEntity.ok(service.getCategories());
    }

    @GetMapping("/categories/{categorie}/activites")
    public ResponseEntity<List<ActiviteDTO>> getByCategorie(@PathVariable String categorie) {
        CategorieActivite cat;
        try {
            cat = CategorieActivite.valueOf(categorie.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.getActivitesByCategorie(cat));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActiviteDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchActivites(keyword));
    }
}
