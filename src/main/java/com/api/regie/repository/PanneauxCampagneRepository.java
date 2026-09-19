package com.api.regie.repository;

import com.api.regie.models.PanneauxCampagne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanneauxCampagneRepository extends JpaRepository<PanneauxCampagne, UUID> {

    List<PanneauxCampagne> findAll();

    List<PanneauxCampagne> findByCampagneId(UUID campagneId);

    List<PanneauxCampagne> findByPanneauxId(UUID panneauxId);

    Optional<PanneauxCampagne> findByCampagneIdAndPanneauxId(UUID campagneId, UUID panneauxId);

    PanneauxCampagne save(PanneauxCampagne panneauxCampagne);

    Optional<PanneauxCampagne> findById(UUID id);

    void deleteByCampagneId(UUID campagneId);
}
