package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_last_update")
    private Date dtLastUpdate;

    @Column(name = "nom")
    @JsonProperty()
    private String nom;

    @Column(name = "prenom")
    @JsonProperty()
    private String prenom;

    @Column(name = "msisdn")
    @JsonProperty()
    private String msisdn;

    @Column(name = "email")
    @JsonProperty()
    private String email;

    @Column(name = "password")
    @JsonProperty()
    private String password;

    @Column(name = "role")
    @JsonProperty()
    private String role;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

    @JsonProperty()
    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;

    @Column(name = "id_user")
    @JsonProperty()
    private UUID idUser;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_profil")
    private  Profils profils;


}
