package com.api.regie.services;

import com.api.regie.models.CampagneStatut;
import com.api.regie.models.Campagnes;
import com.api.regie.models.Statut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Changement de statut d'une campagne avec tenue de son historique.
 *
 * <p>Isolé dans un service parce que la bascule écrit sur deux tables — la campagne et son
 * historique — et doit donc être atomique. Appelée depuis un composant planifié, une méthode
 * transactionnelle du planificateur lui-même ne serait pas prise en charge par le proxy Spring.</p>
 */
@Service
public class CampagneTransitionService {

    private static final Logger log = LoggerFactory.getLogger(CampagneTransitionService.class);

    private final CampagnesService campagnesService;
    private final CampagneStatutService campagneStatutService;
    private final StatutService statutService;

    public CampagneTransitionService(CampagnesService campagnesService,
                                     CampagneStatutService campagneStatutService,
                                     StatutService statutService) {
        this.campagnesService = campagnesService;
        this.campagneStatutService = campagneStatutService;
        this.statutService = statutService;
    }

    /**
     * Fait passer une campagne à un nouveau statut.
     *
     * <p>Reprend la mécanique déjà en place ailleurs : l'entrée d'historique courante est
     * désactivée, une nouvelle est créée et datée, puis la campagne est mise à jour. Aucun
     * utilisateur n'est rattaché : la bascule est automatique, elle n'engage personne.</p>
     *
     * @param commentaire motif inscrit à l'historique
     */
    @Transactional
    public void changerStatut(Campagnes campagne, String nouveauCode, String commentaire) {
        Optional<Statut> statutOpt = statutService.findByCodeStatut(nouveauCode);
        if (statutOpt.isEmpty()) {
            throw new IllegalStateException("Statut '" + nouveauCode + "' introuvable en base de données");
        }

        Optional<CampagneStatut> ancien = campagneStatutService.findByCampagneAndBtEnabled(campagne, true);
        ancien.ifPresent(campagneStatutService::disableCampagneStatut);

        CampagneStatut nouveau = new CampagneStatut();
        nouveau.setCampagne(campagne);
        nouveau.setStatut(statutOpt.get());
        nouveau.setDateChangement(new Date());
        nouveau.setCommentaire(commentaire);
        nouveau.setBtEnabled(true);
        campagneStatutService.addCampagneStatut(nouveau);

        campagne.setStatut(nouveauCode);
        campagnesService.addCampagnes(campagne);

        log.info("Campagne {} : statut porté à {}.", campagne.getNomCampagne(), nouveauCode);
    }
}
