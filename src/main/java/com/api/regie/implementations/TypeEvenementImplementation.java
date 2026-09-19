package com.api.regie.implementations;

import com.api.regie.models.TypeEvenement;
import com.api.regie.repository.TypeEvenementRepository;
import com.api.regie.services.TypeEvenementService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeEvenementImplementation implements TypeEvenementService {

    private final TypeEvenementRepository typeEvenementRepository;

    public TypeEvenementImplementation(TypeEvenementRepository typeEvenementRepository) {
        this.typeEvenementRepository = typeEvenementRepository;
    }

    @Override
    public List<TypeEvenement> getAllTypeEvenements() {
        return typeEvenementRepository.findAll();
    }

    @Override
    public TypeEvenement addTypeEvenement(TypeEvenement typeEvenement) {
        return typeEvenementRepository.save(typeEvenement);
    }

    @Override
    public Optional<TypeEvenement> findByType(String type) {
        return typeEvenementRepository.findByType(type);
    }

    @Override
    public Optional<TypeEvenement> findByTypeAndIdNot(String type, UUID id) {
        return typeEvenementRepository.findByTypeAndIdNot(type, id);
    }

    @Override
    public Optional<TypeEvenement> findById(UUID id) {
        return typeEvenementRepository.findById(id);
    }
}