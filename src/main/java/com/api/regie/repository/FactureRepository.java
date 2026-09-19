package com.api.regie.repository;

import com.api.regie.models.Facture;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactureRepository extends JpaRepository<Facture, UUID> {

    List<Facture> findAll();

    List<Facture> findByCampagneId(UUID campagneId);

    void deleteByCampagneId(UUID campagneId);

    List<Facture> findByUserId(UUID userId);

    List<Facture> findByCampagneClientTelephoneResponsable(String telephoneResponsable);

    Facture save(Facture facture);

    Optional<Facture> findById(UUID id);

    Optional<Facture> findByReference(String reference);

    List<Facture> findByDtCreatedBetweenOrderByDtCreatedDesc(Date dateDebut, Date dateFin);

    List<Facture> findByDateEcheanceBetweenOrderByDateEcheanceAsc(Date dateDebut, Date dateFin);

    List<Facture> findAllByOrderByDtCreatedDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(f.montantNet), 0) FROM Facture f WHERE f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Double getTotalFacture(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COALESCE(SUM(f.montantPaye), 0) FROM Facture f WHERE f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Double getTotalPaye(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COALESCE(SUM(f.montantResteAPaye), 0) FROM Facture f WHERE f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Double getTotalImpaye(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COALESCE(AVG(f.montantNet), 0) FROM Facture f WHERE f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Double getMontantMoyen(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.montantResteAPaye = 0 AND f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Long getNombreFacturesPayees(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.montantPaye > 0 AND f.montantResteAPaye > 0 AND f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Long getNombreFacturesPartielles(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.montantPaye = 0 AND f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Long getNombreFacturesImpayees(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.dateEcheance < CURRENT_DATE AND f.montantResteAPaye > 0 AND f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Long getNombreFacturesEnRetard(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT COALESCE(SUM(f.montantResteAPaye), 0) FROM Facture f WHERE f.dateEcheance < CURRENT_DATE AND f.montantResteAPaye > 0 AND f.dtCreated >= :dateDebut AND f.dtCreated <= :dateFin")
    Double getMontantFacturesEnRetard(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT TO_CHAR(f.dt_created, 'YYYY-MM') AS mois, COALESCE(SUM(f.montant_net), 0) AS montant " +
            "FROM factures f WHERE DATE(f.dt_created) >= DATE(:dateDebut) " +
            "AND DATE(f.dt_created) <= DATE(:dateFin) " +
            "GROUP BY TO_CHAR(f.dt_created, 'YYYY-MM') ORDER BY mois", nativeQuery = true)
    List<Object[]> getCAMensuel(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT cl.denomination, cl.sigle, COUNT(DISTINCT c.id) AS nombre_campagnes, COALESCE(SUM(f.montant_net), 0) AS ca " +
            "FROM factures f JOIN campagnes c ON f.id_campagne = c.id JOIN clients cl ON c.id_client = cl.id " +
            "WHERE DATE(f.dt_created) >= DATE(:dateDebut) " +
            "AND DATE(f.dt_created) <= DATE(:dateFin) " +
            "GROUP BY cl.id, cl.denomination, cl.sigle ORDER BY ca DESC LIMIT 5", nativeQuery = true)
    List<Object[]> getTopClients(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT COUNT(DISTINCT c.id_client) FROM factures f " +
            "JOIN campagnes c ON f.id_campagne = c.id " +
            "WHERE f.montant_reste_a_paye > 0 " +
            "AND DATE(f.dt_created) >= DATE(:dateDebut) " +
            "AND DATE(f.dt_created) <= DATE(:dateFin)", nativeQuery = true)
    Long countClientsAvecImpayes(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query(value = "SELECT cl.denomination, cl.sigle, COUNT(f.id) AS nombre_factures, " +
            "COALESCE(SUM(f.montant_net), 0) AS montant_facture, COALESCE(SUM(f.montant_reste_a_paye), 0) AS reste_a_payer " +
            "FROM factures f JOIN campagnes c ON f.id_campagne = c.id JOIN clients cl ON c.id_client = cl.id " +
            "WHERE f.montant_reste_a_paye > 0 " +
            "AND DATE(f.dt_created) >= DATE(:dateDebut) " +
            "AND DATE(f.dt_created) <= DATE(:dateFin) " +
            "GROUP BY cl.id, cl.denomination, cl.sigle ORDER BY reste_a_payer DESC", nativeQuery = true)
    List<Object[]> getImpayesParSociete(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);
}
