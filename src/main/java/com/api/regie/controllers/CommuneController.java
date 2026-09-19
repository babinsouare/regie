package com.api.regie.controllers;

import com.api.regie.models.Commune;
import com.api.regie.models.Region;
import com.api.regie.models.Result;
import com.api.regie.services.CommuneService;
import com.api.regie.services.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("commune")
public class CommuneController {

    private final CommuneService communeService;
    private final RegionService regionService;

    public CommuneController(CommuneService communeService, RegionService regionService) {
        this.communeService = communeService;
        this.regionService = regionService;
    }

    @GetMapping("liste")
    public Result listeCommune(){return Result.success(communeService.getAllCommune(),
            "Liste des communes.",
            "List of municipalities.",
            "Lista dos municípios.");}

    @GetMapping("getbyid")
    public Result getById(@RequestParam("idCommune") UUID idCommune){return Result.success(communeService.findById(idCommune),
            "Détail de la commune.",
            "Municipality details.",
            "Detalhes do município.");}

    @PostMapping("add")
    public Result addCommune(@RequestBody Commune commune, @RequestParam("idRegion") UUID idRegion){

        Optional<Commune> checkIfCommuneExist = communeService.findByCommune(commune.getCommune());
        if(checkIfCommuneExist.isPresent()){ return Result.error(400,
                "Cette commune existe déjà.",
                "This municipality already exists.",
                "Este município já existe.");}

        Optional<Region> checkRegion = regionService.findById(idRegion);
        if(checkRegion.isEmpty()) return Result.error(400,
                "Cette région n'existe pas.",
                "This region does not exist.",
                "Esta região não existe.");

        commune.setRegion(checkRegion.get());

        return Result.success(communeService.addCommune(commune),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");

    }

    @PutMapping("edit")
    public Result editCommune(@RequestBody Commune commune, @RequestParam("idRegion") UUID idRegion){
        Optional<Commune> checkId = communeService.findById(commune.getId());
        if(checkId.isEmpty()){ return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos."); }

        Optional<Commune> checkCommune = communeService.findByCommuneAndIdNot(commune.getCommune(), commune.getId());
        if(checkCommune.isPresent()) return Result.error(400,
                "Cette commune existe déjà.",
                "This municipality already exists.",
                "Este município já existe.");

        Optional<Region> checkRegion = regionService.findById(idRegion);
        if(checkRegion.isEmpty()) return Result.error(400,
                "Cette région n'existe pas.",
                "This region does not exist.",
                "Esta região não existe.");

        commune.setRegion(checkRegion.get());
        return Result.success(communeService.addCommune(commune),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");

    }

    @GetMapping("region")
    public Result listeCommuneParRegion(@RequestParam("idRegion") UUID idRegion){
        Optional<Region> checkRegion = regionService.findById(idRegion);
        if(checkRegion.isEmpty()) return Result.error(400,
                "Cette région n'existe pas.",
                "This region does not exist.",
                "Esta região não existe.");

        return Result.success(communeService.findByRegionId(idRegion),
                "Liste des communes de la région.",
                "List of the region's municipalities.",
                "Lista dos municípios da região.");
    }

}
