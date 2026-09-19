package com.api.regie.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Helpers {

    public String generateReference(){

        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString().replace("-", "").toUpperCase().substring(0, 10);
    }

    /**
     * Ramène la date au premier instant de sa journée (00:00:00.000).
     *
     * <p>Les colonnes de date portent une heure ; sans cette normalisation une borne de début
     * saisie au format « yyyy-MM-dd » exclurait les enregistrements de la matinée.</p>
     */
    public static Date debutDeJournee(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Ramène la date au dernier instant de sa journée (23:59:59.999).
     *
     * <p>Rend la borne de fin inclusive : une facture créée à 18h le jour de {@code dateFin}
     * entre bien dans l'intervalle.</p>
     */
    public static Date finDeJournee(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

}
