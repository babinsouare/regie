package com.api.regie.repository;

import com.api.regie.models.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LogsRepository extends JpaRepository<Logs, Long> {

    List<Logs> findAll();

    List<Logs> findByMsisdn(String msisdn);

    List<Logs> findByReference(String reference);

    List<Logs> findByDtCreatedBetween(Date dateDebut, Date dateFin);

    @Query(value = "SELECT * FROM logs WHERE DATE(dt_created) >= DATE(:dateDebut) AND DATE(dt_created) <= DATE(:dateFin) ORDER BY dt_created DESC", nativeQuery = true)
    List<Logs> findByDtCreatedBetweenCast(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);

    Logs save(Logs logs);

    Optional<Logs> findById(Long id);
}
