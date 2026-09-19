package com.api.regie.controllers;

import com.api.regie.dto.PermissionsBody;
import com.api.regie.dto.UpdatePassword;
import com.api.regie.models.*;
import com.api.regie.services.*;
import com.api.regie.utils.SecurityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("api/users")
public class UsersController {

    private final UsersService usersService;
    private final PermissionUsersService permissionUsersService;
    private final PermissionService permissionService;
    private final ProfilService profilService;
    private final PermissionsProfilService permissionsProfilService;
    private final SecurityUtils securityUtils;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsersController(UsersService usersService, PermissionUsersService permissionUsersService, PermissionService permissionService, ProfilService profilService, PermissionsProfilService permissionsProfilService, SecurityUtils securityUtils) {
        this.usersService = usersService;
        this.permissionUsersService = permissionUsersService;
        this.permissionService = permissionService;
        this.profilService = profilService;
        this.permissionsProfilService = permissionsProfilService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("liste")
    public Result findAll(){
        return Result.success(usersService.getAll(), "Liste des utilisateurs.", "List of users.", "Lista dos utilizadores.");
    }

    @PostMapping("users")
    public Result save(@RequestBody Users user, @RequestParam UUID idProfile){

        Users currentUser = securityUtils.getCurrentUser();
        Optional<Profils> profils = profilService.getProfilsById(idProfile);
        if(profils.isEmpty())return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        List<PermissionProfil> listePermission = permissionsProfilService.getAllPermissionsByProfileAndBtEnabled(profils.get(), true);
        if(listePermission.isEmpty())return Result.error(404,
                "Ce profil n'a aucune permission.",
                "This profile has no permissions.",
                "Este perfil não tem permissões.");

        Optional<Users> userMsisdn = usersService.findByMsisdn(user.getMsisdn());
        if(userMsisdn.isPresent())return Result.error(400,
                "Le numéro de téléphone est déjà utilisé.",
                "This phone number is already in use.",
                "Este número de telefone já está a ser utilizado.");

        Optional<Users> userEmail = usersService.findByEmail(user.getEmail());
        if(userEmail.isPresent())return Result.error(400,
                "L'adresse e-mail est déjà utilisée.",
                "This email address is already in use.",
                "Este endereço de e-mail já está a ser utilizado.");

        user.setPassword(encoder.encode("0000"));
        user.setProfils(profils.get());
        user.setIdUser(currentUser.getId());
        Users users = usersService.save(user);

        List<PermissionUsers> listePermissionUser = new ArrayList<>();
        for(PermissionProfil permission : listePermission){
            PermissionUsers permissionUser = new PermissionUsers();
            permissionUser.setUsers(users);
            permissionUser.setPermissions(permission.getPermissions());

            listePermissionUser.add(permissionUser);
        }

        permissionUsersService.savePermissionUser(listePermissionUser);

        return Result.success(users, "Ajout effectué avec succès.", "Successfully added.", "Adicionado com sucesso.");
    }

    @PutMapping("update")
    public Result update(@RequestBody Users user, @RequestParam UUID idProfile){

        Optional<Users> checkIdUser = usersService.findById(user.getId());
        if(checkIdUser.isEmpty()) return Result.error(404,
                "Utilisateur introuvable.",
                "User not found.",
                "Utilizador não encontrado.");

        Optional<Profils> profils = profilService.getProfilsById(idProfile);
        if(profils.isEmpty())return Result.error(404, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        List<PermissionProfil> listePermission = permissionsProfilService.getAllPermissionsByProfileForUpdate(profils.get());
        if(listePermission.isEmpty())return Result.error(404,
                "Ce profil n'a aucune permission.",
                "This profile has no permissions.",
                "Este perfil não tem permissões.");

        Optional<Users> userMsisdn = usersService.findByMsisdnAndIdNot(user.getMsisdn(), checkIdUser.get().getId());
        if(userMsisdn.isPresent())return Result.error(400,
                "Le numéro de téléphone est déjà utilisé.",
                "This phone number is already in use.",
                "Este número de telefone já está a ser utilizado.");

        Optional<Users> userEmail = usersService.findByEmailAndIdNot(user.getEmail(), checkIdUser.get().getId());
        if(userEmail.isPresent())return Result.error(400,
                "L'adresse e-mail est déjà utilisée.",
                "This email address is already in use.",
                "Este endereço de e-mail já está a ser utilizado.");

        if(checkIdUser.get().getProfils() != profils.get()) permissionUsersService.revokeAllPermissionUserByUser(checkIdUser.get());

        //user.setPassword(encoder.encode(user.getPassword()));
        user.setProfils(profils.get());
        Users users = usersService.save(user);

        List<PermissionUsers> listePermissionUser = new ArrayList<>();
        for(PermissionProfil permission : listePermission){

            Optional<PermissionUsers> checkPermissionUser = permissionUsersService.getPermissionUsersByUserAndPermission(users, permission.getPermissions());
            if (checkPermissionUser.isEmpty()){
                PermissionUsers permissionUser = new PermissionUsers();
                permissionUser.setUsers(users);
                permissionUser.setPermissions(permission.getPermissions());
                permissionUser.setBtEnabled(permission.getBtEnabled());

                listePermissionUser.add(permissionUser);
            }else{
                checkPermissionUser.get().setBtEnabled(permission.getBtEnabled());
                listePermissionUser.add(checkPermissionUser.get());
            }

        }

        permissionUsersService.savePermissionUser(listePermissionUser);

        return Result.success(users, "Ajout effectué avec succès.", "Successfully added.", "Adicionado com sucesso.");
    }

    @GetMapping("disableorenable")
    public Result enableOrDiableUser(@RequestParam("id_user") UUID idUser, @RequestParam("bt_enabled") Boolean btEnabled){

        Optional<Users> checkUser = usersService.findById(idUser);

        if(checkUser.isEmpty())return Result.error(400,
                "Utilisateur introuvable.",
                "User not found.",
                "Utilizador não encontrado.");

        checkUser.get().setBtEnabled(btEnabled);

        return Result.success(usersService.save(checkUser.get()),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");

    }

    @GetMapping("getbyid")
    public Result getUserDetails(@RequestParam("id_user") UUID idUser){
        Optional<Users> user = usersService.findById(idUser);
        if(user.isEmpty())return Result.error(404, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");

        List<PermissionUsers> permissions = permissionUsersService.getPermissionUsersByUser(user.get());

        List<Permissions> liste = new ArrayList<>();

        for (PermissionUsers permissionUsers : permissions){
            permissionUsers.getPermissions().setBtEnabled(permissionUsers.getBtEnabled());
            liste.add(permissionUsers.getPermissions());
        }

        Map<String, Object> data = new HashMap<>();

        data.put("user",user.get());
        data.put("permissions",liste);

        return Result.success(data, "Les détails de l'utilisateur.", "User details.", "Detalhes do utilizador.");
    }

    @GetMapping("getbytoken")
    public Result getUserDetailsByToken(){


        Users currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) return Result.error(401,
                "Utilisateur non authentifié.",
                "User is not authenticated.",
                "Utilizador não autenticado.");

        Optional<Users> user = usersService.findById(currentUser.getId());
        if(user.isEmpty())return Result.error(404, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");

        List<PermissionUsers> permissions = permissionUsersService.getPermissionUsersByUser(user.get());

        List<Permissions> liste = new ArrayList<>();

        for (PermissionUsers permissionUsers : permissions){
            permissionUsers.getPermissions().setBtEnabled(permissionUsers.getBtEnabled());
            liste.add(permissionUsers.getPermissions());
        }

        Map<String, Object> data = new HashMap<>();

        data.put("user",user.get());
        data.put("permissions",liste.stream().filter(r -> Boolean.TRUE.equals(r.getBtEnabled())).map(Permissions::getCode).toList());


        return Result.success(data, "Les détails de l'utilisateur.", "User details.", "Detalhes do utilizador.");
    }

    @PostMapping("password/update")
    public Result updatePassword(@RequestBody UpdatePassword updatePassword){
        Optional<Users> user = usersService.findById(updatePassword.getId());
        if(user.isEmpty())return Result.error(404, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");

        if (!encoder.matches(updatePassword.getOld_password(), user.get().getPassword())) {
            return Result.error(404,
                    "Ancien mot de passe incorrect.",
                    "Old password is incorrect.",
                    "Palavra-passe antiga incorreta.");
        }

        user.get().setPassword(encoder.encode(updatePassword.getNew_password()));
        user.get().setIsFirstLogin(false);

        return Result.success(usersService.save(user.get()),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

    @PostMapping("password/update/token")
    public Result updatePasswordWithtoken(@RequestBody UpdatePassword updatePassword){
        Users currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) return Result.error(401,
                "Utilisateur non authentifié.",
                "User is not authenticated.",
                "Utilizador não autenticado.");

        Optional<Users> user = usersService.findById(currentUser.getId());
        if(user.isEmpty())return Result.error(404, "Utilisateur introuvable.", "User not found.", "Utilizador não encontrado.");

        if (!encoder.matches(updatePassword.getOld_password(), user.get().getPassword())) {
            return Result.error(404,
                    "Ancien mot de passe incorrect.",
                    "Old password is incorrect.",
                    "Palavra-passe antiga incorreta.");
        }

        user.get().setPassword(encoder.encode(updatePassword.getNew_password()));
        user.get().setIsFirstLogin(false);

        return Result.success(usersService.save(user.get()),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }

    @PostMapping("permission/add-revoque")
    public Result grantPermission(@RequestParam UUID idUser, @RequestBody List<PermissionsBody> listIdPermission){

        Optional<Users> checkUser = usersService.findById(idUser);
        if(checkUser.isEmpty())return Result.error(400,
                "Utilisateur introuvable.",
                "User not found.",
                "Utilizador não encontrado.");

        Users currentUser = securityUtils.getCurrentUser();

        for(PermissionsBody permissionsBody : listIdPermission){

            Optional<Permissions> permission = permissionService.getPermissionById(permissionsBody.getIdPermission());

            if(permission.isPresent()){
                Optional<PermissionUsers> permissionUser = permissionUsersService.getPermissionUsersByUserAndPermission(checkUser.get(), permission.get());

                if(permissionUser.isPresent()){

                    permissionUser.get().setBtEnabled(permissionsBody.getBtEnabled());
                    permissionUser.get().setUsers(checkUser.get());
                    permissionUser.get().setIdUser(currentUser.getId());

                    permissionUsersService.savePermissionUser(Collections.singletonList(permissionUser.get()));

                }else{
                    PermissionUsers permissionUsers = new PermissionUsers();
                    permissionUsers.setUsers(checkUser.get());
                    permissionUsers.setPermissions(permission.get());
                    permissionUsers.setBtEnabled(permissionsBody.getBtEnabled());
                    permissionUsers.setIdUser(currentUser.getId());
                    permissionUsersService.savePermissionUser(Collections.singletonList(permissionUsers));
                }
            }

        }

        return Result.success(null,
                "Traitement effectué avec succès.",
                "Processing completed successfully.",
                "Processamento efetuado com sucesso.");

    }

    @PostMapping("permission/revoke")
    public Result revokePermission(@RequestParam UUID idUser, @RequestBody List<UUID> listIdPermission){

        Optional<Users> checkUser = usersService.findById(idUser);
        if(checkUser.isEmpty())return Result.error(400,
                "Utilisateur introuvable.",
                "User not found.",
                "Utilizador não encontrado.");

        permissionUsersService.revokePermissionUser(checkUser.get(), listIdPermission);

        return Result.success(null,
                "Traitement effectué avec succès.",
                "Processing completed successfully.",
                "Processamento efetuado com sucesso.");

    }

    @GetMapping("permissions")
    public Result getAllUsersPermissions(){

        List<PermissionUsers> data = permissionUsersService.getPermissionUsersByUserAndStatut(securityUtils.getCurrentUser(),true);

        return Result.success(data.stream().map(r -> r.getPermissions().getCode()).toList(),
                "Liste des permissions de l'utilisateur.",
                "List of the user's permissions.",
                "Lista das permissões do utilizador.");
    }

    @PostMapping("profil/get")
    public Result getUsersByProfil(@RequestParam UUID idProfil){

        Optional<Profils> profil = profilService.getProfilsById(idProfil);
        if(profil.isEmpty())return Result.error(400, "Profil introuvable.", "Profile not found.", "Perfil não encontrado.");

        return Result.success(usersService.findByProfile(profil.get()),
                "Traitement effectué avec succès.",
                "Processing completed successfully.",
                "Processamento efetuado com sucesso.");

    }

}
