package com.api.regie.notifications;

/**
 * Expéditeur d'un canal de notification.
 *
 * <p>Le moteur ne connaît que cette interface : brancher la plateforme SMS de DBA, un
 * serveur SMTP ou un service de push revient à fournir une nouvelle implémentation, sans
 * toucher au paramétrage, au journal ni au déclenchement.</p>
 */
public interface NotificationSender {

    /** Canal servi : sms, push ou email. */
    String canal();

    /**
     * Remet le message au canal.
     *
     * @param adresse numéro, adresse électronique ou identifiant de terminal
     * @param objet   sujet, utilisé par le seul canal email
     * @param message corps du message, variables déjà substituées
     * @return issue de la remise
     */
    ResultatEnvoi envoyer(String adresse, String objet, String message);

    /** Issue d'une remise : statut porté au journal et libellé de retour. */
    record ResultatEnvoi(String statut, String messageRetour) {

        public static ResultatEnvoi simule(String detail) {
            return new ResultatEnvoi("simule", detail);
        }

        public static ResultatEnvoi echec(String detail) {
            return new ResultatEnvoi("echec", detail);
        }
    }
}
