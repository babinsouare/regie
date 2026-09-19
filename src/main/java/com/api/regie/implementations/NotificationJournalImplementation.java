package com.api.regie.implementations;

import com.api.regie.models.NotificationJournal;
import com.api.regie.repository.NotificationJournalRepository;
import com.api.regie.services.NotificationJournalService;
import com.api.regie.utils.Helpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationJournalImplementation implements NotificationJournalService {

    private final NotificationJournalRepository journalRepository;

    public NotificationJournalImplementation(NotificationJournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @Override
    public List<NotificationJournal> getAllJournaux() {
        return journalRepository.findAll();
    }

    @Override
    public Optional<NotificationJournal> findById(UUID id) {
        return journalRepository.findById(id);
    }

    @Override
    public List<NotificationJournal> findByReferenceObjet(UUID referenceObjet) {
        return journalRepository.findByReferenceObjetOrderByDtCreatedDesc(referenceObjet);
    }

    @Override
    public List<NotificationJournal> findByEvenement(String evenement) {
        return journalRepository.findByEvenementOrderByDtCreatedDesc(evenement);
    }

    @Override
    public List<NotificationJournal> findByPeriode(Date dateDebut, Date dateFin) {
        return journalRepository.findByDtCreatedBetweenOrderByDtCreatedDesc(
                Helpers.debutDeJournee(dateDebut), Helpers.finDeJournee(dateFin));
    }

    @Override
    public List<NotificationJournal> findDerniers(int limit) {
        return journalRepository.findAllByOrderByDtCreatedDesc(PageRequest.of(0, limit));
    }
}
