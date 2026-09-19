package com.api.regie.controllers;

import com.api.regie.models.TypeEvenement;
import com.api.regie.models.Result;
import com.api.regie.services.TypeEvenementService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/typeevenement")
public class TypeEvenementController {

    private final TypeEvenementService typeEvenementService;

    public TypeEvenementController(TypeEvenementService typeEvenementService) {
        this.typeEvenementService = typeEvenementService;
    }

    @GetMapping("liste")
    public Result getAllTypeEvenements(){
        return Result.success(typeEvenementService.getAllTypeEvenements(),
                "Liste des types d'événements.",
                "List of event types.",
                "Lista dos tipos de eventos.");
    }

    @GetMapping("getbyid")
    public Result getTypeEvenementsById(@RequestParam("idTypeEvenement")UUID idTypeEvenement){
        return Result.success(typeEvenementService.findById(idTypeEvenement),
                "Les informations du type d'événement.",
                "Event type details.",
                "Detalhes do tipo de evento.");
    }

    @PostMapping("/add")
    public Result addTypeEvenement(@RequestBody TypeEvenement typeEvenement){

        Optional<TypeEvenement> checkTypeEvenement = typeEvenementService.findByType(typeEvenement.getType());

        if(checkTypeEvenement.isPresent()){ 
            return Result.error(400,
                    "Ce type d'événement existe déjà.",
                    "This event type already exists.",
                    "Este tipo de evento já existe.");
        }

        return Result.success(typeEvenementService.addTypeEvenement(typeEvenement),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateTypeEvenement(@RequestBody TypeEvenement typeEvenement){

        Optional<TypeEvenement> checkTypeEvenementId = typeEvenementService.findById(typeEvenement.getId());

        if(checkTypeEvenementId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<TypeEvenement> checkTypeEvenement = typeEvenementService.findByTypeAndIdNot(typeEvenement.getType(), typeEvenement.getId());

        if(checkTypeEvenement.isPresent()){
            return Result.error(400,
                    "Ce type d'événement existe déjà.",
                    "This event type already exists.",
                    "Este tipo de evento já existe.");
        }

        return Result.success(typeEvenementService.addTypeEvenement(typeEvenement),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}