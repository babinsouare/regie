package com.api.regie.notifications;

import com.api.regie.models.Campagnes;
import com.api.regie.models.Clients;
import com.api.regie.models.Facture;
import com.api.regie.models.Paiements;
import com.api.regie.models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Éléments rassemblés autour d'un déclenchement : l'objet concerné, le client à joindre
 * et les valeurs qui viendront remplacer les variables des modèles de message.
 */
public class NotificationContexte {

    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy");

    private final UUID referenceObjet;
    private final Clients client;
    private final Map<String, String> variables = new LinkedHashMap<>();

    /** Utilisateur rattaché à l'objet : sert de destinataire aux règles visant l'agent. */
    private Users utilisateur;

    private NotificationContexte(UUID referenceObjet, Clients client) {
        this.referenceObjet = referenceObjet;
        this.client = client;
    }

    public static NotificationContexte pourCampagne(Campagnes campagne) {
        Clients client = campagne == null ? null : campagne.getClient();
        NotificationContexte contexte = new NotificationContexte(
                campagne == null ? null : campagne.getId(), client);

        contexte.ajouterClient(client);
        if (campagne != null) {
            contexte.utilisateur = campagne.getUser();
            contexte.variables.put("campagne", valeur(campagne.getNomCampagne()));
            contexte.variables.put("dateDebut", date(campagne.getDateDebut()));
            contexte.variables.put("dateFin", date(campagne.getDateFin()));
            contexte.variables.put("statut", valeur(campagne.getStatut()));
        }

        return contexte;
    }

    public static NotificationContexte pourFacture(Facture facture) {
        Campagnes campagne = facture == null ? null : facture.getCampagne();
        Clients client = campagne == null ? null : campagne.getClient();
        NotificationContexte contexte = new NotificationContexte(
                facture == null ? null : facture.getId(), client);

        contexte.ajouterClient(client);
        if (campagne != null) {
            contexte.variables.put("campagne", valeur(campagne.getNomCampagne()));
            contexte.variables.put("dateDebut", date(campagne.getDateDebut()));
            contexte.variables.put("dateFin", date(campagne.getDateFin()));
        }
        if (facture != null) {
            contexte.utilisateur = facture.getUser() != null
                    ? facture.getUser()
                    : (campagne == null ? null : campagne.getUser());
            contexte.variables.put("facture", valeur(facture.getReference()));
            contexte.variables.put("montant", montant(facture.getMontantNet()));
            contexte.variables.put("montantPaye", montant(facture.getMontantPaye()));
            contexte.variables.put("resteAPayer", montant(facture.getMontantResteAPaye()));
            contexte.variables.put("dateEcheance", date(facture.getDateEcheance()));
        }

        return contexte;
    }

    public static NotificationContexte pourPaiement(Paiements paiement) {
        Facture facture = paiement == null ? null : paiement.getFacture();
        NotificationContexte contexte = pourFacture(facture);

        if (paiement != null) {
            contexte.variables.put("montantPaiement", montant(paiement.getMontant()));
            contexte.variables.put("referencePaiement", valeur(paiement.getReference()));
            contexte.variables.put("modePaiement",
                    paiement.getModePaiement() == null ? "" : valeur(paiement.getModePaiement().getMode()));
        }

        return contexte;
    }

    private void ajouterClient(Clients client) {
        if (client == null) return;

        String nomComplet = ((valeur(client.getPrenomResponsable()) + " "
                + valeur(client.getNomResponsable())).trim());

        variables.put("client", valeur(client.getDenomination()));
        variables.put("sigle", valeur(client.getSigle()));
        variables.put("responsable", nomComplet);
    }

    /** Remplace les {variables} du modèle ; une variable inconnue est laissée telle quelle. */
    public String appliquer(String modele) {
        if (modele == null) return null;

        String resultat = modele;
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            resultat = resultat.replace("{" + variable.getKey() + "}", variable.getValue());
        }

        return resultat;
    }

    public UUID getReferenceObjet() {
        return referenceObjet;
    }

    public Clients getClient() {
        return client;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    private static String valeur(String v) {
        return v == null ? "" : v;
    }

    private static String date(Date d) {
        return d == null ? "" : FORMAT_DATE.format(d);
    }

    private static String montant(Double m) {
        return m == null ? "0" : String.format("%,.0f", m).replace(",", " ");
    }
}
