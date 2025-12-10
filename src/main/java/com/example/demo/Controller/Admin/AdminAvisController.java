package com.example.demo.Controller.Admin;

import com.example.demo.Dto.AvisDTO;
import com.example.demo.Service.Admin.AdminAvisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/avis")
public class AdminAvisController {

    private final AdminAvisService adminAvisService;

    public AdminAvisController(AdminAvisService adminAvisService) {
        this.adminAvisService = adminAvisService;
    }

    @GetMapping
    public ResponseEntity<List<AvisDTO>> getAll() {
        return ResponseEntity.ok(adminAvisService.getAllAvis());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminAvisService.deleteAvis(id);
        return ResponseEntity.noContent().build();
    }
}
