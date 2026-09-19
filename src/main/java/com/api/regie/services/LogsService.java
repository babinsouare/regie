package com.api.regie.services;

import com.api.regie.models.Logs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LogsService {

    List<Logs> getAllLogs();

    Logs addLogs(Logs logs);

    List<Logs> findByMsisdn(String msisdn);

    List<Logs> findByReference(String reference);

    List<Logs> findByDtCreatedBetween(Date dateDebut, Date dateFin);

    Optional<Logs> findById(Long id);
}
