package com.api.regie.services;

import com.api.regie.models.Commune;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommuneService {

    List<Commune> getAllCommune();

    Optional<Commune> findById(UUID id);

    Optional<Commune> findByCommune(String commune);

    Optional<Commune> findByCommuneAndIdNot(String commune, UUID id);

    Commune addCommune(Commune commune);

    List<Commune> findByRegionId(UUID idRegion);
}
