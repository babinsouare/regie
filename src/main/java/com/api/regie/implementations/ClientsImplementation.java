package com.api.regie.implementations;

import com.api.regie.models.Clients;
import com.api.regie.repository.ClientsRepository;
import com.api.regie.services.ClientsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientsImplementation implements ClientsService {

    private final ClientsRepository clientsRepository;

    public ClientsImplementation(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Override
    public List<Clients> getAllClients() {
        return clientsRepository.findAll();
    }

    @Override
    public Clients addClients(Clients clients) {
        return clientsRepository.save(clients);
    }

    @Override
    public Optional<Clients> findByEmailResponsable(String emailResponsable) {
        return clientsRepository.findByEmailResponsable(emailResponsable);
    }

    @Override
    public Optional<Clients> findByEmailResponsableAndIdNot(String emailResponsable, UUID id) {
        return clientsRepository.findByEmailResponsableAndIdNot(emailResponsable, id);
    }

    @Override
    public Optional<Clients> findByTelephoneResponsable(String telephoneResponsable) {
        return clientsRepository.findByTelephoneResponsable(telephoneResponsable);
    }

    @Override
    public Optional<Clients> findByTelephoneResponsableAndIdNot(String telephoneResponsable, UUID id) {
        return clientsRepository.findByTelephoneResponsableAndIdNot(telephoneResponsable, id);
    }

    @Override
    public Optional<Clients> findByDenomination(String denomination) {
        return clientsRepository.findByDenomination(denomination);
    }

    @Override
    public Optional<Clients> findByDenominationAndIdNot(String denomination, UUID id) {
        return clientsRepository.findByDenominationAndIdNot(denomination, id);
    }

    @Override
    public Optional<Clients> findById(UUID id) {
        return clientsRepository.findById(id);
    }
}