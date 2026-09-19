package com.api.regie.controllers;

import com.api.regie.dto.CampagneDetailsResponse;
import com.api.regie.dto.CampagneRequest;
import com.api.regie.models.*;
import com.api.regie.notifications.NotificationContexte;
import com.api.regie.notifications.NotificationEngine;
import com.api.regie.notifications.NotificationEvenements;
import com.api.regie.services.*;
import com.api.regie.utils.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campagne")
public class CampagnesController {

    private final CampagnesService campagnesService;
    private final PanneauxCampagneService panneauxCampagneService;
    private final PanneauxService panneauxService;
    private final StatutService statutService;
    private final CampagneStatutService campagneStatutService;
    private final ClientsService clientsService;
    private final DevisService devisService;
    private final FactureService factureService;
    private final TarifsService tarifsService;
    private final SecurityUtils securityUtils;
    private final NotificationEngine notificationEngine;


    public CampagnesController(CampagnesService campagnesService,
                               PanneauxCampagneService panneauxCampagneService,
                               PanneauxService panneauxService,
                               StatutService statutService,
                               CampagneStatutService campagneStatutService,
                               ClientsService clientsService,
                               DevisService devisService,
                               FactureService factureService,
                               TarifsService tarifsService,
                               SecurityUtils securityUtils,
                               NotificationEngine notificationEngine) {
        this.campagnesService = campagnesService;
        this.panneauxCampagneService = panneauxCampagneService;
        this.panneauxService = panneauxService;
        this.statutService = statutService;
        this.campagneStatutService = campagneStatutService;
        this.clientsService = clientsService;
        this.devisService = devisService;
        this.factureService = factureService;
        this.tarifsService = tarifsService;
        this.securityUtils = securityUtils;
        this.notificationEngine = notificationEngine;
    }

    @GetMapping("liste")
    public Result getAllCampagnes(){
        return Result.success(campagnesService.getAllCampagnes(),
                "Liste des campagnes.",
                "List of campaigns.",
                "Lista das campanhas.");
    }

    @GetMapping("getbyid")
    public Result getCampagneById(@RequestParam("idCampagne") UUID idCampagne){
        Optional<Campagnes> campagneOpt = campagnesService.findById(idCampagne);
        
        if (campagneOpt.isEmpty()) {
            return Result.error(404, "Campagne introuvable.", "Campaign not found.", "Campanha não encontrada.");
        }
        
        Campagnes campagne = campagneOpt.get();
        
        // Récupérer les panneaux associés à la campagne
        List<PanneauxCampagne> panneauxCampagnes = panneauxCampagneService.findByCampagneId(idCampagne);
        List<Panneaux> panneaux = panneauxCampagnes.stream()
                .map(PanneauxCampagne::getPanneaux)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (Panneaux panneau : panneaux) {
            if (!Boolean.TRUE.equals(panneau.getHasSpecialPrice()) && panneau.getCaracteristiquePanneaux() != null) {
                List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(panneau.getCaracteristiquePanneaux().getId());
                if (!tarifs.isEmpty()) {
                    Tarifs tarif = tarifs.get(0);
                    panneau.setPriceDay(tarif.getPriceDay());
                    panneau.setPriceWeek(tarif.getPriceWeek());
                    panneau.setPriceMonth(tarif.getPriceMonth());
                }
            }
        }

        List<Devis> devisList = devisService.findByCampagneId(idCampagne);
        List<Facture> factureList = factureService.findByCampagneId(idCampagne);

        // Créer la réponse avec campagne et panneaux
        CampagneDetailsResponse response = new CampagneDetailsResponse(campagne, panneaux, devisList, factureList);
        
        return Result.success(response,
                "Les informations de la campagne et de ses panneaux.",
                "Campaign details with its billboards.",
                "Detalhes da campanha e dos seus painéis.");
    }

    @GetMapping("getbyclient")
    public Result getCampagnesByClient(@RequestParam("idClient") UUID idClient){
        return Result.success(campagnesService.findByClientId(idClient),
                "Liste des campagnes du client.",
                "List of the client's campaigns.",
                "Lista das campanhas do cliente.");
    }

