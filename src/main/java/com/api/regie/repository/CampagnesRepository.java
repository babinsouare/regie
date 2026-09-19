package com.api.regie.repository;

import com.api.regie.models.Campagnes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampagnesRepository extends JpaRepository<Campagnes, UUID> {

    List<Campagnes> findAll();

    Optional<Campagnes> findByNomCampagne(String nomCampagne);

    Optional<Campagnes> findByNomCampagneAndIdNot(String nomCampagne, UUID id);

    List<Campagnes> findByClientId(UUID clientId);

    List<Campagnes> findByUserId(UUID userId);

    List<Campagnes> findByStatut(String statut);

    List<Campagnes> findByDtCreatedBetweenOrderByDtCreatedDesc(Date dateDebut, Date dateFin);

    List<Campagnes> findByDateDebutBetweenOrderByDateDebutAsc(Date dateDebut, Date dateFin);

    List<Campagnes> findByDateFinBetweenOrderByDateFinAsc(Date dateDebut, Date dateFin);

    Campagnes save(Campagnes campagnes);

    Optional<Campagnes> findById(UUID id);

    // Vérifier si un panneau est déjà occupé dans une période donnée
    @Query("SELECT c FROM Campagnes c " +
            "JOIN PanneauxCampagne pc ON pc.campagne.id = c.id " +
            "WHERE pc.panneaux.id = :panneauId " +
            "AND c.statut NOT IN ('proposition', 'reservation','ended','archived') " +
            "AND ((c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut))")
    List<Campagnes> findCampagnesWithPanneauInPeriod(
            @Param("panneauId") UUID panneauId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin
    );

    @Query("SELECT c FROM Campagnes c " +
            "JOIN PanneauxCampagne pc ON pc.campagne.id = c.id " +
            "WHERE pc.panneaux.id = :panneauId " +
            "AND c.id != :campagneIdToExclude " +
            "AND c.statut NOT IN ('proposition', 'reservation','ended','archived') " +
            "AND ((c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut))")
    List<Campagnes> findCampagnesWithPanneauInPeriodExcludingCampagne(
            @Param("panneauId") UUID panneauId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin,
            @Param("campagneIdToExclude") UUID campagneIdToExclude
    );

    long countByStatut(String statut);

    // KPI Queries avec cast DATE pour comparaison uniquement sur la date (sans heure)
    @Query(value = "SELECT COUNT(*)  FROM campagnes c WHERE DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin)",nativeQuery = true)
    Long getNombreTotalCampagnes(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT c.statut, COUNT(*) FROM campagnes c WHERE DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) GROUP BY c.statut;", nativeQuery = true)
    List<Object[]> getCampagnesParStatut(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT c.statut_paiement, COUNT(*) FROM campagnes c WHERE DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) GROUP BY c.statut_paiement", nativeQuery = true)
    List<Object[]> getCampagnesParStatutPaiement(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT COALESCE(AVG(c.date_fin - c.date_debut), 0) FROM campagnes c WHERE c.date_debut IS NOT NULL AND c.date_fin IS NOT NULL AND DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin)", nativeQuery = true)
    Double getDureeMoyenneCampagnes(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    // KPI par Client
    @Query(value = "SELECT c.id_client, cl.denomination, COUNT(*), AVG(c.date_fin - c.date_debut) " +
            "FROM campagnes c JOIN clients cl ON c.id_client = cl.id WHERE c.id_client IS NOT NULL " +
            "AND DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) " +
            "GROUP BY c.id_client, cl.denomination ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<Object[]> getKPIParClient(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT COUNT(*), AVG(c.date_fin - c.date_debut) " +
            "FROM campagnes c WHERE c.id_client = :clientId " +
            "AND DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin)", nativeQuery = true)
    Object[] getKPIParClientId(@Param("clientId") UUID clientId, @Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    // KPI par Période
    @Query(value = "SELECT DATE_TRUNC('day', c.dt_created), COUNT(*) " +
            "FROM campagnes c WHERE DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) " +
            "GROUP BY DATE_TRUNC('day', c.dt_created) ORDER BY DATE_TRUNC('day', c.dt_created)", nativeQuery = true)
    List<Object[]> getCampagnesParJour(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT DATE_TRUNC('month', c.dt_created), COUNT(*) " +
            "FROM campagnes c WHERE DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) " +
            "GROUP BY DATE_TRUNC('month', c.dt_created) ORDER BY DATE_TRUNC('month', c.dt_created)", nativeQuery = true)
    List<Object[]> getCampagnesParMois(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    // KPI Panneaux - Nombre de jours d'utilisation par panneau (exclut canceled et proposition)
    @Query(value = "SELECT p.id, p.code, p.nom_panneau, COALESCE(SUM(c.date_fin - c.date_debut), 0) as total_jours " +
            "FROM panneaux p LEFT JOIN panneaux_campagne pc ON p.id = pc.id_panneau " +
            "LEFT JOIN campagnes c ON pc.id_campagne = c.id AND c.date_debut IS NOT NULL AND c.date_fin IS NOT NULL " +
            "AND c.statut NOT IN ('canceled', 'proposition') " +
            "AND DATE(c.dt_created) BETWEEN DATE(:dateDebut) AND DATE(:dateFin) " +
            "GROUP BY p.id, p.code, p.nom_panneau ORDER BY total_jours DESC", nativeQuery = true)
    List<Object[]> getPanneauxAvecJoursUtilisation(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);
}
