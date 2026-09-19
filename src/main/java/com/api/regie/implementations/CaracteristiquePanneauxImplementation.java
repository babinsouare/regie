package com.api.regie.implementations;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.repository.CaracteristiquePanneauxRepository;
import com.api.regie.services.CaracteristiquePanneauxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CaracteristiquePanneauxImplementation implements CaracteristiquePanneauxService {

    private final CaracteristiquePanneauxRepository caracteristiquePanneauxRepository;

    public CaracteristiquePanneauxImplementation(CaracteristiquePanneauxRepository caracteristiquePanneauxRepository) {
        this.caracteristiquePanneauxRepository = caracteristiquePanneauxRepository;
    }

    @Override
    public List<CaracteristiquePanneaux> getAllCaracteristiquePanneaux() {
        return caracteristiquePanneauxRepository.findAll();
    }

    @Override
    public CaracteristiquePanneaux addCaracteristiquePanneaux(CaracteristiquePanneaux caracteristiquePanneaux) {
        return caracteristiquePanneauxRepository.save(caracteristiquePanneaux);
    }

    @Override
    public List<CaracteristiquePanneaux> findByCategoriePanneauxId(UUID categorieId) {
        return caracteristiquePanneauxRepository.findByCategoriePanneauxId(categorieId);
    }

    @Override
    public Optional<CaracteristiquePanneaux> findByCaracteristiqueAndIdNot(String caracteristique, UUID id) {
        return caracteristiquePanneauxRepository.findByCaracteristiqueAndIdNot(caracteristique, id);
    }

    @Override
    public Optional<CaracteristiquePanneaux> findByCaracteristique(String caracteristique) {
        return caracteristiquePanneauxRepository.findByCaracteristique(caracteristique);
    }

    @Override
    public List<CaracteristiquePanneaux> findByBtEnabled(Boolean btEnabled) {
        return caracteristiquePanneauxRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public Optional<CaracteristiquePanneaux> findById(UUID id) {
        return caracteristiquePanneauxRepository.findById(id);
    }
}
