package com.api.regie.services;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.models.Tarifs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarifsService {

    List<Tarifs> getAllTarifs();

    Tarifs addTarifs(Tarifs tarifs);

    List<Tarifs> findByCaracteristiquePanneauxId(UUID caracteristiquePanneauxId);

    List<Tarifs> findByCaracteristiquePanneaux(CaracteristiquePanneaux caracteristiquePanneaux);

    List<Tarifs> findByBtEnabled(Boolean btEnabled);

    Optional<Tarifs> findById(UUID id);
}
