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
@Table(name = "factures")
public class Facture {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_echeance")
    private Date dateEcheance;

    @Column(name = "montant_brute")
    @JsonProperty()
    private Double montantBrute;

    @Column(name = "montant_remise")
    @JsonProperty()
    private Double montantRemise;

    @Column(name = "montant_net")
    @JsonProperty()
    private Double montantNet;

    @Column(name = "montant_paye")
    @JsonProperty()
    private Double montantPaye;

    @Column(name = "montant_reste_a_paye")
    @JsonProperty()
    private Double montantResteAPaye;

    @Column(name = "reference")
    @JsonProperty()
    private String reference;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_remise")
    private Remise remise;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_campagne")
    private Campagnes campagne;
}
