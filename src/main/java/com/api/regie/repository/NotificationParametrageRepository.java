package com.api.regie.repository;

import com.api.regie.models.NotificationParametrage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationParametrageRepository extends JpaRepository<NotificationParametrage, UUID> {

    List<NotificationParametrage> findAll();

    Optional<NotificationParametrage> findByCode(String code);

    Optional<NotificationParametrage> findByCodeAndIdNot(String code, UUID id);

    List<NotificationParametrage> findByEvenement(String evenement);

    List<NotificationParametrage> findByEvenementAndBtEnabled(String evenement, Boolean btEnabled);

    List<NotificationParametrage> findByBtEnabled(Boolean btEnabled);

    NotificationParametrage save(NotificationParametrage parametrage);

    Optional<NotificationParametrage> findById(UUID id);
}
