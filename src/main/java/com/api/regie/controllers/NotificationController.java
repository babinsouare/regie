package com.api.regie.controllers;

import com.api.regie.models.NotificationParametrage;
import com.api.regie.models.Result;
import com.api.regie.notifications.NotificationEvenements;
import com.api.regie.notifications.NotificationScheduler;
import com.api.regie.services.NotificationJournalService;
import com.api.regie.services.NotificationParametrageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationParametrageService parametrageService;
    private final NotificationJournalService journalService;
    private final NotificationScheduler scheduler;

    public NotificationController(NotificationParametrageService parametrageService,
                                  NotificationJournalService journalService,
                                  NotificationScheduler scheduler) {
        this.parametrageService = parametrageService;
        this.journalService = journalService;
        this.scheduler = scheduler;
    }

    // ------------------------------------------------------------------ catalogue

    @GetMapping("catalogue")
    public Result getCatalogue(){
        Map<String, Object> catalogue = new LinkedHashMap<>();
        catalogue.put("evenements", NotificationEvenements.TOUS);
        catalogue.put("evenementsImmediats", NotificationEvenements.IMMEDIATS);
        catalogue.put("evenementsPlanifies", NotificationEvenements.PLANIFIES);
        catalogue.put("moments", NotificationEvenements.MOMENTS);
        catalogue.put("destinataires", NotificationEvenements.DESTINATAIRES);
        catalogue.put("canaux", List.of(
                NotificationEvenements.CANAL_SMS,
                NotificationEvenements.CANAL_PUSH,
                NotificationEvenements.CANAL_EMAIL));
        catalogue.put("variables", List.of(
                "{client}", "{sigle}", "{responsable}", "{campagne}", "{statut}",
                "{dateDebut}", "{dateFin}", "{facture}", "{montant}", "{montantPaye}",
                "{resteAPayer}", "{dateEcheance}", "{montantPaiement}", "{referencePaiement}",
                "{modePaiement}"));

        return Result.success(catalogue,
                "Catalogue des déclencheurs, canaux et variables disponibles.",
                "Catalogue of available triggers, channels and variables.",
                "Catálogo dos acionadores, canais e variáveis disponíveis.");
    }

    // ------------------------------------------------------------------ paramétrage

    @GetMapping("parametrage/liste")
    public Result getAllParametrages(){
        return Result.success(parametrageService.getAllParametrages(),
                "Liste des règles de notification.",
                "List of notification rules.",
                "Lista das regras de notificação.");
    }

    @GetMapping("parametrage/actifs")
    public Result getParametragesActifs(){
        return Result.success(parametrageService.findByBtEnabled(true),
                "Liste des règles de notification actives.",
                "List of active notification rules.",
                "Lista das regras de notificação ativas.");
    }

    @GetMapping("parametrage/getbyid")
    public Result getParametrageById(@RequestParam("idParametrage") UUID idParametrage){
        Optional<NotificationParametrage> parametrage = parametrageService.findById(idParametrage);

        if (parametrage.isEmpty()) return Result.error(404,
                "Règle de notification introuvable.",
                "Notification rule not found.",
                "Regra de notificação não encontrada.");

        return Result.success(parametrage.get(),
                "Les informations de la règle de notification.",
                "Notification rule details.",
                "Detalhes da regra de notificação.");
    }

    @GetMapping("parametrage/getbyevenement")
    public Result getParametragesByEvenement(@RequestParam("evenement") String evenement){
        return Result.success(parametrageService.findByEvenement(evenement),
                "Liste des règles pour ce déclencheur.",
                "List of rules for this trigger.",
                "Lista das regras para este acionador.");
    }

    @PostMapping("parametrage/add")
    public Result addParametrage(@RequestBody NotificationParametrage parametrage){
        Result erreur = valider(parametrage);
        if (erreur != null) return erreur;

        if (parametrageService.findByCode(parametrage.getCode()).isPresent()) {
            return Result.error(400,
                    "Ce code de règle existe déjà.",
                    "This rule code already exists.",
                    "Este código de regra já existe.");
        }

        parametrage.setId(null);

        return Result.success(parametrageService.save(parametrage),
                "Règle de notification créée avec succès.",
                "Notification rule created successfully.",
                "Regra de notificação criada com sucesso.");
    }

    @PutMapping("parametrage/update")
    public Result updateParametrage(@RequestBody NotificationParametrage parametrage){
        if (parametrage.getId() == null) return Result.error(400,
                "L'identifiant de la règle est obligatoire.",
                "The rule identifier is required.",
                "O identificador da regra é obrigatório.");

        if (parametrageService.findById(parametrage.getId()).isEmpty()) return Result.error(404,
                "Règle de notification introuvable.",
                "Notification rule not found.",
                "Regra de notificação não encontrada.");

        Result erreur = valider(parametrage);
        if (erreur != null) return erreur;

        if (parametrageService.findByCodeAndIdNot(parametrage.getCode(), parametrage.getId()).isPresent()) {
            return Result.error(400,
                    "Ce code de règle existe déjà.",
                    "This rule code already exists.",
                    "Este código de regra já existe.");
        }

        return Result.success(parametrageService.save(parametrage),
                "Règle de notification mise à jour avec succès.",
                "Notification rule updated successfully.",
                "Regra de notificação atualizada com sucesso.");
    }

    @GetMapping("parametrage/disableorenable")
    public Result activerOuDesactiver(@RequestParam("idParametrage") UUID idParametrage){
        Optional<NotificationParametrage> existante = parametrageService.findById(idParametrage);

        if (existante.isEmpty()) return Result.error(404,
                "Règle de notification introuvable.",
                "Notification rule not found.",
                "Regra de notificação não encontrada.");

        NotificationParametrage parametrage = existante.get();
        parametrage.setBtEnabled(!Boolean.TRUE.equals(parametrage.getBtEnabled()));

        return Result.success(parametrageService.save(parametrage),
                Boolean.TRUE.equals(parametrage.getBtEnabled())
                        ? "Règle activée." : "Règle désactivée.",
                Boolean.TRUE.equals(parametrage.getBtEnabled())
                        ? "Rule enabled." : "Rule disabled.",
                Boolean.TRUE.equals(parametrage.getBtEnabled())
                        ? "Regra ativada." : "Regra desativada.");
    }

    // ------------------------------------------------------------------ journal

    @GetMapping("journal/liste")
    public Result getJournal(@RequestParam(value = "limit", required = false, defaultValue = "50") int limit){
        if (limit <= 0) return Result.error(400,
                "Le nombre de lignes demandé doit être supérieur à 0.",
                "The requested number of rows must be greater than 0.",
                "O número de linhas solicitado deve ser superior a 0.");

        return Result.success(journalService.findDerniers(limit),
                "Journal des notifications.",
                "Notification log.",
                "Registo das notificações.");
    }

    @GetMapping("journal/getbyobjet")
    public Result getJournalByObjet(@RequestParam("referenceObjet") UUID referenceObjet){
        return Result.success(journalService.findByReferenceObjet(referenceObjet),
                "Notifications émises pour cet objet.",
                "Notifications sent for this item.",
                "Notificações enviadas para este item.");
    }

    @GetMapping("journal/getbyevenement")
    public Result getJournalByEvenement(@RequestParam("evenement") String evenement){
        return Result.success(journalService.findByEvenement(evenement),
                "Notifications émises pour ce déclencheur.",
                "Notifications sent for this trigger.",
                "Notificações enviadas para este acionador.");
    }

    @GetMapping("journal/getbyperiode")
    public Result getJournalByPeriode(
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut.after(dateFin)) return Result.error(400,
                "La date de début doit précéder la date de fin.",
                "The start date must be before the end date.",
                "A data de início deve ser anterior à data de fim.");

        return Result.success(journalService.findByPeriode(dateDebut, dateFin),
                "Journal des notifications sur cette période.",
                "Notification log over this period.",
                "Registo das notificações neste período.");
    }

    // ------------------------------------------------------------------ exécution manuelle

    @PostMapping("balayage/executer")
    public Result executerBalayage(){
        scheduler.balayerMaintenant();

        return Result.success(scheduler.reglesPlanifieesActives(),
                "Balayage des notifications planifiées exécuté.",
                "Scheduled notification sweep completed.",
                "Varrimento das notificações planeadas executado.");
    }

    // ------------------------------------------------------------------ validation

    private Result valider(NotificationParametrage parametrage) {
        if (parametrage.getCode() == null || parametrage.getCode().isBlank()) {
            return Result.error(400,
                    "Le code de la règle est obligatoire.",
                    "The rule code is required.",
                    "O código da regra é obrigatório.");
        }

        if (!NotificationEvenements.TOUS.contains(parametrage.getEvenement())) {
            return Result.error(400,
                    "Déclencheur inconnu. Consultez /notification/catalogue.",
                    "Unknown trigger. See /notification/catalogue.",
                    "Acionador desconhecido. Consulte /notification/catalogue.");
        }

        if (parametrage.getMomentRelatif() != null
                && !NotificationEvenements.MOMENTS.contains(parametrage.getMomentRelatif().toLowerCase())) {
            return Result.error(400,
                    "Moment invalide. Valeurs acceptées : avant, jour_meme, apres.",
                    "Invalid moment. Accepted values: avant, jour_meme, apres.",
                    "Momento inválido. Valores aceites: avant, jour_meme, apres.");
        }

        if (parametrage.getDestinataire() != null
                && !NotificationEvenements.DESTINATAIRES.contains(parametrage.getDestinataire().toLowerCase())) {
            return Result.error(400,
                    "Destinataire invalide. Valeurs acceptées : client, agent, interne.",
                    "Invalid recipient. Accepted values: client, agent, interne.",
                    "Destinatário inválido. Valores aceites: client, agent, interne.");
        }

        if (parametrage.getNombreJours() != null && parametrage.getNombreJours() < 0) {
            return Result.error(400,
                    "Le nombre de jours ne peut pas être négatif ; utilisez le champ moment.",
                    "The number of days cannot be negative; use the moment field.",
                    "O número de dias não pode ser negativo; utilize o campo momento.");
        }

        if (!Boolean.TRUE.equals(parametrage.getBtSms())
                && !Boolean.TRUE.equals(parametrage.getBtPush())
                && !Boolean.TRUE.equals(parametrage.getBtEmail())) {
            return Result.error(400,
                    "Au moins un canal doit être activé : SMS, push ou email.",
                    "At least one channel must be enabled: SMS, push or email.",
                    "Pelo menos um canal deve estar ativo: SMS, push ou email.");
        }

        return null;
    }
}
