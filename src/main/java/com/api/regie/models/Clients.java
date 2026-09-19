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
@Table(name = "clients")
public class Clients {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "denomination")
    @JsonProperty()
    private String denomination;

    @Column(name = "sigle")
    @JsonProperty()
    private String sigle;

    @Column(name = "domaine_activite")
    @JsonProperty()
    private String domaineActivite;

    @Column(name = "nom_responsable")
    @JsonProperty()
    private String nomResponsable;

    @Column(name = "prenom_responsable")
    @JsonProperty()
    private String prenomResponsable;

    @Column(name = "email_responsable")
    @JsonProperty()
    private String emailResponsable;

    @Column(name = "telephone_responsable")
    @JsonProperty()
    private String telephoneResponsable;

    @Column(name = "adresse")
    @JsonProperty()
    private String adresse;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

    /** Langue de contact du client : fr, en ou pt. Détermine la version du message envoyée. */
    @Column(name = "langue")
    @JsonProperty()
    private String langue = "fr";

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_type_client")
    private  TypeClient typeClient;
}
