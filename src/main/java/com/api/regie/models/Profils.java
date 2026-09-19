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
@Table(name = "profils")
public class Profils {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "profil")
    @JsonProperty()
    private String profil;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

}

