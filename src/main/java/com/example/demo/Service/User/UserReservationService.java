// java
package com.example.demo.Service.User;

import com.example.demo.Dto.ReservationCreateDTO;
import com.example.demo.Dto.ReservationDTO;

import java.util.List;
import java.util.UUID;

public interface UserReservationService {
    ReservationDTO createReservation(ReservationCreateDTO reservationCreateDTO);
    List<ReservationDTO> findByUserId(Long userId);
    ReservationDTO findByIdAndUserId(UUID id, Long userId);
    ReservationDTO cancelReservation(UUID id, Long userId);

}