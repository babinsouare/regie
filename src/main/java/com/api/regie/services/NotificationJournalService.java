package com.api.regie.services;

import com.api.regie.models.NotificationJournal;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationJournalService {

    List<NotificationJournal> getAllJournaux();

    Optional<NotificationJournal> findById(UUID id);

    List<NotificationJournal> findByReferenceObjet(UUID referenceObjet);

    List<NotificationJournal> findByEvenement(String evenement);

    List<NotificationJournal> findByPeriode(Date dateDebut, Date dateFin);

    List<NotificationJournal> findDerniers(int limit);
}
