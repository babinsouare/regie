package com.api.regie.services;

import com.api.regie.models.PermissionProfil;
import com.api.regie.models.Permissions;
import com.api.regie.models.Profils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionsProfilService {

    List<PermissionProfil> getAllPermissionsProfils();

    List<Permissions> getAllPermissionsByProfile(Profils profils);

    List<PermissionProfil> getAllPermissionsByProfileAndBtEnabled(Profils profils, Boolean btEnabled);

    List<PermissionProfil> getAllPermissionsByProfileForUpdate(Profils profils);

    Optional<PermissionProfil> getPermissionProfilByProfilAndPermission(Profils profil, Permissions permission);

    PermissionProfil savePermissionProfil(PermissionProfil permissionProfil);

    PermissionProfil updatePermissionProfil(PermissionProfil permissionProfil);

    Optional<PermissionProfil> getPermissionProfilById(UUID id);

    void deletePermissionProfilByProfilsAndPermissions(Profils profils, Permissions permissions);
}
