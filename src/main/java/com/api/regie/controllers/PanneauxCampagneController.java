package com.api.regie.controllers;

import com.api.regie.models.PanneauxCampagne;
import com.api.regie.models.Result;
import com.api.regie.services.PanneauxCampagneService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/panneaux-campagne")
public class PanneauxCampagneController {

    private final PanneauxCampagneService panneauxCampagneService;

    public PanneauxCampagneController(PanneauxCampagneService panneauxCampagneService) {
        this.panneauxCampagneService = panneauxCampagneService;
    }

    @GetMapping("liste")
    public Result getAllPanneauxCampagnes(){
        return Result.success(panneauxCampagneService.getAllPanneauxCampagnes(),
                "Liste des associations panneau-campagne.",
                "List of billboard-campaign associations.",
                "Lista das associações painel-campanha.");
    }

    @GetMapping("getbyid")
    public Result getPanneauxCampagneById(@RequestParam("idPanneauxCampagne") UUID idPanneauxCampagne){
        return Result.success(panneauxCampagneService.findById(idPanneauxCampagne),
                "Les informations de l'association.",
                "Association details.",
                "Detalhes da associação.");
    }

    @GetMapping("getbycampagne")
    public Result getPanneauxByCampagne(@RequestParam("idCampagne") UUID idCampagne){
        return Result.success(panneauxCampagneService.findByCampagneId(idCampagne),
                "Liste des panneaux de la campagne.",
                "List of the campaign's billboards.",
                "Lista dos painéis da campanha.");
    }

    @GetMapping("getbypanneau")
    public Result getCampagnesByPanneau(@RequestParam("idPanneau") UUID idPanneau){
        return Result.success(panneauxCampagneService.findByPanneauxId(idPanneau),
                "Liste des campagnes utilisant ce panneau.",
                "List of campaigns using this billboard.",
                "Lista das campanhas que utilizam este painel.");
    }

    @PostMapping("/add")
    public Result addPanneauxCampagne(@RequestBody PanneauxCampagne panneauxCampagne){

        // Validation: campagne obligatoire
        if(panneauxCampagne.getCampagne() == null || panneauxCampagne.getCampagne().getId() == null){
            return Result.error(400, "La campagne est obligatoire.", "The campaign is required.", "A campanha é obrigatória.");
        }

        // Validation: panneau obligatoire
        if(panneauxCampagne.getPanneaux() == null || panneauxCampagne.getPanneaux().getId() == null){
            return Result.error(400, "Le panneau est obligatoire.", "The billboard is required.", "O painel é obrigatório.");
        }

        // Validation: éviter les doublons
        Optional<PanneauxCampagne> checkExist = panneauxCampagneService.findByCampagneIdAndPanneauxId(
            panneauxCampagne.getCampagne().getId(),
            panneauxCampagne.getPanneaux().getId()
        );

        if(checkExist.isPresent()){
            return Result.error(400,
                    "Ce panneau est déjà associé à cette campagne.",
                    "This billboard is already linked to this campaign.",
                    "Este painel já está associado a esta campanha.");
        }

        return Result.success(panneauxCampagneService.addPanneauxCampagne(panneauxCampagne),
                "Association effectuée avec succès.",
                "Association created successfully.",
                "Associação efetuada com sucesso.");
    }

    @PutMapping("/update")
    public Result updatePanneauxCampagne(@RequestBody PanneauxCampagne panneauxCampagne){

        Optional<PanneauxCampagne> checkPanneauxCampagneId = panneauxCampagneService.findById(panneauxCampagne.getId());

        if(checkPanneauxCampagneId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        return Result.success(panneauxCampagneService.addPanneauxCampagne(panneauxCampagne),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