    @GetMapping("getbyuser")
    public Result getCampagnesByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(campagnesService.findByUserId(idUser),
                "Liste des campagnes de l'utilisateur.",
                "List of the user's campaigns.",
                "Lista das campanhas do utilizador.");
    }

    @GetMapping("getbystatut")
    public Result getCampagnesByStatut(@RequestParam("statut") String statut){
        return Result.success(campagnesService.findByStatut(statut),
                "Liste des campagnes par statut.",
                "List of campaigns by status.",
                "Lista das campanhas por estado.");
    }

    @GetMapping("getbyperiode")
    public Result getCampagnesByPeriode(
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin,
            @RequestParam(value = "typeDate", required = false, defaultValue = "creation") String typeDate){

        if (dateDebut.after(dateFin)) return Result.error(400,
                "La date de début doit précéder la date de fin.",
                "The start date must be before the end date.",
                "A data de início deve ser anterior à data de fim.");

        String typeNormalise = typeDate == null ? "creation" : typeDate.trim().toLowerCase();

        if (!List.of("creation", "debut", "fin").contains(typeNormalise)) {
            return Result.error(400,
                    "Type de date invalide. Valeurs acceptées : creation, debut, fin.",
                    "Invalid date type. Accepted values: creation, debut, fin.",
                    "Tipo de data inválido. Valores aceites: creation, debut, fin.");
        }

        List<Campagnes> campagnes = campagnesService.findByPeriode(typeNormalise, dateDebut, dateFin);

        String message = switch (typeNormalise) {
            case "debut" -> "Liste des campagnes démarrant sur cette période.";
            case "fin" -> "Liste des campagnes se terminant sur cette période.";
            default -> "Liste des campagnes créées sur cette période.";
        };
        String messageEn = switch (typeNormalise) {
            case "debut" -> "List of campaigns starting over this period.";
            case "fin" -> "List of campaigns ending over this period.";
            default -> "List of campaigns created over this period.";
        };
        String messagePt = switch (typeNormalise) {
            case "debut" -> "Lista das campanhas que começam neste período.";
            case "fin" -> "Lista das campanhas que terminam neste período.";
            default -> "Lista das campanhas criadas neste período.";
        };

        return Result.success(campagnes, message, messageEn, messagePt);
    }

    @PostMapping("/add")
    @Transactional
    public Result addCampagne(@RequestBody CampagneRequest request, @RequestParam("clientMsisdn") String clientMsisdn){
        
        Campagnes campagne = request.getCampagne();
        List<UUID> panneauxIds = request.getPanneauxIds();
        
        if(campagne == null){
            return Result.error(400, "La campagne est obligatoire.", "The campaign is required.", "A campanha é obrigatória.");
        }

        if (campagne.getNombre() <=0) return Result.error(400,
                "Le nombre doit être supérieur à 0.",
                "The number must be greater than 0.",
                "O número deve ser superior a 0.");

        Optional<Clients> clientOpt = clientsService.findByTelephoneResponsable(clientMsisdn);
        if(clientOpt.isEmpty()){
            return Result.error(400, "Le client est obligatoire.", "The client is required.", "O cliente é obrigatório.");
        }
        
        if(campagne.getDateDebut() == null || campagne.getDateFin() == null){
            return Result.error(400,
                    "Les dates de début et de fin sont obligatoires.",
                    "The start and end dates are required.",
                    "As datas de início e de fim são obrigatórias.");
        }
        
        if(campagne.getDateDebut().after(campagne.getDateFin())){
            return Result.error(400,
                    "La date de début doit être antérieure à la date de fin.",
                    "The start date must be earlier than the end date.",
                    "A data de início deve ser anterior à data de fim.");
        }
        
        if(campagne.getNomCampagne() != null){
            Optional<Campagnes> checkCampagne = campagnesService.findByNomCampagne(campagne.getNomCampagne());
            if(checkCampagne.isPresent()){ 
                return Result.error(400,
                        "Ce nom de campagne existe déjà.",
                        "This campaign name already exists.",
                        "Este nome de campanha já existe.");
            }
        }

        if(!Objects.equals(campagne.getCycle(), "day") && !Objects.equals(campagne.getCycle(), "week") && !Objects.equals(campagne.getCycle(), "month") &&
                !Objects.equals(campagne.getCycle(), "jour") && !Objects.equals(campagne.getCycle(), "mois") && !Objects.equals(campagne.getCycle(), "semaine"))
            return Result.error(400, "Le cycle est incorrect.", "The cycle is invalid.", "O ciclo é inválido.");

        if(panneauxIds == null || panneauxIds.isEmpty()){
            return Result.error(400,
                    "Au moins un panneau est requis.",
                    "At least one billboard is required.",
                    "É necessário pelo menos um painel.");
        }
        
        for(UUID panneauId : panneauxIds){
            Optional<Panneaux> panneauOpt = panneauxService.findById(panneauId);
            if(panneauOpt.isEmpty()){
                return Result.error(400, "Le panneau avec l'identifiant " + panneauId + " n'existe pas.",
                        "The billboard with ID " + panneauId + " does not exist.",
                        "O painel com o identificador " + panneauId + " não existe.");
            }
            
            List<Campagnes> campagnesConflicts = campagnesService.findCampagnesWithPanneauInPeriod(
                panneauId, campagne.getDateDebut(), campagne.getDateFin()
            );
            
            if(!campagnesConflicts.isEmpty()){
                String conflictNames = String.join(", ", 
                    campagnesConflicts.stream()
                        .map(c -> c.getNomCampagne())
                        .toArray(String[]::new)
                );
                return Result.error(400,
                        "Le panneau " + panneauOpt.get().getReference()
                                + " est déjà occupé dans la période demandée par les campagnes : " + conflictNames,
                        "Billboard " + panneauOpt.get().getReference()
                                + " is already booked for the requested period by campaigns: " + conflictNames,
                        "O painel " + panneauOpt.get().getReference()
                                + " já está ocupado no período pedido pelas campanhas: " + conflictNames);
            }
        }

        campagne.setStatutPaiement("pending");
        campagne.setClient(clientOpt.get());
        campagne.setUser(securityUtils.getCurrentUser());
        
        Campagnes savedCampagne = campagnesService.addCampagnes(campagne);
        
        for(UUID panneauId : panneauxIds){
            PanneauxCampagne panneauCampagne = new PanneauxCampagne();
            panneauCampagne.setCampagne(savedCampagne);
            
            Panneaux panneau = panneauxService.findById(panneauId).orElse(null);
            panneauCampagne.setPanneaux(panneau);
            
            panneauxCampagneService.addPanneauxCampagne(panneauCampagne);
        }
        
        Optional<Statut> statutPropositionOpt = statutService.findByCodeStatut("proposition");
        if(statutPropositionOpt.isPresent()){
            CampagneStatut campagneStatut = new CampagneStatut();
            campagneStatut.setCampagne(savedCampagne);
            campagneStatut.setStatut(statutPropositionOpt.get());
            campagneStatut.setDateChangement(new Date());
            campagneStatut.setCommentaire("Création de la campagne");
            
            campagneStatutService.addCampagneStatut(campagneStatut);
            
            savedCampagne.setStatut("proposition");
            campagnesService.addCampagnes(savedCampagne);
        }
        
        notificationEngine.declencher(NotificationEvenements.CAMPAGNE_CREATION,
                NotificationContexte.pourCampagne(savedCampagne));

        return Result.success(savedCampagne,"Campagne créée avec succès avec " + panneauxIds.size() + " panneaux.",
                "Campaign created successfully with " + panneauxIds.size() + " billboards.",
                "Campanha criada com sucesso com " + panneauxIds.size() + " painéis.");
    }

    @GetMapping("statut/update")
    @Transactional
    public Result updateStatutCampagne(@RequestParam(name = "idCampagne") UUID idCampagne, 
                                       @RequestParam(name = "type") String type,
                                       @RequestParam(name = "commentaire", required = false) String commentaire) {

        Optional<Campagnes> campagneOpt = campagnesService.findById(idCampagne);

        if (campagneOpt.isEmpty()) {
            return Result.error(404, "Campagne introuvable.", "Campaign not found.", "Campanha não encontrada.");
        }

        if (!type.equalsIgnoreCase("validation") && !type.equalsIgnoreCase("rejet")) {
            return Result.error(400, "Type de mise à jour incorrect.", "Invalid update type.", "Tipo de atualização inválido.");
        }

        Campagnes campagne = campagneOpt.get();

        Optional<CampagneStatut> campagneStatutOpt = campagneStatutService.findByCampagneAndBtEnabled(campagne, true);

        if (campagneStatutOpt.isEmpty()) {
            return Result.error(404,
                    "Aucun statut actif trouvé pour cette campagne.",
                    "No active status found for this campaign.",
                    "Nenhum estado ativo encontrado para esta campanha.");
        }

        CampagneStatut currentCampagneStatut = campagneStatutOpt.get();
        String currentStatutCode = currentCampagneStatut.getStatut().getCodeStatut();

        // Bloquer si statut est archived ou canceled
        if (currentStatutCode.equalsIgnoreCase("archived") || currentStatutCode.equalsIgnoreCase("canceled")) {
            return Result.error(400,
                    "Impossible de modifier une campagne clôturée ou annulée.",
                    "A closed or cancelled campaign cannot be modified.",
                    "Não é possível alterar uma campanha encerrada ou cancelada.");
        }

        int currentOrdre = currentCampagneStatut.getStatut().getOrdre();
        int newOrdre = type.equalsIgnoreCase("validation") ? currentOrdre + 1 : currentOrdre - 1;

        Optional<Statut> newStatutOpt = statutService.findByOrdre(newOrdre);

        if (newStatutOpt.isEmpty()) {
            return Result.error(404,
                    "Aucun statut trouvé pour cette transition.",
                    "No status found for this transition.",
                    "Nenhum estado encontrado para esta transição.");
        }

        Statut newStatut = newStatutOpt.get();
        String newStatutCode = newStatut.getCodeStatut();

        // Interdire la transition vers "paied"
        if (newStatutCode.equalsIgnoreCase("paied")) {
            return Result.error(400,
                    "Le statut « paied » ne peut pas être défini manuellement.",
                    "The 'paied' status cannot be set manually.",
                    "O estado 'paied' não pode ser definido manualmente.");
        }

        // Désactiver l'ancien statut
        campagneStatutService.disableCampagneStatut(currentCampagneStatut);

        // Créer le nouveau statut
        CampagneStatut newCampagneStatut = new CampagneStatut();
        newCampagneStatut.setCampagne(campagne);
        newCampagneStatut.setStatut(newStatut);
        newCampagneStatut.setDateChangement(new Date());
        newCampagneStatut.setCommentaire(commentaire != null ? commentaire : "Changement de statut: " + currentStatutCode + " -> " + newStatutCode);
        newCampagneStatut.setBtEnabled(true);
        newCampagneStatut.setUser(securityUtils.getCurrentUser());

        campagneStatutService.addCampagneStatut(newCampagneStatut);

        // Mettre à jour le statut de la campagne
        campagne.setStatut(newStatutCode);
        campagnesService.addCampagnes(campagne);

        // La campagne est tenue pour validée lorsqu'elle quitte la proposition pour attendre son paiement.
        if ("pending_payment".equals(newStatutCode)) {
            notificationEngine.declencher(NotificationEvenements.CAMPAGNE_VALIDATION,
                    NotificationContexte.pourCampagne(campagne));
        }

        return Result.success(newCampagneStatut,
                "Statut de la campagne modifié avec succès.",
                "Campaign status updated successfully.",
                "Estado da campanha alterado com sucesso.");
    }

    @PutMapping("/update")
    @Transactional
    public Result updateCampagne(@RequestBody CampagneRequest request , @RequestParam("clientMsisdn") String clientMsisdn){

        Campagnes campagne = request.getCampagne();
        List<UUID> panneauxIds = request.getPanneauxIds();

        if(campagne == null || campagne.getId() == null){
            return Result.error(400,
                    "La campagne et son identifiant sont obligatoires.",
                    "The campaign and its ID are required.",
                    "A campanha e o seu identificador são obrigatórios.");
        }

        Optional<Campagnes> checkCampagneId = campagnesService.findById(campagne.getId());
        if(checkCampagneId.isEmpty()){
            return Result.error(400, "Campagne introuvable.", "Campaign not found.", "Campanha não encontrada.");
        }

        Campagnes existingCampagne = checkCampagneId.get();
        String oldStatut = existingCampagne.getStatut();

        Optional<Clients> clientOpt = clientsService.findByTelephoneResponsable(clientMsisdn);
        if(clientOpt.isEmpty()){
            return Result.error(400, "Le client est obligatoire.", "The client is required.", "O cliente é obrigatório.");
        }

        if(campagne.getDateDebut() == null || campagne.getDateFin() == null){
            return Result.error(400,
                    "Les dates de début et de fin sont obligatoires.",
                    "The start and end dates are required.",
                    "As datas de início e de fim são obrigatórias.");
        }

        if(campagne.getDateDebut().after(campagne.getDateFin())){
            return Result.error(400,
                    "La date de début doit être antérieure à la date de fin.",
                    "The start date must be earlier than the end date.",
                    "A data de início deve ser anterior à data de fim.");
        }

        if(campagne.getNomCampagne() != null){
            Optional<Campagnes> checkCampagne = campagnesService.findByNomCampagneAndIdNot(campagne.getNomCampagne(), campagne.getId());
            if(checkCampagne.isPresent()){
                return Result.error(400,
                        "Ce nom de campagne existe déjà.",
                        "This campaign name already exists.",
                        "Este nome de campanha já existe.");
            }
        }

        if(panneauxIds == null || panneauxIds.isEmpty()){
            return Result.error(400,
                    "Au moins un panneau est requis.",
                    "At least one billboard is required.",
                    "É necessário pelo menos um painel.");
        }

        for(UUID panneauId : panneauxIds){
            Optional<Panneaux> panneauOpt = panneauxService.findById(panneauId);
            if(panneauOpt.isEmpty()){
                return Result.error(400, "Le panneau avec l'identifiant " + panneauId + " n'existe pas.",
                        "The billboard with ID " + panneauId + " does not exist.",
                        "O painel com o identificador " + panneauId + " não existe.");
            }

            List<Campagnes> campagnesConflicts = campagnesService.findCampagnesWithPanneauInPeriodExcludingCampagne(
                panneauId, campagne.getDateDebut(), campagne.getDateFin(), campagne.getId()
            );

            if(!campagnesConflicts.isEmpty()){
                String conflictNames = String.join(", ",
                    campagnesConflicts.stream()
                        .map(c -> c.getNomCampagne())
                        .toArray(String[]::new)
                );
                return Result.error(400,
                        "Le panneau " + panneauOpt.get().getReference()
                                + " est déjà occupé dans la période demandée par les campagnes : " + conflictNames,
                        "Billboard " + panneauOpt.get().getReference()
                                + " is already booked for the requested period by campaigns: " + conflictNames,
                        "O painel " + panneauOpt.get().getReference()
                                + " já está ocupado no período pedido pelas campanhas: " + conflictNames);
            }
        }

        campagne.setClient(clientOpt.get());

        Campagnes savedCampagne = campagnesService.addCampagnes(campagne);

        panneauxCampagneService.deleteByCampagneId(campagne.getId());
        devisService.deleteByCampagneId(campagne.getId());
        factureService.deleteByCampagneId(campagne.getId());

        for(UUID panneauId : panneauxIds){
            PanneauxCampagne panneauCampagne = new PanneauxCampagne();
            panneauCampagne.setCampagne(savedCampagne);

            Panneaux panneau = panneauxService.findById(panneauId).orElse(null);
            panneauCampagne.setPanneaux(panneau);

            panneauxCampagneService.addPanneauxCampagne(panneauCampagne);
        }

        if(campagne.getStatut() != null && !campagne.getStatut().equals(oldStatut)){
            Optional<Statut> newStatutOpt = statutService.findByCodeStatut(campagne.getStatut());
            if(newStatutOpt.isPresent()){
                CampagneStatut campagneStatut = new CampagneStatut();
                campagneStatut.setCampagne(savedCampagne);
                campagneStatut.setStatut(newStatutOpt.get());
                campagneStatut.setDateChangement(new Date());
                campagneStatut.setCommentaire("Modification du statut: " + oldStatut + " -> " + campagne.getStatut());

                campagneStatutService.addCampagneStatut(campagneStatut);
            }
        }

        return Result.success(savedCampagne,"Campagne modifiée avec succès avec " + panneauxIds.size() + " panneaux.",
                "Campaign updated successfully with " + panneauxIds.size() + " billboards.",
                "Campanha alterada com sucesso com " + panneauxIds.size() + " painéis.");
    }

    @GetMapping("/kpi")
    public Result getKPI(@RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                         @RequestParam(value = "dateFin", required = false) String dateFinStr) {

        Date dateDebut = java.sql.Date.valueOf(LocalDate.of(2000, 1, 1));
        Date dateFin = java.sql.Date.valueOf(LocalDate.of(2100, 12, 31));

        try {
            if (dateDebutStr != null && !dateDebutStr.trim().isEmpty()) {
                LocalDate ld = LocalDate.parse(dateDebutStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                dateDebut = java.sql.Date.valueOf(ld);
            }
            if (dateFinStr != null && !dateFinStr.trim().isEmpty()) {
                LocalDate ld = LocalDate.parse(dateFinStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                dateFin = java.sql.Date.valueOf(ld);
            }
        } catch (DateTimeParseException e) {
            return Result.error(400,
                    "Format de date invalide. Utilisez uniquement « yyyy-MM-dd » (ex. 2025-01-01).",
                    "Invalid date format. Use only 'yyyy-MM-dd' (e.g. 2025-01-01).",
                    "Formato de data inválido. Utilize apenas 'yyyy-MM-dd' (ex.: 2025-01-01).");
        }

        Map<String, Object> kpi = new HashMap<>();

        Long nombreTotal = campagnesService.getNombreTotalCampagnes(dateDebut, dateFin);
        List<Object[]> campagnesParStatut = campagnesService.getCampagnesParStatut(dateDebut, dateFin);
        List<Object[]> campagnesParStatutPaiement = campagnesService.getCampagnesParStatutPaiement(dateDebut, dateFin);
        //Double dureeMoyenne = campagnesService.getDureeMoyenneCampagnes(dateDebut, dateFin);

        kpi.put("nombreTotalCampagnes", nombreTotal);
        //kpi.put("dureeMoyenneJours", dureeMoyenne);

        Map<String, Long> statuts = new HashMap<>();
        for (Object[] row : campagnesParStatut) {
            if (row[0] != null) {
                statuts.put((String) row[0], (Long) row[1]);
            }
        }
        kpi.put("campagnesParStatut", statuts);

        Map<String, Long> statutsPaiement = new HashMap<>();
        for (Object[] row : campagnesParStatutPaiement) {
            if (row[0] != null) {
                statutsPaiement.put((String) row[0], (Long) row[1]);
            }
        }
        kpi.put("campagnesParStatutPaiement", statutsPaiement);

        return Result.success(kpi, "KPI des campagnes.", "Campaign KPIs.", "KPIs das campanhas.");
    }

    @GetMapping("/kpi/client")
    public Result getKPIParClient(@RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                                  @RequestParam(value = "dateFin", required = false) String dateFinStr){

        Date dateDebut = java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1));
        Date dateFin = java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31));

        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = new SimpleDateFormat("dd-MM-yyyy").parse(dateDebutStr);
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = new SimpleDateFormat("dd-MM-yyyy").parse(dateFinStr);
            }
        } catch (Exception e) {
            return Result.error(400,
                    "Format de date invalide. Utilisez le format dd-MM-yyyy.",
                    "Invalid date format. Use the dd-MM-yyyy format.",
                    "Formato de data inválido. Utilize o formato dd-MM-yyyy.");
        }

        List<Object[]> results = campagnesService.getKPIParClient(dateDebut, dateFin);
        List<Map<String, Object>> kpiList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> clientKPI = new HashMap<>();
            clientKPI.put("clientId", row[0]);
            clientKPI.put("denominationClient", row[1]);
            clientKPI.put("nombreCampagnes", row[2]);
            clientKPI.put("dureeMoyenneJours", row[3]);
            kpiList.add(clientKPI);
        }

        return Result.success(kpiList, "KPI par client.", "KPIs by client.", "KPIs por cliente.");
    }

    @GetMapping("/kpi/client/{idClient}")
    public Result getKPIParClientId(@PathVariable("idClient") UUID idClient,
                                    @RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                                    @RequestParam(value = "dateFin", required = false) String dateFinStr){

        Date dateDebut = java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1));
        Date dateFin = java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31));

        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = new SimpleDateFormat("dd-MM-yyyy").parse(dateDebutStr);
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = new SimpleDateFormat("dd-MM-yyyy").parse(dateFinStr);
            }
        } catch (Exception e) {
            return Result.error(400,
                    "Format de date invalide. Utilisez le format dd-MM-yyyy.",
                    "Invalid date format. Use the dd-MM-yyyy format.",
                    "Formato de data inválido. Utilize o formato dd-MM-yyyy.");
        }

        Object[] result = campagnesService.getKPIParClientId(idClient, dateDebut, dateFin);

        if (result == null || result[0] == null) {
            return Result.error(404,
                    "Aucune campagne trouvée pour ce client.",
                    "No campaign found for this client.",
                    "Nenhuma campanha encontrada para este cliente.");
        }

        Map<String, Object> clientKPI = new HashMap<>();
        clientKPI.put("nombreCampagnes", result[0]);
        clientKPI.put("dureeMoyenneJours", result[1]);

        return Result.success(clientKPI, "KPI du client.", "Client KPIs.", "KPIs do cliente.");
    }

    @GetMapping("/kpi/periode")
    public Result getKPIParPeriode(@RequestParam("type") String type,
                                   @RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                                   @RequestParam(value = "dateFin", required = false) String dateFinStr){

        Date dateDebut = java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1));
        Date dateFin = java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31));

        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = new SimpleDateFormat("dd-MM-yyyy").parse(dateDebutStr);
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = new SimpleDateFormat("dd-MM-yyyy").parse(dateFinStr);
            }
        } catch (Exception e) {
            return Result.error(400,
                    "Format de date invalide. Utilisez le format dd-MM-yyyy.",
                    "Invalid date format. Use the dd-MM-yyyy format.",
                    "Formato de data inválido. Utilize o formato dd-MM-yyyy.");
        }

        List<Map<String, Object>> kpiList = new ArrayList<>();

        if ("journalier".equalsIgnoreCase(type) || "jour".equalsIgnoreCase(type)) {
            List<Object[]> results = campagnesService.getCampagnesParJour(dateDebut, dateFin);
            for (Object[] row : results) {
                Map<String, Object> periodeKPI = new HashMap<>();
                periodeKPI.put("date", row[0]);
                periodeKPI.put("nombreCampagnes", row[1]);
                kpiList.add(periodeKPI);
            }
        } else if ("mensuel".equalsIgnoreCase(type) || "mois".equalsIgnoreCase(type)) {
            List<Object[]> results = campagnesService.getCampagnesParMois(dateDebut, dateFin);
            for (Object[] row : results) {
                Map<String, Object> periodeKPI = new HashMap<>();
                periodeKPI.put("mois", row[0]);
                periodeKPI.put("nombreCampagnes", row[1]);
                kpiList.add(periodeKPI);
            }
        } else {
            return Result.error(400,
                    "Type de période invalide. Utilisez « journalier » ou « mensuel ».",
                    "Invalid period type. Use 'journalier' or 'mensuel'.",
                    "Tipo de período inválido. Utilize 'journalier' ou 'mensuel'.");
        }

        return Result.success(kpiList, "KPI par période.", "KPIs by period.", "KPIs por período.");
    }

    @GetMapping("/kpi/panneaux/utilisation")
    public Result getPanneauxAvecJoursUtilisation(@RequestParam(value = "dateDebut", required = false) String dateDebutStr,
                                                  @RequestParam(value = "dateFin", required = false) String dateFinStr){

        Date dateDebut = java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1));
        Date dateFin = java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31));

        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = new SimpleDateFormat("dd-MM-yyyy").parse(dateDebutStr);
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = new SimpleDateFormat("dd-MM-yyyy").parse(dateFinStr);
            }
        } catch (Exception e) {
            return Result.error(400,
                    "Format de date invalide. Utilisez le format dd-MM-yyyy.",
                    "Invalid date format. Use the dd-MM-yyyy format.",
                    "Formato de data inválido. Utilize o formato dd-MM-yyyy.");
        }

        List<Object[]> results = campagnesService.getPanneauxAvecJoursUtilisation(dateDebut, dateFin);
        List<Map<String, Object>> panneauxList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> panneauInfo = new HashMap<>();
            panneauInfo.put("panneauId", row[0]);
            panneauInfo.put("code", row[1]);
            panneauInfo.put("nomPanneau", row[2]);
            panneauInfo.put("totalJoursUtilisation", row[3]);
            panneauxList.add(panneauInfo);
        }

        return Result.success(panneauxList,
                "Panneaux avec jours d'utilisation, du plus utilisé au moins utilisé.",
                "Billboards with usage days, from most to least used.",
                "Painéis com dias de utilização, do mais ao menos utilizado.");
    }
}
