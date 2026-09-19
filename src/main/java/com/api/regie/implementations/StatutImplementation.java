package com.api.regie.implementations;

import com.api.regie.models.Statut;
import com.api.regie.repository.StatutRepository;
import com.api.regie.services.StatutService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StatutImplementation implements StatutService {

    private final StatutRepository statutRepository;

    public StatutImplementation(StatutRepository statutRepository) {
        this.statutRepository = statutRepository;
    }

    @Override
    public List<Statut> getAllStatuts() {
        return statutRepository.findAll();
    }

    @Override
    public Statut addStatut(Statut statut) {
        return statutRepository.save(statut);
    }

    @Override
    public Optional<Statut> findByCodeStatut(String codeStatut) {
        return statutRepository.findByCodeStatut(codeStatut);
    }

    @Override
    public Optional<Statut> findByCodeStatutAndIdNot(String codeStatut, UUID id) {
        return statutRepository.findByCodeStatutAndIdNot(codeStatut, id);
    }

    @Override
    public List<Statut> findByBtEnabled(Boolean btEnabled) {
        return statutRepository.findByBtEnabled(btEnabled);
    }

    @Override
    public List<Statut> findAllByOrderByOrdreAsc() {
        return statutRepository.findAllByOrderByOrdreAsc();
    }

    @Override
    public Optional<Statut> findById(UUID id) {
        return statutRepository.findById(id);
    }

    @Override
    public Optional<Statut> findByOrdre(Integer ordre) {
        return statutRepository.findByOrdre(ordre);
    }
}
