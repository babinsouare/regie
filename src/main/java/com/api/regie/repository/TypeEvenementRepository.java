package com.api.regie.repository;

import com.api.regie.models.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeEvenementRepository extends JpaRepository<TypeEvenement, UUID> {

    List<TypeEvenement> findAll();

    Optional<TypeEvenement> findByType(String type);

    Optional<TypeEvenement> findByTypeAndIdNot(String type, UUID id);

    TypeEvenement save(TypeEvenement typeEvenement);

    Optional<TypeEvenement> findById(UUID id);
}