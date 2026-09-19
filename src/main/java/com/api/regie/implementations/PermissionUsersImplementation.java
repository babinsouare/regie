package com.api.regie.implementations;

import com.api.regie.models.PermissionUsers;
import com.api.regie.models.Permissions;
import com.api.regie.models.Users;
import com.api.regie.repository.PermissionUsersRepository;
import com.api.regie.services.PermissionUsersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionUsersImplementation implements PermissionUsersService {

    private final PermissionUsersRepository permissionUsersRepository;

    public PermissionUsersImplementation(PermissionUsersRepository permissionUsersRepository) {
        this.permissionUsersRepository = permissionUsersRepository;
    }

    @Override
    public List<PermissionUsers> getAllPermissionUsers() {
        return permissionUsersRepository.findAll();
    }

    @Override
    public List<PermissionUsers> getPermissionUsersByUser(Users users) {
        return permissionUsersRepository.findByUsers(users);
    }

    @Override
    public List<Permissions> getPermissionsByUser(Users users) {
        return permissionUsersRepository.findPermissionsByUsers(users);
    }

    @Override
    public List<PermissionUsers> getPermissionUsersByPermission(Permissions permissions) {
        return permissionUsersRepository.findByPermissions(permissions);
    }

    @Override
    public Optional<PermissionUsers> getPermissionUsersByUserAndPermission(Users users, Permissions permission) {
        return permissionUsersRepository.findByUsersAndPermissions(users, permission);
    }

    @Override
    public List<PermissionUsers> getPermissionUsersByUserAndStatut(Users users, Boolean btEnabled) {
        return permissionUsersRepository.findByUsersAndBtEnabled(users, btEnabled);
    }

    @Override
    public Optional<PermissionUsers> getPermissionUserById(UUID id) {
        return permissionUsersRepository.findById(id);
    }

    @Override
    public void savePermissionUser(List<PermissionUsers> permissionUser) {
         permissionUsersRepository.saveAll(permissionUser);
    }

    @Override
    public void revokePermissionUser(Users users, List<UUID> listIdPermissionUsers) {
        permissionUsersRepository.revokePermissionUser(users, listIdPermissionUsers);
    }

    @Override
    public void revokePermissionUserByPermission(Users users, List<Permissions> listePermissions) {
        permissionUsersRepository.revokePermissionUserByPermissions(users, listePermissions);
    }

    @Override
    public void revokeAllPermissionUserByUser(Users users) {
        permissionUsersRepository.revokeAllPermissionUserByUser(users);
    }
}
