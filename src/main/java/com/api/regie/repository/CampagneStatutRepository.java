package com.api.regie.repository;

import com.api.regie.models.CampagneStatut;
import com.api.regie.models.Campagnes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampagneStatutRepository extends JpaRepository<CampagneStatut, UUID> {

    List<CampagneStatut> findAll();

    List<CampagneStatut> findByCampagneId(UUID campagneId);

    List<CampagneStatut> findByCampagneIdOrderByDateChangementDesc(UUID campagneId);

    List<CampagneStatut> findByStatutId(UUID statutId);

    List<CampagneStatut> findByUserId(UUID userId);

    CampagneStatut save(CampagneStatut campagneStatut);

    Optional<CampagneStatut> findById(UUID id);

    Optional<CampagneStatut> findByCampagneAndBtEnabled(Campagnes campagne, Boolean btEnabled);
}
