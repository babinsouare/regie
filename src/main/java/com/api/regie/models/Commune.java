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
@Table(name = "commune")
public class Commune {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "commune")
    @JsonProperty()
    private String commune;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_region")
    private  Region region;

}
