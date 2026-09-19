package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission_users")
public class PermissionUsers {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private Date dtCreated;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "updated_at")
    private Date dtUpdated;

    @JsonProperty()
    @Column(name = "bt_enabled")
    private Boolean btEnabled = true;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_user")
    private  Users users;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_permission")
    private  Permissions permissions;

    @Column(name = "iduser")
    @JsonProperty()
    private UUID idUser;
}
