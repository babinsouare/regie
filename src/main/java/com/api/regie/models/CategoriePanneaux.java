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
@Table(name = "categorie_panneaux")
public class CategoriePanneaux {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "categorie")
    @JsonProperty()
    private String categorie;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;
}
