package com.api.regie.implementations;

import com.api.regie.models.Commune;
import com.api.regie.models.Quartier;
import com.api.regie.repository.QuartierRepository;
import com.api.regie.services.QuartierService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuartierImplementation implements QuartierService {

    private final QuartierRepository quartierRepository;

    public QuartierImplementation(QuartierRepository quartierRepository) {
        this.quartierRepository = quartierRepository;
    }

    @Override
    public List<Quartier> getAllQuartier() {
        return quartierRepository.findAll();
    }

    @Override
    public Optional<Quartier> findById(UUID id) {
        return quartierRepository.findById(id);
    }

    @Override
    public Optional<Quartier> findByQuartier(String quartier) {
        return quartierRepository.findByQuartier(quartier);
    }

    @Override
    public Optional<Quartier> findByQuartierAndIdNot(String quartier, UUID id) {
        return quartierRepository.findByQuartierAndIdNot(quartier, id);
    }

    @Override
    public Optional<Quartier> findByQuartierAndIdNotAndCommune(String quartier, UUID id, Commune commune) {
        return quartierRepository.findByQuartierAndIdNotAndCommune(quartier, id, commune);
    }

    @Override
    public Optional<Quartier> findByQuartierAndCommune(String quartier, Commune commune) {
        return quartierRepository.findByQuartierAndCommune(quartier, commune);
    }

    @Override
    public Quartier addQuartier(Quartier quartier) {
        return quartierRepository.save(quartier);
    }

    @Override
    public List<Quartier> findByCommuneId(UUID idCommune) {
        return quartierRepository.findByCommuneId(idCommune);
    }

}