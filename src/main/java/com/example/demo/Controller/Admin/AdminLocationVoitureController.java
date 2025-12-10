    package com.example.demo.Controller.Admin;

    import com.example.demo.Dto.LocationVoitureDTO;
    import com.example.demo.Model.EtatDemande;
    import com.example.demo.Service.Admin.AdminLocationVoitureService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/admin/locationvoiture")
    public class AdminLocationVoitureController {

        @Autowired
        private AdminLocationVoitureService locationService;

        @GetMapping
        public ResponseEntity<List<LocationVoitureDTO>> getAllLocations() {
            return ResponseEntity.ok(locationService.getAllLocations());
        }

        @GetMapping("/{id}")
        public ResponseEntity<LocationVoitureDTO> getLocationById(@PathVariable UUID id) {
            return ResponseEntity.ok(locationService.getLocationById(id));
        }

        @GetMapping("/etat/{etat}")
        public ResponseEntity<List<LocationVoitureDTO>> getLocationsByEtat(@PathVariable EtatDemande etat) {
            return ResponseEntity.ok(locationService.getLocationsByEtat(etat));
        }

        @PutMapping("/{id}/valider")
        public ResponseEntity<LocationVoitureDTO> validerLocation(@PathVariable UUID id) {
            return ResponseEntity.ok(locationService.validerLocation(id));
        }

        @PutMapping("/{id}/refuser")
        public ResponseEntity<LocationVoitureDTO> refuserLocation(@PathVariable UUID id) {
            return ResponseEntity.ok(locationService.refuserLocation(id));
        }

        @PutMapping("/{id}/terminer")
        public ResponseEntity<LocationVoitureDTO> terminerLocation(@PathVariable UUID id) {
            return ResponseEntity.ok(locationService.terminerLocation(id));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> supprimerLocation(@PathVariable UUID id) {
            locationService.supprimerLocation(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/voiture/{voitureId}")
        public ResponseEntity<List<LocationVoitureDTO>> getLocationsByVoiture(@PathVariable UUID voitureId) {
            return ResponseEntity.ok(locationService.getLocationsByVoiture(voitureId));
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<List<LocationVoitureDTO>> getLocationsByUser(@PathVariable Long userId) {
            return ResponseEntity.ok(locationService.getLocationsByUser(userId));
        }

        @PostMapping
        public ResponseEntity<LocationVoitureDTO> createLocation(@RequestBody LocationVoitureDTO dto) {
            LocationVoitureDTO created = locationService.createLocation(dto);
            return ResponseEntity.ok(created);
        }

        @PutMapping("/{id}")
        public ResponseEntity<LocationVoitureDTO> updateLocation(@PathVariable UUID id, @RequestBody LocationVoitureDTO dto) {
            LocationVoitureDTO updated = locationService.updateLocation(id, dto);
            return ResponseEntity.ok(updated);
        }
    }