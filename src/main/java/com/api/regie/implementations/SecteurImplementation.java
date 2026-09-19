package com.api.regie.implementations;

import com.api.regie.models.Secteur;
import com.api.regie.repository.SecteurRepository;
import com.api.regie.services.SecteurService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SecteurImplementation implements SecteurService {

    private final SecteurRepository secteurRepository;

    public SecteurImplementation(SecteurRepository secteurRepository) {
        this.secteurRepository = secteurRepository;
    }

    @Override
    public List<Secteur> getAllSecteur() {
        return secteurRepository.findAll();
    }

    @Override
    public Optional<Secteur> findById(UUID id) {
        return secteurRepository.findById(id);
    }

    @Override
    public Optional<Secteur> findBySecteur(String secteur) {
        return secteurRepository.findBySecteur(secteur);
    }

    @Override
    public Optional<Secteur> findBySecteurAndIdNot(String secteur, UUID id) {
        return secteurRepository.findBySecteurAndIdNot(secteur, id);
    }

    @Override
    public Secteur addSecteur(Secteur secteur) {
        return secteurRepository.save(secteur);
    }

    @Override
    public List<Secteur> findByQuartierId(UUID idQuartier) {
        return secteurRepository.findByQuartierId(idQuartier);
    }

}