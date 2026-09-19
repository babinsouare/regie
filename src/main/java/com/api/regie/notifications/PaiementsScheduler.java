package com.api.regie.notifications;

import com.api.regie.dto.SoutraTransactionInfo;
import com.api.regie.models.Paiements;
import com.api.regie.services.PaiementSoutraService;
import com.api.regie.services.PaiementsService;
import com.api.regie.services.SoutraTransfertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Rattrapage des paiements Soutra dont le callback n'est jamais parvenu.
 *
 * <p>Sans ce filet, un encaissement réellement effectué chez Soutra resterait indéfiniment en
 * attente côté OGP et la facture ne serait jamais imputée.</p>
 */
@Component
public class PaiementsScheduler {

    private static final Logger log = LoggerFactory.getLogger(PaiementsScheduler.class);

    private final PaiementsService paiementsService;
    private final SoutraTransfertService soutraTransfertService;
    private final PaiementSoutraService paiementSoutraService;

    /** Âge minimal d'un paiement avant de solliciter Soutra, pour laisser au callback le temps d'arriver. */
    @Value("${ogp.soutra.rattrapage.delai-minutes:10}")
    private long delaiMinutes;

    /** Au-delà de cet âge, le paiement est abandonné et cesse d'être interrogé. */
    @Value("${ogp.soutra.rattrapage.expiration-heures:24}")
    private long expirationHeures;

    public PaiementsScheduler(PaiementsService paiementsService,
                              SoutraTransfertService soutraTransfertService,
                              PaiementSoutraService paiementSoutraService) {
        this.paiementsService = paiementsService;
        this.soutraTransfertService = soutraTransfertService;
        this.paiementSoutraService = paiementSoutraService;
    }

    @Scheduled(cron = "${ogp.soutra.rattrapage.cron:0 0/5 * * * *}")
    public void rattraper() {
        Date limite = Date.from(Instant.now().minus(delaiMinutes, ChronoUnit.MINUTES));
        List<Paiements> enAttente = paiementsService.findByStatutAndDtCreatedBefore("pending", limite);

        if (enAttente.isEmpty()) return;

        log.info("Rattrapage Soutra : {} paiement(s) en attente à vérifier.", enAttente.size());

        Date peremption = Date.from(Instant.now().minus(expirationHeures, ChronoUnit.HOURS));

        for (Paiements paiement : enAttente) {
            try {
                if (paiement.getDtCreated() != null && paiement.getDtCreated().before(peremption)) {
                    paiement.setStatut("expired");
                    paiementsService.addPaiements(paiement);
                    log.info("Paiement {} abandonné après {} h sans confirmation.",
                            paiement.getReference(), expirationHeures);
                    continue;
                }

                SoutraTransactionInfo info = soutraTransfertService.statusOf(paiement.getReference());
                paiementSoutraService.appliquerRetourSoutra(info);

            } catch (Exception e) {
                // Un paiement en erreur ne doit pas interrompre le balayage des suivants.
                log.error("Rattrapage du paiement {} en échec : {}",
                        paiement.getReference(), e.getMessage(), e);
            }
        }
    }

    /** Exposé pour un déclenchement manuel depuis l'API. */
    public void rattraperMaintenant() {
        rattraper();
    }
}
