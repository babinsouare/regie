package com.api.regie.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Expéditeur email simulé : journalise le message au lieu de le remettre à un serveur SMTP. */
@Component
public class EmailSenderSimule implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderSimule.class);

    @Override
    public String canal() {
        return NotificationEvenements.CANAL_EMAIL;
    }

    @Override
    public ResultatEnvoi envoyer(String adresse, String objet, String message) {
        if (adresse == null || adresse.isBlank()) {
            return ResultatEnvoi.echec("Adresse électronique absente.");
        }

        log.info("[EMAIL SIMULÉ] vers {} — objet : {} — corps : {}", adresse, objet, message);

        return ResultatEnvoi.simule("Email simulé, aucun envoi réel.");
    }
}
