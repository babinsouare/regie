package com.api.regie.implementations;

import com.api.regie.models.Panneaux;
import com.api.regie.repository.PanneauxRepository;
import com.api.regie.services.PanneauxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PanneauxImplementation implements PanneauxService {

    private final PanneauxRepository panneauxRepository;

    public PanneauxImplementation(PanneauxRepository panneauxRepository) {
        this.panneauxRepository = panneauxRepository;
    }

    @Override
    public List<Panneaux> getAllPanneaux() {
        return panneauxRepository.findAll();
    }

    @Override
    public Panneaux addPanneaux(Panneaux panneaux) {
        return panneauxRepository.save(panneaux);
    }

    @Override
    public Optional<Panneaux> findByReference(String reference) {
        return panneauxRepository.findByReference(reference);
    }

    @Override
    public Optional<Panneaux> findByReferenceAndFace(String reference, String face) {
        return panneauxRepository.findByReferenceAndFace(reference, face);
    }

    @Override
    public Optional<Panneaux> findByReferenceAndFaceAndIdNot(String reference, String face, UUID id) {
        return panneauxRepository.findByReferenceAndFaceAndIdNot(reference, face, id);
    }

    @Override
    public List<Panneaux> findBySecteurId(UUID secteurId) {
        return panneauxRepository.findBySecteurId(secteurId);
    }

    @Override
    public List<Panneaux> findBySecteurQuartierId(UUID quartierId) {
        return panneauxRepository.findBySecteurQuartierId(quartierId);
    }

    @Override
    public List<Panneaux> findBySecteurQuartierCommuneId(UUID communeId) {
        return panneauxRepository.findBySecteurQuartierCommuneId(communeId);
    }

    @Override
    public List<Panneaux> findBySecteurQuartierCommuneRegionId(UUID regionId) {
        return panneauxRepository.findBySecteurQuartierCommuneRegionId(regionId);
    }

    @Override
    public List<Panneaux> findByCaracteristiquePanneauxId(UUID caracteristiqueId) {
        return panneauxRepository.findByCaracteristiquePanneauxId(caracteristiqueId);
    }

    @Override
    public List<Panneaux> findByBtAvailable(Boolean btAvailable) {
        return panneauxRepository.findByBtAvailable(btAvailable);
    }

    @Override
    public List<Panneaux> findByBtValide(Boolean btValide) {
        return panneauxRepository.findByBtValide(btValide);
    }

    @Override
    public Optional<Panneaux> findById(UUID id) {
        return panneauxRepository.findById(id);
    }

    @Override
    public long countByBtAvailable(Boolean btAvailable) {
        return panneauxRepository.countByBtAvailable(btAvailable);
    }

    @Override
    public List<Object[]> countPanneauxByRegion() {
        return panneauxRepository.countPanneauxByRegion();
    }

    @Override
    public List<Object[]> countPanneauxParLocalite(String typeLocalite) {
        return switch (typeLocalite.trim().toLowerCase()) {
            case "region" -> panneauxRepository.countPanneauxGroupByRegion();
            case "commune" -> panneauxRepository.countPanneauxGroupByCommune();
            case "quartier" -> panneauxRepository.countPanneauxGroupByQuartier();
            case "secteur" -> panneauxRepository.countPanneauxGroupBySecteur();
            default -> throw new IllegalArgumentException("Type de localité inconnu : " + typeLocalite);
        };
    }
}
