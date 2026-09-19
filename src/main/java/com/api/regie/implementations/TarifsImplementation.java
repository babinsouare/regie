package com.api.regie.implementations;

import com.api.regie.models.CaracteristiquePanneaux;
import com.api.regie.models.Tarifs;
import com.api.regie.repository.TarifsRepository;
import com.api.regie.services.TarifsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TarifsImplementation implements TarifsService {

    private final TarifsRepository tarifsRepository;

    public TarifsImplementation(TarifsRepository tarifsRepository) {
        this.tarifsRepository = tarifsRepository;
    }

    @Override
    public List<Tarifs> getAllTarifs() {
        return tarifsRepository.findAll();
    }

    @Override
    public Tarifs addTarifs(Tarifs tarifs) {
        return tarifsRepository.save(tarifs);
    }

    @Override
    public List<Tarifs> findByCaracteristiquePanneauxId(UUID caracteristiquePanneauxId) {
        return tarifsRepository.findByCaracteristiquePanneauxId(caracteristiquePanneauxId);
    }

    @Override
    public List<Tarifs> findByCaracteristiquePanneaux(CaracteristiquePanneaux caracteristiquePanneaux) {
        return tarifsRepository.findByCaracteristiquePanneaux(caracteristiquePanneaux);
    }

    @Override
    public List<Tarifs> findByBtEnabled(Boolean btEnabled) {
        return tarifsRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public Optional<Tarifs> findById(UUID id) {
        return tarifsRepository.findById(id);
    }
}
