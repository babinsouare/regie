package com.api.regie.services;

import com.api.regie.models.PermissionUsers;
import com.api.regie.models.Permissions;
import com.api.regie.models.Users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionUsersService {

    List<PermissionUsers> getAllPermissionUsers();

    List<PermissionUsers> getPermissionUsersByUser(Users users);

    List<Permissions> getPermissionsByUser(Users users);

    List<PermissionUsers> getPermissionUsersByPermission(Permissions permissions);

    Optional<PermissionUsers> getPermissionUsersByUserAndPermission(Users users, Permissions permission);

    List<PermissionUsers> getPermissionUsersByUserAndStatut(Users users, Boolean btEnabled);

    Optional<PermissionUsers> getPermissionUserById(UUID id);

    void savePermissionUser(List<PermissionUsers> permissionUser);

    void revokePermissionUser(Users users, List<UUID> listIdPermissionUsers);

    void revokePermissionUserByPermission(Users users, List<Permissions> listePermissions);

    void revokeAllPermissionUserByUser(Users users);

}
