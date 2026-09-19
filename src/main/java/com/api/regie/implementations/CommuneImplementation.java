package com.api.regie.implementations;

import com.api.regie.models.Commune;
import com.api.regie.repository.CommuneRepository;
import com.api.regie.services.CommuneService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommuneImplementation implements CommuneService {

    private final CommuneRepository communeRepository;

    public CommuneImplementation(CommuneRepository communeRepository) {
        this.communeRepository = communeRepository;
    }

    @Override
    public List<Commune> getAllCommune() {
        return communeRepository.findAll();
    }

    @Override
    public Optional<Commune> findById(UUID id) {
        return communeRepository.findById(id);
    }

    @Override
    public Optional<Commune> findByCommune(String commune) {
        return communeRepository.findByCommune(commune);
    }

    @Override
    public Optional<Commune> findByCommuneAndIdNot(String commune, UUID id) {
        return communeRepository.findByCommuneAndIdNot(commune, id);
    }

    @Override
    public Commune addCommune(Commune commune) {
        return communeRepository.save(commune);
    }

    @Override
    public List<Commune> findByRegionId(UUID idRegion) {
        return communeRepository.findByRegionId(idRegion);
    }

}
