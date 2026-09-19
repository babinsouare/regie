package com.api.regie.notifications;

import com.api.regie.models.Campagnes;
import com.api.regie.models.Facture;
import com.api.regie.models.NotificationParametrage;
import com.api.regie.repository.NotificationParametrageRepository;
import com.api.regie.services.CampagnesService;
import com.api.regie.services.FactureService;
import com.api.regie.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Balayage quotidien des règles dont le déclenchement dépend d'une date.
 *
 * <p>Pour chaque règle active, calcule la journée visée à partir de {@code momentRelatif} et
 * {@code nombreJours}, puis applique la règle aux campagnes ou aux factures qui tombent ce
 * jour-là. Le journal empêche qu'un même rappel reparte au passage suivant.</p>
 */
@Component
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    private final NotificationParametrageRepository parametrageRepository;
    private final CampagnesService campagnesService;
    private final FactureService factureService;
    private final NotificationEngine engine;

    public NotificationScheduler(NotificationParametrageRepository parametrageRepository,
                                 CampagnesService campagnesService,
                                 FactureService factureService,
                                 NotificationEngine engine) {
        this.parametrageRepository = parametrageRepository;
        this.campagnesService = campagnesService;
        this.factureService = factureService;
        this.engine = engine;
    }

    /** Passage quotidien, à 8 h par défaut ; l'heure est réglable par {@code ogp.notifications.cron}. */
    @Scheduled(cron = "${ogp.notifications.cron:0 0 8 * * *}")
    public void balayer() {
        log.info("Balayage des notifications planifiées — début.");

        for (String evenement : NotificationEvenements.PLANIFIES) {
            for (NotificationParametrage regle :
                    parametrageRepository.findByEvenementAndBtEnabled(evenement, true)) {
                try {
                    traiter(regle);
                } catch (Exception e) {
                    log.error("Règle {} : balayage interrompu — {}", regle.getCode(), e.getMessage(), e);
                }
            }
        }

        log.info("Balayage des notifications planifiées — fin.");
    }

    private void traiter(NotificationParametrage regle) {
        LocalDate cible = journeeCible(regle);
        Date debut = Helpers.debutDeJournee(java.sql.Date.valueOf(cible));
        Date fin = Helpers.finDeJournee(java.sql.Date.valueOf(cible));

        switch (regle.getEvenement()) {
            case NotificationEvenements.CAMPAGNE_DEBUT -> {
                for (Campagnes campagne : campagnesService.findByPeriode("debut", debut, fin)) {
                    engine.appliquer(regle, NotificationContexte.pourCampagne(campagne));
                }
            }
            case NotificationEvenements.CAMPAGNE_FIN, NotificationEvenements.CAMPAGNE_AVANT_FIN -> {
                for (Campagnes campagne : campagnesService.findByPeriode("fin", debut, fin)) {
                    engine.appliquer(regle, NotificationContexte.pourCampagne(campagne));
                }
            }
            case NotificationEvenements.FACTURE_AVANT_ECHEANCE, NotificationEvenements.FACTURE_ECHUE -> {
                for (Facture facture : factureService.findByPeriodeEcheance(debut, fin)) {
                    // Une facture soldée n'a plus à être relancée.
                    if (resteAPayer(facture) <= 0) continue;

                    engine.appliquer(regle, NotificationContexte.pourFacture(facture));
                }
            }
            default -> log.warn("Règle {} : déclencheur planifié inconnu {}.",
                    regle.getCode(), regle.getEvenement());
        }
    }

    /**
     * Journée que la règle vise aujourd'hui.
     *
     * <p>Un rappel « 7 jours avant l'échéance » exécuté ce matin doit retenir les factures dont
     * l'échéance tombe dans 7 jours ; le décalage s'applique donc à l'inverse de son libellé.</p>
     */
    private LocalDate journeeCible(NotificationParametrage regle) {
        int jours = regle.getNombreJours() == null ? 0 : Math.abs(regle.getNombreJours());
        String moment = regle.getMomentRelatif() == null ? "jour_meme" : regle.getMomentRelatif();

        return switch (moment.toLowerCase()) {
            case "avant" -> LocalDate.now().plusDays(jours);
            case "apres" -> LocalDate.now().minusDays(jours);
            default -> LocalDate.now();
        };
    }

    private double resteAPayer(Facture facture) {
        return facture.getMontantResteAPaye() == null ? 0 : facture.getMontantResteAPaye();
    }

    /** Exposé pour permettre un déclenchement manuel depuis l'API. */
    public void balayerMaintenant() {
        balayer();
    }

    /** Règles planifiées actives, pour restitution dans l'API. */
    public List<NotificationParametrage> reglesPlanifieesActives() {
        return NotificationEvenements.PLANIFIES.stream()
                .flatMap(e -> parametrageRepository.findByEvenementAndBtEnabled(e, true).stream())
                .toList();
    }
}
