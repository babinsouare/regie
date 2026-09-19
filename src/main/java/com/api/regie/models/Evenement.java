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
@Table(name = "evenement")
public class Evenement {

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

    @Column(name = "date_evenement")
    @JsonProperty()
    private Date dateEvenement;

    @Column(name = "commentaire")
    @JsonProperty()
    private String commentaire;

    @Column(name = "cout_total")
    @JsonProperty()
    private Double coutTotal;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_type_evenement")
    private  TypeEvenement typeEvenement;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private Users user;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_panneau")
    private Panneaux panneau;

}
