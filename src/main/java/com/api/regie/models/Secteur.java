package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "secteur")
public class Secteur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "secteur")
    @JsonProperty()
    private String secteur;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_quartier")
    private Quartier quartier;

}