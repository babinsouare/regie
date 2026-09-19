package com.api.regie.controllers;

import com.api.regie.models.*;
import com.api.regie.services.CaracteristiquePanneauxService;
import com.api.regie.services.PanneauxService;
import com.api.regie.services.SecteurService;
import com.api.regie.services.TarifsService;
import com.api.regie.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/panneau")
public class PanneauxController {

    private final PanneauxService panneauxService;
    private final CaracteristiquePanneauxService caracteristiquePanneauxService;
    private final SecteurService secteurService;
    private final TarifsService tarifsService;
    private final SecurityUtils securityUtils;

    public PanneauxController(PanneauxService panneauxService, CaracteristiquePanneauxService caracteristiquePanneauxService, SecteurService secteurService, TarifsService tarifsService, SecurityUtils securityUtils) {
        this.panneauxService = panneauxService;
        this.caracteristiquePanneauxService = caracteristiquePanneauxService;
        this.secteurService = secteurService;
        this.tarifsService = tarifsService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("liste")
    public Result getAllPanneaux(){
        List<Panneaux> panneaux = panneauxService.getAllPanneaux();
        for (Panneaux panneau : panneaux) {
            if (!Boolean.TRUE.equals(panneau.getHasSpecialPrice()) && panneau.getCaracteristiquePanneaux() != null) {
                List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(panneau.getCaracteristiquePanneaux().getId());
                if (!tarifs.isEmpty()) {
                    Tarifs tarif = tarifs.get(0);
                    panneau.setPriceDay(tarif.getPriceDay());
                    panneau.setPriceWeek(tarif.getPriceWeek());
                    panneau.setPriceMonth(tarif.getPriceMonth());
                }
            }
        }
        return Result.success(panneaux, "Liste des panneaux.", "List of billboards.", "Lista dos painéis.");
    }

    @GetMapping("getbyid")
    public Result getPanneauById(@RequestParam("idPanneau") UUID idPanneau){

        Optional<Panneaux> panneaux=panneauxService.findById(idPanneau);
        if(panneaux.isEmpty()) return Result.error(404, "Panneau introuvable.", "Billboard not found.", "Painel não encontrado.");

        Map<String, Object> response = new HashMap<>();
        response.put("panneau", panneaux.get());

        CaracteristiquePanneaux caracteristique = panneaux.get().getCaracteristiquePanneaux();
        if (caracteristique != null) {
            List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(caracteristique.getId());
            if (!tarifs.isEmpty()) response.put("tarif", tarifs.get(0));
        }
        return Result.success(response, "Les informations du panneau.", "Billboard details.", "Detalhes do painel.");
    }

    @GetMapping("getbylocalisation")
    public Result getPanneauxByLocalisation(
            @RequestParam(value = "idRegion", required = false) UUID idRegion,
            @RequestParam(value = "idCommune", required = false) UUID idCommune,
            @RequestParam(value = "idQuartier", required = false) UUID idQuartier,
            @RequestParam(value = "idSecteur", required = false) UUID idSecteur) {

        List<Panneaux> panneaux;
        String message;
        String messageEn;
        String messagePt;

        if (idSecteur != null) {
            panneaux = panneauxService.findBySecteurId(idSecteur);
            message = "Liste des panneaux du secteur.";
            messageEn = "List of the sector's billboards.";
            messagePt = "Lista dos painéis do setor.";
        } else if (idQuartier != null) {
            panneaux = panneauxService.findBySecteurQuartierId(idQuartier);
            message = "Liste des panneaux du quartier.";
            messageEn = "List of the district's billboards.";
            messagePt = "Lista dos painéis do bairro.";
        } else if (idCommune != null) {
            panneaux = panneauxService.findBySecteurQuartierCommuneId(idCommune);
            message = "Liste des panneaux de la commune.";
            messageEn = "List of the municipality's billboards.";
            messagePt = "Lista dos painéis do município.";
        } else if (idRegion != null) {
            panneaux = panneauxService.findBySecteurQuartierCommuneRegionId(idRegion);
            message = "Liste des panneaux de la région.";
            messageEn = "List of the region's billboards.";
            messagePt = "Lista dos painéis da região.";
        } else {
            return Result.error(400,
                    "Veuillez fournir au moins un critère : idRegion, idCommune, idQuartier ou idSecteur.",
                    "Please provide at least one criterion: idRegion, idCommune, idQuartier or idSecteur.",
                    "Indique pelo menos um critério: idRegion, idCommune, idQuartier ou idSecteur.");
        }

        for (Panneaux panneau : panneaux) {
            if (!Boolean.TRUE.equals(panneau.getHasSpecialPrice()) && panneau.getCaracteristiquePanneaux() != null) {
                List<Tarifs> tarifs = tarifsService.findByCaracteristiquePanneauxId(panneau.getCaracteristiquePanneaux().getId());
                if (!tarifs.isEmpty()) {
                    Tarifs tarif = tarifs.get(0);
                    panneau.setPriceDay(tarif.getPriceDay());
                    panneau.setPriceWeek(tarif.getPriceWeek());
                    panneau.setPriceMonth(tarif.getPriceMonth());
                }
            }
        }

        return Result.success(panneaux, message, messageEn, messagePt);
    }

    @GetMapping("getbysecteur")
    public Result getPanneauxBySecteur(@RequestParam("idSecteur") UUID idSecteur){
        return Result.success(panneauxService.findBySecteurId(idSecteur),
                "Liste des panneaux du secteur.",
                "List of the sector's billboards.",
                "Lista dos painéis do setor.");
    }

    @GetMapping("getbycaracteristique")
    public Result getPanneauxByCaracteristique(@RequestParam("idCaracteristique") UUID idCaracteristique){
        return Result.success(panneauxService.findByCaracteristiquePanneauxId(idCaracteristique),
                "Liste des panneaux par caractéristique.",
                "List of billboards by characteristic.",
                "Lista dos painéis por característica.");
    }

    @GetMapping("count-by-localite")
    public Result countPanneauxParLocalite(@RequestParam("type") String type){

        if (type == null || type.isBlank()) return Result.error(400,
                "Le type de localité est obligatoire : region, commune, quartier ou secteur.",
                "The locality type is required: region, commune, quartier or secteur.",
                "O tipo de localidade é obrigatório: region, commune, quartier ou secteur.");

        String typeNormalise = type.trim().toLowerCase();

        if (!List.of("region", "commune", "quartier", "secteur").contains(typeNormalise)) {
            return Result.error(400,
                    "Type de localité invalide. Valeurs acceptées : region, commune, quartier, secteur.",
                    "Invalid locality type. Accepted values: region, commune, quartier, secteur.",
                    "Tipo de localidade inválido. Valores aceites: region, commune, quartier, secteur.");
        }

        List<Map<String, Object>> repartition = new ArrayList<>();
        int totalPanneaux = 0;

        for (Object[] row : panneauxService.countPanneauxParLocalite(typeNormalise)) {
            Map<String, Object> localite = new LinkedHashMap<>();
            localite.put("id", row[0]);
            localite.put("libelle", row[1]);
            localite.put("nombrePanneaux", row[2]);
            repartition.add(localite);

            totalPanneaux += ((Number) row[2]).intValue();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("type", typeNormalise);
        response.put("totalPanneaux", totalPanneaux);
        response.put("repartition", repartition);

        return Result.success(response,
                "Nombre de panneaux par " + typeNormalise + ".",
                "Number of billboards by " + typeNormalise + ".",
                "Número de painéis por " + typeNormalise + ".");
    }

    @GetMapping("getdisponibles")
    public Result getPanneauxDisponibles(){
        return Result.success(panneauxService.findByBtAvailable(true),
                "Liste des panneaux disponibles.",
                "List of available billboards.",
                "Lista dos painéis disponíveis.");
    }

    @GetMapping("getvalides")
    public Result getPanneauxValides(){
        return Result.success(panneauxService.findByBtValide(true),
                "Liste des panneaux validés.",
                "List of approved billboards.",
                "Lista dos painéis validados.");
    }

    @PostMapping("/add")
    public Result addPanneau(@RequestBody Panneaux panneau, @RequestParam("idCaracteristique") UUID idCaracteristique, @RequestParam("idSecteur") UUID idSecteur){

        if(panneau.getNombreFace()<=0 || panneau.getNombreFace()>4) return Result.error(400,
                "Nombre de faces incorrect.",
                "Invalid number of faces.",
                "Número de faces inválido.");

        Optional<Secteur> secteur = secteurService.findById(idSecteur);
        if (secteur.isEmpty()) return Result.error(404, "Secteur introuvable.", "Sector not found.", "Setor não encontrado.");

        Optional<CaracteristiquePanneaux> caracteristiquePanneaux = caracteristiquePanneauxService.findById(idCaracteristique);
        if (caracteristiquePanneaux.isEmpty()) return Result.error(404,
                "Caractéristique de panneaux introuvable.",
                "Billboard characteristic not found.",
                "Característica de painéis não encontrada.");

        panneau.setSecteur(secteur.get());
        panneau.setCaracteristiquePanneaux(caracteristiquePanneaux.get());
        panneau.setIdUser(securityUtils.getCurrentUser() != null ? securityUtils.getCurrentUser().getId() : null);

        String[] faces = {"A", "B", "C", "D"};
        List<Panneaux> liste = new ArrayList<>();

        for (int i = 0; i < panneau.getNombreFace(); i++) {
            Panneaux copie = new Panneaux(panneau);
            copie.setFace(faces[i]);
            liste.add(panneauxService.addPanneaux(copie));
        }

        return Result.success(liste, "Ajout effectué avec succès.", "Successfully added.", "Adicionado com sucesso.");
    }

    @PutMapping("/update")
    public Result updatePanneau(@RequestBody Panneaux panneau, @RequestParam("idCaracteristique") UUID idCaracteristique, @RequestParam("idSecteur") UUID idSecteur){

        Optional<Panneaux> checkPanneauId = panneauxService.findById(panneau.getId());

        if(checkPanneauId.isEmpty()){
            return Result.error(400, "Données incorrectes.", "Invalid data.", "Dados inválidos.");
        }

        Optional<Panneaux> checkPanneau = panneauxService.findByReferenceAndFaceAndIdNot(panneau.getReference(), panneau.getFace(), panneau.getId());

        if(checkPanneau.isPresent()){
            return Result.error(400,
                    "Cette référence de panneau existe déjà.",
                    "This billboard reference already exists.",
                    "Esta referência de painel já existe.");
        }

        Optional<Secteur> secteur = secteurService.findById(idSecteur);
        if (secteur.isEmpty()) return Result.error(404, "Secteur introuvable.", "Sector not found.", "Setor não encontrado.");

        Optional<CaracteristiquePanneaux> caracteristiquePanneaux = caracteristiquePanneauxService.findById(idCaracteristique);
        if (caracteristiquePanneaux.isEmpty()) return Result.error(404,
                "Caractéristique de panneaux introuvable.",
                "Billboard characteristic not found.",
                "Característica de painéis não encontrada.");

        panneau.setSecteur(secteur.get());
        panneau.setCaracteristiquePanneaux(caracteristiquePanneaux.get());

        return Result.success(panneauxService.addPanneaux(panneau),
                "Modification effectuée avec succès.",
                "Update completed successfully.",
                "Alteração efetuada com sucesso.");
    }
}
