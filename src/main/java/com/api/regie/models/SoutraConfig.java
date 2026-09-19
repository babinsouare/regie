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
@Table(name = "soutra_config")
public class SoutraConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "environnement")
    @JsonProperty()
    private String environnement;

    @Column(name = "base_url")
    @JsonProperty()
    private String baseUrl;

    @Column(name = "redirect_url")
    @JsonProperty()
    private String redirectUrl;

    @Column(name = "msisdn_marchand")
    @JsonProperty()
    private String msisdnMarchand;

    @Column(name = "client_id")
    @JsonProperty()
    private String clientId;

    @Column(name = "client_secret")
    @JsonProperty()
    private String clientSecret;

    @Column(name = "api_key")
    @JsonProperty()
    private String apiKey;

    @Column(name = "actif")
    @JsonProperty()
    private Boolean actif = false;
}
