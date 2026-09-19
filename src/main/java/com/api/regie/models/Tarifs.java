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
@Table(name = "tarifs")
public class Tarifs {
    /*
    On pourrait typer les tarifs pour avoir par jour, semaine, mois, trimestre, semestre et annuel.
     */
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

    @Column(name = "price_day")
    @JsonProperty()
    private Double priceDay;

    @Column(name = "price_week")
    @JsonProperty()
    private Double priceWeek;

    @Column(name = "price_month")
    @JsonProperty()
    private Double priceMonth;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_caracteristique")
    private  CaracteristiquePanneaux caracteristiquePanneaux;

}
