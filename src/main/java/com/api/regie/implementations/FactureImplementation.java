package com.api.regie.implementations;

import com.api.regie.models.*;
import com.api.regie.repository.FactureRepository;
import com.api.regie.services.*;
import com.api.regie.utils.Helpers;
import com.api.regie.utils.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FactureImplementation implements FactureService {

    private final FactureRepository factureRepository;
    private final CampagnesService campagnesService;
    private final PanneauxCampagneService panneauxCampagneService;
    private final TarifsService tarifsService;
    private final RemiseService remiseService;
    private final Helpers helpers=new Helpers();
    private final CampagneStatutService campagneStatutService;
    private final StatutService statutService;
    private final SecurityUtils securityUtils;

    public FactureImplementation(FactureRepository factureRepository,
                                 CampagnesService campagnesService,
                                 PanneauxCampagneService panneauxCampagneService,
                                 PanneauxService panneauxService,
                                 TarifsService tarifsService,
                                 RemiseService remiseService, CampagneStatutService campagneStatutService, StatutService statutService, SecurityUtils securityUtils) {
        this.factureRepository = factureRepository;
        this.campagnesService = campagnesService;
        this.panneauxCampagneService = panneauxCampagneService;
        this.tarifsService = tarifsService;
        this.remiseService = remiseService;
        this.campagneStatutService = campagneStatutService;
        this.statutService = statutService;
        this.securityUtils = securityUtils;
    }

    @Override
    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    @Override
    public Facture addFacture(Facture facture) {
        return factureRepository.save(facture);
    }

    @Override
    public Facture createFactureForCampagne(UUID campagneId, UUID remiseId) {
        Campagnes campagne = campagnesService.findById(campagneId)
                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));

        List<PanneauxCampagne> panneauxCampagnes = panneauxCampagneService.findByCampagneId(campagneId);

        if (panneauxCampagnes.isEmpty()) {
            throw new RuntimeException("Aucun panneau associé à cette campagne");
        }

        String cycle = campagne.getCycle();
        double montantBrute = 0.0;

        for (PanneauxCampagne pc : panneauxCampagnes) {
            Panneaux panneau = pc.getPanneaux();
            if (panneau == null) continue;

            double montantPanneau = 0.0;

            if (Boolean.TRUE.equals(panneau.getHasSpecialPrice())) {
                montantPanneau = getMontantByCycle(cycle, panneau.getPriceDay(), panneau.getPriceWeek(), panneau.getPriceMonth(), campagne.getNombre());
            } else {
                CaracteristiquePanneaux caracteristique = panneau.getCaracteristiquePanneaux();
                if (caracteristique != null) {
                    List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(caracteristique.getId());
                    if (!tarifs.isEmpty()) {
                        Tarifs tarif = tarifs.get(0);
                        montantPanneau = getMontantByCycle(cycle, tarif.getPriceDay(), tarif.getPriceWeek(), tarif.getPriceMonth(), campagne.getNombre());
                    }
                }
            }

            montantBrute += montantPanneau;
        }

        double montantRemise = 0.0;
        if (remiseId != null) {
            Remise remise = remiseService.findById(remiseId)
                    .orElseThrow(() -> new RuntimeException("Remise non trouvée"));

            if ("pourcentage".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = montantBrute * (remise.getValeurRemise() / 100.0);
            } else if ("montant_fixe".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = remise.getValeurRemise();
            }
        }

        double montantNet = montantBrute - montantRemise;

        Facture facture = new Facture();
        facture.setCampagne(campagne);
        facture.setMontantBrute(montantBrute);
        facture.setMontantRemise(montantRemise);
        facture.setMontantNet(montantNet);
        facture.setMontantPaye(0.0);
        facture.setMontantResteAPaye(montantNet);
        facture.setDateEcheance(new Date());
        facture.setReference(helpers.generateReference());

        if (remiseId != null) {
            Remise remise = remiseService.findById(remiseId).orElse(null);
            facture.setRemise(remise);
        }

        //Update Campagne statut

        Optional<CampagneStatut> campagneStatutOpt = campagneStatutService.findByCampagneAndBtEnabled(campagne, true);

        if (campagneStatutOpt.isPresent()) {

            CampagneStatut currentCampagneStatut = campagneStatutOpt.get();
            String currentStatutCode = currentCampagneStatut.getStatut().getCodeStatut();

            // Bloquer si statut est archived ou canceled
            if (currentStatutCode.equalsIgnoreCase("proposition")) {
                int currentOrdre = currentCampagneStatut.getStatut().getOrdre();
                int newOrdre = currentOrdre + 1 ;
                Optional<Statut> newStatutOpt = statutService.findByOrdre(newOrdre);

                if (newStatutOpt.isPresent()) {
                    Statut newStatut = newStatutOpt.get();
                    String newStatutCode = newStatut.getCodeStatut();
                    // Interdire la transition vers "paied"
                    if (!newStatutCode.equalsIgnoreCase("paied")) {

                        // Désactiver l'ancien statut
                        campagneStatutService.disableCampagneStatut(currentCampagneStatut);

                        // Créer le nouveau statut
                        CampagneStatut newCampagneStatut = new CampagneStatut();
                        newCampagneStatut.setCampagne(campagne);
                        newCampagneStatut.setStatut(newStatut);
                        newCampagneStatut.setDateChangement(new Date());
                        newCampagneStatut.setCommentaire( "Changement de statut: " + currentStatutCode + " -> " + newStatutCode);
                        newCampagneStatut.setBtEnabled(true);
                        newCampagneStatut.setUser(securityUtils.getCurrentUser());

                        campagneStatutService.addCampagneStatut(newCampagneStatut);

                        // Mettre à jour le statut de la campagne
                        campagne.setStatut(newStatutCode);
                        campagnesService.addCampagnes(campagne);
                    }
                }
            }
        }

        return factureRepository.save(facture);
    }

    @Override
    public Facture updateFacture(UUID idFacture, Date dateEcheance, UUID remiseId) {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        // Mettre à jour la date d'échéance si fournie
        if (dateEcheance != null) {
            facture.setDateEcheance(dateEcheance);
        }

        // Mettre à jour la remise si fournie
        Remise remise = null;
        if (remiseId != null) {
            remise = remiseService.findById(remiseId)
                    .orElseThrow(() -> new RuntimeException("Remise non trouvée"));
            facture.setRemise(remise);
        } else {
            facture.setRemise(null);
        }

        // Recalculer le montant brut à partir des panneaux de la campagne
        Campagnes campagne = facture.getCampagne();
        if (campagne == null) {
            throw new RuntimeException("La facture n'est associée à aucune campagne");
        }

        List<PanneauxCampagne> panneauxCampagnes = panneauxCampagneService.findByCampagneId(campagne.getId());
        String cycle = campagne.getCycle();
        double montantBrute = 0.0;

        for (PanneauxCampagne pc : panneauxCampagnes) {
            Panneaux panneau = pc.getPanneaux();
            if (panneau == null) continue;

            double montantPanneau = 0.0;

            if (Boolean.TRUE.equals(panneau.getHasSpecialPrice())) {
                montantPanneau = getMontantByCycle(cycle, panneau.getPriceDay(), panneau.getPriceWeek(), panneau.getPriceMonth(), campagne.getNombre());
            } else {
                CaracteristiquePanneaux caracteristique = panneau.getCaracteristiquePanneaux();
                if (caracteristique != null) {
                    List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(caracteristique.getId());
                    if (!tarifs.isEmpty()) {
                        Tarifs tarif = tarifs.get(0);
                        montantPanneau = getMontantByCycle(cycle, tarif.getPriceDay(), tarif.getPriceWeek(), tarif.getPriceMonth(), campagne.getNombre());
                    }
                }
            }
            montantBrute += montantPanneau;
        }

        facture.setMontantBrute(montantBrute);
        
        // Recalculer la remise
        double montantRemise = 0.0;
        if (remise != null) {
            if ("pourcentage".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = montantBrute * (remise.getValeurRemise() / 100.0);
            } else if ("montant_fixe".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = remise.getValeurRemise();
            }
        }
        facture.setMontantRemise(montantRemise);
        
        // Recalculer le montant net
        double montantNet = montantBrute - montantRemise;
        facture.setMontantNet(montantNet);
        
        // Recalculer le reste à payer
        double montantPaye = facture.getMontantPaye() != null ? facture.getMontantPaye() : 0.0;
        facture.setMontantResteAPaye(montantNet - montantPaye);

        return factureRepository.save(facture);
    }

    private double getMontantByCycle(String cycle, Double priceDay, Double priceWeek, Double priceMonth, int nombre) {
        if (cycle == null) return 0.0;

        switch (cycle.toLowerCase()) {
            case "day":
            case "jour":
                return priceDay != null ? (priceDay*nombre) : 0.0;
            case "week":
            case "semaine":
                return priceWeek != null ? (priceWeek*nombre) : 0.0;
            case "month":
            case "mois":
                return priceMonth != null ? (priceMonth*nombre) : 0.0;
            default:
                return 0.0;
        }
    }

    @Override
    public List<Facture> findByCampagneId(UUID campagneId) {
        return factureRepository.findByCampagneId(campagneId);
    }

    @Override
    public void deleteByCampagneId(UUID campagneId) {
        factureRepository.deleteByCampagneId(campagneId);
    }

    @Override
    public List<Facture> findByUserId(UUID userId) {
        return factureRepository.findByUserId(userId);
    }

    @Override
    public List<Facture> findByClientTelephone(String telephoneResponsable) {
        return factureRepository.findByCampagneClientTelephoneResponsable(telephoneResponsable);
    }

    @Override
    public Optional<Facture> findById(UUID id) {
        return factureRepository.findById(id);
    }

    @Override
    public Optional<Facture> findByReference(String reference) {
        return factureRepository.findByReference(reference);
    }

    @Override
    public List<Facture> findByPeriodeCreation(Date dateDebut, Date dateFin) {
        return factureRepository.findByDtCreatedBetweenOrderByDtCreatedDesc(
                Helpers.debutDeJournee(dateDebut), Helpers.finDeJournee(dateFin));
    }

    @Override
    public List<Facture> findByPeriodeEcheance(Date dateDebut, Date dateFin) {
        return factureRepository.findByDateEcheanceBetweenOrderByDateEcheanceAsc(
                Helpers.debutDeJournee(dateDebut), Helpers.finDeJournee(dateFin));
    }

    @Override
    public List<Facture> findDernieresFactures(int limit) {
        return factureRepository.findAllByOrderByDtCreatedDesc(PageRequest.of(0, limit));
    }

    @Override
    public Double getTotalFacture(Date dateDebut, Date dateFin) {
        return factureRepository.getTotalFacture(dateDebut, dateFin);
    }

    @Override
    public Double getTotalPaye(Date dateDebut, Date dateFin) {
        return factureRepository.getTotalPaye(dateDebut, dateFin);
    }

    @Override
    public Double getTotalImpaye(Date dateDebut, Date dateFin) {
        return factureRepository.getTotalImpaye(dateDebut, dateFin);
    }

    @Override
    public Double getMontantMoyen(Date dateDebut, Date dateFin) {
        return factureRepository.getMontantMoyen(dateDebut, dateFin);
    }

    @Override
    public Long getNombreFacturesPayees(Date dateDebut, Date dateFin) {
        return factureRepository.getNombreFacturesPayees(dateDebut, dateFin);
    }

    @Override
    public Long getNombreFacturesPartielles(Date dateDebut, Date dateFin) {
        return factureRepository.getNombreFacturesPartielles(dateDebut, dateFin);
    }

    @Override
    public Long getNombreFacturesImpayees(Date dateDebut, Date dateFin) {
        return factureRepository.getNombreFacturesImpayees(dateDebut, dateFin);
    }

    @Override
    public Long getNombreFacturesEnRetard(Date dateDebut, Date dateFin) {
        return factureRepository.getNombreFacturesEnRetard(dateDebut, dateFin);
    }

    @Override
    public Double getMontantFacturesEnRetard(Date dateDebut, Date dateFin) {
        return factureRepository.getMontantFacturesEnRetard(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getCAMensuel(Date dateDebut, Date dateFin) {
        return factureRepository.getCAMensuel(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getTopClients(Date dateDebut, Date dateFin) {
        return factureRepository.getTopClients(dateDebut, dateFin);
    }

    @Override
    public Long countClientsAvecImpayes(Date dateDebut, Date dateFin) {
        return factureRepository.countClientsAvecImpayes(dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getImpayesParSociete(Date dateDebut, Date dateFin) {
        return factureRepository.getImpayesParSociete(dateDebut, dateFin);
    }
}
