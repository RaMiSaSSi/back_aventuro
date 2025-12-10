package com.example.demo.Dto;

import java.time.LocalDateTime;

public class DateIndisponibleDTO {
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    public DateIndisponibleDTO(LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }
}