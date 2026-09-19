package com.api.regie.implementations;

import com.api.regie.models.SoutraConfig;
import com.api.regie.repository.SoutraConfigRepository;
import com.api.regie.services.SoutraConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SoutraConfigImplementation implements SoutraConfigService {

    private final SoutraConfigRepository soutraConfigRepository;

    public SoutraConfigImplementation(SoutraConfigRepository soutraConfigRepository) {
        this.soutraConfigRepository = soutraConfigRepository;
    }

    @Override
    public List<SoutraConfig> getAllSoutraConfigs() {
        return soutraConfigRepository.findAll();
    }

    @Override
    public Optional<SoutraConfig> findById(UUID id) {
        return soutraConfigRepository.findById(id);
    }

    @Override
    public Optional<SoutraConfig> findActive() {
        return soutraConfigRepository.findByActifTrue();
    }

    @Override
    public SoutraConfig addSoutraConfig(SoutraConfig soutraConfig) {
        soutraConfig.setActif(false);
        return soutraConfigRepository.save(soutraConfig);
    }

    @Override
    public SoutraConfig updateSoutraConfig(SoutraConfig soutraConfig) {
        return soutraConfigRepository.save(soutraConfig);
    }

    @Override
    @Transactional
    public SoutraConfig activer(UUID id) {
        SoutraConfig target = soutraConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration Soutra introuvable."));

        soutraConfigRepository.findByActifTrue().ifPresent(current -> {
            if (!current.getId().equals(id)) {
                current.setActif(false);
                soutraConfigRepository.save(current);
            }
        });

        target.setActif(true);
        return soutraConfigRepository.save(target);
    }
}
