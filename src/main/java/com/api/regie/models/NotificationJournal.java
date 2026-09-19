package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

/**
 * Trace d'une notification produite.
 *
 * <p>Sert autant de piste d'audit que de garde anti-doublon : le couple
 * {règle, objet concerné, canal} n'est honoré qu'une seule fois, sans quoi la tâche
 * quotidienne réémettrait le même rappel à chaque passage.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_journal")
public class NotificationJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @ManyToOne
    @JsonProperty()
    @JoinColumn(name = "id_parametrage")
    private NotificationParametrage parametrage;

    @Column(name = "evenement")
    @JsonProperty()
    private String evenement;

    /** Canal effectivement emprunté : sms, push ou email. */
    @Column(name = "canal")
    @JsonProperty()
    private String canal;

    /** Identifiant de la campagne ou de la facture à l'origine de l'envoi. */
    @Column(name = "reference_objet")
    @JsonProperty()
    private UUID referenceObjet;

    /** Numéro, adresse ou identifiant de terminal selon le canal. */
    @Column(name = "adresse_destinataire")
    @JsonProperty()
    private String adresseDestinataire;

    @Column(name = "objet")
    @JsonProperty()
    private String objet;

    @Column(name = "message", length = 4000)
    @JsonProperty()
    private String message;

    /** simule, envoye ou echec. */
    @Column(name = "statut")
    @JsonProperty()
    private String statut;

    @Column(name = "message_retour", length = 1000)
    @JsonProperty()
    private String messageRetour;

}
