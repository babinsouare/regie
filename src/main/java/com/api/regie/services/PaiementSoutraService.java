package com.api.regie.services;

import com.api.regie.dto.SoutraTransactionInfo;
import com.api.regie.models.*;
import com.api.regie.notifications.NotificationContexte;
import com.api.regie.notifications.NotificationEngine;
import com.api.regie.notifications.NotificationEvenements;
import com.api.regie.utils.SoutraTxnStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Écriture comptable d'un paiement et application des retours Soutra.
 *
 * <p>Regroupée ici plutôt que dans le contrôleur car trois chemins doivent produire le même effet :
 * l'encaissement en espèces, le callback Soutra et le rattrapage planifié. La mutualisation garantit
 * qu'une facture est imputée de façon identique quelle que soit l'origine du règlement.</p>
 */
@Service
public class PaiementSoutraService {

    private static final Logger log = LoggerFactory.getLogger(PaiementSoutraService.class);

    private final PaiementsService paiementsService;
    private final FactureService factureService;
    private final CampagnesService campagnesService;
    private final CampagneStatutService campagneStatutService;
    private final StatutService statutService;
    private final NotificationEngine notificationEngine;

    public PaiementSoutraService(PaiementsService paiementsService,
                                 FactureService factureService,
                                 CampagnesService campagnesService,
                                 CampagneStatutService campagneStatutService,
                                 StatutService statutService,
                                 NotificationEngine notificationEngine) {
        this.paiementsService = paiementsService;
        this.factureService = factureService;
        this.campagnesService = campagnesService;
        this.campagneStatutService = campagneStatutService;
        this.statutService = statutService;
        this.notificationEngine = notificationEngine;
    }

    /** Issue de l'application d'un retour Soutra, pour que l'appelant compose sa réponse. */
    public enum Issue {
        PAIEMENT_INTROUVABLE,
        DEJA_TRAITE,
        TOUJOURS_EN_ATTENTE,
        CONFIRME,
        NON_ABOUTI
    }

    public record Retour(Issue issue, Paiements paiement) {}

    /**
     * Applique le retour d'une transaction Soutra au paiement correspondant.
     *
     * <p>Met à jour le statut du paiement et, lorsque l'encaissement est confirmé, impute la
     * facture. Un paiement qui n'est plus en attente est laissé intact : le callback de Soutra
     * peut être réémis, et une double imputation fausserait la facture.</p>
     */
    @Transactional
    public Retour appliquerRetourSoutra(SoutraTransactionInfo info) {
        if (info == null || info.uniqueTxnId() == null) {
            return new Retour(Issue.PAIEMENT_INTROUVABLE, null);
        }

        Optional<Paiements> paiementOpt = paiementsService.findByReference(info.uniqueTxnId());
        if (paiementOpt.isEmpty()) {
            log.warn("Retour Soutra sans paiement correspondant : {}", info.uniqueTxnId());
            return new Retour(Issue.PAIEMENT_INTROUVABLE, null);
        }

        Paiements paiement = paiementOpt.get();

        if (!"pending".equalsIgnoreCase(paiement.getStatut())) {
            log.info("Retour Soutra ignoré, paiement {} déjà au statut {}.",
                    paiement.getReference(), paiement.getStatut());
            return new Retour(Issue.DEJA_TRAITE, paiement);
        }

        if (info.txnId() != null) paiement.setReferenceExterne(info.txnId());
        if (paiement.getMsisdnClient() == null) paiement.setMsisdnClient(info.phoneNumber());
        if (info.username() != null) paiement.setMessage(info.username());

        // Transaction encore en cours : le paiement reste en attente, le rattrapage repassera.
        if (SoutraTxnStatus.estTransitoire(info.txnStatus())) {
            return new Retour(Issue.TOUJOURS_EN_ATTENTE, paiementsService.addPaiements(paiement));
        }

        if (info.txnStatus() == SoutraTxnStatus.SUCCESS) {
            // Personne n'est authentifié sur un callback : l'imputation accepte un utilisateur nul.
            imputerSurFacture(paiement, paiement.getUser());

            paiement.setStatut("success");
            Paiements savedPaiement = paiementsService.addPaiements(paiement);

            notificationEngine.declencher(NotificationEvenements.PAIEMENT_RECU,
                    NotificationContexte.pourPaiement(savedPaiement));

            return new Retour(Issue.CONFIRME, savedPaiement);
        }

        // Échec, rejet ou remboursement : la facture n'est pas touchée. Un remboursement relève
        // d'une écriture décidée par l'OGP, jamais d'un automatisme.
        paiement.setStatut(SoutraTxnStatus.statutPaiement(info.txnStatus()));

        return new Retour(Issue.NON_ABOUTI, paiementsService.addPaiements(paiement));
    }

    /**
     * Impute un paiement sur sa facture et fait suivre la campagne.
     *
     * @param utilisateur auteur de l'écriture, {@code null} lorsqu'elle provient d'un callback
     */
    @Transactional
    public void imputerSurFacture(Paiements paiement, Users utilisateur) {
        Facture facture = paiement.getFacture();

        Double nouveauMontantPaye = (facture.getMontantPaye() != null ? facture.getMontantPaye() : 0.0)
                + paiement.getMontant();
        Double nouveauResteAPayer = facture.getMontantNet() - nouveauMontantPaye;
        if (nouveauResteAPayer < 0) {
            nouveauResteAPayer = 0.0;
        }

        facture.setMontantPaye(nouveauMontantPaye);
        facture.setMontantResteAPaye(nouveauResteAPayer);
        factureService.addFacture(facture);

        Campagnes campagne = facture.getCampagne();
        if (campagne == null) return;

        if (nouveauResteAPayer > 0) {
            campagne.setStatutPaiement("ongoing");
        } else {
            campagne.setStatutPaiement("completed");

            Optional<Statut> statutPaiedOpt = statutService.findByCodeStatut("paied");
            if (statutPaiedOpt.isEmpty()) {
                throw new RuntimeException("Statut 'paied' introuvable en base de données");
            }

            Optional<CampagneStatut> oldCampagneStatutOpt =
                    campagneStatutService.findByCampagneAndBtEnabled(campagne, true);
            if (oldCampagneStatutOpt.isPresent()) {
                campagneStatutService.disableCampagneStatut(oldCampagneStatutOpt.get());
            }

            CampagneStatut newCampagneStatut = new CampagneStatut();
            newCampagneStatut.setCampagne(campagne);
            newCampagneStatut.setStatut(statutPaiedOpt.get());
            newCampagneStatut.setDateChangement(new Date());
            newCampagneStatut.setCommentaire("Paiement de la facture");
            newCampagneStatut.setUser(utilisateur);
            newCampagneStatut.setBtEnabled(true);
            campagneStatutService.addCampagneStatut(newCampagneStatut);

            campagne.setStatut("paied");
        }

        campagnesService.addCampagnes(campagne);
    }
}
