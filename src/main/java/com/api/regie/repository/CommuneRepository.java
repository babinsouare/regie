package com.api.regie.repository;

import com.api.regie.models.Commune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommuneRepository extends JpaRepository<Commune, UUID> {

    List<Commune> findAll();

    Optional<Commune> findById(UUID id);

    Optional<Commune> findByCommune(String commune);

    Optional<Commune> findByCommuneAndIdNot(String commune, UUID id);

    List<Commune> findByRegionId(UUID idRegion);

}
