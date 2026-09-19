package com.api.regie.repository;

import com.api.regie.models.Devis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DevisRepository extends JpaRepository<Devis, UUID> {

    List<Devis> findAll();

    Optional<Devis> findByNumeroDevis(String numeroDevis);

    Optional<Devis> findByNumeroDevisAndIdNot(String numeroDevis, UUID id);

    List<Devis> findByClientId(UUID clientId);

    List<Devis> findByUserId(UUID userId);

    List<Devis> findByCampagneId(UUID campagneId);

    void deleteByCampagneId(UUID campagneId);

    Devis save(Devis devis);

    Optional<Devis> findById(UUID id);
}
