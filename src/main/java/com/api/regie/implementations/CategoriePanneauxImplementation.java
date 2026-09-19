package com.api.regie.implementations;

import com.api.regie.models.CategoriePanneaux;
import com.api.regie.repository.CategoriePanneauxRepository;
import com.api.regie.services.CategoriePanneauxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoriePanneauxImplementation implements CategoriePanneauxService {

    private final CategoriePanneauxRepository categoriePanneauxRepository;

    public CategoriePanneauxImplementation(CategoriePanneauxRepository categoriePanneauxRepository) {
        this.categoriePanneauxRepository = categoriePanneauxRepository;
    }

    @Override
    public List<CategoriePanneaux> getAllCategoriePanneaux() {
        return categoriePanneauxRepository.findAll();
    }

    @Override
    public CategoriePanneaux addCategoriePanneaux(CategoriePanneaux categoriePanneaux) {
        return categoriePanneauxRepository.save(categoriePanneaux);
    }

    @Override
    public Optional<CategoriePanneaux> findByCategorie(String categorie) {
        return categoriePanneauxRepository.findByCategorie(categorie);
    }

    @Override
    public Optional<CategoriePanneaux> findByCategorieAndIdNot(String categorie, UUID id) {
        return categoriePanneauxRepository.findByCategorieAndIdNot(categorie, id);
    }

    @Override
    public List<CategoriePanneaux> findByBtEnabled(Boolean btEnabled) {
        return categoriePanneauxRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public Optional<CategoriePanneaux> findById(UUID id) {
        return categoriePanneauxRepository.findById(id);
    }
}
