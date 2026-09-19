package com.api.regie.config;

import com.api.regie.models.Campagnes;
import com.api.regie.services.CampagneTransitionService;
import com.api.regie.services.CampagnesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Avancement automatique des campagnes le long de leur calendrier.
 *
 * <p>Une campagne réglée reste en {@code paied} jusqu'à ce que sa période commence, puis passe en
 * {@code ongoing} tant qu'on se trouve dans son intervalle de dates, et enfin en {@code ended} une
 * fois la date de fin dépassée. Sans ce passage quotidien, aucune campagne n'atteindrait jamais
 * ces deux statuts : rien d'autre dans l'application ne les pose.</p>
 */
@Component
public class CampagnesScheduler {

    private static final Logger log = LoggerFactory.getLogger(CampagnesScheduler.class);

    private static final String PAIED = "paied";
    private static final String ONGOING = "ongoing";
    private static final String ENDED = "ended";

    private final CampagnesService campagnesService;
    private final CampagneTransitionService transitionService;

    public CampagnesScheduler(CampagnesService campagnesService,
                              CampagneTransitionService transitionService) {
        this.campagnesService = campagnesService;
        this.transitionService = transitionService;
    }

    /** Passage quotidien peu après minuit, l'échéance étant un changement de date. */
    @Scheduled(cron = "${ogp.campagnes.transition.cron:0 15 0 * * *}")
    public void avancerLesCampagnes() {
        LocalDate aujourdhui = LocalDate.now();
        int bascules = 0;

        bascules += depuisPaied(aujourdhui);
        bascules += depuisOngoing(aujourdhui);

        if (bascules > 0) {
            log.info("Avancement des campagnes : {} bascule(s) appliquée(s).", bascules);
        }
    }

    /** Une campagne réglée démarre à sa date de début, ou se clôt d'emblée si sa période est passée. */
    private int depuisPaied(LocalDate aujourdhui) {
        int bascules = 0;

        for (Campagnes campagne : campagnesService.findByStatut(PAIED)) {
            try {
                LocalDate debut = enDateLocale(campagne.getDateDebut());
                LocalDate fin = enDateLocale(campagne.getDateFin());

                // Cas de rattrapage : la période s'est écoulée sans que la campagne ait démarré.
                if (fin != null && fin.isBefore(aujourdhui)) {
                    transitionService.changerStatut(campagne, ENDED,
                            "Fin de campagne atteinte");
                    bascules++;
                } else if (debut != null && !debut.isAfter(aujourdhui)) {
                    transitionService.changerStatut(campagne, ONGOING,
                            "Début de campagne atteint");
                    bascules++;
                }
            } catch (Exception e) {
                // Une campagne en erreur ne doit pas interrompre le traitement des suivantes.
                log.error("Avancement de la campagne {} en échec : {}",
                        campagne.getNomCampagne(), e.getMessage(), e);
            }
        }

        return bascules;
    }

    /** Une campagne en diffusion se clôt une fois sa date de fin dépassée. */
    private int depuisOngoing(LocalDate aujourdhui) {
        int bascules = 0;

        for (Campagnes campagne : campagnesService.findByStatut(ONGOING)) {
            try {
                LocalDate fin = enDateLocale(campagne.getDateFin());

                if (fin != null && fin.isBefore(aujourdhui)) {
                    transitionService.changerStatut(campagne, ENDED,
                            "Fin de campagne atteinte");
                    bascules++;
                }
            } catch (Exception e) {
                log.error("Clôture de la campagne {} en échec : {}",
                        campagne.getNomCampagne(), e.getMessage(), e);
            }
        }

        return bascules;
    }

    /** Passe par l'instant plutôt que par {@code toInstant()}, incompatible avec {@code java.sql.Date}. */
    private LocalDate enDateLocale(Date date) {
        if (date == null) return null;

        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** Exposé pour un déclenchement manuel. */
    public void avancerMaintenant() {
        avancerLesCampagnes();
    }
}
