package com.api.regie.services;

import com.api.regie.models.SoutraConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SoutraConfigService {

    List<SoutraConfig> getAllSoutraConfigs();

    Optional<SoutraConfig> findById(UUID id);

    Optional<SoutraConfig> findActive();

    SoutraConfig addSoutraConfig(SoutraConfig soutraConfig);

    SoutraConfig updateSoutraConfig(SoutraConfig soutraConfig);

    SoutraConfig activer(UUID id);
}
