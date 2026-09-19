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
@Table(name = "panneaux_campagne")
public class PanneauxCampagne {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_campagne")
    private Campagnes campagne;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_panneaux")
    private Panneaux panneaux;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

}
