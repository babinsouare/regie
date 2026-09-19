package com.api.regie.services;

import com.api.regie.models.CampagneStatut;
import com.api.regie.models.Campagnes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampagneStatutService {

    List<CampagneStatut> getAllCampagneStatuts();

    CampagneStatut addCampagneStatut(CampagneStatut campagneStatut);

    List<CampagneStatut> findByCampagneId(UUID campagneId);

    List<CampagneStatut> findByCampagneIdOrderByDateChangementDesc(UUID campagneId);

    List<CampagneStatut> findByStatutId(UUID statutId);

    List<CampagneStatut> findByUserId(UUID userId);

    Optional<CampagneStatut> findById(UUID id);

    Optional<CampagneStatut> findByCampagneAndBtEnabled(Campagnes campagne, Boolean btEnabled);

    void disableCampagneStatut(CampagneStatut campagneStatut);
}
