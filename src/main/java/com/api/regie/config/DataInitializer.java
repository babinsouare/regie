package com.api.regie.config;

import com.api.regie.models.CategoriePanneaux;
import com.api.regie.models.ModePaiement;
import com.api.regie.models.Permissions;
import com.api.regie.models.Region;
import com.api.regie.models.Statut;
import com.api.regie.models.TypeClient;
import com.api.regie.models.TypeEvenement;
import com.api.regie.models.NotificationParametrage;
import com.api.regie.models.Users;
import com.api.regie.services.CategoriePanneauxService;
import com.api.regie.services.ModePaiementService;
import com.api.regie.services.PermissionService;
import com.api.regie.services.RegionService;
import com.api.regie.services.StatutService;
import com.api.regie.services.TypeClientService;
import com.api.regie.services.TypeEvenementService;
import com.api.regie.services.NotificationParametrageService;
import com.api.regie.services.UsersService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.api.regie.notifications.NotificationEvenements;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsersService usersService;
    private final BCryptPasswordEncoder encoder;
    private final StatutService statutService;
    private final ModePaiementService modePaiementService;
    private final TypeClientService typeClientService;
    private final TypeEvenementService typeEvenementService;
    private final CategoriePanneauxService categoriePanneauxService;
    private final RegionService regionService;
    private final PermissionService permissionService;
    private final NotificationParametrageService notificationParametrageService;

    public DataInitializer(UsersService usersService, BCryptPasswordEncoder encoder, StatutService statutService, 
                          ModePaiementService modePaiementService, TypeClientService typeClientService,
                          TypeEvenementService typeEvenementService, CategoriePanneauxService categoriePanneauxService,
                          RegionService regionService, PermissionService permissionService,
                          NotificationParametrageService notificationParametrageService) {
        this.usersService = usersService;
        this.encoder = encoder;
        this.statutService = statutService;
        this.modePaiementService = modePaiementService;
        this.typeClientService = typeClientService;
        this.typeEvenementService = typeEvenementService;
        this.categoriePanneauxService = categoriePanneauxService;
        this.regionService = regionService;
        this.permissionService = permissionService;
        this.notificationParametrageService = notificationParametrageService;
    }

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
        initStatuts();
        initModesPaiement();
        initTypesClients();
        initTypesEvenements();
        initCategoriesPanneaux();
        initRegions();
        initPermissions();
        initNotifications();
    }

    private void initAdmin() {
        Optional<Users> users = usersService.findByEmail("admin@gmail.com");
        if (users.isEmpty()) {
            Users user = new Users();
            user.setEmail("admin@gmail.com");
            user.setPassword(encoder.encode("000000"));
            user.setNom("Admin");
            user.setPrenom("Admin");
            user.setMsisdn("00000000000");
            usersService.save(user);
        }
    }

    private void initStatuts() {
        initStatut("proposition", "Proposition de campagne", 1);
        initStatut("pending_payment", "Paiement en attente", 2);
        initStatut("paied", "Campagne payée", 3);
        initStatut("ongoing", "Campagne en cours", 4);
        initStatut("ended", "Campagne terminée", 5);
        initStatut("canceled", "Campagne annulée", 6);
        initStatut("archived", "Campagne archivée", 7);
    }

    private void initStatut(String codeStatut, String description, int ordre) {
        Optional<Statut> existingStatut = statutService.findByCodeStatut(codeStatut);
        if (existingStatut.isEmpty()) {
            Statut statut = new Statut();
            statut.setCodeStatut(codeStatut);
            statut.setDescription(description);
            statut.setOrdre(ordre);
            statutService.addStatut(statut);
        }
    }

    private void initModesPaiement() {
        initModePaiement("Cash", "Mode de paiement cash");
    }

    private void initModePaiement(String mode, String description) {
        Optional<ModePaiement> existingMode = modePaiementService.findByMode(mode);
        if (existingMode.isEmpty()) {
            ModePaiement modePaiement = new ModePaiement();
            modePaiement.setMode(mode);
            modePaiement.setDescription(description);
            modePaiementService.addModePaiement(modePaiement);
        }
    }

    private void initTypesClients() {
        initTypeClient("Particulier", "Client particulier");
        initTypeClient("Entreprise", "Client entreprise");
        initTypeClient("Administration", "Client administration publique");
    }

    private void initTypeClient(String typeClient, String description) {
        Optional<TypeClient> existingType = typeClientService.findByTypeClient(typeClient);
        if (existingType.isEmpty()) {
            TypeClient type = new TypeClient();
            type.setTypeClient(typeClient);
            type.setDescription(description);
            type.setBtEnabled(true);
            typeClientService.addTypeClient(type);
        }
    }

    private void initTypesEvenements() {
        initTypeEvenement("Maintenance", "Maintenance préventive ou corrective");
        initTypeEvenement("Installation", "Installation d'un nouveau panneau");
        initTypeEvenement("Réparation", "Réparation suite à dommage");
        initTypeEvenement("Inspection", "Inspection périodique");
        initTypeEvenement("Retrait", "Retrait ou démontage du panneau");
    }

    private void initTypeEvenement(String type, String description) {
        Optional<TypeEvenement> existingType = typeEvenementService.findByType(type);
        if (existingType.isEmpty()) {
            TypeEvenement typeEvenement = new TypeEvenement();
            typeEvenement.setType(type);
            typeEvenement.setDescription(description);
            typeEvenement.setBtEmabled(true);
            typeEvenementService.addTypeEvenement(typeEvenement);
        }
    }

    private void initCategoriesPanneaux() {
        initCategoriePanneau("Billboard", "Panneau publicitaire grand format");
        initCategoriePanneau("4x3", "Panneau standard 4m x 3m");
        initCategoriePanneau("Affichage urbain", "Mobilier urbain publicitaire");
        initCategoriePanneau("Digital", "Panneau numérique/LED");
        initCategoriePanneau("Abribus", "Panneau dans abribus");
    }

    private void initCategoriePanneau(String categorie, String description) {
        Optional<CategoriePanneaux> existingCategorie = categoriePanneauxService.findByCategorie(categorie);
        if (existingCategorie.isEmpty()) {
            CategoriePanneaux categoriePanneaux = new CategoriePanneaux();
            categoriePanneaux.setCategorie(categorie);
            categoriePanneaux.setDescription(description);
            categoriePanneaux.setBtEnabled(true);
            categoriePanneauxService.addCategoriePanneaux(categoriePanneaux);
        }
    }

    private void initRegions() {
        // 8 régions administratives de la République de Guinée
        initRegion("Conakry");
        initRegion("Boké");
        initRegion("Kindia");
        initRegion("Mamou");
        initRegion("Labé");
        initRegion("Faranah");
        initRegion("Kankan");
        initRegion("N'Zérékoré");
    }

    private void initRegion(String region) {
        Optional<Region> existingRegion = regionService.findByRegion(region);
        if (existingRegion.isEmpty()) {
            Region newRegion = new Region();
            newRegion.setRegion(region);
            newRegion.setBtEnabled(true);
            regionService.addRegion(newRegion);
        }
    }

    private void initPermissions() {
        // Dashboard
        initPermission("dashboard.view_only", "Accès en lecture au tableau de bord", "dashboard", "Tableau de bord");

        // Client
        initPermission("client.view_only", "Accès en lecture aux clients", "client", "Client");
        initPermission("client.full_access", "Accès complet aux clients", "client", "Client");

        // Panneau
        initPermission("panneau.view_only", "Accès en lecture aux panneaux", "panneau", "Panneau");
        initPermission("panneau.full_access", "Accès complet aux panneaux", "panneau", "Panneau");

        // Evenement
        initPermission("evenement.view_only", "Accès en lecture aux événements", "evenement", "Événement");
        initPermission("evenement.full_access", "Accès complet aux événements", "evenement", "Événement");

        // Campagne
        initPermission("campagne.view_only", "Accès en lecture aux campagnes", "campagne", "Campagne");
        initPermission("campagne.full_access", "Accès complet aux campagnes", "campagne", "Campagne");

        // Categorie panneaux
        initPermission("categorie_panneaux.view_only", "Accès en lecture aux catégories de panneaux", "categorie_panneaux", "Catégorie panneaux");
        initPermission("categorie_panneaux.full_access", "Accès complet aux catégories de panneaux", "categorie_panneaux", "Catégorie panneaux");

        // Caracteristique panneaux
        initPermission("caracteristique_panneaux.view_only", "Accès en lecture aux caractéristiques de panneaux", "caracteristique_panneaux", "Caractéristiques panneaux");
        initPermission("caracteristique_panneaux.full_access", "Accès complet aux caractéristiques de panneaux", "caracteristique_panneaux", "Caractéristiques panneaux");

        // Type evenement
        initPermission("type_evenement.view_only", "Accès en lecture aux types d'événement", "type_evenement", "Type d'événement");
        initPermission("type_evenement.full_access", "Accès complet aux types d'événement", "type_evenement", "Type d'événement");

        // Type client
        initPermission("type_client.view_only", "Accès en lecture aux types de client", "type_client", "Type client");
        initPermission("type_client.full_access", "Accès complet aux types de client", "type_client", "Type client");

        // Mode paiement
        initPermission("mode_paiement.view_only", "Accès en lecture aux modes de paiement", "mode_paiement", "Mode de paiement");
        initPermission("mode_paiement.full_access", "Accès complet aux modes de paiement", "mode_paiement", "Mode de paiement");

        // Tarif
        initPermission("tarif.view_only", "Accès en lecture aux tarifs", "tarif", "Tarif");
        initPermission("tarif.full_access", "Accès complet aux tarifs", "tarif", "Tarif");

        // Remise
        initPermission("remise.view_only", "Accès en lecture aux remises", "remise", "Remise");
        initPermission("remise.full_access", "Accès complet aux remises", "remise", "Remise");

        // Region
        initPermission("region.view_only", "Accès en lecture aux régions", "region", "Région");
        initPermission("region.full_access", "Accès complet aux régions", "region", "Région");

        // Commune
        initPermission("commune.view_only", "Accès en lecture aux communes", "commune", "Commune");
        initPermission("commune.full_access", "Accès complet aux communes", "commune", "Commune");

        // Quartier
        initPermission("quartier.view_only", "Accès en lecture aux quartiers", "quartier", "Quartier");
        initPermission("quartier.full_access", "Accès complet aux quartiers", "quartier", "Quartier");

        // Secteur
        initPermission("secteur.view_only", "Accès en lecture aux secteurs", "secteur", "Secteur");
        initPermission("secteur.full_access", "Accès complet aux secteurs", "secteur", "Secteur");
    }

    private void initPermission(String code, String description, String module, String displayedLabel) {
        Optional<Permissions> existingPermission = permissionService.getPermissionByCode(code);
        if (existingPermission.isEmpty()) {
            Permissions permission = new Permissions();
            permission.setCode(code);
            permission.setDescription(description);
            permission.setModule(module);
            permission.setDisplayedLabel(displayedLabel);
            permission.setBtEnabled(true);
            permissionService.save(permission);
        }
    }

    /**
     * Jeu de règles de notification par défaut.
     *
     * <p>Volontairement posées désactivées : l'OGP les revoit, ajuste les textes et les délais,
     * puis les active depuis le back-office. Aucun message ne part donc sans décision explicite.</p>
     */
    private void initNotifications() {
        initNotification("CAMPAGNE_CREATION_CLIENT", NotificationEvenements.CAMPAGNE_CREATION,
                "Accusé de création de campagne",
                "jour_meme", 0, true, false, true, "client",
                "Votre campagne {campagne} a bien été enregistrée.",
                "Your campaign {campagne} has been registered.",
                "A sua campanha {campagne} foi registada.",
                "Campagne {campagne} enregistrée");

        initNotification("CAMPAGNE_VALIDATION_CLIENT", NotificationEvenements.CAMPAGNE_VALIDATION,
                "Confirmation de validation de campagne",
                "jour_meme", 0, true, false, true, "client",
                "Votre campagne {campagne} est validée. Elle débutera le {dateDebut}.",
                "Your campaign {campagne} is approved. It starts on {dateDebut}.",
                "A sua campanha {campagne} foi validada. Começa a {dateDebut}.",
                "Campagne {campagne} validée");

        initNotification("CAMPAGNE_DEBUT_CLIENT", NotificationEvenements.CAMPAGNE_DEBUT,
                "Démarrage de campagne",
                "jour_meme", 0, true, false, false, "client",
                "Votre campagne {campagne} démarre aujourd'hui et se termine le {dateFin}.",
                "Your campaign {campagne} starts today and ends on {dateFin}.",
                "A sua campanha {campagne} começa hoje e termina a {dateFin}.",
                "Campagne {campagne} : démarrage");

        initNotification("CAMPAGNE_AVANT_FIN_J3", NotificationEvenements.CAMPAGNE_AVANT_FIN,
                "Rappel de fin de campagne à J-3",
                "avant", 3, true, false, true, "client",
                "Votre campagne {campagne} se termine dans 3 jours, le {dateFin}.",
                "Your campaign {campagne} ends in 3 days, on {dateFin}.",
                "A sua campanha {campagne} termina dentro de 3 dias, a {dateFin}.",
                "Campagne {campagne} : fin proche");

        initNotification("CAMPAGNE_FIN_CLIENT", NotificationEvenements.CAMPAGNE_FIN,
                "Clôture de campagne",
                "jour_meme", 0, false, false, true, "client",
                "Votre campagne {campagne} s'achève aujourd'hui. Merci de votre confiance.",
                "Your campaign {campagne} ends today. Thank you for your trust.",
                "A sua campanha {campagne} termina hoje. Obrigado pela sua confianca.",
                "Campagne {campagne} terminée");

        initNotification("FACTURE_EMISE_CLIENT", NotificationEvenements.FACTURE_EMISE,
                "Transmission de facture",
                "jour_meme", 0, true, false, true, "client",
                "Votre facture {facture} d'un montant de {montant} GNF est disponible. Échéance : {dateEcheance}.",
                "Your invoice {facture} for {montant} GNF is available. Due date: {dateEcheance}.",
                "A sua fatura {facture} no valor de {montant} GNF esta disponivel. Vencimento: {dateEcheance}.",
                "Facture {facture}");

        initNotification("FACTURE_RAPPEL_J7", NotificationEvenements.FACTURE_AVANT_ECHEANCE,
                "Rappel d'échéance à J-7",
                "avant", 7, false, false, true, "client",
                "Votre facture {facture} arrive à échéance le {dateEcheance}. Reste à payer : {resteAPayer} GNF.",
                "Your invoice {facture} is due on {dateEcheance}. Outstanding: {resteAPayer} GNF.",
                "A sua fatura {facture} vence a {dateEcheance}. Em divida: {resteAPayer} GNF.",
                "Facture {facture} : échéance proche");

        initNotification("FACTURE_RAPPEL_J1", NotificationEvenements.FACTURE_AVANT_ECHEANCE,
                "Rappel d'échéance à J-1",
                "avant", 1, true, false, false, "client",
                "Votre facture {facture} arrive à échéance demain. Reste à payer : {resteAPayer} GNF.",
                "Your invoice {facture} is due tomorrow. Outstanding: {resteAPayer} GNF.",
                "A sua fatura {facture} vence amanha. Em divida: {resteAPayer} GNF.",
                "Facture {facture} : échéance demain");

        initNotification("FACTURE_ECHUE_J3", NotificationEvenements.FACTURE_ECHUE,
                "Relance d'impayé à J+3",
                "apres", 3, true, false, true, "client",
                "Votre facture {facture} est échue depuis le {dateEcheance}. Reste à payer : {resteAPayer} GNF.",
                "Your invoice {facture} has been overdue since {dateEcheance}. Outstanding: {resteAPayer} GNF.",
                "A sua fatura {facture} esta vencida desde {dateEcheance}. Em divida: {resteAPayer} GNF.",
                "Facture {facture} : impayée");

        initNotification("FACTURE_ECHUE_INTERNE", NotificationEvenements.FACTURE_ECHUE,
                "Alerte interne sur impayé à J+3",
                "apres", 3, false, false, true, "interne",
                "Facture {facture} du client {client} impayée depuis le {dateEcheance}. Reste : {resteAPayer} GNF.",
                "Invoice {facture} from {client} overdue since {dateEcheance}. Outstanding: {resteAPayer} GNF.",
                "Fatura {facture} do cliente {client} vencida desde {dateEcheance}. Em divida: {resteAPayer} GNF.",
                "Impayé : facture {facture}");

        initNotification("PAIEMENT_RECU_CLIENT", NotificationEvenements.PAIEMENT_RECU,
                "Confirmation de paiement",
                "jour_meme", 0, true, false, true, "client",
                "Paiement de {montantPaiement} GNF reçu pour la facture {facture}. Reste à payer : {resteAPayer} GNF.",
                "Payment of {montantPaiement} GNF received for invoice {facture}. Outstanding: {resteAPayer} GNF.",
                "Pagamento de {montantPaiement} GNF recebido para a fatura {facture}. Em divida: {resteAPayer} GNF.",
                "Paiement reçu : facture {facture}");
    }

    private void initNotification(String code, String evenement, String libelle,
                                  String momentRelatif, int nombreJours,
                                  boolean sms, boolean push, boolean email, String destinataire,
                                  String messageFr, String messageEn, String messagePt, String objet) {
        Optional<NotificationParametrage> existante = notificationParametrageService.findByCode(code);
        if (existante.isPresent()) return;

        NotificationParametrage regle = new NotificationParametrage();
        regle.setCode(code);
        regle.setLibelle(libelle);
        regle.setEvenement(evenement);
        regle.setMomentRelatif(momentRelatif);
        regle.setNombreJours(nombreJours);
        regle.setBtSms(sms);
        regle.setBtPush(push);
        regle.setBtEmail(email);
        regle.setDestinataire(destinataire);
        regle.setObjetEmail(objet);
        regle.setMessageSms(messageFr);
        regle.setMessageSmsEn(messageEn);
        regle.setMessageSmsPt(messagePt);
        regle.setMessageEmail(messageFr);
        regle.setMessageEmailEn(messageEn);
        regle.setMessageEmailPt(messagePt);
        regle.setMessagePush(messageFr);
        regle.setMessagePushEn(messageEn);
        regle.setMessagePushPt(messagePt);
        regle.setHeureEnvoi("08:00");
        regle.setBtEnabled(false);

        notificationParametrageService.save(regle);
    }

}
