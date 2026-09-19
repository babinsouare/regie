package com.api.regie.repository;

import com.api.regie.models.Panneaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanneauxRepository extends JpaRepository<Panneaux, UUID> {

    List<Panneaux> findAll();

    Optional<Panneaux> findByReference(String reference);

    Optional<Panneaux> findByReferenceAndFace(String reference, String face);

    Optional<Panneaux> findByReferenceAndFaceAndIdNot(String reference,String face, UUID id);

    List<Panneaux> findBySecteurId(UUID secteurId);

    List<Panneaux> findBySecteurQuartierId(UUID quartierId);

    List<Panneaux> findBySecteurQuartierCommuneId(UUID communeId);

    List<Panneaux> findBySecteurQuartierCommuneRegionId(UUID regionId);

    List<Panneaux> findByCaracteristiquePanneauxId(UUID caracteristiqueId);

    List<Panneaux> findByBtAvailable(Boolean btAvailable);

    List<Panneaux> findByBtValide(Boolean btValide);

    long countByBtAvailable(Boolean btAvailable);

    // Comptage des panneaux par niveau de localité.
    // En JPQL (et non en SQL natif) pour rester indépendant du SGBD ; chaque requête renvoie
    // {id de la localité, libellé, nombre de panneaux}. Les localités sans aucun panneau ne
    // sont pas retournées, la jointure partant de la table des panneaux.
    @Query("SELECT r.id, r.region, COUNT(p.id) FROM Panneaux p " +
            "JOIN p.secteur s JOIN s.quartier q JOIN q.commune c JOIN c.region r " +
            "GROUP BY r.id, r.region ORDER BY COUNT(p.id) DESC")
    List<Object[]> countPanneauxGroupByRegion();

    @Query("SELECT c.id, c.commune, COUNT(p.id) FROM Panneaux p " +
            "JOIN p.secteur s JOIN s.quartier q JOIN q.commune c " +
            "GROUP BY c.id, c.commune ORDER BY COUNT(p.id) DESC")
    List<Object[]> countPanneauxGroupByCommune();

    @Query("SELECT q.id, q.quartier, COUNT(p.id) FROM Panneaux p " +
            "JOIN p.secteur s JOIN s.quartier q " +
            "GROUP BY q.id, q.quartier ORDER BY COUNT(p.id) DESC")
    List<Object[]> countPanneauxGroupByQuartier();

    @Query("SELECT s.id, s.secteur, COUNT(p.id) FROM Panneaux p " +
            "JOIN p.secteur s " +
            "GROUP BY s.id, s.secteur ORDER BY COUNT(p.id) DESC")
    List<Object[]> countPanneauxGroupBySecteur();

    @Query(value = "SELECT r.region, COUNT(p.id) FROM panneaux p " +
            "JOIN secteur s ON p.id_secteur = s.id " +
            "JOIN quartier q ON s.id_quartier = q.id " +
            "JOIN commune c ON q.id_commune = c.id " +
            "JOIN region r ON c.id_region = r.id " +
            "GROUP BY r.id, r.region ORDER BY COUNT(p.id) DESC", nativeQuery = true)
    List<Object[]> countPanneauxByRegion();

    Panneaux save(Panneaux panneaux);

    Optional<Panneaux> findById(UUID id);
}
