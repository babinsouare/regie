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
@Table(name = "type_client")
public class TypeClient {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "type_client")
    @JsonProperty()
    private String typeClient;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

}

