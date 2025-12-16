package com.example.demo.Controller.Admin;

import com.example.demo.Dto.UtilisateurInscritDTO;
import com.example.demo.Service.Admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMINISTRATEUR')")
@RestController
@RequestMapping("/admin/admins")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurInscritDTO>> list() {
        return ResponseEntity.ok(service.findAllAdmins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurInscritDTO> get(@PathVariable long id) {
        return ResponseEntity.ok(service.findAdminById(id));
    }

    @PostMapping
    public ResponseEntity<UtilisateurInscritDTO> create(@RequestBody UtilisateurInscritDTO dto) {
        return ResponseEntity.ok(service.createAdmin(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurInscritDTO> update(@PathVariable long id, @RequestBody UtilisateurInscritDTO dto) {
        return ResponseEntity.ok(service.updateAdmin(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}