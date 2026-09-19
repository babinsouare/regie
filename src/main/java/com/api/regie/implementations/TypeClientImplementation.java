package com.api.regie.implementations;

import com.api.regie.models.TypeClient;
import com.api.regie.repository.TypeClientRepository;
import com.api.regie.services.TypeClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TypeClientImplementation implements TypeClientService {

    private final TypeClientRepository typeClientRepository;

    public TypeClientImplementation(TypeClientRepository typeClientRepository) {
        this.typeClientRepository = typeClientRepository;
    }

    @Override
    public List<TypeClient> getAllTypeClients() {
        return typeClientRepository.findAll();
    }

    @Override
    public TypeClient addTypeClient(TypeClient typeClient) {
        return typeClientRepository.save(typeClient);
    }

    @Override
    public Optional<TypeClient> findByTypeClient(String typeClient) {
        return typeClientRepository.findByTypeClient(typeClient);
    }

    @Override
    public Optional<TypeClient> findByTypeClientAndIdNot(String typeClient, UUID id) {
        return typeClientRepository.findByTypeClientAndIdNot(typeClient, id);
    }

    @Override
    public Optional<TypeClient> findById(UUID id) {
        return typeClientRepository.findById(id);
    }
}