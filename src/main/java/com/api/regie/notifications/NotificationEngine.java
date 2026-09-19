package com.api.regie.notifications;

import com.api.regie.models.Clients;
import com.api.regie.models.NotificationJournal;
import com.api.regie.models.NotificationParametrage;
import com.api.regie.models.Users;
import com.api.regie.repository.NotificationJournalRepository;
import com.api.regie.repository.NotificationParametrageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Moteur d'envoi des notifications.
 *
 * <p>Pour un déclencheur donné, retient les règles actives, compose le message dans la langue
 * du destinataire, le remet à chaque canal activé puis inscrit l'envoi au journal. Le journal
 * fait aussi office de garde : une règle ne s'applique qu'une fois par objet et par canal.</p>
 *
 * <p>Un incident d'envoi n'interrompt jamais l'opération métier appelante : il est capturé,
 * journalisé en échec, et le traitement se poursuit.</p>
 */
@Service
public class NotificationEngine {

    private static final Logger log = LoggerFactory.getLogger(NotificationEngine.class);

    private final NotificationParametrageRepository parametrageRepository;
    private final NotificationJournalRepository journalRepository;
    private final Map<String, NotificationSender> senders;

    @Value("${ogp.notifications.interne.email:notifications@ogp.gn}")
    private String emailInterne;

    @Value("${ogp.notifications.interne.msisdn:}")
    private String msisdnInterne;

    public NotificationEngine(NotificationParametrageRepository parametrageRepository,
                              NotificationJournalRepository journalRepository,
                              List<NotificationSender> senders) {
        this.parametrageRepository = parametrageRepository;
        this.journalRepository = journalRepository;
        this.senders = senders.stream()
                .collect(Collectors.toMap(NotificationSender::canal, Function.identity()));
    }

    /** Applique toutes les règles actives portant sur ce déclencheur. */
    public void declencher(String evenement, NotificationContexte contexte) {
        if (contexte == null) return;

        try {
            for (NotificationParametrage regle :
                    parametrageRepository.findByEvenementAndBtEnabled(evenement, true)) {
                appliquer(regle, contexte);
            }
        } catch (Exception e) {
            // Le déclencheur est appelé depuis le flux métier : il ne doit rien faire échouer.
            log.error("Déclencheur {} : notifications abandonnées — {}", evenement, e.getMessage(), e);
        }
    }

    /** Applique une règle précise, le balayage quotidien ayant déjà vérifié sa date. */
    public void appliquer(NotificationParametrage regle, NotificationContexte contexte) {
        if (regle == null || contexte == null || !Boolean.TRUE.equals(regle.getBtEnabled())) return;

        if (Boolean.TRUE.equals(regle.getBtSms())) {
            remettre(regle, contexte, NotificationEvenements.CANAL_SMS);
        }
        if (Boolean.TRUE.equals(regle.getBtEmail())) {
            remettre(regle, contexte, NotificationEvenements.CANAL_EMAIL);
        }
        if (Boolean.TRUE.equals(regle.getBtPush())) {
            remettre(regle, contexte, NotificationEvenements.CANAL_PUSH);
        }
    }

    private void remettre(NotificationParametrage regle, NotificationContexte contexte, String canal) {
        try {
            if (dejaEnvoye(regle, contexte, canal)) return;

            String langue = langueDe(regle, contexte);
            String message = contexte.appliquer(modele(regle, canal, langue));

            if (message == null || message.isBlank()) {
                log.debug("Règle {} : aucun modèle {} renseigné, envoi ignoré.", regle.getCode(), canal);
                return;
            }

            String adresse = adresse(regle, contexte, canal);
            String objet = contexte.appliquer(objetEmail(regle, langue));

            NotificationSender sender = senders.get(canal);
            NotificationSender.ResultatEnvoi resultat = sender == null
                    ? NotificationSender.ResultatEnvoi.echec("Aucun expéditeur pour le canal " + canal)
                    : sender.envoyer(adresse, objet, message);

            journaliser(regle, contexte, canal, adresse, objet, message, resultat);

        } catch (Exception e) {
            // Une notification ne doit jamais faire échouer la campagne, la facture ou le paiement.
            log.error("Échec de la notification {} sur le canal {} : {}",
                    regle.getCode(), canal, e.getMessage(), e);
        }
    }

