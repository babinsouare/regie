package com.api.regie.repository;

import com.api.regie.models.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModePaiementRepository extends JpaRepository<ModePaiement, UUID> {

    List<ModePaiement> findAll();

    Optional<ModePaiement> findByMode(String mode);

    Optional<ModePaiement> findByModeAndIdNot(String mode, UUID id);

    ModePaiement save(ModePaiement modePaiement);

    Optional<ModePaiement> findById(UUID id);
}