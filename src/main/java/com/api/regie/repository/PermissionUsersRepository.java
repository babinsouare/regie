package com.api.regie.repository;

import com.api.regie.models.PermissionUsers;
import com.api.regie.models.Permissions;
import com.api.regie.models.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionUsersRepository extends JpaRepository<PermissionUsers, UUID> {

    List<PermissionUsers> findAll();

    List<PermissionUsers> findByUsers(Users user);

    @Query(value = "select p.permissions from PermissionUsers p where p.users=?1")
    List<Permissions> findPermissionsByUsers(Users user);

    List<PermissionUsers> findByPermissions(Permissions permission);

    Optional<PermissionUsers> findByUsersAndPermissions(Users user, Permissions permissions);

    List<PermissionUsers> findByUsersAndBtEnabled(Users user, Boolean btEnabled);

    Optional<PermissionUsers> findById(UUID id);

    @Transactional
    @Modifying
    @Query(value = "delete PermissionUsers p  where p.users=?1 and p.permissions.id in ?2")
    void revokePermissionUser(Users users, List<UUID> listIdPermissionUsers);

    @Transactional
    @Modifying
    @Query(value = "delete PermissionUsers p  where p.users=?1 and p.permissions in ?2")
    void revokePermissionUserByPermissions(Users users, List<Permissions> listIdPermissionUsers);

    @Transactional
    @Modifying
    @Query(value = "delete PermissionUsers p  where p.users=?1")
    void revokeAllPermissionUserByUser(Users users);
}
