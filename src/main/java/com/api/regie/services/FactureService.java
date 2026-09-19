package com.api.regie.services;

import com.api.regie.models.Facture;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactureService {

    List<Facture> getAllFactures();

    Facture addFacture(Facture facture);

    Facture createFactureForCampagne(UUID campagneId, UUID remiseId);

    Facture updateFacture(UUID idFacture, Date dateEcheance, UUID remiseId);

    List<Facture> findByCampagneId(UUID campagneId);

    void deleteByCampagneId(UUID campagneId);

    List<Facture> findByUserId(UUID userId);

    List<Facture> findByClientTelephone(String telephoneResponsable);

    Optional<Facture> findById(UUID id);

    Optional<Facture> findByReference(String reference);

    List<Facture> findByPeriodeCreation(Date dateDebut, Date dateFin);

    List<Facture> findByPeriodeEcheance(Date dateDebut, Date dateFin);

    List<Facture> findDernieresFactures(int limit);

    // KPI Methods
    Double getTotalFacture(Date dateDebut, Date dateFin);

    Double getTotalPaye(Date dateDebut, Date dateFin);

    Double getTotalImpaye(Date dateDebut, Date dateFin);

    Double getMontantMoyen(Date dateDebut, Date dateFin);

    Long getNombreFacturesPayees(Date dateDebut, Date dateFin);

    Long getNombreFacturesPartielles(Date dateDebut, Date dateFin);

    Long getNombreFacturesImpayees(Date dateDebut, Date dateFin);

    Long getNombreFacturesEnRetard(Date dateDebut, Date dateFin);

    Double getMontantFacturesEnRetard(Date dateDebut, Date dateFin);

    List<Object[]> getCAMensuel(Date dateDebut, Date dateFin);

    List<Object[]> getTopClients(Date dateDebut, Date dateFin);

    Long countClientsAvecImpayes(Date dateDebut, Date dateFin);

    List<Object[]> getImpayesParSociete(Date dateDebut, Date dateFin);
}
