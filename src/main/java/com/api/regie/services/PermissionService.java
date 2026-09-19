package com.api.regie.services;

import com.api.regie.models.Permissions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionService {

    List<Permissions> getAllPermissions();

    Optional<Permissions> getPermissionById(UUID id);

    List<Permissions> getPermissionsByBtEnabled(Boolean btEnabled);

    Optional<Permissions> getPermissionByCode(String code);

    Optional<Permissions> getPermissionByModule(String module);

    Optional<Permissions> getPermissionByCodeAndIdNot(String code, UUID id);

    Permissions save(Permissions permission);
}
