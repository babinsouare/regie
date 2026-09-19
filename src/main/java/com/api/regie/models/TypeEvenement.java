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
@Table(name = "type_evenement")
public class TypeEvenement {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "type")
    @JsonProperty()
    private String type;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @Column(name = "bt_emabled")
    @JsonProperty()
    private Boolean btEmabled = true;


}
