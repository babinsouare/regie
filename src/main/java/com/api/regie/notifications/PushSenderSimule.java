package com.api.regie.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Expéditeur push simulé, en attendant l'enregistrement des terminaux et le service de push. */
@Component
public class PushSenderSimule implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(PushSenderSimule.class);

    @Override
    public String canal() {
        return NotificationEvenements.CANAL_PUSH;
    }

    @Override
    public ResultatEnvoi envoyer(String adresse, String objet, String message) {
        log.info("[PUSH SIMULÉ] vers {} : {}", adresse == null ? "terminal inconnu" : adresse, message);

        return ResultatEnvoi.simule("Notification push simulée, aucun terminal enregistré.");
    }
}
