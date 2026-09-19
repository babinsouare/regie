package com.api.regie.repository;

import com.api.regie.models.Secteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecteurRepository extends JpaRepository<Secteur, UUID> {

    List<Secteur> findAll();

    Optional<Secteur> findBySecteur(String secteur);

    Optional<Secteur> findBySecteurAndIdNot(String secteur, UUID id);

    Secteur save(Secteur secteur);

    Optional<Secteur> findById(UUID id);

    List<Secteur> findByQuartierId(UUID idQuartier);

}