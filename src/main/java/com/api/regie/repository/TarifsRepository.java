package com.api.regie.repository;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.models.Tarifs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarifsRepository extends JpaRepository<Tarifs, UUID> {

    List<Tarifs> findAll();

    List<Tarifs> findByCaracteristiquePanneauxId(UUID caracteristiquePanneauxId);

    List<Tarifs> findByCaracteristiquePanneaux(CaracteristiquePanneaux caracteristique);

    List<Tarifs> findByBtEnabled(Boolean btEnabled);

    Tarifs save(Tarifs tarifs);

    Optional<Tarifs> findById(UUID id);
}
