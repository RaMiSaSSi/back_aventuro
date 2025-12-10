package com.example.demo.Controller.User;

                import com.example.demo.Dto.DateIndisponibleDTO;
                import com.example.demo.Dto.LocationVoitureDTO;
                import com.example.demo.Model.LocationVoiture;
                import com.example.demo.Service.User.UserServiceLocationVoiture;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.format.annotation.DateTimeFormat;
                import org.springframework.http.HttpStatus;
                import org.springframework.http.ResponseEntity;
                import org.springframework.security.core.Authentication;
                import org.springframework.web.bind.annotation.*;

                import java.time.LocalDateTime;
                import java.util.List;
                import java.util.UUID;

                @RestController
                @RequestMapping("/api/user/locationvoiture")
                public class UserLocationVoitureController {

                    @Autowired
                    private UserServiceLocationVoiture locationService;

                    @PostMapping
                    public ResponseEntity<LocationVoiture> creerLocation(@RequestBody LocationVoitureDTO dto) {
                        LocationVoiture location = locationService.creerLocation(dto);
                        return ResponseEntity.status(HttpStatus.CREATED).body(location);
                    }
                    @PutMapping("/{id}/annuler")
                    public ResponseEntity<Void> annulerLocation(
                            @PathVariable UUID id,
                            Authentication authentication) {
                        Long userId = extractUserIdFromAuthentication(authentication);
                        locationService.annulerLocation(id, userId);
                        return ResponseEntity.noContent().build();
                    }

                    @GetMapping("/mes-locations")
                    public ResponseEntity<List<LocationVoiture>> getMesLocations(Authentication authentication) {
                        Long userId = extractUserIdFromAuthentication(authentication);
                        List<LocationVoiture> locations = locationService.getMesLocations(userId);
                        return ResponseEntity.ok(locations);
                    }

                    private Long extractUserIdFromAuthentication(Authentication authentication) {
                        return Long.parseLong(authentication.getName());
                    }
                    @GetMapping("/disponibilite")
                    public ResponseEntity<Boolean> verifierDisponibilite(
                            @RequestParam UUID voitureId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
                        boolean disponible = locationService.isVoitureDisponible(voitureId, dateDebut, dateFin);
                        return ResponseEntity.ok(disponible);
                    }
                    @GetMapping("/voiture/{voitureId}/unavailable-dates")
                    public ResponseEntity<List<DateIndisponibleDTO>> getUnavailableDates(@PathVariable UUID voitureId) {
                        List<DateIndisponibleDTO> dates = locationService.getUnavailableDates(voitureId);
                        return ResponseEntity.ok(dates);
                    }

                }