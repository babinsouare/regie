package com.api.regie.notifications;

import java.util.List;

/** Catalogue des déclencheurs reconnus par le moteur de notification. */
public interface NotificationEvenements {

    // Déclenchés depuis le flux métier, au moment où l'action a lieu.
    String CAMPAGNE_CREATION = "CAMPAGNE_CREATION";
    String CAMPAGNE_VALIDATION = "CAMPAGNE_VALIDATION";
    String FACTURE_EMISE = "FACTURE_EMISE";
    String PAIEMENT_RECU = "PAIEMENT_RECU";

    // Déclenchés par le balayage quotidien, en fonction d'une date à venir ou passée.
    String CAMPAGNE_DEBUT = "CAMPAGNE_DEBUT";
    String CAMPAGNE_AVANT_FIN = "CAMPAGNE_AVANT_FIN";
    String CAMPAGNE_FIN = "CAMPAGNE_FIN";
    String FACTURE_AVANT_ECHEANCE = "FACTURE_AVANT_ECHEANCE";
    String FACTURE_ECHUE = "FACTURE_ECHUE";

    List<String> IMMEDIATS = List.of(
            CAMPAGNE_CREATION, CAMPAGNE_VALIDATION, FACTURE_EMISE, PAIEMENT_RECU);

    List<String> PLANIFIES = List.of(
            CAMPAGNE_DEBUT, CAMPAGNE_AVANT_FIN, CAMPAGNE_FIN, FACTURE_AVANT_ECHEANCE, FACTURE_ECHUE);

    List<String> TOUS = List.of(
            CAMPAGNE_CREATION, CAMPAGNE_VALIDATION, CAMPAGNE_DEBUT, CAMPAGNE_AVANT_FIN, CAMPAGNE_FIN,
            FACTURE_EMISE, FACTURE_AVANT_ECHEANCE, FACTURE_ECHUE, PAIEMENT_RECU);

    List<String> MOMENTS = List.of("avant", "jour_meme", "apres");

    List<String> DESTINATAIRES = List.of("client", "agent", "interne");

    String CANAL_SMS = "sms";
    String CANAL_PUSH = "push";
    String CANAL_EMAIL = "email";
}
