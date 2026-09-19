package com.api.regie.controllers;

import com.api.regie.models.*;
import com.api.regie.services.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/devis")
public class DevisController {

    private final DevisService devisService;
    private final CampagnesService campagnesService;
    private final RemiseService remiseService;

    public DevisController(DevisService devisService, 
                          CampagnesService campagnesService,
                          RemiseService remiseService) {
        this.devisService = devisService;
        this.campagnesService = campagnesService;
        this.remiseService = remiseService;
    }

    @GetMapping("liste")
    public Result getAllDevis(){
        return Result.success(devisService.getAllDevis(), "Liste des devis.", "List of quotes.", "Lista dos orçamentos.");
    }

    @GetMapping("getbyid")
    public Result getDevisById(@RequestParam("idDevis") UUID idDevis){
        return Result.success(devisService.findById(idDevis),
                "Les informations du devis.",
                "Quote details.",
                "Detalhes do orçamento.");
    }

    @GetMapping("getbyclient")
    public Result getDevisByClient(@RequestParam("idClient") UUID idClient){
        return Result.success(devisService.findByClientId(idClient),
                "Liste des devis du client.",
                "List of the client's quotes.",
                "Lista dos orçamentos do cliente.");
    }

    @GetMapping("getbyuser")
    public Result getDevisByUser(@RequestParam("idUser") UUID idUser){
        return Result.success(devisService.findByUserId(idUser),
                "Liste des devis de l'utilisateur.",
                "List of the user's quotes.",
                "Lista dos orçamentos do utilizador.");
    }

    @PostMapping("/add")
    @Transactional
    public Result addDevis(@RequestParam("idCampagne") UUID idCampagne,
                           @RequestParam(value = "idRemise", required = false) UUID idRemise,@RequestParam(value = "dateValidite", required = false)  Date dateValidite){
        try {
            Devis devis = devisService.createDevisForCampagne(idCampagne, idRemise, dateValidite);
            return Result.success(devis,
                    "Devis créé avec succès.",
                    "Quote created successfully.",
                    "Orçamento criado com sucesso.");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage(), e.getMessage(), e.getMessage());
        }
    }

    @PutMapping("/update")
    @Transactional
    public Result updateDevis(@RequestParam("idDevis") UUID idDevis,
                              @RequestParam(value = "dateValidite", required = false)  Date dateValidite,
                              @RequestParam(value = "observations", required = false) String observations,
                              @RequestParam(value = "idRemise", required = false) UUID idRemise){
        try {
            Devis devis = devisService.updateDevis(idDevis, dateValidite, observations, idRemise);
            return Result.success(devis,
                    "Devis mis à jour avec succès.",
                    "Quote updated successfully.",
                    "Orçamento atualizado com sucesso.");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage(), e.getMessage(), e.getMessage());
        }
    }

}
