package com.api.regie.services;

import com.api.regie.models.Evenement;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvenementService {

    List<Evenement> getAllEvenements();

    Evenement addEvenement(Evenement evenement);

    List<Evenement> findByPanneauId(UUID panneauId);

    List<Evenement> findByTypeEvenementId(UUID typeEvenementId);

    List<Evenement> findByUserId(UUID userId);

    Optional<Evenement> findById(UUID id);

    /** Les bornes sont facultatives : passer null des deux côtés agrège la totalité des événements. */
    List<Object[]> aggregateParAgent(Date dateDebut, Date dateFin);

    List<Object[]> aggregateParType(Date dateDebut, Date dateFin);

    List<Object[]> aggregateParAgentEtType(Date dateDebut, Date dateFin);
}
