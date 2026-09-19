package com.api.regie.controllers;

import com.api.regie.models.*;
import com.api.regie.services.EvenementService;
import com.api.regie.services.PanneauxService;
import com.api.regie.services.TypeEvenementService;
import com.api.regie.services.UsersService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/evenement")
public class EvenementController {

    private final EvenementService evenementService;
    private final UsersService usersService;
    private final PanneauxService panneauxService;
    private final TypeEvenementService typeEvenementService;

    public EvenementController(EvenementService evenementService, UsersService usersService, PanneauxService panneauxService, TypeEvenementService typeEvenementService) {
        this.evenementService = evenementService;
        this.usersService = usersService;
        this.panneauxService = panneauxService;
        this.typeEvenementService = typeEvenementService;
    }

    @GetMapping("liste")
    public Result getAllEvenements(){
        return Result.success(evenementService.getAllEvenements(),
                "Liste des événements.",
                "List of events.",
                "Lista dos eventos.");
    }

    @GetMapping("getbyid")
    public Result getEvenementById(@RequestParam("idEvenement") UUID idEvenement){
        return Result.success(evenementService.findById(idEvenement),
                "Les informations de l'événement.",
                "Event details.",
                "Detalhes do evento.");
    }

    @GetMapping("getbypanneau")
    public Result getEvenementsByPanneau(@RequestParam("idPanneau") UUID idPanneau){
        return Result.success(evenementService.findByPanneauId(idPanneau),
                "Liste des événements du panneau.",
                "List of the billboard's events.",
                "Lista dos eventos do painel.");
    }

    @GetMapping("getbytype")
    public Result getEvenementsByType(@RequestParam("idTypeEvenement") UUID idTypeEvenement){
        return Result.success(evenementService.findByTypeEvenementId(idTypeEvenement),
                "Liste des événements de ce type.",
                "List of events of this type.",
                "Lista dos eventos deste tipo.");
    }

