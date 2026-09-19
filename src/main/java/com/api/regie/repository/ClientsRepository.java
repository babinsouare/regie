package com.api.regie.repository;

import com.api.regie.models.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientsRepository extends JpaRepository<Clients, UUID> {

    List<Clients> findAll();

    Optional<Clients> findByEmailResponsable(String emailResponsable);

    Optional<Clients> findByEmailResponsableAndIdNot(String emailResponsable, UUID id);

    Optional<Clients> findByTelephoneResponsable(String telephoneResponsable);

    Optional<Clients> findByTelephoneResponsableAndIdNot(String telephoneResponsable, UUID id);

    Optional<Clients> findByDenomination(String denomination);

    Optional<Clients> findByDenominationAndIdNot(String denomination, UUID id);

    Clients save(Clients clients);

    Optional<Clients> findById(UUID id);
}