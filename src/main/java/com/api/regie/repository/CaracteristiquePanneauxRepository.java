package com.api.regie.repository;

import com.api.regie.models.CaracteristiquePanneaux;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaracteristiquePanneauxRepository extends JpaRepository<CaracteristiquePanneaux, UUID> {

    List<CaracteristiquePanneaux> findAll();

    List<CaracteristiquePanneaux> findByCategoriePanneauxId(UUID categorieId);

    Optional<CaracteristiquePanneaux> findByCaracteristiqueAndIdNot(String caracteristique, UUID id);

    Optional<CaracteristiquePanneaux> findByCaracteristique(String caracteristique);

    List<CaracteristiquePanneaux> findByBtEnabled(Boolean btEnabled);

    CaracteristiquePanneaux save(CaracteristiquePanneaux caracteristiquePanneaux);

    Optional<CaracteristiquePanneaux> findById(UUID id);
}
