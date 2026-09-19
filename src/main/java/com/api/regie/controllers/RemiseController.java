package com.api.regie.controllers;

import com.api.regie.models.Remise;
import com.api.regie.models.Result;
import com.api.regie.services.RemiseService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/remise")
public class RemiseController {

    private final RemiseService remiseService;

    public RemiseController(RemiseService remiseService) {
        this.remiseService = remiseService;
    }

    @GetMapping("liste")
    public Result getAllRemises(){
        return Result.success(remiseService.getAllRemises(), "Liste des remises.", "List of discounts.", "Lista dos descontos.");
    }

    @GetMapping("getbyid")
    public Result getRemisesById(@RequestParam("idRemise")UUID idRemise){
        return Result.success(remiseService.findById(idRemise),
                "Les informations de la remise.",
                "Discount details.",
                "Detalhes do desconto.");
    }

    @PostMapping("/add")
    public Result addRemise(@RequestBody Remise remise){

        Optional<Remise> checkRemise = remiseService.findByTypeRemise(remise.getTypeRemise());

        if(checkRemise.isPresent()){ 
            return Result.error(400, "Cette remise existe déjà.", "This discount already exists.", "Este desconto já existe.");
        }

        return Result.success(remiseService.addRemise(remise),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateRemise(@RequestBody Remise remise){

        Optional<Remise> checkRemiseId = remiseService.findById(remise.getId());

        if(checkRemiseId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<Remise> checkRemise = remiseService.findByTypeRemiseAndIdNot(remise.getTypeRemise(), remise.getId());

        if(checkRemise.isPresent()){
            return Result.error(400, "Cette remise existe déjà.", "This discount already exists.", "Este desconto já existe.");
        }

        return Result.success(remiseService.addRemise(remise),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}