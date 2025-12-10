package com.example.demo.Controller.User;

import com.example.demo.Dto.AvisDTO;
import com.example.demo.Service.Auth.AuthService;
import com.example.demo.Service.User.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/avis")
public class AvisController {
    @Autowired
    private  AvisService avisService;
    @Autowired
    private  AuthService authService; // helper to get current user from token/session



    @PostMapping
    public ResponseEntity<AvisDTO> createAvis(@RequestBody AvisDTO dto, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        if (userId == null) return ResponseEntity.status(401).build();
        AvisDTO created = avisService.createAvis(dto, userId);
        if (created == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(created);
    }

    @GetMapping("/activite/{id}")
    public ResponseEntity<List<AvisDTO>> getByActivite(@PathVariable UUID id) {
        return ResponseEntity.ok(avisService.getAvisByActivite(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AvisDTO>> getMyAvis(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(avisService.getAvisByUtilisateur(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvisDTO> getById(@PathVariable UUID id) {
        AvisDTO dto = avisService.getAvisById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<List<AvisDTO>> getAll() {
        return ResponseEntity.ok(avisService.getAllAvis());
    }
    @GetMapping("/average")
    public ResponseEntity<Double> getAverageAvis() {
        double avg = avisService.getAverageNote();
        return ResponseEntity.ok(avg);
    }
}
