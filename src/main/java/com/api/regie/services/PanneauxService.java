package com.api.regie.services;

import com.api.regie.models.Panneaux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanneauxService {

    List<Panneaux> getAllPanneaux();

    Panneaux addPanneaux(Panneaux panneaux);

    Optional<Panneaux> findByReference(String reference);

    Optional<Panneaux> findByReferenceAndFace(String reference, String face);

    Optional<Panneaux> findByReferenceAndFaceAndIdNot(String reference, String face, UUID id);

    List<Panneaux> findBySecteurId(UUID secteurId);

    List<Panneaux> findBySecteurQuartierId(UUID quartierId);

    List<Panneaux> findBySecteurQuartierCommuneId(UUID communeId);

    List<Panneaux> findBySecteurQuartierCommuneRegionId(UUID regionId);

    List<Panneaux> findByCaracteristiquePanneauxId(UUID caracteristiqueId);

    List<Panneaux> findByBtAvailable(Boolean btAvailable);

    List<Panneaux> findByBtValide(Boolean btValide);

    Optional<Panneaux> findById(UUID id);

    long countByBtAvailable(Boolean btAvailable);

    List<Object[]> countPanneauxByRegion();

    List<Object[]> countPanneauxParLocalite(String typeLocalite);
}
