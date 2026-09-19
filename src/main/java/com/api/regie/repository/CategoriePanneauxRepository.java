package com.api.regie.repository;

import com.api.regie.models.CategoriePanneaux;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriePanneauxRepository extends JpaRepository<CategoriePanneaux, UUID> {

    List<CategoriePanneaux> findAll();

    Optional<CategoriePanneaux> findByCategorie(String categorie);

    Optional<CategoriePanneaux> findByCategorieAndIdNot(String categorie, UUID id);

    List<CategoriePanneaux> findByBtEnabled(Boolean btEnabled);

    CategoriePanneaux save(CategoriePanneaux categoriePanneaux);

    Optional<CategoriePanneaux> findById(UUID id);
}
