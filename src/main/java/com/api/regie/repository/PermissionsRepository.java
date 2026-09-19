package com.api.regie.repository;

import com.api.regie.models.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionsRepository extends JpaRepository<Permissions, UUID> {

    List<Permissions> findAll();

    Optional<Permissions> findById(UUID id);

    Optional<Permissions> findByCode(String code);

    Optional<Permissions> findByModule(String module);

    List<Permissions> findByBtEnabled(Boolean btEnabled);

    Optional<Permissions> findByCodeAndIdNot(String code, UUID id);
}
