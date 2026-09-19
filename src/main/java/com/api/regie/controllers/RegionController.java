package com.api.regie.controllers;

import com.api.regie.models.Region;
import com.api.regie.models.Result;
import com.api.regie.services.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/region")
public class RegionController {


    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("liste")
    public Result getAllRegions(){return Result.success(regionService.getAllRegions(),
            "Liste des régions.",
            "List of regions.",
            "Lista das regiões.");}

    @GetMapping("getbyid")
    public Result getRegionsById(@RequestParam("idRegion")UUID idRegion){return Result.success(regionService.findById(idRegion),
            "Les informations de la région.",
            "Region details.",
            "Detalhes da região.");}

    @PostMapping("/add")
    public Result addRegion(@RequestBody Region region){

        Optional<Region> checkRegion = regionService.findByRegion(region.getRegion());

        if(checkRegion.isPresent()){ return Result.error(400,
                "Cette région existe déjà.",
                "This region already exists.",
                "Esta região já existe.");}

        return Result.success(regionService.addRegion(region),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateRegion(@RequestBody Region region){

        Optional<Region> checkRegionId = regionService.findById(region.getId());

        if(checkRegionId.isEmpty()){return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");}

        Optional<Region> checkRegion = regionService.findByRegionAndIdNot(region.getRegion(), region.getId());

        if(checkRegion.isPresent()){return Result.error(400,
                "Cette région existe déjà.",
                "This region already exists.",
                "Esta região já existe.");}

        return Result.success(regionService.addRegion(region),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
