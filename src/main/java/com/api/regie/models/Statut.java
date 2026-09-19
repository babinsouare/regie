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
@Table(name = "statut")
public class Statut {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "code_statut")
    @JsonProperty()
    private String codeStatut;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @Column(name = "ordre")
    @JsonProperty()
    private Integer ordre;

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;
}