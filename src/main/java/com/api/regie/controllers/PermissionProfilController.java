package com.api.regie.controllers;

import com.api.regie.models.*;
import com.api.regie.services.*;
import com.api.regie.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("permission_profil")
public class PermissionProfilController {

    private final PermissionsProfilService permissionProfilService;
    private final ProfilService profilService;
    private final PermissionService permissionService;
    private final PermissionUsersService permissionUsersService;
    private final UsersService usersService;
    private final SecurityUtils securityUtils;

    public PermissionProfilController(PermissionsProfilService permissionProfilService, ProfilService profilService, PermissionService permissionService, PermissionUsersService permissionUsersService, UsersService usersService, SecurityUtils securityUtils, SecurityUtils securityUtils1) {

        this.permissionProfilService = permissionProfilService;
        this.profilService = profilService;
        this.permissionService = permissionService;
        this.permissionUsersService = permissionUsersService;
        this.usersService = usersService;
        this.securityUtils = securityUtils1;
    }

    @GetMapping("liste")
    public Result getAllPermissionProfil(){
        return Result.success(permissionProfilService.getAllPermissionsProfils(),
                "Liste des permissions des profils.",
                "List of profile permissions.",
                "Lista das permissões dos perfis.");
    }

    @GetMapping("getbyid")
    public Result getPermissionProfileById(@RequestParam UUID idPermissionProfil){
        return Result.success(permissionProfilService.getPermissionProfilById(idPermissionProfil),
                "Les informations de la liaison.",
                "Link details.",
                "Detalhes da associação.");
    }

    @GetMapping("profile/statut")
    public Result getPermissionProfilsAndStatut(@RequestParam UUID idProfil, @RequestParam Boolean btEnabled){

        Optional<Profils> profils = profilService.getProfilsById(idProfil);
        if(profils.isEmpty()) return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        return Result.success(permissionProfilService.getAllPermissionsByProfileAndBtEnabled(profils.get(), btEnabled),
                "Liste des permissions actives du profil.",
                "List of the profile's active permissions.",
                "Lista das permissões ativas do perfil.");
    }

    @PostMapping("add")
    public Result addPermissionProfile(@RequestParam UUID idProfil, @RequestBody List<UUID> listIdPermission) {

        Optional<Profils> profils = profilService.getProfilsById(idProfil);
        if (profils.isEmpty()) return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        List<Users> listUsersUsingCurrentProfil = usersService.findByProfile(profils.get());

        Users currentUser = securityUtils.getCurrentUser();

        for (UUID id : listIdPermission) {
            Optional<Permissions> permissions = permissionService.getPermissionById(id);
            if (permissions.isPresent()) {

                Optional<PermissionProfil> checkPermissionProfil = permissionProfilService.getPermissionProfilByProfilAndPermission(profils.get(), permissions.get());

                boolean permissionExists = false;

                if (checkPermissionProfil.isEmpty()) {
                    permissionExists = true;
                    PermissionProfil permissionProfil = new PermissionProfil();
                    permissionProfil.setPermissions(permissions.get());
                    permissionProfil.setProfils(profils.get());
                    permissionProfil.setIdUser(currentUser.getId());
                    permissionProfilService.savePermissionProfil(permissionProfil);
                }

                if (permissionExists) {
                    if(!listUsersUsingCurrentProfil.isEmpty()){
                        for (Users users : listUsersUsingCurrentProfil) {
                            PermissionUsers permissionUsers = new PermissionUsers();
                            permissionUsers.setPermissions(permissions.get());
                            permissionUsers.setUsers(users);
                            permissionUsers.setIdUser(currentUser.getId());
                            permissionUsersService.savePermissionUser(Collections.singletonList(permissionUsers));
                        }

                    }
                }

            }

        }
        return Result.success(null, "Ajout effectué avec succès.", "Successfully added.", "Adicionado com sucesso.");
    }

    @PostMapping("remove")
    public Result removePermissionProfile(@RequestParam UUID idProfil, @RequestBody List<UUID> listIdPermission) {

        Optional<Profils> profils = profilService.getProfilsById(idProfil);

        if (profils.isEmpty()) return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        List<Users> listUsersUsingCurrentProfil = usersService.findByProfile(profils.get());

        for (UUID id : listIdPermission) {
            Optional<Permissions> permissions = permissionService.getPermissionById(id);
            if (permissions.isPresent()) {
                Optional<PermissionProfil> checkPermissionProfil = permissionProfilService.getPermissionProfilByProfilAndPermission(profils.get(), permissions.get());
                //Delete permission for current profile
                if (checkPermissionProfil.isPresent()) {
                    permissionProfilService.deletePermissionProfilByProfilsAndPermissions(profils.get(), permissions.get());
                    if(!listUsersUsingCurrentProfil.isEmpty()){
                        for (Users users : listUsersUsingCurrentProfil) {
                            permissionUsersService.revokePermissionUserByPermission(users, Collections.singletonList(permissions.get()));
                        }

                    }

                }

            }

        }
        return Result.success(null,
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }



}
