package com.api.regie.repository;

import com.api.regie.models.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeClientRepository extends JpaRepository<TypeClient, UUID> {

    List<TypeClient> findAll();

    Optional<TypeClient> findByTypeClient(String typeClient);

    Optional<TypeClient> findByTypeClientAndIdNot(String typeClient, UUID id);

    TypeClient save(TypeClient typeClient);

    Optional<TypeClient> findById(UUID id);
}