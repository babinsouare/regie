package com.api.regie.services;

import com.api.regie.models.Commune;
import com.api.regie.models.Quartier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuartierService {

    List<Quartier> getAllQuartier();

    Optional<Quartier> findById(UUID id);

    Optional<Quartier> findByQuartier(String quartier);

    Optional<Quartier> findByQuartierAndIdNot(String quartier, UUID id);

    Optional<Quartier> findByQuartierAndIdNotAndCommune(String quartier, UUID id, Commune commune);

    Optional<Quartier> findByQuartierAndCommune(String quartier, Commune commune);

    Quartier addQuartier(Quartier quartier);

    List<Quartier> findByCommuneId(UUID idCommune);
}