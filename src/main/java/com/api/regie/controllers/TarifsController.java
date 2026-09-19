package com.api.regie.controllers;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.models.Tarifs;
import com.api.regie.models.Result;
import com.api.regie.services.CaracteristiquePanneauxService;
import com.api.regie.services.TarifsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tarifs")
public class TarifsController {

    private final TarifsService tarifsService;
    private final CaracteristiquePanneauxService caracteristiquePanneauxService;

    public TarifsController(TarifsService tarifsService, CaracteristiquePanneauxService caracteristiquePanneauxService) {
        this.tarifsService = tarifsService;
        this.caracteristiquePanneauxService = caracteristiquePanneauxService;
    }

    @GetMapping("liste")
    public Result getAllTarifs(){
        return Result.success(tarifsService.getAllTarifs(), "Liste des tarifs.", "List of rates.", "Lista das tarifas.");
    }

    @GetMapping("actifs")
    public Result getTarifsActifs(){
        return Result.success(tarifsService.findByBtEnabled(true),
                "Liste des tarifs actifs.",
                "List of active rates.",
                "Lista das tarifas ativas.");
    }

    @GetMapping("getbyid")
    public Result getTarifsById(@RequestParam("idTarif") UUID idTarif){
        return Result.success(tarifsService.findById(idTarif),
                "Les informations du tarif.",
                "Rate details.",
                "Detalhes da tarifa.");
    }

    @GetMapping("getbycaracteristique")
    public Result getTarifsByCaracteristique(@RequestParam("idCaracteristique") UUID idCaracteristique){
        return Result.success(tarifsService.findByCaracteristiquePanneauxId(idCaracteristique),
                "Liste des tarifs pour cette caractéristique.",
                "List of rates for this characteristic.",
                "Lista das tarifas para esta característica.");
    }

    @PostMapping("/add")
    public Result addTarifs(@RequestBody Tarifs tarifs, @RequestParam("idCaracteristique") UUID idCaracteristique){


        Optional<CaracteristiquePanneaux> caracteristiquePanneaux = caracteristiquePanneauxService.findById(idCaracteristique);
        if (caracteristiquePanneaux.isEmpty()) return Result.error(404,
                "Caractéristique de panneaux introuvable.",
                "Billboard characteristic not found.",
                "Característica de painéis não encontrada.");

        List<Tarifs> checkTarif = tarifsService.findByCaracteristiquePanneaux(caracteristiquePanneaux.get());
        if(!checkTarif.isEmpty()) return Result.error(400,
                "Cette caractéristique a déjà un tarif.",
                "This characteristic already has a rate.",
                "Esta característica já tem uma tarifa.");

        tarifs.setCaracteristiquePanneaux(caracteristiquePanneaux.get());

        return Result.success(tarifsService.addTarifs(tarifs),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateTarifs(@RequestBody Tarifs tarifs, @RequestParam("idCaracteristique") UUID idCaracteristique){

        Optional<Tarifs> checkTarifsId = tarifsService.findById(tarifs.getId());

        if(checkTarifsId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<CaracteristiquePanneaux> caracteristiquePanneaux = caracteristiquePanneauxService.findById(idCaracteristique);
        if (caracteristiquePanneaux.isEmpty()) return Result.error(404,
                "Caractéristique de panneaux introuvable.",
                "Billboard characteristic not found.",
                "Característica de painéis não encontrada.");

        tarifs.setCaracteristiquePanneaux(caracteristiquePanneaux.get());

        return Result.success(tarifsService.addTarifs(tarifs),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
