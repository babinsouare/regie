package com.api.regie.controllers;

import com.api.regie.models.Commune;
import com.api.regie.models.Quartier;
import com.api.regie.models.Result;
import com.api.regie.services.CommuneService;
import com.api.regie.services.QuartierService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/quartier")
public class QuartierController {

    private final QuartierService quartierService;
    private final CommuneService communeService;

    public QuartierController(QuartierService quartierService, CommuneService communeService) {
        this.quartierService = quartierService;
        this.communeService = communeService;
    }

    @GetMapping("liste")
    public Result getAllQuartiers(){
        return Result.success(quartierService.getAllQuartier(),
                "Liste des quartiers.",
                "List of districts.",
                "Lista dos bairros.");
    }

    @GetMapping("getbyid")
    public Result getQuartierById(@RequestParam("idQuartier") UUID idQuartier){
        return Result.success(quartierService.findById(idQuartier),
                "Détail du quartier.",
                "District details.",
                "Detalhes do bairro.");
    }

    @PostMapping("add")
    public Result addQuartier(@RequestBody Quartier quartier, @RequestParam("idCommune") UUID idCommune){
        
        Optional<Commune> checkCommune = communeService.findById(idCommune);
        if(checkCommune.isEmpty()) return Result.error(400,
                "Cette commune n'existe pas.",
                "This municipality does not exist.",
                "Este município não existe.");

        Optional<Quartier> checkQuartierDuplication = quartierService.findByQuartierAndCommune(quartier.getQuartier(), checkCommune.get());

        if(checkQuartierDuplication.isPresent()){
            return Result.error(400, "Ce quartier existe déjà.", "This district already exists.", "Este bairro já existe.");
        }

        quartier.setCommune(checkCommune.get());
        
        return Result.success(quartierService.addQuartier(quartier),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("edit")
    public Result editQuartier(@RequestBody Quartier quartier, @RequestParam("idCommune") UUID idCommune){
        Optional<Quartier> checkId = quartierService.findById(quartier.getId());
        if(checkId.isEmpty()){ return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos."); }

        Optional<Commune> commune = communeService.findById(idCommune);
        if(commune.isEmpty()){ return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos."); }

        Optional<Quartier> checkQuartier = quartierService.findByQuartierAndIdNotAndCommune(quartier.getQuartier(), quartier.getId(), commune.get());
        if(checkQuartier.isPresent()) return Result.error(400,
                "Ce quartier existe déjà.",
                "This district already exists.",
                "Este bairro já existe.");

        quartier.setCommune(commune.get());

        return Result.success(quartierService.addQuartier(quartier),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

    @GetMapping("commune")
    public Result listeQuartierParCommune(@RequestParam("idCommune") UUID idCommune){
        Optional<Commune> checkCommune = communeService.findById(idCommune);
        if(checkCommune.isEmpty()) return Result.error(400,
                "Cette commune n'existe pas.",
                "This municipality does not exist.",
                "Este município não existe.");

        return Result.success(quartierService.findByCommuneId(idCommune),
                "Liste des quartiers de la commune.",
                "List of the municipality's districts.",
                "Lista dos bairros do município.");
    }
}