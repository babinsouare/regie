package com.api.regie.implementations;

import com.api.regie.models.Campagnes;
import com.api.regie.repository.CampagnesRepository;
import com.api.regie.services.CampagnesService;
import com.api.regie.utils.Helpers;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampagnesImplementation implements CampagnesService {

    private final CampagnesRepository campagnesRepository;

    public CampagnesImplementation(CampagnesRepository campagnesRepository) {
        this.campagnesRepository = campagnesRepository;
    }

    @Override
    public List<Campagnes> getAllCampagnes() {
        return campagnesRepository.findAll();
    }

    @Override
    public Campagnes addCampagnes(Campagnes campagnes) {
        return campagnesRepository.save(campagnes);
    }

    @Override
    public Optional<Campagnes> findByNomCampagne(String nomCampagne) {
        return campagnesRepository.findByNomCampagne(nomCampagne);
    }

    @Override
    public Optional<Campagnes> findByNomCampagneAndIdNot(String nomCampagne, UUID id) {
        return campagnesRepository.findByNomCampagneAndIdNot(nomCampagne, id);
    }

    @Override
    public List<Campagnes> findByClientId(UUID clientId) {
        return campagnesRepository.findByClientId(clientId);
    }

    @Override
    public List<Campagnes> findByUserId(UUID userId) {
        return campagnesRepository.findByUserId(userId);
    }

    @Override
    public List<Campagnes> findByStatut(String statut) {
        return campagnesRepository.findByStatut(statut);
    }

    @Override
    public List<Campagnes> findByPeriode(String typeDate, Date dateDebut, Date dateFin) {
        Date debut = Helpers.debutDeJournee(dateDebut);
        Date fin = Helpers.finDeJournee(dateFin);

        return switch (typeDate.trim().toLowerCase()) {
            case "creation" -> campagnesRepository.findByDtCreatedBetweenOrderByDtCreatedDesc(debut, fin);
            case "debut" -> campagnesRepository.findByDateDebutBetweenOrderByDateDebutAsc(debut, fin);
            case "fin" -> campagnesRepository.findByDateFinBetweenOrderByDateFinAsc(debut, fin);
            default -> throw new IllegalArgumentException("Type de date inconnu : " + typeDate);
        };
    }

    @Override
    public Optional<Campagnes> findById(UUID id) {
        return campagnesRepository.findById(id);
    }

    @Override
    public List<Campagnes> findCampagnesWithPanneauInPeriod(UUID panneauId, Date dateDebut, Date dateFin) {
        return campagnesRepository.findCampagnesWithPanneauInPeriod(panneauId, dateDebut, dateFin);
    }

    @Override
    public List<Campagnes> findCampagnesWithPanneauInPeriodExcludingCampagne(UUID panneauId, Date dateDebut, Date dateFin, UUID campagneIdToExclude) {
        return campagnesRepository.findCampagnesWithPanneauInPeriodExcludingCampagne(panneauId, dateDebut, dateFin, campagneIdToExclude);
    }

    @Override
    public Long getNombreTotalCampagnes(Date dateDebut, Date dateFin) {
        return campagnesRepository.getNombreTotalCampagnes(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getCampagnesParStatut(Date dateDebut, Date dateFin) {
        return campagnesRepository.getCampagnesParStatut(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getCampagnesParStatutPaiement(Date dateDebut, Date dateFin) {
        return campagnesRepository.getCampagnesParStatutPaiement(dateDebut, dateFin);
    }

    @Override
    public Double getDureeMoyenneCampagnes(Date dateDebut, Date dateFin) {
        return campagnesRepository.getDureeMoyenneCampagnes(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getKPIParClient(Date dateDebut, Date dateFin) {
        return campagnesRepository.getKPIParClient(dateDebut, dateFin);
    }

    @Override
    public Object[] getKPIParClientId(UUID clientId, Date dateDebut, Date dateFin) {
        return campagnesRepository.getKPIParClientId(clientId, dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getCampagnesParJour(Date dateDebut, Date dateFin) {
        return campagnesRepository.getCampagnesParJour(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getCampagnesParMois(Date dateDebut, Date dateFin) {
        return campagnesRepository.getCampagnesParMois(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getPanneauxAvecJoursUtilisation(Date dateDebut, Date dateFin) {
        return campagnesRepository.getPanneauxAvecJoursUtilisation(dateDebut, dateFin);
    }

    @Override
    public long countByStatut(String statut) {
        return campagnesRepository.countByStatut(statut);
    }
}
