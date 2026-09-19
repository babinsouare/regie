package com.api.regie.services;

import com.api.regie.models.NotificationParametrage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationParametrageService {

    List<NotificationParametrage> getAllParametrages();

    NotificationParametrage save(NotificationParametrage parametrage);

    Optional<NotificationParametrage> findById(UUID id);

    Optional<NotificationParametrage> findByCode(String code);

    Optional<NotificationParametrage> findByCodeAndIdNot(String code, UUID id);

    List<NotificationParametrage> findByEvenement(String evenement);

    List<NotificationParametrage> findByBtEnabled(Boolean btEnabled);
}
