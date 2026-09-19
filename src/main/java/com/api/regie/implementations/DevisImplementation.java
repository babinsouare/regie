package com.api.regie.implementations;

import com.api.regie.models.*;
import com.api.regie.repository.DevisRepository;
import com.api.regie.services.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DevisImplementation implements DevisService {

    private final DevisRepository devisRepository;
    private final CampagnesService campagnesService;
    private final PanneauxCampagneService panneauxCampagneService;
    private final PanneauxService panneauxService;
    private final TarifsService tarifsService;
    private final RemiseService remiseService;

    public DevisImplementation(DevisRepository devisRepository,
                               CampagnesService campagnesService,
                               PanneauxCampagneService panneauxCampagneService,
                               PanneauxService panneauxService,
                               TarifsService tarifsService,
                               RemiseService remiseService) {
        this.devisRepository = devisRepository;
        this.campagnesService = campagnesService;
        this.panneauxCampagneService = panneauxCampagneService;
        this.panneauxService = panneauxService;
        this.tarifsService = tarifsService;
        this.remiseService = remiseService;
    }

    @Override
    public List<Devis> getAllDevis() {
        return devisRepository.findAll();
    }

    @Override
    public Devis addDevis(Devis devis) {
        return devisRepository.save(devis);
    }

    @Override
    public Devis createDevisForCampagne(UUID campagneId, UUID remiseId, Date dateValidite) {
        // Vérifier si un devis existe déjà pour cette campagne
        List<Devis> existingDevis = devisRepository.findByCampagneId(campagneId);
        if (!existingDevis.isEmpty()) {
            throw new RuntimeException("Un devis existe déjà pour cette campagne");
        }

        // Récupérer la campagne
        Campagnes campagne = campagnesService.findById(campagneId)
                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));

        // Récupérer les panneaux liés à la campagne
        List<PanneauxCampagne> panneauxCampagnes = panneauxCampagneService.findByCampagneId(campagneId);

        if (panneauxCampagnes.isEmpty()) {
            throw new RuntimeException("Aucun panneau associé à cette campagne");
        }

        String cycle = campagne.getCycle();
        double montantBrute = 0.0;

        // Calculer le montant brut
        for (PanneauxCampagne pc : panneauxCampagnes) {
            Panneaux panneau = pc.getPanneaux();
            if (panneau == null) continue;

            double montantPanneau = 0.0;

            if (Boolean.TRUE.equals(panneau.getHasSpecialPrice())) {
                // Utiliser les prix du panneau
                montantPanneau = getMontantByCycle(cycle, panneau.getPriceDay(), panneau.getPriceWeek(), panneau.getPriceMonth(), campagne.getNombre());
            } else {
                // Utiliser les prix du tarif lié à la caractéristique

                CaracteristiquePanneaux caracteristique = panneau.getCaracteristiquePanneaux();
                if (caracteristique != null) {
                    List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(caracteristique.getId());
                    if (!tarifs.isEmpty()) {
                        Tarifs tarif = tarifs.get(0);
                        montantPanneau = getMontantByCycle(cycle, tarif.getPriceDay(), tarif.getPriceWeek(), tarif.getPriceMonth(), campagne.getNombre());
                    }else {
                        System.err.println("Aucun tarif trouvé pour la caractéristique du panneau avec ID " + panneau.getId());
                    }
                }else{
                    System.err.println("Le panneau avec ID " + panneau.getId() + " n'a pas de caractéristique associée.");
                }
            }


            montantBrute += montantPanneau;
        }

        // Calculer la remise
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

        // Générer le numéro de devis
        String numeroDevis = generateNumeroDevis();

        // Calculer la date de validité (+30 jours)
        Date dateEmission = new Date();
        //Date dateValidite = new Date(dateEmission.getTime() + (30L * 24 * 60 * 60 * 1000));

        // Créer le devis
        Devis devis = new Devis();
        devis.setNumeroDevis(numeroDevis);
        devis.setCampagne(campagne);
        devis.setClient(campagne.getClient());
        devis.setUser(campagne.getUser());
        devis.setDateEmission(dateEmission);
        devis.setDateValidite(dateValidite);
        devis.setDateDebutPrevu(campagne.getDateDebut());
        devis.setDateFinPrevu(campagne.getDateFin());
        devis.setMontantBrute(montantBrute);
        devis.setMontantRemise(montantRemise);
        devis.setMontantNet(montantNet);

        if (remiseId != null) {
            Remise remise = remiseService.findById(remiseId).orElse(null);
            devis.setRemise(remise);
        }

        return devisRepository.save(devis);
    }

    @Override
    public Devis updateDevis(UUID idDevis, Date dateValidite, String observations, UUID remiseId) {
        Devis devis = devisRepository.findById(idDevis)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));

        // Mettre à jour la date de validité si fournie
        if (dateValidite != null) {
            devis.setDateValidite(dateValidite);
        }

        // Mettre à jour les observations si fournies
        if (observations != null) {
            devis.setObservations(observations);
        }

        // Mettre à jour la remise si fournie
        Remise remise = null;
        if (remiseId != null) {
            remise = remiseService.findById(remiseId)
                    .orElseThrow(() -> new RuntimeException("Remise non trouvée"));
            devis.setRemise(remise);
        } else {
            devis.setRemise(null);
        }

        // Recalculer le montant brut à partir des panneaux de la campagne
        Campagnes campagne = devis.getCampagne();
        if (campagne == null) {
            throw new RuntimeException("Le devis n'est associé à aucune campagne");
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

        devis.setMontantBrute(montantBrute);
        
        // Recalculer la remise
        double montantRemise = 0.0;
        if (remise != null) {
            if ("pourcentage".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = montantBrute * (remise.getValeurRemise() / 100.0);
            } else if ("montant_fixe".equalsIgnoreCase(remise.getTypeRemise())) {
                montantRemise = remise.getValeurRemise();
            }
        }
        devis.setMontantRemise(montantRemise);
        
        // Recalculer le montant net
        double montantNet = montantBrute - montantRemise;
        devis.setMontantNet(montantNet);

        return devisRepository.save(devis);
    }

    private String generateNumeroDevis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "DEV-" + dateStr + "-" + uuid;
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
    public Optional<Devis> findByNumeroDevis(String numeroDevis) {
        return devisRepository.findByNumeroDevis(numeroDevis);
    }

    @Override
    public Optional<Devis> findByNumeroDevisAndIdNot(String numeroDevis, UUID id) {
        return devisRepository.findByNumeroDevisAndIdNot(numeroDevis, id);
    }

    @Override
    public List<Devis> findByClientId(UUID clientId) {
        return devisRepository.findByClientId(clientId);
    }

    @Override
    public List<Devis> findByUserId(UUID userId) {
        return devisRepository.findByUserId(userId);
    }

    @Override
    public List<Devis> findByCampagneId(UUID campagneId) {
        return devisRepository.findByCampagneId(campagneId);
    }


    @Override
    public void deleteByCampagneId(UUID campagneId) {
        devisRepository.deleteByCampagneId(campagneId);
    }

    @Override
    public Optional<Devis> findById(UUID id) {
        return devisRepository.findById(id);
    }
}
