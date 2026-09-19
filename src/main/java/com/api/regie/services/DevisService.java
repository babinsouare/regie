package com.api.regie.services;

import com.api.regie.models.Devis;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DevisService {

    List<Devis> getAllDevis();

    Devis addDevis(Devis devis);

    Devis createDevisForCampagne(UUID campagneId, UUID remiseId, Date dateValidite);

    Devis updateDevis(UUID idDevis, Date dateValidite, String observations, UUID remiseId);

    Optional<Devis> findByNumeroDevis(String numeroDevis);

    Optional<Devis> findByNumeroDevisAndIdNot(String numeroDevis, UUID id);

    List<Devis> findByClientId(UUID clientId);

    List<Devis> findByUserId(UUID userId);

    List<Devis> findByCampagneId(UUID campagneId);

    void deleteByCampagneId(UUID campagneId);

    Optional<Devis> findById(UUID id);
}
