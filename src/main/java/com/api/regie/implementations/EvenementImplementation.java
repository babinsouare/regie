package com.api.regie.implementations;

import com.api.regie.models.Evenement;
import com.api.regie.repository.EvenementRepository;
import com.api.regie.services.EvenementService;
import com.api.regie.utils.Helpers;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EvenementImplementation implements EvenementService {

    private final EvenementRepository evenementRepository;

    public EvenementImplementation(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    public Evenement addEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public List<Evenement> findByPanneauId(UUID panneauId) {
        return evenementRepository.findByPanneauId(panneauId);
    }

    @Override
    public List<Evenement> findByTypeEvenementId(UUID typeEvenementId) {
        return evenementRepository.findByTypeEvenementId(typeEvenementId);
    }

    @Override
    public List<Evenement> findByUserId(UUID userId) {
        return evenementRepository.findByUserId(userId);
    }

    @Override
    public Optional<Evenement> findById(UUID id) {
        return evenementRepository.findById(id);
    }

    @Override
    public List<Object[]> aggregateParAgent(Date dateDebut, Date dateFin) {
        if (dateDebut == null && dateFin == null) return evenementRepository.aggregateParAgent();

        return evenementRepository.aggregateParAgentSurPeriode(borneDebut(dateDebut), borneFin(dateFin));
    }

    @Override
    public List<Object[]> aggregateParType(Date dateDebut, Date dateFin) {
        if (dateDebut == null && dateFin == null) return evenementRepository.aggregateParType();

        return evenementRepository.aggregateParTypeSurPeriode(borneDebut(dateDebut), borneFin(dateFin));
    }

    @Override
    public List<Object[]> aggregateParAgentEtType(Date dateDebut, Date dateFin) {
        if (dateDebut == null && dateFin == null) return evenementRepository.aggregateParAgentEtType();

        return evenementRepository.aggregateParAgentEtTypeSurPeriode(borneDebut(dateDebut), borneFin(dateFin));
    }

    /** Une seule borne fournie reste valide : l'autre est ouverte. */
    private Date borneDebut(Date dateDebut) {
        return dateDebut == null
                ? java.sql.Date.valueOf(java.time.LocalDate.of(2000, 1, 1))
                : Helpers.debutDeJournee(dateDebut);
    }

    private Date borneFin(Date dateFin) {
        return dateFin == null
                ? java.sql.Date.valueOf(java.time.LocalDate.of(2100, 12, 31))
                : Helpers.finDeJournee(dateFin);
    }
}
