package com.api.regie.services;

import com.api.regie.models.Secteur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecteurService {

    List<Secteur> getAllSecteur();

    Optional<Secteur> findById(UUID id);

    Optional<Secteur> findBySecteur(String secteur);

    Optional<Secteur> findBySecteurAndIdNot(String secteur, UUID id);

    Secteur addSecteur(Secteur secteur);

    List<Secteur> findByQuartierId(UUID idQuartier);
}