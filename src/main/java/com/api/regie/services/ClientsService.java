package com.api.regie.services;

import com.api.regie.models.Clients;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientsService {

     List<Clients> getAllClients();

     Clients addClients(Clients clients);

     Optional<Clients> findByEmailResponsable(String emailResponsable);

     Optional<Clients> findByEmailResponsableAndIdNot(String emailResponsable, UUID id);

     Optional<Clients> findByTelephoneResponsable(String telephoneResponsable);

     Optional<Clients> findByTelephoneResponsableAndIdNot(String telephoneResponsable, UUID id);

     Optional<Clients> findByDenomination(String denomination);

     Optional<Clients> findByDenominationAndIdNot(String denomination, UUID id);

     Optional<Clients> findById(UUID id);
}