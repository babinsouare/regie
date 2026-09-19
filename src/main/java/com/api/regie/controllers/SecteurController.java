package com.api.regie.controllers;

import com.api.regie.models.Quartier;
import com.api.regie.models.Result;
import com.api.regie.models.Secteur;
import com.api.regie.services.QuartierService;
import com.api.regie.services.SecteurService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/secteur")
public class SecteurController {

    private final SecteurService secteurService;
    private final QuartierService quartierService;

    public SecteurController(SecteurService secteurService, QuartierService quartierService) {
        this.secteurService = secteurService;
        this.quartierService = quartierService;
    }

    @GetMapping("liste")
    public Result getAllSecteurs(){
        return Result.success(secteurService.getAllSecteur(), "Liste des secteurs.", "List of sectors.", "Lista dos setores.");
    }

    @GetMapping("getbyid")
    public Result getSecteurById(@RequestParam("idSecteur") UUID idSecteur){
        return Result.success(secteurService.findById(idSecteur), "Détail du secteur.", "Sector details.", "Detalhes do setor.");
    }

    @PostMapping("add")
    public Result addSecteur(@RequestBody Secteur secteur, @RequestParam("idQuartier") UUID idQuartier){

        Optional<Secteur> checkSecteur = secteurService.findBySecteur(secteur.getSecteur());

        if(checkSecteur.isPresent()){ 
            return Result.error(400, "Ce secteur existe déjà.", "This sector already exists.", "Este setor já existe.");
        }
        
        Optional<Quartier> checkQuartier = quartierService.findById(idQuartier);
        if(checkQuartier.isEmpty()) return Result.error(400,
                "Ce quartier n'existe pas.",
                "This district does not exist.",
                "Este bairro não existe.");

        secteur.setQuartier(checkQuartier.get());
        
        return Result.success(secteurService.addSecteur(secteur),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("edit")
    public Result editSecteur(@RequestBody Secteur secteur, @RequestParam(value = "idQuartier") UUID idQuartier){
        Optional<Secteur> checkId = secteurService.findById(secteur.getId());
        if(checkId.isEmpty()){ return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos."); }

        Optional<Secteur> checkSecteur = secteurService.findBySecteurAndIdNot(secteur.getSecteur(), secteur.getId());
        if(checkSecteur.isPresent()) return Result.error(400,
                "Ce secteur existe déjà.",
                "This sector already exists.",
                "Este setor já existe.");

        Optional<Quartier> checkQuartier = quartierService.findById(idQuartier);
        if(checkQuartier.isEmpty()) return Result.error(400,
                "Ce quartier n'existe pas.",
                "This district does not exist.",
                "Este bairro não existe.");
        secteur.setQuartier(checkQuartier.get());

        return Result.success(secteurService.addSecteur(secteur),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

    @GetMapping("quartier")
    public Result listeSecteurParQuartier(@RequestParam("idQuartier") UUID idQuartier){
        Optional<Quartier> checkQuartier = quartierService.findById(idQuartier);
        if(checkQuartier.isEmpty()) return Result.error(400,
                "Ce quartier n'existe pas.",
                "This district does not exist.",
                "Este bairro não existe.");

        return Result.success(secteurService.findByQuartierId(idQuartier),
                "Liste des secteurs du quartier.",
                "List of the district's sectors.",
                "Lista dos setores do bairro.");
    }
}