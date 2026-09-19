package com.api.regie.implementations;

import com.api.regie.models.Permissions;
import com.api.regie.repository.PermissionsRepository;
import com.api.regie.services.PermissionService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionsImplementation implements PermissionService {

    private final PermissionsRepository permissionsRepository;

    public PermissionsImplementation(PermissionsRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
    }

    @Override
    public List<Permissions> getAllPermissions() {
        return permissionsRepository.findAll();
    }

    @Override
    public Optional<Permissions> getPermissionById(UUID id) {
        return permissionsRepository.findById(id);
    }

    @Override
    public List<Permissions> getPermissionsByBtEnabled(Boolean btEnabled) {
        return permissionsRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public Optional<Permissions> getPermissionByCode(String code) {
        return permissionsRepository.findByCode(code);
    }

    @Override
    public Optional<Permissions> getPermissionByModule(String module) {
        return permissionsRepository.findByModule(module);
    }

    @Override
    public Optional<Permissions> getPermissionByCodeAndIdNot(String code, UUID id) {
        return permissionsRepository.findByCodeAndIdNot(code, id);
    }

    @Override
    public Permissions save(Permissions permission) {
        return permissionsRepository.save(permission);
    }
}
