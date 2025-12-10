package com.example.demo.Dto;

import com.example.demo.Model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class VoitureDTO {
    private UUID id;
    private String marque;
    private String modele;
    private Integer annee;
    private BigDecimal prixParJour;
    private BoiteVitesse boite;
    private TypeCarburant carburant;
    private Integer kilometrage;
    private List<String> options;
    private Boolean climatisation;
    private Integer nombrePlaces;
    private Integer nombrePortes;
    private List<String> images;
    private String politiqueCarburant;
    private BigDecimal depotGarantie;
    private Boolean estActive;

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public BigDecimal getPrixParJour() { return prixParJour; }
    public void setPrixParJour(BigDecimal prixParJour) { this.prixParJour = prixParJour; }
    public BoiteVitesse getBoite() { return boite; }
    public void setBoite(BoiteVitesse boite) { this.boite = boite; }
    public TypeCarburant getCarburant() { return carburant; }
    public void setCarburant(TypeCarburant carburant) { this.carburant = carburant; }
    public Integer getKilometrage() { return kilometrage; }
    public void setKilometrage(Integer kilometrage) { this.kilometrage = kilometrage; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public Boolean getClimatisation() { return climatisation; }
    public void setClimatisation(Boolean climatisation) { this.climatisation = climatisation; }
    public Integer getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    public Integer getNombrePortes() { return nombrePortes; }
    public void setNombrePortes(Integer nombrePortes) { this.nombrePortes = nombrePortes; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getPolitiqueCarburant() { return politiqueCarburant; }
    public void setPolitiqueCarburant(String politiqueCarburant) { this.politiqueCarburant = politiqueCarburant; }
    public BigDecimal getDepotGarantie() { return depotGarantie; }
    public void setDepotGarantie(BigDecimal depotGarantie) { this.depotGarantie = depotGarantie; }
    public Boolean getEstActive() { return estActive; }
    public void setEstActive(Boolean estActive) { this.estActive = estActive; }

    // simple mappers
    public static VoitureDTO fromEntity(com.example.demo.Model.Voiture e) {
        if (e == null) return null;
        VoitureDTO d = new VoitureDTO();
        d.setId(e.getId());
        d.setMarque(e.getMarque());
        d.setModele(e.getModele());
        d.setAnnee(e.getAnnee());
        d.setPrixParJour(e.getPrixParJour());
        d.setBoite(e.getBoite());
        d.setCarburant(e.getCarburant());
        d.setKilometrage(e.getKilometrage());
        d.setOptions(e.getOptions());
        d.setClimatisation(e.getClimatisation());
        d.setNombrePlaces(e.getNombrePlaces());
        d.setNombrePortes(e.getNombrePortes());
        d.setImages(e.getImages());
        d.setPolitiqueCarburant(e.getPolitiqueCarburant());
        d.setDepotGarantie(e.getDepotGarantie());
        d.setEstActive(e.getEstActive());
        return d;
    }

    public com.example.demo.Model.Voiture toEntity() {
        com.example.demo.Model.Voiture e = new com.example.demo.Model.Voiture();
        e.setId(this.id);
        e.setMarque(this.marque);
        e.setModele(this.modele);
        e.setAnnee(this.annee);
        e.setPrixParJour(this.prixParJour);
        e.setBoite(this.boite);
        e.setCarburant(this.carburant);
        e.setKilometrage(this.kilometrage);
        e.setOptions(this.options);
        e.setClimatisation(this.climatisation);
        e.setNombrePlaces(this.nombrePlaces);
        e.setNombrePortes(this.nombrePortes);
        e.setImages(this.images);
        e.setPolitiqueCarburant(this.politiqueCarburant);
        e.setDepotGarantie(this.depotGarantie);
        e.setEstActive(this.estActive);
        return e;
    }
}