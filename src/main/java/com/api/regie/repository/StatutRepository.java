package com.api.regie.repository;

import com.api.regie.models.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatutRepository extends JpaRepository<Statut, UUID> {

    List<Statut> findAll();

    Optional<Statut> findByCodeStatut(String codeStatut);

    Optional<Statut> findByCodeStatutAndIdNot(String codeStatut, UUID id);

    List<Statut> findByBtEnabled(Boolean btEnabled);

    List<Statut> findAllByOrderByOrdreAsc();

    Statut save(Statut statut);

    Optional<Statut> findById(UUID id);

    Optional<Statut> findByOrdre(Integer ordre);
}
