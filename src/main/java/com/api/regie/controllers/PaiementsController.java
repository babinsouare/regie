package com.api.regie.controllers;

import com.api.regie.dto.SoutraTransactionInfo;
import com.api.regie.models.*;
import com.api.regie.notifications.NotificationContexte;
import com.api.regie.notifications.NotificationEngine;
import com.api.regie.notifications.NotificationEvenements;
import com.api.regie.services.*;
import com.api.regie.utils.Helpers;
import com.api.regie.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/paiement")
public class PaiementsController {

    private static final Logger log = LoggerFactory.getLogger(PaiementsController.class);

    /** Mode de paiement encaissé sur place, seul à imputer la facture immédiatement. */
    private static final String MODE_CASH = "Cash";

    private final PaiementsService paiementsService;
    private final FactureService factureService;
    private final ModePaiementService modePaiementService;
    private final CampagnesService campagnesService;
    private final CampagneStatutService campagneStatutService;
    private final StatutService statutService;
    private final SecurityUtils securityUtils;
    private final NotificationEngine notificationEngine;
    private final SoutraTransfertService soutraTransfertService;
    private final PaiementSoutraService paiementSoutraService;
    private  Helpers helpers = new Helpers();

    public PaiementsController(PaiementsService paiementsService,
                               FactureService factureService,
                               ModePaiementService modePaiementService,
                               CampagnesService campagnesService,
                               CampagneStatutService campagneStatutService,
                               StatutService statutService,
                               SecurityUtils securityUtils,
                               NotificationEngine notificationEngine,
                               SoutraTransfertService soutraTransfertService,
                               PaiementSoutraService paiementSoutraService) {
        this.paiementsService = paiementsService;
        this.factureService = factureService;
        this.modePaiementService = modePaiementService;
        this.campagnesService = campagnesService;
        this.campagneStatutService = campagneStatutService;
        this.statutService = statutService;
        this.securityUtils = securityUtils;
        this.notificationEngine = notificationEngine;
        this.soutraTransfertService = soutraTransfertService;
        this.paiementSoutraService = paiementSoutraService;
    }

    @GetMapping("liste")
    public Result getAllPaiements(){
        return Result.success(paiementsService.getAllPaiements(),
                "Liste des paiements.",
                "List of payments.",
                "Lista dos pagamentos.");
    }

    @GetMapping("getbyid")
    public Result getPaiementById(@RequestParam("idPaiement") UUID idPaiement){
        return Result.success(paiementsService.findById(idPaiement),
                "Les informations du paiement.",
                "Payment details.",
                "Detalhes do pagamento.");
    }

    @GetMapping("getbyfacture")
    public Result getPaiementsByFacture(@RequestParam("idFacture") UUID idFacture){
        return Result.success(paiementsService.findByFactureId(idFacture),
                "Liste des paiements de la facture.",
                "List of the invoice's payments.",
                "Lista dos pagamentos da fatura.");
    }

    @GetMapping("getbymodepaiement")
    public Result getPaiementsByModePaiement(@RequestParam("idModePaiement") UUID idModePaiement){
        return Result.success(paiementsService.findByModePaiementId(idModePaiement),
                "Liste des paiements par mode de paiement.",
                "List of payments by payment method.",
                "Lista dos pagamentos por método de pagamento.");
    }

