package com.example.demo.Controller.Admin;

import com.example.demo.Dto.ActiviteDTO;
import com.example.demo.Service.Admin.ActiviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/activites")
public class ActiviteController {

    @Autowired
    ActiviteService service;
    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;
    @GetMapping
    public ResponseEntity<List<ActiviteDTO>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiviteDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Accept multipart/form-data: part "activite" (JSON) and optional files "images"
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ActiviteDTO> create(
            @RequestPart("activite") ActiviteDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        try {
            if (images != null && !images.isEmpty()) {
                List<String> saved = new ArrayList<>();
                for (MultipartFile f : images) {
                    if (f != null && !f.isEmpty()) {
                        String path = saveFile(f, "images");
                        saved.add(path);
                    }
                }
                dto.setImages(saved);
            }

            // save single video file if provided
            if (video != null && !video.isEmpty()) {
                String videoPath = saveFile(video, "videos");
                dto.setVideo(videoPath);
            }

            return ResponseEntity.ok(service.create(dto));
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Accept multipart/form-data for update as well
    @PutMapping(value = "/{id}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ActiviteDTO> update(
            @PathVariable UUID id,
            @RequestPart("activite") ActiviteDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        try {
            if (images != null && !images.isEmpty()) {
                List<String> saved = new ArrayList<>();
                for (MultipartFile f : images) {
                    if (f != null && !f.isEmpty()) {
                        String path = saveFile(f, "images");
                        saved.add(path);
                    }
                }
                dto.setImages(saved);
            }

            // save/replace single video file if provided
            if (video != null && !video.isEmpty()) {
                String videoPath = saveFile(video, "videos");
                dto.setVideo(videoPath);
            }

            return ResponseEntity.ok(service.update(id, dto));
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    private String saveFile(MultipartFile file, String subdir) throws IOException {
        Path base = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
        Path uploadDir = (subdir == null || subdir.isBlank()) ? base : base.resolve(subdir).normalize();

        Files.createDirectories(uploadDir);

        String original = file.getOriginalFilename() == null ? "file" : Path.of(file.getOriginalFilename()).getFileName().toString();
        String safe = original.replaceAll("[^A-Za-z0-9._-]", "_");
        String filename = System.currentTimeMillis() + "_" + safe;

        Path target = uploadDir.resolve(filename).normalize();
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + (subdir == null || subdir.isBlank() ? "" : subdir + "/") + filename;
    }
}