    private boolean dejaEnvoye(NotificationParametrage regle, NotificationContexte contexte, String canal) {
        if (contexte.getReferenceObjet() == null) return false;

        return journalRepository.existsByParametrageIdAndReferenceObjetAndCanal(
                regle.getId(), contexte.getReferenceObjet(), canal);
    }

    private void journaliser(NotificationParametrage regle, NotificationContexte contexte, String canal,
                             String adresse, String objet, String message,
                             NotificationSender.ResultatEnvoi resultat) {
        NotificationJournal journal = new NotificationJournal();
        journal.setParametrage(regle);
        journal.setEvenement(regle.getEvenement());
        journal.setCanal(canal);
        journal.setReferenceObjet(contexte.getReferenceObjet());
        journal.setAdresseDestinataire(adresse);
        journal.setObjet(objet);
        journal.setMessage(message);
        journal.setStatut(resultat.statut());
        journal.setMessageRetour(resultat.messageRetour());

        journalRepository.save(journal);
    }

    private String langueDe(NotificationParametrage regle, NotificationContexte contexte) {
        if (!"client".equalsIgnoreCase(regle.getDestinataire())) return "fr";

        Clients client = contexte.getClient();
        String langue = client == null ? null : client.getLangue();

        return langue == null || langue.isBlank() ? "fr" : langue.trim().toLowerCase();
    }

    private String modele(NotificationParametrage regle, String canal, String langue) {
        return switch (canal) {
            case NotificationEvenements.CANAL_SMS -> switch (langue) {
                case "en" -> premierRenseigne(regle.getMessageSmsEn(), regle.getMessageSms());
                case "pt" -> premierRenseigne(regle.getMessageSmsPt(), regle.getMessageSms());
                default -> regle.getMessageSms();
            };
            case NotificationEvenements.CANAL_EMAIL -> switch (langue) {
                case "en" -> premierRenseigne(regle.getMessageEmailEn(), regle.getMessageEmail());
                case "pt" -> premierRenseigne(regle.getMessageEmailPt(), regle.getMessageEmail());
                default -> regle.getMessageEmail();
            };
            case NotificationEvenements.CANAL_PUSH -> switch (langue) {
                case "en" -> premierRenseigne(regle.getMessagePushEn(), regle.getMessagePush());
                case "pt" -> premierRenseigne(regle.getMessagePushPt(), regle.getMessagePush());
                default -> regle.getMessagePush();
            };
            default -> null;
        };
    }

    private String objetEmail(NotificationParametrage regle, String langue) {
        return switch (langue) {
            case "en" -> premierRenseigne(regle.getObjetEmailEn(), regle.getObjetEmail());
            case "pt" -> premierRenseigne(regle.getObjetEmailPt(), regle.getObjetEmail());
            default -> regle.getObjetEmail();
        };
    }

    /** Retombe sur le français dès qu'une traduction manque, plutôt que de n'envoyer rien. */
    private String premierRenseigne(String traduction, String defaut) {
        return traduction == null || traduction.isBlank() ? defaut : traduction;
    }

    private String adresse(NotificationParametrage regle, NotificationContexte contexte, String canal) {
        String destinataire = regle.getDestinataire() == null ? "client" : regle.getDestinataire().toLowerCase();

        return switch (destinataire) {
            case "agent" -> adresseUtilisateur(contexte.getUtilisateur(), canal);
            case "interne" -> NotificationEvenements.CANAL_EMAIL.equals(canal) ? emailInterne : msisdnInterne;
            default -> adresseClient(contexte.getClient(), canal);
        };
    }

    private String adresseClient(Clients client, String canal) {
        if (client == null) return null;

        return switch (canal) {
            case NotificationEvenements.CANAL_EMAIL -> client.getEmailResponsable();
            case NotificationEvenements.CANAL_SMS -> client.getTelephoneResponsable();
            default -> client.getId() == null ? null : client.getId().toString();
        };
    }

    private String adresseUtilisateur(Users utilisateur, String canal) {
        if (utilisateur == null) return null;

        return switch (canal) {
            case NotificationEvenements.CANAL_EMAIL -> utilisateur.getEmail();
            case NotificationEvenements.CANAL_SMS -> utilisateur.getMsisdn();
            default -> utilisateur.getId() == null ? null : utilisateur.getId().toString();
        };
    }
}
