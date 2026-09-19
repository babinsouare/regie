package com.api.regie.implementations;

import com.api.regie.models.Remise;
import com.api.regie.repository.RemiseRepository;
import com.api.regie.services.RemiseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RemiseImplementation implements RemiseService {

    private final RemiseRepository remiseRepository;

    public RemiseImplementation(RemiseRepository remiseRepository) {
        this.remiseRepository = remiseRepository;
    }

    @Override
    public List<Remise> getAllRemises() {
        return remiseRepository.findAll();
    }

    @Override
    public Remise addRemise(Remise remise) {
        return remiseRepository.save(remise);
    }

    @Override
    public Optional<Remise> findByTypeRemise(String typeRemise) {
        return remiseRepository.findByTypeRemise(typeRemise);
    }

    @Override
    public Optional<Remise> findByTypeRemiseAndIdNot(String typeRemise, UUID id) {
        return remiseRepository.findByTypeRemiseAndIdNot(typeRemise, id);
    }

    @Override
    public Optional<Remise> findById(UUID id) {
        return remiseRepository.findById(id);
    }
}