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
@Table(name = "campagnes")
public class Campagnes {

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_debut")
    private Date dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_fin")
    private Date dateFin;

    @Column(name = "nom_campagne")
    @JsonProperty()
    private String nomCampagne;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @Column(name = "cycle") //day; week; month;
    @JsonProperty()
    private String cycle;

    @Column(name = "nombre") //nombre de jour/semaine/mois de la campagne;
    @JsonProperty()
    private Integer nombre;

    @Column(name = "statut")
    @JsonProperty()
    private String statut;

    @Column(name = "statut_paiement")
    @JsonProperty()
    private String statutPaiement;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_client")
    private Clients client;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;

}
