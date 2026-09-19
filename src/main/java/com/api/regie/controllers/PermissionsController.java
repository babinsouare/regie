package com.api.regie.controllers;


import com.api.regie.models.Permissions;
import com.api.regie.models.Result;
import com.api.regie.services.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("permissions")
public class PermissionsController {

    private final PermissionService permissionService;

    public PermissionsController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("liste")
    public Result getAllPermissions() {return Result.success(permissionService.getAllPermissions(),
            "Liste des permissions.",
            "List of permissions.",
            "Lista das permissões.");}

    @GetMapping("getbyid")
    public Result getPermissionById(@RequestParam("idPermission")UUID idPermission) {return Result.success(permissionService.getPermissionById(idPermission),
            "Les informations de la permission.",
            "Permission details.",
            "Detalhes da permissão.");}

    @GetMapping("liste/active")
    public Result getActivePermission() { return Result.success(permissionService.getPermissionsByBtEnabled(true),
            "Liste des permissions actives.",
            "List of active permissions.",
            "Lista das permissões ativas.");}

    @PostMapping("add")
    public Result addPermission(@RequestBody Permissions permissions) {

        Optional<Permissions> checkPermission = permissionService.getPermissionByCode(permissions.getCode());

        if (checkPermission.isPresent()) return Result.error(400,
                "Ce code existe déjà.",
                "This code already exists.",
                "Este código já existe.");

        return Result.success(permissionService.save(permissions),
                "Ajout effectué avec succès.",
                "Successfully added.",
                "Adicionado com sucesso.");
    }

    @PutMapping("update")
    public Result updatePermission(@RequestBody Permissions permissions) {

        Optional<Permissions> checkId = permissionService.getPermissionById(permissions.getId());
        if (checkId.isEmpty()) {return Result.error(404, "Élément introuvable.", "Item not found.", "Elemento não encontrado.");}

        Optional<Permissions> checkPermission = permissionService.getPermissionByCodeAndIdNot(permissions.getCode(), permissions.getId());

        if (checkPermission.isPresent()) {return Result.error(400,
                "Ce code existe déjà.",
                "This code already exists.",
                "Este código já existe.");}

        return Result.success(permissionService.save(permissions),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

}
