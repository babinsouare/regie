package com.api.regie.controllers;

import com.api.regie.models.Result;
import com.api.regie.services.CampagnesService;
import com.api.regie.services.FactureService;
import com.api.regie.services.PanneauxService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Date DATE_MIN = java.sql.Date.valueOf(LocalDate.of(2000, 1, 1));
    private static final Date DATE_MAX = java.sql.Date.valueOf(LocalDate.of(2100, 12, 31));

    private final PanneauxService panneauxService;
    private final FactureService factureService;
    private final CampagnesService campagnesService;

    public DashboardController(PanneauxService panneauxService,
                               FactureService factureService,
                               CampagnesService campagnesService) {
        this.panneauxService = panneauxService;
        this.factureService = factureService;
        this.campagnesService = campagnesService;
    }

    @GetMapping("/vue-generale")
    public Result getVueGenerale(@RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                                 @RequestParam(value = "dateFin", required = false) String dateFinStr) {
        try {
            Date dateDebut;
            Date dateFin;
            try {
                Date[] dates = parseDates(dateDebutStr, dateFinStr);
                dateDebut = dates[0];
                dateFin = dates[1];
            } catch (DateTimeParseException e) {
                return Result.error(400,
                        "Format de date invalide. Utilisez le format yyyy-MM-dd.",
                        "Invalid date format. Use the yyyy-MM-dd format.",
                        "Formato de data inválido. Utilize o formato yyyy-MM-dd.");
            }

            Map<String, Object> kpi = new LinkedHashMap<>();

            // 1. Taux d'occupation des panneaux
            long totalPanneaux = panneauxService.countByBtAvailable(true) + panneauxService.countByBtAvailable(false);
            long panneauxOccupes = panneauxService.countByBtAvailable(false);
            long panneauxDisponibles = panneauxService.countByBtAvailable(true);
            double tauxOccupation = totalPanneaux > 0 ? (double) panneauxOccupes / totalPanneaux * 100 : 0.0;

            Map<String, Object> occupation = new LinkedHashMap<>();
            occupation.put("totalPanneaux", totalPanneaux);
            occupation.put("panneauxOccupes", panneauxOccupes);
            occupation.put("panneauxDisponibles", panneauxDisponibles);
            occupation.put("tauxOccupation", Math.round(tauxOccupation * 100.0) / 100.0);
            kpi.put("tauxOccupation", occupation);

            // 2. Chiffre d'affaires du mois en cours + mois précédent (tendance)
            LocalDate now = LocalDate.now();
            Date debutMoisCourant = java.sql.Date.valueOf(now.withDayOfMonth(1));
            Date finMoisCourant = java.sql.Date.valueOf(now);
            Date debutMoisPrecedent = java.sql.Date.valueOf(now.minusMonths(1).withDayOfMonth(1));
            Date finMoisPrecedent = java.sql.Date.valueOf(now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth()));

            Double caMoisCourant = factureService.getTotalFacture(debutMoisCourant, finMoisCourant);
            Double caMoisPrecedent = factureService.getTotalFacture(debutMoisPrecedent, finMoisPrecedent);

            Map<String, Object> ca = new LinkedHashMap<>();
            ca.put("moisCourant", caMoisCourant);
            ca.put("moisPrecedent", caMoisPrecedent);
            kpi.put("chiffreAffairesMois", ca);

            // 3. Reste à payer global
            Double resteAPayer = factureService.getTotalImpaye(DATE_MIN, DATE_MAX);
            Double resteAPayerMoisPrecedent = factureService.getTotalImpaye(DATE_MIN, finMoisPrecedent);

            Map<String, Object> resteGlobal = new LinkedHashMap<>();
            resteGlobal.put("montantActuel", resteAPayer);
            resteGlobal.put("montantMoisPrecedent", resteAPayerMoisPrecedent);
            kpi.put("resteAPayer", resteGlobal);

            // 4. Campagnes actives
            long campagnesActives = campagnesService.countByStatut("active");
            Map<String, Object> campagnesActivesMap = new LinkedHashMap<>();
            campagnesActivesMap.put("nombre", campagnesActives);
            kpi.put("campagnesActives", campagnesActivesMap);

            // 5. État du parc de panneaux
            Map<String, Object> etatParc = new LinkedHashMap<>();
            etatParc.put("total", totalPanneaux);
            etatParc.put("disponibles", panneauxDisponibles);
            etatParc.put("occupes", panneauxOccupes);
            kpi.put("etatParc", etatParc);

            // 6. Évolution du chiffre d'affaires (mensuel sur la période)
            List<Object[]> caMensuelData = factureService.getCAMensuel(dateDebut, dateFin);
            List<Map<String, Object>> evolutionCA = new ArrayList<>();
            for (Object[] row : caMensuelData) {
                Map<String, Object> mois = new LinkedHashMap<>();
                mois.put("mois", row[0]);
                mois.put("montant", row[1]);
                evolutionCA.add(mois);
            }
            kpi.put("evolutionCA", evolutionCA);

            // 7. Panneaux par région
            List<Object[]> panneauxRegionData = panneauxService.countPanneauxByRegion();
            List<Map<String, Object>> panneauxParRegion = new ArrayList<>();
            for (Object[] row : panneauxRegionData) {
                Map<String, Object> region = new LinkedHashMap<>();
                region.put("region", row[0]);
                region.put("nombrePanneaux", row[1]);
                panneauxParRegion.add(region);
            }
            kpi.put("panneauxParRegion", panneauxParRegion);

            // 8. Top 5 clients sur la période
            List<Object[]> topClientsData = factureService.getTopClients(dateDebut, dateFin);
            List<Map<String, Object>> topClients = new ArrayList<>();
            for (Object[] row : topClientsData) {
                Map<String, Object> client = new LinkedHashMap<>();
                client.put("denomination", row[0]);
                client.put("sigle", row[1]);
                client.put("nombreCampagnes", row[2]);
                client.put("ca", row[3]);
                topClients.add(client);
            }
            kpi.put("topClients", topClients);

            return Result.success(kpi, "KPI - vue générale.", "KPIs - overview.", "KPIs - visão geral.");
        } catch (Exception e) {
            return Result.error(500,
                    "Erreur du tableau de bord vue générale : " + e.getMessage(),
                    "Overview dashboard error: " + e.getMessage(),
                    "Erro do painel de visão geral: " + e.getMessage());
        }
    }

    @GetMapping("/impayes")
    public Result getImpayes(@RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                             @RequestParam(value = "dateFin", required = false) String dateFinStr) {
        try {
            Date dateDebut;
            Date dateFin;
            try {
                Date[] dates = parseDates(dateDebutStr, dateFinStr);
                dateDebut = dates[0];
                dateFin = dates[1];
            } catch (DateTimeParseException e) {
                return Result.error(400,
                        "Format de date invalide. Utilisez le format yyyy-MM-dd.",
                        "Invalid date format. Use the yyyy-MM-dd format.",
                        "Formato de data inválido. Utilize o formato yyyy-MM-dd.");
            }

            Map<String, Object> kpi = new LinkedHashMap<>();

            // 9. Total impayé sur la période
            Double totalImpaye = factureService.getTotalImpaye(dateDebut, dateFin);
            kpi.put("totalImpaye", totalImpaye);

            // 10. Nombre de factures en retard
            Long facturesEnRetard = factureService.getNombreFacturesEnRetard(dateDebut, dateFin);
            kpi.put("nombreFacturesEnRetard", facturesEnRetard);

            // 11. Nombre de sociétés concernées
            Long societesConcernees = factureService.countClientsAvecImpayes(dateDebut, dateFin);
            kpi.put("nombreSocietesConcernees", societesConcernees);

            // 12. Impayés par société
            List<Object[]> impayesData = factureService.getImpayesParSociete(dateDebut, dateFin);
            List<Map<String, Object>> impayesParSociete = new ArrayList<>();
            for (Object[] row : impayesData) {
                Map<String, Object> societe = new LinkedHashMap<>();
                societe.put("denomination", row[0]);
                societe.put("sigle", row[1]);
                societe.put("nombreFactures", row[2]);
                societe.put("montantFacture", row[3]);
                societe.put("resteAPayer", row[4]);
                impayesParSociete.add(societe);
            }
            kpi.put("impayesParSociete", impayesParSociete);

            return Result.success(kpi, "KPI - impayés.", "KPIs - unpaid.", "KPIs - dívidas.");
        } catch (Exception e) {
            return Result.error(500,
                    "Erreur du tableau de bord impayés : " + e.getMessage(),
                    "Unpaid dashboard error: " + e.getMessage(),
                    "Erro do painel de dívidas: " + e.getMessage());
        }
    }

    private Date[] parseDates(String dateDebutStr, String dateFinStr) {
        Date dateDebut = DATE_MIN;
        Date dateFin = DATE_MAX;

        if (dateDebutStr != null && !dateDebutStr.trim().isEmpty()) {
            LocalDate ld = LocalDate.parse(dateDebutStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            dateDebut = java.sql.Date.valueOf(ld);
        }
        if (dateFinStr != null && !dateFinStr.trim().isEmpty()) {
            LocalDate ld = LocalDate.parse(dateFinStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            dateFin = java.sql.Date.valueOf(ld);
        }

        return new Date[]{dateDebut, dateFin};
    }
}
