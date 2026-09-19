package com.api.regie.repository;

import com.api.regie.models.PermissionProfil;
import com.api.regie.models.Permissions;
import com.api.regie.models.Profils;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionProfilRepository extends JpaRepository<PermissionProfil, UUID> {

    List<PermissionProfil> findAll();

    @Query(value = "select p.permissions from PermissionProfil p where p.profils=?1 and p.btEnabled=true")
    List<Permissions> findByProfil(Profils profils);

    List<PermissionProfil> findByProfilsAndBtEnabled(Profils profils, Boolean enabled);

    List<PermissionProfil> findByProfils(Profils profils);

    Optional<PermissionProfil> findByProfilsAndPermissions(Profils profils, Permissions permissions);

    Optional<PermissionProfil> findById(UUID id);

    @Transactional
    @Modifying
    @Query("delete PermissionProfil p where p.profils = ?1 and p.permissions = ?2")
    void deleteByProfilsAndPermissions(Profils profils, Permissions permissions);
}
