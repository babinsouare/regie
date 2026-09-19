package com.api.regie.repository;

import com.api.regie.models.SoutraConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoutraConfigRepository extends JpaRepository<SoutraConfig, UUID> {

    List<SoutraConfig> findAll();

    Optional<SoutraConfig> findById(UUID id);

    Optional<SoutraConfig> findByActifTrue();

    Optional<SoutraConfig> findByEnvironnement(String environnement);

    SoutraConfig save(SoutraConfig soutraConfig);
}