    @GetMapping("getbyuser")
    public Result getEvenementsByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(evenementService.findByUserId(idUser),
                "Liste des événements enregistrés par cet utilisateur.",
                "List of events recorded by this user.",
                "Lista dos eventos registados por este utilizador.");
    }

    @GetMapping("stats/par-agent")
    public Result getStatsParAgent(
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) return periodeInvalide();

        List<Map<String, Object>> repartition = new ArrayList<>();
        long totalEvenements = 0;
        double coutTotal = 0;

        for (Object[] row : evenementService.aggregateParAgent(dateDebut, dateFin)) {
            Map<String, Object> agent = new LinkedHashMap<>();
            agent.put("idAgent", row[0]);
            agent.put("nom", row[1]);
            agent.put("prenom", row[2]);
            agent.put("nombreEvenements", row[3]);
            agent.put("coutTotal", row[4]);
            repartition.add(agent);

            totalEvenements += ((Number) row[3]).longValue();
            coutTotal += ((Number) row[4]).doubleValue();
        }

        return Result.success(enveloppe(dateDebut, dateFin, totalEvenements, coutTotal, repartition),
                "Répartition des événements par agent.",
                "Breakdown of events by agent.",
                "Repartição dos eventos por agente.");
    }

    @GetMapping("stats/par-type")
    public Result getStatsParType(
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) return periodeInvalide();

        List<Map<String, Object>> repartition = new ArrayList<>();
        long totalEvenements = 0;
        double coutTotal = 0;

        for (Object[] row : evenementService.aggregateParType(dateDebut, dateFin)) {
            Map<String, Object> type = new LinkedHashMap<>();
            type.put("idTypeEvenement", row[0]);
            type.put("type", row[1]);
            type.put("nombreEvenements", row[2]);
            type.put("coutTotal", row[3]);
            repartition.add(type);

            totalEvenements += ((Number) row[2]).longValue();
            coutTotal += ((Number) row[3]).doubleValue();
        }

        return Result.success(enveloppe(dateDebut, dateFin, totalEvenements, coutTotal, repartition),
                "Répartition des événements par type.",
                "Breakdown of events by type.",
                "Repartição dos eventos por tipo.");
    }

    @GetMapping("stats/par-agent-type")
    public Result getStatsParAgentEtType(
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin){

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) return periodeInvalide();

        // Les lignes arrivent triées par agent : on les replie en un bloc par agent,
        // portant sa propre ventilation par type d'événement.
        Map<UUID, Map<String, Object>> parAgent = new LinkedHashMap<>();
        long totalEvenements = 0;
        double coutTotal = 0;

        for (Object[] row : evenementService.aggregateParAgentEtType(dateDebut, dateFin)) {
            UUID idAgent = (UUID) row[0];
            long nombre = ((Number) row[5]).longValue();
            double cout = ((Number) row[6]).doubleValue();

            Map<String, Object> agent = parAgent.get(idAgent);
            if (agent == null) {
                agent = new LinkedHashMap<>();
                agent.put("idAgent", idAgent);
                agent.put("nom", row[1]);
                agent.put("prenom", row[2]);
                agent.put("nombreEvenements", 0L);
                agent.put("coutTotal", 0.0);
                agent.put("types", new ArrayList<Map<String, Object>>());
                parAgent.put(idAgent, agent);
            }

            Map<String, Object> type = new LinkedHashMap<>();
            type.put("idTypeEvenement", row[3]);
            type.put("type", row[4]);
            type.put("nombreEvenements", nombre);
            type.put("coutTotal", cout);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> types = (List<Map<String, Object>>) agent.get("types");
            types.add(type);

            agent.put("nombreEvenements", ((Number) agent.get("nombreEvenements")).longValue() + nombre);
            agent.put("coutTotal", ((Number) agent.get("coutTotal")).doubleValue() + cout);

            totalEvenements += nombre;
            coutTotal += cout;
        }

        return Result.success(enveloppe(dateDebut, dateFin, totalEvenements, coutTotal,
                        new ArrayList<>(parAgent.values())),
                "Répartition des événements par agent et par type.",
                "Breakdown of events by agent and by type.",
                "Repartição dos eventos por agente e por tipo.");
    }

    private Result periodeInvalide() {
        return Result.error(400,
                "La date de début doit précéder la date de fin.",
                "The start date must be before the end date.",
                "A data de início deve ser anterior à data de fim.");
    }

    private Map<String, Object> enveloppe(Date dateDebut, Date dateFin, long totalEvenements,
                                          double coutTotal, List<Map<String, Object>> repartition) {
        Map<String, Object> periode = new LinkedHashMap<>();
        periode.put("dateDebut", dateDebut);
        periode.put("dateFin", dateFin);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("periode", periode);
        response.put("totalEvenements", totalEvenements);
        response.put("coutTotal", coutTotal);
        response.put("repartition", repartition);

        return response;
    }

    @PostMapping("/add")
    public Result addEvenement(@RequestBody Evenement evenement, @RequestParam("idUser") UUID idUser, @RequestParam("idPanneau") UUID idPanneau, @RequestParam("idTypeEvenement") UUID idTypeEvenement){

        Optional<Users> checkUser = usersService.findById(idUser);
        if(checkUser.isEmpty()){
            return Result.error(400, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");
        }

        Optional<TypeEvenement> typeEvenement = typeEvenementService.findById(idTypeEvenement);
        if(typeEvenement.isEmpty()){
            return Result.error(400, "Type d'événement introuvable.", "Event type not found.", "Tipo de evento não encontrado.");
        }

        Optional<Panneaux> panneaux = panneauxService.findById(idPanneau);
        if(panneaux.isEmpty()){
            return Result.error(400, "Panneau introuvable.", "Billboard not found.", "Painel não encontrado.");
        }

        evenement.setUser(checkUser.get());
        evenement.setPanneau(panneaux.get());
        evenement.setTypeEvenement(typeEvenement.get());

        return Result.success(evenementService.addEvenement(evenement),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateEvenement(@RequestBody Evenement evenement, @RequestParam("idUser") UUID idUser, @RequestParam("idPanneau") UUID idPanneau, @RequestParam("idTypeEvenement") UUID idTypeEvenement){

        Optional<Evenement> checkEvenementId = evenementService.findById(evenement.getId());

        if(checkEvenementId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<Users> checkUser = usersService.findById(idUser);
        if(checkUser.isEmpty()){
            return Result.error(400, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");
        }

        Optional<Panneaux> panneaux = panneauxService.findById(idPanneau);
        if(panneaux.isEmpty()){
            return Result.error(400, "Panneau introuvable.", "Billboard not found.", "Painel não encontrado.");
        }

        Optional<TypeEvenement> typeEvenement = typeEvenementService.findById(idTypeEvenement);
        if(typeEvenement.isEmpty()){
            return Result.error(400, "Type d'événement introuvable.", "Event type not found.", "Tipo de evento não encontrado.");
        }

        evenement.setUser(checkUser.get());
        evenement.setPanneau(panneaux.get());
        evenement.setTypeEvenement(typeEvenement.get());

        return Result.success(evenementService.addEvenement(evenement),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }


}
