package com.api.regie.controllers;


import com.api.regie.dto.PermissionsBody;
import com.api.regie.dto.ProfileWithPermissions;
import com.api.regie.models.*;
import com.api.regie.services.*;
import com.api.regie.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("profils")
public class ProfilsController {

    private final ProfilService profilService;
    private final PermissionsProfilService permissionsProfilService;
    private final UsersService usersService;
    private final PermissionUsersService permissionUsersService;
    private final SecurityUtils securityUtils;
    private final PermissionService permissionService;

    public ProfilsController(ProfilService profilService, PermissionsProfilService permissionsProfilService, UsersService usersService, PermissionUsersService permissionUsersService, SecurityUtils securityUtils, PermissionService permissionService) {
        this.profilService = profilService;
        this.permissionsProfilService = permissionsProfilService;
        this.usersService = usersService;
        this.permissionUsersService = permissionUsersService;
        this.securityUtils = securityUtils;
        this.permissionService = permissionService;
    }

    @GetMapping("liste")
    public Result getAllProfils() {return Result.success(profilService.getAll(),
            "Liste des profils.",
            "List of profiles.",
            "Lista dos perfis.");}

    @GetMapping("getbyid")
    public Result getProfilsById(@RequestParam("idProfil")UUID idProfil) {

        Optional<Profils> profils = profilService.getProfilsById(idProfil);
        if(profils.isEmpty()) return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        List<Permissions> permission = permissionsProfilService.getAllPermissionsByProfile(profils.get());

        Map<String, Object> data = new HashMap<>();
        data.put("profils", profils.get());
        data.put("permission", permission);

        return Result.success(data, "Les informations du profil.", "Profile details.", "Detalhes do perfil.");
    }

    @GetMapping("liste/active")
    public Result getActiveProfils() { return Result.success(profilService.getProfilssByBtEnabled(true),
            "Liste des profils actifs.",
            "List of active profiles.",
            "Lista dos perfis ativos.");}

    @PostMapping("add")
    public Result addProfilsWithPermissions(@RequestBody ProfileWithPermissions profilsWithPermissions) {

        Optional<Profils> checkProfils = profilService.getProfilsByProfil(profilsWithPermissions.getProfils().getProfil());

        if (checkProfils.isPresent()) return Result.error(400,
                "Ce profil existe déjà.",
                "This profile already exists.",
                "Este perfil já existe.");

        Profils profils = profilService.save(profilsWithPermissions.getProfils());

        for (PermissionsBody permissions : profilsWithPermissions.getPermissions()) {

            Optional<Permissions> checkId = permissionService.getPermissionById(permissions.getIdPermission());

            if (checkId.isPresent()){

                PermissionProfil item = new PermissionProfil();
                item.setProfils(profils);
                item.setPermissions(checkId.get());
                item.setIdUser(securityUtils.getCurrentUser().getId());
                item.setBtEnabled(permissions.getBtEnabled());

                permissionsProfilService.savePermissionProfil(item);
            }



        }

        return Result.success(profils, "Ajout effectué avec succès.", "Successfully added.", "Adicionado com sucesso.");
    }

    @PutMapping("update")
    public Result updateProfil(@RequestBody ProfileWithPermissions profilsWithPermissions) {

        Optional<Profils> checkId = profilService.getProfilsById(profilsWithPermissions.getProfils().getId());
        if (checkId.isEmpty()) {return Result.error(404, "Élément introuvable.", "Item not found.", "Elemento não encontrado.");}

        Optional<Profils> checkProfils= profilService.getProfilsByProfilAndIdNot(profilsWithPermissions.getProfils().getProfil(), profilsWithPermissions.getProfils().getId());
        if (checkProfils.isPresent()) {return Result.error(400,
                "Ce profil existe déjà.",
                "This profile already exists.",
                "Este perfil já existe.");}

        Profils profils = profilService.save(profilsWithPermissions.getProfils());

        List<PermissionProfil> liste = permissionsProfilService.getAllPermissionsByProfileForUpdate(profils);

        for (PermissionsBody permissions : profilsWithPermissions.getPermissions()) {
            Optional<Permissions> checkPermission = permissionService.getPermissionById(permissions.getIdPermission());
            if (checkPermission.isPresent()) {

                Optional<PermissionProfil> checkIdPermission = permissionsProfilService.getPermissionProfilByProfilAndPermission(profils, checkPermission.get());

                if (checkIdPermission.isPresent()) {
                    checkIdPermission.get().setBtEnabled(permissions.getBtEnabled());
                    permissionsProfilService.savePermissionProfil(checkIdPermission.get());
                }else{
                    PermissionProfil item = new PermissionProfil();
                    item.setProfils(profils);
                    item.setPermissions(checkPermission.get());
                    item.setIdUser(securityUtils.getCurrentUser().getId());
                    item.setBtEnabled(permissions.getBtEnabled());
                    permissionsProfilService.savePermissionProfil(item);
                }

            }

        }

        List<Users> users = usersService.findByProfile(profils);

        for (Users user : users) {

            for (PermissionsBody permission : profilsWithPermissions.getPermissions()) {
                Optional<Permissions> permissions = permissionService.getPermissionById(permission.getIdPermission());
                if (permissions.isPresent()) {
                    PermissionUsers permissionUsers = new PermissionUsers();
                    permissionUsers.setUsers(user);
                    permissionUsers.setIdUser(securityUtils.getCurrentUser().getId());
                    permissionUsers.setPermissions(permissions.get());
                    permissionUsers.setBtEnabled(permission.getBtEnabled());

                    permissionUsersService.savePermissionUser(Collections.singletonList(permissionUsers));
                }

            }

        }

        return Result.success(null,
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

}
