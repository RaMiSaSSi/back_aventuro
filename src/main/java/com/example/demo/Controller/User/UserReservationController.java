package com.example.demo.Controller.User;

import com.example.demo.Dto.*;
import com.example.demo.Model.Payment;
import com.example.demo.Service.Auth.AuthService;
import com.example.demo.Service.User.UserReservationService;
import com.example.demo.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/reservations")
public class UserReservationController {

    @Autowired
    private UserReservationService userReservationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ReservationDTO> create(@RequestBody ReservationCreateDTO reservationCreateDTO) {
        ReservationDTO saved = userReservationService.createReservation(reservationCreateDTO);
        if (saved == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationDTO>> myReservations(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        UtilisateurInscritDTO userDto;
        try {
            userDto = authService.getUserInfo(token);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ReservationDTO> reservations = userReservationService.findByUserId(userDto.getId());
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("{id}/cancel")
    public ResponseEntity<ReservationDTO> cancel(@PathVariable UUID id, @RequestParam Long userId) {
        ReservationDTO r = userReservationService.cancelReservation(id, userId);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentInitResponse> initiatePayment(
            @RequestBody PaymentInitRequest request,
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        UtilisateurInscritDTO userDto = authService.getUserInfo(token);

        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Payment payment = paymentService.createPayment(
                request.getAmount(),
                userDto.getId(),
                request.getReservationId(),
                null
        );

        PaymentInitResponse response = new PaymentInitResponse();
        response.setPaymentId(payment.getId().toString());
        response.setRedirectUrl(payment.getFlouciPaymentId());
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/verify/{paymentId}")
    public ResponseEntity<Payment> verifyPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.verifyPayment(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}