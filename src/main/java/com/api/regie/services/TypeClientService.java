package com.api.regie.services;

import com.api.regie.models.TypeClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeClientService {

     List<TypeClient> getAllTypeClients();

     TypeClient addTypeClient(TypeClient typeClient);

     Optional<TypeClient> findByTypeClient(String typeClient);

     Optional<TypeClient> findByTypeClientAndIdNot(String typeClient, UUID id);

     Optional<TypeClient> findById(UUID id);
}