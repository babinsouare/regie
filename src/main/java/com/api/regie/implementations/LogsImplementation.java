package com.api.regie.implementations;

import com.api.regie.models.Logs;
import com.api.regie.repository.LogsRepository;
import com.api.regie.services.LogsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LogsImplementation implements LogsService {

    private final LogsRepository logsRepository;

    public LogsImplementation(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @Override
    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    @Override
    public Logs addLogs(Logs logs) {
        return logsRepository.save(logs);
    }

    @Override
    public List<Logs> findByMsisdn(String msisdn) {
        return logsRepository.findByMsisdn(msisdn);
    }

    @Override
    public List<Logs> findByReference(String reference) {
        return logsRepository.findByReference(reference);
    }

    @Override
    public List<Logs> findByDtCreatedBetween(Date dateDebut, Date dateFin) {
        return logsRepository.findByDtCreatedBetween(dateDebut, dateFin);
    }

    @Override
    public Optional<Logs> findById(Long id) {
        return logsRepository.findById(id);
    }
}
