package com.api.regie.controllers;

import com.api.regie.models.Campagnes;
import com.api.regie.models.Facture;
import com.api.regie.models.Result;
import com.api.regie.notifications.NotificationContexte;
import com.api.regie.notifications.NotificationEngine;
import com.api.regie.notifications.NotificationEvenements;
import com.api.regie.services.CampagnesService;
import com.api.regie.services.FactureService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/facture")
public class FactureController {

    private final FactureService factureService;
    private final CampagnesService campagnesService;
    private final NotificationEngine notificationEngine;

    public FactureController(FactureService factureService, CampagnesService campagnesService,
                             NotificationEngine notificationEngine) {
        this.factureService = factureService;
        this.campagnesService = campagnesService;
        this.notificationEngine = notificationEngine;
    }

    @GetMapping("liste")
    public Result getAllFactures(){
        return Result.success(factureService.getAllFactures(), "Liste des factures.", "List of invoices.", "Lista das faturas.");
    }

    @GetMapping("getbyid")
    public Result getFactureById(@RequestParam("idFacture") UUID idFacture){
        return Result.success(factureService.findById(idFacture),
                "Les informations de la facture.",
                "Invoice details.",
                "Detalhes da fatura.");
    }

    @GetMapping("getbycampagne")
    public Result getFacturesByCampagne(@RequestParam("idCampagne") UUID idCampagne){
        return Result.success(factureService.findByCampagneId(idCampagne),
                "Liste des factures de la campagne.",
                "List of the campaign's invoices.",
                "Lista das faturas da campanha.");
    }

    @GetMapping("getbyuser")
    public Result getFacturesByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(factureService.findByUserId(idUser),
                "Liste des factures de l'utilisateur.",
                "List of the user's invoices.",
                "Lista das faturas do utilizador.");
    }

    @GetMapping("getbyclient")
    public Result getFacturesByClient(@RequestParam("telephoneResponsable") String telephoneResponsable){
        return Result.success(factureService.findByClientTelephone(telephoneResponsable),
                "Liste des factures du client.",
                "List of the client's invoices.",
                "Lista das faturas do cliente.");
    }

    @GetMapping("getbyperiode")
    public Result getFacturesByPeriode(
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut.after(dateFin)) return Result.error(400,
                "La date de début doit précéder la date de fin.",
                "The start date must be before the end date.",
                "A data de início deve ser anterior à data de fim.");

        return Result.success(factureService.findByPeriodeCreation(dateDebut, dateFin),
                "Liste des factures créées sur cette période.",
                "List of invoices created over this period.",
                "Lista das faturas criadas neste período.");
    }

    @GetMapping("getbyecheance")
    public Result getFacturesByEcheance(
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut.after(dateFin)) return Result.error(400,
                "La date de début doit précéder la date de fin.",
                "The start date must be before the end date.",
                "A data de início deve ser anterior à data de fim.");

        return Result.success(factureService.findByPeriodeEcheance(dateDebut, dateFin),
                "Liste des factures dont l'échéance tombe sur cette période.",
                "List of invoices due over this period.",
                "Lista das faturas com vencimento neste período.");
    }

    @GetMapping("dernieres")
    public Result getDernieresFactures(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit){

        if (limit <= 0) return Result.error(400,
                "Le nombre de factures demandé doit être supérieur à 0.",
                "The requested number of invoices must be greater than 0.",
                "O número de faturas solicitado deve ser superior a 0.");

        return Result.success(factureService.findDernieresFactures(limit),
                "Liste des " + limit + " dernières factures.",
                "List of the last " + limit + " invoices.",
                "Lista das últimas " + limit + " faturas.");
    }

    @PostMapping("/add")
    @Transactional
    public Result addFacture(@RequestParam("idCampagne") UUID idCampagne,
                             @RequestParam(value = "idRemise", required = false) UUID idRemise){
        try {
            Optional<Campagnes> campagnes = campagnesService.findById(idCampagne);
            if(campagnes.isEmpty()) return Result.error(404,
                    "Campagne introuvable.",
                    "Campaign not found.",
                    "Campanha não encontrada.");
            Facture facture = factureService.createFactureForCampagne(idCampagne, idRemise);

            notificationEngine.declencher(NotificationEvenements.FACTURE_EMISE,
                    NotificationContexte.pourFacture(facture));

            return Result.success(facture,
                    "Facture créée avec succès.",
                    "Invoice created successfully.",
                    "Fatura criada com sucesso.");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage(), e.getMessage(), e.getMessage());
        }
    }

    @PutMapping("/update")
    @Transactional
    public Result updateFacture(@RequestParam("idFacture") UUID idFacture,
                                @RequestParam(value = "dateEcheance", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateEcheance,
                                @RequestParam(value = "idRemise", required = false) UUID idRemise){
        try {
            Facture facture = factureService.updateFacture(idFacture, dateEcheance, idRemise);
            return Result.success(facture,
                    "Facture mise à jour avec succès.",
                    "Invoice updated successfully.",
                    "Fatura atualizada com sucesso.");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage(), e.getMessage(), e.getMessage());
        }
    }

    @GetMapping("/kpi")
    public Result getKPI(@RequestParam(value = "dateDebut", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateDebut,
                         @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date dateFin){

        if (dateDebut == null) dateDebut = java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1));
        if (dateFin == null) dateFin = java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31));

        Map<String, Object> kpi = new HashMap<>();

        // KPI Financiers
        Double totalFacture = factureService.getTotalFacture(dateDebut, dateFin);
        Double totalPaye = factureService.getTotalPaye(dateDebut, dateFin);
        Double totalImpaye = factureService.getTotalImpaye(dateDebut, dateFin);
        Double montantMoyen = factureService.getMontantMoyen(dateDebut, dateFin);

        // Calcul du taux de recouvrement
        Double tauxRecouvrement = (totalFacture != null && totalFacture > 0 && totalPaye != null) ? (totalPaye / totalFacture) * 100 : 0.0;

        // KPI par statut
        Long nombreFacturesPayees = factureService.getNombreFacturesPayees(dateDebut, dateFin);
        Long nombreFacturesPartielles = factureService.getNombreFacturesPartielles(dateDebut, dateFin);
        Long nombreFacturesImpayees = factureService.getNombreFacturesImpayees(dateDebut, dateFin);
        Long nombreFacturesEnRetard = factureService.getNombreFacturesEnRetard(dateDebut, dateFin);
        Double montantFacturesEnRetard = factureService.getMontantFacturesEnRetard(dateDebut, dateFin);

        // Total des factures
        Long totalFactures = nombreFacturesPayees + nombreFacturesPartielles + nombreFacturesImpayees;

        // Assemblage des résultats
        kpi.put("totalFacture", totalFacture);
        kpi.put("totalPaye", totalPaye);
        kpi.put("totalImpaye", totalImpaye);
        kpi.put("montantMoyen", montantMoyen);
        kpi.put("tauxRecouvrement", Math.round(tauxRecouvrement * 100.0) / 100.0); // Arrondi à 2 décimales

        kpi.put("nombreFacturesPayees", nombreFacturesPayees);
        kpi.put("nombreFacturesPartielles", nombreFacturesPartielles);
        kpi.put("nombreFacturesImpayees", nombreFacturesImpayees);
        kpi.put("totalFactures", totalFactures);

        kpi.put("nombreFacturesEnRetard", nombreFacturesEnRetard);
        kpi.put("montantFacturesEnRetard", montantFacturesEnRetard);

        return Result.success(kpi, "KPI des factures.", "Invoice KPIs.", "KPIs das faturas.");
    }
}
