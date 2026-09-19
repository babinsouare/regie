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
@Table(name = "campagne_statut")
public class CampagneStatut {

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

    @Column(name = "date_changement")
    @JsonProperty()
    private Date dateChangement;

    @Column(name = "commentaire")
    @JsonProperty()
    private String commentaire;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_campagne")
    private Campagnes campagne;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_statut")
    private Statut statut;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;
}