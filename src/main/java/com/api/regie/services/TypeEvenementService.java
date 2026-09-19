package com.api.regie.services;

import com.api.regie.models.TypeEvenement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeEvenementService {

     List<TypeEvenement> getAllTypeEvenements();

     TypeEvenement addTypeEvenement(TypeEvenement typeEvenement);

     Optional<TypeEvenement> findByType(String type);

     Optional<TypeEvenement> findByTypeAndIdNot(String type, UUID id);

     Optional<TypeEvenement> findById(UUID id);
}