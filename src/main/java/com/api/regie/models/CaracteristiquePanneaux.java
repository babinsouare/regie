package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "caracteristique_panneaux")
public class CaracteristiquePanneaux {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_last_update")
    private Date dtLastUpdate;

    @Column(name = "type")
    @JsonProperty()
    private String type;

    @Column(name = "caracteristique")
    @JsonProperty()
    private String caracteristique;

    @Column(name = "dimenssion")
    @JsonProperty()
    private String dimenssion;

    @Column(name = "longeur")
    @JsonProperty()
    private Double longeur;

    @Column(name = "largeur")
    @JsonProperty()
    private Double largeur;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_categorie_panneaux")
    private  CategoriePanneaux categoriePanneaux;

}
