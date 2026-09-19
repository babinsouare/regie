package com.api.regie.controllers;

import com.api.regie.models.Statut;
import com.api.regie.models.Result;
import com.api.regie.services.StatutService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/statut")
public class StatutController {

    private final StatutService statutService;

    public StatutController(StatutService statutService) {
        this.statutService = statutService;
    }

    @GetMapping("liste")
    public Result getAllStatuts(){
        return Result.success(statutService.getAllStatuts(), "Liste des statuts.", "List of statuses.", "Lista dos estados.");
    }

    @GetMapping("liste-ordonnee")
    public Result getStatutsOrdonnés(){
        return Result.success(statutService.findAllByOrderByOrdreAsc(),
                "Liste des statuts ordonnés.",
                "List of statuses in order.",
                "Lista dos estados ordenados.");
    }

    @GetMapping("getbyid")
    public Result getStatutById(@RequestParam("idStatut") UUID idStatut){
        return Result.success(statutService.findById(idStatut),
                "Les informations du statut.",
                "Status details.",
                "Detalhes do estado.");
    }

    @GetMapping("actifs")
    public Result getStatutsActifs(){
        return Result.success(statutService.findByBtEnabled(true),
                "Liste des statuts actifs.",
                "List of active statuses.",
                "Lista dos estados ativos.");
    }

    @PostMapping("/add")
    public Result addStatut(@RequestBody Statut statut){

        if(statut.getCodeStatut() != null){
            Optional<Statut> checkStatut = statutService.findByCodeStatut(statut.getCodeStatut());

            if(checkStatut.isPresent()){ 
                return Result.error(400,
                        "Ce code de statut existe déjà.",
                        "This status code already exists.",
                        "Este código de estado já existe.");
            }
        }

        return Result.success(statutService.addStatut(statut),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateStatut(@RequestBody Statut statut){

        Optional<Statut> checkStatutId = statutService.findById(statut.getId());

        if(checkStatutId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        if(statut.getCodeStatut() != null){
            Optional<Statut> checkStatut = statutService.findByCodeStatutAndIdNot(statut.getCodeStatut(), statut.getId());

            if(checkStatut.isPresent()){
                return Result.error(400,
                        "Ce code de statut existe déjà.",
                        "This status code already exists.",
                        "Este código de estado já existe.");
            }
        }

        return Result.success(statutService.addStatut(statut),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
