package com.api.regie.services;

import com.api.regie.models.PanneauxCampagne;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanneauxCampagneService {

    List<PanneauxCampagne> getAllPanneauxCampagnes();

    PanneauxCampagne addPanneauxCampagne(PanneauxCampagne panneauxCampagne);

    List<PanneauxCampagne> findByCampagneId(UUID campagneId);

    List<PanneauxCampagne> findByPanneauxId(UUID panneauxId);

    Optional<PanneauxCampagne> findByCampagneIdAndPanneauxId(UUID campagneId, UUID panneauxId);

    Optional<PanneauxCampagne> findById(UUID id);

    void deleteByCampagneId(UUID campagneId);
}
