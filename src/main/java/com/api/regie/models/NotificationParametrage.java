package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

/**
 * Règle d'envoi de notification.
 *
 * <p>Une ligne répond à quatre questions : à quel moment ({@code evenement},
 * {@code momentRelatif}, {@code nombreJours}), par quel canal ({@code btSms},
 * {@code btPush}, {@code btEmail}), vers qui ({@code destinataire}) et avec quel
 * texte (les modèles trilingues).</p>
 *
 * <p>Le {@code code} est unique, mais l'{@code evenement} ne l'est pas : plusieurs
 * règles peuvent viser le même déclencheur, ce qui permet par exemple un rappel
 * d'échéance à J-7 par email puis un second à J-1 par SMS.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_parametrage")
public class NotificationParametrage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created", updatable = false)
    private Date dtCreated;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_last_update")
    private Date dtLastUpdate;

    /** Identifiant fonctionnel de la règle, ex. FACTURE_RAPPEL_J7. */
    @Column(name = "code", unique = true)
    @JsonProperty()
    private String code;

    @Column(name = "libelle")
    @JsonProperty()
    private String libelle;

    @Column(name = "description")
    @JsonProperty()
    private String description;

    /** Déclencheur, parmi les codes de {@link com.api.regie.notifications.NotificationEvenements}. */
    @Column(name = "evenement")
    @JsonProperty()
    private String evenement;

    /** Position par rapport au moment de référence : avant, jour_meme ou apres. */
    @Column(name = "moment_relatif")
    @JsonProperty()
    private String momentRelatif = "jour_meme";

    /** Nombre de jours d'écart, ignoré lorsque le moment est jour_meme. */
    @Column(name = "nombre_jours")
    @JsonProperty()
    private Integer nombreJours = 0;

    @Column(name = "bt_sms")
    @JsonProperty()
    private Boolean btSms = false;

    @Column(name = "bt_push")
    @JsonProperty()
    private Boolean btPush = false;

    @Column(name = "bt_email")
    @JsonProperty()
    private Boolean btEmail = false;

    /** Cible de l'envoi : client, agent ou interne. */
    @Column(name = "destinataire")
    @JsonProperty()
    private String destinataire = "client";

    @Column(name = "objet_email")
    @JsonProperty()
    private String objetEmail;

    @Column(name = "objet_email_en")
    @JsonProperty("objet_email_en")
    private String objetEmailEn;

    @Column(name = "objet_email_pt")
    @JsonProperty("objet_email_pt")
    private String objetEmailPt;

    @Column(name = "message_sms", length = 1000)
    @JsonProperty()
    private String messageSms;

    @Column(name = "message_sms_en", length = 1000)
    @JsonProperty("message_sms_en")
    private String messageSmsEn;

    @Column(name = "message_sms_pt", length = 1000)
    @JsonProperty("message_sms_pt")
    private String messageSmsPt;

    @Column(name = "message_email", length = 4000)
    @JsonProperty()
    private String messageEmail;

    @Column(name = "message_email_en", length = 4000)
    @JsonProperty("message_email_en")
    private String messageEmailEn;

    @Column(name = "message_email_pt", length = 4000)
    @JsonProperty("message_email_pt")
    private String messageEmailPt;

    @Column(name = "message_push", length = 1000)
    @JsonProperty()
    private String messagePush;

    @Column(name = "message_push_en", length = 1000)
    @JsonProperty("message_push_en")
    private String messagePushEn;

    @Column(name = "message_push_pt", length = 1000)
    @JsonProperty("message_push_pt")
    private String messagePushPt;

    /** Heure d'envoi des règles planifiées, au format HH:mm, à titre indicatif. */
    @Column(name = "heure_envoi")
    @JsonProperty()
    private String heureEnvoi = "08:00";

    @Column(name = "bt_enabled")
    @JsonProperty()
    private Boolean btEnabled = true;

}
