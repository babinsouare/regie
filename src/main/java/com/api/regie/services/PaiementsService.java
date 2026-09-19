package com.api.regie.services;

import com.api.regie.models.Paiements;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementsService {

    List<Paiements> getAllPaiements();

    Paiements addPaiements(Paiements paiements);

    Optional<Paiements> findByReference(String reference);

    Optional<Paiements> findByReferenceAndIdNot(String reference, UUID id);

    List<Paiements> findByFactureId(UUID factureId);

    List<Paiements> findByModePaiementId(UUID modePaiementId);

    List<Paiements> findByUserId(UUID userId);

    List<Paiements> findByStatut(String statut);

    List<Paiements> findByStatutAndDtCreatedBefore(String statut, Date date);

    List<Paiements> findDerniersPaiements(int limit);

    Optional<Paiements> findById(UUID id);
}
