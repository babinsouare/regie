package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "panneaux")
public class Panneaux {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_last_update")
    private Date dtLastUpdate;

    @Column(name = "reference")
    @JsonProperty()
    private String reference;

    @Column(name = "face")
    @JsonProperty()
    private String face;

    @Column(name = "latitude")
    @JsonProperty()
    private Double latitude;

    @Column(name = "longitude")
    @JsonProperty()
    private Double longitude;

    @Column(name = "bt_valide")
    @JsonProperty()
    private Boolean btValide = true;

    @Column(name = "bt_available")
    @JsonProperty()
    private Boolean btAvailable = true;

    @Column(name = "has_special_price")
    @JsonProperty()
    private Boolean hasSpecialPrice = false;

    @Column(name = "nombre_face")
    @JsonProperty()
    private Integer nombreFace = 0;

    @Column(name = "price_day")
    @JsonProperty()
    private Double priceDay;

    @Column(name = "price_week")
    @JsonProperty()
    private Double priceWeek;

    @Column(name = "price_month")
    @JsonProperty()
    private Double priceMonth;

    @Column(name = "uuid")
    @JsonProperty()
    private UUID idUser;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_caracteristique")
    private  CaracteristiquePanneaux caracteristiquePanneaux;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_secteur")
    private  Secteur secteur;

    public Panneaux(Panneaux source) {
        this.reference = source.reference;
        this.face = source.face;
        this.latitude = source.latitude;
        this.longitude = source.longitude;
        this.btValide = source.btValide;
        this.btAvailable = source.btAvailable;
        this.hasSpecialPrice = source.hasSpecialPrice;
        this.nombreFace = source.nombreFace;
        this.priceDay = source.priceDay;
        this.priceWeek = source.priceWeek;
        this.priceMonth = source.priceMonth;
        this.idUser = source.idUser;
        this.caracteristiquePanneaux = source.caracteristiquePanneaux;
        this.secteur = source.secteur;
    }

}
