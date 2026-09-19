package com.api.regie.controllers;

import com.api.regie.models.Clients;
import com.api.regie.models.Result;
import com.api.regie.models.TypeClient;
import com.api.regie.services.ClientsService;
import com.api.regie.services.TypeClientService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientsService clientsService;
    private final TypeClientService typeClientService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ClientsController(ClientsService clientsService, TypeClientService typeClientService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clientsService = clientsService;
        this.typeClientService = typeClientService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("liste")
    public Result getAllClients(){
        return Result.success(clientsService.getAllClients(), "Liste des clients.", "List of clients.", "Lista dos clientes.");
    }

    @GetMapping("getbyid")
    public Result getClientsById(@RequestParam("idClient")UUID idClient){
        return Result.success(clientsService.findById(idClient),
                "Les informations du client.",
                "Client details.",
                "Detalhes do cliente.");
    }

    @PostMapping("/add")
    public Result addClients(@RequestBody Clients clients, @RequestParam("idTypeClient")UUID idTypeClient){

        Optional<Clients> checkByEmail = clientsService.findByEmailResponsable(clients.getEmailResponsable());
        Optional<Clients> checkByTel = clientsService.findByTelephoneResponsable(clients.getTelephoneResponsable());
        Optional<Clients> checkByDenom = clientsService.findByDenomination(clients.getDenomination());
        Optional<TypeClient> typeClient = typeClientService.findById(idTypeClient);

        if(checkByEmail.isPresent()){ 
            return Result.error(400,
                    "Un client avec cet e-mail existe déjà.",
                    "A client with this email already exists.",
                    "Já existe um cliente com este e-mail.");
        }
        
        if(checkByTel.isPresent()){ 
            return Result.error(400,
                    "Un client avec ce téléphone existe déjà.",
                    "A client with this phone number already exists.",
                    "Já existe um cliente com este telefone.");
        }
        
        if(checkByDenom.isPresent()){ 
            return Result.error(400,
                    "Un client avec cette dénomination existe déjà.",
                    "A client with this name already exists.",
                    "Já existe um cliente com esta denominação.");
        }

        if(typeClient.isEmpty()) return Result.error(400,
                "Type de client introuvable.",
                "Client type not found.",
                "Tipo de cliente não encontrado.");

        clients.setPassword(bCryptPasswordEncoder.encode("000000"));
        clients.setTypeClient(typeClient.get());

        return Result.success(clientsService.addClients(clients),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updateClients(@RequestBody Clients clients, @RequestParam("idTypeClient")UUID idTypeClient){

        Optional<Clients> checkClientsId = clientsService.findById(clients.getId());

        if(checkClientsId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<Clients> checkByEmail = clientsService.findByEmailResponsableAndIdNot(clients.getEmailResponsable(), clients.getId());
        Optional<Clients> checkByTel = clientsService.findByTelephoneResponsableAndIdNot(clients.getTelephoneResponsable(), clients.getId());
        Optional<Clients> checkByDenom = clientsService.findByDenominationAndIdNot(clients.getDenomination(), clients.getId());
        Optional<TypeClient> typeClient = typeClientService.findById(idTypeClient);

        if(checkByEmail.isPresent()){
            return Result.error(400,
                    "Un client avec cet e-mail existe déjà.",
                    "A client with this email already exists.",
                    "Já existe um cliente com este e-mail.");
        }
        
        if(checkByTel.isPresent()){
            return Result.error(400,
                    "Un client avec ce téléphone existe déjà.",
                    "A client with this phone number already exists.",
                    "Já existe um cliente com este telefone.");
        }
        
        if(checkByDenom.isPresent()){
            return Result.error(400,
                    "Un client avec cette dénomination existe déjà.",
                    "A client with this name already exists.",
                    "Já existe um cliente com esta denominação.");
        }

        if(typeClient.isEmpty()) return Result.error(400,
                "Type de client introuvable.",
                "Client type not found.",
                "Tipo de cliente não encontrado.");

        if(clients.getPassword() != null && !clients.getPassword().isBlank()) {
            clients.setPassword(bCryptPasswordEncoder.encode(clients.getPassword()));
        } else {
            clients.setPassword(checkClientsId.get().getPassword());
        }

        clients.setTypeClient(typeClient.get());

        return Result.success(clientsService.addClients(clients),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}