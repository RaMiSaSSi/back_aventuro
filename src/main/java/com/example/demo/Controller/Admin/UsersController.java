package com.example.demo.Controller.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;
import com.example.demo.Service.Admin.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/admin/users")
public class UsersController {

    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurInscritDTO>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurInscritDTO> get(@PathVariable long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<UtilisateurInscritDTO> create(@RequestBody UtilisateurInscritDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurInscritDTO> update(@PathVariable long id, @RequestBody UtilisateurInscritDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}