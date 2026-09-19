package com.api.regie.services;

import com.api.regie.models.Campagnes;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampagnesService {

    List<Campagnes> getAllCampagnes();

    Campagnes addCampagnes(Campagnes campagnes);

    Optional<Campagnes> findByNomCampagne(String nomCampagne);

    Optional<Campagnes> findByNomCampagneAndIdNot(String nomCampagne, UUID id);

    List<Campagnes> findByClientId(UUID clientId);

    List<Campagnes> findByUserId(UUID userId);

    List<Campagnes> findByStatut(String statut);

    List<Campagnes> findByPeriode(String typeDate, Date dateDebut, Date dateFin);

    Optional<Campagnes> findById(UUID id);

    List<Campagnes> findCampagnesWithPanneauInPeriod(UUID panneauId, Date dateDebut, Date dateFin);

    List<Campagnes> findCampagnesWithPanneauInPeriodExcludingCampagne(UUID panneauId, Date dateDebut, Date dateFin, UUID campagneIdToExclude);

    // KPI Methods
    Long getNombreTotalCampagnes(Date dateDebut, Date dateFin);

    List<Object[]> getCampagnesParStatut(Date dateDebut, Date dateFin);

    List<Object[]> getCampagnesParStatutPaiement(Date dateDebut, Date dateFin);

    Double getDureeMoyenneCampagnes(Date dateDebut, Date dateFin);

    List<Object[]> getKPIParClient(Date dateDebut, Date dateFin);

    Object[] getKPIParClientId(UUID clientId, Date dateDebut, Date dateFin);

    List<Object[]> getCampagnesParJour(Date dateDebut, Date dateFin);

    List<Object[]> getCampagnesParMois(Date dateDebut, Date dateFin);

    List<Object[]> getPanneauxAvecJoursUtilisation(Date dateDebut, Date dateFin);

    long countByStatut(String statut);
}
