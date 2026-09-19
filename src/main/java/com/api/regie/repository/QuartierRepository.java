package com.api.regie.repository;

import com.api.regie.models.Commune;
import com.api.regie.models.Quartier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuartierRepository extends JpaRepository<Quartier, UUID> {

    List<Quartier> findAll();

    Optional<Quartier> findByQuartier(String quartier);

    Optional<Quartier> findByQuartierAndIdNot(String quartier, UUID id);

    Optional<Quartier> findByQuartierAndIdNotAndCommune(String quartier, UUID id, Commune commune);

    Optional<Quartier> findByQuartierAndCommune(String quartier, Commune commune);

    Quartier save(Quartier quartier);

    Optional<Quartier> findById(UUID id);

    List<Quartier> findByCommuneId(UUID idCommune);

}