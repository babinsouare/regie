package com.api.regie.implementations;

import com.api.regie.models.PermissionProfil;
import com.api.regie.models.Permissions;
import com.api.regie.models.Profils;
import com.api.regie.repository.PermissionProfilRepository;
import com.api.regie.services.PermissionsProfilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionsProfilImplementation implements PermissionsProfilService {

    private final PermissionProfilRepository permissionProfilRepository;

    public PermissionsProfilImplementation(PermissionProfilRepository permissionProfilRepository) {
        this.permissionProfilRepository = permissionProfilRepository;
    }

    @Override
    public List<PermissionProfil> getAllPermissionsProfils() {
        return permissionProfilRepository.findAll();
    }

    @Override
    public List<Permissions> getAllPermissionsByProfile(Profils profils) {
        return permissionProfilRepository.findByProfil(profils);
    }

    @Override
    public List<PermissionProfil> getAllPermissionsByProfileAndBtEnabled(Profils profils, Boolean btEnabled) {
        return permissionProfilRepository.findByProfilsAndBtEnabled(profils, btEnabled);
    }

    @Override
    public List<PermissionProfil> getAllPermissionsByProfileForUpdate(Profils profils) {
        return permissionProfilRepository.findByProfils(profils);
    }

    @Override
    public Optional<PermissionProfil> getPermissionProfilByProfilAndPermission(Profils profil, Permissions permission) {
        return permissionProfilRepository.findByProfilsAndPermissions(profil, permission);
    }

    @Override
    public PermissionProfil savePermissionProfil(PermissionProfil permissionProfil) {
        return permissionProfilRepository.save(permissionProfil);
    }

    @Override
    public PermissionProfil updatePermissionProfil(PermissionProfil permissionProfil) {
        return permissionProfilRepository.save(permissionProfil);
    }

    @Override
    public Optional<PermissionProfil> getPermissionProfilById(UUID id) {
        return permissionProfilRepository.findById(id);
    }

    @Override
    public void deletePermissionProfilByProfilsAndPermissions(Profils profils, Permissions permissions) {
        permissionProfilRepository.deleteByProfilsAndPermissions(profils, permissions);
    }
}
