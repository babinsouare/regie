package com.api.regie.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Expéditeur SMS simulé.
 *
 * <p>Journalise le message au lieu d'appeler la plateforme SMS de Digital Business Africa.
 * Le remplacement par l'appel réel se limite au corps de {@link #envoyer}.</p>
 */
@Component
public class SmsSenderSimule implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SmsSenderSimule.class);

    @Override
    public String canal() {
        return NotificationEvenements.CANAL_SMS;
    }

    @Override
    public ResultatEnvoi envoyer(String adresse, String objet, String message) {
        if (adresse == null || adresse.isBlank()) {
            return ResultatEnvoi.echec("Numéro de téléphone absent.");
        }

        log.info("[SMS SIMULÉ] vers {} : {}", adresse, message);

        return ResultatEnvoi.simule("SMS simulé, aucun appel réel à la plateforme SMS.");
    }
}
