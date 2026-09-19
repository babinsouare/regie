package com.api.regie.implementations;

import com.api.regie.models.CampagneStatut;
import com.api.regie.models.Campagnes;
import com.api.regie.repository.CampagneStatutRepository;
import com.api.regie.services.CampagneStatutService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampagneStatutImplementation implements CampagneStatutService {

    private final CampagneStatutRepository campagneStatutRepository;

    public CampagneStatutImplementation(CampagneStatutRepository campagneStatutRepository) {
        this.campagneStatutRepository = campagneStatutRepository;
    }

    @Override
    public List<CampagneStatut> getAllCampagneStatuts() {
        return campagneStatutRepository.findAll();
    }

    @Override
    public CampagneStatut addCampagneStatut(CampagneStatut campagneStatut) {
        return campagneStatutRepository.save(campagneStatut);
    }

    @Override
    public List<CampagneStatut> findByCampagneId(UUID campagneId) {
        return campagneStatutRepository.findByCampagneId(campagneId);
    }

    @Override
    public List<CampagneStatut> findByCampagneIdOrderByDateChangementDesc(UUID campagneId) {
        return campagneStatutRepository.findByCampagneIdOrderByDateChangementDesc(campagneId);
    }

    @Override
    public List<CampagneStatut> findByStatutId(UUID statutId) {
        return campagneStatutRepository.findByStatutId(statutId);
    }

    @Override
    public List<CampagneStatut> findByUserId(UUID userId) {
        return campagneStatutRepository.findByUserId(userId);
    }

    @Override
    public Optional<CampagneStatut> findById(UUID id) {
        return campagneStatutRepository.findById(id);
    }

    @Override
    public Optional<CampagneStatut> findByCampagneAndBtEnabled(Campagnes campagne, Boolean btEnabled) {
        return campagneStatutRepository.findByCampagneAndBtEnabled(campagne, btEnabled);
    }

    @Override
    public void disableCampagneStatut(CampagneStatut campagneStatut) {
        campagneStatut.setBtEnabled(false);
        campagneStatutRepository.save(campagneStatut);
    }
}
