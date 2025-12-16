package com.example.demo.Controller.User;

import com.example.demo.Dto.VoitureDTO;
import com.example.demo.Service.User.UserVoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/voitures")
public class UserVoitureController {

    @Autowired
    private UserVoitureService service;

    @GetMapping
    public ResponseEntity<List<VoitureDTO>> list() {
        return ResponseEntity.ok(service.getVoitures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoitureDTO> get(@PathVariable UUID id) {
        VoitureDTO dto = service.getVoitureById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VoitureDTO>> search(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(service.searchVoitures(q));
    }
}