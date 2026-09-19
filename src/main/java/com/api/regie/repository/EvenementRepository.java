package com.api.regie.repository;

import com.api.regie.models.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvenementRepository extends JpaRepository<Evenement, UUID> {

    List<Evenement> findAll();

    List<Evenement> findByPanneauId(UUID panneauId);

    List<Evenement> findByTypeEvenementId(UUID typeEvenementId);

    List<Evenement> findByUserId(UUID userId);

    Evenement save(Evenement evenement);

    Optional<Evenement> findById(UUID id);

    // Agrégations des événements. En JPQL pour rester indépendant du SGBD.
    // Chaque axe existe en deux variantes : sans filtre de période, et bornée sur dateEvenement.
    // La variante non bornée est nécessaire pour ne pas écarter les événements dont la date
    // n'est pas renseignée, ce qu'un simple encadrement par des bornes larges ferait.

    @Query("SELECT u.id, u.nom, u.prenom, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.user u " +
            "GROUP BY u.id, u.nom, u.prenom ORDER BY COUNT(e.id) DESC")
    List<Object[]> aggregateParAgent();

    @Query("SELECT u.id, u.nom, u.prenom, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.user u " +
            "WHERE e.dateEvenement >= :dateDebut AND e.dateEvenement <= :dateFin " +
            "GROUP BY u.id, u.nom, u.prenom ORDER BY COUNT(e.id) DESC")
    List<Object[]> aggregateParAgentSurPeriode(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT t.id, t.type, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.typeEvenement t " +
            "GROUP BY t.id, t.type ORDER BY COUNT(e.id) DESC")
    List<Object[]> aggregateParType();

    @Query("SELECT t.id, t.type, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.typeEvenement t " +
            "WHERE e.dateEvenement >= :dateDebut AND e.dateEvenement <= :dateFin " +
            "GROUP BY t.id, t.type ORDER BY COUNT(e.id) DESC")
    List<Object[]> aggregateParTypeSurPeriode(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    @Query("SELECT u.id, u.nom, u.prenom, t.id, t.type, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.user u JOIN e.typeEvenement t " +
            "GROUP BY u.id, u.nom, u.prenom, t.id, t.type " +
            "ORDER BY u.nom, u.prenom, COUNT(e.id) DESC")
    List<Object[]> aggregateParAgentEtType();

    @Query("SELECT u.id, u.nom, u.prenom, t.id, t.type, COUNT(e.id), COALESCE(SUM(e.coutTotal), 0) " +
            "FROM Evenement e JOIN e.user u JOIN e.typeEvenement t " +
            "WHERE e.dateEvenement >= :dateDebut AND e.dateEvenement <= :dateFin " +
            "GROUP BY u.id, u.nom, u.prenom, t.id, t.type " +
            "ORDER BY u.nom, u.prenom, COUNT(e.id) DESC")
    List<Object[]> aggregateParAgentEtTypeSurPeriode(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);
}
