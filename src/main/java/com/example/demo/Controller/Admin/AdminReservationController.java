// java
    package com.example.demo.Controller.Admin;

    import com.example.demo.Dto.ReservationDTO;
    import com.example.demo.Model.StatutReservation;
    import com.example.demo.Service.Admin.AdminReservationService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/admin/reservations")
    public class AdminReservationController {
        @Autowired
        private AdminReservationService adminReservationService;

        @GetMapping
        public List<ReservationDTO> listAll() {
            return adminReservationService.findAll();
        }

        @GetMapping("/{id}")
        public ResponseEntity<ReservationDTO> get(@PathVariable UUID id) {
            ReservationDTO r = adminReservationService.findById(id);
            if (r == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(r);
        }

        @PostMapping
        public ResponseEntity<ReservationDTO> create(@RequestBody ReservationDTO dto) {
            ReservationDTO created = adminReservationService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @PutMapping("/{id}/status")
        public ResponseEntity<ReservationDTO> updateStatus(@PathVariable UUID id, @RequestBody StatutReservation statut) {
            ReservationDTO updated = adminReservationService.updateStatus(id, statut);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable UUID id) {
            adminReservationService.delete(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/statut/{statut}")
        public List<ReservationDTO> byStatut(@PathVariable StatutReservation statut) {
            return adminReservationService.findByStatut(statut);
        }

        @GetMapping("/pending")
        public List<ReservationDTO> pending() {
            return adminReservationService.findByStatut(StatutReservation.PENDING);
        }

        @GetMapping("/confirmed")
        public List<ReservationDTO> confirmed() {
            return adminReservationService.findByStatut(StatutReservation.CONFIRMED);
        }

        @GetMapping("/completed")
        public List<ReservationDTO> completed() {
            return adminReservationService.findByStatut(StatutReservation.COMPLETED);
        }

        @GetMapping("/activite/{activiteId}")
        public List<ReservationDTO> byActivite(@PathVariable UUID activiteId) {
            return adminReservationService.findByActiviteId(activiteId);
        }
    }