    @GetMapping("getbyuser")
    public Result getPaiementsByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(paiementsService.findByUserId(idUser),
                "Liste des paiements de l'utilisateur.",
                "List of the user's payments.",
                "Lista dos pagamentos do utilizador.");
    }

    @GetMapping("getbystatut")
    public Result getPaiementsByStatut(@RequestParam("statut") String statut){
        return Result.success(paiementsService.findByStatut(statut),
                "Liste des paiements par statut.",
                "List of payments by status.",
                "Lista dos pagamentos por estado.");
    }

    @GetMapping("derniers")
    public Result getDerniersPaiements(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit){

        if (limit <= 0) return Result.error(400,
                "Le nombre de paiements demandé doit être supérieur à 0.",
                "The requested number of payments must be greater than 0.",
                "O número de pagamentos solicitado deve ser superior a 0.");

        return Result.success(paiementsService.findDerniersPaiements(limit),
                "Liste des " + limit + " derniers paiements.",
                "List of the last " + limit + " payments.",
                "Lista dos últimos " + limit + " pagamentos.");
    }

    @PostMapping("/add")
    @Transactional
    public Result addPaiement(@RequestParam("referenceFacture") String referenceFacture,
                              @RequestParam("montant") Double montant,
                              @RequestParam("modePaiement") String modePaiementCode,
                              @RequestParam(value = "message", required = false) String message,
                              @RequestParam(value = "msisdnClient", required = false) String msisdnClient){

        if(montant == null || montant <= 0){
            return Result.error(400,
                    "Le montant doit être supérieur à 0.",
                    "The amount must be greater than 0.",
                    "O montante deve ser superior a 0.");
        }

        Optional<Facture> factureOpt = factureService.findByReference(referenceFacture);
        if(factureOpt.isEmpty()){
            return Result.error(404, "Facture introuvable.", "Invoice not found.", "Fatura não encontrada.");
        }
        Facture facture = factureOpt.get();

        Double resteAPayer = facture.getMontantResteAPaye() != null ? facture.getMontantResteAPaye() : facture.getMontantNet();
        if(resteAPayer <= 0){
            return Result.error(400,
                    "Cette facture est déjà entièrement payée.",
                    "This invoice is already fully paid.",
                    "Esta fatura já está totalmente paga.");
        }

        if(montant > resteAPayer){
            return Result.error(400, "Le montant dépasse le reste à payer (" + resteAPayer + ").",
                    "The amount exceeds the outstanding balance (" + resteAPayer + ").",
                    "O montante excede o valor em dívida (" + resteAPayer + ").");
        }

        Optional<ModePaiement> modePaiementOpt = modePaiementService.findByMode(modePaiementCode);
        if(modePaiementOpt.isEmpty()){
            return Result.error(400, "Mode de paiement invalide.", "Invalid payment method.", "Método de pagamento inválido.");
        }
        ModePaiement modePaiement = modePaiementOpt.get();

        Users currentUser = securityUtils.getCurrentUser();
        boolean estCash = MODE_CASH.equalsIgnoreCase(modePaiementCode);

        // Un encaissement en espèces est une écriture manuelle : elle engage l'agent qui la saisit.
        // Les autres modes peuvent être initiés par le client depuis son mobile, sans compte interne.
        if(estCash && currentUser == null){
            return Result.error(401,
                    "Vous devez être connecté pour enregistrer un paiement en espèces.",
                    "You must be signed in to record a cash payment.",
                    "Tem de iniciar sessão para registar um pagamento em numerário.");
        }

        Paiements paiement = new Paiements();
        paiement.setMontant(montant);
        paiement.setFacture(facture);
        paiement.setModePaiement(modePaiement);
        paiement.setMessage(message);
        paiement.setUser(currentUser);
        paiement.setMsisdnClient(msisdnClient);
        paiement.setReference(helpers.generateReference());

        if(estCash){
            paiementSoutraService.imputerSurFacture(paiement, currentUser);

            paiement.setStatut("success");
            Paiements savedPaiement = paiementsService.addPaiements(paiement);

            notificationEngine.declencher(NotificationEvenements.PAIEMENT_RECU,
                    NotificationContexte.pourPaiement(savedPaiement));

            return Result.success(savedPaiement,
                    "Paiement effectué avec succès.",
                    "Payment completed successfully.",
                    "Pagamento efetuado com sucesso.");
        }

        // Hors espèces : la facture n'est pas touchée ici. Elle ne le sera qu'au retour de Soutra,
        // par le callback ou par le rattrapage, une fois l'encaissement confirmé.
        paiement.setStatut("pending");
        Paiements savedPaiement = paiementsService.addPaiements(paiement);

        String note = "Facture " + facture.getReference()
                + (facture.getCampagne() != null && facture.getCampagne().getClient() != null
                        ? " - " + facture.getCampagne().getClient().getDenomination() : "");

        try {
            var reponse = soutraTransfertService.initiatePayment(montant, note, savedPaiement.getReference());

            if(reponse != null && reponse.data() != null && reponse.data().has("txn_id")){
                savedPaiement.setReferenceExterne(reponse.data().get("txn_id").asText());
                savedPaiement = paiementsService.addPaiements(savedPaiement);
            }

            Map<String, Object> donnees = new LinkedHashMap<>();
            donnees.put("paiement", savedPaiement);
            donnees.put("paymentUrl", reponse == null ? null : reponse.paymentUrl());

            return Result.success(donnees,
                    "Paiement initié, en attente de confirmation.",
                    "Payment initiated, awaiting confirmation.",
                    "Pagamento iniciado, a aguardar confirmação.");

        } catch (Exception e) {
            log.error("Initiation Soutra en échec pour le paiement {} : {}",
                    savedPaiement.getReference(), e.getMessage(), e);

            savedPaiement.setStatut("failed");
            savedPaiement.setMessage(e.getMessage());
            paiementsService.addPaiements(savedPaiement);

            return Result.error(502,
                    "Impossible d'initier le paiement auprès de Soutra.",
                    "Unable to initiate the payment with Soutra.",
                    "Não foi possível iniciar o pagamento junto da Soutra.");
        }
    }

    /**
     * Callback appelé par Soutra à l'issue d'une transaction.
     *
     * <p>La route est publique : c'est le déchiffrement de {@code data} avec le secret partagé qui
     * authentifie l'appelant. La réponse est toujours un 200, y compris lorsque rien n'est appliqué,
     * afin que Soutra ne réémette pas indéfiniment.</p>
     */
    @RequestMapping(value = "/callback/soutra", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional
    public Result callbackSoutra(@RequestParam(value = "data", required = false) String data,
                                 @RequestParam(value = "merchant_mobile_no", required = false) String merchantMobileNo){

        log.info("Callback Soutra reçu [marchand={}]", merchantMobileNo);

        if(data == null || data.isBlank()){
            return Result.error(400,
                    "Donnée de callback absente.",
                    "Missing callback data.",
                    "Dados de callback em falta.");
        }

        try {
            SoutraTransactionInfo info = soutraTransfertService.parseCallback(data);

            return traduire(paiementSoutraService.appliquerRetourSoutra(info));

        } catch (Exception e) {
            log.error("Callback Soutra en erreur : {}", e.getMessage(), e);

            return Result.error(400,
                    "Callback illisible.",
                    "Unreadable callback.",
                    "Callback ilegível.");
        }
    }

    /**
     * Rattrapage manuel : interroge Soutra sur un paiement et applique le retour obtenu.
     *
     * <p>Sert lorsque le callback ne parvient pas, un encaissement réel restant alors bloqué en attente.</p>
     */
    @PostMapping("/verifier")
    @Transactional
    public Result verifierPaiement(@RequestParam("reference") String reference){
        try {
            SoutraTransactionInfo info = soutraTransfertService.statusOf(reference);

            return traduire(paiementSoutraService.appliquerRetourSoutra(info));

        } catch (Exception e) {
            log.error("Vérification Soutra en échec pour {} : {}", reference, e.getMessage(), e);

            return Result.error(502,
                    "Impossible de joindre Soutra pour vérifier ce paiement.",
                    "Unable to reach Soutra to verify this payment.",
                    "Não foi possível contactar a Soutra para verificar este pagamento.");
        }
    }

    @PutMapping("/update")
    @Transactional
    public Result updatePaiement(@RequestParam("idPaiement") UUID idPaiement,
                                 @RequestParam(value = "montant", required = false) Double montant,
                                 @RequestParam(value = "message", required = false) String message){

        Optional<Paiements> paiementOpt = paiementsService.findById(idPaiement);
        if(paiementOpt.isEmpty()){
            return Result.error(404, "Paiement introuvable.", "Payment not found.", "Pagamento não encontrado.");
        }
        Paiements paiement = paiementOpt.get();

        if(paiement.getModePaiement() == null || !"Cash".equalsIgnoreCase(paiement.getModePaiement().getMode())){
            return Result.error(403,
                    "Modification non autorisée : le mode de paiement n'est pas Cash.",
                    "Update not allowed: the payment method is not Cash.",
                    "Alteração não permitida: o método de pagamento não é Cash.");
        }

        Facture facture = paiement.getFacture();
        if(facture == null){
            return Result.error(400,
                    "Paiement non associé à une facture.",
                    "Payment is not linked to an invoice.",
                    "Pagamento não associado a uma fatura.");
        }

        Users currentUser = securityUtils.getCurrentUser();

        if(message != null){
            paiement.setMessage(message);
        }

        if(montant != null && !montant.equals(paiement.getMontant())){
            if(montant <= 0){
                return Result.error(400,
                        "Le montant doit être supérieur à 0.",
                        "The amount must be greater than 0.",
                        "O montante deve ser superior a 0.");
            }

            Double ancienMontant = paiement.getMontant();
            Double difference = montant - ancienMontant;

            Double ancienMontantPaye = facture.getMontantPaye() != null ? facture.getMontantPaye() : 0.0;
            Double nouveauMontantPaye = ancienMontantPaye + difference;

            if(nouveauMontantPaye < 0){
                return Result.error(400,
                        "La réduction du montant rendrait le total payé négatif.",
                        "Reducing the amount would make the total paid negative.",
                        "A redução do montante tornaria o total pago negativo.");
            }

            if(nouveauMontantPaye > facture.getMontantNet()){
                Double maxMontant = ancienMontant + (facture.getMontantNet() - ancienMontantPaye);
                return Result.error(400, "Le montant dépasse le total de la facture (maximum autorisé : " + maxMontant + ").",
                    "The amount exceeds the invoice total (maximum allowed: " + maxMontant + ").",
                    "O montante excede o total da fatura (máximo permitido: " + maxMontant + ").");
            }

            paiement.setMontant(montant);

            Double nouveauResteAPayer = facture.getMontantNet() - nouveauMontantPaye;
            if(nouveauResteAPayer < 0){
                nouveauResteAPayer = 0.0;
            }

            Double ancienResteAPayer = facture.getMontantNet() - ancienMontantPaye;

            facture.setMontantPaye(nouveauMontantPaye);
            facture.setMontantResteAPaye(nouveauResteAPayer);
            factureService.addFacture(facture);

            Campagnes campagne = facture.getCampagne();
            if(campagne != null){
                if(ancienResteAPayer <= 0 && nouveauResteAPayer > 0){
                    campagne.setStatutPaiement("ongoing");

                    Optional<CampagneStatut> campagneStatutOpt = campagneStatutService.findByCampagneAndBtEnabled(campagne, true);
                    if(campagneStatutOpt.isPresent()){
                        campagneStatutService.disableCampagneStatut(campagneStatutOpt.get());
                    }

                    Optional<Statut> statutPendingOpt = statutService.findByCodeStatut("pending_payment");
                    if(statutPendingOpt.isEmpty()){
                        throw new RuntimeException("Statut 'pending_payment' introuvable en base de données");
                    }

                    CampagneStatut newCampagneStatut = new CampagneStatut();
                    newCampagneStatut.setCampagne(campagne);
                    newCampagneStatut.setStatut(statutPendingOpt.get());
                    newCampagneStatut.setDateChangement(new Date());
                    newCampagneStatut.setCommentaire("Modification paiement : retour à paiement partiel");
                    newCampagneStatut.setUser(currentUser);
                    newCampagneStatut.setBtEnabled(true);
                    campagneStatutService.addCampagneStatut(newCampagneStatut);

                    campagne.setStatut("pending_payment");
                }
                else if(ancienResteAPayer > 0 && nouveauResteAPayer <= 0){
                    campagne.setStatutPaiement("completed");

                    Optional<Statut> statutPaiedOpt = statutService.findByCodeStatut("paied");
                    if(statutPaiedOpt.isEmpty()){
                        throw new RuntimeException("Statut 'paied' introuvable en base de données");
                    }

                    Optional<CampagneStatut> oldCampagneStatutOpt = campagneStatutService.findByCampagneAndBtEnabled(campagne, true);
                    if(oldCampagneStatutOpt.isPresent()){
                        campagneStatutService.disableCampagneStatut(oldCampagneStatutOpt.get());
                    }

                    CampagneStatut newCampagneStatut = new CampagneStatut();
                    newCampagneStatut.setCampagne(campagne);
                    newCampagneStatut.setStatut(statutPaiedOpt.get());
                    newCampagneStatut.setDateChangement(new Date());
                    newCampagneStatut.setCommentaire("Modification paiement : paiement complet");
                    newCampagneStatut.setUser(currentUser);
                    newCampagneStatut.setBtEnabled(true);
                    campagneStatutService.addCampagneStatut(newCampagneStatut);

                    campagne.setStatut("paied");
                }
                else {
                    campagne.setStatutPaiement(nouveauResteAPayer > 0 ? "ongoing" : "completed");
                }

                campagnesService.addCampagnes(campagne);
            }
        }

        Paiements savedPaiement = paiementsService.addPaiements(paiement);
        return Result.success(savedPaiement,
                "Paiement modifié avec succès.",
                "Payment updated successfully.",
                "Pagamento alterado com sucesso.");
    }

    /** Traduit l'issue d'un retour Soutra en réponse d'API. */
    private Result traduire(PaiementSoutraService.Retour retour) {
        return switch (retour.issue()) {
            case PAIEMENT_INTROUVABLE -> Result.error(404,
                    "Paiement introuvable.", "Payment not found.", "Pagamento não encontrado.");
            case DEJA_TRAITE -> Result.success(retour.paiement(),
                    "Paiement déjà traité.", "Payment already processed.", "Pagamento já processado.");
            case TOUJOURS_EN_ATTENTE -> Result.success(retour.paiement(),
                    "Paiement toujours en attente de confirmation.",
                    "Payment still awaiting confirmation.",
                    "Pagamento ainda a aguardar confirmação.");
            case CONFIRME -> Result.success(retour.paiement(),
                    "Paiement confirmé et imputé.",
                    "Payment confirmed and applied.",
                    "Pagamento confirmado e aplicado.");
            case NON_ABOUTI -> Result.success(retour.paiement(),
                    "Paiement non abouti, facture inchangée.",
                    "Payment unsuccessful, invoice unchanged.",
                    "Pagamento não concluído, fatura inalterada.");
        };
    }
}
