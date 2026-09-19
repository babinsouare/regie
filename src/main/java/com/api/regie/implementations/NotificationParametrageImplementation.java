package com.api.regie.implementations;

import com.api.regie.models.NotificationParametrage;
import com.api.regie.repository.NotificationParametrageRepository;
import com.api.regie.services.NotificationParametrageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationParametrageImplementation implements NotificationParametrageService {

    private final NotificationParametrageRepository parametrageRepository;

    public NotificationParametrageImplementation(NotificationParametrageRepository parametrageRepository) {
        this.parametrageRepository = parametrageRepository;
    }

    @Override
    public List<NotificationParametrage> getAllParametrages() {
        return parametrageRepository.findAll();
    }

    @Override
    public NotificationParametrage save(NotificationParametrage parametrage) {
        return parametrageRepository.save(parametrage);
    }

    @Override
    public Optional<NotificationParametrage> findById(UUID id) {
        return parametrageRepository.findById(id);
    }

    @Override
    public Optional<NotificationParametrage> findByCode(String code) {
        return parametrageRepository.findByCode(code);
    }

    @Override
    public Optional<NotificationParametrage> findByCodeAndIdNot(String code, UUID id) {
        return parametrageRepository.findByCodeAndIdNot(code, id);
    }

    @Override
    public List<NotificationParametrage> findByEvenement(String evenement) {
        return parametrageRepository.findByEvenement(evenement);
    }

    @Override
    public List<NotificationParametrage> findByBtEnabled(Boolean btEnabled) {
        return parametrageRepository.findByBtEnabled(btEnabled);
    }
}
