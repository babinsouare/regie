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
@Table(name = "paiements")
public class Paiements {

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

    @Column(name = "montant")
    @JsonProperty()
    private Double montant;

    @Column(name = "reference")
    @JsonProperty()
    private String reference;

    @Column(name = "msisdn_client")
    @JsonProperty()
    private String msisdnClient;

    @Column(name = "reference_externe")
    @JsonProperty()
    private String referenceExterne;

    @Column(name = "statut")
    @JsonProperty()
    private String statut;

    @Column(name = "message")
    @JsonProperty()
    private String message;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_mode_paiement")
    private ModePaiement modePaiement;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_facture")
    private Facture facture;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;

}
