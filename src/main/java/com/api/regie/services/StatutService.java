package com.api.regie.services;

import com.api.regie.models.Statut;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatutService {

    List<Statut> getAllStatuts();

    Statut addStatut(Statut statut);

    Optional<Statut> findByCodeStatut(String codeStatut);

    Optional<Statut> findByCodeStatutAndIdNot(String codeStatut, UUID id);

    List<Statut> findByBtEnabled(Boolean btEnabled);

    List<Statut> findAllByOrderByOrdreAsc();

    Optional<Statut> findById(UUID id);

    Optional<Statut> findByOrdre(Integer ordre);
}
