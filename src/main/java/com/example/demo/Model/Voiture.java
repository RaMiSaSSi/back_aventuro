package com.example.demo.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
public class Voiture {
    @Id
    @GeneratedValue
    private UUID id;

    private String marque;
    private String modele;
    private Integer annee;



    private BigDecimal prixParJour;

    @Enumerated(EnumType.STRING)
    private BoiteVitesse boite;

    @Enumerated(EnumType.STRING)
    private TypeCarburant carburant;

    private Integer kilometrage;

    @ElementCollection
    private List<String> options;

    private Boolean climatisation;
    private Integer nombrePlaces;
    private Integer nombrePortes;

    @ElementCollection
    private List<String> images;

    private String politiqueCarburant;
    private BigDecimal depotGarantie;
    private Boolean estActive = true ;

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
}