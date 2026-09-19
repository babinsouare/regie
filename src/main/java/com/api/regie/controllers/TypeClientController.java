package com.api.regie.controllers;

import com.api.regie.models.TypeClient;
import com.api.regie.models.Result;
import com.api.regie.services.TypeClientService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/typeclient")
public class TypeClientController {

    private final TypeClientService typeClientService;

    public TypeClientController(TypeClientService typeClientService) {
        this.typeClientService = typeClientService;
    }

    @GetMapping("liste")
    public Result getAllTypeClients(){
        return Result.success(typeClientService.getAllTypeClients(),
                "Liste des types de clients.",
                "List of client types.",
                "Lista dos tipos de clientes.");
    }

    @GetMapping("getbyid")
    public Result getTypeClientsById(@RequestParam("idTypeClient")UUID idTypeClient){
        return Result.success(typeClientService.findById(idTypeClient),
                "Les informations du type de client.",
                "Client type details.",
                "Detalhes do tipo de cliente.");
    }

    @PostMapping("/add")
    public Result addTypeClient(@RequestBody TypeClient typeClient){

        Optional<TypeClient> checkTypeClient = typeClientService.findByTypeClient(typeClient.getTypeClient());

        if(checkTypeClient.isPresent()){ 
            return Result.error(400,
                    "Ce type de client existe déjà.",
                    "This client type already exists.",
                    "Este tipo de cliente já existe.");
        }

        return Result.success(typeClientService.addTypeClient(typeClient),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateTypeClient(@RequestBody TypeClient typeClient){

        Optional<TypeClient> checkTypeClientId = typeClientService.findById(typeClient.getId());

        if(checkTypeClientId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<TypeClient> checkTypeClient = typeClientService.findByTypeClientAndIdNot(typeClient.getTypeClient(), typeClient.getId());

        if(checkTypeClient.isPresent()){
            return Result.error(400,
                    "Ce type de client existe déjà.",
                    "This client type already exists.",
                    "Este tipo de cliente já existe.");
        }

        return Result.success(typeClientService.addTypeClient(typeClient),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}