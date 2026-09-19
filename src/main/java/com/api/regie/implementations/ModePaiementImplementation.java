package com.api.regie.implementations;

import com.api.regie.models.ModePaiement;
import com.api.regie.repository.ModePaiementRepository;
import com.api.regie.services.ModePaiementService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModePaiementImplementation implements ModePaiementService {

    private final ModePaiementRepository modePaiementRepository;

    public ModePaiementImplementation(ModePaiementRepository modePaiementRepository) {
        this.modePaiementRepository = modePaiementRepository;
    }

    @Override
    public List<ModePaiement> getAllModePaiements() {
        return modePaiementRepository.findAll();
    }

    @Override
    public ModePaiement addModePaiement(ModePaiement modePaiement) {
        return modePaiementRepository.save(modePaiement);
    }

    @Override
    public Optional<ModePaiement> findByMode(String mode) {
        return modePaiementRepository.findByMode(mode);
    }

    @Override
    public Optional<ModePaiement> findByModeAndIdNot(String mode, UUID id) {
        return modePaiementRepository.findByModeAndIdNot(mode, id);
    }

    @Override
    public Optional<ModePaiement> findById(UUID id) {
        return modePaiementRepository.findById(id);
    }
}