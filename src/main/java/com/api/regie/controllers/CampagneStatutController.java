package com.api.regie.controllers;

import com.api.regie.models.Result;
import com.api.regie.services.CampagneStatutService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/campagne-statut")
public class CampagneStatutController {

    private final CampagneStatutService campagneStatutService;

    public CampagneStatutController(CampagneStatutService campagneStatutService) {
        this.campagneStatutService = campagneStatutService;
    }

    @GetMapping("liste")
    public Result getAllCampagneStatuts(){
        return Result.success(campagneStatutService.getAllCampagneStatuts(),
                "Liste des statuts de campagne.",
                "List of campaign statuses.",
                "Lista dos estados de campanha.");
    }

    @GetMapping("getbyid")
    public Result getCampagneStatutById(@RequestParam("idCampagneStatut") UUID idCampagneStatut){
        return Result.success(campagneStatutService.findById(idCampagneStatut),
                "Les informations du statut de campagne.",
                "Campaign status details.",
                "Detalhes do estado de campanha.");
    }

    @GetMapping("getbycampagne")
    public Result getCampagneStatutsByCampagne(@RequestParam("idCampagne") UUID idCampagne){
        return Result.success(campagneStatutService.findByCampagneId(idCampagne),
                "Liste des statuts de la campagne.",
                "List of the campaign's statuses.",
                "Lista dos estados da campanha.");
    }

    @GetMapping("historique-campagne")
    public Result getHistoriqueCampagne(@RequestParam("idCampagne") UUID idCampagne){
        return Result.success(campagneStatutService.findByCampagneIdOrderByDateChangementDesc(idCampagne),
                "Historique des statuts de la campagne.",
                "Campaign status history.",
                "Histórico dos estados da campanha.");
    }

    @GetMapping("getbystatut")
    public Result getCampagneStatutsByStatut(@RequestParam("idStatut") UUID idStatut){
        return Result.success(campagneStatutService.findByStatutId(idStatut),
                "Liste des campagnes avec ce statut.",
                "List of campaigns with this status.",
                "Lista das campanhas com este estado.");
    }

    @GetMapping("getbyuser")
    public Result getCampagneStatutsByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(campagneStatutService.findByUserId(idUser),
                "Liste des changements de statut effectués par cet utilisateur.",
                "List of status changes made by this user.",
                "Lista das alterações de estado efetuadas por este utilizador.");
    }

}
