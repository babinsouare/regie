package com.api.regie.services;

import com.api.regie.models.CategoriePanneaux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriePanneauxService {

    List<CategoriePanneaux> getAllCategoriePanneaux();

    CategoriePanneaux addCategoriePanneaux(CategoriePanneaux categoriePanneaux);

    Optional<CategoriePanneaux> findByCategorie(String categorie);

    Optional<CategoriePanneaux> findByCategorieAndIdNot(String categorie, UUID id);

    List<CategoriePanneaux> findByBtEnabled(Boolean btEnabled);

    Optional<CategoriePanneaux> findById(UUID id);
}
