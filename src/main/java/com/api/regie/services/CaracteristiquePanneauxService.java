package com.api.regie.services;

import com.api.regie.models.CaracteristiquePanneaux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaracteristiquePanneauxService {

    List<CaracteristiquePanneaux> getAllCaracteristiquePanneaux();

    CaracteristiquePanneaux addCaracteristiquePanneaux(CaracteristiquePanneaux caracteristiquePanneaux);

    List<CaracteristiquePanneaux> findByCategoriePanneauxId(UUID categorieId);

    Optional<CaracteristiquePanneaux> findByCaracteristiqueAndIdNot(String caracteristique, UUID id);

    Optional<CaracteristiquePanneaux> findByCaracteristique(String caracteristique);

    List<CaracteristiquePanneaux> findByBtEnabled(Boolean btEnabled);

    Optional<CaracteristiquePanneaux> findById(UUID id);
}
