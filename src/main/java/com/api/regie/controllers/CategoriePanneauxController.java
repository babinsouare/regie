package com.api.regie.controllers;

import com.api.regie.models.CategoriePanneaux;
import com.api.regie.models.Result;
import com.api.regie.services.CategoriePanneauxService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/categorie-panneaux")
public class CategoriePanneauxController {

    private final CategoriePanneauxService categoriePanneauxService;

    public CategoriePanneauxController(CategoriePanneauxService categoriePanneauxService) {
        this.categoriePanneauxService = categoriePanneauxService;
    }

    @GetMapping("liste")
    public Result getAllCategoriePanneaux(){
        return Result.success(categoriePanneauxService.getAllCategoriePanneaux(),
                "Liste des catégories de panneaux.",
                "List of billboard categories.",
                "Lista das categorias de painéis.");
    }

    @GetMapping("actifs")
    public Result getCategoriePanneauxActifs(){
        return Result.success(categoriePanneauxService.findByBtEnabled(true),
                "Liste des catégories actives.",
                "List of active categories.",
                "Lista das categorias ativas.");
    }

    @GetMapping("getbyid")
    public Result getCategoriePanneauxById(@RequestParam("idCategorie") UUID idCategorie){
        return Result.success(categoriePanneauxService.findById(idCategorie),
                "Les informations de la catégorie.",
                "Category details.",
                "Detalhes da categoria.");
    }

    @PostMapping("/add")
    public Result addCategoriePanneaux(@RequestBody CategoriePanneaux categoriePanneaux){

        if(categoriePanneaux.getCategorie() != null){
            Optional<CategoriePanneaux> checkCategorie = categoriePanneauxService.findByCategorie(categoriePanneaux.getCategorie());

            if(checkCategorie.isPresent()){ 
                return Result.error(400,
                        "Cette catégorie existe déjà.",
                        "This category already exists.",
                        "Esta categoria já existe.");
            }
        }

        return Result.success(categoriePanneauxService.addCategoriePanneaux(categoriePanneaux),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateCategoriePanneaux(@RequestBody CategoriePanneaux categoriePanneaux){

        Optional<CategoriePanneaux> checkCategorieId = categoriePanneauxService.findById(categoriePanneaux.getId());

        if(checkCategorieId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        if(categoriePanneaux.getCategorie() != null){
            Optional<CategoriePanneaux> checkCategorie = categoriePanneauxService.findByCategorieAndIdNot(categoriePanneaux.getCategorie(), categoriePanneaux.getId());

            if(checkCategorie.isPresent()){
                return Result.error(400,
                        "Cette catégorie existe déjà.",
                        "This category already exists.",
                        "Esta categoria já existe.");
            }
        }

        return Result.success(categoriePanneauxService.addCategoriePanneaux(categoriePanneaux),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
