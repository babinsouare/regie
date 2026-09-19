package com.api.regie.implementations;

import com.api.regie.models.PanneauxCampagne;
import com.api.regie.repository.PanneauxCampagneRepository;
import com.api.regie.services.PanneauxCampagneService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PanneauxCampagneImplementation implements PanneauxCampagneService {

    private final PanneauxCampagneRepository panneauxCampagneRepository;

    public PanneauxCampagneImplementation(PanneauxCampagneRepository panneauxCampagneRepository) {
        this.panneauxCampagneRepository = panneauxCampagneRepository;
    }

    @Override
    public List<PanneauxCampagne> getAllPanneauxCampagnes() {
        return panneauxCampagneRepository.findAll();
    }

    @Override
    public PanneauxCampagne addPanneauxCampagne(PanneauxCampagne panneauxCampagne) {
        return panneauxCampagneRepository.save(panneauxCampagne);
    }

    @Override
    public List<PanneauxCampagne> findByCampagneId(UUID campagneId) {
        return panneauxCampagneRepository.findByCampagneId(campagneId);
    }

    @Override
    public List<PanneauxCampagne> findByPanneauxId(UUID panneauxId) {
        return panneauxCampagneRepository.findByPanneauxId(panneauxId);
    }

    @Override
    public Optional<PanneauxCampagne> findByCampagneIdAndPanneauxId(UUID campagneId, UUID panneauxId) {
        return panneauxCampagneRepository.findByCampagneIdAndPanneauxId(campagneId, panneauxId);
    }

    @Override
    public Optional<PanneauxCampagne> findById(UUID id) {
        return panneauxCampagneRepository.findById(id);
    }

    @Override
    public void deleteByCampagneId(UUID campagneId) {
        panneauxCampagneRepository.deleteByCampagneId(campagneId);
    }
}
