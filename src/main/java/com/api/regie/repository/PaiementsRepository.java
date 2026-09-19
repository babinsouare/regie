package com.api.regie.repository;

import com.api.regie.models.Paiements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementsRepository extends JpaRepository<Paiements, UUID> {

    List<Paiements> findAll();

    Optional<Paiements> findByReference(String reference);

    Optional<Paiements> findByReferenceAndIdNot(String reference, UUID id);

    List<Paiements> findByFactureId(UUID factureId);

    List<Paiements> findByModePaiementId(UUID modePaiementId);

    List<Paiements> findByUserId(UUID userId);

    List<Paiements> findByStatut(String statut);

    /** Paiements figés dans un statut depuis un certain temps, pour le rattrapage Soutra. */
    List<Paiements> findByStatutAndDtCreatedBefore(String statut, Date date);

    List<Paiements> findAllByOrderByDtCreatedDesc(Pageable pageable);

    Paiements save(Paiements paiements);

    Optional<Paiements> findById(UUID id);
}
