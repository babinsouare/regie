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
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column(name = "numero_devis", unique = true)
    @JsonProperty()
    private String numeroDevis;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_emission")
    private Date dateEmission;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_validite")
    private Date dateValidite;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_debut_prevu")
    private Date dateDebutPrevu;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_fin_prevu")
    private Date dateFinPrevu;

    @Column(name = "montant_brute")
    @JsonProperty()
    private Double montantBrute;

    @Column(name = "montant_remise")
    @JsonProperty()
    private Double montantRemise;

    @Column(name = "montant_net")
    @JsonProperty()
    private Double montantNet;

    @Column(name = "observations", length = 1000)
    @JsonProperty()
    private String observations;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_client")
    private Clients client;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_remise")
    private Remise remise;

    @OneToOne
    @JsonProperty()
    @JoinColumn(name = "id_campagne")
    private Campagnes campagne;

}
