package com.api.regie.controllers;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.models.CategoriePanneaux;
import com.api.regie.models.Result;
import com.api.regie.services.CaracteristiquePanneauxService;
import com.api.regie.services.CategoriePanneauxService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/caracteristique-panneaux")
public class CaracteristiquePanneauxController {

    private final CaracteristiquePanneauxService caracteristiquePanneauxService;
    private final CategoriePanneauxService categoriePanneauxService;

    public CaracteristiquePanneauxController(CaracteristiquePanneauxService caracteristiquePanneauxService, CategoriePanneauxService categoriePanneauxService) {
        this.caracteristiquePanneauxService = caracteristiquePanneauxService;
        this.categoriePanneauxService = categoriePanneauxService;
    }

    @GetMapping("liste")
    public Result getAllCaracteristiquePanneaux(){
        return Result.success(caracteristiquePanneauxService.getAllCaracteristiquePanneaux(),
                "Liste des caractéristiques de panneaux.",
                "List of billboard characteristics.",
                "Lista das características de painéis.");
    }

    @GetMapping("actifs")
    public Result getCaracteristiquePanneauxActifs(){
        return Result.success(caracteristiquePanneauxService.findByBtEnabled(true),
                "Liste des caractéristiques actives.",
                "List of active characteristics.",
                "Lista das características ativas.");
    }

    @GetMapping("getbyid")
    public Result getCaracteristiquePanneauxById(@RequestParam("idCaracteristique") UUID idCaracteristique){
        return Result.success(caracteristiquePanneauxService.findById(idCaracteristique),
                "Les informations de la caractéristique.",
                "Characteristic details.",
                "Detalhes da característica.");
    }

    @GetMapping("getbycategorie")
    public Result getCaracteristiquePanneauxByCategorie(@RequestParam("idCategorie") UUID idCategorie){
        return Result.success(caracteristiquePanneauxService.findByCategoriePanneauxId(idCategorie),
                "Liste des caractéristiques de cette catégorie.",
                "List of this category's characteristics.",
                "Lista das características desta categoria.");
    }

    @PostMapping("/add")
    public Result addCaracteristiquePanneaux(@RequestBody CaracteristiquePanneaux caracteristiquePanneaux, @RequestParam("idCategorie") UUID idCategorie){

        if(caracteristiquePanneaux.getCaracteristique() != null){
            Optional<CaracteristiquePanneaux> checkCaracteristique = caracteristiquePanneauxService.findByCaracteristique(caracteristiquePanneaux.getCaracteristique());

            if(checkCaracteristique.isPresent()){ 
                return Result.error(400,
                        "Cette caractéristique existe déjà.",
                        "This characteristic already exists.",
                        "Esta característica já existe.");
            }
        }

        Optional<CategoriePanneaux> categorie = categoriePanneauxService.findById(idCategorie);
        if(categorie.isEmpty()){
            return Result.error(404,
                    "Catégorie de panneaux introuvable.",
                    "Billboard category not found.",
                    "Categoria de painéis não encontrada.");
        }

        caracteristiquePanneaux.setCategoriePanneaux(categorie.get());

        return Result.success(caracteristiquePanneauxService.addCaracteristiquePanneaux(caracteristiquePanneaux),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateCaracteristiquePanneaux(@RequestBody CaracteristiquePanneaux caracteristiquePanneaux, @RequestParam("idCategorie") UUID idCategorie){

        Optional<CaracteristiquePanneaux> checkCaracteristiqueId = caracteristiquePanneauxService.findById(caracteristiquePanneaux.getId());

        if(checkCaracteristiqueId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        if(caracteristiquePanneaux.getCaracteristique() != null){
            Optional<CaracteristiquePanneaux> checkCaracteristique = caracteristiquePanneauxService.findByCaracteristiqueAndIdNot(caracteristiquePanneaux.getCaracteristique(), caracteristiquePanneaux.getId());

            if(checkCaracteristique.isPresent()){
                return Result.error(400,
                        "Cette caractéristique existe déjà.",
                        "This characteristic already exists.",
                        "Esta característica já existe.");
            }
        }

        Optional<CategoriePanneaux> categorie = categoriePanneauxService.findById(idCategorie);
        if(categorie.isEmpty()){
            return Result.error(404,
                    "Catégorie de panneaux introuvable.",
                    "Billboard category not found.",
                    "Categoria de painéis não encontrada.");
        }

        caracteristiquePanneaux.setCategoriePanneaux(categorie.get());

        return Result.success(caracteristiquePanneauxService.addCaracteristiquePanneaux(caracteristiquePanneaux),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
