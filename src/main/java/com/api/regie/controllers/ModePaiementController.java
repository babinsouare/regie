package com.api.regie.controllers;

import com.api.regie.models.ModePaiement;
import com.api.regie.models.Result;
import com.api.regie.services.ModePaiementService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/modepaiement")
public class ModePaiementController {

    private final ModePaiementService modePaiementService;

    public ModePaiementController(ModePaiementService modePaiementService) {
        this.modePaiementService = modePaiementService;
    }

    @GetMapping("liste")
    public Result getAllModePaiements(){
        return Result.success(modePaiementService.getAllModePaiements(),
                "Liste des modes de paiement.",
                "List of payment methods.",
                "Lista dos métodos de pagamento.");
    }

    @GetMapping("getbyid")
    public Result getModePaiementsById(@RequestParam("idModePaiement")UUID idModePaiement){
        return Result.success(modePaiementService.findById(idModePaiement),
                "Les informations du mode de paiement.",
                "Payment method details.",
                "Detalhes do método de pagamento.");
    }

    @PostMapping("/add")
    public Result addModePaiement(@RequestBody ModePaiement modePaiement){

        Optional<ModePaiement> checkModePaiement = modePaiementService.findByMode(modePaiement.getMode());

        if(checkModePaiement.isPresent()){ 
            return Result.error(400,
                    "Ce mode de paiement existe déjà.",
                    "This payment method already exists.",
                    "Este método de pagamento já existe.");
        }

        return Result.success(modePaiementService.addModePaiement(modePaiement),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateModePaiement(@RequestBody ModePaiement modePaiement){

        Optional<ModePaiement> checkModePaiementId = modePaiementService.findById(modePaiement.getId());

        if(checkModePaiementId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<ModePaiement> checkModePaiement = modePaiementService.findByModeAndIdNot(modePaiement.getMode(), modePaiement.getId());

        if(checkModePaiement.isPresent()){
            return Result.error(400,
                    "Ce mode de paiement existe déjà.",
                    "This payment method already exists.",
                    "Este método de pagamento já existe.");
        }

        return Result.success(modePaiementService.addModePaiement(modePaiement),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}