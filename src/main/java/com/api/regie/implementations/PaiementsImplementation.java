package com.api.regie.implementations;

import com.api.regie.models.Paiements;
import com.api.regie.repository.PaiementsRepository;
import com.api.regie.services.PaiementsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaiementsImplementation implements PaiementsService {

    private final PaiementsRepository paiementsRepository;

    public PaiementsImplementation(PaiementsRepository paiementsRepository) {
        this.paiementsRepository = paiementsRepository;
    }

    @Override
    public List<Paiements> getAllPaiements() {
        return paiementsRepository.findAll();
    }

    @Override
    public Paiements addPaiements(Paiements paiements) {
        return paiementsRepository.save(paiements);
    }

    @Override
    public Optional<Paiements> findByReference(String reference) {
        return paiementsRepository.findByReference(reference);
    }

    @Override
    public Optional<Paiements> findByReferenceAndIdNot(String reference, UUID id) {
        return paiementsRepository.findByReferenceAndIdNot(reference, id);
    }

    @Override
    public List<Paiements> findByFactureId(UUID factureId) {
        return paiementsRepository.findByFactureId(factureId);
    }

    @Override
    public List<Paiements> findByModePaiementId(UUID modePaiementId) {
        return paiementsRepository.findByModePaiementId(modePaiementId);
    }

    @Override
    public List<Paiements> findByUserId(UUID userId) {
        return paiementsRepository.findByUserId(userId);
    }

    @Override
    public List<Paiements> findByStatut(String statut) {
        return paiementsRepository.findByStatut(statut);
    }

    @Override
    public List<Paiements> findByStatutAndDtCreatedBefore(String statut, Date date) {
        return paiementsRepository.findByStatutAndDtCreatedBefore(statut, date);
    }

    @Override
    public List<Paiements> findDerniersPaiements(int limit) {
        return paiementsRepository.findAllByOrderByDtCreatedDesc(PageRequest.of(0, limit));
    }

    @Override
    public Optional<Paiements> findById(UUID id) {
        return paiementsRepository.findById(id);
    }
}
